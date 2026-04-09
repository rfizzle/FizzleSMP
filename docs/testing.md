# FizzleSMP Testing Checklist

Systematic testing guide for verifying mod compatibility and pack stability. Run through these checks after adding new mods, updating existing ones, or before a release.

---

## 1. Startup & Loading

- [ ] **Clean launch** — Start the client with a fresh config. Confirm no crashes during loading screen.
- [ ] **Log audit** — Check `latest.log` for `ERROR` and `WARN` lines. Note any mixin conflicts, failed injections, or missing dependencies.
- [ ] **Mod menu verification** — Open Mod Menu and confirm all expected mods are listed and report correct versions.
- [ ] **Server launch** — Start a dedicated server with the same mod set. Confirm clean startup with no errors.

## 2. World Creation & Chunk Generation

These mods all touch worldgen and are the most likely to conflict: Terralith, Tectonic, Geophilic, Terraphilic, Incendium, Nullscape, C2ME, NoisiumForked, Sparse Structures, all YUNG's mods, Explorify, MVS - Moog's Voyager Structures, MES - Moog's End Structures, MNS - Moog's Nether Structures, MSS - Moog's Soaring Structures, Philip's Ruins, Tidal Towns, The Aether, Deeper and Darker.

- [ ] **No experimental warning** — Create a new world. Confirm the "Experimental Settings" warning screen does NOT appear (Disable Custom Worlds Advice suppresses it).
- [ ] **New world creation** — Create a new world (default settings). Confirm no crash during initial chunk generation.
- [ ] **Explore Overworld biomes** — Fly/teleport through at least 10 different biomes. Confirm:
  - Terralith custom biomes generate correctly
  - Tectonic terrain (mountains, valleys) looks correct
  - Geophilic biome tweaks appear (check flower meadows, etc.)
  - No floating structures, cut-off terrain, or void holes
- [ ] **Structure generation** — Locate and enter at least one of each. To identify which mod generated a structure, use **MiniHUD's structure bounding box overlay** (Renderer Hotkeys → "Structure Bounding Boxes") which displays namespaced IDs like `explorify:ruin_plains` — the prefix before `:` is the mod. As a fallback, stand inside the structure and check the **F3 debug screen** (BetterF3) which shows the namespaced structure ID you're currently in.
  - Vanilla village (verify CTOV overhauls + Gazebos with Spell Binding Table)
  - YUNG's Better Dungeon
  - YUNG's Better Mineshaft
  - YUNG's Better Stronghold
  - YUNG's Better Desert Temple
  - YUNG's Better Ocean Monument
  - Hopo Underwater Ruins
  - Explorify structure (dungeon, ruin, or point of interest)
  - MVS - Moog's Voyager Structures (at least 2-3 different structure types)
  - Philip's Ruins ancient ruin
  - Tidal Towns floating ocean village
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
- [ ] **C2ME parallel chunk gen** — Create a new world and fly rapidly across terrain. Monitor TPS with Spark. Confirm C2ME parallelizes chunk generation across CPU cores without errors. Check `latest.log` for any thread-safety exceptions.
- [ ] **C2ME + Lithium coexistence** — With both mods active, generate chunks in a fresh world near sculk sensors/wardens. Confirm no `ArrayIndexOutOfBoundsException` from `GameEventDispatcherStorage`. If issues arise, disable Lithium's `mixin.world.game_events` in `modpack/config/lithium.properties`.
- [ ] **C2ME + NoisiumForked coexistence** — Pre-generate 1000-block radius with `/chunky start`. Confirm both optimizations stack (C2ME threading + NoisiumForked algorithms) without conflicts or log errors.
- [ ] **Chunk gen performance** — Use `/chunky start` to pre-generate a 1000-block radius. Monitor TPS with Spark. Confirm NoisiumForked generates chunks without errors in log.

## 3. Rendering & Visual Mods

Key mods: Sodium, Sodium Extra, Iris, ImmediatelyFast, Entity Culling, Enhanced Block Entities, More Culling, Continuity, LambDynamicLights, Visuality, Falling Leaves, Not Enough Animations.

- [ ] **Sodium rendering** — Confirm smooth rendering with Sodium's default settings. Check no graphical glitches (z-fighting, missing faces).
- [ ] **Shader loading** — Enable Complementary Reimagined via Iris. Walk around and confirm:
  - Lighting and shadows render correctly
  - Water reflections work
  - No flickering or artifacts
  - Switch to Complementary Unbound and verify it also loads cleanly
- [ ] **Connected textures** — Place glass panes and bookshelves. Confirm Continuity connected textures work.
- [ ] **Dynamic lighting** — Hold a torch in hand (LambDynamicLights). Confirm light emits around the player and doesn't conflict with Sodium.
- [ ] **Enhanced Block Entities** — Place several chests, signs, beds, and shulker boxes in a room. Confirm they render correctly (no invisible or flickering blocks). Compare FPS with EBE enabled vs disabled in a storage room with 50+ chests.
- [ ] **More Culling** — Stand inside a dense forest (leaf blocks). Confirm FPS improvement from hidden leaf face culling. Check no visual artifacts on block edges.
- [ ] **Sodium Extra toggles** — Open Sodium settings. Confirm Sodium Extra adds toggles for animations, particles, weather, fog, and FPS display. Toggle each setting and verify the change takes effect in-game.
- [ ] **Entity culling** — Stand behind a wall near a mob farm. Open F3 and confirm entity render count drops (Entity Culling working).
- [ ] **Particles** — Break blocks near leaves (Falling Leaves), hit entities (Visuality). Confirm particles render without lag spikes.
- [ ] **Animations** — Perform actions (eating, using bow, mining) and confirm Not Enough Animations adds proper third-person animations.

## 4. HUD & UI Mods

Key mods: Jade, MiniHUD, AppleSkin, Colorful Hearts, guy's Armor HUD, BetterF3, EMI, Mod Menu, Mouse Tweaks, Crafting Tweaks, Inventory Essentials, Blur+, Legendary Tooltips, Item Borders, Shulker Box Tooltip, Controlling.

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
- [ ] **Mouse Tweaks** — Shift-click, scroll-transfer items between inventory and chest. Confirm drag-splitting and scroll behavior work correctly.
- [ ] **Crafting Tweaks — grid buttons** — Open a crafting table. Confirm rotate, balance, and clear buttons appear next to the crafting grid. Test each button with items in the grid.
- [ ] **Crafting Tweaks — shift-click partial stack fix** — Fill inventory completely except for one partial stack of a craftable item (e.g., 32 sticks). Place the recipe in a crafting table. Shift-click the output. Confirm crafted items merge into the existing partial stack instead of failing silently.
- [ ] **Crafting Tweaks — right-click craft stack** — Place a recipe in the crafting table. Right-click the output slot. Confirm it crafts the maximum possible amount.
- [ ] **Inventory Essentials — Ctrl-click** — Open a chest. Ctrl-click an item. Confirm only a single item transfers (instead of the full stack).
- [ ] **Crafting Tweaks + Mouse Tweaks coexistence** — With both mods active, test drag-splitting in a crafting grid and shift-clicking output. Confirm no keybind conflicts or unexpected behavior.
- [ ] **Legendary Tooltips** — Hover over items of different rarities (common, uncommon, rare, epic). Confirm tooltip borders and backgrounds change style/color based on rarity tier. Check that Enchantment Descriptions text and Shulker Box Tooltip previews render correctly inside styled frames.
- [ ] **Item Borders** — Open inventory with items of different rarities. Confirm colored glowing borders appear around inventory slots matching each item's rarity tier.
- [ ] **Legendary Tooltips + Item Borders integration** — Confirm tooltip styling and inventory slot borders use consistent rarity color scheme. Check that both activate simultaneously without visual glitches.
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
- [ ] **Arsenal loot drops** — Confirm Arsenal legendary weapons drop from bosses and end-game chests. Verify passive spells activate on use.
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
- [ ] **JustHammers crafting** — Craft an Iron Hammer and a Diamond Hammer. Confirm recipes appear in EMI and crafting succeeds.
- [ ] **JustHammers mining radius** — Mine stone with an Iron Hammer (3x3), Gold Hammer (3x3x3), Diamond Hammer (5x5), and Netherite Hammer (5x5x5). Confirm each tier mines the correct area.
- [ ] **JustHammers durability** — Use a hammer until it reaches 1 durability. Confirm it stops working but does not break. Repair it in an anvil and confirm it functions again.
- [ ] **JustHammers area-mining** — Craft a hammer and confirm area-mining functions correctly across all tiers.
- [ ] **JustHammers enchanting** — Enchant a hammer with Efficiency and Unbreaking via Enchanting Infuser or anvil. Confirm enchantments apply and function during multi-block mining.

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

### Mob Difficulty & Mini-Bosses
- [ ] **Illager Invasion structures** — Explore the overworld for new illager structures. Confirm new illager types spawn and have unique behaviors.
- [ ] **Illager Invasion raids** — Trigger a village raid. Confirm new illager types participate and expanded raid mechanics function.
- [ ] **Illager Invasion + Friends & Foes** — Trigger a raid with both mods loaded. Confirm no duplicate or conflicting illager-adjacent mobs in raid waves.
- [ ] **Mutant Monsters spawning** — Explore the overworld. Confirm mutant variants (Mutant Zombie, Mutant Creeper, Mutant Skeleton, etc.) spawn naturally as mini-bosses.
- [ ] **Mutant Monsters combat** — Fight a mutant mob. Confirm unique attacks, animations, and drops function correctly.
- [ ] **Mutant Monsters + Creeper Overhaul** — Confirm Mutant Creeper spawns independently of Creeper Overhaul's biome variants.
- [ ] **Hostile Mobs Improve Over Time** — Play through several in-game days. Confirm hostile mobs progressively gain stronger stats (more HP, damage, armor). Check config for tuning scaling rate.

### Enchantment Expansion
- [ ] **NeoEnchant+ enchantments** — Open an enchanting table or Enchanting Infuser. Confirm NeoEnchant+ enchantments (Fury, Life+, Bright Vision, Builder Arms, Rebound) appear in the enchantment pool and can be applied to gear.
- [ ] **NeoEnchant+ + Enchanting Infuser** — Use the Enchanting Infuser to select a NeoEnchant+ enchantment. Confirm it applies correctly and the effect activates in-game.
- [ ] **NeoEnchant+ + Grind Enchantments** — Disenchant a NeoEnchant+ enchantment via grindstone onto a book. Confirm the enchantment transfers successfully.
- [ ] **NeoEnchant+ + Enchantment Descriptions** — Hover over an item with a NeoEnchant+ enchantment. Confirm the description text appears in the tooltip.
- [ ] **BeyondEnchant level caps** — Enchant a sword with Sharpness. Confirm the cap exceeds vanilla level V (up to VII). Repeat with Efficiency (up to X) and Protection (up to V).
- [ ] **BeyondEnchant + Easy Anvils** — Combine high-level BeyondEnchant enchantments on an anvil. Confirm Easy Anvils removes the "too expensive" restriction and allows the combination.
- [ ] **BeyondEnchant + Archers rebalancing** — Enchant a bow with Power beyond vanilla cap. Confirm Archers' +8%/level scaling applies to the higher levels (not vanilla +50%/level).
- [ ] **BeyondEnchant + Rogues & Warriors rebalancing** — Enchant a sword with Sharpness beyond vanilla cap. Confirm R&W's +8%/level scaling applies to the higher levels.
- [ ] **NeoEnchant+ + BeyondEnchant together** — Load both mods simultaneously. Confirm new enchantments from NeoEnchant+ and raised caps from BeyondEnchant both function without conflicts.

### Death & Respawn
- [ ] **You're in Grave Danger** — Die with items. Return to death location. Confirm grave spawns and contains all items.
- [ ] **Grave + Lootr interaction** — Die near a Lootr chest. Confirm grave doesn't interfere with Lootr instancing.

## 8. Navigation & Travel

Key mods: Xaero's Minimap, Xaero's World Map, Waystones, Nature's Compass, Explorer's Compass, Traveler's Titles, NetherPortalFix.

- [ ] **Minimap** — Confirm Xaero's Minimap renders correctly. Check entity radar, waypoints.
- [ ] **World map** — Open Xaero's World Map. Confirm explored chunks display. Create a waypoint and navigate to it.
- [ ] **Waystones** — Craft and place a Waystone. Teleport between two waystones.
- [ ] **Nature's Compass** — Craft and use. Search for a biome. Confirm it locates correctly (including Terralith custom biomes).
- [ ] **Explorer's Compass** — Search for a structure. Confirm it finds both vanilla and modded structures (YUNG's, CTOV, Moog's, etc.).
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

Key mods: Ledger, Connectivity, Chunky, Spark, Open Parties and Claims, No Chat Reports, Fabric Tailor, Neruina, Not Enough Crashes, MixinTrace.

- [ ] **Ledger** — Break/place blocks. Run `/ledger search` to confirm action logging. Test rollback on a small area.
- [ ] **Spark** — Run `/spark profiler start`, wait 30 seconds, then `/spark profiler stop`. Review the report for abnormal tick times.
- [ ] **Chunky** — Run `/chunky start` for a small radius. Confirm chunks pre-generate without errors.
- [ ] **Connectivity + Krypton** — With both loaded, connect/disconnect from the server multiple times. Monitor for timeout errors or failed handshakes (known soft conflict — both modify networking).
- [ ] **No Chat Reports** — Send a chat message. Confirm the signature stripping icon appears and messages send successfully.
- [ ] **Fabric Tailor** — Change skin via command. Confirm it updates for other players.
- [ ] **Neruina ticking entity catch** — Spawn or encounter a ticking entity crash scenario (e.g., modded entity with broken AI). Confirm Neruina catches the crash, logs the offending entity, and removes it instead of crashing the server. Check `/neruina` command for entity kill log.
- [ ] **Not Enough Crashes recovery** — If a client crash occurs, confirm the game returns to the title screen instead of closing. Check that the crash report identifies the responsible mod.
- [ ] **MixinTrace in crash reports** — After any crash, open the crash report. Confirm mixin class names appear in the stack trace with mod attribution (e.g., `at modid.mixin.ClassName`).
- [ ] **Carpet loading** — Confirm Carpet loads on the server. Run `/carpet list` to verify the rule list is accessible.
- [ ] **Carpet rules** — Toggle a rule (e.g., `/carpet setDefault commandPlayer true`). Confirm it takes effect. Reset it and confirm it reverts.
- [ ] **Carpet + C2ME coexistence** — With both mods loaded, fly rapidly across unloaded chunks while Carpet rules are active. Monitor TPS with Spark. Confirm no thread-safety exceptions in `latest.log`.
- [ ] **Carpet + Lithium coexistence** — With both mods loaded, run a Spark profiler during normal gameplay. Confirm both optimizations stack without conflicts.

## 11. Loot System

Key mods: Lootr, Better Loot, Loot Integrations, YUNG's Structures Addon for Loot Integrations, LI: CTOV, LI: Philip's Ruins, LI: Moog's Voyager.

- [ ] **Lootr instancing** — Open the same chest as two different players. Confirm each gets unique loot.
- [ ] **Better Loot tables** — Check loot quality in vanilla structures. Confirm Better Loot overhauls are present.
- [ ] **Loot Integrations** — Check loot in YUNG's structures. Confirm modded items (Simply Swords, Artifacts, Mythic Upgrades) appear in loot tables.
- [ ] **LI: CTOV addon** — Find a CTOV village or outpost chest. Confirm modded items appear in the loot (Simply Swords, Artifacts, etc.).
- [ ] **LI: Philip's Ruins addon** — Find a Philip's Ruins structure chest. Confirm modded items appear in loot tables.
- [ ] **LI: Moog's Voyager addon** — Find an MVS structure chest. Confirm modded items appear in loot tables.
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
- [ ] **Chunk loading stress** — Fly at high speed across unloaded chunks. Confirm C2ME and NoisiumForked handle parallel generation without crashes.
- [ ] **Memory usage** — After 30+ minutes of gameplay, check F3 memory. Confirm FerriteCore and ModernFix keep usage reasonable.
- [ ] **Background performance** — Alt-tab away from the game. Confirm Dynamic FPS reduces resource usage.
- [ ] **Block entity performance** — Build a room with 100+ chests. Compare FPS with Enhanced Block Entities enabled vs disabled. Confirm significant improvement.

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

## 16. Controller Support

Key mods: Controlify, Better Combat, EMI, Controlling.

- [ ] **Controller detection** — Connect a gamepad/controller. Launch the game. Confirm Controlify detects the controller and runs automatic deadzone calibration.
- [ ] **Controller navigation** — Navigate the main menu, inventory, and settings screens using only the controller. Confirm cursor snapping works in inventory slots.
- [ ] **On-screen button prompts** — With a controller active, confirm on-screen button guides display correct glyphs for your controller model (Xbox, PlayStation, etc.).
- [ ] **Controller combat** — Attack mobs using controller inputs. Confirm Better Combat swing animations trigger correctly from controller attack button.
- [ ] **Controller + Combat Roll** — Trigger a combat roll via controller input. Confirm dodge animation plays.
- [ ] **Controller + EMI** — Open inventory with controller. Navigate EMI recipe sidebar using controller. Search for and view a recipe.
- [ ] **Radial menu** — Open the Controlify radial menu. Confirm bound actions are accessible and functional.
- [ ] **Vibration feedback** — Take damage, break blocks, and trigger lightning. Confirm controller vibrates on each event (if supported by controller).
- [ ] **Controller + Controlling keybinds** — Open Controls screen. Confirm controller bindings are accessible and Controlling's search/filter works for controller-mapped keys.
- [ ] **Gyro aiming** (if supported) — Enable gyro in Controlify settings. Confirm fine aim control works for looking around.

## 17. Miscellaneous

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
