package com.navaronee.meuprimeiromod.item;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MeuPrimeiroMod.MODID);

    public static final RegistryObject<Item> RAW_LEAD = ITEMS.register("raw_lead",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> LEAD_INGOT = ITEMS.register("lead_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> LEAD_HELMET = ITEMS.register("lead_helmet",
            () -> new ArmorItem(ModArmorMaterials.LEAD, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> LEAD_CHESTPLATE = ITEMS.register("lead_chestplate",
            () -> new ArmorItem(ModArmorMaterials.LEAD, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> LEAD_LEGGINGS = ITEMS.register("lead_leggings",
            () -> new ArmorItem(ModArmorMaterials.LEAD, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> LEAD_BOOTS = ITEMS.register("lead_boots",
            () -> new ArmorItem(ModArmorMaterials.LEAD, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
