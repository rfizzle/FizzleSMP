// Tier: 3 (Fabric Gametest) — TEST-6.2-T3
package com.fizzlesmp.fizzle_enchanting.gametest;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

public class IcyThornsEffectGameTest implements FabricGameTest {

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void icyThornsAppliesSlownessToAttacker(GameTestHelper helper) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);

        ResourceLocation icyThornsId = FizzleEnchanting.id("icy_thorns");
        Holder<Enchantment> icyThorns = reg.getHolder(icyThornsId).orElse(null);
        if (icyThorns == null) {
            helper.fail("icy_thorns enchantment not in registry");
            return;
        }

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
}
