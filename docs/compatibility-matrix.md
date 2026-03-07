# Compatibility Matrix — FizzleSMP

Tracks known interactions between mods in the pack.

## Legend

| Symbol | Meaning |
|--------|---------|
| :white_check_mark: | Verified compatible — tested together, no issues |
| :warning: | Soft conflict — overlapping features or config tuning needed |
| :x: | Hard conflict — crashes, data corruption, or fundamentally incompatible |
| :question: | Untested — needs verification |

## Known Hard Conflicts

<!-- Pairs of mods that absolutely cannot coexist. -->

| Mod A | Mod B | Details |
|---|---|---|
| Sodium | OptiFine | Fundamentally incompatible rendering engines |
| Iris Shaders | OptiFine | Iris replaces OptiFine shader loading; cannot coexist |
| ImmediatelyFast | OptiFine/OptiFabric | Incompatible rendering optimizations |

## Known Soft Conflicts

<!-- Pairs that work but need config adjustment or have overlapping features. -->

| Mod A | Mod B | Details | Resolution |
|---|---|---|---|
| Entity Culling | Mods with oversized block entities (Create, Botania) | Oversized block entities may be culled incorrectly | Whitelist affected entities in Entity Culling config |

## Verified Compatible

<!-- Pairs explicitly tested and confirmed to work well together. -->

| Mod A | Mod B | Notes |
|---|---|---|
| Sodium | Iris Shaders | Iris is designed to work with Sodium; Sodium is a required dependency |
| Sodium | ImmediatelyFast | Complementary optimizations — tested together in benchmarks |
| Sodium | Entity Culling | Entity Culling extends Sodium's chunk-based visibility with finer entity-level culling |
| Iris Shaders | ImmediatelyFast | Tested together alongside Sodium |
| Iris Shaders | Entity Culling | Confirmed compatible in testing |
| ImmediatelyFast | Entity Culling | Confirmed compatible in testing |
| Lithium | Sodium | Complementary optimizations — Lithium handles game logic, Sodium handles rendering |
| Lithium | Iris Shaders | No overlap — different optimization targets |
| Lithium | ImmediatelyFast | No overlap — different optimization targets |
| Lithium | Entity Culling | No overlap — different optimization targets |
| Noisium | Sodium | Confirmed compatible per Noisium docs |
| Noisium | Lithium | Confirmed compatible per Noisium docs |
| Noisium | Iris Shaders | No overlap — worldgen vs. rendering |
| Noisium | ImmediatelyFast | No overlap — worldgen vs. rendering |
| Noisium | Entity Culling | No overlap — worldgen vs. entity rendering |
| FerriteCore | Sodium | Complementary — FerriteCore optimizes memory, Sodium optimizes rendering |
| FerriteCore | Lithium | Complementary — memory vs. game logic optimizations |
| FerriteCore | Iris Shaders | No overlap — different optimization targets |
| FerriteCore | ImmediatelyFast | No overlap — different optimization targets |
| FerriteCore | Entity Culling | No overlap — different optimization targets |
| FerriteCore | Noisium | No overlap — memory vs. worldgen optimizations |
| FerriteCore | ModernFix | Complementary — both reduce memory but target different areas |
| ModernFix | Sodium | Compatible per ModernFix description; complementary optimizations |
| ModernFix | Lithium | Compatible per ModernFix description; complementary optimizations |
| ModernFix | Iris Shaders | No overlap — different optimization targets |
| ModernFix | ImmediatelyFast | No overlap — different optimization targets |
| ModernFix | Entity Culling | No overlap — different optimization targets |
| ModernFix | Noisium | No overlap — memory/loading vs. worldgen optimizations |
