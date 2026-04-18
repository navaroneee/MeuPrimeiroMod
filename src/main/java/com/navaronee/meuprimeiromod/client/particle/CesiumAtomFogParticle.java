package com.navaronee.meuprimeiromod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * Fog azul pálido no chão — fallout cesium-137 residual.
 */
public class CesiumAtomFogParticle extends CesiumAtomBaseParticle {

    protected CesiumAtomFogParticle(ClientLevel level, double x, double y, double z,
                                    double mx, double my, double mz,
                                    SpriteSet sprites, float size) {
        super(level, x, y, z, mx, my, mz, sprites, 2.5F + level.random.nextFloat() * 2.5F);
        this.lifetime = 200 + level.random.nextInt(80);
        this.gravity = 0.02F;
        this.hasPhysics = false;

        this.rCol = 0.3F;
        this.gCol = 0.65F;
        this.bCol = 0.95F;
        this.alpha = 0.35F;

        setSpriteIndex(level.random.nextInt(3) + 5);
    }

    @Override
    public void tick() {
        super.tick();
        float fade = (float) this.age / (float) this.lifetime;
        this.alpha = 0.35F * (1F - fade);
        this.quadSize *= 1.003F;
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
            return new CesiumAtomFogParticle(level, x, y, z, mx, my, mz, sprites, 1.0F);
        }
    }
}
