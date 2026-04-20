package com.fizzlesmp.fizzle_enchanting.shelf;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.fizzlesmp.fizzle_enchanting.enchanting.IEnchantingStatProvider;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base class for every shelf that contributes to an enchanting table's stat tuple. Numerical
 * contributions live in datapack JSON under {@code data/fizzle_enchanting/enchanting_stats/}
 * (resolved through the {@link IEnchantingStatProvider} default method) so operators can rebalance
 * without a jar rebuild.
 *
 * <p>The {@link ParticleTheme} carried here classifies the shelf for particle emission (Nether
 * fire, ocean water, End, sculk, or generic enchant swirl). {@link #animateTick} fires client-side
 * only and emits 1–3 particles at a low probability per call; the sculk theme's chance is
 * configurable via {@code config.shelves.sculkParticleChance} so operators can dial down the
 * busier sculk visuals.
 */
public class EnchantingShelfBlock extends Block implements IEnchantingStatProvider {

    /** Per-call emission chance for non-sculk themes. animateTick is invoked at high cadence
     * for blocks near the camera, so the chance has to stay small to avoid a particle storm. */
    private static final double DEFAULT_PARTICLE_CHANCE = 0.20D;

    private final ParticleTheme particleTheme;

    public EnchantingShelfBlock(BlockBehaviour.Properties properties, ParticleTheme particleTheme) {
        super(properties);
        this.particleTheme = Objects.requireNonNull(particleTheme, "particleTheme");
    }

    public ParticleTheme getParticleTheme() {
        return this.particleTheme;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double chance = this.particleTheme == ParticleTheme.ENCHANT_SCULK
                ? sculkParticleChance()
                : DEFAULT_PARTICLE_CHANCE;
        if (chance <= 0.0D || random.nextDouble() >= chance) {
            return;
        }
        SimpleParticleType type = this.particleTheme.getParticleType();
        int count = 1 + random.nextInt(3); // 1..3
        for (int i = 0; i < count; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 0.5D + random.nextDouble() * 0.5D;
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(type, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    private static double sculkParticleChance() {
        FizzleEnchantingConfig config = FizzleEnchanting.getConfig();
        return config == null ? 0.0D : config.shelves.sculkParticleChance;
    }
}
