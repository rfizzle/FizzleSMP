package com.fizzlesmp.fizzle_enchanting.shelf;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.enchanting.IEnchantingStatProvider;

import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * T-3.1.1 — proves the base {@link EnchantingShelfBlock} registers cleanly against
 * {@link BuiltInRegistries#BLOCK} and exposes its {@link ParticleTheme}. Follows
 * {@code FizzleEnchantingRegistryTest}'s unfreeze/refreeze pattern since {@code Bootstrap.bootStrap}
 * freezes the registry.
 */
class EnchantingShelfBlockTest {

    private static final ResourceLocation TEST_ID = FizzleEnchanting.id("test_shelf");

    private static EnchantingShelfBlock registered;

    @BeforeAll
    static void bootstrap() throws Exception {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        unfreezeBlockRegistry();
        registered = Registry.register(
                BuiltInRegistries.BLOCK,
                TEST_ID,
                new EnchantingShelfBlock(
                        BlockBehaviour.Properties.of()
                                .mapColor(MapColor.WOOD)
                                .strength(0.75F)
                                .sound(SoundType.WOOD),
                        ParticleTheme.ENCHANT_FIRE));
        BuiltInRegistries.BLOCK.freeze();
    }

    @Test
    void register_resolvesFromBlockRegistry() {
        Block resolved = BuiltInRegistries.BLOCK.get(TEST_ID);
        assertNotNull(resolved, "block must resolve from BuiltInRegistries.BLOCK after register()");
        assertSame(registered, resolved, "resolved block must be the exact instance registered");
        assertInstanceOf(EnchantingShelfBlock.class, resolved);
        assertInstanceOf(IEnchantingStatProvider.class, resolved,
                "shelf must implement IEnchantingStatProvider so the stat registry picks it up");
    }

    @Test
    void getParticleTheme_returnsConstructorValue() {
        assertSame(ParticleTheme.ENCHANT_FIRE, registered.getParticleTheme(),
                "the theme passed to the constructor must round-trip through getParticleTheme");
    }

    private static void unfreezeBlockRegistry() throws Exception {
        Field frozen = MappedRegistry.class.getDeclaredField("frozen");
        frozen.setAccessible(true);
        frozen.setBoolean(BuiltInRegistries.BLOCK, false);
        // freeze() nulls the intrusive-holder map; without restoring it, Block.<init> throws
        // "This registry can't create intrusive holders" when we try to construct a new block.
        Field intrusive = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
        intrusive.setAccessible(true);
        intrusive.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
    }
}
