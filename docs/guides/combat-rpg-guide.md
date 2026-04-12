# Combat & RPG Guide

FizzleSMP overhauls combat with new mechanics, weapon types, RPG classes with spells, and tougher enemies. This guide covers everything you need to know.

---

## Combat Basics

### Better Combat — Revamped Melee

Melee combat has been completely reworked:

- **Swing animations** — Weapons have unique slash, stab, and slam animations depending on type.
- **Weapon combos** — Some weapons chain different attack types in sequence.
- **Dual wielding** — Equip one-handed weapons in both hands. Attack alternates between main hand and off-hand.
- **Hit detection** — No more pixel-hunting with the crosshair. Weapons hit anything in the swing arc. Third person is now viable.
- **Hold to attack** — Hold the attack button to auto-swing on cooldown.
- **Sweeping Edge reworked** — Multi-target swings deal reduced damage unless you have the Sweeping Edge enchantment.

### Combat Roll — Dodge Mechanic

- Press **R** (default) to roll/dodge in whatever direction you're moving.
- 4-second cooldown (shown on HUD).
- Uses a small amount of hunger.
- Can't roll while jumping, swimming, or using an item.

**Enchantments for rolling:**
| Enchantment | Slot | Effect |
|---|---|---|
| Multi-Roll | Helmet | +1 roll per level |
| Acrobat | Chest/Legs | +10% recharge speed per level |
| Longfooted | Boots | +1 block roll distance per level |

### Critical Strike

Adds a critical strike system — attacks have a chance to deal bonus damage. Look for gear with crit chance/damage bonuses.

---

## Weapons

### Simply Swords — 14 New Weapon Types

Each weapon type has a different playstyle and attack animation:

- **Longswords, Rapiers, Katanas** — Fast one-handed weapons
- **Claymores, Greataxes, Greathammers** — Slow, heavy two-handed weapons
- **Twinblades** — Dual-wielded, extremely fast
- **Glaives, Spears** — Long reach
- **Chakrams** — Throwable returning weapons
- **Scythes, Halberds** — Wide sweeping attacks
- And more

**Unique weapons** with special abilities drop from loot chests while exploring. These cannot be crafted — you have to find them.

### Mythic Upgrades

Adds new material tiers beyond netherite with unique weapon and armor sets. Check EMI for crafting recipes.

---

## RPG Classes

The pack includes a full RPG class system powered by the Spell Engine. All classes follow the same pattern:

### How to Get Started with Any Class

1. **Pick up a weapon** that matches your class (see below).
2. **Find a Spell Binding Table** — look for **Gazebo** structures in villages, or craft your own. Surround it with bookshelves (like an enchanting table).
3. **Create your class Spell Book** at the Spell Binding Table. This unlocks all spells for that class.
4. **Equip the spell book** in your off-hand (or accessory slot) while holding a weapon.
5. **Craft Runes** — these are ammunition for spells, like arrows for bows. Check EMI for rune recipes.

### The Classes

#### Wizard (Arcane / Fire / Frost)
- **Weapons:** Wands, Staves
- **Spell Books:** Tome of Arcane, Tome of Fire, Tome of Frost
- **Playstyle:** Ranged magic damage. Arcane for focused single-target, Fire for area damage, Frost for damage + crowd control.
- **Armor:** Craftable wizard robes in material tiers
- **Village:** Wizard Towers (sell magic equipment)

#### Paladin
- **Weapons:** Hammers, Maces, Claymores (any melee + shield works)
- **Spell Book:** Paladin Libram
- **Playstyle:** Tanky frontline. Protect allies, deal melee damage, defensive buffs.
- **Armor:** Heavy paladin armor sets
- **Village:** Sanctuaries (Monk villagers sell equipment)

#### Priest
- **Weapons:** Holy Wands, Holy Staves
- **Spell Book:** Holy Book
- **Playstyle:** Healer/support. Heal and shield allies, smite enemies.
- **Armor:** Priest robes
- **Village:** Sanctuaries (same as Paladin)

#### Archer
- **Weapons:** Any bow or crossbow (vanilla or modded)
- **Spell Book:** Archery Manual
- **Playstyle:** Ranged physical damage with archery skills.
- **Bonus:** Auto-Fire Hook — a craftable gadget that attaches to any bow/crossbow for automatic rapid fire. Removable on a grindstone.
- **Armor:** Archery-focused armor with ranged bonuses
- **Village:** Archery Ranges

#### Rogue
- **Weapons:** Daggers, Sickles (fast, one-handed, dual-wield recommended)
- **Spell Book:** Rogue Manual
- **Playstyle:** Evasion, tricks, quick strikes. Dual wield for maximum DPS.
- **Village:** Barracks

#### Warrior
- **Weapons:** Double Axes, Glaives, Claymores (heavy, slow weapons)
- **Spell Book:** Warrior Codex
- **Playstyle:** Brute force. Weaken and smash opponents with powerful melee skills.
- **Village:** Barracks (same as Rogue)

### Skill Tree

Each class has a **100+ node skill tree** with offensive and defensive specializations, spell modifiers, and passive abilities. Spending points costs XP.

- Open the skill tree UI to see your available nodes and plan a build.
- Different branches offer different playstyles within the same class.
- **Respec:** Craft an **Orb of Oblivion** (check EMI for the recipe) to fully reset your skill tree and reclaim all spent points.

### RPG Gear & Loot

- **Class equipment** is craftable in material tiers (check EMI for recipes).
- **Dungeon loot** — class-specific weapons and armor drop from dungeon chests.
- **Jewelry** — craftable rings, necklaces, and bracelets that boost spell power, health, and other attributes.
- **Spell enchantments** — look for Spell Infinity (no rune cost), Spell Power (more damage), and Spell Haste (faster casting).

### Enchantment Rebalancing

The RPG mods rebalance some vanilla enchantments for fairer scaling:

| Enchantment | Vanilla | FizzleSMP |
|---|---|---|
| Sharpness | +1 damage/level | +8%/level |
| Power (bows) | +50%/level | +8%/level |
| Quick Draw (crossbows) | -20% draw/level | -10%/level |

---

## Artifacts

Powerful accessory items found in dungeon chests, archaeology, or rarely dropped by mobs. **Cannot be crafted** — exploration only.

Equip artifacts in accessory slots for passive bonuses and unique effects. A new underground **Campsite** structure spawns with loot chests, but beware of **Mimics** — dangerous enemies that disguise as chests and always drop a random artifact when killed.

---

## Enemies & Bosses

### Hostile Mobs Improve Over Time

Mobs get stronger the longer you play. Difficulty is **per player** — veterans face tougher mobs while new players start easy.

- Difficulty increases based on playtime (up to level 100).
- Affected mobs: Zombies, Skeletons, Spiders, Creepers, Pillagers, and many more.
- Mobs gain new abilities, better stats, and improved AI at higher levels.
- **Level Shatter Shards** rarely drop from mobs — consume them to reduce your difficulty level (has side effects!).
- Check your level: look at the `HostileMobs` scoreboard.
- Config: `/function hostilemobs:config` to adjust difficulty, toggle mob types, and set caps.

### Boss Fights — Bosses of Mass Destruction

Four endgame bosses found in rare structures:

| Boss | Location | How to Find |
|---|---|---|
| **Night Lich** | Towers in cold biomes | Follow Soul Stars |
| **Void Blossom** | Caves at the bottom of the world | Follow Void Lilies |
| **Obsidilith** | Structures on End islands | Explore outer End |
| **Nether Gauntlet** | Structures in the Nether | Explore the Nether |

### Mutant Monsters

Rare, powerful mutant variants of vanilla mobs (Mutant Creeper, Mutant Zombie, etc.) spawn in the overworld. They're mini-bosses with unique attacks and drops.

### Illager Invasion

New illager types and structures spawn in the overworld. Raids include additional illager variants with unique behaviors.
