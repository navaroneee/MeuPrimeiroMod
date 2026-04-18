package com.navaronee.meuprimeiromod.client;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.client.entity.AtomicCloudRenderer;
import com.navaronee.meuprimeiromod.client.entity.CesiumGranadeRenderer;
import com.navaronee.meuprimeiromod.client.entity.CesiumNukePrimedRenderer;
import com.navaronee.meuprimeiromod.client.particle.CesiumAtomCoreParticle;
import com.navaronee.meuprimeiromod.client.particle.CesiumAtomFogParticle;
import com.navaronee.meuprimeiromod.client.particle.CesiumAtomRingParticle;
import com.navaronee.meuprimeiromod.client.particle.CesiumFireParticle;
import com.navaronee.meuprimeiromod.client.particle.CesiumLightSmokeParticle;
import com.navaronee.meuprimeiromod.client.particle.CesiumShockwaveParticle;
import com.navaronee.meuprimeiromod.particle.ModParticles;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import com.navaronee.meuprimeiromod.client.model.LeadArmorModel;
import com.navaronee.meuprimeiromod.entity.ModEntities;
import com.navaronee.meuprimeiromod.menu.ModMenuTypes;
import com.navaronee.meuprimeiromod.screen.CesiumRefinerScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LeadArmorModel.LAYER_LOCATION, LeadArmorModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.CESIUM_REFINER.get(), CesiumRefinerScreen::new);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CESIUM_GRANADE.get(), CesiumGranadeRenderer::new);
        event.registerEntityRenderer(ModEntities.CESIUM_NUKE_PRIMED.get(), CesiumNukePrimedRenderer::new);
        event.registerEntityRenderer(ModEntities.ATOMIC_CLOUD.get(), AtomicCloudRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.CESIUM_ATOM_CORE.get(), CesiumAtomCoreParticle.Provider::new);
        event.registerSpriteSet(ModParticles.CESIUM_ATOM_RING.get(), CesiumAtomRingParticle.Provider::new);
        event.registerSpriteSet(ModParticles.CESIUM_ATOM_FOG.get(), CesiumAtomFogParticle.Provider::new);
        event.registerSpriteSet(ModParticles.CESIUM_SHOCKWAVE.get(), CesiumShockwaveParticle.Provider::new);
        event.registerSpriteSet(ModParticles.CESIUM_FIRE.get(), CesiumFireParticle.Provider::new);
        event.registerSpriteSet(ModParticles.CESIUM_LIGHT_SMOKE.get(), CesiumLightSmokeParticle.Provider::new);
    }
}
