package com.navaronee.meuprimeiromod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * Núcleo brilhante (fireball) — azul ciano intenso cesium-137.
 * Pequeno, vida curta, brilho pulsante.
 */
public class CesiumAtomRingParticle extends CesiumAtomBaseParticle {

    protected CesiumAtomRingParticle(ClientLevel level, double x, double y, double z,
                                     double mx, double my, double mz,
                                     SpriteSet sprites, float size) {
        super(level, x, y, z, mx, my, mz, sprites, 1.2F + level.random.nextFloat() * 1.5F);
        this.lifetime = 40 + level.random.nextInt(30);
        this.gravity = 0.0F;
        this.hasPhysics = false;

        this.rCol = 0.55F;
        this.gCol = 0.90F;
        this.bCol = 1.0F;
        this.alpha = 0.95F;

        setSpriteIndex(level.random.nextInt(3) + 5);
    }

    @Override
    public void tick() {
        super.tick();

        float progress = (float) this.age / (float) this.lifetime;
        // Brilho pico no meio, fade no fim
        float brightness = progress < 0.3F ? (progress / 0.3F) : (1F - (progress - 0.3F) / 0.7F);
        this.alpha = 0.95F * brightness;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 240 << 16 | 240; // fullbright (brilha no escuro)
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
            return new CesiumAtomRingParticle(level, x, y, z, mx, my, mz, sprites, 1.0F);
        }
    }
}
