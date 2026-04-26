package com.navaronee.meuprimeiromod.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.navaronee.meuprimeiromod.entity.MutantTntProjectileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import com.mojang.math.Axis;

/**
 * Renderiza o projétil TNT como um bloco de TNT girando, pra deixar claro
 * visualmente que é rebatível (não é fireball).
 */
public class MutantTntProjectileRenderer extends EntityRenderer<MutantTntProjectileEntity> {

    private final BlockRenderDispatcher blockRenderer;

    public MutantTntProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(MutantTntProjectileEntity entity, float entityYaw, float partialTicks,
                       PoseStack pose, MultiBufferSource buffer, int packedLight) {
        pose.pushPose();
        pose.translate(0.0F, 0.25F, 0.0F);
        pose.scale(0.5F, 0.5F, 0.5F); // tamanho reduzido 50%
        float spin = (entity.tickCount + partialTicks) * 12.0F;
        pose.mulPose(Axis.YP.rotationDegrees(spin));
        pose.translate(-0.5F, -0.5F, -0.5F);
        TntMinecartRenderer.renderWhiteSolidBlock(
                this.blockRenderer, Blocks.TNT.defaultBlockState(),
                pose, buffer, packedLight, false);
        pose.popPose();
        super.render(entity, entityYaw, partialTicks, pose, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MutantTntProjectileEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
