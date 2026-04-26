package com.navaronee.meuprimeiromod.entity.ai;

import com.navaronee.meuprimeiromod.entity.MutantEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

/**
 * MeleeAttackGoal phase-aware: desativa completamente na fase 4 (sem melee),
 * nas demais fases funciona normal (o cooldown de hit é aplicado no
 * MutantEntity.doHurtTarget).
 */
public class MutantMeleeAttackGoal extends MeleeAttackGoal {

    private final MutantEntity mutant;

    public MutantMeleeAttackGoal(MutantEntity mutant, double speedModifier, boolean followEvenIfNotSeen) {
        super(mutant, speedModifier, followEvenIfNotSeen);
        this.mutant = mutant;
    }

    @Override
    public boolean canUse() {
        if (mutant.getPhase() >= 4) return false;
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (mutant.getPhase() >= 4) return false;
        return super.canContinueToUse();
    }
}
