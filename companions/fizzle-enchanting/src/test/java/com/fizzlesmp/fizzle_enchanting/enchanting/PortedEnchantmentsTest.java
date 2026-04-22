package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-6.1.2 coverage: every enchantment JSON ported from NeoEnchant+ v5.14.0 must sit under
 * {@code data/fizzle_enchanting/enchantment/}, carry no trace of the {@code enchantplus:} namespace
 * in ID references, and conform to the vanilla enchantment-definition schema.
 *
 * <p>NeoEnchant+ v5.14.0 actually ships <b>57</b> enchant JSONs (TODO.md says 56 — off by one).
 * Cutting the 7 files listed in the DESIGN cut table leaves <b>50</b> ports, so the file-count
 * assertion is 50 even though the original TODO bullets said 49.
 *
 * <p><b>Codec scope:</b> this Tier-2 test validates structural shape — mandatory top-level fields
 * present, effects block well-formed — matching TESTING-TODO § TEST-6.1-T2 "format only". Full
 * {@code Enchantment.DIRECT_CODEC} parse requires a loaded datapack (the JSONs reference
 * {@code voxel:} item tags, data-driven damage_type registry entries, and cross-namespace item
 * tags) and is deferred to the Tier-3 gametest TEST-6.1-T3.
 */
class PortedEnchantmentsTest {

    private static final String RESOURCE_DIR = "/data/fizzle_enchanting/enchantment/";

    private static final List<String> CUT_NAMES = List.of(
            "axe/timber.json",
            "pickaxe/bedrock_breaker.json",
            "pickaxe/spawner_touch.json",
            "tools/auto_smelt.json",
            "helmet/auto_feed.json",
            "chestplate/magnet.json",
            "sword/runic_despair.json");

    private static final int EXPECTED_PORT_COUNT = 50;

    /** Top-level fields the vanilla Enchantment codec requires. */
    private static final List<String> REQUIRED_FIELDS = List.of(
            "description",
            "supported_items",
            "weight",
            "max_level",
            "min_cost",
            "max_cost",
            "slots",
            "effects");

    private static Path resourceDir() throws Exception {
        URL url = PortedEnchantmentsTest.class.getResource(RESOURCE_DIR);
        assertNotNull(url, "enchantment resource dir must be on the test classpath");
        return Paths.get(url.toURI());
    }

    private static List<Path> allJsonFiles() throws Exception {
        try (Stream<Path> files = Files.walk(resourceDir())) {
            return files.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .sorted()
                    .toList();
        }
    }

    @Test
    void portedFileCount_matchesManifest() throws Exception {
        assertEquals(EXPECTED_PORT_COUNT, allJsonFiles().size(),
                "expected exactly " + EXPECTED_PORT_COUNT + " ported NeoEnchant+ enchant files");
    }

    @Test
    void cutFiles_areAbsent() throws Exception {
        Path root = resourceDir();
        List<String> offenders = new ArrayList<>();
        for (String cut : CUT_NAMES) {
            if (Files.exists(root.resolve(cut))) offenders.add(cut);
        }
        assertTrue(offenders.isEmpty(), "cut files should not be present: " + offenders);
    }

    @Test
    void noFile_containsEnchantplusNamespaceLiteral() throws Exception {
        List<String> offenders = new ArrayList<>();
        for (Path file : allJsonFiles()) {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            if (content.contains("\"enchantplus:") || content.contains("#enchantplus:")) {
                offenders.add(file.getFileName().toString());
            }
        }
        assertTrue(offenders.isEmpty(), "leftover enchantplus: references in: " + offenders);
    }

    @TestFactory
    Stream<DynamicTest> everyPortedFile_matchesEnchantmentSchema() throws Exception {
        Path root = resourceDir();
        return allJsonFiles().stream()
                .map(file -> DynamicTest.dynamicTest(
                        root.relativize(file).toString().replace('\\', '/'),
                        () -> assertValidEnchantmentShape(file)));
    }

    private static void assertValidEnchantmentShape(Path file) throws Exception {
        JsonElement element;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            element = JsonParser.parseReader(reader);
        }
        assertTrue(element.isJsonObject(), file.getFileName() + " must be a JSON object");
        JsonObject obj = element.getAsJsonObject();

        List<String> missing = new ArrayList<>();
        for (String field : REQUIRED_FIELDS) {
            if (!obj.has(field)) missing.add(field);
        }
        assertTrue(missing.isEmpty(),
                file.getFileName() + " missing required enchantment fields: " + missing);

        assertTrue(obj.get("effects").isJsonObject(),
                file.getFileName() + " effects must be an object keyed by component type");
        assertTrue(obj.get("slots").isJsonArray(),
                file.getFileName() + " slots must be an array of EquipmentSlotGroup ids");
        assertTrue(obj.get("weight").isJsonPrimitive() && obj.get("weight").getAsJsonPrimitive().isNumber(),
                file.getFileName() + " weight must be numeric");
        assertTrue(obj.get("max_level").isJsonPrimitive() && obj.get("max_level").getAsJsonPrimitive().isNumber(),
                file.getFileName() + " max_level must be numeric");
    }
}
