package com.navaronee.meuprimeiromod.block;

import com.navaronee.meuprimeiromod.blockentity.MultiblockPortBlockEntity;
import com.navaronee.meuprimeiromod.blockentity.PortalCreatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Porta de entrada de energia (FE) do multiblock Portal.
 * BlockEntity bridge: getCapability(ENERGY) delega pro master quando linkado.
 * Quando LINKED=true (multiblock formado), bloco vira invisível pro modelo grande aparecer.
 * Quebrar = avisa o master pra unform.
 */
public class PortalEnergyPortBlock extends Block implements EntityBlock {
    public static final BooleanProperty LINKED = BooleanProperty.create("linked");

    public PortalEnergyPortBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(LINKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LINKED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(LINKED) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.block();
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return MultiblockPortBlockEntity.energy(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MultiblockPortBlockEntity port && port.getMasterPos() != null) {
                BlockEntity master = level.getBlockEntity(port.getMasterPos());
                if (master instanceof PortalCreatorBlockEntity m && !m.isUnforming()) m.unform();
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }
}
