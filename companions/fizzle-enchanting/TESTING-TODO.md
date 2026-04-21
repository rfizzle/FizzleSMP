# Fizzle Enchanting — Testing Plan

Companion to `TODO.md`. Mirrors its epic/story/task structure 1:1 — every implementation task `T-X.Y.Z` maps to a testing task `TEST-X.Y.Z` (or is explicitly folded into a parent test with a **Dependencies** pointer). Each test row is a self-contained proposal: tier, assertion, acceptance checkboxes, shared setup.

Do **not** re-read this top-to-bottom when picking up work — jump to the first `- [ ]` test under the current story and work it in isolation.

## Status legend

- `- [x]` — test already written; see the referenced file under `src/test/`.
- `- [ ]` — gap: either the code isn't built yet, or the coverage is missing.
- **Tier** (from `.claude/skills/fabric-testing/SKILL.md`):
  - **1** — pure JUnit, zero `net.minecraft.*` / `net.fabricmc.*` imports.
  - **2** — `fabric-loader-junit`; `@BeforeAll Bootstrap.bootStrap()`; vanilla registries readable, mod-registered content is **not** (onInitialize doesn't fire).
  - **3** — Fabric Gametest; real `ServerLevel`, mod content available, world tick available. Runs under `./gradlew runGametest`.
- **Dependencies** — names other TEST-IDs whose fixtures/setup this test shares. Read as "co-locate in the same test class" or "reuse the same harness," not "run strictly after."

## How to use this file

1. Read the paragraph at the top of each section — that's the **what** and the **risk model** (what silently breaks if untested).
2. Pick the first `- [ ]` test under the story the impl work just landed in.
3. Tick the sub-checkboxes inside acceptance as each assertion lands.
4. When all sub-boxes are ticked, flip the test row's `- [ ]` to `- [x]`.
5. When every test under a story is ticked, add a one-line backfill to the story's paragraph if the risk model shifted.

See **Ordering** at the bottom for the recommended first-to-last sequence.

---

## Table of contents

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

# Epic 1 — Project Scaffolding

Gradle/Loom wiring, entrypoints, JSON config, and the `/fizzleenchanting` command skeleton. If any of this silently regresses, the mod either (a) fails to build, (b) boots with default config every time because `load()` silently swallowed a parse error, (c) clamps are removed and operators ship servers with invalid values, or (d) `/fizzleenchanting reload` runs at perm 0 and lets non-ops mutate live config. The build failure is loud; the other three are silent.

## Story S-1.1 — Buildable Gradle project

Pure build plumbing. No assertions at this level — `./gradlew build` exit code is the oracle. Nothing to propose.

## Story S-1.2 — Mod entrypoints

Trivial — covered by ModBootTest for the main initializer constants; datagen and client initializers have no assertions worth running in isolation (they either exist and fire, or the mod doesn't boot).

- [x] **TEST-1.2.3** — `FizzleEnchanting.MOD_ID` and `LOGGER` are non-null, and `id("foo")` emits `fizzle_enchanting:foo`.
  - **Tier:** 1. No Minecraft classes in the assertion surface beyond `Identifier`, but the existing test pulls it in — leave as-is.
  - **Acceptance:**
    - [x] `MOD_ID == "fizzle_enchanting"`.
    - [x] `LOGGER != null`.
    - [x] `id("foo").toString() == "fizzle_enchanting:foo"`.
  - **Covered by:** `src/test/java/.../ModBootTest.java`.

## Story S-1.3 — Configuration surface

Silent-drift risk: a changed default, a missing clamp row, or a broken migration all ship without a loud failure. Config is the operator's only runtime lever; if it drifts from DESIGN.md the operator is flying blind.

- [x] **TEST-1.3.1** — Defaults class instantiates with every DESIGN field present and clamp-valid.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] Every nested section (`EnchantingTable`, `Shelves`, `Anvil`, `Library`, `Tomes`, `Warden`, `ForeignEnchantments`, `Display`) has its DESIGN default.
    - [x] `configVersion == 1`.
  - **Covered by:** `src/test/java/.../config/FizzleEnchantingConfigTest.java`.

- [x] **TEST-1.3.2** — GSON load/save round-trip + missing-file + partial-file handling.
  - **Tier:** 1. Temp dir via JUnit `@TempDir`.
  - **Acceptance:**
    - [x] Missing file → defaults written + returned.
    - [x] Partial JSON → missing sections filled from defaults.
    - [x] Round-trip preserves values.
    - [x] Malformed JSON → warn + defaults returned, not a crash.
  - **Covered by:** `src/test/java/.../config/FizzleEnchantingConfigTest.java`.
  - **Dependencies:** TEST-1.3.1 (same harness).

- [x] **TEST-1.3.3** — Out-of-range values clamp; `display.overLeveledColor` regex falls back on mismatch.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] `maxEterna: 0 → 1`, `sculkShelfShriekerChance: -0.5 → 0`, `tendrilLootingBonus: 2.0 → 1`.
    - [x] `overLeveledColor: "not-a-hex" → "#FF6600"`.
    - [x] Every clamp emits the expected `warn` log line (assert via a captured logger or skip if fragile — structure of the message is documented in T-1.3.3).
  - **Covered by:** `src/test/java/.../config/FizzleEnchantingConfigTest.java`.
  - **Dependencies:** TEST-1.3.1.

- [x] **TEST-1.3.4** — `migrate()` is a no-op at `configVersion >= CURRENT_VERSION`.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] Config with `configVersion: 1` → `migrate()` is no-op.
    - [x] Config with `configVersion: 2` (future-dated) → migrate doesn't crash, doesn't downgrade.
  - **Covered by:** `src/test/java/.../config/FizzleEnchantingConfigTest.java`.

## Story S-1.4 — `/fizzleenchanting` command skeleton

Silent-drift risk: perm-level check removed by a refactor — non-ops reload live config on the SMP server. Message-key drift leaves operators looking at placeholder-untranslated strings at runtime.

- [x] **TEST-1.4.1/2/3** — Brigadier dispatch for `reload`, `stats`, `library`, `give-tome`; permission gates enforced.
  - **Tier:** 2 — Brigadier itself is vanilla code and needs the dispatcher classloaded via Knot.
  - **Acceptance:**
    - [x] `fizzleenchanting reload` at perm 2 returns `Command.SINGLE_SUCCESS` and re-reads config from disk.
    - [x] `fizzleenchanting reload` at perm 0 rejects before execution.
    - [x] Every stub subcommand (`stats`, `library dump`, `give-tome <type>`) parses without exception at its declared perm level.
    - [x] `give-tome <player> scrap|improved_scrap|extraction` parses the literal `<type>` argument; unknown literals reject.
  - **Covered by:** `src/test/java/.../command/FizzleEnchantingCommandTest.java`.

---

# Epic 2 — Stat System & Table

The Eterna/Quanta/Arcana/Rectification/Clues stack that replaces vanilla's single "power" value. If any of the stat-path code silently regresses, the *cost* and *selection* of every enchant roll is wrong in a way players can't see — there's no "shelves are broken" error, just a gradual drift from expected outcomes. High-risk because it's both the mechanical spine of the mod and entirely invisible at the UI level.

## Story S-2.1 — Stat data model

Codec drift = silent data loss on datapack reload. The `ZERO` constant is a hot path — any shelf outside the registry falls back to it, and if it's accidentally mutated the whole mod leaks state across reloads.

- [x] **TEST-2.1.1** — `EnchantingStats` codec round-trips; missing JSON fields zero-fill; `ZERO` is immutable.
  - **Tier:** 2 — `Codec` needs the DFU registry populated.
  - **Acceptance:**
    - [x] Full JSON → record → JSON preserves every field.
    - [x] `{}` decodes to `ZERO`-equal record.
    - [x] Negative floats accepted; `clues` must be an integer (decimal literal rejected).
    - [x] `ZERO == ZERO` is reference-equal (no per-call construction).
  - **Covered by:** `src/test/java/.../enchanting/EnchantingStatsTest.java`.

- [x] **TEST-2.1.3/4** — `EnchantingStatRegistry.lookup` precedence (direct block > tag > Java fallback > `ZERO`); `vanilla_provider.json` seeds the vanilla bookshelf.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Direct-block reg beats tag reg.
    - [x] Tag reg beats `ENCHANTMENT_POWER_PROVIDER` Java fallback.
    - [x] `ENCHANTMENT_POWER_PROVIDER` tag → `(15,1,0,0,0,0)` fallback fires for an unregistered block.
    - [x] Absent everything → `ZERO`.
    - [x] JSON carrying both `block` and `tag` fails parse with a clear message.
    - [x] Loading `vanilla_provider.json` only → a vanilla bookshelf returns `maxEterna:15, eterna:1`.
  - **Covered by:** `src/test/java/.../enchanting/EnchantingStatRegistryTest.java`.

## Story S-2.2 — Shelf scan & aggregation

Off-by-one risk on `BOOKSHELF_OFFSETS`, clamp drift (`clues` to 3), and — most dangerously — LOS check inversion. If the transmitter check regresses to pass-through, a single stone wall around the table becomes a silent Eterna boost. If LOS is too strict, shelves never contribute.

- [x] **TEST-2.2.1/2/3** — Scan iterates the vanilla offset list, respects LOS, and applies per-stat aggregation rules.
  - **Tier:** 2 for the pure aggregation surface; Tier 3 wrapper pushed to S-2.5 for the real-level variant.
  - **Acceptance:**
    - [x] 15 shelves placed → eterna sums cleanly to 15 with a `(1,1,0,0,0,0)` stub lookup.
    - [x] Inserting a stone at the midpoint of one offset → that offset contributes 0 until stone is removed.
    - [x] Shelves whose eterna sum exceeds `maxEterna` → clamped to `max(maxEterna_i)`.
    - [x] A single shelf with `clues: 5` clamps to 3.
    - [x] Mixed `maxEterna` values → scan uses the highest seen.
  - **Covered by:** `src/test/java/.../enchanting/EnchantingStatRegistryGatherTest.java`.

- [x] **TEST-2.2.4** — Filtering/treasure BE hooks invoked exactly once per in-range BE, no-op when classes absent.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Mock BE fixture: one filtering BE in range → blacklist union observed once.
    - [x] One treasure BE in range → `treasureAllowed == true`.
    - [x] No BEs → both default (empty blacklist, false).
    - [x] BE class absent (dynamic lookup path) → no throw.
  - **Covered by:** `src/test/java/.../enchanting/FilteringTreasureIntegrationTest.java`.
  - **Dependencies:** TEST-2.2.1/2/3.

## Story S-2.3 — S2C network payloads

Codec drift here corrupts the screen silently — the wrong floats render in the HUD, or the crafting-row ghost-vanishes. `ResourceKey<Enchantment>` uses a `RegistryFriendlyByteBuf`-only codec; if it swaps to `FriendlyByteBuf` at some point the test has to catch it.

- [x] **TEST-2.3.1** — `StatsPayload` + `CraftingResultEntry` round-trip over `RegistryFriendlyByteBuf`.
  - **Tier:** 2 — requires a bootstrapped registry access.
  - **Acceptance:**
    - [x] All-zero, mid-load, and saturated variants survive round-trip byte-identical.
    - [x] `craftingResult` Optional preserved in both present and empty shapes.
    - [x] `blacklist` ordering irrelevant (Set semantics) but cardinality matches.
  - **Covered by:** `src/test/java/.../net/PayloadCodecTest.java`.

- [x] **TEST-2.3.2** — `CluesPayload` round-trip including `exhaustedList` flag.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Empty clue list round-trips.
    - [x] 3-entry list round-trips with per-entry `level` preserved.
    - [x] `exhaustedList=true` round-trips.
  - **Covered by:** `src/test/java/.../net/PayloadCodecTest.java`.

- [x] **TEST-2.3.3** — Both payload types registered in the S2C registry after `onInitialize`.
  - **Tier:** 2. Can't fire `onInitialize` from Knot, so the test re-runs the registration manually and checks the registry state.
  - **Acceptance:**
    - [x] `PayloadTypeRegistry.playS2C().get(StatsPayload.TYPE) != null`.
    - [x] `PayloadTypeRegistry.playS2C().get(CluesPayload.TYPE) != null`.
  - **Covered by:** `src/test/java/.../net/NetworkingRegistryTest.java`.

## Story S-2.4 — Enchantment selection algorithm

The highest-risk zone in the mod. Monotonicity bugs in `getEnchantmentCost`, weight-function drift in `selectEnchantment`, or a pool-filter regression mean the mod **still works** — it just rolls the wrong things at the wrong eterna levels, indistinguishable from player bad luck until someone compares against Zenith. Seed determinism tests are the only safety net.

- [x] **TEST-2.4.1** — `getEnchantmentCost` monotonic across slots; seeded RNG reproducible; eterna=50 lands slot-2 in `[25, 50]`.
  - **Tier:** 1 for the math surface; spot-check values come from Zenith's fixture.
  - **Acceptance:**
    - [x] Seeded RNG → byte-identical cost outputs across runs.
    - [x] `cost(slot=2) >= cost(slot=1) >= cost(slot=0)` over 1000 seeded rolls.
    - [x] At `eterna=50`, slot-2 cost ∈ `[25, 50]`.
    - [x] Honors `config.enchantingTable.maxEterna` as a clamp.
  - **Covered by:** `src/test/java/.../enchanting/RealEnchantmentHelperTest.java`.

- [x] **TEST-2.4.2** — `selectEnchantment` blacklist honored, treasure-gate toggles, quanta widens stdev, rectification skews the negative half.
  - **Tier:** 2 — reads `BuiltInRegistries.ENCHANTMENT`.
  - **Acceptance:**
    - [x] Over 1000 rolls, blacklisted key never appears.
    - [x] `treasureAllowed=false` → zero treasure-tagged enchants in output.
    - [x] `treasureAllowed=true` → treasure-tagged enchants appear at non-zero rate.
    - [x] Stdev of levels rolled at `quanta=50` exceeds stdev at `quanta=10` over equal sample sizes.
    - [x] `rectification=∞` → outcomes strictly monotonic with eterna.
  - **Covered by:** `src/test/java/.../enchanting/SelectEnchantmentTest.java`.

- [x] **TEST-2.4.3** — `buildClueList` first clue equals the actual rolled enchant under same seed; exhausted pools flag correctly.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Over 100 seeds, first clue == selected enchant.
    - [x] Pool smaller than `cluesCount` → `exhaustedList == true`.
    - [x] Pool equal to `cluesCount` → list full, `exhaustedList == false`.
  - **Covered by:** `src/test/java/.../enchanting/BuildClueListTest.java`.

## Story S-2.5 — Menu + screen replacement

Three integration surfaces at once: the mixin that swaps the menu provider, the subclass that drives state, and the screen that reads it. Silent-break modes: (a) clicking slot 3 before Epic 5 no-ops instead of throwing (loses the Epic 5 wiring signal); (b) XP/lapis validation order drifts and lets players enchant without lapis; (c) screen formatter truncates labels so `E:` values render wrong.

- [x] **TEST-2.5.1** — Menu click flow: successful enchant mutates item + decrements XP/lapis; lapis-insufficient and XP-insufficient paths decline without mutation.
  - **Tier:** 3 preferred (the menu depends on `FizzleEnchantingRegistry` having registered `MenuType`). Current coverage is at Tier 2 by exercising the `FizzleEnchantmentLogic` helper in isolation — acceptable while Tier 3 wiring isn't live.
  - **Acceptance:**
    - [x] Successful enchant → `ItemEnchantments` grows, XP decremented, lapis count decremented.
    - [x] Lapis missing → output rejected, no state mutation.
    - [x] XP insufficient → rejected, no state mutation.
    - [x] `id == 3` throws `UnsupportedOperationException` until Epic 5 wires it (now overridden by S-5.3 — see TEST-5.3.3).
  - **Covered by:** `src/test/java/.../enchanting/FizzleEnchantmentLogicTest.java`.

- [x] **TEST-2.5.2** — `EnchantmentTableBlockMixin` returns the fizzle menu provider at HEAD.
  - **Tier:** 2. Synthetic `BlockState` construction works once Knot loads the mixin.
  - **Acceptance:**
    - [x] `getMenuProvider` called on vanilla table state → returns `SimpleMenuProvider` whose factory yields `FizzleEnchantmentMenu`.
  - **Covered by:** `src/test/java/.../mixin/EnchantmentTableBlockMixinTest.java`.

- [x] **TEST-2.5.3** — `EnchantmentMenuAccessor` exposes `enchantSlots`, `random`, `enchantmentSeed`.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Each `@Accessor` method returns a non-null reference on a constructed vanilla `EnchantmentMenu`.
    - [x] Accessor name uses the `fizzleEnchanting$` prefix (compile-time, but assert presence).
  - **Covered by:** `src/test/java/.../mixin/EnchantmentMenuAccessorTest.java`.

- [x] **TEST-2.5.4** — Stat-line formatter renders `E: Q: A: R: C:` in display order with the right separators.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] `(50, 12, 5, 10, 2)` → `"E: 50  Q: 12  A: 5  R: 10  C: 2"` (exact spacing matches DESIGN).
    - [x] Fractional eterna floors/rounds as documented.
    - [x] `config.enchantingTable.showLevelIndicator=false` → formatter returns empty string.
  - **Covered by:** `src/test/java/.../enchanting/StatLineFormatterTest.java`.

- [ ] **TEST-2.5.5** — Menu registration resolves through `MenuType` lookup.
  - **Tier:** 3 — `FizzleEnchantingRegistry.registerMenuType` is post-freeze-registered mod content; can't cleanly verify at Tier 2.
  - **Acceptance:**
    - [ ] Under `runGametest`, `BuiltInRegistries.MENU.get(FizzleEnchanting.id("enchantment"))` is non-null and type-correct.
    - [ ] `HandledScreens` lookup at client-sided gametest returns the fizzle screen factory (or document as unreachable in gametest and defer to manual smoke).

---

# Epic 3 — Shelf Family

25 Zenith shelves + utility shelves + BE-backed specials. Silent regressions here are loud at the player level (missing textures, wrong recipe), except for the three insidious ones: (a) a shelf registers but its stat JSON is namespaced to `zenith:` after a bad port, so its contribution silently stays at `ENCHANTMENT_POWER_PROVIDER` defaults; (b) a particle theme resolves to the wrong `ParticleType` and the shelf renders sculk particles; (c) the filtering-shelf slot targeting math mis-targets and inserts books into the wrong slot.

## Story S-3.1 — Shelf infrastructure

- [x] **TEST-3.1.1** — Base `EnchantingShelfBlock` delegates `getStats` to the registry; particle theme preserved through construction.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Construction with `ParticleTheme.ENCHANT_SCULK` → `theme()` returns same enum.
    - [x] `getStats(level, pos, state)` calls `EnchantingStatRegistry#lookup` (observable via a spy/stub registry).
  - **Covered by:** `src/test/java/.../shelf/EnchantingShelfBlockTest.java`.

- [x] **TEST-3.1.2** — Each `ParticleTheme` enum value maps to the right vanilla `ParticleType`.
  - **Tier:** 2 (reads `ParticleTypes` static).
  - **Acceptance:**
    - [x] `ENCHANT → ParticleTypes.ENCHANT`.
    - [x] `ENCHANT_FIRE → FLAME` (or whatever DESIGN pins; cross-reference DESIGN).
    - [x] `ENCHANT_WATER`, `ENCHANT_END`, `ENCHANT_SCULK` similarly pinned.
    - [x] Parameterized over the full enum — adding a new theme forces the test author to extend coverage.
  - **Covered by:** `src/test/java/.../shelf/ParticleThemeTest.java`.

- [x] **TEST-3.1.3** — `FizzleEnchantingRegistry` helpers register blocks + BlockItems into `BuiltInRegistries` once.
  - **Tier:** 3 — the helper mutates vanilla registries; Tier 2 can only validate structure, not post-boot state.
  - **Acceptance:**
    - [x] Under `runGametest`, every shelf id in DESIGN resolves in `BuiltInRegistries.BLOCK`.
    - [x] Same for `BuiltInRegistries.ITEM` (BlockItem).
    - [x] Registrations are idempotent across re-calls (safeguard against `onInitialize` running twice in test harnesses).
  - **Covered by:** `src/test/java/.../FizzleEnchantingRegistryTest.java`.

## Story S-3.2 — Full Zenith shelf roster

- [x] **TEST-3.2.1** — Every expected shelf id from DESIGN resolves in `BuiltInRegistries.BLOCK`.
  - **Tier:** 3.
  - **Acceptance:**
    - [x] Parameterized list from DESIGN: each id → `BuiltInRegistries.BLOCK.get(id) != null`.
    - [x] Sound group matches DESIGN (WOOD vs. STONE) — checked via `SoundType` on the block's properties.
    - [x] Strength matches DESIGN per row.
  - **Covered by:** `src/test/java/.../shelf/FizzleShelvesTest.java`.

- [x] **TEST-3.2.2** — Every ported stat JSON parses and namespace-rewrites to `fizzle_enchanting:`.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] `listResources("enchanting_stats")` returns the expected file list.
    - [x] Every file parses via `EnchantingStats.CODEC`.
    - [x] No file's JSON body contains the literal `"zenith:"`.
    - [x] Datapack-loader round-trip leaves every registration keyed by its `fizzle_enchanting:` id.
  - **Covered by:** `src/test/java/.../enchanting/PortedEnchantingStatsTest.java`.

- [x] **TEST-3.2.3** — Shelf texture bundle present + animated `.mcmeta` preserved; reforging/augmenting dirs not copied.
  - **Tier:** 1 — pure filesystem assertion.
  - **Acceptance:**
    - [x] Glob `assets/fizzle_enchanting/textures/block/*.png` contains every expected shelf.
    - [x] `blazing_hellshelf.png.mcmeta` exists.
    - [x] No files under a `reforging/` or `augmenting/` path exist (forbidden copy).
  - **Covered by:** `src/test/java/.../shelf/ShelfTextureBundleTest.java`.

- [x] **TEST-3.2.4** — Every registered shelf has a `block.fizzle_enchanting.<id>` lang key.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] `en_us.json` parses.
    - [x] For each id in the shelf roster, the matching key exists with a non-empty value.
    - [x] No dangling keys reference cut shelves.
  - **Covered by:** `src/test/java/.../shelf/ShelfLangKeysTest.java`.

## Story S-3.3 — Utility shelves

- [x] **TEST-3.3.1** — Sightshelf tiers contribute clues pre-clamp; `maxEterna: 0` confirmed.
  - **Tier:** 2. Works on the registry directly, no real shelf placement needed.
  - **Acceptance:**
    - [x] `sightshelf.json` → `clues: 1, maxEterna: 0`.
    - [x] `sightshelf_t2.json` → `clues: 2, maxEterna: 0`.
    - [x] Stacked unclamped result before T-2.2.3 clamp == 4.
  - **Covered by:** `src/test/java/.../enchanting/SightshelfStatsTest.java`.

- [x] **TEST-3.3.2** — Rectifier tiers map to `rectification: 10/15/20`.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] `rectifier.json → 10`, `rectifier_t2.json → 15`, `rectifier_t3.json → 20`.
  - **Covered by:** `src/test/java/.../enchanting/RectifierStatsTest.java`.

## Story S-3.4 — Datagen providers

Silent regression risk: one provider silently starts emitting stale output because its generator wasn't wired; missed at commit time because `runDatagen` is idempotent — but only if everyone re-runs it. Idempotency assertion is the key safety net.

- [x] **TEST-3.4.1** — Model provider output covers every registered shelf + the hand-shipped items.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] Post-datagen, `src/main/generated/assets/fizzle_enchanting/models/block/<id>.json` exists per shelf.
    - [x] Item model generator slot populated for `infused_breath`, `warden_tendril`, `prismatic_web`, 3 tomes.
  - **Covered by:** `src/test/java/.../data/FizzleModelProviderTest.java`.

- [x] **TEST-3.4.2** — Loot table `dropSelf` output per block.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] Every shelf / filtering shelf / treasure shelf / library / ender library → one-pool-one-entry-drop-self.
  - **Covered by:** `src/test/java/.../data/FizzleBlockLootTableProviderTest.java`.

- [x] **TEST-3.4.3** — Recipe provider emits every expected shaped recipe; no iron-block anvil-repair recipe (that's anvil-handler territory).
  - **Tier:** 1.
  - **Acceptance:**
    - [x] Every shelf has a generated shaped recipe.
    - [x] Prismatic Web recipe present.
    - [x] Custom `enchanting` / `keep_nbt_enchanting` files are NOT in the generated set (must stay hand-shipped).
  - **Covered by:** `src/test/java/.../data/FizzleRecipeProviderTest.java`.

- [ ] **TEST-3.4.4** — Running datagen twice is a no-op (diff clean).
  - **Tier:** 1 — runs `./gradlew runDatagen` twice via `ProcessBuilder`, then `git diff --exit-code`.
  - **Rationale:** worth wiring if datagen drift becomes a recurring PR-review pain point; skip if manual re-run is disciplined.
  - **Acceptance:**
    - [ ] Second run leaves working tree clean under `src/main/generated/`.

## Story S-3.5 — Filtering & treasure shelves

BE-backed shelves are the highest-risk shelf variant: NBT save/load round-trips, client sync via update packet, and slot-targeting math all have to line up. Silent breaks: (a) NBT load silently loses enchantments so the blacklist shrinks after a server restart; (b) `getUpdatePacket` forgets a field so the client renders an empty slot while the server thinks it's full.

- [x] **TEST-3.5.1** — `FilteringShelfBlockEntity` insert/extract round-trip; NBT round-trip; slot hit-target math.
  - **Tier:** 2 for NBT + blacklist math; Tier 3 advised for real-level cursor-hit targeting.
  - **Acceptance:**
    - [x] Insert enchanted book → blacklist grows by that enchant.
    - [x] Extract that book → blacklist shrinks by exactly that enchant.
    - [x] `saveAdditional` + `load(CompoundTag, RegistryAccess)` restores the full book list.
    - [x] Slot targeting for each of the four corner hits maps to the right slot index.
    - [x] Full shelf rejects additional inserts.
    - [x] Empty shelf contributes as a wood-tier base shelf (DESIGN fallback path).
  - **Covered by:** `src/test/java/.../shelf/FilteringShelfTest.java`.

- [x] **TEST-3.5.2** — `TreasureShelfBlockEntity` presence flips `treasureAllowed`; zero Eterna contribution.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] One treasure BE in range → `StatCollection.treasureAllowed == true`.
    - [x] None in range → `false`.
    - [x] Its `getStats` returns `EnchantingStats.ZERO`.
  - **Covered by:** `src/test/java/.../enchanting/TreasureShelfTest.java`.

- [x] **TEST-3.5.3** — BE hooks wire into `StatCollection` aggregation; union across multiple filtering shelves works.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Two filtering shelves each holding a unique enchanted book → blacklist size 2.
    - [x] Overlapping blacklists dedupe.
    - [x] Treasure flag latches true on first treasure BE found.
  - **Covered by:** `src/test/java/.../enchanting/FilteringTreasureIntegrationTest.java`.

---

# Epic 4 — Anvil & Library

Prismatic Web, iron-block anvil-repair, two library tiers with hopper I/O, custom recipe types. The library has the largest persisted state of any block in the mod — NBT-format drift here silently loses enchantment points that players spent hours banking. The anvil dispatcher handler-order is also load-bearing: adding a new handler at the wrong position silently voids prior behavior.

## Story S-4.1 — Anvil dispatcher

- [x] **TEST-4.1.1** — `AnvilMenuMixin` calls dispatcher at TAIL and overwrites output + cost when a result is returned.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Stub dispatcher returning canned result → anvil output slot receives it.
    - [x] Dispatcher returning empty → vanilla output preserved.
    - [x] `AnvilMenuAccessor` exposes `cost` / `repairItemCountCost` mutators.
  - **Covered by:** `src/test/java/.../mixin/AnvilMenuMixinTest.java`, `src/test/java/.../mixin/AnvilMenuAccessorTest.java`.

- [x] **TEST-4.1.2** — Dispatcher iterates handlers in order; first non-empty wins.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] Two stub handlers — first returns non-empty → second never consulted.
    - [x] First returns empty → second consulted; if second returns non-empty, that's the result.
    - [x] Handler insertion order preserved across resets.
  - **Covered by:** `src/test/java/.../anvil/AnvilDispatcherTest.java`.

- [x] **TEST-4.1.3** — `PrismaticWebItem` resolves and recipe JSON parses.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] `BuiltInRegistries.ITEM.getKey(FizzleEnchantingRegistry.PRISMATIC_WEB).toString() == "fizzle_enchanting:prismatic_web"`.
    - [x] Recipe JSON resolves via `RecipeManager` in a server-test context.
  - **Covered by:** `src/test/java/.../anvil/PrismaticWebItemTest.java`.

- [x] **TEST-4.1.4** — `PrismaticWebHandler` strips curses; preserves non-curse enchants; declines on empty/curse-less input; config-gated.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Curse-of-Vanishing + Sharpness-3 → output has Sharpness 3, no curse.
    - [x] Non-cursed input → handler declines.
    - [x] Right-hand non-web → declines.
    - [x] `config.anvil.prismaticWebRemovesCurses=false` → declines.
    - [x] XP cost == `config.anvil.prismaticWebLevelCost`; consumes 1 web.
  - **Covered by:** `src/test/java/.../anvil/PrismaticWebHandlerTest.java`.

## Story S-4.2 — Iron-block anvil repair

- [x] **TEST-4.2.1/2** — Tier ladder, iron-ingot rejection, enchant preservation, config gate.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Damaged → chipped; chipped → normal.
    - [x] Normal anvil → declines.
    - [x] Iron ingot (wrong material) → declines.
    - [x] Anvil with `ItemEnchantments` component → enchantments preserved on upgrade.
    - [x] `config.anvil.ironBlockRepairsAnvil=false` → declines even on valid inputs.
    - [x] XP cost flat 1 level; consumes 1 iron block.
  - **Covered by:** `src/test/java/.../anvil/IronBlockAnvilRepairTest.java`.

## Story S-4.3 — Library storage engine

Persisted-state risk: point math sign-bit overflow (`int` at 31 levels), map keys drifting format (`toString()` → `tryParse` round-trip must be lossless), and `load` dropping a key without a loud log line. The ender library bounds exist precisely because `int` overflows at level 32.

- [x] **TEST-4.3.1** — Abstract `EnchantmentLibraryBlockEntity` deposit/extract/canExtract behavior.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Deposit single-enchant book updates `points` and `maxLevels`.
    - [x] `canExtract` rejects when `maxLevels[key] < target`.
    - [x] `canExtract` rejects when `points[key] < points(target) - points(curLvl)`.
    - [x] `extract` mutates state only when `canExtract` passes.
  - **Covered by:** `src/test/java/.../library/EnchantmentLibraryBlockEntityTest.java`.

- [x] **TEST-4.3.2** — `BasicLibraryBlockEntity` and `EnderLibraryBlockEntity` carry correct constants.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Basic: `maxLevel == 16`, `maxPoints == 32_768`.
    - [x] Ender: `maxLevel == 31`, `maxPoints == 1_073_741_824`.
    - [x] No config knob exposes these constants.
  - **Covered by:** `src/test/java/.../library/LibraryTierBlockEntityTest.java`.

- [x] **TEST-4.3.3** — `points(level) = 2^(level-1)`, `maxLevelAffordable` matches DESIGN.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] `points(1)=1, points(5)=16, points(16)=32_768, points(31)=1_073_741_824`.
    - [x] `points(0)=0` or as DESIGN specifies for `level<=0`.
    - [x] `maxLevelAffordable(points, curLvl) == 1 + log2(points + points(curLvl))` matches the shift-click DESIGN formula over a parameterized table.
  - **Covered by:** `src/test/java/.../library/PointMathTest.java`.

- [x] **TEST-4.3.4** — NBT round-trip preserves both maps; unknown keys dropped with warn.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Save then load → both maps equal pre-save.
    - [x] Inject an unknown enchant id into the NBT → load succeeds, key dropped, remainder intact, warn observable.
    - [x] No schema version field in MVP (assert absence to catch accidental adds).
  - **Covered by:** `src/test/java/.../library/LibraryNbtTest.java`.

- [x] **TEST-4.3.5** — Client sync packet: server state equals client reconstruction.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Mutate server BE → `getUpdateTag` emits tag containing mutation.
    - [x] Client BE reconstructed from tag `.equals()` server BE (by map contents).
    - [x] Any mutation triggers a full resend (assert via packet count over 3 sequential mutations).
  - **Covered by:** `src/test/java/.../library/LibraryClientSyncTest.java`.

## Story S-4.4 — Library block + UI

- [x] **TEST-4.4.1** — Both library blocks register and resolve; basic recipe parses.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Basic + Ender library blocks each in `BuiltInRegistries.BLOCK`.
    - [x] `library.json` recipe parses.
  - **Covered by:** `src/test/java/.../library/EnchantmentLibraryBlockTest.java`.

- [x] **TEST-4.4.2** — Menu deposit/extract flow including shift-click formula and maxLevels gating.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Book in slot 0 → absorbed on `setChanged`; slot cleared; `points` updated.
    - [x] Extract at `maxLevels=1` with sufficient points → denied.
    - [x] Extract with matching `maxLevels` + sufficient points → slot 1 gets upgraded book.
    - [x] Shift-click → `maxLevelAffordable` formula hit, correct level emitted.
  - **Covered by:** `src/test/java/.../library/EnchantmentLibraryMenuTest.java`.

- [x] **TEST-4.4.3** — Library row formatter renders `{name, maxLevel badge, total points}` cleanly.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] `{Sharpness, maxLevels=5, points=6144}` → expected formatted string.
    - [x] Long enchant names truncated (or not) per DESIGN.
  - **Covered by:** `src/test/java/.../library/LibraryRowFormatterTest.java`.

- [x] **TEST-4.4.4** — Listener set grows on open, shrinks on close; no GC leak after 1000 cycles.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Open menu → listener set size 1.
    - [x] External mutation → listener's `onChanged` fires once.
    - [x] `removed()` → listener set empty.
    - [x] 1000 open/close cycles → listener set still empty at the end.
  - **Covered by:** `src/test/java/.../library/EnchantmentLibraryMenuTest.java` (leak check inline).

## Story S-4.5 — Hopper integration

Transaction-model risk: `SnapshotParticipant` abort/commit semantics are non-obvious. A broken abort leaks stale state across transactions; a broken commit no-ops silently.

- [x] **TEST-4.5.1** — `Storage<ItemVariant>` adapter: inserts only books, rejects everything else, voids overflow.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Diamond sword insert → 0 accepted.
    - [x] Enchanted book insert at cap → returns full input amount (void overflow).
    - [x] `extract` returns 0 unconditionally.
  - **Covered by:** `src/test/java/.../library/LibraryStorageTest.java`.

- [x] **TEST-4.5.2** — `SnapshotParticipant` abort restores pre-insert state; commit persists + fires `setChanged` once.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Begin → insert → abort → state == pre-insert.
    - [x] Begin → insert → commit → state mutated; `setChanged` invoked exactly once.
  - **Covered by:** `src/test/java/.../library/LibraryStorageTest.java`.
  - **Dependencies:** TEST-4.5.1.

- [x] **TEST-4.5.3** — Rate limit drops subsequent inserts within `ioRateLimitTicks`.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Two inserts within rate-limit window → second dropped.
    - [x] `ioRateLimitTicks = 0` → no rate limit.
  - **Covered by:** `src/test/java/.../library/LibraryStorageTest.java`.
  - **Dependencies:** TEST-4.5.1.

## Story S-4.6 — Custom recipe types

Datapack schema is the contract with every shipped recipe file. Codec drift silently breaks the 7 hand-shipped recipes.

- [x] **TEST-4.6.1** — `EnchantingRecipe` codec + `StatRequirements` record semantics.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] `StatRequirements(e,q,a)` round-trips; `-1` maxes encoded.
    - [x] `EnchantingRecipe` MAP_CODEC round-trips.
    - [x] `matches(input, stats)` true when stats ≥ `requirements` and ≤ `maxRequirements`; false otherwise.
    - [x] `RECIPE_TYPE` registered under `BuiltInRegistries.RECIPE_TYPE` post-bootstrap.
  - **Covered by:** `src/test/java/.../enchanting/recipe/EnchantingRecipeTest.java`, `src/test/java/.../enchanting/recipe/StatRequirementsTest.java`.

- [x] **TEST-4.6.2** — `KeepNbtEnchantingRecipe.assemble` preserves `ItemEnchantments`.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Input with Sharpness-5 → result carries Sharpness-5.
    - [x] Codec round-trip for the new type.
  - **Covered by:** `src/test/java/.../enchanting/recipe/EnchantingRecipeTest.java` (second variant) OR a new file — note if missing.

- [x] **TEST-4.6.3** — `EnchantingRecipeRegistry.findMatch` returns the right recipe across both subtypes.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Recipes with varying `(requirements, maxRequirements)` → hit/miss as expected.
    - [x] Mixed `enchanting` + `keep_nbt_enchanting` in the recipe set → findMatch picks the correct subtype.
  - **Covered by:** `src/test/java/.../enchanting/recipe/EnchantingRecipeRegistryTest.java`.

- [x] **TEST-4.6.4** — All 7 shipped recipe JSONs parse and resolve to the declared subtype + bounds match Zenith.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Each of the 7 files loads via `RecipeManager` in a server-test context.
    - [x] Each parses into the expected subtype (6× `enchanting`, 1× `keep_nbt_enchanting`).
    - [x] `(e, q, a)` requirement tuples match DESIGN's Zenith-copied values.
  - **Covered by:** `src/test/java/.../enchanting/recipe/EnchantingRecipeRegistryTest.java`.
  - **Dependencies:** TEST-4.6.3.

---

# Epic 5 — Tomes & Table Crafting UX

Tomes move player-persistent XP value between items; a broken handler either destroys items it shouldn't (silent) or double-counts enchantments (loud). The crafting-result row wires the table menu to a whole second recipe type — id=3 before Epic 5 was a sentinel that threw; after Epic 5 it's the hot path.

## Story S-5.1 — Tome items

- [x] **TEST-5.1.1** — Three tome items register with `stackSize=1`, no durability.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Each tome resolves in `BuiltInRegistries.ITEM`.
    - [x] `getMaxStackSize() == 1` for all three.
    - [x] No durability component attached.
  - **Covered by:** `src/test/java/.../tome/TomeItemsTest.java`.

- [x] **TEST-5.1.2** — Textures present at `item/tome/`, lang keys present, model JSONs reference the right textures.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] 3 tome `.png` files in `assets/fizzle_enchanting/textures/item/tome/`.
    - [x] 3 lang keys present.
    - [x] Each item model JSON has `"textures": {"layer0": "fizzle_enchanting:item/tome/<tome>"}`.
    - [x] No typed-tome textures (9 cut Zenith tomes) present.
  - **Covered by:** `src/test/java/.../tome/TomeAssetsTest.java`.

- [x] **TEST-5.1.3** — Scrap tome vanilla-shape recipe parses.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] `scrap_tome.json` loads via `RecipeManager`.
    - [x] Inputs match DESIGN (ported from Zenith).
  - **Covered by:** `src/test/java/.../tome/ScrapTomeRecipeTest.java`.

## Story S-5.2 — Tome anvil handlers

- [x] **TEST-5.2.1** — `ScrapTomeHandler` seeded RNG → deterministic single-enchant output; unenchanted input declines.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Seeded world random → output enchant equals expected.
    - [x] Left item destroyed, tome consumed.
    - [x] Unenchanted left → handler declines (returns empty).
    - [x] XP cost == `config.tomes.scrapTomeXpCost`.
  - **Covered by:** `src/test/java/.../anvil/ScrapTomeHandlerTest.java`.

- [x] **TEST-5.2.2** — `ImprovedScrapTomeHandler` copies all enchants to the output book.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] 3-enchant input → output book carries all 3 at same levels.
    - [x] Left item destroyed, tome consumed.
    - [x] XP cost == `config.tomes.improvedScrapTomeXpCost`.
  - **Covered by:** `src/test/java/.../anvil/ImprovedScrapTomeHandlerTest.java`.

- [x] **TEST-5.2.3** — `ExtractionTomeHandler` preserves item (damaged), emits full-book output; durability clamp honored.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] 3 enchants → output book has 3, left item survives unenchanted, damage == `config.tomes.extractionTomeItemDamage`.
    - [x] Left durability 1 → stays at 1 after handler (clamped).
    - [x] XP cost == `config.tomes.extractionTomeXpCost`.
  - **Covered by:** `src/test/java/.../anvil/ExtractionTomeHandlerTest.java`.

- [x] **TEST-5.2.4** — Extraction Tome in anvil fuel slot repairs the left item by `repairPercent * maxDurability`.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Damaged sword + tome in fuel slot (no right-hand item) → sword's damage reduced by `repairPercent * maxDurability`.
    - [x] Tome consumed.
    - [x] XP cost identical to standard Extraction.
  - **Covered by:** `src/test/java/.../anvil/ExtractionTomeFuelSlotRepairHandlerTest.java`.

## Story S-5.3 — Table crafting-result row

- [x] **TEST-5.3.1** — `slotsChanged` triggers `findMatch` and stores the result on the menu.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Scripted stats + library input → `currentRecipe.isPresent()`.
    - [x] Stats outside bounds → `currentRecipe.isEmpty()`.
  - **Covered by:** `src/test/java/.../enchanting/TableCraftingLookupTest.java`.

- [x] **TEST-5.3.2** — `CraftingResultEntry` correctly projected onto outgoing `StatsPayload`.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] Recipe present → payload's `craftingResult` is `Optional.of(CraftingResultEntry(stack, xpCost, recipeId))`.
    - [x] Recipe absent → `Optional.empty()`.
    - [x] Round-trip byte-identical.
  - **Covered by:** `src/test/java/.../enchanting/CraftingResultProjectionTest.java`.
  - **Dependencies:** TEST-2.3.1.

- [x] **TEST-5.3.3** — Server handler for `buttonId=3`: both recipe types, XP validation, no-op on no recipe.
  - **Tier:** 2 at the handler boundary; a Tier 3 gametest for the full menu click-to-output flow is a stretch goal.
  - **Acceptance:**
    - [x] `keep_nbt_enchanting` path: input's `ItemEnchantments` preserved onto output (basic library book → ender library book).
    - [x] `enchanting` path: input stack decremented, result inserted.
    - [x] Insufficient XP → no-op.
    - [x] No recipe matched → no-op (safe id==3).
    - [x] Successful path consumes XP and refires `slotsChanged`.
  - **Covered by:** `src/test/java/.../enchanting/CraftingResultFlowTest.java`.

- [x] **TEST-5.3.4** — Client row formatter produces `"<Item> — <X> levels"` + recipe-id hint.
  - **Tier:** 1.
  - **Acceptance:**
    - [x] `(ItemStack(ender_library), 20, recipeId)` → label reads `"Ender Library — 20 levels"`.
    - [x] Plural/singular handling for levels (if DESIGN specifies).
  - **Covered by:** `src/test/java/.../enchanting/CraftingRowFormatterTest.java`.

## Story S-5.4 — Specialty materials

The only story in this epic with partially-written tests. `warden_tendril` item + the Warden loot modifier are the next test slots.

- [x] **TEST-5.4.1** — `InfusedBreathItem` registered; `infused_breath` recipe resolves.
  - **Tier:** 2.
  - **Acceptance:**
    - [x] `BuiltInRegistries.ITEM.get(fizzle_enchanting:infused_breath) != null`.
    - [x] `fizzle_enchanting:enchanting/infused_breath` recipe loads.
    - [x] Lang key + texture present; `.mcmeta` animated metadata preserved.
  - **Covered by:** `src/test/java/.../item/InfusedBreathItemTest.java`.

- [ ] **TEST-5.4.2** — `WardenTendrilItem` registered + assets present.
  - **Tier:** 2.
  - **Acceptance:**
    - [ ] `BuiltInRegistries.ITEM.get(fizzle_enchanting:warden_tendril) != null`.
    - [ ] Texture at `assets/fizzle_enchanting/textures/item/warden_tendril.png` present.
    - [ ] Lang key `item.fizzle_enchanting.warden_tendril` present and non-empty.
    - [ ] Item model JSON resolves to the tendril texture.
  - **Dependencies:** follow TEST-5.4.1 harness.

- [ ] **TEST-5.4.3** — `WardenLootHandler` modifies the warden table with two pools; drop-rate and looting-bonus semantics observable.
  - **Tier:** 3 for the actual loot table execution (needs a `ServerLevel` + `LootContext`); a Tier 2 slice can cover pool shape.
  - **Acceptance:**
    - [ ] After handler runs, `entities/warden` loot table has exactly 2 pools referencing `warden_tendril` (Tier 2 assertion on the pool list, via `LootTableEvents.MODIFY` capture).
    - [ ] Tier 3: `dropChance=1.0, looting=0` → kill mock warden over 100 sims → observed tendril count ≥ 100.
    - [ ] Tier 3: `dropChance=0.0, looting=3` → observed count bounded in `[0, 200]` over 1000 sims (pool B still gated by the looting-chance roll).
    - [ ] Tier 3: `dropChance=0.0, looting=0` → zero tendrils over 100 sims.
  - **Dependencies:** shares gametest template with TEST-3.1.3.

- [ ] **TEST-5.4.4** — `/fizzleenchanting reload` re-reads warden drop values at roll time.
  - **Tier:** 3 — needs a real loot roll.
  - **Acceptance:**
    - [ ] Boot with `dropChance=0.0` → simulate 100 kills → 0 tendrils.
    - [ ] Mutate config in-memory to `dropChance=1.0`, call reload path, simulate 100 kills → ≥100 tendrils.
    - [ ] Assert the handler reads config **at roll time** (inject a mutation mid-batch and observe the change take effect).
  - **Dependencies:** TEST-5.4.3.

---

# Epic 6 — Enchantment Roster

51 enchantments — 49 NeoEnchant+ ports + 2 authored. Silent-break model: the namespace rewrite missed a single file, or a tag entry references a cut enchant, and a subset of enchants either "exist but do nothing" or "drop from the pool" at roll time. The roster is data-only, so parse-failure at server boot is the loud failure mode — but silent downstream effects (missing tag membership, missing lang key, wrong weight) are where the tests earn their keep.

## Story S-6.1 — NeoEnchant+ port (49 files)

- [ ] **TEST-6.1.1** — NeoEnchant+ v5.14.0 extraction manifest matches expected file list (56 JSONs at source).
  - **Tier:** 1 — scratch-dir assertion before the copy-and-rewrite runs.
  - **Acceptance:**
    - [ ] Expected 56 `.json` files present under `data/enchantplus/enchantment/**`.
    - [ ] Directory names match the slot sub-structure DESIGN expects.

- [ ] **TEST-6.1.2** — Post-port: exactly 49 files present, none contain `enchantplus:`, each parses via `Enchantment.CODEC`, none of the 7 cut file names exist.
  - **Tier:** 2 — `Enchantment.CODEC` requires bootstrap.
  - **Acceptance:**
    - [ ] 49 `.json` files under `src/main/resources/data/fizzle_enchanting/enchantment/`.
    - [ ] `grep -r "enchantplus:"` on the port dir returns nothing.
    - [ ] Each file parses via `Enchantment.CODEC` without warnings.
    - [ ] The 7 cut IDs (`axe/timber`, `pickaxe/bedrock_breaker`, `pickaxe/spawner_touch`, `tools/auto_smelt`, `helmet/auto_feed`, `chestplate/magnet`, `sword/runic_despair`) are absent.

- [ ] **TEST-6.1.3** — Every `exclusive_set` tag entry resolves to a present enchant key; no orphaned entries from the cut list.
  - **Tier:** 2.
  - **Acceptance:**
    - [ ] Tag files under `data/fizzle_enchanting/tags/enchantment/exclusive_set/` parse.
    - [ ] Every entry's `ResourceLocation` corresponds to a loaded enchant.
    - [ ] No entry references any of the 7 cut enchants.

- [ ] **TEST-6.1.4** — Lang file has one `enchantment.fizzle_enchanting.<id>` key per ported enchant; no leftover `enchantment.enchantplus.*` keys.
  - **Tier:** 1.
  - **Acceptance:**
    - [ ] `en_us.json` parses.
    - [ ] For each of the 49 ported enchant ids, the matching key exists non-empty.
    - [ ] No key still namespaced to `enchantplus`.
    - [ ] Keys for the 7 cut enchants absent.

## Story S-6.2 — Authored enchants

- [ ] **TEST-6.2.1** — `icy_thorns.json` parses, targets chest armor, applies slowness via `minecraft:post_attack`.
  - **Tier:** 2.
  - **Acceptance:**
    - [ ] Parses via `Enchantment.CODEC`.
    - [ ] `supported_items` resolves to `#minecraft:enchantable/chest_armor`.
    - [ ] Effect carries `minecraft:post_attack` with `affected: "attacker"`, `enchanted: "victim"`.
    - [ ] Applied effect is `minecraft:apply_mob_effect` with Slowness.
    - [ ] Lang key present.

- [ ] **TEST-6.2.2** — `shield_bash.json` + weapon-tag expansion: tag includes `minecraft:shield`, enchant parses with damage + durability-cost effects.
  - **Tier:** 2.
  - **Acceptance:**
    - [ ] `data/minecraft/tags/item/enchantable/weapon.json` has `replace: false` and includes `minecraft:shield`.
    - [ ] `shield_bash.json` parses; `supported_items` resolves.
    - [ ] Carries both `minecraft:damage` and `minecraft:post_attack`-driven `minecraft:damage_item`.
    - [ ] Lang key present.
    - [ ] In a bootstrap test, `Items.SHIELD` satisfies the resolved `supported_items`.

## Story S-6.3 — Foreign enchant overrides

Silent-break risk: bundled override loads but a later datapack silently stomps it; or the `applyBundledOverrides=false` path fails open and always ships overrides.

- [ ] **TEST-6.3.1** — `mending.json` override loads with non-vanilla weight; treasure tag membership preserved.
  - **Tier:** 3 — need a full server boot to see the merged enchant registry.
  - **Acceptance:**
    - [ ] After boot, `BuiltInRegistries.ENCHANTMENT.get(minecraft:mending)` has the bundled weight.
    - [ ] `#minecraft:treasure` tag still contains `minecraft:mending`.
    - [ ] Treasure shelf still gates it (assert via a selection-pool check at `treasureAllowed=false`).

- [ ] **TEST-6.3.2** — `soulbound.json` override is valid JSON even when yigd is absent; when present, overrides.
  - **Tier:** 2 for JSON validity; Tier 3 branch for yigd-present case (environment-dependent, may be skipped in CI).
  - **Acceptance:**
    - [ ] JSON parses.
    - [ ] `supported_items` parse-tolerates an unresolved tag (or documented fallback) when yigd is absent.
    - [ ] Conditional assert: yigd installed → registry reflects override weight.

- [ ] **TEST-6.3.3** — `applyBundledOverrides` flag toggles override vs. upstream.
  - **Tier:** 3.
  - **Acceptance:**
    - [ ] Boot with flag `true` → overrides present.
    - [ ] Boot with flag `false` → upstream values restored (via higher-priority resource-pack source OR via skipping the copy path — whichever approach T-6.3.3 lands with).
    - [ ] Switching the flag and reloading resources flips behavior without a restart (if the chosen approach supports it; otherwise document).

---

# Epic 7 — Integrations

EMI / REI / JEI / Jade adapters. Failure mode is near-silent: the adapter classes never run in-proc if their host mod isn't installed, so integration-only regressions don't show up in `./gradlew test`. Unit-testable portion is the shared `TableCraftingDisplay` extractor.

## Story S-7.1 — EMI adapter

- [ ] **TEST-7.1.1** — Build succeeds with and without EMI in the dev runtime.
  - **Tier:** 1 — CI matrix or two separate gradle runs.
  - **Acceptance:**
    - [ ] `./gradlew build` green with EMI `modRuntimeOnly` active.
    - [ ] `./gradlew build` green with EMI excluded.
    - [ ] `fabric.mod.json` entry `"emi": [...]` present.

- [ ] **TEST-7.1.2** — `TableCraftingDisplay` extractor returns the expected number of entries for both recipe subtypes.
  - **Tier:** 2 — reads `RecipeManager`.
  - **Acceptance:**
    - [ ] Extractor output includes every shipped `enchanting` + `keep_nbt_enchanting` recipe.
    - [ ] Each display carries input ingredient, result stack, stat requirements, XP cost.
    - [ ] No EMI runtime needed for this slice.
  - **Dependencies:** TEST-4.6.4.

- [ ] **TEST-7.1.3** — EMI recipe render (manual smoke, not automated).
  - **Tier:** n/a — document the smoke checklist in the story PR.
  - **Acceptance:**
    - [ ] With EMI installed in dev runtime, categories "Shelves" and "Tomes" appear in the panel.
    - [ ] Each recipe displays input/output/requirements/XP correctly.

## Story S-7.2 — REI adapter

- [ ] **TEST-7.2.1** — Build succeeds with and without REI; plugin entry present.
  - **Tier:** 1.
  - **Acceptance:** Same shape as TEST-7.1.1.

- [ ] **TEST-7.2.2** — Shared `TableCraftingDisplay` adapts to REI's display type without loss.
  - **Tier:** 2.
  - **Acceptance:**
    - [ ] One-test exercise over the shared extractor; assert the REI adapter preserves every field.
  - **Dependencies:** TEST-7.1.2.

## Story S-7.3 — JEI adapter

- [ ] **TEST-7.3.1** — JEI plugin class loads at classloader level; manual smoke documented.
  - **Tier:** 1 for the classload smoke; manual for the UI.
  - **Acceptance:**
    - [ ] `Class.forName("com.fizzlesmp.fizzle_enchanting.compat.jei.JeiEnchantingPlugin")` succeeds when JEI is on classpath.
    - [ ] Manual smoke checklist documented.

## Story S-7.4 — Jade probe tooltips

- [ ] **TEST-7.4.1** — Jade tooltip string builders return expected labels.
  - **Tier:** 1.
  - **Acceptance:**
    - [ ] Enchanting table probe string has all 5 stats in `E/Q/A/R/C` order.
    - [ ] Library probe string reads `"Basic Library — N enchants stored"` with the right `N`.
    - [ ] Probe does NOT expose per-enchant points when outside the library UI.

---

# Epic 8 — Polish & Release

Content finalization: advancements, tooltips, docs, release prep. Silent-break model: advancement triggers reference an enchant that was cut; tooltip recolor ignores a config hex fallback that T-1.3.3 already enforces. Most of this epic is manual-review territory.

## Story S-8.1 — Advancement tree

- [ ] **TEST-8.1.1** — All 10 advancement JSONs parse via `Advancement.CODEC`; every `requirements` references present shelves/items.
  - **Tier:** 2.
  - **Acceptance:**
    - [ ] All 10 JSONs in `data/fizzle_enchanting/advancement/` parse.
    - [ ] Every item/tag reference resolves after bootstrap.
    - [ ] `apotheosis` advancement uses a valid trigger (custom or repurposed `minecraft:enchanted_item`).

- [ ] **TEST-8.1.2** — Every advancement has a title + description lang key.
  - **Tier:** 1.
  - **Acceptance:**
    - [ ] For each advancement id, both `advancement.fizzle_enchanting.<id>.title` and `advancement.fizzle_enchanting.<id>.description` present and non-empty.

## Story S-8.2 — Tooltips + overleveled coloring

- [ ] **TEST-8.2.1** — Tooltip formatter recolors over-leveled enchants; vanilla-cap map populated; fallback color honored.
  - **Tier:** 1.
  - **Acceptance:**
    - [ ] Sharpness 7 → recolored with `config.display.overLeveledColor`.
    - [ ] Sharpness 5 → vanilla color.
    - [ ] Invalid config hex → fallback `#FF6600` per TEST-1.3.3.
    - [ ] Vanilla-cap map has one entry per shipped enchant (hard-coded MVP).

- [ ] **TEST-8.2.2** — Stored-book per-level tooltip lines suppressed when `config.display.showBookTooltips=false`.
  - **Tier:** 1.
  - **Acceptance:**
    - [ ] Flag true → lines present.
    - [ ] Flag false → empty list.

## Story S-8.3 — Operator docs

Documentation. No test rows — manual review only.

## Story S-8.4 — Release prep

- [ ] **TEST-8.4.1** — Full test + build sweep green: `./gradlew runDatagen && ./gradlew clean build test` is the release gate.
  - **Tier:** 1 (CI orchestration).
  - **Acceptance:**
    - [ ] All three gradle tasks exit zero.
    - [ ] No new warnings beyond the known baseline.

- [ ] **TEST-8.4.2** — CHANGELOG and plugin-list entries lint-valid (format matches CLAUDE.md rules).
  - **Tier:** 1 — content check only.
  - **Acceptance:**
    - [ ] `companions/fizzle-enchanting/CHANGELOG.md` has a `[0.1.0]` heading with epic highlights.
    - [ ] Root `CHANGELOG.md` `[Unreleased] → Added` has the Fizzle Enchanting line.
    - [ ] `plugins/gameplay.md` has a `## Fizzle Enchanting` entry with every required field.

- [ ] **TEST-8.4.3** — Conflict audit artifact: `/check-conflicts` output recorded, meaningful findings landed in `docs/compatibility-matrix.md`.
  - **Tier:** n/a — documentation / audit step.
  - **Acceptance:**
    - [ ] Audit output committed alongside the release PR.
    - [ ] Every non-trivial conflict has a matrix row.

---

# Epic 9 — Post-MVP Iteration Backlog

Each story below only gets a testing plan when the iteration is scheduled. Placeholder rows so the `TEST-9.x` IDs are reserved and won't collide with MVP IDs.

## Story S-9.1 — Absorb BeyondEnchant

- [ ] **TEST-9.1.1** — 16 override JSONs at `data/minecraft/enchantment/*.json` parse and carry the expected caps; `levelCaps.perEnchantment` overrides bundled defaults.
  - **Tier:** 3.
  - **Acceptance:**
    - [ ] All 16 overrides parse.
    - [ ] Runtime cap lookup uses `config.levelCaps.perEnchantment` when `enabled=true`.
    - [ ] Spectrum-BeyondEnchant-LevelCap-Fix Paxi pack still applies cleanly.

## Story S-9.2 — Absorb Easy Magic (item persistence)

- [ ] **TEST-9.2.1** — Table BE persists slot contents across GUI close + logout/login + pre-existing vanilla table migration.
  - **Tier:** 3.
  - **Acceptance:**
    - [ ] Place item, close GUI, reopen → item present.
    - [ ] Place item, logout/login → item present.
    - [ ] Migrate a vanilla table chunk → BE attaches idempotently.
    - [ ] Re-roll feature confirmed absent (negative test).

## Story S-9.3 — Easy Anvils `tooExpensiveCap`

- [ ] **TEST-9.3.1** — Cap values 40, -1, 0 behave per DESIGN (only if story green-lit).
  - **Tier:** 3.
  - **Acceptance:**
    - [ ] `cap=40` → "too expensive" triggers at the configured level.
    - [ ] `cap=-1` → never triggers.
    - [ ] `cap=0` → vanilla behavior preserved.

## Story S-9.4 — Per-iteration bookkeeping checklist

No test rows — this is a process checklist that runs once per iteration landing.

---

# Ordering

Work tests in the order below. Two guiding principles: **within a section**, Tier 1 → Tier 2 → Tier 3; **across sections**, user-facing hot paths before defensive edges. Existing `- [x]` rows can be skipped unless they regress.

## Phase 0 — Existing coverage regression check (already `[x]`)

Before picking up new work, run `./gradlew test` once. Every `- [x]` row corresponds to a real file under `src/test/`; if any fail, fix before adding rows below. Do this as a **single sanity step**, not a test-by-test walk.

## Phase 1 — Highest-risk gaps still unchecked (MVP-blocking)

These sit on hot user-facing paths. Silent breaks here ship bugs to live SMP.

1. **TEST-5.4.2** — `WardenTendrilItem` registration + assets (Tier 2). Gate for everything else in S-5.4.
2. **TEST-5.4.3** — `WardenLootHandler` pool shape + drop-rate (Tier 2 slice first, then Tier 3). The only way players obtain tendrils — if this silently no-ops the two sculkshelves become uncraftable.
3. **TEST-5.4.4** — Config-reload re-reads drop chances at roll time (Tier 3). Depends on 5.4.3.

## Phase 2 — Epic 6 data layer (MVP-blocking)

The whole epic is data files + a few Java surfaces; tests are fast, failure modes are silent. Run 6.1 before 6.2 before 6.3 — the file inventory gates everything downstream.

4. **TEST-6.1.1** — NeoEnchant+ manifest check (Tier 1). Pre-port sanity.
5. **TEST-6.1.2** — Port completeness + CODEC parse (Tier 2). Biggest regression surface in Epic 6.
6. **TEST-6.1.3** — Exclusive-set tag entries resolve (Tier 2). Depends on 6.1.2.
7. **TEST-6.1.4** — Lang key merge (Tier 1). Independent of 6.1.2/3 but conceptually paired.
8. **TEST-6.2.1** — Icy Thorns parse + effect shape (Tier 2).
9. **TEST-6.2.2** — Shield Bash + weapon tag expansion (Tier 2).
10. **TEST-6.3.1** — Mending override + treasure gate preserved (Tier 3). Hot path — Mending is the highest-value enchant in the game.
11. **TEST-6.3.2** — Soulbound override parse (Tier 2 / conditional Tier 3).
12. **TEST-6.3.3** — `applyBundledOverrides` flag (Tier 3).

## Phase 3 — Datagen idempotency (deferred defensive)

13. **TEST-3.4.4** — Datagen re-run is a no-op. Low urgency unless datagen drift becomes a recurring review pain; easy to wire when it does.

## Phase 4 — Tier 3 gametest stretch for S-2.5.5

14. **TEST-2.5.5** — `MenuType<FizzleEnchantmentMenu>` post-boot resolution. Currently deferred because mod-registered content is a Tier 3 problem; land it once the Tier 3 harness exists for Epic 6's overrides.

## Phase 5 — Epic 7 integrations

Run after MVP data layer is green. Shared extractor test gives two integrations for the price of one.

15. **TEST-7.1.2** — EMI extractor (Tier 2). Pull 7.1.1 and 7.1.3 alongside as cheap additions.
16. **TEST-7.2.2** — REI adapts the shared extractor. Cheapest win after 7.1.2.
17. **TEST-7.1.1** / **TEST-7.2.1** — Build matrices with/without host mods.
18. **TEST-7.3.1** — JEI plugin classload.
19. **TEST-7.4.1** — Jade label builders.

## Phase 6 — Epic 8 release gates

20. **TEST-8.1.1** — Advancement parse + reference resolution (Tier 2). Data only; catches cut-enchant drift.
21. **TEST-8.1.2** — Advancement lang keys (Tier 1).
22. **TEST-8.2.1** / **TEST-8.2.2** — Tooltip formatter + suppression (Tier 1).
23. **TEST-8.4.1** — Full gradle sweep. Release gate.
24. **TEST-8.4.2** — CHANGELOG + plugin-list lint.
25. **TEST-8.4.3** — Conflict audit capture.

## Phase 7 — Epic 9 (post-MVP, only when scheduled)

26. **TEST-9.1.1** — BeyondEnchant overrides.
27. **TEST-9.2.1** — Easy Magic persistence (Tier 3, the most involved Tier 3 in the mod).
28. **TEST-9.3.1** — Too-expensive cap (conditional).
