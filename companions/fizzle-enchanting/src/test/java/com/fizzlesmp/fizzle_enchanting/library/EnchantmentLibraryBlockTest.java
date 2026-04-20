package com.fizzlesmp.fizzle_enchanting.library;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-4.4.1 — proves both library blocks round-trip through {@link FizzleEnchantingRegistry}:
 * instance, companion {@link BlockItem}, insertion-ordered {@link FizzleEnchantingRegistry#BLOCKS}
 * entry, and the BE type bound to the right block. A regression here would silently break vanilla
 * {@code BlockEntity#validateBlockState} at chunk-load, where the mismatch only surfaces as log
 * spam — so the assertions guard both directions of the pairing.
 */
class EnchantmentLibraryBlockTest {

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
    void basicLibrary_registeredWithCompanionItem() {
        ResourceLocation id = FizzleEnchanting.id("library");

        Block resolvedBlock = BuiltInRegistries.BLOCK.get(id);
        assertNotNull(resolvedBlock, "library block must be present in BuiltInRegistries.BLOCK");
        assertSame(FizzleEnchantingRegistry.BASIC_LIBRARY, resolvedBlock,
                "registry lookup must return the Basic Library instance exposed on the registry");

        Item resolvedItem = BuiltInRegistries.ITEM.get(id);
        assertInstanceOf(BlockItem.class, resolvedItem,
                "registerBlock must ship a companion BlockItem for the Basic Library");

        assertTrue(FizzleEnchantingRegistry.BLOCKS.containsKey(id),
                "BLOCKS insertion-ordered view must include the Basic Library");
    }

    @Test
    void enderLibrary_registeredWithCompanionItem() {
        ResourceLocation id = FizzleEnchanting.id("ender_library");

        Block resolvedBlock = BuiltInRegistries.BLOCK.get(id);
        assertNotNull(resolvedBlock, "ender_library block must be present in BuiltInRegistries.BLOCK");
        assertSame(FizzleEnchantingRegistry.ENDER_LIBRARY, resolvedBlock,
                "registry lookup must return the Ender Library instance exposed on the registry");

        Item resolvedItem = BuiltInRegistries.ITEM.get(id);
        assertInstanceOf(BlockItem.class, resolvedItem,
                "registerBlock must ship a companion BlockItem for the Ender Library");

        assertTrue(FizzleEnchantingRegistry.BLOCKS.containsKey(id),
                "BLOCKS insertion-ordered view must include the Ender Library");
    }

    @Test
    void libraryBlocks_distinctInstances() {
        // Guard against a copy-paste mistake where both tiers accidentally point at the same
        // block — which would silently merge the point pools of the two tiers at a player's
        // library upgrade.
        assertNotNull(FizzleEnchantingRegistry.BASIC_LIBRARY);
        assertNotNull(FizzleEnchantingRegistry.ENDER_LIBRARY);
        assertTrue(FizzleEnchantingRegistry.BASIC_LIBRARY
                != FizzleEnchantingRegistry.ENDER_LIBRARY,
                "Basic and Ender libraries must be distinct block instances");
    }

    @Test
    void basicLibrary_newBlockEntity_producesBasicTile() {
        BlockEntity be = FizzleEnchantingRegistry.BASIC_LIBRARY.newBlockEntity(
                BlockPos.ZERO,
                FizzleEnchantingRegistry.BASIC_LIBRARY.defaultBlockState());
        assertInstanceOf(BasicLibraryBlockEntity.class, be,
                "Basic Library block's supplier must mint BasicLibraryBlockEntity");
        assertEquals(BasicLibraryBlockEntity.MAX_LEVEL, ((BasicLibraryBlockEntity) be).getMaxLevel());
    }

    @Test
    void enderLibrary_newBlockEntity_producesEnderTile() {
        BlockEntity be = FizzleEnchantingRegistry.ENDER_LIBRARY.newBlockEntity(
                BlockPos.ZERO,
                FizzleEnchantingRegistry.ENDER_LIBRARY.defaultBlockState());
        assertInstanceOf(EnderLibraryBlockEntity.class, be,
                "Ender Library block's supplier must mint EnderLibraryBlockEntity");
        assertEquals(EnderLibraryBlockEntity.MAX_LEVEL, ((EnderLibraryBlockEntity) be).getMaxLevel());
    }

    @Test
    void libraryBlockEntityTypes_boundToLibraryBlocks() {
        // Vanilla warns at chunk-load if a BE's type does not accept its host block's state; binding
        // each library BE type to the matching block is what silences that warning, so we assert
        // both legs of the pairing.
        BlockEntityType<?> basicType = BuiltInRegistries.BLOCK_ENTITY_TYPE
                .get(FizzleEnchanting.id("library"));
        assertSame(FizzleEnchantingRegistry.BASIC_LIBRARY_BE, basicType);
        assertTrue(basicType.isValid(FizzleEnchantingRegistry.BASIC_LIBRARY.defaultBlockState()),
                "BASIC_LIBRARY_BE must accept the Basic Library block's default state");

        BlockEntityType<?> enderType = BuiltInRegistries.BLOCK_ENTITY_TYPE
                .get(FizzleEnchanting.id("ender_library"));
        assertSame(FizzleEnchantingRegistry.ENDER_LIBRARY_BE, enderType);
        assertTrue(enderType.isValid(FizzleEnchantingRegistry.ENDER_LIBRARY.defaultBlockState()),
                "ENDER_LIBRARY_BE must accept the Ender Library block's default state");
    }

    /** Same unfreeze contract as {@link com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistryTest}. */
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
