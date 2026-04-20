package com.fizzlesmp.fizzle_enchanting.data;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.shelf.EnchantingShelfBlock;
import com.fizzlesmp.fizzle_enchanting.shelf.FizzleShelves;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-3.4.1 — pins the per-shelf cube_column texture pairs the model provider feeds to datagen.
 *
 * <p>Each shelf must appear exactly once in {@link FizzleModelProvider#shelfTextures()} and the
 * three Zenith-aliased blocks must reuse their family base's side texture (matching Zenith's
 * {@code infused_hellshelf} → {@code block/hellshelf} pattern). Drift here would silently change
 * generated blockstate JSON next time {@code runDatagen} runs.
 */
class FizzleModelProviderTest {

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
    void shelfTextures_coversEvery25Shelves() {
        Map<Block, FizzleModelProvider.ColumnTextures> map = FizzleModelProvider.shelfTextures();
        assertEquals(25, map.size(), "one cube_column entry per shelf in DESIGN");
        // Filter to cube-column shelves only — chiseled-bookshelf-style blocks (filtering shelf,
        // and eventually treasure shelf) own their own model shape and are intentionally absent.
        for (Block shelf : FizzleEnchantingRegistry.BLOCKS.values()) {
            if (!(shelf instanceof EnchantingShelfBlock)) continue;
            assertTrue(map.containsKey(shelf),
                    () -> "shelf " + shelf + " is missing a model entry");
        }
    }

    @Test
    void infusedAndDormantShelves_aliasBaseFamilySide() {
        Map<Block, FizzleModelProvider.ColumnTextures> map = FizzleModelProvider.shelfTextures();
        assertEquals(map.get(FizzleShelves.HELLSHELF).side(),
                map.get(FizzleShelves.INFUSED_HELLSHELF).side(),
                "infused_hellshelf reuses hellshelf side per Zenith");
        assertEquals(map.get(FizzleShelves.SEASHELF).side(),
                map.get(FizzleShelves.INFUSED_SEASHELF).side(),
                "infused_seashelf reuses seashelf side per Zenith");
        assertEquals(map.get(FizzleShelves.DEEPSHELF).side(),
                map.get(FizzleShelves.DORMANT_DEEPSHELF).side(),
                "dormant_deepshelf reuses deepshelf side per Zenith");
    }

    @Test
    void sculkShelves_shareSculkshelfTopEnd() {
        Map<Block, FizzleModelProvider.ColumnTextures> map = FizzleModelProvider.shelfTextures();
        ResourceLocation sculkTop = FizzleEnchanting.id("block/sculkshelf_top");
        assertEquals(sculkTop, map.get(FizzleShelves.ECHOING_SCULKSHELF).end());
        assertEquals(sculkTop, map.get(FizzleShelves.SOUL_TOUCHED_SCULKSHELF).end());
    }

    @Test
    void sightshelfBaseTier_usesSightSideAndTop() {
        FizzleModelProvider.ColumnTextures tex =
                FizzleModelProvider.shelfTextures().get(FizzleShelves.SIGHTSHELF);
        assertEquals(FizzleEnchanting.id("block/sight_side"), tex.side());
        assertEquals(FizzleEnchanting.id("block/sight_top"), tex.end());
    }

    @Test
    void rectifierTier_endTexturesMatchZenithParents() {
        Map<Block, FizzleModelProvider.ColumnTextures> map = FizzleModelProvider.shelfTextures();
        assertEquals(ResourceLocation.withDefaultNamespace("block/prismarine_bricks"),
                map.get(FizzleShelves.RECTIFIER).end());
        assertEquals(FizzleEnchanting.id("block/rectifier_t2_top"),
                map.get(FizzleShelves.RECTIFIER_T2).end());
        assertEquals(ResourceLocation.withDefaultNamespace("block/purpur_block"),
                map.get(FizzleShelves.RECTIFIER_T3).end());
    }

    @Test
    void everyEntry_hasNonNullSideAndEnd() {
        for (Map.Entry<Block, FizzleModelProvider.ColumnTextures> entry :
                FizzleModelProvider.shelfTextures().entrySet()) {
            FizzleModelProvider.ColumnTextures tex = entry.getValue();
            assertNotNull(tex.side(), () -> "missing side texture for " + entry.getKey());
            assertNotNull(tex.end(), () -> "missing end texture for " + entry.getKey());
        }
    }

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
