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

    public static final RegistryObject<EntityType<RadioactiveBeeEntity>> RADIOACTIVE_BEE =
            ENTITY_TYPES.register("radioactive_bee",
                    () -> EntityType.Builder.<RadioactiveBeeEntity>of(RadioactiveBeeEntity::new, MobCategory.MONSTER)
                            .sized(0.7F, 0.6F)
                            .clientTrackingRange(8)
                            .build("radioactive_bee"));

    public static final RegistryObject<EntityType<RadioactiveSlimeEntity>> RADIOACTIVE_SLIME =
            ENTITY_TYPES.register("radioactive_slime",
                    () -> EntityType.Builder.<RadioactiveSlimeEntity>of(RadioactiveSlimeEntity::new, MobCategory.MONSTER)
                            .sized(2.04F, 2.04F)
                            .clientTrackingRange(10)
                            .updateInterval(3)
                            .build("radioactive_slime"));

    public static final RegistryObject<EntityType<MutantEntity>> MUTANT =
            ENTITY_TYPES.register("mutant",
                    () -> EntityType.Builder.of(MutantEntity::new, MobCategory.MONSTER)
                            .sized(3.2F, 6.8F) // 2x do BB original (1.6 × 3.4) pra casar visual 2x
                            .clientTrackingRange(10)
                            .build("mutant"));

    public static final RegistryObject<EntityType<MutantTntProjectileEntity>> MUTANT_TNT_PROJECTILE =
            ENTITY_TYPES.register("mutant_tnt_projectile",
                    () -> EntityType.Builder.<MutantTntProjectileEntity>of(MutantTntProjectileEntity::new, MobCategory.MISC)
                            .sized(0.6F, 0.6F)
                            .clientTrackingRange(8)
                            .updateInterval(5)
                            .build("mutant_tnt_projectile"));

    public static final RegistryObject<EntityType<SlimeShotEntity>> SLIME_SHOT =
            ENTITY_TYPES.register("slime_shot",
                    () -> EntityType.Builder.<SlimeShotEntity>of(SlimeShotEntity::new, MobCategory.MISC)
                            .sized(0.4F, 0.4F)
                            .clientTrackingRange(8)
                            .updateInterval(2)
                            .build("slime_shot"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
