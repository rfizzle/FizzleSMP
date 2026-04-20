package com.fizzlesmp.fizzle_enchanting.enchanting;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IEnchantingStatProvider {

    default EnchantingStats getStats(Level level, BlockPos pos, BlockState state) {
        return EnchantingStatRegistry.lookup(level, state);
    }
}
