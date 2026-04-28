# Tribulation

**A unified mob scaling system for Minecraft 1.21.1 Fabric.**

Tribulation replaces both [HMIOT](https://www.curseforge.com/minecraft/mc-mods/hostile-mobs-improve-over-time) (Hostile Mobs Improve Over Time) and [RpgDifficulty](https://www.curseforge.com/minecraft/mc-mods/rpgdifficulty) with a single formula-driven scaling system. Three independent axes — player playtime, distance from spawn, and Y-level — combine to scale mob stats dynamically. No hardcoded tiers, no scoreboards, no datapacks — one config file controls everything.

---

## Features

### Three Scaling Axes

Mob stats are computed from three independent factors that sum together before applying to attributes:

| Axis | What it measures | Computed from |
|------|-----------------|---------------|
| **Time** | Player's cumulative online playtime | Per-player persistent level (0–250) |
| **Distance** | Mob's distance from world spawn | 2D horizontal (XZ) block distance at spawn time |
| **Height** | Mob's Y-level relative to sea level | Absolute Y offset from baseline (default: Y=62) |

Scaling is applied **once at spawn time** via namespaced attribute modifiers that persist across chunk unload/reload. Mobs are never re-processed.

### Player Difficulty Level

- Each player has a persistent difficulty level (0–250)
- Levels up by 1 every hour of real-time play (configurable)
- Timer is per-player and only ticks while online
- Persists across deaths, logouts, and server restarts

### 250-Hour Progression Curve

| Level Range | Hours Played | Feel | Gear Expectation |
|-------------|-------------|------|------------------|
| 0–25 | 0–25h | Vanilla | Any |
| 25–75 | 25–75h | Noticeably tougher | Iron gear |
| 75–125 | 75–125h | Challenging | Diamond gear + enchants |
| 125–175 | 125–175h | Dangerous | Netherite recommended |
| 175–250 | 175–250h | Brutal endgame | Full netherite + good enchants, group play |

### Distance Scaling

Mobs further from world spawn are tougher. Distance scaling starts at 1000 blocks and increases by one level per 300 blocks beyond that (default, configurable). Capped at +150% stats.

Distance is measured in 2D (XZ) — Y is excluded so height doesn't double-dip. Nether coordinates are not multiplied by 8. Optionally disabled in non-overworld dimensions.

### Height Scaling

Mobs at extreme Y-levels (deep caves, high peaks) are tougher. Scaling is bidirectional from the baseline (sea level) at one level per 30 blocks of offset. Capped at +50% stats.

### 21 Scaled Mob Types

Each mob type has its own tuned attribute rates. The zombie is the reference mob:

| Attribute | Rate / Level | Cap | Lvl 0 | Lvl 250 |
|-----------|-------------|-----|-------|---------|
| Max Health | +1% | +250% (3.5x) | 20 HP | **70 HP** |
| Attack Damage | +1.5% | +375% (4.75x) | 3 dmg | **14.25** |
| Movement Speed | +0.12% | +30% | 0.23 | **0.299** |
| Follow Range | +1% | +100% (2x) | 35 | **70** |
| Armor | +1 per 31 levels | +8 | 0 | **8** |
| Armor Toughness | +1 per 42 levels | +6 | 0 | **6** |

Speed and follow range scale through playtime only — distance and height affect health, damage, armor, and toughness but not speed or perception, avoiding compound frustration spikes.

### Tier-Gated Abilities

5 tiers unlock special mob abilities as the nearest player's level increases:

| Tier | Level | Examples |
|------|-------|---------|
| 1 | 50 | Zombie reinforcements, Creeper shorter fuse, Hoglin knockback resist |
| 2 | 100 | Skeleton sword switch, Spider web placing, Drowned trident upgrade |
| 3 | 150 | Zombie door-breaking, Spider crop trampling, Wither Skeleton sprint |
| 4 | 200 | Skeleton flame arrows, Husk Hunger II, Wither Skeleton fire aspect |
| 5 | 250 | Zombie sprinting, Creeper charged chance, Spider leap attack |

### Special Zombie Variants

Random chance to spawn variant zombies (applies to Zombie, Husk, Drowned, Zombified Piglin):

- **Big Zombie** (10% chance) — 30% larger, +10 HP, +2 damage, 30% slower
- **Speed Zombie** (10% chance) — 30% faster, -10 HP

Variants are mutually exclusive and stack with normal scaling.

### Boss Scaling

Bosses (identified by the `c:bosses` entity type tag) scale with reduced rates. Height scaling is skipped entirely for bosses. Distance and time scaling use separate boss-specific rates capped at 3.0x total. Boss scaling works in all dimensions.

### Death Relief

Losing 2 levels per death (configurable) prevents frustration spirals. Has a 5-minute cooldown to prevent suicide exploits. Enabled by default.

### Shatter Shards

Rare mob drops (0.5% chance, starting at player level 25) that reduce the user's level by 5 on right-click. Applies mild debuffs (Slowness II, Mining Fatigue II, Weakness II for 10 seconds).

### Modded Mob Support

Unlisted hostile mobs (any `Monster` subclass not in the vanilla allowlist) receive conservative health-and-damage-only scaling by default. Per-mod opt-out via `excludedNamespaces`, or hand-tune any specific entity ID via the `scaling` map.

### Extra XP

Scaled mobs drop bonus XP proportional to their difficulty (up to 2x, configurable). Optional extra loot drops are available but disabled by default.

---

## Installation

### Requirements

- Minecraft **1.21.1**
- Fabric Loader **0.16.10+**
- Fabric API
- Java **21**

### Setup

Drop the jar into the `mods/` directory on both server and client. The mod must be present on **both sides** — it uses `TrackedData` for Big Zombie rendering on the client.

---

## Configuration

The mod generates `config/tribulation.json` on first launch with sensible defaults. Every value can be tuned without a restart using `/tribulation reload`.

Key config sections:

| Section | Controls |
|---------|----------|
| `general` | Max level, level-up interval, detection range, excluded entities |
| `timeScaling` | Enable/disable time axis |
| `distanceScaling` | Starting distance, step size, rate, cap, dimension exclusion |
| `heightScaling` | Baseline Y, step size, rate, cap, directional toggles, dimension exclusion |
| `statCaps` | Global caps per attribute (prevents extreme stacking across all axes) |
| `deathRelief` | Enable, amount, cooldown, minimum level floor |
| `shards` | Enable, drop level, power, drop chance, side effects, glow carriers |
| `scaling` | Per-mob attribute rates and caps (keyed by entity path or full ID) |
| `unlistedHostileMobs` | Fallback scaling for modded hostile mobs |
| `specialZombies` | Big/Speed zombie variant chances and stat adjustments |
| `bosses` | Boss scaling rates and cap |
| `xpAndLoot` | XP multiplier, extra loot toggle |
| `tiers` | Level thresholds for ability tiers 1–5 |
| `mobToggles` | Per-mob enable/disable switches |
| `abilities` | Per-ability enable/disable switches |

---

## Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/tribulation info` | 0 | Show your own level, tier, and progress to next level |
| `/tribulation level <player>` | 2 | Admin lookup of another player's level and progress |
| `/tribulation config` | 2 | Show the full scaling config summary |
| `/tribulation set <player> <level>` | 2 | Set a player's level |
| `/tribulation reset <player>` | 2 | Reset a player to level 0 |
| `/tribulation reload` | 2 | Reload config from disk |
| `/tribulation debug <player>` | 2 | Show all three axis factors for a player's current position |
| `/tribulation inspect` | 2 | Show scaling details for the mob you're looking at |

---

## Building from Source

```sh
./gradlew build          # produces build/libs/tribulation-<version>.jar
./gradlew test           # runs unit tests
./gradlew runGametest    # runs Fabric gametest suite
```

---

## What This Replaces

| Aspect | HMIOT | RpgDifficulty | Tribulation |
|--------|-------|---------------|-------------|
| Architecture | Datapack + scoreboards | Fabric mod (Cloth Config) | Fabric mod (no extra deps) |
| Level range | 0–100 | N/A (world time) | 0–250 |
| Level-up interval | 20 min | N/A | 1 hour |
| Scaling data | 101 hardcoded entries per mob | Config-driven | Formula-driven (rate × level, capped) |
| Application trigger | Proximity (32 blocks) | Spawn time | Spawn time |
| Time basis | Per-player | World time | Per-player online time |
| Distance/height | Not supported | Supported | Supported |
| Boss scaling | Not supported | Supported | Supported (reduced rates) |
| Modded mob fallback | None | All MobEntity (including passive) | Hostile-only (health + damage) |
| Attribute strategy | Varies | `setBaseValue()` | Namespaced `EntityAttributeModifier` |
| Shard side effects | Wild chaos (vex spawns, terrain grief) | N/A | Mild debuffs only |

---

## License

MIT
