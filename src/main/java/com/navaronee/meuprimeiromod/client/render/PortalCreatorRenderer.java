package com.navaronee.meuprimeiromod.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.navaronee.meuprimeiromod.block.PortalCreatorBlock;
import com.navaronee.meuprimeiromod.blockentity.PortalCreatorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

/**
 * Renderer da animação de firing do Portal Creator.
 * Fases derivadas de (gameTime - firingStartGameTime), com FIRING_DURATION=200 ticks total:
 *   0-60   (3s)  CHARGING  — bola de energia cresce no centro do controller
 *   60-100 (2s)  LASER     — beam dispara pra frente, bola encolhe
 *   100-160 (3s) HOLD      — beam segura no alvo, pulsando
 *   160-200 (2s) SPLIT     — beam abre em 4 pontos (efeito portal abrindo)
 *
 * Usa RenderType.lightning() (additive blend, unlit) pra dar feeling de glow.
 */
public class PortalCreatorRenderer implements BlockEntityRenderer<PortalCreatorBlockEntity> {

    private static final float CHARGE_END = 60f;
    private static final float LASER_END = 100f;
    private static final float HOLD_END = 160f;
    private static final float SPLIT_END = 200f;

    private static final float BEAM_R = 0.20f, BEAM_G = 0.95f, BEAM_B = 0.55f;
    private static final float CORE_R = 0.55f, CORE_G = 1.00f, CORE_B = 0.80f;

    public PortalCreatorRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(PortalCreatorBlockEntity be, float partialTick, PoseStack pose,
                       MultiBufferSource buf, int light, int overlay) {
        if (!be.isFiring() || be.getLevel() == null) return;

        long start = be.getFiringStartGameTime();
        long now = be.getLevel().getGameTime();
        float elapsed = (now - start) + partialTick;
        if (elapsed < 0 || elapsed > SPLIT_END) return;

        Direction facing = be.getBlockState().getValue(PortalCreatorBlock.FACING);
        // 1:1 com Navarone: laser fires BACKWARD (facing.getOpposite()) — mesma direção
        // que findWall busca a parede e onde o portal forma.
        Vec3 emitter = new Vec3(0.5, 0.5, 0.5);
        Vec3 frontDir = Vec3.atLowerCornerOf(facing.getOpposite().getNormal());
        Vec3 target = emitter.add(frontDir.scale(4.0));

        pose.pushPose();
        Matrix4f mat = pose.last().pose();

        if (elapsed < CHARGE_END) {
            float progress = elapsed / CHARGE_END;
            float pulse = 1f + 0.06f * (float) Math.sin(elapsed * 0.4);
            float radius = (0.06f + progress * 0.28f) * pulse;
            renderBall(buf, mat, emitter, radius, 0.5f + 0.5f * progress);
        } else if (elapsed < LASER_END) {
            float p = (elapsed - CHARGE_END) / (LASER_END - CHARGE_END);
            float ball = 0.3f * (1f - p);
            if (ball > 0.02f) renderBall(buf, mat, emitter, ball, 1f - p);
            Vec3 tip = emitter.add(target.subtract(emitter).scale(p));
            renderBeam(buf, mat, emitter, tip, 0.13f, 0.85f, true);
        } else if (elapsed < HOLD_END) {
            float pulse = 0.9f + 0.1f * (float) Math.sin(elapsed * 0.2);
            renderBeam(buf, mat, emitter, target, 0.13f * pulse, 0.9f, true);
        } else {
            // SPLIT: 4 beams abrem do alvo central pra 4 cantos do "portal"
            float p = (elapsed - HOLD_END) / (SPLIT_END - HOLD_END);
            float pulse = 0.9f + 0.1f * (float) Math.sin(elapsed * 0.25);
            Vec3 right = Vec3.atLowerCornerOf(facing.getCounterClockWise().getNormal());
            Vec3 up = new Vec3(0, 1, 0);
            float spread = 1.5f * p;
            Vec3[] corners = new Vec3[]{
                    target.add(right.scale(-spread)).add(up.scale(-spread)),
                    target.add(right.scale(+spread)).add(up.scale(-spread)),
                    target.add(right.scale(+spread)).add(up.scale(+spread)),
                    target.add(right.scale(-spread)).add(up.scale(+spread))
            };
            for (Vec3 c : corners) renderBeam(buf, mat, emitter, c, 0.10f * pulse, 0.85f, true);
        }

        pose.popPose();
    }

    /** "Bola" feita como cubo — additive blending faz parecer um glow esférico. */
    private void renderBall(MultiBufferSource buf, Matrix4f mat, Vec3 c, float r, float a) {
        VertexConsumer vc = buf.getBuffer(RenderType.lightning());
        float cx = (float) c.x, cy = (float) c.y, cz = (float) c.z;
        // outer glow
        addCube(vc, mat, cx, cy, cz, r * 1.4f, BEAM_R, BEAM_G, BEAM_B, a * 0.4f);
        // inner core
        addCube(vc, mat, cx, cy, cz, r, CORE_R, CORE_G, CORE_B, a * 0.85f);
        // hot center
        addCube(vc, mat, cx, cy, cz, r * 0.5f, 0.95f, 1f, 0.95f, a);
    }

    private void addCube(VertexConsumer vc, Matrix4f mat,
                         float cx, float cy, float cz, float h,
                         float r, float g, float b, float a) {
        // 6 quads (24 verts no QUADS mode da lightning RT)
        // Top (Y+)
        v(vc, mat, cx-h, cy+h, cz-h, r, g, b, a);
        v(vc, mat, cx-h, cy+h, cz+h, r, g, b, a);
        v(vc, mat, cx+h, cy+h, cz+h, r, g, b, a);
        v(vc, mat, cx+h, cy+h, cz-h, r, g, b, a);
        // Bottom (Y-)
        v(vc, mat, cx-h, cy-h, cz+h, r, g, b, a);
        v(vc, mat, cx-h, cy-h, cz-h, r, g, b, a);
        v(vc, mat, cx+h, cy-h, cz-h, r, g, b, a);
        v(vc, mat, cx+h, cy-h, cz+h, r, g, b, a);
        // North (Z-)
        v(vc, mat, cx+h, cy-h, cz-h, r, g, b, a);
        v(vc, mat, cx-h, cy-h, cz-h, r, g, b, a);
        v(vc, mat, cx-h, cy+h, cz-h, r, g, b, a);
        v(vc, mat, cx+h, cy+h, cz-h, r, g, b, a);
        // South (Z+)
        v(vc, mat, cx-h, cy-h, cz+h, r, g, b, a);
        v(vc, mat, cx+h, cy-h, cz+h, r, g, b, a);
        v(vc, mat, cx+h, cy+h, cz+h, r, g, b, a);
        v(vc, mat, cx-h, cy+h, cz+h, r, g, b, a);
        // East (X+)
        v(vc, mat, cx+h, cy-h, cz+h, r, g, b, a);
        v(vc, mat, cx+h, cy-h, cz-h, r, g, b, a);
        v(vc, mat, cx+h, cy+h, cz-h, r, g, b, a);
        v(vc, mat, cx+h, cy+h, cz+h, r, g, b, a);
        // West (X-)
        v(vc, mat, cx-h, cy-h, cz-h, r, g, b, a);
        v(vc, mat, cx-h, cy-h, cz+h, r, g, b, a);
        v(vc, mat, cx-h, cy+h, cz+h, r, g, b, a);
        v(vc, mat, cx-h, cy+h, cz-h, r, g, b, a);
    }

    /** Beam como cruz de 2 quads (perpendiculares) pra ficar bom de qualquer ângulo. */
    private void renderBeam(MultiBufferSource buf, Matrix4f mat, Vec3 start, Vec3 end,
                            float width, float alpha, boolean withCore) {
        if (start.distanceTo(end) < 0.01) return;
        VertexConsumer vc = buf.getBuffer(RenderType.lightning());
        Vec3 dir = end.subtract(start).normalize();
        Vec3 perp1 = (Math.abs(dir.y) < 0.99
                ? dir.cross(new Vec3(0, 1, 0))
                : dir.cross(new Vec3(1, 0, 0))).normalize();
        Vec3 perp2 = dir.cross(perp1).normalize();
        // Outer beam
        addBeamQuads(vc, mat, start, end, perp1, perp2, width, BEAM_R, BEAM_G, BEAM_B, alpha);
        // Inner core (mais brilhante, mais fino)
        if (withCore) {
            addBeamQuads(vc, mat, start, end, perp1, perp2, width * 0.35f, CORE_R, CORE_G, CORE_B, alpha);
        }
    }

    private void addBeamQuads(VertexConsumer vc, Matrix4f mat, Vec3 start, Vec3 end,
                              Vec3 perp1, Vec3 perp2, float w,
                              float r, float g, float b, float a) {
        Vec3 o1 = perp1.scale(w);
        Vec3 o2 = perp2.scale(w);
        addQuad(vc, mat, start.add(o1), start.subtract(o1), end.subtract(o1), end.add(o1), r, g, b, a);
        addQuad(vc, mat, end.add(o1), end.subtract(o1), start.subtract(o1), start.add(o1), r, g, b, a);
        addQuad(vc, mat, start.add(o2), start.subtract(o2), end.subtract(o2), end.add(o2), r, g, b, a);
        addQuad(vc, mat, end.add(o2), end.subtract(o2), start.subtract(o2), start.add(o2), r, g, b, a);
    }

    private void addQuad(VertexConsumer vc, Matrix4f mat,
                         Vec3 c1, Vec3 c2, Vec3 c3, Vec3 c4,
                         float r, float g, float b, float a) {
        v(vc, mat, (float)c1.x, (float)c1.y, (float)c1.z, r, g, b, a);
        v(vc, mat, (float)c2.x, (float)c2.y, (float)c2.z, r, g, b, a);
        v(vc, mat, (float)c3.x, (float)c3.y, (float)c3.z, r, g, b, a);
        v(vc, mat, (float)c4.x, (float)c4.y, (float)c4.z, r, g, b, a);
    }

    private void v(VertexConsumer vc, Matrix4f mat, float x, float y, float z,
                   float r, float g, float b, float a) {
        vc.vertex(mat, x, y, z).color(r, g, b, a).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(PortalCreatorBlockEntity be) {
        return be.isFiring();
    }

    @Override
    public int getViewDistance() { return 96; }
}
