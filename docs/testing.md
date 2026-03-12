# FizzleSMP Testing Checklist

Systematic testing guide for verifying mod compatibility and pack stability. Run through these checks after adding new mods, updating existing ones, or before a release.

---

## 1. Startup & Loading

- [ ] **Clean launch** — Start the client with a fresh config. Confirm no crashes during loading screen.
- [ ] **Log audit** — Check `latest.log` for `ERROR` and `WARN` lines. Note any mixin conflicts, failed injections, or missing dependencies.
- [ ] **Mod menu verification** — Open Mod Menu and confirm all expected mods are listed and report correct versions.
- [ ] **Server launch** — Start a dedicated server with the same mod set. Confirm clean startup with no errors.

## 2. World Creation & Chunk Generation

These mods all touch worldgen and are the most likely to conflict: Terralith, Tectonic, Geophilic, Terraphilic, Incendium, Nullscape, C2ME, NoisiumForked, Sparse Structures, all YUNG's mods, Repurposed Structures, Towns and Towers, Structory, Explorify, MVS - Moog's Voyager Structures, MES - Moog's End Structures, MNS - Moog's Nether Structures, MSS - Moog's Soaring Structures, Philip's Ruins, Tidal Towns, Better Archeology, The Aether, Deeper and Darker.

- [ ] **New world creation** — Create a new world (default settings). Confirm no crash during initial chunk generation.
- [ ] **Explore Overworld biomes** — Fly/teleport through at least 10 different biomes. Confirm:
  - Terralith custom biomes generate correctly
  - Tectonic terrain (mountains, valleys) looks correct
  - Geophilic biome tweaks appear (check flower meadows, etc.)
  - No floating structures, cut-off terrain, or void holes
- [ ] **Structure generation** — Locate and enter at least one of each. To identify which mod generated a structure, use **MiniHUD's structure bounding box overlay** (Renderer Hotkeys → "Structure Bounding Boxes") which displays namespaced IDs like `explorify:ruin_plains` — the prefix before `:` is the mod. As a fallback, stand inside the structure and check the **F3 debug screen** (BetterF3) which shows the namespaced structure ID you're currently in.
  - Vanilla village (verify Towns and Towers expansions + Gazebos with Spell Binding Table)
  - YUNG's Better Dungeon
  - YUNG's Better Mineshaft
  - YUNG's Better Stronghold
  - YUNG's Better Desert Temple
  - YUNG's Better Ocean Monument
  - Repurposed Structures variant (biome-themed)
  - Structory tower or ruin
  - Hopo Underwater Ruins
  - Explorify structure (dungeon, ruin, or point of interest)
  - MVS - Moog's Voyager Structures (at least 2-3 different structure types)
  - Philip's Ruins ancient ruin
  - Tidal Towns floating ocean village
  - Better Archeology structure (confirm brush/archeology mechanic works)
- [ ] **Structure overlap** — Check that YUNG's dungeons and Repurposed Structures dungeons don't spawn on top of each other (RS dungeons/temples should be disabled in RS config).
- [ ] **Structure density** — With all structure mods active, fly across 2000+ blocks. Confirm Sparse Structures prevents oversaturation and structures don't generate on top of each other.
- [ ] **MSS - Moog's Soaring Structures** — Look for floating islands in the sky while exploring. Confirm structures generate with loot and mobs.
- [ ] **Nether generation** — Enter the Nether. Confirm Incendium biomes, custom structures, and YUNG's Better Nether Fortresses generate.
- [ ] **MNS - Moog's Nether Structures** — Explore the Nether beyond fortresses. Locate at least 1-2 MNS structures. Confirm loot and enemies spawn.
- [ ] **End generation** — Enter the End. Confirm Nullscape terrain and YUNG's Better End Island generate.
- [ ] **MES - Moog's End Structures** — Explore outer End islands. Locate at least 1-2 MES structures. Confirm loot and enemies spawn on Nullscape terrain.
- [ ] **The Aether dimension** — Build a glowstone portal and light it with a water bucket. Enter the Aether. Confirm:
  - Aether biomes generate correctly (floating islands, clouds)
  - Mobs spawn (Moas, Aechor Plants, Swets, Zephyrs)
  - Locate a Bronze Dungeon. Confirm structure generates with loot and boss
  - Loot chests work with Lootr (per-player instancing)
- [ ] **Deeper and Darker dimension** — Find an Ancient City. Locate the portal to The Otherside. Enter and confirm:
  - Sculk-themed biomes generate correctly
  - New blocks and mobs spawn
  - Loot containers work with Lootr (per-player instancing)
- [ ] **Chunk gen performance** — Use `/chunky start` to pre-generate a 1000-block radius. Monitor TPS with Spark. Confirm C2ME and NoisiumForked aren't conflicting (watch for deadlocks or errors in log).

## 3. Rendering & Visual Mods

Key mods: Sodium, Iris, ImmediatelyFast, Entity Culling, Continuity, LambDynamicLights, Visuality, Falling Leaves, Make Bubbles Pop, Not Enough Animations.

- [ ] **Sodium rendering** — Confirm smooth rendering with Sodium's default settings. Check no graphical glitches (z-fighting, missing faces).
- [ ] **Shader loading** — Enable Complementary Reimagined via Iris. Walk around and confirm:
  - Lighting and shadows render correctly
  - Water reflections work
  - No flickering or artifacts
  - Switch to Complementary Unbound and verify it also loads cleanly
- [ ] **Connected textures** — Place glass panes and bookshelves. Confirm Continuity connected textures work.
- [ ] **Dynamic lighting** — Hold a torch in hand (LambDynamicLights). Confirm light emits around the player and doesn't conflict with Sodium.
- [ ] **Entity culling** — Stand behind a wall near a mob farm. Open F3 and confirm entity render count drops (Entity Culling working).
- [ ] **Particles** — Break blocks near leaves (Falling Leaves), pop bubbles underwater (Make Bubbles Pop), hit entities (Visuality). Confirm particles render without lag spikes.
- [ ] **Animations** — Perform actions (eating, using bow, mining) and confirm Not Enough Animations adds proper third-person animations.

## 4. HUD & UI Mods

Key mods: Jade, MiniHUD, AppleSkin, Colorful Hearts, guy's Armor HUD, BetterF3, EMI, Mod Menu, Inventory Profiles Next, Mouse Tweaks, Blur+, Equipment Compare, Shulker Box Tooltip, Controlling.

- [ ] **Jade overlay** — Look at blocks and entities. Confirm Jade tooltip appears with correct info. Check Jade Addons shows info for modded blocks (Tech Reborn machines, Iron Chests, etc.).
- [ ] **MiniHUD** — Enable light level overlay and slime chunk overlay. Confirm they render without conflicts with BetterF3.
- [ ] **BetterF3** — Press F3 and confirm the custom debug screen displays properly (no overlap with MiniHUD elements).
- [ ] **Health display** — Take damage and confirm Colorful Hearts renders colored hearts in a single row. No overlap with guy's Armor HUD.
- [ ] **AppleSkin** — Hold food and confirm saturation/exhaustion overlays appear on the hunger bar.
- [ ] **Armor HUD** — Equip armor and confirm guy's Armor HUD shows durability correctly.
- [ ] **EMI** — Open inventory. Confirm EMI sidebar shows items. Search for a recipe. Verify:
  - EMI Loot shows mob drops
  - EMI Enchanting shows enchant info
  - EMI Ores shows ore distribution
  - EMI Addon shows Tech Reborn / Farmer's Delight recipes
  - EMIffect shows potion effects
  - EMI Professions shows villager trades
- [ ] **Inventory sorting** — Open a chest. Use Inventory Profiles Next sort button. Confirm it works without breaking Mouse Tweaks scroll behavior.
- [ ] **Mouse Tweaks** — Shift-click, scroll-transfer items between inventory and chest. Confirm no keybind conflicts with IPN.
- [ ] **Equipment Compare** — Hover over armor/weapons while wearing gear. Confirm comparison tooltip appears.
- [ ] **Shulker Box Tooltip** — Hover over a filled shulker box. Confirm contents display.
- [ ] **Blur+** — Open any GUI. Confirm background blurs without artifacts or performance drops.
- [ ] **Controlling** — Open Controls screen. Search for a keybind. Confirm Controlling search/filter works and Searchables integration functions.

## 5. Combat System

Key mods: Better Combat, Combat Roll, Simply Swords, SwingThrough, Spell Engine, Wizards, Paladins & Priests, Archers, Rogues & Warriors, Jewelry, Armory, Arsenal, Skill Tree, Gazebos, Critical Strike, playerAnimator, Bosses of Mass Destruction.

- [ ] **Better Combat animations** — Attack with sword, axe, and Simply Swords weapons. Confirm swing animations play correctly in both first and third person.
- [ ] **Simply Swords** — Craft or spawn a unique weapon (e.g., Emberblade). Confirm special abilities trigger. Verify pinned version (v1.62.0) loads without Simply Tooltips.
- [ ] **Combat Roll** — Double-tap movement key (or configured key). Confirm dodge animation plays and invincibility frames work.
- [ ] **SwingThrough** — Attack a mob through tall grass or vines. Confirm attacks connect through transparent blocks.
- [ ] **Spell casting** — Create a Wizard or Paladin loadout. Cast spells. Confirm Spell Engine and playerAnimator render spell effects and animations.
- [ ] **Archers class** — Craft an Archery Manual at a Spell Binding Table. Equip it with a bow. Cast archery skills (e.g., Multi Shot, Power Shot). Confirm skills fire correctly and animations play via playerAnimator.
- [ ] **Archers Auto-Fire Hook** — Craft an Auto-Fire Hook and attach it to a bow. Hold right-click and confirm automatic arrow release on full charge. Remove it via Grindstone.
- [ ] **Rogues class** — Craft a Rogue Manual at a Spell Binding Table. Equip with dual-wielded daggers/sickles. Cast rogue skills (evasion, backstab). Confirm animations and Better Combat dual wield integration.
- [ ] **Warriors class** — Craft a Warrior Codex. Equip with a heavy weapon (double axe, glaive). Cast warrior skills. Confirm heavy weapon swings and stagger effects.
- [ ] **Rogues & Warriors village structures** — Locate a village with a Barracks. Confirm rogue/warrior equipment is sold by villagers there.
- [ ] **Jewelry crafting** — Mine gem veins at diamond depth. Craft rings and necklaces. Equip in trinket/accessory slots. Confirm combat attribute bonuses apply.
- [ ] **Jewelry villagers** — Find a Jeweler villager in a village. Confirm jewelry items are available for trade.
- [ ] **Jewelry + Accessories Compat** — Equip jewelry alongside Artifacts trinkets. Confirm both work through Accessories Compatibility Layer without slot conflicts.
- [ ] **Armory upgrade** — Obtain a Superior Armor Upgrade and Upgrade Crystal from End City or Ancient City chests. Upgrade RPG class armor into epic variant. Confirm 3D model, set bonuses, and spell modifiers apply.
- [ ] **Arsenal legendary weapons** — Kill Ender Dragon, Wither, or Warden. Confirm Arsenal legendary weapons drop. Verify passive spells activate on use (Swirling, Shockwave, Radiance, etc.).
- [ ] **Arsenal + TieredZ** — Check if Arsenal legendary weapons receive TieredZ modifiers. Confirm passive spells still function with tier bonuses.
- [ ] **Skill Tree** — Press `K` to open skill tree. Spend XP-earned skill points on nodes. Confirm offensive/defensive specializations apply to class skills. Test spell modifier nodes that alter existing spells.
- [ ] **Skill Tree reset** — Obtain and use an Orb of Oblivion. Confirm all skill points are refunded and skills are reset.
- [ ] **Enchantment rebalancing** — Enchant a bow with Power V. Confirm damage bonus is +40% total (8%/level × 5) instead of vanilla +250%. Enchant a sword with Sharpness V. Confirm +40% instead of vanilla bonus.
- [ ] **Gazebos in villages** — Locate a village (plains, desert, savanna, snowy, or taiga). Confirm a gazebo structure generates containing a Spell Binding Table. Verify players can use it to create spell books.
- [ ] **Critical Strike melee** — Attack mobs with a melee weapon. Confirm RNG-based critical hits trigger with particle effects (sparkle/skull/circle) and sound effects. Verify base 5% crit chance and 1.5x damage.
- [ ] **Critical Strike ranged** — Shoot mobs with a bow/crossbow. Confirm ranged critical hits trigger with visual/audio feedback.
- [ ] **Critical Strike enchantments** — Enchant a weapon with Critical Hit and Critical Impact enchantments (via Enchanting Infuser or anvil). Confirm crit chance and damage increase per level.
- [ ] **Critical Strike + Better Combat** — Attack with Better Combat swing animations. Confirm crit particles and sounds trigger correctly alongside Better Combat's directional hit detection.
- [ ] **Critical Strike + vanilla jump crits** — Verify config option to toggle vanilla jump criticals. Confirm the chosen setting (enabled/disabled) works as expected.
- [ ] **Boss fights** — Spawn or locate a Bosses of Mass Destruction boss. Fight it and confirm:
  - Boss mechanics work (phases, special attacks)
  - Combat mods (Better Combat, Combat Roll) don't break boss AI
  - Loot drops correctly (including Arsenal weapons from applicable bosses)

## 6. Accessories & Equipment

Key mods: Accessories, Trinkets, Accessories Compatibility Layer, Artifacts, Things, Accessorify, Jewelry.

- [ ] **Accessories screen** — Open the Accessories GUI. Confirm slots render correctly (no crash — this was a previous bug with Simply Tooltips).
- [ ] **Trinkets via compat layer** — Equip a Trinkets-based item (e.g., from Artifacts). Confirm it works through the Accessories Compatibility Layer.
- [ ] **Artifacts** — Find or spawn an Artifact. Equip it and confirm the effect activates.
- [ ] **Things trinkets** — Equip a Things item. Confirm functionality.
- [ ] **Accessorify** — Equip a vanilla item as an accessory (e.g., spyglass). Confirm it appears and functions.
- [ ] **Slot conflicts** — Try equipping items from different mods in the same slot type. Confirm no crashes or visual glitches.

## 7. Gameplay & Content Mods

### Farming & Food
- [ ] **RightClickHarvest** — Plant and grow wheat. Right-click to harvest. Confirm auto-replant.
- [ ] **Farmer's Delight** — Craft a Cooking Pot. Cook a recipe. Confirm Chef's Delight professions appear on villagers.
- [ ] **RightClickHarvest + Supplementaries** — Grow Flax (Supplementaries). Confirm RightClickHarvest compat allows right-click harvest.
- [ ] **HT's TreeChop** — Chop a tree. Confirm gradual chopping animation and tree falls.

### Storage & Transport
- [ ] **Iron Chests** — Craft and place copper → obsidian chests. Confirm they open and store items.
- [ ] **Metal Barrels** — Craft metal barrels. Confirm no rendering issues.
- [ ] **Traveler's Backpack** — Craft a backpack. Open it, store items, verify fluid tank works.
- [ ] **Nether Chested** — Place linked chests in Overworld and Nether. Transfer items between dimensions.
- [ ] **Simple Conveyor Belts** — Place conveyor belts. Drop items and confirm transport.
- [ ] **Carry On + Lootr** — Pick up a Lootr chest with Carry On. Place it down. Check if per-player loot instancing is preserved (known soft conflict — may lose instancing).

### Tech & Machines
- [ ] **Tech Reborn** — Craft basic machines (Generator, Electric Furnace). Confirm power generation and processing work.
- [ ] **Oritech** — Craft Oritech machines. Confirm animations play and ore processing functions.
- [ ] **Tech Reborn + Oritech coexistence** — Confirm both tech mods load without recipe conflicts. Check Polymorph resolves any overlapping recipes.
- [ ] **Polymorph** — Craft an item with conflicting recipes (e.g., if both tech mods add a gear recipe). Confirm Polymorph popup lets you choose.

### Magic & Progression
- [ ] **Spectrum** — Begin Spectrum progression. Place a Pedestal, craft basic items. Confirm Revelationary block revelation works.
- [ ] **Enchanting Infuser** — Craft an Enchanting Infuser. Select specific enchantments. Confirm it works alongside Easy Magic's vanilla table improvements.
- [ ] **Easy Anvils** — Use an anvil with high-level enchants. Confirm "too expensive" limit is removed.
- [ ] **Grind Enchantments** — Use a grindstone to disenchant onto a book. Confirm transfer works.
- [ ] **XP Storage** — Craft XP Storage block. Store and retrieve XP.

### Mobs & Entities
- [ ] **Creeper Overhaul** — Find biome-specific creepers (desert, jungle, etc.). Confirm custom models render.
- [ ] **Creeper Healing + Creeper Overhaul** — Let a custom creeper explode. Confirm Creeper Healing regenerates the terrain.
- [ ] **Illager Invasion** — Find an illager structure or trigger a raid. Confirm new illager types spawn.
- [ ] **Friends & Foes** — Find Copper Golems, Glare, etc. Confirm they spawn and function.
- [ ] **Critters and Companions** — Find ambient critters. Confirm spawning and taming work.
- [ ] **Pickable Villagers** — Right-click a villager with empty hand (or configured item). Confirm pickup and re-placement.
- [ ] **Villager Names** — Check that villagers have random names displayed.
- [ ] **Let Me Despawn** — Give an item to a mob. Wait for despawn timer. Confirm it despawns (vanilla would keep it).

### Item Tiers & Reforging
- [ ] **TieredZ tier assignment** — Craft a diamond sword and pickaxe. Confirm each receives a random tier modifier with stat bonuses shown in the tooltip.
- [ ] **TieredZ reforging** — Place flint, a tiered item, and an amethyst shard in an anvil. Confirm reforging changes the tier and that higher-tier results become more likely with repeated reforges.
- [ ] **TieredZ + Simply Swords** — Craft or spawn a Simply Swords weapon. Confirm it receives a TieredZ modifier and that the weapon's special ability still functions correctly.
- [ ] **TieredZ + Mythic Upgrades** — Craft a Mythic Upgrades item (e.g., Ruby Sword). Confirm it receives a TieredZ tier and that the material bonuses stack correctly with the tier modifier.
- [ ] **TieredZ + Enchanting** — Apply enchantments to a tiered item via Enchanting Infuser and Easy Anvils. Confirm enchantments and tier modifiers coexist without issues.

### Death & Respawn
- [ ] **You're in Grave Danger** — Die with items. Return to death location. Confirm grave spawns and contains all items.
- [ ] **Grave + Lootr interaction** — Die near a Lootr chest. Confirm grave doesn't interfere with Lootr instancing.

## 8. Navigation & Travel

Key mods: Xaero's Minimap, Xaero's World Map, Waystones, Nature's Compass, Explorer's Compass, Traveler's Titles, NetherPortalFix.

- [ ] **Minimap** — Confirm Xaero's Minimap renders correctly. Check entity radar, waypoints.
- [ ] **World map** — Open Xaero's World Map. Confirm explored chunks display. Create a waypoint and navigate to it.
- [ ] **Waystones** — Craft and place a Waystone. Teleport between two waystones.
- [ ] **Nature's Compass** — Craft and use. Search for a biome. Confirm it locates correctly (including Terralith custom biomes).
- [ ] **Explorer's Compass** — Search for a structure. Confirm it finds both vanilla and modded structures (YUNG's, RS, etc.).
- [ ] **Traveler's Titles** — Enter a new biome. Confirm RPG-style title appears on screen.
- [ ] **NetherPortalFix** — Enter a nether portal from a non-standard location. Return and confirm you arrive at the correct overworld portal.
- [ ] **Open Parties and Claims** — Claim a chunk. Confirm claim appears on Xaero's Minimap (OPAC integration).

## 9. Audio Mods

Key mods: Sound Physics Remastered, Presence Footsteps, AmbientSounds, Simple Voice Chat.

- [ ] **Sound Physics** — Enter a cave. Confirm reverb and echo effects play. Stand behind a wall and confirm sound attenuation.
- [ ] **Presence Footsteps** — Walk on different surfaces (wood, stone, grass, sand). Confirm unique footstep sounds per material.
- [ ] **AmbientSounds** — Stand in a forest, near water, in a cave. Confirm ambient audio plays (birds, water, dripping).
- [ ] **Simple Voice Chat** — Connect two clients. Test proximity voice chat. Confirm Sound Physics Remastered integration (directional voice audio).

## 10. Server-Side & Admin Tools

Key mods: Ledger, Carpet, Connectivity, Chunky, Spark, Open Parties and Claims, No Chat Reports, Fabric Tailor.

- [ ] **Ledger** — Break/place blocks. Run `/ledger search` to confirm action logging. Test rollback on a small area.
- [ ] **Carpet** — Run a Carpet command (e.g., `/carpet setDefault simulationDistance 10`). Confirm it applies.
- [ ] **Spark** — Run `/spark profiler start`, wait 30 seconds, then `/spark profiler stop`. Review the report for abnormal tick times.
- [ ] **Chunky** — Run `/chunky start` for a small radius. Confirm chunks pre-generate without errors.
- [ ] **Connectivity + Krypton** — With both loaded, connect/disconnect from the server multiple times. Monitor for timeout errors or failed handshakes (known soft conflict — both modify networking).
- [ ] **No Chat Reports** — Send a chat message. Confirm the signature stripping icon appears and messages send successfully.
- [ ] **Fabric Tailor** — Change skin via command. Confirm it updates for other players.

## 11. Loot System

Key mods: Lootr, Better Loot, Loot Integrations, YUNG's Structures Addon for Loot Integrations, LI: CTOV, LI: Philip's Ruins, LI: Moog's Voyager, LI: Better Archeology.

- [ ] **Lootr instancing** — Open the same chest as two different players. Confirm each gets unique loot.
- [ ] **Better Loot tables** — Check loot quality in vanilla structures. Confirm Better Loot overhauls are present.
- [ ] **Loot Integrations** — Check loot in YUNG's structures. Confirm modded items (Simply Swords, Artifacts, Mythic Upgrades) appear in loot tables.
- [ ] **LI: CTOV addon** — Find a CTOV village or outpost chest. Confirm modded items appear in the loot (Simply Swords, Artifacts, etc.).
- [ ] **LI: Philip's Ruins addon** — Find a Philip's Ruins structure chest. Confirm modded items appear in loot tables.
- [ ] **LI: Moog's Voyager addon** — Find an MVS structure chest. Confirm modded items appear in loot tables.
- [ ] **LI: Better Archeology addon** — Find a Better Archeology structure chest. Confirm modded items appear in loot tables.
- [ ] **Mythic Upgrades** — Mine in deep caves. Confirm new ores generate. Craft endgame items.

## 12. Decorative & Building

Key mods: Chipped, Supplementaries, Supplementaries Squared, Handcrafted, Beautify: Refabricated, Every Compat.

- [ ] **Chipped** — Open a Chipped workbench. Browse decorative variants. Confirm textures render correctly.
- [ ] **Supplementaries** — Place jars, signposts, planters. Interact with each. Confirm functionality.
- [ ] **Supplementaries Squared** — Place additional variants. Confirm no visual issues.
- [ ] **Handcrafted** — Craft and place furniture: chair, table, couch, shelf, lamp. Confirm player can sit in chairs. Test multiple wood-type variants.
- [ ] **Beautify: Refabricated** — Place hanging planters, blinds, lamps, and trellis. Confirm textures render and interactions work.
- [ ] **Every Compat wood variants** — If modded wood types are available (e.g., from Terralith or Incendium), confirm Handcrafted, Supplementaries, and Chipped blocks have matching wood variants via Every Compat.
- [ ] **Handcrafted + Chipped coexistence** — Place furniture and decorative block variants in the same build. Confirm no visual or interaction conflicts.

## 13. Performance Stress Tests

- [ ] **Entity stress** — Spawn 200+ mobs in a small area. Monitor FPS and TPS. Confirm Entity Culling, Clumps (for XP), and Let Me Despawn help manage load.
- [ ] **Redstone stress** — Build a large redstone contraption. Confirm Lithium optimizations keep TPS stable.
- [ ] **Chunk loading stress** — Fly at high speed across unloaded chunks. Confirm C2ME + NoisiumForked handle parallel generation without crashes.
- [ ] **Memory usage** — After 30+ minutes of gameplay, check F3 memory. Confirm FerriteCore and ModernFix keep usage reasonable.
- [ ] **Background performance** — Alt-tab away from the game. Confirm Dynamic FPS reduces resource usage.

## 14. Cross-Mod Recipe & Item Conflicts

- [ ] **Recipe conflicts** — Search EMI for items with multiple recipes (gears, plates, rods). Confirm Polymorph resolves conflicts with a chooser popup.
- [ ] **Enchantment stacking** — Combine enchantments from different mods. Confirm no crashes or impossible combinations.
- [ ] **Trade Cycling** — Cycle villager trades. Confirm modded items appear in trade pools where expected.

## 15. Seasons

Key mods: Fabric Seasons, Fabric Seasons: Terralith Compat, Fabric Seasons: Delight Compat.

- [ ] **Season cycle** — Use `/season set` commands to cycle through Spring, Summer, Autumn, Winter. Confirm foliage colors change appropriately for each season.
- [ ] **Terralith biome seasons** — Visit Terralith custom biomes in different seasons. Confirm seasonal color changes apply (Terralith Compat working).
- [ ] **Crop growth rates** — Plant wheat and other vanilla crops. Change season to Winter. Confirm growth slows or stops. Change to Spring/Summer. Confirm growth resumes.
- [ ] **Farmer's Delight seasonal crops** — Plant Farmer's Delight crops (tomatoes, cabbages). Confirm they respect seasonal growth rates (Delight Compat working).
- [ ] **Falling Leaves + Seasons** — Check if Falling Leaves particle colors adjust with seasonal foliage changes.
- [ ] **Traveler's Titles + Seasons** — Enter a biome during different seasons. Confirm no visual conflicts with biome title display.
- [ ] **SeasonHud display** — Confirm current season displays on the HUD or under Xaero's Minimap. Cycle through seasons and verify the display updates.

## 16. Miscellaneous

- [ ] **Zoomify** — Press zoom key. Confirm smooth zoom without conflicts with Xaero's Minimap keybinds.
- [ ] **Better Than Mending** — Sneak + right-click with a Mending item and XP. Confirm instant repair.
- [ ] **Double Doors** — Open one side of a double door. Confirm both sides open simultaneously.
- [ ] **Magnum Torch** — Place a Magnum Torch. Confirm mob spawning stops in the configured radius.
- [ ] **Easy Mob Farm** — Place a mob farm block. Confirm mobs spawn and drops collect.
- [ ] **Steve's Realistic Sleep** — Sleep in a bed. Confirm time gradually advances rather than skipping instantly.
- [ ] **Modonomicon** — Open an in-game guide book (e.g., Spectrum's). Confirm pages render correctly.

---

## Regression Testing After Mod Updates

When updating mods, re-run the relevant sections above plus:

- [ ] **Config preservation** — Confirm existing configs aren't overwritten or reset.
- [ ] **World migration** — Load an existing world after updating. Confirm no chunk corruption or missing blocks.
- [ ] **Removed features** — If a mod removed features in an update, check that dependent mods handle the absence gracefully.

---

## How to Report Issues

When a test fails, document:

1. **Which test failed** (section and checkbox)
2. **Steps to reproduce**
3. **Expected vs. actual behavior**
4. **Relevant log lines** (from `latest.log` or crash report)
5. **Mod versions involved**

Add findings to `docs/compatibility-matrix.md` if a mod conflict is discovered.
