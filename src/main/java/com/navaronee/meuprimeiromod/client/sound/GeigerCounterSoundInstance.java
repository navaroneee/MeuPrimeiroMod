package com.navaronee.meuprimeiromod.client.sound;

import com.navaronee.meuprimeiromod.block.ModBlocks;
import com.navaronee.meuprimeiromod.item.ModItems;
import com.navaronee.meuprimeiromod.sound.ModSounds;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class GeigerCounterSoundInstance extends AbstractTickableSoundInstance {

    private final Player player;
    private int currentLevel;

    public GeigerCounterSoundInstance(Player player, SoundEvent sound, int level) {
        super(sound, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.player = player;
        this.currentLevel = level;
        this.looping = true;
        this.delay = 0;
        this.volume = getVolumeForLevel(level);
        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    private static float getVolumeForLevel(int level) {
        return switch (level) {
            case 3 -> 0.8F;
            case 2 -> 0.6F;
            default -> 0.4F;
        };
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    @Override
    public void tick() {
        if (!player.isAlive()) {
            stop();
            return;
        }

        boolean holdingCounter = player.getMainHandItem().getItem() == ModItems.GAISER_COUNTER.get()
                || player.getOffhandItem().getItem() == ModItems.GAISER_COUNTER.get();

        if (!holdingCounter) {
            stop();
            return;
        }

        int newLevel = getDistanceLevel(player);
        if (newLevel == 0) {
            stop();
            return;
        }

        if (newLevel != currentLevel) {
            stop();
            return;
        }

        this.x = player.getX();
        this.y = player.getY();
        this.z = player.getZ();
    }

    public static int getDistanceLevel(Player player) {
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();
        double closestDist = Double.MAX_VALUE;
        int range = 15;

        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-range, -range, -range),
                playerPos.offset(range, range, range))) {
            if (level.getBlockState(pos).is(ModBlocks.CESIUM_ORE.get())) {
                double dist = Math.sqrt(pos.distSqr(playerPos));
                if (dist < closestDist) {
                    closestDist = dist;
                }
            }
        }

        if (closestDist <= 5.0) return 3;
        if (closestDist <= 10.0) return 2;
        if (closestDist <= 15.0) return 1;
        return 0;
    }

    public static SoundEvent getSoundForLevel(int level) {
        return switch (level) {
            case 3 -> ModSounds.COUNTER_LV3.get();
            case 2 -> ModSounds.COUNTER_LV2.get();
            default -> ModSounds.COUNTER_LV1.get();
        };
    }
}
