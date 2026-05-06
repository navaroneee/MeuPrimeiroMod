package com.navaronee.meuprimeiromod.item;

import com.navaronee.meuprimeiromod.block.PortalCreatorBlock;
import com.navaronee.meuprimeiromod.blockentity.PortalCreatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Wrench — usada no Portal Creator (e talvez outras máquinas no futuro) pra
 * disparar a validação do multiblock. Sem ataque, sem mining, só uso direito.
 */
public class WrenchItem extends Item {

    public WrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        // Por enquanto, só PortalCreatorBlock reage à wrench
        if (state.getBlock() instanceof PortalCreatorBlock) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PortalCreatorBlockEntity portal) {
                // Shift+wrench = toggle ghost preview client-side
                if (player.isShiftKeyDown()) {
                    if (level.isClientSide()) {
                        boolean on = com.navaronee.meuprimeiromod.client.render.MultiblockGhostRenderer.togglePreview(pos);
                        player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                                on ? "§aPreview ON" : "§7Preview OFF"), true);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
                // Wrench normal = assemble
                if (!level.isClientSide()) {
                    portal.tryAssemble(player);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }

    /** Permite que o wrench segurado NÃO bypass o Block.use() ao sneakar — pra que
     *  shift+rclick rode a lógica do item (preview toggle) em vez de cair no use(). */
    @Override
    public boolean doesSneakBypassUse(net.minecraft.world.item.ItemStack stack,
                                      net.minecraft.world.level.LevelReader world,
                                      net.minecraft.core.BlockPos pos,
                                      net.minecraft.world.entity.player.Player player) {
        return false;
    }
}
