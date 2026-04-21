package com.fizzlesmp.fizzle_enchanting.item;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
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

/**
 * T-5.4.1 — proves the {@code infused_breath} specialty material registers under its expected id,
 * carries Zenith-parity {@link Rarity#EPIC} rarity, ships its animated texture + mcmeta + item
 * model on the classpath, and the table-crafting recipe that produces it resolves to this item's
 * registry key (not a stale stub).
 *
 * <p>The recipe stat-window match is covered by {@code EnchantingRecipeRegistryTest}; this test
 * only needs to prove the {@code result.id} in the hand-shipped JSON is our registered item and
 * the stack size matches DESIGN (3 per craft).
 */
class InfusedBreathItemTest {

    private static final String RECIPE_PATH =
            "/data/fizzle_enchanting/recipe/enchanting/infused_breath.json";
    private static final String TEXTURE_PATH =
            "/assets/fizzle_enchanting/textures/item/infused_breath.png";
    private static final String MCMETA_PATH =
            "/assets/fizzle_enchanting/textures/item/infused_breath.png.mcmeta";
    private static final String MODEL_PATH =
            "/assets/fizzle_enchanting/models/item/infused_breath.json";
    private static final String LANG_PATH =
            "/assets/fizzle_enchanting/lang/en_us.json";

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
    void infusedBreath_resolvesFromItemRegistry() {
        Item resolved = BuiltInRegistries.ITEM.get(FizzleEnchanting.id("infused_breath"));
        assertNotNull(resolved, "InfusedBreathItem must be registered under fizzle_enchanting:infused_breath");
        assertSame(FizzleEnchantingRegistry.INFUSED_BREATH, resolved,
                "registry entry must be the exact instance exposed on FizzleEnchantingRegistry");
        assertInstanceOf(InfusedBreathItem.class, resolved);
    }

    @Test
    void infusedBreath_isEpicRarity() {
        // Epic rarity mirrors Zenith's presentation — the purple name distinguishes the material
        // from bulk ingredients at a glance when it drops into the player's inventory after a craft.
        ItemStack stack = new ItemStack(FizzleEnchantingRegistry.INFUSED_BREATH);
        assertEquals(Rarity.EPIC, stack.getRarity(),
                "infused_breath must render at Rarity.EPIC to match Zenith parity");
    }

    @Test
    void textureAndMcmeta_existOnClasspath() throws Exception {
        try (InputStream texture = getClass().getResourceAsStream(TEXTURE_PATH)) {
            assertNotNull(texture, "infused_breath.png missing at " + TEXTURE_PATH);
        }
        try (InputStream mcmeta = getClass().getResourceAsStream(MCMETA_PATH)) {
            assertNotNull(mcmeta, "infused_breath.png.mcmeta missing at " + MCMETA_PATH
                    + " — animation frames will not play without the mcmeta");
        }
    }

    @Test
    void itemModel_parentsGeneratedAndPointsAtInfusedBreathTexture() throws Exception {
        try (InputStream in = getClass().getResourceAsStream(MODEL_PATH)) {
            assertNotNull(in, "model JSON missing at " + MODEL_PATH);
            JsonObject model = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            assertEquals("minecraft:item/generated", model.get("parent").getAsString(),
                    "infused_breath is a flat 2D item, must parent minecraft:item/generated");
            assertEquals("fizzle_enchanting:item/infused_breath",
                    model.getAsJsonObject("textures").get("layer0").getAsString(),
                    "model layer0 must point at the top-level item/ texture, not a subfolder");
        }
    }

    @Test
    void langKey_present() throws Exception {
        try (InputStream in = getClass().getResourceAsStream(LANG_PATH)) {
            assertNotNull(in, "en_us.json must be on the test classpath");
            JsonObject lang = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            assertTrue(lang.has("item.fizzle_enchanting.infused_breath"),
                    "lang file must declare item.fizzle_enchanting.infused_breath so the inventory UI doesn't show the raw id");
            assertEquals("Infused Breath",
                    lang.get("item.fizzle_enchanting.infused_breath").getAsString());
        }
    }

    @Test
    void tableCraftingRecipe_producesThisItem() throws Exception {
        // The item is only obtainable via the fizzle_enchanting:enchanting recipe that consumes
        // dragon_breath. Proving the hand-shipped JSON references our registered result id (and
        // yields the expected 3-count from DESIGN) pins the progression contract — if the recipe
        // drifts, the item becomes unobtainable without a config/dev-mode workaround.
        try (InputStream in = getClass().getResourceAsStream(RECIPE_PATH)) {
            assertNotNull(in, "recipe JSON missing at " + RECIPE_PATH);
            JsonObject recipe = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                    .getAsJsonObject();
            assertEquals("fizzle_enchanting:enchanting", recipe.get("type").getAsString(),
                    "infused_breath recipe must stay on the custom enchanting type — vanilla grid crafts would bypass the stat gate");
            assertEquals("minecraft:dragon_breath",
                    recipe.getAsJsonObject("input").get("item").getAsString(),
                    "recipe input must be dragon_breath (Zenith parity)");
            JsonObject result = recipe.getAsJsonObject("result");
            assertEquals("fizzle_enchanting:infused_breath", result.get("id").getAsString(),
                    "recipe result must reference our registered item id, not a stale or missing key");
            assertEquals(3, result.get("count").getAsInt(),
                    "Zenith parity: 3 infused_breath per dragon_breath consumed");
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
