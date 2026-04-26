package com.fizzlesmp.fizzle_enchanting.client;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.client.net.ClientPayloadHandlers;
import com.fizzlesmp.fizzle_enchanting.client.screen.EnchantmentLibraryScreen;
import com.fizzlesmp.fizzle_enchanting.client.screen.FizzleEnchantmentScreen;
import com.fizzlesmp.fizzle_enchanting.client.tooltip.OverLeveledTooltipHandler;
import com.fizzlesmp.fizzle_enchanting.client.tooltip.ShelfStatTooltipHandler;
import com.fizzlesmp.fizzle_enchanting.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.FlyTowardsPositionParticle;

public class FizzleEnchantingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPayloadHandlers.register();
        MenuScreens.register(FizzleEnchantingRegistry.ENCHANTING_TABLE_MENU, FizzleEnchantmentScreen::new);
        MenuScreens.register(FizzleEnchantingRegistry.LIBRARY_MENU, EnchantmentLibraryScreen::new);
        OverLeveledTooltipHandler.register();
        ShelfStatTooltipHandler.register();
        registerParticleProviders();
    }

    private static void registerParticleProviders() {
        ParticleFactoryRegistry r = ParticleFactoryRegistry.getInstance();
        r.register(ModParticles.ENCHANT_FIRE, FlyTowardsPositionParticle.EnchantProvider::new);
        r.register(ModParticles.ENCHANT_WATER, FlyTowardsPositionParticle.EnchantProvider::new);
        r.register(ModParticles.ENCHANT_SCULK, FlyTowardsPositionParticle.EnchantProvider::new);
        r.register(ModParticles.ENCHANT_END, FlyTowardsPositionParticle.EnchantProvider::new);
    }
}
