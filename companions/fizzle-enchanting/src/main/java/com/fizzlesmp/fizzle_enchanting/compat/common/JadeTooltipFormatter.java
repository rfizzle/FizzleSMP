package com.fizzlesmp.fizzle_enchanting.compat.common;

import com.fizzlesmp.fizzle_enchanting.enchanting.StatCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure-text formatters for the Jade probe tooltip (T-7.4.1). Keeps the tooltip content as a
 * plain {@code List<String>} so the strings can be pinned in unit tests without a Jade runtime.
 *
 * <p>Two surfaces:
 * <ul>
 *   <li>{@link #enchantingTableLines(StatCollection)} — the five-axis stat summary shown when
 *       the probe is aimed at an enchanting table. Always emits one line per Apotheosis/Zenith
 *       axis (Eterna, Quanta, Arcana, Rectification, Clues) regardless of the stat value, so
 *       the tooltip has a stable shape the player can learn — Jade's own line renderer handles
 *       the visual compaction.</li>
 *   <li>{@link #libraryLine(String, int)} — the one-line summary shown when aimed at either
 *       library tier. Per-enchant points stay inside the library UI (DESIGN: detailed
 *       pool breakdown only in the menu, never bleeding into world tooltips).</li>
 * </ul>
 *
 * <p>Numeric formatting follows the same rules as {@link RecipeInfoFormatter#formatFloat}:
 * round-number floats drop {@code .0}, fractional values keep one decimal.
 */
public final class JadeTooltipFormatter {

    private JadeTooltipFormatter() {
    }

    /**
     * Five-line stat readout for an enchanting table, in the canonical Apotheosis ordering.
     * Eterna is rendered as {@code "Eterna: <value> / <maxEterna>"} whenever {@code maxEterna}
     * is non-zero so the player can see how much headroom remains on the primary axis; a zero
     * ceiling degrades to a plain {@code "Eterna: 0"}. Rectification is rendered as a percentage
     * (Apotheosis convention — the stat feeds a 0..1 probability filter). The Clues line always
     * renders as an integer.
     */
    public static List<String> enchantingTableLines(StatCollection stats) {
        List<String> lines = new ArrayList<>(5);
        if (stats.maxEterna() > 0F) {
            lines.add("Eterna: " + formatFloat(stats.eterna()) + " / " + formatFloat(stats.maxEterna()));
        } else {
            lines.add("Eterna: " + formatFloat(stats.eterna()));
        }
        lines.add("Quanta: " + formatFloat(stats.quanta()));
        lines.add("Arcana: " + formatFloat(stats.arcana()));
        lines.add("Rectification: " + formatPercent(stats.rectification()));
        lines.add("Clues: " + stats.clues());
        return lines;
    }

    /**
     * Singular/plural aware library summary. {@code tierName} is the display string for the
     * library tier (typically resolved from the BE's block's {@code getName().getString()} by
     * the caller so lang swaps flow through naturally). {@code storedCount} is the number of
     * enchantments with at least one stored point.
     */
    public static String libraryLine(String tierName, int storedCount) {
        int clamped = Math.max(0, storedCount);
        String noun = clamped == 1 ? "enchant" : "enchants";
        return tierName + " — " + clamped + " " + noun + " stored";
    }

    static String formatFloat(float value) {
        if (Math.abs(value - Math.round(value)) < 1e-4F) {
            return Integer.toString(Math.round(value));
        }
        return Float.toString(Math.round(value * 10F) / 10F);
    }

    static String formatPercent(float value) {
        float pct = value * 100F;
        if (Math.abs(pct - Math.round(pct)) < 1e-4F) {
            return Math.round(pct) + "%";
        }
        return (Math.round(pct * 10F) / 10F) + "%";
    }
}
