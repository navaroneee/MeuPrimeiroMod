package com.navaronee.meuprimeiromod.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

/**
 * Projétil disparado pela Slime Gun. Voa reto (AbstractHurtingProjectile), ao
 * impactar explode com raio 3 e ExplosionInteraction.NONE — entidades levam
 * dano da explosão mas nenhum bloco é destruído.
 */
public class SlimeShotEntity extends AbstractHurtingProjectile {

    public static final float EXPLOSION_RADIUS = 3.5F;
    public static final float MAX_DAMAGE = 12.0F;

    public SlimeShotEntity(EntityType<? extends SlimeShotEntity> type, Level level) {
        super(type, level);
    }

    public SlimeShotEntity(Level level, LivingEntity shooter,
                           double xPower, double yPower, double zPower) {
        super(ModEntities.SLIME_SHOT.get(), shooter, xPower, yPower, zPower, level);
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide()) return;

        // Visual + som de explosão (sem usar level.explode pra garantir zero block break)
        if (level() instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.EXPLOSION,
                    getX(), getY(), getZ(), 1, 0, 0, 0, 0);
            server.sendParticles(ParticleTypes.LARGE_SMOKE,
                    getX(), getY() + 0.5, getZ(),
                    20, 0.5, 0.5, 0.5, 0.05);
        }
        level().playSound(null, getX(), getY(), getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.2F, 1.4F);

        // Aplica dano manualmente em entidades dentro do raio.
        // Falloff linear: full damage no centro, zero na borda.
        Entity owner = this.getOwner();
        LivingEntity ownerLiving = owner instanceof LivingEntity le ? le : null;
        DamageSource src = damageSources().mobProjectile(this, ownerLiving);

        AABB aabb = new AABB(
                getX() - EXPLOSION_RADIUS, getY() - EXPLOSION_RADIUS, getZ() - EXPLOSION_RADIUS,
                getX() + EXPLOSION_RADIUS, getY() + EXPLOSION_RADIUS, getZ() + EXPLOSION_RADIUS);
        Vec3 boom = this.position();
        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (entity == owner) continue;

            // Distância da explosão até o ponto MAIS PRÓXIMO da BB do alvo.
            // Pra mobs grandes (Mutant 6.8 alto, slime size 4), distância até centro
            // já passa do raio em hits nos extremos. Usando AABB-clamp, hit dentro/
            // tocando = dist 0 = dano cheio.
            AABB targetBB = entity.getBoundingBox();
            double cx = Math.max(targetBB.minX, Math.min(boom.x, targetBB.maxX));
            double cy = Math.max(targetBB.minY, Math.min(boom.y, targetBB.maxY));
            double cz = Math.max(targetBB.minZ, Math.min(boom.z, targetBB.maxZ));
            double dx = boom.x - cx, dy = boom.y - cy, dz = boom.z - cz;
            double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
            if (dist > EXPLOSION_RADIUS) continue;

            float damage = MAX_DAMAGE * (1.0F - (float) (dist / EXPLOSION_RADIUS));
            entity.hurt(src, damage);

            // Knockback radial — empurra do epicentro pra fora
            Vec3 push = entity.getBoundingBox().getCenter().subtract(boom).normalize().scale(0.6);
            entity.push(push.x, push.y + 0.25, push.z);
        }

        discard();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
