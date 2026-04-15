# Modded Villager Trades

Several mods in FizzleSMP add new villager professions with their own trade tables. This guide lists every modded villager, their workstation, and what they trade at each level.

> Tip: the EMI Professions addon is installed — press **U** on an Emerald or Villager Spawn Egg in EMI to see which workstation maps to which profession in-game.

## Summary

| Profession | Mod | Workstation | Spawns In |
|---|---|---|---|
| Metallurgist | Tech Reborn | Iron Alloy Furnace | Vanilla villages (desert/plains/savanna/snowy/taiga) |
| Electrician | Tech Reborn | Solid Fuel Generator | Vanilla villages (desert/plains/savanna/snowy/taiga) |
| Monk | Paladins & Priests | Monk Workbench | Sanctuaries (new village structure) |
| Jeweler | Jewelry (RPG Series) | Jeweler's Kit | Vanilla villages |
| Cook | Chef's Delight | Skillet | Chef's Delight village structures |
| Chef | Chef's Delight | Cooking Pot | Chef's Delight village structures |

---

## Tech Reborn

### Metallurgist
Workstation: **Iron Alloy Furnace**

| Level | Trades |
|---|---|
| Novice | Buys 6× Raw Tin → 1 em · Buys 4× Raw Lead → 1 em · Sells 3× Rubber ← 2 em |
| Apprentice | Sells Bronze Ingot ← 2 em · Sells Brass Ingot ← 5 em · Sells 2× Electronic Circuit ← 3 em |
| Journeyman | Sells 3× Electrum Ingot ← 7 em · Buys 3× Carbon Fiber → 1 em |
| Expert | Sells 4× Advanced Alloy ← 7 em · Buys 1× Nickel Ingot → 1 em |
| Master | Sells 3× Advanced Circuit ← 7 em |

### Electrician
Workstation: **Solid Fuel Generator**

| Level | Trades |
|---|---|
| Novice | Buys 6× Rubber → 1 em · Buys 3× Copper Ingot → 1 em · Sells 3× Insulated Copper Cable ← 1 em |
| Apprentice | Buys 4× Gold Ingot → 1 em · Sells 3× Insulated Gold Cable ← 5 em · Sells 2× Electronic Circuit ← 3 em |
| Journeyman | Buys 1× Red Cell Battery → 1 em · Sells Low Voltage SU ← 8 em · Sells Solid Fuel Generator ← 8 em |
| Expert | Sells 3× Advanced Circuit ← 7 em · Buys 6× Ruby → 1 em · Sells Glass Fiber Cable ← 4 em |
| Master | Sells LED Lamp ← 8 em · Sells Lithium-Ion Battery ← 30 em |

Wandering traders in Tech Reborn also offer **Rubber Saplings** (1 sapling ← 5 em).

---

## Paladins & Priests — Monk
Workstation: **Monk Workbench**. Spawns in Sanctuaries, a new village structure.

| Level | Trades |
|---|---|
| Novice (sells) | 8× Healing Rune ← 2 em · Acolyte Wand ← 4 em · Wooden Great Hammer ← 8 em |
| Apprentice (buys) | 5× White Wool → 1 em · 6× Iron Ingot → 1 em · 6× Chain → 1 em · 6× Gold Ingot → 1 em |
| Journeyman (sells, 15 em each) | Paladin Helmet/Boots T1 · Priest Helmet/Boots T1 |
| Expert (sells, 20 em each) | Paladin Chestplate/Leggings T1 · Priest Chestplate/Leggings T1 |
| Master (sells enchanted, 40 em each) | Diamond Holy Staff · Diamond Claymore · Diamond Great Hammer |

---

## Jewelry — Jeweler
Workstation: **Jeweler's Kit**.

| Level | Trades |
|---|---|
| Novice | Buys 8× Copper Ingot → 1 em · Buys 7× String → 1 em · Sells Copper Ring ← 4 em |
| Apprentice | Buys 7× Gold Ingot → 1 em · Sells Iron Ring ← 4 em · Sells Gold Ring ← 18 em |
| Journeyman | Buys 1× Diamond → 1 em · Sells Emerald Necklace ← 20 em · Sells Diamond Necklace ← 25 em |
| Expert (sells, 35 em each) | Ruby · Topaz · Citrine · Jade · Sapphire · Tanzanite Rings |
| Master (sells, 45 em each) | Ruby · Topaz · Citrine · Jade · Sapphire · Tanzanite Necklaces |

---

## Chef's Delight — Cook & Chef
Exact trade tiers are not published and the mod's source is not open — use EMI in-game to view current offers.

- **Cook** — workstation **Skillet**. Trades simple foods and ingredients; buys raw crops and meats.
- **Chef** — workstation **Cooking Pot**. Trades advanced feasts including **Roast Chicken** and **Honey Glazed Ham**; buys raw ingredients.

Both spawn in custom village structures added by Chef's Delight.

---

## Sources

- Tech Reborn — [`TRVillager.java`](https://github.com/TechReborn/TechReborn/blob/1.21/src/main/java/techreborn/init/TRVillager.java)
- Paladins & Priests — [`PaladinVillagers.java`](https://github.com/ZsoltMolnarrr/Paladins/blob/1.21.1/common/src/main/java/net/paladins/village/PaladinVillagers.java)
- Jewelry — [`JewelryVillagers.java`](https://github.com/ZsoltMolnarrr/Jewelry/blob/1.21.1/common/src/main/java/net/jewelry/village/JewelryVillagers.java)
- Chef's Delight — [Modrinth page](https://modrinth.com/mod/chefs-delight)
