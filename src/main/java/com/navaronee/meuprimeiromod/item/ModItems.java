package com.navaronee.meuprimeiromod.item;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.entity.ModEntities;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
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

    public static final RegistryObject<Item> CESIUM_DUST = ITEMS.register("cesium_dust",
            () -> new CesiumDustItem(new Item.Properties()));

    public static final RegistryObject<Item> GAISER_COUNTER = ITEMS.register("gaiser_counter",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> REFINED_CESIUM = ITEMS.register("refined_cesium",
            () -> new Item(new Item.Properties()) {
                @Override
                public boolean isFoil(net.minecraft.world.item.ItemStack stack) {
                    return true;
                }
            });

    public static final RegistryObject<Item> CESIUM_GRANADE = ITEMS.register("cesium_granade",
            () -> new CesiumGranadeItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> LEAD_HELMET = ITEMS.register("lead_helmet",
            () -> new LeadArmorItem(ModArmorMaterials.LEAD, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> LEAD_CHESTPLATE = ITEMS.register("lead_chestplate",
            () -> new LeadArmorItem(ModArmorMaterials.LEAD, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> LEAD_LEGGINGS = ITEMS.register("lead_leggings",
            () -> new LeadArmorItem(ModArmorMaterials.LEAD, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> LEAD_BOOTS = ITEMS.register("lead_boots",
            () -> new LeadArmorItem(ModArmorMaterials.LEAD, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<Item> LEAD_SHIELD = ITEMS.register("lead_shield",
            () -> new LeadShieldItem(new Item.Properties().durability(500)));

    public static final RegistryObject<Item> CESIUM_FRAGMENT = ITEMS.register("cesium_fragment",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> AMMO_SLIME = ITEMS.register("ammo_slime",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SLIME_GUN = ITEMS.register("slime_gun",
            () -> new SlimeGunItem(new Item.Properties().stacksTo(1).durability(250)));

    // Knight Armor
    public static final RegistryObject<Item> KNIGHT_HELMET = ITEMS.register("knight_helmet",
            () -> new KnightArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> KNIGHT_CHESTPLATE = ITEMS.register("knight_chestplate",
            () -> new KnightArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> KNIGHT_LEGGINGS = ITEMS.register("knight_leggings",
            () -> new KnightArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> KNIGHT_BOOTS = ITEMS.register("knight_boots",
            () -> new KnightArmorItem(ModArmorMaterials.KNIGHT, ArmorItem.Type.BOOTS, new Item.Properties()));

    // SwordItem signature: (Tier, attackDamageBonus, attackSpeed, Properties)
    // Damage final = tier.getAttackDamageBonus() + bonus + 1 = 5 + 4 + 1 = 10 (vs 8 netherite)
    public static final RegistryObject<Item> KNIGHT_SWORD = ITEMS.register("knight_sword",
            () -> new net.minecraft.world.item.SwordItem(
                    ModTiers.KNIGHT, 4, -2.4F,
                    new Item.Properties()));

    // Portal Creator (EP07)
    public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench",
            () -> new WrenchItem(new Item.Properties().stacksTo(1).durability(250)));

    public static final RegistryObject<Item> ENDER_PEARL_DUST = ITEMS.register("ender_pearl_dust",
            () -> new Item(new Item.Properties()) {
                @Override
                public boolean isFoil(net.minecraft.world.item.ItemStack stack) {
                    return true; // brilho radioativo + ender
                }
            });

    public static final RegistryObject<Item> SHAPE = ITEMS.register("shape",
            () -> new ShapeItem(new Item.Properties().stacksTo(1)));

    // Spawn eggs
    public static final RegistryObject<Item> RADIOACTIVE_BEE_SPAWN_EGG = ITEMS.register("radioactive_bee_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.RADIOACTIVE_BEE, 0x39FF14, 0xFFD700, new Item.Properties()));

    public static final RegistryObject<Item> RADIOACTIVE_SLIME_SPAWN_EGG = ITEMS.register("radioactive_slime_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.RADIOACTIVE_SLIME, 0x39FF14, 0x1E7A0F, new Item.Properties()));

    public static final RegistryObject<Item> MUTANT_SPAWN_EGG = ITEMS.register("mutant_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MUTANT, 0x3A1F0D, 0x39FF14, new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
