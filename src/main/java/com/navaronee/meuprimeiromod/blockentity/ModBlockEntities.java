package com.navaronee.meuprimeiromod.blockentity;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MeuPrimeiroMod.MODID);

    public static final RegistryObject<BlockEntityType<CesiumRefinerBlockEntity>> CESIUM_REFINER =
            BLOCK_ENTITIES.register("cesium_refiner",
                    () -> BlockEntityType.Builder.of(CesiumRefinerBlockEntity::new,
                            ModBlocks.CESIUM_REFINER.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
