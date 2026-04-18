package com.navaronee.meuprimeiromod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

/**
 * Base das partículas do cogumelo atômico.
 * Adaptado de ParticleAtomBase (Immersive Intelligence, Pabilo8 — com crédito).
 */
public abstract class CesiumAtomBaseParticle extends TextureSheetParticle {

    protected final SpriteSet sprites;
    protected final float particleSize;

    protected CesiumAtomBaseParticle(ClientLevel level, double x, double y, double z,
                                     double mx, double my, double mz,
                                     SpriteSet sprites, float size) {
        super(level, x, y, z, 0, 0, 0);
        this.sprites = sprites;
        this.particleSize = size;

        // Motion amplificado horizontal, atenuado vertical (II: 1.55 / 0.65)
        this.xd = mx * 1.55;
        this.yd = my * 0.65;
        this.zd = mz * 1.55;

        this.gravity = 0.25F;
        this.friction = 0.96F;
        this.quadSize = size;
        this.hasPhysics = false; // partículas não colidem com blocos
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.move(this.xd, this.yd, this.zd);
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;

        // Deflecção de players
        Player player = this.level.getNearestPlayer(this.x, this.y, this.z, 2.0, false);
        if (player != null) {
            AABB aabb = player.getBoundingBox();
            if (this.y > aabb.minY) {
                this.y += (aabb.minY - this.y) * 0.2;
                this.yd += (player.getDeltaMovement().y - this.yd) * 0.2;
                this.setPos(this.x, this.y, this.z);
            }
        }

        if (this.onGround) {
            this.xd *= 0.7;
            this.zd *= 0.7;
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return 240 << 16 | 240; // fullbright
    }

    protected float getProgress(float partialTick) {
        return (this.age + partialTick) / (float) this.lifetime;
    }

    protected void setSpriteIndex(int index) {
        int total = 8;
        int clamped = Math.max(0, Math.min(total - 1, index));
        this.setSprite(this.sprites.get(clamped, total));
    }
}
