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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-5.2.3 — behavioural contract for {@link ExtractionTomeHandler}. Covers every acceptance
 * bullet on the task:
 * <ul>
 *   <li>3-enchant input → output book carries all three, left sword survives unenchanted with
 *       the configured damage tick applied.</li>
 *   <li>Durability clamp: a sword already at 1 durability stays at 1 — the tome never breaks
 *       the source item.</li>
 *   <li>XP cost reads {@code config.tomes.extractionTomeXpCost}; one tome consumed.</li>
 *   <li>Unenchanted left, empty slots, non-tome right, sibling-tome right, and missing config
 *       all decline cleanly — this handler must not bleed into pairings owned by
 *       {@link ScrapTomeHandler} or {@link ImprovedScrapTomeHandler}.</li>
 * </ul>
 *
 * <p>Bootstrap mirrors {@link ImprovedScrapTomeHandlerTest}'s pattern: the registry is
 * unfrozen so {@link FizzleEnchantingRegistry#EXTRACTION_TOME} registers, and a synthetic
 * enchantment registry is built so the test can construct real {@link ItemEnchantments}
 * fixtures without depending on vanilla's data-driven enchantment loading.
 */
class ExtractionTomeHandlerTest {

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
    void handle_threeEnchants_outputBookCarriesAllThreeAndSwordSurvives() {
        ExtractionTomeHandler handler = new ExtractionTomeHandler(
                ExtractionTomeHandlerTest::defaultConfig);
        FizzleEnchantingConfig config = defaultConfig();

        Map<ResourceKey<Enchantment>, Integer> loadout = Map.of(
                SHARPNESS, 3,
                UNBREAKING, 2,
                MENDING, 1);
        ItemStack left = enchantedSword(loadout);
        ItemStack right = tome(1);

        AnvilResult r = handler.handle(left, right, null).orElseThrow(() ->
                new AssertionError("enchanted sword + extraction tome must produce a claim"));

        assertSame(Items.ENCHANTED_BOOK, r.output().getItem(),
                "extraction tome always outputs a vanilla enchanted book");
        assertEquals(1, r.output().getCount(),
                "one book per salvage — input count doesn't multiply the output");

        ItemEnchantments stored = r.output().getOrDefault(
                DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(loadout.size(), stored.size(),
                "extraction transfers EVERY enchantment — that's the whole pitch of the top tier");
        for (Map.Entry<ResourceKey<Enchantment>, Integer> entry : loadout.entrySet()) {
            Holder<Enchantment> holder = holderFor(entry.getKey());
            assertEquals(entry.getValue().intValue(), stored.getLevel(holder),
                    "level preserved verbatim for " + entry.getKey().location());
        }

        assertEquals(config.tomes.extractionTomeXpCost, r.xpCost(),
                "xp cost must come from config.tomes.extractionTomeXpCost (DESIGN § Tome items)");
        assertEquals(1, r.rightConsumed(),
                "one tome per salvage — vanilla onTake will delete it when the player takes the output");

        ItemStack preserved = r.leftReplacement();
        assertFalse(preserved.isEmpty(),
                "extraction tome PRESERVES the source — leftReplacement must be non-empty, "
                        + "otherwise the AnvilMenuMixin take-tail has nothing to reinstate");
        assertSame(Items.DIAMOND_SWORD, preserved.getItem(),
                "preserved item must be the same kind as the source (diamond sword in, diamond sword out)");

        ItemEnchantments swordAfter = preserved.getOrDefault(
                DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertTrue(swordAfter.isEmpty(),
                "preserved sword must come back unenchanted — all enchants moved to the book");
        assertEquals(
                config.tomes.extractionTomeItemDamage,
                preserved.getDamageValue(),
                "damage tick applied from config.tomes.extractionTomeItemDamage");
    }

    @Test
    void handle_durabilityClamp_swordAtOneDurabilityStaysAtOne() {
        // "Durability clamped — sword with 1 durability stays at 1 after handler" (task bullet).
        // Starting damage = maxDamage - 1 means exactly one hit-point of durability left.
        // The handler's damage tick must NOT cross maxDamage; the clamped ceiling is
        // maxDamage - 1, preserving that single point. Otherwise the tome would occasionally
        // shatter the source item, violating the "preserved" contract.
        ExtractionTomeHandler handler = new ExtractionTomeHandler(
                ExtractionTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 5));
        int maxDamage = left.getMaxDamage();
        assertTrue(maxDamage > 0, "diamond sword must be damageable for this test to mean anything");
        int nearBreaking = maxDamage - 1;
        left.setDamageValue(nearBreaking);

        AnvilResult r = handler.handle(left, tome(1), null).orElseThrow();
        ItemStack preserved = r.leftReplacement();

        assertEquals(nearBreaking, preserved.getDamageValue(),
                "damage clamped at maxDamage-1 — the sword must NOT break, regardless of how "
                        + "aggressive config.tomes.extractionTomeItemDamage is");
        assertEquals(1, preserved.getMaxDamage() - preserved.getDamageValue(),
                "remaining durability stays at 1 point (durability = maxDamage - damageValue)");
    }

    @Test
    void handle_customXpCost_reflectsConfig() {
        // Operators tuning the cost should see it at claim time — not restart — mirroring the
        // pattern already locked in for the two Scrap tiers.
        FizzleEnchantingConfig config = defaultConfig();
        config.tomes.extractionTomeXpCost = 42;
        ExtractionTomeHandler handler = new ExtractionTomeHandler(() -> config);

        AnvilResult r = handler.handle(enchantedSword(Map.of(SHARPNESS, 3)), tome(1), null)
                .orElseThrow();
        assertEquals(42, r.xpCost(),
                "xp cost reads from live config, not a cached snapshot");
    }

    @Test
    void handle_customDamage_reflectsConfig() {
        FizzleEnchantingConfig config = defaultConfig();
        config.tomes.extractionTomeItemDamage = 111;
        ExtractionTomeHandler handler = new ExtractionTomeHandler(() -> config);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        AnvilResult r = handler.handle(left, tome(1), null).orElseThrow();

        assertEquals(111, r.leftReplacement().getDamageValue(),
                "damage tick reads from live config — any operator override takes effect at claim time");
    }

    @Test
    void handle_zeroDamageConfig_preservesFullDurability() {
        // Operators who want the tome to behave like Zenith's original (no durability cost) set
        // the damage to 0. The handler must then leave the preserved sword completely untouched.
        FizzleEnchantingConfig config = defaultConfig();
        config.tomes.extractionTomeItemDamage = 0;
        ExtractionTomeHandler handler = new ExtractionTomeHandler(() -> config);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        AnvilResult r = handler.handle(left, tome(1), null).orElseThrow();

        assertEquals(0, r.leftReplacement().getDamageValue(),
                "damage=0 → preserved sword keeps full durability");
    }

    @Test
    void handle_bookInput_strippedAndNotDamaged() {
        // Edge case: a pre-enchanted book as the input. getEnchantmentsForCrafting pulls the
        // STORED_ENCHANTMENTS, the tome transfers those to the output, and the preserved book
        // comes back blank. Books aren't damageable, so the damage branch is skipped.
        ExtractionTomeHandler handler = new ExtractionTomeHandler(
                ExtractionTomeHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable stored = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        stored.set(holderFor(SHARPNESS), 3);
        left.set(DataComponents.STORED_ENCHANTMENTS, stored.toImmutable());

        AnvilResult r = handler.handle(left, tome(1), null).orElseThrow();

        ItemEnchantments outStored = r.output().getOrDefault(
                DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(1, outStored.size(),
                "book input surfaces its stored enchantments onto the output book");

        ItemStack preservedBook = r.leftReplacement();
        ItemEnchantments preservedStored = preservedBook.getOrDefault(
                DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertTrue(preservedStored.isEmpty(),
                "preserved enchanted book must come back with its STORED_ENCHANTMENTS cleared — "
                        + "same \"unenchanted\" semantics as a sword's ENCHANTMENTS component");
        assertEquals(0, preservedBook.getDamageValue(),
                "books aren't damageable — damage branch must no-op");
    }

    @Test
    void handle_unenchantedLeft_declines() {
        ExtractionTomeHandler handler = new ExtractionTomeHandler(
                ExtractionTomeHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        ItemStack right = tome(1);

        Optional<AnvilResult> result = handler.handle(left, right, null);
        assertTrue(result.isEmpty(),
                "nothing to extract — handler must decline so the tome isn't consumed on a no-op");
    }

    @Test
    void handle_scrapTomeOnRight_declines() {
        // Sibling-tome defence: the Scrap Tome is handled by its own dispatcher entry and must
        // not cross-fire into this handler even though it is conceptually related.
        ExtractionTomeHandler handler = new ExtractionTomeHandler(
                ExtractionTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = new ItemStack(FizzleEnchantingRegistry.SCRAP_TOME);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "scrap tome is handled elsewhere — this handler must instance-check ExtractionTomeItem");
    }

    @Test
    void handle_improvedScrapTomeOnRight_declines() {
        ExtractionTomeHandler handler = new ExtractionTomeHandler(
                ExtractionTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = new ItemStack(FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "improved scrap tome is handled elsewhere — this handler must instance-check ExtractionTomeItem");
    }

    @Test
    void handle_nonTomeRight_declines() {
        ExtractionTomeHandler handler = new ExtractionTomeHandler(
                ExtractionTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = new ItemStack(Items.BOOK);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "right slot must be an ExtractionTomeItem — plain books belong to vanilla's enchant-combine path");
    }

    @Test
    void handle_emptySlots_declines() {
        ExtractionTomeHandler handler = new ExtractionTomeHandler(
                ExtractionTomeHandlerTest::defaultConfig);

        assertTrue(handler.handle(ItemStack.EMPTY, tome(1), null).isEmpty(),
                "empty left — handler must decline");
        assertTrue(handler.handle(enchantedSword(Map.of(SHARPNESS, 1)), ItemStack.EMPTY, null).isEmpty(),
                "empty right — handler must decline");
    }

    @Test
    void handle_configMissing_declines() {
        // getConfig() returns null before onInitialize has run; the handler must treat that as
        // "feature off" rather than NPE.
        ExtractionTomeHandler handler = new ExtractionTomeHandler(() -> null);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3));
        ItemStack right = tome(1);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "null config means onInitialize has not finished — handler must decline instead of NPE");
    }

    @Test
    void handle_doesNotMutateLeftInPlace() {
        // The mixin TAIL hook reinstates leftReplacement INTO slot 0. That write must be a
        // distinct stack from the in-slot source, otherwise the original stack's components
        // would already be modified before vanilla's onTake clears the slot — which, in edge
        // cases, could let the stripped enchantments leak back into the display slot.
        ExtractionTomeHandler handler = new ExtractionTomeHandler(
                ExtractionTomeHandlerTest::defaultConfig);

        ItemStack left = enchantedSword(Map.of(SHARPNESS, 3, UNBREAKING, 2));
        AnvilResult r = handler.handle(left, tome(1), null).orElseThrow();

        assertNotSame(left, r.leftReplacement(),
                "preserved stack must be a copy — mutating the copy must not touch the in-slot source");

        ItemEnchantments leftStill = left.getOrDefault(
                DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(3, leftStill.getLevel(holderFor(SHARPNESS)),
                "source stack must stay intact until vanilla's onTake consumes it");
        assertEquals(2, leftStill.getLevel(holderFor(UNBREAKING)),
                "unbreaking level on the original sword must remain unchanged");
        assertEquals(0, left.getDamageValue(),
                "source sword's damage must not be touched — only the preserved copy gets damaged");
    }

    @Test
    void stripAndDamage_nonDamageable_leavesDamageZero() {
        // Direct helper exposure — locks the non-damageable fast-path so future refactors can't
        // silently make damage apply to books or other indestructible items.
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable stored = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        stored.set(holderFor(SHARPNESS), 5);
        book.set(DataComponents.STORED_ENCHANTMENTS, stored.toImmutable());

        ItemStack stripped = ExtractionTomeHandler.stripAndDamage(book, 999);

        assertEquals(0, stripped.getDamageValue(),
                "damage-delta must not apply to non-damageable items — isDamageableItem gate");
        assertTrue(stripped.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty(),
                "STORED_ENCHANTMENTS must be cleared even on non-damageable items");
    }

    // ---- Fixtures ----------------------------------------------------------

    private static FizzleEnchantingConfig defaultConfig() {
        return new FizzleEnchantingConfig();
    }

    private static ItemStack tome(int count) {
        ItemStack stack = new ItemStack(FizzleEnchantingRegistry.EXTRACTION_TOME);
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
