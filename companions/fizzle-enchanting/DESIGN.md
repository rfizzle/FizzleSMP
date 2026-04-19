# Fizzle Enchanting — Design

## Overview

Fizzle Enchanting is a companion Fabric mod that overhauls the vanilla enchanting experience on FizzleSMP. It is a clean-room reimplementation of the enchanting module from [Apotheosis](https://www.curseforge.com/minecraft/mc-mods/apotheosis) (Forge/NeoForge) and its outdated Fabric port [Zenith](https://www.curseforge.com/minecraft/mc-mods/zenith) (MC 1.20.1), targeted at Minecraft 1.21.1 Fabric/Quilt.

The mod has two long-term goals:

1. **Bring Apotheosis-style enchanting to 1.21.1 Fabric** — a stat-driven enchanting table (Eterna/Quanta/Arcana/Rectification/Clues), shelf blocks, an Enchantment Library, anvil tweaks, and tome items for moving enchantments around.
2. **Absorb and replace five existing modpack dependencies** — Easy Anvils, Enchanting Infuser, NeoEnchant+, BeyondEnchant, and Grind Enchantments. Their functionality is folded into Fizzle Enchanting across iterations, consolidating five mods' worth of enchanting/anvil behavior into one config surface.

The MVP implements the Apotheosis/Zenith core only. The iteration backlog (see the last section) lays out how each of the five replaced mods is subsumed in later iterations.

Inspired by Apotheosis (Shadows_of_Fire) and Zenith (bageldotjpg). Textures reused with credit; all code is a fresh 1.21.1 rewrite.

## Goals

1. **Stat-driven enchanting** — five independent stats (Eterna, Quanta, Arcana, Rectification, Clues) replace vanilla's single "power" value.
2. **Data-driven everywhere** — shelf stats, per-enchantment rules, and new enchantments live in datapack JSON, not hardcoded.
3. **1.21.1-native** — leverage the new `EnchantmentEffectComponents` system; don't port a 1.20.1 enchant-class-per-file architecture.
4. **Fabric + Quilt** — single Fabric artifact that runs on Quilt via Quilted Fabric API. No cross-loader abstraction.
5. **No heavy runtime deps** — Fabric API only. No PortingLib, no Cardinal Components, no Mixin Squared.
6. **Integration-friendly** — first-class EMI, REI, JEI, and Jade compat at launch.
7. **Minimal mixins** — 1.21.1's data-driven enchant model and Fabric API cover most hooks; mixins only where genuinely required.
8. **Consolidate the enchanting stack** — over successive iterations, subsume Easy Anvils, Enchanting Infuser, NeoEnchant+, BeyondEnchant, and Grind Enchantments so FizzleSMP ships one enchanting mod instead of six.

## What This Replaces

Eventually, from the modpack's plugin lists:

- **Easy Anvils** — anvil QoL (cap removal, items stay, configurable tweaks).
- **Enchanting Infuser** — pick-your-enchantment block with XP cost.
- **NeoEnchant+** — data-driven new enchantments.
- **BeyondEnchant** — raised vanilla enchantment level caps.
- **Grind Enchantments** — grindstone-based enchantment salvaging.

The MVP does not remove any of these — they come out of the pack one-by-one as each iteration lands. See the iteration backlog for the replacement schedule.

## Scope — MVP In / Out

The MVP is the **Apotheosis/Zenith core**. Everything else is iteration backlog.

| Feature | MVP | Notes |
|---|---|---|
| Stat-driven enchanting table (5 stats) | ✅ | Core |
| Stat datapack (`enchanting_stats/*.json`) | ✅ | Schema ported from Zenith 1:1 |
| Full Zenith shelf roster (25 blocks) | ✅ | Exact parity with Zenith — see "Shelf Blocks" below |
| Enchantment-table crafting (stat-gated) | ✅ | Zenith's `zenith:enchanting` + `zenith:keep_nbt_enchanting` recipe types; gates tier-3 shelves, tome upgrades, Ender Library |
| Enchantment Library block (2 tiers) | ✅ | Basic (lvl 16) + Ender (lvl 31) |
| Prismatic Web (removes curses) | ✅ | The only non-tome anvil interaction Zenith ships |
| Iron block repairs damaged anvil | ✅ | Zenith's iron-block anvil-repair recipe |
| Scrap Tome + Improved Scrap Tome | ✅ | Both destroy the item; Scrap outputs one random enchant, Improved outputs all |
| Extraction Tome | ✅ | Preserves the item; outputs all enchants as one book (most expensive tier) |
| 51 MVP enchantments (49 NeoEnchant+ ports + 2 Zenith) | ✅ | All pure JSON against vanilla `EnchantmentEffectComponents` — zero custom effect component types needed. See list below |
| `infused_breath` specialty item | ✅ | Required to craft endshelf + infused-tier shelves + library; produced via table crafting from dragon_breath |
| `warden_tendril` specialty item + Warden loot modifier | ✅ | Required for the 2 sculkshelves; `LootTableEvents.MODIFY` on Warden (1 guaranteed + 10%/looting for a 2nd) |
| Obliteration / Splitting anvil enchants | ⏳ | Post-MVP content drop |
| `inert_trident` (Zenith's trident-crafting path) | ❌ | Cut — only used to craft a vanilla trident at the table. Not a shelf/library/tome dependency |
| `ender_lead` | ❌ | Cut — belongs to Zenith's `garden` module, unrelated to enchanting |
| Typed Tomes (fishing/weapon/armor/etc.) | ❌ | Zenith ships 9 of these; cut by design — UX felt counter-intuitive |
| Corrupted / Twisted / Masterwork enchantments | ❌ | Cut — use the iteration backlog (NeoEnchant+ port) for "more enchants" instead |
| Everything in the iteration backlog | ❌ | See "Iteration Backlog" at the end of this doc |

### MVP Enchantments (51)

The MVP ships **49 enchantments ported from NeoEnchant+ v5.14.0** (Hardel, CC BY-NC-SA) plus **2 standalone ports from Zenith** (Icy Thorns + Shield Bash). The original Zenith 11 picks (Bane, Chromatic, Inert, Nature's Blessing, Rebounding, Reflective, Spearfishing, Stable Footing, Tempting) were dropped — most had behaviors that felt niche or misaligned, and `Inert` only existed to mark the now-cut `inert_trident` item.

**Why NeoEnchant+ over bespoke design:** every NeoEnchant+ enchant is a pure JSON file against 1.21.1's vanilla `EnchantmentEffectComponents` (`minecraft:damage`, `minecraft:post_attack`, `minecraft:attributes`, `minecraft:armor_effectiveness`, `minecraft:damage_protection`, `minecraft:hit_block`, `minecraft:location_changed`). Porting is a mechanical **namespace rewrite from `enchantplus:` to `fizzle_enchanting:`** on the JSONs and their exclusive-set tags — zero custom Java effect-component types, zero mixins, zero runtime hooks. NeoEnchant+ was originally Iteration 2 in the backlog; it's been pulled forward to MVP for this reason.

**Explicitly cut from NeoEnchant+** (7 of the shipped 56 JSONs are not ported):
- `axe/timber` — whole-tree fellers have a history of server-lag and grief issues.
- `pickaxe/bedrock_breaker` — bedrock protection is deliberate; allowing it invites dimension escape.
- `pickaxe/spawner_touch` — trivializes mob farms and spawner economies.
- `tools/auto_smelt` — vanilla already provides via `Fortune`/`Silk Touch` interaction patterns; duplicates Apotheosis Masterwork territory we cut.
- `helmet/auto_feed` — hunger-management removal undercuts vanilla food variety loops.
- `chestplate/magnet` — ships with an empty `effects` block (marker-enchant pattern relying on downstream integration we don't have). Shipping it inert is deceptive; writing a tick-hook handler breaks the zero-Java-runtime-hooks claim for one enchant. Revisit in a scoped post-MVP iteration if players want magnet behavior.
- `sword/runic_despair` — dimension-predicate enchant targeting Yggdrasil's Runic dimension. Yggdrasil is not in the FizzleSMP pack, so the predicate is perpetually false and the enchant is permanently inert. `sword/dimensional_hit` (generic "other dimensions") stays — it works universally.

Full roster, grouped by slot:

**Armor (all pieces)** (3)
| Id | Name | Description |
|---|---|---|
| `armor/fury` | Fury | Reduces armor, increases penetration and damage. |
| `armor/lifeplus` | Life+ | Grants extra hearts. |
| `armor/venom_protection` | Venom Protection | Protects against negative effects. |

**Helmet** (2 — `auto_feed` cut)
| Id | Name | Description |
|---|---|---|
| `helmet/bright_vision` | Bright Vision | Grants night vision. |
| `helmet/voidless` | Voidless | Brief levitation when falling into the void. |

**Chestplate** (1 — `magnet` cut)
| Id | Name | Description |
|---|---|---|
| `chestplate/builder_arm` | Builder Arm | Extends block placement and breaking range. |

**Leggings** (4)
| Id | Name | Description |
|---|---|---|
| `leggings/dwarfed` | Dwarfed | Decreases size and speed, adds step assist. |
| `leggings/fast_swim` | Fast Swim | Grants Dolphin's Grace effect. |
| `leggings/leaping` | Leaping | Enhances jumping ability. |
| `leggings/oversize` | Oversize | Increases size, reduces damage and attack reach. |

**Boots** (3)
| Id | Name | Description |
|---|---|---|
| `boots/agility` | Agility | Increases movement speed. |
| `boots/lava_walker` | Lava Walker | Allows walking on lava. |
| `boots/step_assist` | Step Assist | Eases block climbing. |

**Elytra** (2)
| Id | Name | Description |
|---|---|---|
| `elytra/armored` | Armored | Reduces damage with elytra. |
| `elytra/kinetic_protection` | Kinetic Protection | Protects from elytra collision damage. |

**Sword** (12 — `runic_despair` cut)
| Id | Name | Description |
|---|---|---|
| `sword/attack_speed` | Attack Speed | Increases attack speed. |
| `sword/critical` | Critical Hit | Chance to deal true damage. |
| `sword/death_touch` | Death Touch | Gives Darkness to enemies. |
| `sword/dimensional_hit` | Dimensional Strike | Increases damage in other dimensions. |
| `sword/fear` | Fear | Delays creeper explosions when hit. |
| `sword/last_hope` | Last Hope | Sacrifices item for infinite damage. |
| `sword/life_steal` | Life Steal | Drains health from targets. |
| `sword/poison_aspect` | Poison Aspect | Poisons targets on hit. |
| `sword/pull` | Pull | Chance to obtain mob eggs on kill. |
| `sword/reach` | Reach | Extends attack range. |
| `sword/tears_of_asflors` | Tears of Asflors | Converts XP to damage. |
| `sword/xp_boost` | XP Boost | Increases XP from mob kills. |

**Bow** (7)
| Id | Name | Description |
|---|---|---|
| `bow/accuracy_shot` | Accuracy Shot | Arrows fly straight without gravity. |
| `bow/breezing_arrow` | Breezing Arrows | Repels targets or creates ground effects. |
| `bow/echo_shot` | Echo Shot | Creates a sonic boom on impact, dealing area damage. |
| `bow/eternal_frost` | Eternal Frost | Freezes blocks and slows targets. |
| `bow/explosive_arrow` | Explosive Arrow | Creates explosions on arrow impact. |
| `bow/rebound` | Rebound | Arrows bounce off walls. |
| `bow/storm_arrow` | Storm Arrows | Summons lightning on impact. |

**Trident** (1)
| Id | Name | Description |
|---|---|---|
| `trident/gungnir_breath` | Gungnir Breath | Freezes water and slows targets. |

**Mace** (3)
| Id | Name | Description |
|---|---|---|
| `mace/striker` | Striker | Summons lightning during storms, grants immunity. |
| `mace/teluric_wave` | Teluric Wave | Creates seismic waves when crouching and striking ground. |
| `mace/wind_propulsion` | Wind Propulsion | Creates explosion on ground impact, launching you. |

**Pickaxe** (1 — `bedrock_breaker` + `spawner_touch` cut)
| Id | Name | Description |
|---|---|---|
| `pickaxe/vein_miner` | Vein Miner | Mines all connected ores. |

**Tools (generic)** (1 — `auto_smelt` cut)
| Id | Name | Description |
|---|---|---|
| `tools/miningplus` | Mining+ | Mines in a 3x3 area. |

**Hoe** (3)
| Id | Name | Description |
|---|---|---|
| `hoe/harvest` | Harvest | Allows planting in an area. |
| `hoe/scyther` | Scyther | Tills larger areas based on enchantment level. |
| `hoe/seiors_oblivion` | Seior's Oblivion | Replaces blocks beneath you with natural terrain. |

**Mounted** (4)
| Id | Name | Description |
|---|---|---|
| `mounted/cavalier_egis` | Cavalier Egis | Reduces damage taken when riding a mount. |
| `mounted/ethereal_leap` | Ethereal Leap | Increases mount jump height and reduces fall damage. |
| `mounted/steel_fang` | Steel Fang | Allows your mount to deal more damage when attacking. |
| `mounted/velocity` | Velocity | Increases mount movement speed. |

**Curses** (2)
| Id | Name | Description |
|---|---|---|
| `durability/curse_of_breaking` | Curse of Fragility | Reduces item durability faster. |
| `durability/curse_of_enchant` | Curse of Enchant | Prevents enchanting or modifying the item. |

**Misc** (1)
| Id | Name | Description |
|---|---|---|
| `midas_touch` | Midas Touch | Transform blocks into gold or gold ore at right-click. |

**From Zenith** (2)
| Id | Name | Description |
|---|---|---|
| `icy_thorns` | Icy Thorns | Chest-armor enchant — applies slowness to attackers who hit you. Pure JSON via `minecraft:post_attack` (enchanted VICTIM → affected ATTACKER) with `minecraft:apply_mob_effect`. |
| `shield_bash` | Shield Bash | Shields (mainhand) deal bonus damage on attack; uses durability on hit. Pure JSON via `minecraft:damage` + tag expansion adding shields to `#minecraft:enchantable/weapon`. |

**Total: 51 enchantments, 100% pure JSON.**

## Architecture

### Stat System

The five stats replace vanilla's monolithic "enchanting power":

| Stat | What it controls | Vanilla analogue |
|---|---|---|
| **Eterna** | Maximum enchanting level achievable (0–50) | The 30-level cap |
| **Quanta** | Upper bound of the random power roll | Internal power-cost randomization |
| **Arcana** | Influences enchantment *selection* (more obscure/rare picks) | Internal rarity weighting |
| **Rectification** | Reduces Quanta's *negative* variance (more consistent results) | None |
| **Clues** | How many of the preview slots show the actual enchantment | Hardcoded vanilla hover reveal |

Each shelf block contributes a per-stat scalar that sums across all shelves within the 5×5×2 reach of the table. Values are defined in datapack JSON (`data/fizzle_enchanting/enchanting_stats/<stat_id>.json`), keyed by block or block tag. Negative contributions are allowed (e.g. a cursed shelf that drains Rectification).

### Table Menu Implementation

Zenith-style menu replacement rather than direct `slotsChanged` injection. Cleaner to reason about, same runtime semantics, and tracks the reference 1:1.

**Mixin footprint — two classes, one injection, three accessors:**

| Mixin | Target | Type | Purpose |
|---|---|---|---|
| `EnchantmentTableBlockMixin` | `getMenuProvider` | `@Inject HEAD cancellable` | Swap in our menu |
| `EnchantmentMenuAccessor` | `enchantSlots` / `random` / `enchantmentSeed` | `@Accessor` × 3 | Read private fields from the subclass |

```java
@Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
void fizzle$menu(BlockState state, Level level, BlockPos pos,
                 CallbackInfoReturnable<MenuProvider> cir) {
    cir.setReturnValue(new SimpleMenuProvider(
        (id, inv, player) -> new FizzleEnchantmentMenu(
            id, inv, ContainerLevelAccess.create(level, pos)),
        Component.translatable("container.enchant")));
}
```

`costs[]`, `enchantClue[]`, and `levelClue[]` are already public fields on vanilla `EnchantmentMenu` — direct access from the subclass, no accessor needed.

**`FizzleEnchantmentMenu` responsibilities** (extends `EnchantmentMenu`):
- `slotsChanged(Container)` — runs `gatherStats()`, recomputes `costs[]` with our Eterna-derived level, selects clues using `quanta` / `arcana` / `rectification`, fires `StatsPayload` and `CluesPayload`.
- `clickMenuButton(Player, int id)` — validates XP/lapis against `costs[id]`, calls `selectEnchantment` with our stats, consumes XP + lapis, re-runs `slotsChanged` to refresh. `id == 3` is our extension point for the crafting-result row (see below).

**Vanilla-shelf interaction.** Two-layer lookup:

1. **Datapack first** — block is keyed in our `enchanting_stats` registry → return that block's stat tuple.
2. **Fallback** — not in the registry but block is in `BlockTags.ENCHANTMENT_POWER_PROVIDER` → Eterna 1, maxEterna 15, zero on other stats (vanilla "+1 level per shelf, 15-level cap" behavior).
3. **Else** — zero contribution.

We ship a stat JSON in `data/fizzle_enchanting/enchanting_stats/vanilla_provider.json` that maps `#minecraft:enchantment_power_provider` to `{maxEterna: 15, eterna: 1}`, so the vanilla fallback is datapack-expressible — operators can retune vanilla bookshelves without a jar rebuild. The Java fallback in step 2 is a safety net for a wiped datapack.

`BlockTags.ENCHANTMENT_POWER_TRANSMITTER` gates the line-of-sight check between table and shelf (vanilla's `BOOKSHELF_OFFSETS` midpoint test). Used unchanged.

### Payload Shapes (S2C custom payloads, 1.21.1)

Registered via `PayloadTypeRegistry.playS2C().register(TYPE, CODEC)` in `FizzleEnchanting#onInitialize`. Full re-send on every relevant `slotsChanged` — no incremental sync.

**`StatsPayload`** — carries all 5 stats, blacklist, treasure flag, and the optional crafting-result row (no separate packet family for crafting):

```java
public record StatsPayload(
    float eterna,
    float quanta,
    float arcana,
    float rectification,
    int clues,
    List<ResourceKey<Enchantment>> blacklist,
    boolean treasure,
    Optional<CraftingResultEntry> craftingResult
) implements CustomPacketPayload {
    public static final Type<StatsPayload> TYPE = new Type<>(id("stats"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StatsPayload> CODEC = ...;
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

public record CraftingResultEntry(
    ItemStack result,
    int xpCost,
    ResourceLocation recipeId   // for JEI/EMI highlight
) { }
```

`blacklist` ships as `ResourceKey<Enchantment>` rather than `ResourceLocation` or integer IDs — 1.21.1's enchantment registry is dynamic (no stable integer IDs), and `ResourceKey` carries registry-reload safety that a raw `ResourceLocation` doesn't. Client resolves against `level.registryAccess()`.

Sent when stats, input, or recipe-match state changes.

**`CluesPayload`** — per-slot clue lists for hover tooltips:

```java
public record CluesPayload(
    int slot,                      // 0, 1, or 2
    List<EnchantmentClue> clues,
    boolean exhaustedList          // true if no more enchants could be selected
) implements CustomPacketPayload { ... }

public record EnchantmentClue(
    ResourceKey<Enchantment> enchantment,
    int level
) { }
```

Fired three times per `slotsChanged` (once per preview slot) when the input is enchantable. Clue count per slot equals `stats.clues()`; the first clue is always the main enchant for that slot, remaining filled from the selection pool until exhausted. Client caches per-slot clue lists for tooltips; cleared on empty-list send (input removed).

### Crafting-Result Row (piggybacks on vanilla button clicks)

The crafting-result row is wired through the existing button-click round-trip — no new packet family, no new click handler family.

Flow:
1. In `slotsChanged`, after gathering stats, run `EnchantingRecipeRegistry.findMatch(level, inputStack, stats)`.
2. If a recipe matches and stat thresholds satisfy its `requirements` / `max_requirements` → populate `craftingResult = Optional.of(...)` on the outgoing `StatsPayload`.
3. Client screen renders a fourth row below the three enchant slots: result icon + XP cost badge. Visible only when `menu.craftingResult.isPresent()`.
4. Click → `menu.clickMenuButton(player, 3)`. The `0/1/2/3` button-ID namespace is ours to define once we own the menu subclass.
5. Server handler for `id == 3`: validate XP balance, consume XP, replace input stack with the recipe output (preserving `ItemEnchantments` component for `keep_nbt_enchanting` recipes), refire `slotsChanged`.

### Shelf Blocks

All shelves implement `IEnchantingStatProvider` (single interface, default methods) and are registered against the `enchanting_stats` datapack. The Java side stays thin — block class + model + particle hook. The *values* live in JSON so server operators can rebalance without a jar.

The shelf roster is **1:1 with Zenith** — same block IDs, same stat JSONs, same recipes, same textures. 25 shelf blocks grouped into biome-themed tiers, plus utility shelves and stat-contributing vanilla blocks picked up by the stat registry.

Each `enchanting_stats/<id>.json` entry has five possible fields (Zenith schema):
- `maxEterna` — per-block Eterna ceiling (the table sums Eterna contributions only up to the highest `maxEterna` it sees)
- `eterna` — per-block contribution (can be negative)
- `quanta` — positive or negative variance contribution
- `arcana` — rarity-bias contribution (can be negative)
- `rectification` — reduces negative Quanta variance
- `clues` — extra preview slots revealed

**Wood-tier shelves** (noise: WOOD, strength 0.75):

| Block | maxEterna | eterna | quanta | arcana | Notes |
|---|---|---|---|---|---|
| `beeshelf` | 15 | 1 | 1 | 0 | Honeycomb + bookshelf |
| `melonshelf` | 15 | 1 | 0 | 1 | Melon + bookshelf |

**Stone-tier shelves** (noise: STONE, strength 1.5–5.0):

| Block | maxEterna | eterna | quanta | arcana | Particle | Tier theme |
|---|---|---|---|---|---|---|
| `stoneshelf` | 0 | −1.5 | 0 | −7.5 | ENCHANT | Baseline/cheap; intentionally weak |
| `hellshelf` | 22.5 | 1.5 | 3 | 0 | ENCHANT_FIRE | Nether T1 — Quanta-heavy |
| `blazing_hellshelf` | 22.5 | 1.5 | +Q | 0 | ENCHANT_FIRE | Nether T2 |
| `glowing_hellshelf` | 22.5 | 1.5 | +Q | 0 | ENCHANT_FIRE | Nether T2 alt |
| `infused_hellshelf` | 30 | 2 | +Q | 0 | ENCHANT_FIRE | Nether T3 (glowy item) |
| `seashelf` | 22.5 | 1.5 | 0 | 2 | ENCHANT_WATER | Ocean T1 — Arcana-heavy |
| `heart_seashelf` | 22.5 | 1.5 | 0 | +A | ENCHANT_WATER | Ocean T2 |
| `crystal_seashelf` | 22.5 | 1.5 | 0 | +A | ENCHANT_WATER | Ocean T2 alt |
| `infused_seashelf` | 30 | 2 | 0 | +A | ENCHANT_WATER | Ocean T3 (glowy item) |
| `endshelf` | 45 | 2.5 | 5 | 5 | ENCHANT_END | End T1 — balanced endgame |
| `pearl_endshelf` | 45 | 2.5 | +Q | +A | ENCHANT_END | End T2 |
| `draconic_endshelf` | 50 | 3 | +Q | +A | ENCHANT_END | End T3 |
| `deepshelf` | 30 | 2 | 2 | 0 | ENCHANT_SCULK | Deep T1 (glowy item) |
| `dormant_deepshelf` | 25 | 1.5 | 1 | 0 | ENCHANT_SCULK | Deep T0 |
| `echoing_deepshelf` | 35 | 2.5 | +Q | 0 | ENCHANT_SCULK | Deep T2 |
| `soul_touched_deepshelf` | 35 | 2.5 | +Q | 0 | ENCHANT_SCULK | Deep T2 alt |

**Sculk-tier shelves** (reanimated-bookshelf subtype):

| Block | maxEterna | eterna | quanta | arcana | Notes |
|---|---|---|---|---|---|
| `echoing_sculkshelf` | 40 | 3 | +Q | +A | Sculk variant |
| `soul_touched_sculkshelf` | 40 | 3 | +Q | +A | Sculk variant |

**Utility shelves** (pure stat contributions, no Eterna):

| Block | Stat | Value | Role |
|---|---|---|---|
| `sightshelf` | `clues` | +1 | Reveals one extra preview enchantment |
| `sightshelf_t2` | `clues` | +2 | Reveals two extra |
| `rectifier` | `rectification` | 10 | Reduces negative Quanta variance |
| `rectifier_t2` | `rectification` | 15 | Stronger rectification |
| `rectifier_t3` | `rectification` | 20 | Maximum rectification |

**Special shelves** (block-entity backed):

| Block | Role |
|---|---|
| `filtering_shelf` | Chiseled-bookshelf variant. Stored books blacklist those enchantments from table results. Acts as a wood-tier base shelf when empty. |
| `treasure_shelf` | Unlocks treasure enchantments at the table (Mending, Frost Walker, Soul Speed, etc.). No Eterna contribution of its own. |

**Non-shelf stat providers** (vanilla blocks picked up by the stat registry):

| Block | Stat | Purpose |
|---|---|---|
| `amethyst_cluster` | `arcana +0.5` (example) | Flavor bonus from nearby amethyst |
| `skeleton_skull` (basic_skulls.json) | flavor | Small bonus near skulls |
| `wither_skull` / `wither_wall_skull` | flavor | Small bonus near wither skulls |

Exact values are copied verbatim from `/home/rfizzle/Projects/Zenith/src/main/resources/data/zenith/enchanting_stats/*.json` at implementation time; the tables above are summaries, not sources of truth. Values marked `+Q`/`+A` indicate "some positive contribution, exact value taken from the Zenith JSON" — I've collapsed them for readability.

**Recipes and textures:** All 25 shelves' textures and recipes come from Zenith verbatim. See "Asset Sources" below for exact source paths. Recipes go under `data/fizzle_enchanting/recipe/` with the same craft patterns. Credit: textures originate from Apotheosis (Shadows_of_Fire) via Zenith (bageldotjpg).

### Asset Sources (Zenith → Fizzle Enchanting)

All reused textures and models live in the Zenith repo. Zenith uses the pre-1.21 plural directory names (`items/`, `blocks/`); 1.21.1 convention is singular (`item/`, `block/`), so every copy must also **rename the directory component**.

**Base path:** `/home/rfizzle/Projects/Zenith/src/main/resources/assets/zenith/`

| Source (Zenith) | Destination (fizzle-enchanting) | Contents |
|---|---|---|
| `textures/items/*.png` (top level) | `assets/fizzle_enchanting/textures/item/` | `infused_breath.png` (+ `.mcmeta`), `warden_tendril.png`, `prismatic_web.png`, and other loose item textures |
| `textures/items/tomes/*.png` | `assets/fizzle_enchanting/textures/item/tome/` | `scrap_tome.png`, `improved_scrap_tome.png`, `extraction_tome.png` — ignore the 9 typed-tome files (cut from MVP) |
| `textures/blocks/*.png` (top level) | `assets/fizzle_enchanting/textures/block/` | All 25 shelf textures: `beeshelf`, `melonshelf`, `stoneshelf`, `hellshelf`, `blazing_hellshelf` (animated — keep `.mcmeta`), `glowing_hellshelf`, `seashelf`, `heart_seashelf`, `crystal_seashelf`, `endshelf`, `pearl_endshelf`, `draconic_endshelf`, `dormant_deepshelf`, `deepshelf`, `echoing_deepshelf`, `soul_touched_deepshelf`, `echoing_sculkshelf`, `soul_touched_sculkshelf`, `sculkshelf_top`, `sightshelf_t2`(+`_top`), `sight_side`/`sight_top` (base sightshelf), `rectifier`, `rectifier_t2`(+`_top`), `rectifier_t3`, `treasure_shelf_side`/`_top` |
| `textures/blocks/library/` | `assets/fizzle_enchanting/textures/block/library/` | Basic library block faces |
| `textures/blocks/ender_library/` | `assets/fizzle_enchanting/textures/block/ender_library/` | Ender library block faces |
| `textures/blocks/filtering_shelf/` | `assets/fizzle_enchanting/textures/block/filtering_shelf/` | Filtering shelf (chiseled-bookshelf-style, occupied-slot variants) |
| `textures/particle/` | `assets/fizzle_enchanting/textures/particle/` | Enchant-swirl particles used by the themed shelves |
| `textures/gui/book/`, `textures/gui/*.png` | `assets/fizzle_enchanting/textures/gui/` | Library screen background + infused tome art. Audit before copy — the `gui/` folder also contains reforging/augmenting/socketing textures we don't ship. |
| `models/block/*.json`, `models/item/*.json`, `blockstates/*.json` | matching `assets/fizzle_enchanting/...` paths | Same rename rule (`items/` → `item/`, `blocks/` → `block/`) |

**Translation pass on copy:** every JSON under `models/` and `blockstates/` references textures by namespaced path (`"zenith:blocks/hellshelf"`). On import, run a find/replace: `zenith:blocks/` → `fizzle_enchanting:block/`, `zenith:items/` → `fizzle_enchanting:item/`, `zenith:` → `fizzle_enchanting:`.

**What not to copy:** anything under `textures/items/gems/`, `items/sigils/`, `items/vial/`, `blocks/reforging/`, `blocks/augmenting/`, `advancements/`, `entity/` — these belong to the Apotheosis systems we've cut (affixes, reforging, augmenting, adventure). Also skip the 9 typed-tome PNGs and the cobweb-related assets if present.

**License reminder:** Apotheosis ships a separate `LICENSE_ASSETS`. Private FizzleSMP use is in bounds; a public Modrinth/CurseForge release requires a compliance pass before shipping these assets.

### Enchantment-Table Crafting

Zenith's enchanting table doubles as a **stat-gated crafting station**. When an item is placed in the table's input slot and the table's stats meet a recipe's thresholds, a result appears that the player can extract for an XP cost. This is the mechanic that gates tier-3 shelves, tome upgrades, the `infused_breath` specialty material, and the Ender Library.

Two recipe types ported from Zenith:

- **`fizzle_enchanting:enchanting`** — consumes the input, produces the result. Zenith's `zenith:enchanting` type.
- **`fizzle_enchanting:keep_nbt_enchanting`** — consumes the input, produces the result with the input's data components (enchantments, custom NBT) preserved. Zenith's `zenith:keep_nbt_enchanting` type. Used for the Basic → Ender Library upgrade (player keeps their stored books).

Each recipe JSON carries:
- `input` — an item or tag match.
- `requirements` — minimum `eterna`, `quanta`, `arcana` (Rectification/Clues don't gate crafting in Zenith).
- `max_requirements` — optional upper bounds per stat (`-1` = no max). Used to lock recipes to specific tiers (e.g. only craft at a table *between* E 40 and E 50).
- `result` — output item + count.
- Optional `display_level` — JEI/EMI hint for integration display only.

**Recipes shipped in MVP** (values copied from Zenith):

| Recipe | Input | Output | E | Q | A | Type |
|---|---|---|---|---|---|---|
| `infused_breath` | `minecraft:dragon_breath` | `infused_breath` × 3 | 40–∞ | 15–25 | 60–∞ | enchanting |
| `infused_hellshelf` | `hellshelf` | `infused_hellshelf` | 22.5–∞ | 30–∞ | 0–∞ | enchanting |
| `infused_seashelf` | `seashelf` | `infused_seashelf` | 22.5–∞ | 0–∞ | 30–∞ | enchanting |
| `deepshelf` | `dormant_deepshelf` | `deepshelf` | 25–∞ | 10–∞ | 0–∞ | enchanting |
| `improved_scrap_tome` | `scrap_tome` | `improved_scrap_tome` × 4 | 22.5–∞ | 25–50 | 35–∞ | enchanting |
| `extraction_tome` | `improved_scrap_tome` | `extraction_tome` × 4 | 30–∞ | 25–75 | 45–∞ | enchanting |
| `ender_library` | `library` | `ender_library` | 50 | 45–50 | 100 | keep_nbt_enchanting |

Exact values (including any `display_level` / stack sizes we missed above) come from `/home/rfizzle/Projects/Zenith/src/main/resources/data/zenith/recipes/enchanting/*.json` at implementation time.

Implementation:
- New `Recipe<SingleRecipeInput>` subtypes registered against `BuiltInRegistries.RECIPE_TYPE`.
- `EnchantmentMenu#slotsChanged` mixin (already touching the menu for stat scanning) additionally looks up a matching recipe by input + current stats. If found, the enchantment-preview panel is replaced by a crafting result row (label + XP cost). On click, XP is consumed server-side, input is decremented (or preserved for `keep_nbt` recipes), output is pushed to the player inventory.
- Result preview + crafting-complete feedback travel over the existing `StatsPayload` path (add a `craftingResult: Optional<CraftingResultEntry>` field) — no new packet family.
- JEI/EMI/REI adapters render these recipes alongside vanilla enchanting in their respective info panels.

### Enchantment Library

Two-tier storage block that pools enchanted books into a per-enchantment point bank. Matches Zenith's behavior exactly; referenced against `/home/rfizzle/Projects/Zenith/src/main/java/dev/shadowsoffire/apotheosis/ench/library/EnchLibraryTile.java`.

- **Basic Library** (`library`) — level cap 16, crafted from bookshelves + iron.
- **Ender Library** (`ender_library`) — level cap 31. Upgrade path is the `ender_library` enchanting-table recipe (`keep_nbt_enchanting`, preserves stored books) — see the Enchantment-Table Crafting section.

**Point math:**

```
points(level) = 2^(level − 1)
```

Per book deposited: `1, 2, 4, 8, 16, 32, …`. Books of the same enchantment stack additively into the pool. Per-enchant point cap = `2^(tierMaxLevel − 1)`:
- Basic: 32,768 pts per enchant
- Ender: 1,073,741,824 pts per enchant (fits in `int`, no `long` needed)

**Two state maps per block entity:**
- `points: Object2IntMap<ResourceKey<Enchantment>>` — accumulated point pool.
- `maxLevels: Object2IntMap<ResourceKey<Enchantment>>` — highest individual book level ever deposited for that enchantment, clamped to the tier cap.

Extraction gate requires **both**: `maxLevels[e] ≥ target` **and** `points[e] ≥ points(target) − points(curLvl)`. So depositing 32,768 Sharpness I books gives you the point budget for Sharpness V but `maxLevels[Sharpness] = 1` blocks the pull — prevents "grind commons, extract rares" strategies.

**Extraction upgrades books in place.** Clicking an enchant button with a book already in the extract slot raises it from `curLvl` → `target` for `points(target) − points(curLvl)` points. Upgrading an existing book is far cheaper than pulling a fresh one — this is a deliberate design beat.

Shift-click resolves `target = min(maxLevels[e], 1 + log₂(points + points(curLvl)))` — the max level affordable from the current pool in one click.

**Encumbrance: silent void.** Deposits clamp to `maxPoints` via `Math.min`; overflow is destroyed without rejection or bounce-back. Required for safe hopper automation — libraries cannot jam when full. Curses are accepted (no blacklist) since they may be useful stored.

**Ender upgrade cap handling.** The `keep_nbt_enchanting` recipe preserves `Points`/`Levels` tags on the upgrade; the tier cap moves 16 → 31 but per-enchant pool values stay frozen at whatever they were. An enchant saturated at Basic's 32,768-pt cap does not auto-scale — a fresh book deposit after upgrade is what pushes past the old ceiling. Matches Zenith.

**NBT schema (two sibling compound tags):**

```
├── Points: CompoundTag
│     "fizzle_enchanting:sword/critical" = 128   (int)
│     "minecraft:sharpness" = 6144               (int)
│     …
└── Levels: CompoundTag
      "fizzle_enchanting:sword/critical" = 4     (int)
      "minecraft:sharpness" = 5                  (int)
      …
```

Keys are `ResourceLocation` strings. Enchantments are a dynamic registry in 1.21.1, so resolution happens lazily against the world's `RegistryAccess`; entries whose key no longer resolves (datapack removed the enchant) are silently dropped on load rather than crashing. No schema version field in MVP — add one if the layout ever changes.

**`Storage<ItemVariant>` adapter (hopper I/O).** Registered via `ItemStorage.SIDED.registerForBlockEntity(...)`:
- Accepts only `Items.ENCHANTED_BOOK` on insert; all other variants rejected at `canInsert`.
- Insert path calls `depositBook` per unit, void-capping at `maxPoints`. Returns the full input amount (overflow is destroyed, not rejected, so from the pipe's perspective every book was accepted).
- Extract path returns 0 unconditionally; the library is never "empty" from a pipe's perspective. Hoppers cannot pull books out — extraction is a menu-only operation.
- Implemented as a dedicated `SnapshotParticipant<LibrarySnapshot>` (not Zenith's `SingleStackStorage` fake-stack wrapper). Snapshot holds both maps + a dirty flag; transaction abort restores cleanly.

Hopper rate limit: `library.ioRateLimitTicks` config (default `0`, off). When > 0, track `lastInsertTick` on the BE and drop inserts landing inside the window. Safety valve against pathological autofarms.

**GUI — three slots** (matches Zenith):
- **Slot 0 — deposit.** Book lands here; on `setChanged` the stack is absorbed into the pool and the slot clears. Sound plays client-side.
- **Slot 1 — extract target.** The book being upgraded; clicking an enchant button writes to this slot (creating a fresh `ENCHANTED_BOOK` if empty).
- **Slot 2 — scratch.** Generic single-stack buffer for shift-click overflow and moving the finished book out of the menu.

Screen lists every enchant with `points > 0`, showing current point total and per-enchant max-level badge. Listener pattern: the BE holds a `Set<EnchantmentLibraryMenu>` and calls `onChanged()` on each when the pool mutates, so open screens repaint without polling. Button-click handler receives `enchantIndex + shift-flag` bitpacked into the click ID (Zenith encoding: high bit = shift).

**Class shape:**
- `EnchantmentLibraryBlockEntity` (abstract, holds both maps + `maxLevel`/`maxPoints`).
- `BasicLibraryBlockEntity` (maxLevel = 16) / `EnderLibraryBlockEntity` (maxLevel = 31).
- Client sync via `BlockEntity#getUpdatePacket` / `getUpdateTag` — full map resend on any mutation. Volume is small (≤ a few hundred ints) so incremental sync is not worth the complexity.
- `AbstractContainerMenu` subclass with the 3 IO slots + player inventory slots.

### Anvil tweaks (MVP)

Two changes, matching Zenith exactly — each gated by a config flag:

1. **Prismatic Web strips curses** — new item; placed in slot B of an anvil against a cursed item in slot A, removes all curses (non-curse enchantments preserved on the item). Cost: 30 levels, 1 Prismatic Web consumed.
2. **Iron block repairs damaged anvil** — place a chipped or damaged anvil in slot A and a block of iron in slot B; the anvil is repaired by one tier (damaged → chipped, or chipped → normal). Cost scales with the enchantments on the anvil (rare in practice but supported — Zenith preserves them through repair). 1 iron block consumed per repair.

Implementation: single mixin on `AnvilMenu#createResult` (Fabric has no vanilla event here). These two interactions, plus the three tome interactions (see below), all dispatch from the same mixin hook.

**What we're not doing in MVP:** no cobweb interaction (Zenith doesn't use it), no "too expensive" cap removal (deferred to Iteration 1 — Easy Anvils absorption). Enchantment removal is covered by the Scrap/Improved Scrap/Extraction tomes below, not by an anvil-plus-web recipe.

### Tome items

Three item families, all stackable to 1, no durability. Matches Zenith's tome set minus the typed tomes (Zenith ships 9 category-specific tomes — boots/bow/chestplate/fishing/helmet/leggings/other/pickaxe/weapon — which we've cut by design).

All three tomes destroy the *tome* on use. They differ by how much of the source item survives and how many enchantments you get back:

| Item | Enchantments removed | Source item fate | Output book |
|---|---|---|---|
| **Scrap Tome** (`scrap_tome`) | **One random** enchantment | **Destroyed** | Enchanted book with that one random enchantment |
| **Improved Scrap Tome** (`improved_scrap_tome`) | **All** enchantments | **Destroyed** | Enchanted book with all the enchantments |
| **Extraction Tome** (`extraction_tome`) | **All** enchantments | **Preserved**, fully unenchanted | Enchanted book with all the enchantments |

The progression is: Scrap Tome is cheap but loses both the item and most of the enchants (only one comes back, randomly). Improved Scrap Tome still burns the item but salvages every enchantment. Extraction Tome does the same salvage *and* gives the item back, making it the "best-of-both" tier and accordingly the most expensive to craft. The Extraction Tome also exposes an item-repair side-path using the anvil fuel slot (Zenith behavior preserved).

All three piggyback on the vanilla anvil menu, dispatched from our single `AnvilMenu#createResult` mixin. No custom screens — the interaction is always "place item in slot A + place tome in slot B → output appears in slot C."

XP costs and crafting recipes are taken from Zenith's defaults, surfaced through the `"tomes"` config section (see below).

### Enchantment Implementation — 1.21.1 approach

All 53 MVP enchantments ship as **datapack JSON** under `data/fizzle_enchanting/enchantment/<category>/<id>.json`, using only vanilla `EnchantmentEffectComponents`. No custom `DataComponentType<?>` registrations, no dispatchers, no event listeners for enchant behavior.

**Port procedure (NeoEnchant+ → fizzle-enchanting):**

1. Copy the shipped `.json` files verbatim from `NeoEnchant-5.14.0.jar!/data/enchantplus/enchantment/**` to `src/main/resources/data/fizzle_enchanting/enchantment/**`.
2. Mechanical namespace rewrite on each file: `enchantplus:` → `fizzle_enchanting:` (affects exclusive-set tag refs and any internal `#enchantplus:...` tag lookups).
3. Copy exclusive-set tag files from `data/enchantplus/tags/enchantment/exclusive_set/*.json` to the fizzle namespace with the same rewrite.
4. Copy English strings from NeoEnchant+'s `assets/minecraft/lang/en_us.json` — the `enchantment.enchantplus.*` keys → `enchantment.fizzle_enchanting.*`.
5. Skip the 5 cut enchants and their tag memberships (see previous section for list).

**Icy Thorns + Shield Bash** are new JSON files written from scratch by us, since we're not pulling their Zenith class code. Both fit vanilla EEC cleanly — no special affordance needed.

**Data-driven everywhere goal:** this fully delivers goal #2 from the project goals. The MVP enchantment set has a **zero-Java footprint** — rebalancing a value, disabling an enchant, or adding a new enchant is a pure datapack edit with no jar rebuild required.

## Project Structure

```
companions/fizzle-enchanting/
├── DESIGN.md                      # This file
├── TODO.md                        # Phased delivery checklist
├── build.gradle
├── gradle.properties
├── settings.gradle
├── gradle/ gradlew gradlew.bat
└── src/
    ├── main/
    │   ├── java/com/fizzlesmp/fizzle_enchanting/
    │   │   ├── FizzleEnchanting.java            # ModInitializer
    │   │   ├── FizzleEnchantingRegistry.java    # Blocks, items, BEs, menus, particles, payloads
    │   │   ├── config/                          # JSON config loader + defaults
    │   │   ├── enchanting/                      # Stat system, IEnchantingStatProvider, table mixins
    │   │   ├── shelf/                           # Shelf block classes + particle hooks
    │   │   ├── library/                         # Library block, BE, menu, storage api adapter
    │   │   ├── anvil/                           # Anvil mixin + prismatic web item
    │   │   ├── tome/                            # 4 tome families + their UX glue
    │   │   ├── event/                           # Fabric event subscriptions (Warden loot modifier, etc. — no per-enchant hooks in MVP)
    │   │   ├── net/                             # Payload records + codecs
    │   │   ├── mixin/                           # Minimum necessary mixins
    │   │   ├── compat/emi/  rei/  jei/  jade/
    │   │   └── data/                            # Fabric Data Generation API entrypoint + providers (models, blockstates, block loot, vanilla-shape recipes)
    │   └── resources/
    │       ├── fabric.mod.json
    │       ├── fizzle_enchanting.mixins.json
    │       ├── assets/fizzle_enchanting/        # Textures from Apotheosis/Zenith (credited)
    │       └── data/fizzle_enchanting/
    │           ├── enchantment/                 # 51 JSON enchantments (49 ported from NeoEnchant+ in slot subdirs, 2 authored: icy_thorns, shield_bash)
    │           ├── enchanting_stats/            # Per-block stat contributions
    │           ├── tags/item/enchantable/       # pickaxe.json, axe.json, etc.
    │           ├── tags/enchantment/exclusive_set/  # Ported NeoEnchant+ exclusive-set tags
    │           ├── tags/block/                  # Shelf tags, library tag
    │           ├── recipe/
    │           ├── loot_table/
    │           └── advancement/
    ├── client/
    │   └── java/com/fizzlesmp/fizzle_enchanting/client/
    │       ├── FizzleEnchantingClient.java      # ClientModInitializer
    │       ├── screen/                          # Enchantment table, library
    │       ├── renderer/                        # Library BER, particles
    │       └── net/                             # Clientbound payload handlers
    └── test/java/...                            # JUnit — config parse, stat sum, point math
```

## Custom Effect Components

**MVP: none.** All 53 enchantments fit inside 1.21.1's stock `EnchantmentEffectComponents` surface. No `DataComponentType<?>` registrations are required at launch.

Reserved for future iterations if a post-MVP enchant can't be expressed in vanilla components:

```java
public static final DataComponentType<FooEffect> FOO_EFFECT =
    Registry.register(
        BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
        id("foo"),
        DataComponentType.<FooEffect>builder()
            .persistent(FooEffect.CODEC)
            .build()
    );
```

If we ever need one, the effect record carries its own payload, the codec is registered in `FizzleEnchantingRegistry`, and a Fabric event listener reads the component off the enchanted stack at dispatch time. Keeping this shape reserved avoids a lock-in if a later iteration (e.g. a post-MVP Apotheosis port) introduces behaviors vanilla can't express.

## Datagen Strategy

Hybrid split, driven by **copy volume vs. boilerplate volume**: ported/hand-authored JSON stays in `src/main/resources/`, repetitive same-shape artifacts are generated via Fabric Data Generation API into `src/main/generated/`. Generated output is committed to git so the diff is reviewable and the build doesn't require running datagen.

### Hand-shipped (`src/main/resources/data/fizzle_enchanting/...`)

| Artifact | Approx count | Why |
|---|---|---|
| Enchantment JSONs (NeoEnchant+ ports) | 51 | Pure `cp` + namespace rewrite from NeoEnchant+ jar. Datagen would mean re-authoring each in Java — massive step backward. |
| Enchantment JSONs (Icy Thorns, Shield Bash) | 2 | One-offs; pure EEC. Easier to hand-write than author a one-time builder. |
| Exclusive-set tag files (`tags/enchantment/exclusive_set/`) | ~12 | Ported from NeoEnchant+ verbatim. |
| `enchanting_stats/*.json` | ~30 | Copied verbatim from Zenith. |
| Custom-recipe-type JSONs (`fizzle_enchanting:enchanting`, `keep_nbt_enchanting`) | 7 | Two custom recipe types — authoring a `RecipeProvider` for them is more code than writing 7 JSONs. |
| Block / item tags | ~4 | Low volume. |
| `assets/.../lang/en_us.json` | 1 | **Single hand-shipped lang file** for everything — 53 enchant keys (sed'd from NeoEnchant+) plus ~40 block/item display names. One file is easier to audit; 40 entries isn't enough boilerplate to justify a `FabricLanguageProvider`. |

### Datagen (`src/main/generated/`)

| Provider | Responsibility | Approx count |
|---|---|---|
| `FizzleModelProvider extends FabricModelProvider` | Shelf block models (cube_column variants, shared template), library orientable, filtering/treasure shelf; parented item models for every block; generated item models for `infused_breath`, `warden_tendril`, `prismatic_web`, 3 tomes. | ~29 block models + ~35 item models + ~29 blockstates |
| `FizzleBlockLootTableProvider extends FabricBlockLootTableProvider` | `dropSelf(block)` per shelf/library/filtering/treasure; no special drops in MVP. | ~29 |
| `FizzleRecipeProvider extends FabricRecipeProvider` | Vanilla-shape recipes only — shelf crafts, tome crafts, Prismatic Web, iron-block anvil repair, library + ender library. Custom recipe types stay hand-shipped. | ~30 |

### Wiring

- `fabric.mod.json` entrypoint:
  ```json
  "entrypoints": { "fabric-datagen": ["com.fizzlesmp.fizzle_enchanting.data.FizzleEnchantingDataGenerator"] }
  ```
- `FizzleEnchantingDataGenerator implements DataGeneratorEntrypoint` — registers the three providers above.
- Gradle: `loom { runs { datagen { … } } }` auto-configured by Loom when the `fabric-datagen` entrypoint is present. Invoked via `./gradlew runDatagen`.
- Output path: `src/main/generated/` (Fabric default). Committed to git alongside hand-shipped resources. Regenerated only when a provider changes.

### Non-goals for MVP datagen

- **Advancements** — deferred to Phase 8 polish; no provider in Phase 1.
- **Lang** — handled by the hand-shipped file per the table above.
- **Enchantment JSONs / tags / stats** — never datagenned; see above.

## Configuration (MVP)

`config/fizzle_enchanting.json` (loaded from `FabricLoader.getConfigDir()`):

```json
{
  "configVersion": 1,
  "enchantingTable": {
    "allowTreasureWithoutShelf": false,
    "maxEterna": 50,
    "showLevelIndicator": true
  },
  "shelves": {
    "sculkShelfShriekerChance": 0.02,
    "sculkParticleChance": 0.05
  },
  "anvil": {
    "prismaticWebRemovesCurses": true,
    "prismaticWebLevelCost": 30,
    "ironBlockRepairsAnvil": true
  },
  "library": {
    "ioRateLimitTicks": 0
  },
  "tomes": {
    "scrapTomeXpCost": 3,
    "improvedScrapTomeXpCost": 5,
    "extractionTomeXpCost": 10,
    "extractionTomeItemDamage": 50,
    "extractionTomeRepairPercent": 0.25
  },
  "warden": {
    "tendrilDropChance": 1.0,
    "tendrilLootingBonus": 0.10
  },
  "display": {
    "showBookTooltips": true,
    "overLeveledColor": "#FF6600"
  }
}
```

**Library tier caps are NOT in config.** `BASIC_LEVEL_CAP = 16` and `ENDER_LEVEL_CAP = 31` are code constants. They drive `points(level) = 2^(level−1)` math and are baked into on-disk NBT pools; changing them mid-save would corrupt stored books. Config only exposes `ioRateLimitTicks` (a runtime throttle, safe to change anytime).

**Per-iteration sections ship absent in v1.** `levelCaps`, `infuser`, and any other backlog sections are **not** added to the MVP config as disabled stubs. Each iteration introduces its own top-level section (e.g. `"infuser": {…}`, `"grindstone": {…}`, `"levelCaps": {…}`) in the release that lands it. Existing v1 configs pick the new section up automatically via `fillDefaults()`'s null-check — no operator action required. Dead stubs shipped ahead of time tend to accumulate stale field names when schemas drift between planning and landing; we pay that cost only when the iteration actually ships.

Stat values per block are **not** in this file — they're in `data/fizzle_enchanting/enchanting_stats/*.json` so datapacks (and the built-in datagen) own them.

### Validation (clamps applied on load)

Matching `fizzle-difficulty`'s helper style (`clampNonNegative`, `clampPositive`, `clampUnit`). Out-of-range values are clamped with a `LOGGER.warn`.

| Field | Rule |
|---|---|
| `enchantingTable.maxEterna` | clamp to `[1, 100]` |
| `shelves.sculkShelfShriekerChance` | `clampUnit` (0–1) |
| `shelves.sculkParticleChance` | `clampUnit` (0–1) |
| `anvil.prismaticWebLevelCost` | `clampNonNegative` |
| `library.ioRateLimitTicks` | `clampNonNegative` |
| `tomes.scrapTomeXpCost` | `clampNonNegative` |
| `tomes.improvedScrapTomeXpCost` | `clampNonNegative` |
| `tomes.extractionTomeXpCost` | `clampNonNegative` |
| `tomes.extractionTomeItemDamage` | `clampNonNegative` |
| `tomes.extractionTomeRepairPercent` | `clampUnit` (0–1) |
| `warden.tendrilDropChance` | `clampUnit` (0–1) |
| `warden.tendrilLootingBonus` | `clampUnit` (0–1) |
| `display.overLeveledColor` | regex `^#[0-9A-Fa-f]{6}$`; on mismatch warn + fall back to `#FF6600` |

### Migration strategy

Match `fizzle-difficulty`: `configVersion = 1` is a reserved marker. A `migrate()` hook runs **after load, before `fillDefaults()`**, and is a no-op at v1. `fillDefaults()` already handles the additive case (new section or field introduced in a later version → null check fills defaults). **`configVersion` itself is bumped only for renames, removes, or semantic changes** — purely additive adds (including the per-iteration sections above) stay at v1 indefinitely.

```java
private static final int CURRENT_VERSION = 1;

private void migrate() {
    if (configVersion >= CURRENT_VERSION) return;
    // while (configVersion < CURRENT_VERSION) switch (configVersion) { case 1 -> migrateV1toV2(); ... }
    configVersion = CURRENT_VERSION;
}
```

When a schema-breaking change lands (e.g. renaming `tomes.extractionTomeItemDamage` → `tomes.extractionTomeDurabilityCost`), bump `CURRENT_VERSION`, implement the per-version step, and `save()` after migration completes.

## Commands (MVP)

| Command | Permission | Description |
|---|---|---|
| `/fizzleenchanting reload` | 2 | Reload config from disk |
| `/fizzleenchanting stats <player>` | 0 | Dump the stats of the enchanting table the player is looking at |
| `/fizzleenchanting library <player> dump` | 2 | Dump point contents of the library the player is looking at |
| `/fizzleenchanting give-tome <player> <type>` | 2 | Debug helper for testing tome flows |

## Integrations

All four shipped at launch as lightweight display adapters (no direct runtime dependency, entry-point-gated in `fabric.mod.json`):

- **EMI** — shelf stat info panel, tome recipe rendering.
- **REI** — same adapter pattern as EMI.
- **JEI** — compatibility for players who run JEI instead of EMI/REI.
- **Jade** — probe tooltip for enchantment tables (showing current stats) and libraries (showing book points for the enchant under the cursor).

Each integration module lives under `compat/<name>/` with its own `entrypoints` stanza. Failure to load one (because the user doesn't have it installed) is silent.

## Fabric & Quilt

Single Fabric artifact. `fabric.mod.json` declares `depends: { "fabricloader": ">=0.16.10", "fabric-api": "*", "minecraft": "~1.21.1" }`. Quilt users run it via Quilted Fabric API transparently — no `quilt.mod.json` is shipped, no dual build. If a Quilt-native release becomes worth doing later it's an additive artifact, not a rewrite.

## Build & Ship

- **Build:** Gradle + Loom 1.9-SNAPSHOT, officialMojangMappings, Java 21, Fabric Loader 0.16.10, Fabric API 0.116.1+1.21.1. Matches `fizzle-difficulty` exactly.
- **Split sourcesets:** `splitEnvironmentSourceSets()` — client-only code under `src/client`.
- **Distribution:** Ships as a FizzleSMP-local jar (packwiz `Mod Loader: Manual` plugin entry). Public Modrinth/CurseForge release is a later decision.
- **License:** MIT on the code. Asset credits in `README.md` point at Apotheosis (Shadows_of_Fire) and Zenith (bageldotjpg) under their respective licenses. **MVP ships private-only** (FizzleSMP-local jar via `Mod Loader: Manual`) — no license action required.
- **Mixed-license surface to audit before any public release:**
  - **Apotheosis textures via Zenith** — governed by Apotheosis's separate `LICENSE_ASSETS`. Requires attribution + compliance pass.
  - **NeoEnchant+ enchantment JSONs + lang keys** — CC BY-NC-SA 4.0 (Hardel). Redistributing the 49 ported JSONs pulls the SA obligation onto derivative works and adds the NC commercial-use question to any public CurseForge/Modrinth hosting. Two paths at publish time: (a) dual-license the `data/` dir as CC BY-NC-SA and clarify NC status for the target platform, or (b) re-author the 49 enchantment JSONs from scratch before shipping publicly.
  - Decision punted until a public release is actually on the table. Private SMP distribution does not trigger either obligation beyond attribution.

## MVP Phased Delivery

1. **Phase 1 — Scaffolding:** Gradle, `fabric.mod.json`, empty initializer, mixin config, datagen skeleton, local `./gradlew build`. Committed as `feat: scaffold fizzle-enchanting`.
2. **Phase 2 — Stat system & table:** `IEnchantingStatProvider`, datapack loader, menu mixin, server→client payloads, one wood shelf block to prove the loop.
3. **Phase 3 — Shelf family:** The other 7 launch blocks + their JSON stat entries + particles.
4. **Phase 4 — Anvil & Library:** The 4 MVP anvil tweaks + the 2-tier library block with menu and storage adapter.
5. **Phase 5 — Tomes:** 3 tome items (Scrap, Improved Scrap, Extraction) wired through the anvil mixin.
6. **Phase 6 — Enchantments:** Port 49 NeoEnchant+ JSONs + author 2 Zenith-flavor JSONs (Icy Thorns, Shield Bash). No Java code — purely `resources/data/` work + lang keys.
7. **Phase 7 — Integrations:** EMI, REI, JEI, Jade adapters.
8. **Phase 8 — Polish:** Advancement tree, tooltips, config docs, test-server playthrough.

Each phase ends with a conventional commit (`feat(enchanting): …`) and is independently reviewable.

---

# Iteration Backlog — Consolidating Five Mods

Each of the five modpack dependencies we want to drop falls into one of two categories:

- **Superseded by MVP** — the MVP's Zenith-equivalent feature already covers the same player need. No new code in `fizzle-enchanting`; the mod just comes out of the pack once MVP ships and is tested.
- **Needs an iteration** — MVP doesn't cover it. A follow-up iteration adds the feature, then the mod comes out.

Summary:

| Mod | Status | Reason |
|---|---|---|
| **Grind Enchantments** | ✅ Superseded by MVP | Scrap / Improved Scrap / Extraction tomes cover the enchant-salvage workflow. The anvil is now the single "enchant management" surface; the grindstone stays vanilla. |
| **Easy Anvils** | ⚠️ Mostly unnecessary | Its main QoL features (cap removal, items-persist, name-tag tweaks) aren't covered by Zenith. Decide per feature whether to implement, or accept that this mod stays in the pack. |
| **BeyondEnchant** | 🔨 Needs Iteration 1 | Raising vanilla enchantment caps is additive — not in Zenith's scope. |
| **NeoEnchant+** | ✅ Absorbed into MVP | 49 of NeoEnchant+'s 56 JSONs ship directly in the MVP enchantment roster (7 cut, see "MVP Enchantments" above). Pulled forward from Iteration 2 because the port is pure JSON copy — no reason to defer. |
| **Enchanting Infuser** | 🔨 Needs Iteration 2 | Deterministic-selection enchanting block has no Zenith analogue. |

**Ordering principle:** each iteration below leaves the modpack in a shippable state. A mod only comes out of `plugins/gameplay.md` once either (a) the MVP or its iteration has been tested on the live server, or (b) it's marked superseded and the pack is confirmed to still work without it.

## Superseded — Grind Enchantments

[Grind Enchantments](https://www.curseforge.com/minecraft/mc-mods/grind-enchantments) (CF 379680) transfers enchantments from items to books via the grindstone. The MVP covers this player-facing need three ways through tomes at the anvil:

- **Cheap gamble** → Scrap Tome (item destroyed, one random enchant → book)
- **Full salvage, lose the item** → Improved Scrap Tome (item destroyed, all enchants → one book)
- **Full salvage, keep the item** → Extraction Tome (item preserved, all enchants → one book)

All three produce the same end artifact (an enchanted book). They cost a tome + XP, which balances the convenience compared to the free-but-destructive vanilla grindstone. The vanilla grindstone stays as-is — it still disenchants for XP — and players who want the enchants back use a tome.

**Acceptance:**
- [ ] Once MVP Phase 5 ships, test all three tome workflows on the live server.
- [ ] Update `docs/guides/enchanting-guide.md` — replace the "Grindstone — Grind Enchantments" section with "Tomes — Scrap, Improved Scrap, Extraction."
- [ ] Remove Grind Enchantments from `plugins/gameplay.md` and `modpack/mods/`.
- [ ] Drop the Grind-Enchantments row from `docs/compatibility-matrix.md`.

## Mostly unnecessary — Easy Anvils (decision pending)

[Easy Anvils](https://www.curseforge.com/minecraft/mc-mods/easy-anvils) (CF 682567, Fuzs) provides anvil QoL that Zenith does not cover. Its usefulness shrinks once tomes handle enchantment movement — the anvil becomes a less-critical tool because endgame players are removing/repairing enchants via tomes rather than repeatedly combining items. The features Zenith/MVP do **not** replace:

- **Remove the "too expensive!" cap** — the one feature players notice most.
- Items persist on GUI close; inline name-tag rename; flat rename cost; configurable durability.

**Decision point (TBD):** two options —
1. **Small follow-up iteration** that adds just `tooExpensiveCap` to the config and clamps/removes the ceiling via a mixin on `AnvilMenu#mayPickup` / `#onTake`. One flag, one mixin hook. Skip everything else. Then remove Easy Anvils from the pack.
2. **Leave Easy Anvils in the pack.** It's stable, doesn't conflict with anything Fizzle Enchanting ships, and requires no version pinning. The cost of coexistence is low.

Either is fine. Pick when the MVP is shipped and playtested — if players complain about the cap, do option 1.

**Config stub** (if we take option 1):
```json
"anvil": {
  "tooExpensiveCap": 40
}
```

## Iteration 1 — Absorb BeyondEnchant

[BeyondEnchant](https://www.curseforge.com/minecraft/mc-mods/beyondenchant) (CF 1135664, by Hardel) raises `max_level` on ~16 vanilla enchantments via `data/minecraft/enchantment/*.json` overrides.

This iteration is *mostly* a datapack shipped inside the mod jar. The only Java work is a small UX affordance: a tooltip color for enchantments that have been raised beyond their vanilla cap (see `overLeveledColor` in the MVP config).

**Shipped level caps** (match BeyondEnchant defaults so the swap is transparent):

| Enchantment | Vanilla | New |
|---|---|---|
| Sharpness | 5 | 7 |
| Smite | 5 | 7 |
| Bane of Arthropods | 5 | 7 |
| Efficiency | 5 | 10 |
| Protection | 4 | 5 |
| Blast Protection | 4 | 5 |
| Projectile Protection | 4 | 5 |
| Fire Protection | 4 | 5 |
| Feather Falling | 4 | 5 |
| Unbreaking | 3 | 10 |
| Fortune | 3 | 5 |
| Looting | 3 | 5 |
| Mending | 1 | 5 |
| Power | 5 | 7 |
| Punch | 2 | 5 |
| Respiration | 3 | 5 |

**Implementation:**
- Ship 16 override JSONs under `data/minecraft/enchantment/*.json` in the mod's resources.
- Server-config flag to disable the overrides en masse (for worlds that prefer vanilla caps).
- Coordinate with the existing **Spectrum-BeyondEnchant-LevelCap-Fix Paxi datapack** — either that datapack stays (Fizzle Enchanting produces the same `max_level` values, so it continues to work unchanged), or we fold its Spectrum recipe overrides into this mod's resources. Decision point: the Paxi datapack is simpler to maintain than bundling mod-specific recipe overrides in `fizzle-enchanting`, so default to **keeping it separate** and document the dependency.

**Config additions** (`"levelCaps"` section):
```json
"enabled": true,
"perEnchantment": {
  "minecraft:sharpness": 7,
  "minecraft:efficiency": 10,
  "minecraft:unbreaking": 10
}
```
`perEnchantment` is a *further* override on top of the shipped defaults — lets operators raise or lower individual caps without editing the jar. At startup, load these overrides into `BuiltInRegistries.ENCHANTMENT` via a datapack reload listener. `overLeveledColor` lives under the MVP `display` section (the tooltip color is a UI concern, not a levelCap concern) — do not duplicate it here.

**Acceptance:**
- [ ] All 16 enchantments cap at the new values at the enchanting table, anvil, and via commands.
- [ ] Spectrum-BeyondEnchant-LevelCap-Fix Paxi datapack still applies cleanly.
- [ ] Remove BeyondEnchant from pack.

## Absorbed in MVP — NeoEnchant+

[NeoEnchant+](https://www.curseforge.com/minecraft/mc-mods/neoenchant) (CF 1135663, by Hardel, CC BY-NC-SA) is absorbed directly into MVP — the iteration never runs as a standalone phase. Its enchantment JSONs are the bulk of our roster.

**Shipped:** 49 of 56 enchantment JSONs, 100% mechanical port (namespace rewrite `enchantplus:` → `fizzle_enchanting:`). Full inventory under "MVP Enchantments (51)" above.

**Cut:** `axe/timber`, `pickaxe/bedrock_breaker`, `pickaxe/spawner_touch`, `tools/auto_smelt`, `helmet/auto_feed`, `chestplate/magnet`, `sword/runic_despair`. Reasons logged in the MVP roster section.

**Acceptance (MVP-gated):**
- [ ] All 49 ported enchants appear in-game at tables, in creative, and on books.
- [ ] Exclusive-set tags respect the new namespace (no lingering `enchantplus:` refs).
- [ ] Lang keys render in tooltips.
- [ ] License/attribution check against NeoEnchant+ CC BY-NC-SA terms clears for FizzleSMP's distribution model before any public release.
- [ ] `docs/guides/enchanting-guide.md` describes the new roster.
- [ ] Remove NeoEnchant+ from `plugins/*.md` and `modpack/mods/`.

## Iteration 2 — Absorb Enchanting Infuser

[Enchanting Infuser](https://www.curseforge.com/minecraft/mc-mods/enchanting-infuser) (CF 551151, by Fuzs) adds a separate block that lets players pick specific enchantments for a configurable XP cost — the anti-gambling alternative to vanilla enchanting.

This is the biggest UX addition remaining. It effectively introduces a second enchanting block alongside our stat-driven table.

**Design decision:** Ship it as a **new block** (`fizzle_enchanting:infusion_table`) rather than a mode toggle on our existing table, to preserve the distinct gameplay loops. The vanilla table + our shelf stats stay the "random but strong" path; the infuser stays the "deterministic but expensive" path.

**Two tiers** (matching current Infuser behavior):
- **Infusion Table** — picks enchantments up to vanilla max levels. Moderate XP cost.
- **Advanced Infusion Table** — picks enchantments up to the raised caps from Iteration 1 (BeyondEnchant). Higher XP cost.

**Implementation:**
- New block + block entity, own menu/screen.
- Menu lists every enchantment legal for the item in the input slot (respecting our typed-tome tag system, BeyondEnchant-style caps, and the datapack-driven `supported_items` rules).
- Server-side XP cost formula: a base cost per enchantment + a per-level multiplier + rarity multiplier. All three configurable.
- Include enchantments that don't normally appear at vanilla tables (Mending, Soulbound from YIGD, etc.) — currently enabled via datapack on the live pack, should continue working.
- Support server-side permission flag so the block can be gated (e.g. only crafting tier players can use it).

**Config additions** (`"infuser"` section):
```json
"enabled": true,
"basicTier": {
  "baseXpCost": 3,
  "perLevelCost": 5,
  "rarityMultipliers": {"common": 1.0, "uncommon": 1.5, "rare": 2.0, "very_rare": 3.0}
},
"advancedTier": {
  "baseXpCost": 10,
  "perLevelCost": 15,
  "rarityMultipliers": {"common": 1.5, "uncommon": 2.0, "rare": 3.0, "very_rare": 5.0}
},
"maxTotalCostLevels": 150
```

**Acceptance:**
- [ ] Both tier blocks craftable and functional.
- [ ] XP costs tunable per-server via config.
- [ ] Currently-datapacked "special" enchants (Mending, Soulbound) selectable at the infuser.
- [ ] Update `docs/guides/enchanting-guide.md` — replace the "Enchanting Infuser" section with the Fizzle equivalent.
- [ ] Remove Enchanting Infuser from pack.

## Iteration 3 — Absorb Easy Magic

[Easy Magic](https://www.curseforge.com/minecraft/mc-mods/easy-magic) (CF 456239, Fuzs) is the sister mod of Easy Anvils/Enchanting Infuser, currently in the pack. It keeps items in the enchanting table across GUI close and lets players re-roll enchantments without pulling items.

**Scope — item persistence only.** Easy Magic's "re-roll" feature is **explicitly cut**: re-roll would undermine the MVP's Clues/Arcana selection system (the whole point of the stat-driven table is that what you see *is* what you'll get — re-rolling on demand collapses that back into a slot-machine loop). Follows Zenith's approach, which also omits re-roll.

**What this iteration ships:**
- **Item persistence on GUI close** — the item in the input slot stays in place across menu close/reopen. Requires adding a block entity backing the enchanting table (vanilla has none) to hold the slot contents, plus a migration path for already-placed tables in existing worlds.
- Nothing else.

**MVP-time commitment: none.** The re-roll cut means we don't need to reserve menu button IDs or structure the table mixin around future UI additions. The BE addition is self-contained to Iteration 3. MVP button-ID namespace stays `0/1/2` (vanilla enchant slots) + `3` (our crafting extract) with no reservations beyond that.

**Acceptance:**
- [ ] Items placed in the enchanting table persist across GUI close and player logout.
- [ ] Already-placed vanilla enchanting tables upgrade cleanly (new BE attached on chunk load without dropping state).
- [ ] Update `docs/guides/enchanting-guide.md` to describe the persistence behavior.
- [ ] Remove Easy Magic from `plugins/*.md` and `modpack/mods/`. Leaves zero Fuzs enchanting mods in the pack.

## Tracking & Bookkeeping

When an iteration lands:
1. Update the `CHANGELOG.md` in this project under `[Unreleased] → Added`.
2. In `/home/rfizzle/Projects/FizzleSMP/CHANGELOG.md`: `### Removed` entry for the replaced mod, `### Changed` entry for the Fizzle Enchanting feature surface.
3. Remove the replaced mod from `plugins/gameplay.md` (or `utility.md` for libraries like Puzzles Lib / Forge Config API Port if they become orphaned).
4. Remove matching entries from `docs/compatibility-matrix.md`.
5. Regenerate packwiz (`./scripts/sync-packwiz.sh --prune`) and commit the metadata changes alongside the plugin/doc edits in the same commit.
6. Verify on the live test server before the replacement mod is removed from production.
