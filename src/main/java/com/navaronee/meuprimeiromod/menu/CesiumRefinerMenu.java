package com.navaronee.meuprimeiromod.menu;

import com.navaronee.meuprimeiromod.block.ModBlocks;
import com.navaronee.meuprimeiromod.blockentity.CesiumRefinerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class CesiumRefinerMenu extends AbstractContainerMenu {

    private final CesiumRefinerBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public CesiumRefinerMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv,
                inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(2));
    }

    public CesiumRefinerMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.CESIUM_REFINER.get(), id);
        this.blockEntity = (CesiumRefinerBlockEntity) entity;
        this.access = ContainerLevelAccess.create(inv.player.level(), blockEntity.getBlockPos());
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        // Slots do refinador: input (slot 0) e output (slot 1)
        this.addSlot(new SlotItemHandler(blockEntity.getItems(), CesiumRefinerBlockEntity.SLOT_INPUT, 50, 35));
        this.addSlot(new SlotItemHandler(blockEntity.getItems(), CesiumRefinerBlockEntity.SLOT_OUTPUT, 110, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addDataSlots(data);
    }

    public boolean isProcessing() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = data.get(0);
        int max = data.get(1);
        int arrowSize = 24;
        return max != 0 && progress != 0 ? progress * arrowSize / max : 0;
    }

    // Slot IDs:
    // 0-26: player inventory (3 rows x 9)
    // 27-35: hotbar
    // 36: input
    // 37: output
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 2;

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < VANILLA_SLOT_COUNT) {
            // Do player inventory pro BE — só aceita no input
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // Do BE pro player inventory
            if (!moveItemStackTo(sourceStack, 0, VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.CESIUM_REFINER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
