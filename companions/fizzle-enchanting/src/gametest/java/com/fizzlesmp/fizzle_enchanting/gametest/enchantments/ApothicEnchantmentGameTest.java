package com.fizzlesmp.fizzle_enchanting.gametest.enchantments;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class ApothicEnchantmentGameTest implements FabricGameTest {

    private Holder<Enchantment> lookup(GameTestHelper helper, String id) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        return reg.getHolder(FizzleEnchanting.id(id)).orElse(null);
    }

    // --- Definition sweep ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void allApothicDefinitionsValid(GameTestHelper helper) {
        record Expected(String id, int maxLevel, int weight) {}
        List<Expected> enchants = List.of(
                new Expected("bag_of_souls", 3, 2),
                new Expected("berserkers_fury", 3, 1),
                new Expected("chromatic", 1, 5),
                new Expected("growth_serum", 1, 1),
                new Expected("life_mending", 3, 1),
                new Expected("rebounding", 3, 2),
                new Expected("reflective_defenses", 5, 2),
                new Expected("scavenger", 3, 1),
                new Expected("stable_footing", 1, 2),
                new Expected("temptation", 1, 5)
        );

        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        List<String> errors = new ArrayList<>();

        for (Expected exp : enchants) {
            Holder<Enchantment> h = reg.getHolder(FizzleEnchanting.id(exp.id())).orElse(null);
            if (h == null) { errors.add(exp.id() + ": missing from registry"); continue; }
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

    // --- Bag of Souls: verify XP-multiplying effect component present ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void bagOfSoulsHasXpEffects(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "bag_of_souls");
        if (ench == null) { helper.fail("bag_of_souls not in registry"); return; }

        Enchantment e = ench.value();
        boolean hasMobXp = !e.getEffects(net.minecraft.world.item.enchantment.EnchantmentEffectComponents.MOB_EXPERIENCE).isEmpty();
        boolean hasBlockXp = !e.getEffects(net.minecraft.world.item.enchantment.EnchantmentEffectComponents.BLOCK_EXPERIENCE).isEmpty();

        if (!hasMobXp || !hasBlockXp) {
            helper.fail("Bag of Souls must have both MOB_EXPERIENCE and BLOCK_EXPERIENCE effects. "
                    + "mob=" + hasMobXp + ", block=" + hasBlockXp);
            return;
        }
        helper.succeed();
    }

    // --- Rebounding: attacker should be pushed away ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void reboundingKnocksBackAttacker(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "rebounding");
        if (ench == null) { helper.fail("rebounding not in registry"); return; }

        Mob victim = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        chestplate.enchant(ench, 3);
        victim.setItemSlot(EquipmentSlot.CHEST, chestplate);

        Mob attacker = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 2));
        Vec3 preAttackPos = attacker.position();

        attacker.doHurtTarget(victim);

        helper.runAfterDelay(3, () -> {
            Vec3 postAttackPos = attacker.position();
            double distance = preAttackPos.distanceTo(postAttackPos);
            if (distance > 0.05) {
                helper.succeed();
            } else {
                helper.fail("Rebounding III should push the attacker away. "
                        + "Movement: " + distance);
            }
        });
    }

    // --- Berserker's Fury: should apply effects after taking damage ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void berserkersFuryAppliesEffects(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "berserkers_fury");
        if (ench == null) { helper.fail("berserkers_fury not in registry"); return; }

        Mob victim = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));
        ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        chestplate.enchant(ench, 1);
        victim.setItemSlot(EquipmentSlot.CHEST, chestplate);

        Mob attacker = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 2));
        attacker.doHurtTarget(victim);

        helper.runAfterDelay(3, () -> {
            boolean hasResistance = victim.hasEffect(MobEffects.DAMAGE_RESISTANCE);
            boolean hasStrength = victim.hasEffect(MobEffects.DAMAGE_BOOST);
            boolean hasSpeed = victim.hasEffect(MobEffects.MOVEMENT_SPEED);

            if (hasResistance && hasStrength && hasSpeed) {
                helper.succeed();
            } else {
                helper.fail("Berserker's Fury should grant Resistance, Strength, Speed. "
                        + "R=" + hasResistance + " S=" + hasStrength + " Sp=" + hasSpeed);
            }
        });
    }

    // --- Life-Mending: healing should repair damaged enchanted item ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void lifeMendingRepairsOnHeal(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "life_mending");
        if (ench == null) { helper.fail("life_mending not in registry"); return; }

        Mob mob = helper.spawnWithNoFreeWill(EntityType.ZOMBIE, new BlockPos(1, 1, 1));

        ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
        chestplate.setDamageValue(100);
        chestplate.enchant(ench, 3);
        mob.setItemSlot(EquipmentSlot.CHEST, chestplate);

        int damageBefore = chestplate.getDamageValue();
        mob.heal(10.0f);

        helper.runAfterDelay(1, () -> {
            int damageAfter = mob.getItemBySlot(EquipmentSlot.CHEST).getDamageValue();
            if (damageAfter < damageBefore) {
                helper.succeed();
            } else {
                helper.fail("Life-Mending should repair item on heal. "
                        + "Before: " + damageBefore + ", after: " + damageAfter);
            }
        });
    }

    // --- Life-Mending exclusive with Mending: verify exclusive set ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void lifeMendingExclusiveWithMending(GameTestHelper helper) {
        Holder<Enchantment> lifeMending = lookup(helper, "life_mending");
        if (lifeMending == null) { helper.fail("life_mending not in registry"); return; }

        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> mending = reg.getHolder(
                net.minecraft.resources.ResourceLocation.withDefaultNamespace("mending")).orElse(null);
        if (mending == null) { helper.fail("minecraft:mending not in registry"); return; }

        if (Enchantment.areCompatible(lifeMending, mending)) {
            helper.fail("Life-Mending and Mending should be exclusive (not compatible)");
            return;
        }
        helper.succeed();
    }

    // --- Stable Footing: verify enchantment exists with correct slot ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void stableFootingDefinition(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "stable_footing");
        if (ench == null) { helper.fail("stable_footing not in registry"); return; }

        Enchantment e = ench.value();
        if (e.definition().maxLevel() != 1) {
            helper.fail("Stable Footing should have maxLevel 1, got " + e.definition().maxLevel());
            return;
        }
        helper.succeed();
    }

    // --- Temptation: verify enchantment definition ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void temptationDefinition(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "temptation");
        if (ench == null) { helper.fail("temptation not in registry"); return; }

        Enchantment e = ench.value();
        if (e.definition().maxLevel() != 1) {
            helper.fail("Temptation should have maxLevel 1, got " + e.definition().maxLevel());
            return;
        }
        helper.succeed();
    }

    // --- Growth Serum + Chromatic: definition tests ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void growthSerumDefinition(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "growth_serum");
        if (ench == null) { helper.fail("growth_serum not in registry"); return; }
        helper.succeed();
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void chromaticDefinition(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "chromatic");
        if (ench == null) { helper.fail("chromatic not in registry"); return; }
        helper.succeed();
    }

    // --- Scavenger: definition test ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void scavengerDefinition(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "scavenger");
        if (ench == null) { helper.fail("scavenger not in registry"); return; }

        Enchantment e = ench.value();
        if (e.definition().maxLevel() != 3) {
            helper.fail("Scavenger should have maxLevel 3, got " + e.definition().maxLevel());
            return;
        }
        helper.succeed();
    }

    // --- Reflective Defenses: definition test ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void reflectiveDefensesDefinition(GameTestHelper helper) {
        Holder<Enchantment> ench = lookup(helper, "reflective_defenses");
        if (ench == null) { helper.fail("reflective_defenses not in registry"); return; }

        Enchantment e = ench.value();
        if (e.definition().maxLevel() != 5) {
            helper.fail("Reflective Defenses should have maxLevel 5, got " + e.definition().maxLevel());
            return;
        }
        helper.succeed();
    }

    // --- Corrupted damage type: verify registration ---

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void corruptedDamageTypeExists(GameTestHelper helper) {
        var reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE);
        var holder = reg.getHolder(FizzleEnchanting.id("corrupted"));
        if (holder.isEmpty()) {
            helper.fail("fizzle_enchanting:corrupted damage type not in registry");
            return;
        }
        helper.succeed();
    }
}
