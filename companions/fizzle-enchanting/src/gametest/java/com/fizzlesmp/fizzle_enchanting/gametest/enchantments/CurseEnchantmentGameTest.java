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

public class CurseEnchantmentGameTest implements FabricGameTest {

    // Curse of Breaking uses probabilistic item_damage — not reliably testable.
    // Curse of Enchant has empty effects (marker enchantment).
    // Both are validated at the definition level.

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void allCurseDefinitionsValid(GameTestHelper helper) {
        record Expected(String id, int maxLevel, int weight) {}
        List<Expected> enchants = List.of(
                new Expected("durability/curse_of_breaking", 5, 3),
                new Expected("durability/curse_of_enchant", 1, 4)
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
