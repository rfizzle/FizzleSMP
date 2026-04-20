package com.fizzlesmp.fizzle_enchanting;

import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.inventory.MenuType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * T-2.5.5 — proves the MenuType for the stat-driven enchanting table lands in
 * {@link BuiltInRegistries#MENU}. The server emits this type in the
 * {@code ClientboundOpenScreenPacket}; without the registration, the client can't reconstruct our
 * menu and the HUD subclass never draws.
 *
 * <p>Pure-vanilla tests (no fabric-loader-junit on the classpath — matches fizzle-difficulty)
 * need {@code Bootstrap.bootStrap()} to initialize the vanilla menu types, but that same call
 * freezes {@link BuiltInRegistries#MENU}. Fabric's mixin defers the freeze in production until
 * after {@code ModInitializer#onInitialize}; we replicate that deferral here by reflecting the
 * {@code frozen} flag back to {@code false} between bootstrap and our registration.
 */
class FizzleEnchantingRegistryTest {

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreezeMenuRegistry();
        FizzleEnchantingRegistry.register();
        // Re-freezing walks every registered entry and binds its Holder.Reference to its value.
        // Without this, our freshly-added entry stays unbound and .get() throws "Trying to access
        // unbound value" — even though the key is in the map.
        BuiltInRegistries.MENU.freeze();
    }

    @Test
    void register_menuTypeResolvesFromBuiltInRegistry() {
        ResourceLocation id = FizzleEnchanting.id("enchanting_table");
        MenuType<?> resolved = BuiltInRegistries.MENU.get(id);
        assertNotNull(resolved, "MenuType must be present in BuiltInRegistries.MENU after register()");
        assertSame(FizzleEnchantingRegistry.ENCHANTING_TABLE_MENU, resolved,
                "registry lookup must return the exact instance exposed on FizzleEnchantingRegistry");
    }

    @Test
    void register_isIdempotent() {
        // Second call must not re-register — the guard flag makes onInitialize safe to invoke
        // twice and lets tests share a bootstrap across classes.
        FizzleEnchantingRegistry.register();
        MenuType<?> resolved = BuiltInRegistries.MENU.get(FizzleEnchanting.id("enchanting_table"));
        assertSame(FizzleEnchantingRegistry.ENCHANTING_TABLE_MENU, resolved);
    }

    private static void unfreezeMenuRegistry() throws Exception {
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(BuiltInRegistries.MENU, false);
    }
}
