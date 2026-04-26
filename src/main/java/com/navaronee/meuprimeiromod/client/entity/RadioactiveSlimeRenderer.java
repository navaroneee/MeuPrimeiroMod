package com.navaronee.meuprimeiromod.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.entity.RadioactiveSlimeEntity;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;

public class RadioactiveSlimeRenderer extends MobRenderer<RadioactiveSlimeEntity, SlimeModel<RadioactiveSlimeEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MeuPrimeiroMod.MODID, "textures/models/entity/radioative_slime.png");

    public RadioactiveSlimeRenderer(EntityRendererProvider.Context context) {
        super(context, new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME)), 0.25F);
        // Camada externa translúcida (slime "gosma" por fora)
        this.addLayer(new SlimeOuterLayer<>(this, context.getModelSet()));
    }

    @Override
    public void render(RadioactiveSlimeEntity entity, float entityYaw, float partialTicks,
                       PoseStack pose, net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight) {
        // Escala o render pelo tamanho do slime (igual vanilla)
        this.shadowRadius = 0.25F * entity.getSize();
        super.render(entity, entityYaw, partialTicks, pose, buffer, packedLight);
    }

    @Override
    protected void scale(RadioactiveSlimeEntity entity, PoseStack pose, float partialTicks) {
        float scale = 0.999F;
        pose.scale(scale, scale, scale);
        pose.translate(0.0F, 0.001F, 0.0F);
        float size = entity.getSize();
        float squish = net.minecraft.util.Mth.lerp(partialTicks, entity.oSquish, entity.squish) / (size * 0.5F + 1.0F);
        float xz = 1.0F / (squish + 1.0F);
        pose.scale(xz * size, 1.0F / xz * size, xz * size);
    }

    @Override
    public ResourceLocation getTextureLocation(RadioactiveSlimeEntity entity) {
        return TEXTURE;
    }
}
