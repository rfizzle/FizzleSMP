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
 * T-6.3.2 coverage: the bundled {@code data/yigd/enchantment/soulbound.json} override must parse
 * without requiring yigd on the classpath, must declare a non-zero weight so the enchant feeds
 * the library pool, and must bind {@code supported_items} to the existing {@code #yigd:soulbindable}
 * tag so the intent matches the legacy EnchantingInfuser Paxi pack this override replaces.
 */
class SoulboundOverrideTest {

    private static final String OVERRIDE_RESOURCE =
            "/resourcepacks/foreign_overrides/data/yigd/enchantment/soulbound.json";

    private static JsonObject loadOverride() throws Exception {
        URL url = SoulboundOverrideTest.class.getResource(OVERRIDE_RESOURCE);
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
    void weight_isNonZero() throws Exception {
        JsonObject obj = loadOverride();
        assertTrue(obj.has("weight"), "override must declare weight");
        int weight = obj.get("weight").getAsInt();
        assertTrue(weight > 0,
                "weight must be non-zero so soulbound can roll at the enchanting table");
    }

    @Test
    void supportedItems_boundToSoulbindableTag() throws Exception {
        JsonObject obj = loadOverride();
        assertEquals("#yigd:soulbindable",
                obj.get("supported_items").getAsString(),
                "supported_items must point at #yigd:soulbindable to inherit the tag's item set");
    }

    @Test
    void description_pointsAtYigdLangKey() throws Exception {
        JsonObject obj = loadOverride();
        JsonObject description = obj.getAsJsonObject("description");
        assertNotNull(description, "override must declare a description block");
        assertEquals("enchantment.yigd.soulbound",
                description.get("translate").getAsString(),
                "description.translate must keep the yigd lang key so the in-game name does not change");
    }

    @Test
    void overridePath_targetsYigdNamespace() {
        URL url = SoulboundOverrideTest.class.getResource(OVERRIDE_RESOURCE);
        assertNotNull(url, "override must live under the yigd namespace to shadow the upstream file");
        assertTrue(url.getPath().endsWith("/data/yigd/enchantment/soulbound.json"),
                "override file path must match the upstream datapack location: " + url.getPath());
    }

    @Test
    void structuralFields_presentAndWellTyped() throws Exception {
        JsonObject obj = loadOverride();

        assertTrue(obj.has("max_level"), "override must declare max_level");
        assertEquals(1, obj.get("max_level").getAsInt());

        assertTrue(obj.has("anvil_cost"), "override must declare anvil_cost");
        assertTrue(obj.get("anvil_cost").getAsInt() > 0,
                "anvil_cost must be positive so the enchant can apply at the anvil");

        JsonObject minCost = obj.getAsJsonObject("min_cost");
        assertNotNull(minCost, "override must declare min_cost");
        assertTrue(minCost.has("base") && minCost.has("per_level_above_first"),
                "min_cost must declare base + per_level_above_first");

        JsonObject maxCost = obj.getAsJsonObject("max_cost");
        assertNotNull(maxCost, "override must declare max_cost");
        assertTrue(maxCost.has("base") && maxCost.has("per_level_above_first"),
                "max_cost must declare base + per_level_above_first");

        assertTrue(obj.has("slots") && obj.getAsJsonArray("slots").size() > 0,
                "override must declare at least one slot");
    }
}
