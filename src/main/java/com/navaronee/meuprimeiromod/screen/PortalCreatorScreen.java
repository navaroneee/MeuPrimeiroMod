package com.navaronee.meuprimeiromod.screen;

import com.navaronee.meuprimeiromod.blockentity.PortalCreatorBlockEntity;
import com.navaronee.meuprimeiromod.menu.PortalCreatorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI 256x256 do Portal Creator. Layout 1:1 com a versão Navarone:
 *   - barra de FE vertical (gradient vermelho) à esquerda
 *   - barra de fuel vertical (textura tilada de ender_pearl_dust) ao lado
 *   - slot de fuel na base da barra
 *   - botões Fire/Close no centro
 *   - status text + valores
 */
public class PortalCreatorScreen extends AbstractContainerScreen<PortalCreatorMenu> {

    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 256;

    private static final int ENERGY_BAR_X = 7, ENERGY_BAR_Y = 6, ENERGY_BAR_W = 9, ENERGY_BAR_H = 150;
    private static final int FUEL_BAR_X = 19, FUEL_BAR_Y = 6, FUEL_BAR_W = 8, FUEL_BAR_H = 150;
    private static final int INFO_X = 50, INFO_Y = 20;

    private static final int FIRE_BTN_X = 50, FIRE_BTN_Y = 115, BTN_W = 80, BTN_H = 20;
    private static final int CLOSE_BTN_X = 50, CLOSE_BTN_Y = 140;

    private static final int ENERGY_TOP_COLOR = 0xFFD65C5C;
    private static final int ENERGY_BOTTOM_COLOR = 0xFF8F2F2F;

    private Button fireButton;
    private Button closeButton;

    public PortalCreatorScreen(PortalCreatorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
        this.inventoryLabelY = -9999; // hide vanilla "Inventory" label (out of view)
        this.titleLabelX = 6;
        this.titleLabelY = -9999;
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos;
        int y = topPos;

        fireButton = Button.builder(Component.literal("Fire"),
                btn -> sendButtonClick(PortalCreatorBlockEntity.BTN_FIRE))
                .bounds(x + FIRE_BTN_X, y + FIRE_BTN_Y, BTN_W, BTN_H).build();
        closeButton = Button.builder(Component.literal("Close"),
                btn -> sendButtonClick(PortalCreatorBlockEntity.BTN_CLOSE))
                .bounds(x + CLOSE_BTN_X, y + CLOSE_BTN_Y, BTN_W, BTN_H).build();
        addRenderableWidget(fireButton);
        addRenderableWidget(closeButton);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        // Fire só habilita em READY; Close só com firing/portal ativo
        var status = menu.getStatus();
        fireButton.active = status == PortalCreatorBlockEntity.MachineStatus.READY;
        closeButton.active = menu.isFiring() || menu.isPortalActive();
    }

    private void sendButtonClick(int id) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;

        gfx.blit(ModGuiTextures.PORTAL_CREATOR_GUI, x, y, 0, 0, GUI_WIDTH, GUI_HEIGHT, GUI_WIDTH, GUI_HEIGHT);

        // Slot template atrás do slot de fuel (FUEL_SLOT_X/Y do Menu, -1 pra alinhar a borda)
        gfx.blit(ModGuiTextures.SLOT_TEMPLATE,
                x + com.navaronee.meuprimeiromod.menu.PortalCreatorMenu.FUEL_SLOT_X - 1,
                y + com.navaronee.meuprimeiromod.menu.PortalCreatorMenu.FUEL_SLOT_Y - 1,
                0, 0, ModGuiTextures.SLOT_SIZE, ModGuiTextures.SLOT_SIZE,
                ModGuiTextures.SLOT_SIZE, ModGuiTextures.SLOT_SIZE);

        // Energy bar fill (gradient vermelho)
        int energy = menu.getEnergy();
        int maxEnergy = menu.getEnergyCapacity();
        if (maxEnergy > 0 && energy > 0) {
            int barH = (int) ((float) energy / maxEnergy * ENERGY_BAR_H);
            int yOff = ENERGY_BAR_H - barH;
            gfx.fillGradient(
                    x + ENERGY_BAR_X, y + ENERGY_BAR_Y + yOff,
                    x + ENERGY_BAR_X + ENERGY_BAR_W, y + ENERGY_BAR_Y + ENERGY_BAR_H,
                    ENERGY_TOP_COLOR, ENERGY_BOTTOM_COLOR);
        }

        // Fuel bar fill (textura ender_pearl_dust tilada verticalmente)
        int fuel = menu.getFuel();
        int maxFuel = menu.getMaxFuel();
        if (maxFuel > 0 && fuel > 0) {
            int barH = (int) ((float) fuel / maxFuel * FUEL_BAR_H);
            int yOff = FUEL_BAR_H - barH;
            int drawY = y + FUEL_BAR_Y + yOff;
            int remaining = barH;
            int tileSize = 16;
            while (remaining > 0) {
                int seg = Math.min(remaining, tileSize);
                gfx.blit(ModGuiTextures.ENDER_PEARL_DUST_TEX,
                        x + FUEL_BAR_X, drawY,
                        0, 0, FUEL_BAR_W, seg, 16, 16);
                drawY += seg;
                remaining -= seg;
            }
        }

        // Firing progress bar (cyan) abaixo do botão Fire quando firing
        if (menu.isFiring()) {
            int ticks = menu.getFiringTicks();
            float progress = (float) ticks / PortalCreatorBlockEntity.FIRING_DURATION;
            gfx.fill(x + INFO_X, y + 70, x + INFO_X + 130, y + 78, 0xFF333333);
            int fillW = (int) (progress * 128);
            gfx.fillGradient(x + INFO_X + 1, y + 71, x + INFO_X + 1 + fillW, y + 77,
                    0xFF00FFFF, 0xFF0088FF);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        // Status text
        String statusText;
        int color;
        switch (menu.getStatus()) {
            case NOT_FORMED -> { statusText = "Multiblock nao formado"; color = 0xFF5555; }
            case NO_WALL    -> { statusText = "Parede 7x7 nao encontrada"; color = 0xFF5555; }
            case OBSTRUCTED -> { statusText = "Caminho do laser obstruido"; color = 0xFF5555; }
            case NO_ENERGY  -> { statusText = "Energia insuficiente"; color = 0xFFAA00; }
            case NO_FUEL    -> { statusText = "Fuel insuficiente"; color = 0xFFAA00; }
            case FIRING     -> { statusText = "Firing..."; color = 0x00FFFF; }
            case PORTAL_ACTIVE -> { statusText = "Portal ativo"; color = 0x00FF00; }
            case READY      -> { statusText = "Pronto pra ativar"; color = 0x55FF55; }
            default         -> { statusText = "?"; color = 0xFFFFFF; }
        }
        gfx.drawString(font, "Status: " + statusText, INFO_X, INFO_Y, color, false);

        // Energy text
        String energyText = "Energia: " + fmt(menu.getEnergy()) + " / " + fmt(menu.getEnergyCapacity()) + " FE";
        int eColor = menu.getEnergy() >= PortalCreatorBlockEntity.ENERGY_TO_FIRE ? 0x00FF00 : 0xFF5555;
        gfx.drawString(font, energyText, INFO_X, INFO_Y + 12, eColor, false);

        // Fuel text
        String fuelText = "Fuel: " + menu.getFuel() + " / " + menu.getMaxFuel();
        int fColor = menu.getFuel() >= PortalCreatorBlockEntity.FUEL_PER_SHOT ? 0x55FF55 : 0xFF8080;
        gfx.drawString(font, fuelText, INFO_X, INFO_Y + 26, fColor, false);

        // Tooltips
        int relX = mouseX - leftPos;
        int relY = mouseY - topPos;
        if (inRegion(relX, relY, ENERGY_BAR_X, ENERGY_BAR_Y, ENERGY_BAR_W, ENERGY_BAR_H)) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal("\u00a7cEnergy Storage"));
            lines.add(Component.literal(String.format("%,d / %,d FE", menu.getEnergy(), menu.getEnergyCapacity())));
            lines.add(Component.literal(""));
            lines.add(Component.literal("\u00a77Custo de fire: " + fmt(PortalCreatorBlockEntity.ENERGY_TO_FIRE) + " FE"));
            gfx.renderComponentTooltip(font, lines, relX, relY);
        }
        if (inRegion(relX, relY, FUEL_BAR_X, FUEL_BAR_Y, FUEL_BAR_W, FUEL_BAR_H)) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal("\u00a75Ender Pearl Fuel"));
            lines.add(Component.literal(menu.getFuel() + " / " + menu.getMaxFuel()));
            lines.add(Component.literal(""));
            lines.add(Component.literal("\u00a77Custo de fire: " + PortalCreatorBlockEntity.FUEL_PER_SHOT));
            lines.add(Component.literal("\u00a77Por dust: +" + PortalCreatorBlockEntity.FUEL_PER_DUST));
            gfx.renderComponentTooltip(font, lines, relX, relY);
        }
    }

    private static boolean inRegion(int relX, int relY, int x, int y, int w, int h) {
        return relX >= x && relX < x + w && relY >= y && relY < y + h;
    }

    private static String fmt(int v) {
        if (v >= 1_000_000) return String.format("%.1fM", v / 1_000_000.0);
        if (v >= 1_000) return String.format("%.1fK", v / 1_000.0);
        return String.valueOf(v);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx);
        super.render(gfx, mouseX, mouseY, partialTick);
        renderTooltip(gfx, mouseX, mouseY);
    }
}
