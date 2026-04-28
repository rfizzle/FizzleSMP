package com.rfizzle.meridian.enchanting;

import com.google.gson.JsonArray;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-6.1.3 coverage: every entry in the ported {@code exclusive_set} tag files must resolve to a
 * present enchant key — either a vanilla {@code minecraft:} id or a {@code meridian:} id
 * whose JSON file ships under {@code data/meridian/enchantment/}. Cut-enchant refs from
 * the original NeoEnchant+ tags must have been pruned, and no {@code enchantplus:} literal may
 * remain.
 *
 * <p>Tier-1 scope: filesystem + JSON-parse only. Tier-3 registry-membership verification is
 * TEST-6.1-T3 in TESTING-TODO.md.
 */
class PortedExclusiveSetTagsTest {

    private static final String TAGS_DIR = "/data/meridian/tags/enchantment/exclusive_set/";
    private static final String ENCHANT_DIR = "/data/meridian/enchantment/";

    /** The 7 NeoEnchant+ enchantments DESIGN.md explicitly cuts from the MVP roster. */
    private static final Set<String> CUT_IDS = Set.of(
            "meridian:axe/timber",
            "meridian:pickaxe/bedrock_breaker",
            "meridian:pickaxe/spawner_touch",
            "meridian:tools/auto_smelt",
            "meridian:helmet/auto_feed",
            "meridian:chestplate/magnet",
            "meridian:sword/runic_despair");

    private static Path tagsDir() throws Exception {
        URL url = PortedExclusiveSetTagsTest.class.getResource(TAGS_DIR);
        assertNotNull(url, "exclusive_set tag resource dir must be on the test classpath");
        return Paths.get(url.toURI());
    }

    private static Path enchantDir() throws Exception {
        URL url = PortedExclusiveSetTagsTest.class.getResource(ENCHANT_DIR);
        assertNotNull(url, "enchantment resource dir must be on the test classpath");
        return Paths.get(url.toURI());
    }

    private static List<Path> allTagFiles() throws Exception {
        try (Stream<Path> files = Files.walk(tagsDir())) {
            return files.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .sorted()
                    .toList();
        }
    }

    private static Set<String> allPortedEnchantIds() throws Exception {
        Path root = enchantDir();
        Set<String> ids = new LinkedHashSet<>();
        try (Stream<Path> files = Files.walk(root)) {
            files.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .forEach(p -> {
                        String rel = root.relativize(p).toString().replace('\\', '/');
                        String base = rel.substring(0, rel.length() - ".json".length());
                        ids.add("meridian:" + base);
                    });
        }
        return ids;
    }

    private static List<String> readValues(Path file) throws Exception {
        JsonElement element;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            element = JsonParser.parseReader(reader);
        }
        assertTrue(element.isJsonObject(), file.getFileName() + " must be a JSON object");
        JsonObject obj = element.getAsJsonObject();
        assertTrue(obj.has("values") && obj.get("values").isJsonArray(),
                file.getFileName() + " must have a \"values\" array");
        JsonArray arr = obj.getAsJsonArray("values");
        List<String> out = new ArrayList<>(arr.size());
        for (JsonElement e : arr) {
            if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
                out.add(e.getAsString());
            } else if (e.isJsonObject() && e.getAsJsonObject().has("id")) {
                out.add(e.getAsJsonObject().get("id").getAsString());
            } else {
                throw new AssertionError(file.getFileName() + " entry must be a string or {\"id\":...} object: " + e);
            }
        }
        return out;
    }

    @Test
    void tagDir_isNonEmpty() throws Exception {
        assertFalse(allTagFiles().isEmpty(),
                "expected ported exclusive_set tags under " + TAGS_DIR);
    }

    @Test
    void noFile_containsEnchantplusNamespaceLiteral() throws Exception {
        List<String> offenders = new ArrayList<>();
        for (Path file : allTagFiles()) {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            if (content.contains("enchantplus:") || content.contains("#enchantplus:")) {
                offenders.add(file.getFileName().toString());
            }
        }
        assertTrue(offenders.isEmpty(), "leftover enchantplus: references in: " + offenders);
    }

    @Test
    void noEntry_referencesCutEnchant() throws Exception {
        List<String> offenders = new ArrayList<>();
        for (Path file : allTagFiles()) {
            for (String entry : readValues(file)) {
                if (CUT_IDS.contains(entry)) {
                    offenders.add(file.getFileName() + " → " + entry);
                }
            }
        }
        assertTrue(offenders.isEmpty(), "cut-enchant references must not appear: " + offenders);
    }

    @Test
    void everyEntry_resolvesToPresentEnchant() throws Exception {
        Set<String> ported = allPortedEnchantIds();
        List<String> unresolved = new ArrayList<>();
        for (Path file : allTagFiles()) {
            for (String entry : readValues(file)) {
                if (entry.startsWith("minecraft:")) continue; // vanilla, trusted
                if (entry.startsWith("meridian:")) {
                    if (!ported.contains(entry)) {
                        unresolved.add(file.getFileName() + " → " + entry);
                    }
                    continue;
                }
                // Unknown namespace — reject so drift shows up loud.
                unresolved.add(file.getFileName() + " → " + entry + " (unknown namespace)");
            }
        }
        assertTrue(unresolved.isEmpty(),
                "exclusive_set entries must resolve to ported or vanilla enchants: " + unresolved);
    }

    @TestFactory
    Stream<DynamicTest> everyTagFile_parsesAndHasOnlyStringEntries() throws Exception {
        Path root = tagsDir();
        return allTagFiles().stream()
                .map(file -> DynamicTest.dynamicTest(
                        root.relativize(file).toString().replace('\\', '/'),
                        () -> readValues(file)));
    }
}
