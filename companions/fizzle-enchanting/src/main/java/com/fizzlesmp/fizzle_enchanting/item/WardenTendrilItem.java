package com.fizzlesmp.fizzle_enchanting.item;

import net.minecraft.world.item.Item;

/**
 * Warden-drop specialty material required to craft the {@code echoing_sculkshelf} and
 * {@code soul_touched_sculkshelf} tier-3 sculk shelves. Distribution is handled by
 * {@code WardenLootHandler} (T-5.4.3) via {@code LootTableEvents.MODIFY}: 1 guaranteed drop gated
 * by {@code config.warden.tendrilDropChance} plus a looting-scaled extra gated by
 * {@code config.warden.tendrilLootingBonus}.
 *
 * <p>The class has no runtime behaviour — it is a plain identity item. Rarity stays default
 * because the tendril is a routine Warden reward, not an epic-tier find like infused_breath.
 */
public class WardenTendrilItem extends Item {

    public WardenTendrilItem(Properties properties) {
        super(properties);
    }
}
