package com.fizzlesmp.fizzle_enchanting.anvil;

/**
 * One-stop registration hook for every MVP {@link AnvilHandler}. Called from
 * {@link com.fizzlesmp.fizzle_enchanting.FizzleEnchanting#onInitialize} so the dispatcher chain is
 * populated before any anvil interaction fires.
 *
 * <p>Order matters — {@link AnvilDispatcher} walks handlers top-to-bottom and the first
 * non-empty result wins. The Prismatic Web handler is the only MVP handler wired today
 * (T-4.1.4); iron-block anvil repair (Story S-4.2) and the tome families (Story S-5.2) slot in
 * below it in later stories.
 */
public final class FizzleAnvilHandlers {

    private static boolean registered = false;

    private FizzleAnvilHandlers() {
    }

    public static void register() {
        if (registered) return;
        registered = true;
        AnvilDispatcher.register(new PrismaticWebHandler());
    }
}
