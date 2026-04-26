package com.navaronee.meuprimeiromod.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;

/**
 * Predicate compartilhado de targets válidos pra mobs radioativos.
 * Ataca tudo que não for:
 *  - Monster (hostis do vanilla/mods)
 *  - Outro mob radioativo nosso (não se canibalizam)
 *
 * Player e mobs passivos (Animal, Villager, IronGolem, Wolf, etc) ENTRAM como alvo.
 */
public final class RadioactiveTargets {

    private RadioactiveTargets() {}

    public static boolean isValidTarget(LivingEntity target) {
        if (target instanceof Monster) return false;
        if (target instanceof RadioactiveBeeEntity) return false;
        if (target instanceof RadioactiveSlimeEntity) return false;
        return true;
    }

    /**
     * Criaturas de césio são imunes à radiação — são feitas dela.
     * Usado pra bypassar aplicação de efeito RADIATION em ticks/ataques/zonas.
     */
    public static boolean isCesiumImmune(LivingEntity entity) {
        return entity instanceof MutantEntity
                || entity instanceof RadioactiveBeeEntity
                || entity instanceof RadioactiveSlimeEntity;
    }
}
