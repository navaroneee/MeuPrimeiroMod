package com.navaronee.meuprimeiromod.client.sound;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID, value = Dist.CLIENT)
public class GeigerSoundHandler {

    private static GeigerCounterSoundInstance currentSound = null;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        boolean holdingCounter = player.getMainHandItem().getItem() == ModItems.GAISER_COUNTER.get()
                || player.getOffhandItem().getItem() == ModItems.GAISER_COUNTER.get();

        if (!holdingCounter) {
            if (currentSound != null) {
                mc.getSoundManager().stop(currentSound);
                currentSound = null;
            }
            return;
        }

        int level = GeigerCounterSoundInstance.getDistanceLevel(player);

        if (level == 0) {
            if (currentSound != null) {
                mc.getSoundManager().stop(currentSound);
                currentSound = null;
            }
            return;
        }

        // If sound stopped or level changed, start new sound
        if (currentSound == null || currentSound.isStopped() || currentSound.getCurrentLevel() != level) {
            if (currentSound != null) {
                mc.getSoundManager().stop(currentSound);
            }
            currentSound = new GeigerCounterSoundInstance(player,
                    GeigerCounterSoundInstance.getSoundForLevel(level), level);
            mc.getSoundManager().play(currentSound);
        }
    }
}
