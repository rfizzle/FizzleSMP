# Meridian Enchantment Design Spec

> **Status: IMPLEMENTED.** All 75 enchantments have been implemented, tested, and
> documented. This file is retained as the design reference. The live
> documentation is `docs/ENCHANTMENTS.md`.

The complete roster of Meridian enchantments. This document was the **only** input
to the implementation phase — all names, IDs, and concepts are Meridian originals.

## Ground rules

1. **Concepts are ideas.** An enchantment concept (e.g., "drains health on hit")
   is a game mechanic, not protectable expression. What matters is that our
   registry IDs, display names, weights, rarities, level ranges, costs, effect
   JSON, and description text are all our own.
2. **This spec is the source of truth.** The implementation phase works from THIS
   file only.
3. **Parity is a non-goal.** We want a rich, balanced roster. Where our design
   diverges from how any other mod tuned a similar idea, that is expected.
4. **Convergence allowance.** Where Minecraft's `EnchantmentEffectComponents`
   admit only one shape for a trivial effect (e.g., a flat attribute bump),
   structural similarity to any mod is unavoidable (merger doctrine) — but our
   weights, levels, costs, and text must still be our own.

## Definition of done

- Every enchantment has a Meridian ID, name, level range, full cost block, and
  effect JSON authored from this spec.
- No description/`fallback` string reuses another mod's wording.
- `docs/ENCHANTMENTS.md`, `README.md`, and `LICENSE` state the accurate origin:
  *all data original to Meridian*. (Deferred until implementation lands.)

---

## Rarity tiers

Weight controls how likely an enchantment is to appear at the enchanting table
and in loot. Lower weight = rarer. Follows vanilla conventions.

| Weight | Tier | Vanilla examples |
|---|---|---|
| 10 | Common | Protection, Sharpness, Efficiency |
| 5 | Uncommon | Fire Protection, Smite, Flame |
| 2 | Rare | Thorns, Frost Walker, Silk Touch |
| 1 | Very rare | Soul Speed, Swift Sneak |

**Treasure** enchantments (`T` in the Wt column) never appear on the enchanting
table. They are found only in loot chests, villager trades, and fishing. Vanilla
examples: Mending, Frost Walker, Curse of Binding.

---

## Design tables

`Lvl` is a **starting suggestion**, not a value to preserve. `Concept` is
idea-level only; the implementer chooses the actual effect components,
magnitudes, and curves. `Wt` is the rarity weight; `T` suffix marks treasure.

### Sword / Weapon

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `tempo` | Tempo | 5 | 1–2 | Faster attack-speed attribute on the weapon. |
| `voidbane` | Voidbane | 5 | 3–5 | Extra damage to Ender-type mobs (endermen, shulkers, etc.). |
| `keen_edge` | Keen Edge | 2 | 3–4 | Chance on hit to deal bonus/true damage. |
| `nightfall` | Nightfall | 2 | 1 | Chance to inflict Darkness on the target. |
| `rift_strike` | Rift Strike | 2 | 3–5 | More damage when not in the Overworld. |
| `sanctify` | Sanctify | 5 | 3–5 | Extra damage to Nether-type mobs (piglins, blazes, wither skeletons, etc.). |
| `quell` | Quell | 5 | 1 | Hitting a creeper delays/resets its fuse. |
| `final_gambit` | Final Gambit | 1T | 1 | One-shot: destroy the item for a huge damage burst. |
| `siphon` | Siphon | 2 | 2–3 | Chance on hit to heal the attacker. |
| `shackle` | Shackle | 5 | 1–3 | Inflicts slowness on the target on hit. |
| `blight` | Blight | 5 | 2–3 | Chance to poison the target on hit. |
| `decay` | Decay | 2 | 2–3 | Chance to inflict Wither on the target on hit. |
| `snare` | Snare | 1T | 1 | Chance for killed mobs to drop their spawn egg. |
| `outreach` | Outreach | 5 | 2–3 | Extends melee attack range attribute. |
| `soul_tax` | Soul Tax | 2 | 2–3 | Spend stored XP to boost hit damage. |
| `sentinel` | Sentinel | 5 | 3–5 | Extra damage to Illagers (vindicators, evokers, pillagers, etc.). |
| `insight` | Insight | 10 | 2–3 | More XP from mob kills. |

### Axe

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `cleave` | Cleave | 2 | 1–3 | Attacks hit multiple enemies in an arc (area melee). |

### Bow / Ranged

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `true_flight` | True Flight | 2 | 1 | Arrows ignore gravity (flat trajectory). |
| `gale_shot` | Gale Shot | 5 | 1–2 | Impact emits a wind-charge-like knockback/ground effect. |
| `resonance` | Resonance | 2 | 1–2 | Impact creates a sonic-boom AoE. |
| `permafrost` | Permafrost | 5 | 1 | Impact freezes nearby blocks and slows targets. |
| `detonation` | Detonation | 1 | 2–4 | Arrows explode on impact (scaling radius). |
| `ricochet` | Ricochet | 2 | 2–3 | Arrows ricochet off surfaces. |
| `stormcall` | Stormcall | 2 | 1 | Impact summons a lightning strike. |

### Trident / Mace

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `glacial_lance` | Glacial Lance | 2 | 2–3 | Thrown trident freezes water and slows on hit. |
| `tempest` | Tempest | 2 | 1–2 | During thunderstorms, mace strikes call lightning + grant brief immunity. |
| `seismic_slam` | Seismic Slam | 2 | 1 | Crouch + ground slam emits a shockwave. |
| `updraft` | Updraft | 5 | 2–3 | Ground-slam impact launches the wielder upward. |

### Helmet

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `luminance` | Luminance | 5 | 1 | Passive night vision while worn. |
| `abyss_ward` | Abyss Ward | 1T | 1 | Falling into the void grants brief levitation to escape once. |
| `premonition` | Premonition | 2 | 1 | Nearby hostile mobs gain Glowing, visible through walls. |

### Chestplate

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `masons_reach` | Mason's Reach | 5 | 2–3 | Extends block reach (place/break distance). |
| `repulse` | Repulse | 2 | 1–3 | Melee attackers are knocked away when striking the wearer. |
| `frostguard` | Frostguard | 2 | 2–3 | Attackers who hit the wearer are slowed. |
| `rally` | Rally | 1T | 1–2 | Dropping below 20% health triggers brief Regeneration (long cooldown). |

### Armor (any slot)

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `animus` | Animus | 5 | 1–3 | Increases XP gained from mob kills and block mining. |
| `bloodrage` | Bloodrage | 1 | 1–3 | Taking damage grants temporary Resistance + Strength + Speed at a health cost. |
| `reckless` | Reckless | 1 | 2–3 | Trades armor for armor-penetration / damage. |
| `bulwark` | Bulwark | 10 | 1–3 | Adds knockback resistance attribute. |
| `vitality` | Vitality | 5 | 3–5 | Adds max-health attribute (extra hearts). |
| `spellguard` | Spellguard | 5 | 2–4 | Reduces incoming magic damage. |
| `antidote` | Antidote | 5 | 1 | Reduces duration/severity of harmful potion effects. |
| `gravitas` | Gravitas | 5 | 1–3 | Increases item pickup radius. |

### Leggings / Boots

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `diminish` | Diminish | 1 | 3–5 | Shrinks the player; slower but auto step-up. |
| `slipstream` | Slipstream | 2 | 1 | Dolphin's Grace while worn. |
| `vault` | Vault | 5 | 2–3 | Higher jump (jump-strength attribute). |
| `colossus` | Colossus | 1 | 2–4 | Enlarges the player; tougher but shorter reach/slower. |
| `alacrity` | Alacrity | 10 | 3–5 | Movement-speed attribute boost. |
| `cinderwalk` | Cinderwalk | 2T | 2–3 | Solidifies lava under the wearer (Frost Walker analog). |
| `steadfast` | Steadfast | 2 | 1 | Negates the mining-speed penalty while airborne/flying. |
| `clamber` | Clamber | 10 | 1–3 | Auto step up full blocks. |

### Elytra

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `ironwing` | Ironwing | 5 | 2–4 | Damage reduction while gliding. |
| `impact_ward` | Impact Ward | 5 | 3–5 | Reduces/negates elytra kinetic (wall) damage. |

### Tools / Hoe

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `excavate` | Excavate | 2 | 1 | 3×3 area mining. |
| `prospect` | Prospect | 2 | 1 | Breaks a connected ore vein in one go. |
| `bounty` | Bounty | 5 | 2–3 | Replants/area-harvests crops. |
| `furrow` | Furrow | 5 | 2–3 | Area-tills soil, scaling with level. |
| `beckon` | Beckon | 10 | 1 | Farm animals follow the wielder while the tool is held. |
| `terrasculpt` | Terrasculpt | 2 | 1 | Walking converts blocks underfoot to natural terrain. |

### Mounted

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `gallop` | Gallop | 5 | 2–4 | Mount moves faster. |
| `trample` | Trample | 5 | 2–3 | Mount deals extra damage. |
| `skybound` | Skybound | 2 | 3–7 | Higher mount jump + reduced fall damage. |
| `saddleguard` | Saddleguard | 5 | 3–5 | Less damage taken while mounted. |

### Shield

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `retribution` | Retribution | 1 | 3–5 | Chance to reflect blocked damage back to the attacker. |
| `pummel` | Pummel | 2 | 2–4 | Shield attacks deal bonus damage and cost durability. |
| `fortify` | Fortify | 5 | 1–3 | Blocking absorbs more damage and costs less durability. |

### Shears

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `prismatic` | Prismatic | 5 | 1 | Sheared wool drops as a random color. |
| `renewal` | Renewal | 2 | 1 | Chance for sheep to instantly regrow wool after shearing. |

### Durability (any item)

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `vital_mend` | Vital Mend | 1T | 1–3 | Incoming healing repairs item durability instead of restoring health. |
| `plunder` | Plunder | 1 | 1–3 | Chance for killed mobs to drop their loot a second time. |
| `tether` | Tether | 1T | 1 | Item is kept in inventory on death instead of dropping. |

### Curses / Misc

| ID | Name | Wt | Lvl | Concept (idea only) |
|---|---|---|---|---|
| `curse_of_decay` | Curse of Decay | 2T | 3–5 | Item loses durability faster. |
| `curse_of_sealing` | Curse of Sealing | 1T | 1 | Item cannot be (re)enchanted or modified. |
| `aurify` | Aurify | 1T | 1 | Right-click chance to convert a block to gold/gold ore. |

---

## Treasure enchantments

These never appear at the enchanting table — found only in loot chests, villager
trades, and fishing.

| Enchant | Rationale |
|---|---|
| Final Gambit | World-ending single hit should be a dramatic find, not rollable. |
| Snare | Spawn eggs are economy-warping; gate behind loot rarity. |
| Abyss Ward | Void insurance is too strong for routine enchanting. |
| Rally | Emergency regen is a powerful safety net; treasure keeps it rare. |
| Cinderwalk | Mirrors vanilla Frost Walker's treasure status. |
| Vital Mend | Mirrors vanilla Mending's treasure status. |
| Tether | Keep-on-death is the strongest durability effect; treasure-only. |
| Curse of Decay | Follows vanilla convention — curses are treasure-only. |
| Curse of Sealing | Follows vanilla convention — curses are treasure-only. |
| Aurify | Gold conversion has economy impact; should be a rare discovery. |

---

## Item applicability

Most enchantments apply to the item type implied by their section header. The
table below lists only enchantments where **primary items** (enchanting table)
differ from **supported items** (anvil/loot), or where the slot is not obvious
from the section header.

In Minecraft's system: `primary_items` controls what the enchanting table offers;
`supported_items` controls what can receive the enchantment via anvil. When only
`supported_items` is set, it serves both roles.

| Enchant | Primary (table) | Supported (anvil) | Notes |
|---|---|---|---|
| Tempo | sword | weapon | Speed benefits all melee weapons. |
| Quell | sword | weapon | Creeper safety on any melee weapon. |
| Siphon | sword | weapon | Life steal works with any melee. |
| Shackle | sword | weapon | On-hit slow applies to all melee. |
| Blight | sword | weapon | Poison aspect, like Fire Aspect — sword primary, anvil onto axes. |
| Decay | sword | weapon | Wither aspect, same rationale as Blight. |
| Nightfall | sword | weapon | Darkness on hit, same rationale. |
| Outreach | sword | weapon | Reach benefits all melee. |
| Insight | sword | weapon | XP gain from any weapon kill. |
| Gale Shot | bow | bow, crossbow | Impact effect works with any projectile. |
| Resonance | bow | bow, crossbow | Impact effect works with any projectile. |
| Permafrost | bow | bow, crossbow | Impact effect works with any projectile. |
| Detonation | bow | bow, crossbow | Impact effect works with any projectile. |
| Ricochet | bow | bow, crossbow | Trajectory mod works with any projectile. |
| Stormcall | bow | bow, crossbow | Impact effect works with any projectile. |
| True Flight | bow | bow, crossbow | Trajectory mod works with any projectile. |
| Repulse | chestplate | chestplate, leggings | Knockback-on-hit works on either torso slot. |
| Frostguard | chestplate | armor | Slow-on-hit can work from any armor slot. |
| Bloodrage | chestplate | armor | Table-rolls on chest only; anvil onto any slot. |
| Excavate | pickaxe | pickaxe, shovel | 3×3 works for digging too. |
| Plunder | sword | weapon | Kill-triggered loot; weapon slot despite Durability section. |

All other enchantments: `supported_items` = `primary_items` = the item type in
the section header (e.g., Helmet enchants apply to helmets only, Mounted
enchants apply to horse armor, etc.).

---

## Exclusive sets

Enchantments that share an exclusive set cannot coexist on the same item.
Implementation: each group maps to an `exclusive_set` tag in the enchantment
JSON. Where a Meridian set extends a vanilla set, Meridian enchantments are
appended to the vanilla tag so they're mutually exclusive with vanilla members
too.

### `minecraft:exclusive_set/damage`

**Meridian members:** Voidbane, Sanctify, Sentinel, Rift Strike, Keen Edge

Extends vanilla (Sharpness, Smite, Bane of Arthropods, Impaling). Only one
damage-scaling strategy per weapon — no stacking flat damage + mob-type bonus +
dimensional bonus + crit.

### `meridian:exclusive_set/aspect`

**Members:** Blight, Decay, Shackle, Nightfall + vanilla Fire Aspect

One on-hit status effect per weapon. Without this, a single sword could apply
fire + poison + wither + slowness + darkness on every swing.

### `meridian:exclusive_set/arrow_impact`

**Members:** Gale Shot, Resonance, Permafrost, Detonation, Stormcall

One special impact effect per bow/crossbow. Prevents arrows that simultaneously
explode, summon lightning, freeze, and sonic-boom.

Note: True Flight and Ricochet are trajectory modifiers, not impact effects —
they are intentionally outside this set and can pair with any impact enchant.

### `minecraft:exclusive_set/mace`

**Meridian members:** Tempest, Seismic Slam, Updraft

Extends vanilla (Density, Breach, Wind Burst). One slam effect per mace.

### `minecraft:exclusive_set/armor`

**Meridian members:** Spellguard

Extends vanilla (Protection, Blast Protection, Fire Protection, Projectile
Protection). One protection type per armor piece — vanilla convention.

Note: Antidote reduces potion effect duration, not damage — it's a different
system and intentionally outside this set.

### `minecraft:exclusive_set/boots`

**Meridian members:** Cinderwalk

Extends vanilla (Frost Walker, Depth Strider). One liquid-traversal method per
boot.

### `meridian:exclusive_set/size`

**Members:** Diminish, Colossus

Can't be big and small at the same time.

### `meridian:exclusive_set/mining`

**Members:** Excavate, Prospect

Area mining (3×3) and vein mining are conflicting break patterns — only one can
drive block selection per swing.

### `meridian:exclusive_set/glass_cannon`

**Members:** Bloodrage, Reckless

Both trade survivability for offense. Stacking both would leave the player too
fragile to function — one glass-cannon tradeoff per armor piece.

### `meridian:exclusive_set/mending`

**Members:** Vital Mend + vanilla Mending

One repair source per item. Both convert a resource (health vs. XP) into
durability; stacking both would make items effectively indestructible.

### Intentionally unrestricted

The following have no exclusive-set conflicts beyond their slot constraints:

- **Sword utility:** Tempo, Final Gambit, Siphon, Snare, Outreach, Soul Tax,
  Insight — all operate on different axes (speed, sacrifice, healing, drops,
  range, XP economy, XP gain).
- **Axe:** Cleave — sole axe enchant.
- **Bow trajectory:** True Flight, Ricochet — modify flight path, compatible
  with any impact enchant.
- **Trident:** Glacial Lance — unique slot, no overlap with Channeling (one is
  always-on, the other is storm-gated).
- **Helmet:** Luminance, Abyss Ward, Premonition — vision, void safety, mob
  detection are independent utilities.
- **Chestplate:** Mason's Reach, Repulse, Frostguard, Rally — reach, knockback,
  slow, and emergency regen are distinct triggers.
- **Armor (general):** Animus, Bulwark, Vitality, Antidote, Gravitas — XP gain,
  knockback resist, max HP, potion resist, and item pickup don't overlap.
- **Leggings/Boots (movement):** Alacrity, Slipstream, Vault, Clamber,
  Steadfast — speed, swim, jump, step-up, and fly-mining are complementary.
  Diminish/Colossus are gated by the size set above.
- **Elytra:** Ironwing, Impact Ward — general damage reduction and collision
  damage reduction serve different scenarios.
- **Hoe:** Bounty, Furrow, Beckon, Terrasculpt — harvest, till, lure, and
  terrain conversion are different actions on different targets.
- **Mounted:** Gallop, Trample, Skybound, Saddleguard — speed, damage, jump,
  and defense are orthogonal mount stats.
- **Shield:** Retribution, Pummel, Fortify — reflect, bash, and block
  efficiency are different shield interactions.
- **Shears:** Prismatic, Renewal — color and regrowth are independent.
- **Durability:** Plunder, Tether — double loot and keep-on-death are different
  purposes. (Vital Mend is gated by the mending set above.)
- **Curses/Misc:** Curse of Decay, Curse of Sealing, Aurify — curses stack
  freely (they're penalties); Aurify is standalone.

---

## Config: per-enchantment disable

The existing `enchantmentOverrides` system in `MeridianConfig` supports level
capping but has no way to fully remove an enchantment. Add an `enabled` boolean
(default `true`) to `EnchantmentOverride`. When `false`:

- The enchantment does not appear in the enchanting table.
- The enchantment does not roll in loot tables or villager trades.
- Existing items keep their enchantment data (no data loss), but the enchantment
  has no effect while disabled.
- The enchantment is hidden from tooltip display.

This lets server operators pick exactly which enchantments exist in their world.
Config example:

```json
"enchantmentOverrides": {
  "meridian:snare": { "enabled": false },
  "meridian:aurify": { "enabled": false }
}
```

---

## Client UI integration

The enchantment overhaul touches several existing client-facing systems. All
changes below must be handled as part of the implementation.

### Lang keys

Every enchantment needs **two** lang keys in `en_us.json`:

- `enchantment.meridian.<id>` — display name (e.g., "Siphon")
- `enchantment.meridian.<id>.desc` — inline description for the tooltip handler
  (e.g., "Chance on hit to heal the attacker")

The inline description is shown when `display.enableInlineEnchDescs` is true
(`InlineEnchDescTooltipHandler`). Missing `.desc` keys will silently omit the
line — but every enchantment should have one.

### EnchantmentInfoPayload (S2C sync)

`EnchantmentInfoPayload` syncs per-enchantment config from server to client on
join and after `/meridian reload`. The new `enabled` field must be included in
this payload so the client knows which enchantments are active. Bump the payload
version or add a new field to the serialization format.

### Enchanting Info Screen

`EnchantingInfoScreen` displays a browsable, filterable list of enchantments with
power ranges and slot tabs. When an enchantment is disabled via config:

- It must not appear in the enchantment list.
- Slot tab counts must reflect only enabled enchantments.

### Enchantment Library Screen

`EnchantmentLibraryScreen` displays stored enchantments with extraction controls.
When an enchantment is disabled:

- Already-stored entries remain visible (data is not deleted).
- Extraction is blocked — the button is greyed out with a tooltip explaining the
  enchantment is disabled by the server.
- New deposits of disabled enchantments are blocked.

### Tooltip handling

`OverLeveledTooltipHandler` and `InlineEnchDescTooltipHandler` process enchantment
lines in item tooltips. When an enchantment is disabled:

- The enchantment line is hidden from the tooltip entirely (matches the
  "hidden from tooltip display" rule in the config section above).
- Items that have only disabled enchantments show no enchantment glint.

### ModMenu config screen

The `ModMenuIntegration` Cloth Config screen does not currently expose
`enchantmentOverrides` (it's JSON-only). This is acceptable — the override map
is dynamic and per-enchantment toggles would bloat the screen. Document the
`enabled` field in `CONFIG.md` with examples and leave the config screen as-is.

---

## Workflow

1. Wipe all existing enchantment data (`data/meridian/enchantment/`), tags, and
   lang keys.
2. Implement each enchantment as fresh JSON from THIS spec — own weights, cost
   blocks, effect components, description text.
3. Add `enabled` field to `EnchantmentOverride` and wire up the disable logic
   in `EnchantmentInfoRegistry`, the enchanting table mixin, and loot injection.
4. Update lang file with all new IDs and Meridian-original description strings.
5. Write a test for each enchantment — verify the effect fires, respects its
   exclusive set, honors the `enabled` config flag, and degrades gracefully
   when the enchantment is disabled on an existing item.
6. Final doc pass: rewrite `ENCHANTMENTS.md`, `README.md`, `LICENSE` to state
   accurate origin.
