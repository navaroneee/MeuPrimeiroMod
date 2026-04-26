package com.navaronee.meuprimeiromod.item;

import com.navaronee.meuprimeiromod.block.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Cesium Dust: item craftável do refinador que ao clicar no chão coloca
 * um CesiumDustBlock (emite luz 10, sem colisão).
 */
public class CesiumDustItem extends Item {

    public CesiumDustItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // brilho radioativo no inventário
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Só aceita placar no TOPO de um bloco (chão), não em parede nem teto
        if (context.getClickedFace() != Direction.UP) return InteractionResult.FAIL;

        BlockPlaceContext place = new BlockPlaceContext(context);
        if (!place.canPlace()) return InteractionResult.FAIL;

        Level level = context.getLevel();
        Block block = ModBlocks.CESIUM_DUST_BLOCK.get();
        BlockState state = block.defaultBlockState();

        if (!level.setBlock(place.getClickedPos(), state, 11)) return InteractionResult.FAIL;

        // Som de colocação
        level.playSound(null, place.getClickedPos(),
                state.getSoundType().getPlaceSound(),
                SoundSource.BLOCKS, 1.0F, 1.0F);

        Player player = context.getPlayer();
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
