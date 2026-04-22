# Fizzle Enchanting

A companion Fabric mod for **FizzleSMP** (Minecraft 1.21.1) that overhauls vanilla enchanting with a stat-driven table, themed shelf roster, anvil tweaks, a two-tier library, and salvage tomes.

It is a clean-room 1.21.1 rewrite of the enchanting module from [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) (Shadows_of_Fire, Forge/NeoForge) and its outdated Fabric port [Zenith](https://www.curseforge.com/minecraft/mc-mods/zenith) (bageldotjpg, MC 1.20.1), rebuilt on vanilla `EnchantmentEffectComponents` with no runtime dependencies beyond Fabric API.

## Features

- **Stat-driven enchanting table** — five independent stats (Eterna, Quanta, Arcana, Rectification, Clues) replace vanilla's single "power" value. Stat contributions per block live in datapack JSON.
- **25 shelf blocks** — biome-themed tiers (wood, stone, nether, ocean, end, deep/sculk) plus utility shelves (sightshelf, rectifier) and special shelves (filtering, treasure).
- **Enchantment-table crafting** — stat-gated recipes produce tier-3 shelves, `infused_breath`, tome upgrades, and the Ender Library. Two recipe types: `fizzle_enchanting:enchanting` and `fizzle_enchanting:keep_nbt_enchanting`.
- **Two-tier Enchantment Library** — Basic (level cap 16) and Ender (level cap 31) blocks pool enchanted books into a per-enchantment point bank, then dispense them deterministically for XP.
- **Anvil tweaks** — Prismatic Web strips curses (30 levels, 1 web), and a block of iron repairs a damaged/chipped anvil by one tier.
- **Salvage tomes** — Scrap (one random enchant, item destroyed), Improved Scrap (all enchants, item destroyed), Extraction (all enchants, item preserved).
- **51 MVP enchantments** — 49 ports from NeoEnchant+ (Hardel) plus 2 authored by us (Icy Thorns, Shield Bash), all pure JSON against vanilla effect components.
- **Foreign-enchant overrides** — bundled datapack raises weights on `minecraft:mending` and `yigd:soulbound` so they feed the library loop.
- **Warden loot modifier** — 1 guaranteed `warden_tendril` drop (+10%/looting for a second), required for sculkshelf crafts.
- **Integrations** — first-class EMI, REI, JEI, and Jade adapters shipped at launch.
- **Advancement tree** — ten-advancement progression from root shelf pickup to Eterna 50 (`apotheosis`).

## Configuration

- **Path:** `config/fizzle_enchanting.json` (inside the Fabric instance's config directory — `FabricLoader.getConfigDir()`).
- **Generated on first launch** with sensible defaults; out-of-range values are clamped with a `LOGGER.warn` on load.
- **Hot reload** via `/fizzleenchanting reload` (permission level 2) — rereads the file from disk without a server restart.

Full annotated reference: [`docs/CONFIG.md`](docs/CONFIG.md).

## Commands

| Command | Permission | Description |
|---|---|---|
| `/fizzleenchanting reload` | 2 | Reload config from disk |
| `/fizzleenchanting stats <player>` | 0 | Dump stats of the enchanting table the player is looking at |
| `/fizzleenchanting library <player> dump` | 2 | Dump point contents of the library the player is looking at |
| `/fizzleenchanting give-tome <player> <type>` | 2 | Debug helper for testing tome flows |

## Requirements

- Minecraft **1.21.1**
- Fabric Loader **0.16.10+**
- Fabric API **0.116.1+1.21.1** or newer
- Java **21**

Quilt users can run the mod transparently via Quilted Fabric API; no separate artifact is shipped.

## Installation

Drop the built jar into the server and client `mods/` directory. The mod is `environment: "*"` and must be present on **both sides** — client-missing setups desync on the first table interaction.

The mod is distributed as a FizzleSMP-local jar (`Mod Loader: Manual` in the packwiz plugin list). A public Modrinth/CurseForge release is a later decision; see "License & Credits" below for the compliance gate.

## Build

```sh
cd companions/fizzle-enchanting
./gradlew build                 # builds build/libs/fizzle-enchanting-<version>.jar
./gradlew test                  # runs unit tests
./gradlew runDatagen            # regenerates src/main/generated/
```

## Documentation

- [`DESIGN.md`](DESIGN.md) — architecture, scope, stat system, shelf/library/anvil/tome details, iteration backlog.
- [`docs/CONFIG.md`](docs/CONFIG.md) — annotated config reference.
- [`docs/PLAYTHROUGH.md`](docs/PLAYTHROUGH.md) — manual QA checklist.

## License & Credits

- **Code:** MIT.
- **Textures:** Reused from Apotheosis (Shadows_of_Fire) via Zenith (bageldotjpg) under Apotheosis's separate `LICENSE_ASSETS`. Private SMP distribution is in bounds; a public release requires a compliance pass.
- **Enchantment JSONs + lang keys:** 49 of the 51 MVP enchantments are ported from **NeoEnchant+** by Hardel, licensed **CC BY-NC-SA 4.0**. Redistribution on a public platform would either require dual-licensing the `data/` tree or re-authoring those JSONs from scratch.

**Credits:**

- **Apotheosis** — Shadows_of_Fire — original enchanting module design, mechanics, and asset source.
- **Zenith** — bageldotjpg — 1.20.1 Fabric port of Apotheosis; stat schema, shelf roster, recipe shapes, and texture pipeline all trace back here.
- **NeoEnchant+** — Hardel — 49 of the 51 MVP enchantments' JSON + lang entries (CC BY-NC-SA 4.0).
