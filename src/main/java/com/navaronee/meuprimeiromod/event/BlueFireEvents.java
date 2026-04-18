package com.navaronee.meuprimeiromod.event;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Fogo azul cesium: spawna SOUL_FIRE_FLAME em entities on fire enquanto o fogo dura.
 * Overrides visualmente o fogo laranja normal dando aspecto radioativo azul.
 */
@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID)
public class BlueFireEvents {

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        // Só entities em fogo
        if (entity.getRemainingFireTicks() <= 0) return;

        // Throttle: 1 spawn a cada 2 ticks por entity (pra não floodar)
        if (entity.tickCount % 2 != 0) return;

        if (!(entity.level() instanceof ServerLevel server)) return;

        // Spawn SOUL_FIRE_FLAME em cima da entity (broadcast pros players próximos)
        double x = entity.getX();
        double y = entity.getY() + entity.getBbHeight() * 0.5;
        double z = entity.getZ();

        for (ServerPlayer player : server.players()) {
            if (player.distanceToSqr(x, y, z) > 64 * 64) continue; // só em range de 64
            server.sendParticles(player, ParticleTypes.SOUL_FIRE_FLAME, false,
                    x, y, z,
                    3,
                    entity.getBbWidth() * 0.3, entity.getBbHeight() * 0.3, entity.getBbWidth() * 0.3,
                    0.02);
        }

        // Dano over-time (fogo azul é radioativo, machuca mais)
        // Aplica a cada 20 ticks (1s) pra não ser instant kill
        if (entity.tickCount % 20 == 0) {
            entity.hurt(entity.level().damageSources().onFire(), 1.5F);
        }
    }
}
