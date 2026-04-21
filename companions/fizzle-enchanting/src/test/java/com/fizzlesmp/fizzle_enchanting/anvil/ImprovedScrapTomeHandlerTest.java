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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-5.2.2 — behavioural contract for {@link ImprovedScrapTomeHandler}. The mid-tier tome mirrors
 * {@link ScrapTomeHandler} except every enchantment from the source item transfers onto the
 * output book — there is no RNG, no seed, no pick index.
 *
 * <p>Tests cover each acceptance bullet:
 * <ul>
 *   <li>3-enchant input → output book has all 3 (the headline acceptance).</li>
 *   <li>XP cost reads {@code config.tomes.improvedScrapTomeXpCost}.</li>
 *   <li>One tome consumed; left destruction follows from vanilla's {@code onTake}.</li>
 *   <li>Unenchanted left declines.</li>
 *   <li>Non-improved-scrap right declines — plain Scrap Tome must not trigger this handler
 *       even though it is a sibling item.</li>
 *   <li>Missing config / empty slots all decline cleanly.</li>
 * </ul>
 *
 * <p>Bootstrap mirrors {@link ScrapTomeHandlerTest}'s pattern: the registry is unfrozen so
 * {@link FizzleEnchantingRegistry#IMPROVED_SCRAP_TOME} registers, and a synthetic enchantment
 * registry is built so the test can construct real {@link ItemEnchantments} fixtures without
 * depending on vanilla's data-driven enchantment loading.
 */
class ImprovedScrapTomeHandlerTest {

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
    void handle_threeEnchantInput_outputBookCarriesAllThree() {
        ImprovedScrapTomeHandler handler = new ImprovedScrapTomeHandler(
                ImprovedScrapTomeHandlerTest::defaultConfig);

        Map<ResourceKey<Enchantment>, Integer> loadout = Map.of(
                SHARPNESS, 3,
                UNBREAKING, 2,
                MENDING, 1);
        ItemStack left = enchantedSword(loadout);
        ItemStack right = tome(1);

        AnvilResult r = handler.handle(left, right, null).orElseThrow(() ->
                new AssertionError("enchanted sword + improved scrap tome must produce a claim"));

        assertSame(Items.ENCHANTED_BOOK, r.output().getItem(),
                "improved scrap tome always outputs a vanilla enchanted book");
        assertEquals(1, r.output().getCount(),
                "exactly one book is produced regardless of input stack size");

        ItemEnchantments stored = r.output().getOrDefault(
                DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(loadout.size(), stored.size(),
                "improved scrap tome keeps EVERY enchantment — that's how it differs from the base Scrap tier");

        for (Map.Entry<ResourceKey<Enchantment>, Integer> entry : loadout.entrySet()) {
            Holder<Enchantment> holder = holderFor(entry.getKey());
            assertEquals(entry.getValue().intValue(), stored.getLevel(holder),
                    "level must carry over verbatim for " + entry.getKey().location());
        }

        assertEquals(defaultConfig().tomes.improvedScrapTomeXpCost, r.xpCost(),
                "xp cost must come from config.tomes.improvedScrapTomeXpCost (DESIGN § Tome items)");
        assertEquals(1, r.rightConsumed(),
                "one tome per salvage — vanilla onTake will delete it when the player takes the output");
    }

    @Test
    void handle_singleEnchantInput_outputBookCarriesThatOne() {
        ImprovedScrapTomeHandler handler = new ImprovedScrapTomeHandler(
                ImprovedScrapTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(MENDING, 1));
        AnvilResult r = handler.handle(left, tome(1), null).orElseThrow();

        ItemEnchantments stored = r.output().getOrDefault(
                DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(1, stored.size(),
                "single-enchant input produces a single-enchant book — nothing else to transfer");
        assertEquals(MENDING,
                stored.keySet().iterator().next().unwrapKey().orElseThrow(),
                "mending must be the transferred enchantment");
        assertEquals(1, stored.getLevel(holderFor(MENDING)),
                "level preserved verbatim");
    }

    @Test
    void handle_deterministic_sameInputsSameOutput() {
        // No RNG means two calls against the same loadout must produce identical output books.
        // This locks the "no seeded pick" contract — any accidental reintroduction of randomness
        // would flip the two stored sets out of sync.
        ImprovedScrapTomeHandler handler = new ImprovedScrapTomeHandler(
                ImprovedScrapTomeHandlerTest::defaultConfig);

        Map<ResourceKey<Enchantment>, Integer> loadout = Map.of(
                SHARPNESS, 3,
                UNBREAKING, 2,
                MENDING, 1);

        ItemEnchantments first = handler.handle(enchantedSword(loadout), tome(1), null)
                .orElseThrow()
                .output()
                .getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments second = handler.handle(enchantedSword(loadout), tome(1), null)
                .orElseThrow()
                .output()
                .getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);

        assertEquals(first.keySet(), second.keySet(),
                "improved scrap is deterministic — the key set must not drift between calls");
        for (Holder<Enchantment> key : first.keySet()) {
            assertEquals(first.getLevel(key), second.getLevel(key),
                    "level for " + key.unwrapKey() + " must match across calls");
        }
    }

    @Test
    void handle_unenchantedLeft_declines() {
        ImprovedScrapTomeHandler handler = new ImprovedScrapTomeHandler(
                ImprovedScrapTomeHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        ItemStack right = tome(1);

        Optional<AnvilResult> result = handler.handle(left, right, null);
        assertTrue(result.isEmpty(),
                "nothing to salvage — handler must decline so the tome isn't consumed on a no-op");
    }

    @Test
    void handle_scrapTomeOnRight_declines() {
        // Defensive: the base Scrap Tome is handled by a separate dispatcher entry. Instance-check
        // must be strict on ImprovedScrapTomeItem so the two tiers don't cross-fire.
        ImprovedScrapTomeHandler handler = new ImprovedScrapTomeHandler(
                ImprovedScrapTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = new ItemStack(FizzleEnchantingRegistry.SCRAP_TOME);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "scrap tome is handled elsewhere — this handler must instance-check ImprovedScrapTomeItem");
    }

    @Test
    void handle_nonTomeRight_declines() {
        ImprovedScrapTomeHandler handler = new ImprovedScrapTomeHandler(
                ImprovedScrapTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = new ItemStack(Items.BOOK);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "right slot must be an ImprovedScrapTomeItem — plain books belong to vanilla's enchant-combine path");
    }

    @Test
    void handle_emptySlots_declines() {
        ImprovedScrapTomeHandler handler = new ImprovedScrapTomeHandler(
                ImprovedScrapTomeHandlerTest::defaultConfig);

        assertTrue(handler.handle(ItemStack.EMPTY, tome(1), null).isEmpty(),
                "empty left — handler must decline");
        assertTrue(handler.handle(enchantedSword(Map.of(SHARPNESS, 1)), ItemStack.EMPTY, null).isEmpty(),
                "empty right — handler must decline");
    }

    @Test
    void handle_configMissing_declines() {
        // getConfig() returns null before onInitialize has run; the handler must treat that as
        // "feature off" rather than NPE.
        ImprovedScrapTomeHandler handler = new ImprovedScrapTomeHandler(() -> null);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = tome(1);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "null config means onInitialize has not finished — handler must decline instead of NPE");
    }

    @Test
    void handle_customXpCost_reflectsConfig() {
        FizzleEnchantingConfig config = defaultConfig();
        config.tomes.improvedScrapTomeXpCost = 11;
        ImprovedScrapTomeHandler handler = new ImprovedScrapTomeHandler(() -> config);

        AnvilResult r = handler.handle(enchantedSword(Map.of(SHARPNESS, 3)), tome(1), null)
                .orElseThrow();
        assertEquals(11, r.xpCost(),
                "operators tuning improvedScrapTomeXpCost must see the change at claim time, not restart");
    }

    @Test
    void handle_doesNotMutateLeftInPlace() {
        ImprovedScrapTomeHandler handler = new ImprovedScrapTomeHandler(
                ImprovedScrapTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3, UNBREAKING, 2));
        ItemStack right = tome(1);

        AnvilResult r = handler.handle(left, right, null).orElseThrow();
        assertNotNull(r);

        ItemEnchantments leftStill = left.getOrDefault(
                DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(3, leftStill.getLevel(holderFor(SHARPNESS)),
                "left stack must stay intact until vanilla's onTake consumes it — the mixin "
                        + "relies on the returned output being independent of the input");
        assertEquals(2, leftStill.getLevel(holderFor(UNBREAKING)),
                "unbreaking level must remain unchanged on the source item");
    }

    // ---- Fixtures ----------------------------------------------------------

    private static FizzleEnchantingConfig defaultConfig() {
        return new FizzleEnchantingConfig();
    }

    private static ItemStack tome(int count) {
        ItemStack stack = new ItemStack(FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME);
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
}
