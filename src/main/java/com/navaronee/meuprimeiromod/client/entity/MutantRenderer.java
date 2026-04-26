package com.navaronee.meuprimeiromod.client.entity;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.client.model.MutantModel;
import com.navaronee.meuprimeiromod.entity.MutantEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MutantRenderer extends MobRenderer<MutantEntity, MutantModel> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MeuPrimeiroMod.MODID, "textures/models/entity/bichao.png");

    public MutantRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantModel(context.bakeLayer(MutantModel.LAYER_LOCATION)), 1.6F);
    }

    @Override
    protected void scale(MutantEntity entity, com.mojang.blaze3d.vertex.PoseStack pose, float partialTicks) {
        pose.scale(2.0F, 2.0F, 2.0F); // visual 2x pra casar com bounding box ampliado
    }

    @Override
    public ResourceLocation getTextureLocation(MutantEntity entity) {
        return TEXTURE;
    }
}
