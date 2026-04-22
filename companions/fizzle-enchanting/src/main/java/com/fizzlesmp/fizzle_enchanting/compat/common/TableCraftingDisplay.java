package com.fizzlesmp.fizzle_enchanting.compat.common;

import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.StatRequirements;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.OptionalInt;

/**
 * Provider-neutral view of a {@code fizzle_enchanting:enchanting} or
 * {@code fizzle_enchanting:keep_nbt_enchanting} recipe shaped for recipe-viewer integrations
 * (EMI / REI / JEI). The plugin code in each {@code compat/<name>/} package adapts this record
 * into its host viewer's display type — keeping the extraction pass shared so a recipe added to
 * the table only has to be plumbed through one place.
 *
 * <p>Designed as part of T-7.1.2 to seed the shared source-of-truth that S-7.2.2 (REI) and
 * the JEI plugin will both consume.
 */
public record TableCraftingDisplay(
        ResourceLocation recipeId,
        Ingredient input,
        ItemStack result,
        StatRequirements requirements,
        StatRequirements maxRequirements,
        OptionalInt displayLevel,
        int xpCost,
        boolean keepNbt
) {
}
