package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-6.3.1 coverage: the bundled {@code data/minecraft/enchantment/mending.json} override must
 * parse, must raise the weight above the vanilla baseline (vanilla 1.21.1 ships {@code weight: 2}),
 * and must keep every other field that controls cost, slot eligibility, and effect math identical
 * to vanilla so the override is a pure weight bump rather than a behavioral change.
 */
class MendingOverrideTest {

    private static final String OVERRIDE_RESOURCE =
            "/resourcepacks/foreign_overrides/data/minecraft/enchantment/mending.json";
    private static final int VANILLA_WEIGHT = 2;
    private static final int BUNDLED_WEIGHT = 4;

    private static JsonObject loadOverride() throws Exception {
        URL url = MendingOverrideTest.class.getResource(OVERRIDE_RESOURCE);
        assertNotNull(url, "override file must ship on the classpath at " + OVERRIDE_RESOURCE);
        Path path = Paths.get(url.toURI());
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            assertTrue(element.isJsonObject(), "override must be a JSON object");
            return element.getAsJsonObject();
        }
    }

    @Test
    void file_parsesAsJson() throws Exception {
        loadOverride();
    }

    @Test
    void weight_isRaisedAboveVanilla() throws Exception {
        JsonObject obj = loadOverride();
        assertTrue(obj.has("weight"), "override must declare weight");
        int weight = obj.get("weight").getAsInt();
        assertEquals(BUNDLED_WEIGHT, weight, "override should ship the documented bundled weight");
        assertTrue(weight > VANILLA_WEIGHT,
                "weight must be raised above vanilla " + VANILLA_WEIGHT + " to feed the library");
    }

    @Test
    void description_pointsAtVanillaLangKey() throws Exception {
        JsonObject obj = loadOverride();
        JsonObject description = obj.getAsJsonObject("description");
        assertNotNull(description, "override must keep the vanilla description block");
        assertEquals("enchantment.minecraft.mending",
                description.get("translate").getAsString(),
                "description.translate must keep vanilla key so the in-game name does not change");
    }

    @Test
    void supportedItems_unchangedFromVanilla() throws Exception {
        JsonObject obj = loadOverride();
        assertEquals("#minecraft:enchantable/durability",
                obj.get("supported_items").getAsString(),
                "supported_items must stay on the vanilla durability tag");
    }

    @Test
    void costAndSlotFields_unchangedFromVanilla() throws Exception {
        JsonObject obj = loadOverride();

        assertEquals(1, obj.get("max_level").getAsInt());
        assertEquals(4, obj.get("anvil_cost").getAsInt());

        JsonObject minCost = obj.getAsJsonObject("min_cost");
        assertEquals(25, minCost.get("base").getAsInt());
        assertEquals(25, minCost.get("per_level_above_first").getAsInt());

        JsonObject maxCost = obj.getAsJsonObject("max_cost");
        assertEquals(75, maxCost.get("base").getAsInt());
        assertEquals(25, maxCost.get("per_level_above_first").getAsInt());

        assertEquals(1, obj.getAsJsonArray("slots").size());
        assertEquals("any", obj.getAsJsonArray("slots").get(0).getAsString());
    }

    @Test
    void repairWithXpEffect_unchangedFromVanilla() throws Exception {
        JsonObject obj = loadOverride();
        JsonObject effects = obj.getAsJsonObject("effects");
        assertNotNull(effects, "override must keep the effects map");
        assertTrue(effects.has("minecraft:repair_with_xp"),
                "override must keep the vanilla repair_with_xp behavior");

        JsonObject effect = effects.getAsJsonArray("minecraft:repair_with_xp")
                .get(0).getAsJsonObject().getAsJsonObject("effect");
        assertEquals("minecraft:multiply", effect.get("type").getAsString());
        assertEquals(2.0, effect.get("factor").getAsDouble(), 0.0001);
    }

    @Test
    void overridePath_targetsVanillaNamespace() {
        URL url = MendingOverrideTest.class.getResource(OVERRIDE_RESOURCE);
        assertNotNull(url, "override must live under the minecraft namespace to shadow vanilla");
        assertTrue(url.getPath().endsWith("/data/minecraft/enchantment/mending.json"),
                "override file path must match the vanilla datapack location: " + url.getPath());
    }
}
