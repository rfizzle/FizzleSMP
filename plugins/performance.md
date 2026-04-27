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

## Euphoria Patches
- **CurseForge ID:** 915902
- **Slug:** euphoria-patches
- **Modrinth Slug:** euphoria-patches
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** An add-on for Complementary Shaders that extends them with more optional features and settings.
- **Why:** Enhances both Complementary Reimagined and Unbound with additional visual options like waving plants, better water, and more customization.
- **Dependencies:** Complementary Shaders - Reimagined or Complementary Shaders - Unbound, Iris Shaders
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

## C2ME (Concurrent Chunk Management Engine)
- **CurseForge ID:** 533097
- **Slug:** c2me
- **Modrinth Slug:** c2me-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Parallelizes chunk loading, generation, and I/O operations to leverage multi-core CPUs for faster chunk performance.
- **Why:** Significantly speeds up chunk generation and loading on the SMP server, complementing Lithium and NoisiumForked.
- **Dependencies:** None
- **Conflicts:** None known (alpha stage — backup worlds recommended)

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
- **Why:** Optimizes crafting table performance by caching the last recipe used and only rechecking when inputs actually change. Confirmed fix for the shift-click crafting bug where output couldn't be shift-clicked into a full/partial inventory.
- **Dependencies:** Cloth Config, Mod Menu
- **Conflicts:** None known

## Enhanced Block Entities
- **CurseForge ID:** 452046
- **Slug:** enhanced-block-entities
- **Modrinth Slug:** ebe
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Replaces block entity renderers (chests, signs, beds, shulker boxes) with baked block models for significant FPS gains.
- **Why:** Major FPS improvement in storage rooms and built-up areas by converting expensive block entity renders to optimized baked models.
- **Dependencies:** Fabric API
- **Conflicts:** None known; some resource packs may cause invisible chests (enable "Force Resource Pack Compatibility" in EBE settings).

## More Culling
- **CurseForge ID:** 630104
- **Slug:** moreculling
- **Modrinth Slug:** moreculling
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Improves culling of hidden block faces (leaves, snow, powder snow) to reduce unnecessary rendering.
- **Why:** Complements Entity Culling by optimizing block-level face culling; significant FPS gain in foliage-heavy and snowy areas.
- **Dependencies:** Cloth Config API
- **Conflicts:** None known

## Sodium Extra
- **CurseForge ID:** 447673
- **Slug:** sodium-extra
- **Modrinth Slug:** sodium-extra
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds extra toggle options to Sodium's settings for animations, particles, weather, fog, and FPS display.
- **Why:** Gives players fine-grained control over visual features for performance tuning; essential companion to Sodium.
- **Dependencies:** Sodium
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

## Staaaaaaaaaaaack (Stxck)
- **CurseForge ID:** 866957
- **Slug:** staaaaaaaaaaaack
- **Modrinth Slug:** staaaaaaaaaaaack
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Merges dropped items beyond the vanilla stack limit to reduce entity lag from large item piles.
- **Why:** Reduces lag caused by mass item drops (mob farms, TNT mining, tree chopping) by consolidating ground items into super-stacks without changing inventory stack sizes.
- **Dependencies:** None
- **Conflicts:** None known
