package com.fizzlesmp.fizzle_enchanting.mixin;

import com.fizzlesmp.fizzle_enchanting.enchanting.FizzleEnchantmentMenu;
import com.fizzlesmp.fizzle_enchanting.enchanting.IEnchantingStatProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(method = "animateTick", at = @At("HEAD"), cancellable = true)
    private void fizzleEnchanting$animateTick(
            BlockState state, Level level, BlockPos pos, RandomSource random,
            CallbackInfo ci) {
        ci.cancel();
        for (BlockPos offset : EnchantingTableBlock.BOOKSHELF_OFFSETS) {
            BlockState shelfState = level.getBlockState(pos.offset(offset));
            if (shelfState.getBlock() instanceof IEnchantingStatProvider provider) {
                provider.spawnTableParticle(shelfState, level, random, pos, offset);
            } else if (EnchantingTableBlock.isValidBookShelf(level, pos, offset)) {
                if (random.nextInt(16) == 0) {
                    level.addParticle(ParticleTypes.ENCHANT,
                            pos.getX() + 0.5D, pos.getY() + 2.0D, pos.getZ() + 0.5D,
                            offset.getX() + random.nextFloat() - 0.5D,
                            offset.getY() - random.nextFloat() - 1.0F,
                            offset.getZ() + random.nextFloat() - 0.5D);
                }
            }
        }
    }
}
