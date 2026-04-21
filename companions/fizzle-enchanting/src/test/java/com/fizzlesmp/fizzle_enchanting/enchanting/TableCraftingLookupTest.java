package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.EnchantingRecipe;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.EnchantingRecipeRegistry;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.KeepNbtEnchantingRecipe;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.StatRequirements;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-5.3.1 — proves {@link FizzleEnchantmentMenu#lookupCraftingResult} resolves the stat-gated
 * crafting recipe the menu's {@code slotsChanged} now consults after {@code gatherStats}. The
 * menu field (`currentRecipe`) is populated by calling this helper with the level's recipe
 * manager and the scripted stat totals from the shelf scan; simulating that path without
 * fabric-loader-junit means driving the helper directly.
 *
 * <p>Scenario modeled here is the DESIGN-documented
 * {@code library → ender_library} upgrade: a {@code keep_nbt_enchanting} recipe registered against
 * {@code Items.BOOK} (the scripted input stand-in) that unlocks only when shelf totals hit
 * {@code E 50 / Q 45 / A 100}. A lower-stat scan (simulated below) should leave
 * {@link Optional#empty()} so the fourth-row preview stays hidden.
 */
class TableCraftingLookupTest {

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreeze(BuiltInRegistries.RECIPE_TYPE);
        unfreeze(BuiltInRegistries.RECIPE_SERIALIZER);
        EnchantingRecipeRegistry.register();
        BuiltInRegistries.RECIPE_TYPE.freeze();
        BuiltInRegistries.RECIPE_SERIALIZER.freeze();
    }

    @Test
    void lookupCraftingResult_inputAtMatchingStats_locatesRecipe() {
        // Library → ender_library shape: keep-nbt recipe, E=50 Q=45..50 A=100.
        KeepNbtEnchantingRecipe enderUpgrade = new KeepNbtEnchantingRecipe(
                Ingredient.of(Items.BOOK),
                new StatRequirements(50F, 45F, 100F),
                new StatRequirements(50F, 50F, 100F),
                new ItemStack(Items.ENCHANTED_BOOK),
                OptionalInt.empty(),
                0);
        RecipeManager rm = managerWith(holder("ender_library", enderUpgrade));

        StatCollection shelves = stats(50F, 48F, 100F);
        Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>> hit =
                FizzleEnchantmentMenu.lookupCraftingResult(rm, new ItemStack(Items.BOOK), shelves);

        assertTrue(hit.isPresent(),
                "library input at Eterna 50 / Quanta 48 / Arcana 100 must resolve the ender upgrade");
        assertSame(enderUpgrade, hit.get().value(),
                "resolver must return the keep-nbt recipe, not another tier that happens to match");
        assertInstanceOf(KeepNbtEnchantingRecipe.class, hit.get().value(),
                "keep-nbt variant is required so clicking the crafting row preserves stored books");
    }

    @Test
    void lookupCraftingResult_statsBelowRequirements_returnsEmpty() {
        EnchantingRecipe tierTwoUpgrade = new EnchantingRecipe(
                Ingredient.of(Items.BOOK),
                new StatRequirements(40F, 15F, 60F),
                StatRequirements.NO_MAX,
                new ItemStack(Items.ENCHANTED_BOOK),
                OptionalInt.empty(),
                0);
        RecipeManager rm = managerWith(holder("upgrade", tierTwoUpgrade));

        StatCollection underpowered = stats(30F, 10F, 40F);
        Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>> hit =
                FizzleEnchantmentMenu.lookupCraftingResult(rm, new ItemStack(Items.BOOK), underpowered);

        assertEquals(Optional.empty(), hit,
                "stats below the recipe's minima must leave the menu's currentRecipe empty");
    }

    @Test
    void lookupCraftingResult_emptyInput_shortCircuits() {
        EnchantingRecipe always = new EnchantingRecipe(
                Ingredient.of(Items.BOOK),
                new StatRequirements(0F, 0F, 0F),
                StatRequirements.NO_MAX,
                new ItemStack(Items.ENCHANTED_BOOK),
                OptionalInt.empty(),
                0);
        RecipeManager rm = managerWith(holder("always", always));

        Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>> hit =
                FizzleEnchantmentMenu.lookupCraftingResult(rm, ItemStack.EMPTY, stats(50F, 50F, 100F));

        assertEquals(Optional.empty(), hit,
                "empty input must short-circuit before the recipe manager scan — no point matching against nothing");
    }

    @Test
    void lookupCraftingResult_ingredientMismatch_returnsEmpty() {
        EnchantingRecipe bookRecipe = new EnchantingRecipe(
                Ingredient.of(Items.BOOK),
                new StatRequirements(0F, 0F, 0F),
                StatRequirements.NO_MAX,
                new ItemStack(Items.ENCHANTED_BOOK),
                OptionalInt.empty(),
                0);
        RecipeManager rm = managerWith(holder("books_only", bookRecipe));

        Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>> hit =
                FizzleEnchantmentMenu.lookupCraftingResult(rm, new ItemStack(Items.DIAMOND_SWORD), stats(50F, 50F, 100F));

        assertEquals(Optional.empty(), hit,
                "input that fails the ingredient predicate must not match regardless of shelf totals");
    }

    private static StatCollection stats(float eterna, float quanta, float arcana) {
        return new StatCollection(eterna, quanta, arcana, 0F, 0, eterna, Set.of(), false);
    }

    private static RecipeHolder<? extends Recipe<SingleRecipeInput>> holder(String path, EnchantingRecipe recipe) {
        return new RecipeHolder<>(FizzleEnchanting.id(path), recipe);
    }

    private static RecipeManager managerWith(RecipeHolder<?>... holders) {
        RecipeManager manager = new RecipeManager(RegistryAccess.EMPTY);
        manager.replaceRecipes(List.of(holders));
        return manager;
    }

    private static void unfreeze(Registry<?> registry) throws Exception {
        // Flip `frozen` only — leaving `unregisteredIntrusiveHolders` at its existing (null) value
        // keeps Registry.register on the non-intrusive path, which is what RECIPE_TYPE /
        // RECIPE_SERIALIZER expect. Touching the intrusive map here triggers the "Missing intrusive
        // holder" assertion path from MappedRegistry.register.
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(registry, false);
    }
}
