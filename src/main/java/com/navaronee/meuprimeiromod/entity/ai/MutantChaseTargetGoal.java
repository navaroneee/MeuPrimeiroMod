package com.navaronee.meuprimeiromod.entity.ai;

import com.navaronee.meuprimeiromod.entity.MutantEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/**
 * Fase 4 pursuit: substitui o MeleeAttackGoal desativado, mantendo o Mutant
 * sempre no encalço do alvo pra que os specials (heavy/spin/teleport) tenham
 * alvo em range. Sem delivery de dano — só pathfinding.
 */
public class MutantChaseTargetGoal extends Goal {

    private final MutantEntity mutant;
    private final double speedModifier;

    public MutantChaseTargetGoal(MutantEntity mutant, double speedModifier) {
        this.mutant = mutant;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mutant.getPhase() < 4) return false;
        if (mutant.hitStrongLock > 0) return false;
        LivingEntity target = mutant.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        if (mutant.getPhase() < 4) return false;
        if (mutant.hitStrongLock > 0) return false;
        LivingEntity target = mutant.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = mutant.getTarget();
        if (target == null) return;

        mutant.getLookControl().setLookAt(target, 30F, 30F);

        double d = mutant.distanceTo(target);

        // Se já tá no range de specials (4-10 blocos), para e deixa os attack goals agir
        if (d <= 5.0) {
            mutant.getNavigation().stop();
            return;
        }

        // Caso contrário, persegue continuamente
        if (mutant.getNavigation().isDone()) {
            mutant.getNavigation().moveTo(target, speedModifier);
        }
    }

    @Override
    public void stop() {
        mutant.getNavigation().stop();
    }
}
