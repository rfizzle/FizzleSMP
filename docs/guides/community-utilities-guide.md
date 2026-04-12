# Community & Utilities Guide

FizzleSMP includes mods for land protection, voice chat, death mechanics, storage, and various quality-of-life features. This guide covers the non-obvious ones.

---

## Land Claims — Open Parties and Claims

Protect your builds from griefing by claiming chunks.

### Claiming Land

1. Press **'** (apostrophe key, rebindable) to open the claims UI.
2. Select chunks on the map to claim them.
3. Alternatively, if you have **Xaero's World Map** installed, open the world map (**M**) and right-click on the map to claim chunks directly.

### Commands

| Command | What It Does |
|---|---|
| `/openpac-claims` | Manage your claims |
| `/openpac-parties` | Manage your party |
| `/opm <message>` | Send a message to party chat |

### Parties

Create a party to share claim access with friends:
- Use `/openpac-parties create <name>` to start a party.
- Invite players with party commands.
- Party members can build in each other's claimed chunks.

### Xaero's Map Integration

Your claims show up visually on both the minimap and world map. Other players' claims are visible too, so you can see where land is already taken.

---

## Voice Chat — Simple Voice Chat

Proximity-based voice chat is built into the server.

### Setup
1. Press **V** (default) to open voice chat settings.
2. Configure your microphone and push-to-talk key.
3. Talk — players nearby will hear you. Volume scales with distance.

### Features
- **Proximity chat** — closer players are louder
- **Groups** — create private voice channels with friends
- **Mute controls** — mute yourself or other players

---

## Skins — Fabric Tailor

Change your skin in-game without restarting Minecraft.

### Commands
Use skin commands to change your appearance on the fly. Check `/fabrictailor` for available commands. You can set skins from URLs or player names.

---

## Death & Graves — You're in Grave Danger

When you die, a **grave** spawns at your death location containing all your items.

- Walk to your grave and break it to recover everything.
- No despawn timer stress — your items are safe in the grave.
- Graves are protected — only you can break your own grave.
- Your death location is marked on Xaero's maps automatically.

---

## Sleep — Steve's Realistic Sleep

Sleep doesn't instantly skip to morning. Instead:
- Time **accelerates** gradually while players sleep.
- **More players sleeping = faster time passage.**
- This prevents one player from forcing a time skip on everyone else.

---

## Storage Options

Beyond Refined Storage (see tech guide), the pack includes several physical storage upgrades:

### Iron Chests & Metal Barrels
Upgraded storage containers with increasing capacity:

| Tier | Slots |
|---|---|
| Iron | 54 (double chest size) |
| Gold | 81 |
| Diamond | 108 |
| Obsidian | 108 (blast-resistant) |

Metal Barrels work the same way but as barrel variants.

### Nether Chested
A chest that stores items in a personal Nether-linked inventory. Access the same items from any Nether Chest you place — works like an Ender Chest but with more storage.

### Traveler's Backpack
A wearable backpack with built-in crafting table and fluid tanks.
- Craft a backpack and equip it.
- Press **B** (default) to open it.
- Stores items, has a crafting grid, and can hold two fluid tanks.
- Different backpack variants exist with different looks.

### Kibe Utilities
Kibe adds several useful storage and utility items:
- **Entangled Chest/Tank** — linked pairs that share inventory across any distance (even cross-dimension).
- **Vacuum Hopper** — picks up items and XP in a radius.
- **Elevators** — stand on one and jump/crouch to teleport between elevator blocks vertically.

---

## Building & Decoration

- **Handcrafted** — Adds craftable furniture: chairs, tables, couches, shelves, lamps, and more. Place and rotate to furnish your builds.
- **Beautify: Refabricated** — Decorative blocks like hanging pots, blinds, trellis, and botanical additions for builds.
- **Chipped** — Adds hundreds of block variants. Craft a **Workbench** (Chipped's, not vanilla) to access alternate textures for stone, wood, glass, and many other blocks. Great for adding variety to builds.
- **Supplementaries** — Adds jars, sign posts, flags, sconces, planters, wind vanes, and many other decorative and functional blocks.
- **Every Compat** — Automatically generates furniture and decoration variants for all modded wood types. If a mod adds new wood, Every Compat ensures Handcrafted, Supplementaries, etc. have matching variants.

---

## Quality of Life

### EMI (Recipe Viewer)
Press **R** while hovering over any item to see its recipes. Press **U** to see what it's used in. The search bar filters everything.

### Jade (Block Info)
Look at any block or entity to see info on your HUD — what it is, its state, contents, and more.

### Xaero's Maps
- **Minimap** — always visible on HUD. Shows mobs, players, waypoints.
- **World Map** — press **M** for full-screen explored map.
- **Waypoints** — press **B** on minimap or click on world map to create waypoints. Death points are automatic.

### Other QoL
- **Shulker Box Tooltip** — hover over a shulker box in your inventory to see its contents.
- **AppleSkin** — shows hunger/saturation values and food restore preview on the HUD.
- **Stack Refill** — automatically refills your hand from inventory when you use up a stack.
- **Mouse Tweaks** — improved inventory management (scroll to move items, drag to distribute).
- **Inventory Essentials** — bulk move items between inventories.
- **Crafting Tweaks** — rotate, balance, and clear the crafting grid with buttons.
- **Double Doors** — opening one door opens both.
- **Trade Cycling** — cycle villager trades more easily.
- **Pickable Villagers** — right-click villagers to pick them up and move them.
- **Let Me Despawn** — mobs given items will still despawn (prevents entity buildup).
- **Zoomify** — press **C** to zoom in (like OptiFine zoom).
