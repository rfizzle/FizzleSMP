package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.net.CraftingResultEntry;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * T-5.3.4 — pins the fourth-row label the enchantment screen paints beneath the three enchant
 * slots. Pure-string formatter; exercises both the raw-{@code (name, xpCost)} overload the screen
 * ultimately delegates to and the {@link CraftingResultEntry} convenience the screen calls each
 * frame, using {@link DataComponents#CUSTOM_NAME} to skip lang-file resolution on the item stack.
 */
class CraftingRowFormatterTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void format_stringOverload_buildsRowFromNameAndXpCost() {
        // Library → Ender Library upgrade's display label, pinned to the DESIGN-documented
        // example. The em-dash separator (—) is the non-negotiable glyph — callers set
        // the exact text the screen paints, not a localized variant.
        assertEquals("Ender Library — 20 levels",
                CraftingRowFormatter.format("Ender Library", 20));
    }

    @Test
    void format_entryOverload_resolvesHoverName() {
        // Stand in for the ender_library block-item: use any Item and override the hover name
        // via the CUSTOM_NAME component so the formatter reads the same display string the
        // in-game render path would. ItemStack(ender_library) without lang-resolution falls
        // back to the translation key, which would leak "block.fizzle_enchanting.ender_library"
        // into the label and defeat the point of the test.
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Ender Library"));
        CraftingResultEntry entry = new CraftingResultEntry(stack, 20,
                FizzleEnchanting.id("ender_library"));

        assertEquals("Ender Library — 20 levels", CraftingRowFormatter.format(entry));
    }

    @Test
    void format_zeroCost_stillRenders() {
        // Free recipes (xp_cost omitted → default 0) must still produce a valid label so the
        // row doesn't disappear when the cost happens to be zero — the formatter is a pure
        // layout helper and doesn't gate on values.
        assertEquals("Prismatic Web — 0 levels",
                CraftingRowFormatter.format("Prismatic Web", 0));
    }

    @Test
    void format_entryOverload_preservesXpCostFromEntry() {
        // The xpCost lane travels verbatim from the payload entry into the rendered label; a
        // mismatched recipe id must not displace that value. Guards against a future refactor
        // that tries to look xp_cost up off the result stack instead of trusting the record.
        ItemStack stack = new ItemStack(Items.DIAMOND);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal("Refined Diamond"));
        CraftingResultEntry entry = new CraftingResultEntry(stack, 7,
                ResourceLocation.fromNamespaceAndPath("minecraft", "diamond"));

        assertEquals("Refined Diamond — 7 levels", CraftingRowFormatter.format(entry));
    }
}
