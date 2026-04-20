package com.fizzlesmp.fizzle_enchanting;

import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-2.5.5 / T-3.1.3 — proves both the enchanting-table {@link MenuType} and the
 * {@code registerBlock}/{@code registerItem} helpers land entries in the vanilla registries.
 * The server emits menu types inside {@code ClientboundOpenScreenPacket}, so a missing
 * registration means the client can't reconstruct the HUD subclass.
 *
 * <p>Pure-vanilla tests (no fabric-loader-junit on the classpath — matches fizzle-difficulty)
 * need {@link Bootstrap#bootStrap()} to initialize the vanilla menu/block/item types, but that
 * same call freezes {@link BuiltInRegistries}. Fabric's mixin defers the freeze in production
 * until after {@code ModInitializer#onInitialize}; we replicate that deferral here by reflecting
 * the {@code frozen} flag back to {@code false} between bootstrap and registration.
 */
class FizzleEnchantingRegistryTest {

    private static final String DUMMY_BLOCK_PATH = "test_dummy_block";
    private static final String DUMMY_ITEM_PATH = "test_dummy_item";

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreeze(BuiltInRegistries.MENU, false);
        unfreeze(BuiltInRegistries.BLOCK, true);
        unfreeze(BuiltInRegistries.ITEM, true);
        unfreeze(BuiltInRegistries.BLOCK_ENTITY_TYPE, true);

        FizzleEnchantingRegistry.register();
        FizzleEnchantingRegistry.registerBlock(
                DUMMY_BLOCK_PATH,
                new Block(BlockBehaviour.Properties.of()),
                new Item.Properties());
        FizzleEnchantingRegistry.registerItem(DUMMY_ITEM_PATH, new Item(new Item.Properties()));

        // Re-freezing walks every registered entry and binds its Holder.Reference to its value.
        // Without this, freshly-added entries stay unbound and .get() throws "Trying to access
        // unbound value" — even though the key is in the map.
        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
        BuiltInRegistries.MENU.freeze();
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();
    }

    @Test
    void register_menuTypeResolvesFromBuiltInRegistry() {
        ResourceLocation id = FizzleEnchanting.id("enchanting_table");
        MenuType<?> resolved = BuiltInRegistries.MENU.get(id);
        assertNotNull(resolved, "MenuType must be present in BuiltInRegistries.MENU after register()");
        assertSame(FizzleEnchantingRegistry.ENCHANTING_TABLE_MENU, resolved,
                "registry lookup must return the exact instance exposed on FizzleEnchantingRegistry");
    }

    @Test
    void register_isIdempotent() {
        // Second call must not re-register — the guard flag makes onInitialize safe to invoke
        // twice and lets tests share a bootstrap across classes.
        FizzleEnchantingRegistry.register();
        MenuType<?> resolved = BuiltInRegistries.MENU.get(FizzleEnchanting.id("enchanting_table"));
        assertSame(FizzleEnchantingRegistry.ENCHANTING_TABLE_MENU, resolved);
    }

    @Test
    void registerBlock_addsBlockAndBlockItemToBuiltInRegistries() {
        ResourceLocation id = FizzleEnchanting.id(DUMMY_BLOCK_PATH);

        Block resolvedBlock = BuiltInRegistries.BLOCK.get(id);
        assertNotNull(resolvedBlock, "registerBlock must populate BuiltInRegistries.BLOCK");

        Item resolvedItem = BuiltInRegistries.ITEM.get(id);
        assertNotNull(resolvedItem, "registerBlock must also register a companion BlockItem");
        assertInstanceOf(BlockItem.class, resolvedItem,
                "companion item registered under the block id must be a BlockItem");
    }

    @Test
    void registerBlock_tracksInsertionOrderedBlocksMap() {
        ResourceLocation id = FizzleEnchanting.id(DUMMY_BLOCK_PATH);
        assertTrue(FizzleEnchantingRegistry.BLOCKS.containsKey(id),
                "BLOCKS must hold the block registered through registerBlock");
        assertSame(BuiltInRegistries.BLOCK.get(id), FizzleEnchantingRegistry.BLOCKS.get(id),
                "BLOCKS entry must point at the same instance that lives in BuiltInRegistries.BLOCK");
    }

    @Test
    void registerItem_addsStandaloneItemToBuiltInRegistry() {
        ResourceLocation id = FizzleEnchanting.id(DUMMY_ITEM_PATH);
        Item resolved = BuiltInRegistries.ITEM.get(id);
        assertNotNull(resolved, "registerItem must populate BuiltInRegistries.ITEM");
    }

    @Test
    void registerMenuType_addsProvidedTypeToMenuRegistry() throws Exception {
        MenuType<?> type = new MenuType<>((syncId, inv) -> null, FeatureFlags.VANILLA_SET);
        unfreeze(BuiltInRegistries.MENU, false);
        try {
            FizzleEnchantingRegistry.registerMenuType("test_dummy_menu", type);
        } finally {
            BuiltInRegistries.MENU.freeze();
        }
        assertSame(type, BuiltInRegistries.MENU.get(FizzleEnchanting.id("test_dummy_menu")));
    }

    /**
     * Vanilla {@link MappedRegistry#freeze()} both sets the {@code frozen} flag and nulls out
     * {@code unregisteredIntrusiveHolders} for registries that use intrusive holders (BLOCK, ITEM,
     * BLOCK_ENTITY_TYPE). Constructing a fresh {@link Block} or {@link Item} after bootstrap calls
     * {@code createIntrusiveHolder}, which throws "This registry can't create intrusive holders"
     * when that map is null. So for those registries we restore the map as well as flipping the
     * {@code frozen} flag. Registries without intrusive holders (MENU) must stay with a null map —
     * otherwise {@code register} trips on "Missing intrusive holder" because the value was never
     * inserted via {@code createIntrusiveHolder}.
     */
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
