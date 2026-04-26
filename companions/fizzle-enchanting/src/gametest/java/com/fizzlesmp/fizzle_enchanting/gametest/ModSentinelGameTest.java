// Tier: 3 (Fabric Gametest) — TEST-1.2-T3
package com.fizzlesmp.fizzle_enchanting.gametest;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class ModSentinelGameTest implements FabricGameTest {

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void modInitializeHasFired(GameTestHelper helper) {
        if (FizzleEnchanting.getConfig() == null) {
            helper.fail("Config is null — onInitialize has not fired");
            return;
        }
        if (FizzleEnchantingRegistry.BLOCKS.isEmpty()) {
            helper.fail("BLOCKS map is empty — registry not populated");
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void modIdConstantCorrect(GameTestHelper helper) {
        if (!"fizzle_enchanting".equals(FizzleEnchanting.MOD_ID)) {
            helper.fail("MOD_ID should be 'fizzle_enchanting', got '" + FizzleEnchanting.MOD_ID + "'");
            return;
        }
        if (FizzleEnchanting.LOGGER == null) {
            helper.fail("LOGGER should not be null");
            return;
        }
        helper.succeed();
    }
}
