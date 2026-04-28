package com.fizzlesmp.fizzle_enchanting.gametest.enchantments;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;

public class MaceEnchantmentGameTest implements FabricGameTest {

    private Holder<Enchantment> lookup(GameTestHelper helper, String id) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        return reg.getHolder(FizzleEnchanting.id(id)).orElse(null);
    }

    // --- Effect tests ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void strikerGrantsLightningImmunity(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "mace/striker");
        if (ench == null) { helper.fail("mace/striker not in registry"); return; }

        ItemStack mace = new ItemStack(Items.MACE);
        mace.enchant(ench, 2);

        Mob mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        mob.setItemSlot(EquipmentSlot.MAINHAND, mace);

        helper.runAfterDelay(2, () -> {
            ServerLevel level = helper.getLevel();
            DamageSource lightningDmg = level.damageSources().lightningBolt();

            ItemStack held = mob.getItemBySlot(EquipmentSlot.MAINHAND);
            if (held.isEmpty()) {
                helper.fail("Mob mainhand is empty after setItemSlot");
                return;
            }
            if (!held.isEnchanted()) {
                helper.fail("Mob mainhand item has no enchantments");
                return;
            }

            boolean isImmune = EnchantmentHelper.isImmuneToDamage(level, mob, lightningDmg);
            if (!isImmune) {
                boolean invulnerable = mob.isInvulnerableTo(lightningDmg);
                helper.fail("isImmuneToDamage=false, isInvulnerableTo=" + invulnerable
                        + ", item=" + held + ", enchants=" + held.getEnchantments());
                return;
            }

            float healthBefore = mob.getHealth();
            mob.hurt(lightningDmg, 10.0f);

            helper.runAfterDelay(1, () -> {
                float healthAfter = mob.getHealth();
                if (healthAfter >= healthBefore) {
                    helper.succeed();
                } else {
                    helper.fail("Damage went through despite isImmuneToDamage=true. Before: " + healthBefore + ", after: " + healthAfter);
                }
            });
        });
    }

    // --- Definition sweep ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void allMaceDefinitionsValid(GameTestHelper helper) {
        record Expected(String id, int maxLevel, int weight) {}
        List<Expected> enchants = List.of(
                new Expected("mace/striker", 2, 1),
                new Expected("mace/teluric_wave", 1, 2),
                new Expected("mace/wind_propulsion", 3, 2)
        );

        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        List<String> errors = new ArrayList<>();

        for (Expected exp : enchants) {
            Holder<Enchantment> h = reg.getHolder(FizzleEnchanting.id(exp.id())).orElse(null);
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
