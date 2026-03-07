# Core Gameplay & Content

<!-- Mods that add or change core gameplay mechanics, items, blocks, mobs, or progression. -->

## RightClickHarvest
- **CurseForge ID:** 452834
- **Slug:** rightclickharvest
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Allows harvesting and replanting crops with a simple right-click.
- **Why:** Essential farming QoL — lets players harvest mature crops with right-click, automatically replanting them.
- **Dependencies:** Fabric API, Architectury API, JamLib
- **Conflicts:** None known

## Architectury API
- **CurseForge ID:** 419699
- **Slug:** architectury-api
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** An intermediary API for developing multi-platform Minecraft mods.
- **Why:** Required dependency of RightClickHarvest.
- **Dependencies:** None
- **Conflicts:** None known

## JamLib
- **CurseForge ID:** 623764
- **Slug:** jamlib
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A library mod providing shared utilities for JamCoreModding mods.
- **Why:** Required dependency of RightClickHarvest.
- **Dependencies:** None
- **Conflicts:** None known

## Lootr
- **CurseForge ID:** 615106
- **Slug:** lootr-fabric
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Makes loot chests instanced per player so every player gets their own loot from the same container.
- **Why:** Ensures all SMP players get fair loot from structures without racing, and discourages generating new chunks just for loot.
- **Dependencies:** Fabric API, Cloth Config API
- **Conflicts:** None known; required on both client and server

## Traveler's Backpack
- **CurseForge ID:** 541171
- **Slug:** travelers-backpack-fabric
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds unique, upgradeable backpacks with tiers, fluid tanks, tool slots, crafting upgrades, and 45+ custom designs.
- **Why:** Provides portable storage and crafting for exploration-heavy SMP gameplay, with tiered progression from leather to netherite.
- **Dependencies:** Fabric API, Cloth Config API, Cardinal Components API
- **Conflicts:** None known

## Cardinal Components API
- **CurseForge ID:** 318449
- **Slug:** cardinal-components-api
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A data attachment API for attaching custom data to game objects like entities, chunks, and worlds.
- **Why:** Required dependency of Traveler's Backpack.
- **Dependencies:** None
- **Conflicts:** None known

## Reforged (formerly TieredZ)
- **CurseForge ID:** 453889
- **Slug:** tiered-forge
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds random quality modifiers to all tools, weapons, and armor with stat bonuses and abilities, plus a rerolling system via villager-sold hammers.
- **Why:** Adds RPG-style item quality tiers to every crafted piece of gear, making loot more exciting and encouraging trading on the SMP.
- **Dependencies:** UnionLib
- **Conflicts:** None known; modifiers apply to weapons from Simply Swords and gear from Mythic Upgrades (desirable interaction).

## UnionLib
- **CurseForge ID:** 367806
- **Slug:** unionlib
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A shared library mod providing common utilities for StereoWalker's mods.
- **Why:** Required dependency of Reforged (formerly TieredZ).
- **Dependencies:** None
- **Conflicts:** None known

## Artifacts
- **CurseForge ID:** 401236
- **Slug:** artifacts-fabric
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds powerful, uncraftable accessory items found in structure chests, archaeology, or dropped by mimics in underground campsites.
- **Why:** Makes exploration more rewarding with unique collectible items in accessory slots; uses Accessories (via Compat Layer) for slot management.
- **Dependencies:** None required; Accessories Compatibility Layer (optional, for accessory slots), Cloth Config API (optional)
- **Conflicts:** None known

## Trinkets
- **CurseForge ID:** 341284
- **Slug:** trinkets
- **Mod Loader:** Fabric
- **Status:** rejected
- **Summary:** A data-driven accessory slot system adding equipment slots for head, chest, legs, feet, hands, and custom groups.
- **Why:** Replaced by Accessories + Accessories Compatibility Layer. The compat layer wraps the Trinkets API so all Trinkets-dependent mods continue working under Accessories.
- **Dependencies:** None
- **Conflicts:** Conflicts with Accessories when both are installed directly; use Accessories Compatibility Layer instead.

## Accessories
- **CurseForge ID:** 938917
- **Slug:** accessories
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A data-driven accessory mod providing an extendable accessory slot system, inspired by Trinkets and Curios with a cross-platform API.
- **Why:** Replaces Trinkets as the accessory slot framework; required by Things and compatible with Spell Engine. Used with Accessories Compatibility Layer for Trinkets API backwards compatibility.
- **Dependencies:** None
- **Conflicts:** Do not install alongside Trinkets directly; use Accessories Compatibility Layer to bridge Trinkets-dependent mods.

## Accessories Compatibility Layer
- **CurseForge ID:** 1315611
- **Slug:** accessories-compat-layer
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Wraps the Trinkets and Curios APIs to work under Accessories, unifying accessory management under one framework.
- **Why:** Enables Trinkets-dependent mods (Artifacts, Spectrum) to work with Accessories without code changes.
- **Dependencies:** Accessories
- **Conflicts:** None known; replaces Trinkets mod (do not install Trinkets alongside this).

## Mythic Upgrades
- **CurseForge ID:** 663567
- **Slug:** mythic-upgrades
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds new ores (Jade, Topaz, Aquamarine, Sapphire, Ruby, Peridot, Zircon, Citrine, Necoium, Ametrine) with craftable ingots and gear upgrades beyond netherite.
- **Why:** Extends endgame progression with new materials and gear tiers, giving SMP players more goals beyond netherite.
- **Dependencies:** Fabric API, owo-lib
- **Conflicts:** None known

## Mythic Compat
- **CurseForge ID:** 1014057
- **Slug:** mythic-lib
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds cross-upgrade recipes for Mythic Upgrades, allowing gear to be upgraded between material tiers.
- **Why:** Enables upgrading between Mythic Upgrades material tiers (e.g., Topaz chestplate to Ametrine chestplate) for smoother progression.
- **Dependencies:** Fabric API, Mythic Upgrades
- **Conflicts:** None known

## owo-lib
- **CurseForge ID:** 532610
- **Slug:** owo-lib
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A general utility, GUI, and configuration library for Fabric mods.
- **Why:** Required dependency of Mythic Upgrades.
- **Dependencies:** None
- **Conflicts:** None known

## Enchanting Infuser
- **CurseForge ID:** 551151
- **Slug:** enchanting-infuser
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds a new enchanting table block that lets players choose specific enchantments for fair, configurable prices.
- **Why:** Removes enchanting randomness, letting SMP players pick exactly which enchantments they want instead of gambling at vanilla tables.
- **Dependencies:** Fabric API, Puzzles Lib, Forge Config API Port
- **Conflicts:** None known; complementary with Easy Magic (same author, different blocks).

## Easy Magic
- **CurseForge ID:** 456239
- **Slug:** easy-magic
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Improves the vanilla enchanting table by keeping items in place after closing and enabling easy enchantment re-rolls.
- **Why:** QoL improvement for vanilla enchanting — items stay in the table when you close the GUI and re-rolling enchantments is simplified.
- **Dependencies:** Fabric API, Puzzles Lib, Forge Config API Port
- **Conflicts:** None known; complementary with Enchanting Infuser (same author, different targets).

## Easy Anvils
- **CurseForge ID:** 682567
- **Slug:** easy-anvils
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Overhauls anvils with items staying after closing, removal of the "too expensive" cap, better name tags, and many configurable tweaks.
- **Why:** Removes the frustrating "too expensive" anvil limit and adds QoL improvements, keeping anvils useful throughout endgame on the SMP.
- **Dependencies:** Fabric API, Puzzles Lib, Forge Config API Port
- **Conflicts:** None known; complementary with Mendable Anvils (Easy Anvils tweaks mechanics, Mendable Anvils adds repair).

## Mendable Anvils
- **CurseForge ID:** 648497
- **Slug:** mendable-anvils
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Allows players to repair damaged anvils by shift + right-clicking with an iron ingot.
- **Why:** Prevents anvil waste on the SMP — players can maintain their anvils instead of constantly crafting new ones.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Spectrum
- **CurseForge ID:** 556967
- **Slug:** spectrum
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A progression and exploration-based magic mod where you combine colors to create magical tools, machines, and equipment, with the goal of breaking through bedrock.
- **Why:** Adds a deep, puzzle-driven magic progression system that rewards exploration and experimentation — perfect for long-term SMP engagement.
- **Dependencies:** Fabric API, Cloth Config API, Trinkets (via Accessories Compat Layer), Revelationary, Modonomicon
- **Conflicts:** None known

## Revelationary
- **CurseForge ID:** 656526
- **Slug:** revelationary
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A data-driven block and item revelation system that gates discovery of new blocks via advancements.
- **Why:** Required dependency of Spectrum for its progressive discovery system.
- **Dependencies:** None
- **Conflicts:** None known

## Modonomicon
- **CurseForge ID:** 538392
- **Slug:** modonomicon
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A data-driven in-game documentation mod with quest/advancement-style navigation, inspired by Thaumcraft's Thaumonomicon and Patchouli.
- **Why:** Required dependency of Spectrum for its in-game guidebook.
- **Dependencies:** None
- **Conflicts:** None known

## Things
- **CurseForge ID:** 456151
- **Slug:** things-fabric
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A collection of trinkets and utility items including a Displacement Tome (teleporter), Bater Wucket (infinite water), Ender Pouch (portable ender chest), and many accessory trinkets.
- **Why:** Adds a variety of useful, fun trinket items and utilities that enhance casual vanilla+ gameplay on the SMP.
- **Dependencies:** owo-lib, Lavender, Accessories
- **Conflicts:** None known; uses Accessories API for trinket slots.
