package com.navaronee.meuprimeiromod.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * BEWLR pro Lead Shield: usa o ShieldModel vanilla (plate + handle) com a
 * textura customizada de chumbo, garantindo o look 3D real igual ao escudo do
 * Minecraft. Wired via LeadShieldItem.initializeClient.
 */
public class LeadShieldRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(MeuPrimeiroMod.MODID, "textures/entity/lead_shield.png");

    private ShieldModel model;

    public LeadShieldRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet modelSet) {
        super(dispatcher, modelSet);
    }

    private ShieldModel getModel() {
        if (model == null) {
            model = new ShieldModel(Minecraft.getInstance()
                    .getEntityModels().bakeLayer(ModelLayers.SHIELD));
        }
        return model;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context,
                             PoseStack pose, MultiBufferSource buffer,
                             int light, int overlay) {
        ShieldModel m = getModel();

        pose.pushPose();
        pose.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(
                buffer, m.renderType(TEXTURE), true, stack.hasFoil());

        m.handle().render(pose, consumer, light, overlay);
        m.plate().render(pose, consumer, light, overlay);

        pose.popPose();
    }
}
