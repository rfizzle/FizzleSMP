package com.fizzlesmp.fizzle_enchanting.mixin;

import com.fizzlesmp.fizzle_enchanting.FizzleEnchanting;
import com.fizzlesmp.fizzle_enchanting.config.FizzleEnchantingConfig;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "getEnchantmentValue", at = @At("RETURN"), cancellable = true)
    private void fizzleEnchanting$overrideEnchantmentValue(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValueI() > 0) return;
        FizzleEnchantingConfig config = FizzleEnchanting.getConfig();
        if (config != null && config.enchantingTable.globalMinEnchantability > 0) {
            cir.setReturnValue(config.enchantingTable.globalMinEnchantability);
        }
    }
}
