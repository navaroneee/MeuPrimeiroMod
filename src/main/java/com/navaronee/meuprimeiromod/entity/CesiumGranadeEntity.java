package com.navaronee.meuprimeiromod.entity;

import com.navaronee.meuprimeiromod.event.RadiationZoneManager;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class CesiumGranadeEntity extends ThrowableItemProjectile {

    public static final float EXPLOSION_POWER = 3.0F;
    public static final double RADIATION_RADIUS = 8.0;
    public static final int RADIATION_DURATION_TICKS = 600; // 30 segundos

    public CesiumGranadeEntity(EntityType<? extends CesiumGranadeEntity> type, Level level) {
        super(type, level);
    }

    public CesiumGranadeEntity(Level level, LivingEntity shooter) {
        super(ModEntities.CESIUM_GRANADE.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.CESIUM_GRANADE.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide()) return;

        // Explosão
        level().explode(this, getX(), getY(), getZ(), EXPLOSION_POWER, Level.ExplosionInteraction.TNT);

        // Zona de radiação temporária (sem spawnar entity — só adiciona ao manager)
        long expireTick = level().getGameTime() + RADIATION_DURATION_TICKS;
        RadiationZoneManager.addZone(blockPosition(), RADIATION_RADIUS, expireTick);

        // Partículas visuais (server envia pros clientes)
        if (level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.LARGE_SMOKE,
                    getX(), getY() + 0.5, getZ(),
                    40, 1.5, 1.5, 1.5, 0.05);
            server.sendParticles(ParticleTypes.ENCHANTED_HIT,
                    getX(), getY() + 0.5, getZ(),
                    60, 2.0, 2.0, 2.0, 0.1);
        }

        discard();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
