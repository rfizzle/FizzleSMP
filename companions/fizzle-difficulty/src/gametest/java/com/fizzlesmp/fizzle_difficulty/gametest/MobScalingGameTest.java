// Tier: 3 (Fabric Gametest)
package com.fizzlesmp.fizzle_difficulty.gametest;

import com.fizzlesmp.fizzle_difficulty.event.MobScalingHandler;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;

/**
 * End-to-end coverage of the spawn-tag dedup contract enforced by
 * {@link MobScalingHandler#onEntityLoad}. Pure-JUnit tests verify the toggle
 * key / exclusion helpers; the Tier 2 bridge test verifies the modifier math
 * against a real AttributeMap; this test closes the loop by spawning a real
 * Zombie in a real ServerLevel and asserting the handler tags it.
 *
 * <p>Structure template ({@code fizzle_difficulty:empty_3x3}) is a 3x3 stone
 * floor under 3 blocks of air — large enough for zombie AABB, small enough
 * that test setup/teardown is cheap.
 */
public class MobScalingGameTest implements FabricGameTest {

    /**
     * Spawning a zombie fires {@code ServerEntityEvents.ENTITY_LOAD} which is
     * what {@link MobScalingHandler#register()} hooks — so the handler is
     * invoked exactly the way it would be for a naturally spawning mob. The
     * PROCESSED_TAG is the canonical "this mob has been through scaling"
     * signal used to prevent re-processing on chunk reload, so asserting it
     * lands proves the whole spawn-hook pipeline wired up correctly.
     */
    @GameTest(template = "fizzle_difficulty:empty_3x3")
    public void zombieSpawn_getsProcessedTag(GameTestHelper helper) {
        Zombie zombie = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 2, 1));
        helper.succeedWhen(() -> helper.assertTrue(
                zombie.getTags().contains(MobScalingHandler.PROCESSED_TAG),
                "zombie missing " + MobScalingHandler.PROCESSED_TAG + " tag after spawn"));
    }
}
