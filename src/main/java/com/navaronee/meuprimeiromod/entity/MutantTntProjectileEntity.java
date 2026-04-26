package com.navaronee.meuprimeiromod.entity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

/**
 * Projétil TNT lançado pelo Mutant — rebatível igual fireball do Ghast.
 *
 * Deflection vem de graça: AbstractHurtingProjectile.hurt() inverte
 * deltaMovement baseado no lookAngle do atacante e troca o owner. Ao explodir
 * de volta no Mutant, source.getEntity() == Player, habilitando o trigger
 * do hitStrong em MutantEntity.hurt().
 */
public class MutantTntProjectileEntity extends AbstractHurtingProjectile {

    public static final float EXPLOSION_POWER = 3.5F;

    public MutantTntProjectileEntity(EntityType<? extends MutantTntProjectileEntity> type, Level level) {
        super(type, level);
    }

    public MutantTntProjectileEntity(Level level, LivingEntity shooter,
                                     double xPower, double yPower, double zPower) {
        super(ModEntities.MUTANT_TNT_PROJECTILE.get(), shooter, xPower, yPower, zPower, level);
    }

    @Override
    protected boolean shouldBurn() {
        return false; // não queima ao voar (diferente de fireball)
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide()) return;

        // Quem detonou? Owner pode ter mudado (deflected = Player).
        // Passar owner como source faz a explosão atribuir dano corretamente.
        level().explode(this.getOwner(),
                getX(), getY(), getZ(),
                EXPLOSION_POWER, Level.ExplosionInteraction.MOB);

        discard();
    }

    @Override
    public boolean isOnFire() {
        return false; // não renderiza chamas
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
