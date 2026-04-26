package com.navaronee.meuprimeiromod.screen;

import com.navaronee.meuprimeiromod.menu.CesiumRefinerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class CesiumRefinerScreen extends AbstractContainerScreen<CesiumRefinerMenu> {

    // Posições dos slots (centralizadas na GUI 176px)
    private static final int INPUT_SLOT_X = 50;
    private static final int INPUT_SLOT_Y = 35;
    private static final int OUTPUT_SLOT_X = 110;
    private static final int OUTPUT_SLOT_Y = 35;

    // Barra de progresso (entre os slots)
    private static final int PROGRESS_BAR_X = 72;
    private static final int PROGRESS_BAR_Y = 39;
    private static final int PROGRESS_BAR_WIDTH = 32;
    private static final int PROGRESS_BAR_HEIGHT = 8;

    // Cores da barra de progresso (verde radioativo)
    private static final int BAR_BG_DARK = 0xFF1A1A1A;
    private static final int BAR_BORDER = 0xFF0A0A0A;
    private static final int BAR_FILL_BRIGHT = 0xFF5FFF2D;
    private static final int BAR_FILL_DARK = 0xFF1E7A0F;
    private static final int BAR_HIGHLIGHT = 0xFFB8FF9E;

    // Barra de energia (vertical, canto esquerdo, vermelho→amarelo)
    private static final int ENERGY_BAR_X = 8;
    private static final int ENERGY_BAR_Y = 18;
    private static final int ENERGY_BAR_WIDTH = 12;
    private static final int ENERGY_BAR_HEIGHT = 50;
    private static final int ENERGY_FILL_BRIGHT = 0xFFFFE066; // amarelo claro topo
    private static final int ENERGY_FILL_DARK = 0xFFD4380F;   // vermelho/laranja base
    private static final int ENERGY_HIGHLIGHT = 0xFFFFFFD0;

    public CesiumRefinerScreen(CesiumRefinerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = ModGuiTextures.GUI_WIDTH;
        this.imageHeight = ModGuiTextures.GUI_HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Fundo: blank GUI (176x166)
        gfx.blit(ModGuiTextures.GUI_BLANK, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // Slots compostos com slot_template (deslocamento -1,-1 porque o slot tem borda)
        gfx.blit(ModGuiTextures.SLOT_TEMPLATE, x + INPUT_SLOT_X - 1, y + INPUT_SLOT_Y - 1,
                0, 0, ModGuiTextures.SLOT_SIZE, ModGuiTextures.SLOT_SIZE,
                ModGuiTextures.SLOT_SIZE, ModGuiTextures.SLOT_SIZE);
        gfx.blit(ModGuiTextures.SLOT_TEMPLATE, x + OUTPUT_SLOT_X - 1, y + OUTPUT_SLOT_Y - 1,
                0, 0, ModGuiTextures.SLOT_SIZE, ModGuiTextures.SLOT_SIZE,
                ModGuiTextures.SLOT_SIZE, ModGuiTextures.SLOT_SIZE);

        // Slots do player inventory (9 cols x 3 rows + 9 hotbar)
        renderPlayerInventorySlots(gfx, x, y);

        renderProgressBar(gfx, x, y);
        renderEnergyBar(gfx, x, y);
    }

    private void renderEnergyBar(GuiGraphics gfx, int x, int y) {
        int bx = x + ENERGY_BAR_X;
        int by = y + ENERGY_BAR_Y;

        // Borda + background (mesmo estilo da progress bar)
        gfx.fill(bx - 1, by - 1, bx + ENERGY_BAR_WIDTH + 1, by + ENERGY_BAR_HEIGHT + 1, BAR_BORDER);
        gfx.fill(bx, by, bx + ENERGY_BAR_WIDTH, by + ENERGY_BAR_HEIGHT, BAR_BG_DARK);

        int e = menu.getEnergy();
        int cap = menu.getEnergyCapacity();
        if (cap == 0 || e == 0) return;

        // Vertical: enche de baixo pra cima
        int filled = e * ENERGY_BAR_HEIGHT / cap;
        if (filled <= 0) return;
        int fillTop = by + ENERGY_BAR_HEIGHT - filled;

        // Gradiente vertical: amarelo claro topo → vermelho/laranja base
        gfx.fillGradient(bx, fillTop, bx + ENERGY_BAR_WIDTH, by + ENERGY_BAR_HEIGHT,
                ENERGY_FILL_BRIGHT, ENERGY_FILL_DARK);

        // Highlight de 1px no topo da fill
        gfx.fill(bx, fillTop, bx + ENERGY_BAR_WIDTH, fillTop + 1, ENERGY_HIGHLIGHT);
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        super.renderLabels(gfx, mouseX, mouseY);

        // Tooltip da barra de energia (mouseX/Y são absolutos; coords aqui são relativas)
        int relX = mouseX - leftPos;
        int relY = mouseY - topPos;
        if (relX >= ENERGY_BAR_X && relX < ENERGY_BAR_X + ENERGY_BAR_WIDTH
                && relY >= ENERGY_BAR_Y && relY < ENERGY_BAR_Y + ENERGY_BAR_HEIGHT) {
            gfx.renderTooltip(font,
                    Component.literal(menu.getEnergy() + " / " + menu.getEnergyCapacity() + " FE"),
                    relX, relY);
        }
    }

    private void renderProgressBar(GuiGraphics gfx, int x, int y) {
        int bx = x + PROGRESS_BAR_X;
        int by = y + PROGRESS_BAR_Y;

        // Borda externa (escura)
        gfx.fill(bx - 1, by - 1, bx + PROGRESS_BAR_WIDTH + 1, by + PROGRESS_BAR_HEIGHT + 1, BAR_BORDER);

        // Background escuro (inset)
        gfx.fill(bx, by, bx + PROGRESS_BAR_WIDTH, by + PROGRESS_BAR_HEIGHT, BAR_BG_DARK);

        if (!menu.isProcessing()) return;

        int filled = menu.getScaledProgress() * PROGRESS_BAR_WIDTH / 24;
        if (filled <= 0) return;

        // Gradiente vertical: brilhante no topo, escuro embaixo
        gfx.fillGradient(bx, by, bx + filled, by + PROGRESS_BAR_HEIGHT, BAR_FILL_BRIGHT, BAR_FILL_DARK);

        // Highlight no topo (1px claro)
        gfx.fill(bx, by, bx + filled, by + 1, BAR_HIGHLIGHT);
    }

    private void renderPlayerInventorySlots(GuiGraphics gfx, int x, int y) {
        // 3 rows de inventário (y=84,102,120)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                gfx.blit(ModGuiTextures.SLOT_TEMPLATE,
                        x + 8 + col * 18 - 1, y + 84 + row * 18 - 1,
                        0, 0, ModGuiTextures.SLOT_SIZE, ModGuiTextures.SLOT_SIZE,
                        ModGuiTextures.SLOT_SIZE, ModGuiTextures.SLOT_SIZE);
            }
        }
        // Hotbar (y=142)
        for (int col = 0; col < 9; col++) {
            gfx.blit(ModGuiTextures.SLOT_TEMPLATE,
                    x + 8 + col * 18 - 1, y + 142 - 1,
                    0, 0, ModGuiTextures.SLOT_SIZE, ModGuiTextures.SLOT_SIZE,
                    ModGuiTextures.SLOT_SIZE, ModGuiTextures.SLOT_SIZE);
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        renderBackground(gfx);
        super.render(gfx, mouseX, mouseY, partialTick);
        renderTooltip(gfx, mouseX, mouseY);
    }
}
