package com.navaronee.meuprimeiromod.menu;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MeuPrimeiroMod.MODID);

    public static final RegistryObject<MenuType<CesiumRefinerMenu>> CESIUM_REFINER =
            MENUS.register("cesium_refiner",
                    () -> IForgeMenuType.create(CesiumRefinerMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
