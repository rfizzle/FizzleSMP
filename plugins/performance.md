# Performance & Optimization

<!-- Mods that improve FPS, reduce lag, speed up loading, or optimize the server/client. -->

## Sodium
- **CurseForge ID:** 394468
- **Slug:** sodium
- **Mod Loader:** Fabric
- **Summary:** The fastest and most compatible rendering optimization mod for Minecraft.
- **Why:** Essential client-side performance boost — dramatically improves FPS and reduces stuttering.
- **Dependencies:** None
- **Conflicts:** None known

## Iris Shaders
- **CurseForge ID:** 455508
- **Slug:** irisshaders
- **Mod Loader:** Fabric
- **Summary:** A modern shader pack loader compatible with existing OptiFine shader packs.
- **Why:** Allows players to use shader packs for visual enhancement while keeping Sodium performance.
- **Dependencies:** Sodium
- **Conflicts:** Incompatible with OptiFine/OptiFabric

## ImmediatelyFast
- **CurseForge ID:** 686911
- **Slug:** immediatelyfast
- **Mod Loader:** Fabric
- **Summary:** Speeds up immediate mode rendering in Minecraft (HUD, text, entities).
- **Why:** Complements Sodium by optimizing rendering areas Sodium doesn't cover (HUD, text, item rendering).
- **Dependencies:** None
- **Conflicts:** Incompatible with OptiFine/OptiFabric

## Entity Culling
- **CurseForge ID:** 448233
- **Slug:** entityculling
- **Mod Loader:** Fabric
- **Summary:** Uses async path-tracing to skip rendering block entities and entities that are not visible.
- **Why:** Significant FPS improvement in entity-heavy areas by culling off-screen entities more aggressively than Sodium.
- **Dependencies:** Fabric API
- **Conflicts:** None known; may need config whitelisting for oversized block entities (e.g., beacons)

## Lithium
- **CurseForge ID:** 360438
- **Slug:** lithium
- **Mod Loader:** Fabric
- **Summary:** No-compromises game logic optimization mod for servers and clients.
- **Why:** Optimizes game logic (AI, physics, chunk loading) for significant server and client performance gains without changing vanilla behavior.
- **Dependencies:** None
- **Conflicts:** None known; individual optimizations can be disabled if issues arise with specific mods.

## Noisium
- **CurseForge ID:** 930207
- **Slug:** noisium
- **Mod Loader:** Fabric
- **Summary:** Optimises worldgen performance for faster chunk generation.
- **Why:** Speeds up world generation, complementing other performance mods; especially beneficial for exploration-heavy SMP gameplay.
- **Dependencies:** None
- **Conflicts:** None known (archived project, but stable for 1.21.1)

## FerriteCore
- **CurseForge ID:** 459857
- **Slug:** ferritecore-fabric
- **Mod Loader:** Fabric
- **Summary:** Reduces memory usage through optimized data structures and blockstate management.
- **Why:** Significantly lowers RAM consumption, allowing more headroom for other mods and larger worlds on the SMP server.
- **Dependencies:** None
- **Conflicts:** None known

## ModernFix
- **CurseForge ID:** 790626
- **Slug:** modernfix
- **Mod Loader:** Fabric
- **Summary:** All-in-one mod that improves performance, reduces memory usage, and fixes many bugs.
- **Why:** Complements existing performance mods by improving launch times, world load times, and memory usage with broad compatibility.
- **Dependencies:** None
- **Conflicts:** None known

## Fabric API
- **CurseForge ID:** 306612
- **Slug:** fabric-api
- **Mod Loader:** Fabric
- **Summary:** Lightweight and modular API providing common hooks and intercompatibility measures for Fabric mods.
- **Why:** Required dependency of Entity Culling.
- **Dependencies:** None
- **Conflicts:** None known
