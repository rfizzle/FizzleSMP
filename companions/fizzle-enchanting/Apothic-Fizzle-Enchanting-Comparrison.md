# Apothic vs Fizzle Enchanting — Gap Assessment

> **Assessment date:** 2026-04-27
> **Method:** 8 parallel agents, each scanning the Fizzle source code against the Apothic Enchanting design doc
> **Apothic reference:** `Apothic-Enchanting-Design.md` (Apothic Enchanting for NeoForge 1.21.1)
> **Fizzle target:** `companions/fizzle-enchanting/` (Fabric 1.21.1)

---

## Status by Phase

| Phase | Area | Parity | Key Gaps |
|-------|------|--------|----------|
| 1 | Core Systems (Structure, Stats, Selection, Network) | ~100% | — |
| 2 | GUI & UX (Screen, Info Browser, Particles) | ~100% | — |
| 3 | Blocks & Shelves (27 shelves, special shelves) | ~100% | — |
| 4 | Recipes & Data-Driven Audit | ~90% | 16/20 infusion recipes (4 intentionally cut); KeepNBT copies enchantments only |
| 5 | Library, Tomes & Anvil | ~90% | Library extract produces books only (not direct apply); 9 typed tomes cut |
| 6 | Enchantments & Config | ~75% | 61 enchantments (12/19 Apothic implemented); 9 Java-backed + 1 Fizzle-original (Bag of Souls); configurable power functions (linear/fixed); inline ench descs option added |
| 7 | Progression & Content (Advancements, API, Items) | ~98% | Corrupted damage type N/A (Berserker's Fury cut) |
| 8 | Integration & Compat (Mixins, REI/EMI/JEI, WTHIT/Jade) | ~100% | Trinkets API wired but idle (all enchantments are JSON) |

---

## Phase 1: Core Systems

### A. Project Structure & Build Setup

- [x] Mod ID `fizzle_enchanting`, version `0.1.0`, targets Minecraft 1.21.1
- [x] `fabric.mod.json` with `main`, `client`, `fabric-datagen`, `fabric-gametest` (31 gametest classes), plus `emi`, `rei_client`, `jei_mod_plugin`, `jade`, `modmenu` integration entrypoints
- [x] Dependencies: `fabricloader>=0.16.10`, `minecraft~1.21.1`, `java>=21`, `fabric-api:*`. Optional deps in `suggests`
- [x] `FizzleEnchanting` (ModInitializer) + `FizzleEnchantingClient` (ClientModInitializer)
- [x] Centralized registration in `FizzleEnchantingRegistry.register()` via `Registry.register()` against `BuiltInRegistries`
- [x] 19 packages: `enchanting/`, `net/`, `config/`, `shelf/`, `library/`, `anvil/`, `tome/`, `event/`, `mixin/`, `data/`, `compat/`, `client/`, `command/`, `advancement/`, `particle/`, `sound/`, `item/`, `client/tooltip/`
- [x] 61 JUnit tests + 31 gametest classes; `useJUnitPlatform()`, `fabric-loader-junit`, gametest sourceset
- [x] 6 mixins in `fizzle_enchanting.mixins.json` + access widener file
- [x] 3 S2C payloads registered via `PayloadTypeRegistry.playS2C()`

### B. Core Stat System

- [x] Five-stat model: Eterna, Quanta, Arcana, Rectification, Clues (superset of Apothic's three + secondaries)
- [x] Eterna clamped to `[0, maxEterna]` (config default 50); Quanta/Arcana/Rectification to `[0, 100]`. Config and DESIGN.md now aligned
- [x] Step-ladder eterna accumulation matches Apothic: contributions grouped by `maxEterna`, sorted ascending, each tier's running total capped at its ceiling
- [x] Baselines: +15 quanta, +itemEnchantability/2 arcana, +1 clue (matches Apothic exactly)
- [x] `StatsPayload` carries all 5 stats + blacklist + treasure flag + crafting result; hand-written `StreamCodec`
- [x] Gathers from `EnchantingTableBlock.BOOKSHELF_OFFSETS` (vanilla 15-offset list); air/transmitter midpoint check
- [x] 31 JSON stat files, loaded via `SimpleSynchronousResourceReloadListener`. Tag-based bindings supported

### C. Selection Algorithm

- [x] Slot levels: slot 2 = `round(eterna)`, slot 1 = 60-80%, slot 0 = 20-40% (matches Apothic)
- [x] Quanta factor: truncated Gaussian `clamp(nextGaussian()/3, -1, 1) * quanta/100`
- [x] Rectification truncates negative tail (at rect=100 equivalent to Apothic's "stable" mode)
- [x] Power range 1-200 (`powerCap = 200`, matching Apothic); power filtering uses `minPower <= power <= maxPower` (Apothic semantics)
- [x] `Arcana` enum with 11 tiers matching Apothic values verbatim
- [x] Guaranteed picks at arcana 0, 33, 66 — matches Apothic (3 picks max)
- [x] Random additional enchantments with `randomBound = max(50, scaledLevel*1.15)`, `scaledLevel /= 2`
- [x] Blacklist filtering from filtering shelves; treasure filtering gated by treasure shelf
- [x] `EnchantableItem` interface for post-processing; compatibility check via `Enchantment.areCompatible()`
- [x] `EnchantmentInfo` system with `PowerFunction` for per-enchantment min/max power overrides

### D. Network & Client-Server Sync

- [x] `StatsPayload` (S2C): 5 stats + maxEterna + blacklist + treasure + crafting result
- [x] `CluesPayload` (S2C): per-slot enchantment clues + exhausted flag; sent 3× per `slotsChanged`
- [x] `EnchantmentInfoPayload` (S2C): full per-enchant config map; synced on join + datapack reload
- [x] All payloads use proper codec serialization; registered on correct channels
- [x] Client handlers update `FizzleEnchantmentMenu` state and `EnchantmentInfoRegistry`

---

## Phase 2: GUI & UX

### A. Enchanting Table Screen

- [x] `FizzleEnchantmentScreen` extends `EnchantmentScreen`; custom GUI texture (197px height)
- [x] Three stat bars at y=75,85,95; width=110px; smooth interpolation (0.1F up, 0.075F down)
- [x] Stat bar tooltips match Apothic pattern: colored header + subtitle, gray description, derived values
- [x] Hover-contextual `drawOnLeft` panels: quanta shows Quantic Warping; arcana shows Arcane Empowerments + rarity weight table
- [x] Slot hover `drawOnLeft` panel: level cost, power range, item enchantability, clue count
- [x] Enchantment slot hover shows clue enchantments; partial vs full clue toggle via `isClientCluesExhausted`
- [x] Info button `[i]` at (148,1) opens `EnchantingInfoScreen`
- [x] Infusion display in slot 2 (yellow underline result name + cost)
- [x] "Infusion Failed" display (red text) when item matches recipe but stats insufficient

### B. Enchantment Info Browser

- [x] Opens from `[i]` button; scrollable 11-row enchantment list with scrollbar
- [x] `PowerSlider` controls `currentPower` within quanta-adjusted range; recomputes enchantments on change
- [x] Arcana weight table (Common/Uncommon/Rare/Very Rare) with per-enchantment weight + chance %
- [x] Exclusion tooltips: "Exclusive With: ..." in red via `Enchantment.areCompatible()` check
- [-] No text search/filter (minor — Apothic also lacks this)

### C. Particles & Ambience

- [x] 4 custom particle types: `ENCHANT_FIRE`, `ENCHANT_WATER`, `ENCHANT_SCULK`, `ENCHANT_END`
- [x] 4 particle JSONs + 104 textures (26 SGA glyphs × 4 themes)
- [x] `FlyTowardsPositionParticle.EnchantProvider` drives motion toward table
- [x] `EnchantmentTableBlockMixin.animateTick` spawns themed particles per shelf via `IEnchantingStatProvider`
- [x] 3 music discs (Eterna, Quanta, Arcana) with sound events, OGG files, jukebox songs, item models/textures

---

## Phase 3: Blocks & Shelves

### A. Shelf Block Registry

- [x] `EnchantingShelfBlock extends Block implements IEnchantingStatProvider` with `ParticleTheme`
- [x] Stats data-driven: 31 JSON files in `enchanting_stats/`; supports block-keyed and tag-keyed bindings
- [x] 25 themed shelves + filtering shelf + treasure shelf = 27 total blocks
- [x] All blockstates, models, textures, loot tables generated via datagen
- [x] Creative tab includes all blocks

#### Complete Shelf Stats Table

| Block | Tier | maxE | eterna | quanta | arcana | clues | rectif. | Special |
|-------|------|------|--------|--------|--------|-------|---------|---------|
| Vanilla Bookshelf | Starter | 15 | 1 | 0 | 0 | 0 | 0 | Tag fallback |
| Stoneshelf | Starter | 0 | -1.5 | 0 | -7.5 | 0 | 0 | Negative stats |
| Beeshelf | Starter | 0 | -15 | 100 | 0 | 0 | 0 | Max quanta, negative eterna |
| Melonshelf | Starter | 0 | -1 | -10 | 0 | 0 | 0 | Negative quanta/eterna |
| Dormant Deepshelf | Starter | 15 | 1 | 0 | 0 | 0 | 0 | Pre-infusion |
| Hellshelf | Early | 22.5 | 1.5 | 3 | 0 | 0 | 0 | |
| Seashelf | Early | 22.5 | 1.5 | 0 | 2 | 0 | 0 | |
| Infused Hellshelf | Mid | 27 | 1.75 | 1.75 | 0 | 0 | 0 | Table craft |
| Infused Seashelf | Mid | 27 | 1.75 | 0 | 1.75 | 0 | 0 | Table craft |
| Glowing Hellshelf | Mid | 30 | 2 | 2 | 4 | 0 | 0 | |
| Blazing Hellshelf | Mid | 30 | 4 | 5 | 0 | -1 | 0 | Removes a clue |
| Crystal Seashelf | Mid | 30 | 2 | 4 | 2 | 0 | 0 | |
| Heart Seashelf | Mid | 30 | 3 | 0 | 10 | 0 | -5 | Negative rectification |
| Deepshelf | Late | 35 | 2.5 | 5 | 5 | 0 | 0 | Table craft |
| Echoing Deepshelf | Late | 37.5 | 2.5 | 0 | 15 | 0 | 0 | Arcana-focused |
| Soul-Touched Deepshelf | Late | 37.5 | 2.5 | 15 | 0 | 0 | 0 | Quanta-focused |
| Echoing Sculkshelf | Late | 40 | 5 | 5 | 15 | 1 | 0 | Clue + sculk |
| Soul-Touched Sculkshelf | Late | 40 | 5 | 15 | 5 | 0 | 5 | Rectification + sculk |
| Endshelf | End | 45 | 2.5 | 5 | 5 | 0 | 0 | |
| Pearl Endshelf | End | 45 | 5 | 7.5 | 7.5 | 0 | 0 | |
| Draconic Endshelf | Max | 50 | 10 | 0 | 0 | 0 | 0 | Only way to max E |
| Sightshelf | Utility | — | 0 | 0 | 0 | 1 | 0 | Clue only |
| Sightshelf T2 | Utility | — | 0 | 0 | 0 | 2 | 0 | Double clue |
| Rectifier | Utility | — | 0 | 0 | 0 | 0 | 10 | Fizzle-original |
| Rectifier T2 | Utility | — | 0 | 0 | 0 | 0 | 15 | Fizzle-original |
| Rectifier T3 | Utility | — | 0 | 0 | 0 | 0 | 25 | Fizzle-original |
| Filtering Shelf | Utility | 15 | 1 | 0 | 0 | 0 | 0 | Blacklists enchantments |
| Treasure Shelf | Utility | 0 | 0 | 0 | 0 | 0 | 0 | Enables treasure enchants |

Non-shelf stat providers: Amethyst Cluster (+1.5 rect), Basic Skulls (+5 quanta), Wither Skeleton Skull (+10 quanta).

### B. Special Shelves

- [x] **Filtering shelf**: 6-slot `ChiseledBookShelfBlock` extension; reads enchantments from stored books; `BlacklistSource` interface; block entity persistence + client sync
- [x] Filtering shelf per-book dynamic stats: `getStats()` override adds +0.5 eterna / +1 arcana per stored book (matches Apothic)
- [x] **Treasure shelf**: `TreasureFlagSource` marker; no stat contribution; custom texture
- [x] **Rectifier shelves (Fizzle-original)**: 3 tiers (10/15/25 rectification); all have recipes, models, textures
- [x] **Sculk ambient**: `SculkShelfBlock` overrides `animateTick()` — plays `SCULK_CATALYST_BLOOM` sounds and spawns `SCULK_SOUL` particles using config fields `sculkShelfShriekerChance` and `sculkParticleChance`

### C. Shelf Gaps

- ~~**CRITICAL: No mineable block tags**~~ — FIXED: wood-tier shelves added to `minecraft:mineable/axe`, stone/sculk-tier shelves + libraries added to `minecraft:mineable/pickaxe`
- ~~**Missing crafting recipes**: filtering shelf, treasure shelf, endshelf, echoing sculkshelf, soul_touched_sculkshelf~~ — FIXED
- **No Geode Shelf** — replaced by Rectifier shelves (intentional divergence)

---

## Phase 4: Recipes & Data-Driven Audit

### A. Infusion Recipe System

- [x] Custom recipe type `fizzle_enchanting:enchanting` with `MapCodec` + `StreamCodec`
- [x] `StatRequirements` with min and optional max requirements (eterna, quanta, arcana)
- [x] `KeepNbtEnchantingRecipe` copies `DataComponents.ENCHANTMENTS` (not full NBT — narrower than Apothic but correct for 1.21.1 component model)
- [x] Sorted by eterna desc, first match wins
- [x] GUI integration: infusion display + "Infusion Failed" display
- [x] Single-item consumption with excess returned to player

### B. Infusion Recipe Coverage (16/20)

| Recipe | Input → Output | E | Q | A | Status |
|--------|---------------|---|---|---|--------|
| Infused Seashelf | seashelf → infused_seashelf | 22.5 | 15 | 10 | [x] |
| Infused Hellshelf | hellshelf → infused_hellshelf | 22.5 | 30 | 0 | [x] |
| Deepshelf | dormant_deepshelf → deepshelf | 30 | 40 | 40 | [x] |
| Improved Scrap Tome | scrap_tome → improved_scrap_tome ×4 | 22.5 | 25 | 35 | [x] |
| Extraction Tome | improved_scrap_tome → extraction_tome ×4 | 30 | 25 | 45 | [x] |
| Infused Breath | dragon_breath → infused_breath ×3 | 40 | 15 | 60 | [x] |
| Budding Amethyst | amethyst_block → budding_amethyst | 30 | 30 | 50 | [x] |
| Golden Carrot | carrot → golden_carrot | 10 | 10 | 0 | [x] |
| Honey → XP T1 | honey_bottle → experience_bottle ×1 | 10 | 25 | 25 | [x] |
| Honey → XP T2 | honey_bottle → experience_bottle ×8 | 30 | 25 | 25 | [x] |
| Honey → XP T3 | honey_bottle → experience_bottle ×32 | 50 | 25 | 25 | [x] |
| Echo Shard | echo_shard → echo_shard ×4 | 35 | 50 | 50 | [x] |
| Ender Library | library → ender_library (keep NBT) | 50 | 45 | 100 | [x] |
| Music Disc: Eterna | #creeper_drop_music_discs → disc_eterna | 40 | 0 | 0 | [x] |
| Music Disc: Quanta | #creeper_drop_music_discs → disc_quanta | 10 | 40 | 0 | [x] |
| Music Disc: Arcana | #creeper_drop_music_discs → disc_arcana | 10 | 0 | 40 | [x] |
| Inert Trident → Trident | — | — | — | — | [N/A] Cut |
| Flimsy Ender Lead → Lead | — | — | — | — | [N/A] Cut |
| Ender Lead → Occult Lead | — | — | — | — | [N/A] Cut |

All eterna thresholds scaled ~0.5× from Apothic (Fizzle maxE=50 vs Apothic maxE=100).

### C. Data-Driven Audit

| System | JSON? | Datapack Override? |
|--------|-------|--------------------|
| Shelf stats | Yes (31 files) | Yes |
| Infusion recipes | Yes (16 files) | Yes |
| Crafting recipes | Yes (3 files) | Yes |
| Enchantments | Yes (52 files, vanilla format) | Yes |
| Advancements | Yes (18 files) | Yes |
| Tags | Yes (20 files) | Yes |
| Loot tables (blocks) | Yes (29 files) | Yes |
| Jukebox songs | Yes (3 files) | Yes |
| Arcana tiers/weights | **Java hardcoded** (enum) | No |
| Selection algorithm | **Java hardcoded** | No |
| Library point system | **Java hardcoded** | No |
| Warden loot injection | **Java + config** | Partially |

---

## Phase 5: Library, Tomes & Anvil

### A. Enchantment Library

- [x] `EnchantmentLibraryBlock` + `EnchantmentLibraryBlockEntity` (abstract) with `Object2IntMap` for points and maxLevels
- [x] Point formula: `2^(level-1)` via `1 << (level - 1)`
- [x] Two tiers: Basic (maxLevel=16, maxPoints=32768) and Ender (maxLevel=31, maxPoints=1073741824)
- [x] Deposit: iterates `STORED_ENCHANTMENTS`, adds points with saturating clamp
- [x] Extract: debits `points(target) - points(currentLevel)` with validation
- [x] NBT persistence, network sync via `ClientboundBlockEntityDataPacket`
- [x] GUI: scrollable list with search/filter, deposit slot, extract slot, point progress bars
- [x] Hopper automation: `LibraryStorageAdapter` (insert-only via `InsertionOnlyStorage`)
- [x] Ender Library upgrade via keep-NBT infusion recipe (E50/Q45/A100)
- [~] **Extract produces enchanted books only** — Apothic applies enchantments directly to any item. Fizzle follows Zenith's book-only pattern

### B. Tomes

- [x] Scrap Tome: removes 1 random enchantment, destroys source, produces book (seeded RNG)
- [x] Improved Scrap Tome: removes all enchantments into one book, destroys source
- [x] Extraction Tome: removes all enchantments into book, preserves source (with damage tick)
- [N/A] 9 typed slot-filtered tomes — intentionally cut ("UX felt counter-intuitive")
- [x] Tome tooltips via `appendHoverText` on all 3 tome items
- [~] Scrap Tome removes 1 random enchantment (not Apothic's "~half")

### C. Anvil Modifications

- [x] `AnvilDispatcher` handler chain (first-match-wins, `@FunctionalInterface`)
- [x] `PrismaticWebHandler`: strips all curse enchantments; config-driven cost (default 30)
- [x] `IronBlockAnvilRepairHandler`: damaged → chipped → normal; 1 iron block + 1 level
- [x] All 3 tome handlers + `ExtractionTomeFuelSlotRepairHandler`
- [x] `AnvilMenuMixin` with re-entrancy guard for left-replacement support
- [~] Anvil repair uses iron blocks (Zenith approach) vs Apothic's XP-only approach

---

## Phase 6: Enchantments & Configuration

### A. Enchantment Roster

**61 total enchantments** (49 NeoEnchant+ ports + 2 Zenith-inspired + 10 Apothic ports). 50 are pure JSON; 9 use Java-backed effects via Fabric events and mixins; 1 (Bag of Souls) is pure JSON with vanilla `mob_experience`/`block_experience` components.

#### Apothic Parity (12/19 implemented, 7/19 not implemented)

| Apothic Enchantment | Status | Notes |
|---------------------|--------|-------|
| Bag of Souls | [x] | Inspired by Prominence 2 / MCDungeonsArmors; armor XP boost via `mob_experience` + `block_experience` multiply; stacks to ~3x |
| Berserker's Fury | [x] | Chest armor; on-damage enrage (Resistance/Strength/Speed) with HP cost and 45s cooldown; custom corrupted damage type |
| Chromatic Aberration | [x] | Shears; sheep wool becomes random color via `SheepMixin` |
| Growth Serum | [x] | Shears; 50% chance sheep regrow wool via `SheepMixin` |
| Icy Thorns | [~] | Present but chest-only, slowness-only (no freeze) |
| Life-Mending | [x] | Any slot; healing → durability at 2^level ratio; exclusive with Mending; via `LivingEntityHealMixin` |
| Rebounding | [x] | Chest/leg armor; knockback melee attackers via `AFTER_DAMAGE` event |
| Reflective Defenses | [x] | Shield; reflects blocked damage at 15%/lvl with proc chance; via `AFTER_DAMAGE` event |
| Scavenger | [x] | Weapon; 2.5%/lvl chance to re-roll loot table on kill; via `AFTER_DEATH` event + invoker mixin |
| Shield Bash | [~] | Present but mainhand attack damage, not shield-blocking knockback |
| Stable Footing | [x] | Boots; negates 5x flying mining penalty via `PlayerMixin` |
| Temptation | [x] | Hoe; farm animals follow player via `TemptGoalMixin` |
| Boon of the Earth, Chainsaw, Crescendo of Bolts, Endless Quiver, Knowledge of the Ages, Miner's Fervor, Nature's Blessing | [-] | Not implemented — low value-to-complexity ratio or niche |

#### Fizzle Enchantment Categories (61 total)

| Category | Count | Examples |
|----------|-------|---------|
| Armor (general) | 5 | Fury, Life+, Venom Protection, Bag of Souls, Rebounding |
| Helmet | 2 | Bright Vision, Voidless |
| Chestplate | 2 | Builder Arm, Berserker's Fury |
| Leggings | 4 | Dwarfed, Fast Swim, Leaping, Oversize |
| Boots | 4 | Agility, Lava Walker, Step Assist, Stable Footing |
| Elytra | 2 | Armored, Kinetic Protection |
| Sword/Weapon | 13 | Attack Speed, Critical, Death Touch, Life Steal, Reach, XP Boost, Scavenger, ... |
| Bow/Crossbow | 7 | Accuracy Shot, Breezing Arrows, Echo Shot, Explosive Arrow, Storm Arrows, ... |
| Trident | 1 | Gungnir Breath |
| Mace | 3 | Striker, Teluric Wave, Wind Propulsion |
| Pickaxe | 1 | Vein Miner |
| Tools | 1 | Mining+ |
| Hoe | 4 | Harvest, Scyther, Seior's Oblivion, Temptation |
| Shears | 2 | Chromatic Aberration, Growth Serum |
| Shield | 1 | Reflective Defenses |
| Any (durability) | 3 | Curse of Breaking, Curse of Enchant, Life-Mending |
| Mounted/Dog | 4 | Cavalier Egis, Ethereal Leap, Steel Fang, Velocity |
| Other | 2 | Midas Touch, Icy Thorns (chest), Shield Bash (mainhand) |

### B. Configuration System

- [x] Per-enchantment overrides: `maxLevel`, `maxLootLevel`, `levelCap` via `enchantmentOverrides.<id>`
- [x] Server-to-client sync via `EnchantmentInfoPayload` on join + reload
- [x] `/fizzleenchanting reload` command triggers config re-read + registry rebuild + client sync
- [x] Client config: `display.showBookTooltips`, `display.overLeveledColor`
- [x] Sculk config fields (`sculkShelfShriekerChance`, `sculkParticleChance`) wired to `SculkShelfBlock.animateTick()`
- [x] **Configurable power functions** — `PowerFunction` sealed interface with 4 implementations: `default_min` (vanilla extrapolation), `default_max` (flat 200), `linear` (base + perLevel × level), `fixed` (constant). Configured per-enchantment via `minPowerFunction`/`maxPowerFunction` in `EnchantmentOverride`
- [x] Inline enchantment descriptions client option (`display.enableInlineEnchDescs`, default off)

### C. Level Scaling & Enforcement

- [x] `EnchantmentMixin.getMaxLevel()` — RETURN inject, reads from `EnchantmentInfoRegistry`
- [x] `ItemMixin.getEnchantmentValue()` — RETURN inject, applies `globalMinEnchantability` floor (default 1)
- [x] `EnchantmentMixin.getFullname()` — RETURN inject, configurable hex color for over-leveled (`#FF6600`)
- [x] `DefaultMinPowerFunction`: vanilla cost extrapolation with 1.6 exponent for above-max levels
- [x] `DefaultMaxPowerFunction`: flat cap at 200

---

## Phase 7: Progression & Content

### A. Advancements (18 total)

| Advancement | Parent | Trigger |
|-------------|--------|---------|
| root | — | Obtain any mod shelf |
| stone_tier | root | Hellshelf/Seashelf/Dormant Deepshelf |
| tier_three | stone_tier | Infused Hellshelf/Seashelf/Deepshelf |
| apotheosis | tier_three | `enchanted_at_table` E≥45, levels≥3 |
| high_arcana | stone_tier | `enchanted_at_table` arcana≥60 |
| high_quanta | stone_tier | `enchanted_at_table` quanta≥60 |
| stable_enchanting | stone_tier | Obtain any rectifier |
| all_seeing | stone_tier | Obtain sightshelf_t2 |
| curator | tier_three | Obtain filtering_shelf |
| treasure_seeker | tier_three | Obtain treasure_shelf |
| library | tier_three | Obtain basic library |
| ender_library | library | Obtain ender library |
| tome_apprentice | root | Obtain scrap_tome |
| tome_master | tome_apprentice | Obtain extraction_tome |
| web_spinner | root | Obtain prismatic_web |
| warden_tendril | root | Obtain warden_tendril |
| sculk_mastery | warden_tendril | Echoing/Soul-Touched Sculkshelf |
| infused_breath | root | Obtain infused_breath |

Custom trigger: `enchanted_at_table` with item/levels/eterna/quanta/arcana/rectification predicates.

### B. API Surface

| Interface | Purpose | Apothic Equivalent |
|-----------|---------|-------------------|
| `IEnchantingStatProvider` | Block stat provider (`getStats()`, `getTableParticle()`, `spawnTableParticle()`) | `EnchantmentStatBlock` |
| `EnchantableItem` | Post-process enchantment selection (`selectEnchantments()`) | `EnchantableItem` (partial — missing `applyEnchantments()`, `isPrimaryItemFor()` cut with typed tomes) |
| `BlacklistSource` | Filtering shelf blacklist hook | Part of `EnchantmentStatBlock` |
| `TreasureFlagSource` | Treasure shelf marker | Part of `EnchantmentStatBlock` |
| `EnchantingStatRegistry` | Data-driven stat lookup (`lookup()`, `gatherStats()`, `blockEntries()`) | `EnchantingStatRegistry` |

No IMC channel (Fabric has no IMC system). All API interfaces live in `enchanting/` package (no separate `api` package).

### C. Items & Tags

**9 standalone items registered:** Prismatic Web, Infused Breath, Warden Tendril, 3 Tomes, 3 Music Discs.
**27 block items** (25 shelves + filtering + treasure) + 2 library blocks.

**20 tag files:** 14 enchantment exclusive sets, 1 block (`non-solid`), 1 entity type (`last_hope_blacklist`), 4 item (deepslate, enchantable/dog, enchantable/mounted, infused_shelves).

**Loot:** Warden tendril via `LootTableEvents.MODIFY` with config-driven drop chance + looting scaling. 29 block self-drop loot tables.

**Gaps:**
- [x] `appendHoverText` on standalone items (prismatic web, tomes, infused breath, warden tendril)
- [N/A] Corrupted damage type — only used by Berserker's Fury which was cut

---

## Phase 8: Integration & Compatibility

### A. Mixins (6 total)

| Mixin | Target | Purpose |
|-------|--------|---------|
| `AnvilMenuAccessor` | `AnvilMenu` | Expose cost + repairItemCountCost fields |
| `AnvilMenuMixin` | `AnvilMenu` | Dispatch custom anvil operations; left-replacement for Extraction Tome |
| `EnchantmentMenuAccessor` | `EnchantmentMenu` | Expose enchantSlots, random, enchantmentSeed |
| `EnchantmentMixin` | `Enchantment` | Override maxLevel from config; over-leveled name color |
| `EnchantmentTableBlockMixin` | `EnchantingTableBlock` | Swap in custom menu; drive shelf particles |
| `ItemMixin` | `Item` | Global minimum enchantability floor |

**2 access wideners:** `Slot.y` (mutable field), `EnchantmentScreen.renderBook()` (accessible method).

Fizzle: 6 mixins + 0 ASM. Apothic: 15+ mixins + 3 ASM coremods. Fizzle leverages 1.21.1 data-driven enchantments and Fabric API events instead.

### B. Third-Party Integrations (7 mods)

| Mod | Plugin Classes | Features |
|-----|---------------|----------|
| EMI | `EmiEnchantingPlugin`, `EmiEnchantingRecipe` | 1 "Infusions" category, per-shelf info panels |
| REI | `ReiEnchantingPlugin`, `ReiEnchantingCategory`, `ReiEnchantingDisplay` | 1 "Infusions" category, same layout |
| JEI | `JeiEnchantingPlugin`, `JeiEnchantingCategory` | 1 "Infusions" category, same layout |
| Jade | `JadeEnchantingPlugin`, 3 providers | Enchanting table 5-axis stats + library enchant count + per-block stat contributions; vanilla "Ench Power" suppressed |
| WTHIT | `WthitCommonPlugin`, `WthitClientPlugin`, 2 providers | Same as Jade; discovered via `waila_plugins.json` |
| Trinkets | `AccessorySlotHelper`, `TrinketsCompat` | Slot enumeration + enchantment level query (wired but idle) |
| ModMenu | `ModMenuIntegration` | Full Cloth Config screen, 7 categories, 19 entries |

Shared layer in `compat/common/`: `TableCraftingDisplayExtractor`, `TableCraftingDisplay`, `RecipeInfoFormatter`, `TomeRecipeClassifier`, `JadeTooltipFormatter`.

### C. fabric.mod.json

- [x] All 9 entrypoints declared (main, client, datagen, gametest, emi, rei_client, jei_mod_plugin, jade, modmenu)
- [x] `suggests` lists all 7 optional deps
- [x] `depends` on fabricloader, minecraft, java, fabric-api
- [x] Mixins and access widener referenced

---

## IMPLEMENTED (Full Apothic Parity)

- Five-stat core system (Eterna/Quanta/Arcana/Rectification/Clues) with 0-100 clamping
- Quanta factor with Gaussian variance; rectification graduates the negative tail
- 27 shelf blocks across all 6 tiers, fully data-driven (31 JSON stat files)
- Custom enchanting table screen with animated stat bars + custom GUI texture
- Clue system (partial + full preview, configurable count via shelves)
- Enchantment selection algorithm (slot levels, power range 1-200, blacklist, treasure gate)
- Filtering shelf (6-slot inventory, `BlacklistSource` interface)
- Treasure shelf (`TreasureFlagSource` marker)
- Infusion recipe system (16 recipes with min/max stat requirements, keep-NBT variant)
- Enchantment Library (2 tiers, `2^(level-1)` points, GUI with search, hopper automation)
- 3 salvage tomes (Scrap/Improved Scrap/Extraction) with anvil dispatch chain
- Prismatic Web curse removal (configurable XP cost)
- Iron-block anvil repair (damaged → chipped → normal)
- `AnvilDispatcher` with 6 handlers and re-entrancy guard
- 3 S2C payloads: Stats + Clues + EnchantmentInfo (synced on join + reload)
- Per-enchantment config (maxLevel, maxLootLevel, levelCap) with server-to-client sync
- Enchantment max level mixin, item enchantability mixin, over-leveled color mixin
- 51 data-driven enchantments (100% pure JSON, zero custom Java effect code)
- 18 advancements with custom `enchanted_at_table` trigger
- 4 custom particle types with themed SGA glyph textures
- 3 music discs with sound events, OGG files, jukebox songs
- 7 third-party integrations (EMI, REI, JEI, Jade, WTHIT, Trinkets, ModMenu)
- Full datapack extensibility (shelves, recipes, enchantments, advancements, tags)
- Comprehensive test infrastructure (61 JUnit + 31 gametests)
- `/fizzleenchanting reload` command with live config + registry rebuild

## GAPS (Remaining Work)

### Critical

~~1. **No mineable block tags** — FIXED~~

### Functional

~~2. **Step-ladder eterna accumulation absent**~~ — FIXED: contributions grouped by maxEterna, sorted ascending, each tier caps running total
3. ~~**Stat bar tooltips too terse**~~ — FIXED: Apothic-aligned tooltips with drawOnLeft panels (quanta warping, arcana rarity weights)
4. ~~**No power range / enchantability / clue count**~~ — FIXED: slot hover drawOnLeft panel shows power range, item enchantability, and clue count
5. ~~**Missing crafting recipes** — filtering shelf, treasure shelf, endshelf, echoing sculkshelf, soul_touched_sculkshelf~~ — FIXED
~~6. **Sculk ambient sounds dead code**~~ — FIXED: `SculkShelfBlock.animateTick()` wires config fields to ambient sounds and particles
~~7. **No tooltips on standalone items**~~ — FIXED: `appendHoverText()` added to prismatic web, 3 tomes, infused breath, warden tendril
~~8. **No configurable power functions**~~ — FIXED: `linear` and `fixed` power function types added to `EnchantmentOverride`
~~9. **No inline enchantment descriptions** client option~~ — FIXED: `display.enableInlineEnchDescs` config option added (default off); shows `.desc` translations inline below each enchantment in item tooltips
~~10. **Filtering shelf per-book dynamic stats**~~ — FIXED: `FilteringShelfBlock.getStats()` override adds +0.5 eterna / +1 arcana per stored book

### Minor

~~11. Arcana guaranteed picks loop fires at 99 (4th pick) — Apothic stops at 66 (3 picks)~~ — FIXED: loop capped at 66 to match Apothic
~~12. Library extract produces books only (not direct item application like Apothic)~~ — INTENTIONAL: Zenith pattern; see Intentionally Different table
~~13. Scrap Tome removes 1 random enchantment (not Apothic's "~half")~~ — INTENTIONAL: Zenith behavior; see Intentionally Different table
~~14. Anvil repair uses iron blocks (Zenith approach) vs Apothic XP-only~~ — INTENTIONAL: Zenith behavior; see Intentionally Different table
~~15. KeepNBT copies `ENCHANTMENTS` component only (not full NBT)~~ — INTENTIONAL: correct for 1.21.1 component model
~~16. DESIGN.md says Eterna 0-50 but code defaults to 0-100 — doc is stale~~ — FIXED: code default changed to 50
~~17. Trinkets API wired but idle (all enchantments are JSON, no Java callers)~~ — INTENTIONAL: no-op integration point for future use

## INTENTIONALLY DIFFERENT (Design Divergences)

| Area | Apothic | Fizzle | Rationale |
|------|---------|--------|-----------|
| Enchantment source | 19 custom enchantments (Java-coded) | 61 total: 49 NeoEnchant+ (JSON) + 10 Apothic ports (9 Java-backed) + 2 Zenith | Larger, more diverse roster; 12/19 Apothic enchants implemented |
| Stability model | Binary `stable` flag from Geode Shelf | Rectification float 0-100 from 3-tier Rectifier shelves | Richer tuning surface; graduated stability |
| Arcana model | Continuous 0-100 weight adjustment | Same (matches Apothic) | — |
| Typed tomes | 9 slot-filtered tomes | Cut entirely | "UX felt counter-intuitive" |
| Eterna scale | maxEterna=100 | maxEterna=50 (config default 100, shelf values halved) | Zenith-derived stat values |
| Mixin strategy | 15+ mixins + 3 ASM coremods | 6 mixins + 0 ASM | Fabric events + data-driven enchantments |
| Library extraction | Direct item application | Book-only (Zenith pattern) | Simpler UX; anvil for application |
| Scrap Tome | Removes ~half enchantments | Removes 1 random enchantment | Zenith behavior |
| Anvil repair | XP cost only | Iron block + 1 level | Zenith behavior |

## FIZZLE-ORIGINAL (Not in Apothic)

- **Rectifier shelves (T1/T2/T3)** — three-tier rectification progression (10/15/25)
- **Rectification as a stat axis** — fifth tracked stat alongside Eterna/Quanta/Arcana/Clues
- **49 NeoEnchant+ enchantments** — mounted combat, mace, elytra, bow elemental, hoe/farming, size modification, and more
- **Bag of Souls** — armor XP multiplier (inspired by MCDungeonsArmors / Prominence 2); stacks across armor slots to ~3x max
- **Non-shelf stat providers** — Amethyst Cluster (+1.5 rectification), Skulls (+5/+10 quanta)
- **Comprehensive recipe viewer support** — EMI + REI + JEI (all three simultaneously)
- **Dual block-info support** — Jade + WTHIT with shared tooltip formatter

## PRIORITY RANKING (Suggested Implementation Order)

### Tier 1 — Critical / Low Effort

- [x] 1. Add mineable block tags for shelves (pickaxe/axe as appropriate)
- [x] 2. Add missing crafting recipes (filtering shelf, treasure shelf, endshelf — infused_breath is available now)
- [x] 3. Fix stale DESIGN.md eterna range — reconciled code default to 50 to match shelf stat scale

### Tier 2 — High Value / Medium Effort

- [x] 4. Enrich stat bar tooltips with descriptive text + drawOnLeft side panels
- [x] 5. Add power range, item enchantability, and clue count to main screen slot tooltips (drawOnLeft panel)
- [x] 6. Implement sculk shelf ambient sounds (wire `randomTick()` to config fields)
- [x] 7. Add `appendHoverText()` to standalone items (tomes, prismatic web, etc.)
- [x] 8. Add remaining crafting recipes (echoing/soul_touched sculkshelf pending warden_tendril availability)

### Tier 3 — Nice to Have (needs design review)

- [x] 9. Implement filtering shelf per-book dynamic stats
- [x] 10. Implement step-ladder eterna accumulation (behavioral change — needs design review)
- [x] 11. Add configurable power functions (simplified: linear + fixed types — no expression engine needed)
- [x] 12. Add inline enchantment descriptions client option
- [x] 13. Fix arcana guaranteed picks at 99 threshold (removed 4th pick to match Apothic)

### Tier 4 — Bug Fixes & Polish

> Discovered via in-game testing 2026-04-28. Screenshots saved in `/mnt/c/Users/colet/Downloads/Screenshot 2026-04-28 00*.png`.

#### GUI / Enchanting Table

- [x] 14. **Eterna bar max inconsistency** — FIXED: Added `maxEterna` to `StatsPayload` so the client receives the real shelf-derived max; bar fill now uses `maxEterna` from stats instead of hardcoded 100; fallback set to 50 matching config default.

- [x] 15. **Weak enchantments at max eterna (level 3 slot)** — FIXED: Two bugs in `RealEnchantmentHelper.getAvailableEnchantmentResults()`: (1) power filtering used `power >= maxPower(level)` instead of Apothic's `power <= maxPower(level)`, causing enchantments to always fall through to minimum level since `DefaultMaxPowerFunction` returns 200; (2) `powerCap` was `maxEterna * 2` (=100) instead of Apothic's hardcoded 200. Both corrected to match Apothic behavior.

#### JADE / WTHIT Integration

- [x] 16. **Old "Ench Power" still showing on enchanting table** — FIXED: `EnchantingTableJadeProvider.appendTooltip()` now calls `tooltip.remove(JadeIds.MC_TOTAL_ENCHANTMENT_POWER)` to suppress the vanilla enchantment power line, matching Apothic's approach.

- [x] 17. **Old "Ench Power" showing on vanilla bookshelves** — FIXED: New `BlockStatsJadeProvider` (client-only, registered for `Block.class`) shows per-block Fizzle stat contributions and suppresses `JadeIds.MC_ENCHANTMENT_POWER`. Vanilla bookshelves resolve via the tag-based `VANILLA_FALLBACK` (eterna: +1 / 15). Also covers custom shelves and non-shelf stat providers (skulls, amethyst clusters).

- [x] 18. **Rectification displays as 7500%** — FIXED: `formatPercent()` was multiplying by 100 but rectification is already in [0, 100] range; removed the double multiplication. Tests updated to use realistic [0, 100] values.

- [x] 19. **No JADE stats on Fizzle Enchanting shelves** — FIXED: `BlockStatsJadeProvider` (added for item 17) is registered for `Block.class` and handles `IEnchantingStatProvider` blocks via instanceof dispatch — custom shelves, filtering shelf, treasure shelf, and non-shelf stat providers (skulls, amethyst clusters) all show per-block stat contributions.

#### EMI / Recipe Viewer Integration

- [x] 20. **"Fizzle Enchanting" label positioning inconsistent** — AUDITED: `EmiEnchantingRecipe.addWidgets()` adds no mod attribution label; `registerShelfInfoPanels()` uses plain `EmiInfoRecipe` with stat lines only. The color/style difference (red vs purple italic) is EMI's built-in mod attribution rendering, which varies by recipe display context — not controllable from our code. Removed duplicate EMI category lang keys (lines 77-78 duplicated lines 19-20 in `en_us.json`).

- [x] 21. **EMI shelf recipe category title overflow** — FIXED: Collapsed the two recipe viewer categories ("Shelves" / "Tomes") into a single "Infusions" category across all three viewers (EMI/REI/JEI). Moved stat-requirement text from x=72 (beside slots) to x=0 (below slots) so lines use the full recipe width instead of being cramped into 72px.

#### Mechanical Decisions (needs design review)

- [x] 22. **XP charging model** — KEPT AS-IS: Fizzle deducts level-based costs that scale with eterna (slot 2 = `round(eterna)` levels, e.g. 50 levels at max eterna). Apothic converts to raw XP points (exponentially more expensive at high levels). Fizzle's model already provides meaningful progression gating via vanilla's `onEnchantmentPerformed`. Documented in "Intentional Mechanical Differences" §1.

- [x] 23. **Rectification vs Stability semantics** — RESOLVED: Rectification is already clamped to `[0, 100]` in `EnchantingStatRegistry.gatherStatsFromOffsets()` (line 191). The 7500% display bug (item 18) was a tooltip formatting issue, not an accumulation bug. Continuous rectification model is intentionally different from Apothic's boolean `stable` flag.

---

## Intentional Mechanical Differences from Apothic

These are places where Fizzle's underlying enchanting system diverges from Apothic by design (or necessity). The GUI is aligned to Apothic's patterns but the display reflects Fizzle's actual mechanics.

1. **XP charging** — Fizzle deducts `slot + 1` levels (vanilla pattern via `player.giveExperienceLevels`). Apothic computes raw XP points via `MiscUtil.getExpCostForSlot` and charges actual experience points. The slot hover panel shows "Level Cost" instead of Apothic's "Raw XP Cost: N (M Levels)".
2. ~~**Item enchantability does not contribute to arcana**~~ — RESOLVED: Fizzle's `applyBaselines` already adds `itemEnchantability / 2` to arcana (matching Apothic). Arcana tooltip now shows "Base Value" (shelves) + "Enchantability Bonus" (item) + "Total Value" breakdown.
3. **Rectification vs Stability** — Apothic has a boolean `stable` flag (quanta variance disabled entirely). Fizzle has a continuous `rectification` stat (0–100%) that truncates the negative tail of quanta variance. The quanta tooltip shows "Rectification" instead of "Quantic Stability".
