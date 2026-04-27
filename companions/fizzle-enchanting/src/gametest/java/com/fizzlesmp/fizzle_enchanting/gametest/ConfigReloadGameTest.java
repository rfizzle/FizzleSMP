// Tier: 3 (Fabric Gametest) — TEST-1.4-T3
package com.fizzlesmp.fizzle_enchanting.gametest;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigReloadGameTest implements FabricGameTest {

    private static final Path CONFIG_FILE =
            FabricLoader.getInstance().getConfigDir().resolve("fizzle_enchanting.json");

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void reloadReReadsConfigFromDisk(GameTestHelper helper) {
        byte[] original;
        try {
            original = Files.readAllBytes(CONFIG_FILE);
        } catch (IOException e) {
            helper.fail("Could not read original config: " + e.getMessage());
            return;
        }
        try {
            String modified = "{\n"
                    + "  \"configVersion\": 1,\n"
                    + "  \"enchantingTable\": { \"maxEterna\": 42 }\n"
                    + "}";
            Files.writeString(CONFIG_FILE, modified);
            FizzleEnchanting.reloadConfig();

            FizzleEnchantingConfig config = FizzleEnchanting.getConfig();
            if (config == null) {
                helper.fail("Config should not be null after reload");
                return;
            }
            if (config.enchantingTable.maxEterna != 42) {
                helper.fail("Expected maxEterna=42 after reload, got "
                        + config.enchantingTable.maxEterna);
                return;
            }
            helper.succeed();
        } catch (IOException e) {
            helper.fail("IO error during test: " + e.getMessage());
        } finally {
            restoreConfig(original);
        }
    }

    @GameTest(template = "fizzle_enchanting:empty_3x3")
    public void reloadWithMalformedJsonFallsToDefaults(GameTestHelper helper) {
        byte[] original;
        try {
            original = Files.readAllBytes(CONFIG_FILE);
        } catch (IOException e) {
            helper.fail("Could not read original config: " + e.getMessage());
            return;
        }
        try {
            Files.writeString(CONFIG_FILE, "{invalid json content");
            FizzleEnchanting.reloadConfig();

            FizzleEnchantingConfig config = FizzleEnchanting.getConfig();
            if (config == null) {
                helper.fail("Config should not be null after malformed reload");
                return;
            }
            if (config.enchantingTable.maxEterna != 50) {
                helper.fail("Expected default maxEterna=50 after malformed reload, got "
                        + config.enchantingTable.maxEterna);
                return;
            }
            helper.succeed();
        } catch (IOException e) {
            helper.fail("IO error during test: " + e.getMessage());
        } finally {
            restoreConfig(original);
        }
    }

    private static void restoreConfig(byte[] original) {
        try {
            Files.write(CONFIG_FILE, original);
            FizzleEnchanting.reloadConfig();
        } catch (IOException ignored) {
        }
    }
}
