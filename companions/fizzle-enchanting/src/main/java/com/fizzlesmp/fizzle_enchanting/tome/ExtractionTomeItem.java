package com.fizzlesmp.fizzle_enchanting.tome;

import net.minecraft.world.item.Item;

/**
 * Top-tier tome — consumed at the anvil with an enchanted item to salvage
 * <strong>every</strong> enchantment onto a fresh enchanted book while leaving the source item
 * intact (fully unenchanted, with a configurable durability hit applied).
 *
 * <p>The Extraction Tome also exposes an item-repair side-path when dropped into the anvil fuel
 * slot alongside a damaged item in slot A; that behaviour is handler-side (see
 * {@code ExtractionTomeFuelSlotRepairHandler}, S-5.2.4). No durability on this item — the fuel
 * slot interaction is a handler concern, not an item-data one.
 */
public class ExtractionTomeItem extends Item {

    public ExtractionTomeItem(Properties properties) {
        super(properties);
    }
}
