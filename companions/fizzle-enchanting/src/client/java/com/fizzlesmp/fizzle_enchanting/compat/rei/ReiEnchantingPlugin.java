package com.fizzlesmp.fizzle_enchanting.compat.rei;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.compat.common.RecipeInfoFormatter;
import com.fizzlesmp.fizzle_enchanting.compat.common.TableCraftingDisplay;
import com.fizzlesmp.fizzle_enchanting.compat.common.TableCraftingDisplayExtractor;
import com.fizzlesmp.fizzle_enchanting.enchanting.EnchantingStatRegistry;
import com.fizzlesmp.fizzle_enchanting.enchanting.EnchantingStats;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Map;

/**
 * REI integration entry point. Mirrors the EMI plugin's two-category split so players on either
 * viewer see the same shape: a "Fizzle Enchanting — Shelves" tab for shelf-upgrade recipes and a
 * "Fizzle Enchanting — Tomes" tab for scrap/extraction variants.
 *
 * <p>Only loads when REI itself loads it (via the {@code rei_client} entry point in
 * {@code fabric.mod.json}), so the REI classes imported here are safe even when REI is absent —
 * the entry point is never resolved.
 *
 * <p>Recipes come from {@link TableCraftingDisplayExtractor}; shelf info panels come from
 * {@link EnchantingStatRegistry#blockEntries()}. Both sources are shared with the EMI plugin so a
 * recipe added to the table only has to be plumbed through one place (S-7.2.2).
 */
public final class ReiEnchantingPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<ReiEnchantingDisplay> SHELVES_ID =
            CategoryIdentifier.of(FizzleEnchanting.MOD_ID, "shelves");

    public static final CategoryIdentifier<ReiEnchantingDisplay> TOMES_ID =
            CategoryIdentifier.of(FizzleEnchanting.MOD_ID, "tomes");

    @Override
    public String getPluginProviderName() {
        return FizzleEnchanting.MOD_ID;
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new ReiEnchantingCategory(
                SHELVES_ID,
                Component.translatable("rei.fizzle_enchanting.category.shelves"),
                Items.ENCHANTING_TABLE));
        registry.add(new ReiEnchantingCategory(
                TOMES_ID,
                Component.translatable("rei.fizzle_enchanting.category.tomes"),
                FizzleEnchantingRegistry.SCRAP_TOME));

        ItemStack table = new ItemStack(Items.ENCHANTING_TABLE);
        registry.addWorkstations(SHELVES_ID, EntryIngredients.of(table));
        registry.addWorkstations(TOMES_ID, EntryIngredients.of(table));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.level == null) {
            // REI registers displays on connect; skip gracefully before a world is joined — the
            // next world load will re-register with live recipes.
            return;
        }
        for (TableCraftingDisplay display : TableCraftingDisplayExtractor.extract(client.level.getRecipeManager())) {
            registry.add(new ReiEnchantingDisplay(display, categoryFor(display)));
        }

        registerShelfInfoPanels(registry);
    }

    /**
     * Converts every block-keyed {@code enchanting_stats/*.json} entry into a
     * {@link DefaultInformationDisplay} so hovering a shelf in REI reveals its stat contribution.
     * Tag-keyed entries are skipped — their stats flow through whatever concrete block the tag
     * targets, and enumerating those would double-surface the same info.
     */
    private static void registerShelfInfoPanels(DisplayRegistry registry) {
        Map<ResourceLocation, EnchantingStats> entries = EnchantingStatRegistry.getInstance().blockEntries();
        for (Map.Entry<ResourceLocation, EnchantingStats> entry : entries.entrySet()) {
            Block block = BuiltInRegistries.BLOCK.get(entry.getKey());
            if (block == Blocks.AIR) {
                continue;
            }
            DefaultInformationDisplay info = DefaultInformationDisplay.createFromEntry(
                    EntryStacks.of(block),
                    Component.translatable(block.getDescriptionId()));
            for (String line : RecipeInfoFormatter.shelfStatLines(entry.getValue())) {
                info.line(Component.literal(line));
            }
            registry.add(info);
        }
    }

    static CategoryIdentifier<ReiEnchantingDisplay> categoryFor(TableCraftingDisplay display) {
        if (display.result().is(FizzleEnchantingRegistry.SCRAP_TOME)
                || display.result().is(FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME)
                || display.result().is(FizzleEnchantingRegistry.EXTRACTION_TOME)) {
            return TOMES_ID;
        }
        return SHELVES_ID;
    }
}
