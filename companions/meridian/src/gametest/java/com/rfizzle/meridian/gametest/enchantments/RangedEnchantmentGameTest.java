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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class RangedEnchantmentGameTest implements FabricGameTest {

    private Holder<Enchantment> lookup(GameTestHelper helper, String id) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        return reg.getHolder(Meridian.id(id)).orElse(null);
    }

    // --- Effect tests ---

    @GameTest(template = "meridian:empty_3x3")
    public void eternalFrostAppliesSlowness(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "bow/eternal_frost");
        if (ench == null) { helper.fail("bow/eternal_frost not in registry"); return; }

        // The second post_attack entry applies Slowness unconditionally (no requirements)
        ItemStack bow = new ItemStack(Items.BOW);
        bow.enchant(ench, 1);

        Mob attacker = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        attacker.setItemSlot(EquipmentSlot.MAINHAND, bow);

        Mob victim = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 2));

        attacker.doHurtTarget(victim);

        helper.runAfterDelay(2, () -> {
            if (victim.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                helper.succeed();
            } else {
                helper.fail("Eternal Frost should apply Slowness to victim (unconditional post_attack entry)");
            }
        });
    }

    @GameTest(template = "meridian:empty_3x3")
    public void gungnirBreathAppliesSlowness(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "trident/gungnir_breath");
        if (ench == null) { helper.fail("trident/gungnir_breath not in registry"); return; }

        // The second post_attack entry applies Slowness unconditionally
        ItemStack trident = new ItemStack(Items.TRIDENT);
        trident.enchant(ench, 3);

        Mob attacker = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        attacker.setItemSlot(EquipmentSlot.MAINHAND, trident);

        Mob victim = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 2));

        attacker.doHurtTarget(victim);

        helper.runAfterDelay(2, () -> {
            if (victim.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                helper.succeed();
            } else {
                helper.fail("Gungnir Breath III should apply Slowness to victim");
            }
        });
    }

    // --- Definition sweep ---

    @GameTest(template = "meridian:empty_3x3")
    public void allRangedDefinitionsValid(GameTestHelper helper) {
        record Expected(String id, int maxLevel, int weight) {}
        List<Expected> enchants = List.of(
                new Expected("bow/accuracy_shot", 1, 2),
                new Expected("bow/breezing_arrow", 2, 2),
                new Expected("bow/echo_shot", 2, 2),
                new Expected("bow/eternal_frost", 1, 2),
                new Expected("bow/explosive_arrow", 4, 2),
                new Expected("bow/rebound", 3, 4),
                new Expected("bow/storm_arrow", 1, 2),
                new Expected("trident/gungnir_breath", 3, 2)
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
