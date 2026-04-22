# Fizzle Enchanting — Sprint Backlog

Structured as **Epics → Stories → Tasks → Subtasks**. Each **Task** is sized to run in a fresh context window — if the assistant's history is cleared mid-sprint, any unstarted task can be picked up from its block alone.

## Status legend

- Every **Epic** and **Story** has a single `- [ ]` status line right under its heading. That line flips to `- [x]` when the story/epic is done.
- Tasks do **not** carry their own status — they roll up through the Story's acceptance/subtask/test checkboxes. When every Task in a Story is finished and the commit is ready, tick the Story line.
- `/continue-enchanting` picks up the first Story whose status line is still `- [ ]`.

## How to use this file

1. Find the first Story with `- [ ] Story complete` still unchecked.
2. Read that Story's tasks top-to-bottom — each Task has its own **Resume context**, **Acceptance**, **Subtasks**, **Tests**.
3. Work tasks in order within the Story; tick acceptance/subtask/test boxes as you go.
4. When all tasks in the Story are done, tick the Story's status line and commit using the **Commit** line at the Epic's top.
5. When every Story in an Epic is ticked, tick the Epic's status line.

---

## Standing context (every task inherits this)

- **Design doc:** `/home/rfizzle/Projects/FizzleSMP/companions/fizzle-enchanting/DESIGN.md`
- **Project root:** `/home/rfizzle/Projects/FizzleSMP/companions/fizzle-enchanting/`
- **Companion guidance (read every task):** `/home/rfizzle/Projects/FizzleSMP/.claude/commands/dev-companion.md`
- **Template companion (Gradle / config / tests / command idioms):** `/home/rfizzle/Projects/FizzleSMP/companions/fizzle-difficulty/`
- **Zenith reference (Fabric 1.20.1, closest prior art):** `/home/rfizzle/Projects/Zenith/`
- **Apotheosis reference (Forge, authoritative for mechanics):** `/home/rfizzle/Projects/Apotheosis/`
- **Mod ID:** `fizzle_enchanting` — base package `com.fizzlesmp.fizzle_enchanting`
- **Build:** `./gradlew build` from `companions/fizzle-enchanting/`
- **Tests:** `fabric-loader-junit` under `src/test/java/com/fizzlesmp/fizzle_enchanting/...`
- **Commit style:** Conventional Commits, `feat(enchanting): …` / `fix(enchanting): …`. One logical commit per **Story**, not per task.
- **1.21.1 gotchas:** `Identifier.of(...)`, singular data paths (`tags/block`), data-driven enchants — see `/dev-companion`.

Every task's Definition of Done implicitly includes:

- `./gradlew build` passes
- `./gradlew test` passes
- `/dev-companion` quality checklist reviewed for touched files
- No new warnings beyond pre-existing baseline

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

---

# Epic 1 — Project Scaffolding

- [x] Epic complete

**Goal:** `./gradlew build` succeeds on an empty mod, config + command plumbing in place.
**Commit at epic close:** `feat(enchanting): scaffold fizzle-enchanting`

---

## Story S-1.1 — Buildable Gradle project

- [x] Story complete

**As a** developer, **I want** a Loom-configured Gradle project **so that** `./gradlew build` succeeds before any feature code lands.

### Task T-1.1.1 — Copy Gradle/Loom scaffolding from fizzle-difficulty

**Resume context:** `companions/fizzle-difficulty/build.gradle`, `settings.gradle`, `gradle.properties`, `gradle/`, `gradlew`, `gradlew.bat`, `Makefile`.

**Acceptance:**
- [x] Files present at `companions/fizzle-enchanting/` with execute bits on `gradlew`.
- [x] `settings.gradle` references this project's name, not fizzle-difficulty's.

**Subtasks:**
- [x] Copy the six files above verbatim.
- [x] `chmod +x gradlew`.
- [x] Rename any project-name references in `settings.gradle` → `fizzle-enchanting`.

**Tests:** `./gradlew --version` runs without error.

---

### Task T-1.1.2 — Wire project metadata

**Resume context:** `companions/fizzle-enchanting/gradle.properties`. Mirror fizzle-difficulty's `computeModVersion()` tag convention — tag prefix becomes `fizzle-enchanting-v*`.

**Acceptance:**
- [x] `archives_base_name=fizzle-enchanting`
- [x] `maven_group=com.fizzlesmp`
- [x] `mod_version=0.1.0`
- [x] `minecraft_version=1.21.1`, `loader_version=0.16.10`, `fabric_version=0.116.1+1.21.1`, `loom_version=1.9-SNAPSHOT`, `java_version=21`
- [x] `computeModVersion()` groovy block ported unchanged from fizzle-difficulty's `build.gradle` — tag prefix reads from `archives_base_name`.

**Subtasks:**
- [x] Edit `gradle.properties`.
- [x] Edit `build.gradle` keeping the `computeModVersion()` / `runGitDescribe()` block byte-for-byte identical to fizzle-difficulty.

**Tests:** `./gradlew printVersion` emits `0.1.0` on a clean HEAD.

---

### Task T-1.1.3 — Source sets + Makefile + first green build

**Acceptance:**
- [x] Dirs exist: `src/main/java`, `src/main/resources`, `src/client/java`, `src/client/resources`, `src/test/java`.
- [x] `loom { splitEnvironmentSourceSets() }` in `build.gradle`.
- [x] JUnit Jupiter + `fabric-loader-junit` deps added (match fizzle-difficulty versions).
- [x] `Makefile` at project root with targets `build`, `clean`, `test`, `runServer`, `runClient`, `runDatagen` mirroring fizzle-difficulty's.

**Subtasks:**
- [x] Create the empty dirs with `.gitkeep` placeholders.
- [x] Copy `Makefile` from fizzle-difficulty; rename any paths.
- [x] Add test deps block to `build.gradle`.

**Tests:**
- [x] `./gradlew build` succeeds.
- [x] `./gradlew test` runs (zero tests, exit 0).

---

## Story S-1.2 — Mod entrypoints

- [x] Story complete

**As the** Fabric loader, **I want** valid entrypoints **so that** the mod initializes (even if it does nothing yet).

### Task T-1.2.1 — `fabric.mod.json`

**Resume context:** DESIGN.md § "Fabric & Quilt" and § "Project Structure". `/dev-companion` § "fabric.mod.json".

**Acceptance:**
- [x] File at `src/main/resources/fabric.mod.json`.
- [x] `id=fizzle_enchanting`, `version=${version}`, `environment="*"`.
- [x] Entrypoints:
  - `main: ["com.fizzlesmp.fizzle_enchanting.FizzleEnchanting"]`
  - `client: ["com.fizzlesmp.fizzle_enchanting.client.FizzleEnchantingClient"]`
  - `fabric-datagen: ["com.fizzlesmp.fizzle_enchanting.data.FizzleEnchantingDataGenerator"]`
- [x] `mixins: ["fizzle_enchanting.mixins.json"]`.
- [x] `depends: { fabricloader: ">=0.16.10", fabric-api: "*", minecraft: "~1.21.1", java: ">=21" }`.

**Subtasks:**
- [x] Write file.
- [x] Add `icon`/`authors`/`contact`/`license` minimal stubs.

**Tests:** `./gradlew runServer` (headless dry-run from Loom's generated config) boots far enough to log `"Fizzle Enchanting initialized"` once T-1.2.3 lands; until then, just assert the mod is registered in the Loader manifest.

---

### Task T-1.2.2 — Mixin config

**Acceptance:**
- [x] File at `src/main/resources/fizzle_enchanting.mixins.json`.
- [x] `compatibilityLevel: "JAVA_21"`, `package: "com.fizzlesmp.fizzle_enchanting.mixin"`, empty `mixins: []` and `client: []` lists, `"refmap": "fizzle_enchanting.refmap.json"` (or let Loom auto-populate).

**Subtasks:**
- [x] Write file.
- [x] Create empty `mixin/` package under `src/main/java/com/fizzlesmp/fizzle_enchanting/`.

**Tests:** `./gradlew build` still green.

---

### Task T-1.2.3 — Initializer classes

**Acceptance:**
- [x] `FizzleEnchanting.java` (main): `MOD_ID="fizzle_enchanting"`, `LOGGER=LoggerFactory.getLogger(MOD_ID)`, `onInitialize()` logs `"Fizzle Enchanting initialized"`.
- [x] `client/FizzleEnchantingClient.java` (client, empty `onInitializeClient()`).
- [x] `data/FizzleEnchantingDataGenerator.java` (empty `onInitializeDataGenerator(FabricDataGenerator)`).

**Subtasks:**
- [x] Write each class.
- [x] Add `public static Identifier id(String path) { return Identifier.of(MOD_ID, path); }` helper on main class.

**Tests:** `src/test/java/com/fizzlesmp/fizzle_enchanting/ModBootTest.java` — assert `FizzleEnchanting.MOD_ID == "fizzle_enchanting"` and `LOGGER != null`.

---

## Story S-1.3 — Configuration surface

- [x] Story complete

**As an** operator, **I want** a JSON config at `config/fizzle_enchanting.json` with validation **so that** I can tune the mod without a jar rebuild.

### Task T-1.3.1 — Config data classes + defaults

**Resume context:** DESIGN.md § "Configuration (MVP)" — copy the JSON shape exactly.

**Acceptance:**
- [x] `config/FizzleEnchantingConfig.java` with nested static sections: `EnchantingTable`, `Shelves`, `Anvil`, `Library`, `Tomes`, `Warden`, `ForeignEnchantments`, `Display`.
- [x] Every field has the default value from the DESIGN.md JSON.
- [x] `int configVersion = 1;`

**Subtasks:**
- [x] Mirror fizzle-difficulty's `FizzleDifficultyConfig` class shape (nested statics, public fields).
- [x] Confirm every DESIGN field is present.

**Tests:** `FizzleEnchantingConfigTest#defaultConfig_hasValidValues` — every default satisfies its clamp (see T-1.3.3).

---

### Task T-1.3.2 — GSON load/save + file I/O

**Acceptance:**
- [x] Static `load()` — reads `FabricLoader.getInstance().getConfigDir().resolve("fizzle_enchanting.json")`, writes defaults on missing file, swallow-and-log on parse failure + fall back to defaults.
- [x] Instance `save()` — pretty-printed GSON output.
- [x] Private `fillDefaults()` null-checks each section.

**Subtasks:**
- [x] Mirror fizzle-difficulty's serializer (bundled GSON, no extra dep).
- [x] Log warnings on I/O errors with the throwable attached.

**Tests:**
- [x] `load_missingFile_writesDefaultsAndReturns` — temp dir, no file → file appears, defaults returned.
- [x] `load_partialFile_fillsDefaults` — `{"enchantingTable":{"maxEterna":42}}` → other sections default.
- [x] `saveAndLoad_roundTrip_preservesValues`.

---

### Task T-1.3.3 — Validation clamps

**Resume context:** DESIGN.md § "Validation (clamps applied on load)" — full field table.

**Acceptance:**
- [x] Helpers `clampNonNegative`, `clampPositive`, `clampUnit` match fizzle-difficulty's signatures.
- [x] Every row in the DESIGN validation table is enforced on `load()`.
- [x] Clamps emit `LOGGER.warn("clamped {} from {} to {}", field, old, new)`.
- [x] `display.overLeveledColor` regex `^#[0-9A-Fa-f]{6}$`; mismatch → warn + fall back to `#FF6600`.

**Tests:**
- [x] `load_clampsOutOfRange` — `maxEterna:0 → 1`, `sculkShelfShriekerChance:-0.5 → 0`, `tendrilLootingBonus:2.0 → 1`.
- [x] `load_invalidOverLeveledColor_fallsBack` → `"#FF6600"`.

---

### Task T-1.3.4 — Migration hook

**Resume context:** DESIGN.md § "Migration strategy".

**Acceptance:**
- [x] `private static final int CURRENT_VERSION = 1;`
- [x] `migrate()` runs **after parse, before `fillDefaults()`**; no-op while `configVersion >= CURRENT_VERSION`.
- [x] Ready for future `case 1 -> migrateV1toV2();` branch without restructuring.

**Tests:** `migrate_versionOne_isNoOp`.

---

## Story S-1.4 — `/fizzleenchanting` command skeleton

- [x] Story complete

**As an** operator, **I want** `/fizzleenchanting reload` **so that** I can reload config live.

### Task T-1.4.1 — Brigadier registration

**Resume context:** `/dev-companion` § "Commands". `companions/fizzle-difficulty/src/main/java/com/fizzlesmp/fizzle_difficulty/command/FizzleDifficultyCommand.java` as template.

**Acceptance:**
- [x] `command/FizzleEnchantingCommand.java` registers via `CommandRegistrationCallback.EVENT` in `FizzleEnchanting#onInitialize`.
- [x] Literal root: `fizzleenchanting`.

**Subtasks:**
- [x] Register literal.
- [x] Wire into main initializer.

**Tests:** Command appears in `/help` output (via a Brigadier dispatch test).

---

### Task T-1.4.2 — `reload` subcommand

**Acceptance:**
- [x] `.requires(s -> s.hasPermissionLevel(2))`, reloads config, replies translated key `command.fizzle_enchanting.reload.ok`.
- [x] On reload failure, replies `command.fizzle_enchanting.reload.error` and logs the throwable.

**Tests:**
- [x] `reload_atPerm2_succeeds` — returns `Command.SINGLE_SUCCESS`, config mutated in memory.
- [x] `reload_atPerm0_fails` — parse/permission error.

---

### Task T-1.4.3 — Stub subcommands

**Resume context:** DESIGN.md § "Commands (MVP)" — surface must match, but body ships as stubs.

**Acceptance:**
- [x] `stats <player>` at perm 0.
- [x] `library <player> dump` at perm 2.
- [x] `give-tome <player> <type>` at perm 2, `<type>` parses literal `scrap|improved_scrap|extraction`.
- [x] Each stub logs `"not implemented yet"` and replies with a placeholder string.

**Tests:** Each stub parses and executes without exception — body no-ops are fine, the surface is the deliverable.

---

# Epic 2 — Stat System & Table

- [x] Epic complete

**Goal:** Stat-driven enchanting table functional with one proof-of-concept shelf.
**Commit at epic close:** `feat(enchanting): stat-driven enchantment table`

---

## Story S-2.1 — Stat data model

- [x] Story complete

**As a** server operator, **I want** per-block stat contributions in datapack JSON **so that** rebalancing doesn't require a rebuild.

### Task T-2.1.1 — `EnchantingStats` record + codec

**Resume context:** DESIGN.md § "Stat System" and § "Shelf Blocks" for field list.

**Acceptance:**
- [x] Record `EnchantingStats(float maxEterna, float eterna, float quanta, float arcana, float rectification, int clues)`.
- [x] `Codec<EnchantingStats>` via `RecordCodecBuilder`; every field is `optionalFieldOf(..., 0)` so missing JSON fields zero-fill.
- [x] `public static final EnchantingStats ZERO = new EnchantingStats(0,0,0,0,0,0);`
- [x] Value-class semantics — record equality works in maps.

**Tests:** `EnchantingStatsTest` — codec round-trips; missing fields default to zero; negative values allowed except `clues` (integer only).

---

### Task T-2.1.2 — `IEnchantingStatProvider` interface

**Acceptance:**
- [x] `enchanting/IEnchantingStatProvider.java`: `EnchantingStats getStats(Level level, BlockPos pos, BlockState state)`.
- [x] Default impl delegates to `EnchantingStatRegistry#lookup(state)`.

**Subtasks:**
- [x] Interface only — no implementors yet (Story S-3.1 wires `EnchantingShelfBlock`).

**Tests:** None at this task (covered downstream in S-2.2).

---

### Task T-2.1.3 — `EnchantingStatRegistry` datapack loader

**Resume context:** DESIGN.md § "Stat System" lookup order (datapack → `ENCHANTMENT_POWER_PROVIDER` tag → zero).

**Acceptance:**
- [x] `SimpleSynchronousResourceReloadListener` registered under `PackType.SERVER_DATA` for path `enchanting_stats`.
- [x] JSON schema accepts **either** `block: "ns:id"` **or** `tag: "#ns:id"` — never both (rejected at parse).
- [x] `lookup(Level, BlockState)` order:
  1. Direct block registration → return.
  2. Tag registration → return (first match if multiple tags match).
  3. Block in `BlockTags.ENCHANTMENT_POWER_PROVIDER` → `new EnchantingStats(15, 1, 0, 0, 0, 0)`.
  4. Else `EnchantingStats.ZERO`.
- [x] Reload clears prior registrations — no stale entries.

**Subtasks:**
- [x] Synchronous loader over `ResourceManager.listResources(...)`.
- [x] Two internal maps: `Map<Block, EnchantingStats>` and `List<Pair<TagKey<Block>, EnchantingStats>>`.

**Tests:** `EnchantingStatRegistryTest` —
- [x] Direct-block match beats tag match.
- [x] Tag match beats Java fallback.
- [x] Java fallback fires for a block tagged `ENCHANTMENT_POWER_PROVIDER` absent from both registries.
- [x] JSON with both `block` and `tag` fails parse with a clear message.

---

### Task T-2.1.4 — Ship `vanilla_provider.json`

**Resume context:** DESIGN.md § "Stat System" — "We ship a stat JSON ... so operators can retune vanilla bookshelves without a jar rebuild."

**Acceptance:**
- [x] `src/main/resources/data/fizzle_enchanting/enchanting_stats/vanilla_provider.json` with `{ "tag": "#minecraft:enchantment_power_provider", "maxEterna": 15, "eterna": 1 }`.

**Tests:** `EnchantingStatRegistryTest#vanillaProviderSeedApplies` — after loading this one file, a vanilla bookshelf returns `{maxEterna:15, eterna:1}`.

---

## Story S-2.2 — Shelf scan & aggregation

- [x] Story complete

**As a** player, **I want** nearby shelves to boost my table's stats **so that** placement choices matter.

### Task T-2.2.1 — `BOOKSHELF_OFFSETS` iteration scaffolding

**Resume context:** `/home/rfizzle/Projects/Zenith/src/main/java/dev/shadowsoffire/apotheosis/ench/table/EnchantingStatRegistry.java` — read for logic; re-implement cleanly.

**Acceptance:**
- [x] `EnchantingStatRegistry#gatherStats(Level, BlockPos)` iterates vanilla `EnchantmentTableBlock.BOOKSHELF_OFFSETS` (public in 1.21.1).
- [x] Returns a `StatCollection` record with fields: `float eterna, quanta, arcana, rectification; int clues; float maxEterna; Set<ResourceKey<Enchantment>> blacklist; boolean treasureAllowed;`.

**Subtasks:**
- [x] Define `StatCollection` record.
- [x] Skeleton scan; no LOS check yet.

**Tests:** Stub lookup returning `(1,1,0,0,0,0)` → 15 shelves placed → eterna summed to 15.

---

### Task T-2.2.2 — Transmitter line-of-sight check

**Acceptance:**
- [x] For each offset, check the midpoint block is in `BlockTags.ENCHANTMENT_POWER_TRANSMITTER`.
- [x] Failure → shelf contributes zero.

**Tests:** [x] One bookshelf blocked by a stone midpoint → that slot contributes zero; removing the stone restores the contribution.

---

### Task T-2.2.3 — Stat aggregation rules

**Acceptance:**
- [x] Sum `eterna`, `quanta`, `arcana`, `rectification`, `clues` across all contributing shelves.
- [x] `maxEterna = max(maxEterna_i)` across contributors.
- [x] Final `eterna` is clamped to `[0, maxEterna]`; other stats uncapped.
- [x] Clamp `clues` to `[0, 3]` (only three preview slots exist).

**Tests:**
- [x] Shelves whose eterna sum exceeds `maxEterna` → clamped.
- [x] Mixed shelves with different `maxEterna` → result uses the highest.
- [x] Shelf with `clues: 5` alone still clamps to 3.

---

### Task T-2.2.4 — Blacklist + treasure accumulation hooks

**Resume context:** DESIGN.md § "Shelf Blocks" — filtering shelf + treasure shelf. BE classes land in Epic 3; this task reserves the scan-side integration.

**Acceptance:**
- [x] If the scanned position has a `FilteringShelfBlockEntity`, its blacklist set is union'd into `blacklist`.
- [x] If a `TreasureShelfBlockEntity` exists in range, `treasureAllowed = true`.
- [x] No-op when the BE classes aren't registered yet (dynamic lookup, safe for pre-Epic-3 builds).

**Tests:** [x] Mock BE fixtures — verify the hooks are called exactly once per in-range BE.

---

## Story S-2.3 — S2C network payloads

- [x] Story complete

**As a** client, **I want** stat + clue data over custom payloads **so that** the enchanting screen can render live state.

### Task T-2.3.1 — `StatsPayload` + `CraftingResultEntry`

**Resume context:** DESIGN.md § "Payload Shapes (S2C custom payloads, 1.21.1)" — copy the record shape exactly.

**Acceptance:**
- [x] `net/StatsPayload.java` record per DESIGN — 5 floats, int clues, `List<ResourceKey<Enchantment>>` blacklist, boolean treasure, `Optional<CraftingResultEntry>` craftingResult.
- [x] `net/CraftingResultEntry.java` record of `ItemStack, int xpCost, ResourceLocation recipeId`.
- [x] `Type<StatsPayload> TYPE = new Type<>(FizzleEnchanting.id("stats"));`
- [x] `StreamCodec<RegistryFriendlyByteBuf, StatsPayload> CODEC` via `StreamCodec.composite`.

**Tests:** `PayloadCodecTest#statsPayloadRoundTrip` — zero-stat, mid-stat, and saturated variants all survive a `RegistryFriendlyByteBuf` round-trip (bootstrap registries in `@BeforeAll`).

---

### Task T-2.3.2 — `CluesPayload` + `EnchantmentClue`

**Acceptance:**
- [x] `net/CluesPayload.java` record: `int slot, List<EnchantmentClue> clues, boolean exhaustedList`.
- [x] `EnchantmentClue` record: `ResourceKey<Enchantment>, int level`.
- [x] Codec via composite.

**Tests:** Empty clue list, 3-entry list, exhausted flag both round-trip.

---

### Task T-2.3.3 — Register payloads + client stubs

**Acceptance:**
- [x] `PayloadTypeRegistry.playS2C().register(TYPE, CODEC)` in `FizzleEnchanting#onInitialize` for both payloads.
- [x] Client registration via `ClientPlayNetworking.registerGlobalReceiver(TYPE, handler)` in `FizzleEnchantingClient`.
- [x] Client handlers log receipt but defer screen updates to S-2.5.

**Tests:** Registry bootstrap test — after `onInitialize`, both types resolve from the payload registry.

---

## Story S-2.4 — Enchantment selection algorithm

- [x] Story complete

**As a** player, **I want** Eterna/Quanta/Arcana/Rectification/Clues to drive enchant rolls **so that** the system behaves like Apotheosis/Zenith.

### Task T-2.4.1 — `getEnchantmentCost` (Eterna → slot costs)

**Resume context:** `/home/rfizzle/Projects/Zenith/src/main/java/dev/shadowsoffire/apotheosis/ench/table/RealEnchantmentHelper.java`. Read; re-author against 1.21.1's dynamic registry.

**Acceptance:**
- [x] `RealEnchantmentHelper.getEnchantmentCost(RandomSource, int slot, float eterna, ItemStack)` — slot 0/1/2 returns cost derived from eterna per Zenith's formula, respecting `config.enchantingTable.maxEterna`.
- [x] Slot 2 cost ≥ slot 1 ≥ slot 0.

**Tests:**
- [x] Seeded RNG → deterministic outputs.
- [x] Monotonic slot ordering.
- [x] `eterna=50` → slot-2 cost in `[25, 50]`.

---

### Task T-2.4.2 — `selectEnchantment` (Quanta/Arcana/Rectification)

**Acceptance:**
- [x] `List<EnchantmentInstance> selectEnchantment(RandomSource, ItemStack, int level, float quanta, float arcana, float rectification, boolean treasureAllowed, Set<ResourceKey<Enchantment>> blacklist, RegistryAccess)`.
- [x] Candidate pool drawn from `registryAccess.registryOrThrow(Registries.ENCHANTMENT)` filtered by `supportedItems`, `#minecraft:in_enchanting_table`, `#minecraft:treasure` (gated by `treasureAllowed`).
- [x] Blacklist applied pre-selection.
- [x] Quanta widens the symmetric random power window around `level`.
- [x] Rectification subtracts from the **negative** half of the quanta window (does not reduce positive variance).
- [x] Arcana shifts the weight function toward rarer enchants.

**Tests:**
- [x] Blacklist filter → blacklisted key never appears over 1000 rolls.
- [x] Treasure flag true/false behavior.
- [x] Quanta widens stdev monotonically over 1000-roll samples.
- [x] Rectification → infinite value makes outcomes monotonic with eterna.

---

### Task T-2.4.3 — `buildClueList` (Clues → per-slot preview)

**Acceptance:**
- [x] Given the same seed that drove a slot's roll, the first clue is the **exact** enchant that slot rolls.
- [x] Remaining clues fill from the slot's candidate pool until exhausted.
- [x] Returns `exhaustedList=true` when the pool couldn't satisfy `cluesCount`.

**Tests:** [x] First-clue-matches-selection property over 100 seeds.

---

## Story S-2.5 — Menu + screen replacement

- [x] Story complete

**As a** player, **I want** the vanilla enchanting table to open Fizzle's menu **so that** stat-driven enchanting replaces vanilla.

### Task T-2.5.1 — `FizzleEnchantmentMenu` subclass

**Resume context:** DESIGN.md § "Table Menu Implementation".

**Acceptance:**
- [x] `enchanting/FizzleEnchantmentMenu.java extends EnchantmentMenu`.
- [x] Override `slotsChanged(Container)`: call `gatherStats`, recompute `costs[]`, populate `enchantClue[]`/`levelClue[]`, fire one `StatsPayload` + three `CluesPayload`.
- [x] Override `clickMenuButton(Player, int id)`: validate id 0/1/2, validate XP + lapis against `costs[id]`, apply enchant via `RealEnchantmentHelper.selectEnchantment`, consume XP + lapis, refire `slotsChanged`.
- [x] Throw `UnsupportedOperationException` for `id == 3` (wired in Epic 5).

**Tests:**
- [x] Successful enchant — item gains `ItemEnchantments`, XP and lapis decremented correctly.
- [x] Insufficient lapis → click rejected, no mutation.
- [x] Insufficient XP → click rejected.
- [x] `id == 3` throws until Epic 5.

---

### Task T-2.5.2 — `EnchantmentTableBlockMixin`

**Resume context:** DESIGN.md § "Table Menu Implementation" — exact mixin snippet included.

**Acceptance:**
- [x] `mixin/EnchantmentTableBlockMixin.java` with `@Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)`.
- [x] Sets return value to a `SimpleMenuProvider` producing `FizzleEnchantmentMenu`.
- [x] Mixin registered in `fizzle_enchanting.mixins.json`.

**Tests:** [x] Bootstrap + synthetic `BlockState` → provider returned is the fizzle one.

---

### Task T-2.5.3 — `EnchantmentMenuAccessor`

**Acceptance:**
- [x] `@Accessor` for `enchantSlots`, `random`, `enchantmentSeed`.
- [x] Method names prefixed `fizzleEnchanting$` per `/dev-companion`.

**Tests:** [x] Accessor returns non-null on a constructed vanilla `EnchantmentMenu` (used by the menu subclass in T-2.5.1).

---

### Task T-2.5.4 — `FizzleEnchantmentScreen` HUD

**Acceptance:**
- [x] `client/screen/FizzleEnchantmentScreen.java` extends `EnchantmentScreen`.
- [x] Renders a 1-row stat line below the three enchant slots: `E: 50  Q: 12  A: 5  R: 10  C: 2`.
- [x] Client-side `StatsPayload` handler stores the last received stats on the menu; screen reads those.
- [x] Toggle via `config.enchantingTable.showLevelIndicator`.

**Tests:** [x] Unit test on the stat-line formatter (no GL context required).

---

### Task T-2.5.5 — Register `MenuType<FizzleEnchantmentMenu>`

**Acceptance:**
- [x] `MenuType` registered in `FizzleEnchantingRegistry` (create the class now if it doesn't exist).
- [x] `HandledScreens.register` wires the screen on the client entrypoint.

**Tests:** Bootstrap assertion — menu type resolves from the registry.

---

# Epic 3 — Shelf Family

- [x] Epic complete

**Goal:** All 25 Zenith shelves + utility shelves + BE-backed specials.
**Commit at epic close:** `feat(enchanting): shelf family and stat scanner`

---

## Story S-3.1 — Shelf infrastructure

- [x] Story complete

### Task T-3.1.1 — Base `EnchantingShelfBlock` class

**Resume context:** DESIGN.md § "Shelf Blocks".

**Acceptance:**
- [x] `shelf/EnchantingShelfBlock.java extends Block implements IEnchantingStatProvider`.
- [x] Constructor takes `BlockBehaviour.Properties` + `ParticleTheme` enum.
- [x] `getStats` delegates to `EnchantingStatRegistry#lookup`.

**Tests:** [x] Registration round-trip; particle theme accessible.

---

### Task T-3.1.2 — Particle theme enum + `animateTick` hook

**Acceptance:**
- [x] `ParticleTheme { ENCHANT, ENCHANT_FIRE, ENCHANT_WATER, ENCHANT_END, ENCHANT_SCULK }`.
- [x] Client-side `animateTick` emits 1–3 particles at low probability, gated by `config.shelves.sculkParticleChance` for sculk theme.
- [x] Each enum resolves to its vanilla `ParticleType`.

**Tests:** [x] Parameterized test — each theme → expected `ParticleType`.

---

### Task T-3.1.3 — `FizzleEnchantingRegistry` block/item registration helpers

**Acceptance:**
- [x] Class `FizzleEnchantingRegistry.java` with:
  - `public static final Map<ResourceLocation, Block> BLOCKS = new LinkedHashMap<>();`
  - `registerBlock(String name, Block block, Item.Properties itemProps)` — registers block + BlockItem via `BuiltInRegistries`.
  - `registerItem(String name, Item)`, `registerMenuType`, `registerBlockEntityType`.
- [x] Called from `FizzleEnchanting#onInitialize`.

**Tests:** [x] Adding a dummy block via the helper → present in `BuiltInRegistries.BLOCK`.

---

## Story S-3.2 — Full Zenith shelf roster

- [x] Story complete

### Task T-3.2.1 — Register all 25 shelves

**Resume context:** DESIGN.md tables under § "Shelf Blocks".

**Acceptance:**
- [x] Every wood/stone/sculk/utility shelf from DESIGN registered with correct properties (sound group WOOD/STONE, correct strength).
- [x] IDs match Zenith 1:1.

**Subtasks:**
- [x] Iterate DESIGN tables; one registration per row.
- [x] Use `BlockBehaviour.Properties.of()` not `.copy(Blocks.BOOKSHELF)` (1.21.1 deprecation).

**Tests:** Parameterized — for each expected id, `BuiltInRegistries.BLOCK.get(id) != null`.

---

### Task T-3.2.2 — Port stat JSONs from Zenith

**Resume context:** `/home/rfizzle/Projects/Zenith/src/main/resources/data/zenith/enchanting_stats/*.json`.

**Acceptance:**
- [x] Every `.json` file in that dir copied to `src/main/resources/data/fizzle_enchanting/enchanting_stats/`.
- [x] Namespace rewrite: any `"zenith:..."` reference → `"fizzle_enchanting:..."`.

**Subtasks:**
- [x] `ls` Zenith source dir for canonical file list.
- [x] Copy + sed.

**Tests:** [x] Loader picks up every file, no parse errors.

---

### Task T-3.2.3 — Copy textures (dir rename)

**Resume context:** DESIGN.md § "Asset Sources (Zenith → Fizzle Enchanting)".

**Acceptance:**
- [x] All shelf textures from `/home/rfizzle/Projects/Zenith/src/main/resources/assets/zenith/textures/blocks/*.png` → `src/main/resources/assets/fizzle_enchanting/textures/block/` (note plural→singular).
- [x] `.mcmeta` files preserved (animated `blazing_hellshelf`).
- [x] Do not copy files under `blocks/reforging/`, `blocks/augmenting/` (cut per DESIGN).

**Tests:** [x] Glob check — expected filenames present; forbidden dirs absent.

---

### Task T-3.2.4 — Lang keys for all shelves

**Acceptance:**
- [x] `assets/fizzle_enchanting/lang/en_us.json` gets a `block.fizzle_enchanting.<id>` key per shelf (English from Zenith / invented where Zenith lacks).

**Tests:** Simple JSON parse + key-presence loop.

---

## Story S-3.3 — Utility shelves

- [x] Story complete

### Task T-3.3.1 — Sightshelf tiers

**Acceptance:**
- [x] `sightshelf` (+1 clues), `sightshelf_t2` (+2 clues) shelves register with `maxEterna: 0`.
- [x] Stat JSONs confirm they don't raise Eterna cap.

**Tests:** Stacking test — two `sightshelf_t2` in range → `clues = 4` (before T-2.2.3 clamp to 3).

---

### Task T-3.3.2 — Rectifier tiers

**Acceptance:**
- [x] `rectifier` / `rectifier_t2` / `rectifier_t3` register with `rectification: 10/15/20`.

**Tests:** [x] `rectifier_t3` in range → `rectification = 20`.

---

## Story S-3.4 — Datagen providers

- [x] Story complete

### Task T-3.4.1 — `FizzleModelProvider`

**Resume context:** DESIGN.md § "Datagen Strategy" → "Datagen" table row 1.

**Acceptance:**
- [x] `data/FizzleModelProvider extends FabricModelProvider`.
- [x] `generateBlockStateModels` — cube_column for every shelf (some uniform, some top/bottom/side).
- [x] `generateItemModels` — parented block items + generated item models for `infused_breath`, `warden_tendril`, `prismatic_web`, and the 3 tomes (Epic 5 ships item classes; the generator slot goes in now).

**Tests:** After `./gradlew runDatagen`, expected blockstates/models exist under `src/main/generated/`.

---

### Task T-3.4.2 — `FizzleBlockLootTableProvider`

**Acceptance:**
- [x] `dropSelf(block)` for every shelf, filtering shelf, treasure shelf, library, ender library.

**Tests:** [x] Generated loot tables contain one pool of the block itself per file.

---

### Task T-3.4.3 — `FizzleRecipeProvider` (vanilla-shape only)

**Resume context:** `/home/rfizzle/Projects/Zenith/src/main/resources/data/zenith/recipes/<shelf>.json` for each shelf's recipe.

**Acceptance:**
- [x] Shaped recipes for every shelf (copied from Zenith, namespace-rewritten).
- [x] Prismatic Web recipe (reserve the slot; body may be added in T-4.1.3).
- [x] Iron-block anvil-repair recipe shape is not a crafting table recipe — **do not** include here; Epic 4 handles via anvil handler.
- [x] **Custom** `fizzle_enchanting:enchanting` / `keep_nbt_enchanting` recipes stay hand-shipped (Epic 4).

**Tests:** [x] Generated recipe files parse and reference valid IDs.

---

### Task T-3.4.4 — Wire providers + commit generated output

**Acceptance:**
- [x] `FizzleEnchantingDataGenerator#onInitializeDataGenerator` registers all three providers.
- [x] `src/main/generated/` committed to git (Fabric default path).
- [x] `./gradlew runDatagen` is idempotent (no diff on second run).

**Tests:** [x] Run datagen twice; `git diff --exit-code src/main/generated/` is clean.

---

## Story S-3.5 — Filtering & treasure shelves

- [x] Story complete

### Task T-3.5.1 — `FilteringShelfBlockEntity` + block

**Resume context:** DESIGN.md § "Shelf Blocks" → filtering_shelf row.

**Acceptance:**
- [x] `shelf/FilteringShelfBlockEntity.java` stores up to 6 `ItemStack` of books.
- [x] On book insert, parse `ItemEnchantments`; expose `Set<ResourceKey<Enchantment>>` to the stat scanner.
- [x] Chiseled-bookshelf-style cursor-hit slot targeting (use vanilla `ChiseledBookShelfBlock` logic as reference).
- [x] Save/load via NBT; client sync via `BlockEntity#getUpdatePacket`/`getUpdateTag`.
- [x] Empty shelf contributes as a wood-tier base shelf (DESIGN fallback).
- [x] Full shelf rejects additional inserts.

**Tests:** `FilteringShelfTest` —
- [x] Insert enchanted book → blacklist grows.
- [x] Extract → blacklist shrinks.
- [x] NBT round-trip.
- [x] Slot targeting produces the correct slot index for each corner hit.

---

### Task T-3.5.2 — `TreasureShelfBlockEntity` + block

**Acceptance:**
- [x] BE carries no storage; presence alone flips `treasureAllowed = true` in the stat scan.
- [x] Block uses a distinct texture + item.
- [x] Zero Eterna contribution.

**Tests:** `TreasureShelfTest` — presence in range → `StatCollection.treasureAllowed=true`; absence → `false`.

---

### Task T-3.5.3 — Wire BE hooks into `StatCollection`

**Acceptance:**
- [x] `T-2.2.4`'s placeholder now resolves to real BE lookups.
- [x] Union of blacklists across multiple filtering shelves works.
- [x] Treasure allowed once any in-range treasure shelf exists.

**Tests:** [x] Two filtering shelves each holding 1 unique book → blacklist has 2 entries.

---

# Epic 4 — Anvil & Library

- [x] Epic complete

**Goal:** Prismatic Web, iron-block anvil repair, 2-tier library with hopper I/O, custom recipe types registered.
**Commit at epic close:** `feat(enchanting): anvil tweaks and enchantment library`

---

## Story S-4.1 — Anvil dispatcher

- [x] Story complete

### Task T-4.1.1 — `AnvilMenuMixin`

**Resume context:** DESIGN.md § "Anvil tweaks (MVP)" — single mixin on `AnvilMenu#createResult`.

**Acceptance:**
- [x] `mixin/AnvilMenuMixin.java` with `@Inject(method = "createResult", at = @At("TAIL"))`.
- [x] Calls `AnvilDispatcher.handle(menu, slotA, slotB, accessor.getCost())`.
- [x] If dispatcher returns a `Result`, overwrite the output slot + cost accessor via an `@Accessor` mixin on `AnvilMenu`.

**Subtasks:**
- [x] Add `AnvilMenuAccessor` for `cost`, `repairItemCountCost`.
- [x] Register mixin.

**Tests:** [x] Stub dispatcher returning a canned result → anvil output slot receives it.

---

### Task T-4.1.2 — `AnvilDispatcher` + handler interface

**Acceptance:**
- [x] `anvil/AnvilDispatcher.java` holds an ordered `List<AnvilHandler>`.
- [x] `anvil/AnvilHandler.java` interface: `Optional<AnvilResult> handle(ItemStack left, ItemStack right, Player player)`.
- [x] First non-empty result wins.
- [x] `AnvilResult` record: `ItemStack output, int xpCost, int rightConsumed`.

**Tests:** [x] Two stub handlers; first fires — second never consulted. First returns empty — second fires.

---

### Task T-4.1.3 — `PrismaticWebItem` + recipe + texture

**Acceptance:**
- [x] `anvil/PrismaticWebItem.java` (plain `Item`).
- [x] Registered via `FizzleEnchantingRegistry.registerItem`.
- [x] Texture `assets/fizzle_enchanting/textures/item/prismatic_web.png` copied from Zenith.
- [x] Recipe JSON hand-shipped at `data/fizzle_enchanting/recipe/prismatic_web.json` (ported from Zenith).
- [x] Lang key.

**Tests:** [x] Item resolves; recipe parses.

---

### Task T-4.1.4 — `PrismaticWebHandler`

**Acceptance:**
- [x] Left = item with any `#minecraft:curse` enchant.
- [x] Right = `PrismaticWebItem` (count ≥ 1).
- [x] Output = item with all curses stripped; non-curse enchantments preserved.
- [x] XP cost = `config.anvil.prismaticWebLevelCost`.
- [x] Consumes 1 web.
- [x] Gate on `config.anvil.prismaticWebRemovesCurses`.

**Tests:** `PrismaticWebHandlerTest` —
- [x] Curse-of-Vanishing + Sharpness-3 → output has Sharpness 3, no curse.
- [x] No curses → handler declines.
- [x] Non-web in right → declines.
- [x] Config off → declines.

---

## Story S-4.2 — Iron-block anvil repair

- [x] Story complete

### Task T-4.2.1 — `IronBlockAnvilRepairHandler`

**Resume context:** DESIGN.md § "Anvil tweaks (MVP)" — item #2.

**Acceptance:**
- [x] Left = `CHIPPED_ANVIL` or `DAMAGED_ANVIL` BlockItem; right = `IRON_BLOCK`.
- [x] Output = one tier better (damaged→chipped, chipped→normal).
- [x] Preserves any `ItemEnchantments` component on the anvil stack (rare but supported).
- [x] Flat XP cost: 1 level.
- [x] Consumes 1 iron block.

**Tests:** `IronBlockAnvilRepairTest` —
- [x] Damaged → chipped.
- [x] Chipped → normal.
- [x] Normal → declines.
- [x] Iron **ingot** in right → declines.
- [x] Enchantments preserved.

---

### Task T-4.2.2 — Config gate

**Acceptance:**
- [x] Handler returns empty when `config.anvil.ironBlockRepairsAnvil` is `false`.

**Tests:** Flag off → declines.

---

## Story S-4.3 — Library storage engine

- [x] Story complete

### Task T-4.3.1 — Abstract `EnchantmentLibraryBlockEntity`

**Resume context:** DESIGN.md § "Enchantment Library" — "Two state maps per block entity."

**Acceptance:**
- [x] `library/EnchantmentLibraryBlockEntity.java` abstract.
- [x] Fields: `Object2IntMap<ResourceKey<Enchantment>> points, maxLevels; final int maxLevel, maxPoints;`
- [x] `depositBook(ItemStack book)`, `canExtract(key, target, curLvl)`, `extract(key, target, curLvl)` methods.

**Tests:** [x] Unit test on core methods using a subclass fixture.

---

### Task T-4.3.2 — `BasicLibraryBlockEntity` + `EnderLibraryBlockEntity`

**Acceptance:**
- [x] Basic: `maxLevel=16`, `maxPoints=32_768`.
- [x] Ender: `maxLevel=31`, `maxPoints=1_073_741_824`.
- [x] DESIGN caveat: these are **code constants**, not config. Do not expose them via config.

**Tests:** [x] Construction test.

---

### Task T-4.3.3 — Point math helpers

**Resume context:** DESIGN.md § "Enchantment Library" — `points(level) = 2^(level−1)`.

**Acceptance:**
- [x] `static int points(int level)` — `level<=0 → 0`; else `1 << (level - 1)`.
- [x] `static int maxLevelAffordable(int points, int curLvl)` — returns `1 + log2(points + points(curLvl))`.

**Tests:** Parameterized —
- [x] `points(1)=1, points(5)=16, points(16)=32768, points(31)=1_073_741_824`.
- [x] Shift-click helper matches DESIGN formula.

---

### Task T-4.3.4 — NBT schema + resolution-safe load

**Resume context:** DESIGN.md § "Enchantment Library" — "NBT schema (two sibling compound tags)".

**Acceptance:**
- [x] `saveAdditional(CompoundTag, RegistryAccess)` writes `Points` + `Levels` compound tags keyed by `ResourceLocation.toString()`.
- [x] `load(CompoundTag, RegistryAccess)` lazy-resolves keys against `RegistryAccess.registryOrThrow(Registries.ENCHANTMENT)`; unresolved keys dropped with `LOGGER.warn`.
- [x] No schema version field in MVP.

**Tests:**
- [x] Round-trip save/load preserves maps.
- [x] NBT with an unknown key → survives load, key dropped, remainder intact.

---

### Task T-4.3.5 — Client sync packet

**Acceptance:**
- [x] `BlockEntity#getUpdatePacket` returns `ClientboundBlockEntityDataPacket.create(this)`.
- [x] `getUpdateTag` serializes both maps.
- [x] Full resend on any mutation (incremental sync deferred).

**Tests:** [x] Simulate mutation → server-side update tag contains the mutation; client-side BE reconstructed from tag equals server BE.

---

## Story S-4.4 — Library block + UI

- [x] Story complete

### Task T-4.4.1 — `EnchantmentLibraryBlock` (both tiers)

**Acceptance:**
- [x] `library/EnchantmentLibraryBlock.java extends BaseEntityBlock`.
- [x] Two block instances (basic + ender) each tying to their BE subclass.
- [x] Registered in `FizzleEnchantingRegistry.BLOCKS`.
- [x] Vanilla-shape recipe for basic library hand-shipped under `data/fizzle_enchanting/recipe/library.json` (copy from Zenith).

**Tests:** Registration round-trip for both blocks.

---

### Task T-4.4.2 — `EnchantmentLibraryMenu`

**Resume context:** DESIGN.md § "Enchantment Library" — "GUI — three slots".

**Acceptance:**
- [x] `library/EnchantmentLibraryMenu.java extends AbstractContainerMenu`.
- [x] Three IO slots: 0 deposit (auto-absorb on change), 1 extract target, 2 scratch; plus player inventory.
- [x] `clickMenuButton(Player, int id)` — `id = (shift << 31) | enchantIndex` per DESIGN.
- [x] Server-side handles deposit → `depositBook`; extract → `extract`; shift-click uses `maxLevelAffordable`.

**Tests:** `EnchantmentLibraryMenuTest` —
- [x] Deposit: book put in slot 0 absorbs on `setChanged`; slot cleared; `points` updated.
- [x] Extract at maxLevels=1 but sufficient points → denied.
- [x] Extract with matching maxLevels + sufficient points → slot 1 gets the upgraded book.
- [x] Shift extract solves the max-affordable formula.

---

### Task T-4.4.3 — `EnchantmentLibraryScreen`

**Acceptance:**
- [x] Client screen lists every enchant with `points > 0`.
- [x] Per-enchant row: enchant name, current `maxLevels[e]` badge, total `points[e]`.
- [x] Button-click sends packed `id` per DESIGN.
- [x] Scrolls via vanilla `ScrollableContainer` pattern.

**Tests:** Unit-testable slice — the row formatter produces the expected string for `{Sharpness, maxLevels=5, points=6144}`.

---

### Task T-4.4.4 — Listener set on BE

**Acceptance:**
- [x] BE holds `Set<EnchantmentLibraryMenu>` of open screens.
- [x] Mutations call `onChanged()` on each listener.
- [x] Menu `removed()` de-registers.

**Tests:** Open menu, mutate BE externally, close menu → listener list goes from [menu] → [] without leaks.

---

## Story S-4.5 — Hopper integration

- [x] Story complete

### Task T-4.5.1 — `Storage<ItemVariant>` adapter

**Resume context:** DESIGN.md § "Enchantment Library" — "`Storage<ItemVariant>` adapter (hopper I/O)".

**Acceptance:**
- [x] `ItemStorage.SIDED.registerForBlockEntity((be, side) -> be.storageAdapter)` in `FizzleEnchantingRegistry`.
- [x] Adapter implements `Storage<ItemVariant>`.
- [x] `canInsert`: accepts only `ItemVariant.of(Items.ENCHANTED_BOOK)`.
- [x] `insert`: calls `depositBook` per unit, void-caps at `maxPoints`; returns full input amount even when overflow occurs.
- [x] `extract`: returns 0 unconditionally.

**Tests:** `LibraryStorageTest` —
- [x] Diamond sword insert → 0 accepted.
- [x] Book insert at cap → still returns full amount accepted (void overflow).
- [x] Extract → always 0.

---

### Task T-4.5.2 — `SnapshotParticipant<LibrarySnapshot>`

**Acceptance:**
- [x] Adapter extends `SnapshotParticipant<LibrarySnapshot>`.
- [x] `LibrarySnapshot` record: `points` map copy, `maxLevels` map copy, `dirty` flag.
- [x] `createSnapshot`/`readSnapshot` round-trip map state correctly.
- [x] `onFinalCommit` calls `setChanged()` once.

**Tests:** [x] Simulate a transaction: begin → insert → abort → state equals pre-insert. Begin → insert → commit → state mutated.

---

### Task T-4.5.3 — Rate limit

**Acceptance:**
- [x] BE tracks `long lastInsertTick`.
- [x] When `config.library.ioRateLimitTicks > 0` and `level.getGameTime() - lastInsertTick < rateLimit`, insert drops.

**Tests:** [x] Two rapid inserts at `rateLimit=20` → second dropped.

---

## Story S-4.6 — Custom recipe types

- [x] Story complete

### Task T-4.6.1 — `EnchantingRecipe` type + serializer

**Resume context:** DESIGN.md § "Enchantment-Table Crafting".

**Acceptance:**
- [x] `enchanting/recipe/EnchantingRecipe.java implements Recipe<SingleRecipeInput>`.
- [x] Fields: `Ingredient input, StatRequirements requirements, StatRequirements maxRequirements, ItemStack result, OptionalInt displayLevel, int xpCost`.
- [x] `StatRequirements` record: `float eterna, quanta, arcana`.
- [x] MAP_CODEC + stream codec.
- [x] `RECIPE_TYPE` registered under `BuiltInRegistries.RECIPE_TYPE`.

**Tests:** Codec round-trip; `matches(input, stats)` logic.

---

### Task T-4.6.2 — `KeepNbtEnchantingRecipe` type

**Acceptance:**
- [x] `enchanting/recipe/KeepNbtEnchantingRecipe.java` extends or implements `Recipe<SingleRecipeInput>` with identical fields.
- [x] `assemble(input, registryAccess)` preserves `ItemEnchantments` component from input onto the result.

**Tests:** Input with Sharpness-5 → result also has Sharpness-5.

---

### Task T-4.6.3 — `EnchantingRecipeRegistry.findMatch`

**Acceptance:**
- [x] Static `findMatch(Level, ItemStack, StatCollection)` scans both recipe types via `RecipeManager`.
- [x] Returns `Optional<RecipeHolder<? extends Recipe<SingleRecipeInput>>>`.
- [x] Matches when input fits `Ingredient` + stats are within `[requirements, maxRequirements]` per axis (`-1` = no max).

**Tests:** Construct recipes with varying bounds → assert expected hits/misses.

---

### Task T-4.6.4 — Ship 7 hand-written recipe JSONs

**Resume context:** DESIGN.md § "Enchantment-Table Crafting" table — "Recipes shipped in MVP (values copied from Zenith)". Source: `/home/rfizzle/Projects/Zenith/src/main/resources/data/zenith/recipes/enchanting/*.json`.

**Acceptance:** Files under `data/fizzle_enchanting/recipe/enchanting/`:
- [x] `infused_breath.json` (`enchanting`)
- [x] `infused_hellshelf.json` (`enchanting`)
- [x] `infused_seashelf.json` (`enchanting`)
- [x] `deepshelf.json` (`enchanting`)
- [x] `improved_scrap_tome.json` (`enchanting`)
- [x] `extraction_tome.json` (`enchanting`)
- [x] `ender_library.json` (`keep_nbt_enchanting`)
- [x] Values match Zenith exactly (E/Q/A bounds).

**Tests:** Each file parses into the correct subtype; `findMatch` locates the right recipe for synthetic inputs.

---

# Epic 5 — Tomes & Table Crafting UX

- [x] Epic complete

**Goal:** 3 tome items wired through the anvil, crafting-result row live on the table, Warden loot modifier shipping tendrils.
**Commit at epic close:** `feat(enchanting): tomes and table crafting`

---

## Story S-5.1 — Tome items

- [x] Story complete

### Task T-5.1.1 — Three tome item classes

**Acceptance:**
- [x] `tome/ScrapTomeItem.java`, `ImprovedScrapTomeItem.java`, `ExtractionTomeItem.java`.
- [x] All `stackSize=1`, no durability (Extraction's anvil-fuel-slot repair is a handler-side concern, not an item-data one).
- [x] Registered.

**Tests:** Resolution + `getMaxStackSize()==1`.

---

### Task T-5.1.2 — Textures, lang, item models

**Resume context:** `/home/rfizzle/Projects/Zenith/src/main/resources/assets/zenith/textures/items/tomes/` — **ignore** the 9 typed-tome PNGs.

**Acceptance:**
- [x] Copy `scrap_tome.png`, `improved_scrap_tome.png`, `extraction_tome.png` to `assets/fizzle_enchanting/textures/item/tome/`.
- [x] Three lang keys.
- [x] Item model JSONs hand-shipped (generated-item-model parents).

**Tests:** Model files resolve to the right texture paths.

---

### Task T-5.1.3 — Scrap tome vanilla-shape recipe

**Resume context:** `/home/rfizzle/Projects/Zenith/src/main/resources/data/zenith/recipes/scrap_tome.json`.

**Acceptance:**
- [x] Hand-shipped at `data/fizzle_enchanting/recipe/scrap_tome.json`.
- [x] Improved Scrap + Extraction come from custom recipes already shipped in T-4.6.4.

**Tests:** Parses.

---

## Story S-5.2 — Tome anvil handlers

- [x] Story complete

### Task T-5.2.1 — `ScrapTomeHandler`

**Resume context:** DESIGN.md § "Tome items".

**Acceptance:**
- [x] Left = enchanted item, right = scrap tome.
- [x] Output = enchanted book with **one random** enchantment (seeded by world random for determinism).
- [x] Left item destroyed, tome consumed.
- [x] XP cost = `config.tomes.scrapTomeXpCost`.
- [x] Declines on unenchanted left.

**Tests:** `ScrapTomeHandlerTest` — seeded RNG picks expected enchant; unenchanted input declines.

---

### Task T-5.2.2 — `ImprovedScrapTomeHandler`

**Acceptance:**
- [x] Same as scrap, but output book carries **all** enchantments.
- [x] XP cost = `config.tomes.improvedScrapTomeXpCost`.

**Tests:** [x] 3-enchant input → output book has all 3.

---

### Task T-5.2.3 — `ExtractionTomeHandler`

**Acceptance:**
- [x] Output book = all enchants; left item preserved, unenchanted, damaged by `config.tomes.extractionTomeItemDamage` (clamped so durability remains ≥ 1).
- [x] XP cost = `config.tomes.extractionTomeXpCost`.

**Tests:** `ExtractionTomeHandlerTest` —
- [x] 3 enchants → output book has 3; sword survives unenchanted with damage applied.
- [x] Durability clamped — sword with 1 durability stays at 1 after handler.

---

### Task T-5.2.4 — Extraction Tome anvil-fuel-slot repair

**Resume context:** DESIGN.md § "Tome items" — "The Extraction Tome also exposes an item-repair side-path using the anvil fuel slot (Zenith behavior preserved)."

**Acceptance:**
- [x] When an Extraction Tome is in the anvil fuel slot (materials-B slot) and a damaged item is in slot A (no right-hand item), consuming the tome restores `config.tomes.extractionTomeRepairPercent * maxDurability` durability.
- [x] XP cost identical to standard Extraction.

**Tests:** [x] Damaged sword + tome → durability increases by `repairPercent * maxDurability`; tome consumed.

---

## Story S-5.3 — Table crafting-result row

- [x] Story complete

### Task T-5.3.1 — Hook `findMatch` into `slotsChanged`

**Acceptance:**
- [x] In `FizzleEnchantmentMenu#slotsChanged`, after `gatherStats`, call `EnchantingRecipeRegistry.findMatch(level, inputStack, stats)`.
- [x] Store the result on the menu (`Optional<RecipeHolder<...>> currentRecipe`).

**Tests:** [x] Scripted shelves + `library` input → `currentRecipe.isPresent()`.

---

### Task T-5.3.2 — Serialize `CraftingResultEntry` on `StatsPayload`

**Acceptance:**
- [x] When `currentRecipe` is present, populate `craftingResult` on the outgoing `StatsPayload` with `(result, xpCost, recipeId)`.
- [x] When absent, `Optional.empty()`.

**Tests:** Round-trip: set recipe → payload carries it; unset → payload empty.

---

### Task T-5.3.3 — Button-id=3 server handler

**Acceptance:**
- [x] `FizzleEnchantmentMenu#clickMenuButton` for `id==3`:
  - Require `currentRecipe.isPresent()`.
  - Validate `player.experienceLevel >= xpCost`.
  - For `enchanting`: input stack decremented, result inserted.
  - For `keep_nbt_enchanting`: preserve input's `ItemEnchantments` on the output.
  - Consume XP; refire `slotsChanged`.

**Tests:** `CraftingResultFlowTest` —
- [x] `keep_nbt_enchanting` path preserves stored-book NBT on the library → ender library upgrade.
- [x] Insufficient XP → no-op.
- [x] No recipe → no-op (id==3 safely ignored).

---

### Task T-5.3.4 — Client screen crafting-row render

**Acceptance:**
- [x] `FizzleEnchantmentScreen` renders a 4th row below the enchant slots when `menu.craftingResult().isPresent()`.
- [x] Row shows the result item, XP cost badge, recipe-id hint (for EMI/JEI).
- [x] Click dispatches vanilla `ServerboundContainerButtonClickPacket` with `buttonId=3`.

**Tests:** Formatter unit test — given `CraftingResultEntry(ItemStack(ender_library), 20, ...)`, label reads `"Ender Library — 20 levels"`.

---

## Story S-5.4 — Specialty materials

- [x] Story complete

### Task T-5.4.1 — `infused_breath` item + recipe

**Acceptance:**
- [x] Item registered.
- [x] Texture from Zenith (animated — preserve `.mcmeta`).
- [x] Lang key.
- [x] Obtainable only via the `fizzle_enchanting:enchanting` recipe already shipped in T-4.6.4.

**Tests:** [x] Item registered; recipe resolved.

---

### Task T-5.4.2 — `warden_tendril` item

**Acceptance:**
- [x] Item registered.
- [x] Texture from Zenith.
- [x] Lang key.

**Tests:** [x] Registered.

---

### Task T-5.4.3 — `WardenLootHandler`

**Resume context:** `/dev-companion` § "Events" — `LootTableEvents.MODIFY`.

**Acceptance:**
- [x] `event/WardenLootHandler.java` subscribes to `LootTableEvents.MODIFY` for id `entities/warden`.
- [x] Adds:
  - Pool A: 1 guaranteed `warden_tendril` (weight 1, `UniformGenerator.between(1,1)`), gated by `LootItemRandomChanceCondition(config.warden.tendrilDropChance)`.
  - Pool B: 1 extra `warden_tendril` gated by `LootItemRandomChanceWithLootingCondition(0.0, config.warden.tendrilLootingBonus)`.
- [x] Registered in `FizzleEnchanting#onInitialize`.

**Tests:** `WardenLootHandlerTest` —
- [x] After modification the table has 2 pools referencing `warden_tendril`.
- [x] `dropChance=1.0, looting=0` → both pools roll, expect 2 tendrils.
- [x] `dropChance=0.0, looting=3` → expected count bounded in `[0, 2]` over 1000 sims.

---

### Task T-5.4.4 — Config reload re-reads drop chances

**Acceptance:**
- [x] Handler reads `config.warden.*` at **roll time**, not cached at registration.
- [x] `/fizzleenchanting reload` after mutating config → subsequent kills reflect the new values.

**Tests:** [x] Mutate config in-memory → re-simulate roll → expectations shift accordingly.

---

# Epic 6 — Enchantment Roster

- [x] Epic complete

**Goal:** 51 enchantments (49 NeoEnchant+ ports + 2 authored), exclusive-set tags, foreign-enchant overrides.
**Commit at epic close:** `feat(enchanting): MVP enchantment roster`

---

## Story S-6.1 — NeoEnchant+ port (49 files)

- [x] Story complete

### Task T-6.1.1 — Acquire NeoEnchant+ v5.14.0 JSONs

**Resume context:** DESIGN.md § "Enchantment Implementation — 1.21.1 approach". License note in DESIGN.md § "Build & Ship" (CC BY-NC-SA).

**Acceptance:**
- [x] NeoEnchant+ v5.14.0 jar acquired (CurseForge id 1135663).
- [x] `data/enchantplus/enchantment/**` extracted to a scratch dir.
- [x] If the jar can't be accessed in the current environment, **stop**, document the blocker, and do not fabricate JSONs.

**Tests:** [x] File list matches the expected NeoEnchant+ v5.14.0 manifest (56 enchant JSONs).

---

### Task T-6.1.2 — Namespace rewrite + cut list

**Resume context:** DESIGN.md § "Explicitly cut from NeoEnchant+" — 7 files.

**Acceptance:**
- [x] Copy all except these 7: `axe/timber`, `pickaxe/bedrock_breaker`, `pickaxe/spawner_touch`, `tools/auto_smelt`, `helmet/auto_feed`, `chestplate/magnet`, `sword/runic_despair`.
- [x] Copied files land under `src/main/resources/data/fizzle_enchanting/enchantment/` preserving slot subdirs.
- [x] Sed across each file: `"enchantplus:` → `"fizzle_enchanting:` and `#enchantplus:` → `#fizzle_enchanting:`.

**Tests:** `PortedEnchantmentsTest` —
- [x] Exactly 49 `.json` files present under the target root.
- [x] No file contains the literal `enchantplus:`.
- [x] Each file parses via `Enchantment.CODEC` in a bootstrapped test.
- [x] None of the 7 cut file names are present.

---

### Task T-6.1.3 — Exclusive-set tag port

**Acceptance:**
- [x] Copy `data/enchantplus/tags/enchantment/exclusive_set/*.json` → `data/fizzle_enchanting/tags/enchantment/exclusive_set/` with same namespace rewrite.
- [x] Entries referencing the 7 cut enchants are removed (not orphaned).

**Tests:** [x] Every tag entry resolves to a present enchant key.

---

### Task T-6.1.4 — Lang key merge

**Acceptance:**
- [x] Extract `enchantment.enchantplus.*` from NeoEnchant+'s `assets/minecraft/lang/en_us.json`.
- [x] Rename to `enchantment.fizzle_enchanting.*`, merge into `assets/fizzle_enchanting/lang/en_us.json`.
- [x] Drop keys for the 7 cut enchants.
- [x] Port note added to `companions/fizzle-enchanting/docs/NEOENCHANT_PORT.md` (1-para attribution + cuts list + license pointer).

**Tests:** [x] Lang file parses; every present enchant JSON has a matching lang key.

---

## Story S-6.2 — Authored enchants

- [x] Story complete

### Task T-6.2.1 — `icy_thorns.json`

**Resume context:** DESIGN.md § "MVP Enchantments (51)" → "From Zenith" row 1. Reference Zenith Java for balance: `/home/rfizzle/Projects/Zenith/src/main/java/dev/shadowsoffire/apotheosis/ench/enchantments/`.

**Acceptance:**
- [x] `src/main/resources/data/fizzle_enchanting/enchantment/icy_thorns.json`.
- [x] Level range, weight, cost: mirror Zenith's values.
- [x] `supported_items`: `#minecraft:enchantable/chest_armor`.
- [x] Effect: `minecraft:post_attack` with `affected: "attacker"`, `enchanted: "victim"`, applying Slowness via `minecraft:apply_mob_effect`.
- [x] Lang key added.

**Tests:** Parses via `Enchantment.CODEC`; targets chest armor only.

---

### Task T-6.2.2 — `shield_bash.json` + tag expansion

**Acceptance:**
- [x] Tag file `data/minecraft/tags/item/enchantable/weapon.json` with `replace: false` and values `["minecraft:shield"]` — this adds shields to the vanilla weapon tag.
- [x] Enchant JSON at `data/fizzle_enchanting/enchantment/shield_bash.json`:
  - `supported_items: "#minecraft:enchantable/weapon"`.
  - `minecraft:damage` effect (additive damage boost).
  - `minecraft:post_attack` applying `minecraft:damage_item` for durability cost.
- [x] Lang key added.

**Tests:** Parses; tag expansion includes `minecraft:shield`.

---

## Story S-6.3 — Foreign enchant overrides

- [x] Story complete

### Task T-6.3.1 — `mending.json` override

**Resume context:** DESIGN.md § "Foreign enchantment support".

**Acceptance:**
- [x] `src/main/resources/data/minecraft/enchantment/mending.json` — identical to vanilla plus raised weight (pick e.g. `weight: 2`).
- [x] `#minecraft:treasure` tag membership unchanged (so `treasure_shelf` still gates it).

**Tests:** Parses; after server boot, `BuiltInRegistries.ENCHANTMENT` `minecraft:mending` entry has the bundled weight.

---

### Task T-6.3.2 — `soulbound.json` override

**Acceptance:**
- [x] `src/main/resources/data/yigd/enchantment/soulbound.json` — non-zero weight, `supported_items: "#yigd:soulbindable"`.
- [x] File is syntactically valid even if `yigd` isn't in the test classpath.

**Tests:** JSON parses; when yigd is present, registry reflects the override.

---

### Task T-6.3.3 — Config-gated repository source

**Acceptance:**
- [x] `config.foreignEnchantments.applyBundledOverrides` wired: when `true`, override files load normally (they're just resources in the jar).
- [x] When `false`, register a higher-priority resource-pack source that restores upstream values (or alternatively skip copying — pick the cleaner approach; document in `companions/fizzle-enchanting/docs/FOREIGN_ENCHANT_OVERRIDES.md`).

**Tests:** Flag true/false → registry-weight observation matches expected.

---

# Epic 7 — Integrations

- [ ] Epic complete

**Goal:** EMI, REI, JEI, Jade adapters — optional dependencies, entry-point gated.
**Commit at epic close:** `feat(enchanting): EMI/REI/JEI/Jade integrations`

---

## Story S-7.1 — EMI adapter

- [ ] Story complete

### Task T-7.1.1 — EMI dependency wiring

**Acceptance:**
- [ ] `modCompileOnly "dev.emi:emi-fabric:<matched-version>"` in `build.gradle`.
- [ ] `modRuntimeOnly` only for local dev (via a runClient-scoped configuration).
- [ ] `fabric.mod.json` entry `"emi": ["com.fizzlesmp.fizzle_enchanting.compat.emi.EmiEnchantingPlugin"]`.

**Tests:** `./gradlew build` succeeds with and without EMI in the dev runtime.

---

### Task T-7.1.2 — Plugin skeleton + display extractor

**Acceptance:**
- [ ] `compat/emi/EmiEnchantingPlugin.java implements EmiPlugin`.
- [ ] Registers categories "Fizzle Enchanting — Shelves", "Fizzle Enchanting — Tomes".
- [ ] Extractor: `List<TableCraftingDisplay>` from the registered `fizzle_enchanting:enchanting` + `keep_nbt_enchanting` recipes.

**Tests:** Unit test the extractor (no EMI runtime needed).

---

### Task T-7.1.3 — Recipe rendering

**Acceptance:**
- [ ] Each recipe shows input, output, stat requirements, XP cost.
- [ ] Shelf info panel shows the block's stat contribution.

**Tests:** Manual smoke test documented in story PR.

---

## Story S-7.2 — REI adapter

- [ ] Story complete

### Task T-7.2.1 — REI dependency + entry point

**Acceptance:** Same pattern as EMI, for REI.

---

### Task T-7.2.2 — Mirror EMI extractor through a shared source of truth

**Acceptance:**
- [ ] Shared `compat/common/TableCraftingDisplay.java` record.
- [ ] REI plugin adapts `TableCraftingDisplay` → REI's display type.

**Tests:** Extractor shared with EMI — test it once, both integrations benefit.

---

## Story S-7.3 — JEI adapter

- [ ] Story complete

### Task T-7.3.1 — JEI plugin

**Acceptance:** `compat/jei/JeiEnchantingPlugin.java implements IModPlugin`, entry-point gated.

**Tests:** JEI runtime smoke test.

---

## Story S-7.4 — Jade probe tooltips

- [ ] Story complete

### Task T-7.4.1 — Jade plugin

**Acceptance:**
- [ ] `compat/jade/JadeEnchantingPlugin.java implements IWailaPlugin`.
- [ ] Providers:
  - Enchanting table: reports 5 stats computed via `EnchantingStatRegistry.gatherStats`.
  - Library BE: shows "Basic Library — N enchants stored"; detailed per-enchant points only inside the library UI (not in world).

**Tests:** Unit-test the string builders.

---

# Epic 8 — Polish & Release

- [ ] Epic complete

**Goal:** Advancements, tooltips, docs, changelog, plugin-list entry.
**Commit at epic close:** `feat(enchanting): MVP release`

---

## Story S-8.1 — Advancement tree

- [ ] Story complete

### Task T-8.1.1 — Hand-shipped advancement JSONs

**Acceptance:** Files under `data/fizzle_enchanting/advancement/`:
- [ ] `root` — inventory any fizzle shelf.
- [ ] `stone_tier` — craft a `hellshelf`/`seashelf`/`dormant_deepshelf`.
- [ ] `tier_three` — craft an `infused_hellshelf`/`infused_seashelf`/`deepshelf`.
- [ ] `library` — craft a `library`.
- [ ] `ender_library` — upgrade to `ender_library`.
- [ ] `tome_apprentice` — craft a `scrap_tome`.
- [ ] `tome_master` — craft an `extraction_tome`.
- [ ] `warden_tendril` — obtain one.
- [ ] `infused_breath` — obtain one.
- [ ] `apotheosis` — hit Eterna 50 at a single table (custom trigger or repurposed `minecraft:enchanted_item`).

**Tests:** Each file parses via `Advancement.CODEC` in a bootstrapped test.

---

### Task T-8.1.2 — Lang titles + descriptions

**Acceptance:** Every advancement has a title + description lang key.

**Tests:** Key-presence sweep.

---

## Story S-8.2 — Tooltips + overleveled coloring

- [ ] Story complete

### Task T-8.2.1 — `ItemTooltipCallback` listener

**Acceptance:**
- [ ] Client-side listener recolors enchant lines where `level > vanillaMax(enchantment)` using `config.display.overLeveledColor`.
- [ ] Vanilla caps hardcoded for MVP (`Map<ResourceKey<Enchantment>, Integer>`) — Iteration 1 feeds this from config.

**Tests:** `TooltipFormatterTest` — Sharpness 7 is recolored; Sharpness 5 is vanilla; invalid config hex → fallback `#FF6600` already enforced by T-1.3.3.

---

### Task T-8.2.2 — Book tooltip toggle

**Acceptance:** When `config.display.showBookTooltips==false`, stored-book per-level tooltip lines are suppressed.

**Tests:** Flag true → lines present; false → empty list.

---

## Story S-8.3 — Operator docs

- [ ] Story complete

### Task T-8.3.1 — `README.md`

**Acceptance:**
- [ ] `companions/fizzle-enchanting/README.md` — 1-pager: features, config path, DESIGN.md link, credits (Apotheosis/Shadows_of_Fire, Zenith/bageldotjpg, NeoEnchant+/Hardel).

**Tests:** n/a (docs).

---

### Task T-8.3.2 — `docs/CONFIG.md`

**Acceptance:** Annotated reference for every config field with examples.

---

### Task T-8.3.3 — `docs/PLAYTHROUGH.md`

**Acceptance:** Manual QA checklist — craft every shelf tier, reach Eterna 50, upgrade library, test all 3 tomes, kill Warden, craft `infused_breath`, verify Mending + Soulbound roll.

---

## Story S-8.4 — Release prep

- [ ] Story complete

### Task T-8.4.1 — Final test + build sweep

**Acceptance:**
- [ ] `./gradlew runDatagen && ./gradlew clean build test` all green.
- [ ] `./gradlew build` artifact signed/verified per companion release conventions.

---

### Task T-8.4.2 — `CHANGELOG.md`

**Acceptance:**
- [ ] `companions/fizzle-enchanting/CHANGELOG.md` gets a `[0.1.0]` entry with highlights per epic.

---

### Task T-8.4.3 — Plugin-list entry + root changelog

**Resume context:** Root `CLAUDE.md` § "CHANGELOG pre-processing" — mod additions require the root `CHANGELOG.md` `[Unreleased] → Added` line in the same commit.

**Acceptance:**
- [ ] `plugins/gameplay.md` gets a `## Fizzle Enchanting` entry with `Mod Loader: Manual`, side `both`, summary + why + deps + conflicts per the CLAUDE.md format rules.
- [ ] `/home/rfizzle/Projects/FizzleSMP/CHANGELOG.md` `[Unreleased] → Added` — one-line entry.

---

### Task T-8.4.4 — Conflict audit

**Acceptance:**
- [ ] Run `/check-conflicts` (or manual pass per that command's checklist).
- [ ] Add meaningful findings to `docs/compatibility-matrix.md` (e.g. "Fizzle Enchanting | Enchanting Infuser — soft conflict, schedule Enchanting Infuser removal per iteration backlog").

---

# Epic 9 — Post-MVP Iteration Backlog

- [ ] Epic complete

These stories **do not run** until MVP is shipped and playtested. Listed here so we don't lose track; each expands into its own tasks when the iteration is scheduled. One commit per story when it lands (with the CHANGELOG + plugin-list cleanup per DESIGN.md "Tracking & Bookkeeping").

---

## Story S-9.1 — Absorb BeyondEnchant

- [ ] Story complete

**Resume context:** DESIGN.md § "Iteration 1 — Absorb BeyondEnchant".

**High-level tasks (expand when scheduled):**

- Ship 16 override JSONs at `data/minecraft/enchantment/*.json` matching the BeyondEnchant defaults table.
- Add `levelCaps` config section (`enabled`, `perEnchantment`).
- Wire `config.display.overLeveledColor` into the runtime cap lookup (already reserved in MVP config).
- Verify Spectrum-BeyondEnchant-LevelCap-Fix Paxi pack still applies.
- Remove BeyondEnchant from `plugins/gameplay.md`.

**Tests:** Each override parses; config `perEnchantment` overrides the bundled default.

---

## Story S-9.2 — Absorb Easy Magic (item persistence)

- [ ] Story complete

**Resume context:** DESIGN.md § "Iteration 2 — Absorb Easy Magic".

**High-level tasks:**

- Add a BE to the enchanting table to hold slot contents across GUI close.
- Idempotent migration: on chunk load, attach the BE if missing.
- Persist across player logout.
- **Explicitly cut:** re-roll feature (clashes with Clues/Arcana design).

**Tests:** Item persists across GUI close, logout/login, and pre-existing vanilla table migration.

---

## Story S-9.3 — Easy Anvils `tooExpensiveCap` (decision pending)

- [ ] Story complete

**Resume context:** DESIGN.md § "Mostly unnecessary — Easy Anvils (decision pending)".

**Decision first:** before starting tasks, confirm player demand for the cap — if Easy Anvils coexistence is stable, skip this story entirely (tick this line with a note `"skipped — coexistence accepted"`).

**High-level tasks (only if green-lit):**

- Mixin `AnvilMenu#mayPickup` / `#onTake` to clamp/remove the `"too expensive!"` cap.
- Add `config.anvil.tooExpensiveCap` (DESIGN stub).
- Skip every other Easy Anvils feature.

**Tests:** Cap values — 40, -1 (disabled), 0 (vanilla) all behave as specified.

---

## Story S-9.4 — Per-iteration bookkeeping checklist (reusable template)

- [ ] Story complete

When any iteration lands, run this pass — don't re-invent per iteration:

- [ ] `companions/fizzle-enchanting/CHANGELOG.md` `[Unreleased] → Added` entry.
- [ ] Root `/home/rfizzle/Projects/FizzleSMP/CHANGELOG.md`: `### Removed` for the replaced mod, `### Changed` for the Fizzle Enchanting surface.
- [ ] `plugins/gameplay.md` — remove the replaced mod entry.
- [ ] `docs/compatibility-matrix.md` — drop rows referencing the replaced mod.
- [ ] `./scripts/sync-packwiz.sh --prune` and commit metadata alongside the plugin/doc edits in the same commit.

---

## Appendix — Reference paths cheat-sheet

- Zenith anvil mechanics: `/home/rfizzle/Projects/Zenith/src/main/java/dev/shadowsoffire/apotheosis/ench/anvil/`
- Zenith library: `/home/rfizzle/Projects/Zenith/src/main/java/dev/shadowsoffire/apotheosis/ench/library/`
- Zenith table + stat registry: `/home/rfizzle/Projects/Zenith/src/main/java/dev/shadowsoffire/apotheosis/ench/table/`
- Zenith shelf stats JSON: `/home/rfizzle/Projects/Zenith/src/main/resources/data/zenith/enchanting_stats/`
- Zenith textures: `/home/rfizzle/Projects/Zenith/src/main/resources/assets/zenith/textures/`
- Zenith recipes: `/home/rfizzle/Projects/Zenith/src/main/resources/data/zenith/recipes/`
- Apotheosis (Forge, authoritative for mechanics): `/home/rfizzle/Projects/Apotheosis/src/main/java/dev/shadowsoffire/apotheosis/ench/`
- Fizzle Difficulty (companion template): `/home/rfizzle/Projects/FizzleSMP/companions/fizzle-difficulty/`
- Companion rules: `/home/rfizzle/Projects/FizzleSMP/.claude/commands/dev-companion.md`
