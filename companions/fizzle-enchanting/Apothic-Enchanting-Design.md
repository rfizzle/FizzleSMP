# Apothic Enchanting - Comprehensive Design Reference

> **Purpose:** This document exhaustively catalogs every feature, system, and mechanic in Apothic Enchanting (v1.5.2, NeoForge 1.21.1) to serve as a comparison guide and implementation design driver for Fizzle Enchanting.
>
> **Source repo:** `~/Projects/Apothic-Enchanting`
> **Author:** Shadows_of_Fire (shadowsoffire)
> **Mod ID:** `apothic_enchanting`
> **Dependencies:** Placebo, Apothic Attributes
> **Mod loader:** NeoForge 21.1.187 (Java 21)

---

## Table of Contents

1. [Enchanting Table Overhaul](#1-enchanting-table-overhaul)
2. [The Three Primary Stats](#2-the-three-primary-stats)
3. [Enchanting Table GUI](#3-enchanting-table-gui)
4. [Bookshelf / Shelf System](#4-bookshelf--shelf-system)
5. [Special Shelves](#5-special-shelves)
6. [Infusion Recipe System](#6-infusion-recipe-system)
7. [Enchantment Library](#7-enchantment-library)
8. [Tomes](#8-tomes)
9. [Anvil Modifications](#9-anvil-modifications)
10. [Custom Enchantments](#10-custom-enchantments)
11. [Enchantment Configuration & Level Scaling](#11-enchantment-configuration--level-scaling)
12. [Particles & Ambience](#12-particles--ambience)
13. [Advancements](#13-advancements)
14. [API Surface](#14-api-surface)
15. [Network Packets](#15-network-packets)
16. [Data-Driven Design](#16-data-driven-design)
17. [Third-Party Integrations](#17-third-party-integrations)
18. [CurseForge Mod Page Notes](#18-curseforge-mod-page-notes)
19. [File Cross-Reference Index](#19-file-cross-reference-index)

---

## 1. Enchanting Table Overhaul

Apothic Enchanting completely replaces vanilla enchanting mechanics. The vanilla enchanting table block is preserved, but the menu, screen, stat calculations, and enchantment selection are all overridden.

### Core Changes
- Max enchanting level raised from 30 to **100**
- Three new stats (Eterna, Quanta, Arcana) replace the simple "bookshelf power" model
- Enchantment selection uses a custom algorithm with arcana-weighted rarity tiers
- Power range is 1-200 (after quanta factor applied to level)
- Lapis cost structure preserved (1/2/3 lapis for slots 0/1/2)
- XP level cost scales with the new stat system
- Custom block entity replaces vanilla enchanting table logic
- Infusion recipes can be performed at the enchanting table (replacing normal enchanting for specific items)

### Slot Level Calculation
The three enchanting slots (0, 1, 2) derive their level cost from Eterna:

| Slot | Formula |
|------|---------|
| Slot 2 (highest) | `round(eterna)` (i.e. eterna directly) |
| Slot 1 (middle) | `round(level * random(0.6, 0.8))` |
| Slot 0 (lowest) | `round(level * random(0.2, 0.4))` |

> **Cross-check:** `table/ApothEnchantmentHelper.java:55-61`

---

## 2. The Three Primary Stats

### 2.1 Eterna (Enchanting Power)

- **Color:** Green (`0x3DB53D`)
- **Range:** 0-100 (clamped)
- **Purpose:** Determines the maximum enchantment level. `level = round(eterna)`
- **Vanilla equivalent:** Bookshelf count (vanilla max = 30 from 15 bookshelves)
- **Max Eterna mechanic:** Each shelf block has a `maxEterna` cap. Blocks are sorted by their max cap (lowest first), and each group can only contribute up to its cap. This creates a step-ladder system where you need higher-tier shelves to push past the cap of lower-tier ones.

**Step-ladder example:** 15 vanilla bookshelves = 30 Eterna (capped at their maxEterna of 30). Adding a 16th bookshelf does nothing. But adding a Hellshelf (maxEterna=45) would push Eterna from 30 to 33, because the Hellshelf contributes up to its higher cap.

> **Cross-check:** `table/EnchantmentTableStats.java:179-192` (Builder.build())

### 2.2 Quanta (Volatility)

- **Color:** Red (`0xFC5454`)
- **Range:** 0-100 (clamped)
- **Default:** 15 (hardcoded in Builder constructor)
- **Purpose:** Controls randomness/variance of the actual enchanting power
- **Formula:**
  - **Unstable (default):** `power = level * (1 + quanta * gaussian(-1..1) / 100)` where gaussian is `clamp(nextGaussian()/3, -1, 1)`
  - **Stable:** `power = level * (1 + quanta * uniform(0..1) / 100)` (no negative rolls)
- **Effective power range:** 1-200 (clamped)
- **Practical effect:** High quanta means enchanting results swing wildly. At 100 quanta, unstable tables can produce power from near-zero to double the level. Stability eliminates the downside.

> **Cross-check:** `table/ApothEnchantmentHelper.java:167-177` (getQuantaFactor)

### 2.3 Arcana (Mystical Quality)

- **Color:** Purple (`0xA800A8`)
- **Range:** 0-100 (clamped)
- **Default:** `itemEnchantability / 2` (from item being enchanted)
- **Purpose:** Two effects:
  1. **Rarity weight adjustment** - Shifts the probability distribution toward rarer enchantments
  2. **Guaranteed extra enchantments** - At 33+ arcana: 2 guaranteed enchantments; at 66+: 3 guaranteed

#### Arcana Tiers and Rarity Weights

| Tier | Threshold | Common | Uncommon | Rare | Very Rare |
|------|-----------|--------|----------|------|-----------|
| EMPTY | 0 | 10 | 5 | 2 | 1 |
| LITTLE | 10 | 8 | 5 | 3 | 1 |
| FEW | 20 | 7 | 5 | 4 | 2 |
| SOME | 30 | 5 | 5 | 4 | 2 |
| LESS | 40 | 5 | 5 | 4 | 3 |
| MEDIUM | 50 | 5 | 5 | 5 | 5 |
| MORE | 60 | 3 | 4 | 5 | 5 |
| VALUE | 70 | 2 | 4 | 5 | 5 |
| EXTRA | 80 | 2 | 4 | 5 | 7 |
| ALMOST | 90 | 1 | 3 | 5 | 8 |
| MAX | 99 | 1 | 2 | 5 | 10 |

The `adjustWeight()` method maps vanilla enchantment weights to a `LegacyRarity` (Common=10, Uncommon=5, Rare=2, VeryRare=1) and then looks up the tier's weight for that rarity.

> **Cross-check:** `table/Arcana.java:1-51`, `table/LegacyRarity.java`

### 2.4 Secondary Stats

| Stat | Default | Purpose |
|------|---------|---------|
| **Clues** | 1 | Number of enchantment previews visible in the GUI (additive from shelves) |
| **Blacklist** | empty | Set of enchantments excluded from rolling |
| **Treasure** | false | If true, treasure enchantments (Mending, Frost Walker, etc.) can appear |
| **Stable** | false | If true, quanta only produces positive factors (no downside) |

> **Cross-check:** `table/EnchantmentTableStats.java:31`

---

## 3. Enchanting Table GUI

### Layout

The GUI uses a custom texture at `textures/gui/enchanting_table.png` (176x197 pixels, taller than vanilla's 166px).

**Elements:**
- **Top section:** Title, enchanting book animation (same as vanilla)
- **Item slot:** Top-left, same position as vanilla
- **Lapis slot:** Below item slot, same as vanilla
- **Three enchantment slots:** Right side, 108x19px each, stacked vertically at y=14, 33, 52
- **Stat bars:** Below the enchantment slots at y=75, 85, 95
  - Eterna bar: green, labeled "Eterna"
  - Quanta bar: red, labeled "Quanta"
  - Arcana bar: purple, labeled "Arcana"
  - Each bar is 110px wide at 100% (scales linearly with stat value / 100)
- **Info button:** Small tab at top-right (145, -15) that opens `EnchantingInfoScreen` showing all available enchantments

### Tooltips (on hover)

**Enchantment slot hover (left panel):**
- Shows enchantment clues (names of enchantments that will appear)
- If all clues are known: "These are all the enchantments on this item" header
- If partial: "Enchanting Clues" header with known enchantments listed
- Shows lapis cost, level cost, and "limited enchantability" warnings

**Enchantment slot hover (right panel, `drawOnLeft`):**
- "Enchanting at Level X" header
- XP cost in both points and levels
- Power range: "[minPow] to [maxPow]" (affected by quanta and stability)
- Item enchantability value
- Number of clues visible

**Eterna bar hover:**
- "Eterna: Enchanting Power" description
- "Eterna determines the level of enchantments you will receive"
- Current value: "X.XX / 100"

**Quanta bar hover:**
- "Quanta: Enchanting Volatility" description
- "Quanta controls the randomness of the actual enchanting power"
- Stability status shown
- Current value display
- Side panel: "Quanta Buff" showing growth percentage

**Arcana bar hover:**
- "Arcana: Mystical Quality" description
- "Arcana increases enchantment rarity and quantity"
- Shows item enchantability bonus contribution
- Total arcana breakdown (shelf arcana + item bonus)
- Side panel: "Arcana Bonus" showing weight changes and minimum guaranteed enchantments
- Weight table: Common/Uncommon/Rare/Very Rare with current tier values

### Additional GUI textures
- `textures/gui/enchanting_info.png` - Full enchantment info screen
- `textures/gui/enchanting_jei.png` - JEI integration display
- `textures/gui/library.png` - Library block GUI
- `textures/gui/book/` - Sub-textures for the info screen (power slider, enchantments window, slot selector, weights display, question mark icon)

> **Cross-check:** `table/ApothEnchantmentScreen.java`, `table/EnchantingInfoScreen.java`

---

## 4. Bookshelf / Shelf System

All stat blocks provide their values through two mechanisms:
1. **JSON data files** in `data/apothic_enchanting/enchanting_stats/` (data-driven)
2. **Java interface** `EnchantmentStatBlock` (code-driven, for special behaviors)

The JSON system is checked first; if a block has a JSON entry, those values are used. The `EnchantmentStatBlock` interface provides defaults and special behaviors (blacklisting, treasure, stability, particles).

### Stat Gathering Process

1. Iterate all `EnchantingTableBlock.BOOKSHELF_OFFSETS` positions (vanilla bookshelf offset list)
2. For each offset, check if the block between the table and the shelf is a `ENCHANTMENT_POWER_TRANSMITTER` (air or equivalent)
3. For each valid position, call `gatherStats()` which reads eterna, maxEterna, quanta, arcana, clues from the registry, plus blacklist/treasure/stability from the block interface
4. Build final stats using the step-ladder algorithm for eterna

### Complete Shelf Stats Table (from JSON data)

| Block | maxEterna | Eterna | Quanta | Arcana | Clues | Notes |
|-------|-----------|--------|--------|--------|-------|-------|
| **Vanilla Bookshelf** | 30 | 2 | 0 | 0 | 0 | Via `getEnchantPowerBonus()` |
| Stoneshelf | 0 | -3 | 0 | -7.5 | 0 | Negative stats, for precise tuning |
| Beeshelf | 0 | -30 | 100 | 0 | 0 | Huge quanta, negative eterna |
| Melonshelf | 0 | -2 | -10 | 0 | 0 | Negative quanta/eterna, for reducing |
| Hellshelf | 45 | 3 | 3 | 0 | 0 | Early-game fire-themed |
| Infused Hellshelf | 60 | 5 | 5 | 0 | 0 | Upgraded via infusion |
| Glowing Hellshelf | 60 | 5 | 5 | 3.33 | 0 | Hellshelf + arcana |
| Blazing Hellshelf | 65 | 10 | 10 | 0 | -1 | High stats but removes a clue |
| Seashelf | 45 | 3 | 0 | 3 | 0 | Early-game ocean-themed |
| Infused Seashelf | 60 | 5 | 0 | 5 | 0 | Upgraded via infusion |
| Crystal Seashelf | 60 | 5 | 3 | 5 | 0 | Balanced variant |
| Heart Seashelf | 60 | 15 | 0 | 20 | 0 | High eterna + arcana |
| Dormant Deepshelf | 30 | 2 | 0 | 0 | 0 | Pre-infusion deepshelf |
| Deepshelf | 70 | 5 | 5 | 5 | 0 | Activated via infusion |
| Echoing Deepshelf | 75 | 5 | 0 | 15 | 0 | Arcana-focused |
| Soul-Touched Deepshelf | 75 | 5 | 15 | 0 | 0 | Quanta-focused |
| Echoing Sculkshelf | 80 | 10 | 10 | 15 | 1 | High-tier + clue |
| Soul-Touched Sculkshelf | 80 | 10 | 15 | 10 | 1 | High-tier + clue |
| Endshelf | 90 | 5 | 5 | 5 | 0 | End-themed |
| Pearl Endshelf | 90 | 10 | 7.5 | 7.5 | 0 | Balanced end variant |
| Draconic Endshelf | 100 | 20 | 0 | 0 | 0 | Only way to reach 100 Eterna |
| Sightshelf | - | 0 | 0 | 0 | 1 | Clue only |
| Sightshelf T2 | - | 0 | 0 | 0 | 2 | Double clue |
| Amethyst Cluster | 40 | 1 | -1 | 0 | 0 | Minor contributor |
| Basic Skulls (tag) | 0 | 0 | 5 | 0 | 0 | Zombie, Piglin, Creeper heads |
| Wither Skeleton Skull | 0 | 0 | 10 | 0 | 0 | Double skull quanta |

> **Cross-check:** `data/apothic_enchanting/enchanting_stats/*.json` (25 files)

### Shelf Progression Tiers

1. **Starter** (maxEterna 0-30): Vanilla Bookshelf, Stoneshelf, Beeshelf, Melonshelf, Dormant Deepshelf
2. **Early** (maxEterna 45): Hellshelf, Seashelf
3. **Mid** (maxEterna 60-65): Infused Hellshelf, Infused Seashelf, Crystal Seashelf, Heart Seashelf, Glowing Hellshelf, Blazing Hellshelf
4. **Late** (maxEterna 70-80): Deepshelf, Echoing/Soul-Touched Deepshelf, Echoing/Soul-Touched Sculkshelf
5. **End** (maxEterna 90): Endshelf, Pearl Endshelf
6. **Max** (maxEterna 100): Draconic Endshelf

---

## 5. Special Shelves

### 5.1 Filtering Shelf

- **Block:** Extends `ChiseledBookShelfBlock` (has 6 internal book slots)
- **Mechanic:** Place enchanted books with exactly 1 enchantment each. Each book's enchantment is added to the table's blacklist, preventing it from appearing in enchanting rolls.
- **Stats:** Eterna +0.5 per book (max +3), Arcana +1 per book (max +6)
- **Use case:** Remove unwanted enchantments from the pool to increase odds of getting desired ones

> **Cross-check:** `objects/FilteringShelfBlock.java`

### 5.2 Treasure Shelf

- **Mechanic:** Sets `treasure=true` on the enchanting table stats
- **Effect:** Allows treasure-only enchantments (Mending, Frost Walker, Soul Speed, Swift Sneak, etc.) to appear in the enchanting table
- **Normally:** These enchantments only appear in loot/trading/fishing

> **Cross-check:** `objects/TreasureShelfBlock.java`

### 5.3 Geode Shelf

- **Rarity:** Uncommon item
- **Mechanic:** Implements `EnchantmentStatBlock` with stability provision (`providesStability()` returns true)
- **Effect:** Prevents negative quanta rolls (stable enchanting)

> **Cross-check:** `objects/GeodeShelfBlock.java`

### 5.4 Sculk Shelves (Echoing, Soul-Touched)

- **Special behavior:** Randomly plays sculk sounds at configurable intervals (`sculkShelfNoiseChance` config, default 1-in-200 per client tick)
- **Block property:** Has `randomTicks` enabled

> **Cross-check:** `objects/TypedShelfBlock.java` (SculkShelfBlock inner class)

---

## 6. Infusion Recipe System

Infusion is a secondary crafting system that uses the enchanting table. Instead of enchanting an item, specific items can be "infused" (transformed) when the table's stats meet certain requirements.

### Recipe Format

```json
{
  "type": "apothic_enchanting:infusion",
  "input": { "item": "..." },
  "requirements": { "eterna": 45, "quanta": 15, "arcana": 10 },
  "max_requirements": { "eterna": -1, "quanta": -1, "arcana": -1 },
  "result": { "id": "...", "count": 1 }
}
```

- `requirements`: Minimum Eterna/Quanta/Arcana needed
- `max_requirements`: Maximum stats allowed (-1 = no max). Forces precision in stat tuning.
- Recipe matching sorts by eterna requirement descending and picks the first match

### Recipe Variants

- **`apothic_enchanting:infusion`** - Standard infusion, output is a new item
- **`apothic_enchanting:keep_nbt_infusion`** - Preserves NBT/components from input (used for Library -> Ender Library upgrade, keeps stored enchantments)

### Complete Infusion Recipe List

| Input | Output | Eterna | Quanta | Arcana | Max Q | Max A | Max E |
|-------|--------|--------|--------|--------|-------|-------|-------|
| Seashelf | Infused Seashelf | 45 | 15 | 10 | - | - | - |
| Hellshelf | Infused Hellshelf | 45 | 30 | 0 | - | - | - |
| Dormant Deepshelf | Deepshelf | 60 | 40 | 40 | - | - | - |
| Scrap Tome | Improved Scrap Tome x4 | 45 | 25 | 35 | 50 | - | - |
| Improved Scrap Tome | Extraction Tome x4 | 60 | 25 | 45 | 75 | - | - |
| Dragon Breath | Infused Breath x3 | 80 | 15 | 60 | 30 | - | - |
| Amethyst Block | Budding Amethyst | 60 | 30 | 50 | 50 | - | - |
| Carrot | Golden Carrot | 20 | 10 | 0 | 30 | - | 20 |
| Honey Bottle | XP Bottle x1 | 20 | 25 | 25 | - | - | - |
| Honey Bottle | XP Bottle x8 | 60 | 25 | 25 | - | - | - |
| Honey Bottle | XP Bottle x32 | 100 | 25 | 25 | - | - | - |
| Echo Shard | Echo Shard x4 | 70 | 50 | 50 | - | - | - |
| Inert Trident | Trident | 40 | 20 | 35 | 50 | - | - |
| Library | Ender Library (keeps data) | 100 | 45 | 100 | 50 | 100 | 100 |
| Flimsy Ender Lead | Ender Lead | 45 | 25 | 40 | - | - | - |
| Ender Lead (w/ Witch) | Occult Ender Lead | 75 | 85 | 60 | - | 85 | - |
| Music Disc (any) | Disc: Eterna | 40 | 0 | 0 | - | - | - |
| Music Disc (any) | Disc: Quanta | 10 | 40 | 0 | - | - | - |
| Music Disc (any) | Disc: Arcana | 10 | 0 | 40 | - | - | - |

> **Cross-check:** `data/apothic_enchanting/recipe/infusion/*.json` (20 files), `table/infusion/InfusionRecipe.java`

### Infusion in the GUI

When an infusable item is placed in the table and stats match, slot 2 (highest) shows a special "Infusion" enchantment display. If stats don't match, it shows "Infusion Failed" in red with italic text.

---

## 7. Enchantment Library

A storage system for enchantments. Deposit enchanted books to store enchantment "points"; extract enchantments onto items at will.

### Two Tiers

| Block | Max Enchantment Level | Max Points | Created By |
|-------|----------------------|------------|------------|
| Library | 16 | 32,768 | Crafting recipe |
| Ender Library (Library of Alexandria) | 31 | 1,073,741,824 | Infusion from Library (100E/45-50Q/100A) |

### Point System

Enchantment levels are converted to points using exponential scaling:

| Level | Points |
|-------|--------|
| 1 | 1 |
| 2 | 2 |
| 3 | 4 |
| 4 | 8 |
| 5 | 16 |
| 10 | 512 |
| 16 | 32,768 |
| 31 | 1,073,741,824 |

Formula: `points = 2^(level - 1)`

### Operations

**Deposit:**
- Insert an enchanted book into the top slot (only enchanted books accepted)
- All enchantments on the book are absorbed
- Points added per enchantment: `2^(level - 1)`
- Max level ever deposited is tracked per enchantment (you can only extract up to the highest level you've ever deposited)
- Overflow points are capped at the library's max

**Extract:**
- Place any item in the target slot
- Select an enchantment and level from the GUI
- Cost: `pointsForTargetLevel - pointsForCurrentLevel`
- Enchantment is applied directly to the item (not just books)
- Can only extract up to the max level previously deposited

### Automation

The library exposes an `IItemHandler` for hopper/pipe integration:
- 1 slot, accepts enchanted books only (count = 1)
- Insert triggers `depositBook()`
- Extract always returns empty (output is through the GUI only)

### Data Storage

Stored in block entity NBT as two `CompoundTag` maps:
- `points`: `{ "minecraft:sharpness": 16, "minecraft:protection": 8, ... }`
- `levels`: `{ "minecraft:sharpness": 5, "minecraft:protection": 4, ... }`

> **Cross-check:** `library/EnchLibraryTile.java`, `library/EnchLibraryBlock.java`, `library/EnchLibraryContainer.java`, `library/EnchLibraryScreen.java`

---

## 8. Tomes

Tomes are special book-like items that filter enchantments by equipment slot. When enchanted on the table, they only receive enchantments valid for their target slot. Once enchanted, right-clicking converts them into enchanted books.

### Tome List

| Tome | Target Item | Receives enchantments for |
|------|-------------|---------------------------|
| Helmet Tome | Diamond Helmet | Head slot enchantments |
| Chestplate Tome | Diamond Chestplate | Chest slot enchantments |
| Leggings Tome | Diamond Leggings | Legs slot enchantments |
| Boots Tome | Diamond Boots | Feet slot enchantments |
| Weapon Tome | Diamond Sword | Mainhand weapon enchantments |
| Pickaxe Tome | Diamond Pickaxe | Mainhand tool enchantments |
| Bow Tome | Bow | Bow enchantments |
| Fishing Tome | Fishing Rod | Fishing enchantments |
| Other Tome | (none) | Enchantments not matching any of the above |

### Implementation

Tomes implement `EnchantableItem` interface. The `isPrimaryItemFor()` method delegates to the representative item (e.g., Diamond Helmet for Helmet Tome). The "Other Tome" accepts anything that none of the specialized tomes accept.

When enchanted, the `applyEnchantments()` method transmutes the tome into an `ENCHANTED_BOOK` and applies all enchantments to it.

### Scrapping Tomes

| Tome | Rarity | Behavior | Anvil Cost |
|------|--------|----------|------------|
| Scrap Tome | Uncommon | Removes ~half the enchantments from an item, outputs enchanted book with removed ones | 6 levels per remaining enchantment |
| Improved Scrap Tome | Rare | Better version of scrapping (same mechanic, likely better odds) | Similar |
| Extraction Tome | Epic | Extracts all enchantments cleanly | Higher cost |

> **Cross-check:** `objects/TomeItem.java`, `objects/ScrappingTomeItem.java`, `objects/ImprovedScrappingTomeItem.java`, `objects/ExtractionTomeItem.java`

---

## 9. Anvil Modifications

### Curse Removal with Prismatic Web

- **Input:** Enchanted item + Prismatic Web
- **Output:** Same item with all curses removed
- **Cost:** 30 XP levels, consumes 1 Prismatic Web

### Anvil Self-Repair

- Chipped Anvil + 5 levels = Damaged Anvil -> Anvil progression
- Allows repair of anvils without crafting new ones

### Tome Integration

Scrapping/Extraction tomes are combined with enchanted items on the anvil:
- Scrap Tome: Randomly removes ~half the enchantments, outputs book with removed enchantments
- Extraction Tome: Cleanly separates enchantments from item

> **Cross-check:** `ApothEnchEvents.java` (anvilEvent method)

---

## 10. Custom Enchantments

Apothic Enchanting adds **20 new enchantments**, all defined via data-driven enchantment JSON files and registered in `ApothEnchantmentProvider.java`.

### Complete Enchantment List

| ID | Name | Max Lvl | Slot | Equipment | Description |
|----|------|---------|------|-----------|-------------|
| `berserkers_fury` | Berserker's Fury | 3 | Chest | Chestplate | **Curse.** When hit, grants Damage Resistance, Damage Boost, and Speed buffs but costs health per tick. Configurable cooldown. |
| `boon_of_the_earth` | Boon of the Earth | 3 | Mainhand | Pickaxe | Chance to drop bonus items from a tag when mining blocks from a target tag (stone, deepslate, netherrack). |
| `chainsaw` | Chainsaw | 1 | Mainhand | Axe | Breaking a log breaks the entire tree (connected logs). |
| `chromatic` | Chromatic | 1 | Mainhand | Shears | Randomizes wool color when shearing sheep. |
| `crescendo_of_bolts` | Crescendo of Bolts | 3 | Mainhand | Crossbow | Fires multiple shots per loaded ammunition without reloading between them. Tracked via data components. |
| `endless_quiver` | Endless Quiver | 1 | Mainhand | Bow/Crossbow | Arrows materialize from the quiver (infinite arrows). |
| `growth_serum` | Growth Serum | 3 | Mainhand | Shears | Sheep regrow wool immediately after shearing (chance per level). |
| `icy_thorns` | Icy Thorns | 3 | Various | Armor | Thorns-like effect that also applies freeze/slowness to attackers. |
| `knowledge_of_the_ages` | Knowledge of the Ages | 5 | Mainhand | Sword | Converts all mob item drops to XP orbs. Items in `cannot_be_converted_to_xp` tag are exempt. |
| `life_mending` | Life Mending | 3 | Various | Armor/Tools | Incoming healing is converted to durability repair. Higher levels = more durability per HP. Fractional HP can be consumed. |
| `miners_fervor` | Miner's Fervor | 5 | Mainhand | Pickaxe | Faster mining speed that scales with level (exponential scaling), but has a cap. Alternative to Efficiency. |
| `natures_blessing` | Nature's Blessing | 5 | Mainhand | Hoe | Right-clicking crops applies bone meal at the cost of durability. |
| `rebounding` | Rebounding | 3 | Various | Armor | Custom entity effect triggered when taking damage. Knockback/ricochet. |
| `reflective_defenses` | Reflective Defenses | 3 | Offhand | Shield | When blocking, chance to reflect a portion of blocked damage back to the attacker. |
| `scavenger` | Scavenger | 5 | Mainhand | Sword | Chance per level to roll an additional copy of a slain mob's loot table. |
| `shield_bash` | Shield Bash | 3 | Offhand | Shield | Knockback enemies when successfully blocking an attack. |
| `stable_footing` | Stable Footing | 1 | Feet | Boots | Negates the mining speed penalty for flying/not being on the ground. |
| `tempting` | Tempting | 1 | Mainhand | Various | Animals follow the player when holding this item (like holding wheat). |
| `worker_exploitation` | Worker Exploitation | 1 | Mainhand | Shears | Doubles wool drops from shearing but deals 2 damage to the sheep. |
| `infusion` | Infusion | - | - | - | Internal enchantment used for infusion recipe display in the GUI. Not a real enchantment. |

### Custom Enchantment Components

- **BerserkingComponent:** `enchantments/components/BerserkingComponent.java` - Configurable mob effects list, health cost, cooldown ticks
- **BoonComponent:** `enchantments/components/BoonComponent.java` - Block tag + drop tag + chance per level
- **ReflectiveComponent:** `enchantments/components/ReflectiveComponent.java` - Chance and damage value for reflection
- **ExponentialLevelBasedValue:** `enchantments/values/ExponentialLevelBasedValue.java` - `base * multiplier^(level-1)` formula for scaling
- **ReboundingEffect:** `enchantments/entity_effects/ReboundingEffect.java` - Custom entity effect type

> **Cross-check:** `Ench.java:329-355` (Enchantments class), `Ench.java:237-327` (EnchantEffects class), `data/ApothEnchantmentProvider.java`

---

## 11. Enchantment Configuration & Level Scaling

### Per-Enchantment Config

Every enchantment (vanilla and modded) gets an `EnchantmentInfo` record with configurable properties:

- **Max Level** - Can scale vanilla enchantments to higher levels (e.g., Sharpness 10)
- **Max Loot Level** - Different max for loot table generation vs. direct table enchanting
- **Forced Level Cap** - Hard cap on effective level regardless of NBT
- **Min Power Function** - Custom expression (EvalEx) for minimum enchanting power needed
- **Max Power Function** - Custom expression for maximum enchanting power (default: 200)

### Hard Caps via IMC

Other mods can set hard level caps via Inter-Mod Communication:
```java
ApothicEnchanting.ENCH_HARD_CAP_IMC = "set_ench_hard_cap"
// Payload: Pair<ResourceKey<Enchantment>, Integer>
```

### Client-Side Config

| Option | Default | Description |
|--------|---------|-------------|
| `showEnchantedBookMetadata` | true | Show treasure/tradeable status in book tooltips |
| `sculkShelfNoiseChance` | 200 | 1/n chance per tick for sculk shelf sound (0=disabled) |
| `enableInlineEnchDescs` | false | Show enchantment descriptions inline in item tooltips |

> **Cross-check:** `ApothEnchConfig.java`, `EnchantmentInfo.java`, `PowerFunction.java`

---

## 12. Particles & Ambience

### Custom Particle Types (4)

| ID | Name | Visual | Used By |
|----|------|--------|---------|
| `enchant_fire` | Fire Enchant | Nether/fire particle | Hellshelf variants |
| `enchant_water` | Water Enchant | Ocean/water particle | Seashelf variants |
| `enchant_sculk` | Sculk Enchant | Deep dark particle | Deepshelf, Sculkshelf variants |
| `enchant_end` | End Enchant | End realm particle | Endshelf, Draconic Endshelf |

All particles flow toward the enchanting table (same motion as vanilla enchant particles but with different textures).

### Music Discs (3)

| Disc | Sound Event | Created By |
|------|-------------|------------|
| Music Disc: Eterna | `music_disc.eterna` | Infusion (40E, 0Q, 0A) |
| Music Disc: Quanta | `music_disc.quanta` | Infusion (10E, 40Q, 0A) |
| Music Disc: Arcana | `music_disc.arcana` | Infusion (10E, 0Q, 40A) |

> **Cross-check:** `Ench.java:474-481` (Particles), `Ench.java:99-116` (Sounds, Songs)

---

## 13. Advancements

18 custom advancements organized as a progression tree:

- **Root:** Enchanter (enchanting table)
- **Bookshelf progression:** Place various shelf types
- **Stat milestones:**
  - Eterna: 60, 80, 100
  - Arcana: 50, 100
  - Quanta: 0 (all negative), 50, 100
- **Configuration milestones:** Stable setup, Rectified setup, max stats
- **Custom trigger:** `EnchantedTrigger` fires when an item is enchanted at the table

> **Cross-check:** `data/apothic_enchanting/advancement/` (18 files), `advancements/EnchantedTrigger.java`

---

## 14. API Surface

### EnchantmentStatBlock Interface

The primary API for addon mods to make blocks provide enchanting stats.

```java
public interface EnchantmentStatBlock extends IBlockExtension {
    float getMaxEnchantingPower(BlockState, LevelReader, BlockPos);  // default: 30
    float getQuantaBonus(BlockState, LevelReader, BlockPos);         // default: 0
    float getArcanaBonus(BlockState, LevelReader, BlockPos);         // default: 0
    int getBonusClues(BlockState, LevelReader, BlockPos);            // default: 0
    Set<Holder<Enchantment>> getBlacklistedEnchantments(...);        // default: empty
    boolean allowsTreasure(BlockState, LevelReader, BlockPos);       // default: false
    boolean providesStability(BlockState, LevelReader, BlockPos);    // default: false
    void spawnTableParticle(...);                                     // default: vanilla particle logic
    ParticleOptions getTableParticle(BlockState);                    // default: ENCHANT
}
```

> **Cross-check:** `api/EnchantmentStatBlock.java`

### EnchantableItem Interface

Implement on items to customize enchantment selection behavior.

```java
public interface EnchantableItem {
    List<EnchantmentInstance> selectEnchantments(List<EnchantmentInstance> chosen, RandomSource, ItemStack, int level, EnchantmentTableStats);
    default ItemStack applyEnchantments(ItemStack stack, List<EnchantmentInstance> enchantments);
    default boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment);
}
```

> **Cross-check:** `api/EnchantableItem.java`

### EnchantingStatRegistry

Static registry methods for querying block stats:
- `getEterna(BlockState, LevelReader, BlockPos)`
- `getMaxEterna(BlockState, LevelReader, BlockPos)`
- `getQuanta(BlockState, LevelReader, BlockPos)`
- `getArcana(BlockState, LevelReader, BlockPos)`
- `getBonusClues(BlockState, LevelReader, BlockPos)`

> **Cross-check:** `table/EnchantingStatRegistry.java`

---

## 15. Network Packets

### Server -> Client Payloads

| Payload | ID | Purpose | Data |
|---------|----|---------|------|
| `StatsPayload` | `apothic_enchanting:stats` | Sync table stats to client for GUI rendering | Full `EnchantmentTableStats` record |
| `CluePayload` | `apothic_enchanting:clue` | Sync enchantment clue previews | Slot index, list of `EnchantmentInstance`, and "all clues" boolean |
| `EnchantmentInfoPayload` | `apothic_enchanting:enchantment_info` | Sync per-enchantment config to client | Map of `EnchantmentInfo` records |

Stats and clues are sent when a player opens the enchanting table or when table state changes. EnchantmentInfo is synced on server startup and datapack reload.

> **Cross-check:** `payloads/StatsPayload.java`, `payloads/CluePayload.java`, `payloads/EnchantmentInfoPayload.java`

---

## 16. Data-Driven Design

### What's Data-Driven (JSON)

| System | Location | Notes |
|--------|----------|-------|
| Shelf stats | `data/.../enchanting_stats/*.json` | 25 files, block-to-stat mappings |
| Infusion recipes | `data/.../recipe/infusion/*.json` | 20 recipes with stat requirements |
| Crafting recipes | `data/.../recipe/*.json` | Standard shaped/shapeless |
| Enchantments | `data/.../enchantment/*.json` | All 20 custom enchantments |
| Advancements | `data/.../advancement/*.json` | 18 progression advancements |
| Tags | `data/.../tags/` | Item, block, entity type tags |
| Loot tables | `data/.../loot_modifier/` | Warden drops, boon drops |
| Damage types | `data/.../damage_type/` | Corrupted damage |

### What's Code-Driven (Java)

| System | Reason |
|--------|--------|
| Enchantment selection algorithm | Complex stat interactions, quanta factor, arcana weighting |
| Library point system | Block entity logic, NBT serialization |
| Filtering shelf behavior | Dynamic blacklist from book contents |
| Treasure/stability flags | Boolean flags from block interface |
| Tome item behavior | Custom `EnchantableItem` implementations |
| Anvil modifications | Event-driven, complex item manipulation |
| ASM/Mixin hooks | Vanilla code modification |

---

## 17. Third-Party Integrations

| Mod | Integration | File |
|-----|-------------|------|
| JEI | Infusion recipe category display | `compat/EnchJEIPlugin.java`, `compat/InfusionRecipeCategory.java` |
| Jade/HWYLA | Shelf block info tooltips (stats) | `compat/EnchJadePlugin.java` |
| Curios | Enchanting curio-slot items | `compat/CuriosCompat.java` |

### Mixin Targets (18 mixins)

| Mixin | Target | Purpose |
|-------|--------|---------|
| `AnvilMenuMixin` | AnvilMenu | Custom anvil operations (tomes, curse removal) |
| `AnvilScreenMixin` | AnvilScreen | Render anvil UI changes |
| `BlockMixin` | Block | Make all blocks implement `EnchantmentStatBlock` checks |
| `BlocksMixin` | Blocks | Register enchant particle spawning for stat blocks |
| `CandleBlockMixin` | CandleBlock | Candles as enchanting fuel |
| `CrossbowItemMixin` | CrossbowItem | Crescendo of Bolts multi-shot logic |
| `EnchantmentMixin` | Enchantment | Color text for above-max-level enchantments |
| `EnchantmentScreenMixin` | EnchantmentScreen | Replace vanilla screen with Apothic screen |
| `ItemMixin` | Item | Enchantment value overrides |
| `ItemStackMixin` | ItemStack | Custom enchanting behavior hooks |
| `ShearsItemMixin` | ShearsItem | Chromatic, Growth Serum, Worker Exploitation |
| `SheepMixin` | Sheep | Wool re-growth for Growth Serum |
| `TemptGoalMixin` | TemptGoal | Tempting enchantment mob follow |
| `ThrownTridentMixin` | ThrownTrident | Trident enchantment effects |
| `TridentItemMixin` | TridentItem | Trident enchanting support |

### ASM Coremods (JavaScript)

Located in `coremods/ench/`, these patch vanilla methods at bytecode level:
- `EnchHooks.getMaxLevel()` - Returns configured max instead of vanilla max
- `EnchHooks.getMaxLootLevel()` - Returns configured loot max
- `EnchHooks.getTicksCaughtDelay()` - Modified fishing rod delay for Lure

> **Cross-check:** `asm/EnchHooks.java`, `mixin/` (18 files), `coremods/ench/`

---

## 18. CurseForge Mod Page Notes

> **Note:** CurseForge returns 403 for direct page fetches. The following is gathered from web searches, guide sites, and the codebase.

### Description Summary

"Apothic Enchanting is the Enchanting Module of Apotheosis. It provides a full overhaul to Minecraft's enchanting system, as well as new enchantments and utilities to further improve the experience."

Key selling points emphasized:
- Three-stat system (Eterna/Quanta/Arcana) instead of simple bookshelf power
- Max enchanting level raised to 100
- Many custom bookshelf blocks with unique stat contributions
- Enchantment Library for storage
- 9 Tomes for targeted enchanting
- New enchantments
- Infusion crafting at the enchanting table

### Screenshot / Visual Design Notes

The CurseForge page and community guides describe these visual elements:

1. **Enchanting Table GUI** - Extended vanilla layout with three colored stat bars below the enchantment slots. Green (Eterna), Red (Quanta), Purple (Arcana). The bars animate smoothly when stats change. A small "info" tab in the upper-right opens a detailed enchantment browser.

2. **Stat tooltips** - Rich hover information showing exact values, power ranges, weight tables, and minimum enchantment guarantees. Information is displayed both in the main tooltip and in a "draw on left" side panel.

3. **Shelf blocks** - Visually distinct blocks themed to their biome/dimension:
   - Hellshelves: dark/nether-themed with fire particles
   - Seashelves: cyan/ocean-themed with water particles
   - Deepshelves: black/sculk-themed with sculk particles
   - Endshelves: sand/end-themed with end particles
   - Each has unique textures in `textures/blocks/`

4. **Library GUI** - A dedicated screen for the enchantment library showing stored enchantments, point values, max levels, and extraction controls.

5. **Infusion display** - When an infusable item is placed in the enchanting table and stats match, the enchantment slot shows "Infusion" with a special display level.

6. **Enchantment color coding** (from community guides):
   - Gray: Common enchantments
   - Light Blue: Common at higher levels
   - Light Red: Curses
   - Dark Red: Dual-effect (Life Mending, Berserker's Fury)
   - Green: Special (Crescendo, Knowledge of Ages, Boon)
   - Purple-Blue: Apothic Enchanting special
   - Magenta: Miner's Fervor, Worker Exploitation

### Dependencies Listed

- **Required:** Placebo (library mod by same author)
- **Required:** Apothic Attributes (attribute system)
- **Optional:** JEI (recipe viewing)
- **Optional:** Jade/WTHIT (block info)
- **Optional:** Curios (trinket slots)

---

## 19. File Cross-Reference Index

### Core Entry Points
- `ApothicEnchanting.java` - Main mod class, registration, IMC
- `Ench.java` - All registered objects (blocks, items, menus, particles, enchantments, etc.)
- `ApothEnchConfig.java` - Configuration options
- `ApothEnchClient.java` - Client-side setup and payload handlers
- `ApothEnchEvents.java` - All event handlers (anvil, drops, healing, block breaking, etc.)

### Enchanting Table System
- `table/EnchantmentTableStats.java` - Stats record, Builder, step-ladder eterna calculation
- `table/ApothEnchantmentHelper.java` - Enchantment selection algorithm, quanta factor, arcana weighting
- `table/ApothEnchantmentMenu.java` - Custom enchanting table container/menu
- `table/ApothEnchantmentScreen.java` - Custom enchanting table GUI rendering
- `table/ApothEnchantingTableBlock.java` - Custom enchanting table block
- `table/Arcana.java` - Arcana tier enum with rarity weight tables
- `table/LegacyRarity.java` - Weight-to-rarity mapping
- `table/EnchantingStatRegistry.java` - Static stat query methods, JSON data loading
- `table/EnchantmentTableItemHandler.java` - Fuel/lapis handling

### Infusion System
- `table/infusion/InfusionRecipe.java` - Recipe type, matching, serialization
- `table/infusion/KeepNBTInfusionRecipe.java` - NBT-preserving variant
- `data/apothic_enchanting/recipe/infusion/*.json` - All infusion recipe definitions

### Library System
- `library/EnchLibraryBlock.java` - Block definition (both tiers)
- `library/EnchLibraryTile.java` - Block entity: point storage, deposit/extract logic, NBT, automation
- `library/EnchLibraryContainer.java` - Menu/container for GUI interaction
- `library/EnchLibraryScreen.java` - Client-side library GUI

### Shelf Blocks
- `objects/TypedShelfBlock.java` - Base shelf block with particle customization
- `objects/TypedShelfBlock.SculkShelfBlock` - Sculk variant with sound effects
- `objects/FilteringShelfBlock.java` - Blacklist shelf (reads enchanted books)
- `objects/TreasureShelfBlock.java` - Enables treasure enchantments
- `objects/GeodeShelfBlock.java` - Provides stability
- `data/apothic_enchanting/enchanting_stats/*.json` - All shelf stat definitions (25 files)

### Tomes & Special Items
- `objects/TomeItem.java` - Base tome (9 variants)
- `objects/ScrappingTomeItem.java` - Basic scrapping
- `objects/ImprovedScrappingTomeItem.java` - Improved scrapping
- `objects/ExtractionTomeItem.java` - Clean extraction
- `objects/EnderLeadItem.java` - Entity capture lead (3 tiers)
- `objects/GlowyBlockItem.java` - Foil-effect block items
- `objects/WardenLootModifier.java` - Warden loot table modification

### Enchantments
- `Ench.java:329-355` - All 20 enchantment ResourceKeys
- `Ench.java:237-327` - All enchantment effect data component types
- `enchantments/components/BerserkingComponent.java` - Berserker's Fury data
- `enchantments/components/BoonComponent.java` - Boon of the Earth data
- `enchantments/components/ReflectiveComponent.java` - Reflective Defenses data
- `enchantments/entity_effects/ReboundingEffect.java` - Rebounding entity effect
- `enchantments/values/ExponentialLevelBasedValue.java` - Custom scaling formula
- `enchantments/ChainsawTask.java` - Tree-breaking logic
- `enchantments/ShearsEnchantments.java` - Shear enchantment effects
- `enchantments/CrescendoHooks.java` - Multi-shot crossbow logic

### API
- `api/EnchantmentStatBlock.java` - Block stat provider interface
- `api/EnchantableItem.java` - Item enchantment selection interface

### Data Generation
- `data/ApothEnchantmentProvider.java` - Enchantment definitions
- `data/EnchRecipeProvider.java` - Crafting recipes
- `data/EnchTagsProvider.java` - Tags
- `data/LootProvider.java` - Loot tables
- `data/SongProvider.java` - Jukebox songs

### Network
- `payloads/StatsPayload.java` - Table stats sync
- `payloads/CluePayload.java` - Enchantment clue sync
- `payloads/EnchantmentInfoPayload.java` - Per-enchantment config sync

### Vanilla Modification
- `asm/EnchHooks.java` - Injected static methods
- `mixin/*.java` - 18 mixin classes
- `coremods/ench/*.js` - JavaScript bytecode patches

### Resources
- `assets/apothic_enchanting/textures/gui/` - GUI textures (enchanting table, library, info screen, JEI)
- `assets/apothic_enchanting/textures/blocks/` - Block textures
- `assets/apothic_enchanting/textures/items/` - Item textures
- `assets/apothic_enchanting/blockstates/` - Block state definitions
- `assets/apothic_enchanting/models/` - Block and item models
- `assets/apothic_enchanting/lang/en_us.json` - English translations
- `assets/apothic_enchanting/particles/` - Particle definitions
- `assets/apothic_enchanting/sounds.json` - Sound registry
- `data/apothic_enchanting/enchanting_stats/` - Block stat JSONs (25 files)
- `data/apothic_enchanting/recipe/` - All recipes
- `data/apothic_enchanting/advancement/` - Advancement tree (18 files)
- `data/apothic_enchanting/tags/` - Custom tags
- `data/apothic_enchanting/damage_type/` - Corrupted damage type

---

## Sources

- [Apothic Enchanting - CurseForge](https://www.curseforge.com/minecraft/mc-mods/apothic-enchanting)
- [Apothic Enchanting - All The Guides (ATM10)](https://allthemods.github.io/alltheguides/atm10/apothicenchanting/)
- [Apotheosis - All The Guides (ATM9)](https://allthemods.github.io/alltheguides/atm9/apotheosis/)
- [Apotheosis Enchanting Guide - Sixthfore](http://sixthfore.blogspot.com/2024/12/minecraft-apotheosis-enchanting-guide.html)
- [Source code: ~/Projects/Apothic-Enchanting](~/Projects/Apothic-Enchanting)
