package com.rfizzle.meridian.gametest.enchantments;

import com.rfizzle.meridian.Meridian;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class SwordEnchantmentGameTest implements FabricGameTest {

    private Holder<Enchantment> lookup(GameTestHelper helper, String id) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        return reg.getHolder(Meridian.id(id)).orElse(null);
    }

    // --- Post-attack effect tests ---

    @GameTest(template = "meridian:empty_3x3")
    public void poisonAspectAppliesWitherToMob(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "sword/poison_aspect");
        if (ench == null) { helper.fail("sword/poison_aspect not in registry"); return; }

        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(ench, 3);

        Mob attacker = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        attacker.setItemSlot(EquipmentSlot.MAINHAND, sword);

        Mob victim = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 2));

        attacker.doHurtTarget(victim);

        helper.runAfterDelay(2, () -> {
            if (victim.hasEffect(MobEffects.WITHER)) {
                helper.succeed();
            } else {
                helper.fail("Zombie victim should have Wither from Poison Aspect III (non-player path applies Wither)");
            }
        });
    }

    @GameTest(template = "meridian:empty_3x3")
    public void shieldBashDealsExtraDamage(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "shield_bash");
        if (ench == null) { helper.fail("shield_bash not in registry"); return; }

        Mob victim = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 2));
        Mob victimPlain = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));

        ItemStack enchSword = new ItemStack(Items.DIAMOND_SWORD);
        enchSword.enchant(ench, 4);
        Mob attackerEnch = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(2, 1, 2));
        attackerEnch.setItemSlot(EquipmentSlot.MAINHAND, enchSword);

        ItemStack plainSword = new ItemStack(Items.DIAMOND_SWORD);
        Mob attackerPlain = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(2, 1, 1));
        attackerPlain.setItemSlot(EquipmentSlot.MAINHAND, plainSword);

        float healthBefore = victim.getHealth();
        float healthBeforePlain = victimPlain.getHealth();

        attackerEnch.doHurtTarget(victim);
        attackerPlain.doHurtTarget(victimPlain);

        helper.runAfterDelay(2, () -> {
            float damageEnch = healthBefore - victim.getHealth();
            float damagePlain = healthBeforePlain - victimPlain.getHealth();
            if (damageEnch <= damagePlain) {
                helper.fail("Shield Bash IV should deal more damage than plain sword. Enchanted: " + damageEnch + ", plain: " + damagePlain);
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "meridian:empty_3x3")
    public void lastHopeDealsLethalDamage(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "sword/last_hope");
        if (ench == null) { helper.fail("sword/last_hope not in registry"); return; }

        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(ench, 1);

        Mob attacker = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        attacker.setItemSlot(EquipmentSlot.MAINHAND, sword);

        Mob victim = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 2));

        attacker.doHurtTarget(victim);

        helper.runAfterDelay(2, () -> {
            if (!victim.isAlive()) {
                helper.succeed();
            } else {
                helper.fail("Last Hope should deal lethal damage (21470000). Victim health: " + victim.getHealth());
            }
        });
    }

    // --- S-10.2e: Scavenger extra loot ---

    @GameTest(template = "meridian:empty_3x3", timeoutTicks = 200)
    public void scavengerProducesExtraLootOnKill(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "scavenger");
        if (ench == null) { helper.fail("scavenger not in registry"); return; }

        // Level 40: chance = 0.025 * 40 = 1.0, guaranteed extra loot roll per kill
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(ench, 40);

        Mob attacker = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        attacker.setItemSlot(EquipmentSlot.MAINHAND, sword);

        for (int i = 0; i < 30; i++) {
            Mob victim = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 2));
            victim.setHealth(1F);
            attacker.doHurtTarget(victim);
        }

        helper.runAfterDelay(5, () -> {
            // 30 kills × 2 rolls each (normal + scavenger) = 60 rolls; mean ~60 rotten flesh.
            // Without Scavenger, only 30 rolls → mean ~30. Threshold 40 separates the cases
            // with <0.01% false-negative and ~1% false-positive.
            int totalFlesh = helper.getEntities(EntityType.ITEM).stream()
                    .map(e -> ((ItemEntity) e).getItem())
                    .filter(s -> s.is(Items.ROTTEN_FLESH))
                    .mapToInt(ItemStack::getCount)
                    .sum();

            if (totalFlesh >= 40) {
                helper.succeed();
            } else {
                helper.fail("Expected >=40 rotten flesh from 30 Scavenger-40 kills (got "
                        + totalFlesh + "); extra loot may not be firing");
            }
        });
    }

    // --- Definition sweep ---

    @GameTest(template = "meridian:empty_3x3")
    public void allSwordDefinitionsValid(GameTestHelper helper) {
        record Expected(String id, int maxLevel, int weight) {}
        List<Expected> enchants = List.of(
                new Expected("sword/attack_speed", 2, 4),
                new Expected("sword/critical", 4, 4),
                new Expected("sword/death_touch", 1, 1),
                new Expected("sword/dimensional_hit", 5, 4),
                new Expected("sword/fear", 1, 2),
                new Expected("sword/last_hope", 1, 4),
                new Expected("sword/life_steal", 3, 4),
                new Expected("sword/poison_aspect", 3, 3),
                new Expected("sword/pull", 1, 1),
                new Expected("sword/reach", 3, 2),
                new Expected("sword/tears_of_asflors", 3, 4),
                new Expected("sword/xp_boost", 3, 2),
                new Expected("shield_bash", 4, 2)
        );

        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        List<String> errors = new ArrayList<>();

        for (Expected exp : enchants) {
            Holder<Enchantment> h = reg.getHolder(Meridian.id(exp.id())).orElse(null);
            if (h == null) { errors.add(exp.id() + ": missing"); continue; }
            Enchantment e = h.value();
            if (e.definition().maxLevel() != exp.maxLevel())
                errors.add(exp.id() + ": maxLevel " + e.definition().maxLevel() + " != " + exp.maxLevel());
            if (e.definition().weight() != exp.weight())
                errors.add(exp.id() + ": weight " + e.definition().weight() + " != " + exp.weight());
        }

        if (!errors.isEmpty()) {
            helper.fail("Definition errors: " + errors);
            return;
        }
        helper.succeed();
    }
}
