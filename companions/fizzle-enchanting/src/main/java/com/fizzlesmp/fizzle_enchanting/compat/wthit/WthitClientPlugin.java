package com.fizzlesmp.fizzle_enchanting.compat.wthit;

import com.fizzlesmp.fizzle_enchanting.library.EnchantmentLibraryBlock;

import net.minecraft.world.level.block.EnchantingTableBlock;

import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IWailaClientPlugin;

public final class WthitClientPlugin implements IWailaClientPlugin {

    @Override
    public void register(IClientRegistrar registrar) {
        registrar.body(EnchantingTableWthitProvider.INSTANCE, EnchantingTableBlock.class);
        registrar.body(EnchantmentLibraryWthitProvider.INSTANCE, EnchantmentLibraryBlock.class);
    }
}
