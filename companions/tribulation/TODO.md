# Tribulation - Future Work

## 1. @ParameterizedTest Adoption

The test suite currently uses `for` loops to iterate over all 21 mob keys (e.g., `defaultScaling_capsMatchRatesAtMaxLevel` loops over `MOB_KEYS` and asserts inside the loop). A failure reports as a single test with a message like "mismatch at key ravager" rather than 21 individually reported results. JUnit 5 parameterized tests would give per-case failure reporting and clearer test names in IDE runners and CI output.

### Proposals

- **`TribulationConfigTest` - mob scaling validation**: Convert the `MOB_KEYS` loops in `defaultConfig_populatesAllMobScalingEntries`, `defaultScaling_perMobRolesAreTuned`, and `defaultScaling_capsMatchRatesAtMaxLevel` to `@ParameterizedTest` with `@MethodSource` providing each of the 21 mob keys. Each mob becomes its own test case: `capsMatchRatesAtMaxLevel[zombie]`, `capsMatchRatesAtMaxLevel[ravager]`, etc.
- **`TierManagerTest` - tier boundary tests**: The level-to-tier mappings in `defaultTiers_thresholdsMatchDesign` and `boundaryConditions_respectsInclusiveThreshold` are natural candidates for `@CsvSource` with `level, expectedTier` pairs (e.g., `"0, 0"`, `"49, 0"`, `"50, 1"`, `"250, 5"`).
- **`ScalingEngineTest` - per-attribute classification**: The `classification_addValueAttributesArmorAndToughness` and `classification_positionScaledSubsetExcludesSpeedAndFollowRange` tests check each of the 6 attribute constants individually. These could use `@EnumSource` or `@MethodSource` providing attribute key + expected boolean pairs.
- **`ScalingEngineTest` - per-attribute rate/cap lookups**: The `computeAttributeFactor` tests for health, speed, follow range, armor, and toughness each test a single attribute. A `@MethodSource` supplying `(attributeKey, level, distanceFactor, heightFactor, expectedTimeFactor, expectedDistanceFactor, expectedHeightFactor)` tuples would consolidate them and make it trivial to add coverage for new attributes.
- **Dependency**: Add `junit-jupiter-params` to `build.gradle`. The artifact is already part of the `junit-bom:5.10.2` platform declaration, so only an explicit `testImplementation "org.junit.jupiter:junit-jupiter-params"` line is needed (no version required).

## 2. Mod Compatibility Integrations

Tribulation is server-focused with no client entrypoints today. These integrations would add optional client-side polish without requiring the mods to be present (soft dependencies via `fabric.mod.json` `suggests`).

### ModMenu - Config Screen

- Register a `modmenu` entrypoint in `fabric.mod.json` pointing to a config screen factory in the `client` source set.
- The mod has 19 ability toggles, 21 mob scaling profiles (each with 12 fields), plus general/distance/height/tier/shard/boss/zombie/xp-loot sections. Editing `tribulation.json` by hand is workable but error-prone.
- A minimal integration could just open the config directory in the system file manager. A richer version could use Cloth Config or YACL to expose the most-edited fields (ability toggles, mob toggles, tier thresholds, shard drop chance).
- Rationale: config-heavy mods without GUI screens are a frequent source of player complaints on SMP servers.

### Jade / WTHIT - Entity Tooltip

- Register a Jade (or WTHIT) entity component provider that reads the mob's current tier, effective level, and active abilities from the `tribulation:scaled` entity data attachment.
- Display in the tooltip: tier name/color, effective level, and icons or labels for active abilities (e.g., "Sprinting", "Flame Arrows").
- Useful for both debugging during development and player awareness in-game ("why is this zombie so tanky?").
- Would require a client entrypoint and a dependency on the Jade API artifact (compile-only, optional at runtime).

### EMI / REI / JEI - Shatter Shard Info

- Register the Shatter Shard item with a description/info recipe in EMI, REI, or JEI.
- Content: drop conditions (mob tier threshold, drop chance percentage), usage effects (level reduction amount, applied debuffs, cooldown), and the fact that it only drops from mobs above `dropStartLevel`.
- Low priority but improves discoverability for new players who find a shard and have no idea what it does.

## 3. Config Version Migration Infrastructure

`TribulationConfig` declares `configVersion = 1` but the field is never read during deserialization. If future updates rename, restructure, or remove config fields, existing server configs will silently lose their customizations when Gson fails to map old keys to new fields.

### Proposed Design

- **Pre-deserialization version read**: Parse the raw JSON into a `JsonObject`, extract `configVersion` as an integer before passing it to Gson. Fall back to version 0 if the field is absent (pre-versioned configs).
- **Migration chain**: Maintain an ordered list of migration functions, each transforming a `JsonObject` from version N to version N+1. Example: `v1ToV2`, `v2ToV3`, etc. Each migration operates on raw JSON keys/values, not on the deserialized Java object.
- **Example migration (v1 to v2)**: The abilities section recently renamed fields (e.g., a hypothetical `creeperFuse` to `creeperShorterFuse`). A v1-to-v2 migrator would check for the old key in `abilities`, rename it to the new key, and preserve the user's boolean value. Without migration, the old key is silently dropped and the default is used.
- **Post-migration save**: After all migrations run, update `configVersion` to the current version, serialize the migrated `JsonObject` through Gson, and overwrite the config file. This ensures the file on disk always reflects the latest schema.
- **Logging**: Log each migration step at INFO level (e.g., "Migrated tribulation.json from v1 to v2: renamed abilities.creeperFuse to abilities.creeperShorterFuse") so server admins know their config was auto-upgraded and can audit what changed.
- **Safety**: If a migration fails (malformed JSON subtree, unexpected type), log a warning and skip that migration step rather than corrupting the config. The worst case is a field reverting to its default, which is the current behavior anyway.
