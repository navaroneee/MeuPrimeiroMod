package com.navaronee.meuprimeiromod.entity.ai;

import com.navaronee.meuprimeiromod.entity.MutantEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

/**
 * Teleport estilo Enderman, restrito ao raio de 10 blocos do player.
 * Desaparece da posição atual e reaparece perto do player pra flanquear.
 * Chance pequena por tick; cooldown curto pra manter o "boss feel" de imprevisibilidade.
 */
public class MutantTeleportGoal extends Goal {

    private static final double MIN_DIST = 3.0;
    private static final double MAX_DIST = 10.0;

    private final MutantEntity mutant;

    public MutantTeleportGoal(MutantEntity mutant) {
        this.mutant = mutant;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        // Fase 1 = nunca teleporta
        int phase = mutant.getPhase();
        if (phase <= 1) return false;

        if (mutant.teleportCooldown > 0) return false;
        if (mutant.hitStrongLock > 0) return false;
        LivingEntity target = mutant.getTarget();
        if (target == null || !target.isAlive()) return false;
        double d = mutant.distanceTo(target);
        if (d < MIN_DIST || d > MAX_DIST) return false;
        return mutant.getRandom().nextInt(triggerChance(phase)) == 0;
    }

    private static int triggerChance(int phase) {
        return switch (phase) {
            case 2 -> 120; // raro
            case 3 -> 60;  // moderado
            default -> 20; // fase 4: muito frequente
        };
    }

    private static int cooldownForPhase(int phase) {
        return switch (phase) {
            case 2 -> 120; // 6s
            case 3 -> 80;  // 4s
            default -> 30; // 1.5s
        };
    }

    @Override
    public boolean canContinueToUse() {
        return false; // ação instantânea
    }

    @Override
    public void start() {
        LivingEntity target = mutant.getTarget();
        if (target == null) return;

        // Tenta até 8 posições candidatas próximas ao player
        for (int i = 0; i < 8; i++) {
            double angle = mutant.getRandom().nextDouble() * Math.PI * 2;
            double dist = 2.5 + mutant.getRandom().nextDouble() * 1.5; // 2.5-4 blocos do player
            double tx = target.getX() + Math.cos(angle) * dist;
            double tz = target.getZ() + Math.sin(angle) * dist;
            double ty = target.getY();

            if (tryTeleport(tx, ty, tz)) return;
        }

        // Falhou — apenas aplica cooldown leve pra não ficar tentando
        mutant.teleportCooldown = 40;
    }

    private boolean tryTeleport(double x, double y, double z) {
        Level level = mutant.level();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);

        // Desce até achar chão (até 6 blocos)
        int descent = 0;
        while (pos.getY() > level.getMinBuildHeight() && !level.getBlockState(pos.below()).blocksMotion() && descent < 6) {
            pos.move(0, -1, 0);
            descent++;
        }

        BlockState below = level.getBlockState(pos.below());
        if (!below.blocksMotion() || below.getFluidState().isSource()) return false;

        // Checa espaço pro corpo do Mutant (3 blocos de altura)
        for (int dy = 0; dy < 3; dy++) {
            BlockState st = level.getBlockState(pos.above(dy));
            if (st.blocksMotion() || !st.getFluidState().isEmpty()) return false;
        }

        double fromX = mutant.getX();
        double fromY = mutant.getY();
        double fromZ = mutant.getZ();

        if (level instanceof ServerLevel server) {
            // Partículas na posição de saída
            server.sendParticles(ParticleTypes.PORTAL,
                    fromX, fromY + 1.5, fromZ, 30, 0.5, 1.0, 0.5, 0.1);
        }

        mutant.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        if (level instanceof ServerLevel server) {
            // Partículas na posição de chegada
            server.sendParticles(ParticleTypes.PORTAL,
                    mutant.getX(), mutant.getY() + 1.5, mutant.getZ(),
                    30, 0.5, 1.0, 0.5, 0.1);
        }
        level.playSound(null, fromX, fromY, fromZ,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);
        level.playSound(null, mutant.getX(), mutant.getY(), mutant.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);

        mutant.teleportCooldown = cooldownForPhase(mutant.getPhase());

        // Fase 3+: teleport encadeia com spin — zera cooldown de spin e sinaliza
        if (mutant.getPhase() >= 3) {
            mutant.spinAttackCooldown = 0;
            mutant.forceSpinAfterTeleport = true;
        }
        return true;
    }
}
