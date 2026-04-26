package com.fizzlesmp.fizzle_enchanting.advancement;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public final class ModTriggers {

    public static final EnchantedAtTableTrigger ENCHANTED_AT_TABLE = new EnchantedAtTableTrigger();

    private static boolean registered = false;

    private ModTriggers() {
    }

    public static void register() {
        if (registered) return;
        registered = true;

        Registry.register(BuiltInRegistries.TRIGGER_TYPES,
                FizzleEnchanting.id("enchanted_at_table"), ENCHANTED_AT_TABLE);
    }
}
