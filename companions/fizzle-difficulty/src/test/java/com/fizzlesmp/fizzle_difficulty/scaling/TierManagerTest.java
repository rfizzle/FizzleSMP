// Tier: 1 (pure JUnit)
package com.fizzlesmp.fizzle_difficulty.scaling;

import com.fizzlesmp.fizzle_difficulty.config.FizzleDifficultyConfig.Tiers;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pure-math tests for {@link TierManager}. No Minecraft bootstrap needed.
 */
class TierManagerTest {

    @Test
    void defaultTiers_thresholdsMatchDesign() {
        Tiers t = new Tiers();
        assertEquals(0, TierManager.getTier(0, t));
        assertEquals(0, TierManager.getTier(49, t));
        assertEquals(1, TierManager.getTier(50, t));
        assertEquals(1, TierManager.getTier(99, t));
        assertEquals(2, TierManager.getTier(100, t));
        assertEquals(2, TierManager.getTier(149, t));
        assertEquals(3, TierManager.getTier(150, t));
        assertEquals(3, TierManager.getTier(199, t));
        assertEquals(4, TierManager.getTier(200, t));
        assertEquals(4, TierManager.getTier(249, t));
        assertEquals(5, TierManager.getTier(250, t));
    }

    @Test
    void levelAboveMax_returnsTopTier() {
        Tiers t = new Tiers();
        assertEquals(5, TierManager.getTier(1000, t));
        assertEquals(5, TierManager.getTier(Integer.MAX_VALUE, t));
    }

    @Test
    void negativeLevel_returnsMinTier() {
        Tiers t = new Tiers();
        assertEquals(0, TierManager.getTier(-1, t));
        assertEquals(0, TierManager.getTier(Integer.MIN_VALUE, t));
    }

    @Test
    void nullConfig_returnsMinTier() {
        assertEquals(0, TierManager.getTier(250, null));
        assertEquals(0, TierManager.getTier(0, null));
    }

    @Test
    void customThresholds_respected() {
        Tiers t = new Tiers();
        t.tier1 = 5;
        t.tier2 = 10;
        t.tier3 = 15;
        t.tier4 = 20;
        t.tier5 = 25;
        assertEquals(0, TierManager.getTier(4, t));
        assertEquals(1, TierManager.getTier(5, t));
        assertEquals(2, TierManager.getTier(10, t));
        assertEquals(3, TierManager.getTier(17, t));
        assertEquals(4, TierManager.getTier(20, t));
        assertEquals(5, TierManager.getTier(25, t));
        assertEquals(5, TierManager.getTier(9999, t));
    }

    @Test
    void boundaryConditions_respectsInclusiveThreshold() {
        Tiers t = new Tiers();
        assertEquals(0, TierManager.getTier(t.tier1 - 1, t));
        assertEquals(1, TierManager.getTier(t.tier1, t));
        assertEquals(1, TierManager.getTier(t.tier2 - 1, t));
        assertEquals(2, TierManager.getTier(t.tier2, t));
        assertEquals(4, TierManager.getTier(t.tier5 - 1, t));
        assertEquals(5, TierManager.getTier(t.tier5, t));
    }

    @Test
    void scalingEngineComputeTier_delegates() {
        // ScalingEngine should remain backwards-compatible — callers that
        // used the old compute function should get the same answers.
        Tiers t = new Tiers();
        for (int level = 0; level <= 300; level += 25) {
            assertEquals(TierManager.getTier(level, t), ScalingEngine.computeTier(level, t),
                    "mismatch at level " + level);
        }
    }
}
