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
| Inventory Profiles Next | Mouse Tweaks | Both modify inventory click/drag behavior | No actual conflict — IPN handles sorting/auto-refill, Mouse Tweaks handles drag-splitting. May need keybind adjustment if defaults overlap. |
| YUNG's suite | Repurposed Structures | Overlapping structure types: dungeons, mineshafts, desert temples, jungle temples, witch huts | Disable overlapping structure types in RS config; let YUNG's mods handle those. Keep RS enabled for its unique shipwrecks, mansions, igloos, and ruins variants. |

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
| Iron Chests | Sodium | No overlap — storage blocks vs. rendering optimization |
| Iron Chests | Lithium | No overlap — storage blocks vs. game logic optimization |
| Iron Chests | Metal Barrels | Complementary — chests and barrels, no overlap |
| Metal Barrels | Sodium | No overlap — storage blocks vs. rendering optimization |
| Metal Barrels | Lithium | No overlap — storage blocks vs. game logic optimization |
| Metal Barrels | Fabric API | Metal Barrels requires Fabric API |
| Iron Chests | Fabric API | Iron Chests requires Fabric API |
| Lootr | Sodium | No overlap — loot instancing vs. rendering optimization |
| Lootr | Lithium | No overlap — loot instancing vs. game logic optimization |
| Lootr | Cloth Config API | Lootr uses Cloth Config for settings |
| Lootr | Fabric API | Lootr requires Fabric API |
| Inventory Profiles Next | Mouse Tweaks | Both modify inventory interactions; generally compatible but may need keybind adjustment |
| Inventory Profiles Next | Sodium | No overlap — inventory management vs. rendering optimization |
| Inventory Profiles Next | Mod Menu | IPN integrates with Mod Menu for settings |
| Inventory Profiles Next | Fabric API | IPN requires Fabric API |
| Traveler's Backpack | Sodium | No overlap — backpack storage vs. rendering optimization |
| Traveler's Backpack | Lithium | No overlap — backpack storage vs. game logic optimization |
| Traveler's Backpack | Cloth Config API | Traveler's Backpack uses Cloth Config for settings |
| Traveler's Backpack | Fabric API | Traveler's Backpack requires Fabric API |
| Traveler's Backpack | Inventory Profiles Next | IPN can sort Traveler's Backpack inventories |
| Terralith | Tectonic | Tectonic mod auto-loads Terratonic compatibility when Terralith is detected |
| Terralith | Noisium | Noisium optimizes worldgen without altering output; confirmed compatible |
| Tectonic | Noisium | Noisium optimizes worldgen without altering output; confirmed compatible |
| Terralith | Repurposed Structures | RS spawns structures in Terralith biomes; complementary |
| Terralith | Towns and Towers | Explicitly confirmed compatible by Towns and Towers |
| Tectonic | Repurposed Structures | No overlap — terrain shaping vs. structure variants |
| Tectonic | Towns and Towers | No overlap — terrain shaping vs. village/outpost expansion |
| Repurposed Structures | Towns and Towers | Explicitly confirmed compatible; different structure types |
| Repurposed Structures | Lootr | Lootr operates at container level; compatible with all structure mods |
| Towns and Towers | Lootr | Lootr operates at container level; compatible with all structure mods |
| Terralith | Lootr | Lootr operates at container level; compatible with all structure mods |
| Terralith | Sodium | No overlap — worldgen vs. rendering optimization |
| Terralith | Lithium | No overlap — worldgen vs. game logic optimization |
| Tectonic | Sodium | No overlap — worldgen vs. rendering optimization |
| Tectonic | Lithium | No overlap — worldgen vs. game logic optimization |
| Repurposed Structures | Sodium | No overlap — structures vs. rendering optimization |
| Repurposed Structures | Lithium | No overlap — structures vs. game logic optimization |
| Repurposed Structures | MidnightLib | MidnightLib is a required dependency of Repurposed Structures |
| Repurposed Structures | Fabric API | Repurposed Structures requires Fabric API |
| Towns and Towers | Sodium | No overlap — structures vs. rendering optimization |
| Towns and Towers | Lithium | No overlap — structures vs. game logic optimization |
| Towns and Towers | Cristel Lib | Cristel Lib is a required dependency of Towns and Towers |
| YUNG's Better Caves | Terralith | Compatible — Better Caves overhauls cave shapes, Terralith adds biomes; no documented conflicts |
| YUNG's Better Caves | Tectonic | No overlap — cave generation vs. terrain shaping |
| YUNG's Better Caves | Noisium | Noisium optimizes worldgen without altering output; compatible |
| YUNG's Better Mineshafts | Terralith | Compatible — biome-specific mineshaft variants work with Terralith biomes |
| YUNG's Better Dungeons | Terralith | No overlap — dungeon overhauls vs. biome additions |
| YUNG's Better Dungeons | Lootr | Lootr operates at container level; compatible with all structure mods |
| YUNG's Better Desert Temples | Lootr | Lootr operates at container level; compatible with all structure mods |
| YUNG's Better Ocean Monuments | Lootr | Lootr operates at container level; compatible with all structure mods |
| YUNG's Better Nether Fortresses | Lootr | Lootr operates at container level; compatible with all structure mods |
| YUNG's Better Jungle Temples | Lootr | Lootr operates at container level; compatible with all structure mods |
| YUNG's Better Strongholds | Lootr | Lootr operates at container level; compatible with all structure mods |
| YUNG's Better Witch Huts | Lootr | Lootr operates at container level; compatible with all structure mods |
| All YUNG's mods | YUNG's API | YUNG's API is the required shared library for all YUNG's mods |
| All YUNG's mods | Sodium | No overlap — worldgen/structures vs. rendering optimization |
| All YUNG's mods | Lithium | No overlap — worldgen/structures vs. game logic optimization |
| Traveler's Titles | YUNG's API | YUNG's API is a required dependency of Traveler's Titles |
| Traveler's Titles | Terralith | Compatible — Traveler's Titles displays biome names from Terralith |
| Traveler's Titles | Sodium | No overlap — biome title display vs. rendering optimization |
