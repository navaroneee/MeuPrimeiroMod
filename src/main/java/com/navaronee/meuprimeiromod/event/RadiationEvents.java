package com.navaronee.meuprimeiromod.event;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.effect.ModEffects;
import com.navaronee.meuprimeiromod.entity.RadioactiveTargets;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID)
public class RadiationEvents {

    private static boolean hasFullLeadArmor(LivingEntity entity) {
        return entity.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.LEAD_HELMET.get()
                && entity.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.LEAD_CHESTPLATE.get()
                && entity.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.LEAD_LEGGINGS.get()
                && entity.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.LEAD_BOOTS.get();
    }

    private static boolean isHoldingCesium(LivingEntity entity) {
        return entity.getMainHandItem().getItem() == ModItems.CESIUM_DUST.get()
                || entity.getOffhandItem().getItem() == ModItems.CESIUM_DUST.get();
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        // PERFORMANCE: throttle de 40 ticks (2s). Vale pra todos os mobs/players.
        if (entity.tickCount % 40 != 0) return;

        // PERFORMANCE: server-side only.
        if (entity.level().isClientSide()) return;

        // Criaturas de césio (Mutant/Bee/Slime radioativos) são imunes — feitas de radiação.
        if (RadioactiveTargets.isCesiumImmune(entity)) return;

        // Armadura de chumbo full: imune + slowness (aplica só em Player porque slowness em mob virou sem sentido)
        if (hasFullLeadArmor(entity)) {
            if (entity instanceof Player) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0, false, false, true));
            }
            return;
        }

        // Segurar cesium na mão (aplicável a players e mobs que carregam itens)
        if (isHoldingCesium(entity)) {
            entity.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 200, 0, false, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 2, false, true, true));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1, false, true, true));
        }

        // PERFORMANCE: early return se não há cesium no mundo nem zonas de granada.
        // Sem isso, todo mob tickaria checando listas vazias.
        boolean hasOres = CesiumTracker.hasActiveOre();
        boolean hasZones = RadiationZoneManager.hasActiveZones();
        if (!hasOres && !hasZones) return;

        // Proximidade a cesium_ore
        if (hasOres) {
            double dist = CesiumTracker.findNearestCesiumDistance(entity);
            if (dist <= 5.0) {
                entity.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 200, 1, false, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 3, false, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2, false, true, true));
            } else if (dist <= 10.0) {
                entity.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 200, 0, false, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 1, false, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0, false, true, true));
            }
        }

        // Zonas de radiação de granada (AOE temporário)
        if (hasZones) {
            int zoneLevel = RadiationZoneManager.getRadiationLevel(entity, entity.level().getGameTime());
            if (zoneLevel == 2) {
                entity.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 200, 2, false, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 4, false, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 3, false, true, true));
            } else if (zoneLevel == 1) {
                entity.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 200, 1, false, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 2, false, true, true));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1, false, true, true));
            }
        }
    }
}
