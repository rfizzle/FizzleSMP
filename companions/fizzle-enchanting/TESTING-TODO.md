# Fizzle Enchanting — Testing Plan

Companion to `TODO.md`. Mirrors its epic/story/task structure. Each story has its coverage planned across the three tiers from `.claude/skills/fabric-testing/SKILL.md`:

- **Tier 1** — pure JUnit, no `net.minecraft.*` / `net.fabricmc.*` references, or MC types used purely as POJOs (no registry reads). Fast, no Bootstrap.
- **Tier 2** — `fabric-loader-junit` + explicit `@BeforeAll Bootstrap.bootStrap()`. Vanilla registries, enchantments, attributes, codecs, mixin/AW wiring. Cannot register mod content.
- **Tier 3** — Fabric Gametest under `./gradlew runGametest`. Real `ServerLevel`, mod-registered content, block entities, menus, tick loop, loot tables.

The existing test suite predates the skill. It uses a prohibited bootstrap-plus-reflection pattern that registers mod items into frozen vanilla registries. This file is therefore both a **coverage plan** (what tests should exist) and a **migration plan** (how the 60 existing tests map onto the three tiers).

## Test-state legend

Per test row, the leading checkbox carries the *coverage* state; the trailing `STATE:` tag carries the *migration* state.

- `[x]` coverage exists; `[ ]` coverage missing.
- `STATE: T1-pure` — pure JUnit, no MC imports. Leave alone.
- `STATE: T1-pseudo` — MC types used as POJOs (`ResourceLocation`, `Component`, Brigadier trees, ASM bytecode introspection). The skill explicitly allows this — document, leave alone.
- `STATE: T2` / `STATE: T3` — migration complete; test lives on fabric-loader-junit (Tier 2) or the gametest source set (Tier 3). Terminal states, same as `T1-pure`.
- `STATE: T2-migrate-clean` — existing legacy test already uses `Bootstrap.bootStrap()` without unfreeze or register. Drop `forkEvery=1`, add `fabric-loader-junit` dep, keep assertions byte-identical.
- `STATE: T2-migrate-unfreeze` — existing legacy test builds a synthetic enchantment registry via reflection to supply fake enchants. Rewrite to read real vanilla enchants via `BuiltInRegistries.ENCHANTMENT.getHolder(Enchantments.SHARPNESS).orElseThrow()` after bootstrap. No unfreeze reflection.
- `STATE: T3-rewrite` — existing legacy test calls `FizzleEnchantingRegistry.register()` post-bootstrap (prohibited). Rewrite as a Fabric Gametest under `src/gametest/`. Sibling Tier 2 coverage may exist alongside.
- `STATE: T3-new` — no existing coverage at any tier for this behavior; write fresh as Gametest.
- `STATE: T2-new` — same, write fresh at Tier 2.
- `STATE: T1-new` — same, fresh Tier 1.

## Migration arithmetic (existing 60 tests)

| Category | Count | Target |
|---|---:|---|
| Pure Tier 1 — keep | 11 | T1-pure |
| Pseudo Tier 1 — keep | 10 | T1-pseudo |
| Clean Bootstrap — migrate | 12 | T2-migrate-clean |
| Bootstrap + unfreeze, no register — migrate | 9 | T2-migrate-unfreeze |
| Bootstrap + unfreeze + register — rewrite | 18 | T3-rewrite |
| **Total** | **60** | |

Keep-as-is (21) + migrate (21) + rewrite (18). The rewrites drop out of `src/test/` and reappear under `src/gametest/` — fixture SNBT templates go under `src/main/resources/data/fizzle_enchanting/gametest/structure/`.

## How to use this file

1. Complete **Story S-0** first. Without `fabric-loader-junit` on the classpath and a gametest source set wired, no Tier 2 or Tier 3 migration can start.
2. Pick a story whose impl is `- [x]` in `TODO.md` but whose tests here still show any `STATE: T2-migrate-*` or `STATE: T3-rewrite` row.
3. Do the whole story at once — do not leave a story half-migrated.
4. After each story's migration, run `./gradlew :companions:fizzle-enchanting:test` (and `runGametest` once any Tier 3 lands) before the commit. Each migrating commit: `refactor(test): migrate <story-id> to fabric-loader-junit / gametest`.
5. Per-story coverage-gap rows (`[ ] STATE: T*-new`) can land in the same commit as the migration if they share fixtures, or in a follow-up `test(<scope>): ...` commit.

---

## Table of contents

- [Story S-0 — Test Infrastructure (prerequisite)](#story-s-0--test-infrastructure-prerequisite)
- [Epic 1 — Project Scaffolding](#epic-1--project-scaffolding)
- [Epic 2 — Stat System & Table](#epic-2--stat-system--table)
- [Epic 3 — Shelf Family](#epic-3--shelf-family)
- [Epic 4 — Anvil & Library](#epic-4--anvil--library)
- [Epic 5 — Tomes & Table Crafting UX](#epic-5--tomes--table-crafting-ux)
- [Epic 6 — Enchantment Roster](#epic-6--enchantment-roster)
- [Epic 7 — Integrations](#epic-7--integrations)
- [Epic 8 — Polish & Release](#epic-8--polish--release)
- [Epic 9 — Post-MVP Iteration Backlog](#epic-9--post-mvp-iteration-backlog)
- [Ordering](#ordering)

---

# Story S-0 — Test Infrastructure (prerequisite)

Sets up the two missing tier surfaces. Until this lands, every Tier-2/3 row below is unreachable. Risk if skipped: half-migrated suite where some files bootstrap via `forkEvery=1` reflection and others expect Knot — under one JVM those two modes interact badly and the whole suite goes flaky.

**Commit at story close:** `refactor(test): wire fabric-loader-junit and gametest source set`

## Task S-0.1 — fabric-loader-junit dependency

- [x] **TEST-0.1-T2** — Tier 2 harness boots; a throwaway vanilla-read smoke test passes.
  - **Tier:** 2.
  - **State:** new.
  - **Acceptance:**
    - [x] `testImplementation "net.fabricmc:fabric-loader-junit:${project.loader_version}"` in `build.gradle` (read version from `gradle.properties`).
    - [x] `configurations.testRuntimeClasspath { exclude group: 'net.fabricmc.fabric-api', module: 'fabric-api' }` present — the loom-remapped `*-common` variants stay on the classpath.
    - [ ] `forkEvery = 1` removed from the `test {}` block. *(Deferred — 29 legacy tests still reflectively unfreeze `BuiltInRegistries` and register mod content; without per-class forking they collide in a shared JVM. Remove once Phase 3's Tier-3 rewrites land.)*
    - [x] `./gradlew :companions:fizzle-enchanting:dependencies --configuration testRuntimeClasspath | grep fabric-loader-junit` shows the dep.
    - [x] A smoke test class (`com.fizzlesmp.fizzle_enchanting.SmokeBootstrapTest`, `// Tier: 2`) with `@BeforeAll Bootstrap.bootStrap()` and one assertion that `Items.DIAMOND_SWORD != null` passes on its own JVM session.

## Task S-0.2 — Gametest source set and run config

- [x] **TEST-0.2-T3** — `runGametest` task exists and a placeholder gametest passes.
  - **Tier:** 3.
  - **State:** new.
  - **Acceptance:**
    - [x] `sourceSets { gametest { ... } }` added in the order the skill specifies (source set *before* `loom { runs { gametest { source sourceSets.gametest } } }`).
    - [x] `configurations { gametestImplementation.extendsFrom implementation; gametestRuntimeOnly.extendsFrom runtimeOnly }`.
    - [x] `loom.runs.gametest` block with `vmArg "-Dfabric-api.gametest"` and junit report path.
    - [x] `src/main/resources/fabric.mod.json` gets a `fabric-gametest` entrypoint pointing at the placeholder class.
    - [x] `src/main/resources/data/fizzle_enchanting/gametest/structure/empty_3x3.snbt` template shipped (DataVersion 3955, 3×3×3 stone floor + air above).
    - [x] Placeholder `com.fizzlesmp.fizzle_enchanting.gametest.PlaceholderGameTest` (`// Tier: 3`) with a single `helper.succeed()` test passes on `./gradlew :companions:fizzle-enchanting:runGametest`.

## Task S-0.3 — CI wiring

- [x] **TEST-0.3** — CI runs `test` and `runGametest` on every PR touching this mod.
  - **Tier:** n/a (CI config).
  - **State:** new.
  - **Acceptance:**
    - [x] GitHub Actions (or local `Makefile` target) invokes both.
    - [x] `junit-gametest.xml` reports land under `build/` and are uploaded as a workflow artifact.

---

# Epic 1 — Project Scaffolding

Gradle/Loom wiring, entrypoints, JSON config, and the `/fizzleenchanting` command skeleton. If any of this silently regresses, the mod either (a) fails to build, (b) boots with default config every time because `load()` silently swallowed a parse error, (c) clamps are removed and operators ship servers with invalid values, or (d) `/fizzleenchanting reload` runs at perm 0 and lets non-ops mutate live config. The build failure is loud; the other three are silent.

## Story S-1.1 — Buildable Gradle project

Pure build plumbing. `./gradlew build` is the oracle. No test rows at any tier.

## Story S-1.2 — Mod entrypoints

Trivial surface. Covered by `ModBootTest` for the constants; mod-actually-loads is covered implicitly by any Tier 3 test passing.

### Tier 1

- [x] **TEST-1.2-T1** — `MOD_ID`, `LOGGER`, `id()` helper constants correct.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] `MOD_ID == "fizzle_enchanting"`.
    - [x] `LOGGER != null`.
    - [x] `id("foo").toString() == "fizzle_enchanting:foo"`.
  - **File:** `src/test/java/.../ModBootTest.java`.

### Tier 3

- [ ] **TEST-1.2-T3** — Client and data-generator entrypoints classload in a gametest run.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Under `runGametest`, `Class.forName("com.fizzlesmp.fizzle_enchanting.client.FizzleEnchantingClient")` resolves on client-side gametest runs.
    - [ ] Asserts the mod's `onInitialize` has fired exactly once (logger sentinel or a static `boolean initialized` flag).
  - **Dependencies:** TEST-0.2-T3.

## Story S-1.3 — Configuration surface

Operator-facing JSON config. No registry reads required — all pure math + filesystem. Tier 2/3 not needed.

### Tier 1

- [x] **TEST-1.3-T1a** — Defaults class instantiates with every DESIGN field + clamp-valid.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] Every nested section (`EnchantingTable`, `Shelves`, `Anvil`, `Library`, `Tomes`, `Warden`, `ForeignEnchantments`, `Display`) has its DESIGN default.
    - [x] `configVersion == 1`.
  - **File:** `src/test/java/.../config/FizzleEnchantingConfigTest.java`.

- [x] **TEST-1.3-T1b** — GSON load/save + missing-file + partial-file handling.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] Missing file → defaults written + returned.
    - [x] Partial JSON → missing sections filled from defaults.
    - [x] Round-trip preserves values.
    - [x] Malformed JSON → warn + defaults returned.
  - **File:** same as above.

- [x] **TEST-1.3-T1c** — Clamp rows + color regex fallback.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] Every DESIGN clamp row enforced (`maxEterna:0→1`, `sculkShelfShriekerChance:-0.5→0`, `tendrilLootingBonus:2.0→1`, …).
    - [x] `overLeveledColor: "not-a-hex" → "#FF6600"` fallback.
    - [x] Every clamp emits a warn log line.
  - **File:** same as above.

- [x] **TEST-1.3-T1d** — Migration hook is a no-op at `configVersion >= CURRENT_VERSION`.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] `configVersion: 1` → no-op.
    - [x] `configVersion: 2` (future-dated) → no downgrade, no throw.
  - **File:** same as above.

## Story S-1.4 — `/fizzleenchanting` command skeleton

Brigadier tree walks don't need a real server — pseudo-T1 covers the whole story. A Tier-3 end-to-end reload is a stretch goal once the gametest harness lives.

### Tier 1

- [x] **TEST-1.4-T1** — Brigadier dispatch for `reload`, `stats`, `library`, `give-tome`; permission gates enforced; translation keys emit.
  - **Tier:** 1 (pseudo — uses `CommandDispatcher`, `Component`, `CommandSourceStack` as POJOs).
  - **State:** T1-pseudo.
  - **Acceptance:**
    - [x] `fizzleenchanting reload` at perm 2 returns `Command.SINGLE_SUCCESS` and re-reads config.
    - [x] `fizzleenchanting reload` at perm 0 rejects before execution.
    - [x] Each stub subcommand parses at its declared perm level.
    - [x] `give-tome <player> scrap|improved_scrap|extraction` parses literal argument; unknown literals reject.
  - **File:** `src/test/java/.../command/FizzleEnchantingCommandTest.java`.

### Tier 3

- [ ] **TEST-1.4-T3** — `reload` on a live `GameTestServer` actually re-reads the config file from disk.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Start gametest, mutate `config/fizzle_enchanting.json` on disk, dispatch `/fizzleenchanting reload` → observe in-memory config reflects the disk change.
    - [ ] Reload failure (malformed JSON) → command replies with the error key, logs throwable, config left at prior state.
  - **Dependencies:** TEST-0.2-T3.

---

# Epic 2 — Stat System & Table

The Eterna/Quanta/Arcana/Rectification/Clues stack that replaces vanilla's single "power" value. If any of the stat-path code silently regresses, the *cost* and *selection* of every enchant roll is wrong in a way players can't see — there's no "shelves are broken" error, just a gradual drift from expected outcomes. High-risk because it's both the mechanical spine of the mod and entirely invisible at the UI level.

## Story S-2.1 — Stat data model

### Tier 1

- [x] **TEST-2.1-T1a** — `EnchantingStats` record value semantics + codec round-trip via DFU only.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] Full JSON → record → JSON preserves every field.
    - [x] `{}` decodes to `ZERO`-equal record.
    - [x] Negative floats accepted; `clues` must be an integer.
    - [x] `ZERO` is reference-equal across calls.
  - **File:** `src/test/java/.../enchanting/EnchantingStatsTest.java`.

- [x] **TEST-2.1-T1b** — `EnchantingStatRegistry.lookup` precedence on a synthetic registry (direct > tag > `ENCHANTMENT_POWER_PROVIDER` > `ZERO`), with JSON-shape enforcement (either `block` or `tag`, never both).
  - **Tier:** 1 (pseudo — uses `ResourceLocation`, `TagKey`, `BlockTags` as POJOs; no `BuiltInRegistries` access).
  - **State:** T1-pseudo.
  - **Acceptance:**
    - [x] Direct-block reg beats tag reg.
    - [x] Tag reg beats Java fallback.
    - [x] `ENCHANTMENT_POWER_PROVIDER` fallback fires on bare blocks.
    - [x] Absent everything → `ZERO`.
    - [x] JSON with both `block` and `tag` fails parse with a clear message.
    - [x] Loading `vanilla_provider.json` only → vanilla bookshelf returns `maxEterna:15, eterna:1`.
  - **File:** `src/test/java/.../enchanting/EnchantingStatRegistryTest.java`.

### Tier 2

- [ ] **TEST-2.1-T2** — Stat JSON files on the classpath parse via `EnchantingStats.CODEC` under a bootstrapped registry (guards against a future codec change silently corrupting existing files).
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] Every file under `src/main/resources/data/fizzle_enchanting/enchanting_stats/*.json` parses.
    - [ ] Any `tag:` references resolve to a loadable `TagKey<Block>` (format-check only at Tier 2; real membership is Tier 3).

### Tier 3

- [ ] **TEST-2.1-T3** — Datapack reload under a real server populates `EnchantingStatRegistry` with every shipped stat JSON.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] On gametest startup, `EnchantingStatRegistry.lookup(vanilla bookshelf state)` returns `maxEterna:15, eterna:1`.
    - [ ] Every registered shelf block has a non-ZERO lookup result.
  - **Dependencies:** TEST-0.2-T3.

## Story S-2.2 — Shelf scan & aggregation

Silent-break model: LOS inversion (transmitter check passes-through through walls), offset-list drift on 1.21.1 bumps, clues-clamp regression.

### Tier 1

- [x] **TEST-2.2-T1** — Pure aggregation (sum across contributors, max of maxEterna, `clues` clamp, eterna clamp against maxEterna).
  - **Tier:** 1 (pseudo — uses `BlockPos` as a POJO).
  - **State:** T1-pseudo.
  - **Acceptance:**
    - [x] 15 contributors each `(1,1,0,0,0,0)` → eterna sums to 15 pre-clamp.
    - [x] A shelf with `clues:5` alone clamps to 3.
    - [x] Mixed maxEterna → result uses the highest seen.
    - [x] Blocked midpoint (stone) → that offset's contribution is zero; unblock restores it.
  - **File:** `src/test/java/.../enchanting/EnchantingStatRegistryGatherTest.java`.

### Tier 2

- [ ] **TEST-2.2-T2** — Scan driven from a synthetic `BlockGetter` uses vanilla `EnchantmentTableBlock.BOOKSHELF_OFFSETS` (guards against the offset list silently changing on a vanilla bump).
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] After bootstrap, iterate `EnchantmentTableBlock.BOOKSHELF_OFFSETS` directly and assert size + every offset's LOS-midpoint.
    - [ ] Scan covers every offset once (no duplicates, no missing).

### Tier 3

- [ ] **TEST-2.2-T3a** — Place vanilla bookshelves around a real enchanting table; Eterna sums to expected.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Template seeds an enchanting table + 15 bookshelves.
    - [ ] Open table → `StatCollection.eterna == 15`.
    - [ ] Block a single offset with stone → Eterna drops by 1.
  - **Dependencies:** TEST-0.2-T3.

- [ ] **TEST-2.2-T3b** — Filtering/treasure BE hooks invoked exactly once per in-range BE under real-level scan.
  - **Tier:** 3 — replaces the legacy unit test `FilteringTreasureIntegrationTest` for the register side; Tier 2 fixture-style coverage is redundant once Tier 3 runs.
  - **State:** T3-rewrite (replaces `FilteringTreasureIntegrationTest`).
  - **Acceptance:**
    - [ ] Two filtering shelves with unique books → blacklist size 2 at the table.
    - [ ] Treasure shelf present → `treasureAllowed == true`.
    - [ ] Removing the treasure shelf → `treasureAllowed == false`.
  - **Dependencies:** TEST-3.5-T3 (shares BE fixtures).

## Story S-2.3 — S2C network payloads

### Tier 2

- [x] **TEST-2.3-T2a** — `StatsPayload` + `CraftingResultEntry` round-trip over `RegistryFriendlyByteBuf`.
  - **Tier:** 2.
  - **State:** T2.
  - **Acceptance:**
    - [x] All-zero / mid-load / saturated variants survive round-trip byte-identical.
    - [x] `craftingResult` Optional preserved in present and empty shapes.
    - [x] `blacklist` cardinality matches post-round-trip.
  - **File:** `src/test/java/.../net/PayloadCodecTest.java`.

- [x] **TEST-2.3-T2b** — `CluesPayload` round-trip including `exhaustedList` flag.
  - **Tier:** 2.
  - **State:** T2.
  - **Acceptance:**
    - [x] Empty clue list round-trips.
    - [x] 3-entry list round-trips.
    - [x] `exhaustedList=true` round-trips.
  - **File:** same as above.

- [x] **TEST-2.3-T2c** — Registered in the S2C registry.
  - **Tier:** 2.
  - **State:** T2.
  - **Acceptance:**
    - [x] After manual `registerPayloads()` call, both `TYPE` lookups succeed.
  - **File:** `src/test/java/.../net/NetworkingRegistryTest.java`.

## Story S-2.4 — Enchantment selection algorithm

Highest-risk zone in the mod. Monotonicity bugs and weight-drift are silent and player-visible only as "bad luck."

### Tier 1

- [ ] **TEST-2.4-T1** — Pure-math slice of `getEnchantmentCost` monotonicity extracted from the current T2 test.
  - **Tier:** 1.
  - **State:** T1-new (splits a deterministic fraction out of `RealEnchantmentHelperTest`).
  - **Acceptance:**
    - [ ] Seeded RNG → byte-identical cost outputs across runs (no `Items` lookup).
    - [ ] `cost(slot=2) >= cost(slot=1) >= cost(slot=0)` over 1000 seeded rolls using a pure `RandomSource.create(seed)`.

### Tier 2

- [x] **TEST-2.4-T2a** — `getEnchantmentCost` end-to-end against a real `ItemStack`.
  - **Tier:** 2.
  - **State:** T2.
  - **Acceptance:**
    - [x] Seeded RNG reproducible at `eterna=50` → slot-2 cost ∈ `[25, 50]`.
    - [x] Honors `config.enchantingTable.maxEterna` clamp.
  - **File:** `src/test/java/.../enchanting/RealEnchantmentHelperTest.java`.

- [x] **TEST-2.4-T2b** — `selectEnchantment` blacklist + treasure gate + quanta stdev + rectification.
  - **Tier:** 2.
  - **State:** T2.
  - **Acceptance:**
    - [x] 1000 rolls: blacklisted key never appears.
    - [x] `treasureAllowed=false` → zero treasure-tagged enchants.
    - [x] `treasureAllowed=true` → treasure-tagged enchants appear.
    - [x] Stdev at `quanta=50` > stdev at `quanta=10`.
    - [x] `rectification=∞` → outcomes monotonic with eterna.
  - **File:** `src/test/java/.../enchanting/SelectEnchantmentTest.java`.

- [x] **TEST-2.4-T2c** — `buildClueList` first-clue == selected enchant under same seed; exhausted flag correct.
  - **Tier:** 2.
  - **State:** T2.
  - **Acceptance:**
    - [x] Over 100 seeds, first clue equals the rolled selection.
    - [x] Pool smaller than `cluesCount` → `exhaustedList == true`.
  - **File:** `src/test/java/.../enchanting/BuildClueListTest.java`.

### Tier 3

- [ ] **TEST-2.4-T3** — End-to-end: player opens a real table with seeded RNG → enchants applied equal the slot's advertised clue.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Spawn player, seed table's RNG deterministically, open menu, click slot 0 → applied enchant matches the clue payload's first entry.
  - **Dependencies:** TEST-2.5-T3, TEST-0.2-T3.

## Story S-2.5 — Menu + screen replacement

Three integration surfaces: mixin swap, menu subclass, screen reader.

### Tier 1

- [x] **TEST-2.5-T1** — Stat-line formatter in display order with exact spacing.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] `(50, 12, 5, 10, 2)` → `"E: 50  Q: 12  A: 5  R: 10  C: 2"`.
    - [x] Fractional eterna floored/rounded per DESIGN.
    - [x] `showLevelIndicator=false` → empty string.
  - **File:** `src/test/java/.../enchanting/StatLineFormatterTest.java`.

- [x] **TEST-2.5-T1b** — Mixin injection target is correctly declared (ASM bytecode introspection — no Bootstrap needed).
  - **Tier:** 1 (pseudo — ASM `ClassReader` on the compiled mixin).
  - **State:** T1-pseudo.
  - **Acceptance:**
    - [x] `EnchantmentTableBlockMixin#getMenuProvider` carries `@Inject(method="getMenuProvider", at=@At("HEAD"), cancellable=true)`.
    - [x] The `CallbackInfoReturnable<MenuProvider>` is the declared return type.
  - **File:** `src/test/java/.../mixin/EnchantmentTableBlockMixinTest.java`.

- [x] **TEST-2.5-T1c** — `EnchantmentMenuAccessor` has the three expected `@Accessor` methods with `fizzleEnchanting$` prefix.
  - **Tier:** 1 (pseudo — reflection on the accessor interface).
  - **State:** T1-pseudo.
  - **Acceptance:**
    - [x] Methods for `enchantSlots`, `random`, `enchantmentSeed` declared.
    - [x] Prefix present.
  - **File:** `src/test/java/.../mixin/EnchantmentMenuAccessorTest.java`.

### Tier 2

- [x] **TEST-2.5-T2** — Menu logic helper: XP/lapis validation, click success/failure paths, id=3 throw pre-Epic 5.
  - **Tier:** 2.
  - **State:** T2-migrate-clean.
  - **Acceptance:**
    - [x] Successful enchant → `ItemEnchantments` grows, XP + lapis decremented.
    - [x] Lapis missing → rejected, no mutation.
    - [x] XP insufficient → rejected, no mutation.
    - [x] Pre-Epic-5 id=3 throws `UnsupportedOperationException`; post-Epic-5 path lives in TEST-5.3-*.
  - **File:** `src/test/java/.../enchanting/FizzleEnchantmentLogicTest.java`.

### Tier 3

- [ ] **TEST-2.5-T3** — End-to-end menu flow: place table, open menu, click slot → enchant applied on a real `ItemStack` in a real inventory.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Template: enchanting table + 15 bookshelves.
    - [ ] Mock `ServerPlayer` teleported adjacent, `useBlock` → `player.containerMenu instanceof FizzleEnchantmentMenu`.
    - [ ] `clickMenuButton(player, 0)` applies an enchant and decrements XP + lapis.
    - [ ] Menu-type + screen registration implicitly verified (menu opens).
  - **Dependencies:** TEST-0.2-T3.

---

# Epic 3 — Shelf Family

25 Zenith shelves + utility shelves + BE-backed specials. Silent regressions: namespace-miss on stat JSON (zenith: survives), particle theme wrong `ParticleType`, filtering-shelf slot-targeting mis-targets.

## Story S-3.1 — Shelf infrastructure

### Tier 1

- [ ] **TEST-3.1-T1a** — Particle-theme enum coverage (parameterized) — one row per enum value so adding a new theme forces a test extension.
  - **Tier:** 1.
  - **State:** T1-new (cleanup — current `ParticleThemeTest` is Tier 2 after migration but doesn't strictly need MC beyond the enum itself).
  - **Acceptance:**
    - [ ] Parameterized over all enum values; each maps to the declared `ParticleType` constant.
    - [ ] Accepts new enum additions with a failing test row (no silent skip).

### Tier 2

- [x] **TEST-3.1-T2a** — Base `EnchantingShelfBlock` delegates `getStats` to the registry; theme preserved.
  - **Tier:** 2.
  - **State:** T2-migrate-unfreeze — drop the synthetic block-registry reflection; construct `EnchantingShelfBlock` locally (no registry side-effects) and assert delegation against a spy `EnchantingStatRegistry`.
  - **Acceptance:**
    - [x] Construction with `ParticleTheme.ENCHANT_SCULK` → `theme()` returns same enum.
    - [x] `getStats` delegates to `EnchantingStatRegistry#lookup` (observed via spy).
  - **File:** `src/test/java/.../shelf/EnchantingShelfBlockTest.java`.

- [x] **TEST-3.1-T2b** — Particle theme → `ParticleType` mapping with bootstrap.
  - **Tier:** 2.
  - **State:** T2.
  - **Acceptance:**
    - [x] Each enum value resolves to the declared `ParticleType` constant (`ParticleTypes.ENCHANT`, etc.).
  - **File:** `src/test/java/.../shelf/ParticleThemeTest.java`.

### Tier 3

- [x] **TEST-3.1-T3** — Registry helpers: every id registered once, ids resolve in `BuiltInRegistries.BLOCK` + `.ITEM`, idempotent across `register()` invocations.
  - **Tier:** 3.
  - **State:** T3.
  - **Acceptance:**
    - [x] Under `runGametest`, every shelf id resolves.
    - [x] Double-invocation of the register helper does not duplicate entries (assert registry size pre vs. post).
  - **File:** `src/gametest/java/.../gametest/RegistryGameTest.java`.
  - **Dependencies:** TEST-0.2-T3.

## Story S-3.2 — Full Zenith shelf roster

### Tier 1

- [x] **TEST-3.2-T1a** — Texture bundle present; animated `.mcmeta` preserved; forbidden `reforging/` / `augmenting/` dirs absent.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] Glob `assets/fizzle_enchanting/textures/block/*.png` contains every expected shelf.
    - [x] `blazing_hellshelf.png.mcmeta` exists.
    - [x] No files under `reforging/` or `augmenting/`.
  - **File:** `src/test/java/.../shelf/ShelfTextureBundleTest.java`.

- [x] **TEST-3.2-T1b** — `block.fizzle_enchanting.<id>` lang key per shelf.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] `en_us.json` parses.
    - [x] One non-empty key per id in the shelf roster.
    - [x] No dangling keys for cut shelves.
  - **File:** `src/test/java/.../shelf/ShelfLangKeysTest.java`.

- [x] **TEST-3.2-T1c** — Every ported stat JSON free of `zenith:` literal, namespaced to `fizzle_enchanting:`.
  - **Tier:** 1 (pseudo — filesystem + JSON parse; uses `ResourceLocation` as a POJO).
  - **State:** T1-pseudo.
  - **Acceptance:**
    - [x] No file contains `"zenith:"`.
    - [x] Every `ResourceLocation`-shaped string resolves.
  - **File:** `src/test/java/.../enchanting/PortedEnchantingStatsTest.java`.

### Tier 2

- [ ] **TEST-3.2-T2** — Every ported stat JSON parses via `EnchantingStats.CODEC` post-bootstrap.
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] `listResources("enchanting_stats")` returns the expected file count.
    - [ ] Each parses without warning.
  - **Dependencies:** TEST-2.1-T2 (shares the codec harness).

### Tier 3

- [ ] **TEST-3.2-T3** — Every expected shelf id registered; sound group + strength match DESIGN.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `FizzleShelvesTest`).
  - **Acceptance:**
    - [ ] Parameterized: each id → `BuiltInRegistries.BLOCK.get(id) != null`.
    - [ ] `SoundType` matches DESIGN (WOOD vs STONE).
    - [ ] `destroySpeed` / `explosionResistance` match DESIGN.
  - **Dependencies:** TEST-0.2-T3.

## Story S-3.3 — Utility shelves

### Tier 1

- [x] **TEST-3.3-T1a** — Sightshelf stat JSONs: `maxEterna: 0`, correct clue count.
  - **Tier:** 1 (pseudo — JSON parse via DFU only).
  - **State:** T1-pseudo.
  - **Acceptance:**
    - [x] `sightshelf.json` → `clues: 1, maxEterna: 0`.
    - [x] `sightshelf_t2.json` → `clues: 2, maxEterna: 0`.
    - [x] Stacked unclamped result before scan clamp == 4.
  - **File:** `src/test/java/.../enchanting/SightshelfStatsTest.java`.

- [x] **TEST-3.3-T1b** — Rectifier tiers map to `10/15/20`.
  - **Tier:** 1 (pseudo).
  - **State:** T1-pseudo.
  - **Acceptance:**
    - [x] `rectifier → 10`, `rectifier_t2 → 15`, `rectifier_t3 → 20`.
  - **File:** `src/test/java/.../enchanting/RectifierStatsTest.java`.

### Tier 3

- [ ] **TEST-3.3-T3** — Stacking in real level respects clue clamp (3) and unbounded rectification.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Template: two `sightshelf_t2` around a table → observed `clues == 3` (clamped from 4).
    - [ ] `rectifier_t3` in range → `rectification == 20` via the live stat payload.
  - **Dependencies:** TEST-2.2-T3a.

## Story S-3.4 — Datagen providers

Datagen tests have historically been hard to isolate — they depend on mod content being registered. The pragmatic answer: move correctness to `./gradlew runDatagen` + `git diff --exit-code src/main/generated/` rather than unit tests. The existing T3-rewrite candidates should be **deleted**, not rewritten, with the CI check replacing them.

### Tier 1

- [ ] **TEST-3.4-T1a** — Post-`runDatagen` filesystem sweep: every expected blockstate/model/loot-table/recipe file present.
  - **Tier:** 1.
  - **State:** T1-new (replaces `FizzleModelProviderTest`, `FizzleBlockLootTableProviderTest`, `FizzleRecipeProviderTest` — delete all three).
  - **Acceptance:**
    - [ ] For each shelf id, `src/main/generated/assets/fizzle_enchanting/blockstates/<id>.json` exists.
    - [ ] For each shelf/library/filtering-shelf/treasure-shelf id, `src/main/generated/data/fizzle_enchanting/loot_table/blocks/<id>.json` exists and has one `minecraft:alternatives` + `minecraft:item` pool.
    - [ ] For each shelf id, a shaped recipe exists under `src/main/generated/data/fizzle_enchanting/recipe/` (or its advancement sibling).
    - [ ] No custom `enchanting` / `keep_nbt_enchanting` recipe files under `src/main/generated/` (must stay hand-shipped).

- [ ] **TEST-3.4-T1b** — `runDatagen` is idempotent.
  - **Tier:** 1 (shell-invocation harness).
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] Run datagen twice → `git diff --exit-code src/main/generated/` clean.
    - [ ] Wire into CI as a non-blocking check initially; promote to blocking once stable.

## Story S-3.5 — Filtering & treasure shelves

### Tier 1

- [ ] **TEST-3.5-T1** — Slot-targeting math (cursor hit → slot index).
  - **Tier:** 1.
  - **State:** T1-new (extract from the legacy `FilteringShelfTest` — pure coordinate math with no registry access).
  - **Acceptance:**
    - [ ] For each of the four corners + center, hit coord maps to the expected 0–5 slot index.
    - [ ] Off-face hit → `-1` (no slot).

### Tier 2

- [ ] **TEST-3.5-T2** — BE NBT round-trip using real vanilla enchantment keys (no synthetic registry).
  - **Tier:** 2.
  - **State:** T2-new (splits NBT math out of the register-heavy legacy test).
  - **Acceptance:**
    - [ ] Insert an enchanted book carrying real `Enchantments.SHARPNESS` + `Enchantments.MENDING` → blacklist set contains both keys.
    - [ ] `saveAdditional` + `load(CompoundTag, RegistryAccess)` round-trip preserves the set.
    - [ ] Unknown enchant key in loaded NBT → dropped with warn, remainder intact.

### Tier 3

- [ ] **TEST-3.5-T3a** — `FilteringShelfBlockEntity` end-to-end: place, insert, verify blacklist flows into adjacent table.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `FilteringShelfTest`).
  - **Acceptance:**
    - [ ] Place filtering shelf, open via `useBlock`, insert book → BE state matches.
    - [ ] Open adjacent table → blacklist propagates to `StatsPayload`.
    - [ ] Extract book → blacklist shrinks.
    - [ ] Full shelf rejects additional inserts.
    - [ ] Empty shelf contributes as a wood-tier base shelf.
  - **Dependencies:** TEST-0.2-T3.

- [ ] **TEST-3.5-T3b** — `TreasureShelfBlockEntity` presence flips `treasureAllowed`.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `TreasureShelfTest`).
  - **Acceptance:**
    - [ ] Treasure shelf in range → `StatCollection.treasureAllowed == true` at the table.
    - [ ] Remove shelf → `false`.
    - [ ] Zero Eterna contribution.
  - **Dependencies:** TEST-3.5-T3a (shares template).

---

# Epic 4 — Anvil & Library

Prismatic Web, iron-block anvil repair, two library tiers with hopper I/O, custom recipe types. The library has the largest persisted state of any block in the mod — NBT-format drift here silently loses enchantment points that players spent hours banking.

## Story S-4.1 — Anvil dispatcher

### Tier 1

- [x] **TEST-4.1-T1a** — Dispatcher iterates handlers in order; first non-empty wins.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] Two stubs: first non-empty → second never consulted.
    - [x] First empty → second consulted.
    - [x] Handler order preserved.
  - **File:** `src/test/java/.../anvil/AnvilDispatcherTest.java`.

- [x] **TEST-4.1-T1b** — `AnvilMenuMixin` injection target declared correctly (bytecode introspection).
  - **Tier:** 1 (pseudo).
  - **State:** T1-pseudo.
  - **Acceptance:**
    - [x] `@Inject(method="createResult", at=@At("TAIL"))` present.
    - [x] Target method signature matches vanilla 1.21.1.
  - **File:** `src/test/java/.../mixin/AnvilMenuMixinTest.java`.

- [x] **TEST-4.1-T1c** — `AnvilMenuAccessor` has the expected `@Accessor` methods.
  - **Tier:** 1 (pseudo).
  - **State:** T1-pseudo.
  - **Acceptance:**
    - [x] `cost` / `repairItemCountCost` accessors declared.
  - **File:** `src/test/java/.../mixin/AnvilMenuAccessorTest.java`.

### Tier 2

- [ ] **TEST-4.1-T2** — `PrismaticWebHandler` strips curses against real vanilla enchantments (no mod registration).
  - **Tier:** 2.
  - **State:** T2-new (splits handler logic from register-heavy legacy).
  - **Acceptance:**
    - [ ] Curse-of-Vanishing + Sharpness-3 left item → handler returns output with Sharpness-3 only.
    - [ ] Non-cursed input → handler declines.
    - [ ] XP cost == `config.anvil.prismaticWebLevelCost`.
    - [ ] `config.anvil.prismaticWebRemovesCurses=false` → declines.

### Tier 3

- [ ] **TEST-4.1-T3** — End-to-end: player at anvil with cursed sword + prismatic web → output slot holds uncursed sword, web consumed on take.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `PrismaticWebItemTest` + `PrismaticWebHandlerTest`).
  - **Acceptance:**
    - [ ] Place anvil, teleport mock player, seed slots, `useBlock` opens `AnvilMenu`.
    - [ ] Menu output slot contains uncursed sword.
    - [ ] Take output → web stack decremented by 1.
  - **Dependencies:** TEST-0.2-T3.

## Story S-4.2 — Iron-block anvil repair

### Tier 2

- [x] **TEST-4.2-T2** — Tier ladder, iron-ingot rejection, enchantment preservation, config gate.
  - **Tier:** 2.
  - **State:** T2.
  - **Acceptance:**
    - [x] Damaged → chipped; chipped → normal.
    - [x] Normal → declines.
    - [x] Iron ingot (wrong material) → declines.
    - [x] Anvil with `ItemEnchantments` → preserved on upgrade.
    - [x] `ironBlockRepairsAnvil=false` → declines.
    - [x] XP cost flat 1 level; consumes 1 iron block.
  - **File:** `src/test/java/.../anvil/IronBlockAnvilRepairTest.java`.

### Tier 3

- [ ] **TEST-4.2-T3** — Full anvil click flow upgrades damaged anvil to chipped in a real level.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Template seeds damaged-anvil block + iron-block ItemStack in slot 1.
    - [ ] Click output → damaged-anvil BlockItem in player inv → place → chipped-anvil block in world.
  - **Dependencies:** TEST-0.2-T3.

## Story S-4.3 — Library storage engine

### Tier 1

- [x] **TEST-4.3-T1** — Point math: `points(level) = 2^(level-1)`, `maxLevelAffordable`.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] `points(1)=1, points(5)=16, points(16)=32_768, points(31)=1_073_741_824`.
    - [x] `points(0)=0` per DESIGN fallback.
    - [x] `maxLevelAffordable` matches DESIGN formula parameterized.
  - **File:** `src/test/java/.../library/PointMathTest.java`.

### Tier 2

- [x] **TEST-4.3-T2a** — Abstract `EnchantmentLibraryBlockEntity` deposit/extract/canExtract using real vanilla enchants.
  - **Tier:** 2.
  - **State:** T2-migrate-unfreeze — replace the synthetic-enchant setup with `Enchantments.SHARPNESS` / `Enchantments.MENDING` lookups post-bootstrap.
  - **Acceptance:**
    - [x] Deposit single-enchant book → `points` + `maxLevels` updated.
    - [x] `canExtract` rejects when `maxLevels[key] < target`.
    - [x] `canExtract` rejects when insufficient points.
    - [x] `extract` mutates state only when `canExtract` passes.
  - **File:** `src/test/java/.../library/EnchantmentLibraryBlockEntityTest.java`.

- [x] **TEST-4.3-T2b** — Basic + Ender constants.
  - **Tier:** 2.
  - **State:** T2-migrate-unfreeze.
  - **Acceptance:**
    - [x] Basic `maxLevel=16, maxPoints=32_768`.
    - [x] Ender `maxLevel=31, maxPoints=1_073_741_824`.
    - [x] No config knob exposes these.
  - **File:** `src/test/java/.../library/LibraryTierBlockEntityTest.java`.

- [x] **TEST-4.3-T2c** — NBT round-trip using real vanilla enchant keys; unknown keys dropped.
  - **Tier:** 2.
  - **State:** T2-migrate-unfreeze.
  - **Acceptance:**
    - [x] Save/load preserves both maps.
    - [x] Injected unknown id → dropped with warn, remainder intact.
    - [x] No schema version field (guard).
  - **File:** `src/test/java/.../library/LibraryNbtTest.java`.

- [x] **TEST-4.3-T2d** — Client sync packet reconstructs server state.
  - **Tier:** 2.
  - **State:** T2-migrate-unfreeze.
  - **Acceptance:**
    - [x] Mutate server BE → `getUpdateTag` includes mutation.
    - [x] Client BE from tag equals server BE by map contents.
    - [x] 3 sequential mutations → 3 full resends.
  - **File:** `src/test/java/.../library/LibraryClientSyncTest.java`.

### Tier 3

- [ ] **TEST-4.3-T3** — Real library BE in world: deposit → server restart → state preserved.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Place basic library, deposit via hopper, assert `points` mutated.
    - [ ] Save chunk, reload, re-assert state.
  - **Dependencies:** TEST-0.2-T3.

## Story S-4.4 — Library block + UI

### Tier 1

- [x] **TEST-4.4-T1** — Library row formatter pure string output.
  - **Tier:** 1.
  - **State:** T1-pure (currently Tier 2 via clean Bootstrap but the formatter is actually pure — migrate down).
  - **Acceptance:**
    - [x] `{Sharpness, maxLevels=5, points=6144}` → expected string.
    - [x] Long names truncated per DESIGN.
  - **File:** `src/test/java/.../library/LibraryRowFormatterTest.java`.

### Tier 3

- [ ] **TEST-4.4-T3a** — Both library blocks register; basic recipe resolves.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `EnchantmentLibraryBlockTest`).
  - **Acceptance:**
    - [ ] Basic + Ender library blocks in `BuiltInRegistries.BLOCK`.
    - [ ] `library.json` recipe parses and is craftable in gametest.
  - **Dependencies:** TEST-0.2-T3.

- [ ] **TEST-4.4-T3b** — Menu deposit/extract/shift-click at a real library block.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `EnchantmentLibraryMenuTest`).
  - **Acceptance:**
    - [ ] Book in slot 0 → absorbed on `setChanged`; slot cleared.
    - [ ] Extract at `maxLevels=1` with sufficient points → denied.
    - [ ] Shift-click uses `maxLevelAffordable` formula.
    - [ ] Listener de-registration on close (leak check: 1000 open/close cycles → empty listener set).

## Story S-4.5 — Hopper integration

### Tier 2

- [x] **TEST-4.5-T2** — `Storage<ItemVariant>` adapter + `SnapshotParticipant` abort/commit; rate limit.
  - **Tier:** 2.
  - **State:** T2-migrate-unfreeze.
  - **Acceptance:**
    - [x] Diamond sword → 0 accepted; enchanted book at cap → returns full amount (void overflow); extract → 0.
    - [x] Begin → insert → abort → state unchanged.
    - [x] Begin → insert → commit → state mutated; `setChanged` exactly once.
    - [x] Two rapid inserts under `ioRateLimitTicks` → second dropped.
  - **File:** `src/test/java/.../library/LibraryStorageTest.java`.

### Tier 3

- [ ] **TEST-4.5-T3** — Real hopper adjacent to library BE actually transfers books.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Template: hopper above library with 1 enchanted book in hopper inventory.
    - [ ] Tick 2 seconds → book transferred, library `points` incremented.
    - [ ] Non-book in hopper → stays in hopper.
  - **Dependencies:** TEST-4.3-T3.

## Story S-4.6 — Custom recipe types

### Tier 1

- [x] **TEST-4.6-T1** — `StatRequirements` record semantics.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] `(e,q,a)` equality; `-1` as no-max sentinel.
    - [x] `matches(stats)` boundary cases.
  - **File:** `src/test/java/.../enchanting/recipe/StatRequirementsTest.java`.

### Tier 2

- [x] **TEST-4.6-T2a** — `EnchantingRecipe` codec round-trip + `matches(input, stats)`.
  - **Tier:** 2.
  - **State:** T2-migrate-unfreeze — swap synthetic enchant registry for vanilla enchants where the codec needs to resolve a `HolderLookup`.
  - **Acceptance:**
    - [x] MAP_CODEC round-trips.
    - [x] `matches` true when stats ∈ `[requirements, maxRequirements]`.
    - [x] `RECIPE_TYPE` registered in `BuiltInRegistries.RECIPE_TYPE`.
  - **File:** `src/test/java/.../enchanting/recipe/EnchantingRecipeTest.java`.

- [x] **TEST-4.6-T2b** — `KeepNbtEnchantingRecipe.assemble` preserves `ItemEnchantments`.
  - **Tier:** 2.
  - **State:** T2-migrate-unfreeze.
  - **Acceptance:**
    - [x] Input with Sharpness-5 → result carries Sharpness-5.
    - [x] Codec round-trip for the new type.
  - **File:** same as above.

- [x] **TEST-4.6-T2c** — `EnchantingRecipeRegistry.findMatch` over both subtypes.
  - **Tier:** 2.
  - **State:** T2-migrate-unfreeze.
  - **Acceptance:**
    - [x] Varying `(requirements, maxRequirements)` → hit/miss as expected.
    - [x] Mixed subtype recipe set → correct subtype picked.
  - **File:** `src/test/java/.../enchanting/recipe/EnchantingRecipeRegistryTest.java`.

- [x] **TEST-4.6-T2d** — All 7 shipped recipe JSONs parse into the declared subtype + bounds match Zenith.
  - **Tier:** 2.
  - **State:** T2-migrate-unfreeze.
  - **Acceptance:**
    - [x] Each file loads via `RecipeManager`.
    - [x] 6× `enchanting`, 1× `keep_nbt_enchanting`.
    - [x] `(e,q,a)` tuples match DESIGN.
  - **File:** same as above.

### Tier 3

- [ ] **TEST-4.6-T3** — Datapack reload populates `RecipeManager` under running server; `findMatch` returns correctly on real `ServerLevel`.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] On gametest boot, all 7 shipped recipes load.
    - [ ] `EnchantingRecipeRegistry.findMatch(level, <library book>, matchingStats)` returns the ender-library keep-nbt recipe.
  - **Dependencies:** TEST-0.2-T3.

---

# Epic 5 — Tomes & Table Crafting UX

Tomes move player-persistent XP value between items; a broken handler either destroys items it shouldn't (silent) or double-counts enchantments (loud). The crafting-result row wires the table menu to a whole second recipe type.

## Story S-5.1 — Tome items

### Tier 1

- [x] **TEST-5.1-T1a** — Tome asset + lang + model JSON presence; no typed-tome textures.
  - **Tier:** 1.
  - **State:** T1-pure.
  - **Acceptance:**
    - [x] 3 tome PNGs under `item/tome/`.
    - [x] 3 lang keys present.
    - [x] Each item model has the right `layer0` texture path.
    - [x] None of the 9 cut typed-tome textures present.
  - **File:** `src/test/java/.../tome/TomeAssetsTest.java`.

- [x] **TEST-5.1-T1b** — Scrap tome vanilla-shape recipe parses.
  - **Tier:** 1 (pseudo — GSON parse only, no registry resolution).
  - **State:** T1-pseudo.
  - **Acceptance:**
    - [x] `scrap_tome.json` parses to expected shape.
    - [x] Ingredient references documented in DESIGN present.
  - **File:** `src/test/java/.../tome/ScrapTomeRecipeTest.java`.

### Tier 2

- [ ] **TEST-5.1-T2** — Scrap tome recipe resolves via `RecipeManager` post-bootstrap (guards against JSON parsing past but recipe-type mismatching).
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] On bootstrap with recipe loader wired, `scrap_tome` resolves to a shaped recipe producing the tome item (tome `Item` instance is the only registration blocker; test skips the output check and asserts only the recipe-type + ingredient shape at Tier 2).

### Tier 3

- [ ] **TEST-5.1-T3** — All three tomes register; `stackSize=1`; no durability component; obtainable via shipped recipes.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `TomeItemsTest`).
  - **Acceptance:**
    - [ ] All three tome items in `BuiltInRegistries.ITEM`.
    - [ ] `getMaxStackSize() == 1`.
    - [ ] No durability component.
    - [ ] Craft scrap tome in gametest → resolves.
  - **Dependencies:** TEST-0.2-T3.

## Story S-5.2 — Tome anvil handlers

### Tier 1

- [ ] **TEST-5.2-T1** — Damage-clamp arithmetic for `ExtractionTomeHandler` (pure math over `int` durability values).
  - **Tier:** 1.
  - **State:** T1-new (extracts the clamp logic from the legacy handler tests).
  - **Acceptance:**
    - [ ] `damage(max=1000, curDmg=0, percent=0.2f)` → 200 damage applied.
    - [ ] `damage(max=2, curDmg=1, percent=0.5f)` → stops at `max-1` (durability ≥ 1 clamp).
    - [ ] Negative percent → rejected by upstream validation (config clamp covered by TEST-1.3-T1c).

### Tier 2

- [ ] **TEST-5.2-T2a** — `ScrapTomeHandler` seeded RNG deterministic single-enchant output using real vanilla enchants.
  - **Tier:** 2.
  - **State:** T2-new (splits handler math from the register-heavy legacy test).
  - **Acceptance:**
    - [ ] Seeded `RandomSource` → output enchant key reproducible.
    - [ ] Unenchanted left → handler declines.
    - [ ] XP cost == config value.

- [ ] **TEST-5.2-T2b** — `ImprovedScrapTomeHandler` copies all enchants.
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] 3-enchant input → 3-enchant output book.
    - [ ] Left item destroyed, tome consumed.

- [ ] **TEST-5.2-T2c** — `ExtractionTomeHandler` preserves item (damaged), outputs all-enchants book.
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] 3 enchants → output book has 3.
    - [ ] Left item survives unenchanted with damage == config percent.
    - [ ] Durability ≥ 1 clamp honored (ref TEST-5.2-T1).

### Tier 3

- [ ] **TEST-5.2-T3a** — `ScrapTomeHandler` end-to-end in anvil menu.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `ScrapTomeHandlerTest`).
  - **Acceptance:**
    - [ ] Place anvil, seed slots with enchanted sword + scrap tome, open menu, take output → book has one of the sword's enchants.
  - **Dependencies:** TEST-0.2-T3.

- [ ] **TEST-5.2-T3b** — `ImprovedScrapTomeHandler` end-to-end.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `ImprovedScrapTomeHandlerTest`).
  - **Acceptance:**
    - [ ] All enchants transferred.
  - **Dependencies:** TEST-5.2-T3a (shares template).

- [ ] **TEST-5.2-T3c** — `ExtractionTomeHandler` end-to-end with durability change.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `ExtractionTomeHandlerTest`).
  - **Acceptance:**
    - [ ] Sword returned unenchanted with new damage value.
  - **Dependencies:** TEST-5.2-T3a.

- [ ] **TEST-5.2-T3d** — Fuel-slot repair path: extraction tome in anvil material slot repairs left sword.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `ExtractionTomeFuelSlotRepairHandlerTest`).
  - **Acceptance:**
    - [ ] Damaged sword + tome in fuel slot (no right-hand item) → sword damage reduced by `repairPercent * maxDurability`.
    - [ ] Tome consumed.
    - [ ] Enchanted sword in this path → `ExtractionTomeHandler` claims it instead (handler ordering).
  - **Dependencies:** TEST-5.2-T3a.

## Story S-5.3 — Table crafting-result row

### Tier 1

- [x] **TEST-5.3-T1** — Client row formatter pure output.
  - **Tier:** 1.
  - **State:** T1-pure (currently T2-migrate-clean; formatter is pure — migrate down).
  - **Acceptance:**
    - [x] `(ItemStack(ender_library), 20, recipeId)` → `"Ender Library — 20 levels"`.
    - [x] Singular/plural handling per DESIGN.
  - **File:** `src/test/java/.../enchanting/CraftingRowFormatterTest.java`.

### Tier 2

- [x] **TEST-5.3-T2a** — `findMatch` hook into `slotsChanged` stores recipe on menu.
  - **Tier:** 2.
  - **State:** T2-migrate-unfreeze.
  - **Acceptance:**
    - [x] Scripted stats + library input → `currentRecipe.isPresent()`.
    - [x] Out-of-bounds stats → `currentRecipe.isEmpty()`.
  - **File:** `src/test/java/.../enchanting/TableCraftingLookupTest.java`.

- [x] **TEST-5.3-T2b** — `CraftingResultEntry` projection onto outgoing `StatsPayload`.
  - **Tier:** 2.
  - **State:** T2.
  - **Acceptance:**
    - [x] Recipe present → payload's `craftingResult` is `Optional.of(...)`.
    - [x] Absent → `Optional.empty()`.
    - [x] Round-trip byte-identical.
  - **File:** `src/test/java/.../enchanting/CraftingResultProjectionTest.java`.

- [x] **TEST-5.3-T2c** — Server handler for `buttonId=3`: recipe subtypes, XP validation, no-op on no recipe.
  - **Tier:** 2.
  - **State:** T2.
  - **Acceptance:**
    - [x] `keep_nbt_enchanting` preserves `ItemEnchantments`.
    - [x] `enchanting` decrements input + inserts result.
    - [x] Insufficient XP → no-op.
    - [x] No recipe matched → no-op.
  - **File:** `src/test/java/.../enchanting/CraftingResultFlowTest.java`.

### Tier 3

- [ ] **TEST-5.3-T3** — Click button id=3 on a real `FizzleEnchantmentMenu` → recipe output materializes in player inventory.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Place table + matching shelves, seed input slot, open menu → `craftingResult` payload present on client.
    - [ ] `clickMenuButton(player, 3)` → output in player inv, XP decremented, input consumed.
    - [ ] `keep_nbt_enchanting` path: input's `ItemEnchantments` preserved on output book.
  - **Dependencies:** TEST-2.5-T3.

## Story S-5.4 — Specialty materials

### Tier 1

- [ ] **TEST-5.4-T1** — `warden_tendril` + `infused_breath` lang key presence; `infused_breath.mcmeta` present for animation.
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] Lang keys `item.fizzle_enchanting.infused_breath` + `item.fizzle_enchanting.warden_tendril` present and non-empty.
    - [ ] `infused_breath.png.mcmeta` present.
    - [ ] Item models reference the right texture paths.

### Tier 3

- [ ] **TEST-5.4-T3a** — `InfusedBreathItem` registered; `enchanting:infused_breath` recipe resolves under real `RecipeManager`.
  - **Tier:** 3.
  - **State:** T3-rewrite (replaces `InfusedBreathItemTest`).
  - **Acceptance:**
    - [ ] Item registered in `BuiltInRegistries.ITEM`.
    - [ ] Recipe loadable and producing the item under gametest.
  - **Dependencies:** TEST-0.2-T3.

- [ ] **TEST-5.4-T3b** — `WardenTendrilItem` registered + assets present.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Item registered.
    - [ ] Texture + model + lang assets present.
  - **Dependencies:** TEST-5.4-T3a.

- [ ] **TEST-5.4-T3c** — `WardenLootHandler` modifies the warden loot table with two pools; drop-rate and looting-bonus semantics.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] After mod init, `entities/warden` loot table has exactly 2 pools referencing `warden_tendril`.
    - [ ] `dropChance=1.0, looting=0` → kill-simulation yields ≥100 tendrils over 100 kills.
    - [ ] `dropChance=0.0, looting=3` → observed count bounded in `[0, 200]` over 1000 kills.
    - [ ] `dropChance=0.0, looting=0` → zero tendrils.
  - **Dependencies:** TEST-5.4-T3b.

- [ ] **TEST-5.4-T3d** — `/fizzleenchanting reload` re-reads warden drop values at roll time.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Boot with `dropChance=0.0` → 0 tendrils over 100 kills.
    - [ ] Mutate config in-memory to `dropChance=1.0`, invoke reload → next 100 kills yield ≥100 tendrils.
    - [ ] Handler reads config at roll time (not cached at registration).
  - **Dependencies:** TEST-5.4-T3c, TEST-1.4-T3.

---

# Epic 6 — Enchantment Roster

51 enchantments — 49 NeoEnchant+ ports + 2 authored. Data-only. Parse-failure is loud at server boot; silent drift is tag membership, lang key, or wrong weight.

## Story S-6.1 — NeoEnchant+ port (49 files)

### Tier 1

- [ ] **TEST-6.1-T1a** — Pre-port NeoEnchant+ v5.14.0 manifest (56 JSONs at source).
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] Expected 56 `.json` files present under the extracted scratch dir.
    - [ ] Dir name matches DESIGN slot sub-structure.

- [ ] **TEST-6.1-T1b** — Post-port filesystem sweep: exactly 49 files, no `enchantplus:` literals, no cut-enchant filenames.
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] 49 `.json` under `data/fizzle_enchanting/enchantment/`.
    - [ ] `grep -r "enchantplus:"` returns nothing.
    - [ ] The 7 cut file names are absent.

- [ ] **TEST-6.1-T1c** — Lang file merge: one key per ported enchant, no leftover `enchantplus.*`, no cut-enchant keys.
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] `en_us.json` parses.
    - [ ] For each of 49 ported ids, matching key non-empty.
    - [ ] No key still namespaced to `enchantplus`.
    - [ ] Keys for 7 cut enchants absent.

### Tier 2

- [ ] **TEST-6.1-T2** — Every ported enchant JSON parses via `Enchantment.CODEC` post-bootstrap.
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] Each file parses without warnings.
    - [ ] `supported_items` tag references resolve (format only; Tier 3 verifies registry membership).

- [ ] **TEST-6.1-T2b** — `exclusive_set` tag entries resolve to present enchant keys.
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] Every entry in `data/fizzle_enchanting/tags/enchantment/exclusive_set/*.json` parses.
    - [ ] No entry references any of the 7 cut enchants.

### Tier 3

- [ ] **TEST-6.1-T3** — Post-boot, all 49 ids appear in `BuiltInRegistries.ENCHANTMENT`.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Parameterized over the 49 ids; each resolves via `registryAccess.registryOrThrow(Registries.ENCHANTMENT).get(id)`.
  - **Dependencies:** TEST-0.2-T3.

## Story S-6.2 — Authored enchants

### Tier 1

- [ ] **TEST-6.2-T1** — Weapon-tag expansion JSON shape + lang keys.
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] `data/minecraft/tags/item/enchantable/weapon.json` has `replace: false` and includes `minecraft:shield`.
    - [ ] Lang keys `enchantment.fizzle_enchanting.icy_thorns` + `shield_bash` present.

### Tier 2

- [ ] **TEST-6.2-T2a** — `icy_thorns.json` parses with chest-armor target + post_attack slowness.
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] Parses via `Enchantment.CODEC`.
    - [ ] `supported_items` resolves to `#minecraft:enchantable/chest_armor`.
    - [ ] `minecraft:post_attack` present with `affected: "attacker"`, `enchanted: "victim"`.
    - [ ] Applied effect is `minecraft:apply_mob_effect` with Slowness.

- [ ] **TEST-6.2-T2b** — `shield_bash.json` parses; weapon tag includes shield.
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] Parses.
    - [ ] After boot, `Items.SHIELD` satisfies `#minecraft:enchantable/weapon`.
    - [ ] Carries both `minecraft:damage` and `minecraft:post_attack → minecraft:damage_item`.

### Tier 3

- [ ] **TEST-6.2-T3** — Enchantment effect fires in-world: apply `icy_thorns` to chest armor, take damage → slowness applied to attacker.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Template seeds two entities; one wears enchanted chest armor.
    - [ ] Script attack → `MobEffects.MOVEMENT_SLOWDOWN` observed on attacker.
  - **Dependencies:** TEST-0.2-T3.

## Story S-6.3 — Foreign enchant overrides

### Tier 1

- [ ] **TEST-6.3-T1** — Override JSON files present at expected paths.
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] `data/minecraft/enchantment/mending.json` exists.
    - [ ] `data/yigd/enchantment/soulbound.json` exists.
    - [ ] Both parse as JSON.

### Tier 2

- [ ] **TEST-6.3-T2** — Override JSONs parse via `Enchantment.CODEC` (Soulbound may warn on missing yigd tag but not fail).
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] Mending override parses cleanly with raised weight.
    - [ ] Soulbound override parses when yigd is absent (documented tolerance for unresolved tag).

### Tier 3

- [ ] **TEST-6.3-T3a** — Mending override: post-boot registry has bundled weight; `#minecraft:treasure` membership preserved.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] `BuiltInRegistries.ENCHANTMENT.get(minecraft:mending).value().weight()` == bundled value.
    - [ ] `minecraft:mending` still in `#minecraft:treasure`.
    - [ ] Selection pool with `treasureAllowed=false` still excludes Mending.
  - **Dependencies:** TEST-0.2-T3.

- [ ] **TEST-6.3-T3b** — `applyBundledOverrides` flag toggles override vs upstream.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Boot with `true` → override present.
    - [ ] Boot with `false` → upstream restored (resource pack source approach).
  - **Dependencies:** TEST-6.3-T3a.

---

# Epic 7 — Integrations

Adapter plugins for EMI, REI, JEI, Jade. Failure mode is near-silent: adapter classes don't run if the host mod isn't installed, so unit coverage focuses on the shared `TableCraftingDisplay` extractor + classload smoke.

## Story S-7.1 — EMI adapter

### Tier 1

- [ ] **TEST-7.1-T1a** — Build matrix: gradle build succeeds with and without EMI in dev runtime.
  - **Tier:** 1 (shell).
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] `./gradlew build` green with `modRuntimeOnly dev.emi:emi-fabric:...` active.
    - [ ] Green with EMI excluded.
    - [ ] `fabric.mod.json` entry `"emi": [...]` present.

- [ ] **TEST-7.1-T1b** — EMI plugin class resolves via `Class.forName` when EMI is on classpath.
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] Classload succeeds with EMI present.
    - [ ] Classload throws `ClassNotFoundException` with EMI absent (expected — entrypoint gated).

### Tier 2

- [ ] **TEST-7.1-T2** — Shared `TableCraftingDisplay` extractor returns expected entries from `RecipeManager`.
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] Output includes every shipped `enchanting` + `keep_nbt_enchanting` recipe.
    - [ ] Each display carries input, output, stat requirements, XP cost.
  - **Dependencies:** TEST-4.6-T2d.

### Tier 3

- [ ] **TEST-7.1-T3** — Manual EMI smoke (documented, not automated).
  - **Tier:** n/a.
  - **State:** manual.
  - **Acceptance:**
    - [ ] With EMI in dev runtime, categories "Shelves" and "Tomes" appear.
    - [ ] Each recipe renders input/output/requirements/XP correctly.

## Story S-7.2 — REI adapter

### Tier 1

- [ ] **TEST-7.2-T1** — Build matrix + classload smoke, same shape as S-7.1.
  - **Tier:** 1.
  - **State:** T1-new.

### Tier 2

- [ ] **TEST-7.2-T2** — REI plugin adapts shared `TableCraftingDisplay` without loss.
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] Adapter preserves every field.
    - [ ] Extractor is shared with EMI — covered once.
  - **Dependencies:** TEST-7.1-T2.

## Story S-7.3 — JEI adapter

### Tier 1

- [ ] **TEST-7.3-T1** — Plugin classload smoke + `fabric.mod.json` entry.
  - **Tier:** 1.
  - **State:** T1-new.

### Tier 3

- [ ] **TEST-7.3-T3** — Manual JEI UI smoke.
  - **Tier:** n/a.
  - **State:** manual.

## Story S-7.4 — Jade probe tooltips

### Tier 1

- [ ] **TEST-7.4-T1** — Jade tooltip string builders.
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] Table probe string carries all 5 stats in `E/Q/A/R/C` order.
    - [ ] Library probe reads `"Basic Library — N enchants stored"`.
    - [ ] Per-enchant points not exposed outside library UI.

---

# Epic 8 — Polish & Release

Content finalization. Silent breaks are mostly about advancement triggers referencing cut enchants and tooltip recolor ignoring the config hex fallback.

## Story S-8.1 — Advancement tree

### Tier 1

- [ ] **TEST-8.1-T1** — Advancement lang-key sweep.
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] Each of the 10 advancement ids has both `advancement.fizzle_enchanting.<id>.title` and `.description`.

### Tier 2

- [ ] **TEST-8.1-T2** — All 10 advancement JSONs parse via `Advancement.CODEC` and their item/tag references resolve.
  - **Tier:** 2.
  - **State:** T2-new.
  - **Acceptance:**
    - [ ] All parse.
    - [ ] Every item/tag reference resolves post-bootstrap.
    - [ ] `apotheosis` advancement uses a valid trigger.

### Tier 3

- [ ] **TEST-8.1-T3** — `apotheosis` advancement trigger fires at Eterna 50 in a real level.
  - **Tier:** 3.
  - **State:** T3-new.
  - **Acceptance:**
    - [ ] Template: table + 15 bookshelves + utility shelves sufficient for Eterna 50.
    - [ ] Open menu → advancement earned on player.
  - **Dependencies:** TEST-2.5-T3.

## Story S-8.2 — Tooltips + overleveled coloring

### Tier 1

- [ ] **TEST-8.2-T1a** — Tooltip formatter: over-leveled enchants recolored; vanilla caps map populated; config fallback honored.
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] Sharpness 7 → recolored with `overLeveledColor`.
    - [ ] Sharpness 5 → vanilla color.
    - [ ] Invalid hex → fallback `#FF6600` (via TEST-1.3-T1c).
    - [ ] Cap map has one entry per shipped enchant.

- [ ] **TEST-8.2-T1b** — Book tooltip toggle suppresses per-level lines.
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] `showBookTooltips=true` → lines present.
    - [ ] `false` → empty list.

## Story S-8.3 — Operator docs

No test rows. Manual review.

## Story S-8.4 — Release prep

### Tier 1

- [ ] **TEST-8.4-T1a** — Full test + build sweep: `./gradlew runDatagen && ./gradlew clean build test runGametest` all green.
  - **Tier:** 1 (CI).
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] All four exit zero.
    - [ ] No new warnings beyond baseline.

- [ ] **TEST-8.4-T1b** — CHANGELOG + plugin-list lint.
  - **Tier:** 1.
  - **State:** T1-new.
  - **Acceptance:**
    - [ ] `companions/fizzle-enchanting/CHANGELOG.md` has `[0.1.0]` with epic highlights.
    - [ ] Root CHANGELOG `[Unreleased] → Added` line present.
    - [ ] `plugins/gameplay.md` `## Fizzle Enchanting` entry with every required field.

---

# Epic 9 — Post-MVP Iteration Backlog

Only tested when the iteration is scheduled. Placeholders so IDs don't collide.

## Story S-9.1 — Absorb BeyondEnchant

### Tier 2

- [ ] **TEST-9.1-T2** — 16 override JSONs parse with expected caps.
  - **Tier:** 2.
  - **State:** T2-new.

### Tier 3

- [ ] **TEST-9.1-T3** — Runtime cap lookup uses `config.levelCaps.perEnchantment` when `enabled=true`; Spectrum Paxi fix still applies.
  - **Tier:** 3.
  - **State:** T3-new.

## Story S-9.2 — Absorb Easy Magic (item persistence)

### Tier 3

- [ ] **TEST-9.2-T3** — Table BE persists slot contents across GUI close + logout/login + pre-existing vanilla table migration.
  - **Tier:** 3.
  - **State:** T3-new.

## Story S-9.3 — Easy Anvils `tooExpensiveCap`

### Tier 3

- [ ] **TEST-9.3-T3** — Cap values 40, -1, 0 behave per DESIGN (if story green-lit).
  - **Tier:** 3.
  - **State:** T3-new.

## Story S-9.4 — Per-iteration bookkeeping checklist

No test rows — process checklist.

---

# Ordering

Run the work in this sequence. Within a section, the skill's tier ordering (T1 → T2 → T3) applies; across sections, hot user-facing paths precede defensive edges.

## Phase 0 — Infrastructure (S-0)

**Blocking — nothing downstream can start until this lands.**

1. **TEST-0.1-T2** — Wire `fabric-loader-junit`, drop `forkEvery`, smoke test passes.
2. **TEST-0.2-T3** — Wire gametest source set, template, entrypoint; placeholder test passes.
3. **TEST-0.3** — CI runs both.

## Phase 1 — Tier 2 migrations of clean-Bootstrap tests

Cheapest wins — the tests already pass under the legacy harness, just move them onto the modern one. Expect diffs to be almost entirely deletions.

4. S-2.3 payload codecs (TEST-2.3-T2a/b/c).
5. S-2.4 selection (TEST-2.4-T2a/b/c).
6. S-4.2 iron-block repair (TEST-4.2-T2).
7. S-4.4 library row formatter **demoted to Tier 1** (TEST-4.4-T1).
8. S-5.3 crafting-row flow + projection (TEST-5.3-T2b/c).
9. S-3.1 particle theme (TEST-3.1-T2b).
10. S-2.5 menu logic helper (TEST-2.5-T2).

## Phase 2 — Tier 2 migrations of unfreeze-without-register tests

These need the bigger rewrite — swap the synthetic enchant registry for real vanilla enchant lookups per the skill's recipe. Library NBT + codec tests dominate.

11. S-4.3 library engine (TEST-4.3-T2a/b/c/d).
12. S-4.5 hopper storage (TEST-4.5-T2).
13. S-4.6 custom recipes (TEST-4.6-T2a/b/c/d).
14. S-5.3 crafting lookup (TEST-5.3-T2a).
15. S-3.1 shelf block (TEST-3.1-T2a).

## Phase 3 — Tier 3 rewrites for register-heavy legacy tests

Biggest cost. Each rewrite owns its SNBT template (or shares via deps), its gametest entrypoint wiring, and the usual teleport-and-`succeedWhen` orchestration.

16. S-3.1 registry helper (TEST-3.1-T3).
17. S-3.2 full shelf roster (TEST-3.2-T3).
18. S-3.5 filtering + treasure shelves (TEST-3.5-T3a/b).
19. S-2.2 shelf scan real-level (TEST-2.2-T3a/b).
20. S-2.5 menu end-to-end (TEST-2.5-T3).
21. S-4.1 prismatic web end-to-end (TEST-4.1-T3).
22. S-4.4 library block + menu (TEST-4.4-T3a/b).
23. S-5.1 tome items register (TEST-5.1-T3).
24. S-5.2 tome anvil handlers (TEST-5.2-T3a/b/c/d).
25. S-5.3 crafting button id=3 (TEST-5.3-T3).
26. S-5.4 specialty materials (TEST-5.4-T3a/b/c/d).

## Phase 4 — Datagen cleanup

27. **Delete** the three register-heavy datagen provider tests and replace with `runDatagen` + filesystem sweep (TEST-3.4-T1a/b).

## Phase 5 — Coverage-gap fills (new tests, not migrations)

Extract pure-math slices and add missing Tier 2/3 coverage where legacy had none.

28. TEST-2.4-T1 (pure cost math slice).
29. TEST-3.5-T1 (slot-targeting math).
30. TEST-5.2-T1 (damage clamp math).
31. TEST-3.1-T1a (particle enum parameterization).
32. TEST-2.1-T2 (stat JSON codec sweep).
33. TEST-2.2-T2 (offset list size guard).
34. TEST-3.5-T2 (BE NBT round-trip Tier 2 slice).
35. TEST-4.1-T2 (Prismatic Web handler logic Tier 2).
36. TEST-5.1-T2 (scrap tome recipe via RecipeManager).
37. TEST-5.2-T2a/b/c (tome handler logic at Tier 2).
38. TEST-2.1-T3 / TEST-4.2-T3 / TEST-4.3-T3 / TEST-4.5-T3 (real-level assertions beyond what the rewrites cover).
39. TEST-1.2-T3 (mod loads sentinel).
40. TEST-1.4-T3 (reload end-to-end).

## Phase 6 — Epic 6 (roster) — MVP blocker

41. TEST-6.1-T1a/b/c (filesystem sweeps).
42. TEST-6.1-T2/T2b (codec + tag parse).
43. TEST-6.1-T3 (registry membership).
44. TEST-6.2-T1/T2a/T2b/T3 (authored enchants + tag expansion).
45. TEST-6.3-T1/T2/T3a/T3b (foreign overrides).

## Phase 7 — Epic 7 (integrations)

46. TEST-7.1-T2 (shared extractor) — pull TEST-7.1-T1a/b + TEST-7.1-T3 alongside.
47. TEST-7.2-T2 / TEST-7.2-T1 (REI adapts shared extractor).
48. TEST-7.3-T1 / TEST-7.3-T3 (JEI classload + manual).
49. TEST-7.4-T1 (Jade label builders).

## Phase 8 — Epic 8 (release)

50. TEST-8.1-T1/T2/T3 (advancements).
51. TEST-8.2-T1a/b (tooltips).
52. TEST-8.4-T1a/b (release gate).

## Phase 9 — Post-MVP (Epic 9, scheduled on demand)

53. TEST-9.1-T2/T3.
54. TEST-9.2-T3.
55. TEST-9.3-T3.
