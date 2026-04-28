// Tier: 3 (Fabric Gametest) — TEST-6.1-T3, TEST-6.3-T3a
package com.rfizzle.meridian.gametest;

import com.rfizzle.meridian.Meridian;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentRosterGameTest implements FabricGameTest {

    private static final List<String> PORTED_IDS = List.of(
            "armor/fury", "armor/lifeplus", "armor/venom_protection",
            "helmet/bright_vision", "helmet/voidless",
            "chestplate/builder_arm",
            "leggings/dwarfed", "leggings/fast_swim", "leggings/leaping", "leggings/oversize",
            "boots/agility", "boots/lava_walker", "boots/step_assist",
            "elytra/armored", "elytra/kinetic_protection",
            "sword/attack_speed", "sword/critical", "sword/death_touch",
            "sword/dimensional_hit", "sword/fear", "sword/last_hope",
            "sword/life_steal", "sword/poison_aspect", "sword/pull",
            "sword/reach", "sword/tears_of_asflors", "sword/xp_boost",
            "bow/accuracy_shot", "bow/breezing_arrow", "bow/echo_shot",
            "bow/eternal_frost", "bow/explosive_arrow", "bow/rebound", "bow/storm_arrow",
            "trident/gungnir_breath",
            "mace/striker", "mace/teluric_wave", "mace/wind_propulsion",
            "pickaxe/vein_miner",
            "tools/miningplus",
            "hoe/harvest", "hoe/scyther", "hoe/seiors_oblivion",
            "mounted/cavalier_egis", "mounted/ethereal_leap", "mounted/steel_fang", "mounted/velocity",
            "durability/curse_of_breaking", "durability/curse_of_enchant",
            "midas_touch");

    private static final List<String> AUTHORED_IDS = List.of(
            "icy_thorns", "shield_bash");

    private static final List<String> CUT_IDS = List.of(
            "axe/timber", "pickaxe/bedrock_breaker", "pickaxe/spawner_touch",
            "tools/auto_smelt", "helmet/auto_feed", "chestplate/magnet", "sword/runic_despair");

    @GameTest(template = "meridian:empty_3x3")
    public void allPortedEnchantmentsResolveInRegistry(GameTestHelper helper) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        List<String> missing = new ArrayList<>();
        for (String id : PORTED_IDS) {
            ResourceLocation loc = Meridian.id(id);
            if (!reg.containsKey(loc)) {
                missing.add(id);
            }
        }
        if (!missing.isEmpty()) {
            helper.fail("Ported enchantments missing from registry: " + missing);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "meridian:empty_3x3")
    public void allAuthoredEnchantmentsResolveInRegistry(GameTestHelper helper) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        List<String> missing = new ArrayList<>();
        for (String id : AUTHORED_IDS) {
            ResourceLocation loc = Meridian.id(id);
            if (!reg.containsKey(loc)) {
                missing.add(id);
            }
        }
        if (!missing.isEmpty()) {
            helper.fail("Authored enchantments missing from registry: " + missing);
            return;
        }
        helper.succeed();
    }

    @GameTest(template = "meridian:empty_3x3")
    public void cutEnchantmentsAbsentFromRegistry(GameTestHelper helper) {
        Registry<Enchantment> reg = helper.getLevel().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT);
        List<String> present = new ArrayList<>();
        for (String id : CUT_IDS) {
            ResourceLocation loc = Meridian.id(id);
            if (reg.containsKey(loc)) {
                present.add(id);
            }
        }
        if (!present.isEmpty()) {
            helper.fail("Cut enchantments should NOT be in registry: " + present);
            return;
        }
        helper.succeed();
    }

}
