package com.fizzlesmp.fizzle_enchanting.library;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Single block class shared by the Basic and Ender libraries. Tier differentiation lives entirely
 * in the {@link BlockEntitySupplier} passed at construction time — both library tiers use the
 * same model, same shape, same interaction surface; only the BE they create differs (and through
 * it, the {@code maxLevel}/{@code maxPoints} caps stored in NBT).
 *
 * <p>{@link BaseEntityBlock} gives us the {@code newBlockEntity} + ticker scaffolding plus the
 * abstract {@link #codec()} contract. The block's own state has no directional property in MVP
 * (deferred to polish — DESIGN "full-map resend" syncs state independent of block orientation),
 * so the codec only needs to round-trip {@link Properties} via {@link #simpleCodec}. The encoded
 * form binds to a null tile supplier: a datapack reconstruction path that goes through this codec
 * could not spawn a functional library, but we never register the block datapack-side — it exists
 * only in code — so that degenerate shape is unreachable.
 *
 * <p>{@link #getRenderShape(BlockState)} is overridden back to {@link RenderShape#MODEL} because
 * {@code BaseEntityBlock}'s default of {@link RenderShape#INVISIBLE} assumes a dedicated
 * {@code BlockEntityRenderer}; our library is a static block model, so disabling the mesh would
 * leave the player staring at empty space.
 */
public class EnchantmentLibraryBlock extends BaseEntityBlock {

    /**
     * Codec exists purely to satisfy {@link BaseEntityBlock}'s abstract contract — both library
     * instances are code-registered and never reconstructed through this path, so the
     * null-supplier fallback constructor it feeds is intentionally degenerate.
     */
    public static final MapCodec<EnchantmentLibraryBlock> CODEC =
            simpleCodec(props -> new EnchantmentLibraryBlock(props, (pos, state) -> null));

    private final BlockEntitySupplier<? extends EnchantmentLibraryBlockEntity> tileSupplier;

    public EnchantmentLibraryBlock(Properties properties,
                                   BlockEntitySupplier<? extends EnchantmentLibraryBlockEntity> tileSupplier) {
        super(properties);
        this.tileSupplier = tileSupplier;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return this.tileSupplier.create(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
