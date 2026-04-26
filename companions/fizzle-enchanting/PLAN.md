# Fizzle Enchanting — Staged Work Plan

Two parallel workstreams converge into a single release-ready state:

1. **Test Migration & Coverage** — Migrate 60 legacy tests onto the modern three-tier harness, fill coverage gaps, then validate new feature areas.
2. **Apothic Parity Assessment** — Systematically compare Fizzle Enchanting against the Apothic design reference, surface gaps, and decide what to implement vs. intentionally skip.

Each **Stage** below is sized for one context window. Stages within the same tier can often run in parallel (noted with `PARALLEL-OK`). Check the box when the stage is done.

---

## Current State (as of 2026-04-27)

| Area | Status |
|------|--------|
| Epics 1-7 (TODO.md) | Complete |
| Epic 8 (Polish & Release) | S-8.1/8.2/8.3 done; S-8.4 (release prep) open |
| Epic 9 (Post-MVP) | Not started |
| Test infrastructure (S-0) | Complete — fabric-loader-junit + gametest source set wired |
| Existing tests | 55 in `src/test/`, 14 in `src/gametest/` |
| Legacy pattern | **Eliminated** — no unfreeze callers remain; `forkEvery=1` removed |
| Apothic assessment | Complete — Tier 1 gaps implemented, Tier 2+ deferred |
| Infusion recipes | 13 total (7 original + 6 new) |
| Advancements | 16 total (10 original + 6 new) |

---

## Stage 0 — Commit In-Flight Changes

- [x] **0.1** Review and commit the staged changes to `FizzleEnchantmentLogic.java` (eterna floor) and `FizzleEnchantmentMenu.java` (baseline stats). *(Done — `bff4947`)*

**Why first:** Uncommitted changes will conflict with test migration work that touches the same files.

---

## Stage 1 — Apothic Parity Assessment (Read-Only)

Run the 20-phase checklist from `Apothic-Fizzle-Enchanting-Comparrison.md`. This is pure research — no code changes. The output is a filled-in summary at the bottom of that document.

**Goal:** Know exactly which Apothic features are implemented, partial, missing, or intentionally skipped before writing any new tests or code.

**Parallelization:** These phases are independent reads. Run 3-4 agents simultaneously, each assigned a batch of phases.

- [x] **1.A** Phases 1-5 (structure, stat system, selection, GUI, shelves) `PARALLEL-OK`
- [x] **1.B** Phases 6-10 (special shelves, infusion, library, tomes, anvil) `PARALLEL-OK`
- [x] **1.C** Phases 11-15 (enchantments, config, particles, advancements, API) `PARALLEL-OK`
- [x] **1.D** Phases 16-20 (network, data-driven audit, mixins, items, integrations) `PARALLEL-OK`
- [x] **1.E** Consolidate findings into the Summary Template. Produce a ranked list of gaps and a decision table: implement / defer / skip for each gap.

**Deliverable:** Updated `Apothic-Fizzle-Enchanting-Comparrison.md` with all checklists filled and summary block complete.

---

## ~~Stage 2 — Test Migration Phase 1 (Clean-Bootstrap T2)~~ ALREADY DONE

All 12 clean-bootstrap tests already have `STATE: T2` (terminal) in TESTING-TODO.md. They were migrated to fabric-loader-junit during implementation. No work remains.

- [x] **2.1-2.7** All Phase 1 migrations complete. Tests use `@BeforeAll Bootstrap.bootStrap()` via fabric-loader-junit.

---

## Stage 3 — Test Migration Phase 2 (Unfreeze T2)

Migrate the 9 tests that use `Bootstrap.bootStrap()` + reflective unfreeze (but don't register mod content). Rewrite to use real vanilla enchants via `BuiltInRegistries` post-bootstrap.

**Ref:** `TESTING-TODO.md` Phase 2, items 11-15.

- [x] **3.1** S-4.3 library engine — `EnchantmentLibraryBlockEntityTest`, `LibraryTierBlockEntityTest`, `LibraryNbtTest`, `LibraryClientSyncTest` (TEST-4.3-T2a/b/c/d)
- [x] **3.2** S-4.5 hopper storage — `LibraryStorageTest` (TEST-4.5-T2)
- [x] **3.3** S-4.6 custom recipes — `EnchantingRecipeTest`, `EnchantingRecipeRegistryTest` (TEST-4.6-T2a/b/c/d)
- [x] **3.4** S-5.3 crafting lookup — `TableCraftingLookupTest` (TEST-5.3-T2a)
- [x] **3.5** S-3.1 shelf block — `EnchantingShelfBlockTest` (TEST-3.1-T2a)

**Gate:** After this stage, all unfreeze-reflection tests are gone. Evaluate whether `forkEvery=1` can be dropped.

---

## Stage 4 — Test Migration Phase 3 (T3 Rewrites)

Rewrite the 18 tests that call `FizzleEnchantingRegistry.register()` post-bootstrap. These move from `src/test/` to `src/gametest/` with SNBT templates.

**Ref:** `TESTING-TODO.md` Phase 3, items 16-26.

These are the most expensive migrations. Group by shared fixtures to reduce template churn.

- [x] **4.1** Shelf registry + roster — `RegistryGameTest`, `ShelfRosterGameTest` (TEST-3.1-T3, TEST-3.2-T3) — STATE: T3
- [x] **4.2** Filtering + treasure shelf gametest (TEST-3.5-T3a/b) — STATE: T3
- [x] **4.3** Shelf scan real-level (TEST-2.2-T3a/b) — STATE: T3
- [x] **4.4** Menu end-to-end (TEST-2.5-T3) — MenuEndToEndGameTest
- [x] **4.5** Prismatic web end-to-end (TEST-4.1-T3) — PrismaticWebGameTest
- [x] **4.6** Library block + menu (TEST-4.4-T3a/b) — LibraryGameTest
- [x] **4.7** Tome items register (TEST-5.1-T3) — TomeRegistryGameTest
- [x] **4.8** Tome anvil handlers (TEST-5.2-T3a/b/c/d) — TomeAnvilGameTest
- [x] **4.9** Crafting button id=2 (TEST-5.3-T3) — CraftingButtonGameTest
- [x] **4.10** Specialty materials (TEST-5.4-T3a/b/c/d) — SpecialtyMaterialsGameTest

**Gate:** `./gradlew test` and `./gradlew runGametest` both green. Delete the legacy test files that were rewritten.

---

## Stage 5 — Datagen Cleanup

**Ref:** `TESTING-TODO.md` Phase 4, item 27.

- [x] **5.1** Delete `FizzleModelProviderTest`, `FizzleBlockLootTableProviderTest`, `FizzleRecipeProviderTest`. Also migrated remaining unfreeze tests: `AdvancementCodecTest` → T3 gametest, `TomeRecipeClassifierTest` → T3 gametest, `TableCraftingDisplayExtractorTest` → clean T2. Removed `forkEvery=1`.
- [x] **5.2** Add `runDatagen` + filesystem sweep test (TEST-3.4-T1a) and idempotency check (TEST-3.4-T1b). Fixed datagen run by adding gametest source set; generated missing library/ender_library loot tables.

**Gate:** `./gradlew runDatagen && git diff --exit-code src/main/generated/` clean.

---

## Stage 6 — Coverage Gap Fills (New Tests)

Pure-math T1 extractions and missing T2/T3 coverage. These don't require legacy migration — they're net-new.

**Ref:** `TESTING-TODO.md` Phase 5, items 28-40.

### Batch A — Pure math T1 extractions `PARALLEL-OK`
- [x] **6.A1** TEST-2.4-T1 — pure cost math slice from `RealEnchantmentHelperTest`
- [x] **6.A2** TEST-3.5-T1 — slot-targeting coordinate math (extracted `ShelfSlotMapping` utility)
- [x] **6.A3** TEST-5.2-T1 — damage clamp arithmetic (extracted `clampDamage` from `ExtractionTomeHandler`)
- [x] **6.A4** TEST-3.1-T1a — SKIPPED: `ParticleTheme` enum requires `ParticleTypes` from Bootstrap at class-load time; already fully covered by T2 `ParticleThemeTest` (parameterized, 5 values, failing row on new additions)

### Batch B — Missing T2 coverage `PARALLEL-OK`
- [x] **6.B1** TEST-2.1-T2 — stat JSON codec sweep (all 31 shipped JSONs parse via `StatEntry.CODEC`)
- [x] **6.B2** TEST-2.2-T2 — offset list size guard (32 offsets, no duplicates, midpoint invariants)
- [x] **6.B3** TEST-3.5-T2 — DEFERRED: `FilteringShelfBlockEntity` constructor requires mod-registered `BlockEntityType`; NBT round-trip deferred to T3
- [x] **6.B4** TEST-4.1-T2 — Prismatic Web curse-stripping logic (extracted `stripCurses`/`hasAnyCurse`; config gate tests)
- [x] **6.B5** TEST-5.1-T2 — DEFERRED: recipe JSON references mod items not parseable at T2; deferred to T3
- [x] **6.B6** TEST-5.2-T2a/b/c — `ScrapTomeHandler` seeded pick helpers + `ExtractionTomeHandler.stripAndDamage` at T2

### Batch C — Missing T3 real-level assertions
- [x] **6.C1** TEST-2.1-T3 — stat registry under real datapack reload (`StatRegistryGameTest`)
- [x] **6.C2** TEST-4.2-T3 — anvil click flow in real level (`AnvilRepairGameTest`)
- [x] **6.C3** TEST-4.3-T3 — library persist through save/load (`LibraryPersistGameTest`)
- [x] **6.C4** TEST-4.5-T3 — hopper transfers books to library (`LibraryHopperGameTest` — tests Transfer API path directly)
- [x] **6.C5** TEST-1.2-T3 — mod loads sentinel (`ModSentinelGameTest`)
- [x] **6.C6** TEST-1.4-T3 — reload end-to-end (`ConfigReloadGameTest`)

---

## Stage 7 — Apothic Parity Gap Implementation

**Depends on:** Stage 1 (assessment results). The exact content here is determined by the gap analysis.

This is the stage where we act on the Apothic comparison findings. Each sub-stage maps to a gap category.

- [x] **7.1** Review Stage 1.E decision table and prioritize gaps.
- [x] **7.2** Implement Tier 1 gaps: `fabric.mod.json` suggests, 6 infusion recipes (honey→XP x3, echo shard, golden carrot, budding amethyst), 6 advancements (sculk mastery, stable enchanting, all-seeing, curator, treasure seeker, web spinner).
- [x] **7.3** Write corresponding tests: updated advancement roster in `AdvancementCodecGameTest`, added recipe type-field + stat validation tests in `EnchantingRecipeTest`.
- [x] **7.4** Update `Apothic-Fizzle-Enchanting-Comparrison.md` to mark resolved gaps.

**Tier 2+ gaps deferred to future sessions:** custom particle types, music discs, ModMenu config GUI, custom advancement trigger, info browser, WTHIT, Inert Trident, Ender Leads.

**Note:** This stage will likely expand into multiple sub-stages once the assessment is complete.

---

## Stage 8 — Enchantment Roster Tests (Epic 6)

**Ref:** `TESTING-TODO.md` Phase 6, items 41-45.

- [x] **8.1** TEST-6.1-T1a/b/c — filesystem sweeps (50 ported files, no `enchantplus:` literals, lang keys) — `PortedEnchantmentsTest`, `PortedEnchantmentLangTest`
- [x] **8.2** TEST-6.1-T2/T2b — codec parse + exclusive_set tag validation — `PortedEnchantmentsTest` (schema), `PortedExclusiveSetTagsTest`
- [x] **8.3** TEST-6.1-T3 — all 50+2 ids in registry, cut ids absent — `EnchantmentRosterGameTest`
- [x] **8.4** TEST-6.2-T1/T2a/T2b/T3 — authored enchants — `AuthoredIcyThornsTest`, `AuthoredShieldBashTest`, `IcyThornsEffectGameTest`
- [x] **8.5** TEST-6.3-T1/T2/T3a/T3b — foreign overrides — `ForeignEnchantmentOverridesTest`, `MendingOverrideTest`, `SoulboundOverrideTest`, `EnchantmentRosterGameTest.mendingOverrideHasBundledWeight`

---

## Stage 9 — Integration Tests (Epic 7)

**Ref:** `TESTING-TODO.md` Phase 7, items 46-49.

- [x] **9.1** TEST-7.1-T1a/b + TEST-7.1-T2 — EMI entrypoint wiring + shared extractor — `IntegrationEntrypointTest`, `TableCraftingDisplayExtractorTest`
- [x] **9.2** TEST-7.2-T1/T2 — REI adapter — `IntegrationEntrypointTest` (entrypoint + source file), extractor shared with EMI
- [x] **9.3** TEST-7.3-T1/T3 — JEI classload + manual smoke — `IntegrationEntrypointTest` (entrypoint + source file); T3 manual
- [x] **9.4** TEST-7.4-T1 — Jade tooltip builders — `JadeTooltipFormatterTest`

---

## Stage 10 — Release Polish Tests (Epic 8)

**Ref:** `TESTING-TODO.md` Phase 8, items 50-52.

- [x] **10.1** TEST-8.1-T1/T2/T3 — advancement tree — `AdvancementCodecGameTest` (lang sweep + codec parse + roster); T3 apotheosis trigger deferred (complex setup)
- [x] **10.2** TEST-8.2-T1a/b — tooltip formatter + book toggle — `TooltipFormatterTest`
- [x] **10.3** TEST-8.4-T1a/b — release gate — CI-level (`./gradlew test runGametest` green); changelog lint is release-prep (Stage 11)

---

## Stage 11 — Release Prep (S-8.4)

**Ref:** `TODO.md` Story S-8.4.

- [ ] **11.1** `./gradlew runDatagen && ./gradlew clean build test runGametest` — all green.
- [ ] **11.2** `forkEvery=1` removed (all legacy tests migrated by now).
- [ ] **11.3** `CHANGELOG.md` gets `[0.1.0]` entry.
- [ ] **11.4** `plugins/gameplay.md` gets `## Fizzle Enchanting` entry.
- [ ] **11.5** Root `CHANGELOG.md` `[Unreleased] > Added` entry.
- [ ] **11.6** Run `/check-conflicts` and update compatibility matrix.

---

## Stage 12 — Post-MVP (Epic 9, on demand)

Not scheduled until MVP ships and is playtested.

- [ ] **12.1** S-9.1 — Absorb BeyondEnchant (16 override JSONs + levelCaps config)
- [ ] **12.2** S-9.2 — Absorb Easy Magic (table BE item persistence)
- [ ] **12.3** S-9.3 — Easy Anvils tooExpensiveCap (decision pending)

---

## Execution Strategy

### What can run in parallel

| Stages | Why parallel |
|--------|--------------|
| 1.A / 1.B / 1.C / 1.D | Independent read-only assessment phases |
| 2.x batches marked `PARALLEL-OK` | Independent test files, no shared fixture changes |
| 6.A / 6.B / 6.C | Independent new tests at different tiers |
| 8.1 / 8.2 | T1 filesystem sweeps vs T2 codec tests |
| 9.1 / 9.2 / 9.3 | Independent integration adapters |

### What must be sequential

| Dependency | Reason |
|-----------|--------|
| Stage 0 before anything | Uncommitted changes block clean work |
| Stage 2 before Stage 3 | Phase 1 migrations remove boilerplate that Phase 2 depends on understanding |
| Stage 3 before Stage 4 | Need all T2 migrations done before T3 rewrites (shared fixtures) |
| Stage 4 before Stage 5 | Datagen cleanup depends on knowing which legacy tests survive |
| Stage 1 before Stage 7 | Can't implement gaps without knowing what they are |
| Stages 2-10 before Stage 11 | Release prep is the gate |

### Recommended session order

1. **Session 1:** Stage 0 (commit) + Stage 1 (assessment, parallel agents)
2. **Session 2:** Stage 2 (clean T2 migrations)
3. **Session 3:** Stage 3 (unfreeze T2 migrations)
4. **Session 4:** Stage 4 (T3 rewrites, biggest session)
5. **Session 5:** Stage 5 (datagen) + Stage 6.A (pure math T1) — parallel
6. **Session 6:** Stage 6.B + 6.C (remaining coverage gaps)
7. **Session 7:** Stage 7 (Apothic gap implementation — scope TBD from Stage 1)
8. **Session 8:** Stage 8 (enchantment roster tests)
9. **Session 9:** Stage 9 (integrations) + Stage 10 (release tests) — parallel
10. **Session 10:** Stage 11 (release prep)

### Metrics

- **Total existing tests to migrate:** 39 (12 clean-bootstrap + 9 unfreeze + 18 register-heavy)
- **Total new tests to write:** ~55 (coverage gaps + Apothic parity + roster + integration + release)
- **Total test files at completion:** ~100+ across `src/test/` and `src/gametest/`
- **Legacy `forkEvery=1` removed:** After Stage 4

---

## How to use this file

1. Find the first unchecked Stage.
2. Read its description and ref links.
3. Do the work; check boxes as you go.
4. When the stage is done, check the stage box and commit.
5. For `PARALLEL-OK` items, spawn parallel agents.
6. Update this file with notes/decisions as gaps are resolved in Stage 7.
