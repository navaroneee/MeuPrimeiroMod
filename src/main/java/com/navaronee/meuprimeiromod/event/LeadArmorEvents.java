package com.navaronee.meuprimeiromod.event;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Buffs do set completo de chumbo (helmet+chestplate+leggings+boots):
 *  - Imune a fall damage
 *  - Jump Boost suave (efeito constante)
 *
 * Slowness por peso continua aplicado em RadiationEvents.
 */
@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID)
public class LeadArmorEvents {

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if (!hasFullLeadSet(entity)) return;
        // Zera dano de queda — set é blindado pra impacto também
        event.setDistance(0);
        event.setDamageMultiplier(0);
    }

    @SubscribeEvent
    public static void onTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)) return;
        if (entity.level().isClientSide()) return;

        // PERFORMANCE: throttle 20 ticks (1s) — efeito dura mais
        if (entity.tickCount % 20 != 0) return;

        if (!hasFullLeadSet(entity)) return;

        // Jump boost level 0 = +50% de altura. Renova a cada 1s.
        entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 40, 0, false, false, true));
    }

    private static boolean hasFullLeadSet(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.LEAD_HELMET.get()
                && entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.LEAD_CHESTPLATE.get()
                && entity.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.LEAD_LEGGINGS.get()
                && entity.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.LEAD_BOOTS.get();
    }
}
