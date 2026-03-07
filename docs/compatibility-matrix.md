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
| Colorful Hearts | Scaling Health | Both modify heart rendering; causes crashes or weird behaviour |

## Known Soft Conflicts

<!-- Pairs that work but need config adjustment or have overlapping features. -->

| Mod A | Mod B | Details | Resolution |
|---|---|---|---|
| Entity Culling | Mods with oversized block entities (Create, Botania) | Oversized block entities may be culled incorrectly | Whitelist affected entities in Entity Culling config |
| Colorful Hearts | Overflowing Bars | Both modify heart rendering code; can crash or render incorrectly | Set `allowLayers` for hearts to `false` in Overflowing Bars config |
| Jade | MiniHUD | Both display block/entity info overlays | No actual conflict — Jade shows tooltip on crosshair, MiniHUD shows F3-style HUD. Complementary. |

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
| AppleSkin | Colorful Hearts | Explicitly supported — Colorful Hearts has built-in AppleSkin compatibility for health restoration visualization |
| AppleSkin | Sodium | No overlap — food HUD vs. rendering optimization |
| AppleSkin | ImmediatelyFast | No overlap — food HUD vs. rendering optimization |
| Colorful Hearts | Sodium | No overlap — heart rendering vs. chunk rendering |
| Colorful Hearts | ImmediatelyFast | No overlap — heart rendering vs. rendering optimization |
| Jade | Sodium | No overlap — info tooltip vs. rendering optimization |
| Jade | Lithium | No overlap — info tooltip vs. game logic optimization |
| MiniHUD | Sodium | No overlap — debug HUD vs. rendering optimization |
| MiniHUD | Lithium | No overlap — debug HUD vs. game logic optimization |
| MiniHUD | MaLiLib | MaLiLib is MiniHUD's required library; designed to work together |
| guy's Armor HUD | Sodium | No overlap — armor HUD vs. rendering optimization |
| guy's Armor HUD | ImmediatelyFast | No overlap — armor HUD widget vs. rendering optimization |
| Mouse Tweaks | Sodium | No overlap — inventory management vs. rendering optimization |
| Mouse Tweaks | ImmediatelyFast | No overlap — inventory management vs. rendering optimization |
| Blur+ | Sodium | No overlap — GUI blur effect vs. chunk rendering optimization |
| Blur+ | Iris Shaders | Compatible — Blur+ works with shader mods |
| Continuity | Sodium | Designed to work together — Continuity provides connected textures for Sodium |
| Continuity | Iris Shaders | Compatible — connected textures work alongside shaders |
| Mod Menu | Sodium | Sodium provides Mod Menu integration for its settings screen |
| Mod Menu | Iris Shaders | Iris provides Mod Menu integration for its settings screen |
| Mod Menu | ImmediatelyFast | No overlap — mod list UI vs. rendering optimization |
| Cloth Config API | Mod Menu | Complementary — Cloth Config screens integrate with Mod Menu |
| RightClickHarvest | Lithium | No overlap — crop harvesting vs. game logic optimization |
| What Are They Up To (Watut) | Sodium | No overlap — player animation visuals vs. rendering optimization |
