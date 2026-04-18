package com.navaronee.meuprimeiromod.client.entity;

import com.navaronee.meuprimeiromod.entity.CesiumGranadeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class CesiumGranadeRenderer extends ThrownItemRenderer<CesiumGranadeEntity> {

    public CesiumGranadeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
