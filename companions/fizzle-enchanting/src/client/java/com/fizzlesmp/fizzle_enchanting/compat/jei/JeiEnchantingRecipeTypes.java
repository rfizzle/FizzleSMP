package com.fizzlesmp.fizzle_enchanting.compat.jei;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.compat.common.TableCraftingDisplay;
import mezz.jei.api.recipe.RecipeType;

/**
 * Holds the two JEI {@link RecipeType} ids the plugin registers — one for the shelf-upgrade tab
 * and one for the tome-crafting tab. Both wrap the shared {@link TableCraftingDisplay} record so
 * the category code never has to reach at the underlying {@code fizzle_enchanting:enchanting} /
 * {@code keep_nbt_enchanting} recipe types directly.
 */
public final class JeiEnchantingRecipeTypes {

    public static final RecipeType<TableCraftingDisplay> SHELVES = RecipeType.create(
            FizzleEnchanting.MOD_ID,
            "shelves",
            TableCraftingDisplay.class);

    public static final RecipeType<TableCraftingDisplay> TOMES = RecipeType.create(
            FizzleEnchanting.MOD_ID,
            "tomes",
            TableCraftingDisplay.class);

    private JeiEnchantingRecipeTypes() {
    }
}
