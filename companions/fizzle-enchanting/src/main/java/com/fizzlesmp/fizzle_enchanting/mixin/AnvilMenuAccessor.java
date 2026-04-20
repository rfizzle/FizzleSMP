package com.fizzlesmp.fizzle_enchanting.mixin;

import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * T-4.1.1 — exposes the two {@link AnvilMenu}-private fields the
 * {@link com.fizzlesmp.fizzle_enchanting.anvil.AnvilDispatcher} pipeline needs to write when a
 * handler claims an output: the {@code cost} {@link DataSlot} (XP charge) and the
 * {@code repairItemCountCost} counter (right-slot consumption count).
 *
 * <p>Method names are prefixed with {@code fizzleEnchanting$} per /dev-companion's mixin rules —
 * the explicit {@link Accessor#value()} tells Mixin which field to bind, since the prefixed name
 * is otherwise unparseable as a getter/setter.
 */
@Mixin(AnvilMenu.class)
public interface AnvilMenuAccessor {

    @Accessor("cost")
    DataSlot fizzleEnchanting$getCost();

    @Accessor("repairItemCountCost")
    int fizzleEnchanting$getRepairItemCountCost();

    @Accessor("repairItemCountCost")
    void fizzleEnchanting$setRepairItemCountCost(int value);
}
