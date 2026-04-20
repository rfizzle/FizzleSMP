package com.fizzlesmp.fizzle_enchanting.anvil;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
import static org.junit.jupiter.api.Assertions.fail;

/**
 * T-4.1.3 — proves the Prismatic Web item lands in {@link BuiltInRegistries#ITEM} under the
 * expected id and that the hand-shipped recipe JSON parses into the 1.21.1 shape the data loader
 * expects. Follows the unfreeze/refreeze pattern from {@code FizzleEnchantingRegistryTest} because
 * {@link Bootstrap#bootStrap()} freezes the vanilla registries before our registration helpers
 * would normally fire.
 */
class PrismaticWebItemTest {

    private static final String RECIPE_PATH =
            "/data/fizzle_enchanting/recipe/prismatic_web.json";

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
    void prismaticWebRegistered_resolvesFromItemRegistry() {
        ResourceLocation id = FizzleEnchanting.id("prismatic_web");
        Item resolved = BuiltInRegistries.ITEM.get(id);
        assertNotNull(resolved, "Prismatic Web must be registered under fizzle_enchanting:prismatic_web");
        assertSame(FizzleEnchantingRegistry.PRISMATIC_WEB, resolved,
                "registry entry must be the exact instance exposed on FizzleEnchantingRegistry — "
                        + "PrismaticWebHandler will key off identity");
        assertInstanceOf(PrismaticWebItem.class, resolved,
                "registered item must be the dedicated PrismaticWebItem subclass");
    }

    @Test
    void recipeJson_parsesAsShapedWithExpectedPatternAndResult() throws Exception {
        JsonObject recipe = loadRecipe();

        assertEquals("minecraft:crafting_shaped", recipe.get("type").getAsString(),
                "prismatic_web must ship as a vanilla shaped recipe so no custom recipe type is needed");

        var pattern = recipe.getAsJsonArray("pattern");
        assertEquals(3, pattern.size(), "pattern is 3x3");
        assertEquals(" P ", pattern.get(0).getAsString());
        assertEquals("PSP", pattern.get(1).getAsString());
        assertEquals(" P ", pattern.get(2).getAsString());

        JsonObject key = recipe.getAsJsonObject("key");
        assertEquals("minecraft:prismarine_shard",
                key.getAsJsonObject("P").get("item").getAsString(),
                "P must resolve to prismarine shards (Zenith parity)");
        assertEquals("minecraft:string",
                key.getAsJsonObject("S").get("item").getAsString(),
                "S must resolve to string (Zenith parity)");

        JsonObject result = recipe.getAsJsonObject("result");
        // 1.21.1 renamed the field from "item" to "id" in recipe results — hand-shipped JSON
        // must match the new shape or it won't parse under the data loader.
        assertTrue(result.has("id"),
                "result must use the 1.21.1 `id` field (not legacy `item`) or the data loader rejects it");
        assertEquals("fizzle_enchanting:prismatic_web", result.get("id").getAsString(),
                "result id must point at our registered item");
        assertEquals(1, result.get("count").getAsInt(), "recipe yields one web per craft");
    }

    private static JsonObject loadRecipe() throws Exception {
        try (InputStream in = PrismaticWebItemTest.class.getResourceAsStream(RECIPE_PATH)) {
            if (in == null) {
                fail("recipe JSON missing at " + RECIPE_PATH + " — hand-shipped resource should be on the classpath");
            }
            try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                return JsonParser.parseReader(reader).getAsJsonObject();
            }
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
