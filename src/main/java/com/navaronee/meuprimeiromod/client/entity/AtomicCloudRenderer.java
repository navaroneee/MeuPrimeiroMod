package com.navaronee.meuprimeiromod.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.navaronee.meuprimeiromod.entity.AtomicCloudEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class AtomicCloudRenderer extends EntityRenderer<AtomicCloudEntity> {

    public AtomicCloudRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(AtomicCloudEntity entity, float yaw, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int packedLight) {
        // Entidade invisível; só serve pra orquestrar partículas via server tick.
    }

    @Override
    public ResourceLocation getTextureLocation(AtomicCloudEntity entity) {
        return new ResourceLocation("textures/entity/experience_orb.png");
    }

    @Override
    public boolean shouldRender(AtomicCloudEntity entity, net.minecraft.client.renderer.culling.Frustum frustum, double x, double y, double z) {
        return false;
    }
}
