package com.fizzlesmp.fizzle_enchanting.library;

import com.mojang.serialization.Lifecycle;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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
 * T-4.5.1 + T-4.5.2 — contract tests for {@link LibraryStorageAdapter}. Exercises the
 * hopper-facing surface and the {@code SnapshotParticipant} rollback semantics on a test-only BE
 * subclass (same fixture pattern as {@link EnchantmentLibraryBlockEntityTest}) so the adapter can
 * be driven without a live world.
 *
 * <p>T-4.5.1 obligations:
 * <ul>
 *     <li>Non-book variants never move points (return 0 accepted).</li>
 *     <li>A saturated pool reports the full {@code maxAmount} as "accepted" so hoppers cannot
 *         jam — the extra units are silently voided inside {@code depositBookSilent}.</li>
 *     <li>Extraction is always a no-op; a library is "never empty" from a pipe's perspective,
 *         matching DESIGN "extraction is a menu-only operation".</li>
 * </ul>
 *
 * <p>T-4.5.2 obligations: aborted transactions roll the pool back to its pre-insert state;
 * committed transactions both apply the mutation and fire the BE's {@code setChanged} once
 * (regardless of how many books were deposited inside the transaction).
 */
class LibraryStorageTest {

    private static final ResourceKey<Enchantment> SHARPNESS = key("sharpness");

    private static Registry<Enchantment> enchantmentRegistry;
    private static BlockEntityType<TestLibraryBlockEntity> basicType;
    private static BlockState state;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreeze(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        enchantmentRegistry = buildEnchantmentRegistry();
        basicType = BlockEntityType.Builder.of(
                (pos, blockState) -> new TestLibraryBlockEntity(basicType, pos, blockState, 16),
                Blocks.BOOKSHELF).build(null);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath("fizzle_enchanting", "test_library_storage"),
                basicType);
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();
        state = Blocks.BOOKSHELF.defaultBlockState();
    }

    @Test
    void insert_rejectsNonBookVariants() {
        TestLibraryBlockEntity lib = newBasic();
        // Build a sword variant with a non-empty component patch so ItemVariantImpl.of skips its
        // item-cache fast-path (which casts the Item to a mixin-injected accessor and therefore
        // only works in a running Fabric runtime, not in a junit-loader unit test).
        ItemStack swordStack = new ItemStack(Items.DIAMOND_SWORD);
        swordStack.set(DataComponents.CUSTOM_NAME, Component.literal("not a book"));
        ItemVariant sword = ItemVariant.of(swordStack);
        try (Transaction tx = Transaction.openOuter()) {
            long accepted = lib.getStorageAdapter().insert(sword, 1, tx);
            tx.commit();
            assertEquals(0, accepted, "non-book variant → zero accepted");
        }
        assertTrue(lib.getPoints().isEmpty(), "non-book insert must not touch the pool");
    }

    @Test
    void insert_bookAtCap_returnsFullAmountWithVoidOverflow() {
        TestLibraryBlockEntity lib = newBasic();
        // Pre-saturate the pool — further inserts should be silently voided without rejecting units.
        lib.getPoints().put(SHARPNESS, lib.getMaxPoints());
        lib.getMaxLevels().put(SHARPNESS, 1);

        ItemVariant bookVariant = ItemVariant.of(singleEnchantBook(SHARPNESS, 1));
        try (Transaction tx = Transaction.openOuter()) {
            long accepted = lib.getStorageAdapter().insert(bookVariant, 10, tx);
            tx.commit();
            assertEquals(10, accepted,
                    "saturated library still reports every book as accepted — pipes never see a full library");
        }
        assertEquals(lib.getMaxPoints(), lib.getPoints().getInt(SHARPNESS),
                "overflow voided inside depositBook; pool stays at the cap");
    }

    @Test
    void insert_freshPool_accumulatesPerUnit() {
        TestLibraryBlockEntity lib = newBasic();
        ItemVariant bookVariant = ItemVariant.of(singleEnchantBook(SHARPNESS, 1));
        try (Transaction tx = Transaction.openOuter()) {
            long accepted = lib.getStorageAdapter().insert(bookVariant, 3, tx);
            tx.commit();
            assertEquals(3, accepted, "three books → three accepted");
        }
        assertEquals(3, lib.getPoints().getInt(SHARPNESS),
                "per-unit depositBook: three Sharpness-I books → 3 * points(1) = 3 points");
        assertEquals(1, lib.getMaxLevels().getInt(SHARPNESS));
    }

    @Test
    void abort_rollsBackInsertedPoints() {
        TestLibraryBlockEntity lib = newBasic();
        ItemVariant bookVariant = ItemVariant.of(singleEnchantBook(SHARPNESS, 3));

        try (Transaction tx = Transaction.openOuter()) {
            long accepted = lib.getStorageAdapter().insert(bookVariant, 5, tx);
            assertEquals(5, accepted);
            // Mid-transaction the live BE map is mutated — the rollback hinges on the snapshot.
            assertEquals(5 * EnchantmentLibraryBlockEntity.points(3),
                    lib.getPoints().getInt(SHARPNESS),
                    "in-flight insert mutates BE state; rollback comes from the snapshot, not deferred apply");
            // Drop out of the try without committing → SnapshotParticipant.readSnapshot fires.
        }
        assertTrue(lib.getPoints().isEmpty(), "abort restored pre-insert pool");
        assertTrue(lib.getMaxLevels().isEmpty(), "abort restored pre-insert max-levels");
        assertEquals(0, lib.setChangedCount,
                "aborted transaction must not fire setChanged — onFinalCommit is success-only");
    }

    @Test
    void commit_appliesMutationAndFiresSetChangedOnce() {
        TestLibraryBlockEntity lib = newBasic();
        ItemVariant bookVariant = ItemVariant.of(singleEnchantBook(SHARPNESS, 2));
        try (Transaction tx = Transaction.openOuter()) {
            // Multiple deposits in one txn → onFinalCommit still fires setChanged exactly once.
            lib.getStorageAdapter().insert(bookVariant, 4, tx);
            tx.commit();
        }
        assertEquals(4 * EnchantmentLibraryBlockEntity.points(2),
                lib.getPoints().getInt(SHARPNESS),
                "committed insert persists; per-unit math: 4 books × points(2)");
        assertEquals(2, lib.getMaxLevels().getInt(SHARPNESS));
        assertEquals(1, lib.setChangedCount,
                "onFinalCommit fires setChanged exactly once per outer transaction, regardless of deposit count");
    }

    @Test
    void abort_preservesPriorMutationsFromEarlierCommit() {
        TestLibraryBlockEntity lib = newBasic();
        ItemVariant bookVariant = ItemVariant.of(singleEnchantBook(SHARPNESS, 2));

        // First transaction commits — pool now holds points(2) = 2.
        try (Transaction tx = Transaction.openOuter()) {
            lib.getStorageAdapter().insert(bookVariant, 1, tx);
            tx.commit();
        }
        int afterFirst = lib.getPoints().getInt(SHARPNESS);
        assertEquals(EnchantmentLibraryBlockEntity.points(2), afterFirst,
                "sanity: first commit landed before the abort case runs");
        int firstCommitSetChanged = lib.setChangedCount;

        // Second transaction aborts — pool must roll back to the post-first-commit state, not zero.
        try (Transaction tx = Transaction.openOuter()) {
            lib.getStorageAdapter().insert(bookVariant, 3, tx);
            // deliberately no commit
        }
        assertEquals(afterFirst, lib.getPoints().getInt(SHARPNESS),
                "abort restores the snapshot taken at insert-time, not an empty pool");
        assertEquals(firstCommitSetChanged, lib.setChangedCount,
                "aborted second txn must not bump the setChanged counter past the first commit");
    }

    @Test
    void insert_rateLimitedSecondCallDrops() {
        // T-4.5.3: two rapid inserts at rateLimit=20 → second dropped. The first insert stamps
        // lastInsertTick at the current game time; a second insert inside the 20-tick window is
        // rejected (returns 0) so the pipe back-pressures instead of silently pushing books that
        // the library refuses to pool. After the window elapses, inserts resume.
        TestLibraryBlockEntity lib = newBasic();
        lib.rateLimit = 20;
        lib.gameTime = 0L;
        ItemVariant bookVariant = ItemVariant.of(singleEnchantBook(SHARPNESS, 1));

        try (Transaction tx = Transaction.openOuter()) {
            long accepted = lib.getStorageAdapter().insert(bookVariant, 1, tx);
            tx.commit();
            assertEquals(1, accepted, "first insert at tick 0 has no prior stamp → allowed");
        }
        assertEquals(1, lib.getPoints().getInt(SHARPNESS), "first insert landed in the pool");
        assertEquals(0L, lib.lastInsertTick, "lastInsertTick stamped at commit time");

        // Second insert five ticks later — inside the 20-tick window, must drop.
        lib.gameTime = 5L;
        try (Transaction tx = Transaction.openOuter()) {
            long accepted = lib.getStorageAdapter().insert(bookVariant, 1, tx);
            tx.commit();
            assertEquals(0, accepted, "second insert within the 20-tick window → dropped");
        }
        assertEquals(1, lib.getPoints().getInt(SHARPNESS),
                "dropped insert must not accumulate points");
        assertEquals(0L, lib.lastInsertTick,
                "dropped insert does not restart the cooldown clock");

        // Third insert exactly at the window boundary — the check is `< rateLimit`, so tick 20 is
        // eligible again.
        lib.gameTime = 20L;
        try (Transaction tx = Transaction.openOuter()) {
            long accepted = lib.getStorageAdapter().insert(bookVariant, 1, tx);
            tx.commit();
            assertEquals(1, accepted, "insert at tick = rateLimit is past the window → allowed");
        }
        assertEquals(2, lib.getPoints().getInt(SHARPNESS));
        assertEquals(20L, lib.lastInsertTick);
    }

    @Test
    void insert_rateLimitZeroNeverDrops() {
        // Default config (rateLimit = 0) must behave like the no-throttle baseline.
        TestLibraryBlockEntity lib = newBasic();
        lib.rateLimit = 0;
        lib.gameTime = 0L;
        ItemVariant bookVariant = ItemVariant.of(singleEnchantBook(SHARPNESS, 1));

        for (int i = 0; i < 3; i++) {
            try (Transaction tx = Transaction.openOuter()) {
                long accepted = lib.getStorageAdapter().insert(bookVariant, 1, tx);
                tx.commit();
                assertEquals(1, accepted, "rate limit off → every insert accepted");
            }
            // Keep game time constant — if rate limit were on this would throttle hard.
        }
        assertEquals(3, lib.getPoints().getInt(SHARPNESS));
    }

    @Test
    void insert_rateLimitedAbortDoesNotBurnCooldown() {
        // A rolled-back insert must not leave lastInsertTick stamped — otherwise a failed
        // transaction would block the next legitimate insert for the duration of the window.
        TestLibraryBlockEntity lib = newBasic();
        lib.rateLimit = 20;
        lib.gameTime = 0L;
        ItemVariant bookVariant = ItemVariant.of(singleEnchantBook(SHARPNESS, 1));

        try (Transaction tx = Transaction.openOuter()) {
            lib.getStorageAdapter().insert(bookVariant, 1, tx);
            // deliberately no commit
        }
        assertEquals(Long.MIN_VALUE, lib.lastInsertTick,
                "abort restores the pre-insert sentinel; no phantom cooldown");

        // Next insert on the next tick should be allowed because the abort released the throttle.
        lib.gameTime = 1L;
        try (Transaction tx = Transaction.openOuter()) {
            long accepted = lib.getStorageAdapter().insert(bookVariant, 1, tx);
            tx.commit();
            assertEquals(1, accepted, "post-abort insert is allowed — aborted call did not throttle");
        }
        assertEquals(1L, lib.lastInsertTick);
    }

    @Test
    void extract_alwaysReturnsZero() {
        TestLibraryBlockEntity lib = newBasic();
        // Seed the pool so there *is* something to extract in principle — the adapter must still
        // decline, because libraries are insert-only from the hopper's perspective.
        lib.getPoints().put(SHARPNESS, lib.getMaxPoints());
        lib.getMaxLevels().put(SHARPNESS, 5);

        ItemVariant bookVariant = ItemVariant.of(singleEnchantBook(SHARPNESS, 1));
        try (Transaction tx = Transaction.openOuter()) {
            long extracted = lib.getStorageAdapter().extract(bookVariant, 64, tx);
            assertEquals(0, extracted, "extract always returns zero — hoppers can't pull from a library");
            tx.commit();
        }
        assertFalse(lib.getStorageAdapter().supportsExtraction(),
                "supportsExtraction() false → pipes skip the extraction path entirely");
    }

    // ---- helpers ------------------------------------------------------------

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
        reg.register(SHARPNESS, syntheticEnchantment(), RegistrationInfo.BUILT_IN);
        return reg.freeze();
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

    /**
     * Test subclass whose sole purpose is to let us instantiate the abstract class. Counts
     * {@link #setChanged()} invocations so the T-4.5.2 commit/abort tests can prove
     * {@code onFinalCommit} fires exactly once per successful outer transaction (and never on
     * abort) without needing a live world to observe the chunk-update side effect.
     */
    private static final class TestLibraryBlockEntity extends EnchantmentLibraryBlockEntity {
        int setChangedCount = 0;
        long gameTime = 0L;
        int rateLimit = 0;

        TestLibraryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxLevel) {
            super(type, pos, state, maxLevel);
        }

        @Override
        public void setChanged() {
            this.setChangedCount++;
            super.setChanged();
        }

        @Override
        protected long currentGameTime() {
            return this.gameTime;
        }

        @Override
        protected int rateLimitTicks() {
            return this.rateLimit;
        }
    }
}
