package com.navaronee.meuprimeiromod;

import com.mojang.logging.LogUtils;
import com.navaronee.meuprimeiromod.block.ModBlocks;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MeuPrimeiroMod.MODID)
public class MeuPrimeiroMod {

    public static final String MODID = "meuprimeiromod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MeuPrimeiroMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Meu Primeiro Mod carregado com sucesso!");
    }
}
