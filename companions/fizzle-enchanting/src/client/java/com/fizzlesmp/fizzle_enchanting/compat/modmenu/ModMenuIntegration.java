package com.fizzlesmp.fizzle_enchanting.compat.modmenu;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            FizzleEnchantingConfig config = FizzleEnchanting.getConfig();
            if (config == null) config = new FizzleEnchantingConfig();
            FizzleEnchantingConfig current = config;

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.translatable("config.fizzle_enchanting.title"));

            ConfigEntryBuilder entry = builder.entryBuilder();

            // Enchanting Table
            ConfigCategory tableCategory = builder.getOrCreateCategory(
                    Component.translatable("config.fizzle_enchanting.category.enchanting_table"));
            tableCategory.addEntry(entry.startBooleanToggle(
                            Component.translatable("config.fizzle_enchanting.allow_treasure_without_shelf"),
                            current.enchantingTable.allowTreasureWithoutShelf)
                    .setDefaultValue(false)
                    .setSaveConsumer(v -> current.enchantingTable.allowTreasureWithoutShelf = v)
                    .build());
            tableCategory.addEntry(entry.startIntSlider(
                            Component.translatable("config.fizzle_enchanting.max_eterna"),
                            current.enchantingTable.maxEterna, 1, 100)
                    .setDefaultValue(100)
                    .setSaveConsumer(v -> current.enchantingTable.maxEterna = v)
                    .build());
            tableCategory.addEntry(entry.startBooleanToggle(
                            Component.translatable("config.fizzle_enchanting.show_level_indicator"),
                            current.enchantingTable.showLevelIndicator)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> current.enchantingTable.showLevelIndicator = v)
                    .build());
            tableCategory.addEntry(entry.startIntSlider(
                            Component.translatable("config.fizzle_enchanting.global_min_enchantability"),
                            current.enchantingTable.globalMinEnchantability, 0, 100)
                    .setDefaultValue(1)
                    .setSaveConsumer(v -> current.enchantingTable.globalMinEnchantability = v)
                    .build());

            // Shelves
            ConfigCategory shelvesCategory = builder.getOrCreateCategory(
                    Component.translatable("config.fizzle_enchanting.category.shelves"));
            shelvesCategory.addEntry(entry.startDoubleField(
                            Component.translatable("config.fizzle_enchanting.sculk_shrieker_chance"),
                            current.shelves.sculkShelfShriekerChance)
                    .setDefaultValue(0.02)
                    .setMin(0.0).setMax(1.0)
                    .setSaveConsumer(v -> current.shelves.sculkShelfShriekerChance = v)
                    .build());
            shelvesCategory.addEntry(entry.startDoubleField(
                            Component.translatable("config.fizzle_enchanting.sculk_particle_chance"),
                            current.shelves.sculkParticleChance)
                    .setDefaultValue(0.05)
                    .setMin(0.0).setMax(1.0)
                    .setSaveConsumer(v -> current.shelves.sculkParticleChance = v)
                    .build());

            // Anvil
            ConfigCategory anvilCategory = builder.getOrCreateCategory(
                    Component.translatable("config.fizzle_enchanting.category.anvil"));
            anvilCategory.addEntry(entry.startBooleanToggle(
                            Component.translatable("config.fizzle_enchanting.prismatic_web_removes_curses"),
                            current.anvil.prismaticWebRemovesCurses)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> current.anvil.prismaticWebRemovesCurses = v)
                    .build());
            anvilCategory.addEntry(entry.startIntField(
                            Component.translatable("config.fizzle_enchanting.prismatic_web_level_cost"),
                            current.anvil.prismaticWebLevelCost)
                    .setDefaultValue(30)
                    .setMin(0)
                    .setSaveConsumer(v -> current.anvil.prismaticWebLevelCost = v)
                    .build());
            anvilCategory.addEntry(entry.startBooleanToggle(
                            Component.translatable("config.fizzle_enchanting.iron_block_repairs_anvil"),
                            current.anvil.ironBlockRepairsAnvil)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> current.anvil.ironBlockRepairsAnvil = v)
                    .build());

            // Library
            ConfigCategory libraryCategory = builder.getOrCreateCategory(
                    Component.translatable("config.fizzle_enchanting.category.library"));
            libraryCategory.addEntry(entry.startIntField(
                            Component.translatable("config.fizzle_enchanting.io_rate_limit_ticks"),
                            current.library.ioRateLimitTicks)
                    .setDefaultValue(0)
                    .setMin(0)
                    .setSaveConsumer(v -> current.library.ioRateLimitTicks = v)
                    .build());

            // Tomes
            ConfigCategory tomesCategory = builder.getOrCreateCategory(
                    Component.translatable("config.fizzle_enchanting.category.tomes"));
            tomesCategory.addEntry(entry.startIntField(
                            Component.translatable("config.fizzle_enchanting.scrap_tome_xp_cost"),
                            current.tomes.scrapTomeXpCost)
                    .setDefaultValue(3).setMin(0)
                    .setSaveConsumer(v -> current.tomes.scrapTomeXpCost = v)
                    .build());
            tomesCategory.addEntry(entry.startIntField(
                            Component.translatable("config.fizzle_enchanting.improved_scrap_tome_xp_cost"),
                            current.tomes.improvedScrapTomeXpCost)
                    .setDefaultValue(5).setMin(0)
                    .setSaveConsumer(v -> current.tomes.improvedScrapTomeXpCost = v)
                    .build());
            tomesCategory.addEntry(entry.startIntField(
                            Component.translatable("config.fizzle_enchanting.extraction_tome_xp_cost"),
                            current.tomes.extractionTomeXpCost)
                    .setDefaultValue(10).setMin(0)
                    .setSaveConsumer(v -> current.tomes.extractionTomeXpCost = v)
                    .build());
            tomesCategory.addEntry(entry.startIntField(
                            Component.translatable("config.fizzle_enchanting.extraction_tome_item_damage"),
                            current.tomes.extractionTomeItemDamage)
                    .setDefaultValue(50).setMin(0)
                    .setSaveConsumer(v -> current.tomes.extractionTomeItemDamage = v)
                    .build());
            tomesCategory.addEntry(entry.startDoubleField(
                            Component.translatable("config.fizzle_enchanting.extraction_tome_repair_percent"),
                            current.tomes.extractionTomeRepairPercent)
                    .setDefaultValue(0.25).setMin(0.0).setMax(1.0)
                    .setSaveConsumer(v -> current.tomes.extractionTomeRepairPercent = v)
                    .build());

            // Warden
            ConfigCategory wardenCategory = builder.getOrCreateCategory(
                    Component.translatable("config.fizzle_enchanting.category.warden"));
            wardenCategory.addEntry(entry.startDoubleField(
                            Component.translatable("config.fizzle_enchanting.tendril_drop_chance"),
                            current.warden.tendrilDropChance)
                    .setDefaultValue(1.0).setMin(0.0).setMax(1.0)
                    .setSaveConsumer(v -> current.warden.tendrilDropChance = v)
                    .build());
            wardenCategory.addEntry(entry.startDoubleField(
                            Component.translatable("config.fizzle_enchanting.tendril_looting_bonus"),
                            current.warden.tendrilLootingBonus)
                    .setDefaultValue(0.10).setMin(0.0).setMax(1.0)
                    .setSaveConsumer(v -> current.warden.tendrilLootingBonus = v)
                    .build());

            // Display
            ConfigCategory displayCategory = builder.getOrCreateCategory(
                    Component.translatable("config.fizzle_enchanting.category.display"));
            displayCategory.addEntry(entry.startBooleanToggle(
                            Component.translatable("config.fizzle_enchanting.show_book_tooltips"),
                            current.display.showBookTooltips)
                    .setDefaultValue(true)
                    .setSaveConsumer(v -> current.display.showBookTooltips = v)
                    .build());
            displayCategory.addEntry(entry.startStrField(
                            Component.translatable("config.fizzle_enchanting.over_leveled_color"),
                            current.display.overLeveledColor)
                    .setDefaultValue("#FF6600")
                    .setSaveConsumer(v -> current.display.overLeveledColor = v)
                    .build());
            displayCategory.addEntry(entry.startBooleanToggle(
                            Component.translatable("config.fizzle_enchanting.enable_inline_ench_descs"),
                            current.display.enableInlineEnchDescs)
                    .setDefaultValue(false)
                    .setSaveConsumer(v -> current.display.enableInlineEnchDescs = v)
                    .build());

            builder.setSavingRunnable(() -> {
                current.save();
                FizzleEnchanting.reloadConfig();
            });

            return builder.build();
        };
    }
}
