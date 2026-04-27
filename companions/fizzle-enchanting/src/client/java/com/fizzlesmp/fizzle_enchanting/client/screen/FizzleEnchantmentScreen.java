package com.fizzlesmp.fizzle_enchanting.client.screen;

import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentLogic;
import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentMenu;
import com.fizzlesmp.fizzle_enchanting.enchanting.StatCollection;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.EnchantingRecipeRegistry;
import com.fizzlesmp.fizzle_enchanting.net.CraftingResultEntry;
import com.fizzlesmp.fizzle_enchanting.net.EnchantmentClue;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Optional;

public class FizzleEnchantmentScreen extends EnchantmentScreen {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("fizzle_enchanting", "textures/gui/enchanting_table.png");

    private static final float ABSOLUTE_MAX_ETERNA = 100.0F;

    private final FizzleEnchantmentMenu fizzleMenu;

    private float eterna, lastEterna;
    private float quanta, lastQuanta;
    private float arcana, lastArcana;
    private int[] savedEnchantClue;

    public FizzleEnchantmentScreen(EnchantmentMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.fizzleMenu = menu instanceof FizzleEnchantmentMenu fm ? fm : null;
        this.imageHeight = 197;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (fizzleMenu == null) return;
        StatCollection stats = fizzleMenu.getLastStats();

        float target = stats.eterna();
        if (target != this.eterna) {
            if (target > this.eterna)
                this.eterna += Math.min(target - this.eterna, Math.max(0.16F, (target - this.eterna) * 0.1F));
            else
                this.eterna = Math.max(this.eterna - this.lastEterna * 0.075F, target);
        }
        if (target > 0) this.lastEterna = target;

        target = stats.quanta();
        if (target != this.quanta) {
            if (target > this.quanta)
                this.quanta += Math.min(target - this.quanta, Math.max(0.04F, (target - this.quanta) * 0.1F));
            else
                this.quanta = Math.max(this.quanta - this.lastQuanta * 0.075F, target);
        }
        if (target > 0) this.lastQuanta = target;

        target = stats.arcana();
        if (target != this.arcana) {
            if (target > this.arcana)
                this.arcana += Math.min(target - this.arcana, Math.max(0.04F, (target - this.arcana) * 0.1F));
            else
                this.arcana = Math.max(this.arcana - this.lastArcana * 0.075F, target);
        }
        if (target > 0) this.lastArcana = target;
    }

    private void renderBgImpl(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        int xCenter = this.leftPos;
        int yCenter = this.topPos;

        gfx.blit(TEXTURE, xCenter, yCenter, 0, 0, this.imageWidth, this.imageHeight);

        this.renderBook(gfx, xCenter, yCenter, partialTicks);

        EnchantmentNames.getInstance().initSeed(this.menu.getEnchantmentSeed());
        int lapis = this.menu.getGoldCount();

        for (int slot = 0; slot < 3; ++slot) {
            int j1 = xCenter + 60;
            int k1 = j1 + 20;
            int level = this.menu.costs[slot];
            if (level == 0) {
                gfx.blit(TEXTURE, j1, yCenter + 14 + 19 * slot, 148, 218, 108, 19);
            } else {
                String s = "" + level;
                int width = 86 - this.font.width(s);
                FormattedText randomName = EnchantmentNames.getInstance().getRandomName(this.font, width);
                int color = 6839882;
                if ((lapis < slot + 1 || this.minecraft.player.experienceLevel < level)
                        && !this.minecraft.player.getAbilities().instabuild
                        || this.menu.enchantClue[slot] == -1) {
                    gfx.blit(TEXTURE, j1, yCenter + 14 + 19 * slot, 148, 218, 108, 19);
                    gfx.blit(TEXTURE, j1 + 1, yCenter + 15 + 19 * slot, 16 * slot, 239, 16, 16);
                    gfx.drawWordWrap(this.font, randomName, k1, yCenter + 16 + 19 * slot, width, (color & 16711422) >> 1);
                    color = 4226832;
                } else {
                    int k2 = mouseX - (xCenter + 60);
                    int l2 = mouseY - (yCenter + 14 + 19 * slot);
                    if (k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19) {
                        gfx.blit(TEXTURE, j1, yCenter + 14 + 19 * slot, 148, 237, 108, 19);
                        color = 16777088;
                    } else {
                        gfx.blit(TEXTURE, j1, yCenter + 14 + 19 * slot, 148, 199, 108, 19);
                    }
                    gfx.blit(TEXTURE, j1 + 1, yCenter + 15 + 19 * slot, 16 * slot, 223, 16, 16);
                    gfx.drawWordWrap(this.font, randomName, k1, yCenter + 16 + 19 * slot, width, color);
                    color = 8453920;
                }
                gfx.drawString(this.font, s, k1 + 86 - this.font.width(s), yCenter + 16 + 19 * slot + 7, color);
            }
        }

        if (this.eterna > 0) {
            gfx.blit(TEXTURE, xCenter + 59, yCenter + 75, 0, 197,
                    (int) (this.eterna / ABSOLUTE_MAX_ETERNA * 110), 5);
        }
        if (this.quanta > 0) {
            gfx.blit(TEXTURE, xCenter + 59, yCenter + 85, 0, 202,
                    (int) (this.quanta / 100F * 110), 5);
        }
        if (this.arcana > 0) {
            gfx.blit(TEXTURE, xCenter + 59, yCenter + 95, 0, 207,
                    (int) (this.arcana / 100F * 110), 5);
        }

        renderLeftPanel(gfx);

        if (isInfoButtonVisible()) {
            int btnX = xCenter + 148;
            int btnY = yCenter + 1;
            boolean hovered = mouseX >= btnX && mouseX < btnX + 20 && mouseY >= btnY && mouseY < btnY + 12;
            int bg = hovered ? 0xFF4A6A8A : 0xFF2A3A5A;
            int border = hovered ? 0xFF8AB0DD : 0xFF5A7AAA;
            gfx.fill(btnX, btnY, btnX + 20, btnY + 12, bg);
            gfx.renderOutline(btnX, btnY, 20, 12, border);
            int textColor = hovered ? 0xFFFFFF : 0xCCDDEE;
            gfx.drawString(this.font, "i", btnX + 8, btnY + 2, textColor, false);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        gfx.drawString(this.font, this.title, 12, 5, 4210752, false);
        gfx.drawString(this.font, this.playerInventoryTitle, 7, this.imageHeight - 96 + 4, 4210752, false);

        if (fizzleMenu == null) return;

        gfx.drawString(this.font, I18n.get("gui.fizzle_enchanting.enchant.eterna"), 19, 74, 0x3DB53D, false);
        gfx.drawString(this.font, I18n.get("gui.fizzle_enchanting.enchant.quanta"), 19, 84, 0xFC5454, false);
        gfx.drawString(this.font, I18n.get("gui.fizzle_enchanting.enchant.arcana"), 19, 94, 0xA800A8, false);
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        if (savedEnchantClue != null) {
            System.arraycopy(savedEnchantClue, 0, this.menu.enchantClue, 0, 3);
        }
        renderBgImpl(gfx, partialTicks, mouseX, mouseY);
        if (savedEnchantClue != null) {
            this.menu.enchantClue[0] = -1;
            this.menu.enchantClue[1] = -1;
            this.menu.enchantClue[2] = -1;
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        savedEnchantClue = new int[]{this.menu.enchantClue[0], this.menu.enchantClue[1], this.menu.enchantClue[2]};
        this.menu.enchantClue[0] = -1;
        this.menu.enchantClue[1] = -1;
        this.menu.enchantClue[2] = -1;
        super.render(gfx, mouseX, mouseY, partialTicks);
        System.arraycopy(savedEnchantClue, 0, this.menu.enchantClue, 0, 3);
        savedEnchantClue = null;

        if (fizzleMenu == null) return;

        boolean creative = this.minecraft.player.getAbilities().instabuild;
        int lapis = this.menu.getGoldCount();

        for (int slot = 0; slot < 3; slot++) {
            int level = this.menu.costs[slot];
            if (level <= 0) continue;
            if (!isHovering(60, 14 + 19 * slot, 108, 17, mouseX, mouseY)) continue;

            List<Component> lines = Lists.newArrayList();

            if (slot == FizzleEnchantmentLogic.CRAFTING_SLOT && craftingResult().isPresent()) {
                CraftingResultEntry entry = craftingResult().get();
                lines.add(entry.result().getHoverName().copy()
                        .withStyle(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE));
                if (!creative) {
                    lines.add(Component.literal(""));
                    int cost = slot + 1;
                    if (this.minecraft.player.experienceLevel < level) {
                        lines.add(Component.translatable("container.enchant.level.requirement", level)
                                .withStyle(ChatFormatting.RED));
                    } else {
                        ChatFormatting lapisColor = lapis >= cost ? ChatFormatting.GRAY : ChatFormatting.RED;
                        lines.add(Component.translatable(cost == 1
                                ? "container.enchant.lapis.one" : "container.enchant.lapis.many", cost)
                                .withStyle(lapisColor));
                        lines.add(Component.translatable(cost == 1
                                ? "container.enchant.level.one" : "container.enchant.level.many", cost)
                                .withStyle(ChatFormatting.GRAY));
                    }
                }
            } else {
                List<EnchantmentClue> clues = fizzleMenu.getClientClues(slot);
                boolean exhausted = fizzleMenu.isClientCluesExhausted(slot);

                boolean isInfusionFailed = slot == FizzleEnchantmentLogic.CRAFTING_SLOT
                        && clues.isEmpty()
                        && isInfusionFailedForInput();

                if (isInfusionFailed) {
                    lines.add(Component.translatable("info.fizzle_enchanting.enchant.infusion")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC));
                    lines.add(Component.literal(""));
                    lines.add(Component.translatable("info.fizzle_enchanting.enchant.infusion_failed")
                            .withStyle(ChatFormatting.RED));
                } else if (!clues.isEmpty()) {
                    lines.add(Component.translatable("info.fizzle_enchanting.enchant.clues"
                            + (exhausted ? "_all" : ""))
                            .withStyle(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE));
                    Registry<Enchantment> registry = this.minecraft.level.registryAccess()
                            .registryOrThrow(Registries.ENCHANTMENT);
                    for (EnchantmentClue clue : clues) {
                        Optional<Holder.Reference<Enchantment>> holder = registry.getHolder(clue.enchantment());
                        holder.ifPresent(ref -> lines.add(Enchantment.getFullname(ref, clue.level())));
                    }
                    if (slot == FizzleEnchantmentLogic.CRAFTING_SLOT && isInfusionFailedForInput()) {
                        lines.add(Component.literal(""));
                        lines.add(Component.translatable("info.fizzle_enchanting.enchant.infusion_failed")
                                .withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
                    }
                } else {
                    lines.add(Component.translatable("info.fizzle_enchanting.enchant.no_clue")
                            .withStyle(ChatFormatting.DARK_RED, ChatFormatting.UNDERLINE));
                }

                if (this.menu.enchantClue[slot] != -1 && !creative) {
                    lines.add(Component.literal(""));
                    int cost = slot + 1;
                    if (this.minecraft.player.experienceLevel < level) {
                        lines.add(Component.translatable("container.enchant.level.requirement", level)
                                .withStyle(ChatFormatting.RED));
                    } else {
                        ChatFormatting lapisColor = lapis >= cost ? ChatFormatting.GRAY : ChatFormatting.RED;
                        lines.add(Component.translatable(cost == 1
                                ? "container.enchant.lapis.one" : "container.enchant.lapis.many", cost)
                                .withStyle(lapisColor));
                        lines.add(Component.translatable(cost == 1
                                ? "container.enchant.level.one" : "container.enchant.level.many", cost)
                                .withStyle(ChatFormatting.GRAY));
                    }
                }
            }

            gfx.renderComponentTooltip(this.font, lines, mouseX, mouseY);
            break;
        }

        StatCollection stats = fizzleMenu.getLastStats();
        if (isHovering(60, 76, 110, 5, mouseX, mouseY) && stats.eterna() > 0) {
            List<Component> tipLines = Lists.newArrayList();
            float displayMax = stats.maxEterna() > 0 ? stats.maxEterna() : ABSOLUTE_MAX_ETERNA;
            tipLines.add(Component.literal(String.format("Eterna: %.1f / %.0f", stats.eterna(), displayMax))
                    .withStyle(style -> style.withColor(0x3DB53D)));
            tipLines.add(Component.translatable("gui.fizzle_enchanting.stat.eterna.desc")
                    .withStyle(ChatFormatting.GRAY));
            tipLines.add(Component.literal(String.format("Enchant Level: %d", Math.round(stats.eterna())))
                    .withStyle(ChatFormatting.YELLOW));
            gfx.renderComponentTooltip(this.font, tipLines, mouseX, mouseY);
        } else if (isHovering(60, 86, 110, 5, mouseX, mouseY) && stats.quanta() > 0) {
            List<Component> tipLines = Lists.newArrayList();
            tipLines.add(Component.literal(String.format("Quanta: %.1f%%", stats.quanta()))
                    .withStyle(style -> style.withColor(0xFC5454)));
            tipLines.add(Component.translatable("gui.fizzle_enchanting.stat.quanta.desc")
                    .withStyle(ChatFormatting.GRAY));
            float rectFrac = stats.rectification() / 100F;
            float lo = (rectFrac - 1F) * stats.quanta();
            tipLines.add(Component.literal(String.format("Power Range: %.0f%% to +%.0f%%", lo, stats.quanta()))
                    .withStyle(ChatFormatting.YELLOW));
            if (stats.rectification() > 0) {
                tipLines.add(Component.literal(String.format("Rectification: %.1f%%", stats.rectification()))
                        .withStyle(ChatFormatting.AQUA));
            }
            gfx.renderComponentTooltip(this.font, tipLines, mouseX, mouseY);
        } else if (isHovering(60, 96, 110, 5, mouseX, mouseY) && stats.arcana() > 0) {
            List<Component> tipLines = Lists.newArrayList();
            tipLines.add(Component.literal(String.format("Arcana: %.1f%%", stats.arcana()))
                    .withStyle(style -> style.withColor(0xA800A8)));
            tipLines.add(Component.translatable("gui.fizzle_enchanting.stat.arcana.desc")
                    .withStyle(ChatFormatting.GRAY));
            int picks = countGuaranteedPicks(stats.arcana());
            tipLines.add(Component.literal(String.format("Guaranteed Picks: %d", picks))
                    .withStyle(ChatFormatting.YELLOW));
            gfx.renderComponentTooltip(this.font, tipLines, mouseX, mouseY);
        }

        if (isInfoButtonVisible()) {
            int btnX = this.leftPos + 148;
            int btnY = this.topPos + 1;
            if (mouseX >= btnX && mouseX < btnX + 20 && mouseY >= btnY && mouseY < btnY + 12) {
                gfx.renderComponentTooltip(this.font, Lists.newArrayList(
                        Component.translatable("gui.fizzle_enchanting.enchant_info.info_button")),
                        mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        if (isInfoButtonVisible()) {
            int btnX = i + 148;
            int btnY = j + 1;
            if (mouseX >= btnX && mouseX < btnX + 20 && mouseY >= btnY && mouseY < btnY + 12) {
                this.minecraft.getSoundManager().play(
                        SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                this.minecraft.setScreen(new EnchantingInfoScreen(this));
                return true;
            }
        }

        for (int k = 0; k < 3; ++k) {
            double d0 = mouseX - (double) (i + 60);
            double d1 = mouseY - (double) (j + 14 + 19 * k);
            if (d0 >= 0.0D && d1 >= 0.0D && d0 < 108.0D && d1 < 19.0D) {
                int cost = this.menu.costs[k];
                if (cost <= 0 || this.menu.enchantClue[k] == -1) continue;
                int lapis = this.menu.getGoldCount();
                boolean canAfford = this.minecraft.player.getAbilities().instabuild
                        || (lapis >= k + 1 && this.minecraft.player.experienceLevel >= cost);
                if (canAfford) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, k);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void renderLeftPanel(GuiGraphics gfx) {
        if (fizzleMenu == null) return;
        StatCollection stats = fizzleMenu.getLastStats();
        if (stats.eterna() <= 0) return;

        float rectFrac = stats.rectification() / 100F;
        float powerLo = (rectFrac - 1F) * stats.quanta();
        int picks = countGuaranteedPicks(stats.arcana());

        String rectText = String.format("Rect: %.0f%%", stats.rectification());
        String cluesText = String.format("Clues: %d", stats.clues());
        String powerText = String.format("Pwr: %.0f%% to +%.0f%%", powerLo, stats.quanta());
        String picksText = String.format("Picks: %d", picks);

        int padding = 4;
        int lineHeight = 10;
        String[] textLines = {rectText, cluesText, powerText, picksText};
        int maxTextWidth = 0;
        for (String line : textLines) {
            maxTextWidth = Math.max(maxTextWidth, this.font.width(line));
        }
        int panelWidth = maxTextWidth + padding * 2;
        int panelHeight = textLines.length * lineHeight + padding * 2 - 2;

        int panelX = this.leftPos - panelWidth - 4;
        int panelY = this.topPos + 70;

        if (panelX < 2) return;

        gfx.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xC0100010);
        gfx.renderOutline(panelX, panelY, panelWidth, panelHeight, 0xFF5A5A8A);

        int y = panelY + padding;
        gfx.drawString(this.font, rectText, panelX + padding, y, 0x55FFFF, false);
        y += lineHeight;
        gfx.drawString(this.font, cluesText, panelX + padding, y, 0x5555FF, false);
        y += lineHeight;
        gfx.drawString(this.font, powerText, panelX + padding, y, 0xFC5454, false);
        y += lineHeight;
        gfx.drawString(this.font, picksText, panelX + padding, y, 0xA800A8, false);
    }

    private static int countGuaranteedPicks(float arcana) {
        int picks = 0;
        for (int i = 0; i < 100; i += 33) {
            if (arcana >= i) picks++;
        }
        return picks;
    }

    private Optional<CraftingResultEntry> craftingResult() {
        return fizzleMenu != null ? fizzleMenu.lastCraftingResult() : Optional.empty();
    }

    private boolean isInfoButtonVisible() {
        if (fizzleMenu == null) return false;
        return this.menu.getSlot(0).hasItem()
                && (this.menu.costs[0] > 0 || this.menu.costs[1] > 0 || this.menu.costs[2] > 0);
    }

    private boolean isInfusionFailedForInput() {
        if (fizzleMenu == null || this.minecraft == null || this.minecraft.level == null) return false;
        ItemStack input = this.menu.getSlot(0).getItem();
        if (input.isEmpty()) return false;
        return craftingResult().isEmpty()
                && EnchantingRecipeRegistry.hasItemMatch(this.minecraft.level.getRecipeManager(), input);
    }
}
