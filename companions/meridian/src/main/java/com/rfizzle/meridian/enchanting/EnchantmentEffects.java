package com.rfizzle.meridian.enchanting;

import com.rfizzle.meridian.Meridian;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class EnchantmentEffects {

    private EnchantmentEffects() {}

    public static int getEnchantmentLevel(ItemStack stack, String id) {
        if (stack.isEmpty()) return 0;
        ResourceLocation key = Meridian.id(id);
        for (var entry : stack.getEnchantments().entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static int getEquippedLevel(LivingEntity entity, String id, EquipmentSlot... slots) {
        int maxLevel = 0;
        for (EquipmentSlot slot : slots) {
            int level = getEnchantmentLevel(entity.getItemBySlot(slot), id);
            if (level > maxLevel) maxLevel = level;
        }
        return maxLevel;
    }
}
