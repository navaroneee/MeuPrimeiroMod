package com.navaronee.meuprimeiromod.block;

import com.navaronee.meuprimeiromod.blockentity.PortalCreatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Frame estrutural do multiblock Portal.
 * Quebrar = procura controller formed num 3×3×3 ao redor e dispara unform.
 */
public class EngineeringFrameBlock extends Block {
    public EngineeringFrameBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            // 3×3×3 vizinhança: o frame fica adjacente ao controller na pattern
            for (BlockPos p : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                BlockEntity be = level.getBlockEntity(p);
                if (be instanceof PortalCreatorBlockEntity master
                        && master.getBlockState().getValue(PortalCreatorBlock.FORMED)) {
                    master.unform();
                    break;
                }
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }
}
