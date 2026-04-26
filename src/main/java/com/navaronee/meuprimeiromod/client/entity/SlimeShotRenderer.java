package com.navaronee.meuprimeiromod.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.navaronee.meuprimeiromod.entity.SlimeShotEntity;
import com.navaronee.meuprimeiromod.item.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Renderiza o SlimeShot como uma billboard do item ammo_slime — visual coeso
 * com a munição, sem precisar criar textura/modelo separado.
 */
public class SlimeShotRenderer extends EntityRenderer<SlimeShotEntity> {

    private final ItemRenderer itemRenderer;

    public SlimeShotRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SlimeShotEntity entity, float entityYaw, float partialTicks,
                       PoseStack pose, MultiBufferSource buffer, int packedLight) {
        pose.pushPose();
        pose.scale(0.6F, 0.6F, 0.6F);
        pose.mulPose(this.entityRenderDispatcher.cameraOrientation());
        itemRenderer.renderStatic(
                new ItemStack(ModItems.AMMO_SLIME.get()),
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                pose, buffer,
                entity.level(), 0);
        pose.popPose();
        super.render(entity, entityYaw, partialTicks, pose, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SlimeShotEntity entity) {
        return net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;
    }
}
