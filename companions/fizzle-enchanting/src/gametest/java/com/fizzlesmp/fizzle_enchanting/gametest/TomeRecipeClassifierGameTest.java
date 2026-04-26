// Tier: 3 (Fabric Gametest)
package com.fizzlesmp.fizzle_enchanting.gametest;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.compat.common.TableCraftingDisplay;
import com.fizzlesmp.fizzle_enchanting.compat.common.TomeRecipeClassifier;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.StatRequirements;
import com.fizzlesmp.fizzle_enchanting.shelf.FizzleShelves;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.OptionalInt;

public class TomeRecipeClassifierGameTest implements FabricGameTest {

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void scrapTomeResultRoutesToTomesTab(GameTestHelper helper) {
        TableCraftingDisplay display = makeDisplay(new ItemStack(FizzleEnchantingRegistry.SCRAP_TOME));
        if (!TomeRecipeClassifier.isTomeRecipe(display)) {
            helper.fail("Scrap Tome output must classify as tome");
            return;
        }
        if (!TomeRecipeClassifier.isTomeResult(display.result())) {
            helper.fail("Scrap Tome must be a tome result");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void improvedScrapTomeResultRoutesToTomesTab(GameTestHelper helper) {
        TableCraftingDisplay display = makeDisplay(new ItemStack(FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME, 4));
        if (!TomeRecipeClassifier.isTomeRecipe(display)) {
            helper.fail("Improved Scrap Tome must classify as tome");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void extractionTomeResultRoutesToTomesTab(GameTestHelper helper) {
        TableCraftingDisplay display = makeDisplay(new ItemStack(FizzleEnchantingRegistry.EXTRACTION_TOME, 4));
        if (!TomeRecipeClassifier.isTomeRecipe(display)) {
            helper.fail("Extraction Tome must classify as tome");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void shelfUpgradeResultRoutesToShelvesTab(GameTestHelper helper) {
        TableCraftingDisplay display = makeDisplay(new ItemStack(FizzleShelves.INFUSED_HELLSHELF));
        if (TomeRecipeClassifier.isTomeRecipe(display)) {
            helper.fail("A shelf upgrade must never surface in the Tomes tab");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void nonTomeResultRoutesToShelvesTab(GameTestHelper helper) {
        TableCraftingDisplay display = makeDisplay(new ItemStack(Items.ENCHANTED_BOOK));
        if (TomeRecipeClassifier.isTomeRecipe(display)) {
            helper.fail("Enchanted books are not tome items — should fall to Shelves tab");
            return;
        }
        helper.succeed();
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
}
