# Core Gameplay & Content

<!-- Mods that add or change core gameplay mechanics, items, blocks, mobs, or progression. -->

## RightClickHarvest
- **CurseForge ID:** 452834
- **Slug:** rightclickharvest
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Allows harvesting and replanting crops with a simple right-click.
- **Why:** Essential farming QoL — lets players harvest mature crops with right-click, automatically replanting them.
- **Dependencies:** Fabric API, Architectury API, JamLib
- **Conflicts:** None known

## Architectury API
- **CurseForge ID:** 419699
- **Slug:** architectury-api
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** An intermediary API for developing multi-platform Minecraft mods.
- **Why:** Required dependency of RightClickHarvest.
- **Dependencies:** None
- **Conflicts:** None known

## JamLib
- **CurseForge ID:** 623764
- **Slug:** jamlib
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A library mod providing shared utilities for JamCoreModding mods.
- **Why:** Required dependency of RightClickHarvest.
- **Dependencies:** None
- **Conflicts:** None known

## Cardinal Components API
- **CurseForge ID:** 318449
- **Slug:** cardinal-components-api
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A data attachment API for attaching custom data to game objects like entities, chunks, and worlds.
- **Why:** Required dependency of Bosses of Mass Destruction.
- **Dependencies:** None
- **Conflicts:** None known

## Trinkets
- **CurseForge ID:** 341284
- **Slug:** trinkets
- **Modrinth Slug:** trinkets
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Provides an accessory/trinket slot system for equipping items beyond standard armor slots.
- **Why:** Provides the accessory slot system required by Spell Engine, the shared library behind the RPG Series magic mods (Wizards, Paladins & Priests). The lightest of the available slot libraries.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## owo-lib
- **CurseForge ID:** 532610
- **Slug:** owo-lib
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A general utility, GUI, and configuration library for Fabric mods.
- **Why:** Required dependency of Oritech.
- **Dependencies:** None
- **Conflicts:** None known

## Collective
- **CurseForge ID:** 342584
- **Slug:** collective
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A shared library mod providing common utilities for Serilum's Fabric mods.
- **Why:** Required dependency of Double Doors and Stack Refill.
- **Dependencies:** None
- **Conflicts:** None known

## Tech Reborn
- **CurseForge ID:** 233564
- **Slug:** techreborn
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A standalone tech mod with tools and machines to gather resources, process materials, and progress through a full tech tree inspired by GregTech and IndustrialCraft 2.
- **Why:** Adds deep industrial tech progression with ore processing, power generation, and automation to the SMP.
- **Dependencies:** Fabric API, Reborn Core
- **Conflicts:** None known; explicit cross-mod compatibility with Oritech.

## Reborn Core
- **CurseForge ID:** 237903
- **Slug:** reborncore
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A library mod providing shared utilities for Tech Reborn and related mods.
- **Why:** Required dependency of Tech Reborn.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Oritech
- **CurseForge ID:** 1030830
- **Slug:** oritech
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A technology-focused mod with animated multiblock machinery, diverse ore processing chains, energy systems, pipes, drones, lasers, and cybernetic augmentation.
- **Why:** Adds modern, well-designed tech content with animated machines, multiple processing paths, and extensive automation to the SMP.
- **Dependencies:** owo-lib, GeckoLib, Architectury API, Athena
- **Conflicts:** None known; has built-in Tech Reborn compatibility for cross-mod recipes.

## Athena
- **CurseForge ID:** 841890
- **Slug:** athena
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A cross-platform connected block textures library for Fabric and Forge.
- **Why:** Required dependency of Oritech.
- **Dependencies:** None
- **Conflicts:** None known

## GeckoLib
- **CurseForge ID:** 388172
- **Slug:** geckolib
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** An animation and model library for Minecraft mods, enabling complex entity animations.
- **Why:** Required dependency of Oritech, Bosses of Mass Destruction, and Critters and Companions.
- **Dependencies:** None
- **Conflicts:** None known

## Chipped
- **CurseForge ID:** 456956
- **Slug:** chipped
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds thousands of decorative block variants accessible through specialized workbenches (mason, botanist, glassblower, etc.).
- **Why:** Massively expands building options with thousands of block variants for creative SMP builders.
- **Dependencies:** Fabric API, Athena, Resourceful Lib
- **Conflicts:** None known

## Resourceful Lib
- **CurseForge ID:** 570073
- **Slug:** resourceful-lib
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A shared library mod providing common utilities for Team Resourceful mods.
- **Why:** Required dependency of Chipped.
- **Dependencies:** None
- **Conflicts:** None known

## HT's TreeChop
- **CurseForge ID:** 421377
- **Slug:** treechop
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds gradual tree chopping where trees are chopped block by block and can fall realistically.
- **Why:** Makes tree chopping more satisfying and prevents floating tree tops on the SMP.
- **Dependencies:** Forge Config API Port
- **Conflicts:** None known

## Simple Conveyor Belts
- **CurseForge ID:** 1292980
- **Slug:** simple-conveyor-belts
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds spline-based conveyor belts that transport items between inventories with flexible curves, bends, and filtering via chutes.
- **Why:** Provides clean, visual item logistics for the SMP — connects machines and storage with flexible conveyor belts.
- **Dependencies:** Architectury API, Fabric API
- **Conflicts:** None known; designed to work with any item storage mod, shader-compatible with Iris.

## Refined Storage
- **CurseForge ID:** 243076
- **Slug:** refined-storage
- **Modrinth Slug:** refined-storage
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A mass storage mod offering a network-based digital storage system with importers, exporters, constructors, destructors, autocrafting, and a unified Grid interface across all connected devices.
- **Why:** Provides the endgame digital storage layer for the SMP's tech ecosystem — players can centralize all items/fluids from Tech Reborn and Oritech machines into a single searchable network with autocrafting.
- **Dependencies:** Fabric API
- **Conflicts:** None known; complementary with Tech Reborn and Oritech (industrial machinery + digital storage layer).

## Kibe
- **CurseForge ID:** 388832
- **Slug:** kibe
- **Modrinth Slug:** kibe
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A miscellaneous utility mod adding Entangled Chests/Tanks, Big Torch, Elevators, Vacuum Hoppers, Tanks, Placers/Breakers, Witherproof blocks, Slime Boots/Sling, Gliders, Rings, Lassos, Spikes, and many other vanilla+ utility items.
- **Why:** Adds a wide collection of QoL utility blocks and items (cross-dimensional storage, mob farming tools, redstone helpers) that complement the SMP's tech and survival gameplay.
- **Dependencies:** Fabric API
- **Conflicts:** Soft overlap with Simple Conveyor Belts (Kibe Conveyor Belts) and Steve's Realistic Sleep (Sleeping Bag). Item Obliterator is added alongside to disable Kibe items that conflict or break balance (e.g., Angel Ring's creative flight). See compatibility matrix.

## Resourceful Config
- **CurseForge ID:** 714059
- **Slug:** resourceful-config
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A configuration library for Minecraft mods by Team Resourceful.
- **Why:** Required dependency of Creeper Overhaul.
- **Dependencies:** None
- **Conflicts:** None known

## Moonlight Lib
- **CurseForge ID:** 499980
- **Slug:** selene
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A dynamic data pack and registration library providing shared utilities for MehVahdJukaar's mods.
- **Why:** Required dependency of Every Compat.
- **Dependencies:** None
- **Conflicts:** None known

## Bosses of Mass Destruction
- **CurseForge ID:** 438365
- **Slug:** bosses-of-mass-destruction
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds challenging boss fights with unique mechanics and custom structures scattered throughout the world.
- **Why:** Adds endgame boss encounters that give players meaningful PvE challenges beyond the Ender Dragon and Wither on the SMP.
- **Dependencies:** Fabric API, Cloth Config API, GeckoLib, Fabric Language Kotlin, Cardinal Components API
- **Conflicts:** None known

## Illager Invasion
- **CurseForge ID:** 891324
- **Slug:** illager-invasion
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds new illager mobs, structures, and raid content as a port of the Illager Expansion mod.
- **Why:** Expands the illager faction with new hostile mobs and encounters, making raids and exploration more varied on the SMP.
- **Dependencies:** Fabric API, Forge Config API Port, Puzzles Lib
- **Conflicts:** None known

## Creeper Overhaul
- **CurseForge ID:** 561625
- **Slug:** creeper-overhaul
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Overhauls vanilla creepers with new biome-specific variants, each with unique textures and behaviors.
- **Why:** Adds visual variety and biome-appropriate creeper variants, making encounters more interesting across different biomes.
- **Dependencies:** GeckoLib, Resourceful Lib, Resourceful Config
- **Conflicts:** None known.

## Critters and Companions
- **CurseForge ID:** 574913
- **Slug:** critters-and-companions
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds new ambient and companion creatures to the world including ferrets, otters, red pandas, and more.
- **Why:** Populates the world with charming ambient creatures and tameable companions, adding life and variety to exploration.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Friends & Foes
- **CurseForge ID:** 551364
- **Slug:** friends-and-foes
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds outvoted and forgotten mobs from Minecraft mob votes (Copper Golem, Moobloom, Iceologer, Glare, Rascal, and more) with vanilla-style implementations.
- **Why:** Brings beloved mob vote losers into the game with faithful, vanilla-feeling implementations that expand mob variety on the SMP.
- **Dependencies:** Fabric API, Resourceful Lib, YetAnotherConfigLib (YACL)
- **Conflicts:** None known

## Farmer's Delight Refabricated
- **CurseForge ID:** 993166
- **Slug:** farmers-delight-refabricated
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Modern Fabric port of the cooking and farming mod, adding new crops, cooking mechanics, and food items via cutting boards, stoves, and more.
- **Why:** Adds depth to farming and cooking with new crops, recipes, and food progression, encouraging players to explore cuisine beyond vanilla foods.
- **Dependencies:** Fabric API
- **Conflicts:** None known; verify RightClickHarvest supports Farmer's Delight custom crops.

## Chef's Delight
- **CurseForge ID:** 736986
- **Slug:** chefs-delight-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds Cook and Chef villager professions with food-related trades, plus custom village structures where they spawn naturally.
- **Why:** Extends Farmer's Delight with new villager professions and village structures, making food trading a natural part of SMP village economies.
- **Dependencies:** Farmer's Delight Refabricated
- **Conflicts:** None known

## Steve's Realistic Sleep
- **CurseForge ID:** 616330
- **Slug:** stevesrealisticsleep
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Makes sleeping speed up time instead of skipping instantly to day, with speed scaling by number of sleeping players.
- **Why:** Adds immersion to sleeping on the SMP — time accelerates gradually instead of jumping to morning, and more players sleeping speeds it up.
- **Dependencies:** Fabric API, Architectury API, Cloth Config API
- **Conflicts:** None known

## Handcrafted
- **CurseForge ID:** 538214
- **Slug:** handcrafted
- **Modrinth Slug:** handcrafted
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a variety of furniture and decoration blocks including chairs, tables, couches, shelves, lamps, and more with wood-type variants.
- **Why:** Fills a major gap in building options by adding high-quality furniture for player homes, taverns, shops, and community builds on the SMP.
- **Dependencies:** Fabric API, Resourceful Lib
- **Conflicts:** None known

## Beautify: Refabricated
- **CurseForge ID:** 809311
- **Slug:** beautify-refabricated
- **Modrinth Slug:** beautify-refabricated
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds decorative blocks including hanging planters, blinds, lamps, shelves, trellis, and rope for interior and exterior decoration.
- **Why:** Complements Handcrafted with additional decorative blocks focused on planters, lighting, and window treatments.
- **Dependencies:** None required; Jade (optional, in pack)
- **Conflicts:** None known

## Every Compat
- **CurseForge ID:** 628539
- **Slug:** every-compat
- **Modrinth Slug:** every-compat
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Automatically generates wood-type variants (stairs, slabs, fences, bookshelves, chests, furniture) for all modded wood types across compatible mods (e.g. Handcrafted, Chipped).
- **Why:** Bridges the gap between wood-adding mods and furniture/block mods so all modded wood types get proper variants for Handcrafted, Chipped, etc.
- **Dependencies:** Fabric API, Moonlight Lib
- **Conflicts:** None known

## Fabric Seasons
- **CurseForge ID:** 413523
- **Slug:** fabric-seasons
- **Modrinth Slug:** fabric-seasons
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a four-season cycle that changes foliage colors, crop growth rates, weather patterns, and mob spawning behavior throughout the year.
- **Why:** Adds dynamic seasonal changes to the SMP world, making the passage of time meaningful and creating visual variety across the year.
- **Dependencies:** Fabric API
- **Conflicts:** Requires Fabric Seasons: Terralith Compat when used with Terralith; requires Fabric Seasons: Delight Refabricated Compat for Farmer's Delight crop integration.

## Fabric Seasons: Terralith Compat
- **CurseForge ID:** 839881
- **Slug:** fabric-seasons-terralith-compat
- **Modrinth Slug:** fabric-seasons-terralith-compat
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Compatibility patch that applies Fabric Seasons foliage color changes to Terralith's custom biomes.
- **Why:** Required compatibility mod to make Fabric Seasons' seasonal color changes work correctly with Terralith's ~100 custom biomes.
- **Dependencies:** Fabric Seasons, Terralith
- **Conflicts:** None — this mod resolves the Fabric Seasons/Terralith incompatibility.

## Fabric Seasons: Extras
- **CurseForge ID:** 839878
- **Slug:** fabric-seasons-extras
- **Modrinth Slug:** fabric-seasons-extras
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds greenhouse blocks (glass, heater, chiller), a Season Detector (redstone output by season), and a Season Calendar to enhance Fabric Seasons gameplay.
- **Why:** Fabric Seasons alone has no items or blocks — this addon adds functional greenhouse mechanics for crop control and utility blocks for season awareness.
- **Dependencies:** Fabric API, Fabric Seasons
- **Conflicts:** None known

## Fabric Seasons: Delight Refabricated Compat
- **CurseForge ID:** N/A
- **Slug:** N/A
- **Modrinth Slug:** seasons-delight-refab-compat
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Compatibility patch that integrates Farmer's Delight Refabricated crops with Fabric Seasons' seasonal growth mechanics, fixing rice and tomato support.
- **Why:** Replaces the original Delight Compat whose mixins targeted wrong class names for FD Refabricated. This fork correctly maps FD Refabricated's crop classes so seasonal growth rates actually apply.
- **Dependencies:** Fabric Seasons, Farmer's Delight Refabricated
- **Conflicts:** Replaces Fabric Seasons: Delight Compat (removed — its mixins were incompatible with FD Refabricated).

## Dark Utilities
- **CurseForge ID:** 242195
- **Slug:** dark-utilities
- **Modrinth Slug:** dark-utilities
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Expansive content mod used here primarily for its vector plates — flat rotatable blocks that push entities in a direction at four speed tiers.
- **Why:** Vector plates are the best-in-class solution for pushing mobs and items in mob farms and automation builds. All other Dark Utilities content (damage/effect plates, mob filters, charms, runes, redstone blocks) is disabled via Item Obliterator.
- **Dependencies:** Fabric API, Bookshelf, Prickle, Pig Pen Cipher, Nyctography, Runelic
- **Conflicts:** Soft overlap with Simple Conveyor Belts (vector plates provide similar entity/item transport). Most content disabled via Item Obliterator to reduce feature bloat.

## Meridian — Enchanting Overhaul
- **CurseForge ID:** 1546092
- **Slug:** meridian-enchanting-overhaul
- **Modrinth Slug:** meridian-enchanting-overhaul
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A complete enchanting overhaul — replaces the vanilla enchanting table with a five-stat system (Eterna, Quanta, Arcana, Rectification, Clues), 25+ themed shelf blocks, a two-tier enchantment library, salvage tomes, anvil upgrades, and 75 original enchantments.
- **Why:** Single source of truth for enchanting on the SMP — replaces Enchanting Infuser, Easy Magic, Grind Enchantments, and NeoEnchant+ with one cohesive, data-driven system. Part of the Concord suite.
- **Dependencies:** Fabric API
- **Conflicts:** None known. Replaces the vanilla enchanting table — do not run alongside other enchanting-table, disenchant, or level-cap overhauls.

## Mercantile — Villager & Trade Overhaul
- **CurseForge ID:** 1591251
- **Slug:** mercantile-villager-overhaul
- **Modrinth Slug:** mercantile-villager-overhaul
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A villager & trade overhaul — villager pickup with full NBT, biome-themed names shown above villagers, emerald-based trade cycling, a five-tier reputation/gossip system, an iron-fueled sentry pylon that spawns guard golems, and a reputation HUD.
- **Why:** Single source of truth for villagers and trading — replaces Pickable Villagers, Villager Names, and Trade Cycling. Part of the Concord suite.
- **Dependencies:** Fabric API
- **Conflicts:** None known. Do not run alongside other villager-name, villager-pickup, or trade-reroll mods.

## Prosperity — Loot Overhaul
- **CurseForge ID:** 1591262
- **Slug:** prosperity-loot-overhaul
- **Modrinth Slug:** prosperity-loot-overhaul
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A loot overhaul — per-player instanced loot for all naturally-generated containers, gold sparkle indicators on unopened containers, distance-based loot and mob-drop scaling, a loot-modifier API, datapack loot injection, structure-specific scaling, and a Jade/EMI loot index.
- **Why:** Single source of truth for loot — replaces Lootr (per-player containers) and the entire Loot Integrations family (loot-modifier API + datapack injection). Part of the Concord suite.
- **Dependencies:** Fabric API
- **Conflicts:** None known. Do not run alongside Lootr or Loot Integrations.

## Better Loot
- **CurseForge ID:** 906394
- **Slug:** better-loot
- **Modrinth Slug:** N/A
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Completely overhauls vanilla loot tables to make them more rewarding.
- **Why:** Enriches the underlying vanilla loot tables across the world. Compatible with Prosperity, which handles per-player instancing and distance scaling on top of whatever the tables roll.
- **Dependencies:** None
- **Conflicts:** None known. Works alongside Prosperity — Better Loot changes table contents, Prosperity handles per-player access and scaling. Savanna grass-drop edge case patched via BetterLoot-Savanna-Grass-Fix.zip (Paxi datapack).
