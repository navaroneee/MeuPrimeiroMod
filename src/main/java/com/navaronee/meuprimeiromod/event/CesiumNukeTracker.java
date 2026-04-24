package com.navaronee.meuprimeiromod.event;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracker de bombas de cesio colocadas no mundo.
 * Set pequeno atualizado em place/break — consulta O(N) leve (N = bombas ativas).
 */
@Mod.EventBusSubscriber(modid = MeuPrimeiroMod.MODID)
public class CesiumNukeTracker {

    private static final Set<BlockPos> NUKE_POSITIONS = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getPlacedBlock().is(ModBlocks.CESIUM_NUKE.get())) {
            NUKE_POSITIONS.add(event.getPos().immutable());
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (event.getState().is(ModBlocks.CESIUM_NUKE.get())) {
            NUKE_POSITIONS.remove(event.getPos());
        }
    }

    /**
     * Encontra a bomba mais próxima de uma posição dentro de um raio.
     * Retorna null se não houver nenhuma no range.
     */
    public static BlockPos findNearest(BlockPos from, double maxDistance) {
        if (NUKE_POSITIONS.isEmpty()) return null;

        double maxDistSq = maxDistance * maxDistance;
        BlockPos closest = null;
        double closestDistSq = maxDistSq;

        for (BlockPos pos : NUKE_POSITIONS) {
            double dsq = pos.distSqr(from);
            if (dsq < closestDistSq) {
                closestDistSq = dsq;
                closest = pos;
            }
        }
        return closest;
    }

    public static void remove(BlockPos pos) {
        NUKE_POSITIONS.remove(pos);
    }
}
