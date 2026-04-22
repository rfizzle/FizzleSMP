package com.fizzlesmp.fizzle_enchanting.enchanting;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resources.ResourceLocation;

public final class ForeignEnchantmentOverrides {
    public static final String PACK_ID = "foreign_overrides";

    private ForeignEnchantmentOverrides() {}

    public static void register() {
        FizzleEnchantingConfig config = FizzleEnchanting.getConfig();
        boolean applyOverrides = config != null && config.foreignEnchantments.applyBundledOverrides;
        ResourcePackActivationType activation = decideActivationType(applyOverrides);

        ResourceLocation id = FizzleEnchanting.id(PACK_ID);
        ModContainer container = FabricLoader.getInstance()
                .getModContainer(FizzleEnchanting.MOD_ID)
                .orElse(null);
        if (container == null) {
            FizzleEnchanting.LOGGER.warn(
                    "Mod container missing; cannot register foreign enchantment overrides pack");
            return;
        }

        boolean registered = ResourceManagerHelper.registerBuiltinResourcePack(id, container, activation);
        if (registered) {
            FizzleEnchanting.LOGGER.info(
                    "Registered foreign enchantment overrides pack {} as {}", id, activation);
        } else {
            FizzleEnchanting.LOGGER.warn(
                    "Failed to register foreign enchantment overrides pack {}", id);
        }
    }

    public static ResourcePackActivationType decideActivationType(boolean applyBundledOverrides) {
        return applyBundledOverrides
                ? ResourcePackActivationType.ALWAYS_ENABLED
                : ResourcePackActivationType.NORMAL;
    }
}
