// Tier: 3 (Fabric Gametest)
package com.fizzlesmp.fizzle_enchanting.gametest;

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

public class PlaceholderGameTest implements FabricGameTest {

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void gametestHarnessBoots(GameTestHelper helper) {
        helper.succeed();
    }
}
