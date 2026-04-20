package com.fizzlesmp.fizzle_enchanting;

import com.fizzlesmp.fizzle_enchanting.anvil.FizzleAnvilHandlers;
import com.fizzlesmp.fizzle_enchanting.command.FizzleEnchantingCommand;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.fizzlesmp.fizzle_enchanting.enchanting.EnchantingStatRegistry;
import com.fizzlesmp.fizzle_enchanting.net.FizzleEnchantingNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FizzleEnchanting implements ModInitializer {
    public static final String MOD_ID = "fizzle_enchanting";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static volatile FizzleEnchantingConfig config;

    @Override
    public void onInitialize() {
        LOGGER.info("Fizzle Enchanting initialized");
        config = FizzleEnchantingConfig.load();
        EnchantingStatRegistry.bootstrap();
        FizzleEnchantingRegistry.register();
        FizzleAnvilHandlers.register();
        FizzleEnchantingNetworking.registerPayloads();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                FizzleEnchantingCommand.register(dispatcher));
    }

    public static FizzleEnchantingConfig getConfig() {
        return config;
    }

    public static void reloadConfig() {
        config = FizzleEnchantingConfig.load();
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
