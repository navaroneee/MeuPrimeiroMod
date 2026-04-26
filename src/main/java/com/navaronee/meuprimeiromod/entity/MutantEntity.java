package com.navaronee.meuprimeiromod.entity;

import com.navaronee.meuprimeiromod.effect.ModEffects;
import com.navaronee.meuprimeiromod.entity.ai.MutantChaseTargetGoal;
import com.navaronee.meuprimeiromod.entity.ai.MutantHeavyAttackGoal;
import com.navaronee.meuprimeiromod.entity.ai.MutantMeleeAttackGoal;
import com.navaronee.meuprimeiromod.entity.ai.MutantPanicTeleportGoal;
import com.navaronee.meuprimeiromod.entity.ai.MutantSpinAttackGoal;
import com.navaronee.meuprimeiromod.entity.ai.MutantTeleportGoal;
import com.navaronee.meuprimeiromod.item.ModItems;
import com.navaronee.meuprimeiromod.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerBossEvent;

/**
 * Mutant ("Bichão") — mob boss radioativo.
 * Ataques:
 *  - Melee (atackSimp) com radiação
 *  - Heavy combo (atack_1_open/close) com 3 payloads aleatórios (abelhas/slimes/tnt)
 *  - Spin charge (atack2)
 *  - HitStrong disparado por TNT rebatida ou granada de césio
 *  - Teleport estilo Enderman dentro de raio 10
 */
public class MutantEntity extends Monster {

    // Eventos de animação server → client. Bytes 60-70 colidem com Sniffer/outros no
    // ClientPacketListener (CCE no momento do dispatch), então usamos 100+ que é free.
    public static final byte EVENT_ATTACK_SIMPLE = 100;
    public static final byte EVENT_ATTACK_HEAVY_OPEN = 101;
    public static final byte EVENT_ATTACK_HEAVY_CLOSE = 102;
    public static final byte EVENT_ATTACK_SPIN = 103;
    public static final byte EVENT_HIT_STRONG = 104;

    // AnimationStates — client-side, disparadas via handleEntityEvent
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState attackSimpleState = new AnimationState();
    public final AnimationState attack2State = new AnimationState();
    public final AnimationState attackHeavyOpenState = new AnimationState();
    public final AnimationState attackHeavyCloseState = new AnimationState();
    public final AnimationState hitStrongState = new AnimationState();

    // Cooldowns (ticks) — apenas servidor
    public int heavyAttackCooldown = 100;
    public int spinAttackCooldown = 200;
    public int teleportCooldown = 60;
    public int simpleAttackCooldown = 0;
    // Trava durante HitStrong: impede ataques e navegação
    public int hitStrongLock = 0;
    // Flag ativada pelo TeleportGoal em fase 3+ — força o próximo spin
    public boolean forceSpinAfterTeleport = false;
    // Flag ativada pelo HeavyAttackGoal — invulnerável exceto a TNT rebatida
    public boolean isCastingHeavy = false;

    // Death sequence: ao bater 0 HP, levanta os braços, larga uma bomba de césio
    // armada (fuse 7s = 140 ticks) e morre exatamente quando o nuke explode.
    public int deathSequenceTick = 0;
    public boolean deathSequenceFinished = false;
    private static final int DEATH_SEQ_NUKE_DROP_TICK = 60;   // fim do open
    private static final int DEATH_SEQ_END_TICK = 200;        // 60 + 140 fuse

    /**
     * Fase do boss baseada em HP:
     *  fase 1: 100-75% (base, melee pesado, specials raros)
     *  fase 2: 75-50%  (menos melee, mais abelhas/spin)
     *  fase 3: 50-25%  (spin alto, teleport+spin combo)
     *  fase 4: 25-0%   (sem melee, tudo alto)
     */
    public int getPhase() {
        float frac = this.getHealth() / this.getMaxHealth();
        if (frac > 0.75F) return 1;
        if (frac > 0.50F) return 2;
        if (frac > 0.25F) return 3;
        return 4;
    }

    // Boss bar visível a todos os players em visual range
    private final ServerBossEvent bossEvent;

    public MutantEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.bossEvent = (ServerBossEvent) new ServerBossEvent(
                this.getDisplayName(),
                BossEvent.BossBarColor.RED,
                BossEvent.BossBarOverlay.PROGRESS)
                .setDarkenScreen(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.ARMOR, 6.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        // Ataques especiais têm priority alta pra disparar antes do melee
        this.goalSelector.addGoal(1, new MutantPanicTeleportGoal(this));
        this.goalSelector.addGoal(1, new MutantHeavyAttackGoal(this));
        this.goalSelector.addGoal(2, new MutantSpinAttackGoal(this));
        this.goalSelector.addGoal(3, new MutantTeleportGoal(this));
        this.goalSelector.addGoal(4, new MutantMeleeAttackGoal(this, 1.25D, true));
        // Fase 4: persegue o alvo sem meleear (supre o pathfinding perdido)
        this.goalSelector.addGoal(4, new MutantChaseTargetGoal(this, 1.3D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        // Prioridade de alvo: Iron Golem > Player > Villager > resto (passivos gerais).
        // Target goals em ordem crescente de prioridade (menor número = mais alta).
        // Se o alvo top-tier está no range, ele preempta os de baixo a cada tick.
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                this, net.minecraft.world.entity.animal.IronGolem.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                this, net.minecraft.world.entity.npc.Villager.class, true));
        // Catch-all: pets, animais passivos etc. — mas NÃO mobs hostis nem as summons
        // radioativas. Random interval 40 tira a agressão constante desses passivos;
        // só vira alvo se o Mutant tiver sido hit ou não tiver top-tier em range.
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(
                this, LivingEntity.class, 40, true, false,
                RadioactiveTargets::isValidTarget));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupAnimationStates();
        }
    }

    @Override
    protected void customServerAiStep() {
        // Atualiza boss bar baseado em HP
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());

        // Death sequence: braços levantados, bomba arma, BOOM, morte
        if (this.deathSequenceTick > 0) {
            this.deathSequenceTick++;
            this.getNavigation().stop();
            this.setTarget(null);
            this.setDeltaMovement(0, this.getDeltaMovement().y, 0);

            // Re-broadcast OPEN periodicamente pra os arms ficarem levantados
            // toda a sequência (auto-stop client é 10s)
            if (this.deathSequenceTick % 50 == 1) {
                this.level().broadcastEntityEvent(this, EVENT_ATTACK_HEAVY_OPEN);
            }

            if (this.deathSequenceTick == DEATH_SEQ_NUKE_DROP_TICK
                    && this.level() instanceof ServerLevel server) {
                // Bomba de césio armada cai aos pés
                CesiumNukePrimedEntity nuke = new CesiumNukePrimedEntity(
                        this.level(),
                        this.getX(), this.getY() + 0.5, this.getZ(),
                        this);
                server.addFreshEntity(nuke);
            }

            if (this.deathSequenceTick >= DEATH_SEQ_END_TICK) {
                // Libera o hurt() e morre via explosão
                this.deathSequenceFinished = true;
                this.deathSequenceTick = 0;
                this.hurt(this.damageSources().explosion(null, null), Float.MAX_VALUE);
            }
            return;
        }

        // Decrementa cooldowns
        if (this.heavyAttackCooldown > 0) this.heavyAttackCooldown--;
        if (this.spinAttackCooldown > 0) this.spinAttackCooldown--;
        if (this.teleportCooldown > 0) this.teleportCooldown--;
        if (this.simpleAttackCooldown > 0) this.simpleAttackCooldown--;

        // Durante HitStrong, Mutant fica stunned (sem AI, sem navegação)
        if (this.hitStrongLock > 0) {
            this.hitStrongLock--;
            this.getNavigation().stop();
            return;
        }

        super.customServerAiStep();
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(net.minecraft.network.chat.Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    /**
     * Idle sempre ativo; walk ativa só quando se move.
     * Os demais AnimationStates são disparados via handleEntityEvent e precisam
     * ser parados manualmente após o length, senão o último frame fica congelado
     * sendo aplicado em cima da pose idle/walk.
     */
    private void setupAnimationStates() {
        if (!this.idleAnimationState.isStarted()) {
            this.idleAnimationState.start(this.tickCount);
        }

        boolean isMoving = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-7D;
        if (isMoving && !this.walkAnimationState.isStarted()) {
            this.walkAnimationState.start(this.tickCount);
        } else if (!isMoving && this.walkAnimationState.isStarted()) {
            this.walkAnimationState.stop();
        }

        // Auto-stop: animações que naturalmente voltam a pose idle podem parar no
        // fim do length. OPEN NÃO auto-para rápido — queremos que o pose final
        // (braços erguidos) fique congelado até o CLOSE explicitamente parar ele
        // via handleEntityEvent. Safety net de 10s só pra cobrir caso CLOSE nunca
        // chegue (entity morreu mid-cast, etc).
        stopIfExpired(this.attackSimpleState, 1000L);        // atackSimp: 1.0s
        stopIfExpired(this.attack2State, 2000L);              // atack2: 2.0s
        stopIfExpired(this.attackHeavyOpenState, 10000L);     // safety net
        stopIfExpired(this.attackHeavyCloseState, 2000L);     // atack_1_close: 2.0s
        stopIfExpired(this.hitStrongState, 2209L);            // hitStrong: 2.2083s
    }

    private static void stopIfExpired(AnimationState state, long lengthMs) {
        if (state.isStarted() && state.getAccumulatedTime() >= lengthMs) {
            state.stop();
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        switch (id) {
            case EVENT_ATTACK_SIMPLE -> {
                stopAllAttackStates();
                this.attackSimpleState.start(this.tickCount);
            }
            case EVENT_ATTACK_HEAVY_OPEN -> {
                stopAllAttackStates();
                this.attackHeavyOpenState.start(this.tickCount);
            }
            case EVENT_ATTACK_HEAVY_CLOSE -> {
                // Para explicitamente a OPEN pra não congelar no último frame
                this.attackHeavyOpenState.stop();
                this.attackHeavyCloseState.start(this.tickCount);
            }
            case EVENT_ATTACK_SPIN -> {
                stopAllAttackStates();
                this.attack2State.start(this.tickCount);
            }
            case EVENT_HIT_STRONG -> {
                stopAllAttackStates();
                this.hitStrongState.start(this.tickCount);
            }
            default -> super.handleEntityEvent(id);
        }
    }

    /**
     * Para todos os AnimationStates de ataque. Necessário pra evitar que o pose
     * congelado do último frame de uma animação previa fique somando com a nova.
     */
    private void stopAllAttackStates() {
        this.attackSimpleState.stop();
        this.attack2State.stop();
        this.attackHeavyOpenState.stop();
        this.attackHeavyCloseState.stop();
        this.hitStrongState.stop();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        int phase = this.getPhase();

        // Fase 4 = nunca usa simple attack
        if (phase >= 4) return false;

        // Cooldown entre simple attacks cresce por fase (p1=rápido, p3=lento)
        if (this.simpleAttackCooldown > 0) return false;
        this.simpleAttackCooldown = switch (phase) {
            case 1 -> 12;  // 0.6s — uso frequente
            case 2 -> 30;  // 1.5s — moderado
            case 3 -> 60;  // 3.0s — raro
            default -> Integer.MAX_VALUE;
        };

        // Broadcast animação de melee antes do dano
        this.level().broadcastEntityEvent(this, EVENT_ATTACK_SIMPLE);
        this.level().playSound(null, this.blockPosition(),
                ModSounds.MUTANT_MELEE.get(), SoundSource.HOSTILE, 1.2F, 1.0F);

        boolean wasBlocking = target instanceof Player p && p.isBlocking();

        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living) {
            applyShieldKnockback(living, wasBlocking);
            // Criaturas de césio + armadura de chumbo full = imunes à radiação
            if (RadioactiveTargets.isCesiumImmune(living)) return hit;
            if (living instanceof Player p && hasFullLeadArmor(p)) return hit;
            living.addEffect(new MobEffectInstance(
                    ModEffects.RADIATION.get(), 300, 2, false, true, true));
        }
        return hit;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Durante a sequência de morte, totalmente invulnerável (até o final liberar)
        if (this.deathSequenceTick > 0 && !this.deathSequenceFinished) return false;

        // Trigger da sequência: HP cairia a zero pela primeira vez
        if (this.deathSequenceTick == 0 && !this.deathSequenceFinished
                && !this.level().isClientSide()
                && this.getHealth() - amount <= 0.0F) {
            this.deathSequenceTick = 1;
            this.setHealth(1.0F); // segura na vida pra rodar o show
            this.getNavigation().stop();
            this.setTarget(null);
            // Levanta os braços (open animation, 3s) — não pisca pra idle/walk porque
            // em deathSequenceTick > 0 o customServerAiStep trava tudo
            this.level().broadcastEntityEvent(this, EVENT_ATTACK_HEAVY_OPEN);
            return false;
        }

        // Durante o combo open/close, imune a tudo EXCETO TNT rebatida pelo player
        if (this.isCastingHeavy) {
            Entity d = source.getDirectEntity();
            Entity c = source.getEntity();
            boolean deflected = d instanceof MutantTntProjectileEntity && c instanceof Player;
            if (!deflected) return false;
        }

        boolean hurt = super.hurt(source, amount);
        if (!hurt || this.level().isClientSide()) return hurt;

        Entity direct = source.getDirectEntity();
        Entity causing = source.getEntity();

        boolean deflectedTnt = direct instanceof MutantTntProjectileEntity
                && causing instanceof Player;
        boolean granade = direct instanceof CesiumGranadeEntity
                || causing instanceof CesiumGranadeEntity;
        boolean explosionByPlayer = source.is(DamageTypes.EXPLOSION)
                && causing instanceof Player;

        if (deflectedTnt || granade || explosionByPlayer) {
            this.level().broadcastEntityEvent(this, EVENT_HIT_STRONG);
            this.hitStrongLock = 44; // ~2.2s, duração da animação hitStrong

            // Knockback forte em direção oposta ao atacante
            if (causing != null) {
                Vec3 kb = this.position().subtract(causing.position()).normalize().scale(1.5);
                this.setDeltaMovement(kb.x, 0.45, kb.z);
                this.hasImpulse = true;
            }
        }
        return hurt;
    }

    /**
     * Aplica dano + radiação fora do caminho do doHurtTarget (que bloqueia na
     * fase 4). Usado pelo SpinAttackGoal — permite que o spin cause dano mesmo
     * quando a melee simples tá desligada.
     */
    public void applyMutantHit(LivingEntity target) {
        boolean wasBlocking = target instanceof Player p && p.isBlocking();

        float damage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);
        target.hurt(this.damageSources().mobAttack(this), damage);

        applyShieldKnockback(target, wasBlocking);

        // Criaturas de césio + armadura de chumbo full = imunes à radiação
        if (RadioactiveTargets.isCesiumImmune(target)) return;
        if (target instanceof Player p && hasFullLeadArmor(p)) return;
        target.addEffect(new MobEffectInstance(
                ModEffects.RADIATION.get(), 300, 2, false, true, true));
    }

    /**
     * Se o target era um Player com shield levantado no momento do hit, arremessa
     * ele longe e desabilita o escudo por 3s (tipo o efeito do machado vanilla).
     */
    public void applyShieldKnockback(LivingEntity target, boolean wasBlocking) {
        if (!wasBlocking || !(target instanceof Player player)) return;

        Vec3 dir = target.position().subtract(this.position()).normalize();
        target.push(dir.x * 3.0, 0.7, dir.z * 3.0);
        target.hurtMarked = true;
        player.getCooldowns().addCooldown(net.minecraft.world.item.Items.SHIELD, 60);
    }

    private static boolean hasFullLeadArmor(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() == ModItems.LEAD_HELMET.get()
                && player.getItemBySlot(EquipmentSlot.CHEST).getItem() == ModItems.LEAD_CHESTPLATE.get()
                && player.getItemBySlot(EquipmentSlot.LEGS).getItem() == ModItems.LEAD_LEGGINGS.get()
                && player.getItemBySlot(EquipmentSlot.FEET).getItem() == ModItems.LEAD_BOOTS.get();
    }
}
