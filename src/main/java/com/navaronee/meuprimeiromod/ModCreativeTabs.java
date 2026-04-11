package com.navaronee.meuprimeiromod;

import com.navaronee.meuprimeiromod.block.ModBlocks;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MeuPrimeiroMod.MODID);

    public static final RegistryObject<CreativeModeTab> MEU_PRIMEIRO_MOD_TAB = CREATIVE_MODE_TABS.register("meuprimeiromod_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModBlocks.WOOD_CHAIR.get()))
                    .title(Component.translatable("itemGroup.meuprimeiromod"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.WOOD_CHAIR.get());
                        output.accept(ModBlocks.LEAD_ORE.get());
                        output.accept(ModItems.RAW_LEAD.get());
                        output.accept(ModItems.LEAD_INGOT.get());
                        output.accept(ModItems.LEAD_HELMET.get());
                        output.accept(ModItems.LEAD_CHESTPLATE.get());
                        output.accept(ModItems.LEAD_LEGGINGS.get());
                        output.accept(ModItems.LEAD_BOOTS.get());
                        output.accept(ModBlocks.CESIUM_ORE.get());
                        output.accept(ModItems.CESIUM_DUST.get());
                        output.accept(ModItems.GAISER_COUNTER.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}