package com.fizzlesmp.fizzle_enchanting.client.tooltip;

import com.fizzlesmp.fizzle_enchanting.enchanting.EnchantingStatRegistry;
import com.fizzlesmp.fizzle_enchanting.enchanting.EnchantingStats;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class ShelfStatTooltipHandler {

    private ShelfStatTooltipHandler() {}

    public static void register() {
        ItemTooltipCallback.EVENT.register(ShelfStatTooltipHandler::onTooltip);
    }

    private static void onTooltip(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context,
                                  net.minecraft.world.item.TooltipFlag flag, List<Component> lines) {
        if (!(stack.getItem() instanceof BlockItem blockItem)) return;

        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock());
        EnchantingStats stats = EnchantingStatRegistry.getInstance().blockEntries().get(blockId);
        if (stats == null || stats.equals(EnchantingStats.ZERO)) return;

        lines.add(Component.empty());
        if (stats.eterna() != 0) {
            lines.add(Component.translatable("info.fizzle_enchanting.shelf.eterna",
                    String.format("%.1f", stats.eterna())).withStyle(ChatFormatting.GREEN));
        }
        if (stats.maxEterna() > 0) {
            lines.add(Component.translatable("info.fizzle_enchanting.shelf.max_eterna",
                    String.format("%.1f", stats.maxEterna())).withStyle(ChatFormatting.GREEN));
        }
        if (stats.quanta() != 0) {
            lines.add(Component.translatable("info.fizzle_enchanting.shelf.quanta",
                    String.format("%.1f%%", stats.quanta())).withStyle(ChatFormatting.RED));
        }
        if (stats.arcana() != 0) {
            lines.add(Component.translatable("info.fizzle_enchanting.shelf.arcana",
                    String.format("%.1f%%", stats.arcana())).withStyle(ChatFormatting.DARK_PURPLE));
        }
        if (stats.rectification() != 0) {
            lines.add(Component.translatable("info.fizzle_enchanting.shelf.rectification",
                    String.format("%.1f%%", stats.rectification())).withStyle(ChatFormatting.YELLOW));
        }
        if (stats.clues() != 0) {
            lines.add(Component.translatable("info.fizzle_enchanting.shelf.clues",
                    stats.clues()).withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}
