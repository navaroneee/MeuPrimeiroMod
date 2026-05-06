package com.navaronee.meuprimeiromod.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fonte criativa de RF — exporta Integer.MAX_VALUE FE/tick pros 6 lados.
 * Bloco de teste; não recebe energia, só fornece.
 */
public class CreativeEnergyBlockEntity extends BlockEntity {

    /** Singleton storage — sem state, infinito. */
    private static final IEnergyStorage INFINITE = new IEnergyStorage() {
        @Override public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }
        @Override public int extractEnergy(int maxExtract, boolean simulate) { return maxExtract; }
        @Override public int getEnergyStored() { return Integer.MAX_VALUE; }
        @Override public int getMaxEnergyStored() { return Integer.MAX_VALUE; }
        @Override public boolean canExtract() { return true; }
        @Override public boolean canReceive() { return false; }
    };

    private final LazyOptional<IEnergyStorage> handler = LazyOptional.of(() -> INFINITE);

    public CreativeEnergyBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_ENERGY.get(), pos, state);
    }

    public void serverTick() {
        if (level == null || level.isClientSide()) return;
        // Empurra max FE pros 6 lados todo tick. Receptor cappa no que aguenta.
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = getBlockPos().relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) continue;
            neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(storage -> {
                if (storage.canReceive()) storage.receiveEnergy(Integer.MAX_VALUE, false);
            });
        }
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) return handler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
    }
}
