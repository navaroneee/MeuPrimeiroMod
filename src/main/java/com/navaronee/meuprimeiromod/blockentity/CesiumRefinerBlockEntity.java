package com.navaronee.meuprimeiromod.blockentity;

import com.navaronee.meuprimeiromod.item.ModItems;
import com.navaronee.meuprimeiromod.menu.CesiumRefinerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CesiumRefinerBlockEntity extends BlockEntity implements MenuProvider {

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int PROCESS_TIME = 200; // 10 segundos

    // Energia: 60.000 FE buffer, recebe até 200 FE/tick (cabos), gasta 50 FE/tick processando
    public static final int ENERGY_CAPACITY = 60_000;
    public static final int ENERGY_RECEIVE = 200;
    public static final int ENERGY_PER_TICK = 50;

    private final ItemStackHandler items = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == SLOT_INPUT) return stack.is(ModItems.CESIUM_DUST.get());
            return false; // output nunca aceita inserção externa
        }
    };

    private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> items);

    private final ModEnergyStorage energy = new ModEnergyStorage(
            ENERGY_CAPACITY, ENERGY_RECEIVE, this::setChanged);
    private LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);

    private int progress = 0;
    private int lastSyncedProgress = -1;
    private int lastSyncedEnergy = -1;

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> PROCESS_TIME;
                case 2 -> energy.getEnergyStored();
                case 3 -> energy.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 2 -> energy.setEnergyDirect(value);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public CesiumRefinerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CESIUM_REFINER.get(), pos, state);
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public void serverTick() {
        // PERFORMANCE: early returns. Se não há trabalho a fazer, nada é feito.
        ItemStack input = items.getStackInSlot(SLOT_INPUT);
        if (input.isEmpty()) {
            if (progress != 0) {
                progress = 0;
                setChanged();
            }
            return;
        }

        if (!input.is(ModItems.CESIUM_DUST.get())) {
            if (progress != 0) {
                progress = 0;
                setChanged();
            }
            return;
        }

        ItemStack output = items.getStackInSlot(SLOT_OUTPUT);
        ItemStack result = new ItemStack(ModItems.REFINED_CESIUM.get());

        // Checa se pode colocar o resultado no output
        if (!output.isEmpty()) {
            if (!ItemStack.isSameItemSameTags(output, result)) return;
            if (output.getCount() + result.getCount() > output.getMaxStackSize()) return;
        }

        // Sem energia? Não progride
        if (energy.getEnergyStored() < ENERGY_PER_TICK) return;
        energy.useEnergy(ENERGY_PER_TICK);

        progress++;

        if (progress >= PROCESS_TIME) {
            // Completa o processo
            input.shrink(1);
            if (output.isEmpty()) {
                items.setStackInSlot(SLOT_OUTPUT, result.copy());
            } else {
                output.grow(result.getCount());
            }
            progress = 0;
            setChanged();
            lastSyncedProgress = 0;
        } else {
            // PERFORMANCE: sync client só quando progresso muda significativamente
            // (evita flood de packets a cada tick)
            if (progress - lastSyncedProgress >= 5 || progress == 1) {
                setChanged();
                lastSyncedProgress = progress;
            }
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        SimpleContainer container = new SimpleContainer(items.getSlots());
        for (int i = 0; i < items.getSlots(); i++) {
            container.setItem(i, items.getStackInSlot(i));
        }
        net.minecraft.world.Containers.dropContents(level, pos, container);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.meuprimeiromod.cesium_refiner");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new CesiumRefinerMenu(id, inventory, this, this.data);
    }

    public ContainerData getContainerData() {
        return data;
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
        energyHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = LazyOptional.of(() -> items);
        energyHandler = LazyOptional.of(() -> energy);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", items.serializeNBT());
        tag.putInt("Progress", progress);
        tag.putInt("Energy", energy.getEnergyStored());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.deserializeNBT(tag.getCompound("Items"));
        progress = tag.getInt("Progress");
        energy.setEnergyDirect(tag.getInt("Energy"));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.put("Items", items.serializeNBT());
        tag.putInt("Progress", progress);
        tag.putInt("Energy", energy.getEnergyStored());
        return tag;
    }

    public ModEnergyStorage getEnergy() {
        return energy;
    }

    @Override
    public @Nullable net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    public int getProgress() {
        return progress;
    }
}
