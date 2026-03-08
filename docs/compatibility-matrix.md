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
| *(none currently)* | | |

## Soft Conflicts

| Mod A | Mod B | Details | Resolution |
|---|---|---|---|
| YUNG's suite | Repurposed Structures | Overlapping structure types: dungeons, mineshafts, desert temples, jungle temples, witch huts | Disable overlapping structure types in RS config; let YUNG's mods handle those. Keep RS enabled for its unique shipwrecks, mansions, igloos, and ruins variants. |
| Geophilic | Terralith | Both modify vanilla biome files; features may override each other | Use Terraphilic compatibility pack (must load after both mods) |
| Hopo Better Underwater Ruins | Repurposed Structures | May overlap on ocean ruin variants | Check RS config to disable overlapping ocean ruin types; let Hopo handle underwater ruins. |
| Inventory Profiles Next | Mouse Tweaks | Both modify inventory click/drag behavior | Adjust keybinds if defaults overlap; IPN handles sorting/auto-refill, Mouse Tweaks handles drag-splitting. |
| Illager Invasion | Friends & Foes | Both add illager-adjacent mobs (new illagers vs Iceologer/Illusioner); both modify raid/patrol systems | Test raid mechanics together; disable overlapping mob spawns if duplicates appear in config. |
| Creeper Overhaul | Creeper Healing | Both modify creeper entities (visual variants vs explosion aftermath) | Generally compatible — different aspects of creeper behavior. Verify biome variants still trigger healing. |
| Carry On | Lootr | Carry On can pick up Lootr chests; carried chests may not retain per-player loot instancing, causing teammates to be unable to open them ([#705](https://github.com/Tschipp/CarryOn/issues/705)) | Configure Carry On to blacklist Lootr chest block entities, or accept that carried loot chests lose instancing. |
| Easy Disenchanting | Easy Anvils | Both modify anvil behavior (Easy Disenchanting adds enchantment transfer recipes, Easy Anvils removes level cap and adds QoL) | Should be complementary — test that disenchanting works correctly with Easy Anvils' modified anvil mechanics. |
| SwingThrough | Better Combat | Both modify attack targeting (SwingThrough allows hitting through transparent blocks, Better Combat changes hit detection to weapon arcs) | Should be complementary — verify that swing-through-grass works correctly with Better Combat's directional attack system. |
| Farmer's Delight | RightClickHarvest | Both interact with crop harvesting mechanics; FD adds custom crops that RCH may not automatically support | Test that right-click harvest works on FD crops (tomatoes, cabbages, etc.); may need config or datapack support. |
| Simple Voice Chat | Sound Physics Remastered | Explicit integration — SVC uses SPR for directional voice audio and sound occlusion through blocks | Enable SPR integration in SVC config for immersive proximity voice. |
| Krypton | Connectivity | Both modify the networking stack (Krypton optimizes packet compression/flushing, Connectivity fixes timeouts/login issues) | Generally compatible — different networking layers. Monitor for unexpected disconnects after adding both. |
| C2ME | Noisium | Both optimize chunk generation (C2ME parallelizes chunk operations, Noisium optimizes worldgen algorithms) | Complementary — C2ME handles threading, Noisium handles algorithm efficiency. Widely used together without issues. |
| Loot Integrations | Lootr | Both modify loot table behavior (LI enriches loot contents, Lootr handles per-player instancing) | Complementary — LI modifies what loot appears in chests, Lootr handles per-player access. No conflict. |
| YUNG's Structures Addon for LI | YUNG's suite | Addon designed specifically for YUNG's structure mods; requires Loot Integrations | Designed integration — enhances loot in all YUNG's structures. |
