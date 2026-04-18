package com.navaronee.meuprimeiromod.entity;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MeuPrimeiroMod.MODID);

    public static final RegistryObject<EntityType<CesiumGranadeEntity>> CESIUM_GRANADE =
            ENTITY_TYPES.register("cesium_granade",
                    () -> EntityType.Builder.<CesiumGranadeEntity>of(CesiumGranadeEntity::new, MobCategory.MISC)
                            .sized(0.35F, 0.35F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("cesium_granade"));

    public static final RegistryObject<EntityType<CesiumNukePrimedEntity>> CESIUM_NUKE_PRIMED =
            ENTITY_TYPES.register("cesium_nuke_primed",
                    () -> EntityType.Builder.<CesiumNukePrimedEntity>of(CesiumNukePrimedEntity::new, MobCategory.MISC)
                            .fireImmune()
                            .sized(0.98F, 0.98F)
                            .clientTrackingRange(10)
                            .updateInterval(10)
                            .build("cesium_nuke_primed"));

    public static final RegistryObject<EntityType<AtomicCloudEntity>> ATOMIC_CLOUD =
            ENTITY_TYPES.register("atomic_cloud",
                    () -> EntityType.Builder.<AtomicCloudEntity>of(AtomicCloudEntity::new, MobCategory.MISC)
                            .fireImmune()
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(32)
                            .updateInterval(20)
                            .noSummon()
                            .build("atomic_cloud"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
