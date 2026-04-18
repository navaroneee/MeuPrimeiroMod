package com.navaronee.meuprimeiromod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * Fumaça cinza-escura do corpo do cogumelo.
 * Partícula pequena (0.5-1.5), longa vida (40-80t), expansão lenta, fade gradual.
 */
public class CesiumAtomCoreParticle extends CesiumAtomBaseParticle {

    protected CesiumAtomCoreParticle(ClientLevel level, double x, double y, double z,
                                     double mx, double my, double mz,
                                     SpriteSet sprites, float size) {
        super(level, x, y, z, mx, my, mz, sprites, 2.0F + level.random.nextFloat() * 2.5F);
        this.lifetime = 180 + level.random.nextInt(120);
        this.gravity = 0.05F;
        this.hasPhysics = false;

        // Fumaça cinza NEUTRA (sem tint azul) — parece fumaça nuclear de verdade
        float gray = 0.22F + level.random.nextFloat() * 0.18F;
        this.rCol = gray;
        this.gCol = gray;
        this.bCol = gray + 0.02F; // só um toque sutil
        this.alpha = 0.8F;

        setSpriteIndex(level.random.nextInt(4) + 2);
    }

    @Override
    public void tick() {
        super.tick();

        // Alpha 80% estável, fade só no final
        float progress = (float) this.age / (float) this.lifetime;
        if (progress < 0.7F) {
            this.alpha = 0.8F;
        } else {
            this.alpha = 0.8F * (1F - (progress - 0.7F) / 0.3F);
        }

        this.quadSize *= 1.007F;
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
            return new CesiumAtomCoreParticle(level, x, y, z, mx, my, mz, sprites, 1.0F);
        }
    }
}
