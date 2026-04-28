package com.rfizzle.meridian.client.tooltip;

import com.rfizzle.meridian.Meridian;
import com.rfizzle.meridian.config.MeridianConfig;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
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
        MeridianConfig config = Meridian.getConfig();
        if (config == null) return;
        suppressStoredBookLines(stack, lines, config.display.showBookTooltips);
        // Over-leveled recoloring is now handled globally by the EnchantmentMixin injection
        // on Enchantment.getFullname(), so tooltip lines arrive pre-colored.
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

}
