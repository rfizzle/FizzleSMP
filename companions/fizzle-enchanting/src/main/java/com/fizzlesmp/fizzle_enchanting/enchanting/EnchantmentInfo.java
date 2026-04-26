package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Per-enchantment configuration data. Populated from the JSON config's
 * {@code enchantmentOverrides} section (server-side) or from an
 * {@link com.fizzlesmp.fizzle_enchanting.net.EnchantmentInfoPayload} (client-side).
 *
 * <p>Mirrors Apothic-Enchanting's {@code EnchantmentInfo} record with the same fields
 * and power function model.
 */
public record EnchantmentInfo(
        Holder<Enchantment> ench,
        int maxLevel,
        int maxLootLevel,
        int levelCap,
        PowerFunction maxPower,
        PowerFunction minPower
) {

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentInfo> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT), EnchantmentInfo::ench,
            ByteBufCodecs.VAR_INT, EnchantmentInfo::maxLevel,
            ByteBufCodecs.VAR_INT, EnchantmentInfo::maxLootLevel,
            ByteBufCodecs.VAR_INT, EnchantmentInfo::levelCap,
            PowerFunction.STREAM_CODEC, EnchantmentInfo::maxPower,
            PowerFunction.STREAM_CODEC, EnchantmentInfo::minPower,
            EnchantmentInfo::new);

    /**
     * Effective max level, respecting the hard cap if set.
     */
    public int getMaxLevel() {
        return levelCap > 0 ? Math.min(levelCap, maxLevel) : maxLevel;
    }

    /**
     * Effective max loot level (for loot tables and villager trades), respecting the hard cap.
     */
    public int getMaxLootLevel() {
        return levelCap > 0 ? Math.min(levelCap, maxLootLevel) : maxLootLevel;
    }

    public int getMinPower(int level) {
        return minPower.getPower(level);
    }

    public int getMaxPower(int level) {
        return maxPower.getPower(level);
    }

    /**
     * Creates an info record using vanilla defaults for an enchantment with no config override.
     */
    public static EnchantmentInfo fallback(Holder<Enchantment> ench) {
        Enchantment e = ench.value();
        return new EnchantmentInfo(
                ench, e.getMaxLevel(), e.getMaxLevel(), -1,
                PowerFunction.DefaultMaxPowerFunction.INSTANCE,
                new PowerFunction.DefaultMinPowerFunction(ench));
    }

    /**
     * Creates an info record by merging a config override with vanilla defaults.
     */
    public static EnchantmentInfo fromOverride(
            Holder<Enchantment> ench, FizzleEnchantingConfig.EnchantmentOverride override) {
        Enchantment e = ench.value();
        int maxLevel = override.maxLevel > 0 ? override.maxLevel : e.getMaxLevel();
        int maxLootLevel = override.maxLootLevel > 0 ? override.maxLootLevel : e.getMaxLevel();
        int levelCap = override.levelCap;
        return new EnchantmentInfo(
                ench, maxLevel, maxLootLevel, levelCap,
                PowerFunction.DefaultMaxPowerFunction.INSTANCE,
                new PowerFunction.DefaultMinPowerFunction(ench));
    }
}
