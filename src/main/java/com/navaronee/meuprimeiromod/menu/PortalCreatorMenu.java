package com.navaronee.meuprimeiromod.menu;

import com.navaronee.meuprimeiromod.block.ModBlocks;
import com.navaronee.meuprimeiromod.blockentity.PortalCreatorBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class PortalCreatorMenu extends AbstractContainerMenu {

    /** Layout fixo em 256x256 (replica Navarone). */
    public static final int FUEL_SLOT_X = 30;
    public static final int FUEL_SLOT_Y = 140;
    public static final int PLAYER_INV_X = 48;
    public static final int PLAYER_INV_Y = 173;
    public static final int PLAYER_HOTBAR_Y = 231;

    private final PortalCreatorBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public PortalCreatorMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv,
                inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(9));
    }

    public PortalCreatorMenu(int id, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.PORTAL_CREATOR.get(), id);
        this.blockEntity = (PortalCreatorBlockEntity) entity;
        this.access = ContainerLevelAccess.create(inv.player.level(), blockEntity.getBlockPos());
        this.data = data;

        // Player inventory + hotbar (posições 256x256)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        PLAYER_INV_X + col * 18, PLAYER_INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, PLAYER_INV_X + col * 18, PLAYER_HOTBAR_Y));
        }

        // Slot 0: ender_pearl_dust input
        this.addSlot(new SlotItemHandler(blockEntity.getItems(),
                PortalCreatorBlockEntity.SLOT_FUEL, FUEL_SLOT_X, FUEL_SLOT_Y));

        addDataSlots(data);
    }

    public int getEnergy() { return data.get(0); }
    public int getEnergyCapacity() { return data.get(1); }
    public boolean isFiring() { return data.get(2) != 0; }
    public int getFiringTicks() { return data.get(3); }
    public boolean isPortalActive() { return data.get(4) != 0; }
    public int getFuel() { return data.get(5); }
    public int getMaxFuel() { return data.get(6); }
    public boolean isFormed() { return data.get(7) != 0; }
    public PortalCreatorBlockEntity.MachineStatus getStatus() {
        int ord = data.get(8);
        PortalCreatorBlockEntity.MachineStatus[] vals = PortalCreatorBlockEntity.MachineStatus.values();
        return vals[Math.min(Math.max(0, ord), vals.length - 1)];
    }

    /**
     * Botões da GUI:
     *  BTN_FIRE → activate(Wasteland) [destino default por enquanto]
     *  BTN_CLOSE → cancela firing
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (!(player instanceof ServerPlayer sp)) return false;
        if (id == PortalCreatorBlockEntity.BTN_FIRE) {
            ResourceKey<Level> dest = ResourceKey.create(
                    Registries.DIMENSION,
                    new ResourceLocation("meuprimeiromod", "wasteland"));
            return blockEntity.activate(sp, dest);
        }
        if (id == PortalCreatorBlockEntity.BTN_CLOSE) {
            if (blockEntity.isPortalActive()) blockEntity.closePortal();
            else blockEntity.cancelFiring();
            return true;
        }
        return false;
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 1;

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (index < VANILLA_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX,
                    TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, 0, VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) sourceSlot.set(ItemStack.EMPTY);
        else sourceSlot.setChanged();
        sourceSlot.onTake(player, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.PORTAL_CREATOR.get());
    }
}
