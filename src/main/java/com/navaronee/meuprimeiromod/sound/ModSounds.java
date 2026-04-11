package com.navaronee.meuprimeiromod.sound;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MeuPrimeiroMod.MODID);

    public static final RegistryObject<SoundEvent> COUNTER_LV1 = registerSoundEvent("counter_lv1");
    public static final RegistryObject<SoundEvent> COUNTER_LV2 = registerSoundEvent("counter_lv2");
    public static final RegistryObject<SoundEvent> COUNTER_LV3 = registerSoundEvent("counter_lv3");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
                new ResourceLocation(MeuPrimeiroMod.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}