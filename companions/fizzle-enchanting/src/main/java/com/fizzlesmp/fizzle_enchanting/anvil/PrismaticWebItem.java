package com.fizzlesmp.fizzle_enchanting.anvil;

import net.minecraft.world.item.Item;

/**
 * Consumable anvil ingredient that strips {@code #minecraft:curse} enchantments from a cursed
 * item when paired against it in slot B. Behaviour is claimed by {@code PrismaticWebHandler}
 * (T-4.1.4) through {@link AnvilDispatcher}; this class is the bare registrable item so the
 * handler — and its recipe — have something to reference.
 *
 * <p>No extra behaviour beyond a plain {@link Item} is needed: the dispatcher inspects the
 * stack by {@link net.minecraft.world.item.ItemStack#getItem() item identity}, so a subclass
 * only exists to make the call sites self-documenting and to anchor future tweaks (e.g. a
 * custom tooltip) to a dedicated type without touching {@link Item}'s defaults.
 */
public class PrismaticWebItem extends Item {

    public PrismaticWebItem(Properties properties) {
        super(properties);
    }
}
