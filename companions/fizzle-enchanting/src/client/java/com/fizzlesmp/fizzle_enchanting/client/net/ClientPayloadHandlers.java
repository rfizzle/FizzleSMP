package com.fizzlesmp.fizzle_enchanting.client.net;

import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentMenu;
import com.fizzlesmp.fizzle_enchanting.net.CluesPayload;
import com.fizzlesmp.fizzle_enchanting.net.StatsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;

/**
 * S2C receivers for the enchanting payloads. The stats payload is forwarded to the open
 * {@link FizzleEnchantmentMenu} so {@code FizzleEnchantmentScreen} can read live stat values from
 * the menu instance. Clues are forwarded to the menu's per-slot clue cache for tooltip rendering.
 */
public final class ClientPayloadHandlers {

    private ClientPayloadHandlers() {
    }

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(StatsPayload.TYPE,
                (payload, context) -> context.client().execute(() -> {
                    LocalPlayer player = context.player();
                    if (player != null && player.containerMenu instanceof FizzleEnchantmentMenu menu) {
                        menu.applyClientStats(payload);
                    }
                }));

        ClientPlayNetworking.registerGlobalReceiver(CluesPayload.TYPE,
                (payload, context) -> context.client().execute(() -> {
                    LocalPlayer player = context.player();
                    if (player != null && player.containerMenu instanceof FizzleEnchantmentMenu menu) {
                        menu.applyClientClues(payload.slot(), payload.clues(), payload.exhaustedList());
                    }
                }));
    }
}
