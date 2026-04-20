package com.fizzlesmp.fizzle_enchanting.client;

import com.fizzlesmp.fizzle_enchanting.client.net.ClientPayloadHandlers;
import net.fabricmc.api.ClientModInitializer;

public class FizzleEnchantingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPayloadHandlers.register();
    }
}
