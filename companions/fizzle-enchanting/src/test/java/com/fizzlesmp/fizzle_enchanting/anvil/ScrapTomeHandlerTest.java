package com.fizzlesmp.fizzle_enchanting.anvil;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.mojang.serialization.Lifecycle;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-5.2.1 — behavioural contract for {@link ScrapTomeHandler}. Covers every acceptance bullet
 * on the task:
 * <ul>
 *   <li>Enchanted item + scrap tome produces an enchanted book.</li>
 *   <li>Output carries exactly <strong>one</strong> enchantment, drawn from the input.</li>
 *   <li>Pick is deterministic (seeded RNG) — repeated calls return the same enchant.</li>
 *   <li>XP cost reads {@code config.tomes.scrapTomeXpCost}.</li>
 *   <li>One tome consumed, left item destroyed (consumption is vanilla's onTake job, so the
 *       handler only asserts {@code rightConsumed==1}; left destruction follows from
 *       {@code AnvilMenu#onTake}'s default behaviour).</li>
 *   <li>Unenchanted left declines.</li>
 *   <li>Missing config / empty slots / non-tome right all decline cleanly.</li>
 * </ul>
 *
 * <p>Bootstrap follows {@link PrismaticWebHandlerTest}'s pattern: the registry needs to be
 * unfrozen to register {@link FizzleEnchantingRegistry#SCRAP_TOME}, and a synthetic enchantment
 * registry is built so the test can construct real {@link ItemEnchantments} fixtures without
 * depending on vanilla's data-driven enchantment loading.
 */
class ScrapTomeHandlerTest {

    private static final ResourceKey<Enchantment> SHARPNESS = mcKey("sharpness");
    private static final ResourceKey<Enchantment> UNBREAKING = mcKey("unbreaking");
    private static final ResourceKey<Enchantment> MENDING = mcKey("mending");

    private static Registry<Enchantment> enchantmentRegistry;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();

        unfreezeIntrusive(BuiltInRegistries.BLOCK);
        unfreezeIntrusive(BuiltInRegistries.ITEM);
        unfreezeIntrusive(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        unfreeze(BuiltInRegistries.MENU);

        FizzleEnchantingRegistry.register();

        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
        BuiltInRegistries.MENU.freeze();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();

        enchantmentRegistry = buildEnchantmentRegistry();
    }

    @Test
    void handle_enchantedSwordPlusTome_producesBookWithOneEnchantment() {
        ScrapTomeHandler handler = new ScrapTomeHandler(ScrapTomeHandlerTest::defaultConfig);

        Map<ResourceKey<Enchantment>, Integer> loadout = Map.of(
                SHARPNESS, 3,
                UNBREAKING, 2,
                MENDING, 1);
        ItemStack left = enchantedSword(loadout);
        ItemStack right = scrapTome(1);

        AnvilResult r = handler.handle(left, right, null).orElseThrow(() ->
                new AssertionError("enchanted sword + scrap tome must produce a claim"));

        assertSame(Items.ENCHANTED_BOOK, r.output().getItem(),
                "scrap tome always outputs a vanilla enchanted book");
        assertEquals(1, r.output().getCount(),
                "exactly one book is produced regardless of input stack size");

        ItemEnchantments stored = r.output().getOrDefault(
                DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(1, stored.size(),
                "scrap tome keeps ONE enchantment — that's the whole point of the cheap tier");

        Holder<Enchantment> picked = stored.keySet().iterator().next();
        ResourceKey<Enchantment> pickedKey = picked.unwrapKey().orElseThrow();
        assertTrue(loadout.containsKey(pickedKey),
                "picked enchantment must be one of the three on the input item");
        assertEquals(loadout.get(pickedKey), stored.getLevel(picked),
                "picked enchantment's level is preserved verbatim — the tome doesn't re-roll it");

        assertEquals(defaultConfig().tomes.scrapTomeXpCost, r.xpCost(),
                "xp cost must come from config.tomes.scrapTomeXpCost (DESIGN § Tome items)");
        assertEquals(1, r.rightConsumed(),
                "one tome per salvage — vanilla onTake will delete it when the player takes the output");
    }

    @Test
    void handle_seededPick_isDeterministic() {
        ScrapTomeHandler handler = new ScrapTomeHandler(ScrapTomeHandlerTest::defaultConfig);

        Map<ResourceKey<Enchantment>, Integer> loadout = Map.of(
                SHARPNESS, 3,
                UNBREAKING, 2,
                MENDING, 1);

        ItemEnchantments first = handler.handle(enchantedSword(loadout), scrapTome(1), null)
                .orElseThrow()
                .output()
                .getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments second = handler.handle(enchantedSword(loadout), scrapTome(1), null)
                .orElseThrow()
                .output()
                .getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);

        Holder<Enchantment> firstPick = first.keySet().iterator().next();
        Holder<Enchantment> secondPick = second.keySet().iterator().next();
        assertEquals(firstPick.unwrapKey(), secondPick.unwrapKey(),
                "same inputs must always yield the same enchantment — seeded RNG is the contract");
    }

    @Test
    void handle_seededPick_matchesPredictedIndex() {
        // Recompute the handler's pick using its exposed helpers so the test fails loudly if the
        // seed recipe ever changes. This is the "seeded RNG picks expected enchant" acceptance.
        ScrapTomeHandler handler = new ScrapTomeHandler(ScrapTomeHandlerTest::defaultConfig);

        Map<ResourceKey<Enchantment>, Integer> loadout = Map.of(
                SHARPNESS, 3,
                UNBREAKING, 2,
                MENDING, 1);
        ItemStack left = enchantedSword(loadout);

        ItemEnchantments existing = left.getOrDefault(
                DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        List<Holder<Enchantment>> sorted = ScrapTomeHandler.sortedKeys(existing);
        long seed = ScrapTomeHandler.seedFor(null, sorted);
        int expectedIndex = new Random(seed).nextInt(sorted.size());
        ResourceKey<Enchantment> expectedKey = sorted.get(expectedIndex).unwrapKey().orElseThrow();

        AnvilResult r = handler.handle(left, scrapTome(1), null).orElseThrow();
        ItemEnchantments stored = r.output().getOrDefault(
                DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        Holder<Enchantment> picked = stored.keySet().iterator().next();

        assertEquals(expectedKey, picked.unwrapKey().orElseThrow(),
                "handler pick must match the seed-derived index — confirms the published seedFor/"
                        + "sortedKeys helpers are what the production code actually uses");
    }

    @Test
    void handle_singleEnchantment_outputMatchesInput() {
        ScrapTomeHandler handler = new ScrapTomeHandler(ScrapTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(MENDING, 1));
        AnvilResult r = handler.handle(left, scrapTome(1), null).orElseThrow();

        ItemEnchantments stored = r.output().getOrDefault(
                DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(1, stored.size());
        assertEquals(MENDING,
                stored.keySet().iterator().next().unwrapKey().orElseThrow(),
                "with only one candidate there is no roll — mending is the single surviving pick");
        assertEquals(1, stored.getLevel(holderFor(MENDING)));
    }

    @Test
    void handle_unenchantedLeft_declines() {
        ScrapTomeHandler handler = new ScrapTomeHandler(ScrapTomeHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        ItemStack right = scrapTome(1);

        Optional<AnvilResult> result = handler.handle(left, right, null);
        assertTrue(result.isEmpty(),
                "nothing to scrap — handler must decline so the tome isn't consumed on a no-op");
    }

    @Test
    void handle_nonTomeRight_declines() {
        ScrapTomeHandler handler = new ScrapTomeHandler(ScrapTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = new ItemStack(Items.BOOK);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "right slot must be a ScrapTomeItem — plain books belong to vanilla's enchant-combine path");
    }

    @Test
    void handle_improvedScrapTomeOnRight_declines() {
        // Defensive: even a sibling tome must not accidentally trigger this handler. The
        // ImprovedScrapTomeHandler (T-5.2.2) is a separate dispatcher entry.
        ScrapTomeHandler handler = new ScrapTomeHandler(ScrapTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = new ItemStack(FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "improved scrap tome is handled elsewhere — this handler must instance-check ScrapTomeItem");
    }

    @Test
    void handle_emptySlots_declines() {
        ScrapTomeHandler handler = new ScrapTomeHandler(ScrapTomeHandlerTest::defaultConfig);

        assertTrue(handler.handle(ItemStack.EMPTY, scrapTome(1), null).isEmpty(),
                "empty left — handler must decline");
        assertTrue(handler.handle(enchantedSword(Map.of(SHARPNESS, 1)), ItemStack.EMPTY, null).isEmpty(),
                "empty right — handler must decline");
    }

    @Test
    void handle_configMissing_declines() {
        // getConfig() returns null before onInitialize has run; the handler must treat that as
        // "feature off" rather than NPE.
        ScrapTomeHandler handler = new ScrapTomeHandler(() -> null);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = scrapTome(1);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "null config means onInitialize has not finished — handler must decline instead of NPE");
    }

    @Test
    void handle_customXpCost_reflectsConfig() {
        FizzleEnchantingConfig config = defaultConfig();
        config.tomes.scrapTomeXpCost = 7;
        ScrapTomeHandler handler = new ScrapTomeHandler(() -> config);

        AnvilResult r = handler.handle(enchantedSword(Map.of(SHARPNESS, 3)), scrapTome(1), null)
                .orElseThrow();
        assertEquals(7, r.xpCost(),
                "operators tuning scrapTomeXpCost must see the change at claim time, not restart");
    }

    @Test
    void handle_doesNotMutateLeftInPlace() {
        ScrapTomeHandler handler = new ScrapTomeHandler(ScrapTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = scrapTome(1);

        handler.handle(left, right, null).orElseThrow();

        ItemEnchantments leftStill = left.getOrDefault(
                DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(3, leftStill.getLevel(holderFor(SHARPNESS)),
                "left stack must stay intact until vanilla's onTake consumes it — the mixin "
                        + "relies on the returned output being independent of the input");
    }

    // ---- Fixtures ----------------------------------------------------------

    private static FizzleEnchantingConfig defaultConfig() {
        return new FizzleEnchantingConfig();
    }

    private static ItemStack scrapTome(int count) {
        ItemStack stack = new ItemStack(FizzleEnchantingRegistry.SCRAP_TOME);
        stack.setCount(count);
        return stack;
    }

    private static ItemStack enchantedSword(Map<ResourceKey<Enchantment>, Integer> enchants) {
        ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (Map.Entry<ResourceKey<Enchantment>, Integer> entry : enchants.entrySet()) {
            mutable.set(holderFor(entry.getKey()), entry.getValue());
        }
        stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
        return stack;
    }

    private static Holder<Enchantment> holderFor(ResourceKey<Enchantment> key) {
        return enchantmentRegistry.getHolderOrThrow(key);
    }

    private static ResourceKey<Enchantment> mcKey(String path) {
        return ResourceKey.create(
                Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath("minecraft", path));
    }

    private static Registry<Enchantment> buildEnchantmentRegistry() {
        MappedRegistry<Enchantment> reg = new MappedRegistry<>(Registries.ENCHANTMENT, Lifecycle.stable());
        reg.register(SHARPNESS, synthetic(), RegistrationInfo.BUILT_IN);
        reg.register(UNBREAKING, synthetic(), RegistrationInfo.BUILT_IN);
        reg.register(MENDING, synthetic(), RegistrationInfo.BUILT_IN);
        return reg.freeze();
    }

    private static Enchantment synthetic() {
        HolderSet<Item> any = HolderSet.direct(List.of(
                BuiltInRegistries.ITEM.wrapAsHolder(Items.DIAMOND_SWORD)));
        Enchantment.EnchantmentDefinition def = Enchantment.definition(
                any, 1, 1,
                Enchantment.dynamicCost(1, 10),
                Enchantment.dynamicCost(51, 10),
                1, EquipmentSlotGroup.ANY);
        return new Enchantment(
                Component.literal("test"),
                def,
                HolderSet.empty(),
                DataComponentMap.EMPTY);
    }

    private static void unfreeze(Registry<?> registry) throws Exception {
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(registry, false);
    }

    private static void unfreezeIntrusive(Registry<?> registry) throws Exception {
        unfreeze(registry);
        Field intrusive = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        intrusive.setAccessible(true);
        if (intrusive.get(registry) == null) {
            intrusive.set(registry, new IdentityHashMap<>());
        }
    }

    // Suppress unused-warning; kept for future assertions that scan ordered candidates.
    @SuppressWarnings("unused")
    private static List<Holder<Enchantment>> asList(ItemEnchantments enchantments) {
        return new ArrayList<>(enchantments.keySet());
    }
}
