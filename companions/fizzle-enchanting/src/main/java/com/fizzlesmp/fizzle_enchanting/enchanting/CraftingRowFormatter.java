package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.net.CraftingResultEntry;

/**
 * Pure-text formatter for the fourth-row crafting-result label painted by
 * {@code FizzleEnchantmentScreen} (T-5.3.4). The row shows the recipe's result item alongside its
 * XP cost — {@code "Ender Library — 20 levels"} — and the label is the single source of truth for
 * that text so unit tests can pin it without a client classpath.
 *
 * <p>Two overloads mirror {@code LibraryRowFormatter}: the raw {@code (name, xpCost)} form runs in
 * a bare JVM (test path), and the {@link CraftingResultEntry} overload resolves the item name
 * through the vanilla hover-name pipeline for the in-game render path.
 */
public final class CraftingRowFormatter {

    private CraftingRowFormatter() {
    }

    /**
     * Build the row label from a pre-resolved item name plus the recipe's {@code xp_cost}. The
     * em-dash separator pins the same glyph Zenith's HUD uses for stat lines so the row reads as
     * a single cohesive phrase rather than two bolted-on fragments.
     */
    public static String format(String itemName, int xpCost) {
        return itemName + " — " + xpCost + " levels";
    }

    /**
     * Resolve the hover name off the payload's result stack and funnel through the primary format
     * template. Called from the screen, where the {@code ItemStack}'s hover name has already been
     * translated against the active lang.
     */
    public static String format(CraftingResultEntry entry) {
        return format(entry.result().getHoverName().getString(), entry.xpCost());
    }
}
