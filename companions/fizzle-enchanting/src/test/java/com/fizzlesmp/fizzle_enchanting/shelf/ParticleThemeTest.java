// Tier: 2 (fabric-loader-junit)
package com.fizzlesmp.fizzle_enchanting.shelf;

import java.util.stream.Stream;

import net.minecraft.SharedConstants;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * T-3.1.2 — every {@link ParticleTheme} must round-trip to its expected vanilla
 * {@link SimpleParticleType}. The ParticleTypes class statically registers its fields against
 * {@code BuiltInRegistries.PARTICLE_TYPE}, so {@link Bootstrap#bootStrap()} has to run before any
 * field is touched.
 */
class ParticleThemeTest {

    @BeforeAll
    static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @ParameterizedTest
    @MethodSource("themes")
    void getParticleType_resolvesToExpectedVanillaParticle(ParticleTheme theme, SimpleParticleType expected) {
        assertNotNull(theme.getParticleType(), "theme " + theme + " must expose a particle type");
        assertSame(expected, theme.getParticleType(),
                "theme " + theme + " must resolve to the expected vanilla particle");
    }

    @Test
    void allFiveThemesPresent() {
        assertEquals(5, ParticleTheme.values().length,
                "five themes are wired across the design (ENCHANT, FIRE, WATER, END, SCULK)");
    }

    private static Stream<Arguments> themes() {
        return Stream.of(
                Arguments.of(ParticleTheme.ENCHANT, ParticleTypes.ENCHANT),
                Arguments.of(ParticleTheme.ENCHANT_FIRE, ParticleTypes.FLAME),
                Arguments.of(ParticleTheme.ENCHANT_WATER, ParticleTypes.SPLASH),
                Arguments.of(ParticleTheme.ENCHANT_END, ParticleTypes.PORTAL),
                Arguments.of(ParticleTheme.ENCHANT_SCULK, ParticleTypes.SCULK_SOUL));
    }
}
