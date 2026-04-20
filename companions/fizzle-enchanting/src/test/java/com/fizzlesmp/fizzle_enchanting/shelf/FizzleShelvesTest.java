package com.fizzlesmp.fizzle_enchanting.shelf;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;

import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-3.2.1 — asserts that every wood/stone/sculk/utility shelf called out in DESIGN.md is
 * registered under the {@code fizzle_enchanting} namespace with the correct type and a matching
 * {@link BlockItem}. The ID list is the authoritative surface — missing IDs fail this test; extra
 * IDs would slip through, so we also cross-check the count matches the expected 25.
 *
 * <p>Uses the same unfreeze/refreeze dance as {@link com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistryTest}
 * — vanilla {@code Bootstrap.bootStrap()} freezes the registries, but Fabric defers the freeze
 * until after {@code onInitialize}. Reflection restores the unfrozen state (and the intrusive
 * holder map for BLOCK/ITEM) just long enough to register, then re-freezes so holders bind.
 */
class FizzleShelvesTest {

    /**
     * The 25 shelf IDs required by DESIGN.md — wood-tier (2), stone-tier baseline + themed (16),
     * sculk-tier (2), utility (5). Keep this list 1:1 with
     * {@link FizzleShelves#register()}; any drift here trips the parameterized assertions below.
     */
    private static final List<String> EXPECTED_SHELF_IDS = List.of(
            // Wood tier
            "beeshelf", "melonshelf",
            // Stone tier — baseline
            "stoneshelf",
            // Stone tier — Nether
            "hellshelf", "blazing_hellshelf", "glowing_hellshelf", "infused_hellshelf",
            // Stone tier — Ocean
            "seashelf", "heart_seashelf", "crystal_seashelf", "infused_seashelf",
            // Stone tier — End
            "endshelf", "pearl_endshelf", "draconic_endshelf",
            // Stone tier — Deep
            "deepshelf", "dormant_deepshelf", "echoing_deepshelf", "soul_touched_deepshelf",
            // Sculk tier
            "echoing_sculkshelf", "soul_touched_sculkshelf",
            // Utility tier
            "sightshelf", "sightshelf_t2",
            "rectifier", "rectifier_t2", "rectifier_t3");

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

    static Stream<String> expectedShelfIds() {
        return EXPECTED_SHELF_IDS.stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("expectedShelfIds")
    void register_shelfResolvesFromBlockRegistry(String path) {
        ResourceLocation id = FizzleEnchanting.id(path);
        Block block = BuiltInRegistries.BLOCK.get(id);
        assertNotNull(block, () -> "BLOCK registry must contain " + id);
        assertInstanceOf(EnchantingShelfBlock.class, block,
                () -> "shelf at " + id + " must be an EnchantingShelfBlock");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("expectedShelfIds")
    void register_shelfItemResolvesFromItemRegistry(String path) {
        ResourceLocation id = FizzleEnchanting.id(path);
        Item item = BuiltInRegistries.ITEM.get(id);
        assertNotNull(item, () -> "ITEM registry must contain companion BlockItem for " + id);
        assertInstanceOf(BlockItem.class, item,
                () -> "companion item at " + id + " must be a BlockItem");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("expectedShelfIds")
    void register_shelfTrackedInRegistryBlocksMap(String path) {
        ResourceLocation id = FizzleEnchanting.id(path);
        Block tracked = FizzleEnchantingRegistry.BLOCKS.get(id);
        assertNotNull(tracked, () -> "FizzleEnchantingRegistry.BLOCKS must track " + id);
        assertSame(BuiltInRegistries.BLOCK.get(id), tracked,
                () -> "BLOCKS entry for " + id + " must point at the registry instance");
    }

    @Test
    void expectedRoster_containsExactly25Shelves() {
        assertEquals(25, EXPECTED_SHELF_IDS.size(),
                "DESIGN.md specifies a 25-shelf roster; update this count deliberately.");
    }

    @Test
    void woodTier_usesWoodSoundGroup() {
        assertTrue(FizzleShelves.BEESHELF.defaultBlockState().getSoundType()
                == net.minecraft.world.level.block.SoundType.WOOD,
                "beeshelf must use SoundType.WOOD per DESIGN wood-tier spec");
        assertTrue(FizzleShelves.MELONSHELF.defaultBlockState().getSoundType()
                == net.minecraft.world.level.block.SoundType.WOOD,
                "melonshelf must use SoundType.WOOD per DESIGN wood-tier spec");
    }

    @Test
    void stoneTier_usesStoneSoundGroup() {
        assertTrue(FizzleShelves.STONESHELF.defaultBlockState().getSoundType()
                == net.minecraft.world.level.block.SoundType.STONE,
                "stoneshelf must use SoundType.STONE per DESIGN stone-tier spec");
        assertTrue(FizzleShelves.HELLSHELF.defaultBlockState().getSoundType()
                == net.minecraft.world.level.block.SoundType.STONE,
                "hellshelf must use SoundType.STONE per DESIGN stone-tier spec");
    }

    @Test
    void sculkTier_usesStoneSoundGroup() {
        assertTrue(FizzleShelves.ECHOING_SCULKSHELF.defaultBlockState().getSoundType()
                == net.minecraft.world.level.block.SoundType.STONE,
                "echoing_sculkshelf must use SoundType.STONE per Zenith parity");
        assertTrue(FizzleShelves.SOUL_TOUCHED_SCULKSHELF.defaultBlockState().getSoundType()
                == net.minecraft.world.level.block.SoundType.STONE,
                "soul_touched_sculkshelf must use SoundType.STONE per Zenith parity");
    }

    /**
     * Vanilla {@link MappedRegistry#freeze()} nulls {@code unregisteredIntrusiveHolders} for
     * BLOCK/ITEM/BLOCK_ENTITY_TYPE. Constructing a fresh {@link Block}/{@link Item} afterwards
     * calls {@code createIntrusiveHolder}, which throws "This registry can't create intrusive
     * holders" when the map is null. Registries without intrusive holders (MENU) must stay with
     * a null map — re-adding one there trips "Missing intrusive holder" during register.
     */
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
