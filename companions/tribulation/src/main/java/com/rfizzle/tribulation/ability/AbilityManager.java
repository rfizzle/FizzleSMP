package com.rfizzle.tribulation.ability;

import com.rfizzle.tribulation.Tribulation;
import com.rfizzle.tribulation.config.TribulationConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Applies tier-based abilities to scaled mobs after the {@link
 * com.rfizzle.tribulation.scaling.ScalingEngine} has set stat
 * modifiers. Each ability is expressed as a namespaced attribute modifier, an
 * infinite-duration status effect, a vanilla setter, or an equipment change
 * — all of which persist in the mob's own NBT, so abilities survive save/load
 * without extra tracking.
 *
 * <p>Only abilities that can be implemented without mixins live here.
 * Abilities that require behavioral changes to AI goals, projectile effects,
 * or packet-level tweaks (spider webs, silverfish spread, creeper fuse,
 * bogged poison arrows, etc.) are deferred to the mixin-based follow-up
 * under the same task in TODO.md. Deferred abilities are noted inline so the
 * gap is easy to audit.
 */
public final class AbilityManager {
    private static final double ZOMBIE_REINFORCEMENT_BONUS = 0.10;
    private static final double SPRINT_SPEED_BONUS = 0.15;
    private static final double HOGLIN_KB_BONUS = 0.5;
    private static final double ZOMBIFIED_PIGLIN_AGGRO_BONUS = 0.5;

    private AbilityManager() {}

    public static ResourceLocation abilityId(String name) {
        return ResourceLocation.fromNamespaceAndPath(Tribulation.MOD_ID, "ability_" + name);
    }

    /**
     * Apply tier-appropriate abilities to the mob. Tier 0 and unknown mob
     * keys are no-ops. Exceptions are caught and logged so one bad entity
     * never aborts the spawn handler.
     */
    public static void applyAbilities(Mob mob, int tier, String mobKey, TribulationConfig cfg) {
        if (mob == null || cfg == null || mobKey == null || tier <= 0) return;
        try {
            switch (mobKey) {
                case "zombie" -> applyZombieAbilities(mob, tier);
                case "drowned" -> applyDrownedAbilities(mob, tier);
                case "zombified_piglin" -> applyZombifiedPiglinAbilities(mob, tier);
                case "hoglin" -> applyHoglinAbilities(mob, tier);
                case "zoglin" -> applyZoglinAbilities(mob, tier);
                case "vindicator" -> applyVindicatorAbilities(mob, tier);
                case "wither_skeleton" -> applyWitherSkeletonAbilities(mob, tier);
                case "piglin" -> applyPiglinAbilities(mob, tier);
                default -> {
                    // Remaining mob abilities (skeleton, creeper, spider, cave_spider,
                    // endermite, silverfish, husk, stray, pillager, witch, guardian,
                    // ravager, bogged) require mixins — tracked in TODO Task 7 follow-up.
                }
            }
        } catch (Exception e) {
            Tribulation.LOGGER.warn("Failed applying abilities to {} tier {}", mobKey, tier, e);
        }
    }

    private static void applyZombieAbilities(Mob mob, int tier) {
        // T1: reinforcement calls — raise base chance of summoning backup on damage.
        if (tier >= 1) {
            addAttributeModifier(mob, Attributes.SPAWN_REINFORCEMENTS_CHANCE,
                    abilityId("zombie_reinforcements"), ZOMBIE_REINFORCEMENT_BONUS,
                    AttributeModifier.Operation.ADD_VALUE);
        }
        // T3: break doors — vanilla AI goal swap. All wood door types are broken.
        if (tier >= 3 && mob instanceof Zombie zombie) {
            zombie.setCanBreakDoors(true);
        }
        // T5: sprint at target — permanent speed boost on top of time-axis scaling.
        if (tier >= 5) {
            addAttributeModifier(mob, Attributes.MOVEMENT_SPEED,
                    abilityId("zombie_sprint"), SPRINT_SPEED_BONUS,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }

    private static void applyDrownedAbilities(Mob mob, int tier) {
        // T2: trident upgrade — fill empty mainhand with a trident so ranged
        // drowned are guaranteed at high difficulty. Existing weapons and
        // nautilus shells are left alone.
        if (tier >= 2 && mob.getMainHandItem().isEmpty()) {
            mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.TRIDENT));
        }
    }

    private static void applyZombifiedPiglinAbilities(Mob mob, int tier) {
        // T5: group aggro — expand follow range so one angered piglin pulls in
        // nearby kin. Easier anger (T1) requires mixin and is deferred.
        if (tier >= 5) {
            addAttributeModifier(mob, Attributes.FOLLOW_RANGE,
                    abilityId("zombified_piglin_aggro"), ZOMBIFIED_PIGLIN_AGGRO_BONUS,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }

    private static void applyHoglinAbilities(Mob mob, int tier) {
        // T1: knockback resistance — harder to combo-punch off cliffs.
        if (tier >= 1) {
            addAttributeModifier(mob, Attributes.KNOCKBACK_RESISTANCE,
                    abilityId("hoglin_kb_resist"), HOGLIN_KB_BONUS,
                    AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void applyZoglinAbilities(Mob mob, int tier) {
        // T3: fire resistance — infinite-duration effect survives save/load.
        if (tier >= 3) {
            applyInfiniteEffect(mob, MobEffects.FIRE_RESISTANCE, 0);
        }
    }

    private static void applyVindicatorAbilities(Mob mob, int tier) {
        // T4: resistance I — cuts incoming damage by 20%.
        if (tier >= 4) {
            applyInfiniteEffect(mob, MobEffects.DAMAGE_RESISTANCE, 0);
        }
    }

    private static void applyWitherSkeletonAbilities(Mob mob, int tier) {
        // T3: sprint — passive speed boost. Fire aspect (T4) is deferred —
        // adding enchantments in 1.21.1 requires registry access that isn't
        // convenient here without a small helper in a later task.
        if (tier >= 3) {
            addAttributeModifier(mob, Attributes.MOVEMENT_SPEED,
                    abilityId("wither_skeleton_sprint"), SPRINT_SPEED_BONUS,
                    AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        }
    }

    private static void applyPiglinAbilities(Mob mob, int tier) {
        // T2: better gear — equip a crossbow if the piglin spawned unarmed.
        // Don't overwrite an existing weapon — piglins naturally spawn with
        // gold swords sometimes, which we want to keep.
        if (tier >= 2 && mob.getMainHandItem().isEmpty()) {
            mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
        }
    }

    private static void addAttributeModifier(Mob mob, Holder<Attribute> attr, ResourceLocation id, double amount, AttributeModifier.Operation op) {
        AttributeInstance inst = mob.getAttribute(attr);
        if (inst == null) return;
        inst.removeModifier(id);
        inst.addPermanentModifier(new AttributeModifier(id, amount, op));
    }

    private static void applyInfiniteEffect(Mob mob, Holder<MobEffect> effect, int amplifier) {
        mob.addEffect(new MobEffectInstance(effect, MobEffectInstance.INFINITE_DURATION, amplifier, false, false));
    }
}
