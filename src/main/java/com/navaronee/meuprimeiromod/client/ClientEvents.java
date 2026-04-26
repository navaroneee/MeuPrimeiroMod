package com.navaronee.meuprimeiromod.client;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.client.entity.AtomicCloudRenderer;
import com.navaronee.meuprimeiromod.client.entity.CesiumGranadeRenderer;
import com.navaronee.meuprimeiromod.client.entity.CesiumNukePrimedRenderer;
import com.navaronee.meuprimeiromod.client.entity.MutantRenderer;
import com.navaronee.meuprimeiromod.client.entity.MutantTntProjectileRenderer;
import com.navaronee.meuprimeiromod.client.entity.RadioactiveBeeRenderer;
import com.navaronee.meuprimeiromod.client.entity.RadioactiveSlimeRenderer;
import com.navaronee.meuprimeiromod.client.entity.SlimeShotRenderer;
import com.navaronee.meuprimeiromod.client.model.MutantModel;
import com.navaronee.meuprimeiromod.block.ModBlocks;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
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
        event.registerLayerDefinition(MutantModel.LAYER_LOCATION, MutantModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.CESIUM_REFINER.get(), CesiumRefinerScreen::new);
            // Cesium dust block: cutout pra textura com transparência
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CESIUM_DUST_BLOCK.get(), RenderType.cutout());
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CESIUM_GRANADE.get(), CesiumGranadeRenderer::new);
        event.registerEntityRenderer(ModEntities.CESIUM_NUKE_PRIMED.get(), CesiumNukePrimedRenderer::new);
        event.registerEntityRenderer(ModEntities.ATOMIC_CLOUD.get(), AtomicCloudRenderer::new);
        event.registerEntityRenderer(ModEntities.RADIOACTIVE_BEE.get(), RadioactiveBeeRenderer::new);
        event.registerEntityRenderer(ModEntities.RADIOACTIVE_SLIME.get(), RadioactiveSlimeRenderer::new);
        event.registerEntityRenderer(ModEntities.MUTANT.get(), MutantRenderer::new);
        event.registerEntityRenderer(ModEntities.MUTANT_TNT_PROJECTILE.get(), MutantTntProjectileRenderer::new);
        event.registerEntityRenderer(ModEntities.SLIME_SHOT.get(), SlimeShotRenderer::new);
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
