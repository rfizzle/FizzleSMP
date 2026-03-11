# Utility & Quality of Life

<!-- Mods for HUD, minimaps, inventory management, recipes, tooltips, and misc QoL. -->

## AppleSkin
- **CurseForge ID:** 248787
- **Slug:** appleskin
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds food/hunger-related HUD improvements including saturation and exhaustion visualization.
- **Why:** Gives players visibility into hidden hunger mechanics (saturation, exhaustion) so they can make informed food choices on the SMP.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Colorful Hearts
- **CurseForge ID:** 854213
- **Slug:** colorful-hearts
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Replaces multiple vanilla heart rows with a single row using colored hearts for cleaner health display.
- **Why:** Improves health readability when players have extra hearts from absorption or mods, reducing HUD clutter.
- **Dependencies:** Fabric API
- **Conflicts:** Hard conflict with Scaling Health (both modify heart rendering). Soft conflict with Overflowing Bars (requires disabling its heart rendering).

## Jade
- **CurseForge ID:** 324717
- **Slug:** jade
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Information HUD mod that shows details about the block or entity you are looking at (Hwyla/Waila fork).
- **Why:** Essential QoL for SMP players — quickly identify blocks, entities, and their properties without opening F3.
- **Dependencies:** None (JEI is optional for recipe integration)
- **Conflicts:** None known

## MiniHUD
- **CurseForge ID:** 244260
- **Slug:** minihud
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** A "mini F3" HUD mod with various overlays like light level, spawn chunks, slime chunks, and more.
- **Why:** Provides compact debug info and visual overlays (light levels, structure bounding boxes) invaluable for building and mob-proofing on the SMP.
- **Dependencies:** MaLiLib
- **Conflicts:** None known

## guy's Armor HUD
- **CurseForge ID:** 1388138
- **Slug:** guys-armor-hud
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds a vanilla-like widget showing equipped armor items with durability warnings when armor is low.
- **Why:** Keeps players aware of armor durability at a glance without opening inventory, important for SMP PvE and PvP.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## MaLiLib
- **CurseForge ID:** 303119
- **Slug:** malilib
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** A library mod containing shared code for masa's client-side mods (MiniHUD, Litematica, etc.).
- **Why:** Required dependency of MiniHUD.
- **Dependencies:** None
- **Conflicts:** None known

## Mouse Tweaks
- **CurseForge ID:** 60089
- **Slug:** mouse-tweaks
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Enhances inventory management by adding various functions to the mouse buttons.
- **Why:** Essential QoL for inventory management — adds RMB drag, LMB drag-splitting, and scroll wheel item movement.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Blur+
- **CurseForge ID:** 393563
- **Slug:** blur-fabric
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds a configurable Gaussian blur effect to the background of all GUI screens.
- **Why:** Provides a sleek, modern look to menus by blurring the game world behind GUIs.
- **Dependencies:** MidnightLib, Fabric API
- **Conflicts:** None known

## Continuity
- **CurseForge ID:** 531351
- **Slug:** continuity
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Enables connected textures, emissive textures, and custom block layers from OptiFine-format resource packs.
- **Why:** Allows resource packs with connected glass, bookshelves, etc. to work without OptiFine, complementing Sodium.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Mod Menu
- **CurseForge ID:** 308702
- **Slug:** modmenu
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds a mod list menu to view and configure installed mods.
- **Why:** Essential for accessing mod config screens in-game; used by many mods for settings integration.
- **Dependencies:** Fabric API, Text Placeholder API
- **Conflicts:** None known

## Cloth Config API
- **CurseForge ID:** 348521
- **Slug:** cloth-config
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Configuration library providing a standard config screen framework for Minecraft mods.
- **Why:** Required config library used by many mods for in-game settings screens.
- **Dependencies:** None
- **Conflicts:** None known

## MidnightLib
- **CurseForge ID:** 488090
- **Slug:** midnightlib
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A common library providing shared utilities for Minecraft mods.
- **Why:** Required dependency of Blur+.
- **Dependencies:** None
- **Conflicts:** None known

## Text Placeholder API
- **CurseForge ID:** 1037459
- **Slug:** text-placeholder-api
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** An API for creating and parsing text placeholders in Minecraft.
- **Why:** Required dependency of Mod Menu.
- **Dependencies:** None
- **Conflicts:** None known

## Iron Chests
- **CurseForge ID:** 498534
- **Slug:** iron-chests-for-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds upgradeable chests (copper, iron, gold, emerald, diamond, crystal, obsidian) with increasing storage capacity.
- **Why:** Provides larger storage options without needing multiple vanilla chests, essential for organized SMP bases.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Metal Barrels
- **CurseForge ID:** 324985
- **Slug:** metal-barrels
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds upgradeable barrels (copper, iron, silver, gold, diamond, crystal, obsidian, netherite) with increasing storage capacity.
- **Why:** Complements Iron Chests with barrel variants that have no rendering overhead and can be opened with blocks on top.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Inventory Profiles Next
- **CurseForge ID:** 495267
- **Slug:** inventory-profiles-next
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Advanced inventory management with sorting, auto-refill, locked slots, gear sets, and more.
- **Why:** Comprehensive inventory QoL — auto-sorts, replaces broken tools, dumps items to chests, and locks slots for organized gameplay.
- **Dependencies:** Fabric API, Fabric Language Kotlin, libIPN, Mod Menu (optional)
- **Conflicts:** None known; may have keybind overlap with Mouse Tweaks (configure keybinds to avoid collision)

## libIPN
- **CurseForge ID:** 679177
- **Slug:** libipn
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** GUI and configuration library for Inventory Profiles Next and related mods.
- **Why:** Required dependency of Inventory Profiles Next.
- **Dependencies:** None
- **Conflicts:** None known

## Fabric Language Kotlin
- **CurseForge ID:** 308769
- **Slug:** fabric-language-kotlin
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Enables Fabric mods written in Kotlin to run on the Fabric mod loader.
- **Why:** Required dependency of Inventory Profiles Next and Zoomify.
- **Dependencies:** None
- **Conflicts:** None known

## Equipment Compare
- **CurseForge ID:** 541329
- **Slug:** equipment-compare-fabric
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Shows a comparison tooltip of currently equipped gear alongside hovered items for easy stat comparison.
- **Why:** Essential QoL for comparing weapons and armor at a glance, especially valuable with Simply Swords and Mythic Upgrades adding many new items.
- **Dependencies:** Fabric API, Iceberg
- **Conflicts:** None known; client-side only.

## Iceberg
- **CurseForge ID:** 539382
- **Slug:** iceberg-fabric
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** A modding library providing common utilities for Grend's mods.
- **Why:** Required dependency of Equipment Compare.
- **Dependencies:** None
- **Conflicts:** None known

## Traveler's Titles
- **CurseForge ID:** 590990
- **Slug:** travelers-titles-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Displays RPG-like title text when entering new biomes or dimensions, fully customizable.
- **Why:** Adds immersive biome/dimension discovery feel to exploration; pairs with Visual Traveler's Titles resource pack for image-based titles.
- **Dependencies:** YUNG's API, Fabric API, Cloth Config API
- **Conflicts:** None known

## Enchantment Descriptions
- **CurseForge ID:** 250419
- **Slug:** enchantment-descriptions
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds plain-text descriptions of enchantment effects to enchanted item tooltips.
- **Why:** Helps SMP players understand what each enchantment does at a glance without alt-tabbing to a wiki.
- **Dependencies:** Fabric API, Bookshelf, Prickle
- **Conflicts:** None known

## Bookshelf
- **CurseForge ID:** 228525
- **Slug:** bookshelf
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** An open-source library mod providing shared utilities for DarkhaxDev's mods.
- **Why:** Required dependency of Enchantment Descriptions.
- **Dependencies:** None
- **Conflicts:** None known

## Prickle
- **CurseForge ID:** 1023259
- **Slug:** prickle
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A JSON-based configuration file format library for Minecraft mods.
- **Why:** Required dependency of Enchantment Descriptions.
- **Dependencies:** None
- **Conflicts:** None known

## Waystones
- **CurseForge ID:** 245755
- **Slug:** waystones
- **Modrinth Slug:** waystones
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds craftable waystones for fast travel, with natural spawning in villages, scrolls, and configurable teleportation costs.
- **Why:** Provides a balanced fast-travel system for the SMP — players discover waystones by exploring villages and can craft their own for base networks. Replaces the previously maintained FabricWaystones fork with the actively maintained upstream mod by BlayTheNinth.
- **Dependencies:** Balm, Fabric API
- **Conflicts:** None known

## Balm
- **CurseForge ID:** 500525
- **Slug:** balm-fabric
- **Modrinth Slug:** balm
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A cross-platform abstraction layer library used by BlayTheNinth's mods.
- **Why:** Required dependency of Waystones.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Lavender
- **CurseForge ID:** 962916
- **Slug:** lavender-api
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A guidebook API and alternative to Patchouli, enabling in-game documentation with markdown syntax and hot-reloading.
- **Why:** Required dependency of Things for its in-game guidebook.
- **Dependencies:** None
- **Conflicts:** None known

## Xaero's Minimap
- **CurseForge ID:** 263420
- **Slug:** xaeros-minimap
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds a self-writing minimap with waypoints, entity radar, cave maps, and customizable appearance.
- **Why:** Essential navigation tool for SMP — helps players find their way, set waypoints for bases and points of interest, and coordinate with others.
- **Dependencies:** Fabric API; Open Parties and Claims (optional, for claim overlay)
- **Conflicts:** None known

## Xaero's World Map
- **CurseForge ID:** 317780
- **Slug:** xaeros-world-map
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds a full-screen world map showing explored terrain, designed to work alongside Xaero's Minimap.
- **Why:** Complements Xaero's Minimap with a full-screen map view for planning routes, reviewing explored territory, and managing waypoints on the SMP.
- **Dependencies:** Fabric API; Open Parties and Claims (optional, for claim overlay)
- **Conflicts:** None known

## Puzzles Lib
- **CurseForge ID:** 495476
- **Slug:** puzzles-lib
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A shared library mod providing common utilities for Fuzs' mods.
- **Why:** Required dependency of Enchanting Infuser and Easy Magic.
- **Dependencies:** None
- **Conflicts:** None known

## YetAnotherConfigLib (YACL)
- **CurseForge ID:** 667299
- **Slug:** yacl
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A builder-based configuration library with a rich GUI for Minecraft mods.
- **Why:** Required dependency of Zoomify.
- **Dependencies:** None
- **Conflicts:** None known

## Zoomify
- **CurseForge ID:** 574741
- **Slug:** zoomify
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** A zoom mod with infinite customizability.
- **Why:** Gives players a configurable zoom key for spotting distant terrain, builds, and mobs on the SMP.
- **Dependencies:** Fabric API, Fabric Language Kotlin, YetAnotherConfigLib (YACL)
- **Conflicts:** None known

## BetterF3
- **CurseForge ID:** 401648
- **Slug:** betterf3
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Replaces Minecraft's original debug HUD with a highly customizable, more human-readable HUD.
- **Why:** Makes the F3 debug screen easier to read with color-coded modules, customizable layout, and cleaner presentation.
- **Dependencies:** Cloth Config API
- **Conflicts:** None known

## Polymorph
- **CurseForge ID:** 388800
- **Slug:** polymorph
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Solves recipe conflicts by letting players choose between all potential outputs shared by the same ingredients.
- **Why:** Essential with a large modpack — resolves crafting, smelting, and smithing recipe conflicts from mods like Tech Reborn, Oritech, and Mythic Upgrades.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## EMI
- **CurseForge ID:** 580555
- **Slug:** emi
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** A featureful and accessible item and recipe viewer for browsing recipes, usages, and crafting trees.
- **Why:** Essential QoL for a large modpack — lets players look up recipes and usages for all modded items without alt-tabbing to a wiki.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## EMI Addon: Extra Mod Integrations
- **CurseForge ID:** 739970
- **Slug:** extra-mod-integrations
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds EMI recipe support for additional mods including Tech Reborn, Farmers' Delight, and more.
- **Why:** Extends EMI with recipe integration for Tech Reborn and other mods in the pack.
- **Dependencies:** EMI, Fabric API
- **Conflicts:** None known

## EMI Loot
- **CurseForge ID:** 681783
- **Slug:** emi-loot
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Displays mob loot tables and chest loot tables directly in EMI.
- **Why:** Lets players see what mobs drop and what loot chests contain in EMI, reducing wiki dependency.
- **Dependencies:** EMI, Fzzy Config
- **Conflicts:** None known

## EMI professions (EMIP)
- **CurseForge ID:** 1065904
- **Slug:** emi-professions-emip
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** An EMI addon that shows villager profession workstations — press Uses (U) on an Emerald or Villager Spawn Egg to see all professions.
- **Why:** Lets players quickly look up which workstation corresponds to each villager profession directly in EMI.
- **Dependencies:** EMI
- **Conflicts:** None known

## EMIffect
- **CurseForge ID:** 735528
- **Slug:** emiffect-status-effects-emi-plugin
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** An EMI addon that displays status effects in EMI with detailed information about each effect.
- **Why:** Lets players browse and understand all status effects (vanilla and modded) directly in EMI.
- **Dependencies:** EMI, Fabric API
- **Conflicts:** None known

## Nether Chested
- **CurseForge ID:** 857971
- **Slug:** nether-chested
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a linked chest block that shares its inventory across dimensions, allowing cross-dimension item transfer.
- **Why:** Enables cross-dimension item transfer for SMP players working across Overworld, Nether, and End.
- **Dependencies:** Fabric API, Puzzles Lib, Forge Config API Port
- **Conflicts:** None known

## Forge Config API Port
- **CurseForge ID:** 547434
- **Slug:** forge-config-api-port
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Ports the NeoForge/Forge config API to Fabric, allowing mods to use Forge-style configuration files.
- **Why:** Required dependency of Enchanting Infuser and Easy Magic.
- **Dependencies:** None
- **Conflicts:** None known

## CreativeCore
- **CurseForge ID:** 257814
- **Slug:** creativecore
- **Modrinth Slug:** creativecore
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A core library mod required by CreativeMD's mods including AmbientSounds.
- **Why:** Required dependency of AmbientSounds.
- **Dependencies:** None
- **Conflicts:** None known

## Paxi
- **CurseForge ID:** 418881
- **Slug:** paxi-fabric
- **Modrinth Slug:** paxi
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Automatic data & resource pack loading; places packs in config/paxi/ for global loading across all worlds.
- **Why:** Required for packwiz to install datapack-type mods (e.g., Terraphilic).
- **Dependencies:** None
- **Conflicts:** None known

## Visuality
- **CurseForge ID:** 521126
- **Slug:** visuality
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds a variety of new particles to enhance visual feedback — hit particles, enchantment glints, mob-specific effects, and more.
- **Why:** Subtle visual polish that makes combat and interactions feel more satisfying with particle effects.
- **Dependencies:** Fabric API, Cloth Config API
- **Conflicts:** None known

## Falling Leaves
- **CurseForge ID:** 417510
- **Slug:** falling-leaves-fabric
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds falling leaf particles beneath leaf blocks for a more atmospheric world.
- **Why:** Enhances world ambiance with gentle leaf particle effects that complement the SMP's visual style.
- **Dependencies:** Fabric API, Cloth Config API
- **Conflicts:** None known

## Make Bubbles Pop
- **CurseForge ID:** 605235
- **Slug:** make-bubbles-pop
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Makes bubble particles pop and rise realistically to the water surface instead of disappearing.
- **Why:** Small visual polish that makes underwater environments feel more alive and realistic.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## Not Enough Animations
- **CurseForge ID:** 433760
- **Slug:** not-enough-animations
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds missing third-person animations for actions like eating, drinking, using maps, crossbows, and more.
- **Why:** Makes third-person view much more expressive and readable for other players on the SMP.
- **Dependencies:** Fabric API
- **Conflicts:** None known


## Nature's Compass
- **CurseForge ID:** 252848
- **Slug:** natures-compass
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a craftable compass that locates any biome in the world.
- **Why:** Essential exploration tool for SMP players searching for specific biomes for resources or building locations.
- **Dependencies:** None
- **Conflicts:** None known

## Explorer's Compass
- **CurseForge ID:** 491794
- **Slug:** explorers-compass
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds a craftable compass that locates any structure in the world, including modded structures.
- **Why:** Helps SMP players find specific structures without aimless wandering; works with modded structures from YUNG's suite, Repurposed Structures, etc.
- **Dependencies:** None
- **Conflicts:** None known

## Carry On
- **CurseForge ID:** 274259
- **Slug:** carry-on
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Allows picking up and carrying tile entities (chests, furnaces, etc.) and mobs with bare hands.
- **Why:** Convenient way to relocate blocks with inventories and mobs without breaking/killing them on the SMP.
- **Dependencies:** None
- **Conflicts:** Soft conflict with Lootr — carried Lootr chests may not open properly for other players (see compatibility matrix).

## Sound Physics Remastered
- **CurseForge ID:** 535489
- **Slug:** sound-physics-remastered
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Provides realistic sound attenuation, reverberation, and absorption through blocks.
- **Why:** Adds immersive audio with realistic reverb in caves, indoors, and large spaces.
- **Dependencies:** None (optional: Mod Menu, Cloth Config — both already included)
- **Conflicts:** None known

## Presence Footsteps
- **CurseForge ID:** 334259
- **Slug:** presence-footsteps
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds dynamic, material-based footstep sounds for every block the player walks on.
- **Why:** Enhances immersion with detailed footstep audio that matches block materials.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## AmbientSounds
- **CurseForge ID:** 254284
- **Slug:** ambientsounds
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds rich ambient environmental sounds to the Minecraft world based on biome and surroundings.
- **Why:** Creates an immersive soundscape with nature sounds, wind, and environmental audio.
- **Dependencies:** CreativeCore, Fabric API
- **Conflicts:** None known

## Better Than Mending
- **CurseForge ID:** 264738
- **Slug:** better-than-mending
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Sneak-right-click a Mending-enchanted item to instantly repair it using your stored XP levels.
- **Why:** Lets players repair Mending gear on-demand instead of waiting at an XP farm, a simple but essential QoL improvement.
- **Dependencies:** None
- **Conflicts:** None known

## Shulker Box Tooltip
- **CurseForge ID:** 315811
- **Slug:** shulkerboxtooltip
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Shows the contents of shulker boxes as a visual tooltip when hovering over them in inventory.
- **Why:** Essential QoL for shulker box organization — see contents at a glance without placing and opening each box.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## XP Storage
- **CurseForge ID:** 403434
- **Slug:** xp-storage
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds blocks and items that let players safely store and retrieve experience points.
- **Why:** Lets SMP players bank XP safely to avoid losing it on death, complementing Better Than Mending for on-demand repairs.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## NetherPortalFix
- **CurseForge ID:** 241160
- **Slug:** netherportalfix
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Ensures correct destinations when travelling back and forth through Nether Portals in multiplayer.
- **Why:** Fixes a vanilla multiplayer bug where nether portals send players to the wrong destination, essential for SMP portal networks.
- **Dependencies:** Balm, Fabric API
- **Conflicts:** None known

## Balm
- **CurseForge ID:** 500525
- **Slug:** balm-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** A cross-platform abstraction library providing shared utilities for BlayTheNinth's mods.
- **Why:** Required dependency of NetherPortalFix.
- **Dependencies:** Fabric API
- **Conflicts:** None known

## LambDynamicLights
- **CurseForge ID:** 393442
- **Slug:** lambdynamiclights
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds dynamic lighting so held torches, glowstone, and other light-emitting items illuminate surroundings in real time.
- **Why:** Enhances exploration immersion — torches light up caves as you walk, lava illuminates surroundings, and dropped glowing items cast light.
- **Dependencies:** Fabric API
- **Conflicts:** Hard conflict with Sodium Dynamic Lights and RyoamicLights (neither in pack).

## Jade Addons (Fabric)
- **CurseForge ID:** 656665
- **Slug:** jade-addons-fabric
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Adds Jade tooltip/HUD integration for blocks and entities from supported mods including Lootr, Supplementaries, and Artifacts.
- **Why:** Extends Jade with mod-specific tooltips for several mods already in the pack (Lootr, Supplementaries, Artifacts), improving information display.
- **Dependencies:** Jade
- **Conflicts:** None known

## EMI Enchanting
- **CurseForge ID:** 936713
- **Slug:** emi-enchanting
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** An EMI plugin that displays enchantment information including valid items, exclusions, curse status, tradability, and max level.
- **Why:** Lets players browse enchantment details directly in EMI, complementing the existing EMI addon suite.
- **Dependencies:** EMI
- **Conflicts:** None known

## EMI Ores
- **CurseForge ID:** 974009
- **Slug:** emi-ores
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** An EMI plugin that displays ore generation information including Y-level distribution, vein size, and biome restrictions.
- **Why:** Lets players look up ore spawn levels and distribution in EMI, especially useful with modded ores from Tech Reborn, Oritech, and Mythic Upgrades.
- **Dependencies:** EMI, Fabric API
- **Conflicts:** None known

## Controlling
- **CurseForge ID:** 250398
- **Slug:** controlling
- **Modrinth Slug:** controlling
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** Adds a search bar to the Key-Bindings menu, with options to filter by conflicts and unbound keys.
- **Why:** Essential QoL for a large modpack with many keybind additions — quickly find, search, and resolve keybind conflicts.
- **Dependencies:** Fabric API, Searchables
- **Conflicts:** None known

## Searchables
- **CurseForge ID:** 858542
- **Slug:** searchables
- **Modrinth Slug:** searchables
- **Mod Loader:** Fabric
- **Side:** client
- **Summary:** A library mod providing search, filter, and auto-complete helper methods for UI elements.
- **Why:** Required dependency of Controlling.
- **Dependencies:** None
- **Conflicts:** None known

## Double Doors
- **CurseForge ID:** 348831
- **Slug:** double-doors
- **Modrinth Slug:** double-doors
- **Mod Loader:** Fabric
- **Side:** both
- **Summary:** Allows identical double doors, trapdoors, and fence gates to be opened and closed simultaneously.
- **Why:** Simple QoL improvement — clicking one side of a double door opens both, also works with trapdoors and fence gates.
- **Dependencies:** Collective
- **Conflicts:** None known
