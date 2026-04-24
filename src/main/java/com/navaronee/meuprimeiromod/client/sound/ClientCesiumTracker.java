package com.navaronee.meuprimeiromod.client.sound;

import com.navaronee.meuprimeiromod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Tracker client-side usado pelo Geiger Counter.
 * Faz scan direto num raio de 15 blocos ao redor do player.
 * PERFORMANCE: scan cacheado por 5 ticks (não roda todo tick).
 */
public class ClientCesiumTracker {

    private static final int SCAN_RADIUS = 15;
    private static final int CACHE_TICKS = 5;

    private static long lastScanTick = -1000;
    private static double cachedDistance = Double.MAX_VALUE;

    public static double findNearestCesiumDistance(Player player) {
        Level level = player.level();
        long now = level.getGameTime();

        // Retorna cached se ainda recente
        if (now - lastScanTick < CACHE_TICKS) {
            return cachedDistance;
        }

        BlockPos playerPos = player.blockPosition();
        double closest = Double.MAX_VALUE;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int dx = -SCAN_RADIUS; dx <= SCAN_RADIUS; dx++) {
            for (int dz = -SCAN_RADIUS; dz <= SCAN_RADIUS; dz++) {
                for (int dy = -SCAN_RADIUS; dy <= SCAN_RADIUS; dy++) {
                    mutable.set(playerPos.getX() + dx, playerPos.getY() + dy, playerPos.getZ() + dz);
                    if (level.getBlockState(mutable).is(ModBlocks.CESIUM_ORE.get())) {
                        double dist = Math.sqrt(mutable.distSqr(playerPos));
                        if (dist < closest) closest = dist;
                    }
                }
            }
        }

        cachedDistance = closest;
        lastScanTick = now;
        return closest;
    }

    public static int getDistanceLevel(Player player) {
        double dist = findNearestCesiumDistance(player);
        if (dist <= 5.0) return 3;
        if (dist <= 10.0) return 2;
        if (dist <= 15.0) return 1;
        return 0;
    }
}
