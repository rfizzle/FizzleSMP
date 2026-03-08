# Compatibility Matrix — FizzleSMP

Tracks known conflicts between mods in the pack and how to resolve them.

## Legend

| Symbol | Meaning |
|--------|---------|
| :x: | Hard conflict — crashes, data corruption, or fundamentally incompatible |
| :warning: | Soft conflict — overlapping features or config tuning needed |

## Hard Conflicts

| Mod A | Mod B | Details |
|---|---|---|
| Simply Swords (≥1.63.0) | Alexandria | Simply Swords 1.63.0+ added a hard dependency on Simply Tooltips, which crashes with Alexandria (Accessories screen crash). **Pin Simply Swords to ≤1.62.0** (`Pin CurseForge File ID: 6958140` in plugins/combat.md) to avoid pulling in Simply Tooltips. |

## Soft Conflicts

| Mod A | Mod B | Details | Resolution |
|---|---|---|---|
| YUNG's suite | Repurposed Structures | Overlapping structure types: dungeons, mineshafts, desert temples, jungle temples, witch huts | **Resolved** — `RS-YUNG-Compat-Config.zip` datapack (in `modpack/config/paxi/datapacks/`) disables RS overworld mineshafts, pyramids, temples, witch huts, and overworld dungeons. YUNG's handles those; RS keeps its unique structures (shipwrecks, mansions, igloos, ruins, cities, villages, etc.) and all nether/end variants. |
| Geophilic | Terralith | Both modify vanilla biome files; features may override each other | Use Terraphilic compatibility pack (must load after both mods) |
| Hopo Better Underwater Ruins | Repurposed Structures | May overlap on ocean ruin variants | Check RS config to disable overlapping ocean ruin types; let Hopo handle underwater ruins. |
| Inventory Profiles Next | Mouse Tweaks | Both modify inventory click/drag behavior | Adjust keybinds if defaults overlap; IPN handles sorting/auto-refill, Mouse Tweaks handles drag-splitting. |
| Illager Invasion | Friends & Foes | Both add illager-adjacent mobs (new illagers vs Iceologer/Illusioner); both modify raid/patrol systems | Test raid mechanics together; disable overlapping mob spawns if duplicates appear in config. |
| Creeper Overhaul | Creeper Healing | Both modify creeper entities (visual variants vs explosion aftermath) | Generally compatible — different aspects of creeper behavior. Verify biome variants still trigger healing. |
| Carry On | Lootr | Carry On can pick up Lootr chests; carried chests may not retain per-player loot instancing, causing teammates to be unable to open them ([#705](https://github.com/Tschipp/CarryOn/issues/705)) | Configure Carry On to blacklist Lootr chest block entities, or accept that carried loot chests lose instancing. |
| Grind Enchantments | Easy Anvils | Both deal with enchantment management but use different blocks (grindstone vs anvil) | No conflict — Grind Enchantments modifies the grindstone, Easy Anvils modifies the anvil. |
| SwingThrough | Better Combat | Both modify attack targeting (SwingThrough allows hitting through transparent blocks, Better Combat changes hit detection to weapon arcs) | Should be complementary — verify that swing-through-grass works correctly with Better Combat's directional attack system. |
| Farmer's Delight | RightClickHarvest | Both interact with crop harvesting mechanics; FD adds custom crops that RCH may not automatically support | Test that right-click harvest works on FD crops (tomatoes, cabbages, etc.); may need config or datapack support. RCH Supplementaries Compat handles Supplementaries crops (Flax). |
| Simple Voice Chat | Sound Physics Remastered | Explicit integration — SVC uses SPR for directional voice audio and sound occlusion through blocks | Enable SPR integration in SVC config for immersive proximity voice. |
| Krypton | Connectivity | Both modify the networking stack (Krypton optimizes packet compression/flushing, Connectivity fixes timeouts/login issues) | Generally compatible — different networking layers. Monitor for unexpected disconnects after adding both. |
| C2ME | Lithium | C2ME's multithreaded chunk generation concurrently accesses Lithium's non-thread-safe `GameEventDispatcherStorage` hashmap, causing `ArrayIndexOutOfBoundsException` during worldgen (especially with feature-heavy mods like YUNG's Cave Biomes) | **Resolved** — disabled Lithium's `mixin.world.game_events` in `modpack/config/lithium.properties`. Minimal performance impact (only affects sculk sensor/warden game event dispatch). |
| C2ME | Noisium | Both optimize chunk generation (C2ME parallelizes chunk operations, Noisium optimizes worldgen algorithms) | Complementary — C2ME handles threading, Noisium handles algorithm efficiency. Widely used together without issues. |
| Loot Integrations | Lootr | Both modify loot table behavior (LI enriches loot contents, Lootr handles per-player instancing) | Complementary — LI modifies what loot appears in chests, Lootr handles per-player access. No conflict. |
| YUNG's Structures Addon for LI | YUNG's suite | Addon designed specifically for YUNG's structure mods; requires Loot Integrations | Designed integration — enhances loot in all YUNG's structures. |
| Spectrum | Tech Reborn | Spectrum bundles optional Tech Reborn integration recipes (grinder/industrial grinder for Spectrum ores) using an outdated recipe JSON format — bare string ingredients and `results`/`tank` keys instead of Tech Reborn's current `outputs`/`fluid` keys with `SizedIngredient` objects | **Resolved** — `Spectrum-TechReborn-Recipe-Fix.zip` datapack (in `modpack/config/paxi/datapacks/`) overrides the 3 broken recipes with corrected format. |
| Mythic Compat | Mythic Upgrades | Mythic Compat's cross-tier smithing recipes reference `mythicupgrades:netherite_*` items that don't exist in the current Mythic Upgrades version, causing ~27 recipe parsing errors at startup | No fix available — Mythic Compat is at latest version. Errors are non-fatal (recipes silently fail to load). Consider removing Mythic Compat if the log spam is undesirable. |
