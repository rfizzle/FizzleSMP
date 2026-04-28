package com.rfizzle.meridian.enchanting;

import com.google.gson.JsonArray;
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
 * T-6.2.1 coverage: the authored {@code icy_thorns} enchantment JSON must exist under
 * {@code data/meridian/enchantment/icy_thorns.json}, match the vanilla enchantment-
 * definition shape, restrict itself to chest armor, and wire a {@code post_attack} slowness
 * effect in the victim→attacker direction.
 *
 * <p>Tier-2 scope: filesystem + structural JSON validation, matching
 * {@link PortedEnchantmentsTest}. Full {@code Enchantment.DIRECT_CODEC} parse is deferred to
 * the gametest tier.
 */
class AuthoredIcyThornsTest {

    private static final String RESOURCE_PATH = "/data/meridian/enchantment/icy_thorns.json";
    private static final String LANG_RESOURCE = "/assets/meridian/lang/en_us.json";

    private static Path resource(String path) throws Exception {
        URL url = AuthoredIcyThornsTest.class.getResource(path);
        assertNotNull(url, "resource must be on the test classpath: " + path);
        return Paths.get(url.toURI());
    }

    private static JsonObject loadJson(String path) throws Exception {
        try (Reader reader = Files.newBufferedReader(resource(path), StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            assertTrue(element.isJsonObject(), path + " must be a JSON object");
            return element.getAsJsonObject();
        }
    }

    @Test
    void icyThorns_filePresent() throws Exception {
        assertTrue(Files.exists(resource(RESOURCE_PATH)),
                "icy_thorns.json must ship at the top of the enchantment resource tree");
    }

    @Test
    void icyThorns_hasRequiredEnchantmentFields() throws Exception {
        JsonObject obj = loadJson(RESOURCE_PATH);
        for (String field : new String[]{
                "description", "supported_items", "weight", "max_level",
                "min_cost", "max_cost", "slots", "effects"}) {
            assertTrue(obj.has(field), "icy_thorns.json missing required field: " + field);
        }
        assertTrue(obj.get("slots").isJsonArray(), "slots must be an array");
        assertTrue(obj.get("effects").isJsonObject(), "effects must be an object");
    }

    @Test
    void icyThorns_targetsChestArmorOnly() throws Exception {
        JsonObject obj = loadJson(RESOURCE_PATH);
        assertEquals("#minecraft:enchantable/chest_armor",
                obj.get("supported_items").getAsString(),
                "supported_items must restrict icy_thorns to the vanilla chest_armor tag");

        JsonArray slots = obj.getAsJsonArray("slots");
        assertEquals(1, slots.size(), "icy_thorns should activate in exactly one slot group");
        assertEquals("chest", slots.get(0).getAsString(),
                "icy_thorns must only fire while worn in the chest slot");
    }

    @Test
    void icyThorns_zenithBalanceValuesMirrored() throws Exception {
        JsonObject obj = loadJson(RESOURCE_PATH);
        assertEquals(3, obj.get("max_level").getAsInt(), "Zenith caps icy_thorns at level 3");
        assertEquals(2, obj.get("weight").getAsInt(),
                "Zenith classifies icy_thorns as RARE (weight 2)");

        JsonObject minCost = obj.getAsJsonObject("min_cost");
        assertEquals(35, minCost.get("base").getAsInt(),
                "min_cost base must mirror Zenith: 35 + (lvl-1)*20");
        assertEquals(20, minCost.get("per_level_above_first").getAsInt(),
                "min_cost slope must mirror Zenith's +20/level");

        JsonObject maxCost = obj.getAsJsonObject("max_cost");
        assertEquals(200, maxCost.get("base").getAsInt(), "max_cost flat cap is Zenith's 200");
        assertEquals(0, maxCost.get("per_level_above_first").getAsInt(),
                "max_cost must stay flat across levels");
    }

    @Test
    void icyThorns_postAttackAppliesSlownessInVictimToAttackerDirection() throws Exception {
        JsonObject obj = loadJson(RESOURCE_PATH);
        JsonObject effects = obj.getAsJsonObject("effects");
        assertTrue(effects.has("minecraft:post_attack"),
                "icy_thorns must attach to minecraft:post_attack");

        JsonArray entries = effects.getAsJsonArray("minecraft:post_attack");
        assertEquals(1, entries.size(), "one post_attack entry is sufficient");

        JsonObject entry = entries.get(0).getAsJsonObject();
        assertEquals("attacker", entry.get("affected").getAsString(),
                "slowness must land on the attacker");
        assertEquals("victim", entry.get("enchanted").getAsString(),
                "the chest-armor wearer is the enchanted victim");

        JsonObject effect = entry.getAsJsonObject("effect");
        assertEquals("minecraft:apply_mob_effect", effect.get("type").getAsString(),
                "effect must be minecraft:apply_mob_effect");
        assertEquals("minecraft:slowness", effect.get("to_apply").getAsString(),
                "slowness is the Zenith-parity retaliation effect");
    }

    @Test
    void icyThorns_hasMatchingLangKey() throws Exception {
        JsonObject lang = loadJson(LANG_RESOURCE);
        JsonObject enchant = loadJson(RESOURCE_PATH);
        String key = enchant.getAsJsonObject("description").get("translate").getAsString();
        assertTrue(lang.has(key), "lang file must expose " + key);
    }
}
