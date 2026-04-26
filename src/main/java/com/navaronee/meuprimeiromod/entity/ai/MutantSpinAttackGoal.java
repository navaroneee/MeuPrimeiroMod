package com.navaronee.meuprimeiromod.entity.ai;

import com.navaronee.meuprimeiromod.entity.MutantEntity;
import com.navaronee.meuprimeiromod.particle.ModParticles;
import com.navaronee.meuprimeiromod.sound.ModSounds;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Spin charge (atack2): Mutant rodopia 2s e carrega na direção do player.
 * Ao encostar (distância < 2.5), causa dano + knockback forte e encerra.
 * Ativado aleatoriamente em média distância (4-10 blocos) com cooldown longo.
 */
public class MutantSpinAttackGoal extends Goal {

    private static final int DURATION = 40;          // 2s = mesma duração da animação
    private static final double CHARGE_SPEED = 0.55;

    private final MutantEntity mutant;
    private int ticks;
    private boolean hitDelivered;

    public MutantSpinAttackGoal(MutantEntity mutant) {
        this.mutant = mutant;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mutant.spinAttackCooldown > 0) return false;
        if (mutant.hitStrongLock > 0) return false;
        LivingEntity target = mutant.getTarget();
        if (target == null || !target.isAlive()) return false;
        double d = mutant.distanceTo(target);
        // Permite spin mesmo em range de melee (a partir de 2.0) pra ficar mais ativo
        if (d < 2.0 || d > 12.0) return false;

        // Force ativação do spin se o TeleportGoal (fase 3+) pediu combo teleport→spin
        if (mutant.forceSpinAfterTeleport) {
            mutant.forceSpinAfterTeleport = false;
            return true;
        }

        // Chance por fase: 1=moderado, 4=muito alto
        return mutant.getRandom().nextInt(triggerChance(mutant.getPhase())) == 0;
    }

    private static int triggerChance(int phase) {
        return switch (phase) {
            case 1 -> 15;
            case 2 -> 10;
            case 3 -> 6;
            default -> 2; // fase 4: spin doido (50% por tick quando cd=0)
        };
    }

    private static int cooldownForPhase(int phase) {
        return switch (phase) {
            case 1 -> 100; // 5s
            case 2 -> 60;  // 3s
            case 3 -> 40;  // 2s
            default -> 10; // 0.5s — back-to-back, quase sem pausa
        };
    }

    @Override
    public boolean canContinueToUse() {
        // Continua até o fim da animação (40 ticks). hitDelivered não para o goal —
        // só impede o segundo hit. Assim o som não corta no meio quando acerta cedo.
        return ticks < DURATION && mutant.hitStrongLock <= 0;
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.hitDelivered = false;
        mutant.getNavigation().stop();
        mutant.level().broadcastEntityEvent(mutant, MutantEntity.EVENT_ATTACK_SPIN);
        mutant.level().playSound(null, mutant.blockPosition(),
                ModSounds.MUTANT_SPIN.get(), SoundSource.HOSTILE, 1.4F, 1.0F);
    }

    @Override
    public void tick() {
        ticks++;
        LivingEntity target = mutant.getTarget();
        if (target == null) return;

        // Fumaça radioativa em volta do mutant durante o spin (vortex visual)
        if (mutant.level() instanceof ServerLevel server) {
            for (int i = 0; i < 5; i++) {
                double angle = (mutant.tickCount * 0.4 + i * (Math.PI * 2 / 5)) % (Math.PI * 2);
                double r = 2.4 + mutant.getRandom().nextDouble() * 0.6;
                double px = mutant.getX() + Math.cos(angle) * r;
                double pz = mutant.getZ() + Math.sin(angle) * r;
                double py = mutant.getY() + mutant.getRandom().nextDouble() * (mutant.getBbHeight() - 0.5);
                server.sendParticles(ModParticles.CESIUM_LIGHT_SMOKE.get(),
                        px, py, pz, 1, 0.0, 0.05, 0.0, 0.02);
            }
        }

        // Carga em direção ao player (ignora navigation, força motion)
        Vec3 dir = target.position().subtract(mutant.position()).normalize();
        mutant.setDeltaMovement(dir.x * CHARGE_SPEED, mutant.getDeltaMovement().y, dir.z * CHARGE_SPEED);
        mutant.getLookControl().setLookAt(target, 90F, 90F);

        // Contato — aplica dano via applyMutantHit (bypassa o bloqueio de doHurtTarget
        // em fase 4, já que o spin é um ataque distinto do melee simples).
        // Aplicado UMA vez por spin (hitDelivered guard) — goal continua até o fim.
        if (!hitDelivered && mutant.distanceTo(target) < 2.5) {
            mutant.applyMutantHit(target);
            Vec3 kb = target.position().subtract(mutant.position()).normalize();
            target.push(kb.x * 1.6, 0.5, kb.z * 1.6);
            hitDelivered = true;
        }
    }

    @Override
    public void stop() {
        mutant.spinAttackCooldown = cooldownForPhase(mutant.getPhase());

        // Para o som do spin nos clients perto — evita o som vazar depois do fim
        if (mutant.level() instanceof ServerLevel server) {
            ClientboundStopSoundPacket pkt = new ClientboundStopSoundPacket(
                    ModSounds.MUTANT_SPIN.get().getLocation(), SoundSource.HOSTILE);
            for (ServerPlayer p : server.players()) {
                if (p.distanceToSqr(mutant) < 64 * 64) {
                    p.connection.send(pkt);
                }
            }
        }
    }
}
