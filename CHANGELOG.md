# Changelog

All notable changes to **FizzleSMP** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Version numbers follow a pragmatic SemVer for modpacks:

- **MAJOR** — world-breaking changes (worldgen or dimension mods added/removed). Existing worlds may require reset or migration.
- **MINOR** — additive changes (new mods, new content). Safe to apply to an existing world.
- **PATCH** — config tweaks, mod version bumps, bug fixes, compatibility fixes.

## [Unreleased]

### Added
- Fabric Seasons: Extras — greenhouse blocks (Glass, Heater, Chiller), Season Detector, and Season Calendar
- Fabric Seasons: Delight Refabricated Compat — replaces Delight Compat with a fork that correctly targets FD Refabricated crop classes
- Dark Utilities — added for vector plates (4 speed tiers for entity/item transport in mob farms); all other content disabled via Item Obliterator

### Changed

### Fixed
- Farmer's Delight crops now respect seasonal growth rates (rice, tomato, cabbage) — old Delight Compat had broken mixins targeting non-existent FD Refabricated class names
- Spectrum x Mythic Upgrades integration errors — MU 4.x removed budding crystal blocks that Spectrum's Crystal Apothecary and guidebook referenced; added `Spectrum-MythicUpgrades-Fix.zip` datapack to suppress broken recipes and fix guidebook icon

### Removed

## [1.0.1] - 2026-04-11
### Added

### Changed

### Fixed
- Savanna village house chest loot table fix with Lootr.
- Spectrum enchantment upgrade recipes now load alongside Mr. BeyondEnchant.

### Removed
- Carry On removed due to a `sync_carry_data` crash loop on 1.21.1.

## [1.0.0] - 2026-04-11

### Added
- Initial release shipped to SMP players. Full mod list tracked in `plugins/*.md`
  and `modpack/mods/*.pw.toml`. See `git log` for the detailed commit history
  that built up the 1.0.0 mod list.
