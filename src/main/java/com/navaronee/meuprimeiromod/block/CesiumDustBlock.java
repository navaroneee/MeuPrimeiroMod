package com.navaronee.meuprimeiromod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Pó de césio espalhado no chão — fino, sem colisão, emite luz radioativa.
 * Player atravessa sem parar (sem bounding box de colisão).
 */
public class CesiumDustBlock extends Block {

    // Shape visual: camada fina no chão (1/16 de altura)
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    public CesiumDustBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
