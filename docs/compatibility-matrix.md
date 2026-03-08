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
