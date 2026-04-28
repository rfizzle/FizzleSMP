package com.rfizzle.meridian.event;

import com.rfizzle.meridian.Meridian;
import com.rfizzle.meridian.enchanting.EnchantmentEffects;
import com.rfizzle.meridian.mixin.LivingEntityLootInvoker;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.WeakHashMap;

public final class EnchantmentEffectHandler {

    private static final ResourceKey<DamageType> CORRUPTED =
            ResourceKey.create(Registries.DAMAGE_TYPE, Meridian.id("corrupted"));

    private static final WeakHashMap<LivingEntity, Long> berserkerCooldowns = new WeakHashMap<>();
    private static final int BERSERKER_COOLDOWN_TICKS = 900;

    private static boolean applyingBerserkerDamage = false;

    private EnchantmentEffectHandler() {}

    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(EnchantmentEffectHandler::onAfterDamage);
        ServerLivingEntityEvents.AFTER_DEATH.register(EnchantmentEffectHandler::onAfterDeath);
    }

    private static void onAfterDamage(LivingEntity entity, DamageSource source,
                                       float baseDamageTaken, float damageTaken, boolean blocked) {
        if (entity.level().isClientSide()) return;

        if (blocked) {
            handleReflectiveDefenses(entity, source, baseDamageTaken);
        }

        if (damageTaken <= 0 && !blocked) return;

        handleRebounding(entity, source);

        if (!applyingBerserkerDamage) {
            handleBerserkersFury(entity);
        }
    }

    private static void handleReflectiveDefenses(LivingEntity entity, DamageSource source,
                                                  float blockedAmount) {
        ItemStack useItem = entity.getUseItem();
        int level = EnchantmentEffects.getEnchantmentLevel(useItem, "reflective_defenses");
        if (level <= 0) return;

        Entity attacker = source.getDirectEntity();
        if (!(attacker instanceof LivingEntity livingAttacker)) return;

        float procChance = 0.15f + 0.10f * level;
        if (entity.getRandom().nextFloat() >= procChance) return;

        float reflectRatio = 0.15f * level;
        float reflectDamage = blockedAmount * reflectRatio;

        if (reflectDamage > 0) {
            livingAttacker.hurt(entity.damageSources().thorns(entity), reflectDamage);
        }
    }

    private static void handleRebounding(LivingEntity entity, DamageSource source) {
        int level = EnchantmentEffects.getEquippedLevel(entity, "rebounding",
                EquipmentSlot.CHEST, EquipmentSlot.LEGS);
        if (level <= 0) return;

        Entity attacker = source.getDirectEntity();
        if (attacker == null) return;

        double distSq = attacker.distanceToSqr(entity);
        if (distSq > 16.0) return; // 4 block range

        Vec3 knockback = attacker.position().subtract(entity.position()).normalize();
        double horizontalForce = 0.5 * level;
        double verticalForce = 0.15 * level;
        attacker.push(knockback.x * horizontalForce, verticalForce, knockback.z * horizontalForce);

        if (attacker instanceof ServerPlayer serverPlayer) {
            serverPlayer.hurtMarked = true;
        }
    }

    private static void handleBerserkersFury(LivingEntity entity) {
        int level = EnchantmentEffects.getEquippedLevel(entity, "berserkers_fury", EquipmentSlot.CHEST);
        if (level <= 0) return;

        long currentTick = entity.level().getGameTime();
        Long lastActivation = berserkerCooldowns.get(entity);
        if (lastActivation != null && (currentTick - lastActivation) < BERSERKER_COOLDOWN_TICKS) return;

        if (entity.level() instanceof ServerLevel serverLevel) {
            Holder<DamageType> damageType = serverLevel.registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(CORRUPTED);

            float hpCost = 2.5f * level;
            applyingBerserkerDamage = true;
            try {
                entity.hurt(new DamageSource(damageType), hpCost);
            } finally {
                applyingBerserkerDamage = false;
            }
        }

        int duration = 500;
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, level - 1));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, level - 1));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, level - 1));

        berserkerCooldowns.put(entity, currentTick);
    }

    private static void onAfterDeath(LivingEntity entity, DamageSource source) {
        if (entity.level().isClientSide()) return;
        handleScavenger(entity, source);
    }

    private static void handleScavenger(LivingEntity entity, DamageSource source) {
        Entity killer = source.getEntity();
        if (!(killer instanceof LivingEntity livingKiller)) return;

        int level = EnchantmentEffects.getEnchantmentLevel(livingKiller.getMainHandItem(), "scavenger");
        if (level <= 0) return;

        float chance = 0.025f * level;
        if (entity.getRandom().nextFloat() >= chance) return;

        if (entity.level().isClientSide()) return;

        boolean hitByPlayer = entity.getKillCredit() != null;
        ((LivingEntityLootInvoker) entity).fizzle$invokeDropFromLootTable(source, hitByPlayer);
    }
}
