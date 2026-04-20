package com.fizzlesmp.fizzle_enchanting;

import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central registry for blocks, items, block entities, menus, and particles shipped by
 * Fizzle Enchanting. {@link #register()} must run during {@code onInitialize} so entries land
 * before {@code BuiltInRegistries} freezes.
 *
 * <p>Shelves, library blocks, and any other content registered through
 * {@link #registerBlock(String, Block, Item.Properties)} is also recorded in {@link #BLOCKS}
 * in insertion order, so datagen and integration compat modules can walk the full set without
 * re-parsing the registry.
 */
public final class FizzleEnchantingRegistry {

    /**
     * Insertion-ordered view of every block registered via
     * {@link #registerBlock(String, Block, Item.Properties)}. Consumers may iterate this for
     * datagen or compat but must not mutate the map directly.
     */
    public static final Map<ResourceLocation, Block> BLOCKS = new LinkedHashMap<>();

    public static final MenuType<FizzleEnchantmentMenu> ENCHANTING_TABLE_MENU =
            new MenuType<>(FizzleEnchantmentMenu::new, FeatureFlags.VANILLA_SET);

    private static boolean registered = false;

    private FizzleEnchantingRegistry() {
    }

    public static void register() {
        if (registered) return;
        registered = true;

        registerMenuType("enchanting_table", ENCHANTING_TABLE_MENU);
    }

    /**
     * Registers a block and its matching {@link BlockItem} under the same path. The block is
     * also added to {@link #BLOCKS}.
     *
     * @param name      path component; namespaced to {@link FizzleEnchanting#MOD_ID}
     * @param block     pre-constructed block instance
     * @param itemProps item properties for the companion {@code BlockItem}
     * @return the registered block, for fluent assignment at call sites
     */
    public static <T extends Block> T registerBlock(String name, T block, Item.Properties itemProps) {
        ResourceLocation id = FizzleEnchanting.id(name);
        Registry.register(BuiltInRegistries.BLOCK, id, block);
        BLOCKS.put(id, block);
        Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, itemProps));
        return block;
    }

    /** Registers a standalone item under {@code fizzle_enchanting:<name>}. */
    public static <T extends Item> T registerItem(String name, T item) {
        Registry.register(BuiltInRegistries.ITEM, FizzleEnchanting.id(name), item);
        return item;
    }

    /** Registers a menu type under {@code fizzle_enchanting:<name>}. */
    public static <T extends AbstractContainerMenu> MenuType<T> registerMenuType(String name, MenuType<T> type) {
        Registry.register(BuiltInRegistries.MENU, FizzleEnchanting.id(name), type);
        return type;
    }

    /** Registers a block-entity type under {@code fizzle_enchanting:<name>}. */
    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntityType(String name, BlockEntityType<T> type) {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, FizzleEnchanting.id(name), type);
        return type;
    }
}
