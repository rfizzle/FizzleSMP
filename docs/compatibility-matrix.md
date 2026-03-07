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
| Accessories | Trinkets | Competing accessory slot APIs; do not install both — use Accessories Compatibility Layer instead |

## Known Soft Conflicts

<!-- Pairs that work but need config adjustment or have overlapping features. -->

| Mod A | Mod B | Details | Resolution |
|---|---|---|---|
| Jade | MiniHUD | Both display block/entity info overlays | No actual conflict — Jade shows tooltip on crosshair, MiniHUD shows F3-style HUD. Complementary. |
| Inventory Profiles Next | Mouse Tweaks | Both modify inventory click/drag behavior | No actual conflict — IPN handles sorting/auto-refill, Mouse Tweaks handles drag-splitting. May need keybind adjustment if defaults overlap. |
| YUNG's suite | Repurposed Structures | Overlapping structure types: dungeons, mineshafts, desert temples, jungle temples, witch huts | Disable overlapping structure types in RS config; let YUNG's mods handle those. Keep RS enabled for its unique shipwrecks, mansions, igloos, and ruins variants. |
| Xaero's Minimap | MiniHUD | Both display map/coordinate overlays on the HUD | No actual conflict — Xaero's shows a corner minimap, MiniHUD shows F3-style text overlays. Complementary; adjust HUD positions to avoid visual overlap. |
| Geophilic | Terralith | Both modify vanilla biome files; features may override each other | Use Terraphilic compatibility pack (must load after both mods) |
| Enchanting Infuser | Easy Magic | Both from Fuzs; Infuser adds a new enchanting block while Easy Magic tweaks the vanilla table — minor feature overlap in enchanting UX | No config needed — they target different blocks. Both can be used together as intended by the author. |

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
| Incendium | Terralith | Same author (Stardust Labs); designed to work together — different dimensions |
| Incendium | Nullscape | Same author (Stardust Labs); designed to work together — different dimensions |
| Nullscape | Terralith | Same author (Stardust Labs); designed to work together — different dimensions |
| Incendium | Tectonic | No overlap — Nether biomes vs. Overworld terrain shaping |
| Nullscape | Tectonic | No overlap — End dimension vs. Overworld terrain shaping |
| Incendium | Lootr | Lootr operates at container level; compatible with all structure mods |
| Nullscape | Lootr | Lootr operates at container level; compatible with all structure mods |
| Incendium | Noisium | Noisium optimizes worldgen without altering output; compatible |
| Nullscape | Noisium | Noisium optimizes worldgen without altering output; compatible |
| Incendium | Sodium | No overlap — Nether worldgen vs. rendering optimization |
| Nullscape | Sodium | No overlap — End worldgen vs. rendering optimization |
| Structory | Terralith | Compatible — Structory structures spawn in Terralith biomes |
| Structory | Tectonic | No overlap — small structures vs. terrain shaping |
| Structory | Repurposed Structures | No overlap — different structure types |
| Structory | Towns and Towers | No overlap — different structure types |
| Structory | Lootr | Lootr operates at container level; compatible with all structure mods |
| Structory: Towers | Terralith | Compatible — towers spawn in Terralith biomes |
| Structory: Towers | Structory | Designed as a companion add-on to Structory |
| Structory: Towers | Towns and Towers | No overlap — different tower styles and structure types |
| Structory: Towers | Lootr | Lootr operates at container level; compatible with all structure mods |
| YUNG's Cave Biomes | YUNG's Better Caves | Same author; designed to work together — Cave Biomes adds biome types, Better Caves overhauls shapes |
| YUNG's Cave Biomes | Terralith | Compatible — cave biomes complement Terralith's overworld biomes |
| YUNG's Cave Biomes | Noisium | Noisium optimizes worldgen without altering output; compatible |
| YUNG's Cave Biomes | YUNG's API | YUNG's API is a required dependency |
| YUNG's Cave Biomes | GeckoLib | GeckoLib is a required dependency |
| YUNG's Cave Biomes | TerraBlender | TerraBlender is a required dependency |
| Geophilic | Tectonic | Compatible — Geophilic modifies biome vegetation, Tectonic modifies terrain shape |
| Geophilic | Terraphilic | Terraphilic resolves biome file conflicts between Geophilic and Terralith |
| Terraphilic | Terralith | Terraphilic is designed specifically for Terralith+Geophilic compatibility |
| Hopo Better Underwater Ruins | YUNG's Better Ocean Monuments | No overlap — ruins vs. monuments are different structure types |
| Hopo Better Underwater Ruins | Terralith | No overlap — underwater structures vs. overworld biomes |
| Hopo Better Underwater Ruins | Lootr | Lootr operates at container level; compatible with all structure mods |
| Hopo Better Underwater Ruins | Repurposed Structures | May overlap on ocean ruin variants; check RS config to avoid duplicate ruins |
| Sparse Structures | All structure mods | Designed to work with all structure mods; adjusts spacing/separation globally |
| Sparse Structures | Fabric API | Sparse Structures requires Fabric API |
| Simply Swords | Better Combat | Officially partnered — Better Combat provides unique attack animations for all Simply Swords weapon types |
| Simply Swords | Architectury API | Architectury API is a required dependency of Simply Swords |
| Simply Swords | Fzzy Config | Fzzy Config is a required dependency of Simply Swords |
| Simply Swords | Fabric API | No overlap — weapons mod vs. API |
| Simply Swords | Sodium | No overlap — weapons vs. rendering optimization |
| Simply Swords | Lithium | No overlap — weapons vs. game logic optimization |
| Better Combat | Combat Roll | Same author (ZsoltMolnarrr); designed to work together — combat animations + dodge mechanics |
| Better Combat | playerAnimator | playerAnimator is a required dependency of Better Combat |
| Better Combat | Cloth Config API | Cloth Config is a required dependency of Better Combat |
| Better Combat | Fabric API | Better Combat requires Fabric API |
| Better Combat | Sodium | No overlap — combat animations vs. rendering optimization |
| Combat Roll | playerAnimator | playerAnimator is a required dependency of Combat Roll |
| Combat Roll | Cloth Config API | Cloth Config is a required dependency of Combat Roll |
| Combat Roll | Fabric API | Combat Roll requires Fabric API |
| Combat Roll | Sodium | No overlap — dodge mechanics vs. rendering optimization |
| Reforged | Simply Swords | Compatible — Reforged applies random quality modifiers to Simply Swords weapons (desirable interaction) |
| Reforged | Mythic Upgrades | Compatible — Reforged applies random quality modifiers to Mythic Upgrades gear (desirable interaction) |
| Reforged | UnionLib | UnionLib is a required dependency of Reforged |
| Reforged | Sodium | No overlap — item modifiers vs. rendering optimization |
| Reforged | Lithium | No overlap — item modifiers vs. game logic optimization |
| Artifacts | Trinkets | Artifacts optionally integrates with Trinkets API; now served via Accessories Compatibility Layer |
| Artifacts | Lootr | Compatible — Artifacts items found in structure chests work with Lootr's per-player instancing |
| Artifacts | Sodium | No overlap — accessory items vs. rendering optimization |
| Artifacts | Lithium | No overlap — accessory items vs. game logic optimization |
| Accessories | Sodium | No overlap — accessory slots vs. rendering optimization |
| Accessories | Fabric API | No overlap — accessory system vs. API |
| Mythic Upgrades | owo-lib | owo-lib is a required dependency of Mythic Upgrades |
| Mythic Upgrades | Fabric API | Mythic Upgrades requires Fabric API |
| Mythic Upgrades | Sodium | No overlap — new ores/gear vs. rendering optimization |
| Mythic Upgrades | Lithium | No overlap — new ores/gear vs. game logic optimization |
| Mythic Compat | Mythic Upgrades | Mythic Compat is a companion mod designed for Mythic Upgrades |
| Mythic Compat | Fabric API | Mythic Compat requires Fabric API |
| Equipment Compare | Iceberg | Iceberg is a required dependency of Equipment Compare |
| Equipment Compare | Fabric API | Equipment Compare requires Fabric API |
| Equipment Compare | Sodium | No overlap — tooltip comparison vs. rendering optimization |
| Equipment Compare | Simply Swords | Compatible — Equipment Compare shows comparison tooltips for Simply Swords weapons |
| Equipment Compare | Mythic Upgrades | Compatible — Equipment Compare shows comparison tooltips for Mythic Upgrades gear |
| Equipment Compare | Artifacts | Compatible — Equipment Compare shows comparison tooltips for Artifacts items |
| Enchanting Infuser | Easy Magic | Same author (Fuzs); complementary — Infuser adds a new block, Easy Magic improves the vanilla table |
| Enchanting Infuser | Puzzles Lib | Puzzles Lib is a required dependency of Enchanting Infuser |
| Enchanting Infuser | Forge Config API Port | Forge Config API Port is a required dependency of Enchanting Infuser |
| Enchanting Infuser | Fabric API | Enchanting Infuser requires Fabric API |
| Enchanting Infuser | Sodium | No overlap — enchanting mechanics vs. rendering optimization |
| Enchanting Infuser | Lithium | No overlap — enchanting mechanics vs. game logic optimization |
| Easy Magic | Puzzles Lib | Puzzles Lib is a required dependency of Easy Magic |
| Easy Magic | Forge Config API Port | Forge Config API Port is a required dependency of Easy Magic |
| Easy Magic | Fabric API | Easy Magic requires Fabric API |
| Easy Magic | Sodium | No overlap — enchanting QoL vs. rendering optimization |
| Easy Magic | Lithium | No overlap — enchanting QoL vs. game logic optimization |
| Enchantment Descriptions | Bookshelf | Bookshelf is a required dependency of Enchantment Descriptions |
| Enchantment Descriptions | Prickle | Prickle is a required dependency of Enchantment Descriptions |
| Enchantment Descriptions | Fabric API | Enchantment Descriptions requires Fabric API |
| Enchantment Descriptions | Sodium | No overlap — tooltip text vs. rendering optimization |
| Enchantment Descriptions | Equipment Compare | Compatible — both add tooltip information, no overlap |
| Easy Anvils | Puzzles Lib | Puzzles Lib is a required dependency of Easy Anvils |
| Easy Anvils | Forge Config API Port | Forge Config API Port is a required dependency of Easy Anvils |
| Easy Anvils | Fabric API | Easy Anvils requires Fabric API |
| Easy Anvils | Mendable Anvils | Complementary — Easy Anvils tweaks anvil mechanics, Mendable Anvils adds physical repair |
| Easy Anvils | Enchanting Infuser | Same author (Fuzs); no overlap — anvil vs. enchanting mechanics |
| Easy Anvils | Easy Magic | Same author (Fuzs); no overlap — anvil vs. enchanting table |
| Easy Anvils | Sodium | No overlap — anvil mechanics vs. rendering optimization |
| Easy Anvils | Lithium | No overlap — anvil mechanics vs. game logic optimization |
| Mendable Anvils | Fabric API | Mendable Anvils requires Fabric API |
| Mendable Anvils | Sodium | No overlap — anvil repair vs. rendering optimization |
| Mendable Anvils | Lithium | No overlap — anvil repair vs. game logic optimization |
| Spell Engine | Better Combat | Same author (ZsoltMolnarrr); designed to work together — spell animations integrate with Better Combat system |
| Spell Engine | playerAnimator | playerAnimator is a required dependency of Spell Engine |
| Spell Engine | Spell Power Attributes | Spell Power Attributes is a required dependency of Spell Engine |
| Spell Engine | Cloth Config API | Cloth Config is a required dependency of Spell Engine |
| Spell Engine | Fabric API | Spell Engine requires Fabric API |
| Spell Engine | Accessories | Spell Engine supports Accessories as its slot mod on Fabric |
| Spell Engine | Accessories Compatibility Layer | Compatible — Spell Engine works with Trinkets API via the compat layer |
| Spell Engine | Sodium | No overlap — magic system vs. rendering optimization |
| Spell Power Attributes | Fabric API | Spell Power Attributes requires Fabric API |
| Spell Power Attributes | Sodium | No overlap — entity attributes vs. rendering optimization |
| Runes | Spell Engine | Runes provides the ammo system consumed by Spell Engine spells |
| Runes | Bundle API | Bundle API is a required dependency of Runes |
| Runes | Fabric API | Runes requires Fabric API |
| Runes | Sodium | No overlap — spell ammo vs. rendering optimization |
| Wizards (RPG Series) | Spell Engine | Spell Engine is a required dependency of Wizards |
| Wizards (RPG Series) | Runes | Runes is a required dependency of Wizards |
| Wizards (RPG Series) | Better Combat | Strongly recommended; Better Combat provides proper weapon animations for wizard weapons |
| Wizards (RPG Series) | Paladins & Priests | Same author; designed to coexist — different RPG classes |
| Wizards (RPG Series) | Simply Swords | Compatible — Wizards adds wands/staves, Simply Swords adds melee weapons; different niches |
| Wizards (RPG Series) | AzureLib Armor | AzureLib Armor is a required dependency of Wizards |
| Wizards (RPG Series) | Structure Pool API | Structure Pool API is a required dependency of Wizards |
| Wizards (RPG Series) | Fabric API | Wizards requires Fabric API |
| Wizards (RPG Series) | Sodium | No overlap — magic classes vs. rendering optimization |
| Paladins & Priests | Spell Engine | Spell Engine is a required dependency of Paladins & Priests |
| Paladins & Priests | Runes | Runes is a required dependency of Paladins & Priests |
| Paladins & Priests | Better Combat | Strongly recommended; Better Combat provides first-person animations for paladin/priest weapons |
| Paladins & Priests | AzureLib Armor | AzureLib Armor is a required dependency of Paladins & Priests |
| Paladins & Priests | Structure Pool API | Structure Pool API is a required dependency of Paladins & Priests |
| Paladins & Priests | Fabric API | Paladins & Priests requires Fabric API |
| Paladins & Priests | Sodium | No overlap — magic classes vs. rendering optimization |
| Structure Pool API | Repurposed Structures | No overlap — structure pool injection API vs. structure variants |
| Structure Pool API | Towns and Towers | No overlap — structure pool injection API vs. village/outpost expansion |
| Spectrum | Fabric API | Spectrum requires Fabric API |
| Spectrum | Cloth Config API | Cloth Config is a required dependency of Spectrum |
| Spectrum | Accessories Compatibility Layer | Spectrum uses Trinkets API; works via Accessories Compat Layer |
| Spectrum | Revelationary | Revelationary is a required dependency of Spectrum |
| Spectrum | Modonomicon | Modonomicon is a required dependency of Spectrum |
| Spectrum | Sodium | No overlap — magic content vs. rendering optimization |
| Spectrum | Lithium | No overlap — magic content vs. game logic optimization |
| Spectrum | Terralith | No overlap — color magic vs. biome additions |
| Things | owo-lib | owo-lib is a required dependency of Things |
| Things | Lavender | Lavender is a required dependency of Things |
| Things | Accessories | Accessories is a required dependency of Things |
| Things | Artifacts | Compatible — both add accessory items; Things uses Accessories API, Artifacts uses Trinkets API via compat layer |
| Things | Sodium | No overlap — trinket items vs. rendering optimization |
| Things | Lithium | No overlap — trinket items vs. game logic optimization |
| Accessories | Accessories Compatibility Layer | Designed to work together — compat layer bridges Trinkets/Curios APIs to Accessories |
| Accessories | Artifacts | Compatible via Accessories Compatibility Layer — Artifacts uses Trinkets API which is wrapped by the compat layer |
| Accessories | Spell Engine | Compatible — Spell Engine supports Accessories directly as its slot mod |
| Wraith Waystones | Repurposed Structures | Compatible — Waystones spawn naturally in Repurposed Structures villages |
| Wraith Waystones | Towns and Towers | Compatible — Waystones spawn in villages expanded by Towns and Towers |
| Wraith Waystones | Terralith | No overlap — teleportation vs. biome additions |
| Wraith Waystones | Sodium | No overlap — teleportation vs. rendering optimization |
| Wraith Waystones | Lithium | No overlap — teleportation vs. game logic optimization |
| Wraith Waystones | Lootr | No overlap — teleportation vs. loot instancing |
| Creeper Healing | Sodium | No overlap — explosion healing vs. rendering optimization |
| Creeper Healing | Lithium | No overlap — explosion healing vs. game logic optimization |
| Creeper Healing | Fabric API | Creeper Healing requires Fabric API |
| Creeper Healing | Monkey Utils | Monkey Utils is a required dependency of Creeper Healing |
| Ledger | Fabric API | Ledger requires Fabric API |
| Ledger | Fabric Language Kotlin | Fabric Language Kotlin is a required dependency of Ledger |
| Ledger | Sodium | No overlap — block logging vs. rendering optimization |
| Ledger | Lithium | No overlap — block logging vs. game logic optimization |
| Open Parties and Claims | Fabric API | OPAC requires Fabric API |
| Open Parties and Claims | Forge Config API Port | Forge Config API Port is a required dependency of OPAC |
| Open Parties and Claims | Xaero's Minimap | Designed integration — OPAC claim boundaries display on Xaero's Minimap |
| Open Parties and Claims | Xaero's World Map | Designed integration — OPAC claim boundaries display on Xaero's World Map |
| Open Parties and Claims | Wraith Waystones | No overlap — chunk claims vs. teleportation |
| Open Parties and Claims | Sodium | No overlap — chunk claims vs. rendering optimization |
| Open Parties and Claims | Lithium | No overlap — chunk claims vs. game logic optimization |
| Carpet | Lithium | Compatible — Carpet provides rule toggles, Lithium optimizes game logic; complementary |
| Carpet | Sodium | No overlap — server rules vs. rendering optimization |
| Carpet | Spark | Complementary — Carpet tweaks mechanics, Spark profiles performance |
| Spark | Sodium | No overlap — profiling vs. rendering optimization |
| Spark | Lithium | No overlap — profiling vs. game logic optimization |
| Fabric Tailor | Sodium | No overlap — skin changing vs. rendering optimization |
| Fabric Tailor | Lithium | No overlap — skin changing vs. game logic optimization |
| Xaero's Minimap | Sodium | No overlap — minimap vs. rendering optimization |
| Xaero's Minimap | Lithium | No overlap — minimap vs. game logic optimization |
| Xaero's Minimap | Fabric API | Xaero's Minimap requires Fabric API |
| Xaero's Minimap | Xaero's World Map | Designed to work together — same author, shared waypoint system |
| Xaero's Minimap | Wraith Waystones | Compatible — waystones can appear as waypoints on minimap via integration mods |
| Xaero's World Map | Sodium | No overlap — world map vs. rendering optimization |
| Xaero's World Map | Lithium | No overlap — world map vs. game logic optimization |
| Xaero's World Map | Fabric API | Xaero's World Map requires Fabric API |
| Xaero's World Map | Wraith Waystones | Compatible — waystones can appear on world map via integration mods |
