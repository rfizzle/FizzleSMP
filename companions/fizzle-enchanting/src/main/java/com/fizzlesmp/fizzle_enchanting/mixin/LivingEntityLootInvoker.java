package com.fizzlesmp.fizzle_enchanting.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LivingEntity.class)
public interface LivingEntityLootInvoker {
    @Invoker("dropFromLootTable")
    void fizzle$invokeDropFromLootTable(DamageSource source, boolean hitByPlayer);
}
