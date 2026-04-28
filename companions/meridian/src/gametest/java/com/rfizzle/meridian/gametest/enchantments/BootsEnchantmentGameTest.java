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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class BootsEnchantmentGameTest implements FabricGameTest {

    private Holder<Enchantment> lookup(GameTestHelper helper, String id) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        return reg.getHolder(Meridian.id(id)).orElse(null);
    }

    // --- Effect tests ---

    @GameTest(template = "meridian:empty_3x3")
    public void agilityIncreasesMovementSpeed(GameTestHelper helper) {
        Holder<Enchantment> agility = lookup(helper, "boots/agility");
        if (agility == null) { helper.fail("boots/agility not in registry"); return; }

        Mob mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        double baseSpeed = mob.getAttributeValue(Attributes.MOVEMENT_SPEED);

        ItemStack boots = new ItemStack(Items.DIAMOND_BOOTS);
        boots.enchant(agility, 3);
        mob.setItemSlot(EquipmentSlot.FEET, boots);

        helper.runAfterDelay(1, () -> {
            double modified = mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
            if (modified <= baseSpeed) {
                helper.fail("Agility III should increase movement speed. Base: " + baseSpeed + ", got: " + modified);
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "meridian:empty_3x3")
    public void stepAssistIncreasesStepHeight(GameTestHelper helper) {
        Holder<Enchantment> stepAssist = lookup(helper, "boots/step_assist");
        if (stepAssist == null) { helper.fail("boots/step_assist not in registry"); return; }

        Mob mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        double baseStep = mob.getAttributeValue(Attributes.STEP_HEIGHT);

        ItemStack boots = new ItemStack(Items.DIAMOND_BOOTS);
        boots.enchant(stepAssist, 2);
        mob.setItemSlot(EquipmentSlot.FEET, boots);

        helper.runAfterDelay(1, () -> {
            double modified = mob.getAttributeValue(Attributes.STEP_HEIGHT);
            if (modified <= baseStep) {
                helper.fail("Step Assist II should increase step height. Base: " + baseStep + ", got: " + modified);
                return;
            }
            helper.succeed();
        });
    }

    // --- Definition sweep ---

    @GameTest(template = "meridian:empty_3x3")
    public void allBootsDefinitionsValid(GameTestHelper helper) {
        record Expected(String id, int maxLevel, int weight) {}
        List<Expected> enchants = List.of(
                new Expected("boots/agility", 5, 8),
                new Expected("boots/lava_walker", 3, 1),
                new Expected("boots/step_assist", 3, 1)
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
