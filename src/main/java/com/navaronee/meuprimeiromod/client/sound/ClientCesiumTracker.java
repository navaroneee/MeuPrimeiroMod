package com.navaronee.meuprimeiromod.client.sound;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID, value = Dist.CLIENT)
public class ClientCesiumTracker {

    private static final Map<ChunkPos, List<BlockPos>> CESIUM_CACHE = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        Level level = (Level) event.getLevel();
        if (!level.isClientSide()) return;

        ChunkPos chunkPos = chunk.getPos();
        List<BlockPos> positions = new ArrayList<>();

        int minX = chunkPos.getMinBlockX();
        int minZ = chunkPos.getMinBlockZ();
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();

        for (int x = minX; x < minX + 16; x++) {
            for (int z = minZ; z < minZ + 16; z++) {
                for (int y = minY; y < maxY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (chunk.getBlockState(pos).is(ModBlocks.CESIUM_ORE.get())) {
                        positions.add(pos.immutable());
                    }
                }
            }
        }

        if (!positions.isEmpty()) {
            CESIUM_CACHE.put(chunkPos, positions);
        } else {
            CESIUM_CACHE.remove(chunkPos);
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!event.getLevel().isClientSide()) return;
        CESIUM_CACHE.remove(event.getChunk().getPos());
    }

    public static double findNearestCesiumDistance(Player player) {
        BlockPos playerPos = player.blockPosition();
        double closestDist = Double.MAX_VALUE;

        for (List<BlockPos> positions : CESIUM_CACHE.values()) {
            for (BlockPos pos : positions) {
                double dist = Math.sqrt(pos.distSqr(playerPos));
                if (dist < closestDist) {
                    closestDist = dist;
                }
            }
        }
        return closestDist;
    }

    public static int getDistanceLevel(Player player) {
        double dist = findNearestCesiumDistance(player);
        if (dist <= 5.0) return 3;
        if (dist <= 10.0) return 2;
        if (dist <= 15.0) return 1;
        return 0;
    }
}
