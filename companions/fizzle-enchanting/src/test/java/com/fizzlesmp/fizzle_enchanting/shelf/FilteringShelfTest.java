package com.fizzlesmp.fizzle_enchanting.shelf;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.mojang.serialization.Lifecycle;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-3.5.1 — covers the four documented behaviors of the filtering shelf:
 * <ol>
 *   <li>Insert: depositing a single-enchant enchanted book grows the blacklist.</li>
 *   <li>Extract: clearing a slot shrinks the blacklist.</li>
 *   <li>NBT round-trip: items survive {@code saveCustomOnly}/{@code loadCustomOnly}.</li>
 *   <li>Slot targeting: corner cursor hits map to deterministic slot indices.</li>
 * </ol>
 *
 * <p>Vanilla {@link Enchantment} is a dynamic registry that {@code Bootstrap.bootStrap()} leaves
 * unpopulated. The test rebuilds a small synthetic enchantment registry (matching the pattern
 * already used by the table's selection tests) and wraps it in a {@link HolderLookup.Provider}
 * so {@code saveCustomOnly} / {@code loadCustomOnly} can resolve the {@code STORED_ENCHANTMENTS}
 * data component without a live world.
 *
 * <p>fabric-loader-junit is not on the test classpath (matches fizzle-difficulty), so the BE is
 * exercised through its public API rather than via vanilla's player-interaction path. Slot
 * targeting calls {@link FilteringShelfBlock#computeHitSlot} directly — the same authority used
 * inside {@link FilteringShelfBlock#useItemOn} — so the mapping verified here is the mapping
 * that drives vanilla's {@code SLOT_OCCUPIED_PROPERTIES} updates.
 */
class FilteringShelfTest {

    private static final ResourceKey<Enchantment> SHARPNESS = key("sharpness");
    private static final ResourceKey<Enchantment> UNBREAKING = key("unbreaking");
    private static final ResourceKey<Enchantment> MENDING = key("mending");

    private static Registry<Enchantment> enchantmentRegistry;
    private static HolderLookup.Provider provider;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreeze(BuiltInRegistries.MENU, false);
        unfreeze(BuiltInRegistries.BLOCK, true);
        unfreeze(BuiltInRegistries.ITEM, true);
        unfreeze(BuiltInRegistries.BLOCK_ENTITY_TYPE, true);

        FizzleEnchantingRegistry.register();

        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
        BuiltInRegistries.MENU.freeze();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();

        enchantmentRegistry = buildEnchantmentRegistry();
        provider = HolderLookup.Provider.create(Stream.of(enchantmentRegistry.asLookup()));
    }

    @Test
    void registry_filteringShelfRegisteredAsExpectedTypes() {
        ResourceLocation id = FizzleEnchanting.id("filtering_shelf");
        assertSame(FizzleEnchantingRegistry.FILTERING_SHELF, BuiltInRegistries.BLOCK.get(id),
                "filtering_shelf block must resolve to FizzleEnchantingRegistry.FILTERING_SHELF");
        assertSame(FizzleEnchantingRegistry.FILTERING_SHELF_BE,
                BuiltInRegistries.BLOCK_ENTITY_TYPE.get(id),
                "filtering_shelf BE type must resolve to the registered instance");
    }

    @Test
    void newBlockEntity_returnsFilteringShelfBlockEntity() {
        BlockEntity be = FizzleEnchantingRegistry.FILTERING_SHELF.newBlockEntity(
                BlockPos.ZERO, FizzleEnchantingRegistry.FILTERING_SHELF.defaultBlockState());
        assertNotNull(be, "newBlockEntity must produce an entity");
        assertInstanceOf(FilteringShelfBlockEntity.class, be,
                "BE produced by FilteringShelfBlock must be the matching subclass");
    }

    @Test
    void getType_returnsRegisteredFilteringShelfType() {
        FilteringShelfBlockEntity be = newEntity();
        BlockEntityType<?> type = be.getType();
        assertSame(FizzleEnchantingRegistry.FILTERING_SHELF_BE, type,
                "BE getType() must return our registered type, not vanilla CHISELED_BOOKSHELF");
    }

    @Test
    void canInsert_acceptsOnlySingleEnchantBooks() {
        assertFalse(FilteringShelfBlockEntity.canInsert(ItemStack.EMPTY),
                "empty stack rejected");
        assertFalse(FilteringShelfBlockEntity.canInsert(new ItemStack(Items.BOOK)),
                "plain book rejected — must be enchanted");
        assertFalse(FilteringShelfBlockEntity.canInsert(new ItemStack(Items.ENCHANTED_BOOK)),
                "blank enchanted book (zero enchants) rejected");
        assertTrue(FilteringShelfBlockEntity.canInsert(singleEnchantBook(SHARPNESS, 1)),
                "single-enchant enchanted book accepted");

        ItemEnchantments.Mutable multi = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        multi.set(holderFor(SHARPNESS), 1);
        multi.set(holderFor(UNBREAKING), 1);
        ItemStack multiBook = new ItemStack(Items.ENCHANTED_BOOK);
        multiBook.set(DataComponents.STORED_ENCHANTMENTS, multi.toImmutable());
        assertFalse(FilteringShelfBlockEntity.canInsert(multiBook),
                "multi-enchant enchanted book rejected per design");
    }

    @Test
    void insert_blacklistGrowsByOnePerBook() {
        FilteringShelfBlockEntity be = newEntity();
        assertTrue(be.getEnchantmentBlacklist().isEmpty(),
                "fresh shelf must start with an empty blacklist");

        be.setItem(0, singleEnchantBook(SHARPNESS, 1));
        Set<ResourceKey<Enchantment>> after1 = be.getEnchantmentBlacklist();
        assertEquals(1, after1.size(), "blacklist grows to 1 after first deposit");
        assertTrue(after1.contains(SHARPNESS), "blacklist contains sharpness key");

        be.setItem(2, singleEnchantBook(UNBREAKING, 3));
        Set<ResourceKey<Enchantment>> after2 = be.getEnchantmentBlacklist();
        assertEquals(2, after2.size(),
                "blacklist grows to 2 after second deposit (different slot, different enchant)");
        assertTrue(after2.contains(UNBREAKING), "blacklist now also contains unbreaking key");
    }

    @Test
    void extract_blacklistShrinksWhenSlotCleared() {
        FilteringShelfBlockEntity be = newEntity();
        be.setItem(0, singleEnchantBook(SHARPNESS, 1));
        be.setItem(1, singleEnchantBook(UNBREAKING, 1));
        assertEquals(2, be.getEnchantmentBlacklist().size(),
                "two books in → two-entry blacklist");

        be.setItem(0, ItemStack.EMPTY);
        Set<ResourceKey<Enchantment>> after = be.getEnchantmentBlacklist();
        assertEquals(1, after.size(), "extracting one book shrinks blacklist back to 1");
        assertTrue(after.contains(UNBREAKING),
                "remaining entry is the un-extracted enchant");
        assertFalse(after.contains(SHARPNESS),
                "extracted enchant must drop out");
    }

    @Test
    void nbt_roundTripPreservesItems() {
        FilteringShelfBlockEntity original = newEntity();
        original.setItem(0, singleEnchantBook(SHARPNESS, 1));
        original.setItem(3, singleEnchantBook(MENDING, 1));

        CompoundTag tag = original.saveCustomOnly(provider);

        FilteringShelfBlockEntity reloaded = newEntity();
        reloaded.loadCustomOnly(tag, provider);

        assertEquals(2, reloaded.count(), "reloaded entity preserves item count");
        ItemStack slot0 = reloaded.getItem(0);
        assertTrue(slot0.is(Items.ENCHANTED_BOOK), "slot 0 retains an enchanted book");
        ItemEnchantments stored = slot0.getOrDefault(DataComponents.STORED_ENCHANTMENTS, ItemEnchantments.EMPTY);
        assertEquals(1, stored.size(), "slot 0 still holds exactly one enchantment");
        assertEquals(Set.of(SHARPNESS, MENDING),
                reloaded.getEnchantmentBlacklist(),
                "blacklist rebuilt from reloaded items matches the saved set");
    }

    @Test
    void getUpdateTag_returnsItemsForClientSync() {
        FilteringShelfBlockEntity be = newEntity();
        be.setItem(0, singleEnchantBook(SHARPNESS, 1));
        CompoundTag updateTag = be.getUpdateTag(provider);
        assertNotNull(updateTag, "getUpdateTag must not return null");
        // saveCustomOnly drops metadata (id/x/y/z) — we still expect the items list to come along
        // since vanilla ChiseledBookShelfBlockEntity#saveAdditional writes it.
        assertTrue(updateTag.contains("Items"),
                "updateTag must carry the Items list for client-side sync");
    }

    @Test
    void computeHitSlot_topRowCornerHitsMapInOrder() {
        BlockState state = facing(Direction.NORTH);
        // North-facing shelf: hit comes in on the +Z face of the block; horizontal axis is X.
        // For a north-facing block, (1 - dx) gives the player-view "left" coordinate, so x near 1
        // is leftmost (slot 0). Top row (slots 0/1/2) requires y >= 0.5.
        OptionalInt slot0 = FilteringShelfBlock.computeHitSlot(
                hit(BlockPos.ZERO, Direction.NORTH, 0.85, 0.85, 1.0), state);
        assertEquals(OptionalInt.of(0), slot0, "top-left corner → slot 0");

        OptionalInt slot1 = FilteringShelfBlock.computeHitSlot(
                hit(BlockPos.ZERO, Direction.NORTH, 0.5, 0.85, 1.0), state);
        assertEquals(OptionalInt.of(1), slot1, "top-middle → slot 1");

        OptionalInt slot2 = FilteringShelfBlock.computeHitSlot(
                hit(BlockPos.ZERO, Direction.NORTH, 0.15, 0.85, 1.0), state);
        assertEquals(OptionalInt.of(2), slot2, "top-right corner → slot 2");
    }

    @Test
    void computeHitSlot_bottomRowCornerHitsMapInOrder() {
        BlockState state = facing(Direction.NORTH);
        OptionalInt slot3 = FilteringShelfBlock.computeHitSlot(
                hit(BlockPos.ZERO, Direction.NORTH, 0.85, 0.15, 1.0), state);
        assertEquals(OptionalInt.of(3), slot3, "bottom-left corner → slot 3");

        OptionalInt slot4 = FilteringShelfBlock.computeHitSlot(
                hit(BlockPos.ZERO, Direction.NORTH, 0.5, 0.15, 1.0), state);
        assertEquals(OptionalInt.of(4), slot4, "bottom-middle → slot 4");

        OptionalInt slot5 = FilteringShelfBlock.computeHitSlot(
                hit(BlockPos.ZERO, Direction.NORTH, 0.15, 0.15, 1.0), state);
        assertEquals(OptionalInt.of(5), slot5, "bottom-right corner → slot 5");
    }

    @Test
    void computeHitSlot_wrongFaceReturnsEmpty() {
        BlockState state = facing(Direction.NORTH);
        // Hit on EAST face of a NORTH-facing block — not the front, not a slot.
        OptionalInt result = FilteringShelfBlock.computeHitSlot(
                hit(BlockPos.ZERO, Direction.EAST, 1.0, 0.5, 0.5), state);
        assertTrue(result.isEmpty(), "hits on non-front faces must return empty");
    }

    @Test
    void computeHitSlot_marginHitsReturnEmpty() {
        BlockState state = facing(Direction.NORTH);
        // Hit at the very top edge (y > 1 - SECTION_MARGIN) is in the top margin → empty.
        OptionalInt margin = FilteringShelfBlock.computeHitSlot(
                hit(BlockPos.ZERO, Direction.NORTH, 0.5, 0.99, 1.0), state);
        assertTrue(margin.isEmpty(), "margin hits return empty so vanilla doesn't pick a slot");
    }

    @Test
    void computeHitSlot_southFaceMirrorsHorizontalAxis() {
        BlockState state = facing(Direction.SOUTH);
        // South-facing means the front is the -Z face; hit comes in at z=0.
        // For a south-facing block, x near 0 corresponds to the leftmost slot (player view from -Z).
        OptionalInt slot0 = FilteringShelfBlock.computeHitSlot(
                hit(BlockPos.ZERO, Direction.SOUTH, 0.15, 0.85, 0.0), state);
        assertEquals(OptionalInt.of(0), slot0, "south-facing top-left → slot 0");
    }

    private static FilteringShelfBlockEntity newEntity() {
        return new FilteringShelfBlockEntity(
                BlockPos.ZERO,
                FizzleEnchantingRegistry.FILTERING_SHELF.defaultBlockState());
    }

    private static BlockState facing(Direction direction) {
        return FizzleEnchantingRegistry.FILTERING_SHELF.defaultBlockState()
                .setValue(HorizontalDirectionalBlock.FACING, direction);
    }

    private static BlockHitResult hit(BlockPos pos, Direction face, double dx, double dy, double dz) {
        Vec3 location = new Vec3(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
        return new BlockHitResult(location, face, pos, false);
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
        register(reg, MENDING);
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

    /** Mirror of the unfreeze pattern shared by every test in this package. */
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
}
