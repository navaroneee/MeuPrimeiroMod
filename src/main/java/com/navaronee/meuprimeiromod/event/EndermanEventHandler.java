package com.navaronee.meuprimeiromod.event;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Injeta o EndermanStealNukeGoal em todos os Endermans que spawnam no servidor.
 */
@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID)
public class EndermanEventHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getEntity() instanceof EnderMan enderman) {
            // Prioridade 1 — alta, mas não absoluta (ataque ao player ainda tem prio 0)
            enderman.goalSelector.addGoal(1, new EndermanStealNukeGoal(enderman));
        }
    }
}
