package com.navaronee.meuprimeiromod.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * BE do DimensionalPortalBlock — guarda master controller pos + axis do facing dele.
 * Quando entidade colide, o portal consulta o controller pro destino atual.
 * O renderer desenha o efeito end_portal nas 2 faces perpendiculares ao facing.
 */
public class DimensionalPortalBlockEntity extends BlockEntity {

    private @Nullable BlockPos masterPos;
    /** Direção FACING do master — define em qual axis o portal "abre" (faces visiveis). */
    private @Nullable Direction facing;

    public DimensionalPortalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIMENSIONAL_PORTAL.get(), pos, state);
    }

    public void setMaster(BlockPos master, Direction facing) {
        this.masterPos = master;
        this.facing = facing;
        setChanged();
    }

    public @Nullable BlockPos getMasterPos() { return masterPos; }
    public @Nullable Direction getFacing() { return facing; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (masterPos != null) tag.put("Master", NbtUtils.writeBlockPos(masterPos));
        if (facing != null) tag.putString("Facing", facing.getName());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Master")) masterPos = NbtUtils.readBlockPos(tag.getCompound("Master"));
        if (tag.contains("Facing")) facing = Direction.byName(tag.getString("Facing"));
    }

    /** Pra BER ser notificado da mudança quando setMaster é chamado server-side. */
    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag t = super.getUpdateTag();
        saveAdditional(t);
        return t;
    }
}
