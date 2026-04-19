package com.fizzlesmp.fizzle_difficulty.event;

import com.fizzlesmp.fizzle_difficulty.FizzleDifficulty;
import com.fizzlesmp.fizzle_difficulty.config.FizzleDifficultyConfig;
import com.fizzlesmp.fizzle_difficulty.data.PlayerDifficultyState;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Applies death relief on a qualifying player death: subtracts
 * {@link FizzleDifficultyConfig.DeathRelief#amount} levels from the dying
 * player's difficulty level, floored at
 * {@link FizzleDifficultyConfig.DeathRelief#minimumLevel}. A cooldown keyed on
 * {@code cooldownTicks} suppresses rapid-suicide exploits. All death causes
 * count — matching HMIOT's behaviour; the cooldown is the sole gate.
 */
public final class DeathReliefHandler {

    private DeathReliefHandler() {}

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(DeathReliefHandler::onAfterDeath);
    }

    static void onAfterDeath(LivingEntity entity, DamageSource damageSource) {
        if (!(entity instanceof ServerPlayer player)) return;

        FizzleDifficultyConfig cfg = FizzleDifficulty.getConfig();
        if (cfg == null || !cfg.deathRelief.enabled) return;

        MinecraftServer server = player.getServer();
        if (server == null) return;

        try {
            PlayerDifficultyState state = PlayerDifficultyState.getOrCreate(server);
            int before = state.getLevel(player.getUUID());
            boolean applied = state.reduceLevel(
                    player.getUUID(),
                    cfg.deathRelief.amount,
                    cfg.deathRelief.cooldownTicks,
                    cfg.deathRelief.minimumLevel,
                    server.getTickCount()
            );
            if (!applied) {
                FizzleDifficulty.LOGGER.debug(
                        "Death relief skipped for {} — within cooldown ({} ticks)",
                        player.getGameProfile().getName(),
                        cfg.deathRelief.cooldownTicks
                );
                return;
            }
            int after = state.getLevel(player.getUUID());
            if (before != after) {
                FizzleDifficulty.LOGGER.debug(
                        "Death relief: {} reduced from level {} to {}",
                        player.getGameProfile().getName(),
                        before,
                        after
                );
            }
        } catch (Exception e) {
            FizzleDifficulty.LOGGER.error("Failed to apply death relief for {}", player, e);
        }
    }
}
