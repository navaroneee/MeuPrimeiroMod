package com.navaronee.meuprimeiromod.entity;

import com.navaronee.meuprimeiromod.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

/**
 * Cogumelo nuclear com crescimento GRADUAL.
 *
 * Fase inicial (LARANJA) = fire + smoke — parece explosão real.
 * Fase do cogumelo (AZUL) = cesium glow + fumaça densa crescendo gradualmente.
 */
public class AtomicCloudEntity extends Entity {

    private static final int LIFETIME_TICKS = 1000;

    public AtomicCloudEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.noCulling = true;
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) return;
        if (!(level() instanceof ServerLevel server)) return;

        int t = tickCount;

        // ============================================================
        // FASE 1: CLARÃO (tick 1)
        // ============================================================
        if (t == 1) {
            bigBoom(server);
        }

        // ============================================================
        // FASE 2: BOLA DE FOGO CRESCENDO (tick 2-55) — explosão mais longa
        // ============================================================
        if (t >= 2 && t < 55) {
            growingFireball(server, t);
        }

        // ============================================================
        // FASE 3: BOLA SOBE continuando visível (tick 40-100)
        // Overlap com growing — transição contínua sem gap
        // ============================================================
        if (t >= 40 && t < 100) {
            risingShrinkingBall(server, t);
        }

        // ============================================================
        // BASE ICEBERG — cratera no chão onde a explosão aconteceu
        // Aparece no pico da fireball (tick 30) — já faz parte do cogumelo bottom-up
        // ============================================================
        if (t == 30) {
            groundImpactWave(server);
        }

        // ============================================================
        // FASE 4: STEM SOBE seguindo o trail da bola (tick 50-160) — bottom-up
        // A bola sobe, deixa rastro que forma o pilar
        // ============================================================
        if (t >= 50 && t < 160 && t % 2 == 0) {
            int ascending = (t - 50); // 0 -> 110
            double stemY = 2 + ascending * 0.4; // 2 -> 46 lento
            addStemSolidAt(server, Math.min(45, stemY));
        }

        // ============================================================
        // FASE 5: CAP forma QUANDO a bola chega no topo (tick 95-180)
        // ============================================================
        if (t >= 95 && t < 180 && t % 3 == 0) {
            float capProgress = Math.min((t - 95) / 55F, 1.0F);
            addCapSlice(server, capProgress);
        }

        // ============================================================
        // FASE 6: 3 CONDENSATION RINGS abaixo do cap — bottom-up
        // Aparecem junto com o stem subindo
        // ============================================================
        // Ring 3 (baixo) aparece primeiro — tick 70-150
        if (t >= 70 && t < 150 && t % 4 == 0) {
            float p = Math.min((t - 70) / 50F, 1.0F);
            addRingAt(server, 12, p);
        }
        // Ring 2 (meio) — tick 100-180
        if (t >= 100 && t < 180 && t % 4 == 0) {
            float p = Math.min((t - 100) / 50F, 1.0F);
            addRingAt(server, 22, p);
        }
        // Ring 1 (topo, abaixo do cap) — tick 130-210
        if (t >= 130 && t < 210 && t % 4 == 0) {
            float p = Math.min((t - 130) / 50F, 1.0F);
            addRingAt(server, 32, p);
        }

        // ============================================================
        // FASE 7: OVERSHOOT RINGS acima do cap — bottom-up
        // Lower overshoot = GIGANTE, upper overshoot = menor (ainda grande)
        // ============================================================
        // Overshoot 1 (logo acima do cap, GIGANTE) — cresce de 4 até 32
        if (t >= 160 && t < 280 && t % 4 == 0) {
            float p = Math.min((t - 160) / 80F, 1.0F);
            addOvershootRing(server, 58, 4 + p * 28, p); // raio 4 -> 32
        }
        // Overshoot 2 (mais alto, menor) — cresce de 3 até 22
        if (t >= 190 && t < 310 && t % 4 == 0) {
            float p = Math.min((t - 190) / 80F, 1.0F);
            addOvershootRing(server, 72, 3 + p * 19, p); // raio 3 -> 22
        }

        // ============================================================
        // FASE 6: SUSTAIN (tick 220-700)
        // Cogumelo mantido + flashes cianos ocasionais
        // ============================================================
        if (t >= 220 && t < 700 && t % 15 == 0) {
            sustainMushroom(server);
        }

        // Flashes cianos aleatórios (identidade cesium) — pontuais, não constantes
        if (t >= 35 && t < 500 && t % 35 == 0) {
            cyanFlashes(server);
        }

        // ============================================================
        // FASE 7: FOG residual no chão (tick 400-950)
        // ============================================================
        if (t >= 400 && t < 950 && t % 20 == 0) {
            groundFog(server);
        }

        if (t > LIFETIME_TICKS) {
            discard();
        }
    }

    // ============================================================
    // FASE 1: CLARÃO E EXPLOSÃO INICIAL LARANJA
    // ============================================================
    private void bigBoom(ServerLevel server) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();

        // Som da explosão já vem do siren (tocado 7s antes do boom no primed entity).
        // Aqui só um reforço sutil pra shake físico.
        server.playSound(null, cx, cy, cz, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.BLOCKS, 6.0F, 0.6F);

        // CLARÃO: flashes em coluna e espalhados
        for (int y = 0; y <= 14; y += 2) {
            sendForcedParticle(server, ParticleTypes.FLASH, cx, cy + y, cz, 0, 0, 0);
        }
        for (int i = 0; i < 12; i++) {
            double a = (i / 12.0) * Math.PI * 2;
            sendForcedParticle(server, ParticleTypes.FLASH,
                    cx + Math.cos(a) * 5, cy + 3, cz + Math.sin(a) * 5, 0, 0, 0);
        }
        // Múltiplos explosion emitters pra clarão denso
        sendForcedParticle(server, ParticleTypes.EXPLOSION_EMITTER, cx, cy + 2, cz, 0, 0, 0);
        sendForcedParticle(server, ParticleTypes.EXPLOSION_EMITTER, cx, cy + 6, cz, 0, 0, 0);
        sendForcedParticle(server, ParticleTypes.EXPLOSION_EMITTER, cx + 3, cy + 4, cz, 0, 0, 0);
        sendForcedParticle(server, ParticleTypes.EXPLOSION_EMITTER, cx - 3, cy + 4, cz, 0, 0, 0);
        sendForcedParticle(server, ParticleTypes.EXPLOSION_EMITTER, cx, cy + 4, cz + 3, 0, 0, 0);
        sendForcedParticle(server, ParticleTypes.EXPLOSION_EMITTER, cx, cy + 4, cz - 3, 0, 0, 0);
    }

    /**
     * Bola de fogo que CRESCE gradualmente.
     * Pico visual ao redor do tick 20, começa pequena no tick 2.
     */
    private void growingFireball(ServerLevel server, int t) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        // Curva de crescimento
        float growth;
        if (t < 20) {
            growth = (float) Math.sqrt(t / 20F);
        } else if (t < 28) {
            growth = 1.0F;
        } else {
            growth = 1.0F - (t - 28) / 10F * 0.6F;
        }

        // Bola MENOR e MAIS DENSA (antes 3+14, agora 3+10)
        double maxR = 3 + growth * 10;

        // CORE: DENSO e FOGO PREDOMINANTE (60% nosso fogo custom)
        int coreCount = (int) (150 + growth * 400); // MUITO mais denso
        double coreMaxR = maxR * 0.6;
        for (int i = 0; i < coreCount; i++) {
            double theta = rng.nextDouble() * 2 * Math.PI;
            double phi = rng.nextDouble() * Math.PI;
            // pow 2 = concentra forte no centro
            double r = Math.pow(rng.nextDouble(), 2.0) * coreMaxR;
            double ox = r * Math.sin(phi) * Math.cos(theta);
            double oy = r * Math.sin(phi) * Math.sin(theta);
            double oz = r * Math.cos(phi);
            double px = cx + ox;
            double py = cy + oy + 4;
            double pz = cz + oz;

            double upwardMotion = 0.08 + growth * 0.2;

            int kind = rng.nextInt(100);
            if (kind < 60) {
                // CESIUM_FIRE DOMINANTE — fogo grande laranja/amarelo
                sendForcedParticle(server, ModParticles.CESIUM_FIRE.get(), px, py, pz, 0, upwardMotion, 0);
            } else if (kind < 80) {
                sendForcedParticle(server, ParticleTypes.LAVA, px, py, pz, 0, 0, 0);
            } else if (kind < 92) {
                sendForcedParticle(server, ParticleTypes.FLAME, px, py, pz, 0, upwardMotion, 0);
            } else {
                sendForcedParticle(server, ParticleTypes.LARGE_SMOKE, px, py, pz, 0, upwardMotion, 0);
            }
        }

        // SHELL: fumaça vanilla densa
        int shellCount = (int) (100 + growth * 200);
        double shellUpward = 0.1 + growth * 0.25;
        for (int i = 0; i < shellCount; i++) {
            double theta = rng.nextDouble() * 2 * Math.PI;
            double phi = rng.nextDouble() * Math.PI;
            double r = maxR * (0.6 + rng.nextDouble() * 0.4);
            double ox = r * Math.sin(phi) * Math.cos(theta);
            double oy = r * Math.sin(phi) * Math.sin(theta);
            double oz = r * Math.cos(phi);

            sendForcedParticle(server, ParticleTypes.LARGE_SMOKE,
                    cx + ox, cy + oy + 4, cz + oz, 0, shellUpward, 0);
        }

        // POOFs extras pra impacto visual (desaparecem em 10 ticks)
        if (t < 20) {
            for (int i = 0; i < 15; i++) {
                double r = rng.nextDouble() * 3;
                double a = rng.nextDouble() * Math.PI * 2;
                sendForcedParticle(server, ParticleTypes.POOF,
                        cx + Math.cos(a) * r, cy + 2 + rng.nextDouble() * 4, cz + Math.sin(a) * r,
                        0, 0.1, 0);
            }
        }
    }

    /**
     * FASE 3: Bola sobe e encolhe — comportamento real de nuke.
     * A bola do fireball sobe de y=0 até y=35, encolhendo.
     * Ao subir, deixa TRAIL de fumaça que forma o stem.
     */
    private void risingShrinkingBall(ServerLevel server, int t) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        // Progresso 0 -> 1 da subida (tick 40 -> 100)
        float progress = (t - 40) / 60F;

        // Altura da bola: sobe de y=8 até y=45
        double ballY = cy + 8 + progress * 37;
        // Raio: ainda grande no começo (bola explode e sobe), encolhe pouco
        double ballR = 17 - progress * 3;

        float fireRatio = 1.0F - progress;
        double ballUpward = 0.15 + progress * 0.15;
        int count = 110;
        for (int i = 0; i < count; i++) {
            double theta = rng.nextDouble() * 2 * Math.PI;
            double phi = rng.nextDouble() * Math.PI;
            double r = Math.pow(rng.nextDouble(), 1.3) * ballR;
            double ox = r * Math.sin(phi) * Math.cos(theta);
            double oy = r * Math.sin(phi) * Math.sin(theta);
            double oz = r * Math.cos(phi);
            double px = cx + ox;
            double py = ballY + oy;
            double pz = cz + oz;

            // Vanilla particles — lifetime curto, somem rápido
            int kind = rng.nextInt(100);
            if (fireRatio > 0.5F && kind < 25) {
                sendForcedParticle(server, ParticleTypes.FLAME, px, py, pz, 0, ballUpward, 0);
            } else if (fireRatio > 0.2F && kind < 40) {
                sendForcedParticle(server, ParticleTypes.LAVA, px, py, pz, 0, 0, 0);
            } else {
                sendForcedParticle(server, ParticleTypes.LARGE_SMOKE, px, py, pz, 0, ballUpward, 0);
            }
        }

        // TRAIL vanilla — morre em ~60 ticks built-in
        int trailCount = 20;
        for (int i = 0; i < trailCount; i++) {
            double a = rng.nextDouble() * Math.PI * 2;
            double r = rng.nextDouble() * 2.2;
            double spawnY = cy + 8 + rng.nextDouble() * Math.max(1, ballY - cy - 8);
            double upSpeed = 0.25 + rng.nextDouble() * 0.2;
            sendForcedParticle(server, ParticleTypes.LARGE_SMOKE,
                    cx + Math.cos(a) * r, spawnY, cz + Math.sin(a) * r,
                    0, upSpeed, 0);
        }
    }

    /**
     * Stem sólido — desce do cap até o chão.
     * Cada chamada adiciona uma camada na altura Y dada.
     */
    private void addStemSolidAt(ServerLevel server, double heightY) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        ParticleOptions smoke = ModParticles.CESIUM_ATOM_CORE.get();

        // Camada densa
        double stemR = 2.0 + rng.nextDouble() * 0.5;
        for (int i = 0; i < 10; i++) {
            double a = rng.nextDouble() * Math.PI * 2;
            double ox = Math.cos(a) * stemR;
            double oz = Math.sin(a) * stemR;
            sendForcedParticle(server, smoke, cx + ox, cy + heightY, cz + oz, 0, 0.01, 0);
        }

        // Núcleo denso (pilar central)
        for (int i = 0; i < 3; i++) {
            double a = rng.nextDouble() * Math.PI * 2;
            double r = rng.nextDouble() * 1.0;
            sendForcedParticle(server, smoke, cx + Math.cos(a) * r, cy + heightY, cz + Math.sin(a) * r, 0, 0.01, 0);
        }
    }

    /**
     * Impacto no chão — quando o stem chega, espalha uma onda radial de fumaça.
     * Como se a "coluna" batesse no chão e cuspisse fumaça pros lados.
     */
    private void groundImpactWave(ServerLevel server) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        ParticleOptions smoke = ModParticles.CESIUM_ATOM_CORE.get();
        ParticleOptions shock = ModParticles.CESIUM_SHOCKWAVE.get();

        // Onda radial no chão — 400 particles espalhando pra fora
        int waveCount = 400;
        for (int i = 0; i < waveCount; i++) {
            double a = rng.nextDouble() * Math.PI * 2;
            double r = rng.nextDouble() * 3; // começa perto do centro
            double px = cx + Math.cos(a) * r;
            double pz = cz + Math.sin(a) * r;
            double py = cy + 0.5 + rng.nextDouble() * 2;

            // Velocidade radial forte (espalha pra fora)
            double speed = 0.5 + rng.nextDouble() * 0.5;
            double mx = Math.cos(a) * speed;
            double mz = Math.sin(a) * speed;

            if (rng.nextFloat() < 0.7F) {
                sendForcedParticle(server, smoke, px, py, pz, mx, 0.02, mz);
            } else {
                sendForcedParticle(server, shock, px, py, pz, mx, 0.02, mz);
            }
        }

        // Base larga que fica ao redor (iceberg final)
        for (int i = 0; i < 250; i++) {
            double depth = rng.nextDouble() * 8.0;
            double widthFactor = 1.0 - (depth / 20.0);
            double maxR = (20.0 + rng.nextDouble() * 12.0) * widthFactor;
            double r = rng.nextDouble() * maxR;
            double a = rng.nextDouble() * Math.PI * 2;
            sendForcedParticle(server, smoke,
                    cx + Math.cos(a) * r, cy - depth + 0.5, cz + Math.sin(a) * r, 0, 0, 0);
        }
    }

    /**
     * Flashes cianos — identidade cesium, pontuais.
     */
    private void cyanFlashes(ServerLevel server) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        ParticleOptions fireball = ModParticles.CESIUM_ATOM_RING.get();

        // 3-5 flashes em posições random no cogumelo
        int count = 3 + rng.nextInt(3);
        for (int i = 0; i < count; i++) {
            double a = rng.nextDouble() * Math.PI * 2;
            // Uma das alturas: base, stem médio, cap
            int zone = rng.nextInt(3);
            double r, y;
            if (zone == 0) {
                r = rng.nextDouble() * 2;
                y = 2 + rng.nextDouble() * 8; // stem baixo
            } else if (zone == 1) {
                r = rng.nextDouble() * 2;
                y = 15 + rng.nextDouble() * 15; // stem médio/alto
            } else {
                r = rng.nextDouble() * 8;
                y = 35 + rng.nextDouble() * 6; // dentro do cap
            }
            sendForcedParticle(server, fireball,
                    cx + Math.cos(a) * r, cy + y, cz + Math.sin(a) * r,
                    0, 0, 0);
        }
    }

    // ============================================================
    // (UNUSED agora — mantido como legado)
    // ============================================================
    private void smokeColumn(ServerLevel server, int t) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        // Smoke translúcida durante a coluna de fogo (deixa ver as chamas)
        ParticleOptions smokeLight = ModParticles.CESIUM_LIGHT_SMOKE.get();
        ParticleOptions fire = ModParticles.CESIUM_FIRE.get();

        double columnHeight = 3 + (t - 5) * 0.6;
        for (int i = 0; i < 30; i++) {
            double a = rng.nextDouble() * Math.PI * 2;
            double r = 1 + rng.nextDouble() * 2;
            double h = rng.nextDouble() * columnHeight;
            double px = cx + Math.cos(a) * r;
            double pz = cz + Math.sin(a) * r;
            double py = cy + h;

            if (rng.nextFloat() < 0.65F) {
                sendForcedParticle(server, smokeLight, px, py, pz, 0, 0.2, 0);
            } else {
                sendForcedParticle(server, fire, px, py, pz, 0, 0.15, 0);
            }
        }
    }

    // ============================================================
    // FASE 3: STEM CRESCENDO GRADUALMENTE (azul cesium)
    // ============================================================
    private void addStemLevel(ServerLevel server, int height) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        ParticleOptions smoke = ModParticles.CESIUM_ATOM_CORE.get();
        ParticleOptions fireball = ModParticles.CESIUM_ATOM_RING.get();

        // Camada do stem na altura atual
        for (int i = 0; i < 12; i++) {
            double a = rng.nextDouble() * Math.PI * 2;
            double ox = Math.cos(a) * (1.3 + rng.nextDouble() * 0.7);
            double oz = Math.sin(a) * (1.3 + rng.nextDouble() * 0.7);
            sendForcedParticle(server, smoke, cx + ox, cy + height, cz + oz, 0, 0.04, 0);
        }
        // Glow azul central
        sendForcedParticle(server, fireball, cx, cy + height, cz, 0, 0.04, 0);

        // Base iceberg gradual
        if (height < 10) {
            for (int i = 0; i < 20; i++) {
                double depth = rng.nextDouble() * 12.0;
                double widthFactor = 1.0 - (depth / 24.0);
                double maxR = (15.0 + rng.nextDouble() * 10.0) * widthFactor;
                double r = rng.nextDouble() * maxR;
                double a = rng.nextDouble() * Math.PI * 2;
                sendForcedParticle(server, smoke,
                        cx + Math.cos(a) * r, cy - depth + 0.5, cz + Math.sin(a) * r, 0, 0, 0);
            }
        }
    }

    // ============================================================
    // FASE 4: CAP GRADUAL (azul cesium)
    // ============================================================
    private void addCapSlice(ServerLevel server, float progress) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        ParticleOptions smoke = ModParticles.CESIUM_ATOM_CORE.get();
        ParticleOptions fireball = ModParticles.CESIUM_ATOM_RING.get();

        double capY = cy + 40;
        // Raio cresce LATERALMENTE (chapéu largo, não bola)
        double capR = 6 + progress * 22; // raio 6 -> 28
        double capHeight = 5 + progress * 3; // altura do cap: 5 -> 8 (bem menor que o raio = achatado)

        int count = 80;
        for (int i = 0; i < count; i++) {
            // Distribuição ACHATADA — raio lateral grande, altura pequena
            double a = rng.nextDouble() * Math.PI * 2;
            double rFlat = Math.pow(rng.nextDouble(), 0.6) * capR; // concentra mais no centro
            double y = (rng.nextDouble() - 0.3) * capHeight; // maior parte ACIMA do meio (topo arredondado)

            // Chapéu de cogumelo: forma abobadada mas achatada
            // Partículas no topo ficam mais próximas do centro (dome effect)
            double heightFactor = 1.0 - Math.abs(y / capHeight) * 0.3; // bordas mais estreitas no topo
            double effectiveR = rFlat * heightFactor;

            double x = Math.cos(a) * effectiveR;
            double z = Math.sin(a) * effectiveR;
            sendForcedParticle(server, smoke, cx + x, capY + y, cz + z, 0, 0, 0);
        }

        // Glow azul no miolo (cesium core) — espalhado no interior do chapéu
        for (int i = 0; i < 14; i++) {
            double a = rng.nextDouble() * Math.PI * 2;
            double r = rng.nextDouble() * capR * 0.5;
            double y = (rng.nextDouble() - 0.3) * capHeight * 0.8;
            sendForcedParticle(server, fireball, cx + Math.cos(a) * r, capY + y, cz + Math.sin(a) * r, 0, 0, 0);
        }
    }

    /**
     * Condensation ring — colar horizontal abaixo do cap.
     * Dá o formato clássico de cogumelo (anel que se vê de longe).
     */
    /**
     * Anel ACIMA do cap — disco horizontal (overshoot vortex).
     * Usa as mesmas partículas dos lower rings (LIGHT_SMOKE + SHOCKWAVE).
     */
    private void addOvershootRing(ServerLevel server, double heightY, double ringRadius, float progress) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        ParticleOptions smokeLight = ModParticles.CESIUM_LIGHT_SMOKE.get();
        ParticleOptions shock = ModParticles.CESIUM_SHOCKWAVE.get();

        double ringY = cy + heightY;

        // Borda do disco (denso)
        int ringPoints = 70;
        for (int i = 0; i < ringPoints; i++) {
            double a = (i / (double) ringPoints) * Math.PI * 2 + rng.nextDouble() * 0.15;
            double rVariation = ringRadius + (rng.nextDouble() - 0.3) * 2.5;
            double ox = Math.cos(a) * rVariation;
            double oz = Math.sin(a) * rVariation;
            double yJitter = (rng.nextDouble() - 0.5) * 1.5;

            sendForcedParticle(server, smokeLight, cx + ox, ringY + yJitter, cz + oz,
                    ox * 0.004, 0, oz * 0.004);

            // Shockwave branco intercalado (highlight)
            if (i % 4 == 0) {
                sendForcedParticle(server, shock, cx + ox, ringY + yJitter, cz + oz,
                        ox * 0.005, 0, oz * 0.005);
            }
        }

        // Interior do disco (preenche pra parecer cheio, não só anel)
        int interiorPoints = 35;
        for (int i = 0; i < interiorPoints; i++) {
            double a = rng.nextDouble() * Math.PI * 2;
            double r = rng.nextDouble() * ringRadius * 0.85;
            double ox = Math.cos(a) * r;
            double oz = Math.sin(a) * r;
            double yJitter = (rng.nextDouble() - 0.5) * 1.0;

            sendForcedParticle(server, smokeLight, cx + ox, ringY + yJitter, cz + oz, 0, 0, 0);
        }
    }

    /**
     * Anel de condensação em altura específica (top-down chain de rings).
     */
    private void addRingAt(ServerLevel server, double heightY, float progress) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        ParticleOptions smoke = ModParticles.CESIUM_LIGHT_SMOKE.get();
        ParticleOptions shock = ModParticles.CESIUM_SHOCKWAVE.get();

        double ringY = cy + heightY;
        double ringR = 5 + progress * 9; // 5 -> 14

        int points = 30;
        for (int i = 0; i < points; i++) {
            double a = (i / (double) points) * Math.PI * 2 + rng.nextDouble() * 0.2;
            double ox = Math.cos(a) * ringR;
            double oz = Math.sin(a) * ringR;
            double yJitter = (rng.nextDouble() - 0.5) * 1.2;

            sendForcedParticle(server, smoke, cx + ox, ringY + yJitter, cz + oz,
                    ox * 0.006, 0, oz * 0.006);

            if (i % 3 == 0) {
                sendForcedParticle(server, shock, cx + ox, ringY + yJitter, cz + oz,
                        ox * 0.008, 0, oz * 0.008);
            }
        }
    }

    private void addCondensationRing(ServerLevel server, float progress) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        // Smoke translúcida pro anel (não bloqueia visão do cogumelo)
        ParticleOptions smoke = ModParticles.CESIUM_LIGHT_SMOKE.get();
        ParticleOptions shock = ModParticles.CESIUM_SHOCKWAVE.get();

        // Y fixo abaixo do cap (altura 28), raio cresce com progresso
        double ringY = cy + 28;
        double ringR = 6 + progress * 8; // raio 6 -> 14

        int points = 40;
        for (int i = 0; i < points; i++) {
            double a = (i / (double) points) * Math.PI * 2 + rng.nextDouble() * 0.2;
            double ox = Math.cos(a) * ringR;
            double oz = Math.sin(a) * ringR;
            double yJitter = (rng.nextDouble() - 0.5) * 1.5;

            // Smoke denso formando o anel
            sendForcedParticle(server, smoke, cx + ox, ringY + yJitter, cz + oz,
                    ox * 0.008, 0, oz * 0.008);

            // Shockwave branco intercalado (highlight)
            if (i % 3 == 0) {
                sendForcedParticle(server, shock, cx + ox, ringY + yJitter, cz + oz,
                        ox * 0.01, 0, oz * 0.01);
            }
        }
    }

    // ============================================================
    // FASE 5: SUSTENTAÇÃO — mantém cogumelo visível
    // ============================================================
    private void sustainMushroom(ServerLevel server) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        ParticleOptions smoke = ModParticles.CESIUM_ATOM_CORE.get();
        ParticleOptions fire = ModParticles.CESIUM_FIRE.get();

        // Refresh do cap (smoke cinza)
        double capY = cy + 40;
        for (int i = 0; i < 120; i++) {
            double r = rng.nextDouble() * 18.0;
            double theta = rng.nextDouble() * Math.PI * 2;
            double phi = rng.nextDouble() * (Math.PI / 2.0);
            double x = Math.sin(phi) * Math.cos(theta) * r;
            double y = Math.cos(phi) * r * 0.7;
            double z = Math.sin(phi) * Math.sin(theta) * r;
            sendForcedParticle(server, smoke, cx + x, capY + y, cz + z, 0, 0, 0);
        }

        // Fogo laranja DISCRETO dentro do cap (glow quente residual)
        for (int i = 0; i < 6; i++) {
            double r = rng.nextDouble() * 5.0;
            double a = rng.nextDouble() * Math.PI * 2;
            double y = rng.nextDouble() * 4 - 2;
            sendForcedParticle(server, fire,
                    cx + Math.cos(a) * r, capY + y, cz + Math.sin(a) * r,
                    0, 0.02, 0);
        }

        // Refresh do stem — começa em Y=14 (não no chão) pra não bloquear visão
        for (int h = 14; h < 38; h += 3) {
            for (int i = 0; i < 3; i++) {
                double a = rng.nextDouble() * Math.PI * 2;
                sendForcedParticle(server, smoke,
                        cx + Math.cos(a) * 1.6, cy + h, cz + Math.sin(a) * 1.6, 0, 0, 0);
            }
        }
    }

    // ============================================================
    // FASE 6: FOG RESIDUAL
    // ============================================================
    private void groundFog(ServerLevel server) {
        double cx = getX();
        double cy = getY();
        double cz = getZ();
        RandomSource rng = server.random;

        ParticleOptions fog = ModParticles.CESIUM_ATOM_FOG.get();

        for (int i = 0; i < 60; i++) {
            double a = rng.nextDouble() * Math.PI * 2;
            double r = rng.nextDouble() * 32.0;
            double ox = Math.cos(a) * r;
            double oz = Math.sin(a) * r;
            sendForcedParticle(server, fog, cx + ox, cy + rng.nextDouble() * 3, cz + oz,
                    (rng.nextDouble() - 0.5) * 0.08, 0, (rng.nextDouble() - 0.5) * 0.08);
        }
    }

    private void sendForcedParticle(ServerLevel server, ParticleOptions type,
                                    double x, double y, double z,
                                    double mx, double my, double mz) {
        for (ServerPlayer player : server.players()) {
            server.sendParticles(player, type, true, x, y, z, 0, mx, my, mz, 1.0);
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
