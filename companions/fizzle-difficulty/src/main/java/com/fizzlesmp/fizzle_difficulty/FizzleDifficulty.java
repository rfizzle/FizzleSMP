package com.fizzlesmp.fizzle_difficulty;

import com.fizzlesmp.fizzle_difficulty.command.FizzleDifficultyCommand;
import com.fizzlesmp.fizzle_difficulty.config.FizzleDifficultyConfig;
import com.fizzlesmp.fizzle_difficulty.data.PlayerDifficultyState;
import com.fizzlesmp.fizzle_difficulty.event.DeathReliefHandler;
import com.fizzlesmp.fizzle_difficulty.event.MobScalingHandler;
import com.fizzlesmp.fizzle_difficulty.event.ShardDropHandler;
import com.fizzlesmp.fizzle_difficulty.event.XpLootHandler;
import com.fizzlesmp.fizzle_difficulty.item.FizzleDifficultyItems;
import com.fizzlesmp.fizzle_difficulty.scaling.TierManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FizzleDifficulty implements ModInitializer {
    public static final String MOD_ID = "fizzle_difficulty";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final int TICK_INTERVAL = 20;

    private static volatile FizzleDifficultyConfig config;

    @Override
    public void onInitialize() {
        LOGGER.info("Fizzle Difficulty initializing");
        config = FizzleDifficultyConfig.load();
        FizzleDifficultyItems.register();
        registerTickHandler();
        MobScalingHandler.register();
        DeathReliefHandler.register();
        ShardDropHandler.register();
        XpLootHandler.register();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                FizzleDifficultyCommand.register(dispatcher));
    }

    public static FizzleDifficultyConfig getConfig() {
        return config;
    }

    public static void reloadConfig() {
        config = FizzleDifficultyConfig.load();
    }

    private static void registerTickHandler() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            FizzleDifficultyConfig cfg = config;
            if (cfg == null || !cfg.timeScaling.enabled) {
                return;
            }
            if (server.getTickCount() % TICK_INTERVAL != 0) {
                return;
            }
            if (server.getPlayerList().getPlayerCount() == 0) {
                return;
            }
            PlayerDifficultyState state = PlayerDifficultyState.getOrCreate(server);
            int levelUpTicks = cfg.general.levelUpTicks;
            int maxLevel = cfg.general.maxLevel;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                int oldLevel = state.getLevel(player.getUUID());
                int oldTier = TierManager.getTier(oldLevel, cfg.tiers);
                int levelsGained = state.incrementTick(player.getUUID(), TICK_INTERVAL, levelUpTicks, maxLevel);
                if (levelsGained > 0 && cfg.general.notifyLevelUp) {
                    int newLevel = state.getLevel(player.getUUID());
                    int newTier = TierManager.getTier(newLevel, cfg.tiers);
                    sendLevelUpMessage(player, newLevel, oldTier, newTier, maxLevel, cfg.general.notifyLevelUpShowTier);
                }
            }
        });
    }

    private static void sendLevelUpMessage(ServerPlayer player, int newLevel, int oldTier, int newTier, int maxLevel, boolean showTier) {
        Component message;
        if (newLevel >= maxLevel) {
            // One-time "hit the cap" message; suppress the normal level_up line
            // so players don't receive two pings on the same tick.
            message = Component.translatable("message.fizzle_difficulty.level_max")
                    .withStyle(ChatFormatting.GREEN);
        } else if (showTier && newTier != oldTier) {
            message = Component.translatable("message.fizzle_difficulty.level_up_tier", newLevel, newTier)
                    .withStyle(ChatFormatting.GREEN);
        } else {
            message = Component.translatable("message.fizzle_difficulty.level_up", newLevel)
                    .withStyle(ChatFormatting.GREEN);
        }
        player.sendSystemMessage(message);
    }
}
