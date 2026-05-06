package com.navaronee.meuprimeiromod.blockentity.portal;

import com.navaronee.meuprimeiromod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Valida o multiblock do Portal Creator (V2 — pattern shape_3.json, 2×2×3 horizontal).
 *
 * Pattern exportado pelo Shape item (size [2,2,3], controller em pattern (0,0,1)):
 *   pattern X axis → -front (X+ = atrás)
 *   pattern Y axis → +up
 *   pattern Z axis → +right (Z- = left, item_port; Z+ = right, energy_port)
 *
 * Layout em coords (right, up, front) relativo ao controller:
 *   Camada chão (up=0):
 *     front= 0 (linha do ctrl):   ItemPort  CTRL  EnergyPort
 *     front=-1 (atrás do ctrl):   F  F  F                 ← parede traseira baixa
 *   Camada teto (up=+1):
 *     front= 0 (acima do ctrl):   F  F  F
 *     front=-1 (atrás-cima):      F  F  F
 *
 * Total: 11 aux (9 frames + 2 ports) + 1 controller = 12 blocos.
 * Front da máquina (onde portal abre) = direção FACING do controller (sem blocos aí).
 */
public final class MultiblockValidator {

    private MultiblockValidator() {}

    public record SlotEntry(BlockPos pos, Block expected, SlotType type) {}
    public enum SlotType { FRAME, ENERGY_PORT, ITEM_PORT }

    public record Result(boolean valid, List<SlotEntry> missing, List<SlotEntry> slots) {
        public static Result ok(List<SlotEntry> slots) { return new Result(true, List.of(), slots); }
    }

    /** Offsets em (right, up, front) — front é a direção FACING do controller. */
    private record Offset(int dRight, int dUp, int dFront, SlotType type) {}

    private static final Offset[] PATTERN = new Offset[] {
            // chão, linha do ctrl: ports laterais
            new Offset(-1, 0,  0, SlotType.ITEM_PORT),
            new Offset(+1, 0,  0, SlotType.ENERGY_PORT),
            // chão, atrás do ctrl: parede traseira baixa
            new Offset(-1, 0, -1, SlotType.FRAME),
            new Offset( 0, 0, -1, SlotType.FRAME),
            new Offset(+1, 0, -1, SlotType.FRAME),
            // teto, acima do ctrl/ports
            new Offset(-1, +1, 0, SlotType.FRAME),
            new Offset( 0, +1, 0, SlotType.FRAME),
            new Offset(+1, +1, 0, SlotType.FRAME),
            // teto, atrás-cima
            new Offset(-1, +1, -1, SlotType.FRAME),
            new Offset( 0, +1, -1, SlotType.FRAME),
            new Offset(+1, +1, -1, SlotType.FRAME),
    };

    /** Alcance do laser (igual ao mod referência). */
    public static final int LASER_RANGE = 10;

    /** Detalhes da parede detectada (pra portal). */
    public record WallDetection(BlockPos center, int distance) {}

    /**
     * Procura uma parede sólida 7×7 perpendicular ao laser, na direção -FACING.
     * Emitter = controller.up(); wallCenter = laserLine.down() (1 bloco abaixo da
     * linha do laser pro alinhamento visual). Range LASER_RANGE.
     */
    public static java.util.Optional<WallDetection> findWall(Level level, BlockPos controllerPos, Direction facing) {
        // 1:1 com Navarone: laser fires BACKWARD (facing.getOpposite()).
        Direction laserDir = facing.getOpposite();
        Direction right = facing.getCounterClockWise();
        BlockPos emitter = controllerPos.above();
        for (int i = 1; i <= LASER_RANGE; i++) {
            BlockPos laserLine = emitter.relative(laserDir, i);
            BlockPos wallCenter = laserLine.below();
            boolean valid = true;
            for (int dr = -3; dr <= 3 && valid; dr++) {
                for (int du = -3; du <= 3 && valid; du++) {
                    BlockPos check = wallCenter.relative(right, dr).above(du);
                    BlockState bs = level.getBlockState(check);
                    if (bs.isAir() || !bs.isSolidRender(level, check)) valid = false;
                }
            }
            if (valid) return java.util.Optional.of(new WallDetection(wallCenter, i));
        }
        return java.util.Optional.empty();
    }

    /**
     * Verifica se o caminho entre o controller e a parede está livre (sem blocos sólidos).
     * @return distância onde achou obstrução, ou -1 se livre
     */
    /** Checa o caminho do laser (linha única) entre emitter e wall — retorna a
     *  distância da primeira obstrução, ou -1 se livre. Ignora blocos do próprio
     *  multiblock (frames, filler, ports), que ficam dentro/atrás do laser. */
    public static int findObstruction(Level level, BlockPos controllerPos, Direction facing, int wallDist) {
        Direction laserDir = facing.getOpposite();
        BlockPos emitter = controllerPos.above();
        for (int j = 1; j < wallDist; j++) {
            BlockPos check = emitter.relative(laserDir, j);
            BlockState bs = level.getBlockState(check);
            if (bs.isAir()) continue;
            // Pula blocos do próprio multi
            if (bs.is(ModBlocks.ENGINEERING_FRAME.get())
                    || bs.is(ModBlocks.MULTIBLOCK_FILLER.get())
                    || bs.is(ModBlocks.PORTAL_ENERGY_PORT.get())
                    || bs.is(ModBlocks.PORTAL_ITEM_PORT.get())) continue;
            return j;
        }
        return -1;
    }

    public static Result validate(Level level, BlockPos controllerPos, Direction facing) {
        Direction right = facing.getCounterClockWise();
        List<SlotEntry> slots = new ArrayList<>(PATTERN.length);
        for (Offset o : PATTERN) {
            BlockPos worldPos = controllerPos
                    .relative(right, o.dRight())
                    .above(o.dUp())
                    .relative(facing, o.dFront());
            Block expected = switch (o.type()) {
                case FRAME -> ModBlocks.ENGINEERING_FRAME.get();
                case ENERGY_PORT -> ModBlocks.PORTAL_ENERGY_PORT.get();
                case ITEM_PORT -> ModBlocks.PORTAL_ITEM_PORT.get();
            };
            slots.add(new SlotEntry(worldPos, expected, o.type()));
        }
        List<SlotEntry> missing = new ArrayList<>();
        for (SlotEntry s : slots) {
            if (!level.getBlockState(s.pos()).is(s.expected())) missing.add(s);
        }
        return missing.isEmpty() ? Result.ok(slots) : new Result(false, missing, slots);
    }
}
