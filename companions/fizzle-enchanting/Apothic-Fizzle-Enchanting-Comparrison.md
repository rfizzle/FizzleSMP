# Apothic vs Fizzle Enchanting — Gap Assessment Checklist

> **How to use this document:**
> Each **Phase** is a self-contained assessment unit. Phases can be run **iteratively** (one at a time) or **in parallel** (multiple agents, each assigned a phase). Each phase lists:
> - **Scope:** What Apothic feature area is being compared
> - **Apothic reference:** Files/sections in `Apothic-Enchanting-Design.md` and `~/Projects/Apothic-Enchanting` to consult
> - **Fizzle scan target:** Where to look in `~/Projects/FizzleSMP/companions/fizzle-enchanting`
> - **Checklist:** Specific items to evaluate. Mark each: `[x]` implemented, `[~]` partial, `[-]` not implemented, `[N/A]` intentionally skipped
> - **Output:** A summary block at the end of each phase for findings
>
> **Important:** Each phase is designed to be evaluable with ONLY the Fizzle Enchanting codebase + the Apothic design doc loaded. You do NOT need the full Apothic source for most phases — the design doc covers it. Only consult Apothic source files when the checklist explicitly says to.

---

## Phase 1: Project Structure & Build Setup

**Scope:** Mod skeleton, dependencies, mod loader, entry points, registration pattern

**Apothic reference:** `Apothic-Enchanting-Design.md` §1 (overview), §19 (file index)

**Fizzle scan target:**
- `build.gradle` / `gradle.properties`
- `src/main/resources/fabric.mod.json`
- `src/main/java/**/` — top-level package structure
- Any `CLAUDE.md`, `PLAN.md`, or `README.md`

**Checklist:**
- [ ] Mod ID, version, and Minecraft target version documented
- [ ] Fabric mod metadata present (`fabric.mod.json`) with correct entrypoints
- [ ] Dependencies declared (Fabric API, any library mods)
- [ ] Main mod initializer class exists (server-side)
- [ ] Client mod initializer class exists (client-side)
- [ ] Registration pattern identified (how are blocks, items, screens registered?)
- [ ] Package structure mapped (list all packages and their apparent purpose)
- [ ] Test infrastructure present? (src/test, gametest, JUnit)
- [ ] Mixin configuration present? (`*.mixins.json`)
- [ ] Networking infrastructure present? (custom packets/payloads)

**Output:**
```
Structure summary: (fill after assessment)
Registration pattern: 
Key differences from Apothic:
Blockers/concerns:
```

---

## Phase 2: Enchanting Table — Core Stat System

**Scope:** Whether Fizzle implements the three-stat model (Eterna/Quanta/Arcana or equivalent), how stats are gathered, clamped, and computed

**Apothic reference:** `Apothic-Enchanting-Design.md` §2 (stats), §4 (stat gathering process)

**Apothic source (if needed):**
- `table/EnchantmentTableStats.java` — record definition, Builder, step-ladder algorithm
- `table/EnchantingStatRegistry.java` — JSON-driven stat lookup

**Fizzle scan target:**
- Any class with "stat", "eterna", "quanta", "arcana", "power", "enchant" in the name
- Any record or data class holding enchanting table state
- Any builder or accumulator pattern for stats
- `src/main/resources/data/**/` — any JSON stat definition files

**Checklist:**
- [ ] Primary stat equivalent to Eterna exists (enchanting power/level)
- [ ] Primary stat equivalent to Quanta exists (volatility/randomness)
- [ ] Primary stat equivalent to Arcana exists (rarity quality)
- [ ] Stats are clamped to a defined range (what range?)
- [ ] Step-ladder / max-cap algorithm for Eterna accumulation exists
- [ ] Default values match or differ intentionally (Quanta default=15, Arcana default=itemEnch/2, Clues default=1)
- [ ] Stats record/class is network-serializable (for client sync)
- [ ] Stats are gathered from block positions around the table (vanilla offsets)
- [ ] Air/transmitter check between table and shelf block exists
- [ ] Stats are data-driven (JSON) vs hardcoded

**Output:**
```
Stat model: (three-stat / two-stat / vanilla / custom)
Stat names used:
Range/clamping:
Accumulation algorithm:
Data-driven: (yes/no/partial)
Key gaps:
```

---

## Phase 3: Enchanting Table — Selection Algorithm

**Scope:** How enchantments are selected once stats are known — slot levels, quanta factor, arcana weighting, guaranteed enchantments, blacklisting, treasure filtering

**Apothic reference:** `Apothic-Enchanting-Design.md` §1 (slot level calc), §2.2 (quanta factor formula), §2.3 (arcana tiers/weights), §2.4 (secondary stats)

**Apothic source (if needed):**
- `table/ApothEnchantmentHelper.java` — full selection algorithm
- `table/Arcana.java` — tier enum and weight table

**Fizzle scan target:**
- Any class with "helper", "selection", "select", "enchant" in the name
- Any method that produces `List<EnchantmentInstance>` or equivalent
- Any rarity/weight adjustment logic
- Any blacklist or filter logic applied during selection

**Checklist:**
- [ ] Slot level calculation exists (3 slots with different level ranges)
- [ ] Slot 2 = full eterna, Slot 1 = 60-80%, Slot 0 = 20-40% (or equivalent)
- [ ] Quanta factor calculation exists (random variance on power)
- [ ] Stable vs unstable quanta distinction exists
- [ ] Power range is 1-200 (or defined max)
- [ ] Arcana tier system exists (rarity weight adjustment)
- [ ] Number of arcana tiers defined (Apothic has 11)
- [ ] Guaranteed extra enchantments from arcana (1 at 0, 2 at 33, 3 at 66)
- [ ] Random additional enchantments with diminishing probability
- [ ] Blacklist filtering applied before selection
- [ ] Treasure enchantment filtering (only if treasure=true)
- [ ] `EnchantableItem` / item-specific post-processing hook exists
- [ ] Enchantment compatibility check (remove incompatible after each pick)

**Output:**
```
Selection algorithm: (custom / vanilla-modified / vanilla)
Quanta model:
Arcana model:
Blacklist support:
Treasure support:
Key gaps:
```

---

## Phase 4: Enchanting Table — GUI / Screen

**Scope:** The client-side enchanting screen — stat bars, tooltips, info button, clue display, infusion display

**Apothic reference:** `Apothic-Enchanting-Design.md` §3 (GUI)

**Apothic source (if needed):**
- `table/ApothEnchantmentScreen.java` — rendering, tooltips, mouse handling
- `assets/apothic_enchanting/textures/gui/enchanting_table.png`

**Fizzle scan target:**
- Any class extending `HandledScreen`, `Screen`, or with "Screen" in name
- `src/main/resources/assets/**/textures/gui/` — GUI textures
- `src/main/java/**/client/screen/` or similar
- Lang file entries for GUI labels

**Checklist:**
- [ ] Custom enchanting table screen class exists (replaces or extends vanilla)
- [ ] Custom GUI texture exists (`enchanting_table.png` or equivalent)
- [ ] Stat bars rendered (one per stat, colored, scaled to value)
- [ ] Stat bar animations (smooth interpolation on value change)
- [ ] Stat bar hover tooltips with descriptions
- [ ] Eterna tooltip: shows current/max, describes purpose
- [ ] Quanta tooltip: shows value, stability status, power range
- [ ] Arcana tooltip: shows value, item bonus, weight table, guaranteed enchantments
- [ ] Side-panel tooltips (`drawOnLeft` style) for quanta buff and arcana bonus
- [ ] Enchantment slot hover: shows clue enchantments
- [ ] Clue system: partial vs full clue display
- [ ] Info button (opens detailed enchantment browser)
- [ ] Infusion display in slot 2 when infusion recipe matches
- [ ] "Infusion Failed" display when item is infusable but stats don't match
- [ ] XP cost display in tooltip (both points and levels)
- [ ] Power range display in tooltip (min to max, based on quanta)
- [ ] Item enchantability display
- [ ] Number of clues display

**Output:**
```
Screen implementation: (custom / mixin-extended / vanilla)
GUI texture: (custom / vanilla)
Stat bars: (yes/no, which stats)
Tooltip depth: (none / basic / rich)
Key gaps:
```

---

## Phase 5: Shelf Blocks — Registry & Stats

**Scope:** Custom bookshelf/shelf blocks, their stats, registration, textures, models, blockstates

**Apothic reference:** `Apothic-Enchanting-Design.md` §4 (shelf system, complete stats table)

**Fizzle scan target:**
- Any block class with "shelf", "bookshelf", "library" in name
- `src/main/resources/data/**/` — any enchanting stat JSON files
- `src/main/resources/assets/**/blockstates/` — shelf block states
- `src/main/resources/assets/**/models/block/` — shelf models
- `src/main/resources/assets/**/textures/blocks/` — shelf textures
- Registration code for blocks

**Checklist:**
- [ ] Custom shelf block base class exists
- [ ] Shelf blocks provide stats (eterna, quanta, arcana, clues)
- [ ] Stats are data-driven (JSON files) vs hardcoded
- [ ] maxEterna cap per shelf type exists
- [ ] Shelf count and tier breakdown:
  - [ ] Starter tier shelves (equiv. to Stoneshelf, Beeshelf, Melonshelf)
  - [ ] Early tier (equiv. to Hellshelf, Seashelf — maxE 45)
  - [ ] Mid tier (equiv. to Infused variants — maxE 60)
  - [ ] Late tier (equiv. to Deepshelf, Sculkshelf — maxE 70-80)
  - [ ] End tier (equiv. to Endshelf — maxE 90)
  - [ ] Max tier (equiv. to Draconic Endshelf — maxE 100)
- [ ] Negative stat shelves exist (for precise tuning)
- [ ] Blockstates defined for all shelves
- [ ] Block models defined for all shelves
- [ ] Block textures present for all shelves
- [ ] Blocks registered in creative tab
- [ ] Crafting recipes for all shelves
- [ ] Blocks have appropriate mining tool/level requirements

**Output:**
```
Total shelf blocks: (count)
Tier coverage: (which tiers exist)
Data-driven stats: (yes/no)
Missing tiers:
Missing assets:
```

---

## Phase 6: Special Shelves — Filtering, Treasure, Stability

**Scope:** Shelves with special behaviors beyond simple stat contribution

**Apothic reference:** `Apothic-Enchanting-Design.md` §5 (special shelves)

**Apothic source (if needed):**
- `objects/FilteringShelfBlock.java`
- `objects/TreasureShelfBlock.java`
- `objects/GeodeShelfBlock.java`

**Fizzle scan target:**
- Any shelf block with inventory slots (for filtering)
- Any shelf that sets boolean flags (treasure, stability)
- Any block entity associated with a shelf

**Checklist:**
- [ ] Filtering shelf exists (blacklists enchantments via stored books)
  - [ ] Has internal inventory (6 slots or equivalent)
  - [ ] Reads enchantments from stored books
  - [ ] Adds read enchantments to table blacklist
  - [ ] Has block entity for inventory persistence
- [ ] Treasure shelf exists (enables treasure enchantments)
  - [ ] Sets treasure flag on table stats
- [ ] Stability shelf exists (prevents negative quanta)
  - [ ] Sets stable flag on table stats
- [ ] Sculk shelves with ambient sounds exist
  - [ ] Configurable sound chance

**Output:**
```
Filtering: (implemented / partial / missing)
Treasure: (implemented / partial / missing)
Stability: (implemented / partial / missing)
Ambient effects: (implemented / partial / missing)
Key gaps:
```

---

## Phase 7: Infusion Recipe System

**Scope:** Stat-gated item transformation at the enchanting table (not normal enchanting)

**Apothic reference:** `Apothic-Enchanting-Design.md` §6 (infusion system, recipe list, recipe format)

**Apothic source (if needed):**
- `table/infusion/InfusionRecipe.java`
- `data/apothic_enchanting/recipe/infusion/*.json`

**Fizzle scan target:**
- Any custom recipe type class
- Any recipe serializer registration
- `src/main/resources/data/**/recipe/` — recipe JSON files
- Any code that checks table stats before producing a non-enchantment result
- Menu/container code that handles infusion vs normal enchanting branching

**Checklist:**
- [ ] Custom recipe type exists (equivalent to `infusion`)
- [ ] Recipe serializer/codec registered
- [ ] Recipe format supports min requirements (eterna, quanta, arcana)
- [ ] Recipe format supports max requirements (upper bounds)
- [ ] Keep-NBT variant exists (for upgrades that preserve data)
- [ ] Recipe matching: sorted by eterna desc, first match wins
- [ ] GUI integration: infusion display in enchanting slot
- [ ] Infusion recipe JSON files present (how many?)
- [ ] Shelf upgrade recipes (e.g., base shelf -> infused shelf)
- [ ] Item transformation recipes (e.g., honey bottle -> XP bottle)
- [ ] Tiered recipes (same input, different output based on stats)
- [ ] JEI/REI/EMI integration for viewing infusion recipes

**Output:**
```
Infusion system: (implemented / partial / missing)
Recipe count:
Recipe format:
GUI integration:
Recipe viewer integration:
Key gaps:
```

---

## Phase 8: Enchantment Library

**Scope:** Block for storing and extracting enchantments using a point system

**Apothic reference:** `Apothic-Enchanting-Design.md` §7 (library system, point formula, two tiers)

**Apothic source (if needed):**
- `library/EnchLibraryTile.java` — point system, deposit/extract logic
- `library/EnchLibraryScreen.java` — GUI

**Fizzle scan target:**
- Any block/class with "library" in name
- Any block entity storing enchantment data (point maps, level maps)
- Any screen/GUI for library interaction
- `src/main/resources/assets/**/textures/gui/library.png` or equivalent

**Checklist:**
- [ ] Library block exists
- [ ] Library block entity with enchantment storage
- [ ] Point system: `2^(level-1)` or equivalent formula
- [ ] Deposit operation: enchanted book -> extract enchantments -> add points
- [ ] Max level tracking per enchantment
- [ ] Extract operation: select enchantment + level -> apply to item -> deduct points
- [ ] Extraction validation: can't extract above max deposited level, can't exceed points
- [ ] Two tiers (basic library + ender/upgraded library)
- [ ] NBT serialization of points and max levels maps
- [ ] Network sync (block entity update packets)
- [ ] Library GUI/screen exists
  - [ ] Shows stored enchantments with point values
  - [ ] Shows max extractable level per enchantment
  - [ ] Allows enchantment + level selection
  - [ ] Has deposit slot and target item slot
- [ ] Automation support (IItemHandler / hopper interaction)
  - [ ] Accepts enchanted books via hopper
  - [ ] No extract via hopper (GUI only)
- [ ] Library -> Ender Library upgrade path (infusion recipe preserving data)

**Output:**
```
Library system: (implemented / partial / missing)
Tiers: (count)
Point formula:
GUI: (exists / missing)
Automation: (yes / no)
Key gaps:
```

---

## Phase 9: Tomes

**Scope:** Slot-filtered enchanting books and enchantment manipulation items

**Apothic reference:** `Apothic-Enchanting-Design.md` §8 (tomes)

**Fizzle scan target:**
- Any item class with "tome" in name
- Any item implementing an enchantable-item interface
- Items that convert to enchanted books on use
- Anvil event handling for tome operations

**Checklist:**
- [ ] Tome base class exists (accepts enchantments filtered by slot)
- [ ] Tome variants (how many of 9: helmet, chest, legs, boots, sword, pickaxe, bow, fishing, other)
- [ ] Tomes convert to enchanted books when used (right-click)
- [ ] "Other" tome accepts enchantments not matching any specialized tome
- [ ] Scrapping tome (removes ~half enchantments, outputs book)
- [ ] Improved scrapping tome
- [ ] Extraction tome (clean extraction)
- [ ] Tome tooltips explaining their purpose
- [ ] Crafting recipes for all tomes

**Output:**
```
Tome system: (implemented / partial / missing)
Tome count:
Scrapping/extraction: (implemented / partial / missing)
Key gaps:
```

---

## Phase 10: Anvil Modifications

**Scope:** Changes to anvil behavior — curse removal, anvil repair, tome integration

**Apothic reference:** `Apothic-Enchanting-Design.md` §9 (anvil)

**Fizzle scan target:**
- Any mixin targeting `AnvilMenu` or `AnvilScreen`
- Any event handler for anvil updates
- Items like "prismatic web" or curse-removal items
- Any anvil repair logic (chipped -> damaged -> normal)

**Checklist:**
- [ ] Curse removal mechanic exists (item + special material = curse-free item)
  - [ ] Curse removal item exists (equiv. to Prismatic Web)
  - [ ] Cost defined (Apothic: 30 levels)
- [ ] Anvil self-repair exists (repair anvil damage state)
- [ ] Tome-anvil integration (scrapping/extraction via anvil)
- [ ] Custom anvil XP cost calculations

**Output:**
```
Anvil modifications: (implemented / partial / missing)
Curse removal: (yes / no)
Anvil repair: (yes / no)
Tome integration: (yes / no)
Key gaps:
```

---

## Phase 11: Custom Enchantments

**Scope:** New enchantments added by the mod

**Apothic reference:** `Apothic-Enchanting-Design.md` §10 (20 enchantments, full table)

**Fizzle scan target:**
- `src/main/resources/data/**/enchantment/` — enchantment JSON definitions
- Any Java class implementing enchantment effects
- Any custom data component types for enchantment data
- Any custom entity effect types
- Any custom `LevelBasedValue` types
- Event handlers applying enchantment effects (on hit, on block break, on heal, etc.)

**Checklist:**
For each Apothic enchantment, check if Fizzle has an equivalent:
- [ ] Berserker's Fury (curse, buffs + health cost)
- [ ] Boon of the Earth (bonus drops from mining)
- [ ] Chainsaw (tree felling)
- [ ] Chromatic (random wool color)
- [ ] Crescendo of Bolts (multi-shot crossbow)
- [ ] Endless Quiver (infinite arrows)
- [ ] Growth Serum (instant wool regrowth)
- [ ] Icy Thorns (thorns + freeze)
- [ ] Knowledge of the Ages (drops -> XP)
- [ ] Life Mending (healing -> durability)
- [ ] Miner's Fervor (fast mining, capped)
- [ ] Nature's Blessing (auto bone meal)
- [ ] Rebounding (knockback on damage taken)
- [ ] Reflective Defenses (reflect blocked damage)
- [ ] Scavenger (extra loot rolls)
- [ ] Shield Bash (knockback on block)
- [ ] Stable Footing (no flying mining penalty)
- [ ] Tempting (animals follow)
- [ ] Worker Exploitation (double wool, damages sheep)
- [ ] Any Fizzle-original enchantments not in Apothic?

**Output:**
```
Enchantments matching Apothic: (count/20)
Fizzle-original enchantments: (list)
Implementation depth per enchantment: (data-driven / code-driven / both)
Key gaps:
```

---

## Phase 12: Enchantment Configuration & Level Scaling

**Scope:** Per-enchantment config (max levels, loot levels, power functions), hard caps, client config

**Apothic reference:** `Apothic-Enchanting-Design.md` §11 (configuration)

**Fizzle scan target:**
- Any config class or config file generation
- Any per-enchantment data structure (max level overrides, power curves)
- Any IMC or API for external mods to set caps
- Client-side config options

**Checklist:**
- [ ] Per-enchantment max level override exists
- [ ] Per-enchantment max loot level exists (separate from table max)
- [ ] Hard level cap system exists
- [ ] Custom power functions (min/max power per enchantment per level)
- [ ] Power functions are configurable (expressions or data-driven)
- [ ] Client config: show enchanted book metadata
- [ ] Client config: sculk shelf noise chance
- [ ] Client config: inline enchantment descriptions
- [ ] Config synced from server to client

**Output:**
```
Config system: (implemented / partial / missing)
Per-enchantment config: (yes / no)
Power functions: (custom / vanilla)
Key gaps:
```

---

## Phase 13: Particles & Ambience

**Scope:** Custom particle types for shelf blocks, music discs

**Apothic reference:** `Apothic-Enchanting-Design.md` §12 (particles, music discs)

**Fizzle scan target:**
- Any particle type registration
- `src/main/resources/assets/**/particles/` — particle definitions
- Any music disc / jukebox song registration
- Any sound event registration

**Checklist:**
- [ ] Custom particle types registered (fire, water, sculk, end — or equivalents)
- [ ] Particle definitions (JSON) present
- [ ] Particles flow toward enchanting table (motion logic)
- [ ] Shelf blocks spawn their themed particles
- [ ] Music discs exist (Eterna, Quanta, Arcana — or equivalents)
- [ ] Sound events registered
- [ ] Jukebox songs registered

**Output:**
```
Custom particles: (count)
Music discs: (count)
Particle theming per shelf: (yes / no)
Key gaps:
```

---

## Phase 14: Advancements

**Scope:** Progression advancements for the enchanting system

**Apothic reference:** `Apothic-Enchanting-Design.md` §13 (18 advancements)

**Fizzle scan target:**
- `src/main/resources/data/**/advancement/` — advancement JSON files
- Any custom advancement trigger classes
- Any criteria trigger registration

**Checklist:**
- [ ] Custom advancement trigger exists (e.g., "enchanted item at table")
- [ ] Root advancement for enchanting system
- [ ] Shelf placement advancements (per tier)
- [ ] Stat milestone advancements (eterna thresholds, arcana thresholds, quanta thresholds)
- [ ] Configuration milestones (stable setup, max stats)
- [ ] Total advancement count

**Output:**
```
Advancements: (count)
Custom triggers: (count)
Progression depth: (none / basic / full tree)
Key gaps:
```

---

## Phase 15: API Surface

**Scope:** Public interfaces for addon/integration, extensibility points

**Apothic reference:** `Apothic-Enchanting-Design.md` §14 (API)

**Fizzle scan target:**
- Any package named "api" or interfaces clearly meant for external use
- Any block interface for providing stats
- Any item interface for custom enchantment selection

**Checklist:**
- [ ] `EnchantmentStatBlock` equivalent (block stat provider interface)
  - [ ] Methods for eterna, quanta, arcana, clues, blacklist, treasure, stability
  - [ ] Default implementations
- [ ] `EnchantableItem` equivalent (item enchantment customization)
  - [ ] Method for post-processing selected enchantments
  - [ ] Method for applying enchantments (transmute item)
- [ ] Static registry/lookup for querying block stats
- [ ] IMC or event-based extensibility for caps/overrides
- [ ] API documented (javadoc or separate doc)

**Output:**
```
API surface: (rich / minimal / none)
Block stat interface: (yes / no)
Item enchant interface: (yes / no)
Extensibility: (IMC / events / none)
Key gaps:
```

---

## Phase 16: Network & Client-Server Sync

**Scope:** Custom packets for syncing enchanting state to the client

**Apothic reference:** `Apothic-Enchanting-Design.md` §15 (network packets)

**Fizzle scan target:**
- Any class in a "network", "packet", "payload" package
- Any `CustomPayload` or `Packet` implementations
- Any codec/stream codec definitions for custom data
- Channel registration in mod initializer

**Checklist:**
- [ ] Stats payload (server -> client, table stats for GUI rendering)
- [ ] Clue payload (server -> client, enchantment previews)
- [ ] Enchantment info payload (server -> client, per-enchantment config sync)
- [ ] Payloads use proper codec serialization
- [ ] Payloads registered on correct channels
- [ ] Client handlers update screen state from payloads

**Output:**
```
Custom payloads: (count)
Stats sync: (yes / no)
Clue sync: (yes / no)
Config sync: (yes / no)
Key gaps:
```

---

## Phase 17: Data-Driven vs Hardcoded Audit

**Scope:** Which systems are data-driven (JSON, datapack-overridable) vs hardcoded in Java

**Apothic reference:** `Apothic-Enchanting-Design.md` §16 (data-driven design table)

**Fizzle scan target:** Entire `src/main/resources/data/` tree + Java source for hardcoded values

**Checklist:**
- [ ] Shelf stats: data-driven (JSON) or hardcoded?
- [ ] Infusion recipes: data-driven or hardcoded?
- [ ] Enchantments: data-driven (1.21 format) or hardcoded?
- [ ] Crafting recipes: data-driven or hardcoded?
- [ ] Advancements: data-driven or hardcoded?
- [ ] Tags: properly used for item/block grouping?
- [ ] Arcana tiers/weights: data-driven or hardcoded?
- [ ] Loot tables: data-driven or hardcoded?
- [ ] Can datapacks override/extend shelf stats?
- [ ] Can datapacks add new infusion recipes?

**Output:**
```
Data-driven systems: (list)
Hardcoded systems: (list)
Datapack extensibility: (full / partial / none)
Key gaps:
```

---

## Phase 18: Vanilla Modification — Mixins & Hooks

**Scope:** How vanilla behavior is intercepted — mixins, events, ASM

**Apothic reference:** `Apothic-Enchanting-Design.md` §17 (mixins table, ASM hooks)

**Fizzle scan target:**
- `src/main/resources/*.mixins.json` — mixin config
- `src/main/java/**/mixin/` — mixin classes
- Any event handler classes

**Checklist:**
- [ ] Enchanting table screen replacement (mixin or screen factory)
- [ ] Enchanting table menu replacement (mixin or menu factory)
- [ ] Enchantment max level override hook
- [ ] Anvil menu modification
- [ ] Block stat check hook (all blocks implement stat interface)
- [ ] Item enchantability override
- [ ] Enchantment text color for above-max levels
- [ ] Event handlers for: drops, healing, shield block, block break, entity interaction
- [ ] Total mixin count and targets

**Output:**
```
Mixins: (count, list targets)
Event handlers: (count, list events)
Vanilla replacement strategy: (mixin / event / both)
Key gaps:
```

---

## Phase 19: Items & Miscellaneous Content

**Scope:** All items, special blocks, damage types, tags, loot modifiers not covered in other phases

**Apothic reference:** `Apothic-Enchanting-Design.md` §8 (tomes), §10 (enchantments), §19 (file index — Items section)

**Fizzle scan target:**
- All registered items (grep registrations)
- All registered blocks (beyond shelves and library)
- All tags (item, block, entity type)
- Damage types
- Loot modifiers
- Creative tab registration

**Checklist:**
- [ ] Prismatic Web (curse removal material)
- [ ] Inert Trident (infusion input for trident crafting)
- [ ] Infused Breath (crafting material)
- [ ] Warden Tendril (loot modifier drop)
- [ ] Ender Lead (3 tiers: flimsy, normal, occult)
- [ ] Music discs (3: eterna, quanta, arcana)
- [ ] Custom damage type (corrupted)
- [ ] Custom tags defined (list all)
- [ ] Loot modifiers (warden drops, boon drops)
- [ ] Creative tab with all mod items
- [ ] Item tooltips for all custom items

**Output:**
```
Items matching Apothic: (count)
Fizzle-original items: (list)
Missing items:
Tags defined: (count)
Key gaps:
```

---

## Phase 20: Third-Party Integration

**Scope:** Recipe viewer, block info, trinket slot compatibility

**Apothic reference:** `Apothic-Enchanting-Design.md` §17 (integrations table)

**Fizzle scan target:**
- Any "compat" package or conditional class loading
- `fabric.mod.json` — suggests/recommends fields
- Any REI/EMI plugin classes
- Any WTHIT/Jade plugin classes
- Any Trinkets/Accessories integration

**Checklist:**
- [ ] Recipe viewer integration (REI or EMI — Fabric equivalent of JEI)
  - [ ] Infusion recipe category
  - [ ] Custom recipe display
- [ ] Block info integration (WTHIT or Jade)
  - [ ] Shelf stat display on hover
- [ ] Trinket/accessory slot support (Trinkets or Accessories API)
- [ ] Mod menu integration (config screen)

**Output:**
```
Recipe viewer: (REI / EMI / none)
Block info: (WTHIT / Jade / none)
Trinkets: (yes / no)
Key gaps:
```

---

## Assessment Summary (completed 2026-04-26)

### Status by Phase

| Phase | Area | Status | Parity |
|-------|------|--------|--------|
| 1 | Project Structure & Build | [x] Complete | 100% |
| 2 | Core Stat System | [x] Complete | 100% |
| 3 | Selection Algorithm | [x] Complete | ~95% (arcana model differs by design) |
| 4 | GUI / Screen | [x] Complete | ~85% (no info button, no "infusion failed" display) |
| 5 | Shelf Blocks | [x] Complete | 100% (31 shelves, all tiers, all data-driven) |
| 6 | Special Shelves | [x] Complete | ~90% (rectification float vs stability boolean) |
| 7 | Infusion Recipes | [~] Partial | ~70% (system done, 7 recipes vs Apothic's 20) |
| 8 | Library | [x] Complete | 100% (2 tiers, points, GUI, hopper, NBT) |
| 9 | Tomes | [~] Partial | ~35% (3 scrapping/extraction tomes; no 9 slot-filtered tomes) |
| 10 | Anvil | [x] Complete | 100% (curse removal, repair, tome handlers) |
| 11 | Custom Enchantments | [x] Different | 49 NeoEnchant+ ports (not Apothic's 20) |
| 12 | Config & Level Scaling | [~] Partial | ~40% (global config, no per-enchant overrides) |
| 13 | Particles & Ambience | [~] Partial | ~30% (vanilla particles via theme enum, no discs) |
| 14 | Advancements | [~] Partial | ~55% (10 of ~18, vanilla triggers only) |
| 15 | API Surface | [~] Minimal | ~25% (marker interfaces, no EnchantableItem, no IMC) |
| 16 | Network Sync | [x] Complete | ~85% (StatsPayload + CluesPayload, no config sync) |
| 17 | Data-Driven Audit | [x] Complete | ~90% (all JSON-driven except arcana weights) |
| 18 | Mixins & Hooks | [x] Complete | Lean (4 mixins vs Apothic's 18; Fabric API covers the rest) |
| 19 | Items & Misc | [~] Partial | ~60% (missing Inert Trident, Ender Leads, Music Discs) |
| 20 | Third-Party Integration | [x] Complete | ~90% (EMI+REI+JEI+Jade; no WTHIT, no ModMenu) |

---

### IMPLEMENTED (matching Apothic)

- Three-stat core system (Eterna/Quanta/Arcana) with 0-100 clamping
- Step-ladder Eterna accumulation algorithm (maxEterna caps per shelf group)
- Quanta factor with Gaussian variance model
- 31 shelf blocks across all 6 tiers (Starter through Max), fully data-driven (31 JSON stat files)
- Custom enchanting table screen with animated stat bars (Eterna/Quanta/Arcana)
- Clue system (partial + full preview, configurable count via shelves)
- Enchantment selection algorithm (slot levels, power range 1-200, blacklist, treasure gate)
- Filtering shelf (6-slot inventory, blacklist propagation via BlacklistSource interface)
- Treasure shelf (treasure flag via TreasureFlagSource interface)
- Sculk shelves with ambient particle effects (configurable chance)
- Enchanting recipe system (custom recipe types with min/max stat requirements)
- Keep-NBT recipe variant (library upgrade preserves stored enchantments)
- Enchantment Library — 2 tiers (Basic maxLevel=16, Ender maxLevel=31), 2^(level-1) points
- Library GUI with deposit/extract/search, hopper automation (deposit-only)
- Scrap / Improved Scrap / Extraction tomes with anvil handler chain
- Prismatic Web curse removal (configurable XP cost)
- Iron-block anvil repair (damaged → chipped → normal)
- AnvilDispatcher pattern with handler priority
- StatsPayload + CluesPayload network sync with codec serialization
- Recipe viewer integration (EMI + REI + JEI — all three)
- Jade block info integration (enchanting table + library stat display)
- Creative tab with all mod items
- 10 advancements (root → shelf tiers → library → apotheosis)
- 19 custom tags (11 enchantment exclusive sets, 5 item, 1 block, 1 entity type)
- Full datapack extensibility (shelf stats, recipes, enchantments, advancements, tags)

### PARTIAL (started but incomplete)

- **Arcana weighting** — continuous 0-100 model instead of Apothic's discrete 11-tier enum with guaranteed enchantment counts at 33/66. Simpler but less player-visible progression.
- **Infusion recipe coverage** — 7 recipes (shelf upgrades, tome tier-ups, infused breath, ender library) vs Apothic's 20 (missing: honey→XP bottles, echo shard duplication, music disc conversions, ender leads, golden carrot, budding amethyst).
- **Stability model** — Rectification is a float stat (0-100) contributed by Rectifier shelves (T1/T2/T3) instead of a binary `stable` flag from a Geode Shelf. Functionally richer but different API shape.
- **Config system** — Server-side JSON config with 8 sections; no per-enchantment overrides, no power functions, no config sync to client.
- **Particles** — ParticleTheme enum maps 5 themes to vanilla particle types. No custom particle type registration, no particle JSON definitions.
- **Advancements** — 10 vs ~18. Missing stat milestone advancements (Eterna 60/80/100, Arcana 50/100, Quanta thresholds). No custom trigger class.
- **API surface** — Marker interfaces (IEnchantingStatProvider, TreasureFlagSource, BlacklistSource) + EnchantingStatRegistry lookup. No EnchantableItem interface, no IMC channel.
- **Tooltips** — Cost/clues/enchantability shown. No rarity weight table tooltip (by design). No enchantment info browser screen.

### NOT IMPLEMENTED (gaps)

- **Enchantment info browser** — No info button on enchanting screen, no EnchantingInfoScreen equivalent
- **9 slot-filtered tomes** — Helmet/Chest/Legs/Boots/Weapon/Pickaxe/Bow/Fishing/Other tomes not implemented (intentional cut)
- **Inert Trident item** — No trident infusion path
- **Ender Lead (3 tiers)** — Flimsy/Normal/Occult ender leads not implemented
- **Music Discs (3)** — Eterna/Quanta/Arcana discs not implemented
- **Custom particle types** — Using vanilla particles only (FLAME, SPLASH, PORTAL, SCULK_SOUL, ENCHANT)
- **Custom sound events** — No jukebox songs, no custom sculk sounds
- **Corrupted damage type** — Not implemented
- **Per-enchantment config** — No max level overrides, no max loot level, no hard caps per enchant, no power functions
- **EnchantmentInfoPayload** — No config sync to client
- **Enchantment max level override mixin** — Cannot enforce hard level caps at the enchantment class level
- **Item enchantability global override** — No mixin on Item#getEnchantability()
- **Enchantment text color for above-max levels** — Tooltip recoloring exists but no mixin-level color override
- **WTHIT integration** — Jade only
- **ModMenu config GUI** — No config screen
- **Trinket/Accessory API** — Not integrated
- **fabric.mod.json suggests/recommends** — Missing optional dependency declarations

### INTENTIONALLY DIFFERENT (design divergences)

- **Enchantment source:** 49 NeoEnchant+ ports + 2 authored (Icy Thorns, Shield Bash) instead of Apothic's 20 custom enchantments. Only 3 overlap. This is a deliberate design choice per DESIGN.md.
- **Arcana model:** Continuous 0-100 weight adjustment instead of discrete 11-tier enum. No guaranteed enchantment count thresholds at arcana 33/66. Simpler, less tooltip-heavy.
- **Stability → Rectification:** Float stat axis (0-100) contributed by 3-tier Rectifier shelves instead of boolean flag from Geode Shelf. Richer tuning surface.
- **Tome system:** Cut 9 typed tomes entirely. Scrapping/extraction tomes serve a different purpose (strip enchants from gear) vs Apothic's typed tomes (filter enchants onto gear by slot).
- **Mixin strategy:** 4 lean mixins + Fabric API vs Apothic's 18 mixins + ASM coremods. Fabric's event system and datapack approach cover most vanilla hooks without bytecode manipulation.
- **Eterna range:** maxEterna defaults scaled differently (50 vs 100). Shelf stat values halved relative to Apothic (maxE 50 Draconic vs 100).

### FIZZLE-ORIGINAL (not in Apothic)

- **Rectifier shelves (T1/T2/T3)** — Three-tier rectification shelf progression (10/15/20 rectification). Apothic has only a binary Geode Shelf.
- **Rectification as a stat axis** — Fifth tracked stat alongside Eterna/Quanta/Arcana/Clues. Synced via StatsPayload, displayed in UI.
- **49 NeoEnchant+ enchantments** — Including: Vein Miner, Step Assist, Fast Swim, Lava Walker, Ethereal Leap, Builder Arm, Attack Speed, Critical, Death Touch, Dimensional Hit, Echo Shot, Explosive Arrow, Fear, Harvest, Midas Touch, Oversize, Poison Aspect, Pull, Reach, Scyther, Storm Arrow, Striker, Tears of Asflors, Teluric Wave, Velocity, Voidless, Wind Propulsion, XP Boost, and many more.
- **Warden Tendril + Infused Breath** — Custom crafting materials with loot integration (Warden drop) and infusion recipe (Dragon Breath → Infused Breath). Apothic has similar items but different recipes.

---

### PRIORITY RANKING (suggested implementation order for gaps)

**Tier 1 — High value, low effort:**
1. Add `suggests`/`recommends` to `fabric.mod.json` for optional deps (EMI, REI, JEI, Jade)
2. Add 8 more stat milestone advancements (Eterna 30/50, Arcana 50, Quanta 0/50, stable setup, max stats)
3. Add missing infusion recipes (honey→XP bottles, echo shard, golden carrot, budding amethyst)

**Tier 2 — Medium value, medium effort:**
4. Custom particle type registration (4 types: fire, water, sculk, end) with proper particle JSONs
5. Music discs (3 items + 3 infusion recipes + 3 sound events)
6. ModMenu config GUI integration
7. Custom advancement trigger for "enchanted at table" (richer criteria than vanilla)

**Tier 3 — Nice to have, can defer:**
8. Enchantment info browser screen (info button on enchanting GUI)
9. "Infusion Failed" display when item is infusable but stats don't match
10. WTHIT integration as Jade fallback
11. Inert Trident + infusion recipe
12. Ender Lead (3 tiers) + infusion recipes

**Tier 4 — Design decisions to revisit post-MVP:**
13. Per-enchantment config system (max level overrides, power functions)
14. EnchantmentInfoPayload for config sync
15. EnchantableItem interface for per-item enchantment filtering
16. 9 slot-filtered tomes (major feature, may not align with Fizzle's design direction)
