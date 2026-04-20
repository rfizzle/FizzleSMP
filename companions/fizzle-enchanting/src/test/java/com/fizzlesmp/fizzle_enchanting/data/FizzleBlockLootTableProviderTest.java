package com.fizzlesmp.fizzle_enchanting.data;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchantingRegistry;

import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * T-3.4.2 — pins the loot-table shape the provider emits. Every block in
 * {@link FizzleEnchantingRegistry#BLOCKS} must produce a single-pool table whose only entry is
 * the block itself, matching vanilla's {@code dropSelf} layout. Drift here would silently change
 * what players get back after mining a shelf.
 */
class FizzleBlockLootTableProviderTest {

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
    }

    @Test
    void everyRegisteredBlock_getsDropSelfTable() {
        Map<ResourceKey<LootTable>, LootTable.Builder> collected = runProvider();

        assertEquals(FizzleEnchantingRegistry.BLOCKS.size(), collected.size(),
                "one loot table per registered block");

        for (Block block : FizzleEnchantingRegistry.BLOCKS.values()) {
            ResourceKey<LootTable> key = block.getLootTable();
            LootTable.Builder builder = collected.get(key);
            assertNotNull(builder, () -> "missing loot table for " + block);

            List<LootPool> pools = pools(builder.build());
            assertEquals(1, pools.size(),
                    () -> "block " + block + " should produce exactly one pool");

            List<LootPoolEntryContainer> entries = entries(pools.get(0));
            assertEquals(1, entries.size(),
                    () -> "pool for " + block + " should hold exactly one entry");

            LootPoolEntryContainer entry = entries.get(0);
            LootItem lootItem = assertInstanceOf(LootItem.class, entry,
                    () -> "entry for " + block + " should be a plain LootItem");

            Item expected = block.asItem();
            assertNotNull(expected,
                    () -> "block " + block + " must have a BlockItem so dropSelf yields it");
            assertEquals(expected, itemOf(lootItem).value(),
                    () -> "pool entry for " + block + " should be the block itself");
        }
    }

    private static Map<ResourceKey<LootTable>, LootTable.Builder> runProvider() {
        FabricDataOutput output = new FabricDataOutput(null, Paths.get("."), false);
        FizzleBlockLootTableProvider provider = new FizzleBlockLootTableProvider(
                output, CompletableFuture.completedFuture(null));

        Map<ResourceKey<LootTable>, LootTable.Builder> collected = new LinkedHashMap<>();
        provider.generate(collected::put);
        return collected;
    }

    @SuppressWarnings("unchecked")
    private static List<LootPool> pools(LootTable table) {
        try {
            Field field = LootTable.class.getDeclaredField("pools");
            field.setAccessible(true);
            return (List<LootPool>) field.get(table);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static List<LootPoolEntryContainer> entries(LootPool pool) {
        try {
            Field field = LootPool.class.getDeclaredField("entries");
            field.setAccessible(true);
            return (List<LootPoolEntryContainer>) field.get(pool);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Holder<Item> itemOf(LootItem entry) {
        try {
            Field field = LootItem.class.getDeclaredField("item");
            field.setAccessible(true);
            return (Holder<Item>) field.get(entry);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
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
