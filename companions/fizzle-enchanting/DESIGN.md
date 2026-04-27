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
| Full shelf roster (27 blocks) | ✅ | 25 themed shelves + filtering shelf + treasure shelf — see "Shelf Blocks" below |
| Enchantment-table crafting (stat-gated) | ✅ | Zenith's `zenith:enchanting` + `zenith:keep_nbt_enchanting` recipe types; gates tier-3 shelves, tome upgrades, Ender Library |
| Enchantment Library block (2 tiers) | ✅ | Basic (lvl 16) + Ender (lvl 31) |
| Prismatic Web (removes curses) | ✅ | The only non-tome anvil interaction Zenith ships |
| Iron block repairs damaged anvil | ✅ | Zenith's iron-block anvil-repair recipe |
| Scrap Tome + Improved Scrap Tome | ✅ | Both destroy the item; Scrap outputs ~half of enchants (Apothic behavior), Improved outputs all |
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

**Mixin footprint — 6 mixins + 2 access wideners:**

| Mixin | Target | Purpose |
|---|---|---|
| `EnchantmentTableBlockMixin` | `EnchantingTableBlock` | Swap in custom menu; drive shelf particles from table |
| `EnchantmentMenuAccessor` | `EnchantmentMenu` | Expose enchantSlots, random, enchantmentSeed |
| `AnvilMenuAccessor` | `AnvilMenu` | Expose cost + repairItemCountCost fields |
| `AnvilMenuMixin` | `AnvilMenu` | Dispatch custom anvil operations (tomes, prismatic web, iron repair); left-replacement with re-entrancy guard |
| `EnchantmentMixin` | `Enchantment` | Override maxLevel from per-enchantment config; over-leveled name color |
| `ItemMixin` | `Item` | Global minimum enchantability floor via `globalMinEnchantability` (default 1) |

**Access wideners:** `Slot.y` (mutable field), `EnchantmentScreen.renderBook()` (accessible method).

Apothic uses 15+ mixins + 3 ASM coremods. Fizzle leverages 1.21.1 data-driven enchantments and Fabric API events instead, keeping the mixin count at 6 with zero ASM.

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

#### Complete Shelf Stats Table

All values are from the implemented `enchanting_stats/*.json` files (31 JSON stat files).

| Block | Tier | maxE | eterna | quanta | arcana | clues | rectif. | Particle | Notes |
|---|---|---|---|---|---|---|---|---|---|
| Vanilla Bookshelf | Starter | 15 | 1 | 0 | 0 | 0 | 0 | ENCHANT | Tag fallback |
| `stoneshelf` | Starter | 0 | −1.5 | 0 | −7.5 | 0 | 0 | ENCHANT | Negative stats for tuning |
| `beeshelf` | Starter | 0 | −15 | 100 | 0 | 0 | 0 | ENCHANT | Max quanta, negative eterna |
| `melonshelf` | Starter | 0 | −1 | −10 | 0 | 0 | 0 | ENCHANT | Negative quanta/eterna |
| `dormant_deepshelf` | Starter | 15 | 1 | 0 | 0 | 0 | 0 | ENCHANT_SCULK | Pre-infusion |
| `hellshelf` | Early | 22.5 | 1.5 | 3 | 0 | 0 | 0 | ENCHANT_FIRE | Nether T1 |
| `seashelf` | Early | 22.5 | 1.5 | 0 | 2 | 0 | 0 | ENCHANT_WATER | Ocean T1 |
| `infused_hellshelf` | Mid | 27 | 1.75 | 1.75 | 0 | 0 | 0 | ENCHANT_FIRE | Table craft |
| `infused_seashelf` | Mid | 27 | 1.75 | 0 | 1.75 | 0 | 0 | ENCHANT_WATER | Table craft |
| `glowing_hellshelf` | Mid | 30 | 2 | 2 | 4 | 0 | 0 | ENCHANT_FIRE | |
| `blazing_hellshelf` | Mid | 30 | 4 | 5 | 0 | −1 | 0 | ENCHANT_FIRE | Removes a clue |
| `crystal_seashelf` | Mid | 30 | 2 | 4 | 2 | 0 | 0 | ENCHANT_WATER | |
| `heart_seashelf` | Mid | 30 | 3 | 0 | 10 | 0 | −5 | ENCHANT_WATER | Negative rectification |
| `deepshelf` | Late | 35 | 2.5 | 5 | 5 | 0 | 0 | ENCHANT_SCULK | Table craft |
| `echoing_deepshelf` | Late | 37.5 | 2.5 | 0 | 15 | 0 | 0 | ENCHANT_SCULK | Arcana-focused |
| `soul_touched_deepshelf` | Late | 37.5 | 2.5 | 15 | 0 | 0 | 0 | ENCHANT_SCULK | Quanta-focused |
| `echoing_sculkshelf` | Late | 40 | 5 | 5 | 15 | 1 | 0 | ENCHANT_SCULK | Clue + sculk |
| `soul_touched_sculkshelf` | Late | 40 | 5 | 15 | 5 | 0 | 5 | ENCHANT_SCULK | Rectification + sculk |
| `endshelf` | End | 45 | 2.5 | 5 | 5 | 0 | 0 | ENCHANT_END | |
| `pearl_endshelf` | End | 45 | 5 | 7.5 | 7.5 | 0 | 0 | ENCHANT_END | Balanced end variant |
| `draconic_endshelf` | Max | 50 | 10 | 0 | 0 | 0 | 0 | ENCHANT_END | Only way to max eterna |
| `sightshelf` | Utility | — | 0 | 0 | 0 | 1 | 0 | — | Clue only |
| `sightshelf_t2` | Utility | — | 0 | 0 | 0 | 2 | 0 | — | Double clue |
| `rectifier` | Utility | — | 0 | 0 | 0 | 0 | 10 | — | Fizzle-original |
| `rectifier_t2` | Utility | — | 0 | 0 | 0 | 0 | 15 | — | Fizzle-original |
| `rectifier_t3` | Utility | — | 0 | 0 | 0 | 0 | 25 | — | Fizzle-original |
| `filtering_shelf` | Utility | 15 | 1 | 0 | 0 | 0 | 0 | — | Blacklists enchantments |
| `treasure_shelf` | Utility | 0 | 0 | 0 | 0 | 0 | 0 | — | Enables treasure enchants |

**Special shelf notes:**
- **Filtering shelf**: `ChiseledBookShelfBlock` extension with 6 book slots. Stored books blacklist their enchantments from the table. Currently gives flat stats from JSON regardless of book count — **planned to match Apothic** which scales +0.5 eterna / +1 arcana per book stored.
- **Treasure shelf**: `TreasureFlagSource` marker; no stat contribution; custom texture.
- **Sculk shelves**: `randomTicks()` set on block properties; config fields `sculkShelfShriekerChance` and `sculkParticleChance` exist but `randomTick()` is not yet wired — **planned to implement** ambient sounds matching Apothic.

**Non-shelf stat providers** (vanilla blocks picked up by the stat registry):

| Block | maxE | eterna | quanta | arcana | rectif. | Notes |
|---|---|---|---|---|---|---|
| Amethyst Cluster | — | 0 | 0 | 0 | +1.5 | Rectification bonus |
| Basic Skulls (zombie, piglin, creeper) | 0 | 0 | +5 | 0 | 0 | Quanta via tag |
| Wither Skeleton Skull | 0 | 0 | +10 | 0 | 0 | Double skull quanta |

Values are the authoritative source of truth in `data/fizzle_enchanting/enchanting_stats/*.json` (31 files). The table above reflects the current implementation.

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
| `infused_seashelf` | `seashelf` | `infused_seashelf` | 22.5 | 15 | 10 | enchanting |
| `infused_hellshelf` | `hellshelf` | `infused_hellshelf` | 22.5 | 30 | 0 | enchanting |
| `deepshelf` | `dormant_deepshelf` | `deepshelf` | 30 | 40 | 40 | enchanting |
| `improved_scrap_tome` | `scrap_tome` | `improved_scrap_tome` × 4 | 22.5 | 25 | 35 | enchanting |
| `extraction_tome` | `improved_scrap_tome` | `extraction_tome` × 4 | 30 | 25 | 45 | enchanting |
| `infused_breath` | `minecraft:dragon_breath` | `infused_breath` × 3 | 40 | 15 | 60 | enchanting |
| `budding_amethyst` | `minecraft:amethyst_block` | `minecraft:budding_amethyst` | 30 | 30 | 50 | enchanting |
| `golden_carrot` | `minecraft:carrot` | `minecraft:golden_carrot` | 10 | 10 | 0 | enchanting |
| `honey_xp_t1` | `minecraft:honey_bottle` | `minecraft:experience_bottle` × 1 | 10 | 25 | 25 | enchanting |
| `honey_xp_t2` | `minecraft:honey_bottle` | `minecraft:experience_bottle` × 8 | 30 | 25 | 25 | enchanting |
| `honey_xp_t3` | `minecraft:honey_bottle` | `minecraft:experience_bottle` × 32 | 50 | 25 | 25 | enchanting |
| `echo_shard` | `minecraft:echo_shard` | `minecraft:echo_shard` × 4 | 35 | 50 | 50 | enchanting |
| `ender_library` | `library` | `ender_library` | 50 | 45 | 100 | keep_nbt_enchanting |
| `disc_eterna` | `#creeper_drop_music_discs` | `disc_eterna` | 40 | 0 | 0 | enchanting |
| `disc_quanta` | `#creeper_drop_music_discs` | `disc_quanta` | 10 | 40 | 0 | enchanting |
| `disc_arcana` | `#creeper_drop_music_discs` | `disc_arcana` | 10 | 0 | 40 | enchanting |

All eterna thresholds are scaled ~0.5× from Apothic (Fizzle maxE=50 vs Apothic maxE=100). 4 Apothic recipes intentionally cut: Inert Trident → Trident, Flimsy Ender Lead → Lead, Ender Lead → Occult Lead (all belong to Apothic modules we don't ship).

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

**Current state: book-only extraction.** The current implementation produces enchanted books only — the player applies them via anvil. Apothic applies enchantments directly to any item in the extract slot. **Planned to align with Apothic** for direct-item application, which streamlines the UX and cuts the anvil step.

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
| **Scrap Tome** (`scrap_tome`) | **~Half** of enchantments (random) | **Destroyed** | Enchanted book with the removed enchantments |
| **Improved Scrap Tome** (`improved_scrap_tome`) | **All** enchantments | **Destroyed** | Enchanted book with all the enchantments |
| **Extraction Tome** (`extraction_tome`) | **All** enchantments | **Preserved**, fully unenchanted | Enchanted book with all the enchantments |

The progression is: Scrap Tome is cheap but loses the item and roughly half the enchants (matching Apothic's ~half behavior — **currently implemented as 1 random, planned to align with Apothic**). Improved Scrap Tome still burns the item but salvages every enchantment. Extraction Tome does the same salvage *and* gives the item back, making it the "best-of-both" tier and accordingly the most expensive to craft. The Extraction Tome also exposes an item-repair side-path using the anvil fuel slot (Zenith behavior preserved).

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

### Foreign enchantment support

The gameplay loop depends on *every* enchantment in the pack eventually rolling at the table — that's the discovery feed that stocks the library pool, which in turn makes the library the deterministic "pay points + XP to get what you want" dispenser. Enchantments that ship with `weight: 0` or narrow `supported_items` break this loop: they exist in-game but the table will never produce them. `minecraft:mending` (treasure-only) and `yigd:soulbound` (weight 0 in YIGD) are the canonical examples on FizzleSMP, currently patched via the `EnchantingInfuser-Mending-Soulbound.zip` Paxi pack.

Fizzle Enchanting ships a **bundled datapack override** inside the jar that raises weights and broadens `supported_items` on a curated foreign-enchant list. Overrides live at `src/main/resources/data/<source>/enchantment/<id>.json` — standard vanilla datapack paths. Any higher-priority datapack (world-level or Paxi) wins cleanly, so operators who want different values don't have to unbundle the jar.

**Shipped overrides (MVP):**

| Enchantment | Source | Why it needs an override |
|---|---|---|
| `minecraft:mending` | vanilla | Treasure-flagged. Override keeps the treasure flag (so a `treasure_shelf` still gates it) but raises weight enough to feed the library. |
| `yigd:soulbound` | You're in Grave Danger | Ships `weight: 0` — never rolls. Override sets a non-zero weight and binds `supported_items` to `#yigd:soulbindable` to match the existing Paxi pack's intent. |

**Config flag** (single boolean in the MVP config):

```json
"foreignEnchantments": {
  "applyBundledOverrides": true
}
```

Default `true`. Set `false` to disable the bundled overrides wholesale — for operators shipping their own curated pack. Per-enchant toggles are not exposed; the escape valve is a higher-priority datapack.

**Expanding the list** is pure-JSON: drop another override file into `resources/data/<ns>/enchantment/<id>.json` and it's picked up at load. New mods with restricted enchants are a PR, not a code change.

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
  "foreignEnchantments": {
    "applyBundledOverrides": true
  },
  "enchantmentOverrides": {
    "minecraft:sharpness": { "maxLevel": 7, "maxLootLevel": 5, "levelCap": -1 },
    "minecraft:efficiency": { "maxLevel": 10, "maxLootLevel": 5, "levelCap": -1 }
  },
  "display": {
    "showBookTooltips": true,
    "overLeveledColor": "#FF6600"
  }
}
```

**Library tier caps are NOT in config.** `BASIC_LEVEL_CAP = 16` and `ENDER_LEVEL_CAP = 31` are code constants. They drive `points(level) = 2^(level−1)` math and are baked into on-disk NBT pools; changing them mid-save would corrupt stored books. Config only exposes `ioRateLimitTicks` (a runtime throttle, safe to change anytime).

**Per-iteration sections ship absent in v1.** `levelCaps` and any other backlog sections are **not** added to the MVP config as disabled stubs. Each iteration introduces its own top-level section (e.g. `"levelCaps": {…}`) in the release that lands it. Existing v1 configs pick the new section up automatically via `fillDefaults()`'s null-check — no operator action required. Dead stubs shipped ahead of time tend to accumulate stale field names when schemas drift between planning and landing; we pay that cost only when the iteration actually ships.

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
| `/fizzleenchanting reload` | 2 | Reload config from disk, rebuild enchantment info registry, sync to all connected clients |
| `/fizzleenchanting stats <player>` | 0 | Dump the stats of the enchanting table the player is looking at |
| `/fizzleenchanting library <player> dump` | 2 | Dump point contents of the library the player is looking at |
| `/fizzleenchanting give-tome <player> <type>` | 2 | Debug helper for testing tome flows |

## Integrations

Seven integrations shipped, all as lightweight display adapters (no direct runtime dependency, entry-point-gated in `fabric.mod.json`):

| Mod | Plugin Classes | Features |
|---|---|---|
| **EMI** | `EmiEnchantingPlugin`, `EmiEnchantingRecipe` | 2 recipe categories (Shelves + Tomes), per-shelf info panels |
| **REI** | `ReiEnchantingPlugin`, `ReiEnchantingCategory`, `ReiEnchantingDisplay` | 2 recipe categories, same layout |
| **JEI** | `JeiEnchantingPlugin`, `JeiEnchantingCategory` | 2 recipe categories, same layout |
| **Jade** | `JadeEnchantingPlugin`, 2 providers | Enchanting table 5-axis stats + library enchant count |
| **WTHIT** | `WthitCommonPlugin`, `WthitClientPlugin`, 2 providers | Same as Jade; discovered via `waila_plugins.json` |
| **Trinkets** | `AccessorySlotHelper`, `TrinketsCompat` | Slot enumeration + enchantment level query (wired but idle — all enchantments are pure JSON) |
| **ModMenu** | `ModMenuIntegration` | Full Cloth Config screen, 7 categories, 19 entries |

Shared compat layer in `compat/common/`: `TableCraftingDisplayExtractor`, `TableCraftingDisplay`, `RecipeInfoFormatter`, `TomeRecipeClassifier`, `JadeTooltipFormatter`.

Each integration module lives under `compat/<name>/` with its own `entrypoints` stanza. All 9 entrypoints declared in `fabric.mod.json` (main, client, datagen, gametest, emi, rei_client, jei_mod_plugin, jade, modmenu). Failure to load one (because the user doesn't have it installed) is silent.

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

## Enchanting Table GUI

The GUI uses a custom texture at `textures/gui/enchanting_table.png` (176×197 pixels, taller than vanilla's 166px).

**Implemented elements:**
- Custom `FizzleEnchantmentScreen` extending `EnchantmentScreen`
- Three stat bars at y=75,85,95; width=110px; smooth interpolation (0.1F up, 0.075F down)
- Enchantment slot hover shows clue enchantments; partial vs full clue toggle via `isClientCluesExhausted`
- Info button `[i]` at (148,1) opens `EnchantingInfoScreen`
- Infusion display in slot 2 (yellow underline result name + cost)
- "Infusion Failed" display (red text) when item matches recipe but stats insufficient

**Enchantment Info Browser:**
- Opens from `[i]` button; scrollable 11-row enchantment list with scrollbar
- `PowerSlider` controls `currentPower` within quanta-adjusted range; recomputes enchantments on change
- Arcana weight table (Common/Uncommon/Rare/Very Rare) with per-enchantment weight + chance %
- Exclusion tooltips: "Exclusive With: ..." in red via `Enchantment.areCompatible()` check

**Planned GUI improvements (to match Apothic):**
- Stat bar tooltips with descriptive text explaining what each stat does (Apothic shows "Eterna: Enchanting Power" + explanation + value)
- `drawOnLeft` side-panel tooltips for quanta buff percentage and arcana bonus breakdown with weight table
- Power range display on main screen slot tooltips
- Item enchantability display
- Explicit clue count display
- XP cost in both points and levels (currently levels only)

## Per-Enchantment Configuration

Every enchantment (vanilla and modded) gets configurable overrides synced from server to client:

- **`maxLevel`** — cap on effective level the enchanting table can produce
- **`maxLootLevel`** — different max for loot table generation vs. direct table enchanting
- **`levelCap`** — hard cap on effective level regardless of NBT

Synced via `EnchantmentInfoPayload` (S2C) on player join and datapack reload. The `/fizzleenchanting reload` command triggers config re-read + registry rebuild + client sync.

**`EnchantmentMixin.getMaxLevel()`** reads from `EnchantmentInfoRegistry` at runtime. **`EnchantmentMixin.getFullname()`** applies configurable hex color (`display.overLeveledColor`, default `#FF6600`) for enchantments above their vanilla max.

**`PowerFunction`** system provides per-enchantment min/max power overrides:
- `DefaultMinPowerFunction`: vanilla cost extrapolation with 1.6 exponent for above-max levels
- `DefaultMaxPowerFunction`: flat cap at 200
- Currently sealed with 2 built-in implementations — **planned: configurable power functions** via expression system (Apothic uses EvalEx)

## Advancements

18 custom advancements with a custom `enchanted_at_table` criterion trigger:

| Advancement | Parent | Trigger |
|---|---|---|
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

The custom `enchanted_at_table` trigger fires with item/levels/eterna/quanta/arcana/rectification predicates.

## Particles & Music

**4 custom particle types:** `ENCHANT_FIRE`, `ENCHANT_WATER`, `ENCHANT_SCULK`, `ENCHANT_END`. 104 textures (26 SGA glyphs × 4 themes). `FlyTowardsPositionParticle.EnchantProvider` drives motion toward the table. `EnchantmentTableBlockMixin.animateTick` spawns themed particles per shelf via `IEnchantingStatProvider`.

**3 music discs** (Eterna, Quanta, Arcana) with sound events, OGG files, jukebox songs, item models/textures. Produced via infusion recipes at the enchanting table.

## Test Infrastructure

- **61 JUnit tests** + **31 gametest classes**
- `useJUnitPlatform()`, `fabric-loader-junit`, dedicated gametest sourceset
- Covers config parsing, stat accumulation, point math, selection algorithm, recipe matching, library operations, anvil dispatch

## Design Divergences from Apothic

The following table documents intentional differences from Apothic Enchanting and whether they are permanent Fizzle-original features or planned to align with Apothic.

| Area | Apothic Behavior | Fizzle Current | Planned Direction |
|---|---|---|---|
| **Stability model** | Binary `stable` flag from Geode Shelf | Rectification float 0–100 from 3-tier Rectifier shelves | **Keep Fizzle-original** — richer tuning surface; graduated stability is a QoL improvement |
| **Enchantment source** | 19 custom enchantments (Java-coded) | 51 NeoEnchant+ ports (100% JSON) | **Keep Fizzle-original** — larger, more diverse roster; fully datapack-editable |
| **Eterna scale** | maxEterna=100 | Code defaults to maxEterna=100 but design target is 50 (shelf values already at half-scale) | **Align with Apothic** — change config default to 50 to match shelf stat scale |
| **Eterna accumulation** | Step-ladder algorithm (blocks sorted by maxEterna ascending, each group gated) | Flat sum clamped to highest maxEterna seen | **Align with Apothic** — low-tier shelves are less strictly gated currently |
| **Library extraction** | Direct item application (any item in extract slot) | Book-only (Zenith pattern) | **Align with Apothic** — streamlines UX, cuts the anvil step |
| **Scrap Tome** | Removes ~half enchantments randomly | Removes 1 random enchantment | **Align with Apothic** — current behavior makes Scrap Tome too weak |
| **Arcana guaranteed picks** | 3 max (at arcana 0, 33, 66) | 4 picks (adds a 4th at arcana 99) | **Align with Apothic** — remove the 4th pick at 99 |
| **Anvil repair** | XP cost only | Iron block + 1 level (Zenith) | **Keep Fizzle-original** — iron block cost adds a material sink |
| **Typed tomes** | 9 slot-filtered tomes | Cut entirely | **Keep cut** — UX felt counter-intuitive |
| **KeepNBT scope** | Full NBT preservation | Copies `ENCHANTMENTS` component only | **Keep Fizzle-original** — correct for 1.21.1 component model |
| **Filtering shelf stats** | Dynamic: +0.5 eterna / +1 arcana per book stored | Flat stats from JSON regardless of occupancy | **Align with Apothic** — per-book scaling is more interesting gameplay |
| **Sculk ambient sounds** | `randomTick()` plays sounds at configurable intervals | Config fields exist but `randomTick()` not wired | **Align with Apothic** — wire `randomTick()` to config fields |
| **GUI tooltips** | Rich descriptive text, `drawOnLeft` side panels, power range, enchantability | Terse stat bar tooltips, no side panels | **Align with Apothic** — full tooltip suite planned |
| **Item tooltips** | `appendHoverText()` on all items | No tooltips on standalone items | **Align with Apothic** — add tooltips to tomes, prismatic web, infused breath, warden tendril, music discs |
| **Mixin strategy** | 15+ mixins + 3 ASM coremods | 6 mixins + 0 ASM | **Keep Fizzle-original** — Fabric events + data-driven enchantments cover the difference |

### Fizzle-Original Features (Not in Apothic)

- **Rectifier shelves (T1/T2/T3)** — three-tier rectification progression (10/15/25)
- **Rectification as a stat axis** — fifth tracked stat alongside Eterna/Quanta/Arcana/Clues
- **49 NeoEnchant+ enchantments** — mounted combat, mace, elytra, bow elemental, hoe/farming, size modification
- **Non-shelf stat providers** — Amethyst Cluster (+1.5 rectification), Skulls (+5/+10 quanta)
- **Triple recipe viewer support** — EMI + REI + JEI simultaneously
- **Dual block-info support** — Jade + WTHIT with shared tooltip formatter
- **Music discs** — 3 custom discs (Eterna, Quanta, Arcana) with infusion recipes

## MVP Phased Delivery

> **Status: All phases complete.** The mod is at ~90% Apothic parity across all areas. Remaining work tracked in the gap assessment (`Apothic-Fizzle-Enchanting-Comparrison.md`).

1. **Phase 1 — Scaffolding:** ✅ Gradle, `fabric.mod.json`, empty initializer, mixin config, datagen skeleton.
2. **Phase 2 — Stat system & table:** ✅ `IEnchantingStatProvider`, datapack loader, menu mixin, 3 S2C payloads.
3. **Phase 3 — Shelf family:** ✅ 27 shelf blocks + 31 JSON stat entries + 4 particle types.
4. **Phase 4 — Anvil & Library:** ✅ `AnvilDispatcher` with 6 handlers + 2-tier library with GUI and hopper automation.
5. **Phase 5 — Tomes:** ✅ 3 tome items (Scrap, Improved Scrap, Extraction) wired through anvil mixin.
6. **Phase 6 — Enchantments:** ✅ 51 enchantments (49 NeoEnchant+ + 2 Zenith-inspired), all pure JSON.
7. **Phase 7 — Integrations:** ✅ EMI, REI, JEI, Jade, WTHIT, Trinkets, ModMenu (7 integrations, expanded from original 4).
8. **Phase 8 — Polish:** ✅ 18 advancements, per-enchantment config system, `/fizzleenchanting reload`, 61 JUnit + 31 gametests.

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
| **BeyondEnchant** | 🔨 Iteration 1 (partially complete) | Java infra done (per-enchant config + mixin + sync). Needs 16 datapack override JSONs + config wiring. |
| **NeoEnchant+** | ✅ Absorbed into MVP | 49 of NeoEnchant+'s 56 JSONs ship directly in the MVP enchantment roster (7 cut, see "MVP Enchantments" above). Pulled forward from Iteration 2 because the port is pure JSON copy — no reason to defer. |
| **Enchanting Infuser** | ✅ Superseded by MVP | Library (deterministic dispense) + stat-driven table (discovery feed) cover the pick-your-enchant loop. Bundled foreign-enchant overrides handle Mending / Soulbound. |

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

> **Status: Partially complete.** The Java infrastructure is done — `EnchantmentMixin.getMaxLevel()` reads from `EnchantmentInfoRegistry`, per-enchantment `maxLevel`/`maxLootLevel`/`levelCap` config is synced via `EnchantmentInfoPayload`, and `overLeveledColor` rendering works. What remains is shipping the 16 datapack override JSONs and wiring the config entries.

[BeyondEnchant](https://www.curseforge.com/minecraft/mc-mods/beyondenchant) (CF 1135664, by Hardel) raises `max_level` on ~16 vanilla enchantments via `data/minecraft/enchantment/*.json` overrides.

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

## Superseded — Enchanting Infuser

[Enchanting Infuser](https://www.curseforge.com/minecraft/mc-mods/enchanting-infuser) (CF 551151, Fuzs) adds a separate block for deterministic enchantment selection — pay XP, pick the enchant. The MVP's stat-driven table + Enchantment Library cover the same player need as a two-step loop:

- **Discovery** → enchant items at the stat-driven table. Every enchant in the pack eventually rolls (foreign enchants included, via the bundled overrides above). Unwanted rolls get scrapped into books that flow into the library pool.
- **Deterministic dispense** → withdraw a specific enchantment at a specific level from the library for `points(level)` points + XP, apply to the target item at a vanilla anvil.

With a stocked library the loop is "spend XP + lapis at the table to keep feeding the pool; spend XP + points at the library to pull exactly what you want." The infuser compresses that to one step, but the two-step version reaches the same endpoint and rewards long-term play — the more you enchant, the richer your library. No new block type, no second enchanting UX, no parallel XP economy.

**The acquire-from-nothing case** (Mending, `yigd:soulbound`) is handled upstream by the foreign-enchantment overrides in the MVP. Once those enchants roll at the table like any other, the library pool fills naturally and the infuser's `pay XP → get a specific enchant from zero` is no longer needed.

**Acceptance:**
- [ ] Once MVP ships, confirm Mending + `yigd:soulbound` appear at tables (Mending gated by `treasure_shelf`).
- [ ] Confirm library pool accepts and dispenses both.
- [ ] Remove the `EnchantingInfuser-Mending-Soulbound.zip` Paxi pack — the jar's bundled overrides replace it.
- [ ] Update `docs/guides/enchanting-guide.md` — replace the "Enchanting Infuser" section with the "Table → Library" workflow.
- [ ] Remove Enchanting Infuser from `plugins/gameplay.md` and `modpack/mods/`.
- [ ] Drop Enchanting Infuser rows from `docs/compatibility-matrix.md`.

## Iteration 2 — Absorb Easy Magic

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
