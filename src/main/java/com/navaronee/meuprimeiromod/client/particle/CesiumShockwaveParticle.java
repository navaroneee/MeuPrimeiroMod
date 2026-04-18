package com.navaronee.meuprimeiromod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * Shockwave branco-azul. Partícula com motion radial forte, vida curta.
 */
public class CesiumShockwaveParticle extends CesiumAtomBaseParticle {

    protected CesiumShockwaveParticle(ClientLevel level, double x, double y, double z,
                                      double mx, double my, double mz,
                                      SpriteSet sprites, float size) {
        super(level, x, y, z, mx, my, mz, sprites, 1.8F + level.random.nextFloat() * 2.0F);
        this.lifetime = 50 + level.random.nextInt(30);
        this.gravity = 0.0F;
        this.hasPhysics = false;

        this.rCol = 0.85F;
        this.gCol = 0.92F;
        this.bCol = 1.0F;
        this.alpha = 0.7F;

        setSpriteIndex(level.random.nextInt(3) + 4);
    }

    @Override
    public void tick() {
        super.tick();
        float fade = (float) this.age / (float) this.lifetime;
        this.alpha = 0.7F * (1F - fade);
        this.quadSize *= 1.01F;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double mx, double my, double mz) {
            return new CesiumShockwaveParticle(level, x, y, z, mx, my, mz, sprites, 1.0F);
        }
    }
}
