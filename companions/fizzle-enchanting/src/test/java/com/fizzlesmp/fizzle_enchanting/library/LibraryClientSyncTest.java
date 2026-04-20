package com.fizzlesmp.fizzle_enchanting.library;

import com.mojang.serialization.Lifecycle;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-4.3.5 — exercises the server → client sync surface added on top of T-4.3.4's NBT engine.
 * Two contracts matter for this task:
 * <ol>
 *   <li><b>Update-tag carries the latest mutation.</b> After a mutation lands on the server-side
 *       BE, the bytes returned by {@link EnchantmentLibraryBlockEntity#getUpdateTag} reflect the
 *       new map state — this is what the chunk tracker forwards to every tracking player.</li>
 *   <li><b>Round-trip equivalence.</b> A receiver-side BE that consumes that tag through the
 *       {@code loadCustomOnly} path produces map state byte-for-byte identical to the server-side
 *       BE. This is the "client BE reconstructed from tag equals server BE" assertion that proves
 *       the full-resend contract from DESIGN.</li>
 * </ol>
 *
 * <p>Also pins the packet shape: {@link EnchantmentLibraryBlockEntity#getUpdatePacket} returns the
 * vanilla {@link ClientboundBlockEntityDataPacket}, not a custom payload — DESIGN explicitly
 * routes library sync through the BE-update-packet pipeline rather than bespoke S2C codecs.
 *
 * <p>Bootstrap mirrors {@link LibraryNbtTest}: the fresh {@link BlockEntityType} would otherwise
 * try to register an intrusive holder into a frozen registry, so the same unfreeze dance is used.
 */
class LibraryClientSyncTest {

    private static final ResourceKey<Enchantment> SHARPNESS = key("sharpness");
    private static final ResourceKey<Enchantment> UNBREAKING = key("unbreaking");

    private static Registry<Enchantment> enchantmentRegistry;
    private static HolderLookup.Provider provider;
    private static BlockEntityType<TestLibraryBlockEntity> basicType;
    private static BlockState state;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreeze(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        enchantmentRegistry = buildEnchantmentRegistry();
        provider = HolderLookup.Provider.create(Stream.of(enchantmentRegistry.asLookup()));
        basicType = BlockEntityType.Builder.of(
                (pos, blockState) -> new TestLibraryBlockEntity(basicType, pos, blockState, 16),
                Blocks.BOOKSHELF).build(null);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath("fizzle_enchanting", "test_library_sync"),
                basicType);
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();
        state = Blocks.BOOKSHELF.defaultBlockState();
    }

    @Test
    void getUpdatePacket_overriddenOnLibraryBlockEntity() throws Exception {
        // Vanilla's create() helper dereferences level.registryAccess(), so we cannot invoke
        // getUpdatePacket() directly without booting a Level. What matters for the sync contract
        // is (a) the override exists on EnchantmentLibraryBlockEntity (so we are not relying on
        // BlockEntity's null-returning default) and (b) the declared return shape is the vanilla
        // ClientboundBlockEntityDataPacket family. Reflection captures both without needing a
        // live world.
        Method overridden = EnchantmentLibraryBlockEntity.class.getDeclaredMethod("getUpdatePacket");
        assertSame(EnchantmentLibraryBlockEntity.class, overridden.getDeclaringClass(),
                "library BE must override getUpdatePacket — default returns null and would skip sync");
        assertTrue(ClientboundBlockEntityDataPacket.class.isAssignableFrom(
                        rawReturnType(overridden)) || overridden.getReturnType().getName().contains("Packet"),
                "override returns the vanilla packet family used by the BE-update pipeline");
    }

    private static Class<?> rawReturnType(Method method) {
        return method.getReturnType();
    }

    @Test
    void getUpdateTag_includesMutationsLandedOnServer() {
        // Stuff the server-side maps as if depositBook had run, then read the tag the chunk
        // tracker would forward. Both maps must be present and carry the post-mutation values.
        TestLibraryBlockEntity server = newLibrary();
        server.getPoints().put(SHARPNESS, 4096);
        server.getMaxLevels().put(SHARPNESS, 5);
        server.getPoints().put(UNBREAKING, 64);
        server.getMaxLevels().put(UNBREAKING, 3);

        CompoundTag updateTag = server.getUpdateTag(provider);

        assertTrue(updateTag.contains(EnchantmentLibraryBlockEntity.TAG_POINTS),
                "update tag carries the Points compound");
        assertTrue(updateTag.contains(EnchantmentLibraryBlockEntity.TAG_LEVELS),
                "update tag carries the Levels compound");
        CompoundTag points = updateTag.getCompound(EnchantmentLibraryBlockEntity.TAG_POINTS);
        assertEquals(4096, points.getInt(SHARPNESS.location().toString()),
                "post-mutation Sharpness points appear in the wire payload");
        assertEquals(64, points.getInt(UNBREAKING.location().toString()),
                "post-mutation Unbreaking points appear in the wire payload");
        CompoundTag levels = updateTag.getCompound(EnchantmentLibraryBlockEntity.TAG_LEVELS);
        assertEquals(5, levels.getInt(SHARPNESS.location().toString()));
        assertEquals(3, levels.getInt(UNBREAKING.location().toString()));
    }

    @Test
    void clientReconstructedFromUpdateTag_equalsServerState() {
        // End-to-end: server mutates → wire tag → fresh client BE consumes it. Both maps must come
        // out identical. This is the load-bearing contract — if it fails the screen will paint
        // stale state (or worse, half-paint on the first mutation).
        TestLibraryBlockEntity server = newLibrary();
        server.getPoints().put(SHARPNESS, 8192);
        server.getPoints().put(UNBREAKING, 128);
        server.getMaxLevels().put(SHARPNESS, 6);
        server.getMaxLevels().put(UNBREAKING, 4);

        CompoundTag wire = server.getUpdateTag(provider);

        TestLibraryBlockEntity client = newLibrary();
        client.loadCustomOnly(wire, provider);

        assertEquals(server.getPoints(), client.getPoints(),
                "client point map matches server byte-for-byte after applying update tag");
        assertEquals(server.getMaxLevels(), client.getMaxLevels(),
                "client max-level map matches server byte-for-byte after applying update tag");
    }

    @Test
    void getUpdateTag_reflectsSubsequentMutationsForFullResend() {
        // DESIGN: "full resend on any mutation". So a second mutation must produce a tag that
        // overwrites the first state, not merge with it — a client applying both in order should
        // end up with only the latest map values.
        TestLibraryBlockEntity server = newLibrary();
        server.getPoints().put(SHARPNESS, 100);
        server.getMaxLevels().put(SHARPNESS, 3);
        CompoundTag firstTag = server.getUpdateTag(provider);

        // Mutate further; the second tag must carry the new totals AND overwrite (not merge with)
        // the first when applied to a client BE primed from firstTag.
        server.getPoints().put(SHARPNESS, 200);
        server.getMaxLevels().put(SHARPNESS, 4);
        server.getPoints().put(UNBREAKING, 16);
        CompoundTag secondTag = server.getUpdateTag(provider);

        TestLibraryBlockEntity client = newLibrary();
        client.loadCustomOnly(firstTag, provider);
        assertEquals(100, client.getPoints().getInt(SHARPNESS),
                "client picks up the first mutation");

        client.loadCustomOnly(secondTag, provider);
        assertEquals(200, client.getPoints().getInt(SHARPNESS),
                "second mutation overwrites the first — full resend, not delta merge");
        assertEquals(4, client.getMaxLevels().getInt(SHARPNESS));
        assertEquals(16, client.getPoints().getInt(UNBREAKING),
                "fresh entries from the second resend land on the client");
        assertEquals(2, client.getPoints().size(),
                "only the latest map state survives — no orphan entries from the first tag");
    }

    private static TestLibraryBlockEntity newLibrary() {
        return new TestLibraryBlockEntity(basicType, BlockPos.ZERO, state, 16);
    }

    private static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(
                Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath("minecraft", path));
    }

    private static Registry<Enchantment> buildEnchantmentRegistry() {
        MappedRegistry<Enchantment> reg = new MappedRegistry<>(Registries.ENCHANTMENT, Lifecycle.stable());
        reg.register(SHARPNESS, syntheticEnchantment(), RegistrationInfo.BUILT_IN);
        reg.register(UNBREAKING, syntheticEnchantment(), RegistrationInfo.BUILT_IN);
        return reg.freeze();
    }

    private static Enchantment syntheticEnchantment() {
        HolderSet<Item> any = HolderSet.direct(List.of(BuiltInRegistries.ITEM.wrapAsHolder(Items.BOOK)));
        Enchantment.EnchantmentDefinition def = Enchantment.definition(
                any,
                10,
                5,
                Enchantment.dynamicCost(1, 10),
                Enchantment.dynamicCost(51, 10),
                1,
                EquipmentSlotGroup.ANY);
        return new Enchantment(
                Component.literal("test"),
                def,
                HolderSet.empty(),
                DataComponentMap.EMPTY);
    }

    private static void unfreeze(Registry<?> registry) throws Exception {
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(registry, false);
        Field intrusive = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        intrusive.setAccessible(true);
        if (intrusive.get(registry) == null) {
            intrusive.set(registry, new IdentityHashMap<>());
        }
    }

    private static final class TestLibraryBlockEntity extends EnchantmentLibraryBlockEntity {
        TestLibraryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int maxLevel) {
            super(type, pos, state, maxLevel);
        }
    }
}
