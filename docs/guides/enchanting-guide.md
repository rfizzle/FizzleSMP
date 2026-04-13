# Enchanting, Repairs & Gear Crafting Guide

FizzleSMP adds several mods that expand what you can do with enchanting tables, anvils, grindstones, and smithing tables. This guide covers how to use each one.

---

## Vanilla Enchanting Table — Easy Magic

The vanilla enchanting table works the same as always, with a few QoL improvements:

- **Items stay in the table** when you close the GUI — no more accidentally losing lapis or your item.
- **Re-roll enchantments easily** — you can re-roll the enchantment options without placing and removing items.

Just use the enchanting table as normal. The improvements are automatic.

---

## Enchanting Infuser

A separate block that lets you **pick exactly which enchantments** you want instead of gambling on random rolls.

### How to Use
1. Craft an **Enchanting Infuser** block (check EMI/recipe viewer for the recipe).
2. Place it down and right-click to open the GUI.
3. Insert the item you want to enchant.
4. Browse available enchantments and select the ones you want.
5. Pay the XP cost shown — prices are configurable by the server.

**Available enchantments include Mending and YIGD's Soulbound** (both added via datapack — they don't normally appear in enchanting tables).

XP costs on FizzleSMP are raised above the infuser's default to preserve vanilla enchanting table viability. Expect to pay up to **100 levels** at the normal infuser and **150 levels** at the advanced infuser for a full top-tier enchantment set.

This is the best way to get specific enchantments without re-rolling at a vanilla table.

---

## Anvil — Easy Anvils

Anvils work like vanilla but with key improvements:

- **No "Too Expensive!" cap** — you can always repair and combine items, no matter how many times they've been through an anvil.
- **Items stay in the anvil** when you close the GUI.
- **Better name tag support** — additional name tag features and tweaks.

Just use the anvil as normal. The improvements are automatic.

---

## BeyondEnchant — Higher Enchantment Caps

Vanilla enchantment levels have been raised beyond their normal limits:

| Enchantment | Vanilla Max | New Max |
|---|---|---|
| Sharpness | 5 | 7 |
| Efficiency | 5 | 10 |
| Protection | 4 | 5 |
| Unbreaking | 3 | 10 |
| Fortune | 3 | 5 |
| Mending | 1 | 5 |

These higher-level enchantments can appear on the Enchanting Infuser or be found as loot. They give endgame players more to work toward.

---

## NeoEnchant+ — New Enchantments

Adds entirely new enchantments to the game, including:

- **Fury** — attack speed boost
- **Life+** — bonus health
- **Bright Vision** — night vision effect
- **Builder Arms** — extended reach
- **Rebound** — knockback resistance
- And more

These appear in the Enchanting Infuser, enchanting tables, and as loot. Check EMI for the full list and which items they apply to.

---

## Grindstone — Grind Enchantments

In vanilla, the grindstone destroys enchantments and gives you XP. With Grind Enchantments, you can **transfer enchantments to books** instead.

### How to Use
1. Open a **grindstone**.
2. Place an **enchanted item** in the top slot.
3. Place a **regular book** in the bottom slot.
4. The output will be an **enchanted book** with the enchantments from your item.

Your original item comes back unenchanted, and you keep the enchantments as a book. This is great for salvaging enchantments from loot you don't want to use.

---

## Smithing Table — Gear Tinkering

The smithing table gets several new uses beyond netherite upgrades. All recipes work by **throwing (Q key) items onto the top of a placed smithing table** — do not open the smithing table GUI.

### Recipes

#### Repair Cost Reset
- **Throw:** Item with repair cost + **amethyst shard**
- **Result:** Repair cost reduced by 1
- **Note:** The item must have been combined/repaired on an anvil at least once to have a repair cost. Each amethyst shard reduces it by 1. A white puff of smoke appears when the cost is already at 0.

#### Enchantment Transfer
- **Throw:** Enchanted item + **book**
- **Result:** Enchantments move from the item to the book

#### Trim Removal
- **Throw:** Trimmed armor piece + **diamond**
- **Result:** Armor trim is removed

#### Curse Removal
- **Throw:** Cursed item (Curse of Vanishing/Binding) + **echo shard**
- **Result:** Curse is removed — but **a vex spawns**, so be ready!

### Tips
- Stand close to the smithing table and press Q to drop each item so they land on top of the block.
- Do **not** right-click the smithing table — these recipes use dropped item entities, not the GUI.
- Both items need to be resting on the smithing table at the same time.

---

## XP Storage

Lets you safely bank experience points so you don't lose them on death.

### How to Use
1. Craft an **XP Storage** block (check EMI for the recipe).
2. Place it down and right-click to open it.
3. Store XP into the block for safekeeping.
4. Withdraw XP when you need it for enchanting or repairs.

Pairs well with the enchanting systems above — bank your XP, then withdraw exactly what you need.

---

## Better Than Mending

An alternative to the Mending enchantment. Instead of XP going into item repair automatically, **Better Than Mending** lets you manually consume XP to repair items on demand.

- Hold a damaged item with Mending and **sneak + right-click** to spend XP and repair it.
- This gives you control over when XP goes to repairs vs. when you keep it for enchanting.

---

## Recommended Workflows

### Enchanting New Gear
1. Bank XP in an **XP Storage** block so you don't lose it.
2. Use the **Enchanting Infuser** to pick exactly the enchantments you want.
3. Combine enchanted books on an **anvil** — the "Too Expensive" cap is removed.

### Salvaging Enchantments from Loot
- **Grindstone method:** Place the item + a book in the grindstone to extract enchantments as a book.
- **Smithing table method:** Throw the item + a book onto a smithing table to transfer enchantments.

### Fixing Expensive Repair Costs
1. If an item's anvil repair cost is getting high, throw it + **amethyst shards** onto a **smithing table** to reduce the cost.
2. The "Too Expensive" cap is already removed by Easy Anvils, but resetting the cost keeps XP prices lower.

### Removing Unwanted Curses
Throw the cursed item + an **echo shard** onto a **smithing table**. Watch out for the vex!
