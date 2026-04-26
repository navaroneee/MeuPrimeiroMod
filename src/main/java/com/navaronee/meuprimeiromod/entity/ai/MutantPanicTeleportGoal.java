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
 * "Enderman-like": se o Mutant ficar por X segundos continuos perto do alvo
 * (distância < 4), teleporta pra LONGE (8-12 blocos) independente da fase.
 * Dá respiro ao player, quebra a grudação, e força o Mutant a reengajar.
 */
public class MutantPanicTeleportGoal extends Goal {

    private static final double CLOSE_THRESHOLD = 4.0;
    private static final int TIME_THRESHOLD = 100;       // 5s grudado → teleport
    private static final double TELEPORT_MIN_DIST = 8.0;
    private static final double TELEPORT_MAX_DIST = 12.0;
    private static final int POST_TELEPORT_COOLDOWN = 140; // 7s de antilock

    private final MutantEntity mutant;
    private int ticksClose = 0;
    private int cooldown = 0;

    public MutantPanicTeleportGoal(MutantEntity mutant) {
        this.mutant = mutant;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mutant.hitStrongLock > 0) return false;

        if (cooldown > 0) {
            cooldown--;
            return false;
        }

        LivingEntity target = mutant.getTarget();
        if (target == null || !target.isAlive()) {
            ticksClose = 0;
            return false;
        }

        double d = mutant.distanceTo(target);
        if (d < CLOSE_THRESHOLD) {
            ticksClose++;
        } else {
            ticksClose = 0;
        }

        return ticksClose >= TIME_THRESHOLD;
    }

    @Override
    public boolean canContinueToUse() {
        return false; // ação instantânea
    }

    @Override
    public void start() {
        LivingEntity target = mutant.getTarget();
        if (target == null) return;

        // Tenta 8 candidatos em torno do alvo, 8-12 blocos de distância
        for (int i = 0; i < 8; i++) {
            double angle = mutant.getRandom().nextDouble() * Math.PI * 2;
            double dist = TELEPORT_MIN_DIST
                    + mutant.getRandom().nextDouble() * (TELEPORT_MAX_DIST - TELEPORT_MIN_DIST);
            double tx = target.getX() + Math.cos(angle) * dist;
            double ty = target.getY();
            double tz = target.getZ() + Math.sin(angle) * dist;

            if (tryTeleport(tx, ty, tz)) {
                ticksClose = 0;
                cooldown = POST_TELEPORT_COOLDOWN;
                // Após teleport pra longe, dispara heavy attack no próximo tick
                mutant.heavyAttackCooldown = 0;
                mutant.isCastingHeavy = false; // garante que canUse não seja bloqueado
                return;
            }
        }

        // Falhou — reseta contador e dá um cd pequeno
        ticksClose = 0;
        cooldown = 40;
    }

    private boolean tryTeleport(double x, double y, double z) {
        Level level = mutant.level();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);

        int descent = 0;
        while (pos.getY() > level.getMinBuildHeight()
                && !level.getBlockState(pos.below()).blocksMotion()
                && descent < 8) {
            pos.move(0, -1, 0);
            descent++;
        }

        BlockState below = level.getBlockState(pos.below());
        if (!below.blocksMotion() || below.getFluidState().isSource()) return false;

        for (int dy = 0; dy < 3; dy++) {
            BlockState st = level.getBlockState(pos.above(dy));
            if (st.blocksMotion() || !st.getFluidState().isEmpty()) return false;
        }

        double fromX = mutant.getX();
        double fromY = mutant.getY();
        double fromZ = mutant.getZ();

        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.PORTAL,
                    fromX, fromY + 1.5, fromZ, 40, 0.6, 1.2, 0.6, 0.12);
        }

        mutant.teleportTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.PORTAL,
                    mutant.getX(), mutant.getY() + 1.5, mutant.getZ(),
                    40, 0.6, 1.2, 0.6, 0.12);
        }
        level.playSound(null, fromX, fromY, fromZ,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.2F, 0.8F);
        level.playSound(null, mutant.getX(), mutant.getY(), mutant.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.2F, 0.8F);

        return true;
    }
}
