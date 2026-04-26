package com.navaronee.meuprimeiromod.client.entity;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.entity.RadioactiveBeeEntity;
import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RadioactiveBeeRenderer extends MobRenderer<RadioactiveBeeEntity, BeeModel<RadioactiveBeeEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MeuPrimeiroMod.MODID, "textures/models/entity/radioative_bee.png");

    public RadioactiveBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new BeeModel<>(context.bakeLayer(ModelLayers.BEE)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(RadioactiveBeeEntity entity) {
        return TEXTURE;
    }
}
