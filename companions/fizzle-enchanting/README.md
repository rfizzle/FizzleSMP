# Meridian

**A complete enchanting overhaul for Minecraft 1.21.1 Fabric.**

Meridian replaces the vanilla enchanting table with a stat-driven system featuring five independent stats, 25+ themed shelf blocks, a two-tier enchantment library, salvage tomes, anvil upgrades, and 68 new enchantments — all built on vanilla's data-driven `EnchantmentEffectComponents` with no heavy runtime dependencies.

---

## Features

### Stat-Driven Enchanting Table

The vanilla enchanting table's single "power" value is replaced by five independent stats, each influencing a different aspect of the enchanting process:

| Stat | Effect |
|------|--------|
| **Eterna** | Maximum enchanting level (scales 0–50, replacing vanilla's 0–30 cap) |
| **Quanta** | Upper bound of the random power roll — higher means more variance |
| **Arcana** | Biases selection toward rarer, more obscure enchantments |
| **Rectification** | Reduces Quanta's negative variance for more consistent results |
| **Clues** | Reveals additional enchantments in the preview tooltip |

Stats are contributed by nearby shelf blocks and are fully data-driven — server operators can retune every block's contribution via datapack JSON without touching the jar.

### Shelf Blocks

25 themed shelves organized into progression tiers:

- **Starter** — Vanilla Bookshelves, Stoneshelf, Beeshelf, Melonshelf, Dormant Deepshelf
- **Early** — Hellshelf, Seashelf (Nether and Ocean materials)
- **Mid** — Infused and upgraded variants (Infused Hellshelf, Glowing Hellshelf, Blazing Hellshelf, Infused Seashelf, Crystal Seashelf, Heart Seashelf)
- **Late** — Deepshelf, Echoing Deepshelf, Soul-Touched Deepshelf, Echoing Sculkshelf, Soul-Touched Sculkshelf
- **End** — Endshelf, Pearl Endshelf, Draconic Endshelf (the only way to reach Eterna 50)
- **Utility** — Sightshelf (bonus Clues), Rectifier (bonus Rectification), Filtering Shelf (blacklist specific enchantments), Treasure Shelf (unlocks treasure enchantments like Mending)

Higher-tier shelves are crafted at the enchanting table itself using stat-gated recipes — building your shelf collection *is* the progression.

### Enchantment-Table Crafting

The enchanting table doubles as a stat-gated crafting station. When the table's stats meet a recipe's thresholds, the third enchant slot is replaced with the crafting result — click it to spend XP and craft the item. Recipes include:

- Tier-2 and tier-3 shelf upgrades
- Infused Breath (from Dragon's Breath — key material for end-tier shelves)
- Tome upgrades (Scrap → Improved Scrap → Extraction)
- Basic Library → Ender Library upgrade
- Budding Amethyst, Experience Bottles, Echo Shards, and more

### Enchantment Library

A two-tier storage block that pools enchanted books into a per-enchantment point bank, then dispenses them deterministically for XP:

- **Basic Library** — stores enchantments up to level 16
- **Ender Library** — stores enchantments up to level 31 (upgrade preserves stored books)

Points follow an exponential curve: `2^(level - 1)` per book deposited. The library tracks both accumulated points *and* the highest level ever deposited per enchantment — you can't grind thousands of Sharpness I books to pull a Sharpness V without first depositing at least one Sharpness V book.

Supports hopper automation for bulk book deposits. Extraction is menu-only.

### Salvage Tomes

Three tomes for moving enchantments between items, each with a different cost/reward tradeoff:

| Tome | Enchantments Recovered | Source Item | XP Cost |
|------|----------------------|-------------|---------|
| **Scrap Tome** | One (random) | Destroyed | 3 levels |
| **Improved Scrap Tome** | All | Destroyed | 5 levels |
| **Extraction Tome** | All | Preserved (takes durability damage) | 10 levels |

Tomes are used in the anvil: place the enchanted item on the left, the tome on the right.

### Anvil Upgrades

- **Prismatic Web** — Strips all curses from an item (30 levels, 1 web consumed). Non-curse enchantments are preserved.
- **Iron Block Repair** — Repairs a Damaged or Chipped Anvil by one tier (1 iron block consumed).

### 68 Enchantments

A roster of 68 enchantments spanning combat, tools, mobility, mounts, and more. All are pure JSON definitions against vanilla effect components — no custom Java code required.

Highlights include:

- **Combat** — Life Steal, Critical Hit, Attack Speed, Poison Aspect, Dimensional Strike, Last Hope, Fear, and more
- **Ranged** — Explosive Arrow, Storm Arrows, Echo Shot, Accuracy Shot, Breezing Arrows, Eternal Frost, Rebound
- **Tools** — Vein Miner, Mining+ (3x3 area), Harvest, Scyther
- **Mobility** — Agility, Step Assist, Lava Walker, Fast Swim, Leaping
- **Utility** — Builder Arm (extended reach), Bright Vision (night vision), Voidless (void safety), XP Boost, Midas Touch
- **Mounts** — Velocity, Steel Fang, Ethereal Leap, Cavalier Egis
- **Elytra** — Armored, Kinetic Protection

### Warden Loot

Wardens drop Warden Tendrils (1 guaranteed, +10% per Looting level for a second), the key material for crafting Sculk-tier shelves.

### Integrations

First-class recipe and tooltip adapters ship at launch for:

- **EMI**, **REI**, and **JEI** — enchanting-table crafting recipes, library mechanics
- **Jade** — shelf stat tooltips, library contents

### Advancement Tree

18 advancements guide players through the progression, from picking up their first shelf to reaching Eterna 50.

### Per-Enchantment Overrides

Server operators can override `maxLevel`, `maxLootLevel`, and `levelCap` for any enchantment (vanilla or modded) via the config file. Changes sync to clients automatically.

---

## Installation

### Requirements

- Minecraft **1.21.1**
- Fabric Loader **0.16.10+**
- Fabric API **0.116.1+1.21.1** or newer
- Java **21**

### Setup

Drop the jar into the `mods/` directory on both server and client. The mod must be present on **both sides** — a client missing the mod will desync on the first table interaction.

Quilt users can run the mod via Quilted Fabric API with no changes.

---

## Configuration

The mod generates `config/fizzle_enchanting.json` on first launch with sensible defaults. Every value can be tuned without a restart using `/fizzleenchanting reload`.

See the full annotated reference: **[Configuration Guide](docs/CONFIG.md)**

---

## Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/fizzleenchanting reload` | 2 | Reload config from disk |
| `/fizzleenchanting stats <player>` | 0 | Show stats of the enchanting table the player is looking at |
| `/fizzleenchanting library <player> dump` | 2 | Dump point contents of a library block |
| `/fizzleenchanting give-tome <player> <type>` | 2 | Give a tome item (debug/testing) |

---

## Building from Source

```sh
./gradlew build          # produces build/libs/fizzle-enchanting-<version>.jar
./gradlew test           # runs unit tests
./gradlew runDatagen     # regenerates src/main/generated/
```

---

## Credits & Attribution

Meridian is a clean-room 1.21.1 Fabric rewrite based on [Zenith](https://www.curseforge.com/minecraft/mc-mods/zenith) by bageldotjpg — the 1.20.1 Fabric port of [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) by Shadows_of_Fire. Zenith's stat schema, shelf roster, recipe shapes, and texture pipeline were the direct reference for Meridian's implementation. All code is a fresh 1.21.1 rewrite — no Zenith source was copied. The original enchanting module concepts (stat-driven table, shelf blocks, enchantment library, anvil interactions, and tome system) trace back to Apotheosis on Forge/NeoForge.

### [NeoEnchant+](https://www.curseforge.com/minecraft/mc-mods/neoenchant) — Hardel
49 of Meridian's enchantments are data-only namespace rewrites of NeoEnchant+ v5.14.0's JSON definitions. NeoEnchant+ is licensed under **CC BY-NC-SA 4.0**.

### [Enchantology](https://www.curseforge.com/minecraft/mc-mods/enchantology) — Various
6 enchantments (Certainty, Divinity, Vigilance, Oppression, Ironclad, Magic Protection) are inspired by Enchantology's designs, implemented as pure JSON.

### [Prominence 2](https://www.curseforge.com/minecraft/modpacks/prominence-2-rpg) — Various
Bag of Souls enchantment concept originates from the Prominence 2 modpack.

---

## License

- **Code:** MIT
- **Textures:** Sourced from Zenith (originally Apotheosis).
- **Enchantment data (49 of 68):** Ported from NeoEnchant+ under **CC BY-NC-SA 4.0**.
- **Enchantment data (12 of 68):** Ported from Zenith (originally Apotheosis).
- **Enchantment data (6 of 68):** Inspired by Enchantology designs, implemented as pure JSON.
- **Enchantment data (1 of 68):** Bag of Souls concept from Prominence 2.
