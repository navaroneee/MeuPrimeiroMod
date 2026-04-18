package com.navaronee.meuprimeiromod.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

/**
 * Fogo laranja da explosão inicial.
 * Transição de cor: laranja -> amarelo ao longo da vida.
 * Escala cresce com idade, fullbright, sprite animado nos 8 frames.
 */
public class CesiumFireParticle extends CesiumAtomBaseParticle {

    private final float baseSize;

    protected CesiumFireParticle(ClientLevel level, double x, double y, double z,
                                 double mx, double my, double mz,
                                 SpriteSet sprites, float size) {
        super(level, x, y, z, mx, my, mz, sprites, 0.5F);
        // GRANDE: baseSize 5-8 (antes era 2.5-4)
        this.baseSize = 5.0F + level.random.nextFloat() * 3.0F;
        this.lifetime = 15 + level.random.nextInt(8);
        this.gravity = -0.02F; // leve subida (fogo sobe)
        this.hasPhysics = false;

        // Laranja inicial (RGB aprox 1.0, 0.47, 0.21)
        this.rCol = 1.0F;
        this.gCol = 0.47F + level.random.nextFloat() * 0.12F;
        this.bCol = 0.21F;
        this.alpha = 1.0F;

        setSpriteIndex(7); // começa com sprite grande/denso
    }

    @Override
    public void tick() {
        super.tick();

        float progress = (float) this.age / (float) this.lifetime;

        // Cor vai de laranja -> amarelo (verde aumenta)
        this.gCol = Math.min(1.0F, 0.47F + progress * 0.5F);

        // Sprite animado: passa pelos 8 frames ao longo da vida
        int spriteIdx = 7 - Math.round(progress * 7);
        setSpriteIndex(spriteIdx);

        // Escala: começa 70% do tamanho, cresce até 150% (fica GRANDE rápido)
        this.quadSize = baseSize * (0.7F + progress * 0.8F);

        // Alpha fade no final — mais cedo e quadrático (some rápido)
        if (progress > 0.5F) {
            float fade = (progress - 0.5F) / 0.5F;
            this.alpha = 1.0F - fade * fade;
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return 240 << 16 | 240; // fullbright (fogo brilha)
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
            return new CesiumFireParticle(level, x, y, z, mx, my, mz, sprites, 1.0F);
        }
    }
}
