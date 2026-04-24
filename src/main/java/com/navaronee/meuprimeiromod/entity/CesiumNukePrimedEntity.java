package com.navaronee.meuprimeiromod.entity;

import com.navaronee.meuprimeiromod.event.RadiationZoneManager;
import com.navaronee.meuprimeiromod.sound.ModSounds;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class CesiumNukePrimedEntity extends PrimedTnt {

    public static final float EXPLOSION_POWER = 45.0F;     // cratera 1.5x maior
    public static final float SECONDARY_POWER = 27.0F;     // secundárias 1.5x
    public static final double RADIATION_RADIUS = 82.0;    // zona 1.5x
    public static final double BURN_RADIUS = 105.0;        // raio 1.5x
    public static final int RADIATION_DURATION_TICKS = 4800; // 4 minutos (mantido)
    public static final int BURN_DURATION_TICKS = 800;     // 40s fogo (mantido)

    /**
     * Fuse sincronizado com o áudio do siren:
     * - Áudio tem a explosão no segundo 7
     * - Fuse = 140 ticks = 7s; siren toca no momento do prime, explosão alinha com o BOOM do áudio
     */
    public static final int FUSE_TICKS = 140;

    public CesiumNukePrimedEntity(EntityType<? extends PrimedTnt> type, Level level) {
        super(type, level);
    }

    public CesiumNukePrimedEntity(Level level, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(ModEntities.CESIUM_NUKE_PRIMED.get(), level);
        this.setPos(x, y, z);
        double angle = level.random.nextDouble() * (Math.PI * 2);
        this.setDeltaMovement(-Math.sin(angle) * 0.02, 0.2, -Math.cos(angle) * 0.02);
        this.setFuse(FUSE_TICKS);
        this.xo = x;
        this.yo = y;
        this.zo = z;

        // Siren toca imediatamente; sincronizado com fuse (boom no segundo 7)
        if (!level.isClientSide()) {
            level.playSound(null, x, y, z, ModSounds.EXPLOSION_SIREN.get(),
                    SoundSource.BLOCKS, 6.0F, 1.0F);
        }
    }

    @Override
    protected void explode() {
        if (level().isClientSide()) return;

        // CRATERA GIGANTE: explosão central forte + secundárias pra forma irregular
        double cx = getX();
        double cy = getY(0.0625);
        double cz = getZ();

        // Central (destruição massiva)
        level().explode(this, cx, cy, cz, EXPLOSION_POWER, Level.ExplosionInteraction.TNT);

        // 6 secundárias em anel (crater wider e irregular)
        for (int i = 0; i < 6; i++) {
            double angle = (i / 6.0) * Math.PI * 2;
            double offsetR = 6 + level().random.nextDouble() * 4;
            double ox = Math.cos(angle) * offsetR;
            double oz = Math.sin(angle) * offsetR;
            level().explode(this, cx + ox, cy, cz + oz,
                    SECONDARY_POWER, Level.ExplosionInteraction.TNT);
        }

        // 2 verticais (pra cratera funda)
        level().explode(this, cx, cy - 4, cz, SECONDARY_POWER, Level.ExplosionInteraction.TNT);
        level().explode(this, cx, cy - 8, cz, SECONDARY_POWER * 0.7F, Level.ExplosionInteraction.TNT);

        // Zona de radiação grande e duradoura
        long expireTick = level().getGameTime() + RADIATION_DURATION_TICKS;
        RadiationZoneManager.addZone(blockPosition(), RADIATION_RADIUS, expireTick);

        // Coloca fogo azul (soul fire) em TODOS os bichos no raio
        AABB burnArea = new AABB(getX() - BURN_RADIUS, getY() - BURN_RADIUS, getZ() - BURN_RADIUS,
                getX() + BURN_RADIUS, getY() + BURN_RADIUS, getZ() + BURN_RADIUS);
        Vec3 center = position();
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, burnArea)) {
            double distSq = entity.distanceToSqr(center);
            if (distSq > BURN_RADIUS * BURN_RADIUS) continue;

            // Fogo longo (aparece como chamas)
            entity.setSecondsOnFire(BURN_DURATION_TICKS / 20);

            // Knockback radial (empurra pra longe do centro)
            double dist = Math.sqrt(distSq);
            if (dist > 0.1) {
                Vec3 dir = entity.position().subtract(center).normalize();
                double strength = (1.0 - dist / BURN_RADIUS) * 1.5;
                entity.setDeltaMovement(entity.getDeltaMovement().add(dir.x * strength, 0.4 * strength, dir.z * strength));
                entity.hurtMarked = true;
            }

            // Dano inicial leve (metade da vida no centro, 0 na borda)
            float initialDamage = (float) ((1.0 - dist / BURN_RADIUS) * (entity.getMaxHealth() * 0.4));
            entity.hurt(level().damageSources().onFire(), initialDamage);
        }

        // Cogumelo visual
        if (level() instanceof ServerLevel server) {
            AtomicCloudEntity cloud = new AtomicCloudEntity(ModEntities.ATOMIC_CLOUD.get(), level());
            cloud.setPos(getX(), getY(), getZ());
            server.addFreshEntity(cloud);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
