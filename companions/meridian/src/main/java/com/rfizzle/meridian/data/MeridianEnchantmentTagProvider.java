package com.rfizzle.meridian.data;

import com.rfizzle.meridian.Meridian;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class MeridianEnchantmentTagProvider extends FabricTagProvider.EnchantmentTagProvider {

    private static final TagKey<Enchantment> EXCLUSIVE_ARCHERY = exclusiveSet("archery");
    private static final TagKey<Enchantment> EXCLUSIVE_ARMOR = exclusiveSet("armor");
    private static final TagKey<Enchantment> EXCLUSIVE_ASPECT = exclusiveSet("aspect");
    private static final TagKey<Enchantment> EXCLUSIVE_BOW = exclusiveSet("bow");
    private static final TagKey<Enchantment> EXCLUSIVE_DURABILITY = exclusiveSet("durability");
    private static final TagKey<Enchantment> EXCLUSIVE_EXPERIENCE = exclusiveSet("experience");
    private static final TagKey<Enchantment> EXCLUSIVE_MACE = exclusiveSet("mace");
    private static final TagKey<Enchantment> EXCLUSIVE_MENDING = exclusiveSet("mending");
    private static final TagKey<Enchantment> EXCLUSIVE_MINING = exclusiveSet("mining");
    private static final TagKey<Enchantment> EXCLUSIVE_SIZE = exclusiveSet("size");
    private static final TagKey<Enchantment> EXCLUSIVE_SWORD_ATTRIBUTE = exclusiveSet("sword_attribute");
    private static final TagKey<Enchantment> EXCLUSIVE_SWORD_EFFECT = exclusiveSet("sword_effect");
    private static final TagKey<Enchantment> EXCLUSIVE_SWORD_EXP = exclusiveSet("sword_exp");
    private static final TagKey<Enchantment> EXCLUSIVE_TRIDENT = exclusiveSet("trident");

    public MeridianEnchantmentTagProvider(FabricDataOutput output,
                                          CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        addExclusiveSets();
        appendVanillaTags();
    }

    private void addExclusiveSets() {
        getOrCreateTagBuilder(EXCLUSIVE_ARCHERY)
                .addOptional(Meridian.id("bow/rebound"))
                .addOptional(mc("quick_charge"))
                .addOptional(mc("multishot"))
                .addOptional(mc("piercing"));

        getOrCreateTagBuilder(EXCLUSIVE_ARMOR)
                .addOptional(Meridian.id("armor/lifeplus"))
                .addOptional(Meridian.id("armor/fury"))
                .addOptional(mc("protection"));

        getOrCreateTagBuilder(EXCLUSIVE_ASPECT)
                .addOptional(mc("fire_aspect"))
                .addOptional(Meridian.id("sword/poison_aspect"))
                .addOptional(Meridian.id("sword/oppression"));

        getOrCreateTagBuilder(EXCLUSIVE_BOW)
                .addOptional(Meridian.id("bow/breezing_arrow"))
                .addOptional(Meridian.id("bow/explosive_arrow"))
                .addOptional(Meridian.id("bow/storm_arrow"))
                .addOptional(Meridian.id("bow/echo_shot"))
                .addOptional(Meridian.id("bow/eternal_frost"))
                .addOptional(mc("quick_charge"));

        getOrCreateTagBuilder(EXCLUSIVE_DURABILITY)
                .addOptional(Meridian.id("durability/curse_of_breaking"))
                .addOptional(mc("mending"))
                .addOptional(mc("unbreaking"));

        getOrCreateTagBuilder(EXCLUSIVE_EXPERIENCE)
                .addOptional(Meridian.id("sword/xp_boost"))
                .addOptional(mc("mending"));

        getOrCreateTagBuilder(EXCLUSIVE_MACE)
                .addOptional(mc("wind_burst"))
                .addOptional(Meridian.id("mace/teluric_wave"));

        getOrCreateTagBuilder(EXCLUSIVE_MENDING)
                .addOptional(Meridian.id("life_mending"))
                .addOptional(mc("mending"));

        getOrCreateTagBuilder(EXCLUSIVE_MINING)
                .addOptional(Meridian.id("pickaxe/vein_miner"))
                .addOptional(Meridian.id("tools/miningplus"));

        getOrCreateTagBuilder(EXCLUSIVE_SIZE)
                .addOptional(Meridian.id("leggings/oversize"))
                .addOptional(Meridian.id("leggings/dwarfed"));

        getOrCreateTagBuilder(EXCLUSIVE_SWORD_ATTRIBUTE)
                .addOptional(Meridian.id("sword/attack_speed"))
                .addOptional(Meridian.id("sword/reach"))
                .addOptional(Meridian.id("sword/dimensional_hit"));

        getOrCreateTagBuilder(EXCLUSIVE_SWORD_EFFECT)
                .addOptional(Meridian.id("sword/pull"))
                .addOptional(Meridian.id("sword/life_steal"))
                .addOptional(Meridian.id("sword/fear"))
                .addOptional(Meridian.id("sword/tears_of_asflors"))
                .addOptional(Meridian.id("sword/last_hope"))
                .addOptional(Meridian.id("sword/critical"))
                .addOptional(Meridian.id("sword/death_touch"));

        getOrCreateTagBuilder(EXCLUSIVE_SWORD_EXP)
                .addOptional(Meridian.id("sword/xp_boost"))
                .addOptional(mc("mending"));

        getOrCreateTagBuilder(EXCLUSIVE_TRIDENT)
                .addOptional(mc("channeling"))
                .addOptional(Meridian.id("trident/gungnir_breath"));
    }

    private void appendVanillaTags() {
        getOrCreateTagBuilder(EnchantmentTags.ARMOR_EXCLUSIVE)
                .addOptional(Meridian.id("armor/magic_protection"));

        getOrCreateTagBuilder(EnchantmentTags.DAMAGE_EXCLUSIVE)
                .addOptional(Meridian.id("sword/certainty"))
                .addOptional(Meridian.id("sword/divinity"))
                .addOptional(Meridian.id("sword/vigilance"));
    }

    private static TagKey<Enchantment> exclusiveSet(String name) {
        return TagKey.create(Registries.ENCHANTMENT, Meridian.id("exclusive_set/" + name));
    }

    private static ResourceLocation mc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }
}
