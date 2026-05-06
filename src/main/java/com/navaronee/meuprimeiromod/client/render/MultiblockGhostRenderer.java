package com.navaronee.meuprimeiromod.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.blockentity.portal.IMultiblockPreviewable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.List;

/**
 * Renderer client-side de ghost blocks pra preview da estrutura do multiblock.
 *
 * Activation: shift+right-click no controller toggla on/off. Estado client-side estatico
 * persiste mesmo olhando pra outro lado. Auto-disable se controller deixar de existir
 * ou virar formed (na visualização, fica menos relevante mas continua mostrando wall).
 *
 * Cores (RGBA):
 *  - WHITE (controller): branco translucido
 *  - GREEN (correto): bloco esperado já no lugar
 *  - RED (faltando): casing/frame faltando
 *  - BLUE (io_port): port slot vazio/errado
 *  - YELLOW (wall guide): posição da parede sugerida (sem bloco solido)
 *  - CYAN (wall guide ok): parede com bloco sólido no lugar
 */
@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MultiblockGhostRenderer {

    private static final float[] CORRECT       = {0.0f, 0.8f, 0.0f, 0.35f};
    private static final float[] MISSING       = {0.9f, 0.1f, 0.1f, 0.45f};
    private static final float[] IO_PORT       = {0.2f, 0.4f, 1.0f, 0.45f};
    private static final float[] CONTROLLER    = {1.0f, 1.0f, 1.0f, 0.25f};

    private static final double INSET = 0.005;

    private static BlockPos activeControllerPos;

    /** Toggle on/off. Chamado pelo PortalCreatorBlock.use() quando shift+rclick. */
    public static boolean togglePreview(BlockPos pos) {
        if (activeControllerPos != null && activeControllerPos.equals(pos)) {
            disable();
            return false;
        }
        activeControllerPos = pos.immutable();
        return true;
    }

    private static void disable() {
        activeControllerPos = null;
    }

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        if (activeControllerPos == null) return;

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if (level == null) { disable(); return; }

        BlockEntity be = level.getBlockEntity(activeControllerPos);
        if (!(be instanceof IMultiblockPreviewable controller)) { disable(); return; }

        Direction facing = controller.getFacing();
        // Recomputa entries todo frame — barato (~50 entries) e garante que a wall
        // preview move pra posição da wall REAL conforme o player constrói.
        List<IMultiblockPreviewable.StructureEntry> entries =
                controller.getPreviewPositions(activeControllerPos, facing);
        if (entries == null || entries.isEmpty()) return;

        Vec3 cam = event.getCamera().getPosition();
        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        pose.translate(-cam.x, -cam.y, -cam.z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder bb = tess.getBuilder();
        bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Matrix4f mat = pose.last().pose();
        for (IMultiblockPreviewable.StructureEntry e : entries) {
            float[] color = colorFor(level, e);
            renderCube(bb, mat, e.position(), color);
        }

        tess.end();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        pose.popPose();
    }

    private static float[] colorFor(Level level, IMultiblockPreviewable.StructureEntry e) {
        BlockState bs = level.getBlockState(e.position());
        return switch (e.type()) {
            case CONTROLLER -> CONTROLLER;
            case WALL_GUIDE ->
                    (!bs.isAir() && bs.isSolidRender(level, e.position())) ? CORRECT : MISSING;
            case IO_PORT ->
                    (e.expected() != null && bs.is(e.expected())) ? CORRECT : IO_PORT;
            case CASING ->
                    (e.expected() != null && bs.is(e.expected())) ? CORRECT : MISSING;
        };
    }

    private static void renderCube(BufferBuilder bb, Matrix4f m, BlockPos pos, float[] c) {
        float x0 = (float) (pos.getX() + INSET);
        float y0 = (float) (pos.getY() + INSET);
        float z0 = (float) (pos.getZ() + INSET);
        float x1 = (float) (pos.getX() + 1 - INSET);
        float y1 = (float) (pos.getY() + 1 - INSET);
        float z1 = (float) (pos.getZ() + 1 - INSET);
        float r = c[0], g = c[1], b = c[2], a = c[3];
        // 6 faces (24 vértices)
        bb.vertex(m, x0, y0, z0).color(r, g, b, a).endVertex();
        bb.vertex(m, x1, y0, z0).color(r, g, b, a).endVertex();
        bb.vertex(m, x1, y0, z1).color(r, g, b, a).endVertex();
        bb.vertex(m, x0, y0, z1).color(r, g, b, a).endVertex();

        bb.vertex(m, x0, y1, z1).color(r, g, b, a).endVertex();
        bb.vertex(m, x1, y1, z1).color(r, g, b, a).endVertex();
        bb.vertex(m, x1, y1, z0).color(r, g, b, a).endVertex();
        bb.vertex(m, x0, y1, z0).color(r, g, b, a).endVertex();

        bb.vertex(m, x0, y0, z0).color(r, g, b, a).endVertex();
        bb.vertex(m, x0, y1, z0).color(r, g, b, a).endVertex();
        bb.vertex(m, x1, y1, z0).color(r, g, b, a).endVertex();
        bb.vertex(m, x1, y0, z0).color(r, g, b, a).endVertex();

        bb.vertex(m, x1, y0, z1).color(r, g, b, a).endVertex();
        bb.vertex(m, x1, y1, z1).color(r, g, b, a).endVertex();
        bb.vertex(m, x0, y1, z1).color(r, g, b, a).endVertex();
        bb.vertex(m, x0, y0, z1).color(r, g, b, a).endVertex();

        bb.vertex(m, x0, y0, z1).color(r, g, b, a).endVertex();
        bb.vertex(m, x0, y1, z1).color(r, g, b, a).endVertex();
        bb.vertex(m, x0, y1, z0).color(r, g, b, a).endVertex();
        bb.vertex(m, x0, y0, z0).color(r, g, b, a).endVertex();

        bb.vertex(m, x1, y0, z0).color(r, g, b, a).endVertex();
        bb.vertex(m, x1, y1, z0).color(r, g, b, a).endVertex();
        bb.vertex(m, x1, y1, z1).color(r, g, b, a).endVertex();
        bb.vertex(m, x1, y0, z1).color(r, g, b, a).endVertex();
    }
}
