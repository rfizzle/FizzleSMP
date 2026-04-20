package com.fizzlesmp.fizzle_enchanting.net;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Registers every S2C payload the enchanting mod sends. Called from the main
 * {@link com.fizzlesmp.fizzle_enchanting.FizzleEnchanting#onInitialize} during mod load so the
 * types are resolvable before any play-phase traffic starts.
 */
public final class FizzleEnchantingNetworking {

    private FizzleEnchantingNetworking() {
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(StatsPayload.TYPE, StatsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CluesPayload.TYPE, CluesPayload.CODEC);
    }
}
