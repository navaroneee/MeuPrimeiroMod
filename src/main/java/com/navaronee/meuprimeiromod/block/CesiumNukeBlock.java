package com.navaronee.meuprimeiromod.block;

import com.navaronee.meuprimeiromod.entity.CesiumNukePrimedEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class CesiumNukeBlock extends TntBlock {

    public CesiumNukeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
        if (level.isClientSide()) return;

        CesiumNukePrimedEntity primed = new CesiumNukePrimedEntity(
                level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, igniter);
        level.addFreshEntity(primed);
        level.playSound(null, primed.getX(), primed.getY(), primed.getZ(),
                SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (level.isClientSide()) {
            super.onBlockExploded(state, level, pos, explosion);
            return;
        }

        CesiumNukePrimedEntity primed = new CesiumNukePrimedEntity(
                level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                explosion.getIndirectSourceEntity());
        int fuse = level.random.nextInt(primed.getFuse() / 4) + primed.getFuse() / 8;
        primed.setFuse(fuse);
        level.addFreshEntity(primed);
        super.onBlockExploded(state, level, pos, explosion);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(Items.FLINT_AND_STEEL) && !stack.is(Items.FIRE_CHARGE)) {
            return super.use(state, level, pos, player, hand, hit);
        }

        onCaughtFire(state, level, pos, hit.getDirection(), player);
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 11);
        if (!player.isCreative()) {
            if (stack.is(Items.FLINT_AND_STEEL)) {
                stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(hand));
            } else {
                stack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
