package com.fizzlesmp.fizzle_enchanting.data;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.shelf.FizzleShelves;

import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Block;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-3.4.3 — pins the vanilla-shape recipe set the provider emits. Every shelf whose dependencies
 * are already registered in the current epic must receive a shaped recipe; the three shelves that
 * depend on future-epic items ({@code endshelf}, {@code echoing_sculkshelf},
 * {@code soul_touched_sculkshelf}) and the Prismatic Web must stay deferred. Drift here — an
 * accidentally-deleted recipe, a wrong result block, a missing pattern slot — would silently skip
 * recipes when datagen runs.
 */
class FizzleRecipeProviderTest {

    /**
     * Shelves the provider emits recipes for. Populated in {@link #bootstrap()} because the
     * {@link FizzleShelves} static fields can only be read after {@link Bootstrap#bootStrap()} has
     * seeded the block registry — referencing them eagerly in a class-level constant would trip
     * {@code BuiltInRegistries}'s bootstrap guard during test classload.
     */
    private static Set<Block> expectedShelfResults;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreeze(BuiltInRegistries.MENU, false);
        unfreeze(BuiltInRegistries.BLOCK, true);
        unfreeze(BuiltInRegistries.ITEM, true);
        unfreeze(BuiltInRegistries.BLOCK_ENTITY_TYPE, true);

        FizzleEnchantingRegistry.register();

        // Fabric's BlockItemTracker wires mod BlockItems into Item.BY_BLOCK at registration time
        // via RegistryEntryAddedCallback. That callback only fires inside a running Fabric loader,
        // so plain-JUnit bootstrap leaves the map unseeded for our mod items and Block.asItem()
        // caches Items.AIR on first call. ShapedRecipeBuilder.save() uses the result item's
        // registry key as the recipe ID, so AIR-cached blocks collapse every recipe to
        // `minecraft:air`, overwriting each other in the output map. Populate BY_BLOCK manually
        // before any asItem() call happens in the provider.
        BuiltInRegistries.ITEM.forEach(item -> {
            if (item instanceof BlockItem blockItem) {
                blockItem.registerBlocks(Item.BY_BLOCK, item);
            }
        });

        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
        BuiltInRegistries.MENU.freeze();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();

        expectedShelfResults = Set.of(
                FizzleShelves.BEESHELF,
                FizzleShelves.MELONSHELF,
                FizzleShelves.STONESHELF,
                FizzleShelves.HELLSHELF,
                FizzleShelves.BLAZING_HELLSHELF,
                FizzleShelves.GLOWING_HELLSHELF,
                FizzleShelves.SEASHELF,
                FizzleShelves.HEART_SEASHELF,
                FizzleShelves.CRYSTAL_SEASHELF,
                FizzleShelves.PEARL_ENDSHELF,
                FizzleShelves.DRACONIC_ENDSHELF,
                FizzleShelves.DORMANT_DEEPSHELF,
                FizzleShelves.ECHOING_DEEPSHELF,
                FizzleShelves.SOUL_TOUCHED_DEEPSHELF,
                FizzleShelves.SIGHTSHELF,
                FizzleShelves.SIGHTSHELF_T2,
                FizzleShelves.RECTIFIER,
                FizzleShelves.RECTIFIER_T2,
                FizzleShelves.RECTIFIER_T3);
    }

    @Test
    void buildRecipes_emitsOneRecipePerExpectedShelf() {
        Map<ResourceLocation, Recipe<?>> collected = runProvider();

        assertEquals(expectedShelfResults.size(), collected.size(),
                "one shaped recipe per registered shelf whose deps exist this epic");

        for (Block expected : expectedShelfResults) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(expected);
            assertNotNull(id, () -> "block " + expected + " must be registered to key a recipe");
            ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                    FizzleEnchanting.MOD_ID, id.getPath());
            Recipe<?> recipe = collected.get(recipeId);
            assertNotNull(recipe, () -> "missing recipe for " + id);

            ShapedRecipe shaped = assertInstanceOf(ShapedRecipe.class, recipe,
                    () -> "recipe for " + id + " must be shaped");
            Item resultItem = shaped.getResultItem(null).getItem();
            assertSame(expected.asItem(), resultItem,
                    () -> "recipe for " + id + " must produce the shelf itself");
        }
    }

    @Test
    void buildRecipes_skipsShelvesWithDeferredDependencies() {
        Map<ResourceLocation, Recipe<?>> collected = runProvider();

        // endshelf needs infused_breath (T-5.4.1); both sculk shelves need warden_tendril (T-5.4.2).
        assertFalse(collected.containsKey(FizzleEnchanting.id("endshelf")),
                "endshelf recipe must stay deferred until infused_breath exists");
        assertFalse(collected.containsKey(FizzleEnchanting.id("echoing_sculkshelf")),
                "echoing_sculkshelf recipe must stay deferred until warden_tendril exists");
        assertFalse(collected.containsKey(FizzleEnchanting.id("soul_touched_sculkshelf")),
                "soul_touched_sculkshelf recipe must stay deferred until warden_tendril exists");
    }

    @Test
    void buildRecipes_skipsCustomRecipeTypeShelves() {
        Map<ResourceLocation, Recipe<?>> collected = runProvider();

        // These three land via fizzle_enchanting:enchanting recipes in T-4.6.4 — the vanilla-shape
        // provider intentionally does not emit them.
        assertFalse(collected.containsKey(FizzleEnchanting.id("infused_hellshelf")),
                "infused_hellshelf comes from the enchanting-table recipe type, not crafting");
        assertFalse(collected.containsKey(FizzleEnchanting.id("infused_seashelf")),
                "infused_seashelf comes from the enchanting-table recipe type, not crafting");
        assertFalse(collected.containsKey(FizzleEnchanting.id("deepshelf")),
                "deepshelf comes from the enchanting-table recipe type, not crafting");
    }

    @Test
    void buildRecipes_doesNotEmitPrismaticWeb() {
        Map<ResourceLocation, Recipe<?>> collected = runProvider();

        // Prismatic Web recipe is reserved for T-4.1.3 once PrismaticWebItem is registered.
        assertFalse(collected.containsKey(FizzleEnchanting.id("prismatic_web")),
                "Prismatic Web recipe stays reserved until T-4.1.3");
    }

    @Test
    void everyRecipe_hasAtLeastOneIngredient() {
        Map<ResourceLocation, Recipe<?>> collected = runProvider();

        for (Map.Entry<ResourceLocation, Recipe<?>> entry : collected.entrySet()) {
            Recipe<?> recipe = entry.getValue();
            List<?> ingredients = recipe.getIngredients();
            assertFalse(ingredients.isEmpty(),
                    () -> "recipe " + entry.getKey() + " must have at least one ingredient");
        }
    }

    @Test
    void dormantDeepshelfRecipe_referencesDeepslateTag() {
        assertEquals(FizzleEnchanting.id("deepslate"), FizzleRecipeProvider.DEEPSLATE_TAG.location(),
                "dormant_deepshelf recipe's E slot pulls from fizzle_enchanting:deepslate");
    }

    @Test
    void stoneshelfRecipe_usesPolishedAndesiteAndBooks() {
        Map<ResourceLocation, Recipe<?>> collected = runProvider();
        Recipe<?> recipe = collected.get(FizzleEnchanting.id("stoneshelf"));
        ShapedRecipe shaped = assertInstanceOf(ShapedRecipe.class, recipe);

        // Pattern EEE / BBB / EEE means the 9 ingredient slots should collectively test only
        // polished_andesite (4 corners + 2 edges = top+bottom rows) and book (middle row).
        List<?> ingredients = shaped.getIngredients();
        assertEquals(9, ingredients.size(), "3x3 pattern yields 9 ingredient slots");

        boolean matchesAndesite = ingredients.stream()
                .anyMatch(i -> ((net.minecraft.world.item.crafting.Ingredient) i)
                        .test(Items.POLISHED_ANDESITE.getDefaultInstance()));
        boolean matchesBook = ingredients.stream()
                .anyMatch(i -> ((net.minecraft.world.item.crafting.Ingredient) i)
                        .test(Items.BOOK.getDefaultInstance()));
        assertTrue(matchesAndesite, "stoneshelf recipe must accept polished_andesite");
        assertTrue(matchesBook, "stoneshelf recipe must accept book");
    }

    private static Map<ResourceLocation, Recipe<?>> runProvider() {
        FabricDataOutput output = new FabricDataOutput(null, Paths.get("."), false);
        // Subclass with a mixin-free potion-ingredient stub: the real method wraps
        // DefaultCustomIngredients.components(...), which classloads Fabric's CustomIngredientImpl
        // and trips IncompatibleClassChangeError under plain-JUnit bootstrap because the
        // IngredientMixin that un-finals Ingredient only fires inside a running Fabric loader.
        FizzleRecipeProvider provider = new FizzleRecipeProvider(
                output, CompletableFuture.completedFuture(null)) {
            @Override
            protected Ingredient potionIngredient(Holder<Potion> potion) {
                return Ingredient.of(Items.POTION);
            }
        };

        Map<ResourceLocation, Recipe<?>> collected = new LinkedHashMap<>();
        RecipeOutput exporter = new RecipeOutput() {
            @Override
            public void accept(ResourceLocation id, Recipe<?> recipe, AdvancementHolder advancement) {
                collected.put(id, recipe);
            }

            @Override
            public net.minecraft.advancements.Advancement.Builder advancement() {
                return net.minecraft.advancements.Advancement.Builder.recipeAdvancement();
            }
        };
        provider.buildRecipes(exporter);
        return collected;
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
