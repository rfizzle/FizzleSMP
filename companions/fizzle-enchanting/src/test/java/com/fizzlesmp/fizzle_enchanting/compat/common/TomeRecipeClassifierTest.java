package com.fizzlesmp.fizzle_enchanting.compat.common;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.StatRequirements;
import com.fizzlesmp.fizzle_enchanting.shelf.FizzleShelves;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-7.3.1 — pins the shared Shelves-vs-Tomes split used by the EMI, REI, and JEI adapters.
 *
 * <p>The three recipe-viewer plugins route every {@link TableCraftingDisplay} into one of two
 * tabs. The decision must stay identical across viewers — this test is the single source of
 * truth and will catch any viewer-specific drift before it reaches a player's UI.
 */
class TomeRecipeClassifierTest {

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreezeIntrusive(BuiltInRegistries.BLOCK);
        unfreezeIntrusive(BuiltInRegistries.ITEM);
        unfreezeIntrusive(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        unfreeze(BuiltInRegistries.MENU);
        FizzleEnchantingRegistry.register();
        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
        BuiltInRegistries.MENU.freeze();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();
    }

    @Test
    void scrapTomeResult_routesToTomesTab() {
        TableCraftingDisplay display = makeDisplay(new ItemStack(FizzleEnchantingRegistry.SCRAP_TOME));
        assertTrue(TomeRecipeClassifier.isTomeRecipe(display),
                "Scrap Tome output is the whole purpose of the Tomes tab — must classify as tome");
        assertTrue(TomeRecipeClassifier.isTomeResult(display.result()));
    }

    @Test
    void improvedScrapTomeResult_routesToTomesTab() {
        TableCraftingDisplay display = makeDisplay(new ItemStack(FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME, 4));
        assertTrue(TomeRecipeClassifier.isTomeRecipe(display));
    }

    @Test
    void extractionTomeResult_routesToTomesTab() {
        TableCraftingDisplay display = makeDisplay(new ItemStack(FizzleEnchantingRegistry.EXTRACTION_TOME, 4));
        assertTrue(TomeRecipeClassifier.isTomeRecipe(display));
    }

    @Test
    void shelfUpgradeResult_routesToShelvesTab() {
        // A fizzle shelf upgrade (hellshelf → infused_hellshelf) belongs in the Shelves tab even
        // though its input could be any bookshelf-family block — the tab split keys off the output.
        TableCraftingDisplay display = makeDisplay(new ItemStack(FizzleShelves.INFUSED_HELLSHELF));
        assertFalse(TomeRecipeClassifier.isTomeRecipe(display),
                "A shelf upgrade must never surface in the Tomes tab — its output is a shelf block");
    }

    @Test
    void nonTomeResult_routesToShelvesTab() {
        TableCraftingDisplay display = makeDisplay(new ItemStack(Items.ENCHANTED_BOOK));
        assertFalse(TomeRecipeClassifier.isTomeRecipe(display),
                "Enchanted books are not tome items — fall through to Shelves tab");
    }

    private static TableCraftingDisplay makeDisplay(ItemStack result) {
        return new TableCraftingDisplay(
                FizzleEnchanting.id("classifier_probe"),
                Ingredient.of(Items.BOOK),
                result,
                new StatRequirements(0F, 0F, 0F),
                StatRequirements.NO_MAX,
                OptionalInt.empty(),
                0,
                false);
    }

    private static void unfreeze(Registry<?> registry) throws Exception {
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(registry, false);
    }

    private static void unfreezeIntrusive(Registry<?> registry) throws Exception {
        unfreeze(registry);
        Field intrusive = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        intrusive.setAccessible(true);
        if (intrusive.get(registry) == null) {
            intrusive.set(registry, new IdentityHashMap<>());
        }
    }
}
