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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-6.2.2 coverage: the authored {@code shield_bash} enchantment plus the weapon-tag expansion
 * that makes it eligible for shields.
 *
 * <p>Two resources ship with this task:
 * <ul>
 *   <li>{@code data/meridian/enchantment/shield_bash.json} — the enchant itself,
 *       attaching a {@code minecraft:damage} additive plus a {@code minecraft:post_attack}
 *       durability cost via {@code minecraft:damage_item}.</li>
 *   <li>{@code data/minecraft/tags/item/enchantable/weapon.json} — a non-replacing tag override
 *       that adds {@code minecraft:shield} to the vanilla weapon-enchantable tag.</li>
 * </ul>
 *
 * <p>Tier-2 scope: filesystem + structural JSON validation only, matching the convention in
 * {@link PortedEnchantmentsTest} and {@link AuthoredIcyThornsTest}. Full registry-resolution
 * checks are deferred to the gametest tier.
 */
class AuthoredShieldBashTest {

    private static final String ENCHANT_RESOURCE = "/data/meridian/enchantment/shield_bash.json";
    private static final String TAG_RESOURCE = "/data/minecraft/tags/item/enchantable/weapon.json";
    private static final String LANG_RESOURCE = "/assets/meridian/lang/en_us.json";

    private static Path resource(String path) throws Exception {
        URL url = AuthoredShieldBashTest.class.getResource(path);
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
    void shieldBash_filePresent() throws Exception {
        assertTrue(Files.exists(resource(ENCHANT_RESOURCE)),
                "shield_bash.json must ship at the top of the enchantment resource tree");
    }

    @Test
    void shieldBash_hasRequiredEnchantmentFields() throws Exception {
        JsonObject obj = loadJson(ENCHANT_RESOURCE);
        for (String field : new String[]{
                "description", "supported_items", "weight", "max_level",
                "min_cost", "max_cost", "slots", "effects"}) {
            assertTrue(obj.has(field), "shield_bash.json missing required field: " + field);
        }
        assertTrue(obj.get("slots").isJsonArray(), "slots must be an array");
        assertTrue(obj.get("effects").isJsonObject(), "effects must be an object");
    }

    @Test
    void shieldBash_targetsWeaponTagMainhand() throws Exception {
        JsonObject obj = loadJson(ENCHANT_RESOURCE);
        assertEquals("#minecraft:enchantable/weapon",
                obj.get("supported_items").getAsString(),
                "supported_items must point at the vanilla weapon tag so the shield expansion picks it up");

        JsonArray slots = obj.getAsJsonArray("slots");
        assertEquals(1, slots.size(), "shield_bash should activate in exactly one slot group");
        assertEquals("mainhand", slots.get(0).getAsString(),
                "shield_bash fires only when the shield is wielded in mainhand");
    }

    @Test
    void shieldBash_zenithBalanceValuesMirrored() throws Exception {
        JsonObject obj = loadJson(ENCHANT_RESOURCE);
        assertEquals(4, obj.get("max_level").getAsInt(), "Zenith caps shield_bash at level 4");
        assertEquals(2, obj.get("weight").getAsInt(),
                "Zenith classifies shield_bash as RARE (weight 2)");

        JsonObject minCost = obj.getAsJsonObject("min_cost");
        assertEquals(1, minCost.get("base").getAsInt(),
                "min_cost base must mirror Zenith: 1 + (lvl-1)*17");
        assertEquals(17, minCost.get("per_level_above_first").getAsInt(),
                "min_cost slope must mirror Zenith's +17/level");

        JsonObject maxCost = obj.getAsJsonObject("max_cost");
        assertEquals(41, maxCost.get("base").getAsInt(),
                "max_cost base = min_cost base + 40 at level 1");
        assertEquals(17, maxCost.get("per_level_above_first").getAsInt(),
                "max_cost slope must track min_cost's +17/level");
    }

    @Test
    void shieldBash_damageEffectIsAdditiveLinear() throws Exception {
        JsonObject obj = loadJson(ENCHANT_RESOURCE);
        JsonObject effects = obj.getAsJsonObject("effects");
        assertTrue(effects.has("minecraft:damage"),
                "shield_bash must attach to minecraft:damage for the bonus-damage beat");

        JsonArray entries = effects.getAsJsonArray("minecraft:damage");
        assertEquals(1, entries.size(), "one damage entry is sufficient");

        JsonObject effect = entries.get(0).getAsJsonObject().getAsJsonObject("effect");
        assertEquals("minecraft:add", effect.get("type").getAsString(),
                "damage must be an additive bonus, not a multiplier");

        JsonObject value = effect.getAsJsonObject("value");
        assertEquals("minecraft:linear", value.get("type").getAsString(),
                "damage value must scale linearly per enchant level");
        assertEquals(3.5, value.get("base").getAsDouble(),
                "Zenith's damage-per-level is 3.5");
        assertEquals(3.5, value.get("per_level_above_first").getAsDouble(),
                "Zenith's damage grows at 3.5/level");
    }

    @Test
    void shieldBash_postAttackCostsDurabilityOnAttacker() throws Exception {
        JsonObject obj = loadJson(ENCHANT_RESOURCE);
        JsonObject effects = obj.getAsJsonObject("effects");
        assertTrue(effects.has("minecraft:post_attack"),
                "shield_bash must pay durability via minecraft:post_attack");

        JsonArray entries = effects.getAsJsonArray("minecraft:post_attack");
        assertEquals(1, entries.size(), "one post_attack entry is sufficient");

        JsonObject entry = entries.get(0).getAsJsonObject();
        assertEquals("attacker", entry.get("affected").getAsString(),
                "the attacker's held shield is the item that takes damage");
        assertEquals("attacker", entry.get("enchanted").getAsString(),
                "the attacker is the side carrying the enchanted shield");

        JsonObject effect = entry.getAsJsonObject("effect");
        assertEquals("minecraft:damage_item", effect.get("type").getAsString(),
                "durability cost must use minecraft:damage_item");

        JsonObject amount = effect.getAsJsonObject("amount");
        assertEquals("minecraft:linear", amount.get("type").getAsString(),
                "damage amount must scale per level");
        assertEquals(19, amount.get("base").getAsInt(),
                "base cost mirrors Zenith's max(1, 20-level) at level 1 → 19");
        assertEquals(-1, amount.get("per_level_above_first").getAsInt(),
                "cost drops by 1 per level so higher tiers save durability");
    }

    @Test
    void weaponTagExpansion_addsShieldWithoutReplace() throws Exception {
        JsonObject tag = loadJson(TAG_RESOURCE);
        if (tag.has("replace")) {
            assertFalse(tag.get("replace").getAsBoolean(),
                    "replace must be false so shield_bash piggybacks on the vanilla weapon tag");
        }

        assertTrue(tag.has("values") && tag.get("values").isJsonArray(),
                "weapon tag must carry a values array");
        JsonArray values = tag.getAsJsonArray("values");

        boolean containsShield = false;
        for (JsonElement element : values) {
            String id = element.isJsonPrimitive() ? element.getAsString()
                    : element.isJsonObject() && element.getAsJsonObject().has("id")
                    ? element.getAsJsonObject().get("id").getAsString() : null;
            if ("minecraft:shield".equals(id)) {
                containsShield = true;
                break;
            }
        }
        assertTrue(containsShield, "weapon tag must add minecraft:shield so shield_bash can apply");
    }

    @Test
    void shieldBash_hasMatchingLangKey() throws Exception {
        JsonObject lang = loadJson(LANG_RESOURCE);
        JsonObject enchant = loadJson(ENCHANT_RESOURCE);
        String key = enchant.getAsJsonObject("description").get("translate").getAsString();
        assertTrue(lang.has(key), "lang file must expose " + key);
    }
}
