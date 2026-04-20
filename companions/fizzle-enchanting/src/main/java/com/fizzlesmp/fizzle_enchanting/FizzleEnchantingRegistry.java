package com.fizzlesmp.fizzle_enchanting;

import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

/**
 * Central registry for blocks, items, block entities, menus, and particles shipped by
 * Fizzle Enchanting. {@link #register()} must run during {@code onInitialize} so entries land
 * before {@code BuiltInRegistries} freezes.
 *
 * <p>MVP surface covers the enchanting-table menu type only; shelf/library/tome registrations
 * land with Epic 3–5.
 */
public final class FizzleEnchantingRegistry {

    public static final MenuType<FizzleEnchantmentMenu> ENCHANTING_TABLE_MENU =
            new MenuType<>(FizzleEnchantmentMenu::new, FeatureFlags.VANILLA_SET);

    private static boolean registered = false;

    private FizzleEnchantingRegistry() {
    }

    public static void register() {
        if (registered) return;
        registered = true;

        Registry.register(
                BuiltInRegistries.MENU,
                FizzleEnchanting.id("enchanting_table"),
                ENCHANTING_TABLE_MENU);
    }
}
