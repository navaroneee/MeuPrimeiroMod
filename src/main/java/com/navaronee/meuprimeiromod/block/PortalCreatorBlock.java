package com.navaronee.meuprimeiromod.block;

import com.navaronee.meuprimeiromod.blockentity.ModBlockEntities;
import com.navaronee.meuprimeiromod.blockentity.PortalCreatorBlockEntity;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.Direction;

/**
 * Controller block do Portal Creator multiblock.
 *
 * Dois estados visuais via property FORMED:
 *  - FORMED=false: bloco "desligado", aguardando estrutura completa
 *  - FORMED=true: estrutura validada, GUI abre, portal pode ser ativado
 *
 * Wrench → tryAssemble (no WrenchItem.useOn).
 * Right-click sem wrench → abre GUI se já formed; senão ignora.
 */
public class PortalCreatorBlock extends HorizontalDirectionalBlock implements EntityBlock {

    public static final BooleanProperty FORMED = BooleanProperty.create("formed");
    public static final BooleanProperty READY = BooleanProperty.create("ready");

    public PortalCreatorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FORMED, false)
                .setValue(READY, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FORMED, READY, FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PortalCreatorBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == ModBlockEntities.PORTAL_CREATOR.get()
                ? (lvl, pos, st, be) -> ((PortalCreatorBlockEntity) be).serverTick()
                : null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // Wrench é tratada via WrenchItem.useOn — não duplicar aqui
        ItemStack held = player.getItemInHand(hand);
        if (held.is(ModItems.WRENCH.get())) return InteractionResult.PASS;

        if (level.isClientSide()) return InteractionResult.SUCCESS;

        // Shift+rclick = toggle ghost preview (mostra cubos coloridos por status no mundo)
        if (player.isShiftKeyDown()) {
            if (level.isClientSide()) {
                com.navaronee.meuprimeiromod.client.render.MultiblockGhostRenderer.togglePreview(pos);
            }
            return InteractionResult.SUCCESS;
        }

        // Só abre GUI se estiver formed
        if (!state.getValue(FORMED)) {
            player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                    "§eUse a Wrench pra montar o multiblock primeiro."), true);
            return InteractionResult.CONSUME;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MenuProvider provider) {
            NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) player, provider, pos);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PortalCreatorBlockEntity portal) {
                portal.unform();
                portal.dropContents(level, pos);
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }
}
