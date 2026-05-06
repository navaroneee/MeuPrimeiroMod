package com.navaronee.meuprimeiromod.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BlockEntity dos blocos auxiliares ENERGY_PORT e ITEM_PORT.
 *
 * Quando linkado a um master (PortalCreator BlockEntity), delega a capability
 * (ENERGY ou ITEM_HANDLER) pra ele — assim cabos/hoppers de outros mods plugam
 * em qualquer port e a energia/itens fluem pro controller.
 *
 * Sem master setado (não-linked), retorna empty (cap não disponível).
 */
public class MultiblockPortBlockEntity extends BlockEntity {

    public enum PortType { ENERGY, ITEM }

    private final PortType type;
    private @Nullable BlockPos masterPos;

    public MultiblockPortBlockEntity(BlockPos pos, BlockState state, PortType type) {
        super(switch (type) {
            case ENERGY -> ModBlockEntities.PORTAL_ENERGY_PORT.get();
            case ITEM -> ModBlockEntities.PORTAL_ITEM_PORT.get();
        }, pos, state);
        this.type = type;
    }

    /** Factories pros BlockEntityType.Builder.of (que precisa BlockEntitySupplier 2-arg). */
    public static MultiblockPortBlockEntity energy(BlockPos pos, BlockState state) {
        return new MultiblockPortBlockEntity(pos, state, PortType.ENERGY);
    }
    public static MultiblockPortBlockEntity item(BlockPos pos, BlockState state) {
        return new MultiblockPortBlockEntity(pos, state, PortType.ITEM);
    }

    public void linkTo(BlockPos master) {
        this.masterPos = master;
        setChanged();
    }

    public void unlink() {
        this.masterPos = null;
        setChanged();
    }

    public @Nullable BlockPos getMasterPos() { return masterPos; }
    public PortType getPortType() { return type; }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (masterPos == null || level == null) return LazyOptional.empty();
        // Filtra por tipo: energy port só responde ENERGY; item port só ITEM
        if (type == PortType.ENERGY && cap != ForgeCapabilities.ENERGY) return LazyOptional.empty();
        if (type == PortType.ITEM && cap != ForgeCapabilities.ITEM_HANDLER) return LazyOptional.empty();

        BlockEntity master = level.getBlockEntity(masterPos);
        if (master instanceof PortalCreatorBlockEntity portal) {
            return portal.getCapability(cap, side);
        }
        // Master inválido — desliga
        masterPos = null;
        setChanged();
        return LazyOptional.empty();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (masterPos != null) tag.put("Master", NbtUtils.writeBlockPos(masterPos));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Master")) masterPos = NbtUtils.readBlockPos(tag.getCompound("Master"));
    }
}
