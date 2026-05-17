---
name: mc-enchantments
description: Create and implement Minecraft 1.21.1 enchantments for Fabric mods — both data-driven JSON definitions and custom Java logic. TRIGGER when creating, editing, or debugging enchantment JSON files, enchantment effect handlers, or enchantment-related mixins in a 1.21.1 Fabric mod.
---

Implementing enchantments for a Minecraft 1.21.1 Fabric mod.

## Key Concepts

- Enchantments are **fully data-driven** in 1.21.1 — JSON at `data/<namespace>/enchantment/<id>.json`
- There is NO Java `Enchantment` subclass. Behavior is defined via effect components in JSON.
- Custom behavior beyond JSON requires **event handlers** or **mixins** that read enchantment levels at runtime via `ResourceKey<Enchantment>`
- Even custom-logic enchantments still need a JSON definition (for table eligibility, cost, weight, exclusive set)

## JSON Fields

**Required:** `description`, `supported_items`, `weight`, `max_level`, `min_cost`, `max_cost`, `anvil_cost`, `slots`

**Optional:** `primary_items` (table-only subset), `exclusive_set` (tag ref), `effects`

**Treasure-only:** Omit `primary_items` entirely — only `supported_items` is set. The enchanting table won't offer it.

## Slots

`"mainhand"`, `"offhand"`, `"head"`, `"chest"`, `"legs"`, `"feet"`, `"armor"` (all 4), `"any"`, `"body"` (horse/wolf armor)

## Effect Components

| Component | Purpose | Key fields |
|-----------|---------|-----------|
| `minecraft:attributes` | Passive attribute modifiers | `amount`, `attribute`, `id`, `operation` |
| `minecraft:damage` | Bonus attack damage (conditional) | `effect.type: add`, `effect.value`, `requirements` |
| `minecraft:post_attack` | After-hit effects (status, ignite, explode) | `enchanted`, `affected`, `effect`, `requirements` |
| `minecraft:damage_protection` | Reduce incoming damage | `effect.value`, `requirements` (damage source filter) |
| `minecraft:damage_immunity` | Block damage entirely | `requirements` only |
| `minecraft:item_damage` | Modify durability loss | `remove_binomial` (reduce) or `add` (increase for curses) |
| `minecraft:mob_experience` | XP multiplier from kills | `effect.value` |
| `minecraft:block_experience` | XP multiplier from mining | `effect.value` |
| `minecraft:location_changed` | On-move trigger (Frost Walker) | `effect`, `requirements` |
| `minecraft:tick` | Every-tick while equipped | particles, sounds |
| `minecraft:equipment_drops` | Mob equipment drop chance | `effect.value` |

## Level Providers

| Type | Formula |
|------|---------|
| `minecraft:linear` | `base + (level-1) * per_level_above_first` |
| `minecraft:clamped` | `clamp(inner_value, min, max)` |
| `minecraft:fraction` | `numerator / denominator` |
| `minecraft:lookup` | `values[level-1]`, fallback if OOB |
| bare number | constant |

## Common Loot Conditions

- **Entity type:** `entity_properties` with `predicate.type: "#tag"`
- **Direct melee:** `damage_source_properties` with `is_direct: true`
- **Damage tags:** `damage_source_properties` with `tags: [{expected, id}]`
- **Random chance:** `random_chance` with `chance` (float or level provider)
- **Combinators:** `all_of`, `any_of`, `inverted`

## Attributes Reference (1.21.1)

`generic.attack_speed`, `generic.attack_damage`, `generic.knockback_resistance`, `generic.max_health`, `generic.movement_speed`, `generic.jump_strength`, `generic.scale`, `generic.step_height`, `generic.block_interaction_range`, `player.entity_interaction_range`, `generic.armor`, `generic.movement_efficiency`, `player.block_break_speed`

## Item Tags (vanilla enchantable)

`sword`, `weapon` (sword+axe+mace+trident), `armor`, `head_armor`, `chest_armor`, `leg_armor`, `foot_armor`, `mining`, `durability`, `bow`, `crossbow`, `trident`, `mace`

## Cost Guidelines

- Common (wt 10): min base 1–5, per_level 8–11, anvil_cost 1
- Uncommon (wt 5): min base 10–20, per_level 10–20, anvil_cost 2–4
- Rare (wt 2): min base 20–40, per_level 15–25, anvil_cost 4
- Very rare (wt 1): min base 40+, per_level 20+, anvil_cost 8
- Treasure: cost irrelevant (never appears at table)

## Exclusive Sets

Tag files at `data/<namespace>/tags/enchantment/exclusive_set/<name>.json`. To extend a vanilla set, put your entries in `data/minecraft/tags/enchantment/exclusive_set/<name>.json`.

## Custom Java Pattern

1. Define `ResourceKey<Enchantment>` constant via `ResourceKey.create(Registries.ENCHANTMENT, id)`
2. Read level from stack: iterate `stack.getEnchantments().entrySet()`, match with `entry.getKey().is(key)`
3. Hook via Fabric events or mixins:
   - `ServerLivingEntityEvents.AFTER_DAMAGE` — on-hit, on-take-damage
   - `ServerLivingEntityEvents.AFTER_DEATH` — on-kill loot
   - `PlayerBlockBreakEvents.AFTER` — area mining, vein mining
   - Mixins for: tick handlers, creeper fuse, shearing, tempt goals, healing interception

## Performance Rules

- Tick handlers: modulo check or periodic_tick, never every tick
- Area break effects: recursion guard flag, iterative BFS, cap vein size
- Entity scanning: cache, limit radius, scheduled intervals
- Attribute modifiers from JSON are free — the game manages them automatically

## Lang Keys

Two per enchantment in `en_us.json`:
- `enchantment.<namespace>.<id>` — display name
- `enchantment.<namespace>.<id>.desc` — inline tooltip description
