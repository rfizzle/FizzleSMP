package com.fizzlesmp.fizzle_enchanting.anvil;

import net.minecraft.world.item.ItemStack;

/**
 * Claim returned by an anvil handler when it wants to replace vanilla's output for a
 * left+right slot combination. The TAIL hook in
 * {@link com.fizzlesmp.fizzle_enchanting.mixin.AnvilMenuMixin} consumes this:
 * {@code output} lands in slot 0 of {@code resultSlots}, {@code xpCost} overwrites the
 * menu's cost {@code DataSlot}, and {@code rightConsumed} becomes the menu's
 * {@code repairItemCountCost}.
 */
public record AnvilResult(ItemStack output, int xpCost, int rightConsumed) {
}
