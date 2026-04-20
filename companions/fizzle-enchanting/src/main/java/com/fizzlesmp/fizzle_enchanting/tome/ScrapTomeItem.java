package com.fizzlesmp.fizzle_enchanting.tome;

import net.minecraft.world.item.Item;

/**
 * Cheapest tome tier — consumed at the anvil with an enchanted item to salvage
 * <strong>one random</strong> enchantment onto a fresh enchanted book. The source item is
 * destroyed in the process (see DESIGN § "Tome items").
 *
 * <p>The class carries no behaviour itself: {@code ScrapTomeHandler} (S-5.2) keys off
 * {@link net.minecraft.world.item.ItemStack#getItem() item identity} through
 * {@link com.fizzlesmp.fizzle_enchanting.anvil.AnvilDispatcher}. Extending {@link Item} only
 * exists so handler code and datagen can reference a dedicated type without re-resolving the
 * item by id, and to anchor future tweaks (custom tooltips etc.) without touching {@link Item}'s
 * defaults.
 */
public class ScrapTomeItem extends Item {

    public ScrapTomeItem(Properties properties) {
        super(properties);
    }
}
