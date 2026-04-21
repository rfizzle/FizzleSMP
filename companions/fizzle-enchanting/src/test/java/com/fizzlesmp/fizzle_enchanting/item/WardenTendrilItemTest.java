package com.fizzlesmp.fizzle_enchanting.item;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-5.4.2 — proves the {@code warden_tendril} specialty material registers under its expected id,
 * ships its static texture + item model on the classpath, and carries the expected lang key.
 *
 * <p>Drop distribution is covered by {@code WardenLootHandlerTest} (T-5.4.3); this test scope is
 * strictly the item-side contract so a broken registration fails fast without depending on the
 * loot-modification pipeline.
 */
class WardenTendrilItemTest {

    private static final String TEXTURE_PATH =
            "/assets/fizzle_enchanting/textures/item/warden_tendril.png";
    private static final String MODEL_PATH =
            "/assets/fizzle_enchanting/models/item/warden_tendril.json";
    private static final String LANG_PATH =
            "/assets/fizzle_enchanting/lang/en_us.json";

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreezeIntrusive(BuiltInRegistries.BLOCK);
        unfreezeIntrusive(BuiltInRegistries.ITEM);
        unfreezeIntrusive(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        unfreeze(BuiltInRegistries.MENU);

        FizzleEnchantingRegistry.register();

        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
        BuiltInRegistries.MENU.freeze();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();
    }

    @Test
    void wardenTendril_resolvesFromItemRegistry() {
        Item resolved = BuiltInRegistries.ITEM.get(FizzleEnchanting.id("warden_tendril"));
        assertNotNull(resolved, "WardenTendrilItem must be registered under fizzle_enchanting:warden_tendril");
        assertSame(FizzleEnchantingRegistry.WARDEN_TENDRIL, resolved,
                "registry entry must be the exact instance exposed on FizzleEnchantingRegistry");
        assertInstanceOf(WardenTendrilItem.class, resolved);
    }

    @Test
    void texture_existsOnClasspath() throws Exception {
        try (InputStream texture = getClass().getResourceAsStream(TEXTURE_PATH)) {
            assertNotNull(texture, "warden_tendril.png missing at " + TEXTURE_PATH);
        }
    }

    @Test
    void itemModel_parentsGeneratedAndPointsAtWardenTendrilTexture() throws Exception {
        try (InputStream in = getClass().getResourceAsStream(MODEL_PATH)) {
            assertNotNull(in, "model JSON missing at " + MODEL_PATH);
            JsonObject model = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            assertEquals("minecraft:item/generated", model.get("parent").getAsString(),
                    "warden_tendril is a flat 2D item, must parent minecraft:item/generated");
            assertEquals("fizzle_enchanting:item/warden_tendril",
                    model.getAsJsonObject("textures").get("layer0").getAsString(),
                    "model layer0 must point at the top-level item/ texture, not a subfolder");
        }
    }

    @Test
    void langKey_present() throws Exception {
        try (InputStream in = getClass().getResourceAsStream(LANG_PATH)) {
            assertNotNull(in, "en_us.json must be on the test classpath");
            JsonObject lang = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            assertTrue(lang.has("item.fizzle_enchanting.warden_tendril"),
                    "lang file must declare item.fizzle_enchanting.warden_tendril so the inventory UI doesn't show the raw id");
            assertEquals("Warden Tendril",
                    lang.get("item.fizzle_enchanting.warden_tendril").getAsString());
        }
    }

    private static void unfreeze(Registry<?> registry) throws Exception {
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(registry, false);
    }

    private static void unfreezeIntrusive(Registry<?> registry) throws Exception {
        unfreeze(registry);
        Field intrusive = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        intrusive.setAccessible(true);
        if (intrusive.get(registry) == null) {
            intrusive.set(registry, new IdentityHashMap<>());
        }
    }
}
