package com.fizzlesmp.fizzle_enchanting.anvil;

/**
 * One-stop registration hook for every MVP {@link AnvilHandler}. Called from
 * {@link com.fizzlesmp.fizzle_enchanting.FizzleEnchanting#onInitialize} so the dispatcher chain is
 * populated before any anvil interaction fires.
 *
 * <p>Order matters — {@link AnvilDispatcher} walks handlers top-to-bottom and the first
 * non-empty result wins. Prismatic Web (T-4.1.4) and iron-block anvil repair (S-4.2) key off
 * disjoint right-slot items (web vs. iron block), so their relative order is behaviour-neutral;
 * they are listed in task-introduction order to keep the registration site readable. The tome
 * families (Story S-5.2) slot in below them in later stories.
 */
public final class FizzleAnvilHandlers {

    private static boolean registered = false;

    private FizzleAnvilHandlers() {
    }

    public static void register() {
        if (registered) return;
        registered = true;
        AnvilDispatcher.register(new PrismaticWebHandler());
        AnvilDispatcher.register(new IronBlockAnvilRepairHandler());
    }
}
