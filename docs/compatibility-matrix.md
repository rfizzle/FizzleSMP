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
