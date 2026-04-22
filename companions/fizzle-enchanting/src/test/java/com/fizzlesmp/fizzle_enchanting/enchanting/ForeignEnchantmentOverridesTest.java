package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
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
 * T-6.3.3 coverage: bundled foreign-enchant overrides live in a built-in Fabric resource pack so
 * the {@code config.foreignEnchantments.applyBundledOverrides} flag can flip the pack between
 * {@code ALWAYS_ENABLED} (bundled overrides active — raises Mending / Soulbound weights) and
 * {@code NORMAL} (off by default — operator can still opt in via the datapack list). This test
 * pins the decision matrix and verifies the pack metadata ships at the expected jar path.
 */
class ForeignEnchantmentOverridesTest {

    @Test
    void decideActivationType_true_returnsAlwaysEnabled() {
        assertEquals(ResourcePackActivationType.ALWAYS_ENABLED,
                ForeignEnchantmentOverrides.decideActivationType(true),
                "overrides on → pack must be forced active so Mending / Soulbound weights are applied");
    }

    @Test
    void decideActivationType_false_returnsNormal() {
        assertEquals(ResourcePackActivationType.NORMAL,
                ForeignEnchantmentOverrides.decideActivationType(false),
                "overrides off → pack must be disabled by default so upstream weights are restored");
    }

    @Test
    void packMcmeta_shipsAtExpectedPath() throws Exception {
        URL url = ForeignEnchantmentOverridesTest.class.getResource(
                "/resourcepacks/foreign_overrides/pack.mcmeta");
        assertNotNull(url,
                "pack.mcmeta must ship inside the jar at /resourcepacks/foreign_overrides/pack.mcmeta");

        Path path = Paths.get(url.toURI());
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            assertTrue(element.isJsonObject(), "pack.mcmeta must be a JSON object");

            JsonObject pack = element.getAsJsonObject().getAsJsonObject("pack");
            assertNotNull(pack, "pack.mcmeta must declare a pack block");
            assertEquals(48, pack.get("pack_format").getAsInt(),
                    "pack_format must be 48 for 1.21.1 data packs");
        }
    }

    @Test
    void packId_matchesRegistrarConstant() {
        assertEquals("foreign_overrides", ForeignEnchantmentOverrides.PACK_ID,
                "pack id feeds the registerBuiltinResourcePack call; keep it in sync with the "
                        + "resources/resourcepacks/foreign_overrides/ directory name");
    }

    @Test
    void mendingOverride_shipsInsideResourcePack() {
        URL url = ForeignEnchantmentOverridesTest.class.getResource(
                "/resourcepacks/foreign_overrides/data/minecraft/enchantment/mending.json");
        assertNotNull(url,
                "mending override must ship inside the built-in resource pack so the config flag "
                        + "can gate it");
    }

    @Test
    void soulboundOverride_shipsInsideResourcePack() {
        URL url = ForeignEnchantmentOverridesTest.class.getResource(
                "/resourcepacks/foreign_overrides/data/yigd/enchantment/soulbound.json");
        assertNotNull(url,
                "soulbound override must ship inside the built-in resource pack so the config flag "
                        + "can gate it");
    }
}
