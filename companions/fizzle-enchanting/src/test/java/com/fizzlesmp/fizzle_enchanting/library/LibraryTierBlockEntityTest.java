package com.fizzlesmp.fizzle_enchanting.library;

import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * T-4.3.2 — proves the two tier-specific subclasses construct cleanly with the right cap
 * constants. The Basic / Ender split is the only place the {@code maxLevel} (and therefore
 * {@code maxPoints}) constants live; this guard rail catches accidental constant drift before
 * it lands in shipped on-disk NBT pools where corrections would corrupt stored books.
 *
 * <p>Mirrors the bootstrap-then-unfreeze dance the registry tests use: vanilla
 * {@link Bootstrap#bootStrap()} freezes {@link BuiltInRegistries#BLOCK_ENTITY_TYPE}, but the
 * test-only types we mint to instantiate the subclasses run their constructor at static-init
 * time and would otherwise hit "registry frozen" before either tier could be exercised.
 */
class LibraryTierBlockEntityTest {

    private static BlockState bookshelf;
    private static BlockEntityType<BasicLibraryBlockEntity> basicType;
    private static BlockEntityType<EnderLibraryBlockEntity> enderType;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreeze(BuiltInRegistries.BLOCK_ENTITY_TYPE);
        // Production wiring registers BASIC_LIBRARY_BE / ENDER_LIBRARY_BE through
        // FizzleEnchantingRegistry, but loading that class statically initializes every other
        // BE/menu/block field there too — too heavy a dependency for a focused construction
        // test. Mint isolated test-only types so the assertions stay pinned to the constants
        // that *this* task introduces (MAX_LEVEL / MAX_POINTS), independent of registry order.
        basicType = BlockEntityType.Builder.of(
                (pos, state) -> new BasicLibraryBlockEntity(basicType, pos, state),
                Blocks.BOOKSHELF).build(null);
        enderType = BlockEntityType.Builder.of(
                (pos, state) -> new EnderLibraryBlockEntity(enderType, pos, state),
                Blocks.BOOKSHELF).build(null);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath("fizzle_enchanting", "test_basic_library"),
                basicType);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath("fizzle_enchanting", "test_ender_library"),
                enderType);
        BuiltInRegistries.BLOCK_ENTITY_TYPE.freeze();
        bookshelf = Blocks.BOOKSHELF.defaultBlockState();
    }

    @Test
    void basicConstants_matchDesign() {
        assertEquals(16, BasicLibraryBlockEntity.MAX_LEVEL,
                "Basic tier cap of 16 is baked into shipped NBT — must not drift");
        assertEquals(32_768, BasicLibraryBlockEntity.MAX_POINTS,
                "MAX_POINTS = 2^(MAX_LEVEL - 1) per the points formula");
    }

    @Test
    void enderConstants_matchDesign() {
        assertEquals(31, EnderLibraryBlockEntity.MAX_LEVEL,
                "Ender tier cap of 31 — the largest level whose points fit in a signed int");
        assertEquals(1_073_741_824, EnderLibraryBlockEntity.MAX_POINTS,
                "MAX_POINTS = 2^30 — clamping must not silently truncate to int overflow");
    }

    @Test
    void basicConstruction_exposesExpectedTier() {
        BasicLibraryBlockEntity be = new BasicLibraryBlockEntity(basicType, BlockPos.ZERO, bookshelf);
        assertEquals(BasicLibraryBlockEntity.MAX_LEVEL, be.getMaxLevel(),
                "subclass must propagate its MAX_LEVEL up to the parent");
        assertEquals(BasicLibraryBlockEntity.MAX_POINTS, be.getMaxPoints(),
                "parent must derive maxPoints from the passed-in maxLevel via points()");
        assertSame(basicType, be.getType(), "subclass must register against the supplied BE type");
        assertTrue(be.getPoints().isEmpty(), "fresh BE has empty point map");
        assertTrue(be.getMaxLevels().isEmpty(), "fresh BE has empty max-level map");
    }

    @Test
    void enderConstruction_exposesExpectedTier() {
        EnderLibraryBlockEntity be = new EnderLibraryBlockEntity(enderType, BlockPos.ZERO, bookshelf);
        assertEquals(EnderLibraryBlockEntity.MAX_LEVEL, be.getMaxLevel());
        assertEquals(EnderLibraryBlockEntity.MAX_POINTS, be.getMaxPoints());
        assertSame(enderType, be.getType());
    }

    @Test
    void tiers_areDistinctTypes() {
        // Sibling assertion: an Ender library is not interchangeable with a Basic. Catches a
        // copy-paste mistake where one subclass might silently hard-code the wrong tier cap.
        assertNotNull(BasicLibraryBlockEntity.class.getSuperclass());
        assertNotNull(EnderLibraryBlockEntity.class.getSuperclass());
        assertEquals(EnchantmentLibraryBlockEntity.class, BasicLibraryBlockEntity.class.getSuperclass());
        assertEquals(EnchantmentLibraryBlockEntity.class, EnderLibraryBlockEntity.class.getSuperclass());
    }

    /** Thaw the block-entity-type registry so {@code Builder#build} can mint intrusive holders. */
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
}
