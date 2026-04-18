package com.navaronee.meuprimeiromod.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Gerencia zonas temporárias de radiação (criadas por granadas).
 *
 * PERFORMANCE:
 * - Lista estática, sem entities extras, sem tickers extras.
 * - Consulta embutida no tick existente do RadiationEvents (zero overhead adicional).
 * - Remoção de zonas expiradas numa única passada quando consultado.
 * - CopyOnWriteArrayList evita lock contention (zonas são adicionadas raramente).
 */
public class RadiationZoneManager {

    public record Zone(BlockPos center, double radius, double radiusSq, long expireTick) {
        public Zone(BlockPos center, double radius, long expireTick) {
            this(center, radius, radius * radius, expireTick);
        }
    }

    private static final List<Zone> ZONES = new CopyOnWriteArrayList<>();

    public static void addZone(BlockPos center, double radius, long expireTick) {
        ZONES.add(new Zone(center.immutable(), radius, expireTick));
    }

    /**
     * Retorna o nível de radiação (0, 1, 2) da zona mais perigosa afetando o player.
     * 0 = fora de qualquer zona
     * 1 = dentro de uma zona mas longe do centro (>50% do raio)
     * 2 = perto do centro (<=50% do raio)
     *
     * Também remove zonas expiradas.
     */
    public static int getRadiationLevel(Entity entity, long currentTick) {
        if (ZONES.isEmpty()) return 0;

        int maxLevel = 0;
        BlockPos entityPos = entity.blockPosition();

        Iterator<Zone> it = ZONES.iterator();
        while (it.hasNext()) {
            Zone zone = it.next();

            if (currentTick >= zone.expireTick) {
                ZONES.remove(zone);
                continue;
            }

            double distSq = zone.center.distSqr(entityPos);
            if (distSq > zone.radiusSq) continue;

            // Dentro da zona. Verifica intensidade.
            int level = distSq <= zone.radiusSq * 0.25 ? 2 : 1;
            if (level > maxLevel) maxLevel = level;
        }

        return maxLevel;
    }

    public static boolean hasActiveZones() {
        return !ZONES.isEmpty();
    }

    public static void clear() {
        ZONES.clear();
    }
}
