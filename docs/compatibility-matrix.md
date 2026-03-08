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
| Hopo Better Underwater Ruins | Repurposed Structures | May overlap on ocean ruin variants | Check RS config to disable overlapping ocean ruin types; let Hopo handle underwater ruins. |
| Athena | Continuity | Both provide connected texture functionality | No actual conflict — Athena is a library used by Oritech for its own block textures, Continuity handles vanilla/Sodium connected textures. They operate independently. |

## Verified Compatible

<!-- Pairs with a meaningful interaction surface — mods that touch the same system, have explicit integrations, or were specifically tested together. Do NOT add dependency relationships (already tracked in plugin files), trivial "no overlap" pairings between unrelated systems (e.g. a content mod vs. a rendering optimizer), or entries that duplicate soft conflict resolutions above. -->

| Mod A | Mod B | Notes |
|---|---|---|
| Sodium | ImmediatelyFast | Complementary optimizations — tested together in benchmarks |
| Sodium | Entity Culling | Entity Culling extends Sodium's chunk-based visibility with finer entity-level culling |
| Lithium | Sodium | Complementary optimizations — Lithium handles game logic, Sodium handles rendering |
| ImmediatelyFast | Entity Culling | Confirmed compatible in testing |
| Iris Shaders | ImmediatelyFast | Tested together alongside Sodium |
| Iris Shaders | Entity Culling | Confirmed compatible in testing |
| FerriteCore | Sodium | Complementary — FerriteCore optimizes memory, Sodium optimizes rendering |
| FerriteCore | Lithium | Complementary — memory vs. game logic optimizations |
| FerriteCore | ModernFix | Complementary — both reduce memory but target different areas |
| ModernFix | Sodium | Compatible per ModernFix description; complementary optimizations |
| ModernFix | Lithium | Compatible per ModernFix description; complementary optimizations |
| Noisium | Sodium | Confirmed compatible per Noisium docs |
| Noisium | Lithium | Confirmed compatible per Noisium docs |
| Noisium | All worldgen mods | Noisium optimizes worldgen without altering output; universally compatible |
| Continuity | Sodium | Designed to work together — Continuity provides connected textures for Sodium |
| Continuity | Iris Shaders | Compatible — connected textures work alongside shaders |
| Blur+ | Iris Shaders | Compatible — Blur+ works with shader mods |
| AppleSkin | Colorful Hearts | Explicitly supported — Colorful Hearts has built-in AppleSkin compatibility for health restoration visualization |
| Traveler's Backpack | Inventory Profiles Next | IPN can sort Traveler's Backpack inventories |
| Iron Chests | Metal Barrels | Complementary — chests and barrels, no overlap |
| Lootr | All structure/content mods | Operates at container level; universally compatible with structure mods |
| Artifacts | Lootr | Compatible — Artifacts items found in structure chests work with Lootr's per-player instancing |
| Terralith | Tectonic | Tectonic mod auto-loads Terratonic compatibility when Terralith is detected |
| Terralith | Repurposed Structures | RS spawns structures in Terralith biomes; complementary |
| Terralith | Towns and Towers | Explicitly confirmed compatible by Towns and Towers |
| Repurposed Structures | Towns and Towers | Explicitly confirmed compatible; different structure types |
| YUNG's Better Caves | Terralith | Compatible — Better Caves overhauls cave shapes, Terralith adds biomes |
| YUNG's Better Caves | Tectonic | Compatible — cave generation and terrain shaping don't conflict |
| YUNG's Better Mineshafts | Terralith | Compatible — biome-specific mineshaft variants work with Terralith biomes |
| YUNG's Cave Biomes | YUNG's Better Caves | Same author; designed to work together — Cave Biomes adds biome types, Better Caves overhauls shapes |
| YUNG's Cave Biomes | Terralith | Compatible — cave biomes complement Terralith's overworld biomes |
| Geophilic | Tectonic | Compatible — Geophilic modifies biome vegetation, Tectonic modifies terrain shape |
| Geophilic | Terraphilic | Terraphilic resolves biome file conflicts between Geophilic and Terralith |
| Terraphilic | Terralith | Terraphilic is designed specifically for Terralith+Geophilic compatibility |
| Incendium | Terralith | Same author (Stardust Labs); designed to work together — different dimensions |
| Incendium | Nullscape | Same author (Stardust Labs); designed to work together — different dimensions |
| Nullscape | Terralith | Same author (Stardust Labs); designed to work together — different dimensions |
| Structory | Terralith | Compatible — Structory structures spawn in Terralith biomes |
| Structory: Towers | Terralith | Compatible — towers spawn in Terralith biomes |
| Structory: Towers | Structory | Designed as a companion add-on to Structory |
| Sparse Structures | All structure mods | Designed to work with all structure mods; adjusts spacing/separation globally |
| Wraith Waystones | Repurposed Structures | Compatible — Waystones spawn naturally in Repurposed Structures villages |
| Wraith Waystones | Towns and Towers | Compatible — Waystones spawn in villages expanded by Towns and Towers |
| Traveler's Titles | Terralith | Compatible — Traveler's Titles displays biome names from Terralith |
| Simply Swords | Better Combat | Officially partnered — Better Combat provides unique attack animations for all Simply Swords weapon types |
| Better Combat | Combat Roll | Same author (ZsoltMolnarrr); designed to work together — combat animations + dodge mechanics |
| Reforged | Simply Swords | Compatible — Reforged applies random quality modifiers to Simply Swords weapons (desirable interaction) |
| Reforged | Mythic Upgrades | Compatible — Reforged applies random quality modifiers to Mythic Upgrades gear (desirable interaction) |
| Accessories | Artifacts | Compatible via Accessories Compatibility Layer — Artifacts uses Trinkets API which is wrapped by the compat layer |
| Accessories | Spell Engine | Compatible — Spell Engine supports Accessories directly as its slot mod |
| Things | Artifacts | Compatible — both add accessory items; Things uses Accessories API, Artifacts uses Trinkets API via compat layer |
| Spectrum | Accessories Compatibility Layer | Spectrum uses Trinkets API; works via Accessories Compat Layer |
| Spell Engine | Better Combat | Same author (ZsoltMolnarrr); designed to work together — spell animations integrate with Better Combat system |
| Runes | Spell Engine | Runes provides the ammo system consumed by Spell Engine spells |
| Wizards (RPG Series) | Better Combat | Strongly recommended; Better Combat provides proper weapon animations for wizard weapons |
| Wizards (RPG Series) | Paladins & Priests | Same author; designed to coexist — different RPG classes |
| Wizards (RPG Series) | Simply Swords | Compatible — Wizards adds wands/staves, Simply Swords adds melee weapons; different niches |
| Paladins & Priests | Better Combat | Strongly recommended; Better Combat provides first-person animations for paladin/priest weapons |
| Equipment Compare | Simply Swords | Compatible — Equipment Compare shows comparison tooltips for Simply Swords weapons |
| Equipment Compare | Mythic Upgrades | Compatible — Equipment Compare shows comparison tooltips for Mythic Upgrades gear |
| Equipment Compare | Artifacts | Compatible — Equipment Compare shows comparison tooltips for Artifacts items |
| Enchantment Descriptions | Equipment Compare | Compatible — both add tooltip information, no overlap |
| Easy Anvils | Mendable Anvils | Complementary — Easy Anvils tweaks anvil mechanics, Mendable Anvils adds physical repair |
| Open Parties and Claims | Xaero's Minimap | Designed integration — OPAC claim boundaries display on Xaero's Minimap |
| Open Parties and Claims | Xaero's World Map | Designed integration — OPAC claim boundaries display on Xaero's World Map |
| Xaero's Minimap | Xaero's World Map | Designed to work together — same author, shared waypoint system |
| Xaero's Minimap | Wraith Waystones | Compatible — waystones can appear as waypoints on minimap via integration mods |
| Xaero's World Map | Wraith Waystones | Compatible — waystones can appear on world map via integration mods |
| Carpet | Lithium | Compatible — Carpet provides rule toggles, Lithium optimizes game logic; complementary |
| Carpet | Spark | Complementary — Carpet tweaks mechanics, Spark profiles performance |
| Pickable Villagers | Villager Names | Compatible — picked-up villagers retain their assigned names |
| Villager Names | Trade Cycling | Compatible — named villagers display names in the trade cycling UI |
| Oritech | Tech Reborn | Built-in cross-mod compatibility — Oritech machines can process Tech Reborn materials and vice-versa |
| Simple Conveyor Belts | Iris Shaders | Shader-compatible per mod description |
| Simple Conveyor Belts | All item storage mods | Designed to work with any item storage mod (chests, barrels, machines) |
| BetterF3 | MiniHUD | Complementary — BetterF3 replaces the F3 debug screen, MiniHUD adds separate configurable overlays; no overlap |
| Polymorph | All recipe-adding mods | Designed to resolve recipe conflicts from any mod; universally compatible |
| Polymorph | EMI | EMI has built-in Polymorph integration — recipe conflict choices appear directly in the EMI viewer |
| EMI Addon: Extra Mod Integrations | Tech Reborn | Provides EMI recipe integration for Tech Reborn machines and processing chains |
| Chipped | Polymorph | Chipped adds many crafting recipes via workbenches; Polymorph resolves any recipe conflicts |
| Magnum Torch | Nether Chested | Both from Fuzs; designed to coexist, no overlap (mob spawning vs. storage) |
| Sound Physics Remastered | Presence Footsteps | Compatible — Sound Physics applies reverb/occlusion to Presence Footsteps' custom step sounds |
| Sound Physics Remastered | AmbientSounds | Compatible — Sound Physics applies reverb/occlusion to AmbientSounds' environmental audio |
| Presence Footsteps | AmbientSounds | Compatible — footstep sounds and ambient environmental sounds operate independently |
| Not Enough Animations | Better Combat | Compatible — NEA defers to Better Combat's animations when both are present |
| Not Enough Animations | First-person Model | Same author (tr7zw); designed to work together — First-person Model requires NEA |
| Not Enough Animations | playerAnimator | Compatible — NEA respects playerAnimator's custom animations (used by Better Combat/Spell Engine) |
| First-person Model | Better Combat | Compatible — first-person body model displays Better Combat animations correctly |
| First-person Model | Better Third Person | Compatible — independent mods targeting different camera perspectives |
| Visuality | Sodium | Compatible — Visuality's particles render correctly with Sodium |
