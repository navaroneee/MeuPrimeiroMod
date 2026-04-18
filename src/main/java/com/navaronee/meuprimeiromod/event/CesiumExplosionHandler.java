package com.navaronee.meuprimeiromod.event;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.entity.CesiumNukePrimedEntity;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Remove entities da lista de afetados pela explosão do cesium nuke.
 * Assim a explosão vanilla só destrói blocos; entities recebem queima lenta
 * (setSecondsOnFire + fogo azul) via CesiumNukePrimedEntity.explode().
 */
@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID)
public class CesiumExplosionHandler {

    @SubscribeEvent
    public static void onDetonate(ExplosionEvent.Detonate event) {
        if (event.getExplosion().getDirectSourceEntity() instanceof CesiumNukePrimedEntity) {
            event.getAffectedEntities().clear();
        }
    }
}
