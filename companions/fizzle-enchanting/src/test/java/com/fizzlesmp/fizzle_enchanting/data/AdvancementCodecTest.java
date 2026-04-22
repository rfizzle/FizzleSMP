// Tier: 2 (fabric-loader-junit)
package com.fizzlesmp.fizzle_enchanting.data;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;

import net.minecraft.SharedConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.io.Reader;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-8.1.1/T-8.1.2 coverage: every advancement JSON shipped under
 * {@code data/fizzle_enchanting/advancement/} must round-trip through
 * {@link Advancement#CODEC} under a {@link RegistryOps} that has the mod's own items
 * registered (icons and inventory-changed predicates reference mod ids), and every advancement
 * must expose both a {@code .title} and {@code .description} entry in the shipped lang file.
 *
 * <p>Uses the prohibited-by-default {@code unfreeze} reflection because the codec check needs
 * the mod's items in {@link BuiltInRegistries#ITEM} — fabric-loader-junit does not run
 * {@code onInitialize}, and {@link Advancement#CODEC} fails fast when a predicate references
 * an unknown item id. This is the same pattern {@code FizzleRecipeProviderTest} uses and is
 * carried over to keep the Tier-2 advancement check out of gametest (which is where pure
 * data-validation tests would otherwise be forced).
 */
class AdvancementCodecTest {

    private static final String ADVANCEMENT_DIR = "/data/fizzle_enchanting/advancement/";
    private static final String LANG_FILE = "/assets/fizzle_enchanting/lang/en_us.json";
    private static final List<String> ADVANCEMENT_IDS = List.of(
            "root",
            "stone_tier",
            "tier_three",
            "library",
            "ender_library",
            "tome_apprentice",
            "tome_master",
            "warden_tendril",
            "infused_breath",
            "apotheosis");

    private static RegistryOps<JsonElement> registryOps;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreeze(BuiltInRegistries.MENU, false);
        unfreeze(BuiltInRegistries.BLOCK, true);
        unfreeze(BuiltInRegistries.ITEM, true);
        unfreeze(BuiltInRegistries.BLOCK_ENTITY_TYPE, true);

        FizzleEnchantingRegistry.register();

        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
        BuiltInRegistries.MENU.freeze();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();

        RegistryAccess.Frozen access = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        registryOps = RegistryOps.create(com.mojang.serialization.JsonOps.INSTANCE, access);
    }

    private static Path advancementDir() throws Exception {
        URL url = AdvancementCodecTest.class.getResource(ADVANCEMENT_DIR);
        assertNotNull(url, "advancement resource dir must be on the test classpath");
        return Paths.get(url.toURI());
    }

    private static List<Path> jsonFiles() throws Exception {
        try (Stream<Path> files = Files.list(advancementDir())) {
            return files.filter(p -> p.getFileName().toString().endsWith(".json")).sorted().toList();
        }
    }

    @Test
    void advancementDirectoryShipsExpectedRoster() throws Exception {
        List<String> shipped = jsonFiles().stream()
                .map(p -> p.getFileName().toString().replace(".json", ""))
                .sorted()
                .toList();
        List<String> expected = ADVANCEMENT_IDS.stream().sorted().toList();
        assertEquals(expected, shipped,
                "advancement directory must ship exactly the MVP roster — no extras, none missing");
    }

    @TestFactory
    Stream<DynamicTest> everyShippedAdvancement_parsesThroughAdvancementCodec() throws Exception {
        return jsonFiles().stream().map(file -> DynamicTest.dynamicTest(file.getFileName().toString(), () -> {
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                JsonElement json = JsonParser.parseReader(reader);
                DataResult<Advancement> result = Advancement.CODEC.parse(registryOps, json);
                assertTrue(result.error().isEmpty(),
                        "Advancement.CODEC parse failed for " + file.getFileName() + ": "
                                + result.error().map(DataResult.Error::message).orElse(""));
                Advancement adv = result.result().orElseThrow();
                assertFalse(adv.criteria().isEmpty(),
                        "advancement " + file.getFileName() + " must declare at least one criterion");
                assertTrue(adv.display().isPresent(),
                        "advancement " + file.getFileName() + " must carry a display block");
            }
        }));
    }

    @Test
    void everyShippedAdvancement_hasTitleAndDescriptionLangKeys() throws Exception {
        URL langUrl = AdvancementCodecTest.class.getResource(LANG_FILE);
        assertNotNull(langUrl, "lang file must be on the test classpath");
        JsonObject lang;
        try (Reader reader = Files.newBufferedReader(Paths.get(langUrl.toURI()), StandardCharsets.UTF_8)) {
            lang = JsonParser.parseReader(reader).getAsJsonObject();
        }

        for (String id : ADVANCEMENT_IDS) {
            String titleKey = "advancements.fizzle_enchanting." + id + ".title";
            String descKey = "advancements.fizzle_enchanting." + id + ".description";
            assertTrue(lang.has(titleKey), "lang must carry " + titleKey);
            assertTrue(lang.has(descKey), "lang must carry " + descKey);
            assertFalse(lang.get(titleKey).getAsString().isBlank(),
                    titleKey + " must be non-blank");
            assertFalse(lang.get(descKey).getAsString().isBlank(),
                    descKey + " must be non-blank");
        }
    }

    private static void unfreeze(Registry<?> registry, boolean intrusiveHolders) throws Exception {
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(registry, false);

        if (intrusiveHolders) {
            Field intrusive = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
            intrusive.setAccessible(true);
            if (intrusive.get(registry) == null) {
                intrusive.set(registry, new IdentityHashMap<>());
            }
        }
    }
}
