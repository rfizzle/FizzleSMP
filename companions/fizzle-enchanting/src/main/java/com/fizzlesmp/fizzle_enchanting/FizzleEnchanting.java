package com.fizzlesmp.fizzle_enchanting;

import com.fizzlesmp.fizzle_enchanting.anvil.FizzleAnvilHandlers;
import com.fizzlesmp.fizzle_enchanting.command.FizzleEnchantingCommand;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.fizzlesmp.fizzle_enchanting.advancement.ModTriggers;
import com.fizzlesmp.fizzle_enchanting.enchanting.EnchantingStatRegistry;
import com.fizzlesmp.fizzle_enchanting.enchanting.EnchantmentInfoRegistry;
import com.fizzlesmp.fizzle_enchanting.enchanting.recipe.EnchantingRecipeRegistry;
import com.fizzlesmp.fizzle_enchanting.event.EnchantmentEffectHandler;
import com.fizzlesmp.fizzle_enchanting.event.WardenLootHandler;
import com.fizzlesmp.fizzle_enchanting.net.EnchantmentInfoPayload;
import com.fizzlesmp.fizzle_enchanting.net.FizzleEnchantingNetworking;
import com.fizzlesmp.fizzle_enchanting.particle.ModParticles;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;
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
        ModParticles.register();
        ModTriggers.register();
        EnchantingStatRegistry.bootstrap();
        FizzleEnchantingRegistry.register();
        FizzleEnchantingRegistry.registerApiLookups();
        EnchantingRecipeRegistry.register();
        FizzleAnvilHandlers.register();
        WardenLootHandler.register();
        EnchantmentEffectHandler.register();
        FizzleEnchantingNetworking.registerPayloads();
        registerLifecycleEvents();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                FizzleEnchantingCommand.register(dispatcher));
    }

    private void registerLifecycleEvents() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> rebuildEnchantmentInfo(server));

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) {
                rebuildEnchantmentInfo(server);
                syncEnchantmentInfoToAll(server);
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            EnchantmentInfoPayload payload = EnchantmentInfoRegistry.buildPayload();
            ServerPlayNetworking.send(handler.player, payload);
        });
    }

    private static void rebuildEnchantmentInfo(MinecraftServer server) {
        Registry<Enchantment> reg = server.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        EnchantmentInfoRegistry.rebuild(reg, config);
    }

    private static void syncEnchantmentInfoToAll(MinecraftServer server) {
        EnchantmentInfoPayload payload = EnchantmentInfoRegistry.buildPayload();
        for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(sp, payload);
        }
    }

    public static FizzleEnchantingConfig getConfig() {
        return config;
    }

    public static void reloadConfig() {
        config = FizzleEnchantingConfig.load();
    }

    public static void reloadConfig(MinecraftServer server) {
        config = FizzleEnchantingConfig.load();
        rebuildEnchantmentInfo(server);
        syncEnchantmentInfoToAll(server);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
