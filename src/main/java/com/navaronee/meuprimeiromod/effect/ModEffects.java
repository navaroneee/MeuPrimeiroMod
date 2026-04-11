package com.navaronee.meuprimeiromod.effect;


import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECT =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MeuPrimeiroMod.MODID);
    public static final RegistryObject<MobEffect> RADIATION = MOB_EFFECT.register("radiation",
            RadiationEffect::new);
    public static void register (IEventBus eventBus){
        MOB_EFFECT.register(eventBus);
    }
}