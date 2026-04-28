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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class ArmorEnchantmentGameTest implements FabricGameTest {

    private Holder<Enchantment> lookup(GameTestHelper helper, String id) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        return reg.getHolder(Meridian.id(id)).orElse(null);
    }

    // --- Effect tests ---

    @GameTest(template = "meridian:empty_3x3")
    public void furyIncreasesAttackDamage(GameTestHelper helper) {
        Holder<Enchantment> fury = lookup(helper, "armor/fury");
        if (fury == null) { helper.fail("armor/fury not in registry"); return; }

        Mob mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        double baseDamage = mob.getAttributeValue(Attributes.ATTACK_DAMAGE);

        ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        chestplate.enchant(fury, 3);
        mob.setItemSlot(EquipmentSlot.CHEST, chestplate);

        helper.runAfterDelay(1, () -> {
            double modified = mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
            if (modified <= baseDamage) {
                helper.fail("Fury III should increase attack damage. Base: " + baseDamage + ", got: " + modified);
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "meridian:empty_3x3")
    public void furyDecreasesArmor(GameTestHelper helper) {
        Holder<Enchantment> fury = lookup(helper, "armor/fury");
        if (fury == null) { helper.fail("armor/fury not in registry"); return; }

        Mob mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));

        ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        chestplate.enchant(fury, 3);
        mob.setItemSlot(EquipmentSlot.CHEST, chestplate);

        helper.runAfterDelay(1, () -> {
            ItemStack plain = new ItemStack(Items.DIAMOND_CHESTPLATE);
            Mob ref = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 2));
            ref.setItemSlot(EquipmentSlot.CHEST, plain);

            helper.runAfterDelay(1, () -> {
                double furyArmor = mob.getAttributeValue(Attributes.ARMOR);
                double plainArmor = ref.getAttributeValue(Attributes.ARMOR);
                if (furyArmor >= plainArmor) {
                    helper.fail("Fury III should decrease armor. Fury: " + furyArmor + ", plain: " + plainArmor);
                    return;
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "meridian:empty_3x3")
    public void lifeplusIncreasesMaxHealth(GameTestHelper helper) {
        Holder<Enchantment> lifeplus = lookup(helper, "armor/lifeplus");
        if (lifeplus == null) { helper.fail("armor/lifeplus not in registry"); return; }

        Mob mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        double baseHealth = mob.getAttributeValue(Attributes.MAX_HEALTH);

        ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        chestplate.enchant(lifeplus, 5);
        mob.setItemSlot(EquipmentSlot.CHEST, chestplate);

        helper.runAfterDelay(1, () -> {
            double modified = mob.getAttributeValue(Attributes.MAX_HEALTH);
            double expected = baseHealth + 10.0;
            if (Math.abs(modified - expected) > 0.01) {
                helper.fail("Life+ V should add 10 HP. Base: " + baseHealth + ", expected: " + expected + ", got: " + modified);
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "meridian:empty_3x3")
    public void icyThornsAppliesSlownessToAttacker(GameTestHelper helper) {
        Holder<Enchantment> icyThorns = lookup(helper, "icy_thorns");
        if (icyThorns == null) { helper.fail("icy_thorns not in registry"); return; }

        ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        chestplate.enchant(icyThorns, 3);

        Mob victim = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        victim.setItemSlot(EquipmentSlot.CHEST, chestplate);

        Mob attacker = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 2));

        attacker.doHurtTarget(victim);

        helper.runAfterDelay(2, () -> {
            if (attacker.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                helper.succeed();
            } else {
                helper.fail("Attacker should have Slowness after hitting a victim wearing Icy Thorns III");
            }
        });
    }

    // --- Definition sweep ---

    @GameTest(template = "meridian:empty_3x3")
    public void allArmorDefinitionsValid(GameTestHelper helper) {
        record Expected(String id, int maxLevel, int weight) {}
        List<Expected> enchants = List.of(
                new Expected("armor/fury", 3, 8),
                new Expected("armor/lifeplus", 5, 8),
                new Expected("armor/venom_protection", 1, 6),
                new Expected("icy_thorns", 3, 2)
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
