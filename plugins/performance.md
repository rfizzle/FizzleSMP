# Performance & Optimization

<!-- Mods that improve FPS, reduce lag, speed up loading, or optimize the server/client. -->

## Sodium
- **CurseForge ID:** 394468
- **Slug:** sodium
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** The fastest and most compatible rendering optimization mod for Minecraft.
- **Why:** Essential client-side performance boost — dramatically improves FPS and reduces stuttering.
- **Dependencies:** None
- **Conflicts:** None known

## Iris Shaders
- **CurseForge ID:** 455508
- **Slug:** irisshaders
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A modern shader pack loader compatible with existing OptiFine shader packs.
- **Why:** Allows players to use shader packs for visual enhancement while keeping Sodium performance.
- **Dependencies:** Sodium
- **Conflicts:** Incompatible with OptiFine/OptiFabric

## ImmediatelyFast
- **CurseForge ID:** 686911
- **Slug:** immediatelyfast
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Speeds up immediate mode rendering in Minecraft (HUD, text, entities).
- **Why:** Complements Sodium by optimizing rendering areas Sodium doesn't cover (HUD, text, item rendering).
- **Dependencies:** None
- **Conflicts:** Incompatible with OptiFine/OptiFabric

## Entity Culling
- **CurseForge ID:** 448233
- **Slug:** entityculling
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Uses async path-tracing to skip rendering block entities and entities that are not visible.
- **Why:** Significant FPS improvement in entity-heavy areas by culling off-screen entities more aggressively than Sodium.
- **Dependencies:** Fabric API
- **Conflicts:** None known; may need config whitelisting for oversized block entities (e.g., beacons)

## Fabric API
- **CurseForge ID:** 306612
- **Slug:** fabric-api
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Lightweight and modular API providing common hooks and intercompatibility measures for Fabric mods.
- **Why:** Required dependency of Entity Culling.
- **Dependencies:** None
- **Conflicts:** None known
