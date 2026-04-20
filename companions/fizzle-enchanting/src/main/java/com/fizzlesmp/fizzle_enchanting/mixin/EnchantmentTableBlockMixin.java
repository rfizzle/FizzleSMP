package com.fizzlesmp.fizzle_enchanting.mixin;

import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantingTableBlock.class)
abstract class EnchantmentTableBlockMixin {

    @Inject(method = "getMenuProvider", at = @At("HEAD"), cancellable = true)
    private void fizzleEnchanting$menuProvider(
            BlockState state, Level level, BlockPos pos,
            CallbackInfoReturnable<MenuProvider> cir) {
        cir.setReturnValue(new SimpleMenuProvider(
                (id, inv, player) -> new FizzleEnchantmentMenu(
                        id, inv, ContainerLevelAccess.create(level, pos)),
                Component.translatable("container.enchant")));
    }
}
