package com.fizzlesmp.fizzle_enchanting.tome;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * T-5.1.1 — proves the three tome items land in {@link BuiltInRegistries#ITEM} under their
 * expected ids, resolve to the exact class instances exposed on
 * {@link FizzleEnchantingRegistry}, and stack to 1 (the anvil interaction is "one tome per
 * use", and a larger stack cap would let players misread slot counts as salvages).
 *
 * <p>Follows the unfreeze/refreeze pattern from {@code FizzleEnchantingRegistryTest} because
 * {@link Bootstrap#bootStrap()} freezes the vanilla registries before our registration helpers
 * would normally fire in production.
 */
class TomeItemsTest {

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
    void scrapTome_resolvesFromItemRegistry() {
        Item resolved = BuiltInRegistries.ITEM.get(FizzleEnchanting.id("scrap_tome"));
        assertNotNull(resolved, "ScrapTomeItem must be registered under fizzle_enchanting:scrap_tome");
        assertSame(FizzleEnchantingRegistry.SCRAP_TOME, resolved,
                "registry entry must be the exact instance exposed on FizzleEnchantingRegistry — "
                        + "the scrap tome handler will key off identity");
        assertInstanceOf(ScrapTomeItem.class, resolved);
    }

    @Test
    void improvedScrapTome_resolvesFromItemRegistry() {
        Item resolved = BuiltInRegistries.ITEM.get(FizzleEnchanting.id("improved_scrap_tome"));
        assertNotNull(resolved, "ImprovedScrapTomeItem must be registered under fizzle_enchanting:improved_scrap_tome");
        assertSame(FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME, resolved);
        assertInstanceOf(ImprovedScrapTomeItem.class, resolved);
    }

    @Test
    void extractionTome_resolvesFromItemRegistry() {
        Item resolved = BuiltInRegistries.ITEM.get(FizzleEnchanting.id("extraction_tome"));
        assertNotNull(resolved, "ExtractionTomeItem must be registered under fizzle_enchanting:extraction_tome");
        assertSame(FizzleEnchantingRegistry.EXTRACTION_TOME, resolved);
        assertInstanceOf(ExtractionTomeItem.class, resolved);
    }

    @Test
    void allTomes_stackToOne() {
        assertEquals(1, FizzleEnchantingRegistry.SCRAP_TOME.getDefaultMaxStackSize(),
                "scrap tome must stack to 1 — the anvil takes one tome per salvage");
        assertEquals(1, FizzleEnchantingRegistry.IMPROVED_SCRAP_TOME.getDefaultMaxStackSize(),
                "improved scrap tome must stack to 1");
        assertEquals(1, FizzleEnchantingRegistry.EXTRACTION_TOME.getDefaultMaxStackSize(),
                "extraction tome must stack to 1");
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
