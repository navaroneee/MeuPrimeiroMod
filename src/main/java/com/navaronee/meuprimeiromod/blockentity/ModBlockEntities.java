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

    public static final RegistryObject<BlockEntityType<PortalCreatorBlockEntity>> PORTAL_CREATOR =
            BLOCK_ENTITIES.register("portal_creator",
                    () -> BlockEntityType.Builder.of(PortalCreatorBlockEntity::new,
                            ModBlocks.PORTAL_CREATOR.get()).build(null));

    public static final RegistryObject<BlockEntityType<MultiblockPortBlockEntity>> PORTAL_ENERGY_PORT =
            BLOCK_ENTITIES.register("portal_energy_port",
                    () -> BlockEntityType.Builder.of(MultiblockPortBlockEntity::energy,
                            ModBlocks.PORTAL_ENERGY_PORT.get()).build(null));

    public static final RegistryObject<BlockEntityType<MultiblockPortBlockEntity>> PORTAL_ITEM_PORT =
            BLOCK_ENTITIES.register("portal_item_port",
                    () -> BlockEntityType.Builder.of(MultiblockPortBlockEntity::item,
                            ModBlocks.PORTAL_ITEM_PORT.get()).build(null));

    public static final RegistryObject<BlockEntityType<MultiblockFillerBlockEntity>> MULTIBLOCK_FILLER =
            BLOCK_ENTITIES.register("multiblock_filler",
                    () -> BlockEntityType.Builder.of(MultiblockFillerBlockEntity::new,
                            ModBlocks.MULTIBLOCK_FILLER.get()).build(null));

    public static final RegistryObject<BlockEntityType<CreativeEnergyBlockEntity>> CREATIVE_ENERGY =
            BLOCK_ENTITIES.register("creative_energy",
                    () -> BlockEntityType.Builder.of(CreativeEnergyBlockEntity::new,
                            ModBlocks.CREATIVE_ENERGY.get()).build(null));

    public static final RegistryObject<BlockEntityType<DimensionalPortalBlockEntity>> DIMENSIONAL_PORTAL =
            BLOCK_ENTITIES.register("dimensional_portal",
                    () -> BlockEntityType.Builder.of(DimensionalPortalBlockEntity::new,
                            ModBlocks.DIMENSIONAL_PORTAL.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
