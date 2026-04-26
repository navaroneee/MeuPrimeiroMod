package com.navaronee.meuprimeiromod.blockentity;

import net.minecraftforge.energy.EnergyStorage;

/**
 * EnergyStorage com callback de mudança (pra setChanged automático na block entity)
 * e método interno useEnergy() pra consumo da máquina, bypassando o maxExtract=0
 * que impede extração externa.
 *
 * Estratégia: maxExtract=0 significa que cabos não conseguem TIRAR energia, mas
 * a máquina pode consumir internamente quando processa.
 */
public class ModEnergyStorage extends EnergyStorage {

    private final Runnable onChange;

    public ModEnergyStorage(int capacity, int maxReceive, Runnable onChange) {
        super(capacity, maxReceive, 0);
        this.onChange = onChange;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received > 0 && !simulate) onChange.run();
        return received;
    }

    /** Consumo interno da máquina — não passa pelo maxExtract. */
    public void useEnergy(int amount) {
        this.energy = Math.max(0, this.energy - amount);
        onChange.run();
    }

    public void setEnergyDirect(int e) {
        this.energy = Math.min(this.capacity, Math.max(0, e));
    }
}
