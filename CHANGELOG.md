# Changelog

All notable changes to **FizzleSMP** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).
Version numbers follow a pragmatic SemVer for modpacks:

- **MAJOR** — world-breaking changes (worldgen or dimension mods added/removed). Existing worlds may require reset or migration.
- **MINOR** — additive changes (new mods, new content). Safe to apply to an existing world.
- **PATCH** — config tweaks, mod version bumps, bug fixes, compatibility fixes.

## [Unreleased]

### Added

### Changed

### Fixed
- Savanna village house chests can now be filled. Better Loot 2.0+mc1.21.1 ships
  a `village_savanna_house.json` loot table that references `minecraft:grass`,
  which was renamed to `minecraft:short_grass` in MC 1.20.5. The broken table
  failed to parse on startup, causing every savanna house chest to error out
  with "loot table couldn't be resolved". Patched via a Paxi datapack override
  at `modpack/config/paxi/datapacks/BetterLoot-Savanna-Grass-Fix.zip`.

### Removed

## [1.0.0] - 2026-04-11

### Added
- Initial release shipped to SMP players. Full mod list tracked in `plugins/*.md`
  and `modpack/mods/*.pw.toml`. See `git log` for the detailed commit history
  that built up the 1.0.0 mod list.
