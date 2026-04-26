package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import com.fizzlesmp.fizzle_enchanting.net.EnchantmentInfoPayload;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores per-enchantment configuration ({@link EnchantmentInfo}) for every registered enchantment.
 *
 * <p>Server-side: populated by {@link #rebuild} on server start and datapack reload, merging
 * config overrides with vanilla defaults.
 *
 * <p>Client-side: populated by {@link #applyFromPayload} when the server sends
 * {@link EnchantmentInfoPayload}.
 */
public final class EnchantmentInfoRegistry {

    private static final Map<ResourceKey<Enchantment>, EnchantmentInfo> INFO = new HashMap<>();

    private EnchantmentInfoRegistry() {
    }

    /**
     * Returns the info for the given enchantment, falling back to vanilla defaults if not present.
     */
    public static EnchantmentInfo getInfo(Holder<Enchantment> ench) {
        ResourceKey<Enchantment> key = ench.unwrapKey().orElse(null);
        if (key != null) {
            EnchantmentInfo info = INFO.get(key);
            if (info != null) return info;
        }
        return EnchantmentInfo.fallback(ench);
    }

    public static Map<ResourceKey<Enchantment>, EnchantmentInfo> getAll() {
        return Collections.unmodifiableMap(INFO);
    }

    /**
     * Rebuilds the registry from the enchantment registry + config overrides. Called on server
     * start and after datapack reload.
     */
    public static void rebuild(Registry<Enchantment> registry, FizzleEnchantingConfig config) {
        INFO.clear();
        Map<String, FizzleEnchantingConfig.EnchantmentOverride> overrides =
                config.enchantmentOverrides != null ? config.enchantmentOverrides : Map.of();
        int overrideCount = 0;
        for (Holder.Reference<Enchantment> holder :
                (Iterable<Holder.Reference<Enchantment>>) registry.holders()::iterator) {
            ResourceKey<Enchantment> key = holder.key();
            FizzleEnchantingConfig.EnchantmentOverride override =
                    overrides.get(key.location().toString());
            if (override != null) {
                INFO.put(key, EnchantmentInfo.fromOverride(holder, override));
                overrideCount++;
            } else {
                INFO.put(key, EnchantmentInfo.fallback(holder));
            }
        }
        FizzleEnchanting.LOGGER.info(
                "Rebuilt enchantment info registry: {} enchantments, {} overrides",
                INFO.size(), overrideCount);
    }

    /**
     * Client-side: replaces the local registry with data received from the server.
     */
    public static void applyFromPayload(Map<ResourceKey<Enchantment>, EnchantmentInfo> data) {
        INFO.clear();
        INFO.putAll(data);
    }

    /**
     * Builds the payload to sync the full registry to a client.
     */
    public static EnchantmentInfoPayload buildPayload() {
        return new EnchantmentInfoPayload(new HashMap<>(INFO));
    }
}
