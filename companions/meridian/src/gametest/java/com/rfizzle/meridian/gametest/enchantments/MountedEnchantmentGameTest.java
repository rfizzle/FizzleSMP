package com.rfizzle.meridian.gametest.enchantments;

import com.rfizzle.meridian.Meridian;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class MountedEnchantmentGameTest implements FabricGameTest {

    private Holder<Enchantment> lookup(GameTestHelper helper, String id) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        return reg.getHolder(Meridian.id(id)).orElse(null);
    }

    // --- Attribute effect tests ---

    @GameTest(template = "meridian:empty_3x3")
    public void velocityIncreasesHorseSpeed(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "mounted/velocity");
        if (ench == null) { helper.fail("mounted/velocity not in registry"); return; }

        Horse horse = helper.spawnWithNoFreeWill(EntityType.HORSE, new BlockPos(1, 1, 1));
        double baseSpeed = horse.getAttributeValue(Attributes.MOVEMENT_SPEED);

        ItemStack armor = new ItemStack(Items.DIAMOND_HORSE_ARMOR);
        armor.enchant(ench, 4);
        horse.setItemSlot(EquipmentSlot.BODY, armor);

        helper.runAfterDelay(1, () -> {
            double modified = horse.getAttributeValue(Attributes.MOVEMENT_SPEED);
            if (modified <= baseSpeed) {
                helper.fail("Velocity IV should increase horse speed. Base: " + baseSpeed + ", got: " + modified);
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "meridian:empty_3x3")
    public void etherealLeapIncreasesHorseJump(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "mounted/ethereal_leap");
        if (ench == null) { helper.fail("mounted/ethereal_leap not in registry"); return; }

        Horse horse = helper.spawnWithNoFreeWill(EntityType.HORSE, new BlockPos(1, 1, 1));
        double baseJump = horse.getAttributeValue(Attributes.JUMP_STRENGTH);

        ItemStack armor = new ItemStack(Items.DIAMOND_HORSE_ARMOR);
        armor.enchant(ench, 5);
        horse.setItemSlot(EquipmentSlot.BODY, armor);

        helper.runAfterDelay(1, () -> {
            double modified = horse.getAttributeValue(Attributes.JUMP_STRENGTH);
            if (modified <= baseJump) {
                helper.fail("Ethereal Leap V should increase horse jump. Base: " + baseJump + ", got: " + modified);
                return;
            }
            helper.succeed();
        });
    }

    // --- Protection effect tests ---

    @GameTest(template = "meridian:empty_3x3")
    public void cavalierEgisReducesDamage(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "mounted/cavalier_egis");
        if (ench == null) { helper.fail("mounted/cavalier_egis not in registry"); return; }

        Horse horse = helper.spawnWithNoFreeWill(EntityType.HORSE, new BlockPos(1, 1, 1));
        ItemStack armor = new ItemStack(Items.DIAMOND_HORSE_ARMOR);
        armor.enchant(ench, 5);
        horse.setItemSlot(EquipmentSlot.BODY, armor);

        Horse horsePlain = helper.spawnWithNoFreeWill(EntityType.HORSE, new BlockPos(1, 1, 2));
        horsePlain.setItemSlot(EquipmentSlot.BODY, new ItemStack(Items.DIAMOND_HORSE_ARMOR));

        helper.runAfterDelay(1, () -> {
            float hp1 = horse.getHealth();
            float hp2 = horsePlain.getHealth();

            horse.hurt(helper.getLevel().damageSources().generic(), 10.0f);
            horsePlain.hurt(helper.getLevel().damageSources().generic(), 10.0f);

            helper.runAfterDelay(1, () -> {
                float dmgEnch = hp1 - horse.getHealth();
                float dmgPlain = hp2 - horsePlain.getHealth();
                if (dmgEnch >= dmgPlain) {
                    helper.fail("Cavalier Egis V should reduce damage. Enchanted took: " + dmgEnch + ", plain took: " + dmgPlain);
                    return;
                }
                helper.succeed();
            });
        });
    }

    // --- Definition sweep ---

    @GameTest(template = "meridian:empty_3x3")
    public void allMountedDefinitionsValid(GameTestHelper helper) {
        record Expected(String id, int maxLevel, int weight) {}
        List<Expected> enchants = List.of(
                new Expected("mounted/cavalier_egis", 5, 4),
                new Expected("mounted/ethereal_leap", 7, 2),
                new Expected("mounted/steel_fang", 3, 2),
                new Expected("mounted/velocity", 4, 5)
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
