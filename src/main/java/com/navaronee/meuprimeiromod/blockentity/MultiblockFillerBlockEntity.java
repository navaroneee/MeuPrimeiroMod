package com.navaronee.meuprimeiromod.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * BlockEntity do MultiblockFillerBlock — armazena o BlockState original
 * (frame que o controller substituiu ao formar) e o masterPos pra restaurar
 * quando o multiblock for desfeito.
 */
public class MultiblockFillerBlockEntity extends BlockEntity {

    private BlockState original = Blocks.AIR.defaultBlockState();
    private BlockPos masterPos;

    public MultiblockFillerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MULTIBLOCK_FILLER.get(), pos, state);
    }

    public void setOriginal(BlockState state, BlockPos master) {
        this.original = state;
        this.masterPos = master;
        setChanged();
    }

    public BlockState getOriginal() { return original; }
    public BlockPos getMasterPos() { return masterPos; }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Original", NbtUtils.writeBlockState(original));
        if (masterPos != null) tag.put("Master", NbtUtils.writeBlockPos(masterPos));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.original = NbtUtils.readBlockState(
                BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("Original"));
        if (this.original == null) this.original = Blocks.AIR.defaultBlockState();
        if (tag.contains("Master")) this.masterPos = NbtUtils.readBlockPos(tag.getCompound("Master"));
    }
}
