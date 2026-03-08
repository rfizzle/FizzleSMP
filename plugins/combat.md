# Combat & Balancing

<!-- Mods that overhaul combat mechanics, add weapons/armor, or rebalance difficulty. -->

## Simply Swords
- **CurseForge ID:** 659887
- **Slug:** simply-swords
- **Mod Loader:** Fabric
- **Summary:** Adds 14 unique weapon variants with different playstyles, plus rare unique weapons with special abilities found in loot chests.
- **Why:** Greatly expands weapon variety for SMP combat with throwable chakrams, twinblades, greataxes, and more — each with distinct attack patterns via Better Combat.
- **Dependencies:** Architectury API, Fzzy Config; Better Combat (optional, recommended)
- **Conflicts:** None known

## Better Combat
- **CurseForge ID:** 639842
- **Slug:** better-combat-by-daedelus
- **Mod Loader:** Fabric
- **Summary:** Overhauls melee combat with Minecraft Dungeons-style attack animations, accurate weapon collision, dual wielding, and weapon combos.
- **Why:** Makes combat visually spectacular and mechanically deeper with swing animations, hit detection by weapon arc, and combo systems.
- **Dependencies:** Fabric API, Cloth Config API, playerAnimator
- **Conflicts:** Incompatible with other dual wielding mods (none in pack). Partnered with Simply Swords for full weapon animation support.

## Combat Roll
- **CurseForge ID:** 678036
- **Slug:** combat-roll
- **Mod Loader:** Fabric
- **Summary:** Adds a dodge/roll ability with configurable cooldown, distance, and optional invulnerability frames, plus related enchantments.
- **Why:** Complements Better Combat with evasive movement, adding depth to PvE and PvP encounters on the SMP.
- **Dependencies:** Fabric API, Cloth Config API, playerAnimator
- **Conflicts:** None known; designed by same author as Better Combat to work together.

## Fzzy Config
- **CurseForge ID:** 1005914
- **Slug:** fzzy-config
- **Mod Loader:** Fabric
- **Summary:** A configuration library for Fabric mods with GUI support.
- **Why:** Required dependency of Simply Swords.
- **Dependencies:** None
- **Conflicts:** None known

## playerAnimator
- **CurseForge ID:** 658587
- **Slug:** playeranimator
- **Mod Loader:** Fabric
- **Summary:** An animation library enabling custom player model animations for Minecraft mods.
- **Why:** Required dependency of Better Combat, Combat Roll, and Spell Engine.
- **Dependencies:** None
- **Conflicts:** None known

## Spell Power Attributes
- **CurseForge ID:** 771265
- **Slug:** spell-power
- **Mod Loader:** Fabric
- **Summary:** Spell power entity attributes with related status effects and enchantments, providing an API for spell damage calculations, critical strikes, and more.
- **Why:** Core attribute system for the RPG Series magic mods; required dependency of Spell Engine.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Spell Engine
- **CurseForge ID:** 807653
- **Slug:** spell-engine
- **Mod Loader:** Fabric
- **Summary:** Data-driven magic library providing a complete spell-casting system including spell books, visual effects, and weapon integration.
- **Why:** Core library powering the RPG Series magic mods (Wizards, Paladins & Priests); works alongside Better Combat for weapon animations.
- **Dependencies:** Fabric API, Cloth Config API, playerAnimator, Spell Power Attributes, Trinkets or Accessories
- **Conflicts:** None known

## Runes
- **CurseForge ID:** 775518
- **Slug:** rune-crafting
- **Mod Loader:** Fabric
- **Summary:** Adds craftable runes that serve as ammunition for casting spells, with a Rune Crafting Altar for efficient production.
- **Why:** Provides the ammo system for Spell Engine spells; required by Wizards and Paladins & Priests.
- **Dependencies:** Fabric API, Bundle API
- **Conflicts:** None known

## Bundle API
- **CurseForge ID:** 1189983
- **Slug:** bundle-api
- **Mod Loader:** Fabric
- **Summary:** API allowing mods to easily add bundles that can hold more than 1 stack of items specified by an item tag.
- **Why:** Required dependency of Runes.
- **Dependencies:** None
- **Conflicts:** None known

## Wizards (RPG Series)
- **CurseForge ID:** 734325
- **Slug:** wizards
- **Mod Loader:** Fabric
- **Summary:** Adds RPG-style wizard classes with Arcane, Fire, and Frost magic skill sets, spell books, wands, staves, and Wizard Towers in villages.
- **Why:** Adds deep magic combat classes to the SMP, complementing the existing melee combat from Better Combat and Simply Swords.
- **Dependencies:** Spell Engine, Runes, AzureLib Armor, Structure Pool API, Fabric API
- **Conflicts:** None known; strongly recommended alongside Better Combat for proper weapon animations.

## Paladins & Priests (RPG Series)
- **CurseForge ID:** 856548
- **Slug:** paladins-and-priests
- **Mod Loader:** Fabric
- **Summary:** Adds Paladin and Priest RPG classes with healing, protection, and holy damage spells, plus Sanctuaries in villages with Monk villagers.
- **Why:** Adds support/healer classes to complement Wizards' DPS classes, enabling party-based gameplay on the SMP.
- **Dependencies:** Spell Engine, Runes, AzureLib Armor, Structure Pool API, Fabric API
- **Conflicts:** None known; strongly recommended alongside Better Combat for first-person animations.

## AzureLib Armor
- **CurseForge ID:** 912767
- **Slug:** azurelib-armor
- **Mod Loader:** Fabric
- **Summary:** A stripped-down AzureLib providing custom armor model and item rendering with reduced memory usage.
- **Why:** Required dependency of Wizards and Paladins & Priests for animated armor rendering.
- **Dependencies:** None
- **Conflicts:** None known

## Structure Pool API
- **CurseForge ID:** 927915
- **Slug:** structure-pool-api
- **Mod Loader:** Fabric
- **Summary:** API to inject structures into vanilla structure pools, enabling mods to add buildings to villages and other generated structures.
- **Why:** Required dependency of Wizards and Paladins & Priests for adding towers and sanctuaries to villages.
- **Dependencies:** None
- **Conflicts:** None known

## SwingThrough
- **CurseForge ID:** 1402841
- **Slug:** swingthrough
- **Mod Loader:** Fabric
- **Summary:** Allows targeting, interacting with, and attacking living entities through transparent blocks like grass, crops, and flowers.
- **Why:** Eliminates the frustration of grass blocking attacks during combat, especially important with Better Combat's directional swings.
- **Dependencies:** None
- **Conflicts:** None known
