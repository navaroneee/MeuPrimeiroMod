package com.navaronee.meuprimeiromod.event;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;


public class EndermanStealNukeGoal extends Goal {

    private static final double RANGE = 32.0;
    private static final int COOLDOWN_AFTER_STEAL = 200;
    private static final int SCAN_INTERVAL = 40;

    private final EnderMan enderman;
    private BlockPos targetNuke;
    private int cooldown = 0;

    public EndermanStealNukeGoal(EnderMan enderman) {
        this.enderman = enderman;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        cooldown = SCAN_INTERVAL; // throttle

        if (enderman.getCarriedBlock() != null) return false;

        BlockPos pos = CesiumNukeTracker.findNearest(enderman.blockPosition(), RANGE);
        if (pos == null) return false;

        this.targetNuke = pos;
        return true;
    }

    @Override
    public void start() {
        if (targetNuke == null) return;

        // Confirma que a bomba ainda está lá
        BlockState state = enderman.level().getBlockState(targetNuke);
        if (!(enderman.level().getBlockState(targetNuke).getBlock() == state.getBlock())
                || state.isAir()) {
            CesiumNukeTracker.remove(targetNuke);
            return;
        }

        // Teleporta pra perto da bomba
        enderman.teleportTo(
                targetNuke.getX() + 0.5,
                targetNuke.getY() + 1,
                targetNuke.getZ() + 0.5);

        // Pega o bloco
        enderman.setCarriedBlock(state);
        enderman.level().removeBlock(targetNuke, false);
        CesiumNukeTracker.remove(targetNuke);

        // Som de satisfação do enderman (risada)
        enderman.level().playSound(null, enderman.getX(), enderman.getY(), enderman.getZ(),
                SoundEvents.ENDERMAN_STARE, SoundSource.HOSTILE, 1.5F, 0.8F);

        // Teleporta pra longe (tenta 16 vezes achar um lugar)
        for (int i = 0; i < 16; i++) {
            double tx = enderman.getX() + (enderman.getRandom().nextDouble() - 0.5) * 80;
            double ty = enderman.getY() + enderman.getRandom().nextInt(32) - 16;
            double tz = enderman.getZ() + (enderman.getRandom().nextDouble() - 0.5) * 80;
            if (enderman.randomTeleport(tx, ty, tz, true)) {
                break;
            }
        }

        cooldown = COOLDOWN_AFTER_STEAL;
    }

    @Override
    public boolean canContinueToUse() {
        return false; // one-shot
    }
}
