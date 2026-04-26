package com.fizzlesmp.fizzle_enchanting.gametest.enchantments;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
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

public class LeggingsEnchantmentGameTest implements FabricGameTest {

    private Holder<Enchantment> lookup(GameTestHelper helper, String id) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        return reg.getHolder(FizzleEnchanting.id(id)).orElse(null);
    }

    // --- Effect tests ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void dwarfedDecreasesScale(GameTestHelper helper) {
        Holder<Enchantment> dwarfed = lookup(helper, "leggings/dwarfed");
        if (dwarfed == null) { helper.fail("leggings/dwarfed not in registry"); return; }

        Mob mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        double baseScale = mob.getAttributeValue(Attributes.SCALE);

        ItemStack leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
        leggings.enchant(dwarfed, 3);
        mob.setItemSlot(EquipmentSlot.LEGS, leggings);

        helper.runAfterDelay(1, () -> {
            double modified = mob.getAttributeValue(Attributes.SCALE);
            if (modified >= baseScale) {
                helper.fail("Dwarfed III should decrease scale. Base: " + baseScale + ", got: " + modified);
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void oversizeIncreasesScale(GameTestHelper helper) {
        Holder<Enchantment> oversize = lookup(helper, "leggings/oversize");
        if (oversize == null) { helper.fail("leggings/oversize not in registry"); return; }

        Mob mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        double baseScale = mob.getAttributeValue(Attributes.SCALE);

        ItemStack leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
        leggings.enchant(oversize, 3);
        mob.setItemSlot(EquipmentSlot.LEGS, leggings);

        helper.runAfterDelay(1, () -> {
            double modified = mob.getAttributeValue(Attributes.SCALE);
            if (modified <= baseScale) {
                helper.fail("Oversize III should increase scale. Base: " + baseScale + ", got: " + modified);
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void oversizeIncreasesStepHeight(GameTestHelper helper) {
        Holder<Enchantment> oversize = lookup(helper, "leggings/oversize");
        if (oversize == null) { helper.fail("leggings/oversize not in registry"); return; }

        Mob mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        double baseStep = mob.getAttributeValue(Attributes.STEP_HEIGHT);

        ItemStack leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
        leggings.enchant(oversize, 3);
        mob.setItemSlot(EquipmentSlot.LEGS, leggings);

        helper.runAfterDelay(1, () -> {
            double modified = mob.getAttributeValue(Attributes.STEP_HEIGHT);
            if (modified <= baseStep) {
                helper.fail("Oversize III should increase step height. Base: " + baseStep + ", got: " + modified);
                return;
            }
            helper.succeed();
        });
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void leapingIncreasesJumpStrength(GameTestHelper helper) {
        Holder<Enchantment> leaping = lookup(helper, "leggings/leaping");
        if (leaping == null) { helper.fail("leggings/leaping not in registry"); return; }

        Mob mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        double baseJump = mob.getAttributeValue(Attributes.JUMP_STRENGTH);

        ItemStack leggings = new ItemStack(Items.DIAMOND_LEGGINGS);
        leggings.enchant(leaping, 3);
        mob.setItemSlot(EquipmentSlot.LEGS, leggings);

        helper.runAfterDelay(1, () -> {
            double modified = mob.getAttributeValue(Attributes.JUMP_STRENGTH);
            if (modified <= baseJump) {
                helper.fail("Leaping III should increase jump strength. Base: " + baseJump + ", got: " + modified);
                return;
            }
            helper.succeed();
        });
    }

    // --- Definition sweep ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void allLeggingsDefinitionsValid(GameTestHelper helper) {
        record Expected(String id, int maxLevel, int weight) {}
        List<Expected> enchants = List.of(
                new Expected("leggings/dwarfed", 5, 5),
                new Expected("leggings/fast_swim", 1, 2),
                new Expected("leggings/leaping", 3, 2),
                new Expected("leggings/oversize", 4, 5)
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
