package com.navaronee.meuprimeiromod.entity;

import com.navaronee.meuprimeiromod.effect.ModEffects;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Slime radioativo: igual ao Slime vanilla (3 tamanhos, split on death), mas
 * aplica radiação quando ataca. Tamanho é gerenciado pelo próprio Slime vanilla
 * via setSize() (1=pequeno, 2=médio, 4=grande).
 *
 * Player com armadura de chumbo full → imune à radiação.
 */
public class RadioactiveSlimeEntity extends Slime {

    public RadioactiveSlimeEntity(EntityType<? extends Slime> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // Remove os targets vanilla de Slime (Player/IronGolem) e amplia.
        this.targetSelector.removeAllGoals(g -> true);
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                this, LivingEntity.class, 10, true, false,
                RadioactiveTargets::isValidTarget));
    }

    /**
     * Slime vanilla retorna apenas getSize() = 1/2/4 dano, raso.
     * Boost: base 4 + size × 2.5 → size 1=6.5, size 2=9, size 4=14.
     */
    @Override
    protected float getAttackDamage() {
        return 4.0F + this.getSize() * 2.5F;
    }

    /**
     * Override completo do dealDamage vanilla. O check `distanceToSqr < 0.6*size²`
     * é apertado e em slime grande contava só toque "perto do centro", então quem
     * encostava na lateral não levava dano. Aqui usamos BB.intersects que é o
     * "encostou em qualquer parte = dano".
     */
    @Override
    protected void dealDamage(LivingEntity target) {
        if (!this.isAlive()) return;

        AABB inflated = this.getBoundingBox().inflate(0.1);
        if (!inflated.intersects(target.getBoundingBox())) return;
        if (!this.hasLineOfSight(target)) return;

        if (!target.hurt(this.damageSources().mobAttack(this), this.getAttackDamage())) return;

        this.playSound(net.minecraft.sounds.SoundEvents.SLIME_ATTACK, 1.0F,
                (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F);
        this.doEnchantDamageEffects(this, target);

        // Criaturas de césio são imunes (não se radiam entre si)
        if (RadioactiveTargets.isCesiumImmune(target)) return;
        // Player com armadura de chumbo full = imune à radiação (mas leva o dano)
        if (target instanceof Player p && hasFullLeadArmor(p)) return;

        // Amplifier escala com tamanho (slime bigger = radiação mais forte)
        int amp = Math.max(0, this.getSize() - 1); // size 1→amp 0, size 2→amp 1, size 4→amp 3
        int duration = 160 + this.getSize() * 80;  // duração escala também

        target.addEffect(new MobEffectInstance(
                ModEffects.RADIATION.get(), duration, amp, false, true, true));
    }

    private static boolean hasFullLeadArmor(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.LEAD_HELMET.get()
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.LEAD_CHESTPLATE.get()
                && player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.LEAD_LEGGINGS.get()
                && player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.LEAD_BOOTS.get();
    }
}
