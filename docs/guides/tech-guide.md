# Technology & Automation Guide

FizzleSMP includes three tech mods that work together: **Oritech** (machines, processing, power), **Tech Reborn** (industrial tech tree), and **Refined Storage** (digital storage and autocrafting). This guide covers how to get started with each and how they complement each other.

---

## Oritech

Oritech is the primary tech mod in the pack. It adds animated multiblock machines, ore processing chains, energy systems, pipes, lasers, drones, and cybernetic augments.

### Getting Started

1. **Mine early resources** — You need **copper, iron, coal, and nickel**. Nickel is especially important for early machine recipes. Grab redstone, gold, and diamonds too.

2. **Build a Basic Generator** — Your first power source. Burns furnace fuels (coal, wood, charcoal) and produces 32 RF/t. Recipe: nickel ingots, copper ingot, magnetic coils, and a furnace (check EMI for exact layout).

3. **Build a Powered Furnace** — Your first QoL upgrade. Uses RF instead of fuel, smelts twice as fast as a vanilla furnace. Needs 1 machine core.

4. **Build a Pulverizer** — Crushes ores into dust for better yields (more ingots per ore). This is your first real processing upgrade.

### Ore Processing Chain

Oritech has a tiered ore processing system. Each step improves your yield:

| Stage | Machine | What It Does |
|---|---|---|
| **Basic** | Pulverizer | Crushes ores into dust for better yield than direct smelting |
| **Intermediate** | Foundry | Creates alloys (steel, electrum, adamant, biosteel, energite) more efficiently than hand-crafting |
| **Advanced** | Fragment Forge | Breaks ores into clumps and small clumps for even higher yields, plus byproducts |
| **Advanced** | Centrifuge | Processes ore clumps into dusts, handles fluid separation, carbon fiber production |
| **Endgame** | Atomic Forge | Creates advanced chips, reinforced deepslate, highest-yield ore processing. Powered by lasers, not cables |

**Tip:** If you have Silk Touch, mine ore blocks whole and feed them directly into the Fragment Forge for maximum yield.

### Alloys

Early alloys (steel, electrum, adamant) can be hand-crafted but it's wasteful. Move to a Pulverizer + Foundry setup as soon as possible — it uses fewer resources and unlocks later alloys like biosteel, energite, and duratium.

Hand-craft recipe example: 2 iron ingots + 2 coal = 1 steel ingot (but the Foundry is much more efficient).

### Energy System

Oritech uses **RF** (Redstone Flux). Power is push-based — generators and storage blocks push energy into pipes, which send it to machines.

#### Generators

| Generator | RF/t | Fuel | Notes |
|---|---|---|---|
| **Basic Generator** | 32 | Coal, wood, charcoal | Starter generator. No multiblock needed |
| **Bio Generator** | 64 | Biomass, organic materials | Good for automating with farms. Multiblock (2 cores) |
| **Lava Generator** | 64 | Lava, Sheol fire | Sheol fire burns much longer. Multiblock (2 cores) |
| **Fuel Generator** | 256 | Oil, diesel, refined fuels | High output. Refined fuels are much more efficient. Multiblock |
| **Big Solar Panel** | 32-224 | Sunlight | 3x3 multiblock. Output scales with core quality. Day only |
| **Steam Engine** | Up to 50,000 | Steam | Endgame. Converts steam from boiler-upgraded generators |

#### Energy Storage

| Block | Capacity | Notes |
|---|---|---|
| **Portable Energy Storage** | 1M RF | Keeps charge when broken. 5,000 RF/t in/out |
| **Large Energy Storage** | 20M RF | Multiblock (3 cores). 10,000 RF/t in/out. Supports addons |

#### Energy Transfer

- **Energy Pipes** — 10,000 RF/t transfer rate. Generators push power into pipes automatically.
- **Superconductors** — For very high power transfer needs.
- **Enderic Lasers** — Long-range wireless power transmission (also used for mining and powering the Atomic Forge).

### Pipes & Logistics

Oritech has three pipe types. All come in standard, framed, and duct variants.

#### Item Pipes
- Transfer 8 items every 5 ticks.
- **Right-click** a pipe connection to toggle extraction mode (pipes don't auto-pull by default).
- Items go to the **closest** connected inventory.
- Install a **motor** in the pipe (right-click with motor) to extract from all inventory slots instead of just the first non-empty slot.
- Use a **wrench** to disable connections in specific directions.

#### Fluid Pipes
- Transfer 0.5 buckets every 3 ticks.
- Right-click to toggle extraction, same as item pipes.

#### Energy Pipes
- Transfer 10,000 RF/t.
- Power is **push-based** — generators push into pipes automatically. No extraction toggle needed.

### Multiblocks & Addons

Many Oritech machines are **multiblocks** that require **machine cores** placed at specific positions.

**How to build:**
1. Place the machine block.
2. Right-click it — required core positions will be highlighted.
3. Place cores at the highlighted spots, or right-click the machine while holding a core to auto-place.

**Core quality** determines how many addon layers (machine extenders) you can add. Better cores don't make machines faster — they just allow more addons.

**Addons** are upgrade blocks attached to machines or machine extenders:
- **Speed addons** — Faster processing
- **Efficiency addons** — Less energy per operation
- **Yield addons** — More output (e.g., double byproducts on Fragment Forge)
- **Fluid addons** — Enable fluid processing on compatible machines
- **Redstone addons** — Comparator output for monitoring
- **Inventory proxy addons** — Control which items go to which slots

Addons turn **blue** when connected, **pink** when disconnected.

### Lasers & Fluxite

Before accessing many advanced machines, you need **fluxite**:

1. Build an **Enderic Laser** (multiblock, 1 core).
2. Craft a **Target Designator** and use it on the laser to set a direction.
3. Point the laser at **amethyst clusters** — the laser converts them into fluxite.
4. The laser has 128-block range and auto-collects drops into its inventory.
5. Use item pipes to extract resources continuously — items that don't fit are destroyed!

Lasers are also used to:
- **Mine blocks** at range (automated strip mining)
- **Power the Atomic Forge** (which can't connect to normal energy pipes)
- **Transmit energy** wirelessly over long distances

### Utility Machines

| Machine | What It Does |
|---|---|
| **Assembler** | 2x2 auto-crafting grid. Many components are cheaper when assembled vs. hand-crafted |
| **Tree Cutter** | Automatically fells entire trees (up to 8,000 blocks). Collects saplings and drops |
| **Pump** | Drains liquid bodies up to 100,000 blocks. 4 buckets/sec |
| **Bedrock Extractor** | Extracts infinite resources from bedrock resource nodes. Requires lasers for power. 26 cores |
| **Equipment Charger** | Recharges electric tools and armor. Also fills jetpack fuel |
| **Drone Port** | Long-range item/fluid transport via flying drones. Requires ports at both ends, 50+ blocks apart |

### Electric Equipment

| Equipment | Purpose |
|---|---|
| **Hand Drill** | Electric pickaxe |
| **Chainsaw** | Electric axe |
| **Electric Mace** | Electric weapon |
| **Exo Suit** | Cybernetic armor with augment slots |
| **Jetpack** | Flight. Fuel via Equipment Charger |
| **Portable Laser** | Handheld mining laser |
| **Promethium Tools** | Endgame pickaxe and axe |
| **Target Designator** | Sets laser and drone port targets |
| **Wrench** | Configures pipes and machines |

### Late-Game Content

- **Nuclear Reactors** — High-output power generation
- **Particle Accelerators** — Endgame research and material creation
- **Cybernetic Augmentation** — Player upgrades via the Exo Suit system

---

## Tech Reborn

Tech Reborn is a standalone industrial tech mod inspired by GregTech and IndustrialCraft 2. It has cross-mod compatibility with Oritech, meaning you can process Tech Reborn materials in Oritech machines and vice versa.

### Energy System

Tech Reborn uses the same RF energy system. It has its own tier system based on transfer rates:

| Tier | RF/t | Cable Type | Machine Level |
|---|---|---|---|
| Micro | 8 | Tin | Basic |
| Low | 32 | Tin | Advanced |
| Medium | 128 | Copper / Insulated Copper | Industrial |
| High | 512 | Gold | Ultimate |
| Extreme | 2,048 | HV / Insulated HV | Quantum |
| Insane | 8,192 | Glass Fiber | — |

**Tip:** Place batteries (energy storage) every 10-15 blocks along cable runs to prevent energy loss over distance. Transformers bridge different tier networks.

### Getting Started

1. **Find rubber trees** — Look in forest biomes for trees with a spike of leaves at the top. Craft a **Treetap** and use it on sap spots to collect sap. Smelt sap into rubber for insulated cables.

2. **Make refined iron** — Smelt an iron ingot in any furnace to get a refined iron ingot. This is used in many machine recipes.

3. **Build a Generator** — Burns coal or wood to produce power. Place machines directly next to the generator to avoid needing cables early on.

4. **Build an Electric Furnace** — Massively more efficient than vanilla furnaces. One coal smelts 49 items (vs. 8 in a regular furnace).

5. **Build a Grinder** — Doubles your ore output. One ore block grinds into 2 dusts, each smelts into 1 ingot.

### Machine Tiers

#### Non-Electric (No Power Needed)
- **Iron Furnace** — Faster than vanilla furnace, no power required
- **Iron Alloy Furnace** — Combines metals into alloys without power

#### Low Tier
- **Electric Furnace** — Powered smelting, extremely fuel-efficient
- **Grinder** — Ore doubling (ore to 2 dusts)
- **Compressor** — Compacts materials (plates, dense plates)
- **Extractor** — Extracts resources from items (rubber from sap)
- **Wire Mill** — Creates wiring and cables
- **Rolling Machine** — Shapes metal into plates
- **Alloy Smelter** — Combines metals into alloys
- **Recycler** — Converts junk into useful scrap
- **Auto Crafting Table** — Automated recipe crafting

#### Medium Tier
- **Industrial Blast Furnace** — Required for advanced metals (steel, titanium, tungsten)
- **Assembling Machine** — Crafts complex components
- **Chemical Reactor** — Chemical processing
- **Industrial Electrolyzer** — Separates compounds
- **Industrial Centrifuge** — Separates mixtures
- **Distillation Tower** — Refines fluids
- **Pump** — Fluid extraction

#### High Tier
- **Charge-O-Mat** — Charges multiple tools/armor simultaneously

#### Insane Tier
- **Matter Fabricator** — Creates UU-Matter for duplicating items
- **Fusion Control Computer** — Powers fusion reactor for massive energy generation

### Machine Configuration

All Tech Reborn machines support:
- **Side configuration** — Set which sides accept input or produce output via the "Configure slots" button
- **Auto input/output** — Toggle automatic item movement
- **Input filtering** — Control what items each side accepts

This makes automation with hoppers, pipes, or Refined Storage straightforward.

---

## Refined Storage

Refined Storage is a digital storage system that connects to your Oritech and Tech Reborn machines. It lets you store everything in one searchable network with autocrafting.

### Core Components

You need three things to start:

1. **Controller** — The brain of your network. Requires power (connect to an Oritech generator or energy pipe).
2. **Disk Drive** — Holds storage disks. Place next to or cable-connect to the Controller.
3. **Grid** — Your access terminal. Search, insert, and extract items. Place next to or cable-connect to the Controller.

**If blocks are placed directly adjacent to each other, no cables are needed.** Otherwise, connect them with Refined Storage **Cables**.

### Storage Disks

Insert storage disks into the Disk Drive to add capacity:

| Disk | Capacity |
|---|---|
| **1K Storage Disk** | ~1,000 items |
| **4K Storage Disk** | ~4,000 items |
| **16K Storage Disk** | ~16,000 items |
| **64K Storage Disk** | ~64,000 items |

Fluid storage disks are also available for storing liquids.

**Alternative:** Instead of disks in a Disk Drive, you can place **Storage Blocks** in the world. They work the same but as physical blocks.

### Connecting to Machines

Use these devices to connect your storage network to the outside world:

| Device | What It Does |
|---|---|
| **Importer** | Pulls items from an adjacent inventory **into** the network |
| **Exporter** | Pushes specific items **out of** the network into an adjacent inventory |
| **External Storage** | Treats an adjacent inventory as part of the network (doesn't move items, just indexes them) |
| **Constructor** | Places blocks from the network into the world |
| **Destructor** | Breaks blocks and puts them into the network |
| **Interface** | Combined import/export for machine automation |

**Typical setup with Oritech/Tech Reborn:** Place an Importer on a machine's output side to pull finished products into storage. Place an Exporter on a machine's input side to feed it materials.

### Grid Variants

| Grid Type | Purpose |
|---|---|
| **Grid** | View, insert, and extract items |
| **Crafting Grid** | Grid + built-in crafting table |
| **Pattern Grid** | Create patterns for autocrafting |
| **Fluid Grid** | View and manage stored fluids |

### Autocrafting

Autocrafting lets the network craft items on demand. Setup:

1. **Craft a Pattern Grid** — This is where you define recipes.
2. **Create patterns** — Open the Pattern Grid, lay out a recipe, and save it to a blank Pattern.
3. **Build an Autocrafter** — Place it on the network and insert your patterns.
4. **Request crafts** — Click any item in the Grid (or Ctrl+click if it's already in stock) to open the autocrafting preview. Set the quantity and confirm.

**Important:**
- You need a pattern for **every intermediate item** in a crafting chain. If a sub-item has no pattern and isn't in storage, the craft fails.
- **Fuzzy mode** on patterns lets you use any variant of an ingredient (e.g., any wood plank type).
- Add **Speed Upgrades** to Autocrafters to increase crafting speed.
- Use an **Autocrafting Monitor** to track and cancel active crafting tasks.
- Use an **Autocrafter Manager** to organize patterns across multiple autocrafters.

### Wireless Access

Refined Storage supports wireless grid access:
- **Wireless Grid** — Access your storage from anywhere within range
- **Wireless Autocrafting Monitor** — Monitor crafting remotely

---

## How the Three Mods Work Together

The three tech mods form a progression pipeline:

### Early Game
1. Build **Oritech Basic Generators** for power.
2. Set up **Oritech Pulverizer + Powered Furnace** for ore doubling.
3. Build a **Tech Reborn Grinder** as an alternative ore processor.
4. Hand-craft early alloys or build an **Oritech Foundry**.

### Mid Game
1. Expand power with **Bio Generators** or **Fuel Generators**.
2. Build an **Oritech Assembler + Centrifuge** for efficient component crafting.
3. Set up **Tech Reborn Industrial Blast Furnace** for advanced metals.
4. Start a basic **Refined Storage** network (Controller + Disk Drive + Grid) to centralize storage.
5. Connect machines to Refined Storage with Importers and Exporters.

### Late Game
1. Build **Fragment Forge** and **Atomic Forge** for maximum ore yields.
2. Set up **Refined Storage autocrafting** to automate complex recipes.
3. Use **Oritech Drones** for long-range item transport between bases.
4. Push into **nuclear reactors** or **particle accelerators**.
5. Build **cybernetic augments** and electric equipment.
6. Use **Enderic Lasers** for automated mining and wireless power.
7. Expand Refined Storage with 64K disks and full autocrafting chains.

### Cross-Mod Compatibility

- Oritech has built-in **Tech Reborn compatibility** — metals and components can be processed in either mod's machines.
- Refined Storage works with both mods via Importers, Exporters, and External Storage on any machine inventory.
- Oritech's Assembler works natively with Refined Storage pattern providers for autocrafting integration.

---

## Other Automation Tools

### Simple Conveyor Belts
Craftable belts that transport items along a path. Place them on the ground and items dropped on them move in the belt's direction. Useful for simple item transport without pipes.

### Dark Utilities
Adds **Vector Plates** — directional plates that push items or entities in a set direction. Also adds mob-related utilities like filters and traps for farm setups.

### JustHammers
Mining hammers that break blocks in a **3x3 area**. Craft higher-tier hammers for larger mining capability. Great for quickly clearing out space for machine rooms or mining in bulk.
