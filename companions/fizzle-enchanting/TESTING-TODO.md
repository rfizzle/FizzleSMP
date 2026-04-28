# Fizzle Enchanting — Testing Status

Coverage snapshot as of 2026-04-29. **636 tests** across 97 files (467 unit tests in `src/test/`, 169 gametests in `src/gametest/`).

Tests use three tiers from `.claude/skills/fabric-testing/SKILL.md`:

- **Tier 1** — pure JUnit (no `net.minecraft.*` registry reads). Fast, no Bootstrap.
- **Tier 2** — `fabric-loader-junit` + `@BeforeAll Bootstrap.bootStrap()`. Vanilla registries, codecs, real enchantments.
- **Tier 3** — Fabric Gametest via `./gradlew runGametest`. Real `ServerLevel`, mod content, block entities, tick loop.

---

## Table of contents

- [Infrastructure](#infrastructure)
- [Epic 1 — Scaffolding & Config](#epic-1--scaffolding--config)
- [Epic 2 — Stat System & Table](#epic-2--stat-system--table)
- [Epic 3 — Shelf Family](#epic-3--shelf-family)
- [Epic 4 — Anvil & Library](#epic-4--anvil--library)
- [Epic 5 — Tomes & Table Crafting](#epic-5--tomes--table-crafting)
- [Epic 6 — Enchantment Roster](#epic-6--enchantment-roster)
- [Epic 7 — Integrations](#epic-7--integrations)
- [Epic 8 — Polish & Release](#epic-8--polish--release)
---

## Infrastructure

All infrastructure is complete. `fabric-loader-junit`, gametest source set, CI wiring, and SNBT templates are all in place.

| ID | Description | Tier | File | Status |
|---|---|---|---|---|
| TEST-0.1-T2 | `fabric-loader-junit` smoke test | 2 | `SmokeBootstrapTest` | Done |
| TEST-0.2-T3 | Gametest source set + placeholder | 3 | `PlaceholderGameTest` | Done |
| TEST-0.3 | CI runs `test` + `runGametest` | — | GitHub Actions | Done |

> **Note:** `forkEvery=1` is still present because some legacy tests reflectively unfreeze registries. Remove once all legacy patterns are eliminated.

---

## Epic 1 — Scaffolding & Config

### S-1.2 — Mod entrypoints

| Test | Tier | File | Methods |
|---|---|---|---|
| Constants (`MOD_ID`, `LOGGER`, `id()`) | 1 | `ModBootTest` | 3 |
| Mod-init sentinel (config loaded, registry populated) | 3 | `ModSentinelGameTest` | 2 |

### S-1.3 — Configuration

| Test | Tier | File | Methods |
|---|---|---|---|
| Defaults, GSON load/save, clamps, color fallback, migration | 1 | `FizzleEnchantingConfigTest` | 15 |

### S-1.4 — Command skeleton

| Test | Tier | File | Methods |
|---|---|---|---|
| Brigadier tree walks, perm gates, translation keys | 1 | `FizzleEnchantingCommandTest` | 17 |
| Live config reload on GameTestServer | 3 | `ConfigReloadGameTest` | 2 |

---

## Epic 2 — Stat System & Table

### S-2.1 — Stat data model

| Test | Tier | File | Methods |
|---|---|---|---|
| `EnchantingStats` record, codec round-trip, ZERO singleton | 1 | `EnchantingStatsTest` | 8 |
| `EnchantingStatRegistry` lookup precedence, JSON parse | 1 | `EnchantingStatRegistryTest` | 15 |
| All shipped stat JSONs parse via codec post-bootstrap | 2 | `StatJsonCodecSweepTest` | 2 |
| Datapack reload populates `EnchantingStatRegistry` | 3 | `StatRegistryGameTest` | 3 |

### S-2.2 — Shelf scan & aggregation

| Test | Tier | File | Methods |
|---|---|---|---|
| Pure aggregation (sums, clamps, LOS, step-ladder, hooks) | 1 | `EnchantingStatRegistryGatherTest` | 27 |
| Vanilla `BOOKSHELF_OFFSETS` guard (size, LOS midpoints) | 2 | `BookshelfOffsetGuardTest` | 5 |
| Real bookshelves around table, LOS blocking, custom shelves, clue clamp | 3 | `ShelfScanGameTest` | 18 |
| Filtering/treasure BE hooks in real scan | 3 | `FilteringTreasureGameTest` | 8 |

### S-2.3 — Network payloads

| Test | Tier | File | Methods |
|---|---|---|---|
| `StatsPayload` + `CluesPayload` round-trip | 2 | `PayloadCodecTest` | 8 |
| Payloads registered in S2C registry | 2 | `NetworkingRegistryTest` | 2 |

### S-2.4 — Enchantment selection

| Test | Tier | File | Methods |
|---|---|---|---|
| `getEnchantmentCost` monotonicity, pure math | 1 | `EnchantmentCostMathTest` | 8 |
| `getEnchantmentCost` end-to-end with real `ItemStack` | 2 | `RealEnchantmentHelperTest` | 10 |
| `selectEnchantment` blacklist, treasure, quanta, rectification, rarity | 2 | `SelectEnchantmentTest` | 14 |
| `buildClueList` determinism, exhausted flag | 2 | `BuildClueListTest` | 14 |
| Pool filtering: treasure gate, blacklist, item-type exclusion | 3 | `EnchantmentSelectionGameTest` | 5 |

### S-2.5 — Menu & screen

| Test | Tier | File | Methods |
|---|---|---|---|
| Stat-line formatter | 1 | `StatLineFormatterTest` | 5 |
| Mixin injection target bytecode introspection | 1 | `EnchantmentTableBlockMixinTest` | 6 |
| `EnchantmentMenuAccessor` declaration | 1 | `EnchantmentMenuAccessorTest` | 5 |
| Menu logic: XP/lapis validation, click paths | 2 | `FizzleEnchantmentLogicTest` | 21 |
| End-to-end menu: open, enchant, costs, lapis, XP, items | 3 | `MenuEndToEndGameTest` | 14 |

---

## Epic 3 — Shelf Family

### S-3.1 — Shelf infrastructure

| Test | Tier | File | Methods |
|---|---|---|---|
| `ParticleTheme` enum coverage | 2 | `ParticleThemeTest` | 1 |
| Base `EnchantingShelfBlock` delegation + theme | 2 | `EnchantingShelfBlockTest` | 2 |
| Registry helpers: all ids resolve, idempotent | 3 | `RegistryGameTest` | 7 |

### S-3.2 — Full Zenith shelf roster

| Test | Tier | File | Methods |
|---|---|---|---|
| Texture bundle + animated `.mcmeta` + no cut dirs | 1 | `ShelfTextureBundleTest` | 2 |
| Lang keys per shelf | 1 | `ShelfLangKeysTest` | dynamic |
| Ported stat JSONs: no `zenith:` literals, valid namespaces | 1 | `PortedEnchantingStatsTest` | 3 |
| Every shelf id registered, sound/strength match DESIGN | 3 | `ShelfRosterGameTest` | 5 |

### S-3.3 — Utility shelves

| Test | Tier | File | Methods |
|---|---|---|---|
| Sightshelf stat JSONs: clue counts, `maxEterna: 0` | 1 | `SightshelfStatsTest` | 3 |
| Rectifier tiers: `10/15/20` | 1 | `RectifierStatsTest` | 5 |
| Sightshelf + sightshelf_t2 in real scan | 3 | `ShelfScanGameTest` (sightshelf* methods) | 2 |

### S-3.4 — Datagen

| Test | Tier | File | Methods |
|---|---|---|---|
| Post-datagen filesystem sweep (blockstates, loot, recipes) | 1 | `DatagenFilesystemSweepTest` | 10 |

### S-3.5 — Filtering & treasure shelves

| Test | Tier | File | Methods |
|---|---|---|---|
| Slot-targeting math (cursor → slot index) | 1 | `SlotTargetingMathTest` | 8 |
| Filtering shelf book-count scaling | 1 | `FilteringShelfBookScalingTest` | 4 |
| Filtering + treasure BE end-to-end (insert, blacklist, treasure flag) | 3 | `FilteringTreasureGameTest` | 8 |

---

## Epic 4 — Anvil & Library

### S-4.1 — Anvil dispatcher

| Test | Tier | File | Methods |
|---|---|---|---|
| Dispatcher handler ordering | 1 | `AnvilDispatcherTest` | 5 |
| `AnvilMenuMixin` bytecode introspection | 1 | `AnvilMenuMixinTest` | 7 |
| `AnvilMenuAccessor` declaration | 1 | `AnvilMenuAccessorTest` | 6 |
| `PrismaticWebHandler` curse-strip logic | 2 | `PrismaticWebCurseLogicTest` | 9 |
| Prismatic web end-to-end in anvil | 3 | `PrismaticWebGameTest` | 2 |

### S-4.2 — Iron-block anvil repair

| Test | Tier | File | Methods |
|---|---|---|---|
| Tier ladder, material rejection, enchant preservation, config gate | 2 | `IronBlockAnvilRepairTest` | 9 |
| Full anvil click: damaged → chipped → normal | 3 | `AnvilRepairGameTest` | 3 |

### S-4.3 — Library storage engine

| Test | Tier | File | Methods |
|---|---|---|---|
| Point math (`2^(level-1)`, `maxLevelAffordable`) | 1 | `PointMathTest` | 3 (parameterized) |
| Deposit/extract/canExtract with real vanilla enchants | 2 | `EnchantmentLibraryBlockEntityTest` | 15 |
| Basic + Ender tier constants | 2 | `LibraryTierBlockEntityTest` | 5 |
| NBT round-trip, unknown key handling | 2 | `LibraryNbtTest` | 6 |
| Client sync packet reconstruction | 2 | `LibraryClientSyncTest` | 4 |
| Real library: deposit, save/load, fresh state | 3 | `LibraryPersistGameTest` | 2 |

### S-4.4 — Library block & UI

| Test | Tier | File | Methods |
|---|---|---|---|
| Row formatter pure string output | 1 | `LibraryRowFormatterTest` | 3 |
| Library blocks register, correct BE, deposit/extract/caps/listeners | 3 | `LibraryGameTest` | 16 |

### S-4.5 — Hopper integration

| Test | Tier | File | Methods |
|---|---|---|---|
| `Storage<ItemVariant>` adapter, abort/commit, rate limit | 2 | `LibraryStorageTest` | 10 |
| Transfer API: find storage, insert, reject non-book, abort rollback | 3 | `LibraryHopperGameTest` | 4 |

### S-4.6 — Custom recipe types

| Test | Tier | File | Methods |
|---|---|---|---|
| `StatRequirements` record semantics | 1 | `StatRequirementsTest` | 4 |
| `EnchantingRecipe` codec, `matches`, shipped JSON parse | 2 | `EnchantingRecipeTest` | 13 |
| `EnchantingRecipeRegistry.findMatch` across subtypes | 2 | `EnchantingRecipeRegistryTest` | 5 |
| Crafting button: recipe match, output, stat bounds, keep-nbt | 3 | `CraftingButtonGameTest` | 7 |

---

## Epic 5 — Tomes & Table Crafting

### S-5.1 — Tome items

| Test | Tier | File | Methods |
|---|---|---|---|
| Tome asset + lang + model JSON; no cut textures | 1 | `TomeAssetsTest` | 3 (parameterized) |
| Scrap tome vanilla-shape recipe parse | 1 | `ScrapTomeRecipeTest` | 1 |
| All tomes registered, stack size, no durability | 3 | `TomeRegistryGameTest` | 4 |
| Tome recipe classifier (routes to correct tab) | 3 | `TomeRecipeClassifierGameTest` | 5 |

### S-5.2 — Tome anvil handlers

| Test | Tier | File | Methods |
|---|---|---|---|
| Damage-clamp arithmetic | 1 | `DamageClampMathTest` | 10 |
| Scrap tome seeded RNG, extraction strip+damage | 2 | `TomeHandlerLogicTest` | 13 |
| All three tome handlers + fuel-slot repair end-to-end | 3 | `TomeAnvilGameTest` | 5 |

### S-5.3 — Table crafting-result row

| Test | Tier | File | Methods |
|---|---|---|---|
| Client row formatter (level text, singular/plural) | 1 | `CraftingRowFormatterTest` (in `FizzleEnchantmentLogicTest`) | covered |
| `findMatch` hook stores recipe on menu | 2 | `TableCraftingLookupTest` | 4 |
| `CraftingResultEntry` projection onto `StatsPayload` | 2 | `CraftingResultProjectionTest` | 4 |
| Server handler for `buttonId=3`: subtypes, XP, no-op | 2 | `CraftingResultFlowTest` | 6 |
| Full crafting button click on live menu | 3 | `CraftingButtonGameTest` | 7 |

### S-5.4 — Specialty materials

| Test | Tier | File | Methods |
|---|---|---|---|
| `WardenPoolCondition` math, codec round-trip | 1 | `WardenPoolConditionTest` | 10 |
| `PowerFunction` linear/fixed/default variants | 1 | `PowerFunctionTest` | 7 |
| Lang keys + animation mcmeta for specialty items | 1 | `SpecialtyMaterialAssetsTest` | 3 |
| Items registered, rarity, condition type, warden tendril drop | 3 | `SpecialtyMaterialsGameTest` | 5 |

---

## Epic 6 — Enchantment Roster

### S-6.1 — NeoEnchant+ port (49 enchantments)

| Test | Tier | File | Methods |
|---|---|---|---|
| File count, no `enchantplus:` literals, cut files absent | 1 | `PortedEnchantmentsTest` | 4 |
| Lang keys: one per ported enchant, no stale keys | 1 | `PortedEnchantmentLangTest` | 4 |
| Exclusive-set tags: no stale refs, entries resolve | 1 | `PortedExclusiveSetTagsTest` | 5 |
| All 49+2 ids resolve in registry; cut enchants absent | 3 | `EnchantmentRosterGameTest` | 3 |
| Per-slot definition validation + effect verification | 3 | `*EnchantmentGameTest` (11 files) | 43 |

### S-6.2 — Authored enchants

| Test | Tier | File | Methods |
|---|---|---|---|
| Icy Thorns JSON shape, target, lang key | 1 | `AuthoredIcyThornsTest` | 6 |
| Shield Bash JSON shape, weapon tag expansion, lang key | 1 | `AuthoredShieldBashTest` | 8 |
| Icy Thorns applies slowness to attacker in-world | 3 | `ArmorEnchantmentGameTest.icyThornsAppliesSlownessToAttacker` | 1 |
| Shield Bash deals extra damage in-world | 3 | `SwordEnchantmentGameTest.shieldBashDealsExtraDamage` | 1 |

### S-6.3 — Foreign enchant overrides

**Not implemented.** Override files (`data/minecraft/enchantment/mending.json`, `data/yigd/enchantment/soulbound.json`) do not exist yet. Tests should be written alongside the feature.

---

## Epic 7 — Integrations

| Test | Tier | File | Methods |
|---|---|---|---|
| `fabric.mod.json` entrypoints + optional deps | 1 | `IntegrationEntrypointTest` | 5 |
| Shared `TableCraftingDisplayExtractor` | 2 | `TableCraftingDisplayExtractorTest` | 3 |
| Jade tooltip formatters (table, library, shelf, recipe) | 1 | `JadeTooltipFormatterTest` | 14 |
| Recipe info formatter (requirements, shelf stats) | 1 | `RecipeInfoFormatterTest` | 8 |

---

## Epic 8 — Polish & Release

### S-8.1 — Advancements

| Test | Tier | File | Methods |
|---|---|---|---|
| Roster size, codec parse, lang keys | 3 | `AdvancementCodecGameTest` | 3 |

### S-8.2 — Tooltips & overleveled coloring

| Test | Tier | File | Methods |
|---|---|---|---|
| Over-leveled detection, vanilla caps, hex parse, book line suppression | 1 | `TooltipFormatterTest` | 18 |

