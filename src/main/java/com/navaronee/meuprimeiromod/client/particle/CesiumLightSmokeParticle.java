package com.navaronee.meuprimeiromod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * Fumaça TRANSLÚCIDA pra explosão/fireball — alpha baixo, deixa ver o fogo através dela.
 */
public class CesiumLightSmokeParticle extends CesiumAtomBaseParticle {

    protected CesiumLightSmokeParticle(ClientLevel level, double x, double y, double z,
                                       double mx, double my, double mz,
                                       SpriteSet sprites, float size) {
        super(level, x, y, z, mx, my, mz, sprites, 1.5F + level.random.nextFloat() * 1.5F);
        // Lifetime MUITO curto: precisa sumir antes do ring aparecer (tick ~110)
        this.lifetime = 12 + level.random.nextInt(10);
        this.gravity = 0.02F;
        this.hasPhysics = false;

        float gray = 0.3F + level.random.nextFloat() * 0.15F;
        this.rCol = gray;
        this.gCol = gray;
        this.bCol = gray + 0.02F;
        this.alpha = 0.35F; // TRANSLÚCIDA

        setSpriteIndex(level.random.nextInt(4) + 2);
    }

    @Override
    public void tick() {
        super.tick();
        float progress = (float) this.age / (float) this.lifetime;
        // Fade quadrático agressivo: some rápido no final
        float fade = (1F - progress);
        fade = fade * fade;
        this.alpha = 0.35F * fade;
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
            return new CesiumLightSmokeParticle(level, x, y, z, mx, my, mz, sprites, 1.0F);
        }
    }
}
