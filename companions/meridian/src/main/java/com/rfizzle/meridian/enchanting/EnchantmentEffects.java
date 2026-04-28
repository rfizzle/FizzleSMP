package com.rfizzle.meridian.enchanting;

import com.rfizzle.meridian.Meridian;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class EnchantmentEffects {

    public static final ResourceKey<Enchantment> REFLECTIVE_DEFENSES = key("reflective_defenses");
    public static final ResourceKey<Enchantment> REBOUNDING = key("rebounding");
    public static final ResourceKey<Enchantment> BERSERKERS_FURY = key("berserkers_fury");
    public static final ResourceKey<Enchantment> SCAVENGER = key("scavenger");
    public static final ResourceKey<Enchantment> CHROMATIC = key("chromatic");
    public static final ResourceKey<Enchantment> GROWTH_SERUM = key("growth_serum");
    public static final ResourceKey<Enchantment> LIFE_MENDING = key("life_mending");
    public static final ResourceKey<Enchantment> STABLE_FOOTING = key("stable_footing");
    public static final ResourceKey<Enchantment> TEMPTATION = key("temptation");

    private EnchantmentEffects() {}

    public static int getEnchantmentLevel(ItemStack stack, ResourceKey<Enchantment> key) {
        if (stack == null || stack.isEmpty()) return 0;
        for (var entry : stack.getEnchantments().entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    public static int getEquippedLevel(LivingEntity entity, ResourceKey<Enchantment> key, EquipmentSlot... slots) {
        int maxLevel = 0;
        for (EquipmentSlot slot : slots) {
            int level = getEnchantmentLevel(entity.getItemBySlot(slot), key);
            if (level > maxLevel) maxLevel = level;
        }
        return maxLevel;
    }

    private static ResourceKey<Enchantment> key(String path) {
        return ResourceKey.create(Registries.ENCHANTMENT, Meridian.id(path));
    }
}
