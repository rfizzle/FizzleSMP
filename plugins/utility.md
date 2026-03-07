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
