package com.navaronee.meuprimeiromod.entity;

import com.navaronee.meuprimeiromod.effect.ModEffects;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Bee radioativa: sempre agressiva + aplica radiação no ataque.
 * Player com armadura de chumbo full é imune (RadiationEvents já trata).
 * Pensada pra ser invocada pelo Mutant como "ataque" à distância.
 */
public class RadioactiveBeeEntity extends Bee {

    public RadioactiveBeeEntity(EntityType<? extends Bee> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        // KILL KILL KILL o player — ignora tudo mais. Range alto + checa a cada tick.
        this.targetSelector.removeAllGoals(g -> true);
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                this, Player.class, 1, true, false, p -> true));
    }

    @Override
    public boolean isAngry() {
        return true; // sempre agressiva (bypassa timer de anger vanilla)
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);

        if (hit && target instanceof LivingEntity livingTarget) {
            // Criaturas de césio são imunes (a própria bee, slime, mutant)
            if (RadioactiveTargets.isCesiumImmune(livingTarget)) return hit;
            // Player com armadura de chumbo full é imune
            if (livingTarget instanceof Player p && hasFullLeadArmor(p)) {
                return hit;
            }

            // Aplica radiação (amplifier 1 = nível 2, dá uma dor)
            livingTarget.addEffect(new MobEffectInstance(
                    ModEffects.RADIATION.get(), 200, 1, false, true, true));
        }

        return hit;
    }

    private static boolean hasFullLeadArmor(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.LEAD_HELMET.get()
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.LEAD_CHESTPLATE.get()
                && player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.LEAD_LEGGINGS.get()
                && player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.LEAD_BOOTS.get();
    }
}
