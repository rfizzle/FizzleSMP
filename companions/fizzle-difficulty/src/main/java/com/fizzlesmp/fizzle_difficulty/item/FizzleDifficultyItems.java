package com.fizzlesmp.fizzle_difficulty.item;

import com.fizzlesmp.fizzle_difficulty.FizzleDifficulty;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

/**
 * Registry-time item declarations for Fizzle Difficulty. Must be loaded and
 * {@link #register()} invoked during {@code onInitialize} so entries land in
 * the item registry before it freezes.
 */
public final class FizzleDifficultyItems {
    public static final String SHATTER_SHARD_PATH = "shatter_shard";
    public static final String CREATIVE_TAB_PATH = "main";

    public static final ResourceLocation SHATTER_SHARD_ID =
            ResourceLocation.fromNamespaceAndPath(FizzleDifficulty.MOD_ID, SHATTER_SHARD_PATH);
    public static final ResourceLocation CREATIVE_TAB_ID =
            ResourceLocation.fromNamespaceAndPath(FizzleDifficulty.MOD_ID, CREATIVE_TAB_PATH);

    public static final Item SHATTER_SHARD = new ShatterShardItem(
            new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)
                    // Enchantment glint matches HMIOT's shatter shard visual.
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
    );

    private static boolean registered = false;

    private FizzleDifficultyItems() {}

    public static void register() {
        if (registered) return;
        registered = true;

        Registry.register(BuiltInRegistries.ITEM, SHATTER_SHARD_ID, SHATTER_SHARD);

        CreativeModeTab tab = FabricItemGroup.builder()
                .icon(() -> new ItemStack(SHATTER_SHARD))
                .title(Component.translatable("itemGroup.fizzle_difficulty.main"))
                .displayItems((params, entries) -> entries.accept(SHATTER_SHARD))
                .build();
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CREATIVE_TAB_ID, tab);
    }
}
