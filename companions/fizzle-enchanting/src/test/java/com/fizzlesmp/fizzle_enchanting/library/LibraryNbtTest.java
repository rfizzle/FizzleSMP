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
import java.util.IdentityHashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-4.3.4 — exercises {@link EnchantmentLibraryBlockEntity}'s NBT read/write path. Two contracts
 * matter for this task:
 * <ol>
 *   <li><b>Round-trip preservation.</b> A fully populated pair of point/level maps survives a
 *       {@code saveCustomOnly} → {@code loadCustomOnly} pass with the same entries, same values,
 *       and nothing extra.</li>
 *   <li><b>Resolution-safe load.</b> A stale NBT key (one whose enchantment left the registry —
 *       simulated here by writing an id the test registry never registered) drops on load with
 *       the rest of the map intact. Matching behavior for a malformed id string. This is the
 *       protection against a "datapack uninstalled" user crash.</li>
 * </ol>
 *
 * <p>Shares the bootstrap/unfreeze dance with sibling library tests because
 * {@link BlockEntityType.Builder#build} registers an intrusive holder into a registry that vanilla
 * {@link Bootstrap} has already frozen.
 */
class LibraryNbtTest {

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
                ResourceLocation.fromNamespaceAndPath("fizzle_enchanting", "test_library_nbt"),
                basicType);
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();
        state = Blocks.BOOKSHELF.defaultBlockState();
    }

    @Test
    void roundTrip_preservesPointsAndMaxLevels() {
        TestLibraryBlockEntity saved = newLibrary();
        saved.getPoints().put(SHARPNESS, 4096);
        saved.getPoints().put(UNBREAKING, 64);
        saved.getMaxLevels().put(SHARPNESS, 5);
        saved.getMaxLevels().put(UNBREAKING, 3);

        CompoundTag tag = saved.saveCustomOnly(provider);

        TestLibraryBlockEntity reloaded = newLibrary();
        reloaded.loadCustomOnly(tag, provider);

        assertEquals(4096, reloaded.getPoints().getInt(SHARPNESS),
                "sharpness points survive the round-trip");
        assertEquals(64, reloaded.getPoints().getInt(UNBREAKING),
                "unbreaking points survive the round-trip");
        assertEquals(5, reloaded.getMaxLevels().getInt(SHARPNESS),
                "sharpness max-level survives the round-trip");
        assertEquals(3, reloaded.getMaxLevels().getInt(UNBREAKING),
                "unbreaking max-level survives the round-trip");
        assertEquals(2, reloaded.getPoints().size(), "no phantom entries added on reload");
        assertEquals(2, reloaded.getMaxLevels().size(), "no phantom entries added on reload");
    }

    @Test
    void save_writesExpectedSchemaShape() {
        TestLibraryBlockEntity be = newLibrary();
        be.getPoints().put(SHARPNESS, 128);
        be.getMaxLevels().put(SHARPNESS, 4);

        CompoundTag tag = be.saveCustomOnly(provider);

        assertTrue(tag.contains(EnchantmentLibraryBlockEntity.TAG_POINTS),
                "save writes the Points compound tag");
        assertTrue(tag.contains(EnchantmentLibraryBlockEntity.TAG_LEVELS),
                "save writes the Levels compound tag");
        CompoundTag points = tag.getCompound(EnchantmentLibraryBlockEntity.TAG_POINTS);
        assertEquals(128, points.getInt(SHARPNESS.location().toString()),
                "keys are serialized via ResourceLocation#toString()");
        CompoundTag levels = tag.getCompound(EnchantmentLibraryBlockEntity.TAG_LEVELS);
        assertEquals(4, levels.getInt(SHARPNESS.location().toString()),
                "max-level serialization uses the same key shape");
    }

    @Test
    void load_dropsUnresolvedEnchantmentsKeepingRemainderIntact() {
        // Synthesize NBT that carries an id our synthetic enchantment registry does not register
        // ("minecraft:never_registered") — the 1.21.1 library must tolerate this rather than crash
        // so a datapack uninstall doesn't eat the entire point pool.
        CompoundTag tag = new CompoundTag();
        CompoundTag points = new CompoundTag();
        points.putInt(SHARPNESS.location().toString(), 2048);
        points.putInt("minecraft:never_registered", 999);
        tag.put(EnchantmentLibraryBlockEntity.TAG_POINTS, points);

        CompoundTag levels = new CompoundTag();
        levels.putInt(SHARPNESS.location().toString(), 5);
        levels.putInt("minecraft:never_registered", 9);
        tag.put(EnchantmentLibraryBlockEntity.TAG_LEVELS, levels);

        TestLibraryBlockEntity be = newLibrary();
        be.loadCustomOnly(tag, provider);

        assertEquals(2048, be.getPoints().getInt(SHARPNESS),
                "resolvable point entry survives the load");
        assertEquals(5, be.getMaxLevels().getInt(SHARPNESS),
                "resolvable max-level entry survives the load");
        assertEquals(1, be.getPoints().size(),
                "unresolved key dropped from points, only the surviving one remains");
        assertEquals(1, be.getMaxLevels().size(),
                "unresolved key dropped from maxLevels as well");
        for (ResourceKey<Enchantment> key : be.getPoints().keySet()) {
            assertFalse(key.location().getPath().equals("never_registered"),
                    "dropped key must not appear in the resolved map");
        }
    }

    @Test
    void load_dropsMalformedKeysWithoutCrashing() {
        // A garbage key string (`"not:a:valid:id"` has multiple colons) must not throw — the
        // reader parses defensively and skips entries with a warning.
        CompoundTag tag = new CompoundTag();
        CompoundTag points = new CompoundTag();
        points.putInt(SHARPNESS.location().toString(), 12);
        points.putInt("not a valid id", 42);
        tag.put(EnchantmentLibraryBlockEntity.TAG_POINTS, points);
        tag.put(EnchantmentLibraryBlockEntity.TAG_LEVELS, new CompoundTag());

        TestLibraryBlockEntity be = newLibrary();
        be.loadCustomOnly(tag, provider);

        assertEquals(12, be.getPoints().getInt(SHARPNESS),
                "valid entry survives alongside a malformed sibling");
        assertEquals(1, be.getPoints().size(),
                "malformed id dropped, leaving the valid entry alone");
    }

    @Test
    void load_clearsPriorStateBeforeApplyingNbt() {
        // Prime the BE with state that would corrupt a merge if load() didn't clear first.
        TestLibraryBlockEntity be = newLibrary();
        be.getPoints().put(UNBREAKING, 77);
        be.getMaxLevels().put(UNBREAKING, 2);

        CompoundTag tag = new CompoundTag();
        CompoundTag points = new CompoundTag();
        points.putInt(SHARPNESS.location().toString(), 1);
        tag.put(EnchantmentLibraryBlockEntity.TAG_POINTS, points);
        tag.put(EnchantmentLibraryBlockEntity.TAG_LEVELS, new CompoundTag());

        be.loadCustomOnly(tag, provider);

        assertFalse(be.getPoints().containsKey(UNBREAKING),
                "pre-load state must be dropped — load is an overwrite, not a merge");
        assertEquals(1, be.getPoints().getInt(SHARPNESS),
                "loaded entry lands as written");
    }

    @Test
    void save_emptyMapsYieldEmptyTags() {
        TestLibraryBlockEntity be = newLibrary();
        CompoundTag tag = be.saveCustomOnly(provider);
        assertTrue(tag.getCompound(EnchantmentLibraryBlockEntity.TAG_POINTS).isEmpty(),
                "empty point map → empty Points compound tag");
        assertTrue(tag.getCompound(EnchantmentLibraryBlockEntity.TAG_LEVELS).isEmpty(),
                "empty max-level map → empty Levels compound tag");

        TestLibraryBlockEntity reloaded = newLibrary();
        reloaded.loadCustomOnly(tag, provider);
        assertTrue(reloaded.getPoints().isEmpty(),
                "empty tag loads into an empty point map, no crash");
        assertTrue(reloaded.getMaxLevels().isEmpty(),
                "empty tag loads into an empty max-level map");
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
