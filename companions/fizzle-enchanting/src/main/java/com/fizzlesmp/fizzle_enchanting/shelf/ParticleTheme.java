package com.fizzlesmp.fizzle_enchanting.shelf;

import java.util.Objects;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Identifies which particle family a shelf emits via {@code animateTick}. The five families mirror
 * Apotheosis/Zenith's themed shelf tiers — generic enchant swirl, Nether (fire), ocean (water),
 * End, and Deep/sculk — but each maps to a vanilla {@link SimpleParticleType} so we don't have to
 * register custom particles for the MVP.
 */
public enum ParticleTheme {
    ENCHANT(ParticleTypes.ENCHANT),
    ENCHANT_FIRE(ParticleTypes.FLAME),
    ENCHANT_WATER(ParticleTypes.SPLASH),
    ENCHANT_END(ParticleTypes.PORTAL),
    ENCHANT_SCULK(ParticleTypes.SCULK_SOUL);

    private final SimpleParticleType particleType;

    ParticleTheme(SimpleParticleType particleType) {
        this.particleType = Objects.requireNonNull(particleType, "particleType");
    }

    public SimpleParticleType getParticleType() {
        return this.particleType;
    }
}
