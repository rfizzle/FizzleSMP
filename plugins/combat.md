# Combat & Balancing

<!-- Mods that overhaul combat mechanics, add weapons/armor, or rebalance difficulty. -->

## Simply Swords
- **CurseForge ID:** 659887
- **Slug:** simply-swords
- **Pin CurseForge File ID:** 6958140
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds 14 unique weapon variants with different playstyles, plus rare unique weapons with special abilities found in loot chests.
- **Why:** Greatly expands weapon variety for SMP combat with throwable chakrams, twinblades, greataxes, and more — each with distinct attack patterns via Better Combat.
- **Dependencies:** Architectury API, Fzzy Config; Better Combat (optional, recommended)
- **Conflicts:** v1.63.0+ requires Simply Tooltips, which hard-conflicts with Alexandria (Accessories screen crash). Pinned to v1.62.0.

## Better Combat
- **CurseForge ID:** 639842
- **Slug:** better-combat-by-daedelus
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Overhauls melee combat with Minecraft Dungeons-style attack animations, accurate weapon collision, dual wielding, and weapon combos.
- **Why:** Makes combat visually spectacular and mechanically deeper with swing animations, hit detection by weapon arc, and combo systems.
- **Dependencies:** Fabric API, Cloth Config API, playerAnimator
- **Conflicts:** Incompatible with other dual wielding mods (none in pack). Partnered with Simply Swords for full weapon animation support.

## Combat Roll
- **CurseForge ID:** 678036
- **Slug:** combat-roll
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a dodge/roll ability with configurable cooldown, distance, and optional invulnerability frames, plus related enchantments.
- **Why:** Complements Better Combat with evasive movement, adding depth to PvE and PvP encounters on the SMP.
- **Dependencies:** Fabric API, Cloth Config API, playerAnimator
- **Conflicts:** None known; designed by same author as Better Combat to work together.

## Fzzy Config
- **CurseForge ID:** 1005914
- **Slug:** fzzy-config
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A configuration library for Fabric mods with GUI support.
- **Why:** Required dependency of Simply Swords.
- **Dependencies:** None
- **Conflicts:** None known

## playerAnimator
- **CurseForge ID:** 658587
- **Slug:** playeranimator
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** An animation library enabling custom player model animations for Minecraft mods.
- **Why:** Required dependency of Better Combat, Combat Roll, and Spell Engine.
- **Dependencies:** None
- **Conflicts:** None known

## Spell Power Attributes
- **CurseForge ID:** 771265
- **Slug:** spell-power
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Spell power entity attributes with related status effects and enchantments, providing an API for spell damage calculations, critical strikes, and more.
- **Why:** Core attribute system for the RPG Series magic mods; required dependency of Spell Engine.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Spell Engine
- **CurseForge ID:** 807653
- **Slug:** spell-engine
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Data-driven magic library providing a complete spell-casting system including spell books, visual effects, and weapon integration.
- **Why:** Core library powering the RPG Series magic mods (Wizards, Paladins & Priests); works alongside Better Combat for weapon animations.
- **Dependencies:** Fabric API, Cloth Config API, playerAnimator, Spell Power Attributes, Trinkets or Accessories
- **Conflicts:** None known

## Runes
- **CurseForge ID:** 775518
- **Slug:** rune-crafting
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds craftable runes that serve as ammunition for casting spells, with a Rune Crafting Altar for efficient production.
- **Why:** Provides the ammo system for Spell Engine spells; required by Wizards and Paladins & Priests.
- **Dependencies:** Fabric API, Bundle API
- **Conflicts:** None known

## Bundle API
- **CurseForge ID:** 1189983
- **Slug:** bundle-api
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** API allowing mods to easily add bundles that can hold more than 1 stack of items specified by an item tag.
- **Why:** Required dependency of Runes.
- **Dependencies:** None
- **Conflicts:** None known

## Wizards (RPG Series)
- **CurseForge ID:** 734325
- **Slug:** wizards
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds RPG-style wizard classes with Arcane, Fire, and Frost magic skill sets, spell books, wands, staves, and Wizard Towers in villages.
- **Why:** Adds deep magic combat classes to the SMP, complementing the existing melee combat from Better Combat and Simply Swords.
- **Dependencies:** Spell Engine, Runes, AzureLib Armor, Structure Pool API, Fabric API
- **Conflicts:** None known; strongly recommended alongside Better Combat for proper weapon animations.

## Paladins & Priests (RPG Series)
- **CurseForge ID:** 856548
- **Slug:** paladins-and-priests
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds Paladin and Priest RPG classes with healing, protection, and holy damage spells, plus Sanctuaries in villages with Monk villagers.
- **Why:** Adds support/healer classes to complement Wizards' DPS classes, enabling party-based gameplay on the SMP.
- **Dependencies:** Spell Engine, Runes, AzureLib Armor, Structure Pool API, Fabric API
- **Conflicts:** None known; strongly recommended alongside Better Combat for first-person animations.

## AzureLib Armor
- **CurseForge ID:** 912767
- **Slug:** azurelib-armor
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A stripped-down AzureLib providing custom armor model and item rendering with reduced memory usage.
- **Why:** Required dependency of Wizards and Paladins & Priests for animated armor rendering.
- **Dependencies:** None
- **Conflicts:** None known

## Structure Pool API
- **CurseForge ID:** 927915
- **Slug:** structure-pool-api
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** API to inject structures into vanilla structure pools, enabling mods to add buildings to villages and other generated structures.
- **Why:** Required dependency of Wizards and Paladins & Priests for adding towers and sanctuaries to villages.
- **Dependencies:** None
- **Conflicts:** None known

## SwingThrough
- **CurseForge ID:** 1402841
- **Slug:** swingthrough
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Allows targeting, interacting with, and attacking living entities through transparent blocks like grass, crops, and flowers.
- **Why:** Eliminates the frustration of grass blocking attacks during combat, especially important with Better Combat's directional swings.
- **Dependencies:** None
- **Conflicts:** None known

## Archers (RPG Series)
- **CurseForge ID:** 932359
- **Slug:** archers
- **Modrinth Slug:** archers
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds RPG-style archery class with skill sets, craftable ranged weapons, armor with archery bonuses, and an Auto-Fire Hook gadget for bows/crossbows.
- **Why:** Adds a dedicated ranged combat class to complement Wizards and Paladins, with archery skills usable with any bow or crossbow.
- **Dependencies:** Spell Engine, AzureLib Armor, Ranged Weapon API, Structure Pool API, Fabric API
- **Conflicts:** Rebalances Power enchantment (+8%/level instead of +50%) and Quick Draw (+10%/level instead of +20%) via datapack overrides. Configurable in `config/archers/tweaks.json`.

## Rogues & Warriors (RPG Series)
- **CurseForge ID:** 1048409
- **Slug:** rogues-and-warriors
- **Modrinth Slug:** rogues-and-warriors
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds Rogue and Warrior RPG classes with evasion/trick skills and heavy-hitting martial skills, plus new weapons, armor sets, and Barracks in villages.
- **Why:** Adds melee martial classes to complement magic classes, with dual-wield rogue skills and heavy-weapon warrior skills for diverse PvP/PvE playstyles.
- **Dependencies:** Spell Engine, AzureLib Armor, Structure Pool API, Fabric API
- **Conflicts:** Rebalances Sharpness enchantment (+8%/level) for balanced fast/slow weapon scaling. Strongly recommended alongside Better Combat.

## Jewelry (RPG Series)
- **CurseForge ID:** 910706
- **Slug:** jewelry
- **Modrinth Slug:** jewelry
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds 25+ craftable jewelry items (rings, necklaces) from mineable gem veins, with Jeweler villagers in villages and unique pieces in end-game loot chests.
- **Why:** Adds an accessory crafting system with combat attribute bonuses, giving players another progression axis alongside weapons and armor.
- **Dependencies:** Spell Power Attributes, Ranged Weapon API, Trinkets (via Accessories Compat Layer), Fabric API
- **Conflicts:** None known; adds gem veins at diamond depth (half as common as diamonds).

## Armory (RPG Series)
- **CurseForge ID:** 1311561
- **Slug:** armory-rpg-series
- **Modrinth Slug:** armory-rpg-series
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds epic end-game armor sets with unique 3D models, set bonuses, and spell modifiers for each RPG Series class.
- **Why:** Provides end-game armor progression for all RPG classes, obtainable via upgrade materials from End City/Ancient City chests and boss drops.
- **Dependencies:** Spell Engine, AzureLib Armor, Ranged Weapon API, Fabric API; requires Archers, Paladins & Priests, Rogues & Warriors, and Wizards
- **Conflicts:** None known

## Arsenal (RPG Series)
- **CurseForge ID:** 1230054
- **Slug:** arsenal-rpg-series
- **Modrinth Slug:** arsenal-rpg-series
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds 40+ legendary weapons of 15 different types with built-in passive spells, obtainable only from boss drops and end-game dungeon chests.
- **Why:** Provides aspirational end-game loot weapons with Minecraft Dungeons-style passive abilities (Swirling, Shockwave, Radiance) as rewards for defeating bosses.
- **Dependencies:** Spell Engine, Ranged Weapon API, Shield API, Fabric API
- **Conflicts:** None known; loot tables configurable in `config/rpg_series/loot_items.json`.

## Skill Tree (RPG Series)
- **CurseForge ID:** 1311513
- **Slug:** skill-tree
- **Modrinth Slug:** skill-tree
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a 100+ node skill tree for all RPG Series classes with offensive/defensive specializations, spell modifiers, and passive abilities, powered by XP.
- **Why:** Adds deep class specialization and meaningful build choices, letting players customize their RPG class with unique skill paths.
- **Dependencies:** Pufferfish's Skills, Spell Engine, Ranged Weapon API, Fabric API; requires Archers, Paladins & Priests, Rogues & Warriors, and Wizards
- **Conflicts:** None known; Orb of Oblivion item allows full skill tree reset.

## Ranged Weapon API
- **CurseForge ID:** 962162
- **Slug:** ranged-weapon-api
- **Modrinth Slug:** ranged-weapon-api
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** API for creating custom bows and crossbows with configurable damage, pull time, and projectile velocity.
- **Why:** Required dependency of Archers, Jewelry, Armory, Arsenal, and Skill Tree.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Shield API
- **CurseForge ID:** 1048720
- **Slug:** shield-api
- **Modrinth Slug:** shield-api
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** API allowing mods to easily add shields with custom models.
- **Why:** Required dependency of Arsenal.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Pufferfish's Skills
- **CurseForge ID:** 835091
- **Slug:** puffish-skills
- **Modrinth Slug:** skills
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A framework for fully configurable skill systems with an online editor and API, supporting custom skill trees via datapacks.
- **Why:** Required dependency of Skill Tree (RPG Series).
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Gazebos (RPG Series)
- **CurseForge ID:** 865298
- **Slug:** gazebos
- **Modrinth Slug:** gazebos
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds gazebo structures to villages containing Spell Binding Tables, with biome variants for desert, plains, savanna, snowy, and taiga villages.
- **Why:** Provides the natural discovery path for RPG class spell books — players find Spell Binding Tables in village gazebos instead of only crafting them.
- **Dependencies:** Structure Pool API
- **Conflicts:** None known; gazebo spawn frequency configurable in `config/gazebo/villages.json`.

## Critical Strike
- **CurseForge ID:** 1379562
- **Slug:** critical-strike
- **Modrinth Slug:** critical-strike
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds chance-based critical hit mechanics for melee and ranged combat with configurable crit chance/damage attributes, two new enchantments, potion effects, and Minecraft Dungeons-style visual/audio feedback.
- **Why:** Adds meaningful combat RNG depth with crit chance builds via enchantments and potions, complementing the RPG class system and Better Combat.
- **Dependencies:** Fabric API
- **Conflicts:** Replaces vanilla jump-critical system with RNG-based crits (configurable). Toggle vanilla jump crits in config.

## Illager Invasion
- **CurseForge ID:** 891324
- **Slug:** illager-invasion
- **Modrinth Slug:** illager-invasion
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds new illager enemy types with expanded raid mechanics and new structures, ported from Illager Expansion.
- **Why:** Makes raids genuinely dangerous and adds organic combat encounters through new illager variants without forced progression.
- **Dependencies:** Fabric API, Forge Config API Port, Puzzles Lib
- **Conflicts:** Overlaps with Friends & Foes on illager-adjacent mobs and raid/patrol systems; test raid mechanics together.

## Mutant Monsters
- **CurseForge ID:** 852665
- **Slug:** mutant-monsters
- **Modrinth Slug:** mutant-monsters
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds mutant variants of vanilla mobs (Mutant Zombie, Mutant Creeper, Mutant Skeleton, etc.) that serve as roaming mini-bosses with unique attacks and drops.
- **Why:** Provides organic mini-boss encounters you stumble into while exploring — no quest gates, just dangerous mutants that spawn naturally.
- **Dependencies:** Fabric API, Forge Config API Port, Puzzles Lib
- **Conflicts:** None known

## Hostile Mobs Improve Over Time
- **CurseForge ID:** 1409469
- **Slug:** hostile-mobs-improve-over-time-unofficial
- **Modrinth Slug:** hostile-mobs-improve-over-time
- **Mod Loader:** Fabric
- **Side:** server
- **Summary:** Hostile mobs progressively gain stronger stats and new abilities the longer the world exists, with configurable scaling.
- **Why:** Adds natural difficulty escalation — the world gets harder as players progress, without gates or quest requirements. Keeps combat challenging in late-game.
- **Dependencies:** None
- **Conflicts:** None known
