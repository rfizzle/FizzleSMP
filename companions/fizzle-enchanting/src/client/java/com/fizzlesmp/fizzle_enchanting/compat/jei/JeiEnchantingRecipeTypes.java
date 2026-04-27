package com.fizzlesmp.fizzle_enchanting.compat.jei;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.compat.common.TableCraftingDisplay;
import mezz.jei.api.recipe.RecipeType;

/**
 * Holds the JEI {@link RecipeType} id for the single "Infusions" category. Wraps the shared
 * {@link TableCraftingDisplay} record so the category code never has to reach at the underlying
 * {@code fizzle_enchanting:enchanting} / {@code keep_nbt_enchanting} recipe types directly.
 */
public final class JeiEnchantingRecipeTypes {

    public static final RecipeType<TableCraftingDisplay> INFUSIONS = RecipeType.create(
            FizzleEnchanting.MOD_ID,
            "infusions",
            TableCraftingDisplay.class);

    private JeiEnchantingRecipeTypes() {
    }
}
