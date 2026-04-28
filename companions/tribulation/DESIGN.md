# Tribulation — Mob Scaling Design

## Overview

Tribulation is a companion Fabric mod that replaces both **HMIOT** (Hostile Mobs Improve Over Time) and **RpgDifficulty** with a unified, formula-driven mob scaling system. All three scaling axes — time, distance, and height — live in one mod with one config file. Mob stats are computed dynamically from formulas, not hardcoded tiers.

## Goals

1. **Longer progression** — 250 levels at 1 hour per level = 250 hours to max
2. **Harder endgame** — level 250 exceeds HMIOT's level 100 ceiling
3. **Formula-based** — no hardcoded per-level data; one formula per attribute
4. **Fully configurable** — JSON config file, no scoreboard hacks
5. **Replaces RpgDifficulty** — absorbs distance and height scaling into the same mod
6. **Special zombies** — big and speed zombie variants carried over from RpgDifficulty
7. **Boss scaling** — bosses scale with distance, configurable separately

## Scaling Application

### When Scaling Is Applied

Scaling is applied **once, at spawn time**, when the entity enters a `ServerWorld` via `ServerEntityEvents.ENTITY_LOAD`. The mob's attributes are computed from its position (distance, height) and the nearest player's difficulty level (time), then locked in via attribute modifiers. The mob is never re-processed.

> **Design rationale:** HMIOT uses proximity-triggered scaling (mob is buffed when a player first walks within 32 blocks), while RpgDifficulty hooks `spawnEntity()` at spawn time. We use spawn-time application because:
> - It is simpler — no tracking of "already processed" tags or periodic scanning.
> - Attribute modifiers persist in NBT automatically via `addPersistentModifier()`, surviving chunk unload/reload without re-application.
> - It avoids the HMIOT footgun where AFK farms work only because the player is >32 blocks away.

### Player Level Resolution (Multiplayer)

When a mob spawns, the **nearest player** within `mobDetectionRange` (default: 32 blocks) determines the time axis level. If no player is within range, the time factor is 0 (mob gets only distance + height scaling).

> **Design rationale:** HMIOT uses `@p` (nearest player) at the moment of first contact. RpgDifficulty does not use player level at all (it uses world time). We follow HMIOT's nearest-player model but apply it at spawn time instead of proximity time.
>
> **SMP implication:** A high-level player's base will have tougher mobs spawning around it. A low-level player visiting that base encounters those pre-scaled mobs. This is intentional — it rewards building in safe (close to spawn) areas and makes frontier bases feel dangerous. The mob's level is frozen at spawn; a high-level player walking through a low-level area does NOT retroactively buff already-spawned mobs.

### Attribute Modifier Strategy

We use Fabric's `EntityAttributeModifier` system (not `setBaseValue()` like RpgDifficulty). This is safer because:
- Modifiers are namespaced (`tribulation:*`) and can be identified, removed, or debugged.
- They don't overwrite the entity's base value, so other mods can still read vanilla base stats.
- `addPersistentModifier()` survives save/load natively.
- We always `removeModifier()` before `addPersistentModifier()` to prevent stacking on re-application.

## Scaling Axes

Three independent axes contribute to a mob's final difficulty. Each axis computes a **factor** (a multiplier added to base stats). The factors from all axes are summed before applying to attributes.

| Axis | What it measures | Computed from | Modifier namespace |
|------|-----------------|---------------|--------------------|
| **Time** | Player's cumulative online playtime | Per-player persistent level (0–250) | `tribulation:time_*` |
| **Distance** | Mob's distance from world spawn | Block distance at spawn time | `tribulation:distance_*` |
| **Height** | Mob's Y-level relative to sea level | Absolute Y offset from baseline | `tribulation:height_*` |

### How Axes Combine

For a given attribute, the total modifier is:

```
timeFactor   = playerLevel * attributeRate           (capped by per-attribute time cap)
distFactor   = distanceLevels * distanceFactor       (capped by maxDistanceFactor)
heightFactor = heightLevels * heightFactor           (capped by maxHeightFactor)

totalFactor = timeFactor + distFactor + heightFactor
effectiveValue = baseValue * (1 + min(totalFactor, globalAttributeCap))
```

The **time axis** has per-attribute rates and caps (e.g., health scales at 0.01/level, capped at 2.5). The **distance and height axes** apply a single uniform factor to a **subset** of attributes only:

- **Scaled by distance/height:** Max Health, Attack Damage, Armor, Armor Toughness, Spawn Reinforcements
- **Time-axis only (not affected by distance/height):** Movement Speed, Follow Range

This avoids compound frustration spikes — a deep cave mob should be tankier and hit harder, but not also faster and more perceptive. Speed and follow range scale gradually through playtime only, giving players time to adapt. The `statCaps` section provides global caps that prevent extreme stacking across all three axes.

## Time Axis — Player Difficulty Level

- Each player has a persistent difficulty level (0–250)
- Level increments by 1 every **72,000 ticks** (1 real-time hour)
- Timer is per-player and only ticks while online
- Level persists across deaths, logouts, and server restarts

## Distance Axis

Mobs further from world spawn are tougher. Carried over from RpgDifficulty.

| Setting | Default | Description |
|---------|---------|-------------|
| `startingDistance` | 1000 | Blocks from spawn before distance scaling begins |
| `increasingDistance` | 300 | Blocks per "distance level" beyond starting distance |
| `distanceFactor` | 0.1 | Stat multiplier per distance level |
| `maxDistanceFactor` | 1.5 | Cap on the distance contribution (prevents infinite scaling at world border) |
| `excludeInOtherDimensions` | true | No distance scaling in Nether/End |

### Distance Scaling Formula

Distance is calculated as **2D horizontal (XZ) distance** from world spawn, matching RpgDifficulty's `squaredDistanceTo(spawnX, mob.Y, spawnZ)` approach. Y is excluded so that height doesn't double-dip into the distance calculation.

```
distanceLevels = max(0, (horizontalDistanceFromSpawn - startingDistance) / increasingDistance)
distanceFactor = min(distanceLevels * distanceFactor, maxDistanceFactor)
```

Example: A mob at 4000 blocks from spawn → `(4000-1000)/300 * 0.1 = 1.0` factor → stats doubled (capped at 1.5).

> **Nether coordinate note:** When `excludeInOtherDimensions` is false and distance scaling is active in the Nether, the distance is measured in Nether coordinates (not multiplied by 8). A Nether base at 125 blocks from Nether spawn corresponds to ~1000 overworld blocks. This matches RpgDifficulty's behavior.

## Height Axis

Mobs at extreme Y-levels (deep caves, high peaks) are tougher. Carried over from RpgDifficulty.

| Setting | Default | Description |
|---------|---------|-------------|
| `startingHeight` | 62 | Y-level baseline (sea level) |
| `heightDistance` | 30 | Blocks above/below baseline per "height level" |
| `heightFactor` | 0.1 | Stat multiplier per height level |
| `maxHeightFactor` | 0.5 | Cap on the height contribution |
| `positiveHeightScaling` | true | Scale going up from baseline |
| `negativeHeightScaling` | true | Scale going down from baseline |
| `excludeInOtherDimensions` | true | No height scaling in Nether/End |

### Height Scaling Formula

```
heightLevels = abs(mobY - startingHeight) / heightDistance
heightFactor = min(heightLevels * heightFactor, maxHeightFactor)
```

Example: A mob at Y=2 (deep cave) → `abs(2-62)/30 * 0.1 = 0.2` factor → +20% stats.

## Attribute Scaling

All attributes use **linear scaling** with per-attribute rates and caps.

### Zombie (Reference Mob)

| Attribute | Operation | Rate / Level | Cap | Lvl 0 | Lvl 50 | Lvl 100 | Lvl 150 | Lvl 200 | Lvl 250 |
|-----------|-----------|-------------|-----|-------|--------|---------|---------|---------|---------|
| Max Health | `add_multiplied_base` | +0.01 | +2.5 (3.5x) | 20 HP | 30 HP | 40 HP | 50 HP | 60 HP | **70 HP** |
| Attack Damage | `add_multiplied_base` | +0.015 | +3.75 (4.75x) | 3 dmg | 5.25 | 7.5 | 9.75 | 12 | **14.25** |
| Movement Speed | `add_multiplied_base` | +0.0012 | +0.3 (30%) | 0.23 | 0.244 | 0.258 | 0.271 | 0.285 | **0.299** |
| Follow Range | `add_multiplied_base` | +0.01 | +1.0 (2x) | 35 | 52.5 | 70 | 70 | 70 | **70** |
| Knockback Resist | `add_multiplied_base` | +0.004 | +1.0 | 0 | 0 | 0 | 0 | 0 | **0** |
| Armor | `add_value` | +1 per 31 levels | +8 | 0 | 1 | 3 | 4 | 6 | **8** |
| Armor Toughness | `add_value` | +1 per 42 levels | +6 | 0 | 1 | 2 | 3 | 4 | **6** |
| Zombie Reinforcements | `add_multiplied_base` | +0.002 | +0.5 | base | +10% | +20% | +30% | +40% | **+50%** |

> **Note on Knockback Resistance:** Vanilla zombies have a base of 0, so `add_multiplied_base` has no effect. This is intentional — knockback resistance comes from armor, not the scaling system.

### Gameplay Feel by Level

| Level Range | Hours Played | Feel | Gear Expectation |
|-------------|-------------|------|------------------|
| 0–25 | 0–25h | Vanilla | Any |
| 25–75 | 25–75h | Noticeably tougher | Iron gear |
| 75–125 | 75–125h | Challenging | Diamond gear + enchants |
| 125–175 | 125–175h | Dangerous | Netherite recommended |
| 175–250 | 175–250h | Brutal endgame | Full netherite + good enchants, group play |

## Tier System

5 tiers gate special abilities. Tiers are assigned based on the nearest player's difficulty level.

| Tier | Level Threshold | Unlocked Abilities |
|------|----------------|--------------------|
| 1 | 50 | Basic ability upgrades |
| 2 | 100 | Mid-tier abilities |
| 3 | 150 | Advanced abilities |
| 4 | 200 | Dangerous abilities |
| 5 | 250 | Maximum threat |

### Per-Mob Abilities by Tier

| Mob | Tier 1 (50) | Tier 2 (100) | Tier 3 (150) | Tier 4 (200) | Tier 5 (250) |
|-----|-------------|-------------|--------------|--------------|--------------|
| Zombie | Reinforcement calls | — | Break doors (all wood types) | — | Sprint at target |
| Skeleton | — | Sword switch at close range | — | Flame arrows | Punch II arrows |
| Creeper | Shorter fuse | — | — | — | Charged chance |
| Spider | — | Web placing | Crop trampling | — | Leap attack |
| Cave Spider | Web placing | — | — | Poison II | — |
| Endermite | — | Teleport | — | — | Rapid teleport |
| Silverfish | Spread to blocks | — | Faster spread | — | Call reinforcements |
| Drowned | — | Trident upgrade | — | — | — |
| Husk | Extended hunger | — | — | Hunger II | — |
| Stray | — | Slowness II | — | — | — |
| Pillager | — | — | Faster crossbow | — | Multi-shot |
| Vindicator | — | — | — | Resistance I | — |
| Witch | — | Better potions | — | — | Faster throw |
| Wither Skeleton | — | — | Sprint | Fire aspect | — |
| Guardian | — | — | Longer beam | — | — |
| Hoglin | Knockback resist | — | — | — | — |
| Zoglin | — | — | Fire resistance | — | — |
| Ravager | — | — | — | Roar stun | — |
| Piglin | — | Better gear | — | — | — |
| Zombified Piglin | Easier anger | — | — | — | Group aggro range+ |
| Bogged | — | Poison arrows | — | — | — |

> **Note:** Zoglin corruption (converting blocks to nether) from HMIOT is **intentionally excluded** — too destructive for an SMP.

## Death Relief

Optional system to reduce difficulty on death, preventing frustration spirals.

| Setting | Default | Description |
|---------|---------|-------------|
| Enabled | true | Whether death reduces difficulty |
| Amount | 2 | Levels lost per death |
| Cooldown | 5 minutes | Prevent exploit via rapid suicide |
| Minimum level | 0 | Floor — can't go below this |

### Death Relief Rules

- Triggers on **all death causes** — PvE, PvP, void, `/kill`, fall damage, etc. HMIOT does not differentiate death causes either.
- The **cooldown** is per-player and tracked in ticks. After a death triggers relief, subsequent deaths within the cooldown window do NOT reduce level. This prevents rapid suicide exploits.
- The cooldown timer resets on each qualifying death (not on each death attempt).
- Death relief is disabled by default in HMIOT but enabled here — SMP frustration spirals are a real retention risk, and 2 levels per death (2 hours of progress) is meaningful enough to matter without being devastating.

## Level Shatter Shards

Carried over from HMIOT — rare mob drops that let players intentionally lower difficulty.

| Setting | Default | Description |
|---------|---------|-------------|
| Enabled | true | Whether shards drop |
| Drop start level | 25 | Minimum player level before shards drop |
| Shard power | 5 | Levels reduced per shard |
| Drop chance | 0.5% | Base chance per mob kill |
| Side effects | true | Debuffs on shard use |
| Glow carriers | false | Shard-carrying mobs glow |

### Implementation Details

- **Item type:** Custom registered item (`tribulation:shatter_shard`) with a custom texture. Appears in a dedicated creative tab.
- **How it drops:** On mob death, if the killing player's level is ≥ `dropStartLevel`, roll `dropChance`. If successful, add the shard to the mob's loot drops. Any player can pick up the dropped shard.
- **How to use:** Right-click (consumable item with `use()` override). Reduces the **using player's** level by `shardPower`. Cannot reduce below `deathRelief.minimumLevel`.
- **Side effects (when enabled):** On use, the player receives Slowness II (10s), Mining Fatigue II (10s), and Weakness II (10s). These are mild, predictable debuffs — HMIOT's wild random effects (vex spawns, block destruction, boss spawns) are **intentionally excluded** as too disruptive for SMP.
- **Glow carriers:** When enabled, mobs that have been flagged to drop a shard on death receive the Glowing effect, giving players a visual hint.

## Special Zombie Variants

Carried over from RpgDifficulty. Random chance to spawn variant zombies with distinct stat profiles.

### Big Zombie

| Setting | Default | Description |
|---------|---------|-------------|
| `bigZombieChance` | 10 | Percentage chance a zombie spawns as a big variant |
| `bigZombieSize` | 1.3 | Scale multiplier (30% larger) |
| `bigZombieBonusHealth` | 10 | Extra HP added |
| `bigZombieBonusDamage` | 2 | Extra damage added |
| `bigZombieSlowness` | 0.7 | Speed multiplier (30% slower) |

### Speed Zombie

| Setting | Default | Description |
|---------|---------|-------------|
| `speedZombieChance` | 10 | Percentage chance a zombie spawns as a speed variant |
| `speedZombieSpeedFactor` | 1.3 | Speed multiplier (30% faster) |
| `speedZombieMalusHealth` | 10 | HP subtracted |

> Special zombie modifiers stack with the normal time/distance/height scaling.

### Variant Rules

- **Mutually exclusive:** A zombie is rolled for Speed first, then Big only if the Speed roll failed. A zombie cannot be both. This matches RpgDifficulty's implementation.
- **Applies to all zombie-family mobs:** Zombie, Husk, Drowned, Zombified Piglin. Baby zombies are excluded.
- **Order of operations:** Normal time/distance/height scaling is applied first. Variant modifiers (bonus health, speed multiplier, etc.) are applied **after** as flat additions/multiplications on the already-scaled values. Example: a level-250 zombie has 70 HP; a Big variant adds +10 → 80 HP. A Speed variant subtracts 10 → 60 HP.

## Boss Scaling

Bosses scale separately with reduced rates to avoid making them impossible.

### Boss Detection

Bosses are identified by the **entity type tag** `c:bosses` (Conventional Tags), matching RpgDifficulty's approach. The mod ships a default tag including:
- `minecraft:ender_dragon`
- `minecraft:wither`
- `minecraft:elder_guardian`
- `minecraft:warden`

Modded bosses are automatically included if their mod adds them to the `c:bosses` tag. Server operators can also add entity types to this tag via datapack.

### Boss Scaling Rules

- Bosses **skip height scaling** entirely (matches RpgDifficulty).
- Distance and time scaling use **separate boss-specific rates** that replace (not multiply) the normal rates.
- Bosses are **not excluded from other dimensions** — boss distance scaling works in all dimensions even when `excludeInOtherDimensions` is true for normal mobs.

| Setting | Default | Description |
|---------|---------|-------------|
| `affectBosses` | true | Whether bosses are affected at all |
| `bossMaxFactor` | 3.0 | Hard cap on total boss scaling factor |
| `bossDistanceFactor` | 0.1 | Distance scaling rate for bosses (replaces normal `distanceFactor`) |
| `bossTimeFactor` | 0.3 | Time scaling rate for bosses (replaces normal per-attribute time rates) |

## Extra XP & Loot

| Setting | Default | Description |
|---------|---------|-------------|
| `extraXp` | true | Scaled mobs drop more XP proportional to their difficulty |
| `maxXpFactor` | 2.0 | Cap on XP multiplier |
| `dropMoreLoot` | false | Whether scaled mobs have a chance to drop extra loot |
| `moreLootChance` | 0.02 | Base chance for extra loot per difficulty factor |
| `maxLootChance` | 0.7 | Cap on extra loot chance |

## Entity Scope

### Scaling Target

**Vanilla hostile mobs** listed in the `mobToggles` config receive full per-attribute scaling (health, damage, speed, follow range, armor, toughness). Unlike RpgDifficulty (which scales ALL `MobEntity` instances including passive mobs), we follow HMIOT's approach of an explicit allowlist for vanilla mobs. This prevents unintended side effects on wolves, iron golems, bees, villager guards, etc.

**Modded hostile mobs** receive a conservative fallback (health and damage only) by default — see "Modded Hostile Mob Fallback Scaling" below. Server admins can also place fully-qualified entity IDs (e.g. `mutantmonsters:mutant_zombie`) directly into the `scaling` map for hand-tuned per-mob overrides.

### Modded Hostile Mob Fallback Scaling

Modded mod packs frequently add hostile mobs (Mutant Monsters' Mutant Zombie, Born in Chaos creatures, mob variants from content packs). Without this fallback, those mobs stay frozen at their base stats while vanilla mobs grow with player level — inverting the modpack's intended threat curve over time. The fallback applies a **health-and-damage-only** scaling profile to any unlisted entity that:

1. Is `instanceof net.minecraft.world.entity.monster.Monster` (the vanilla hostile-mob interface that nearly every modded hostile entity extends).
2. Is not in `general.excludedEntities`.
3. Is not in the `c:bosses` tag (bosses go through their own scaling path).
4. Is not in a namespace listed in `unlistedHostileMobs.excludedNamespaces` (per-mod opt-out).

#### Why health and damage only?

Modded hostile mobs are usually already tuned in speed, follow range, armor, and AI behavior — these are part of how the mod author designed the encounter. Boosting them further can feel unfair (e.g. a Mutant Zombie that's also faster) or break carefully-balanced mod content. Health and damage are safer because:

- Players already expect tougher enemies as they level — a Mutant Zombie that hits a bit harder at level 250 than at level 0 reads as natural progression.
- Health and damage scale predictably and don't compound with the mod's existing tuning surprises.
- It avoids breaking pack-specific encounter design (boss arenas, scripted spawns, etc.).

Server admins who want different behavior for a specific modded mob can override via the per-ID escape hatch (see below).

#### Lookup precedence

When a mob spawns, scaling is resolved in this order (first match wins):

1. **Full-ID override** — if `scaling` contains an entry keyed by the entity's full registry ID (e.g. `"mutantmonsters:mutant_zombie"`), that `MobScaling` is used. This works for both vanilla and modded entities and is the escape hatch for hand-tuning.
2. **Vanilla path lookup** — for `minecraft:` namespace mobs, if `scaling` contains an entry under the path (e.g. `"zombie"`) AND `mobToggles` enables it, that `MobScaling` is used. This is the standard vanilla path. If a vanilla mob is listed but its toggle is `false`, scaling is skipped (the fallback does NOT take over — explicit no-scale wins).
3. **Modded fallback** — if `unlistedHostileMobs.enabled` is true, the mob is `instanceof Monster`, and its namespace is not in `unlistedHostileMobs.excludedNamespaces`, the fallback `MobScaling` is used.
4. **Otherwise** — no scaling is applied.

#### Defaults

| Setting | Default | Description |
|---------|---------|-------------|
| `unlistedHostileMobs.enabled` | `true` | Master toggle for the modded fallback |
| `unlistedHostileMobs.excludedNamespaces` | `[]` | Per-mod opt-out (e.g. `["bosses_of_mass_destruction"]` to skip an entire mod) |
| `unlistedHostileMobs.scaling.healthRate` | `0.010` | Same as zombie reference rate |
| `unlistedHostileMobs.scaling.healthCap` | `2.50` | +250% health at max level |
| `unlistedHostileMobs.scaling.damageRate` | `0.015` | Same as zombie reference rate |
| `unlistedHostileMobs.scaling.damageCap` | `3.75` | +375% damage at max level |
| All other rates/caps | `0` | Speed, follow range, armor, toughness left untouched |

### Excluded Entities

Entities that should never be affected by difficulty scaling, even if they match a mob toggle:

```json
"excludedEntities": [
  "the_bumblezone:cosmic_crystal_entity"
]
```

Custom entity IDs can be added to exempt modded entities from all scaling.

### Intentionally Omitted Mobs

| Mob | Reason |
|-----|--------|
| **Phantom** | Phantoms punish players who skip sleeping. Adding difficulty scaling on top would double-punish. They're already annoying enough at vanilla stats. Can be added later if requested. |
| **Breeze** | 1.21 trial chamber mob. Trial chambers are designed as self-contained challenges with their own scaling via trial spawners and the ominous system. Scaling Breezes would break the balanced loot-to-difficulty ratio. |
| **Warden** | The Warden is intended as an avoid-at-all-costs mob, not a fight-it mob. Scaling its stats would be meaningless since players aren't supposed to engage it. Included in the `c:bosses` tag for boss detection but receives no scaling. |
| **Shulker** | Shulkers deal fixed levitation effect damage. Scaling their stats doesn't meaningfully change the encounter. |
| **Evoker** | Evoker damage comes from Evoker Fangs (separate entity). Scaling the Evoker's melee attack is largely irrelevant. Can be added later with fang damage scaling via mixin if desired. |
| **Ghast** | Ghast fireballs are reflected back — scaling Ghast health/damage changes the encounter minimally. Can be added later. |
| **Blaze** | Similar to Ghast — fireball-focused. Can be added later. |
| **Magma Cube / Slime** | Split mechanics make stat scaling awkward (smaller splits inherit parent stats?). Can be added later with split-handling logic. |

### Spawner Mobs

Mobs from spawners (dungeons, trial chambers, custom spawners) **are scaled** — there is no special spawner detection. This matches RpgDifficulty's behavior. The `extraXp` reward compensates for the increased difficulty. If spawner farm balance becomes an issue, a `excludeSpawnerMobs` config toggle can be added later.

> **Note:** This is a deliberate design choice. Exempting spawner mobs would require detecting the spawn source (natural, spawner, spawn egg, command), which adds complexity. The simpler approach is to scale everything and tune XP/loot rewards.

## All Scaled Mob Types

Each mob type has its own attribute scaling rates tuned to its role:

| Mob | Unique Scaling Notes |
|-----|---------------------|
| Zombie | Reference mob — all attributes scale |
| Skeleton | Power (bow damage) instead of melee attack |
| Creeper | Fuse time decreases |
| Spider | Speed scales faster than other mobs |
| Cave Spider | Poison duration scales with tier |
| Endermite | Low health scaling, high speed scaling |
| Silverfish | Minimal stat scaling, ability-focused |
| Drowned | Trident damage scales separately |
| Husk | Hunger effect duration scales |
| Stray | Slowness effect scales |
| Pillager | Crossbow-based damage scaling |
| Vindicator | Heavy melee scaling (axe damage) |
| Witch | Potion effectiveness scales |
| Wither Skeleton | Wither effect duration scales |
| Guardian | Beam damage scales |
| Hoglin | Heavy knockback and health |
| Zoglin | High health, moderate damage |
| Ravager | Extreme health, roar mechanics |
| Piglin | Crossbow + melee hybrid |
| Zombified Piglin | Group aggro mechanics |
| Bogged | Poison arrow scaling |

## Config File Structure

`config/tribulation.json`:

```json
{
  "configVersion": 1,
  "general": {
    "maxLevel": 250,
    "levelUpTicks": 72000,
    "mobDetectionRange": 32,
    "excludedEntities": [
      "the_bumblezone:cosmic_crystal_entity"
    ]
  },
  "timeScaling": {
    "enabled": true
  },
  "distanceScaling": {
    "enabled": true,
    "startingDistance": 1000,
    "increasingDistance": 300,
    "distanceFactor": 0.1,
    "maxDistanceFactor": 1.5,
    "excludeInOtherDimensions": true
  },
  "heightScaling": {
    "enabled": true,
    "startingHeight": 62,
    "heightDistance": 30,
    "heightFactor": 0.1,
    "maxHeightFactor": 0.5,
    "positiveHeightScaling": true,
    "negativeHeightScaling": true,
    "excludeInOtherDimensions": true
  },
  "statCaps": {
    "maxFactorHealth": 4.0,
    "maxFactorDamage": 4.5,
    "maxFactorSpeed": 0.5,
    "maxFactorProtection": 2.0,
    "maxFactorFollowRange": 1.5
  },
  "deathRelief": {
    "enabled": true,
    "amount": 2,
    "cooldownTicks": 6000,
    "minimumLevel": 0
  },
  "shards": {
    "enabled": true,
    "dropStartLevel": 25,
    "shardPower": 5,
    "dropChance": 0.005,
    "sideEffects": true,
    "glowCarriers": false
  },
  "scaling": {
    "zombie": {
      "healthRate": 0.01,
      "healthCap": 2.5,
      "damageRate": 0.015,
      "damageCap": 3.75,
      "speedRate": 0.0012,
      "speedCap": 0.3,
      "followRangeRate": 0.01,
      "followRangeCap": 1.0,
      "armorRate": 0.032,
      "armorCap": 8,
      "toughnessRate": 0.024,
      "toughnessCap": 6
    }
  },
  "unlistedHostileMobs": {
    "enabled": true,
    "excludedNamespaces": [],
    "scaling": {
      "healthRate": 0.010,
      "healthCap": 2.50,
      "damageRate": 0.015,
      "damageCap": 3.75,
      "speedRate": 0.0,
      "speedCap": 0.0,
      "followRangeRate": 0.0,
      "followRangeCap": 0.0,
      "armorRate": 0.0,
      "armorCap": 0.0,
      "toughnessRate": 0.0,
      "toughnessCap": 0.0
    }
  },
  "specialZombies": {
    "enabled": true,
    "bigZombieChance": 10,
    "bigZombieSize": 1.3,
    "bigZombieBonusHealth": 10,
    "bigZombieBonusDamage": 2,
    "bigZombieSlowness": 0.7,
    "speedZombieChance": 10,
    "speedZombieSpeedFactor": 1.3,
    "speedZombieMalusHealth": 10
  },
  "bosses": {
    "affectBosses": true,
    "bossMaxFactor": 3.0,
    "bossDistanceFactor": 0.1,
    "bossTimeFactor": 0.3
  },
  "xpAndLoot": {
    "extraXp": true,
    "maxXpFactor": 2.0,
    "dropMoreLoot": false,
    "moreLootChance": 0.02,
    "maxLootChance": 0.7
  },
  "tiers": {
    "tier1": 50,
    "tier2": 100,
    "tier3": 150,
    "tier4": 200,
    "tier5": 250
  },
  "mobToggles": {
    "zombie": true,
    "skeleton": true,
    "creeper": true,
    "spider": true,
    "cave_spider": true,
    "endermite": true,
    "silverfish": true,
    "drowned": true,
    "husk": true,
    "stray": true,
    "pillager": true,
    "vindicator": true,
    "witch": true,
    "wither_skeleton": true,
    "guardian": true,
    "hoglin": true,
    "zoglin": true,
    "ravager": true,
    "piglin": true,
    "zombified_piglin": true,
    "bogged": true
  },
  "abilities": {
    "endermiteTeleport": true,
    "caveSpiderWebs": true,
    "silverfishSpread": true,
    "skeletonSwordSwitch": true,
    "zombifiedPiglinAnger": true,
    "creeperFuse": true,
    "spiderCropTrample": true
  }
}
```

## Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/tribulation info` | 0 | Show the caller's own level, tier, and progress to the next level |
| `/tribulation level <player>` | 2 | Admin lookup of another player's level and progress |
| `/tribulation config` | 2 | Show the full scaling config summary (axes, caps, subsystem toggles) |
| `/tribulation set <player> <level>` | 2 | Set a player's level |
| `/tribulation reset <player>` | 2 | Reset a player to level 0 |
| `/tribulation reload` | 2 | Reload config from disk |
| `/tribulation debug <player>` | 2 | Show all three axis factors for a player's current position |
| `/tribulation inspect` | 2 | Show scaling details for the mob the player is looking at (applied modifiers, tier, variant type) |

## What This Replaces

- **Removes:** HMIOT (Hostile Mobs Improve Over Time) — replaced by time-based scaling
- **Removes:** RpgDifficulty — replaced by distance and height scaling
- **Adds:** Tribulation companion mod (unified replacement)

Both mods' functionality is consolidated into one mod with one config file. HMIOT's datapack/scoreboard approach and RpgDifficulty's separate config are replaced by a proper Fabric mod with formula-driven scaling across all three axes.

### Key Differences from HMIOT

| Aspect | HMIOT | Tribulation |
|--------|-------|-------------------|
| Architecture | Datapack + scoreboards + `.mcfunction` files | Fabric mod with Java attribute modifiers |
| Level range | 0–100 | 0–250 |
| Level-up interval | 20 minutes (configurable 1–360 min) | 1 hour (configurable) |
| Scaling data | 101 hardcoded entries per mob per attribute | Formula-driven (rate * level, capped) |
| Application trigger | Proximity (mob enters 32-block range of player) | Spawn time (`ENTITY_LOAD` event) |
| Shatter shard effects | Wild random chaos (vex spawns, block destruction, boss spawns, terrain grief) | Mild predictable debuffs (slowness, weakness, mining fatigue) |
| Zoglin corruption | Converts blocks to Nether equivalents (extremely destructive) | Excluded — too destructive for SMP |
| Death relief | Off by default | On by default (2 levels per death) |
| Boss scaling | Not supported | Supported with reduced rates |
| Distance/height | Not supported | Supported (absorbed from RpgDifficulty) |

### Key Differences from RpgDifficulty

| Aspect | RpgDifficulty | Tribulation |
|--------|---------------|-------------------|
| Time scaling basis | World time (ticks since world creation) | Per-player playtime (online hours only) |
| Attribute application | `setBaseValue()` (overwrites vanilla base) | `EntityAttributeModifier` (namespaced, removable) |
| Mob scope | All `MobEntity` including passive mobs | Hostile-only allowlist |
| Special zombie overlap | Mutually exclusive (speed checked first) | Same — mutually exclusive |
| Dependencies | Cloth Config API (required) | None beyond Fabric API |
| Client requirement | Required (TrackedData for Big Zombie rendering) | Required (TrackedData for Big Zombie rendering) |
| Boss detection | `c:bosses` entity type tag | Same — `c:bosses` entity type tag |
| Per-dimension overrides | Datapack-driven JSON per dimension | Config flags only (simpler) |
