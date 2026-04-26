package com.fizzlesmp.fizzle_enchanting.gametest.enchantments;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class UpperBodyEnchantmentGameTest implements FabricGameTest {

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void allHelmetDefinitionsValid(GameTestHelper helper) {
        record Expected(String id, int maxLevel, int weight) {}
        List<Expected> enchants = List.of(
                new Expected("helmet/bright_vision", 1, 2),
                new Expected("helmet/voidless", 1, 8)
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

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void chestplateBuilderArmDefinitionValid(GameTestHelper helper) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> h = reg.getHolder(FizzleEnchanting.id("chestplate/builder_arm")).orElse(null);
        if (h == null) { helper.fail("chestplate/builder_arm: missing"); return; }

        Enchantment e = h.value();
        List<String> errors = new ArrayList<>();
        if (e.definition().maxLevel() != 3) errors.add("maxLevel " + e.definition().maxLevel() + " != 3");
        if (e.definition().weight() != 2) errors.add("weight " + e.definition().weight() + " != 2");

        if (!errors.isEmpty()) {
            helper.fail("builder_arm: " + errors);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void allElytraDefinitionsValid(GameTestHelper helper) {
        record Expected(String id, int maxLevel, int weight) {}
        List<Expected> enchants = List.of(
                new Expected("elytra/armored", 4, 1),
                new Expected("elytra/kinetic_protection", 5, 1)
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
