package com.rfizzle.meridian.data;

import com.rfizzle.meridian.Meridian;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class MeridianEntityTypeTagProvider extends FabricTagProvider.EntityTypeTagProvider {

    private static final TagKey<EntityType<?>> LAST_HOPE_BLACKLIST =
            TagKey.create(Registries.ENTITY_TYPE, Meridian.id("last_hope_blacklist"));
    private static final TagKey<EntityType<?>> SENSITIVE_TO_CERTAINTY =
            TagKey.create(Registries.ENTITY_TYPE, Meridian.id("sensitive_to_certainty"));
    private static final TagKey<EntityType<?>> SENSITIVE_TO_DIVINITY =
            TagKey.create(Registries.ENTITY_TYPE, Meridian.id("sensitive_to_divinity"));
    private static final TagKey<EntityType<?>> SENSITIVE_TO_VIGILANCE =
            TagKey.create(Registries.ENTITY_TYPE, Meridian.id("sensitive_to_vigilance"));
    private static final TagKey<EntityType<?>> TELURIC_BLACKLIST =
            TagKey.create(Registries.ENTITY_TYPE, Meridian.id("teluric_blacklist"));

    public MeridianEntityTypeTagProvider(FabricDataOutput output,
                                         CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(LAST_HOPE_BLACKLIST);

        getOrCreateTagBuilder(SENSITIVE_TO_CERTAINTY)
                .add(EntityType.ENDERMAN)
                .add(EntityType.ENDERMITE)
                .add(EntityType.SHULKER)
                .add(EntityType.ENDER_DRAGON);

        getOrCreateTagBuilder(SENSITIVE_TO_DIVINITY)
                .add(EntityType.BLAZE)
                .add(EntityType.GHAST)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.ZOMBIFIED_PIGLIN)
                .add(EntityType.HOGLIN)
                .add(EntityType.ZOGLIN)
                .add(EntityType.MAGMA_CUBE)
                .add(EntityType.STRIDER)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.WITHER);

        getOrCreateTagBuilder(SENSITIVE_TO_VIGILANCE)
                .add(EntityType.PILLAGER)
                .add(EntityType.VINDICATOR)
                .add(EntityType.EVOKER)
                .add(EntityType.ILLUSIONER)
                .add(EntityType.RAVAGER)
                .add(EntityType.WITCH)
                .add(EntityType.VEX);

        getOrCreateTagBuilder(TELURIC_BLACKLIST)
                .addOptionalTag(EntityTypeTags.AQUATIC)
                .addOptionalTag(EntityTypeTags.ARROWS)
                .add(EntityType.PLAYER);
    }
}
