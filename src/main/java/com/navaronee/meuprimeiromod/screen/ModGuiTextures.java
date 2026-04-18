package com.navaronee.meuprimeiromod.screen;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.resources.ResourceLocation;

public class ModGuiTextures {

    public static final ResourceLocation GUI_BLANK =
            new ResourceLocation(MeuPrimeiroMod.MODID, "textures/gui/gui_blank.png");

    public static final ResourceLocation SLOT_TEMPLATE =
            new ResourceLocation(MeuPrimeiroMod.MODID, "textures/gui/slot_template.png");

    public static final int GUI_WIDTH = 176;
    public static final int GUI_HEIGHT = 166;
    public static final int SLOT_SIZE = 18;

    private ModGuiTextures() {}
}
