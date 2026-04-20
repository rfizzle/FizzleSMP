package com.fizzlesmp.fizzle_enchanting.client.net;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.net.CluesPayload;
import com.fizzlesmp.fizzle_enchanting.net.StatsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * S2C receivers for the enchanting payloads. Handlers currently log receipt only — screen
 * updates land with the menu/screen replacement in S-2.5.
 */
public final class ClientPayloadHandlers {

    private ClientPayloadHandlers() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(StatsPayload.TYPE,
                (payload, context) -> FizzleEnchanting.LOGGER.debug(
                        "Received stats payload (eterna={}, clues={}, blacklist={}, treasure={}, crafting={})",
                        payload.eterna(), payload.clues(), payload.blacklist().size(),
                        payload.treasure(), payload.craftingResult().isPresent()));

        ClientPlayNetworking.registerGlobalReceiver(CluesPayload.TYPE,
                (payload, context) -> FizzleEnchanting.LOGGER.debug(
                        "Received clues payload (slot={}, count={}, exhausted={})",
                        payload.slot(), payload.clues().size(), payload.exhaustedList()));
    }
}
