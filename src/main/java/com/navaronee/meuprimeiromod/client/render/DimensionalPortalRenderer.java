package com.navaronee.meuprimeiromod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.navaronee.meuprimeiromod.blockentity.DimensionalPortalBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

/**
 * Renderer do portal usando o shader vanilla do End Portal (RenderType.endPortal()).
 * Desenha 2 quads perpendiculares ao FACING do master — ficam expostos nas 2 faces
 * do bloco que ficam ao longo do eixo do facing (efeito "vertical wall portal").
 */
public class DimensionalPortalRenderer implements BlockEntityRenderer<DimensionalPortalBlockEntity> {

    public DimensionalPortalRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(DimensionalPortalBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buf, int light, int overlay) {
        Direction facing = be.getFacing();
        if (facing == null) return;

        VertexConsumer vc = buf.getBuffer(RenderType.endPortal());
        Matrix4f m = pose.last().pose();
        float off = 0.005f;

        switch (facing.getAxis()) {
            case Z -> {
                // Face NORTH (z=-off) e SOUTH (z=1+off)
                quad(vc, m,
                        0, 0, -off,
                        0, 1, -off,
                        1, 1, -off,
                        1, 0, -off);
                quad(vc, m,
                        1, 0, 1 + off,
                        1, 1, 1 + off,
                        0, 1, 1 + off,
                        0, 0, 1 + off);
            }
            case X -> {
                // Face WEST (x=-off) e EAST (x=1+off)
                quad(vc, m,
                        -off, 0, 1,
                        -off, 1, 1,
                        -off, 1, 0,
                        -off, 0, 0);
                quad(vc, m,
                        1 + off, 0, 0,
                        1 + off, 1, 0,
                        1 + off, 1, 1,
                        1 + off, 0, 1);
            }
            default -> {}
        }
    }

    private void quad(VertexConsumer vc, Matrix4f m,
                      float x1, float y1, float z1,
                      float x2, float y2, float z2,
                      float x3, float y3, float z3,
                      float x4, float y4, float z4) {
        vc.vertex(m, x1, y1, z1).endVertex();
        vc.vertex(m, x2, y2, z2).endVertex();
        vc.vertex(m, x3, y3, z3).endVertex();
        vc.vertex(m, x4, y4, z4).endVertex();
    }
}
