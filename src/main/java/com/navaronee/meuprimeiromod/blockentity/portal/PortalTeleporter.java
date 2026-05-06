package com.navaronee.meuprimeiromod.blockentity.portal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

/**
 * Teleporter custom — coloca o player num spawn safe na dimensão de destino.
 * Sem busca por portal vanilla — só teletransporte direto.
 */
public class PortalTeleporter implements ITeleporter {

    private final ServerLevel destination;

    public PortalTeleporter(ServerLevel destination) {
        this.destination = destination;
    }

    @Override
    public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld,
                              float yaw, Function<Boolean, Entity> repositionEntity) {
        Entity placed = repositionEntity.apply(false);
        // Spawn no Y=80 do mesmo X,Z (ou ajustar pro topo do terreno)
        int targetY = destWorld.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
                (int) entity.getX(), (int) entity.getZ());
        if (targetY < destWorld.getMinBuildHeight() + 1) targetY = 80;
        placed.teleportTo(entity.getX(), targetY + 1, entity.getZ());
        return placed;
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld,
                                    Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return new PortalInfo(
                new Vec3(entity.getX(), entity.getY(), entity.getZ()),
                Vec3.ZERO,
                entity.getYRot(),
                entity.getXRot());
    }

    @Override
    public boolean playTeleportSound(net.minecraft.server.level.ServerPlayer player,
                                     ServerLevel sourceWorld, ServerLevel destWorld) {
        return false; // som é tocado pelo BlockEntity.activate
    }
}
