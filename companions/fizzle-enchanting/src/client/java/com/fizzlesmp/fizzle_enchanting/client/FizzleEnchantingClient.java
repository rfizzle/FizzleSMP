package com.fizzlesmp.fizzle_enchanting.client;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.fizzlesmp.fizzle_enchanting.client.net.ClientPayloadHandlers;
import com.fizzlesmp.fizzle_enchanting.client.screen.EnchantmentLibraryScreen;
import com.fizzlesmp.fizzle_enchanting.client.screen.FizzleEnchantmentScreen;
import com.fizzlesmp.fizzle_enchanting.client.tooltip.OverLeveledTooltipHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class FizzleEnchantingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPayloadHandlers.register();
        MenuScreens.register(FizzleEnchantingRegistry.ENCHANTING_TABLE_MENU, FizzleEnchantmentScreen::new);
        MenuScreens.register(FizzleEnchantingRegistry.LIBRARY_MENU, EnchantmentLibraryScreen::new);
        OverLeveledTooltipHandler.register();
    }
}
