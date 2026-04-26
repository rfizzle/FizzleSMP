# Apothic Enchanting Parity — GameTest Plan

> **Purpose:** Exhaustive list of GameTests to validate that Fizzle Enchanting has feature parity with Apothic Enchanting. Each test is tagged with tier, priority, and the Apothic design section it validates.
>
> **Test tiers (matching existing Fizzle conventions):**
> - **T1** — Pure JUnit (`src/test`). No registry, no world. Math, codecs, data structures.
> - **T2** — fabric-loader-junit (`src/test`). Registry access, no world. Datagen validation, tag checks.
> - **T3** — Fabric GameTest (`src/gametest`). Full server world. Block placement, menus, entity interaction.
>
> **Priority:**
> - **P0** — Core system, blocks other tests. Implement first.
> - **P1** — Primary feature. Validates a key Apothic mechanic.
> - **P2** — Secondary feature or edge case.
> - **P3** — Polish, integration, or rare scenario.
>
> **Naming convention:** `<SystemArea>_<what>` (matches existing: `ShelfScanGameTest`, `FilteringTreasureGameTest`, etc.)
>
> **Structure templates used:**
> - `fizzle_enchanting:empty_3x3` — empty 3x3x3 box
> - `fizzle_enchanting:shelf_scan_9x4x9` — 9x4x9 room for enchanting table + shelves
> - New templates noted where needed

---

## S-1: Stat System Core (T1)

> **Apothic ref:** Design §2 (three stats), §2.1 (eterna step-ladder), §2.2 (quanta factor), §2.3 (arcana tiers)

### S-1.1 Stat Clamping

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-1.1a | Eterna clamps to [0, 100] when constructed with out-of-range values | T1 | P0 |
| S-1.1b | Quanta clamps to [0, 100] | T1 | P0 |
| S-1.1c | Arcana clamps to [0, 100] | T1 | P0 |
| S-1.1d | Clues clamps to [0, ∞) (negative input -> 0) | T1 | P0 |
| S-1.1e | Stats record with all-zero values is valid and equal to INVALID/default sentinel | T1 | P0 |

### S-1.2 Eterna Step-Ladder Accumulation

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-1.2a | Single shelf group: eterna = min(maxEterna, sum of eterna contributions) | T1 | P0 |
| S-1.2b | Two groups with different maxEterna: lower group fills first, higher group adds remainder up to its cap | T1 | P0 |
| S-1.2c | Three groups: lowest maxE=30 (sum=40), mid maxE=60 (sum=20), high maxE=100 (sum=15). Final eterna = min(100, 30+20+15) = 65 | T1 | P0 |
| S-1.2d | Adding shelves with maxEterna=0 contributes negative eterna unconditionally (no cap) | T1 | P1 |
| S-1.2e | Overflow: total contributions exceed 100, clamped to 100 | T1 | P1 |
| S-1.2f | All shelves have maxEterna=0: eterna remains 0 regardless of contribution sum | T1 | P1 |

### S-1.3 Quanta Factor

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-1.3a | Stable quanta factor is always in [1.0, 1.0 + quanta/100] (run 1000 iterations) | T1 | P0 |
| S-1.3b | Unstable quanta factor is in [1.0 - quanta/100, 1.0 + quanta/100] (run 1000 iterations) | T1 | P0 |
| S-1.3c | Quanta=0 always produces factor=1.0 (both stable and unstable) | T1 | P0 |
| S-1.3d | Quanta=100 unstable: factor range spans approximately [0.0, 2.0] over many runs | T1 | P1 |
| S-1.3e | Quanta=100 stable: factor range spans approximately [1.0, 2.0] over many runs | T1 | P1 |
| S-1.3f | Power = clamp(round(level * quantaFactor), 1, 200) never exceeds 200 or goes below 1 | T1 | P0 |

### S-1.4 Arcana Tiers

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-1.4a | Arcana 0 returns EMPTY tier: weights [10, 5, 2, 1] | T1 | P0 |
| S-1.4b | Arcana 50 returns MEDIUM tier: weights [5, 5, 5, 5] | T1 | P0 |
| S-1.4c | Arcana 99+ returns MAX tier: weights [1, 2, 5, 10] | T1 | P0 |
| S-1.4d | Each of 11 tier thresholds (0,10,20,30,40,50,60,70,80,90,99) maps to correct weights | T1 | P1 |
| S-1.4e | Arcana between thresholds rounds down to lower tier (e.g., 45 -> LESS at 40) | T1 | P1 |
| S-1.4f | adjustWeight() correctly maps vanilla weight 10 -> Common, 5 -> Uncommon, 2 -> Rare, 1 -> VeryRare | T1 | P1 |

### S-1.5 Builder Defaults

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-1.5a | Builder with itemEnch=0 produces quanta=15, arcana=0, clues=1 | T1 | P0 |
| S-1.5b | Builder with itemEnch=30 produces arcana=15 (itemEnch/2) | T1 | P0 |
| S-1.5c | Empty builder (no shelves) produces eterna=0 | T1 | P0 |

---

## S-2: Stat Gathering from World (T3)

> **Apothic ref:** Design §4 (stat gathering process, shelf positions, transmitter check)

### S-2.1 Vanilla Baseline

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-2.1a | Enchanting table with no shelves: eterna=0, quanta=15, arcana=itemEnch/2 | T3 | P0 |
| S-2.1b | 15 vanilla bookshelves around table: eterna=15 (maxE=15 for vanilla) | T3 | P0 |
| S-2.1c | 32 vanilla bookshelves (all offsets filled): eterna still capped at 15 | T3 | P0 |
| S-2.1d | Blocking midpoint with solid block reduces eterna by expected amount | T3 | P1 |

### S-2.2 Custom Shelf Stats

*Template: `shelf_scan_9x4x9`*

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-2.2a | Single hellshelf at valid offset: eterna=3, quanta=15+3=18, arcana=itemEnch/2 | T3 | P0 |
| S-2.2b | 15 hellshelves: eterna = min(45, 15*3) = 45 | T3 | P0 |
| S-2.2c | 15 vanilla bookshelves + 1 hellshelf: eterna = 15 + min(45-15, 3) = 18 (step-ladder) | T3 | P0 |
| S-2.2d | Draconic endshelf alone: eterna=20, maxEterna=100 | T3 | P1 |
| S-2.2e | Stoneshelf produces negative eterna and negative arcana | T3 | P1 |
| S-2.2f | Beeshelf produces quanta=100 and eterna=-30 | T3 | P1 |
| S-2.2g | Melonshelf produces negative quanta and negative eterna | T3 | P1 |
| S-2.2h | Sightshelf contributes only clues (no eterna/quanta/arcana) | T3 | P1 |
| S-2.2i | Sightshelf_t2 contributes 2 clues | T3 | P1 |

### S-2.3 Every Shelf's JSON Stats

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-2.3a | For each of the 25 shelf types: place one at valid offset, verify eterna/quanta/arcana/clues match JSON definition | T3 | P1 |
| S-2.3b | Amethyst cluster contributes eterna=1, quanta=-1, maxE=40 | T3 | P2 |
| S-2.3c | Wither skeleton skull contributes quanta=10 | T3 | P2 |
| S-2.3d | Basic skulls (zombie, piglin, creeper) contribute quanta=5 each | T3 | P2 |

### S-2.4 Mixed Shelf Configurations

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-2.4a | Max eterna config: combination that reaches exactly 100 | T3 | P1 |
| S-2.4b | Max quanta config: beeshelf-heavy setup reaches 100 quanta (clamped) | T3 | P2 |
| S-2.4c | Max arcana config: heart seashelf + echoing shelves reaches 100 arcana (clamped) | T3 | P2 |
| S-2.4d | All-negative shelf setup: eterna floors at 0 (not negative) | T3 | P1 |

---

## S-3: Special Shelf Behaviors (T3)

> **Apothic ref:** Design §5 (filtering, treasure, stability)

### S-3.1 Filtering Shelf

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-3.1a | Fresh filtering shelf has empty blacklist | T3 | P0 |
| S-3.1b | Insert enchanted book with Sharpness -> blacklist contains Sharpness | T3 | P0 |
| S-3.1c | Insert 6 different single-enchantment books -> blacklist has 6 entries | T3 | P1 |
| S-3.1d | Book with 2+ enchantments does NOT add to blacklist (only single-enchant books count) | T3 | P1 |
| S-3.1e | Removing book from slot removes its enchantment from blacklist | T3 | P1 |
| S-3.1f | Filtering shelf contributes eterna +0.5 per book and arcana +1 per book to table stats | T3 | P1 |
| S-3.1g | Blacklisted enchantments from filtering shelf propagate to gathered EnchantmentTableStats | T3 | P0 |
| S-3.1h | Filtering shelf accepts only enchanted books (rejects plain books, other items) | T3 | P2 |
| S-3.1i | Filtering shelf persists blacklist through save/load (NBT roundtrip) | T3 | P1 |

### S-3.2 Treasure Shelf

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-3.2a | Table with no treasure shelf: treasure=false in gathered stats | T3 | P0 |
| S-3.2b | Table with treasure shelf at valid offset: treasure=true | T3 | P0 |
| S-3.2c | Treasure shelf block entity creates correctly when placed | T3 | P1 |

### S-3.3 Stability (Rectifier / Geode Shelf)

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-3.3a | Table with no stability block: stable=false in gathered stats | T3 | P0 |
| S-3.3b | Table with rectifier/geode shelf at valid offset: stable=true | T3 | P0 |
| S-3.3c | Stability + quanta: quanta factor is always >= 1.0 (verified over many rolls) | T3 | P1 |

---

## S-4: Enchantment Selection (T1 + T3)

> **Apothic ref:** Design §1 (slot levels), §3 (selection algorithm), §2.3 (guaranteed enchants)

### S-4.1 Slot Level Calculation

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-4.1a | Slot 2 level = round(eterna) | T1 | P0 |
| S-4.1b | Slot 1 level is between 60%-80% of slot 2 (verify over 1000 samples) | T1 | P0 |
| S-4.1c | Slot 0 level is between 20%-40% of slot 2 (verify over 1000 samples) | T1 | P0 |
| S-4.1d | All slot levels are at least 1 when eterna > 0 | T1 | P0 |
| S-4.1e | Eterna=0 produces level=0 for all slots (item with 0 enchantability) | T1 | P1 |

### S-4.2 Enchantment Pool Filtering

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-4.2a | Only IN_ENCHANTING_TABLE enchantments appear when treasure=false | T3 | P0 |
| S-4.2b | TREASURE enchantments appear when treasure=true | T3 | P0 |
| S-4.2c | Blacklisted enchantments never appear in selection (run 100 rolls, verify absence) | T3 | P0 |
| S-4.2d | Enchantments already on item are excluded from pool | T3 | P1 |
| S-4.2e | Only enchantments valid for the item type appear (sword gets sword enchants, not bow) | T3 | P1 |

### S-4.3 Selection Guarantees

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-4.3a | At least 1 enchantment is always selected (when pool is non-empty) | T1 | P0 |
| S-4.3b | Arcana >= 33: at least 2 enchantments selected (when pool has 2+) | T1 | P1 |
| S-4.3c | Arcana >= 66: at least 3 enchantments selected (when pool has 3+) | T1 | P1 |
| S-4.3d | Incompatible enchantments are removed after each pick (Sharpness excludes Smite) | T1 | P0 |
| S-4.3e | Empty pool produces empty enchantment list (no crash) | T1 | P1 |

### S-4.4 Arcana Weight Influence

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-4.4a | At arcana=0 (EMPTY tier), common enchantments dominate (weight 10 vs 1 for very rare) | T1 | P1 |
| S-4.4b | At arcana=99 (MAX tier), very rare enchantments dominate (weight 10 vs 1 for common) | T1 | P1 |
| S-4.4c | Statistical test: over 10,000 rolls at arcana=99, very rare enchantments appear more often than common | T1 | P2 |

---

## S-5: Enchanting Table Menu (T3)

> **Apothic ref:** Design §1 (core changes), §3 (GUI), §6 (infusion in menu)

### S-5.1 Menu Basics

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-5.1a | Opening enchanting table creates custom menu type (not vanilla EnchantmentMenu) | T3 | P0 |
| S-5.1b | Menu has item slot (slot 0) and lapis slot (slot 1) | T3 | P0 |
| S-5.1c | Stats are computed and stored on menu when item is placed | T3 | P0 |
| S-5.1d | Costs array has 3 entries corresponding to 3 enchanting slots | T3 | P0 |

### S-5.2 Lapis & XP Requirements

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-5.2a | Slot 0 costs 1 lapis, slot 1 costs 2, slot 2 costs 3 | T3 | P1 |
| S-5.2b | Cannot enchant without sufficient lapis | T3 | P1 |
| S-5.2c | Cannot enchant without sufficient player XP levels | T3 | P1 |
| S-5.2d | Successful enchant consumes correct lapis and XP | T3 | P1 |

### S-5.3 Enchanting Produces Correct Results

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-5.3a | Diamond sword at eterna=30: receives at least 1 enchantment from sword pool | T3 | P0 |
| S-5.3b | Book at eterna=30: receives at least 1 enchantment | T3 | P0 |
| S-5.3c | Item with 0 enchantability: no enchantment options available | T3 | P1 |
| S-5.3d | After enchanting, item has enchantments and costs are reset | T3 | P1 |

---

## S-6: Infusion Recipes (T1 + T3)

> **Apothic ref:** Design §6 (infusion system, recipe format, recipe list)

### S-6.1 Recipe Matching Logic

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-6.1a | Recipe with min requirements only: matches when all stats >= min | T1 | P0 |
| S-6.1b | Recipe with min requirements only: fails when any stat < min | T1 | P0 |
| S-6.1c | Recipe with max requirements: fails when any stat > max (where max != -1) | T1 | P0 |
| S-6.1d | Recipe with max_requirements=-1: no upper bound enforced for that stat | T1 | P0 |
| S-6.1e | Multiple recipes for same input: highest eterna requirement matched first | T1 | P1 |
| S-6.1f | Tiered XP bottle recipe: honey_bottle at eterna=20 -> 1x, eterna=60 -> 8x, eterna=100 -> 32x | T1 | P1 |

### S-6.2 Recipe Serialization

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-6.2a | Infusion recipe JSON roundtrips through codec (encode -> decode = equal) | T1 | P0 |
| S-6.2b | Keep-NBT infusion recipe preserves components from input item | T1 | P1 |
| S-6.2c | All shipped infusion recipe JSONs parse without error | T2 | P0 |

### S-6.3 Infusion in World

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-6.3a | Place infusable item in table with matching stats: infusion result produced | T3 | P0 |
| S-6.3b | Place infusable item with stats below min: no infusion, normal enchant available | T3 | P1 |
| S-6.3c | Place infusable item with stats above max: no infusion match | T3 | P1 |
| S-6.3d | Shelf upgrade: hellshelf at eterna=45, quanta=30 -> infused hellshelf | T3 | P1 |
| S-6.3e | Keep-NBT: library with stored enchantments -> ender library retains enchantment data | T3 | P1 |
| S-6.3f | Infusion consumes exactly 1 input item and produces correct output count | T3 | P1 |

---

## S-7: Enchantment Library (T1 + T3)

> **Apothic ref:** Design §7 (library, point system, two tiers, automation)

### S-7.1 Point Formula

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-7.1a | levelToPoints(1) = 1 | T1 | P0 |
| S-7.1b | levelToPoints(2) = 2 | T1 | P0 |
| S-7.1c | levelToPoints(3) = 4 | T1 | P0 |
| S-7.1d | levelToPoints(5) = 16 | T1 | P0 |
| S-7.1e | levelToPoints(10) = 512 | T1 | P0 |
| S-7.1f | levelToPoints(16) = 32768 (basic library max) | T1 | P1 |
| S-7.1g | levelToPoints(31) = 1073741824 (ender library max) | T1 | P1 |

### S-7.2 Deposit

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-7.2a | Deposit Sharpness V book: points for Sharpness = 16 | T3 | P0 |
| S-7.2b | Deposit Sharpness V book: maxLevel for Sharpness = 5 | T3 | P0 |
| S-7.2c | Deposit two Sharpness III books: points = 4 + 4 = 8 | T3 | P0 |
| S-7.2d | Deposit book with multiple enchantments: each tracked independently | T3 | P0 |
| S-7.2e | Deposit overflow: points capped at library maxPoints (no negative wrap) | T3 | P1 |
| S-7.2f | Only enchanted books accepted (plain book rejected) | T3 | P1 |
| S-7.2g | Non-book items rejected | T3 | P1 |
| S-7.2h | Deposit updates maxLevel to highest ever seen (Sharpness III then V -> maxLevel=5) | T3 | P1 |
| S-7.2i | Deposit Sharpness V then III: maxLevel stays 5 (doesn't downgrade) | T3 | P1 |

### S-7.3 Extract

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-7.3a | Extract Sharpness V onto diamond sword: sword has Sharpness V, points deducted by 16 | T3 | P0 |
| S-7.3b | Extract Sharpness III onto sword already with Sharpness I: cost = points(3) - points(1) = 3 | T3 | P0 |
| S-7.3c | Cannot extract level higher than maxLevel ever deposited | T3 | P0 |
| S-7.3d | Cannot extract if insufficient points for the level jump | T3 | P0 |
| S-7.3e | Extract onto empty item (no prior enchantment): full cost deducted | T3 | P1 |
| S-7.3f | Extract same level as current level: no-op (no change, no cost) | T3 | P1 |
| S-7.3g | Points never go below 0 after extraction | T3 | P1 |

### S-7.4 Library Tiers

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-7.4a | Basic library: maxLevel=16, maxPoints=32768 | T3 | P0 |
| S-7.4b | Ender library: maxLevel=31, maxPoints=1073741824 | T3 | P0 |
| S-7.4c | Basic library truncates deposited level > 16 to 16 | T3 | P1 |
| S-7.4d | Ender library accepts levels up to 31 | T3 | P1 |

### S-7.5 Library Persistence & Sync

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-7.5a | Library data survives save/load (deposit, save, load, verify points and maxLevels) | T3 | P0 |
| S-7.5b | Library sends update packet to nearby players on deposit | T3 | P2 |
| S-7.5c | Library sends update packet on extract | T3 | P2 |

### S-7.6 Automation

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-7.6a | Hopper above library inserts enchanted book: book consumed, points added | T3 | P1 |
| S-7.6b | Hopper below library cannot extract (always empty) | T3 | P1 |
| S-7.6c | Hopper with non-enchanted-book item: item not inserted, stays in hopper | T3 | P1 |
| S-7.6d | Hopper with stack of 2+ enchanted books: only 1 accepted per operation | T3 | P2 |

---

## S-8: Tomes (T1 + T2 + T3)

> **Apothic ref:** Design §8 (9 tomes, scrapping, extraction)

### S-8.1 Tome Enchantment Filtering

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-8.1a | Helmet tome only receives head-slot enchantments (Protection yes, Sharpness no) | T3 | P0 |
| S-8.1b | Weapon tome only receives sword enchantments | T3 | P0 |
| S-8.1c | Pickaxe tome only receives pickaxe enchantments | T3 | P0 |
| S-8.1d | Other tome receives enchantments not matching any specialized tome | T3 | P1 |
| S-8.1e | Each of 9 tome types filters correctly (one test per tome) | T3 | P1 |

### S-8.2 Tome Conversion

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-8.2a | Enchanted tome converts to enchanted book on right-click use | T3 | P0 |
| S-8.2b | Converted book retains all enchantments from the tome | T3 | P0 |
| S-8.2c | Unenchanted tome does nothing on right-click (pass result) | T3 | P1 |
| S-8.2d | Tome with enchantments shows "use to convert" tooltip | T2 | P2 |

### S-8.3 Scrapping Tomes

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-8.3a | Scrap tome + enchanted item in anvil: output item has fewer enchantments | T1 | P0 |
| S-8.3b | Scrap tome output book has the removed enchantments | T1 | P0 |
| S-8.3c | Scrap tome anvil cost = 6 levels per remaining enchantment | T1 | P1 |
| S-8.3d | Improved scrap tome: same mechanic, possibly better split | T1 | P1 |
| S-8.3e | Extraction tome: all enchantments move to book, item left clean | T1 | P0 |
| S-8.3f | Extraction tome with unenchanted item: no output | T1 | P1 |

---

## S-9: Anvil Modifications (T1 + T3)

> **Apothic ref:** Design §9 (curse removal, anvil repair, tome integration)

### S-9.1 Curse Removal (Prismatic Web)

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-9.1a | Cursed item + Prismatic Web in anvil: output has curses removed | T1 | P0 |
| S-9.1b | Non-curse enchantments are preserved after curse removal | T1 | P0 |
| S-9.1c | Anvil cost for curse removal = 30 levels (or configured amount) | T1 | P0 |
| S-9.1d | 1 Prismatic Web consumed per operation | T1 | P1 |
| S-9.1e | Item with no curses + Prismatic Web: no anvil output | T1 | P1 |

### S-9.2 Anvil Self-Repair

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-9.2a | Chipped anvil + iron block in anvil: output is less-damaged anvil | T3 | P1 |
| S-9.2b | Damaged anvil can be repaired back to normal through multiple steps | T3 | P1 |
| S-9.2c | Normal anvil + repair material: no output (already fully repaired) | T3 | P2 |
| S-9.2d | Repair cost = 5 levels per step (or configured amount) | T3 | P1 |

---

## S-10: Custom Enchantments (T3)

> **Apothic ref:** Design §10 (20 enchantments)
>
> **Note:** Each enchantment needs at minimum a "does it exist and apply" test. Effect-specific behavioral tests are listed where the effect is testable server-side. Client-only effects (visual, tooltip) cannot be GameTested.

### S-10.1 Registration & Applicability

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-10.1a | All custom enchantment IDs resolve in the enchantment registry | T3 | P0 |
| S-10.1b | Each enchantment can be applied to its target item type via command | T3 | P0 |
| S-10.1c | Enchantment max levels match expected values | T2 | P1 |
| S-10.1d | Incompatible enchantment pairs are correctly defined | T2 | P1 |

### S-10.2 Behavioral Tests (server-side testable)

| ID | Enchantment | Test | Tier | Pri |
|----|-------------|------|------|-----|
| S-10.2a | Chainsaw | Breaking a log with Chainsaw axe breaks connected logs | T3 | P1 |
| S-10.2b | Chainsaw | Breaking non-log block: no chain effect | T3 | P2 |
| S-10.2c | Knowledge of the Ages | Kill mob with KotA sword: item drops converted to XP orbs | T3 | P1 |
| S-10.2d | Knowledge of the Ages | Items in cannot_be_converted_to_xp tag are not converted | T3 | P2 |
| S-10.2e | Scavenger | Kill mob with Scavenger sword: extra loot roll occurs (statistical over many kills) | T3 | P2 |
| S-10.2f | Boon of the Earth | Break stone with Boon pickaxe: chance of bonus drop from boon loot table | T3 | P1 |
| S-10.2g | Life Mending | Heal entity wearing Life Mending armor: durability restored, health reduced | T3 | P1 |
| S-10.2h | Life Mending | No healing: no durability change | T3 | P2 |
| S-10.2i | Nature's Blessing | Right-click crop with Nature's Blessing hoe: crop advances growth stage | T3 | P1 |
| S-10.2j | Nature's Blessing | Right-click non-crop: no effect, no durability loss | T3 | P2 |
| S-10.2k | Stable Footing | Player flying + mining with Stable Footing boots: no speed penalty applied | T3 | P2 |
| S-10.2l | Shield Bash | Block attack with Shield Bash shield: attacker knocked back | T3 | P1 |
| S-10.2m | Reflective Defenses | Block attack with Reflective shield: attacker takes damage | T3 | P1 |
| S-10.2n | Icy Thorns | Take damage wearing Icy Thorns armor: attacker receives freeze/slowness | T3 | P2 |
| S-10.2o | Tempting | Hold Tempting item near animal: animal follows player | T3 | P2 |
| S-10.2p | Crescendo of Bolts | Fire crossbow with Crescendo: multiple shots without reloading | T3 | P2 |
| S-10.2q | Endless Quiver | Fire bow with Endless Quiver: no arrow consumed | T3 | P2 |
| S-10.2r | Rebounding | Take damage with Rebounding armor: attacker experiences knockback | T3 | P2 |
| S-10.2s | Worker Exploitation | Shear sheep with Exploitation shears: double wool, sheep takes damage | T3 | P2 |
| S-10.2t | Chromatic | Shear sheep with Chromatic shears: wool color randomized | T3 | P2 |
| S-10.2u | Growth Serum | Shear sheep with Growth Serum shears: wool regrows immediately | T3 | P2 |
| S-10.2v | Miner's Fervor | Mine block with Fervor pickaxe: faster than base speed (measure tick count) | T3 | P2 |
| S-10.2w | Berserker's Fury | Take damage with Berserker's chest: buffs applied, health ticks down | T3 | P2 |

---

## S-11: Enchantment Config & Level Scaling (T1 + T2)

> **Apothic ref:** Design §11

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-11.1a | Per-enchantment max level override: Sharpness config max=10 -> table can roll up to 10 | T1 | P1 |
| S-11.1b | Max loot level separate from max table level | T1 | P2 |
| S-11.1c | Hard cap: level capped regardless of NBT value | T1 | P1 |
| S-11.1d | Power function: min power at level N matches configured expression | T1 | P2 |
| S-11.1e | Power function: max power defaults to 200 | T1 | P2 |
| S-11.1f | Config values are synced server -> client on join | T3 | P2 |

---

## S-12: Particles (T3)

> **Apothic ref:** Design §12

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-12.1a | Fire particle type registered | T2 | P2 |
| S-12.1b | Water particle type registered | T2 | P2 |
| S-12.1c | Sculk particle type registered | T2 | P2 |
| S-12.1d | End particle type registered | T2 | P2 |
| S-12.1e | Hellshelf near table spawns fire particles (client-only, verify particle type assignment) | T2 | P3 |
| S-12.1f | Each shelf family maps to correct particle type | T2 | P2 |

---

## S-13: Advancements (T2)

> **Apothic ref:** Design §13

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-13.1a | All advancement JSON files parse without error | T2 | P1 |
| S-13.1b | Root advancement exists for enchanting system | T2 | P1 |
| S-13.1c | Advancement tree forms valid DAG (no orphans except root) | T2 | P2 |
| S-13.1d | Custom enchanted trigger fires when item is enchanted at table | T3 | P2 |
| S-13.1e | Stat milestone advancements reference correct stat thresholds | T2 | P2 |

---

## S-14: Network Sync (T3)

> **Apothic ref:** Design §15

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-14.1a | Stats payload codec roundtrips (encode -> decode = equal) | T1 | P0 |
| S-14.1b | Clue payload codec roundtrips | T1 | P1 |
| S-14.1c | Opening enchanting table sends stats payload to client | T3 | P1 |
| S-14.1d | Stats payload contains correct eterna/quanta/arcana values | T3 | P1 |
| S-14.1e | Changing shelf configuration updates stats and re-sends payload | T3 | P2 |
| S-14.1f | Enchantment info payload synced on player join / datapack reload | T3 | P2 |

---

## S-15: Data-Driven Content Validation (T2)

> **Apothic ref:** Design §16

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-15.1a | All enchanting stat JSONs parse without error | T2 | P0 |
| S-15.1b | Every shelf block has a corresponding stat JSON (no orphans) | T2 | P0 |
| S-15.1c | All crafting recipe JSONs parse without error | T2 | P0 |
| S-15.1d | All infusion recipe JSONs parse without error | T2 | P0 |
| S-15.1e | All advancement JSONs parse without error | T2 | P0 |
| S-15.1f | All tag JSONs parse and reference valid IDs | T2 | P1 |
| S-15.1g | Enchantment JSONs (1.21 data-driven format) parse without error | T2 | P0 |
| S-15.1h | Every block has blockstate JSON, model JSON, and loot table | T2 | P1 |
| S-15.1i | Every item has model JSON | T2 | P1 |
| S-15.1j | Lang file has translation key for every registered block, item, enchantment, and GUI label | T2 | P1 |

---

## S-16: Registration Completeness (T2 + T3)

> **Apothic ref:** Design §19 (file index — complete object list)

### S-16.1 Block Registry

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-16.1a | All 25+ shelf block IDs resolve in block registry | T3 | P0 |
| S-16.1b | Library and ender_library block IDs resolve | T3 | P0 |
| S-16.1c | Every registered block has a companion BlockItem | T3 | P0 |

### S-16.2 Item Registry

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-16.2a | All 9 tome item IDs resolve | T3 | P0 |
| S-16.2b | Prismatic web, infused breath, warden tendril item IDs resolve | T3 | P0 |
| S-16.2c | Scrap tome, improved scrap tome, extraction tome IDs resolve | T3 | P0 |
| S-16.2d | Music disc items resolve (if implemented) | T3 | P2 |
| S-16.2e | Ender lead items resolve (if implemented) | T3 | P2 |

### S-16.3 Other Registries

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-16.3a | Custom menu types resolve (enchanting_table, library) | T3 | P0 |
| S-16.3b | Block entity types resolve (filtering_shelf, treasure_shelf, library, ender_library) | T3 | P0 |
| S-16.3c | Custom recipe type resolves (infusion) | T3 | P0 |
| S-16.3d | Custom particle types resolve | T3 | P2 |
| S-16.3e | Creative tab exists and contains all mod items | T3 | P1 |

---

## S-17: Items & Miscellaneous (T3)

> **Apothic ref:** Design §19 (items section)

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-17.1a | Prismatic Web has no special use on its own (only via anvil) | T3 | P2 |
| S-17.1b | Infused Breath is an Epic rarity crafting material | T2 | P2 |
| S-17.1c | Warden Tendril drops from Warden via loot modifier | T3 | P1 |
| S-17.1d | Inert Trident is stack-of-1, infusable into Trident | T3 | P2 |
| S-17.1e | Ender Lead captures entity on use (if implemented) | T3 | P2 |
| S-17.1f | Ender Lead stores entity type and name in data components (if implemented) | T3 | P2 |

---

## S-18: Integration Hooks (T3)

> **Apothic ref:** Design §14 (API), §17 (integrations)

| ID | Test | Tier | Pri |
|----|------|------|-----|
| S-18.1a | Non-mod blocks (vanilla stone, etc.) return zero stats from stat registry (no crash) | T3 | P1 |
| S-18.1b | Block implementing stat interface provides custom stats to table | T3 | P1 |
| S-18.1c | Recipe viewer plugin class exists and loads without error (if applicable) | T2 | P2 |
| S-18.1d | Block info plugin class exists (if applicable) | T2 | P3 |

---

## Summary: Test Counts by Priority

| Priority | T1 (JUnit) | T2 (Loader-JUnit) | T3 (GameTest) | Total |
|----------|------------|-------------------|---------------|-------|
| **P0** | ~30 | ~8 | ~25 | ~63 |
| **P1** | ~15 | ~10 | ~35 | ~60 |
| **P2** | ~5 | ~8 | ~35 | ~48 |
| **P3** | 0 | 1 | 0 | 1 |
| **Total** | ~50 | ~27 | ~95 | **~172** |

## Implementation Order

1. **S-1** (stat math) + **S-16** (registration) — foundation, no world needed
2. **S-2** (stat gathering) + **S-3** (special shelves) — core table mechanics
3. **S-4** (selection) + **S-5** (menu) — enchanting flow end-to-end
4. **S-7** (library) + **S-6** (infusion) — secondary systems
5. **S-8** (tomes) + **S-9** (anvil) — item mechanics
6. **S-10** (enchantments) — individual enchantment effects
7. **S-11** through **S-18** — config, data, polish, integration
