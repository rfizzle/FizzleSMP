# World Generation & Terrain

<!-- Mods that alter biomes, structures, terrain shape, or dimension generation. -->

## Terralith
- **CurseForge ID:** 513688
- **Slug:** terralith
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds nearly 100 new biomes with overhauled terrain using only vanilla blocks, plus immersive structures.
- **Why:** Massively expands world diversity with realistic and fantasy biomes, making exploration more rewarding on the SMP.
- **Dependencies:** None
- **Conflicts:** None known

## Lithostitched
- **CurseForge ID:** 936015
- **Slug:** lithostitched
- **Modrinth Slug:** lithostitched
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Library mod with new configurability and compatibility enhancements for worldgen.
- **Why:** Required dependency of Tectonic for worldgen stitching.
- **Dependencies:** None
- **Conflicts:** None known

## Tectonic
- **CurseForge ID:** 686836
- **Slug:** tectonic
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Overhauls terrain generation with grander, more varied mountains, valleys, and landforms.
- **Why:** Creates dramatic terrain shaping that complements Terralith's biome additions for a visually stunning world.
- **Dependencies:** Lithostitched
- **Conflicts:** None known; mod version auto-loads Terratonic compatibility when Terralith is detected.

## Repurposed Structures
- **CurseForge ID:** 391366
- **Slug:** repurposed-structures-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds biome-themed variants of vanilla structures (pyramids, villages, dungeons, mineshafts, and more).
- **Why:** Adds structure variety across biomes so exploration feels fresh and structures fit their surroundings.
- **Dependencies:** Fabric API, MidnightLib
- **Conflicts:** Soft conflict with YUNG's suite — overlapping structure types (dungeons, mineshafts, desert temples, jungle temples, witch huts). Resolved via `RS-YUNG-Compat-Config.zip` datapack (shipped in `modpack/config/paxi/datapacks/`) which disables RS overworld mineshafts, pyramids, temples, witch huts, and overworld dungeons to let YUNG's handle them. RS remains enabled for its unique shipwrecks, mansions, igloos, ruins, cities, bastions, villages, outposts, and nether/end variants.

## ChoiceTheorem's Overhauled Village
- **CurseForge ID:** 623908
- **Slug:** choicetheorems-overhauled-village
- **Modrinth Slug:** ct-overhaul-village
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Completely overhauls villages and pillager outposts with detailed, biome-appropriate redesigns.
- **Why:** Replaces vanilla villages with beautifully redesigned variants for every biome, making settlements feel unique and immersive.
- **Dependencies:** None
- **Conflicts:** None known

## YUNG's Better Caves
- **CurseForge ID:** 408465
- **Slug:** yungs-better-caves-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Completely overhauls Minecraft's cave systems with lava caverns, underground lakes and rivers, flooded caverns, and more.
- **Why:** Makes underground exploration dramatically more interesting with varied cave types that get crazier the deeper you go.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known; compatible with Terralith.

## YUNG's Better Mineshafts
- **CurseForge ID:** 373591
- **Slug:** yungs-better-mineshafts-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Revamps abandoned mineshafts into dynamic webs of tunnels with 13 biome variants, ore deposits, and hidden loot.
- **Why:** Transforms mineshafts from boring straight lines into varied, explorable tunnel networks.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known

## YUNG's Better Strongholds
- **CurseForge ID:** 480684
- **Slug:** yungs-better-strongholds-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Completely redesigns strongholds with 15+ room types, varied tunnels, staircases, traps, and hidden areas.
- **Why:** Makes the stronghold a proper dungeon-crawling experience instead of a repetitive maze.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known

## YUNG's Better Dungeons
- **CurseForge ID:** 525586
- **Slug:** yungs-better-dungeons-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Redesigns vanilla dungeons and adds three new types: Catacombs, Fortresses of the Undead, and Spider Caves.
- **Why:** Replaces boring single-room spawner dungeons with varied, atmospheric dungeon experiences.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** Overlaps with Repurposed Structures dungeon variants; disable RS dungeons in its config.

## YUNG's Better Desert Temples
- **CurseForge ID:** 631020
- **Slug:** yungs-better-desert-temples-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Redesigns desert temples with new puzzles, traps, parkour challenges, and a pharaoh boss encounter.
- **Why:** Transforms desert temples from trivial loot boxes into engaging multi-room dungeons.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known

## YUNG's Better Ocean Monuments
- **CurseForge ID:** 689252
- **Slug:** yungs-better-ocean-monuments-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Overhauls ocean monuments to be larger, randomized, more engaging, and more rewarding with better loot.
- **Why:** Makes ocean monuments worth exploring with varied layouts and improved loot including tridents.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known

## YUNG's Better Nether Fortresses
- **CurseForge ID:** 817666
- **Slug:** yungs-better-nether-fortresses-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Completely redesigns Nether fortresses with bridge networks, a Keep, and Lava Halls extending deep underground.
- **Why:** Makes Nether fortresses much larger, more complex, and more rewarding to explore.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known

## YUNG's Better Jungle Temples
- **CurseForge ID:** 897678
- **Slug:** yungs-better-jungle-temples-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Redesigns jungle temples with expanded layouts, new puzzles, traps, and improved loot.
- **Why:** Transforms jungle temples from small, forgettable structures into proper jungle dungeon experiences.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known

## YUNG's Better Witch Huts
- **CurseForge ID:** 631403
- **Slug:** yungs-better-witch-huts-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Overhauls swamp witch huts with multiple biome-specific variants and expanded interiors.
- **Why:** Adds variety to witch huts so they feel more integrated into the world.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known

## YUNG's Better End Island
- **CurseForge ID:** 901350
- **Slug:** yungs-better-end-island-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Overhauls the main End Island with redesigned obsidian pillars, gateways, spawn platform, and a bell tower summon mechanic.
- **Why:** Makes the dragon fight arena more impressive and changes the dragon encounter to be summoned rather than auto-spawning.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known

## YUNG's Extras
- **CurseForge ID:** 590993
- **Slug:** yungs-extras-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds miscellaneous small structures and features to the world like desert wells, shipwrecks, igloos, and more.
- **Why:** Fills in small worldgen gaps with extra structures that complement the larger YUNG's overhauls.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known

## YUNG's Bridges
- **CurseForge ID:** 590988
- **Slug:** yungs-bridges-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds naturally generated bridges throughout the world with 15+ biome-specific variants.
- **Why:** Adds beautiful, immersive bridges that span ravines and rivers, enhancing world atmosphere.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known

## YUNG's API
- **CurseForge ID:** 421649
- **Slug:** yungs-api-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A common API and library mod used by all YUNG's mods for shared structure generation code.
- **Why:** Required dependency of all YUNG's Better structure mods and Traveler's Titles.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Incendium
- **CurseForge ID:** 591388
- **Slug:** incendium
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A nether biome overhaul with challenging structures, unique weapons, and tricky mobs.
- **Why:** Transforms the Nether into a rich, explorable dimension with new biomes, structures, loot, and mobs for SMP adventure.
- **Dependencies:** None
- **Conflicts:** None known; incompatible with Amplified Nether (not in pack).

## Nullscape
- **CurseForge ID:** 570354
- **Slug:** nullscape
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Transforms the End into an alien dimension with surreal terrain and new biomes while keeping it desolate.
- **Why:** Makes the End worth exploring beyond just the dragon fight, with dramatic terrain and new biomes.
- **Dependencies:** None
- **Conflicts:** None known; compatible with Terralith and Incendium (same author — Stardust Labs).

## Structory
- **CurseForge ID:** 636540
- **Slug:** structory
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds atmospheric, lore-rich structures throughout the world with seasonal updates.
- **Why:** Fills the world with immersive small-to-medium structures that enhance exploration and world atmosphere.
- **Dependencies:** None
- **Conflicts:** None known

## Structory: Towers
- **CurseForge ID:** 783522
- **Slug:** structory-towers
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds immersive biome-themed towers to the world as a standalone option or add-on to Structory.
- **Why:** Complements Structory with vertical tower structures that serve as landmarks and exploration targets.
- **Dependencies:** None (standalone, but designed to pair with Structory)
- **Conflicts:** None known


## Geophilic
- **CurseForge ID:** 711216
- **Slug:** geophilic
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A subtle overhaul of vanilla Overworld biomes with reworked vegetation and terrain features.
- **Why:** Enhances vanilla biomes with more natural-looking vegetation and terrain details without adding new biomes.
- **Dependencies:** None
- **Conflicts:** Soft conflict with Terralith — both modify vanilla biome files. Requires Terraphilic compatibility pack to use together.

## Terraphilic
- **CurseForge ID:** 1098706
- **Slug:** terraphilic
- **Modrinth Slug:** terraphilic
- **Mod Loader:** Datapack
- **Side:** both
- **Summary:** Geophilic-Terralith compatibility datapack that merges biome files from both mods.
- **Why:** Required compatibility pack to run Geophilic alongside Terralith without biome conflicts.
- **Dependencies:** Terralith, Geophilic
- **Conflicts:** None — this mod resolves the Geophilic/Terralith conflict.

## Hopo Better Underwater Ruins
- **CurseForge ID:** 632622
- **Slug:** hopo-better-underwater-ruins
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Overhauls ocean ruins with new, larger, and more varied underwater structure designs.
- **Why:** Enhances underwater exploration with better-looking and more rewarding ocean ruins.
- **Dependencies:** None
- **Conflicts:** None known

## Sparse Structures
- **CurseForge ID:** 911437
- **Slug:** sparse-structures
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Makes all structures (including modded) more spread out and configurable in rarity.
- **Why:** Prevents structure spam in a heavily modded worldgen pack; encourages exploration by making structures rarer and better distributed.
- **Dependencies:** Fabric API
- **Conflicts:** None known; works with all structure mods including modded and datapack structures.

## YUNG's Structures Addon for Loot Integrations
- **CurseForge ID:** 1012211
- **Slug:** yung-structures-addon-for-loot-integrations
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds better, more varied loot to all YUNG's structure mod chests via Loot Integrations.
- **Why:** Enhances loot in YUNG's redesigned structures (dungeons, temples, mineshafts, etc.) with items from other mods in the pack.
- **Dependencies:** Loot Integrations, YUNG's API
- **Conflicts:** None known

## Loot Integrations: ChoiceTheorem's Overhauled Village & Immersive Structures
- **CurseForge ID:** 1135500
- **Slug:** loot-integrations-choicetheorems-overhauled
- **Modrinth Slug:** N/A
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds better loot to ChoiceTheorem's Overhauled Village & Immersive Structures chests, including modded loot.
- **Why:** Enriches loot in CTOV village and outpost chests with items from other mods in the pack via Loot Integrations.
- **Dependencies:** Loot Integrations, ChoiceTheorem's Overhauled Village
- **Conflicts:** None known

## Loot Integrations: Philip's Ruins
- **CurseForge ID:** 1152718
- **Slug:** loot-integrations-philips-ruins
- **Modrinth Slug:** N/A
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds better loot to Philip's Ruins chests including modded loot.
- **Why:** Enriches loot in Philip's Ruins structure chests with items from other mods in the pack via Loot Integrations.
- **Dependencies:** Loot Integrations, Philip's Ruins
- **Conflicts:** None known

## Loot Integrations: Moog's Voyager, Soaring, End & Nether Structures
- **CurseForge ID:** 1152715
- **Slug:** loot-integrations-moogs-voyager-soaring-end-nether
- **Modrinth Slug:** N/A
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds better loot to Moog's Voyager, Soaring, End & Nether Structures chests, including modded loot.
- **Why:** Enriches loot in MVS structure chests with items from other mods in the pack via Loot Integrations.
- **Dependencies:** Loot Integrations, MVS - Moog's Voyager Structures (also covers MES, MNS, MSS)
- **Conflicts:** None known

## Loot Integrations: Better Archeology
- **CurseForge ID:** 1130800
- **Slug:** loot-integrations-better-archeology
- **Modrinth Slug:** N/A
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds better loot to Better Archeology chests, including modded loot.
- **Why:** Enriches loot in Better Archeology structure chests with items from other mods in the pack via Loot Integrations.
- **Dependencies:** Loot Integrations, Better Archeology
- **Conflicts:** None known

## Explorify
- **CurseForge ID:** 698309
- **Slug:** explorify
- **Modrinth Slug:** explorify
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A simplistic, vanilla-friendly collection of new structures including dungeons, ruins, and points of interest.
- **Why:** Adds lightweight, vanilla-aesthetic structures that fill gaps between larger structure mods without feeling out of place.
- **Dependencies:** None
- **Conflicts:** None known

## MVS - Moog's Voyager Structures
- **CurseForge ID:** 656977
- **Slug:** moogs-voyager-structures
- **Modrinth Slug:** moogs-voyager-structures
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds 130+ vanilla-style structures to the world using only vanilla blocks, including dungeons and enemies.
- **Why:** Massively increases structure variety with vanilla-block structures that blend naturally into the world.
- **Dependencies:** Moog's Structure Lib
- **Conflicts:** None known

## Moog's Structure Lib
- **CurseForge ID:** 1337167
- **Slug:** moogs-structure-lib
- **Modrinth Slug:** moogs-structure-lib
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A library mod for the Moog's Structure series.
- **Why:** Required dependency of MVS - Moog's Voyager Structures, MES, MNS, and MSS.
- **Dependencies:** None
- **Conflicts:** None known

## MES - Moog's End Structures
- **CurseForge ID:** 892382
- **Slug:** moogs-end-structures
- **Modrinth Slug:** N/A
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds vanilla-styled structures to the End dimension with loot, enemies, and dungeons.
- **Why:** Fills the nearly empty outer End islands with explorable structures, addressing the pack's biggest structure gap.
- **Dependencies:** Moog's Structure Lib
- **Conflicts:** None known

## MNS - Moog's Nether Structures
- **CurseForge ID:** 967466
- **Slug:** mns-moogs-nether-structures
- **Modrinth Slug:** N/A
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds vanilla-block structures to the Nether dimension with loot, enemies, and dungeons.
- **Why:** Supplements Incendium and YUNG's Better Nether Fortresses with additional Nether structures for more exploration variety.
- **Dependencies:** Moog's Structure Lib
- **Conflicts:** None known

## MSS - Moog's Soaring Structures
- **CurseForge ID:** 1040210
- **Slug:** moogs-soaring-structures
- **Modrinth Slug:** N/A
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds vanilla-style floating islands with buildings, loot, mobs, and dungeons to explore in the sky.
- **Why:** Fills a unique vertical niche with sky-based structures that no other mod in the pack covers.
- **Dependencies:** Moog's Structure Lib
- **Conflicts:** None known

## Philip's Ruins
- **CurseForge ID:** 569737
- **Slug:** ruins
- **Modrinth Slug:** philips-ruins
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds ancient ruins scattered throughout the Minecraft world.
- **Why:** Populates the world with atmospheric small ruins that add a sense of history and discovery during exploration.
- **Dependencies:** None
- **Conflicts:** None known

## Tidal Towns
- **CurseForge ID:** 891880
- **Slug:** tidal-towns
- **Modrinth Slug:** tidal-towns
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds floating ocean villages built from driftwood that generate on the waves.
- **Why:** Adds unique ocean village structures, filling a gap in ocean content and giving players a reason to explore oceans.
- **Dependencies:** None
- **Conflicts:** None known

## Better Archeology
- **CurseForge ID:** 835687
- **Slug:** better-archeology
- **Modrinth Slug:** better-archeology
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds new archeology structures, artifacts, and fossils to discover throughout the world.
- **Why:** Expands the vanilla archeology system with new structures and rewards, making brush-based exploration more worthwhile.
- **Dependencies:** Architectury API, Resourceful Config
- **Conflicts:** None known

## Repurposed Structures - Farmer's Delight Compat
- **CurseForge ID:** 1071336
- **Slug:** repurposed-structures-farmers-delight-compat-mod
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds Farmer's Delight crops and compost buildings to Repurposed Structures villages.
- **Why:** Integrates Farmer's Delight content into RS village generation, making modded village farms use FD crops and buildings.
- **Dependencies:** Repurposed Structures, Farmer's Delight Refabricated
- **Conflicts:** None known
