# Utility & Quality of Life

<!-- Mods for HUD, minimaps, inventory management, recipes, tooltips, and misc QoL. -->

## AppleSkin
- **CurseForge ID:** 248787
- **Slug:** appleskin
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds food/hunger-related HUD improvements including saturation and exhaustion visualization.
- **Why:** Gives players visibility into hidden hunger mechanics (saturation, exhaustion) so they can make informed food choices on the SMP.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Colorful Hearts
- **CurseForge ID:** 854213
- **Slug:** colorful-hearts
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Replaces multiple vanilla heart rows with a single row using colored hearts for cleaner health display.
- **Why:** Improves health readability when players have extra hearts from absorption or mods, reducing HUD clutter.
- **Dependencies:** Fabric API
- **Conflicts:** Hard conflict with Scaling Health (both modify heart rendering). Soft conflict with Overflowing Bars (requires disabling its heart rendering).

## Jade
- **CurseForge ID:** 324717
- **Slug:** jade
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Information HUD mod that shows details about the block or entity you are looking at (Hwyla/Waila fork).
- **Why:** Essential QoL for SMP players — quickly identify blocks, entities, and their properties without opening F3.
- **Dependencies:** None (JEI is optional for recipe integration)
- **Conflicts:** None known

## MiniHUD
- **CurseForge ID:** 244260
- **Slug:** minihud
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A "mini F3" HUD mod with various overlays like light level, spawn chunks, slime chunks, and more.
- **Why:** Provides compact debug info and visual overlays (light levels, structure bounding boxes) invaluable for building and mob-proofing on the SMP.
- **Dependencies:** MaLiLib
- **Conflicts:** None known

## guy's Armor HUD
- **CurseForge ID:** 1388138
- **Slug:** guys-armor-hud
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds a vanilla-like widget showing equipped armor items with durability warnings when armor is low.
- **Why:** Keeps players aware of armor durability at a glance without opening inventory, important for SMP PvE and PvP.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## MaLiLib
- **CurseForge ID:** 303119
- **Slug:** malilib
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A library mod containing shared code for masa's client-side mods (MiniHUD, Litematica, etc.).
- **Why:** Required dependency of MiniHUD.
- **Dependencies:** None
- **Conflicts:** None known

## Mouse Tweaks
- **CurseForge ID:** 60089
- **Slug:** mouse-tweaks
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Enhances inventory management by adding various functions to the mouse buttons.
- **Why:** Essential QoL for inventory management — adds RMB drag, LMB drag-splitting, and scroll wheel item movement.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Blur+
- **CurseForge ID:** 393563
- **Slug:** blur-fabric
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds a configurable Gaussian blur effect to the background of all GUI screens.
- **Why:** Provides a sleek, modern look to menus by blurring the game world behind GUIs.
- **Dependencies:** MidnightLib, Fabric API
- **Conflicts:** None known

## Continuity
- **CurseForge ID:** 531351
- **Slug:** continuity
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Enables connected textures, emissive textures, and custom block layers from OptiFine-format resource packs.
- **Why:** Allows resource packs with connected glass, bookshelves, etc. to work without OptiFine, complementing Sodium.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Mod Menu
- **CurseForge ID:** 308702
- **Slug:** modmenu
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds a mod list menu to view and configure installed mods.
- **Why:** Essential for accessing mod config screens in-game; used by many mods for settings integration.
- **Dependencies:** Fabric API, Text Placeholder API
- **Conflicts:** None known

## Cloth Config API
- **CurseForge ID:** 348521
- **Slug:** cloth-config
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Configuration library providing a standard config screen framework for Minecraft mods.
- **Why:** Required config library used by many mods for in-game settings screens.
- **Dependencies:** None
- **Conflicts:** None known

## MidnightLib
- **CurseForge ID:** 488090
- **Slug:** midnightlib
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** A common library providing shared utilities for Minecraft mods.
- **Why:** Required dependency of Blur+.
- **Dependencies:** None
- **Conflicts:** None known

## Text Placeholder API
- **CurseForge ID:** 1037459
- **Slug:** text-placeholder-api
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** An API for creating and parsing text placeholders in Minecraft.
- **Why:** Required dependency of Mod Menu.
- **Dependencies:** None
- **Conflicts:** None known

## Iron Chests
- **CurseForge ID:** 498534
- **Slug:** iron-chests-for-fabric
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds upgradeable chests (copper, iron, gold, emerald, diamond, crystal, obsidian) with increasing storage capacity.
- **Why:** Provides larger storage options without needing multiple vanilla chests, essential for organized SMP bases.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Metal Barrels
- **CurseForge ID:** 324985
- **Slug:** metal-barrels
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Adds upgradeable barrels (copper, iron, silver, gold, diamond, crystal, obsidian, netherite) with increasing storage capacity.
- **Why:** Complements Iron Chests with barrel variants that have no rendering overhead and can be opened with blocks on top.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Inventory Profiles Next
- **CurseForge ID:** 495267
- **Slug:** inventory-profiles-next
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Advanced inventory management with sorting, auto-refill, locked slots, gear sets, and more.
- **Why:** Comprehensive inventory QoL — auto-sorts, replaces broken tools, dumps items to chests, and locks slots for organized gameplay.
- **Dependencies:** Fabric API, Fabric Language Kotlin, libIPN, Mod Menu (optional)
- **Conflicts:** None known; may have keybind overlap with Mouse Tweaks (configure keybinds to avoid collision)

## libIPN
- **CurseForge ID:** 679177
- **Slug:** libipn
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** GUI and configuration library for Inventory Profiles Next and related mods.
- **Why:** Required dependency of Inventory Profiles Next.
- **Dependencies:** None
- **Conflicts:** None known

## Fabric Language Kotlin
- **CurseForge ID:** 308769
- **Slug:** fabric-language-kotlin
- **Mod Loader:** Fabric
- **Status:** included
- **Summary:** Enables Fabric mods written in Kotlin to run on the Fabric mod loader.
- **Why:** Required dependency of Inventory Profiles Next.
- **Dependencies:** None
- **Conflicts:** None known
