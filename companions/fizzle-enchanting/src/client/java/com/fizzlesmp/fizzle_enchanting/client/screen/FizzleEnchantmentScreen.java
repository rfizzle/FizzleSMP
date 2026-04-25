package com.fizzlesmp.fizzle_enchanting.client.screen;

import com.fizzlesmp.fizzle_enchanting.enchanting.CraftingRowFormatter;
import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentLogic;
import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentMenu;
import com.fizzlesmp.fizzle_enchanting.enchanting.StatCollection;
import com.fizzlesmp.fizzle_enchanting.net.CraftingResultEntry;
import com.fizzlesmp.fizzle_enchanting.net.EnchantmentClue;
import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;
import java.util.Optional;

public class FizzleEnchantmentScreen extends EnchantmentScreen {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("fizzle_enchanting", "textures/gui/enchanting_table.png");

    private static final float ABSOLUTE_MAX_ETERNA = 100.0F;

    private static final int CRAFTING_ROW_X = 60;
    private static final int CRAFTING_ROW_Y = 103;
    private static final int CRAFTING_ROW_W = 108;
    private static final int CRAFTING_ROW_H = 10;
    private static final int CRAFTING_ROW_TEXT_COLOR = 0xFF404040;
    private static final int CRAFTING_ROW_TEXT_HOVER_COLOR = 0xFF80A030;

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
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        gfx.drawString(this.font, this.title, 12, 5, 4210752, false);
        gfx.drawString(this.font, this.playerInventoryTitle, 7, this.imageHeight - 96 + 4, 4210752, false);

        if (fizzleMenu == null) return;

        gfx.drawString(this.font, I18n.get("gui.fizzle_enchanting.enchant.eterna"), 19, 74, 0x3DB53D, false);
        gfx.drawString(this.font, I18n.get("gui.fizzle_enchanting.enchant.quanta"), 19, 84, 0xFC5454, false);
        gfx.drawString(this.font, I18n.get("gui.fizzle_enchanting.enchant.arcana"), 19, 94, 0xA800A8, false);

        craftingResult().ifPresent(entry -> renderCraftingRow(gfx, entry, mouseX, mouseY));
    }

    private void renderCraftingRow(GuiGraphics gfx, CraftingResultEntry entry, int mouseX, int mouseY) {
        int color = isHovering(CRAFTING_ROW_X, CRAFTING_ROW_Y, CRAFTING_ROW_W, CRAFTING_ROW_H, mouseX, mouseY)
                ? CRAFTING_ROW_TEXT_HOVER_COLOR
                : CRAFTING_ROW_TEXT_COLOR;
        String label = CraftingRowFormatter.format(entry);
        gfx.drawString(this.font, label, CRAFTING_ROW_X + 2, CRAFTING_ROW_Y + 1, color, false);
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
            List<EnchantmentClue> clues = fizzleMenu.getClientClues(slot);
            boolean exhausted = fizzleMenu.isClientCluesExhausted(slot);

            if (!clues.isEmpty()) {
                lines.add(Component.translatable("info.fizzle_enchanting.enchant.clues"
                        + (exhausted ? "_all" : ""))
                        .withStyle(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE));
                Registry<Enchantment> registry = this.minecraft.level.registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT);
                for (EnchantmentClue clue : clues) {
                    Optional<Holder.Reference<Enchantment>> holder = registry.getHolder(clue.enchantment());
                    holder.ifPresent(ref -> lines.add(Enchantment.getFullname(ref, clue.level())));
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

            gfx.renderComponentTooltip(this.font, lines, mouseX, mouseY);
            break;
        }

        StatCollection stats = fizzleMenu.getLastStats();
        if (isHovering(60, 76, 110, 5, mouseX, mouseY) && stats.eterna() > 0) {
            gfx.renderComponentTooltip(this.font, Lists.newArrayList(
                    Component.literal(String.format("Eterna: %.1f / %.0f", stats.eterna(), ABSOLUTE_MAX_ETERNA))),
                    mouseX, mouseY);
        } else if (isHovering(60, 86, 110, 5, mouseX, mouseY) && stats.quanta() > 0) {
            gfx.renderComponentTooltip(this.font, Lists.newArrayList(
                    Component.literal(String.format("Quanta: %.1f%%", stats.quanta()))),
                    mouseX, mouseY);
        } else if (isHovering(60, 96, 110, 5, mouseX, mouseY) && stats.arcana() > 0) {
            gfx.renderComponentTooltip(this.font, Lists.newArrayList(
                    Component.literal(String.format("Arcana: %.1f%%", stats.arcana()))),
                    mouseX, mouseY);
        }

        Optional<CraftingResultEntry> crafting = craftingResult();
        if (crafting.isPresent()
                && isHovering(CRAFTING_ROW_X, CRAFTING_ROW_Y, CRAFTING_ROW_W, CRAFTING_ROW_H, mouseX, mouseY)) {
            CraftingResultEntry entry = crafting.get();
            gfx.renderComponentTooltip(this.font, Lists.newArrayList(
                    entry.result().getHoverName(),
                    Component.translatable("info.fizzle_enchanting.crafting_row.xp_cost", entry.xpCost()),
                    Component.translatable("info.fizzle_enchanting.crafting_row.recipe_id", entry.recipeId().toString())),
                    mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Crafting row click
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

        // Enchantment option button clicks — validate against synced data slots directly,
        // bypassing clickMenuButton which checks server-only slotPicks and always fails client-side.
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
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

    private Optional<CraftingResultEntry> craftingResult() {
        return fizzleMenu != null ? fizzleMenu.lastCraftingResult() : Optional.empty();
    }
}
