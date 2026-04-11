package com.navaronee.meuprimeiromod.event;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.block.ModBlocks;
import com.navaronee.meuprimeiromod.effect.ModEffects;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID)
public class RadiationEvents {

    private static boolean hasFullLeadArmor(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.LEAD_HELMET.get()
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.LEAD_CHESTPLATE.get()
                && player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.LEAD_LEGGINGS.get()
                && player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.LEAD_BOOTS.get();
    }

    private static boolean isHoldingCesium(Player player) {
        return player.getMainHandItem().getItem() == ModItems.CESIUM_DUST.get()
                || player.getOffhandItem().getItem() == ModItems.CESIUM_DUST.get();
    }

    private static double findNearestCesiumDistance(Player player, Level level) {
        BlockPos playerPos = player.blockPosition();
        double closestDist = Double.MAX_VALUE;
        int range = 15;

        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-range, -range, -range),
                playerPos.offset(range, range, range))) {
            if (level.getBlockState(pos).is(ModBlocks.CESIUM_ORE.get())) {
                double dist = Math.sqrt(pos.distSqr(playerPos));
                if (dist < closestDist) {
                    closestDist = dist;
                }
            }
        }
        return closestDist;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide()) return;

        boolean fullLead = hasFullLeadArmor(player);

        // Full lead armor gives slowness
        if (fullLead && player.tickCount % 40 == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0, false, false, true));
        }

        // Radiation from holding cesium dust
        if (isHoldingCesium(player) && !fullLead) {
            if (player.tickCount % 40 == 0) {
                player.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 200, 0, false, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 2, false, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1, false, true, true));
            }
        }

        // Radiation from nearby cesium ore blocks
        double dist = findNearestCesiumDistance(player, level);
        if (dist <= 5.0 && !fullLead) {
            if (player.tickCount % 40 == 0) {
                player.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 200, 1, false, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 3, false, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 2, false, true, true));
            }
        } else if (dist <= 10.0 && !fullLead) {
            if (player.tickCount % 40 == 0) {
                player.addEffect(new MobEffectInstance(ModEffects.RADIATION.get(), 200, 0, false, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 200, 1, false, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0, false, true, true));
            }
        }

    }
}
