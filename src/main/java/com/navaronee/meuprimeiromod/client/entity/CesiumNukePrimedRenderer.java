package com.navaronee.meuprimeiromod.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.navaronee.meuprimeiromod.block.ModBlocks;
import com.navaronee.meuprimeiromod.entity.CesiumNukePrimedEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.PrimedTnt;

public class CesiumNukePrimedRenderer extends EntityRenderer<CesiumNukePrimedEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/tnt.png");
    private final net.minecraft.client.renderer.block.BlockRenderDispatcher blockRenderer;

    public CesiumNukePrimedRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(CesiumNukePrimedEntity entity, float entityYaw, float partialTick, PoseStack pose,
                       MultiBufferSource buffer, int packedLight) {
        pose.pushPose();
        pose.translate(0.0F, 0.5F, 0.0F);
        int fuse = entity.getFuse();
        if ((float) fuse - partialTick + 1.0F < 10.0F) {
            float f = 1.0F - ((float) fuse - partialTick + 1.0F) / 10.0F;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f *= f;
            f *= f;
            float scale = 1.0F + f * 0.3F;
            pose.scale(scale, scale, scale);
        }
        pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90.0F));
        pose.translate(-0.5F, -0.5F, 0.5F);
        pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(90.0F));
        TntMinecartRenderer.renderWhiteSolidBlock(blockRenderer, ModBlocks.CESIUM_NUKE.get().defaultBlockState(),
                pose, buffer, packedLight, fuse / 5 % 2 == 0);
        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(CesiumNukePrimedEntity entity) {
        return TEXTURE;
    }
}
