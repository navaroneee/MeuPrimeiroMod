package com.navaronee.meuprimeiromod.blockentity.portal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Implementado por BEs de controllers de multiblock que querem mostrar preview
 * fantasma (ghost blocks) onde os blocos da estrutura precisam ser colocados.
 *
 * O MultiblockGhostRenderer chama getPreviewPositions() pra renderizar cubos
 * translúcidos coloridos por status: verde=correto, vermelho=falta, azul=port,
 * branco=controller, amarelo=guia.
 */
public interface IMultiblockPreviewable {

    List<StructureEntry> getPreviewPositions(BlockPos controllerPos, Direction facing);

    boolean isFormed();

    Direction getFacing();

    enum SlotType { CONTROLLER, CASING, IO_PORT, WALL_GUIDE }

    record StructureEntry(BlockPos position, Block expected, String displayName, SlotType type) {

        public static StructureEntry controller(BlockPos pos) {
            return new StructureEntry(pos, null, "Controller", SlotType.CONTROLLER);
        }
        public static StructureEntry casing(BlockPos pos, Block expected, String name) {
            return new StructureEntry(pos, expected, name, SlotType.CASING);
        }
        public static StructureEntry ioPort(BlockPos pos, Block expected, String name) {
            return new StructureEntry(pos, expected, name, SlotType.IO_PORT);
        }
        public static StructureEntry wallGuide(BlockPos pos) {
            return new StructureEntry(pos, null, "Wall (qualquer solido)", SlotType.WALL_GUIDE);
        }
    }
}
