package com.navaronee.meuprimeiromod.client;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.entity.AtomicCloudEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

/**
 * Screen shake enquanto houver um AtomicCloudEntity ativo perto do player.
 * Intensidade decai com o tempo (tick) e com a distância.
 */
@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID, value = Dist.CLIENT)
public class ScreenShakeEvents {

    private static final Random RNG = new Random();
    private static final double MAX_RANGE = 120.0;

    @SubscribeEvent
    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        float totalIntensity = computeIntensity(player);
        if (totalIntensity <= 0) return;

        // Perturbação proporcional à intensidade (menos violenta)
        float pitchJitter = (RNG.nextFloat() - 0.5F) * totalIntensity * 1.2F;
        float yawJitter = (RNG.nextFloat() - 0.5F) * totalIntensity * 1.2F;
        float rollJitter = (RNG.nextFloat() - 0.5F) * totalIntensity * 1.8F;

        event.setPitch(event.getPitch() + pitchJitter);
        event.setYaw(event.getYaw() + yawJitter);
        event.setRoll(event.getRoll() + rollJitter);
    }

    private static float computeIntensity(Player player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return 0;

        AABB searchBox = player.getBoundingBox().inflate(MAX_RANGE);
        float maxIntensity = 0;

        for (AtomicCloudEntity cloud : mc.level.getEntitiesOfClass(AtomicCloudEntity.class, searchBox)) {
            double dist = player.distanceTo(cloud);
            if (dist > MAX_RANGE) continue;

            // Intensidade temporal (cresce, estabiliza, decai)
            float timeIntensity = timeCurve(cloud.tickCount);
            if (timeIntensity <= 0) continue;

            // Intensidade por distância (mais perto = mais forte)
            float distFactor = 1F - (float) (dist / MAX_RANGE);
            distFactor = distFactor * distFactor;

            float intensity = timeIntensity * distFactor;
            if (intensity > maxIntensity) maxIntensity = intensity;
        }

        return maxIntensity;
    }

    /**
     * Curva com fade-in/out suave. Usa ease functions pra transições naturais.
     *
     * - 0-10:    fade-in suave (0 -> 1.2) — sem jump brusco
     * - 10-40:   pico durante o impacto (1.2 -> 2.2)
     * - 40-120:  sustentação leve durante formação do cogumelo (2.2 -> 1.2)
     * - 120-260: decay gradual (1.2 -> 0.4)
     * - 260-500: fade-out muito suave (0.4 -> 0)
     */
    private static float timeCurve(int age) {
        if (age < 10) {
            // Ease-in quadratic
            float p = age / 10F;
            return p * p * 1.2F;
        }
        if (age < 40) {
            // Sobe suave de 1.2 até 2.2 (pico menor que antes)
            float p = (age - 10) / 30F;
            return 1.2F + p * 1.0F;
        }
        if (age < 120) {
            // Decai devagar de 2.2 até 1.2 com flutter leve
            float p = (age - 40) / 80F;
            float base = 2.2F - p * 1.0F;
            float flutter = Mth.sin(age * 0.3F) * 0.15F;
            return base + flutter;
        }
        if (age < 260) {
            // Decay gradual
            float p = (age - 120) / 140F;
            return 1.2F - p * 0.8F;
        }
        if (age < 500) {
            // Fade-out suave cúbico
            float p = (age - 260) / 240F;
            float eased = (1F - p) * (1F - p) * (1F - p);
            return 0.4F * eased;
        }
        return 0;
    }
}
