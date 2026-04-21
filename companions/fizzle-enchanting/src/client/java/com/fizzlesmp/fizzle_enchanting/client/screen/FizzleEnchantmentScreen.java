package com.fizzlesmp.fizzle_enchanting.client.screen;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.fizzlesmp.fizzle_enchanting.enchanting.CraftingRowFormatter;
import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentLogic;
import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentMenu;
import com.fizzlesmp.fizzle_enchanting.enchanting.StatLineFormatter;
import com.fizzlesmp.fizzle_enchanting.net.CraftingResultEntry;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;

import java.util.List;
import java.util.Optional;

/**
 * Stat-aware enchantment screen. Extends vanilla {@link EnchantmentScreen} and draws a single-row
 * stat summary ({@code E: 50  Q: 12  A: 5  R: 10  C: 2}) below the three enchant preview slots.
 * Visibility toggles with {@code config.enchantingTable.showLevelIndicator}.
 *
 * <p>Also paints the fourth-row crafting-result preview (T-5.3.4) when
 * {@link FizzleEnchantmentMenu#lastCraftingResult()} is present — a compact text label produced by
 * {@link CraftingRowFormatter} sitting below the stat line. Clicks in that row dispatch the
 * vanilla {@code ServerboundContainerButtonClickPacket} via {@code MultiPlayerGameMode} with
 * {@code buttonId=3}; server-side routing flows through
 * {@link FizzleEnchantmentMenu#clickMenuButton} (T-5.3.3).
 */
public class FizzleEnchantmentScreen extends EnchantmentScreen {

    private static final int STAT_LINE_X = 8;
    private static final int STAT_LINE_Y = 63;
    private static final int STAT_LINE_COLOR = 0x3F3F3F;

    /**
     * Fourth-row layout, packed into the 12-pixel gap between the bottom of the enchant slots
     * (y=71) and the first player-inventory row (y=84). The vanilla "Inventory" label normally
     * occupies this band — we suppress it while the crafting row is visible so the label doesn't
     * bleed through the recipe text.
     */
    private static final int CRAFTING_ROW_X = 60;
    private static final int CRAFTING_ROW_Y = 72;
    private static final int CRAFTING_ROW_W = 108;
    private static final int CRAFTING_ROW_H = 10;
    private static final int CRAFTING_ROW_TEXT_COLOR = 0xFF404040;
    private static final int CRAFTING_ROW_TEXT_HOVER_COLOR = 0xFF80A030;

    private final FizzleEnchantmentMenu fizzleMenu;

    public FizzleEnchantmentScreen(EnchantmentMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.fizzleMenu = menu instanceof FizzleEnchantmentMenu fm ? fm : null;
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        Optional<CraftingResultEntry> crafting = craftingResult();
        int originalInventoryLabelY = this.inventoryLabelY;
        if (crafting.isPresent()) {
            // Suppress the stock "Inventory" label so super.renderLabels doesn't paint it into
            // the same band we're about to use for the crafting row. Any negative Y above the
            // titleLabelY band would also work; sentinel is clearer than a magic offset.
            this.inventoryLabelY = Integer.MIN_VALUE;
        }
        super.renderLabels(gfx, mouseX, mouseY);
        this.inventoryLabelY = originalInventoryLabelY;

        if (fizzleMenu == null) {
            return;
        }
        FizzleEnchantingConfig config = FizzleEnchanting.getConfig();
        if (config != null && config.enchantingTable.showLevelIndicator) {
            String line = StatLineFormatter.format(fizzleMenu.getLastStats());
            gfx.drawString(this.font, line, STAT_LINE_X, STAT_LINE_Y, STAT_LINE_COLOR, false);
        }
        crafting.ifPresent(entry -> renderCraftingRow(gfx, entry, mouseX, mouseY));
    }

    private void renderCraftingRow(GuiGraphics gfx, CraftingResultEntry entry, int mouseX, int mouseY) {
        int color = isHovering(CRAFTING_ROW_X, CRAFTING_ROW_Y, CRAFTING_ROW_W, CRAFTING_ROW_H, mouseX, mouseY)
                ? CRAFTING_ROW_TEXT_HOVER_COLOR
                : CRAFTING_ROW_TEXT_COLOR;
        String label = CraftingRowFormatter.format(entry);
        gfx.drawString(this.font, label, CRAFTING_ROW_X + 2, CRAFTING_ROW_Y + 1, color, false);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        super.render(gfx, mouseX, mouseY, partialTicks);
        Optional<CraftingResultEntry> crafting = craftingResult();
        if (crafting.isEmpty()) {
            return;
        }
        // The in-container hover test is screen-relative; renderLabels' isHovering uses the
        // same check — so we can reuse it directly to align tooltip + row colouring.
        if (!isHovering(CRAFTING_ROW_X, CRAFTING_ROW_Y, CRAFTING_ROW_W, CRAFTING_ROW_H, mouseX, mouseY)) {
            return;
        }
        CraftingResultEntry entry = crafting.get();
        List<Component> lines = Lists.newArrayList(
                entry.result().getHoverName(),
                Component.translatable(
                        "info.fizzle_enchanting.crafting_row.xp_cost", entry.xpCost()),
                Component.translatable(
                        "info.fizzle_enchanting.crafting_row.recipe_id", entry.recipeId().toString()));
        gfx.renderComponentTooltip(this.font, lines, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (craftingResult().isPresent()
                && isHovering(CRAFTING_ROW_X, CRAFTING_ROW_Y, CRAFTING_ROW_W, CRAFTING_ROW_H, mouseX, mouseY)) {
            Minecraft mc = this.minecraft;
            if (mc != null && mc.gameMode != null && mc.player != null) {
                mc.gameMode.handleInventoryButtonClick(this.menu.containerId,
                        FizzleEnchantmentLogic.CRAFTING_BUTTON_ID);
                mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private Optional<CraftingResultEntry> craftingResult() {
        return fizzleMenu != null ? fizzleMenu.lastCraftingResult() : Optional.empty();
    }
}
