// Tier: 1 (pure JUnit)
package com.fizzlesmp.fizzle_difficulty.command;

import com.fizzlesmp.fizzle_difficulty.config.FizzleDifficultyConfig;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the pure helpers in {@link FizzleDifficultyCommand}. The
 * Brigadier-wired paths require a ServerLevel and CommandSourceStack and are
 * exercised in-game; the formatting/duration logic and modifier-based variant
 * detection live here.
 */
class FizzleDifficultyCommandTest {

    // ---- formatTicksAsDuration ----

    @Test
    void formatTicksAsDuration_subSecondReturnsTicks() {
        assertEquals("0t", FizzleDifficultyCommand.formatTicksAsDuration(0));
        assertEquals("19t", FizzleDifficultyCommand.formatTicksAsDuration(19));
    }

    @Test
    void formatTicksAsDuration_secondsAtOrAbove20() {
        assertEquals("1s", FizzleDifficultyCommand.formatTicksAsDuration(20));
        assertEquals("59s", FizzleDifficultyCommand.formatTicksAsDuration(59 * 20));
    }

    @Test
    void formatTicksAsDuration_minutes() {
        assertEquals("1m", FizzleDifficultyCommand.formatTicksAsDuration(60 * 20));
        assertEquals("5m30s", FizzleDifficultyCommand.formatTicksAsDuration(5 * 60 * 20 + 30 * 20));
        assertEquals("30m", FizzleDifficultyCommand.formatTicksAsDuration(30 * 60 * 20));
    }

    @Test
    void formatTicksAsDuration_hoursMatchDesignDefault() {
        // 1 real-time hour @ 20 tps = 72000 ticks — matches DESIGN.md's levelUpTicks.
        assertEquals("1h", FizzleDifficultyCommand.formatTicksAsDuration(72000));
        assertEquals("2h30m", FizzleDifficultyCommand.formatTicksAsDuration(2 * 72000 + 30 * 60 * 20));
    }

    // ---- onOff ----

    @Test
    void onOff_rendersBooleans() {
        assertEquals("on", FizzleDifficultyCommand.onOff(true));
        assertEquals("off", FizzleDifficultyCommand.onOff(false));
    }

    // ---- formatConfigSummary ----

    @Test
    void formatConfigSummary_includesAllAxesAndCaps() {
        FizzleDifficultyConfig cfg = new FizzleDifficultyConfig();
        List<String> lines = FizzleDifficultyCommand.formatConfigSummary(cfg);

        String joined = String.join("\n", lines);
        assertTrue(joined.contains("Max level: 250"), "shows max level");
        assertTrue(joined.contains("1h"), "level-up interval rendered as hours");
        assertTrue(joined.contains("Detection range"), "shows detection range");
        assertTrue(joined.contains("Axes:"), "has axes line");
        assertTrue(joined.contains("Distance:"), "distance details when enabled");
        assertTrue(joined.contains("Height:"), "height details when enabled");
        assertTrue(joined.contains("Stat caps:"), "lists stat caps");
        assertTrue(joined.contains("Death relief:"), "lists death relief");
        assertTrue(joined.contains("Shards:"), "lists shards");
        assertTrue(joined.contains("Bosses:"), "lists boss scaling");
        assertTrue(joined.contains("Tiers:"), "lists tier thresholds");
    }

    @Test
    void formatConfigSummary_omitsDistanceDetailsWhenDisabled() {
        FizzleDifficultyConfig cfg = new FizzleDifficultyConfig();
        cfg.distanceScaling.enabled = false;
        List<String> lines = FizzleDifficultyCommand.formatConfigSummary(cfg);
        String joined = String.join("\n", lines);
        assertTrue(joined.contains("distance=off"));
        assertFalse(joined.contains("  Distance:"),
                "indented distance-detail line should be omitted when disabled");
    }

    @Test
    void formatConfigSummary_omitsHeightDetailsWhenDisabled() {
        FizzleDifficultyConfig cfg = new FizzleDifficultyConfig();
        cfg.heightScaling.enabled = false;
        List<String> lines = FizzleDifficultyCommand.formatConfigSummary(cfg);
        String joined = String.join("\n", lines);
        assertTrue(joined.contains("height=off"));
        assertFalse(joined.contains("  Height:"));
    }

    // ---- formatPlayerInfo ----

    @Test
    void formatPlayerInfo_showsNameLevelTierAndProgress() {
        FizzleDifficultyConfig cfg = new FizzleDifficultyConfig();
        List<String> lines = FizzleDifficultyCommand.formatPlayerInfo("Alice", 5, 0, cfg);
        String joined = String.join("\n", lines);
        assertTrue(joined.contains("Alice"), "includes player name");
        assertTrue(joined.contains("Level: 5 / " + cfg.general.maxLevel), "shows level / max level");
        assertTrue(joined.contains("tier "), "shows tier");
        assertTrue(joined.contains("Progress:"), "shows progress line");
        assertTrue(joined.contains("until next level"), "shows time remaining");
    }

    @Test
    void formatPlayerInfo_rendersProgressAsDuration() {
        FizzleDifficultyConfig cfg = new FizzleDifficultyConfig();
        // Halfway through a one-hour level @ 20 tps → 30 minutes remaining.
        int halfway = cfg.general.levelUpTicks / 2;
        List<String> lines = FizzleDifficultyCommand.formatPlayerInfo("Bob", 10, halfway, cfg);
        String joined = String.join("\n", lines);
        assertTrue(joined.contains("30m"), "remaining time rendered as duration: " + joined);
    }

    @Test
    void formatPlayerInfo_atMaxLevelOmitsProgress() {
        FizzleDifficultyConfig cfg = new FizzleDifficultyConfig();
        List<String> lines = FizzleDifficultyCommand.formatPlayerInfo("Carol", cfg.general.maxLevel, 0, cfg);
        String joined = String.join("\n", lines);
        assertTrue(joined.contains("max level reached"));
        assertFalse(joined.contains("until next level"));
    }

    // ---- Variant enum ----

    @Test
    void variantLabels_matchDesignTerminology() {
        assertEquals("none", FizzleDifficultyCommand.Variant.NONE.label());
        assertEquals("big", FizzleDifficultyCommand.Variant.BIG.label());
        assertEquals("speed", FizzleDifficultyCommand.Variant.SPEED.label());
    }

    @Test
    void detectVariant_nullMobReturnsNone() {
        assertEquals(FizzleDifficultyCommand.Variant.NONE,
                FizzleDifficultyCommand.detectVariant(null));
    }

    // ---- Command structural constants ----

    @Test
    void rootCommand_isStable() {
        assertEquals("fizzledifficulty", FizzleDifficultyCommand.ROOT);
    }

    @Test
    void inspectRange_isPositive() {
        assertTrue(FizzleDifficultyCommand.INSPECT_RANGE > 0);
    }
}
