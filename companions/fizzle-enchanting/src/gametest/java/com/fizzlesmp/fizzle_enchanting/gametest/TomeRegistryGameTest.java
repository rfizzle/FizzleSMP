// Tier: 3 (Fabric Gametest)
package com.fizzlesmp.fizzle_enchanting.gametest;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TomeRegistryGameTest implements FabricGameTest {

    private static final String[] TOME_IDS = {"scrap_tome", "improved_scrap_tome", "extraction_tome"};

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void allTomeItemsRegistered(GameTestHelper helper) {
        for (String id : TOME_IDS) {
            ResourceLocation loc = FizzleEnchanting.id(id);
            if (!BuiltInRegistries.ITEM.containsKey(loc)) {
                helper.fail("Tome item not registered: " + loc);
                return;
            }
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void allTomesStackToSixteen(GameTestHelper helper) {
        Item[] tomes = {
                FizzleEnchantingRegistry.SCRAP_TOME,
                FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME,
                FizzleEnchantingRegistry.EXTRACTION_TOME
        };
        for (Item tome : tomes) {
            ItemStack stack = new ItemStack(tome);
            if (stack.getMaxStackSize() != 16) {
                helper.fail(tome + " maxStackSize=" + stack.getMaxStackSize() + ", expected 16");
                return;
            }
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void noTomeHasDurabilityComponent(GameTestHelper helper) {
        Item[] tomes = {
                FizzleEnchantingRegistry.SCRAP_TOME,
                FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME,
                FizzleEnchantingRegistry.EXTRACTION_TOME
        };
        for (Item tome : tomes) {
            ItemStack stack = new ItemStack(tome);
            if (stack.has(DataComponents.MAX_DAMAGE)) {
                helper.fail(tome + " should not have a durability component");
                return;
            }
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void tomesSingletonMatchRegistry(GameTestHelper helper) {
        if (BuiltInRegistries.ITEM.get(FizzleEnchanting.id("scrap_tome")) != FizzleEnchantingRegistry.SCRAP_TOME) {
            helper.fail("SCRAP_TOME singleton doesn't match registry");
            return;
        }
        if (BuiltInRegistries.ITEM.get(FizzleEnchanting.id("improved_scrap_tome")) != FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME) {
            helper.fail("IMPROVED_SCRAP_TOME singleton doesn't match registry");
            return;
        }
        if (BuiltInRegistries.ITEM.get(FizzleEnchanting.id("extraction_tome")) != FizzleEnchantingRegistry.EXTRACTION_TOME) {
            helper.fail("EXTRACTION_TOME singleton doesn't match registry");
            return;
        }
        helper.succeed();
    }
}
