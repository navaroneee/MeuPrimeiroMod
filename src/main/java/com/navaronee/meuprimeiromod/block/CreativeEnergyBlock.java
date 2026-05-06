package com.navaronee.meuprimeiromod.block;

import com.navaronee.meuprimeiromod.blockentity.CreativeEnergyBlockEntity;
import com.navaronee.meuprimeiromod.blockentity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Fonte criativa de RF — pra testar máquinas sem precisar montar geração. */
public class CreativeEnergyBlock extends Block implements EntityBlock {
    public CreativeEnergyBlock(Properties props) { super(props); }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CreativeEnergyBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.CREATIVE_ENERGY.get()
                ? (lvl, pos, st, be) -> ((CreativeEnergyBlockEntity) be).serverTick()
                : null;
    }
}
