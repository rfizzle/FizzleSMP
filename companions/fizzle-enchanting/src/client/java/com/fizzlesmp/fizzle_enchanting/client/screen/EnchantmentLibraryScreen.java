package com.fizzlesmp.fizzle_enchanting.client.screen;

import com.fizzlesmp.fizzle_enchanting.library.EnchantmentLibraryBlockEntity;
import com.fizzlesmp.fizzle_enchanting.library.EnchantmentLibraryMenu;
import com.fizzlesmp.fizzle_enchanting.library.LibraryRowFormatter;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Client screen for the Basic and Ender enchantment libraries. Paints a scrollable list of every
 * enchantment in the BE's pool with {@code points > 0}, alongside the per-enchant max-level badge
 * and the running point total formatted by {@link LibraryRowFormatter}. Clicking a row sends a
 * bit-packed click ID per DESIGN ({@code id = (shift << 31) | enchantIndex}) which the server-side
 * menu decodes in {@code clickMenuButton}.
 *
 * <p>Scrolling follows the vanilla {@code LoomScreen} / {@code StonecutterScreen} pattern — there
 * is no shared {@code ScrollableContainer} class in 1.21.1, but the convention is consistent: a
 * float {@code scrollOffs} drives a {@code startIndex} into the row list, with mouse wheel and
 * scrollbar drag both writing through the same clamp. The MVP uses a flat solid-color background
 * (no custom GUI texture) so the screen is functional out of the box; texture polish is deferred.
 *
 * <p>Row state is rebuilt every frame from {@link EnchantmentLibraryMenu#getTile()} so the live
 * sync packet (DESIGN: full-map resend on every mutation) feeds the screen without an explicit
 * listener. The menu also exposes {@link EnchantmentLibraryMenu#setNotifier} (wired by
 * {@link EnchantmentLibraryMenu#onChanged()}) — currently unused since the per-frame rebuild
 * already covers the refresh need; the hook stays available for an event-driven optimization.
 */
public class EnchantmentLibraryScreen extends AbstractContainerScreen<EnchantmentLibraryMenu> {

    public static final int MAX_VISIBLE = 5;
    public static final int ROW_HEIGHT = 18;

    private static final int LIST_X = 8;
    private static final int LIST_Y = 18;
    private static final int LIST_W = 116;
    private static final int LIST_H = MAX_VISIBLE * ROW_HEIGHT;

    private static final int SCROLLBAR_X = 128;
    private static final int SCROLLBAR_Y = LIST_Y;
    private static final int SCROLLBAR_W = 6;
    private static final int SCROLLBAR_H = LIST_H;
    private static final int SCROLLBAR_THUMB_H = 12;

    private static final int BG_PANEL_COLOR = 0xFFC6C6C6;
    private static final int LIST_FRAME_COLOR = 0xFF373737;
    private static final int ROW_BG = 0xFF4F4F4F;
    private static final int ROW_BG_HOVER = 0xFF606060;
    private static final int ROW_TEXT = 0xFFFFFFFF;
    private static final int SCROLL_THUMB_ACTIVE = 0xFFA0A0A0;
    private static final int SCROLL_THUMB_INACTIVE = 0xFF555555;

    private float scrollOffs;
    private boolean scrolling;
    private int startIndex;
    private final List<Row> rows = new ArrayList<>();

    public EnchantmentLibraryScreen(EnchantmentLibraryMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 230;
        this.inventoryLabelY = this.imageHeight - 94;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();
        rebuildRows();
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        rebuildRows();
        renderBackground(gfx, mouseX, mouseY, partialTicks);
        super.render(gfx, mouseX, mouseY, partialTicks);
        renderTooltip(gfx, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        gfx.fill(this.leftPos, this.topPos,
                this.leftPos + this.imageWidth, this.topPos + this.imageHeight,
                BG_PANEL_COLOR);
        gfx.fill(this.leftPos + LIST_X - 1, this.topPos + LIST_Y - 1,
                this.leftPos + LIST_X + LIST_W + 1, this.topPos + LIST_Y + LIST_H + 1,
                LIST_FRAME_COLOR);
        for (int i = 0; i < MAX_VISIBLE && (this.startIndex + i) < this.rows.size(); i++) {
            Row row = this.rows.get(this.startIndex + i);
            int rowX = this.leftPos + LIST_X;
            int rowY = this.topPos + LIST_Y + i * ROW_HEIGHT;
            boolean hovered = isHovering(LIST_X, LIST_Y + i * ROW_HEIGHT, LIST_W, ROW_HEIGHT, mouseX, mouseY);
            gfx.fill(rowX, rowY, rowX + LIST_W, rowY + ROW_HEIGHT, hovered ? ROW_BG_HOVER : ROW_BG);
            String text = LibraryRowFormatter.format(row.name(), row.maxLevel(), row.points());
            gfx.drawString(this.font, text, rowX + 4, rowY + 5, ROW_TEXT, false);
        }
        renderScrollbar(gfx);
        renderIoSlotFrames(gfx);
    }

    private void renderScrollbar(GuiGraphics gfx) {
        gfx.fill(this.leftPos + SCROLLBAR_X, this.topPos + SCROLLBAR_Y,
                this.leftPos + SCROLLBAR_X + SCROLLBAR_W, this.topPos + SCROLLBAR_Y + SCROLLBAR_H,
                LIST_FRAME_COLOR);
        int thumbY = this.topPos + SCROLLBAR_Y
                + (int) ((SCROLLBAR_H - SCROLLBAR_THUMB_H) * Mth.clamp(this.scrollOffs, 0F, 1F));
        gfx.fill(this.leftPos + SCROLLBAR_X, thumbY,
                this.leftPos + SCROLLBAR_X + SCROLLBAR_W, thumbY + SCROLLBAR_THUMB_H,
                isScrollBarActive() ? SCROLL_THUMB_ACTIVE : SCROLL_THUMB_INACTIVE);
    }

    private void renderIoSlotFrames(GuiGraphics gfx) {
        // Visual frames around the three IO slots so they read as deposit / extract / scratch
        // even without a custom GUI texture in MVP.
        drawSlotFrame(gfx, 142, 18);
        drawSlotFrame(gfx, 142, 77);
        drawSlotFrame(gfx, 142, 106);
        // Inventory + hotbar frame
        gfx.fill(this.leftPos + 7, this.topPos + 147,
                this.leftPos + 7 + 162, this.topPos + 147 + 58,
                LIST_FRAME_COLOR);
        gfx.fill(this.leftPos + 7, this.topPos + 205,
                this.leftPos + 7 + 162, this.topPos + 205 + 18,
                LIST_FRAME_COLOR);
    }

    private void drawSlotFrame(GuiGraphics gfx, int x, int y) {
        gfx.fill(this.leftPos + x - 1, this.topPos + y - 1,
                this.leftPos + x + 17, this.topPos + y + 17,
                LIST_FRAME_COLOR);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.scrolling = false;
        if (this.isHovering(SCROLLBAR_X, SCROLLBAR_Y, SCROLLBAR_W, SCROLLBAR_H, mouseX, mouseY)) {
            this.scrolling = isScrollBarActive();
            return true;
        }
        for (int i = 0; i < MAX_VISIBLE && (this.startIndex + i) < this.rows.size(); i++) {
            if (isHovering(LIST_X, LIST_Y + i * ROW_HEIGHT, LIST_W, ROW_HEIGHT, mouseX, mouseY)) {
                Row row = this.rows.get(this.startIndex + i);
                int id = row.registryIndex() & EnchantmentLibraryMenu.INDEX_MASK;
                if (Screen.hasShiftDown()) id |= EnchantmentLibraryMenu.SHIFT_BIT;
                if (this.minecraft != null && this.minecraft.gameMode != null) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
                }
                if (this.minecraft != null) {
                    this.minecraft.getSoundManager().play(
                            SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (this.scrolling && isScrollBarActive()) {
            int barTop = this.topPos + SCROLLBAR_Y;
            int barBot = barTop + SCROLLBAR_H;
            this.scrollOffs = ((float) mouseY - barTop - SCROLLBAR_THUMB_H / 2F)
                    / (barBot - barTop - SCROLLBAR_THUMB_H);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0F, 1F);
            this.startIndex = (int) (this.scrollOffs * getOffscreenRows() + 0.5D);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dx, double dy) {
        if (isScrollBarActive()) {
            int offscreen = getOffscreenRows();
            this.scrollOffs = (float) (this.scrollOffs - dy / offscreen);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0F, 1F);
            this.startIndex = (int) (this.scrollOffs * offscreen + 0.5D);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, dx, dy);
    }

    private boolean isScrollBarActive() {
        return this.rows.size() > MAX_VISIBLE;
    }

    private int getOffscreenRows() {
        return Math.max(1, this.rows.size() - MAX_VISIBLE);
    }

    private void rebuildRows() {
        this.rows.clear();
        EnchantmentLibraryBlockEntity tile = this.menu.getTile();
        if (tile == null || this.minecraft == null || this.minecraft.player == null) return;
        Registry<Enchantment> registry = this.minecraft.player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        for (Object2IntMap.Entry<ResourceKey<Enchantment>> entry
                : tile.getPoints().object2IntEntrySet()) {
            int points = entry.getIntValue();
            if (points <= 0) continue;
            ResourceKey<Enchantment> key = entry.getKey();
            Enchantment ench = registry.get(key);
            if (ench == null) continue;
            int maxLvl = tile.getMaxLevels().getInt(key);
            int idx = registry.getId(ench);
            this.rows.add(new Row(key, ench.description(), maxLvl, points, idx));
        }
        this.rows.sort(Comparator.comparing(r -> r.name().getString()));
        if (!isScrollBarActive()) {
            this.scrollOffs = 0F;
            this.startIndex = 0;
        } else if (this.startIndex > getOffscreenRows()) {
            this.startIndex = getOffscreenRows();
            this.scrollOffs = 1F;
        }
    }

    private record Row(ResourceKey<Enchantment> key, Component name,
                       int maxLevel, int points, int registryIndex) {}
}
