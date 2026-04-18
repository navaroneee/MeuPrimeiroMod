package com.navaronee.meuprimeiromod.particle;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MeuPrimeiroMod.MODID);

    public static final RegistryObject<SimpleParticleType> CESIUM_ATOM_CORE =
            PARTICLES.register("cesium_atom_core", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> CESIUM_ATOM_RING =
            PARTICLES.register("cesium_atom_ring", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> CESIUM_ATOM_FOG =
            PARTICLES.register("cesium_atom_fog", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> CESIUM_SHOCKWAVE =
            PARTICLES.register("cesium_shockwave", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> CESIUM_FIRE =
            PARTICLES.register("cesium_fire", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> CESIUM_LIGHT_SMOKE =
            PARTICLES.register("cesium_light_smoke", () -> new SimpleParticleType(false));

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}
