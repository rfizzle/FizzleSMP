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
