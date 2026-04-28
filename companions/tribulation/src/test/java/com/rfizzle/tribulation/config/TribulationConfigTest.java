// Tier: 1 (pure JUnit)
package com.rfizzle.tribulation.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TribulationConfigTest {

    @Test
    void defaultConfig_hasValidValues() {
        TribulationConfig cfg = new TribulationConfig();

        assertTrue(cfg.general.maxLevel > 0, "maxLevel must be > 0");
        assertTrue(cfg.general.levelUpTicks > 0, "levelUpTicks must be > 0");
        assertTrue(cfg.general.mobDetectionRange >= 0, "mobDetectionRange must be >= 0");

        assertTrue(cfg.distanceScaling.startingDistance >= 0);
        assertTrue(cfg.distanceScaling.increasingDistance > 0);
        assertTrue(cfg.distanceScaling.distanceFactor >= 0);
        assertTrue(cfg.distanceScaling.maxDistanceFactor >= 0);

        assertTrue(cfg.heightScaling.heightDistance > 0);
        assertTrue(cfg.heightScaling.heightFactor >= 0);
        assertTrue(cfg.heightScaling.maxHeightFactor >= 0);

        assertTrue(cfg.statCaps.maxFactorHealth > 0);
        assertTrue(cfg.statCaps.maxFactorDamage > 0);
        assertTrue(cfg.statCaps.maxFactorSpeed >= 0);
        assertTrue(cfg.statCaps.maxFactorProtection >= 0);
        assertTrue(cfg.statCaps.maxFactorFollowRange >= 0);

        assertTrue(cfg.shards.dropChance >= 0 && cfg.shards.dropChance <= 1);
        assertTrue(cfg.specialZombies.bigZombieChance >= 0 && cfg.specialZombies.bigZombieChance <= 100);
        assertTrue(cfg.specialZombies.speedZombieChance >= 0 && cfg.specialZombies.speedZombieChance <= 100);

        assertTrue(cfg.tiers.tier1 <= cfg.tiers.tier2);
        assertTrue(cfg.tiers.tier2 <= cfg.tiers.tier3);
        assertTrue(cfg.tiers.tier3 <= cfg.tiers.tier4);
        assertTrue(cfg.tiers.tier4 <= cfg.tiers.tier5);
    }

    @Test
    void defaultConfig_populatesAllMobScalingEntries() {
        TribulationConfig cfg = new TribulationConfig();

        assertEquals(TribulationConfig.MOB_KEYS.length, cfg.scaling.size());
        assertEquals(TribulationConfig.MOB_KEYS.length, cfg.mobToggles.size());

        for (String key : TribulationConfig.MOB_KEYS) {
            TribulationConfig.MobScaling m = cfg.scaling.get(key);
            assertNotNull(m, "missing scaling for " + key);
            assertTrue(m.healthRate >= 0 && m.healthCap > 0, "bad health for " + key);
            assertTrue(m.damageRate >= 0 && m.damageCap > 0, "bad damage for " + key);
            assertTrue(m.speedRate >= 0 && m.speedCap >= 0, "bad speed for " + key);
            assertTrue(m.followRangeRate >= 0 && m.followRangeCap >= 0, "bad followRange for " + key);
            assertTrue(m.armorRate >= 0 && m.armorCap >= 0, "bad armor for " + key);
            assertTrue(m.toughnessRate >= 0 && m.toughnessCap >= 0, "bad toughness for " + key);
            assertTrue(cfg.mobToggles.getOrDefault(key, Boolean.FALSE), "toggle for " + key + " should default true");
        }
    }

    @Test
    void getMobScaling_unknownMob_fallsBackToZombie(@TempDir Path tmp) {
        TribulationConfig cfg = new TribulationConfig();
        TribulationConfig.MobScaling zombie = cfg.scaling.get("zombie");
        TribulationConfig.MobScaling fallback = cfg.getMobScaling("not_a_real_mob");
        // Same instance — fallback returns the zombie entry directly.
        assertEquals(zombie.healthRate, fallback.healthRate);
        assertEquals(zombie.damageRate, fallback.damageRate);
    }

    @Test
    void isMobEnabled_unknownMob_isFalse() {
        TribulationConfig cfg = new TribulationConfig();
        assertFalse(cfg.isMobEnabled("not_a_real_mob"));
    }

    @Test
    void load_missingFile_writesDefaultsAndReturnsThem(@TempDir Path tmp) {
        Path path = tmp.resolve("tribulation.json");
        TribulationConfig loaded = TribulationConfig.load(path);

        assertTrue(Files.exists(path), "load() should have created the missing file");
        assertEquals(250, loaded.general.maxLevel);
        assertEquals(72000, loaded.general.levelUpTicks);
        assertEquals(TribulationConfig.MOB_KEYS.length, loaded.scaling.size());
    }

    @Test
    void roundTrip_preservesValues(@TempDir Path tmp) {
        Path path = tmp.resolve("tribulation.json");
        TribulationConfig original = new TribulationConfig();
        original.general.maxLevel = 123;
        original.general.levelUpTicks = 4567;
        original.shards.dropChance = 0.42;
        original.scaling.get("zombie").healthRate = 0.05;
        original.mobToggles.put("zombie", false);
        original.save(path);

        TribulationConfig reloaded = TribulationConfig.load(path);

        assertEquals(123, reloaded.general.maxLevel);
        assertEquals(4567, reloaded.general.levelUpTicks);
        assertEquals(0.42, reloaded.shards.dropChance);
        assertEquals(0.05, reloaded.scaling.get("zombie").healthRate);
        assertFalse(reloaded.mobToggles.get("zombie"));
    }

    @Test
    void load_emptyJsonObject_fillsAllDefaults(@TempDir Path tmp) throws IOException {
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, "{}");

        TribulationConfig loaded = TribulationConfig.load(path);

        assertNotNull(loaded.general);
        assertNotNull(loaded.timeScaling);
        assertNotNull(loaded.distanceScaling);
        assertNotNull(loaded.heightScaling);
        assertNotNull(loaded.statCaps);
        assertNotNull(loaded.deathRelief);
        assertNotNull(loaded.shards);
        assertNotNull(loaded.specialZombies);
        assertNotNull(loaded.bosses);
        assertNotNull(loaded.xpAndLoot);
        assertNotNull(loaded.tiers);
        assertNotNull(loaded.abilities);
        assertEquals(250, loaded.general.maxLevel);
        assertEquals(TribulationConfig.MOB_KEYS.length, loaded.scaling.size());
        assertEquals(TribulationConfig.MOB_KEYS.length, loaded.mobToggles.size());
    }

    @Test
    void load_partialScaling_fillsMissingMobsWithDefaults(@TempDir Path tmp) throws IOException {
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, """
                {
                  "scaling": {
                    "zombie": { "healthRate": 0.99 }
                  },
                  "mobToggles": {
                    "zombie": false
                  }
                }
                """);

        TribulationConfig loaded = TribulationConfig.load(path);

        assertEquals(0.99, loaded.scaling.get("zombie").healthRate);
        assertFalse(loaded.mobToggles.get("zombie"));
        // Other mobs filled in.
        for (String key : TribulationConfig.MOB_KEYS) {
            assertNotNull(loaded.scaling.get(key), "scaling missing for " + key);
            assertNotNull(loaded.mobToggles.get(key), "toggle missing for " + key);
        }
        // Untouched zombie fields fall back to MobScaling defaults (Gson leaves unset primitives as 0,
        // since Gson doesn't run the field initializer when only some fields are present in JSON).
        // So we don't assert damageRate here — it'll be 0, which validate() accepts.
        // The contract is "missing mobs filled", not "missing fields within a mob filled".
    }

    @Test
    void load_malformedJson_returnsDefaultsWithoutOverwriting(@TempDir Path tmp) throws IOException {
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, "{ this is not valid json");

        TribulationConfig loaded = TribulationConfig.load(path);

        assertEquals(250, loaded.general.maxLevel);
        // Bad file is preserved, not overwritten — operator can fix it.
        assertEquals("{ this is not valid json", Files.readString(path));
    }

    @Test
    void load_emptyFile_overwritesWithDefaults(@TempDir Path tmp) throws IOException {
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, "");

        TribulationConfig loaded = TribulationConfig.load(path);

        assertEquals(250, loaded.general.maxLevel);
        assertTrue(Files.size(path) > 0, "empty file should be replaced with defaults");
    }

    @Test
    void load_negativeRates_clampedToZero(@TempDir Path tmp) throws IOException {
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, """
                {
                  "scaling": {
                    "zombie": {
                      "healthRate": -1.0,
                      "damageRate": -5.0,
                      "armorRate": -0.1
                    }
                  },
                  "statCaps": {
                    "maxFactorHealth": -2.0
                  }
                }
                """);

        TribulationConfig loaded = TribulationConfig.load(path);

        assertEquals(0.0, loaded.scaling.get("zombie").healthRate);
        assertEquals(0.0, loaded.scaling.get("zombie").damageRate);
        assertEquals(0.0, loaded.scaling.get("zombie").armorRate);
        assertEquals(0.0, loaded.statCaps.maxFactorHealth);
    }

    @Test
    void load_maxLevelBelowOne_clampedToOne(@TempDir Path tmp) throws IOException {
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, """
                { "general": { "maxLevel": 0, "levelUpTicks": 0, "mobDetectionRange": -10 } }
                """);

        TribulationConfig loaded = TribulationConfig.load(path);

        assertEquals(1, loaded.general.maxLevel);
        assertEquals(1, loaded.general.levelUpTicks);
        assertEquals(0.0, loaded.general.mobDetectionRange);
    }

    @Test
    void load_dropChanceOutOfRange_clampedToUnitInterval(@TempDir Path tmp) throws IOException {
        Path tooHigh = tmp.resolve("high.json");
        Files.writeString(tooHigh, "{ \"shards\": { \"dropChance\": 5.0 } }");
        assertEquals(1.0, TribulationConfig.load(tooHigh).shards.dropChance);

        Path tooLow = tmp.resolve("low.json");
        Files.writeString(tooLow, "{ \"shards\": { \"dropChance\": -2.0 } }");
        assertEquals(0.0, TribulationConfig.load(tooLow).shards.dropChance);
    }

    @Test
    void load_zombieChanceOutOfRange_clampedToPercentRange(@TempDir Path tmp) throws IOException {
        Path tooHigh = tmp.resolve("high.json");
        Files.writeString(tooHigh, """
                { "specialZombies": { "bigZombieChance": 9999, "speedZombieChance": 9999 } }
                """);
        TribulationConfig high = TribulationConfig.load(tooHigh);
        assertEquals(100, high.specialZombies.bigZombieChance);
        assertEquals(100, high.specialZombies.speedZombieChance);

        Path tooLow = tmp.resolve("low.json");
        Files.writeString(tooLow, """
                { "specialZombies": { "bigZombieChance": -50, "speedZombieChance": -50 } }
                """);
        TribulationConfig low = TribulationConfig.load(tooLow);
        assertEquals(0, low.specialZombies.bigZombieChance);
        assertEquals(0, low.specialZombies.speedZombieChance);
    }

    @Test
    void load_nonPositiveDistanceClampedToOne(@TempDir Path tmp) throws IOException {
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, """
                {
                  "distanceScaling": { "increasingDistance": 0 },
                  "heightScaling": { "heightDistance": -5 },
                  "specialZombies": { "bigZombieSize": 0 }
                }
                """);

        TribulationConfig loaded = TribulationConfig.load(path);

        assertEquals(1.0, loaded.distanceScaling.increasingDistance);
        assertEquals(1.0, loaded.heightScaling.heightDistance);
        assertEquals(1.0, loaded.specialZombies.bigZombieSize);
    }

    @Test
    void load_nonMonotonicTiers_areCorrected(@TempDir Path tmp) throws IOException {
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, """
                { "tiers": { "tier1": -10, "tier2": 5, "tier3": 3, "tier4": 1, "tier5": 0 } }
                """);

        TribulationConfig loaded = TribulationConfig.load(path);

        assertEquals(0, loaded.tiers.tier1);
        assertEquals(5, loaded.tiers.tier2);
        assertEquals(5, loaded.tiers.tier3);
        assertEquals(5, loaded.tiers.tier4);
        assertEquals(5, loaded.tiers.tier5);
    }

    @Test
    void load_negativeDeathReliefValues_clampedToZero(@TempDir Path tmp) throws IOException {
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, """
                {
                  "deathRelief": { "amount": -3, "cooldownTicks": -100, "minimumLevel": -1 },
                  "shards": { "dropStartLevel": -7, "shardPower": -2 }
                }
                """);

        TribulationConfig loaded = TribulationConfig.load(path);

        assertEquals(0, loaded.deathRelief.amount);
        assertEquals(0, loaded.deathRelief.cooldownTicks);
        assertEquals(0, loaded.deathRelief.minimumLevel);
        assertEquals(0, loaded.shards.dropStartLevel);
        assertEquals(0, loaded.shards.shardPower);
    }

    @Test
    void roundTrip_afterClamping_isStable(@TempDir Path tmp) throws IOException {
        // Bad values get clamped on first load, then a save+load should be a no-op.
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, """
                { "general": { "maxLevel": -5 }, "shards": { "dropChance": 99 } }
                """);
        TribulationConfig first = TribulationConfig.load(path);
        Path second = tmp.resolve("again.json");
        first.save(second);
        TribulationConfig reloaded = TribulationConfig.load(second);

        assertEquals(first.general.maxLevel, reloaded.general.maxLevel);
        assertEquals(first.shards.dropChance, reloaded.shards.dropChance);
        assertEquals(1, reloaded.general.maxLevel);
        assertEquals(1.0, reloaded.shards.dropChance);
    }

    @Test
    void defaultScaling_zombieMatchesDesignReferenceRates() {
        TribulationConfig cfg = new TribulationConfig();
        TribulationConfig.MobScaling z = cfg.scaling.get("zombie");
        // Reference values pulled straight from DESIGN.md — any drift here means
        // the reference mob no longer matches the documented level-breakpoint table.
        assertEquals(0.010, z.healthRate);
        assertEquals(2.5, z.healthCap);
        assertEquals(0.015, z.damageRate);
        assertEquals(3.75, z.damageCap);
        assertEquals(0.0012, z.speedRate);
        assertEquals(0.3, z.speedCap);
        assertEquals(0.010, z.followRangeRate);
        assertEquals(1.0, z.followRangeCap);
        assertEquals(0.032, z.armorRate);
        assertEquals(8.0, z.armorCap);
        assertEquals(0.024, z.toughnessRate);
        assertEquals(6.0, z.toughnessCap);
    }

    @Test
    void defaultScaling_perMobRolesAreTuned() {
        TribulationConfig cfg = new TribulationConfig();
        Map<String, TribulationConfig.MobScaling> s = cfg.scaling;

        // Spider & friends scale speed faster than zombies.
        assertTrue(s.get("spider").speedRate > s.get("zombie").speedRate,
                "spider should scale speed faster than zombie");
        assertTrue(s.get("cave_spider").speedRate > s.get("zombie").speedRate,
                "cave spider should scale speed faster than zombie");
        assertTrue(s.get("endermite").speedRate >= s.get("spider").speedRate,
                "endermite should scale speed at least as fast as spider");

        // Ravager is the tankiest mob — highest health scaling.
        double ravagerHealth = s.get("ravager").healthRate;
        for (String key : TribulationConfig.MOB_KEYS) {
            if (key.equals("ravager")) continue;
            assertTrue(ravagerHealth >= s.get(key).healthRate,
                    "ravager health rate should be >= " + key);
        }

        // Vindicator is the heaviest melee hitter.
        double vindicatorDamage = s.get("vindicator").damageRate;
        for (String key : TribulationConfig.MOB_KEYS) {
            if (key.equals("vindicator")) continue;
            assertTrue(vindicatorDamage >= s.get(key).damageRate,
                    "vindicator damage rate should be >= " + key);
        }

        // Endermite & silverfish have trivial HP pools vs. the reference zombie.
        assertTrue(s.get("endermite").healthRate < s.get("zombie").healthRate);
        assertTrue(s.get("silverfish").healthRate < s.get("zombie").healthRate);

        // Creeper has low direct health scaling — its threat is the explosion.
        assertTrue(s.get("creeper").healthRate < s.get("zombie").healthRate);

        // Armorless mobs (endermite, silverfish) have zero armor scaling so we
        // don't accidentally give them unreachable armor via modifiers.
        assertEquals(0.0, s.get("endermite").armorRate);
        assertEquals(0.0, s.get("endermite").armorCap);
        assertEquals(0.0, s.get("silverfish").armorRate);
        assertEquals(0.0, s.get("silverfish").armorCap);

        // Husk & wither skeleton are tougher than their baseline counterparts.
        assertTrue(s.get("husk").healthRate > s.get("zombie").healthRate);
        assertTrue(s.get("wither_skeleton").healthRate > s.get("skeleton").healthRate);

        // Bogged mirrors skeleton — same rates.
        assertEquals(s.get("skeleton").healthRate, s.get("bogged").healthRate);
        assertEquals(s.get("skeleton").damageRate, s.get("bogged").damageRate);
    }

    @Test
    void defaultScaling_capsMatchRatesAtMaxLevel() {
        // Each mob's cap should equal its rate * maxLevel (DESIGN.md invariant: the
        // per-attribute cap is reached exactly at the documented max level). This
        // catches copy-paste errors where a rate or cap drifts out of sync.
        TribulationConfig cfg = new TribulationConfig();
        int maxLevel = cfg.general.maxLevel;

        for (String key : TribulationConfig.MOB_KEYS) {
            TribulationConfig.MobScaling m = cfg.scaling.get(key);
            assertEquals(m.healthRate * maxLevel, m.healthCap, 1e-9,
                    key + ": healthCap should equal healthRate * maxLevel");
            assertEquals(m.damageRate * maxLevel, m.damageCap, 1e-9,
                    key + ": damageCap should equal damageRate * maxLevel");
            assertEquals(m.speedRate * maxLevel, m.speedCap, 1e-9,
                    key + ": speedCap should equal speedRate * maxLevel");
        }
    }

    @Test
    void load_missingUnlistedHostileMobs_fillsDefaults(@TempDir Path tmp) throws IOException {
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, "{}");

        TribulationConfig loaded = TribulationConfig.load(path);

        assertNotNull(loaded.unlistedHostileMobs);
        assertTrue(loaded.unlistedHostileMobs.enabled);
        assertNotNull(loaded.unlistedHostileMobs.excludedNamespaces);
        assertNotNull(loaded.unlistedHostileMobs.scaling);
        assertEquals(0.010, loaded.unlistedHostileMobs.scaling.healthRate);
        assertEquals(0.015, loaded.unlistedHostileMobs.scaling.damageRate);
    }

    @Test
    void load_partialUnlistedHostileMobs_preservesUserValuesAndFillsRest(@TempDir Path tmp) throws IOException {
        // User disables fallback and sets an excluded namespace but omits the
        // nested scaling block — we should preserve their overrides and fill
        // in the missing scaling object with defaults.
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, """
                {
                  "unlistedHostileMobs": {
                    "enabled": false,
                    "excludedNamespaces": ["somemod"]
                  }
                }
                """);

        TribulationConfig loaded = TribulationConfig.load(path);

        assertFalse(loaded.unlistedHostileMobs.enabled);
        assertEquals(1, loaded.unlistedHostileMobs.excludedNamespaces.size());
        assertEquals("somemod", loaded.unlistedHostileMobs.excludedNamespaces.get(0));
        assertNotNull(loaded.unlistedHostileMobs.scaling);
        assertEquals(0.010, loaded.unlistedHostileMobs.scaling.healthRate);
    }

    @Test
    void load_negativeUnlistedRates_clampedToZero(@TempDir Path tmp) throws IOException {
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, """
                {
                  "unlistedHostileMobs": {
                    "scaling": {
                      "healthRate": -1.0,
                      "damageRate": -2.5,
                      "armorRate": -0.3
                    }
                  }
                }
                """);

        TribulationConfig loaded = TribulationConfig.load(path);

        assertEquals(0.0, loaded.unlistedHostileMobs.scaling.healthRate);
        assertEquals(0.0, loaded.unlistedHostileMobs.scaling.damageRate);
        assertEquals(0.0, loaded.unlistedHostileMobs.scaling.armorRate);
    }

    @Test
    void roundTrip_unlistedHostileMobs_preservesValues(@TempDir Path tmp) {
        Path path = tmp.resolve("tribulation.json");
        TribulationConfig original = new TribulationConfig();
        original.unlistedHostileMobs.enabled = false;
        original.unlistedHostileMobs.excludedNamespaces.add("mutantmonsters");
        original.unlistedHostileMobs.scaling.healthRate = 0.02;
        original.save(path);

        TribulationConfig reloaded = TribulationConfig.load(path);

        assertFalse(reloaded.unlistedHostileMobs.enabled);
        assertTrue(reloaded.unlistedHostileMobs.excludedNamespaces.contains("mutantmonsters"));
        assertEquals(0.02, reloaded.unlistedHostileMobs.scaling.healthRate);
    }

    @Test
    void mobToggles_extraKeyFromUserIsPreserved(@TempDir Path tmp) throws IOException {
        // User-added entries (e.g., for modded mobs) should survive load/fillDefaults.
        Path path = tmp.resolve("tribulation.json");
        Files.writeString(path, """
                { "mobToggles": { "modded:custom_mob": false } }
                """);

        TribulationConfig loaded = TribulationConfig.load(path);

        Map<String, Boolean> toggles = loaded.mobToggles;
        assertFalse(toggles.get("modded:custom_mob"));
        for (String key : TribulationConfig.MOB_KEYS) {
            assertTrue(toggles.containsKey(key), "default mob toggle missing: " + key);
        }
    }
}
