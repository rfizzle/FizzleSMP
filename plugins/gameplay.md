# Core Gameplay & Content

<!-- Mods that add or change core gameplay mechanics, items, blocks, mobs, or progression. -->

## RightClickHarvest
- **CurseForge ID:** 452834
- **Slug:** rightclickharvest
- **Mod Loader:** Fabric
- **Summary:** Allows harvesting and replanting crops with a simple right-click.
- **Why:** Essential farming QoL — lets players harvest mature crops with right-click, automatically replanting them.
- **Dependencies:** Fabric API, Architectury API, JamLib
- **Conflicts:** None known

## Architectury API
- **CurseForge ID:** 419699
- **Slug:** architectury-api
- **Mod Loader:** Fabric
- **Summary:** An intermediary API for developing multi-platform Minecraft mods.
- **Why:** Required dependency of RightClickHarvest.
- **Dependencies:** None
- **Conflicts:** None known

## JamLib
- **CurseForge ID:** 623764
- **Slug:** jamlib
- **Mod Loader:** Fabric
- **Summary:** A library mod providing shared utilities for JamCoreModding mods.
- **Why:** Required dependency of RightClickHarvest.
- **Dependencies:** None
- **Conflicts:** None known

## Lootr
- **CurseForge ID:** 615106
- **Slug:** lootr-fabric
- **Mod Loader:** Fabric
- **Summary:** Makes loot chests instanced per player so every player gets their own loot from the same container.
- **Why:** Ensures all SMP players get fair loot from structures without racing, and discourages generating new chunks just for loot.
- **Dependencies:** Fabric API, Cloth Config API
- **Conflicts:** None known; required on both client and server

## Traveler's Backpack
- **CurseForge ID:** 541171
- **Slug:** travelers-backpack-fabric
- **Mod Loader:** Fabric
- **Summary:** Adds unique, upgradeable backpacks with tiers, fluid tanks, tool slots, crafting upgrades, and 45+ custom designs.
- **Why:** Provides portable storage and crafting for exploration-heavy SMP gameplay, with tiered progression from leather to netherite.
- **Dependencies:** Fabric API, Cloth Config API, Cardinal Components API
- **Conflicts:** None known

## Cardinal Components API
- **CurseForge ID:** 318449
- **Slug:** cardinal-components-api
- **Mod Loader:** Fabric
- **Summary:** A data attachment API for attaching custom data to game objects like entities, chunks, and worlds.
- **Why:** Required dependency of Traveler's Backpack.
- **Dependencies:** None
- **Conflicts:** None known

## Reforged (formerly TieredZ)
- **CurseForge ID:** 453889
- **Slug:** tiered-forge
- **Mod Loader:** Fabric
- **Summary:** Adds random quality modifiers to all tools, weapons, and armor with stat bonuses and abilities, plus a rerolling system via villager-sold hammers.
- **Why:** Adds RPG-style item quality tiers to every crafted piece of gear, making loot more exciting and encouraging trading on the SMP.
- **Dependencies:** UnionLib
- **Conflicts:** None known; modifiers apply to weapons from Simply Swords and gear from Mythic Upgrades (desirable interaction).

## UnionLib
- **CurseForge ID:** 367806
- **Slug:** unionlib
- **Mod Loader:** Fabric
- **Summary:** A shared library mod providing common utilities for StereoWalker's mods.
- **Why:** Required dependency of Reforged (formerly TieredZ).
- **Dependencies:** None
- **Conflicts:** None known

## Artifacts
- **CurseForge ID:** 401236
- **Slug:** artifacts-fabric
- **Mod Loader:** Fabric
- **Summary:** Adds powerful, uncraftable accessory items found in structure chests, archaeology, or dropped by mimics in underground campsites.
- **Why:** Makes exploration more rewarding with unique collectible items in accessory slots; uses Accessories (via Compat Layer) for slot management.
- **Dependencies:** None required; Accessories Compatibility Layer (optional, for accessory slots), Cloth Config API (optional)
- **Conflicts:** None known

## Accessories
- **CurseForge ID:** 938917
- **Slug:** accessories
- **Mod Loader:** Fabric
- **Summary:** A data-driven accessory mod providing an extendable accessory slot system, inspired by Trinkets and Curios with a cross-platform API.
- **Why:** Replaces Trinkets as the accessory slot framework; required by Things and compatible with Spell Engine. Used with Accessories Compatibility Layer for Trinkets API backwards compatibility.
- **Dependencies:** None
- **Conflicts:** Do not install alongside Trinkets directly; use Accessories Compatibility Layer to bridge Trinkets-dependent mods.

## Accessories Compatibility Layer
- **CurseForge ID:** 1315611
- **Slug:** accessories-compat-layer
- **Mod Loader:** Fabric
- **Summary:** Wraps the Trinkets and Curios APIs to work under Accessories, unifying accessory management under one framework.
- **Why:** Enables Trinkets-dependent mods (Artifacts, Spectrum) to work with Accessories without code changes.
- **Dependencies:** Accessories
- **Conflicts:** None known; replaces Trinkets mod (do not install Trinkets alongside this).

## Mythic Upgrades
- **CurseForge ID:** 663567
- **Slug:** mythic-upgrades
- **Mod Loader:** Fabric
- **Summary:** Adds new ores (Jade, Topaz, Aquamarine, Sapphire, Ruby, Peridot, Zircon, Citrine, Necoium, Ametrine) with craftable ingots and gear upgrades beyond netherite.
- **Why:** Extends endgame progression with new materials and gear tiers, giving SMP players more goals beyond netherite.
- **Dependencies:** Fabric API, owo-lib
- **Conflicts:** None known

## Mythic Compat
- **CurseForge ID:** 1014057
- **Slug:** mythic-lib
- **Mod Loader:** Fabric
- **Summary:** Adds cross-upgrade recipes for Mythic Upgrades, allowing gear to be upgraded between material tiers.
- **Why:** Enables upgrading between Mythic Upgrades material tiers (e.g., Topaz chestplate to Ametrine chestplate) for smoother progression.
- **Dependencies:** Fabric API, Mythic Upgrades
- **Conflicts:** None known

## owo-lib
- **CurseForge ID:** 532610
- **Slug:** owo-lib
- **Mod Loader:** Fabric
- **Summary:** A general utility, GUI, and configuration library for Fabric mods.
- **Why:** Required dependency of Mythic Upgrades.
- **Dependencies:** None
- **Conflicts:** None known

## Enchanting Infuser
- **CurseForge ID:** 551151
- **Slug:** enchanting-infuser
- **Mod Loader:** Fabric
- **Summary:** Adds a new enchanting table block that lets players choose specific enchantments for fair, configurable prices.
- **Why:** Removes enchanting randomness, letting SMP players pick exactly which enchantments they want instead of gambling at vanilla tables.
- **Dependencies:** Fabric API, Puzzles Lib, Forge Config API Port
- **Conflicts:** None known; complementary with Easy Magic (same author, different blocks).

## Easy Magic
- **CurseForge ID:** 456239
- **Slug:** easy-magic
- **Mod Loader:** Fabric
- **Summary:** Improves the vanilla enchanting table by keeping items in place after closing and enabling easy enchantment re-rolls.
- **Why:** QoL improvement for vanilla enchanting — items stay in the table when you close the GUI and re-rolling enchantments is simplified.
- **Dependencies:** Fabric API, Puzzles Lib, Forge Config API Port
- **Conflicts:** None known; complementary with Enchanting Infuser (same author, different targets).

## Easy Anvils
- **CurseForge ID:** 682567
- **Slug:** easy-anvils
- **Mod Loader:** Fabric
- **Summary:** Overhauls anvils with items staying after closing, removal of the "too expensive" cap, better name tags, and many configurable tweaks.
- **Why:** Removes the frustrating "too expensive" anvil limit and adds QoL improvements, keeping anvils useful throughout endgame on the SMP.
- **Dependencies:** Fabric API, Puzzles Lib, Forge Config API Port
- **Conflicts:** None known; complementary with Mendable Anvils (Easy Anvils tweaks mechanics, Mendable Anvils adds repair).

## Mendable Anvils
- **CurseForge ID:** 648497
- **Slug:** mendable-anvils
- **Mod Loader:** Fabric
- **Summary:** Allows players to repair damaged anvils by shift + right-clicking with an iron ingot.
- **Why:** Prevents anvil waste on the SMP — players can maintain their anvils instead of constantly crafting new ones.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Spectrum
- **CurseForge ID:** 556967
- **Slug:** spectrum
- **Mod Loader:** Fabric
- **Summary:** A progression and exploration-based magic mod where you combine colors to create magical tools, machines, and equipment, with the goal of breaking through bedrock.
- **Why:** Adds a deep, puzzle-driven magic progression system that rewards exploration and experimentation — perfect for long-term SMP engagement.
- **Dependencies:** Fabric API, Cloth Config API, Trinkets (via Accessories Compat Layer), Revelationary, Modonomicon
- **Conflicts:** None known

## Revelationary
- **CurseForge ID:** 656526
- **Slug:** revelationary
- **Mod Loader:** Fabric
- **Summary:** A data-driven block and item revelation system that gates discovery of new blocks via advancements.
- **Why:** Required dependency of Spectrum for its progressive discovery system.
- **Dependencies:** None
- **Conflicts:** None known

## Modonomicon
- **CurseForge ID:** 538392
- **Slug:** modonomicon
- **Mod Loader:** Fabric
- **Summary:** A data-driven in-game documentation mod with quest/advancement-style navigation, inspired by Thaumcraft's Thaumonomicon and Patchouli.
- **Why:** Required dependency of Spectrum for its in-game guidebook.
- **Dependencies:** None
- **Conflicts:** None known

## Things
- **CurseForge ID:** 456151
- **Slug:** things-fabric
- **Mod Loader:** Fabric
- **Summary:** A collection of trinkets and utility items including a Displacement Tome (teleporter), Bater Wucket (infinite water), Ender Pouch (portable ender chest), and many accessory trinkets.
- **Why:** Adds a variety of useful, fun trinket items and utilities that enhance casual vanilla+ gameplay on the SMP.
- **Dependencies:** owo-lib, Lavender, Accessories
- **Conflicts:** None known; uses Accessories API for trinket slots.

## Pickable Villagers
- **CurseForge ID:** 636067
- **Slug:** pickable-villagers
- **Mod Loader:** Fabric
- **Summary:** Pick up villagers as items that remember their job, level, trades, and name, then place them wherever you want.
- **Why:** Makes relocating villagers painless on the SMP — no more minecart/boat pushing across long distances.
- **Dependencies:** Fabric API, Cloth Config API, Architectury API, Alexandria Lib
- **Conflicts:** None known

## Alexandria Lib
- **CurseForge ID:** 992905
- **Slug:** alexandria
- **Mod Loader:** Fabric
- **Summary:** A shared library mod providing common utilities for Fabric mods.
- **Why:** Required dependency of Pickable Villagers.
- **Dependencies:** None
- **Conflicts:** None known

## Villager Names
- **CurseForge ID:** 345854
- **Slug:** villager-names
- **Mod Loader:** Fabric
- **Summary:** Gives all villager entities a random name from a pool of 5000+ names, with profession shown on the trade screen.
- **Why:** Adds personality and immersion to villages on the SMP — every villager gets a unique name.
- **Dependencies:** Collective
- **Conflicts:** None known

## Collective
- **CurseForge ID:** 342584
- **Slug:** collective
- **Mod Loader:** Fabric
- **Summary:** A shared library mod providing common utilities for Serilum's Fabric mods.
- **Why:** Required dependency of Villager Names.
- **Dependencies:** None
- **Conflicts:** None known

## Trade Cycling
- **CurseForge ID:** 570431
- **Slug:** trade-cycling
- **Mod Loader:** Fabric
- **Summary:** Adds a button to the villager trade screen to refresh available trades without breaking and replacing workstations.
- **Why:** Eliminates tedious workstation-breaking when cycling for desired villager trades on the SMP.
- **Dependencies:** None
- **Conflicts:** None known

## Tech Reborn
- **CurseForge ID:** 233564
- **Slug:** techreborn
- **Mod Loader:** Fabric
- **Summary:** A standalone tech mod with tools and machines to gather resources, process materials, and progress through a full tech tree inspired by GregTech and IndustrialCraft 2.
- **Why:** Adds deep industrial tech progression with ore processing, power generation, and automation to the SMP.
- **Dependencies:** Fabric API, Reborn Core
- **Conflicts:** None known; explicit cross-mod compatibility with Oritech.

## Reborn Core
- **CurseForge ID:** 237903
- **Slug:** reborncore
- **Mod Loader:** Fabric
- **Summary:** A library mod providing shared utilities for Tech Reborn and related mods.
- **Why:** Required dependency of Tech Reborn.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Oritech
- **CurseForge ID:** 1030830
- **Slug:** oritech
- **Mod Loader:** Fabric
- **Summary:** A technology-focused mod with animated multiblock machinery, diverse ore processing chains, energy systems, pipes, drones, lasers, and cybernetic augmentation.
- **Why:** Adds modern, well-designed tech content with animated machines, multiple processing paths, and extensive automation to the SMP.
- **Dependencies:** owo-lib, GeckoLib, Architectury API, Athena
- **Conflicts:** None known; has built-in Tech Reborn compatibility for cross-mod recipes.

## Athena
- **CurseForge ID:** 841890
- **Slug:** athena
- **Mod Loader:** Fabric
- **Summary:** A cross-platform connected block textures library for Fabric and Forge.
- **Why:** Required dependency of Oritech.
- **Dependencies:** None
- **Conflicts:** None known

## Chipped
- **CurseForge ID:** 456956
- **Slug:** chipped
- **Mod Loader:** Fabric
- **Summary:** Adds thousands of decorative block variants accessible through specialized workbenches (mason, botanist, glassblower, etc.).
- **Why:** Massively expands building options with thousands of block variants for creative SMP builders.
- **Dependencies:** Fabric API, Athena, Resourceful Lib
- **Conflicts:** None known

## Resourceful Lib
- **CurseForge ID:** 570073
- **Slug:** resourceful-lib
- **Mod Loader:** Fabric
- **Summary:** A shared library mod providing common utilities for Team Resourceful mods.
- **Why:** Required dependency of Chipped.
- **Dependencies:** None
- **Conflicts:** None known

## HT's TreeChop
- **CurseForge ID:** 421377
- **Slug:** treechop
- **Mod Loader:** Fabric
- **Summary:** Adds gradual tree chopping where trees are chopped block by block and can fall realistically.
- **Why:** Makes tree chopping more satisfying and prevents floating tree tops on the SMP.
- **Dependencies:** Forge Config API Port
- **Conflicts:** None known

## Magnum Torch
- **CurseForge ID:** 593981
- **Slug:** magnum-torch
- **Mod Loader:** Fabric
- **Summary:** Adds a powerful torch block that prevents hostile mob spawning in a configurable large area.
- **Why:** Lets players protect large areas from mob spawning without placing hundreds of torches.
- **Dependencies:** Fabric API, Forge Config API Port, Puzzles Lib
- **Conflicts:** None known

## Simple Conveyor Belts
- **CurseForge ID:** 1292980
- **Slug:** simple-conveyor-belts
- **Mod Loader:** Fabric
- **Summary:** Adds spline-based conveyor belts that transport items between inventories with flexible curves, bends, and filtering via chutes.
- **Why:** Provides clean, visual item logistics for the SMP — connects machines and storage with flexible conveyor belts.
- **Dependencies:** Architectury API, Fabric API
- **Conflicts:** None known; designed to work with any item storage mod, shader-compatible with Iris.

## Resourceful Config
- **CurseForge ID:** 714059
- **Slug:** resourceful-config
- **Mod Loader:** Fabric
- **Summary:** A configuration library for Minecraft mods by Team Resourceful.
- **Why:** Required dependency of Creeper Overhaul.
- **Dependencies:** None
- **Conflicts:** None known

## Supplementaries
- **CurseForge ID:** 412082
- **Slug:** supplementaries
- **Mod Loader:** Fabric
- **Summary:** Vanilla+ additions including jars, signposts, faucets, weather vanes, planters, sconces, and many decorative and functional blocks.
- **Why:** Adds a wide variety of useful and decorative blocks that feel vanilla-appropriate, enhancing building and automation options on the SMP.
- **Dependencies:** Fabric API, Moonlight Lib
- **Conflicts:** None known

## Moonlight Lib
- **CurseForge ID:** 499980
- **Slug:** selene
- **Mod Loader:** Fabric
- **Summary:** A dynamic data pack and registration library providing shared utilities for MehVahdJukaar's mods.
- **Why:** Required dependency of Supplementaries.
- **Dependencies:** None
- **Conflicts:** None known

## Bosses of Mass Destruction
- **CurseForge ID:** 438365
- **Slug:** bosses-of-mass-destruction
- **Mod Loader:** Fabric
- **Summary:** Adds challenging boss fights with unique mechanics and custom structures scattered throughout the world.
- **Why:** Adds endgame boss encounters that give players meaningful PvE challenges beyond the Ender Dragon and Wither on the SMP.
- **Dependencies:** Fabric API, Cloth Config API, GeckoLib, Fabric Language Kotlin, Cardinal Components API
- **Conflicts:** None known

## Illager Invasion
- **CurseForge ID:** 891324
- **Slug:** illager-invasion
- **Mod Loader:** Fabric
- **Summary:** Adds new illager mobs, structures, and raid content as a port of the Illager Expansion mod.
- **Why:** Expands the illager faction with new hostile mobs and encounters, making raids and exploration more varied on the SMP.
- **Dependencies:** Fabric API, Forge Config API Port, Puzzles Lib
- **Conflicts:** None known

## Creeper Overhaul
- **CurseForge ID:** 561625
- **Slug:** creeper-overhaul
- **Mod Loader:** Fabric
- **Summary:** Overhauls vanilla creepers with new biome-specific variants, each with unique textures and behaviors.
- **Why:** Adds visual variety and biome-appropriate creeper variants, making encounters more interesting across different biomes.
- **Dependencies:** GeckoLib, Resourceful Lib, Resourceful Config
- **Conflicts:** None known; compatible with Creeper Healing (different aspects — visual variants vs explosion repair).

## Critters and Companions
- **CurseForge ID:** 574913
- **Slug:** critters-and-companions
- **Mod Loader:** Fabric
- **Summary:** Adds new ambient and companion creatures to the world including ferrets, otters, red pandas, and more.
- **Why:** Populates the world with charming ambient creatures and tameable companions, adding life and variety to exploration.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Friends & Foes
- **CurseForge ID:** 551364
- **Slug:** friends-and-foes
- **Mod Loader:** Fabric
- **Summary:** Adds outvoted and forgotten mobs from Minecraft mob votes (Copper Golem, Moobloom, Iceologer, Glare, Rascal, and more) with vanilla-style implementations.
- **Why:** Brings beloved mob vote losers into the game with faithful, vanilla-feeling implementations that expand mob variety on the SMP.
- **Dependencies:** Fabric API, Resourceful Lib, YetAnotherConfigLib (YACL)
- **Conflicts:** None known

## Easy Disenchanting
- **CurseForge ID:** 1266689
- **Slug:** easy-disenchanting
- **Mod Loader:** Fabric
- **Summary:** Allows transferring enchantments from items onto books using a vanilla anvil, returning the disenchanted item.
- **Why:** Lets SMP players salvage enchantments from unwanted gear instead of losing them, complementing Easy Anvils' anvil improvements.
- **Dependencies:** Fabric API, TxniLib
- **Conflicts:** None known

## TxniLib
- **CurseForge ID:** 1104882
- **Slug:** txnilib
- **Mod Loader:** Fabric
- **Summary:** A shared library mod providing common utilities for Txni's mods.
- **Why:** Required dependency of Easy Disenchanting.
- **Dependencies:** None
- **Conflicts:** None known

## You're in Grave Danger
- **CurseForge ID:** 544912
- **Slug:** youre-in-grave-danger
- **Mod Loader:** Fabric
- **Summary:** Spawns a grave at the player's death location that stores their items for safe retrieval.
- **Why:** Prevents frustrating item loss on death — players can recover their gear from a grave instead of racing a despawn timer.
- **Dependencies:** Fabric API, Cloth Config API
- **Conflicts:** None known

## Farmer's Delight Refabricated
- **CurseForge ID:** 993166
- **Slug:** farmers-delight-refabricated
- **Mod Loader:** Fabric
- **Summary:** Modern Fabric port of the cooking and farming mod, adding new crops, cooking mechanics, and food items via cutting boards, stoves, and more.
- **Why:** Adds depth to farming and cooking with new crops, recipes, and food progression, encouraging players to explore cuisine beyond vanilla foods.
- **Dependencies:** Fabric API
- **Conflicts:** None known; verify RightClickHarvest supports Farmer's Delight custom crops.

## Steve's Realistic Sleep
- **CurseForge ID:** 616330
- **Slug:** stevesrealisticsleep
- **Mod Loader:** Fabric
- **Summary:** Makes sleeping speed up time instead of skipping instantly to day, with speed scaling by number of sleeping players.
- **Why:** Adds immersion to sleeping on the SMP — time accelerates gradually instead of jumping to morning, and more players sleeping speeds it up.
- **Dependencies:** Fabric API, Architectury API, Cloth Config API
- **Conflicts:** None known
