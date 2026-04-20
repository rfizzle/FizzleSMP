package com.fizzlesmp.fizzle_enchanting.tome;

import net.minecraft.world.item.Item;

/**
 * Mid-tier tome — consumed at the anvil with an enchanted item to salvage
 * <strong>every</strong> enchantment onto one fresh enchanted book. The source item is still
 * destroyed; the Extraction tier (see {@link ExtractionTomeItem}) is the one that preserves it.
 *
 * <p>Like the other tomes this class is a bare {@link Item} hook — behaviour lives in
 * {@code ImprovedScrapTomeHandler} (S-5.2) which dispatches from
 * {@link com.fizzlesmp.fizzle_enchanting.anvil.AnvilDispatcher}.
 */
public class ImprovedScrapTomeItem extends Item {

    public ImprovedScrapTomeItem(Properties properties) {
        super(properties);
    }
}
