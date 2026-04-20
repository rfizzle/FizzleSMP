package com.fizzlesmp.fizzle_enchanting.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class FizzleEnchantingDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(FizzleModelProvider::new);
        pack.addProvider(FizzleBlockLootTableProvider::new);
        pack.addProvider(FizzleRecipeProvider::new);
    }
}
