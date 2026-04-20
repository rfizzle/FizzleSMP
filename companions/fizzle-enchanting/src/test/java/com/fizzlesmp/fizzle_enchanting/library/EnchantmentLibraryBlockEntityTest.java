package com.fizzlesmp.fizzle_enchanting.library;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-4.3.1 — exercises the abstract library BE's three methods against a test-only subclass
 * fixture. The fixture wires a {@link BlockEntityType} whose supplier returns
 * {@link TestLibraryBlockEntity} against vanilla {@link Blocks#BOOKSHELF} so BE construction
 * doesn't need a registered block of our own (T-4.3.2 ships the real subclasses and their
 * registered types).
 *
 * <p>Vanilla {@link Enchantment} is a dynamic registry, left empty by {@code Bootstrap.bootStrap}.
 * The test builds a synthetic registry with a couple of stand-in enchantments and wraps their
 * holders so {@link ItemEnchantments.Mutable} can store them on a {@link Items#ENCHANTED_BOOK}
 * stack for deposit. Extraction tests work purely on the {@link ResourceKey} surface so they
 * don't need the book back out.
 */
class EnchantmentLibraryBlockEntityTest {

    private static final ResourceKey<Enchantment> SHARPNESS = key("sharpness");
    private static final ResourceKey<Enchantment> UNBREAKING = key("unbreaking");
    private static final ResourceKey<Enchantment> UNKNOWN = key("never_registered");

    private static Registry<Enchantment> enchantmentRegistry;
    private static BlockEntityType<TestLibraryBlockEntity> basicType;
    private static BlockEntityType<TestLibraryBlockEntity> enderType;
    private static BlockState state;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        // BlockEntityType's ctor registers an intrusive holder against BLOCK_ENTITY_TYPE — the
        // registry is frozen post-bootstrap, so the same unfreeze dance used by the shelf tests
        // is required to mint test-only types.
        unfreeze(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        enchantmentRegistry = buildEnchantmentRegistry();
        basicType = BlockEntityType.Builder.of(
                (pos, blockState) -> new TestLibraryBlockEntity(basicType, pos, blockState, 16),
                Blocks.BOOKSHELF).build(null);
        enderType = BlockEntityType.Builder.of(
                (pos, blockState) -> new TestLibraryBlockEntity(enderType, pos, blockState, 31),
                Blocks.BOOKSHELF).build(null);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath("fizzle_enchanting", "test_library_basic"),
                basicType);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath("fizzle_enchanting", "test_library_ender"),
                enderType);
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();
        state = Blocks.BOOKSHELF.defaultBlockState();
    }

    /** Thaw the block-entity-type registry so {@code Builder#build} can mint intrusive holders. */
    private static void unfreeze(Registry<?> registry) throws Exception {
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(registry, false);
        Field intrusive = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        intrusive.setAccessible(true);
        if (intrusive.get(registry) == null) {
            intrusive.set(registry, new IdentityHashMap<>());
        }
    }

    // ---- ctor sanity --------------------------------------------------------

    @Test
    void construction_exposesTierConstants() {
        TestLibraryBlockEntity basic = newBasic();
        assertEquals(16, basic.getMaxLevel(), "Basic tier carries the tier-cap constant");
        assertEquals(32_768, basic.getMaxPoints(), "Basic tier points ceiling = points(16)");

        TestLibraryBlockEntity ender = newEnder();
        assertEquals(31, ender.getMaxLevel());
        assertEquals(1_073_741_824, ender.getMaxPoints(), "Ender tier points ceiling = points(31)");
        assertTrue(basic.getPoints().isEmpty(), "fresh library has empty point map");
        assertTrue(basic.getMaxLevels().isEmpty(), "fresh library has empty max-level map");
    }

    // ---- deposit ------------------------------------------------------------

    @Test
    void depositBook_accumulatesPointsAndMaxLevel() {
        TestLibraryBlockEntity lib = newBasic();
        lib.depositBook(singleEnchantBook(SHARPNESS, 3));
        assertEquals(4, lib.getPoints().getInt(SHARPNESS), "Sharpness-3 book contributes 2^(3-1) = 4");
        assertEquals(3, lib.getMaxLevels().getInt(SHARPNESS), "max level tracks the deposited level");

        // Stacking additional levels of the same enchant is additive on points, monotonic on maxLevel.
        lib.depositBook(singleEnchantBook(SHARPNESS, 1));
        assertEquals(5, lib.getPoints().getInt(SHARPNESS), "second deposit adds points(1) = 1 → 5 total");
        assertEquals(3, lib.getMaxLevels().getInt(SHARPNESS), "max level does not regress when a lower book is added");

        lib.depositBook(singleEnchantBook(SHARPNESS, 5));
        assertEquals(21, lib.getPoints().getInt(SHARPNESS), "third deposit adds 2^(5-1) = 16 → 21");
        assertEquals(5, lib.getMaxLevels().getInt(SHARPNESS), "max level raises when a higher book is added");
    }

    @Test
    void depositBook_clampsMaxLevelToTierCap() {
        TestLibraryBlockEntity basic = newBasic();
        // 31 > Basic cap of 16 → clamped down, even though we accept the book.
        basic.depositBook(singleEnchantBook(SHARPNESS, 31));
        assertEquals(16, basic.getMaxLevels().getInt(SHARPNESS), "Basic tier never exposes maxLevel > 16");
    }

    @Test
    void depositBook_voidsOverflowAtMaxPoints() {
        TestLibraryBlockEntity basic = newBasic();
        // Depositing 18 Sharpness-I books: each adds 1 point, but the tier cap is 32_768. All 18
        // fit trivially, so we instead simulate the saturating case by pre-filling the map to the
        // edge and depositing one more.
        basic.getPoints().put(SHARPNESS, basic.getMaxPoints() - 1);
        basic.depositBook(singleEnchantBook(SHARPNESS, 3)); // +4 points
        assertEquals(basic.getMaxPoints(), basic.getPoints().getInt(SHARPNESS),
                "sum clamps to maxPoints; overflow is destroyed (silent void)");
    }

    @Test
    void depositBook_rejectsNonBookStacks() {
        TestLibraryBlockEntity lib = newBasic();
        lib.depositBook(ItemStack.EMPTY);
        lib.depositBook(new ItemStack(Items.DIAMOND));
        assertTrue(lib.getPoints().isEmpty(), "non-book deposits must not mutate state");
        assertTrue(lib.getMaxLevels().isEmpty());
    }

    @Test
    void depositBook_ignoresBlankEnchantedBook() {
        TestLibraryBlockEntity lib = newBasic();
        lib.depositBook(new ItemStack(Items.ENCHANTED_BOOK));
        assertTrue(lib.getPoints().isEmpty(),
                "enchanted book with no STORED_ENCHANTMENTS component is a no-op deposit");
    }

    @Test
    void depositBook_acceptsMultipleEnchantmentsInOneBook() {
        TestLibraryBlockEntity lib = newBasic();
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(holderFor(SHARPNESS), 2);
        mutable.set(holderFor(UNBREAKING), 4);
        stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
        lib.depositBook(stack);
        assertEquals(2, lib.getPoints().getInt(SHARPNESS), "Sharpness-2 → 2 points");
        assertEquals(8, lib.getPoints().getInt(UNBREAKING), "Unbreaking-4 → 8 points");
        assertEquals(2, lib.getMaxLevels().getInt(SHARPNESS));
        assertEquals(4, lib.getMaxLevels().getInt(UNBREAKING));
    }

    // ---- canExtract ---------------------------------------------------------

    @Test
    void canExtract_gatesOnBothMaxLevelAndPoints() {
        TestLibraryBlockEntity lib = newBasic();
        // Pool 32_768 Sharpness-I deposits is infeasible to build iteratively in a test, so we
        // hand-stuff the maps to the "enough points but too-low maxLevel" state.
        lib.getPoints().put(SHARPNESS, lib.getMaxPoints());
        lib.getMaxLevels().put(SHARPNESS, 1);
        assertFalse(lib.canExtract(SHARPNESS, 5, 0),
                "points suffice but maxLevels = 1 blocks Sharpness-V extraction");

        // Inverse: maxLevels high but points starved.
        lib.getMaxLevels().put(SHARPNESS, 10);
        lib.getPoints().put(SHARPNESS, 4); // only enough for level 3
        assertFalse(lib.canExtract(SHARPNESS, 5, 0),
                "maxLevels high enough but pool only holds 4 points → decline level 5");
    }

    @Test
    void canExtract_allowsUpgradePathingAtReducedCost() {
        TestLibraryBlockEntity lib = newBasic();
        lib.getMaxLevels().put(SHARPNESS, 5);
        // Upgrading an existing Sharpness-IV (8 points) to V (16 points) costs 8 points.
        lib.getPoints().put(SHARPNESS, 8);
        assertTrue(lib.canExtract(SHARPNESS, 5, 4),
                "upgrade IV→V costs points(5) - points(4) = 8; pool has exactly 8");

        lib.getPoints().put(SHARPNESS, 7);
        assertFalse(lib.canExtract(SHARPNESS, 5, 4),
                "one point short → decline");
    }

    @Test
    void canExtract_declinesWhenTargetIsNotAnUpgrade() {
        TestLibraryBlockEntity lib = newBasic();
        lib.getMaxLevels().put(SHARPNESS, 5);
        lib.getPoints().put(SHARPNESS, 1024);
        assertFalse(lib.canExtract(SHARPNESS, 3, 3), "same-level target is a no-op, declined");
        assertFalse(lib.canExtract(SHARPNESS, 2, 3), "downgrade request declined");
        assertFalse(lib.canExtract(SHARPNESS, 0, 0), "target 0 declined");
    }

    @Test
    void canExtract_declinesWhenTargetExceedsTierCap() {
        TestLibraryBlockEntity basic = newBasic();
        basic.getMaxLevels().put(SHARPNESS, 31); // hand-stuffed beyond tier; still gated
        basic.getPoints().put(SHARPNESS, basic.getMaxPoints());
        assertFalse(basic.canExtract(SHARPNESS, 17, 0),
                "Basic tier never extracts past level 16, even if the map were tampered with");
    }

    @Test
    void canExtract_declinesForUnseenEnchantment() {
        TestLibraryBlockEntity lib = newBasic();
        assertFalse(lib.canExtract(UNKNOWN, 1, 0),
                "no deposits ever seen → both maps zero → decline");
    }

    // ---- extract ------------------------------------------------------------

    @Test
    void extract_debitsPointsOnSuccess() {
        TestLibraryBlockEntity lib = newBasic();
        lib.getMaxLevels().put(SHARPNESS, 5);
        lib.getPoints().put(SHARPNESS, 100);
        assertTrue(lib.extract(SHARPNESS, 3, 0),
                "target 3 costs 4 points; pool has 100 → success");
        assertEquals(96, lib.getPoints().getInt(SHARPNESS),
                "points debited by points(3) - points(0) = 4");
    }

    @Test
    void extract_upgradesAreCheaperThanFreshPulls() {
        TestLibraryBlockEntity lib = newBasic();
        lib.getMaxLevels().put(SHARPNESS, 5);
        lib.getPoints().put(SHARPNESS, 32);
        // Fresh pull to V: points(5) - points(0) = 16.
        assertTrue(lib.extract(SHARPNESS, 5, 0));
        assertEquals(16, lib.getPoints().getInt(SHARPNESS));
        // Upgrade IV→V on a second book: costs points(5) - points(4) = 8.
        assertTrue(lib.extract(SHARPNESS, 5, 4));
        assertEquals(8, lib.getPoints().getInt(SHARPNESS));
    }

    @Test
    void extract_returnsFalseAndLeavesStateUntouchedOnDecline() {
        TestLibraryBlockEntity lib = newBasic();
        lib.getMaxLevels().put(SHARPNESS, 1);
        lib.getPoints().put(SHARPNESS, 999);
        assertFalse(lib.extract(SHARPNESS, 5, 0),
                "declined when target exceeds maxLevels");
        assertEquals(999, lib.getPoints().getInt(SHARPNESS),
                "pool untouched on decline — no partial debit");
        assertEquals(1, lib.getMaxLevels().getInt(SHARPNESS),
                "maxLevels untouched on decline");
    }

    // ---- helpers ------------------------------------------------------------

    private static TestLibraryBlockEntity newBasic() {
        return new TestLibraryBlockEntity(basicType, BlockPos.ZERO, state, 16);
    }

    private static TestLibraryBlockEntity newEnder() {
        return new TestLibraryBlockEntity(enderType, BlockPos.ZERO, state, 31);
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

    /** Test subclass whose sole purpose is to let us instantiate the abstract class. */
    private static final class TestLibraryBlockEntity extends EnchantmentLibraryBlockEntity {
        TestLibraryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxLevel) {
            super(type, pos, state, maxLevel);
        }
    }
}
