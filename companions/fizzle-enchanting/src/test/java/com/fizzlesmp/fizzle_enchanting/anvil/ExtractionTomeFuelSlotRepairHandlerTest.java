package com.fizzlesmp.fizzle_enchanting.anvil;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.mojang.serialization.Lifecycle;
import net.minecraft.SharedConstants;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-5.2.4 — behavioural contract for {@link ExtractionTomeFuelSlotRepairHandler}. The acceptance
 * reduces to "damaged item + Extraction Tome → output is the same item with
 * {@code floor(maxDamage * repairPercent)} durability restored, tome consumed, XP cost reads from
 * {@code config.tomes.extractionTomeXpCost}."
 *
 * <p>The handler deliberately declines when the left carries enchantments so the sibling
 * {@link ExtractionTomeHandler} claims the pair instead — verified by
 * {@link #handle_enchantedDamagedLeft_declines()}. That ordering is what keeps the two
 * Extraction-Tome handlers from fighting over the same right-slot item.
 *
 * <p>Bootstrap mirrors {@link ExtractionTomeHandlerTest}: registries are unfrozen so
 * {@link FizzleEnchantingRegistry#EXTRACTION_TOME} registers, and a synthetic enchantment
 * registry is built so the sibling-handler check can construct real {@link ItemEnchantments}.
 */
class ExtractionTomeFuelSlotRepairHandlerTest {

    private static final ResourceKey<Enchantment> SHARPNESS = mcKey("sharpness");

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
    void handle_damagedUnenchanted_restoresConfiguredPercentOfMaxDurability() {
        // Headline acceptance: "Damaged sword + tome → durability increases by
        // repairPercent * maxDurability; tome consumed."
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(
                ExtractionTomeFuelSlotRepairHandlerTest::defaultConfig);
        FizzleEnchantingConfig config = defaultConfig();

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        int maxDamage = left.getMaxDamage();
        int startingDamage = maxDamage - 50;
        left.setDamageValue(startingDamage);

        AnvilResult r = handler.handle(left, tome(1), null).orElseThrow(() ->
                new AssertionError("damaged sword + extraction tome must produce a repair claim"));

        int expectedRestore = (int) Math.floor(maxDamage * config.tomes.extractionTomeRepairPercent);
        assertSame(Items.DIAMOND_SWORD, r.output().getItem(),
                "repair output is the same item kind as the source — diamond sword stays a diamond sword");
        assertEquals(
                Math.max(0, startingDamage - expectedRestore),
                r.output().getDamageValue(),
                "damage reduced by floor(maxDamage * repairPercent) — the configured restore amount");
        assertEquals(1, r.rightConsumed(), "one tome consumed per repair — matches the acceptance");
        assertEquals(config.tomes.extractionTomeXpCost, r.xpCost(),
                "XP cost must match standard Extraction — the task explicitly wires the same field");
        assertNull(r.leftReplacement(),
                "repair path has no leftReplacement — the output IS the repaired item, "
                        + "vanilla's onTake correctly clears slot 0 of the original damaged copy");
    }

    @Test
    void handle_nearlyPristine_clampsDamageAtZero() {
        // Sword with 2 damage points, maxDamage >1000 → repairPercent * maxDamage >>> 2. The
        // floor(0, startingDamage - restored) clamp keeps the output at 0, never negative.
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(
                ExtractionTomeFuelSlotRepairHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        left.setDamageValue(2);

        AnvilResult r = handler.handle(left, tome(1), null).orElseThrow();
        assertEquals(0, r.output().getDamageValue(),
                "overshoot repair must clamp at 0 — no negative damage values, no maxDamage bump");
    }

    @Test
    void handle_customRepairPercent_reflectsLiveConfig() {
        // Operators tuning repairPercent should see the new value at claim time, same pattern as
        // the rest of the config-driven handlers (reads via configSupplier, not cached).
        FizzleEnchantingConfig config = defaultConfig();
        config.tomes.extractionTomeRepairPercent = 0.5;
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(() -> config);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        int maxDamage = left.getMaxDamage();
        int startingDamage = maxDamage - 10;
        left.setDamageValue(startingDamage);

        AnvilResult r = handler.handle(left, tome(1), null).orElseThrow();
        int expectedRestore = (int) Math.floor(maxDamage * 0.5);
        assertEquals(
                Math.max(0, startingDamage - expectedRestore),
                r.output().getDamageValue(),
                "overriding repairPercent at runtime must change the restore amount at the next claim");
    }

    @Test
    void handle_customXpCost_reflectsLiveConfig() {
        FizzleEnchantingConfig config = defaultConfig();
        config.tomes.extractionTomeXpCost = 42;
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(() -> config);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        left.setDamageValue(100);

        AnvilResult r = handler.handle(left, tome(1), null).orElseThrow();
        assertEquals(42, r.xpCost(),
                "xp cost reads from live config, not a cached snapshot — matches T-5.2.3 pattern");
    }

    @Test
    void handle_undamagedLeft_declines() {
        // "Damaged item in slot A" — pristine items have nothing to repair. Silently declining
        // instead of running a zero-effect claim prevents the player from burning a tome for no
        // gain (the dispatcher would otherwise still consume the tome on output-take).
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(
                ExtractionTomeFuelSlotRepairHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        assertEquals(0, left.getDamageValue(), "fresh stack must be undamaged for this test to mean anything");

        assertTrue(handler.handle(left, tome(1), null).isEmpty(),
                "undamaged left — no repair work available, handler must decline");
    }

    @Test
    void handle_nonDamageableLeft_declines() {
        // Dirt is not damageable. The handler must treat "no damage axis" the same as "no damage
        // to fix" — otherwise every non-tool item placed in slot A would silently eat a tome.
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(
                ExtractionTomeFuelSlotRepairHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIRT);
        assertTrue(handler.handle(left, tome(1), null).isEmpty(),
                "non-damageable items have no repair axis — handler must decline");
    }

    @Test
    void handle_enchantedDamagedLeft_declines() {
        // Sibling-handler boundary: enchanted damaged sword belongs to ExtractionTomeHandler. If
        // this handler claimed it first, the player would lose the enchantments to a "repair" —
        // catastrophic UX. The registration order in FizzleAnvilHandlers puts extraction before
        // repair precisely so the extraction claim wins, but the repair handler still declines
        // on enchanted input as a defence-in-depth check.
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(
                ExtractionTomeFuelSlotRepairHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        left.setDamageValue(200);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(enchantmentRegistry.getHolderOrThrow(SHARPNESS), 3);
        left.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());

        assertTrue(handler.handle(left, tome(1), null).isEmpty(),
                "enchanted left belongs to ExtractionTomeHandler — repair handler must defer");
    }

    @Test
    void handle_zeroRepairPercent_declines() {
        // Operators who disable repair by setting the percent to 0 must not see the tome consumed
        // on a no-op repair. Declining here lets the dispatcher fall through to vanilla so the
        // anvil UI doesn't offer a free output at all.
        FizzleEnchantingConfig config = defaultConfig();
        config.tomes.extractionTomeRepairPercent = 0.0;
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(() -> config);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        left.setDamageValue(500);

        assertTrue(handler.handle(left, tome(1), null).isEmpty(),
                "repairPercent = 0 → no repair work — handler must decline, tome stays intact");
    }

    @Test
    void handle_scrapTomeOnRight_declines() {
        // Sibling-tome defence: the Scrap Tome lives in its own handler. Mis-binding here would
        // let a cheap tome accidentally repair items — wrong economy.
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(
                ExtractionTomeFuelSlotRepairHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        left.setDamageValue(200);
        ItemStack right = new ItemStack(FizzleEnchantingRegistry.SCRAP_TOME);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "scrap tome is handled elsewhere — this handler must instance-check ExtractionTomeItem");
    }

    @Test
    void handle_improvedScrapTomeOnRight_declines() {
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(
                ExtractionTomeFuelSlotRepairHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        left.setDamageValue(200);
        ItemStack right = new ItemStack(FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "improved scrap tome is handled elsewhere — this handler must instance-check ExtractionTomeItem");
    }

    @Test
    void handle_nonTomeRight_declines() {
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(
                ExtractionTomeFuelSlotRepairHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        left.setDamageValue(200);
        ItemStack right = new ItemStack(Items.BOOK);

        assertTrue(handler.handle(left, right, null).isEmpty(),
                "right slot must be an ExtractionTomeItem — non-tome items belong to vanilla's repair path");
    }

    @Test
    void handle_emptySlots_declines() {
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(
                ExtractionTomeFuelSlotRepairHandlerTest::defaultConfig);

        assertTrue(handler.handle(ItemStack.EMPTY, tome(1), null).isEmpty(),
                "empty left — handler must decline");
        assertTrue(handler.handle(damagedSword(200), ItemStack.EMPTY, null).isEmpty(),
                "empty right — handler must decline");
    }

    @Test
    void handle_configMissing_declines() {
        // Same defensive stance as the sibling handlers — pre-onInitialize dispatch must not NPE.
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(() -> null);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        left.setDamageValue(200);

        assertTrue(handler.handle(left, tome(1), null).isEmpty(),
                "null config means onInitialize has not finished — handler must decline instead of NPE");
    }

    @Test
    void handle_doesNotMutateLeftInPlace() {
        // Vanilla's onTake clears slot 0 after the player takes the output — but until that runs,
        // the display must still show the original damaged item. If the handler mutated `left` in
        // place, the UI would flash the repaired state before the take actually committed.
        ExtractionTomeFuelSlotRepairHandler handler = new ExtractionTomeFuelSlotRepairHandler(
                ExtractionTomeFuelSlotRepairHandlerTest::defaultConfig);

        ItemStack left = new ItemStack(Items.DIAMOND_SWORD);
        int originalDamage = 300;
        left.setDamageValue(originalDamage);

        AnvilResult r = handler.handle(left, tome(1), null).orElseThrow();
        assertNotSame(left, r.output(),
                "output must be a copy — mutating the copy must not touch the source in slot 0");
        assertEquals(originalDamage, left.getDamageValue(),
                "source stack's damage must stay intact until vanilla's onTake consumes it");
    }

    @Test
    void repairAmount_roundsDown() {
        // Locks the rounding contract — floor, not round. A sword with 10 max damage and a 0.25
        // repair percent yields 2, not 3. Refactors that swap floor for round or ceil would
        // silently bump every repair claim up by one point.
        assertEquals(2, ExtractionTomeFuelSlotRepairHandler.repairAmount(10, 0.25),
                "floor(10 * 0.25) = 2");
        assertEquals(0, ExtractionTomeFuelSlotRepairHandler.repairAmount(3, 0.25),
                "floor(3 * 0.25) = 0 — the sub-integer case the handler uses to decline");
        assertEquals(1561, ExtractionTomeFuelSlotRepairHandler.repairAmount(1561, 1.0),
                "100% repair on a diamond sword returns every durability point");
        assertEquals(0, ExtractionTomeFuelSlotRepairHandler.repairAmount(0, 0.5),
                "zero max damage (non-damageable) → zero restore");
        assertEquals(0, ExtractionTomeFuelSlotRepairHandler.repairAmount(1561, 0.0),
                "zero percent → zero restore");
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

    private static ItemStack damagedSword(int damage) {
        ItemStack stack = new ItemStack(Items.DIAMOND_SWORD);
        stack.setDamageValue(damage);
        return stack;
    }

    private static ResourceKey<Enchantment> mcKey(String path) {
        return ResourceKey.create(
                Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath("minecraft", path));
    }

    private static Registry<Enchantment> buildEnchantmentRegistry() {
        MappedRegistry<Enchantment> reg = new MappedRegistry<>(Registries.ENCHANTMENT, Lifecycle.stable());
        reg.register(SHARPNESS, synthetic(), RegistrationInfo.BUILT_IN);
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
