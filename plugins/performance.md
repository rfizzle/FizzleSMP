# Performance & Optimization

<!-- Mods that improve FPS, reduce lag, speed up loading, or optimize the server/client. -->

## Sodium
- **CurseForge ID:** 394468
- **Slug:** sodium
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** The fastest and most compatible rendering optimization mod for Minecraft.
- **Why:** Essential client-side performance boost — dramatically improves FPS and reduces stuttering.
- **Dependencies:** None
- **Conflicts:** None known

## Iris Shaders
- **CurseForge ID:** 455508
- **Slug:** irisshaders
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** A modern shader pack loader compatible with existing OptiFine shader packs.
- **Why:** Allows players to use shader packs for visual enhancement while keeping Sodium performance.
- **Dependencies:** Sodium
- **Conflicts:** Incompatible with OptiFine/OptiFabric

## Complementary Shaders - Reimagined
- **CurseForge ID:** 627557
- **Slug:** complementary-reimagined
- **Modrinth Slug:** complementary-reimagined
- **Mod Loader:** Iris
- **Side:** client
- **Summary:** A shader pack that preserves the vanilla Minecraft aesthetic with modern lighting, reflections, and atmosphere.
- **Why:** Gives players a beautiful, performance-friendly shader option that stays faithful to vanilla visuals.
- **Dependencies:** Iris Shaders
- **Conflicts:** None known

## Complementary Shaders - Unbound
- **CurseForge ID:** 385587
- **Slug:** complementary-unbound
- **Modrinth Slug:** complementary-unbound
- **Mod Loader:** Iris
- **Side:** client
- **Summary:** A shader pack that transforms Minecraft visuals with dramatic lighting, volumetrics, and stylized effects.
- **Why:** Offers players a more cinematic, stylized shader alternative alongside Reimagined.
- **Dependencies:** Iris Shaders
- **Conflicts:** None known

## ImmediatelyFast
- **CurseForge ID:** 686911
- **Slug:** immediatelyfast
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Speeds up immediate mode rendering in Minecraft (HUD, text, entities).
- **Why:** Complements Sodium by optimizing rendering areas Sodium doesn't cover (HUD, text, item rendering).
- **Dependencies:** None
- **Conflicts:** Incompatible with OptiFine/OptiFabric

## Entity Culling
- **CurseForge ID:** 448233
- **Slug:** entityculling
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Uses async path-tracing to skip rendering block entities and entities that are not visible.
- **Why:** Significant FPS improvement in entity-heavy areas by culling off-screen entities more aggressively than Sodium.
- **Dependencies:** Fabric API
- **Conflicts:** None known; may need config whitelisting for oversized block entities (e.g., beacons)

## Lithium
- **CurseForge ID:** 360438
- **Slug:** lithium
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** No-compromises game logic optimization mod for servers and clients.
- **Why:** Optimizes game logic (AI, physics, chunk loading) for significant server and client performance gains without changing vanilla behavior.
- **Dependencies:** None
- **Conflicts:** None known; individual optimizations can be disabled if issues arise with specific mods.

## NoisiumForked
- **CurseForge ID:** 1357563
- **Slug:** noisiumforked
- **Modrinth Slug:** noisiumforked
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Optimises worldgen performance for faster chunk generation (actively maintained fork of archived Noisium).
- **Why:** Speeds up world generation, complementing other performance mods; especially beneficial for exploration-heavy SMP gameplay. Replaces the archived Noisium with an actively maintained fork.
- **Dependencies:** None
- **Conflicts:** None known

## FerriteCore
- **CurseForge ID:** 459857
- **Slug:** ferritecore-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Reduces memory usage through optimized data structures and blockstate management.
- **Why:** Significantly lowers RAM consumption, allowing more headroom for other mods and larger worlds on the SMP server.
- **Dependencies:** None
- **Conflicts:** None known

## ModernFix
- **CurseForge ID:** 790626
- **Slug:** modernfix
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** All-in-one mod that improves performance, reduces memory usage, and fixes many bugs.
- **Why:** Complements existing performance mods by improving launch times, world load times, and memory usage with broad compatibility.
- **Dependencies:** None
- **Conflicts:** None known

## Clumps
- **CurseForge ID:** 256717
- **Slug:** clumps
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Groups nearby XP orbs into a single entity to reduce lag.
- **Why:** Reduces entity count and tick overhead when many XP orbs spawn, improving server performance in farms and combat areas.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Fabric API
- **CurseForge ID:** 306612
- **Slug:** fabric-api
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Lightweight and modular API providing common hooks and intercompatibility measures for Fabric mods.
- **Why:** Required dependency of Entity Culling.
- **Dependencies:** None
- **Conflicts:** None known

## Let Me Despawn
- **CurseForge ID:** 663477
- **Slug:** let-me-despawn
- **Mod Loader:** Fabric
- **Side:** server
- **Summary:** Allows mobs that picked up items to despawn naturally, dropping their items, preventing entity buildup over time.
- **Why:** Prevents server entity lag from mobs with picked-up items becoming permanently persistent; server-side only.
- **Dependencies:** Almanac Lib
- **Conflicts:** None known

## Krypton
- **CurseForge ID:** 428912
- **Slug:** krypton
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Optimizes the Minecraft networking stack with improved packet compression, flush consolidation, and login handling.
- **Why:** Reduces server bandwidth usage and improves multiplayer performance, complementing Lithium (game logic) and Sodium (rendering).
- **Dependencies:** None
- **Conflicts:** Soft conflict with Connectivity — both modify networking (see compatibility matrix).

## Dynamic FPS
- **CurseForge ID:** 335493
- **Slug:** dynamic-fps
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Reduces resource usage while Minecraft is in the background, idle, or on battery.
- **Why:** Saves CPU/GPU when players alt-tab or go idle, reducing client-side resource waste on the SMP.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Debugify
- **CurseForge ID:** 596224
- **Slug:** debugify
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Fixes numerous Minecraft bugs found on the official bug tracker.
- **Why:** Patches vanilla bugs that affect gameplay quality — fixes for phantom spawning, pathfinding, rendering glitches, and more.
- **Dependencies:** Fabric API, YetAnotherConfigLib (YACL)
- **Conflicts:** None known

## FastWorkbench
- **CurseForge ID:** 1471415
- **Slug:** fastworkbench-fabric
- **Modrinth Slug:** fastworkbench-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Caches crafting recipes and reduces redundant recipe matching operations, fixing the shift-click crafting lag introduced in 1.12.
- **Why:** Optimizes crafting table performance by caching the last recipe used and only rechecking when inputs actually change; may also fix shift-click crafting into partial stacks in a full inventory.
- **Dependencies:** Cloth Config, Mod Menu
- **Conflicts:** None known

## Almanac Lib
- **CurseForge ID:** 1115285
- **Slug:** almanac-lib
- **Mod Loader:** Fabric
- **Side:** server
- **Summary:** A loader-independent library providing shared utilities for Almanac mods.
- **Why:** Required dependency of Let Me Despawn.
- **Dependencies:** None
- **Conflicts:** None known
