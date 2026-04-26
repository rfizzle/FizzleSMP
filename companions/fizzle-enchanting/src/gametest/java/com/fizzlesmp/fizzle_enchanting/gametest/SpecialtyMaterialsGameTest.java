// Tier: 3 (Fabric Gametest)
package com.fizzlesmp.fizzle_enchanting.gametest;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.event.WardenLootHandler;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;

public class SpecialtyMaterialsGameTest implements FabricGameTest {

    // --- TEST-5.4-T3a: InfusedBreathItem registered ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void infusedBreathRegistered(GameTestHelper helper) {
        ResourceLocation loc = FizzleEnchanting.id("infused_breath");
        if (!BuiltInRegistries.ITEM.containsKey(loc)) {
            helper.fail("InfusedBreathItem not registered: " + loc);
            return;
        }
        if (BuiltInRegistries.ITEM.get(loc) != FizzleEnchantingRegistry.INFUSED_BREATH) {
            helper.fail("Registry entry doesn't match singleton");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void infusedBreathIsEpicRarity(GameTestHelper helper) {
        ItemStack stack = new ItemStack(FizzleEnchantingRegistry.INFUSED_BREATH);
        if (stack.getRarity() != Rarity.EPIC) {
            helper.fail("Infused Breath should be EPIC rarity, got " + stack.getRarity());
            return;
        }
        helper.succeed();
    }

    // --- TEST-5.4-T3b: WardenTendrilItem registered ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void wardenTendrilRegistered(GameTestHelper helper) {
        ResourceLocation loc = FizzleEnchanting.id("warden_tendril");
        if (!BuiltInRegistries.ITEM.containsKey(loc)) {
            helper.fail("WardenTendrilItem not registered: " + loc);
            return;
        }
        if (BuiltInRegistries.ITEM.get(loc) != FizzleEnchantingRegistry.WARDEN_TENDRIL) {
            helper.fail("Registry entry doesn't match singleton");
            return;
        }
        helper.succeed();
    }

    // --- TEST-5.4-T3c: WardenLootHandler modifies warden loot table ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void wardenPoolConditionTypeRegistered(GameTestHelper helper) {
        ResourceLocation loc = FizzleEnchanting.id("warden_pool");
        if (!BuiltInRegistries.LOOT_CONDITION_TYPE.containsKey(loc)) {
            helper.fail("WardenPoolCondition type not registered: " + loc);
            return;
        }
        if (BuiltInRegistries.LOOT_CONDITION_TYPE.get(loc) != FizzleEnchantingRegistry.WARDEN_POOL_CONDITION) {
            helper.fail("LootItemConditionType doesn't match singleton");
            return;
        }
        helper.succeed();
    }
}
