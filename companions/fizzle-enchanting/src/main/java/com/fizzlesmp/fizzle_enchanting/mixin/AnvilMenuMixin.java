package com.fizzlesmp.fizzle_enchanting.mixin;

import com.fizzlesmp.fizzle_enchanting.anvil.AnvilDispatcher;
import com.fizzlesmp.fizzle_enchanting.anvil.AnvilResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

/**
 * T-4.1.1 — TAIL hook on {@code AnvilMenu#createResult} that lets
 * {@link AnvilDispatcher} swap in custom outputs for combinations vanilla doesn't handle
 * (prismatic web curse-strip, iron-block anvil repair, the tome families).
 *
 * <p>Running at TAIL lets vanilla compute its output first, then we overwrite it only when a
 * dispatcher handler claims the pairing. Every other left/right combination keeps vanilla's
 * result untouched — the mixin is behaviorally transparent when no handler fires.
 *
 * <p>T-5.2.3 extends the hook with an {@code onTake} tail that reinstates the handler's
 * {@link AnvilResult#leftReplacement()} into slot 0 after vanilla has cleared it. Most
 * handlers leave {@code leftReplacement} empty — the source item is consumed — but the
 * Extraction Tome returns a damaged, unenchanted copy of the source so the player recovers
 * the item. We stash the most recent claimed result on the mixin instance so the take-path
 * can read it without re-running the dispatcher (which might yield a different result if the
 * slots have already been mutated by vanilla's take logic).
 *
 * <p>The class extends {@link ItemCombinerMenu} to give the mixin lexical access to the
 * parent's {@code inputSlots} / {@code resultSlots} / {@code player} protected fields; the stub
 * constructor only satisfies the Java compiler's super-call requirement — Mixin does not merge
 * mixin constructors into the target class.
 */
@Mixin(AnvilMenu.class)
abstract class AnvilMenuMixin extends ItemCombinerMenu {

    /**
     * The most recent claim returned by the dispatcher, or {@code null} if no handler fired
     * on the current slot state. Stashed at {@code createResult} TAIL and consumed at
     * {@code onTake} TAIL. Cleared whenever the dispatcher returns empty so a stale
     * {@code leftReplacement} from a prior slot state cannot leak into a later take.
     */
    @Unique
    @Nullable
    private AnvilResult fizzleEnchanting$pendingResult;

    private AnvilMenuMixin(
            @Nullable MenuType<?> type, int containerId, Inventory inv, ContainerLevelAccess access) {
        super(type, containerId, inv, access);
    }

    @Inject(method = "createResult", at = @At("TAIL"))
    private void fizzleEnchanting$dispatch(CallbackInfo ci) {
        AnvilMenu self = (AnvilMenu) (Object) this;
        AnvilMenuAccessor accessor = (AnvilMenuAccessor) (Object) this;
        ItemStack left = this.inputSlots.getItem(0);
        ItemStack right = this.inputSlots.getItem(1);
        Player player = this.player;
        int currentCost = accessor.fizzleEnchanting$getCost().get();

        Optional<AnvilResult> result =
                AnvilDispatcher.handle(self, left, right, player, currentCost);
        if (result.isEmpty()) {
            this.fizzleEnchanting$pendingResult = null;
            return;
        }
        AnvilResult r = result.get();
        this.fizzleEnchanting$pendingResult = r;

        this.resultSlots.setItem(0, r.output());
        accessor.fizzleEnchanting$getCost().set(r.xpCost());
        accessor.fizzleEnchanting$setRepairItemCountCost(r.rightConsumed());
        self.broadcastChanges();
    }

    @Inject(method = "onTake", at = @At("TAIL"))
    private void fizzleEnchanting$restoreLeft(Player player, ItemStack stack, CallbackInfo ci) {
        AnvilResult pending = this.fizzleEnchanting$pendingResult;
        this.fizzleEnchanting$pendingResult = null;
        if (pending == null) return;
        ItemStack replacement = pending.leftReplacement();
        if (replacement == null || replacement.isEmpty()) return;
        this.inputSlots.setItem(0, replacement.copy());
    }
}
