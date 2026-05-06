package com.navaronee.meuprimeiromod.block;

import com.navaronee.meuprimeiromod.blockentity.DimensionalPortalBlockEntity;
import com.navaronee.meuprimeiromod.blockentity.PortalCreatorBlockEntity;
import com.navaronee.meuprimeiromod.blockentity.portal.PortalTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
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
 * Bloco do portal aberto. Player atravessando = teleporte.
 * Sem colisão (player anda dentro). Render simples (cube_all com translucent).
 * Particles de portal pra dar vida visual.
 */
public class DimensionalPortalBlock extends Block implements EntityBlock {

    public DimensionalPortalBlock(Properties props) { super(props); }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        // Invisivel; o BlockEntityRenderer custom desenha o efeito end portal vertical
        return RenderShape.INVISIBLE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DimensionalPortalBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.block();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)) return;
        if (player.isOnPortalCooldown()) return;
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof DimensionalPortalBlockEntity portal) || portal.getMasterPos() == null) return;
        BlockEntity master = level.getBlockEntity(portal.getMasterPos());
        if (!(master instanceof PortalCreatorBlockEntity controller)) return;
        ResourceKey<Level> destKey = controller.getActiveDestination();
        if (destKey == null) return;
        MinecraftServer server = level.getServer();
        if (server == null) return;
        ServerLevel target = server.getLevel(destKey);
        if (target == null) return;
        player.changeDimension(target, new PortalTeleporter(target));
        player.setPortalCooldown();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        for (int i = 0; i < 3; i++) {
            level.addParticle(ParticleTypes.PORTAL,
                    pos.getX() + rand.nextDouble(),
                    pos.getY() + rand.nextDouble(),
                    pos.getZ() + rand.nextDouble(),
                    (rand.nextDouble() - 0.5) * 0.5,
                    (rand.nextDouble() - 0.5) * 0.5,
                    (rand.nextDouble() - 0.5) * 0.5);
        }
        if (rand.nextInt(8) == 0) {
            level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    net.minecraft.sounds.SoundEvents.PORTAL_AMBIENT,
                    net.minecraft.sounds.SoundSource.BLOCKS,
                    0.3F, rand.nextFloat() * 0.4F + 0.8F, false);
        }
    }
}
