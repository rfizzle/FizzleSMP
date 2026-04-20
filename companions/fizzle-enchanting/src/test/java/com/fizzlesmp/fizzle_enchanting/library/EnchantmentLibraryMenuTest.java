package com.fizzlesmp.fizzle_enchanting.library;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.mojang.serialization.Lifecycle;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-4.4.2 — exercises the menu's three player-visible behaviors against a stand-in BE fixture:
 * the deposit-slot auto-absorb hook, the click-time extraction gating, and the shift-click
 * max-affordable solver. Production click resolution flows through the dynamic enchantment
 * registry on the player's level; tests bypass that by calling
 * {@link EnchantmentLibraryMenu#attemptExtract} directly with a synthetic {@link Holder} so the
 * assertions stay focused on menu logic, not registry plumbing (which is exercised separately).
 *
 * <p>Bootstrap follows the same dance as the sibling library tests: vanilla bootstrap freezes
 * {@link BuiltInRegistries}, so {@link BlockEntityType.Builder#build} (which mints intrusive
 * holders against {@code BLOCK_ENTITY_TYPE}) needs the registry thawed before construction.
 * {@link FizzleEnchantingRegistry#register()} runs to register {@link FizzleEnchantingRegistry#LIBRARY_MENU}
 * so the menu's {@code super(...)} call can reference it.
 */
class EnchantmentLibraryMenuTest {

    private static final ResourceKey<Enchantment> SHARPNESS = key("sharpness");
    private static final ResourceKey<Enchantment> UNBREAKING = key("unbreaking");

    private static Registry<Enchantment> enchantmentRegistry;
    private static BlockEntityType<TestLibraryBlockEntity> basicType;
    private static BlockState state;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreeze(BuiltInRegistries.MENU, false);
        unfreeze(BuiltInRegistries.BLOCK, true);
        unfreeze(BuiltInRegistries.ITEM, true);
        unfreeze(BuiltInRegistries.BLOCK_ENTITY_TYPE, true);

        FizzleEnchantingRegistry.register();

        basicType = BlockEntityType.Builder.of(
                (pos, blockState) -> new TestLibraryBlockEntity(basicType, pos, blockState, 16),
                Blocks.BOOKSHELF).build(null);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath("fizzle_enchanting", "test_library_basic_menu"),
                basicType);

        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
        BuiltInRegistries.MENU.freeze();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();

        enchantmentRegistry = buildEnchantmentRegistry();
        state = Blocks.BOOKSHELF.defaultBlockState();
    }

    // ---- structural sanity --------------------------------------------------

    @Test
    void menuType_registeredUnderLibraryId() {
        // The factory path through Fabric's ExtendedScreenHandlerType only lights up when the
        // type is in the MENU registry — guard the pairing here so a copy-paste mistake on the
        // registration call surfaces as a unit-test failure rather than a silent client-open
        // crash.
        assertSame(FizzleEnchantingRegistry.LIBRARY_MENU,
                BuiltInRegistries.MENU.get(ResourceLocation.fromNamespaceAndPath(
                        "fizzle_enchanting", "library")));
    }

    @Test
    void menu_exposesThreeIoSlots() {
        EnchantmentLibraryMenu menu = newMenu(newBasic());
        assertEquals(EnchantmentLibraryMenu.IO_SLOT_COUNT, menu.slots.size(),
                "test-only constructor wires three IO slots and skips player inventory");
    }

    // ---- deposit-slot auto-absorb -------------------------------------------

    @Test
    void depositSlot_setChangedAbsorbsBookAndClearsSlot() {
        TestLibraryBlockEntity tile = newBasic();
        EnchantmentLibraryMenu menu = newMenu(tile);

        ItemStack book = singleEnchantBook(SHARPNESS, 3);
        // Slot.set fires Slot.setChanged, which our deposit-slot override re-routes through
        // absorbDepositSlot — the same code path vanilla takes when a player drops a book in.
        menu.getSlot(EnchantmentLibraryMenu.DEPOSIT_SLOT).set(book);

        assertEquals(4, tile.getPoints().getInt(SHARPNESS),
                "Sharpness-3 absorbs as 2^(3-1) = 4 points into the pool");
        assertEquals(3, tile.getMaxLevels().getInt(SHARPNESS),
                "deposit-time max-level tracking matches the deposited book");
        assertTrue(menu.ioInv.getItem(EnchantmentLibraryMenu.DEPOSIT_SLOT).isEmpty(),
                "deposit slot must clear once the book is absorbed");
    }

    @Test
    void depositSlot_emptyChangePassesThrough() {
        TestLibraryBlockEntity tile = newBasic();
        EnchantmentLibraryMenu menu = newMenu(tile);

        // The clear-to-empty pass after absorb fires Slot.setChanged again. The absorb path must
        // tolerate that without re-entering the deposit logic on an empty stack.
        menu.getSlot(EnchantmentLibraryMenu.DEPOSIT_SLOT).set(ItemStack.EMPTY);

        assertTrue(tile.getPoints().isEmpty(),
                "no book in slot → no pool mutation, no spurious entries");
    }

    // ---- extraction gating --------------------------------------------------

    @Test
    void attemptExtract_deniesWhenMaxLevelTooLow() {
        TestLibraryBlockEntity tile = newBasic();
        // Pool is large enough for Sharpness-V (16 points) but maxLevels caps at 1 — DESIGN's
        // "grind commons, extract rares" guard.
        tile.getPoints().put(SHARPNESS, tile.getMaxPoints());
        tile.getMaxLevels().put(SHARPNESS, 1);
        EnchantmentLibraryMenu menu = newMenu(tile);
        // Pre-load extract slot with a Sharpness-IV book so curLvl + 1 = 5 → exceeds maxLevels=1.
        menu.ioInv.setItem(EnchantmentLibraryMenu.EXTRACT_SLOT,
                singleEnchantBook(SHARPNESS, 4));

        boolean result = menu.attemptExtract(holderFor(SHARPNESS), false);

        assertFalse(result, "attemptExtract returns false on decline");
        assertEquals(tile.getMaxPoints(), tile.getPoints().getInt(SHARPNESS),
                "pool untouched — no partial debit on a declined click");
        ItemStack outSlot = menu.ioInv.getItem(EnchantmentLibraryMenu.EXTRACT_SLOT);
        ItemEnchantments stored = outSlot.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(4, stored.getLevel(holderFor(SHARPNESS)),
                "extract-slot book unchanged — still Sharpness-IV");
    }

    @Test
    void attemptExtract_succeedsAndUpgradesBookInPlace() {
        TestLibraryBlockEntity tile = newBasic();
        tile.getMaxLevels().put(SHARPNESS, 5);
        tile.getPoints().put(SHARPNESS, 32);
        EnchantmentLibraryMenu menu = newMenu(tile);
        menu.ioInv.setItem(EnchantmentLibraryMenu.EXTRACT_SLOT,
                singleEnchantBook(SHARPNESS, 4));

        boolean result = menu.attemptExtract(holderFor(SHARPNESS), false);

        assertTrue(result, "matching maxLevels + sufficient points → extract succeeds");
        ItemStack outSlot = menu.ioInv.getItem(EnchantmentLibraryMenu.EXTRACT_SLOT);
        ItemEnchantments stored = outSlot.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(5, stored.getLevel(holderFor(SHARPNESS)),
                "book upgraded in place to curLvl + 1 = V");
        assertEquals(24, tile.getPoints().getInt(SHARPNESS),
                "pool debited points(5) - points(4) = 8; 32 - 8 = 24");
    }

    @Test
    void attemptExtract_succeedsOnEmptySlotMintingFreshBook() {
        TestLibraryBlockEntity tile = newBasic();
        tile.getMaxLevels().put(SHARPNESS, 5);
        tile.getPoints().put(SHARPNESS, 16);
        EnchantmentLibraryMenu menu = newMenu(tile);
        // Empty extract slot — attemptExtract should mint a fresh ENCHANTED_BOOK at level 1.

        boolean result = menu.attemptExtract(holderFor(SHARPNESS), false);

        assertTrue(result);
        ItemStack outSlot = menu.ioInv.getItem(EnchantmentLibraryMenu.EXTRACT_SLOT);
        assertSame(Items.ENCHANTED_BOOK, outSlot.getItem(),
                "empty slot path mints a vanilla enchanted book");
        ItemEnchantments stored = outSlot.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(1, stored.getLevel(holderFor(SHARPNESS)),
                "no curLvl on a fresh book → target = 0 + 1 = 1");
        assertEquals(15, tile.getPoints().getInt(SHARPNESS),
                "pool debited points(1) - points(0) = 1; 16 - 1 = 15");
    }

    @Test
    void attemptExtract_preservesOtherEnchantsOnUpgradedBook() {
        // Upgrading Sharpness on a multi-enchant book must leave the other enchant untouched
        // (Zenith's "set" path on Mutable preserves siblings).
        TestLibraryBlockEntity tile = newBasic();
        tile.getMaxLevels().put(SHARPNESS, 5);
        tile.getPoints().put(SHARPNESS, 32);
        EnchantmentLibraryMenu menu = newMenu(tile);

        ItemStack mixed = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(holderFor(SHARPNESS), 4);
        mutable.set(holderFor(UNBREAKING), 2);
        mixed.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
        menu.ioInv.setItem(EnchantmentLibraryMenu.EXTRACT_SLOT, mixed);

        assertTrue(menu.attemptExtract(holderFor(SHARPNESS), false));
        ItemEnchantments stored = menu.ioInv.getItem(EnchantmentLibraryMenu.EXTRACT_SLOT)
                .getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(5, stored.getLevel(holderFor(SHARPNESS)),
                "Sharpness upgraded to V");
        assertEquals(2, stored.getLevel(holderFor(UNBREAKING)),
                "Unbreaking-II preserved through the Sharpness upgrade");
    }

    // ---- shift-click max-affordable -----------------------------------------

    @Test
    void attemptExtract_shiftSolvesMaxAffordableFromEmptySlot() {
        // pool = 64 points → log2(64) + 1 = 7. Cap at maxLevels = 5 → final target is 5.
        TestLibraryBlockEntity tile = newBasic();
        tile.getMaxLevels().put(SHARPNESS, 5);
        tile.getPoints().put(SHARPNESS, 64);
        EnchantmentLibraryMenu menu = newMenu(tile);

        boolean result = menu.attemptExtract(holderFor(SHARPNESS), true);

        assertTrue(result);
        ItemEnchantments stored = menu.ioInv.getItem(EnchantmentLibraryMenu.EXTRACT_SLOT)
                .getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(5, stored.getLevel(holderFor(SHARPNESS)),
                "shift-click clamps log2(pool) + 1 against the maxLevels cap");
        assertEquals(64 - 16, tile.getPoints().getInt(SHARPNESS),
                "pool debited points(5) = 16; 64 - 16 = 48");
    }

    @Test
    void attemptExtract_shiftRespectsExistingBookLevel() {
        // pool 32 + curLvl 4 (8 pts contribution) → log2(40) + 1 ≈ 6.32 → 6. Cap at 7 lets it
        // through; the book climbs IV → VI on a single shift-click.
        TestLibraryBlockEntity tile = newBasic();
        tile.getMaxLevels().put(SHARPNESS, 7);
        tile.getPoints().put(SHARPNESS, 32);
        EnchantmentLibraryMenu menu = newMenu(tile);
        menu.ioInv.setItem(EnchantmentLibraryMenu.EXTRACT_SLOT,
                singleEnchantBook(SHARPNESS, 4));

        boolean result = menu.attemptExtract(holderFor(SHARPNESS), true);

        assertTrue(result);
        ItemEnchantments stored = menu.ioInv.getItem(EnchantmentLibraryMenu.EXTRACT_SLOT)
                .getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(6, stored.getLevel(holderFor(SHARPNESS)),
                "shift-click upgrade IV → VI uses 1 + log2(32 + points(4)) = 6");
        assertEquals(32 - (32 - 8), tile.getPoints().getInt(SHARPNESS),
                "pool debited points(6) - points(4) = 32 - 8 = 24; 32 - 24 = 8");
    }

    @Test
    void attemptExtract_shiftClampsToMaxLevels() {
        // pool gives the budget for level 10 but maxLevels caps at 3 — final target must be 3.
        TestLibraryBlockEntity tile = newBasic();
        tile.getMaxLevels().put(SHARPNESS, 3);
        tile.getPoints().put(SHARPNESS, 1024);
        EnchantmentLibraryMenu menu = newMenu(tile);

        boolean result = menu.attemptExtract(holderFor(SHARPNESS), true);

        assertTrue(result);
        ItemEnchantments stored = menu.ioInv.getItem(EnchantmentLibraryMenu.EXTRACT_SLOT)
                .getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(3, stored.getLevel(holderFor(SHARPNESS)),
                "shift extract never exceeds maxLevels even with a saturated pool");
    }

    // ---- BE listener set (T-4.4.4) -----------------------------------------

    @Test
    void listener_addedOnConstructionAndRemovedOnClose() {
        // The "open menu, mutate BE externally, close menu → list goes [menu] → []" round-trip.
        // Verifies both the registration on construction and the deregistration on removed(),
        // with a notifier hook proving the BE actually drives the menu's onChanged path.
        TestLibraryBlockEntity tile = newBasic();
        AtomicInteger counter = new AtomicInteger(0);

        EnchantmentLibraryMenu menu = newMenu(tile);
        menu.setNotifier(counter::incrementAndGet);

        assertEquals(1, tile.activeContainersForTest().size(),
                "constructor must register the menu on the BE listener set");
        assertTrue(tile.activeContainersForTest().contains(menu),
                "registered listener is the menu itself, not a wrapper");

        // Mutate the BE through its public deposit path — same surface a hopper or sibling menu
        // would hit. setChanged → notifyListeners fires the menu's onChanged → notifier.
        tile.depositBook(singleEnchantBook(SHARPNESS, 3));
        assertEquals(1, counter.get(),
                "external mutation must fire the registered listener exactly once");

        // Close the menu. Pass null player — AbstractContainerMenu.removed only acts on
        // ServerPlayer for the carried-stack drop, so null is the safe sentinel here.
        menu.removed(null);

        assertTrue(tile.activeContainersForTest().isEmpty(),
                "removed() must drop the menu from the listener set — no leak");

        // Subsequent mutations must not reach the closed menu.
        counter.set(0);
        tile.depositBook(singleEnchantBook(SHARPNESS, 4));
        assertEquals(0, counter.get(),
                "deregistered menu must not receive further callbacks");
    }

    @Test
    void listener_addListenerIsIdempotent() {
        // HashSet semantics — registering the same menu twice keeps a single entry. Defensive
        // against a reload path that re-registers an already-bound menu.
        TestLibraryBlockEntity tile = newBasic();
        EnchantmentLibraryMenu menu = newMenu(tile);

        tile.addListener(menu);
        assertEquals(1, tile.activeContainersForTest().size(),
                "duplicate addListener must not grow the set");
    }

    // ---- helpers ------------------------------------------------------------

    private static EnchantmentLibraryMenu newMenu(TestLibraryBlockEntity tile) {
        EnchantmentLibraryMenu menu = new EnchantmentLibraryMenu(0, tile);
        assertNotNull(menu, "test ctor must produce a usable menu");
        return menu;
    }

    private static TestLibraryBlockEntity newBasic() {
        return new TestLibraryBlockEntity(basicType, BlockPos.ZERO, state, 16);
    }

    private static ItemStack singleEnchantBook(ResourceKey<Enchantment> enchant, int level) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(holderFor(enchant), level);
        stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
        return stack;
    }

    private static Holder<Enchantment> holderFor(ResourceKey<Enchantment> key) {
        return enchantmentRegistry.getHolderOrThrow(key);
    }

    private static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(
                Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath("minecraft", path));
    }

    private static Registry<Enchantment> buildEnchantmentRegistry() {
        MappedRegistry<Enchantment> reg = new MappedRegistry<>(Registries.ENCHANTMENT, Lifecycle.stable());
        register(reg, SHARPNESS);
        register(reg, UNBREAKING);
        return reg.freeze();
    }

    private static void register(MappedRegistry<Enchantment> registry, ResourceKey<Enchantment> key) {
        registry.register(key, syntheticEnchantment(), RegistrationInfo.BUILT_IN);
    }

    private static Enchantment syntheticEnchantment() {
        HolderSet<Item> any = HolderSet.direct(List.of(BuiltInRegistries.ITEM.wrapAsHolder(Items.BOOK)));
        Enchantment.EnchantmentDefinition def = Enchantment.definition(
                any,
                10,
                5,
                Enchantment.dynamicCost(1, 10),
                Enchantment.dynamicCost(51, 10),
                1,
                EquipmentSlotGroup.ANY);
        return new Enchantment(
                Component.literal("test"),
                def,
                HolderSet.empty(),
                DataComponentMap.EMPTY);
    }

    /** Same unfreeze contract as the sibling library tests. */
    private static void unfreeze(Registry<?> registry, boolean intrusiveHolders) throws Exception {
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(registry, false);

        if (intrusiveHolders) {
            Field intrusive = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
            intrusive.setAccessible(true);
            if (intrusive.get(registry) == null) {
                intrusive.set(registry, new IdentityHashMap<>());
            }
        }
    }

    /** BE fixture matches the pattern used by sibling library tests — public no-op subclass. */
    private static final class TestLibraryBlockEntity extends EnchantmentLibraryBlockEntity {
        TestLibraryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxLevel) {
            super(type, pos, state, maxLevel);
        }
    }
}
