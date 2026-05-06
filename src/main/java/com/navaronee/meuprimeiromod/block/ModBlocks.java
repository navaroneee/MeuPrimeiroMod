package com.navaronee.meuprimeiromod.block;


import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MeuPrimeiroMod.MODID);

    public static final RegistryObject<Block> WOOD_CHAIR = registerBlock("wood_chair",
            () -> new HorizontalFacingBlock(BlockBehaviour.Properties.of()
                    .strength(2f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));

    public static final RegistryObject<Block> LEAD_ORE = registerBlock("lead_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Block> CESIUM_ORE = registerBlock("cesium_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(3f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 7)));

    public static final RegistryObject<Block> CESIUM_REFINER = registerBlock("cesium_refiner",
            () -> new CesiumRefinerBlock(BlockBehaviour.Properties.of()
                    .strength(4f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    public static final RegistryObject<Block> CESIUM_NUKE = registerBlock("cesium_nuke",
            () -> new CesiumNukeBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.TNT)));

    public static final RegistryObject<Block> PORTAL_CREATOR = registerBlock("portal_creator",
            () -> new PortalCreatorBlock(BlockBehaviour.Properties.of()
                    .strength(5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .noOcclusion()
                    .lightLevel(state -> state.getValue(PortalCreatorBlock.FORMED) ? 12 : 0)));

    public static final RegistryObject<Block> ENGINEERING_FRAME = registerBlock("engineering_frame",
            () -> new EngineeringFrameBlock(BlockBehaviour.Properties.of()
                    .strength(5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> PORTAL_ENERGY_PORT = registerBlock("portal_energy_port",
            () -> new PortalEnergyPortBlock(BlockBehaviour.Properties.of()
                    .strength(5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> PORTAL_ITEM_PORT = registerBlock("portal_item_port",
            () -> new PortalItemPortBlock(BlockBehaviour.Properties.of()
                    .strength(5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final RegistryObject<Block> CREATIVE_ENERGY = registerBlock("creative_energy",
            () -> new CreativeEnergyBlock(BlockBehaviour.Properties.of()
                    .strength(-1f, 3600000f)
                    .sound(SoundType.METAL)
                    .lightLevel(s -> 15)));

    // Filler invisível (não vai pra creative tab — sem BlockItem)
    public static final RegistryObject<Block> MULTIBLOCK_FILLER = BLOCKS.register("multiblock_filler",
            () -> new MultiblockFillerBlock(BlockBehaviour.Properties.of()
                    .strength(5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    // Portal aberto — só placado pelo controller, não tem BlockItem
    public static final RegistryObject<Block> DIMENSIONAL_PORTAL = BLOCKS.register("dimensional_portal",
            () -> new DimensionalPortalBlock(BlockBehaviour.Properties.of()
                    .strength(-1f, 3600000f)
                    .noOcclusion()
                    .noLootTable()
                    .lightLevel(s -> 12)
                    .sound(SoundType.GLASS)));

    // Sem BlockItem auto — placado pelo item cesium_dust via useOn
    public static final RegistryObject<Block> CESIUM_DUST_BLOCK = BLOCKS.register("cesium_dust_block",
            () -> new CesiumDustBlock(BlockBehaviour.Properties.of()
                    .strength(0.1f)
                    .sound(SoundType.SAND)
                    .noCollission()
                    .noOcclusion()
                    .lightLevel(state -> 10)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}