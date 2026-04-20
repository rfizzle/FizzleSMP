package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.shelf.TreasureShelfBlockEntity;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-3.5.2 — pins the two documented behaviors of the treasure shelf:
 * <ol>
 *   <li>The block + BE register cleanly under {@code fizzle_enchanting:treasure_shelf}, with the
 *       BE typed to {@link FizzleEnchantingRegistry#TREASURE_SHELF_BE} (not the vanilla CHISELED
 *       type, which would trip {@code validateBlockState}).</li>
 *   <li>An in-range {@link TreasureShelfBlockEntity} flips
 *       {@link StatCollection#treasureAllowed()} to {@code true}; absence keeps the flag
 *       {@code false}. The BE alone is the source of truth — there is no stat JSON for
 *       {@code treasure_shelf}, so the only path that can set the flag is the
 *       {@link TreasureFlagSource} marker.</li>
 * </ol>
 *
 * <p>Mirrors the registry-unfreeze + bootstrap dance from {@code FilteringShelfTest}: vanilla's
 * {@link Bootstrap#bootStrap()} freezes {@link BuiltInRegistries}, so we manually un-freeze BLOCK,
 * ITEM, MENU, and BLOCK_ENTITY_TYPE long enough to register our own entries before re-freezing.
 */
class TreasureShelfTest {

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
    }

    @Test
    void registry_treasureShelfRegisteredUnderExpectedId() {
        ResourceLocation id = FizzleEnchanting.id("treasure_shelf");
        assertSame(FizzleEnchantingRegistry.TREASURE_SHELF, BuiltInRegistries.BLOCK.get(id),
                "treasure_shelf block must resolve to FizzleEnchantingRegistry.TREASURE_SHELF");
        assertSame(FizzleEnchantingRegistry.TREASURE_SHELF_BE,
                BuiltInRegistries.BLOCK_ENTITY_TYPE.get(id),
                "treasure_shelf BE type must resolve to the registered instance");
    }

    @Test
    void registry_treasureShelfHasCompanionBlockItem() {
        Item item = BuiltInRegistries.ITEM.get(FizzleEnchanting.id("treasure_shelf"));
        assertNotNull(item, "registerBlock must produce a companion Item");
        assertInstanceOf(BlockItem.class, item,
                "the companion Item must be a BlockItem so the player can place it");
    }

    @Test
    void newBlockEntity_returnsTreasureShelfBlockEntity() {
        BlockEntity be = FizzleEnchantingRegistry.TREASURE_SHELF.newBlockEntity(
                BlockPos.ZERO, FizzleEnchantingRegistry.TREASURE_SHELF.defaultBlockState());
        assertNotNull(be, "newBlockEntity must produce an entity");
        assertInstanceOf(TreasureShelfBlockEntity.class, be,
                "BE produced by TreasureShelfBlock must be the matching subclass");
        assertInstanceOf(TreasureFlagSource.class, be,
                "the BE must implement TreasureFlagSource so the gather pipeline picks it up");
    }

    @Test
    void getType_returnsRegisteredTreasureShelfType() {
        TreasureShelfBlockEntity be = newEntity();
        BlockEntityType<?> type = be.getType();
        assertSame(FizzleEnchantingRegistry.TREASURE_SHELF_BE, type,
                "BE getType() must return our registered type");
    }

    @Test
    void gather_treasureShelfPresentInRange_setsTreasureAllowed() {
        EnchantingStatRegistry reg = new EnchantingStatRegistry();
        TreasureShelfBlockEntity be = newEntity();
        Function<BlockPos, Object> contextLookup = pos ->
                pos.getX() == 1 ? be : null;

        StatCollection result = reg.gatherStatsFromOffsets(
                offsets(3), pos -> EnchantingStats.ZERO, pos -> true, contextLookup);

        assertTrue(result.treasureAllowed(),
                "a single in-range TreasureShelfBlockEntity must flip the treasure flag on");
    }

    @Test
    void gather_treasureShelfAbsent_treasureAllowedStaysFalse() {
        EnchantingStatRegistry reg = new EnchantingStatRegistry();

        StatCollection result = reg.gatherStatsFromOffsets(
                offsets(3), pos -> EnchantingStats.ZERO, pos -> true, pos -> null);

        assertFalse(result.treasureAllowed(),
                "no treasure shelf in range → treasureAllowed must remain false");
    }

    @Test
    void gather_treasureShelfBlockedByMidpointCheck_doesNotSetFlag() {
        EnchantingStatRegistry reg = new EnchantingStatRegistry();
        TreasureShelfBlockEntity be = newEntity();
        // Shelf sits at x=0 but the midpoint check rejects that position — the BE must not be
        // consulted, matching the "blocked midpoint contributes nothing" rule from T-2.2.3.
        Function<BlockPos, Object> contextLookup = pos -> pos.getX() == 0 ? be : null;

        StatCollection result = reg.gatherStatsFromOffsets(
                offsets(3), pos -> EnchantingStats.ZERO, pos -> pos.getX() != 0, contextLookup);

        assertFalse(result.treasureAllowed(),
                "a treasure shelf whose midpoint is blocked must not flip the flag");
    }

    @Test
    void gather_treasureShelfWithZeroStatLookup_keepsAggregatesAtZero() {
        // DESIGN's "no Eterna contribution of its own" bullet is enforced by shipping no stat
        // JSON for treasure_shelf — the registry resolver returns EnchantingStats.ZERO for any
        // block missing both a JSON entry and an ENCHANTMENT_POWER_PROVIDER tag, so the gather
        // pipeline sums in zeros. Simulate that by feeding ZERO as the stat lookup result and
        // confirming the only effect of an in-range treasure shelf is the treasure flag.
        EnchantingStatRegistry reg = new EnchantingStatRegistry();
        TreasureShelfBlockEntity be = newEntity();
        Function<BlockPos, Object> contextLookup = pos -> pos.getX() == 0 ? be : null;

        StatCollection result = reg.gatherStatsFromOffsets(
                offsets(3), pos -> EnchantingStats.ZERO, pos -> true, contextLookup);

        assertEquals(0F, result.eterna(), 1e-6, "treasure shelf must add no eterna");
        assertEquals(0F, result.maxEterna(), 1e-6, "treasure shelf must add no maxEterna cap");
        assertEquals(0F, result.quanta(), 1e-6, "treasure shelf must add no quanta");
        assertEquals(0F, result.arcana(), 1e-6, "treasure shelf must add no arcana");
        assertTrue(result.treasureAllowed(),
                "the treasure flag is the shelf's only contribution");
    }

    private static TreasureShelfBlockEntity newEntity() {
        return new TreasureShelfBlockEntity(
                BlockPos.ZERO,
                FizzleEnchantingRegistry.TREASURE_SHELF.defaultBlockState());
    }

    private static List<BlockPos> offsets(int count) {
        BlockPos[] arr = new BlockPos[count];
        for (int i = 0; i < count; i++) {
            arr[i] = new BlockPos(i, 0, 0);
        }
        return List.of(arr);
    }

    /** Mirror of the unfreeze pattern shared by every registry-touching test in this package. */
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
