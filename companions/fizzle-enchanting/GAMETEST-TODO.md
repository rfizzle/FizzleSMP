# Fizzle Enchanting — GameTest TODO

> Derived from `Apothic-GameTests.md` gap analysis against existing test suite.
> Each section lists tests that are **not yet implemented** and would add meaningful coverage.
> Mark items `[x]` as they are completed.

---

## 1. S-8.2 Tome Conversion (T3) — Priority: Critical

N/A — Typed tomes with right-click conversion were cut from the MVP (DESIGN.md). Our tomes (scrap/improved scrap/extraction) are anvil-based and already covered in `TomeAnvilGameTest`.

- [x] S-8.2a — ~~Enchanted tome converts to enchanted book on right-click use~~ N/A
- [x] S-8.2b — ~~Converted book retains all enchantments from the tome~~ N/A
- [x] S-8.2c — ~~Unenchanted tome does nothing on right-click (pass result)~~ N/A

---

## 2. S-5 Enchanting Table Menu (T3) — Priority: High

`MenuEndToEndGameTest` has only 3 tests. The primary player-facing UI needs broader coverage.

### S-5.1 Menu Basics
- [x] S-5.1b — Menu has item slot (slot 0) and lapis slot (slot 1)
- [x] S-5.1c — Stats are computed and stored on menu when item is placed
- [x] S-5.1d — Costs array has 3 entries corresponding to 3 enchanting slots

### S-5.2 Lapis & XP Requirements
- [x] S-5.2a — Slot 0 costs 1 lapis, slot 1 costs 2, slot 2 costs 3
- [x] S-5.2b — Cannot enchant without sufficient lapis
- [x] S-5.2c — Cannot enchant without sufficient player XP levels
- [x] S-5.2d — Successful enchant consumes correct lapis and XP

### S-5.3 Enchanting Results
- [ ] S-5.3a — Diamond sword at eterna=30: receives at least 1 enchantment from sword pool
- [ ] S-5.3b — Book at eterna=30: receives at least 1 enchantment
- [ ] S-5.3c — Item with 0 enchantability: no enchantment options available
- [ ] S-5.3d — After enchanting, item has enchantments and costs are reset

**File:** `src/gametest/java/.../gametest/MenuEndToEndGameTest.java` (extend existing)

---

## 3. S-6.3 Infusion in World (T3) — Priority: High

`CraftingButtonGameTest` covers basic recipe matching. Edge cases and keep-NBT are untested.

- [ ] S-6.3b — Item with stats below min: no infusion, normal enchant available
- [ ] S-6.3c — Item with stats above max: no infusion match
- [ ] S-6.3d — Shelf upgrade infusion (hellshelf at correct stats -> infused hellshelf)
- [ ] S-6.3e — Keep-NBT infusion (library -> ender library retains stored data)
- [ ] S-6.3f — Infusion consumes exactly 1 input and produces correct output count

**File:** `src/gametest/java/.../gametest/CraftingButtonGameTest.java` (extend existing)

---

## 4. S-7 Library Edge Cases (T3) — Priority: High

Core deposit/extract/persist/hopper tests exist. These edge cases are missing.

### S-7.2 Deposit Edge Cases
- [ ] S-7.2e — Deposit overflow: points capped at library maxPoints (no negative wrap)
- [ ] S-7.2h — Deposit updates maxLevel to highest ever seen (III then V -> maxLevel=5)
- [ ] S-7.2i — Deposit V then III: maxLevel stays 5 (doesn't downgrade)

### S-7.3 Extract Edge Cases
- [ ] S-7.3b — Extract onto sword with Sharpness I, requesting III: cost = points(3) - points(1)
- [ ] S-7.3f — Extract same level as current level: no-op (no change, no cost)

### S-7.4 Library Tier Boundaries
- [ ] S-7.4c — Basic library truncates deposited level > 16 to 16
- [ ] S-7.4d — Ender library accepts levels up to 31

**File:** `src/gametest/java/.../gametest/LibraryGameTest.java` (extend existing)

---

## 5. S-2 Stat Gathering — Individual Shelves (T3) — Priority: Medium

Unit-level `gather_*` tests verify the math, but no T3 test places a specific shelf and asserts exact stat values against the JSON definitions.

### S-2.1 Vanilla Baseline
- [ ] S-2.1a — Enchanting table with no shelves: eterna=0, quanta=15, arcana=itemEnch/2
- [ ] S-2.1c — 32 vanilla bookshelves (all offsets filled): eterna still capped at 15

### S-2.2 Custom Shelf Stats
- [ ] S-2.2a — Single hellshelf at valid offset: eterna=3, quanta=18
- [ ] S-2.2b — 15 hellshelves: eterna = min(45, 15*3) = 45
- [ ] S-2.2c — 15 vanilla bookshelves + 1 hellshelf: eterna = 18 (step-ladder)
- [ ] S-2.2d — Draconic endshelf alone: eterna=20, maxEterna=100
- [ ] S-2.2e — Stoneshelf produces negative eterna and negative arcana
- [ ] S-2.2f — Beeshelf produces quanta=100 and eterna=-30
- [ ] S-2.2g — Melonshelf produces negative quanta and negative eterna
- [ ] S-2.2h — Sightshelf contributes only clues (no eterna/quanta/arcana)
- [ ] S-2.2i — Sightshelf_t2 contributes 2 clues

### S-2.4 Mixed Configurations
- [ ] S-2.4a — Max eterna config: combination that reaches exactly 100
- [ ] S-2.4d — All-negative shelf setup: eterna floors at 0 (not negative)

**File:** `src/gametest/java/.../gametest/ShelfScanGameTest.java` (extend existing)

---

## 6. S-4 Enchantment Selection — Integration (T3) — Priority: Medium

Unit tests cover selection math. These T3 tests validate pool filtering works end-to-end in a real world.

### S-4.2 Pool Filtering
- [ ] S-4.2a — Only IN_ENCHANTING_TABLE enchantments appear when treasure=false
- [ ] S-4.2b — TREASURE enchantments appear when treasure=true
- [ ] S-4.2c — Blacklisted enchantments never appear in selection (100 rolls, verify absence)
- [ ] S-4.2d — Enchantments already on item are excluded from pool
- [ ] S-4.2e — Only enchantments valid for item type appear (sword gets sword enchants, not bow)

**File:** `src/gametest/java/.../gametest/EnchantmentSelectionGameTest.java` (new)

---

## 7. S-10.2 Custom Enchantment Behaviors (T3) — Priority: Medium

Registration and definition tests exist. These behavioral tests verify actual in-world effects.

- [ ] S-10.2e — Scavenger: kill mob -> extra loot roll occurs (statistical)
- [ ] S-10.2i — Nature's Blessing: right-click crop with hoe advances growth stage
- [ ] S-10.2j — Nature's Blessing: right-click non-crop has no effect


**File:** `src/gametest/java/.../gametest/enchantments/` (extend existing per-category files)

---

## Summary

| # | Section | Tests | Priority |
|---|---------|-------|----------|
| 1 | S-8.2 Tome Conversion | 3 | Critical |
| 2 | S-5 Menu | 11 | High |
| 3 | S-6.3 Infusion in World | 5 | High |
| 4 | S-7 Library Edge Cases | 7 | High |
| 5 | S-2 Shelf Stats in World | 13 | Medium |
| 6 | S-4 Selection Integration | 5 | Medium |
| 7 | S-10.2 Enchantment Behaviors | 13 | Medium |
| | **Total** | **57** | |
