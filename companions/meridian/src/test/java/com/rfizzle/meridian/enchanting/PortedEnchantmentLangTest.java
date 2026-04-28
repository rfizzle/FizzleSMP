package com.rfizzle.meridian.enchanting;

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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-6.1.4 coverage: the English lang file must parse as a flat string→string JSON object, must
 * carry no leftover {@code enchantment.enchantplus.*} keys, and must expose a name entry for every
 * translate key referenced by a ported enchant JSON under
 * {@code data/meridian/enchantment/}.
 */
class PortedEnchantmentLangTest {

    private static final String LANG_RESOURCE = "/assets/meridian/lang/en_us.json";
    private static final String ENCHANT_DIR = "/data/meridian/enchantment/";

    private static Path resource(String path) throws Exception {
        URL url = PortedEnchantmentLangTest.class.getResource(path);
        assertNotNull(url, "resource must be on the test classpath: " + path);
        return Paths.get(url.toURI());
    }

    private static JsonObject loadLang() throws Exception {
        try (Reader reader = Files.newBufferedReader(resource(LANG_RESOURCE), StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            assertTrue(element.isJsonObject(), "lang file must be a JSON object");
            return element.getAsJsonObject();
        }
    }

    private static List<Path> enchantmentJsonFiles() throws Exception {
        try (Stream<Path> files = Files.walk(resource(ENCHANT_DIR))) {
            return files.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(".json"))
                    .sorted()
                    .toList();
        }
    }

    @Test
    void langFile_parses() throws Exception {
        JsonObject obj = loadLang();
        assertFalse(obj.entrySet().isEmpty(), "lang file must contain entries");
        for (var entry : obj.entrySet()) {
            assertTrue(entry.getValue().isJsonPrimitive()
                            && entry.getValue().getAsJsonPrimitive().isString(),
                    "lang entry must be a string: " + entry.getKey());
        }
    }

    @Test
    void langFile_hasNoEnchantplusKeys() throws Exception {
        JsonObject obj = loadLang();
        List<String> leftovers = new ArrayList<>();
        for (String key : obj.keySet()) {
            if (key.startsWith("enchantment.enchantplus.")) leftovers.add(key);
        }
        assertTrue(leftovers.isEmpty(), "leftover enchantplus lang keys: " + leftovers);
    }

    @Test
    void langFile_dropsCutEnchantKeys() throws Exception {
        JsonObject obj = loadLang();
        List<String> cutSegments = List.of(
                "axe/timber",
                "pickaxe/bedrock_breaker",
                "pickaxe/spawner_touch",
                "tools/auto_smelt",
                "helmet/auto_feed",
                "chestplate/magnet",
                "sword/runic_despair");
        List<String> offenders = new ArrayList<>();
        for (String key : obj.keySet()) {
            for (String seg : cutSegments) {
                if (key.contains(seg)) {
                    offenders.add(key);
                    break;
                }
            }
        }
        assertTrue(offenders.isEmpty(), "cut-enchant lang keys should be dropped: " + offenders);
    }

    @TestFactory
    Stream<DynamicTest> everyPortedEnchantment_hasMatchingLangKey() throws Exception {
        JsonObject lang = loadLang();
        Path root = resource(ENCHANT_DIR);
        return enchantmentJsonFiles().stream()
                .map(file -> DynamicTest.dynamicTest(
                        root.relativize(file).toString().replace('\\', '/'),
                        () -> assertLangKeyForEnchantment(file, lang)));
    }

    private static void assertLangKeyForEnchantment(Path file, JsonObject lang) throws Exception {
        JsonObject obj;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            obj = JsonParser.parseReader(reader).getAsJsonObject();
        }
        JsonElement description = obj.get("description");
        assertNotNull(description, file.getFileName() + " missing description");
        assertTrue(description.isJsonObject(),
                file.getFileName() + " description must be a translatable component");
        String translateKey = description.getAsJsonObject().get("translate").getAsString();
        assertTrue(lang.has(translateKey),
                file.getFileName() + " references missing lang key: " + translateKey);
    }
}
