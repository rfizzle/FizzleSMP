package com.rfizzle.meridian.gametest.enchantments;

import com.rfizzle.meridian.Meridian;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;

public class CustomEnchantmentGameTest implements FabricGameTest {

    private Holder<Enchantment> lookup(GameTestHelper helper, String id) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        return reg.getHolder(Meridian.id(id)).orElse(null);
    }

    @GameTest(template = "meridian:empty_3x3")
    public void bagOfSoulsDefinition(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "bag_of_souls");
        if (ench == null) { helper.fail("bag_of_souls not in registry"); return; }

        Enchantment e = ench.value();
        if (e.definition().maxLevel() != 3) {
            helper.fail("Bag of Souls should have maxLevel 3, got " + e.definition().maxLevel());
            return;
        }
        if (e.definition().weight() != 2) {
            helper.fail("Bag of Souls should have weight 2, got " + e.definition().weight());
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "meridian:empty_3x3")
    public void bagOfSoulsHasXpEffects(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "bag_of_souls");
        if (ench == null) { helper.fail("bag_of_souls not in registry"); return; }

        Enchantment e = ench.value();
        boolean hasMobXp = !e.getEffects(EnchantmentEffectComponents.MOB_EXPERIENCE).isEmpty();
        boolean hasBlockXp = !e.getEffects(EnchantmentEffectComponents.BLOCK_EXPERIENCE).isEmpty();

        if (!hasMobXp || !hasBlockXp) {
            helper.fail("Bag of Souls must have both MOB_EXPERIENCE and BLOCK_EXPERIENCE effects. "
                    + "mob=" + hasMobXp + ", block=" + hasBlockXp);
            return;
        }
        helper.succeed();
    }
}
