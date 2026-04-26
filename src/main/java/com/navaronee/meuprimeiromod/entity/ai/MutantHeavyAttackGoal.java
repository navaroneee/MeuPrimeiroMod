package com.navaronee.meuprimeiromod.entity.ai;

import com.navaronee.meuprimeiromod.entity.ModEntities;
import com.navaronee.meuprimeiromod.entity.MutantEntity;
import com.navaronee.meuprimeiromod.entity.MutantTntProjectileEntity;
import com.navaronee.meuprimeiromod.entity.RadioactiveBeeEntity;
import com.navaronee.meuprimeiromod.entity.RadioactiveSlimeEntity;
import com.navaronee.meuprimeiromod.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;

import java.util.EnumSet;

/**
 * Heavy attack combo: animação open (3s) → spawn payload aleatório → close (2s).
 * Três variantes selecionadas aleatoriamente no start():
 *   BEES  — 3 abelhas radioativas perseguem o player
 *   SLIMES — 1 slime grande + 4 pequenos perseguem o player
 *   TNT   — lança projétil TNT rebatível na direção do player
 */
public class MutantHeavyAttackGoal extends Goal {

    private static final int OPEN_DURATION = 60;     // 3s
    private static final int PAYLOAD_TICK = 30;      // meio da open
    private static final int CLOSE_DURATION = 40;    // 2s
    private static final int TOTAL_DURATION = OPEN_DURATION + CLOSE_DURATION;

    private enum Variant { BEES, SLIMES, TNT }

    private final MutantEntity mutant;
    private int ticks;
    private Variant variant;
    private boolean payloadFired;
    private boolean closeFired;

    public MutantHeavyAttackGoal(MutantEntity mutant) {
        this.mutant = mutant;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (mutant.heavyAttackCooldown > 0) return false;
        if (mutant.hitStrongLock > 0) return false;
        LivingEntity target = mutant.getTarget();
        if (target == null || !target.isAlive()) return false;
        double d = mutant.distanceTo(target);
        return d >= 3.0 && d <= 14.0;
    }

    @Override
    public boolean canContinueToUse() {
        return ticks < TOTAL_DURATION && mutant.hitStrongLock <= 0;
    }

    @Override
    public void start() {
        this.ticks = 0;
        this.payloadFired = false;
        this.closeFired = false;
        this.variant = pickVariantForPhase(mutant.getPhase());
        mutant.getNavigation().stop();
        mutant.isCastingHeavy = true;
        mutant.level().broadcastEntityEvent(mutant, MutantEntity.EVENT_ATTACK_HEAVY_OPEN);
        mutant.level().playSound(null, mutant.blockPosition(),
                ModSounds.MUTANT_OPEN.get(), SoundSource.HOSTILE, 1.4F, 1.0F);
    }

    /**
     * Pesos de variante por fase. Slime tem peso menor que abelha porque 5 slimes
     * visualmente "pesam" muito na tela (a mesma presença que 1 heavy com abelhas
     * parece dominar a luta). TNT escala bastante em fases finais.
     */
    private Variant pickVariantForPhase(int phase) {
        int roll = mutant.getRandom().nextInt(100);
        return switch (phase) {
            case 1 -> roll < 55 ? Variant.BEES : (roll < 80 ? Variant.SLIMES : Variant.TNT);
            case 2 -> roll < 55 ? Variant.BEES : (roll < 78 ? Variant.SLIMES : Variant.TNT);
            case 3 -> roll < 45 ? Variant.BEES : (roll < 70 ? Variant.SLIMES : Variant.TNT);
            default -> roll < 40 ? Variant.BEES : (roll < 65 ? Variant.SLIMES : Variant.TNT);
        };
    }

    /**
     * Cooldown pós-cast (ticks) por fase.
     */
    private int cooldownForPhase(int phase) {
        return switch (phase) {
            case 1 -> 240;  // 12s
            case 2 -> 160;  // 8s
            case 3 -> 100;  // 5s
            default -> 40;  // 2s — rapid fire na fase 4
        };
    }

    @Override
    public void tick() {
        ticks++;
        LivingEntity target = mutant.getTarget();

        // Trava no lugar durante o cast — zera navegação e velocidade horizontal,
        // olha pro alvo
        mutant.getNavigation().stop();
        mutant.setDeltaMovement(0, mutant.getDeltaMovement().y, 0);
        if (target != null) mutant.getLookControl().setLookAt(target, 30.0F, 30.0F);

        if (!payloadFired && ticks >= PAYLOAD_TICK) {
            payloadFired = true;
            firePayload(target);
        }

        if (!closeFired && ticks >= OPEN_DURATION) {
            closeFired = true;
            mutant.level().broadcastEntityEvent(mutant, MutantEntity.EVENT_ATTACK_HEAVY_CLOSE);
        }
    }

    @Override
    public void stop() {
        mutant.isCastingHeavy = false;
        mutant.heavyAttackCooldown = cooldownForPhase(mutant.getPhase());
    }

    private void firePayload(LivingEntity target) {
        if (!(mutant.level() instanceof ServerLevel server)) return;
        if (target == null) return;

        switch (variant) {
            case BEES -> spawnBees(server, target, 3);
            case SLIMES -> spawnSlimes(server, target);
            case TNT -> shootTnt(target);
        }
    }

    private void spawnBees(ServerLevel server, LivingEntity target, int count) {
        for (int i = 0; i < count; i++) {
            RadioactiveBeeEntity bee = ModEntities.RADIOACTIVE_BEE.get().create(server);
            if (bee == null) continue;
            Vec3 pos = mutant.position().add(
                    (mutant.getRandom().nextDouble() - 0.5) * 2,
                    1.5 + mutant.getRandom().nextDouble(),
                    (mutant.getRandom().nextDouble() - 0.5) * 2);
            bee.moveTo(pos.x, pos.y, pos.z, mutant.getRandom().nextFloat() * 360F, 0);
            bee.finalizeSpawn(server, server.getCurrentDifficultyAt(bee.blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null, null);
            bee.setTarget(target);
            server.addFreshEntity(bee);
        }
    }

    private void spawnSlimes(ServerLevel server, LivingEntity target) {
        spawnSlime(server, target, 4); // 1 grande
        for (int i = 0; i < 2; i++) spawnSlime(server, target, 1); // 2 pequenos
    }

    private void spawnSlime(ServerLevel server, LivingEntity target, int size) {
        RadioactiveSlimeEntity slime = ModEntities.RADIOACTIVE_SLIME.get().create(server);
        if (slime == null) return;
        slime.setSize(size, true);
        Vec3 pos = mutant.position().add(
                (mutant.getRandom().nextDouble() - 0.5) * 2.5,
                0.2,
                (mutant.getRandom().nextDouble() - 0.5) * 2.5);
        slime.moveTo(pos.x, pos.y, pos.z, mutant.getRandom().nextFloat() * 360F, 0);
        slime.finalizeSpawn(server, server.getCurrentDifficultyAt(slime.blockPosition()),
                MobSpawnType.MOB_SUMMONED, null, null);
        slime.setTarget(target);
        server.addFreshEntity(slime);
    }

    private void shootTnt(LivingEntity target) {
        // Origin: altura do umbigo (~50% da BB) — bem no meio do corpo
        Vec3 origin = mutant.position().add(0, mutant.getBbHeight() * 0.5, 0);
        Vec3 dir = target.position().add(0, target.getBbHeight() * 0.5, 0).subtract(origin).normalize();

        MutantTntProjectileEntity tnt = new MutantTntProjectileEntity(
                mutant.level(), mutant, dir.x, dir.y, dir.z);
        tnt.setPos(origin.x, origin.y, origin.z);
        mutant.level().addFreshEntity(tnt);
    }
}
