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

### Removed

## [1.4.0] - 2026-04-16
### Added
- Amecs — enables multi-key keybind combos (modifier + key chords) for players who need more bindable actions than vanilla allows
- Inventory Profiles Next — replaces Inventory Essentials as the sorting/management mod. Configured with alphabetical (`ITEM_NAME`) sort as the default, slimmed in-GUI controls, and a `rules-v2` override that neutralizes the creative-menu sort rule (which freezes clients on first use per blackd/Inventory-Profiles#440)

### Changed
- Removed `options.txt` from packwiz overrides so the client no longer overwrites player-customized Minecraft settings on update
- guy's Armor HUD — ship a default config that anchors the widget to the bottom-left in vertical orientation (offset 0,0) instead of the horizontal hotbar-side default

### Removed
- Inventory Essentials — swapped out in favor of Inventory Profiles Next

## [1.3.3] - 2026-04-15
### Removed
- Gear Tinkering — removed from the pack. Spark profiling traced ~77% of server-thread tick time to vanilla command dispatch driven by its bundled `gear_tinkering:tick` datapack, which runs `execute as @e[type=item, nbt={OnGround:1b, Item:{count:1}}] …` every tick (an unbounded `@e` scan with NBT predicate across every loaded chunk). Grind Enchantments already covers the core enchantment-extraction workflow without a tick datapack.
- Creeper Healing (and its Monkey Utils dependency) — removed proactively alongside Gear Tinkering. Not implicated in the spark report, but the explosion-restore workflow is non-essential and trimming server-side mods reduces tick budget pressure.

## [1.3.2] - 2026-04-14
### Removed
- Infinite Trading — removed from the pack. Combined with Pickable Villagers' housed traders, unlimited trade volume caused vanilla `demandBonus` to inflate prices far beyond the intended base (e.g. iron 4 → 25 per emerald); vanilla's lockout is the safety valve keeping demand in check, so removing the mod restores balance

## [1.3.1] - 2026-04-14
### Fixed
- RpgDifficulty side classification: was shipped as `server`-only in v1.3.0, but the mod modifies entity attributes/tracked data and must run on the client too. Missing it on the client caused a `DataTracker` `ArrayIndexOutOfBoundsException` crash when the server sent tracked data for a scaled mob. Flipped to `both` so the client pack includes it.

## [1.3.0] - 2026-04-14
### Added
- RpgDifficulty — distance-based mob scaling that complements HMIOT's time-based curve; 1000-block safe ring at spawn, damage capped at 2×, speed at 1.3×

### Changed
- Bosses of Mass Destruction: doubled HP across all four bosses and buffed attack/armor/healing to stay threatening at high HMIOT progression

### Fixed
- Easy Mob Farm: blacklisted bosses (Ender Dragon, Wither, Warden, Elder Guardian, BMD, Illager Invasion, Mutant Monsters, Friends & Foes) from all catchers to prevent exploits like Warden grinders; raised buffer to 1024 with overflow pausing instead of item entity spawning to eliminate lag

## [1.2.1] - 2026-04-14
### Added
- `EnchantingInfuser-Mending-Soulbound.zip` datapack — adds Mending and YIGD Soulbound to the `enchantinginfuser:in_enchanting_infuser` and `in_advanced_enchanting_infuser` tags so both enchantments can be applied at the infuser
- `config/enchantinginfuser-server.toml` — raises `maximumCost` to 100 (normal infuser) and 150 (advanced infuser) to rebalance the infuser's XP cost

### Changed

### Fixed
- `NeoEnchant-AutoSmelt-Disable.zip` datapack — changed enchantment `weight` from `0` to `1` to satisfy the 1.21.1 enchantment codec range `[1, 1024]`, which was causing server startup to fail during registry load (`slots` and `effects` remain empty so the enchantment is still effectively disabled)

### Removed

## [1.2.0] - 2026-04-12
### Added
- Lighty — light overlay mod with carpet-style, number, and cross visualization modes (F7/F8 toggle)
- Euphoria Patches — add-on for Complementary Shaders with extra visual features and settings
- GriefLogger — SQLite/MySQL-backed player interaction logger, replacing Ledger
- SuperMartijn642's Config Lib — required dependency for GriefLogger
- Infinite Trading — prevents villager and wandering trader trades from locking up

### Changed

### Fixed
- Server crash caused by Ledger's `ledgerCloseScreenLogChanges` mixin NPE when players interacted with Easy Mob Farm containers — resolved by replacing Ledger with GriefLogger
- NeoEnchant+ Auto Smelt enchantment producing ingots with extra data components that don't stack — disabled via datapack override (upstream closed as Not Planned: Hardel-DW/NeoEnchant#73); Artifacts' Smelting item covers the use case
- CurseForge export rejecting Artifacts and Mod Menu as overrides — reinstalled both from CurseForge source; corrected Artifacts CurseForge project ID (312353, was 401236)
- YIGD Soulbound enchantment unable to be applied to Traveler's Backpack, Artifacts, and Jewelry items — extended `yigd:soulbindable` item tag via datapack to include modded item tags

### Removed
- Ledger — abandoned on 1.21.1 (last update Nov 2024), crashes with Easy Mob Farm's custom containers

## [1.1.0] - 2026-04-12
### Added
- Fabric Seasons: Extras — greenhouse blocks (Glass, Heater, Chiller), Season Detector, and Season Calendar
- Fabric Seasons: Delight Refabricated Compat — replaces Delight Compat with a fork that correctly targets FD Refabricated crop classes
- Dark Utilities — added for vector plates (4 speed tiers for entity/item transport in mob farms); all other content disabled via Item Obliterator
- Gear Tinkering — adds new smithing table uses: repair cost resetting (amethyst shards), trim removal, curse removal, enchantment transferring, and book unsigning

### Changed
- Easy Mob Farm: buffed tier progression — base cycle reduced to 4 min (from 5), tier speed bonuses increased significantly (T1: 2.5 min, T2: 1.7 min, T3: 1 min per cycle)

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
