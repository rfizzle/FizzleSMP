# Farming, Seasons & Food Guide

FizzleSMP adds a seasonal cycle that affects crop growth, an expanded cooking system, and new farming tools. This guide covers how it all works together.

---

## Seasons — Fabric Seasons

The world cycles through four seasons, each lasting **28 in-game days** (configurable). Seasons are tied to world time.

### Season Effects

| Season | Weather | Crops | Visuals |
|---|---|---|---|
| **Spring** | Normal | Normal growth rates | Green, flowers blooming |
| **Summer** | Rain in cold biomes | Some crops grow faster | Lush, vibrant colors |
| **Autumn** | Transitional | Some crops slow down | Orange/brown foliage |
| **Winter** | Snow in temperate biomes | Most crops grow very slowly or stop | White/bare trees |

### What This Means for Farming

- **Crops grow at different speeds per season.** Each crop has its own seasonal growth rates. Some crops thrive in summer, others prefer spring.
- **Winter is harsh** — most crops barely grow. Plan ahead by stockpiling food before winter hits.
- **Tropical biomes** are steady year-round — less affected by seasons.
- **Hot biomes** get a rainy season in winter.

### Checking the Season

**SeasonHUD** shows the current season and day on your HUD. You can always see what season it is at a glance.

### Tips

- Build farms in different biomes to hedge against seasonal slowdowns.
- Use greenhouses or underground farms to protect crops from winter (temperature affects growth).
- Stock up on food during productive seasons.

---

## Farmer's Delight — Expanded Cooking

Farmer's Delight adds a full cooking system with new crops, foods, and the **Cooking Pot**.

### Getting Started

1. **Craft a Cooking Pot** — the core block for multi-ingredient recipes.
2. **Place it over a heat source** — campfire, stove, or other heat block underneath.
3. **Add ingredients** — right-click to insert items, or use a hopper.
4. **Wait for it to cook** — the pot will show progress. Output appears in the pot when done.

### New Crops

- **Tomatoes** — grow on vines, need support
- **Onions** — planted like potatoes
- **Cabbage** — ground crop
- **Rice** — planted in water (like sugarcane mechanics)

All of these are affected by seasons — check EMI for which season each crop grows best in.

### Key Recipes

Check EMI for the full recipe list, but highlights include:

- **Stews and soups** — multi-ingredient meals that give long-lasting food buffs
- **Pies and sandwiches** — portable, filling food options
- **Cutting Board** — craft one to slice ingredients (bread into slices, meat into strips, etc.)
- **Stove** — an upgraded furnace for food items, also works as heat source for cooking pot

### Chef's Delight

An addon that adds more Farmer's Delight recipes and food items. All recipes show up in EMI.

### Right-Click Harvest

Right-click mature crops to harvest them without breaking the plant. The crop drops its items and resets to an earlier growth stage — no need to replant.

---

## Easy Mob Farm

Adds dedicated mob farm blocks for server-friendly mob farming without building lag-inducing mob grinders.

### How to Use

1. **Craft a Mob Farm block** (check EMI for the recipe — has multiple tiers).
2. **Place it down** and capture a mob in it (use the appropriate capture method).
3. **The farm produces mob drops over time**, automatically deposited into the block's inventory.
4. **Upgrade tiers** for faster production cycles:

| Tier | Cycle Time |
|---|---|
| Base | 4 minutes |
| Tier 1 | 2.5 minutes |
| Tier 2 | 1.7 minutes |
| Tier 3 | 1 minute |

Extract drops with hoppers, pipes, or Refined Storage importers.

---

## Other Farming & Food Features

### Fabric Seasons: Extras
Adds seasonal extras like seasonal crops and other season-dependent features.

### Supplementaries
Adds **Jars** for storing items visually, **Faucets** for automation, and many other utility blocks useful for kitchen and farm builds.

### HT's TreeChop
Chop down entire trees by breaking the bottom block — all logs fall. Useful for wood farming.

### RightClickHarvest + Supplementaries
The Supplementaries compat module ensures right-click harvest works correctly with Supplementaries' planter boxes and other custom farming blocks.
