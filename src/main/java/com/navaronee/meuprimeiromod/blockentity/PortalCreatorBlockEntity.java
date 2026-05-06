package com.navaronee.meuprimeiromod.blockentity;

import com.navaronee.meuprimeiromod.block.PortalCreatorBlock;
import com.navaronee.meuprimeiromod.blockentity.portal.MultiblockValidator;
import com.navaronee.meuprimeiromod.blockentity.portal.PortalTeleporter;
import com.navaronee.meuprimeiromod.item.ModItems;
import com.navaronee.meuprimeiromod.menu.PortalCreatorMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Portal Creator BlockEntity — controller do multiblock.
 * Gerencia FE storage, slot de ender_pearl_dust, validação e teleporte.
 */
public class PortalCreatorBlockEntity extends BlockEntity implements MenuProvider,
        com.navaronee.meuprimeiromod.blockentity.portal.IMultiblockPreviewable {

    public static final int SLOT_FUEL = 0;
    public static final int ENERGY_CAPACITY = 100_000;
    public static final int ENERGY_RECEIVE = 4_000;
    public static final int ENERGY_TO_FIRE = 50_000;
    public static final int ENERGY_MAINTENANCE = 100;     // RF/t enquanto portal ativo
    public static final int MAX_FUEL_RESERVOIR = 6_400;
    public static final int FUEL_PER_DUST = 100;          // 1 dust = 100 fuel
    public static final int FUEL_PER_SHOT = 200;          // por ativação
    public static final int FIRING_DURATION = 200;        // 10s a 20tps

    public static final int BTN_FIRE = 0;
    public static final int BTN_CLOSE = 1;
    public static final int BTN_NETHER = 0;     // legacy (compat)
    public static final int BTN_WASTELAND = 1;  // legacy (compat)

    /** Estados sincronizados pra GUI mostrar status */
    public enum MachineStatus {
        NOT_FORMED, NO_WALL, OBSTRUCTED, NO_ENERGY, NO_FUEL, READY, FIRING, PORTAL_ACTIVE
    }

    public static final ResourceKey<Level> WASTELAND_DIM = ResourceKey.create(
            Registries.DIMENSION,
            new ResourceLocation("meuprimeiromod", "wasteland"));

    private final ItemStackHandler items = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == SLOT_FUEL && stack.is(ModItems.ENDER_PEARL_DUST.get());
        }
    };
    private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> items);

    private final ModEnergyStorage energy = new ModEnergyStorage(
            ENERGY_CAPACITY, ENERGY_RECEIVE, this::setChanged);
    private LazyOptional<IEnergyStorage> energyHandler = LazyOptional.of(() -> energy);

    /** ContainerData fields — sync server→client pra GUI:
     *   0=energy, 1=maxEnergy, 2=firing(0/1), 3=firingTicks,
     *   4=portalActive(0/1, future), 5=fuel(reservoir),
     *   6=maxFuel, 7=formed(0/1), 8=status(ordinal MachineStatus). */
    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> energy.getEnergyStored();
                case 1 -> energy.getMaxEnergyStored();
                case 2 -> firing ? 1 : 0;
                case 3 -> firingTicks;
                case 4 -> portalActive ? 1 : 0;
                case 5 -> fuelReservoir;
                case 6 -> MAX_FUEL_RESERVOIR;
                case 7 -> getBlockState().getValue(PortalCreatorBlock.FORMED) ? 1 : 0;
                case 8 -> computeStatus().ordinal();
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            if (index == 0) energy.setEnergyDirect(value);
        }
        @Override public int getCount() { return 9; }
    };

    public MachineStatus computeStatus() {
        if (!getBlockState().getValue(PortalCreatorBlock.FORMED)) return MachineStatus.NOT_FORMED;
        if (portalActive) return MachineStatus.PORTAL_ACTIVE;
        if (firing) return MachineStatus.FIRING;
        if (level != null) {
            Direction facing = getBlockState().getValue(PortalCreatorBlock.FACING);
            var wallOpt = MultiblockValidator.findWall(level, getBlockPos(), facing);
            if (wallOpt.isEmpty()) return MachineStatus.NO_WALL;
            int obstr = MultiblockValidator.findObstruction(level, getBlockPos(), facing, wallOpt.get().distance());
            if (obstr > 0) return MachineStatus.OBSTRUCTED;
        }
        if (energy.getEnergyStored() < ENERGY_TO_FIRE) return MachineStatus.NO_ENERGY;
        if (fuelReservoir < FUEL_PER_SHOT) return MachineStatus.NO_FUEL;
        return MachineStatus.READY;
    }

    /** Posições dos ports linkados (mantidas em NBT pra unlink ao quebrar/unform). */
    private final List<BlockPos> linkedPorts = new ArrayList<>();
    /** Posições onde frames foram substituídos por filler invisível. */
    private final List<BlockPos> filledFrames = new ArrayList<>();
    /** Flag transient — true enquanto unform() está restaurando, pra fillers não chamarem unform recursivo. */
    private boolean unforming = false;
    /** Reserva de fuel (drenada do slot a cada tick). 1 dust = FUEL_PER_DUST. */
    private int fuelReservoir = 0;
    /** Estado de tiro/ativação. */
    private boolean firing = false;
    private int firingTicks = 0;
    /** Game time absoluto quando firing começou — pra renderer client computar fase. */
    private long firingStartGameTime = 0L;
    /** Destino do firing atual. */
    private @Nullable ResourceKey<Level> firingDestKey = null;
    private @Nullable ServerPlayer firingPlayer = null;
    /** Estado do portal aberto. */
    private boolean portalActive = false;
    private @Nullable ResourceKey<Level> activeDestination = null;
    /** Posição central da parede onde o portal foi aberto. */
    private @Nullable BlockPos wallCenter = null;
    /** Skeleton dos blocos da wall que viraram portal — pra restaurar ao fechar. */
    private final java.util.List<BlockPos> portalBlocks = new ArrayList<>();
    private final java.util.Map<BlockPos, BlockState> wallSkeleton = new java.util.HashMap<>();

    public long getFiringStartGameTime() { return firingStartGameTime; }
    public boolean isPortalActive() { return portalActive; }
    public @Nullable ResourceKey<Level> getActiveDestination() { return activeDestination; }

    public PortalCreatorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PORTAL_CREATOR.get(), pos, state);
    }

    public List<BlockPos> getLinkedPorts() { return linkedPorts; }
    public boolean isUnforming() { return unforming; }

    private int readyCheckCooldown = 0;

    public void serverTick() {
        if (level == null || level.isClientSide()) return;
        boolean formed = getBlockState().getValue(PortalCreatorBlock.FORMED);

        if (!formed) {
            // READY check periódico (1s) só quando não formado
            if (--readyCheckCooldown <= 0) {
                readyCheckCooldown = 20;
                Direction facing = getBlockState().getValue(PortalCreatorBlock.FACING);
                boolean valid = MultiblockValidator.validate(level, getBlockPos(), facing).valid();
                if (getBlockState().getValue(PortalCreatorBlock.READY) != valid) {
                    level.setBlock(getBlockPos(),
                            getBlockState().setValue(PortalCreatorBlock.READY, valid), 3);
                }
            }
            return;
        }

        // Drena 1 item do slot pra reservoir se couber (todo tick — barato)
        if (fuelReservoir + FUEL_PER_DUST <= MAX_FUEL_RESERVOIR) {
            ItemStack stack = items.getStackInSlot(SLOT_FUEL);
            if (!stack.isEmpty() && stack.is(ModItems.ENDER_PEARL_DUST.get())) {
                stack.shrink(1);
                fuelReservoir += FUEL_PER_DUST;
                setChanged();
            }
        }

        // Animação de firing — quando completa, abre portal
        if (firing) {
            firingTicks++;
            if (firingTicks >= FIRING_DURATION) {
                completeFiring();
            }
            setChanged();
        }

        // Manutenção do portal ativo: drena ENERGY_MAINTENANCE/tick. Se zerar, fecha.
        if (portalActive) {
            if (energy.getEnergyStored() < ENERGY_MAINTENANCE) {
                closePortal();
            } else {
                energy.useEnergy(ENERGY_MAINTENANCE);
            }
        }
    }

    private void completeFiring() {
        firing = false;
        firingTicks = 0;
        if (level == null || level.isClientSide() || wallCenter == null) {
            firingPlayer = null;
            firingDestKey = null;
            return;
        }
        // Coloca 5×5 portal blocks centrados em wallCenter, salvando skeleton
        Direction facing = getBlockState().getValue(PortalCreatorBlock.FACING);
        Direction right = facing.getCounterClockWise();
        portalBlocks.clear();
        wallSkeleton.clear();
        for (int dr = -2; dr <= 2; dr++) {
            for (int du = -2; du <= 2; du++) {
                BlockPos p = wallCenter.relative(right, dr).above(du);
                wallSkeleton.put(p, level.getBlockState(p));
                level.setBlock(p, com.navaronee.meuprimeiromod.block.ModBlocks.DIMENSIONAL_PORTAL.get().defaultBlockState(), 3);
                BlockEntity be = level.getBlockEntity(p);
                if (be instanceof com.navaronee.meuprimeiromod.blockentity.DimensionalPortalBlockEntity dp) {
                    dp.setMaster(getBlockPos(), facing);
                }
                portalBlocks.add(p);
            }
        }
        portalActive = true;
        level.playSound(null, getBlockPos(),
                SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 1.5F, 1.0F);
        firingPlayer = null;
        firingDestKey = null;
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),
                net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
    }

    /** Fecha o portal: restaura a parede a partir do skeleton. */
    public void closePortal() {
        if (level == null || level.isClientSide() || !portalActive) return;
        for (BlockPos p : portalBlocks) {
            BlockState orig = wallSkeleton.get(p);
            if (orig != null && level.getBlockState(p).is(com.navaronee.meuprimeiromod.block.ModBlocks.DIMENSIONAL_PORTAL.get())) {
                level.setBlock(p, orig, 3);
            }
        }
        portalBlocks.clear();
        wallSkeleton.clear();
        portalActive = false;
        activeDestination = null;
        wallCenter = null;
        level.playSound(null, getBlockPos(),
                SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.8F, 1.5F);
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),
                net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
    }

    /** Implementação de IMultiblockPreviewable — lista todas posições da estrutura
     *  + perímetro da wall 7×7 a 4 blocos à frente como guia visual. */
    @Override
    public java.util.List<com.navaronee.meuprimeiromod.blockentity.portal.IMultiblockPreviewable.StructureEntry>
    getPreviewPositions(BlockPos ctrlPos, Direction facing) {
        java.util.List<com.navaronee.meuprimeiromod.blockentity.portal.IMultiblockPreviewable.StructureEntry> list = new ArrayList<>();
        list.add(com.navaronee.meuprimeiromod.blockentity.portal.IMultiblockPreviewable.StructureEntry.controller(ctrlPos));
        boolean formed = isFormed();
        // Slots do multiblock — só mostra quando NÃO formed (depois de formed, frames
        // viraram filler invisível, mostrar entries causa "vermelho fantasma" por o
        // bloco real ser filler, não frame).
        if (!formed && level != null) {
            MultiblockValidator.Result r = MultiblockValidator.validate(level, ctrlPos, facing);
            for (MultiblockValidator.SlotEntry slot : r.slots()) {
                switch (slot.type()) {
                    case FRAME -> list.add(com.navaronee.meuprimeiromod.blockentity.portal.IMultiblockPreviewable.StructureEntry
                            .casing(slot.pos(), slot.expected(), "Engineering Frame"));
                    case ENERGY_PORT -> list.add(com.navaronee.meuprimeiromod.blockentity.portal.IMultiblockPreviewable.StructureEntry
                            .ioPort(slot.pos(), slot.expected(), "Energy Port"));
                    case ITEM_PORT -> list.add(com.navaronee.meuprimeiromod.blockentity.portal.IMultiblockPreviewable.StructureEntry
                            .ioPort(slot.pos(), slot.expected(), "Item Port"));
                }
            }
        }
        // Wall guide: 1:1 com Navarone — laser fires BACKWARD (facing.getOpposite()).
        Direction right = facing.getCounterClockWise();
        Direction laserDir = facing.getOpposite();
        BlockPos emitter = ctrlPos.above();
        BlockPos wallCenter;
        if (level != null) {
            var walloOpt = MultiblockValidator.findWall(level, ctrlPos, facing);
            wallCenter = walloOpt.map(MultiblockValidator.WallDetection::center)
                    .orElse(emitter.relative(laserDir, MultiblockValidator.LASER_RANGE).below());
        } else {
            wallCenter = emitter.relative(laserDir, MultiblockValidator.LASER_RANGE).below();
        }
        for (int dr = -3; dr <= 3; dr++) {
            for (int du = -3; du <= 3; du++) {
                BlockPos p = wallCenter.relative(right, dr).above(du);
                list.add(com.navaronee.meuprimeiromod.blockentity.portal.IMultiblockPreviewable.StructureEntry.wallGuide(p));
            }
        }
        return list;
    }

    @Override
    public boolean isFormed() {
        return getBlockState().getValue(PortalCreatorBlock.FORMED);
    }

    @Override
    public Direction getFacing() {
        return getBlockState().getValue(PortalCreatorBlock.FACING);
    }

    private String statusMessage(MachineStatus s) {
        return switch (s) {
            case NOT_FORMED -> "Multiblock nao formado";
            case NO_WALL -> "Parede 7x7 nao encontrada na frente";
            case OBSTRUCTED -> "Caminho obstruido ate a parede";
            case NO_ENERGY -> "Energia insuficiente";
            case NO_FUEL -> "Fuel insuficiente";
            case FIRING -> "Ja firing";
            case PORTAL_ACTIVE -> "Portal ja ativo";
            default -> "Pronto";
        };
    }

    /**
     * Validação do multiblock (V2). Wrench dispara isso.
     * Padrão facing-relative 3×3 wall com 6 frames + 2 ports.
     * Linka os ports ao master se válido.
     */
    public void tryAssemble(Player player) {
        if (level == null) return;
        Direction facing = getBlockState().getValue(PortalCreatorBlock.FACING);
        MultiblockValidator.Result result = MultiblockValidator.validate(level, getBlockPos(), facing);

        if (result.valid()) {
            // Linka ports + substitui frames por filler invisível
            linkedPorts.clear();
            filledFrames.clear();
            for (MultiblockValidator.SlotEntry slot : result.slots()) {
                switch (slot.type()) {
                    case ENERGY_PORT, ITEM_PORT -> {
                        BlockEntity be = level.getBlockEntity(slot.pos());
                        if (be instanceof MultiblockPortBlockEntity port) {
                            port.linkTo(getBlockPos());
                            linkedPorts.add(slot.pos());
                            // Flip LINKED state pra ficar invisível
                            BlockState ps = level.getBlockState(slot.pos());
                            if (slot.type() == MultiblockValidator.SlotType.ENERGY_PORT) {
                                level.setBlock(slot.pos(),
                                        ps.setValue(com.navaronee.meuprimeiromod.block.PortalEnergyPortBlock.LINKED, true), 3);
                            } else {
                                level.setBlock(slot.pos(),
                                        ps.setValue(com.navaronee.meuprimeiromod.block.PortalItemPortBlock.LINKED, true), 3);
                            }
                        }
                    }
                    case FRAME -> {
                        BlockState original = level.getBlockState(slot.pos());
                        level.setBlock(slot.pos(),
                                com.navaronee.meuprimeiromod.block.ModBlocks.MULTIBLOCK_FILLER.get().defaultBlockState(), 3);
                        BlockEntity be = level.getBlockEntity(slot.pos());
                        if (be instanceof com.navaronee.meuprimeiromod.blockentity.MultiblockFillerBlockEntity filler) {
                            filler.setOriginal(original, getBlockPos());
                        }
                        filledFrames.add(slot.pos());
                    }
                }
            }
            level.setBlock(getBlockPos(),
                    getBlockState().setValue(PortalCreatorBlock.FORMED, true), 3);
            setChanged();
            player.displayClientMessage(Component.literal("§aPortal Creator formado!"), false);
            level.playSound(null, getBlockPos(),
                    SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.2F);
        } else {
            unform();
            player.displayClientMessage(Component.literal(
                    "§cFaltam " + result.missing().size() + " blocos no padrão (frames + ports)."), false);
            level.playSound(null, getBlockPos(),
                    SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 0.8F, 0.6F);
        }
    }

    /**
     * Desfaz a formação: unlinka ports + zera FORMED.
     * Chamado quando wrench falha validação ou aux block é quebrado.
     */
    public void unform() {
        if (level == null || unforming) return;
        unforming = true;
        try {
            // Fecha portal aberto antes de desformar
            if (portalActive) closePortal();
            cancelFiring();
            // Unlinka ports + reseta LINKED=false pra voltar a renderizar
            for (BlockPos portPos : linkedPorts) {
                BlockEntity be = level.getBlockEntity(portPos);
                if (be instanceof MultiblockPortBlockEntity port) port.unlink();
                BlockState ps = level.getBlockState(portPos);
                if (ps.getBlock() instanceof com.navaronee.meuprimeiromod.block.PortalEnergyPortBlock) {
                    level.setBlock(portPos,
                            ps.setValue(com.navaronee.meuprimeiromod.block.PortalEnergyPortBlock.LINKED, false), 3);
                } else if (ps.getBlock() instanceof com.navaronee.meuprimeiromod.block.PortalItemPortBlock) {
                    level.setBlock(portPos,
                            ps.setValue(com.navaronee.meuprimeiromod.block.PortalItemPortBlock.LINKED, false), 3);
                }
            }
            linkedPorts.clear();
            // Restaura frames a partir do BlockState salvo no filler.
            // Pula posições onde o filler já foi quebrado (player não ganha frame de graça).
            for (BlockPos fillerPos : filledFrames) {
                BlockState atPos = level.getBlockState(fillerPos);
                if (!atPos.is(com.navaronee.meuprimeiromod.block.ModBlocks.MULTIBLOCK_FILLER.get())) continue;
                BlockState original = com.navaronee.meuprimeiromod.block.ModBlocks.ENGINEERING_FRAME.get().defaultBlockState();
                BlockEntity be = level.getBlockEntity(fillerPos);
                if (be instanceof com.navaronee.meuprimeiromod.blockentity.MultiblockFillerBlockEntity filler) {
                    BlockState saved = filler.getOriginal();
                    if (!saved.isAir()) original = saved;
                }
                level.setBlock(fillerPos, original, 3);
            }
            filledFrames.clear();
            if (getBlockState().getValue(PortalCreatorBlock.FORMED)) {
                level.setBlock(getBlockPos(),
                        getBlockState().setValue(PortalCreatorBlock.FORMED, false), 3);
            }
            setChanged();
        } finally {
            unforming = false;
        }
    }

    /**
     * Ativa o portal — drena recursos e inicia animação de firing.
     * Teleporte acontece após FIRING_DURATION ticks (em completeFiring).
     */
    public boolean activate(ServerPlayer player, ResourceKey<Level> destKey) {
        if (level == null || level.isClientSide()) return false;
        if (firing || portalActive) return false;
        MachineStatus st = computeStatus();
        if (st != MachineStatus.READY) {
            player.displayClientMessage(Component.literal("§c" + statusMessage(st)), true);
            return false;
        }
        // Cacheia wall na hora do firing pra usar em completeFiring
        Direction facing = getBlockState().getValue(PortalCreatorBlock.FACING);
        var wallOpt = MultiblockValidator.findWall(level, getBlockPos(), facing);
        if (wallOpt.isEmpty()) {
            player.displayClientMessage(Component.literal("§cParede nao encontrada."), true);
            return false;
        }
        wallCenter = wallOpt.get().center();
        activeDestination = destKey;
        // Drena recursos
        energy.useEnergy(ENERGY_TO_FIRE);
        fuelReservoir = Math.max(0, fuelReservoir - FUEL_PER_SHOT);
        // Inicia firing animation
        firing = true;
        firingTicks = 0;
        firingStartGameTime = level.getGameTime();
        firingPlayer = player;
        firingDestKey = destKey;
        level.playSound(null, getBlockPos(),
                SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 1.0F, 1.0F);
        setChanged();
        // Notifica cliente (sync da animação)
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),
                net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
        return true;
    }

    /** Cancela firing em progresso (botão Close ou unform). */
    public void cancelFiring() {
        if (!firing) return;
        firing = false;
        firingTicks = 0;
        firingPlayer = null;
        firingDestKey = null;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),
                    net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        SimpleContainer container = new SimpleContainer(items.getSlots());
        for (int i = 0; i < items.getSlots(); i++) container.setItem(i, items.getStackInSlot(i));
        Containers.dropContents(level, pos, container);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.meuprimeiromod.portal_creator");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new PortalCreatorMenu(id, inventory, this, this.data);
    }

    public ContainerData getContainerData() { return data; }
    public ItemStackHandler getItems() { return items; }
    public ModEnergyStorage getEnergy() { return energy; }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return itemHandler.cast();
        if (cap == ForgeCapabilities.ENERGY) return energyHandler.cast();
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
        tag.putInt("Energy", energy.getEnergyStored());
        ListTag list = new ListTag();
        for (BlockPos p : linkedPorts) list.add(NbtUtils.writeBlockPos(p));
        tag.put("LinkedPorts", list);
        ListTag fl = new ListTag();
        for (BlockPos p : filledFrames) fl.add(NbtUtils.writeBlockPos(p));
        tag.put("FilledFrames", fl);
        tag.putInt("FuelReservoir", fuelReservoir);
        tag.putBoolean("Firing", firing);
        tag.putInt("FiringTicks", firingTicks);
        tag.putLong("FiringStartGameTime", firingStartGameTime);
        tag.putBoolean("PortalActive", portalActive);
        if (activeDestination != null) tag.putString("ActiveDest", activeDestination.location().toString());
        if (wallCenter != null) tag.put("WallCenter", NbtUtils.writeBlockPos(wallCenter));
        ListTag pb = new ListTag();
        for (BlockPos p : portalBlocks) pb.add(NbtUtils.writeBlockPos(p));
        tag.put("PortalBlocks", pb);
        ListTag sk = new ListTag();
        for (java.util.Map.Entry<BlockPos, BlockState> e : wallSkeleton.entrySet()) {
            CompoundTag c = new CompoundTag();
            c.put("Pos", NbtUtils.writeBlockPos(e.getKey()));
            c.put("State", NbtUtils.writeBlockState(e.getValue()));
            sk.add(c);
        }
        tag.put("WallSkeleton", sk);
    }

    /** Sync inicial pro cliente quando o chunk carrega. */
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    /** Sync incremental quando server faz sendBlockUpdated. */
    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.deserializeNBT(tag.getCompound("Items"));
        energy.setEnergyDirect(tag.getInt("Energy"));
        linkedPorts.clear();
        ListTag list = tag.getList("LinkedPorts", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            linkedPorts.add(NbtUtils.readBlockPos(list.getCompound(i)));
        }
        filledFrames.clear();
        ListTag fl = tag.getList("FilledFrames", Tag.TAG_COMPOUND);
        for (int i = 0; i < fl.size(); i++) {
            filledFrames.add(NbtUtils.readBlockPos(fl.getCompound(i)));
        }
        fuelReservoir = tag.getInt("FuelReservoir");
        firing = tag.getBoolean("Firing");
        firingTicks = tag.getInt("FiringTicks");
        firingStartGameTime = tag.getLong("FiringStartGameTime");
        portalActive = tag.getBoolean("PortalActive");
        if (tag.contains("ActiveDest")) {
            activeDestination = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION,
                    new ResourceLocation(tag.getString("ActiveDest")));
        } else activeDestination = null;
        wallCenter = tag.contains("WallCenter") ? NbtUtils.readBlockPos(tag.getCompound("WallCenter")) : null;
        portalBlocks.clear();
        ListTag pb = tag.getList("PortalBlocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < pb.size(); i++) portalBlocks.add(NbtUtils.readBlockPos(pb.getCompound(i)));
        wallSkeleton.clear();
        ListTag sk = tag.getList("WallSkeleton", Tag.TAG_COMPOUND);
        for (int i = 0; i < sk.size(); i++) {
            CompoundTag c = sk.getCompound(i);
            BlockPos p = NbtUtils.readBlockPos(c.getCompound("Pos"));
            BlockState s = NbtUtils.readBlockState(net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup(),
                    c.getCompound("State"));
            if (s != null) wallSkeleton.put(p, s);
        }
    }

    public int getFuelReservoir() { return fuelReservoir; }
    public int getMaxFuelReservoir() { return MAX_FUEL_RESERVOIR; }
    public boolean isFiring() { return firing; }
    public int getFiringTicks() { return firingTicks; }
}
