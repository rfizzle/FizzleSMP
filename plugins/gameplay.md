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

## Lootr
- **CurseForge ID:** 615106
- **Slug:** lootr-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Makes loot chests instanced per player so every player gets their own loot from the same container.
- **Why:** Ensures all SMP players get fair loot from structures without racing, and discourages generating new chunks just for loot.
- **Dependencies:** Fabric API, Cloth Config API
- **Conflicts:** None known; required on both client and server

## Traveler's Backpack
- **CurseForge ID:** 541171
- **Slug:** travelers-backpack-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds unique, upgradeable backpacks with tiers, fluid tanks, tool slots, crafting upgrades, and 45+ custom designs.
- **Why:** Provides portable storage and crafting for exploration-heavy SMP gameplay, with tiered progression from leather to netherite.
- **Dependencies:** Fabric API, Cloth Config API, Cardinal Components API
- **Conflicts:** None known

## Cardinal Components API
- **CurseForge ID:** 318449
- **Slug:** cardinal-components-api
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A data attachment API for attaching custom data to game objects like entities, chunks, and worlds.
- **Why:** Required dependency of Traveler's Backpack.
- **Dependencies:** None
- **Conflicts:** None known

## Artifacts
- **CurseForge ID:** 401236
- **Slug:** artifacts-fabric
- **Modrinth Slug:** artifacts
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds powerful, uncraftable accessory items found in structure chests, archaeology, or dropped by mimics in underground campsites.
- **Why:** Makes exploration more rewarding with unique collectible items in accessory slots; uses Accessories (via Compat Layer) for slot management.
- **Dependencies:** None required; Accessories Compatibility Layer (optional, for accessory slots), Cloth Config API (optional)
- **Conflicts:** None known

## Trinkets
- **CurseForge ID:** 341284
- **Slug:** trinkets
- **Modrinth Slug:** trinkets
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Provides an accessory/trinket slot system for equipping items beyond standard armor slots.
- **Why:** Required dependency of Spectrum; works alongside Accessories via the Accessories Compatibility Layer.
- **Dependencies:** Fabric API
- **Conflicts:** None known; requires Accessories Compatibility Layer when used with Accessories.

## Accessories
- **CurseForge ID:** 938917
- **Slug:** accessories
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A data-driven accessory mod providing an extendable accessory slot system, inspired by Trinkets and Curios with a cross-platform API.
- **Why:** Primary accessory slot framework; required by Things and compatible with Spell Engine. Used with Accessories Compatibility Layer to bridge Trinkets-dependent mods.
- **Dependencies:** None
- **Conflicts:** None known; use Accessories Compatibility Layer to bridge Trinkets alongside.

## Accessories Compatibility Layer
- **CurseForge ID:** 1315611
- **Slug:** accessories-compat-layer
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Wraps the Trinkets and Curios APIs to work under Accessories, unifying accessory management under one framework.
- **Why:** Enables Trinkets-dependent mods (Artifacts, Spectrum) to work with Accessories without code changes.
- **Dependencies:** Accessories, Trinkets (>= 3.10.0)
- **Conflicts:** Breaks with Trinkets versions below 3.10.0.

## Mythic Upgrades
- **CurseForge ID:** 663567
- **Slug:** mythic-upgrades
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds new ores (Jade, Topaz, Aquamarine, Sapphire, Ruby, Peridot, Zircon, Citrine, Necoium, Ametrine) with craftable ingots and gear upgrades beyond netherite.
- **Why:** Extends endgame progression with new materials and gear tiers, giving SMP players more goals beyond netherite.
- **Dependencies:** Fabric API, owo-lib, Better Loot
- **Conflicts:** Spectrum (soft — MU 4.x removed budding crystal blocks that Spectrum's integration expects; resolved via Spectrum-MythicUpgrades-Fix.zip datapack)

## Better Loot
- **CurseForge ID:** 906394
- **Slug:** better-loot
- **Modrinth Slug:** N/A
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Completely overhauls vanilla loot tables to make them more rewarding.
- **Why:** Required dependency of Mythic Upgrades.
- **Dependencies:** None
- **Conflicts:** None known

## owo-lib
- **CurseForge ID:** 532610
- **Slug:** owo-lib
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A general utility, GUI, and configuration library for Fabric mods.
- **Why:** Required dependency of Mythic Upgrades.
- **Dependencies:** None
- **Conflicts:** None known

## Enchanting Infuser
- **CurseForge ID:** 551151
- **Slug:** enchanting-infuser
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a new enchanting table block that lets players choose specific enchantments for fair, configurable prices.
- **Why:** Removes enchanting randomness, letting SMP players pick exactly which enchantments they want instead of gambling at vanilla tables.
- **Dependencies:** Fabric API, Puzzles Lib, Forge Config API Port
- **Conflicts:** None known; complementary with Easy Magic (same author, different blocks).

## Easy Magic
- **CurseForge ID:** 456239
- **Slug:** easy-magic
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Improves the vanilla enchanting table by keeping items in place after closing and enabling easy enchantment re-rolls.
- **Why:** QoL improvement for vanilla enchanting — items stay in the table when you close the GUI and re-rolling enchantments is simplified.
- **Dependencies:** Fabric API, Puzzles Lib, Forge Config API Port
- **Conflicts:** None known; complementary with Enchanting Infuser (same author, different targets).

## Easy Anvils
- **CurseForge ID:** 682567
- **Slug:** easy-anvils
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Overhauls anvils with items staying after closing, removal of the "too expensive" cap, better name tags, and many configurable tweaks.
- **Why:** Removes the frustrating "too expensive" anvil limit and adds QoL improvements, keeping anvils useful throughout endgame on the SMP.
- **Dependencies:** Fabric API, Puzzles Lib, Forge Config API Port
- **Conflicts:** None known

## Spectrum
- **CurseForge ID:** 556967
- **Slug:** spectrum
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A progression and exploration-based magic mod where you combine colors to create magical tools, machines, and equipment, with the goal of breaking through bedrock.
- **Why:** Adds a deep, puzzle-driven magic progression system that rewards exploration and experimentation — perfect for long-term SMP engagement.
- **Dependencies:** Fabric API, Cloth Config API, Trinkets (via Accessories Compat Layer), Revelationary, Modonomicon
- **Conflicts:** Mythic Upgrades (soft — bundled MU integration references removed registry IDs; resolved via Spectrum-MythicUpgrades-Fix.zip datapack), Tech Reborn (soft — resolved via datapack), BeyondEnchant (soft — resolved via datapack)

## Revelationary
- **CurseForge ID:** 656526
- **Slug:** revelationary
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A data-driven block and item revelation system that gates discovery of new blocks via advancements.
- **Why:** Required dependency of Spectrum for its progressive discovery system.
- **Dependencies:** None
- **Conflicts:** None known

## Modonomicon
- **CurseForge ID:** 538392
- **Slug:** modonomicon
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A data-driven in-game documentation mod with quest/advancement-style navigation, inspired by Thaumcraft's Thaumonomicon and Patchouli.
- **Why:** Required dependency of Spectrum for its in-game guidebook.
- **Dependencies:** None
- **Conflicts:** None known

## Things
- **CurseForge ID:** 456151
- **Slug:** things-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A collection of trinkets and utility items including a Displacement Tome (teleporter), Bater Wucket (infinite water), Ender Pouch (portable ender chest), and many accessory trinkets.
- **Why:** Adds a variety of useful, fun trinket items and utilities that enhance casual vanilla+ gameplay on the SMP.
- **Dependencies:** owo-lib, Lavender, Accessories
- **Conflicts:** None known; uses Accessories API for trinket slots.

## Pickable Villagers
- **CurseForge ID:** 636067
- **Slug:** pickable-villagers
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Pick up villagers as items that remember their job, level, trades, and name, then place them wherever you want.
- **Why:** Makes relocating villagers painless on the SMP — no more minecart/boat pushing across long distances.
- **Dependencies:** Fabric API, Cloth Config API, Architectury API, Alexandria Lib
- **Conflicts:** None known

## Alexandria Lib
- **CurseForge ID:** 992905
- **Slug:** alexandria
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A shared library mod providing common utilities for Fabric mods.
- **Why:** Required dependency of Pickable Villagers.
- **Dependencies:** None
- **Conflicts:** None known

## Villager Names
- **CurseForge ID:** 345854
- **Slug:** villager-names
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Gives all villager entities a random name from a pool of 5000+ names, with profession shown on the trade screen.
- **Why:** Adds personality and immersion to villages on the SMP — every villager gets a unique name.
- **Dependencies:** Collective
- **Conflicts:** None known

## Collective
- **CurseForge ID:** 342584
- **Slug:** collective
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A shared library mod providing common utilities for Serilum's Fabric mods.
- **Why:** Required dependency of Villager Names, Double Doors, and Stack Refill.
- **Dependencies:** None
- **Conflicts:** None known

## Trade Cycling
- **CurseForge ID:** 570431
- **Slug:** trade-cycling
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a button to the villager trade screen to refresh available trades without breaking and replacing workstations.
- **Why:** Eliminates tedious workstation-breaking when cycling for desired villager trades on the SMP.
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

## JustHammers
- **CurseForge ID:** 681606
- **Slug:** justhammers
- **Modrinth Slug:** just-hammers
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds tiered hammers (iron through netherite) that mine in increasingly large areas from 3x3 up to 5x5x5.
- **Why:** Gives players multi-block mining tools for large excavation projects, with a natural progression from iron (3x3) to netherite (5x5x5). Hammers never fully break (stop at 1 durability) and are repairable at anvils.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Magnum Torch
- **CurseForge ID:** 593981
- **Slug:** magnum-torch
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a powerful torch block that prevents hostile mob spawning in a configurable large area.
- **Why:** Lets players protect large areas from mob spawning without placing hundreds of torches.
- **Dependencies:** Fabric API, Forge Config API Port, Puzzles Lib
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
- **Conflicts:** Soft overlap with Magnum Torch (Big Torch), Simple Conveyor Belts (Kibe Conveyor Belts), Steve's Realistic Sleep (Sleeping Bag), and Things (Magnet). Item Obliterator is added alongside to disable Kibe items that conflict or break balance (e.g., Angel Ring's creative flight). See compatibility matrix.

## Resourceful Config
- **CurseForge ID:** 714059
- **Slug:** resourceful-config
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A configuration library for Minecraft mods by Team Resourceful.
- **Why:** Required dependency of Creeper Overhaul.
- **Dependencies:** None
- **Conflicts:** None known

## Supplementaries
- **CurseForge ID:** 412082
- **Slug:** supplementaries
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Vanilla+ additions including jars, signposts, faucets, weather vanes, planters, sconces, and many decorative and functional blocks.
- **Why:** Adds a wide variety of useful and decorative blocks that feel vanilla-appropriate, enhancing building and automation options on the SMP.
- **Dependencies:** Fabric API, Moonlight Lib
- **Conflicts:** Road sign auto-generation causes lag with CTOV (many village types make structure locate calls expensive). Disable road signs in `config/supplementaries-common.json`. See compatibility matrix.

## Moonlight Lib
- **CurseForge ID:** 499980
- **Slug:** selene
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A dynamic data pack and registration library providing shared utilities for MehVahdJukaar's mods.
- **Why:** Required dependency of Supplementaries.
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
- **Conflicts:** None known; compatible with Creeper Healing (different aspects — visual variants vs explosion repair).

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

## NeoEnchant+
- **CurseForge ID:** 1135663
- **Slug:** neoenchant
- **Modrinth Slug:** neoenchant
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a suite of new enchantments (Fury, Life+, Bright Vision, Builder Arms, Rebound, and more) using Minecraft's Data Driven Enchant system.
- **Why:** Expands the enchantment pool with creative new options that add depth to gear customization and character builds on the SMP.
- **Dependencies:** None required; BeyondEnchant (optional companion)
- **Conflicts:** None known

## BeyondEnchant
- **CurseForge ID:** 1135664
- **Slug:** beyondenchant
- **Modrinth Slug:** beyondenchant
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Raises vanilla enchantment level caps (Sharpness to 7, Efficiency to 10, Protection to 5, Unbreaking to 10, Fortune to 5, Mending to 5, and more) with an online configurator.
- **Why:** Extends endgame gear progression by allowing higher enchantment levels, giving SMP players more to work toward beyond vanilla caps.
- **Dependencies:** None required; NeoEnchant+ (optional companion)
- **Conflicts:** None known

## Grind Enchantments
- **CurseForge ID:** 379680
- **Slug:** grind-enchantments
- **Modrinth Slug:** grind-enchantments
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Disenchant and transfer enchantments using a grindstone instead of discarding them.
- **Why:** Lets SMP players salvage enchantments from unwanted gear via the grindstone, avoiding conflicts with Easy Anvils' anvil modifications.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## You're in Grave Danger
- **CurseForge ID:** 544912
- **Slug:** youre-in-grave-danger
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Spawns a grave at the player's death location that stores their items for safe retrieval.
- **Why:** Prevents frustrating item loss on death — players can recover their gear from a grave instead of racing a despawn timer.
- **Dependencies:** Fabric API, Cloth Config API
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

## Cupboard
- **CurseForge ID:** 326652
- **Slug:** cupboard
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Provides code, frameworks, and utilities for Minecraft mods.
- **Why:** Required dependency of Loot Integrations.
- **Dependencies:** None
- **Conflicts:** None known

## Loot Integrations
- **CurseForge ID:** 580689
- **Slug:** loot-integrations
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Allows loot from loot tables to be integrated into other loot tables through datapacks, enriching structure chest loot with modded items.
- **Why:** Enriches loot found in dungeon and structure chests by integrating items from all installed mods, making exploration more rewarding.
- **Dependencies:** Fabric API, Cupboard
- **Conflicts:** None known

## RightClickHarvest Supplementaries Compat
- **CurseForge ID:** 1038427
- **Slug:** rightclickharvest-supplementaries-compat
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds compatibility between RightClickHarvest and Supplementaries, making Flax and other Supplementaries crops right-click-harvestable.
- **Why:** Bridges the gap between RightClickHarvest and Supplementaries so all crops (including Flax) support right-click harvesting.
- **Dependencies:** RightClickHarvest, Supplementaries
- **Conflicts:** None known

## Supplementaries Squared
- **CurseForge ID:** 838411
- **Slug:** supplementaries-squared
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** An addon for Supplementaries adding block variants like colored sacks, wood-variant item shelves, golden candle holders, daub stairs/walls, metal frames, and plaques.
- **Why:** Expands Supplementaries with additional decorative and functional block variants for more building options on the SMP.
- **Dependencies:** Moonlight Lib, Fabric API, Supplementaries
- **Conflicts:** None known

## Accessorify
- **CurseForge ID:** 1169634
- **Slug:** accessorify
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Makes vanilla items (clock, compass, elytra, totem, spyglass, lantern, ender chest, shulker boxes, arrows) equippable as accessories.
- **Why:** Frees up inventory space by letting players wear useful vanilla items as accessories, with configurable item support and modded variant compatibility.
- **Dependencies:** Accessories, Fzzy Config, Fabric API
- **Conflicts:** None known


## Easy Mob Farm
- **CurseForge ID:** 563464
- **Slug:** easy-mob-farm
- **Modrinth Slug:** easy-mob-farm
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds mob farm blocks that capture and automatically farm mobs for their loot drops in a server-friendly way.
- **Why:** Provides a balanced, server-friendly way to automate mob farming without lag-inducing traditional mob grinders.
- **Dependencies:** Fabric API
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
- **Why:** Complements Handcrafted and Supplementaries with additional decorative blocks focused on planters, lighting, and window treatments.
- **Dependencies:** None required; Jade (optional, in pack)
- **Conflicts:** None known

## Every Compat
- **CurseForge ID:** 628539
- **Slug:** every-compat
- **Modrinth Slug:** every-compat
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Automatically generates wood-type variants (stairs, slabs, fences, bookshelves, chests, furniture) for all modded wood types across compatible mods.
- **Why:** Bridges the gap between wood-adding mods and furniture/block mods so all modded wood types get proper variants for Handcrafted, Supplementaries, Chipped, etc.
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
