package com.fizzlesmp.fizzle_enchanting.client.screen;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentMenu;
import com.fizzlesmp.fizzle_enchanting.enchanting.StatLineFormatter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;

/**
 * Stat-aware enchantment screen. Extends vanilla {@link EnchantmentScreen} and draws a single-row
 * stat summary ({@code E: 50  Q: 12  A: 5  R: 10  C: 2}) below the three enchant preview slots.
 * Visibility toggles with {@code config.enchantingTable.showLevelIndicator}.
 *
 * <p>Hooked into the vanilla {@code MenuType.ENCHANTING_TABLE} factory in T-2.5.5; this task only
 * ships the class definition and the render hook.
 */
public class FizzleEnchantmentScreen extends EnchantmentScreen {

    private static final int STAT_LINE_X = 8;
    private static final int STAT_LINE_Y = 63;
    private static final int STAT_LINE_COLOR = 0x3F3F3F;

    private final FizzleEnchantmentMenu fizzleMenu;

    public FizzleEnchantmentScreen(EnchantmentMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.fizzleMenu = menu instanceof FizzleEnchantmentMenu fm ? fm : null;
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        super.renderLabels(gfx, mouseX, mouseY);
        if (fizzleMenu == null) {
            return;
        }
        FizzleEnchantingConfig config = FizzleEnchanting.getConfig();
        if (config == null || !config.enchantingTable.showLevelIndicator) {
            return;
        }
        String line = StatLineFormatter.format(fizzleMenu.getLastStats());
        gfx.drawString(this.font, line, STAT_LINE_X, STAT_LINE_Y, STAT_LINE_COLOR, false);
    }
}
