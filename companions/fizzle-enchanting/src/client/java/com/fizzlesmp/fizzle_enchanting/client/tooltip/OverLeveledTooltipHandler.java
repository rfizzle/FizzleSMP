package com.fizzlesmp.fizzle_enchanting.client.tooltip;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

/**
 * Recolors enchantment tooltip lines whose level exceeds the hardcoded vanilla cap in
 * {@link TooltipFormatter#VANILLA_CAPS}. Applies to both item-applied enchantments
 * ({@link DataComponents#ENCHANTMENTS}) and stored-book enchantments
 * ({@link DataComponents#STORED_ENCHANTMENTS}), so a Sharpness VII on either a sword or an
 * enchanted book flags consistently.
 *
 * <p>Line matching relies on {@link Enchantment#getFullname} reproducing the exact {@code Component}
 * vanilla {@code ItemEnchantments#addToTooltip} emits — string-equals against the rendered line is
 * stable in 1.21.1 because there's no resource-pack hook between the tooltip producer and the
 * list we receive here.
 */
public final class OverLeveledTooltipHandler {

    private OverLeveledTooltipHandler() {}

    public static void register() {
        ItemTooltipCallback.EVENT.register(OverLeveledTooltipHandler::onTooltip);
    }

    private static void onTooltip(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context,
                                  net.minecraft.world.item.TooltipFlag flag, List<Component> lines) {
        FizzleEnchantingConfig config = FizzleEnchanting.getConfig();
        if (config == null) return;
        // Suppression runs before recolor: once book lines are stripped there's no row left to
        // paint, and running it after would force the recolor to examine phantom lines.
        suppressStoredBookLines(stack, lines, config.display.showBookTooltips);
        TextColor color = TooltipFormatter.parseColor(config.display.overLeveledColor);
        recolor(stack, DataComponents.ENCHANTMENTS, lines, color);
        recolor(stack, DataComponents.STORED_ENCHANTMENTS, lines, color);
    }

    private static void suppressStoredBookLines(ItemStack stack, List<Component> lines, boolean showBookTooltips) {
        if (showBookTooltips) return;
        ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);
        if (stored == null || stored.isEmpty()) return;
        List<Component> bookLines = new ArrayList<>(stored.size());
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : stored.entrySet()) {
            bookLines.add(Enchantment.getFullname(entry.getKey(), entry.getIntValue()));
        }
        TooltipFormatter.suppressBookLines(lines, bookLines, false);
    }

    private static void recolor(ItemStack stack, DataComponentType<ItemEnchantments> type,
                                List<Component> lines, TextColor color) {
        ItemEnchantments enchantments = stack.get(type);
        if (enchantments == null || enchantments.isEmpty()) return;
        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            int level = entry.getIntValue();
            ResourceKey<Enchantment> key = holder.unwrapKey().orElse(null);
            if (key == null || !TooltipFormatter.isOverLeveled(key, level)) continue;
            replaceLine(lines, Enchantment.getFullname(holder, level), color);
        }
    }

    private static void replaceLine(List<Component> lines, Component target, TextColor color) {
        String expected = target.getString();
        for (int i = 0; i < lines.size(); i++) {
            if (expected.equals(lines.get(i).getString())) {
                lines.set(i, recolored(target, color));
                return;
            }
        }
    }

    private static MutableComponent recolored(Component source, TextColor color) {
        MutableComponent copy = source.copy();
        copy.setStyle(copy.getStyle().withColor(color));
        return copy;
    }
}
