package com.fizzlesmp.fizzle_enchanting.item;

import net.minecraft.world.item.Item;

/**
 * Specialty crafting material produced only by the {@code fizzle_enchanting:enchanting}
 * table-crafting recipe that consumes {@code minecraft:dragon_breath}. Required to craft the
 * tier-3 themed shelves (infused hellshelf / seashelf / endshelf) and the Basic Library.
 *
 * <p>The class has no runtime behaviour — progression is gated purely by whether the player
 * can reach the recipe's stat thresholds at the enchanting table (E ≥ 40, Q 15-25, A ≥ 60).
 * Rarity is {@link net.minecraft.world.item.Rarity#EPIC} to match Zenith's presentation and to
 * make the item visually distinct from bulk ingredients in inventories.
 */
public class InfusedBreathItem extends Item {

    public InfusedBreathItem(Properties properties) {
        super(properties);
    }
}
