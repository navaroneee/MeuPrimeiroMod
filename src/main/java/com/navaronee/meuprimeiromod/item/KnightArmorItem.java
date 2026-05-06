package com.navaronee.meuprimeiromod.item;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.client.model.KnightArmorModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class KnightArmorItem extends ArmorItem {

    public KnightArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private KnightArmorModel<?> mainModel;
            private KnightArmorModel<?> leggingsModel;
            private KnightArmorModel<?> bootsModel;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack,
                                                                     EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (equipmentSlot == EquipmentSlot.FEET) {
                    if (bootsModel == null) {
                        bootsModel = new KnightArmorModel<>(Minecraft.getInstance().getEntityModels()
                                .bakeLayer(KnightArmorModel.LAYER_LOCATION_BOOTS));
                    }
                    return bootsModel;
                }
                if (equipmentSlot == EquipmentSlot.LEGS) {
                    if (leggingsModel == null) {
                        leggingsModel = new KnightArmorModel<>(Minecraft.getInstance().getEntityModels()
                                .bakeLayer(KnightArmorModel.LAYER_LOCATION_LEGGINGS));
                    }
                    return leggingsModel;
                }
                if (mainModel == null) {
                    mainModel = new KnightArmorModel<>(Minecraft.getInstance().getEntityModels()
                            .bakeLayer(KnightArmorModel.LAYER_LOCATION));
                }
                // Atualiza visibilidade do sword/bainha bones com base no inventário/mão
                // do entity. Roda toda vez que o chestplate é renderizado.
                mainModel.updateSwordVisibility(livingEntity);
                return mainModel;
            }
        });
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, @Nullable String type) {
        return MeuPrimeiroMod.MODID + ":textures/models/armor/knight_armor.png";
    }
}
