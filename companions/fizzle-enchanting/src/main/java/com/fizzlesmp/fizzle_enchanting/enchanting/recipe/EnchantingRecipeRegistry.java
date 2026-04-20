package com.fizzlesmp.fizzle_enchanting.enchanting.recipe;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.enchanting.StatCollection;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Holds the two new {@link RecipeType} / serializer pairs introduced by Epic 4 and routes recipe
 * lookups at runtime. The types land in {@link BuiltInRegistries#RECIPE_TYPE} /
 * {@link BuiltInRegistries#RECIPE_SERIALIZER} during {@link #register()} — call during
 * {@code onInitialize} before registries freeze.
 *
 * <p>Recipe IDs:
 * <ul>
 *   <li>{@code fizzle_enchanting:enchanting} — {@link EnchantingRecipe}.</li>
 *   <li>{@code fizzle_enchanting:keep_nbt_enchanting} — {@link KeepNbtEnchantingRecipe}.</li>
 * </ul>
 */
public final class EnchantingRecipeRegistry {

    public static final RecipeType<EnchantingRecipe> ENCHANTING_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return "fizzle_enchanting:enchanting";
        }
    };

    public static final RecipeType<KeepNbtEnchantingRecipe> KEEP_NBT_TYPE = new RecipeType<>() {
        @Override
        public String toString() {
            return "fizzle_enchanting:keep_nbt_enchanting";
        }
    };

    public static final EnchantingRecipe.Serializer ENCHANTING_SERIALIZER = new EnchantingRecipe.Serializer();
    public static final KeepNbtEnchantingRecipe.Serializer KEEP_NBT_SERIALIZER = new KeepNbtEnchantingRecipe.Serializer();

    private static boolean registered = false;

    private EnchantingRecipeRegistry() {
    }

    public static void register() {
        if (registered) return;
        registered = true;
        Registry.register(BuiltInRegistries.RECIPE_TYPE, FizzleEnchanting.id("enchanting"), ENCHANTING_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_TYPE, FizzleEnchanting.id("keep_nbt_enchanting"), KEEP_NBT_TYPE);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, FizzleEnchanting.id("enchanting"), ENCHANTING_SERIALIZER);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, FizzleEnchanting.id("keep_nbt_enchanting"), KEEP_NBT_SERIALIZER);
    }

    /**
     * Locates the best-matching enchantment-table recipe for the given input + stat totals. Scans
     * both recipe types and picks the first one whose ingredient accepts the stack and whose
     * stat window contains the supplied stats.
     *
     * <p>Recipes are visited in descending order of their Eterna minimum so tiered recipes on the
     * same input (see DESIGN § "Enchantment-Table Crafting" — `hellshelf → infused_hellshelf` vs.
     * a hypothetical cheaper base) resolve to the hardest match first. Mirrors Zenith's
     * {@code EnchantingRecipe#findMatch} sort behaviour.
     *
     * @return the matching {@link RecipeHolder}, or {@link Optional#empty()} when no recipe
     *         accepts this pairing.
     */
    public static Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>> findMatch(
            Level level, ItemStack input, StatCollection stats) {
        return findMatch(level.getRecipeManager(), input, stats);
    }

    /**
     * {@link RecipeManager}-scoped overload, carved out so unit tests can exercise the matcher
     * without standing up a full {@link Level}.
     */
    public static Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>> findMatch(
            RecipeManager recipes, ItemStack input, StatCollection stats) {
        List<RecipeHolder<? extends EnchantingRecipe>> candidates = new ArrayList<>();
        candidates.addAll(recipes.getAllRecipesFor(ENCHANTING_TYPE));
        candidates.addAll(recipes.getAllRecipesFor(KEEP_NBT_TYPE));
        candidates.sort(Comparator.comparingDouble(
                (RecipeHolder<? extends EnchantingRecipe> r) -> r.value().getRequirements().eterna()).reversed());

        for (RecipeHolder<? extends EnchantingRecipe> holder : candidates) {
            EnchantingRecipe recipe = holder.value();
            if (recipe.matches(input, stats.eterna(), stats.quanta(), stats.arcana())) {
                return Optional.of(holder);
            }
        }
        return Optional.empty();
    }
}
