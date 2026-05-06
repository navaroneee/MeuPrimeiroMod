package com.navaronee.meuprimeiromod.block;

import com.navaronee.meuprimeiromod.blockentity.MultiblockFillerBlockEntity;
import com.navaronee.meuprimeiromod.blockentity.PortalCreatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Bloco invisível que substitui frames quando o multiblock forma.
 * Mantém colisão (full cube) pra player não atravessar a estrutura.
 * Quebrar = avisa o master pra unform e restaurar skeleton.
 */
public class MultiblockFillerBlock extends Block implements EntityBlock {

    public MultiblockFillerBlock(Properties props) { super(props); }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.block();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MultiblockFillerBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MultiblockFillerBlockEntity filler && filler.getMasterPos() != null) {
                BlockEntity master = level.getBlockEntity(filler.getMasterPos());
                // Pula a chamada se o master já está unforming (caso onde unform() está
                // restaurando os frames e setBlock() trigga onRemove deste filler).
                if (master instanceof PortalCreatorBlockEntity m && !m.isUnforming()) m.unform();
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }
}
