package com.navaronee.meuprimeiromod.client.model;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class LeadArmorModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(MeuPrimeiroMod.MODID, "lead_armor"), "main");

    public LeadArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // === HEAD ===
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-5.0F, -33.0F, -5.0F, 10.0F, 9.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(68, 32).addBox(-6.0F, -30.0F, -3.5F, 12.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(64, 15).addBox(-6.0F, -34.0F, -2.0F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(68, 55).addBox(-5.8F, -27.0F, -6.2F, 5.8F, 1.0F, 0.8F, new CubeDeformation(0.0F))
                .texOffs(126, 100).addBox(-1.9F, -27.3F, -6.4F, 3.8F, 1.6F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 36).addBox(0.0F, -27.0F, -6.2F, 5.9F, 1.0F, 0.8F, new CubeDeformation(0.0F))
                .texOffs(54, 29).addBox(5.2F, -27.0F, -5.4F, 0.7F, 1.0F, 6.1F, new CubeDeformation(0.0F))
                .texOffs(44, 80).addBox(-5.8F, -27.0F, -5.4F, 0.7F, 1.0F, 5.1F, new CubeDeformation(0.0F))
                .texOffs(36, 78).addBox(4.2F, -27.0F, -0.4F, 1.0F, 1.0F, 1.1F, new CubeDeformation(0.0F))
                .texOffs(112, 108).addBox(-7.0F, -34.0F, -2.0F, 1.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-4.0F, -32.0F, -5.2F, 0.2F, 2.0F, 0.2F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(3.8F, -32.0F, -5.2F, 0.2F, 2.0F, 0.2F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.8F, -32.0F, -5.2F, 7.6F, 0.2F, 0.2F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.8F, -30.2F, -5.2F, 2.175F, 0.2F, 0.2F, new CubeDeformation(0.0F))
                .texOffs(64, 11).addBox(-3.9F, -27.9F, -5.5F, 7.8F, 2.8F, 0.5F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.625F, -30.2F, -5.2F, 2.175F, 0.2F, 0.2F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.3F, -30.6F, -5.2F, 2.6F, 0.2F, 0.2F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        helmet.addOrReplaceChild("head_r1", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.25F, -0.1F, 0.3F, 0.65F, 0.2F, 0.2F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.1132F, -29.6454F, -5.5F, 0.0F, 0.0F, 0.7854F));

        helmet.addOrReplaceChild("head_r2", CubeListBuilder.create()
                .texOffs(0, 0).addBox(0.6F, -0.1F, 0.3F, 0.65F, 0.2F, 0.2F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.1132F, -29.6454F, -5.5F, 0.0F, 0.0F, -0.7854F));

        // hat (required by HumanoidModel, empty)
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        // === BODY ===
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(110, 97).addBox(-4.0F, 0.0F, -3.8F, 1.0F, 9.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(110, 86).addBox(2.9F, 0.0F, -3.8F, 1.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        body.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                .texOffs(112, 126).addBox(-7.8F, -1.2F, 6.2F, 3.5F, 3.2F, 0.6F, new CubeDeformation(0.0F))
                .texOffs(104, 126).addBox(-4.0924F, -1.2F, 6.2F, 3.5F, 3.2F, 0.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(4.1962F, 0.8009F, -9.2F, -0.2007F, 0.0F, 0.0F));

        body.addOrReplaceChild("cube_r2", CubeListBuilder.create()
                .texOffs(44, 57).addBox(2.7481F, -0.7669F, 3.9624F, 2.2369F, 2.0451F, 0.3835F, new CubeDeformation(0.0F))
                .texOffs(40, 57).addBox(0.3786F, -0.7669F, 3.9624F, 2.2369F, 2.0451F, 0.3835F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.6818F, 6.158F, -7.0397F, -0.2007F, 0.0F, 0.0F));

        body.addOrReplaceChild("cube_r3", CubeListBuilder.create()
                .texOffs(78, 15).addBox(-4.985F, -0.7669F, 3.9624F, 2.2369F, 2.0451F, 0.3835F, new CubeDeformation(0.0F))
                .texOffs(78, 17).addBox(-2.6154F, -0.7669F, 3.9624F, 2.2369F, 2.0451F, 0.3835F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.6818F, 4.2407F, -7.0397F, -0.2007F, 0.0F, 0.0F));

        // Tank (child of body)
        PartDefinition tank = body.addOrReplaceChild("tank", CubeListBuilder.create()
                .texOffs(62, 109).addBox(8.2272F, -8.4F, 6.55F, 0.7956F, 9.4F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(104, 60).addBox(12.7272F, -8.4F, 6.55F, 0.7956F, 9.4F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(72, 109).addBox(11.125F, -8.4F, 8.1522F, 4.0F, 9.4F, 0.7956F, new CubeDeformation(0.0F))
                .texOffs(102, 109).addBox(6.625F, -8.4F, 8.1522F, 4.0F, 9.4F, 0.7956F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(8.425F, -10.55F, 8.375F, 0.4F, 1.45F, 0.4F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(12.925F, -10.55F, 8.375F, 0.4F, 1.45F, 0.4F, new CubeDeformation(0.0F))
                .texOffs(64, 18).addBox(8.275F, -10.875F, 8.2F, 5.3F, 0.325F, 0.7F, new CubeDeformation(0.0F))
                .texOffs(56, 80).addBox(10.525F, -15.2F, 8.25F, 1.0F, 4.3F, 0.6F, new CubeDeformation(0.0F))
                .texOffs(32, 78).addBox(10.525F, -15.7F, 7.45F, 1.0F, 0.5F, 1.4F, new CubeDeformation(0.0F)),
                PartPose.offset(-11.125F, 10.1F, -2.65F));

        tank.addOrReplaceChild("hexadecagon_r1", CubeListBuilder.create()
                .texOffs(124, 70).addBox(-0.3978F, -6.3F, 0.15F, 0.7956F, 9.4F, 0.775F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(8.825F, -3.1F, 8.05F, 0.0F, -0.3927F, 0.0F));

        tank.addOrReplaceChild("hexadecagon_r2", CubeListBuilder.create()
                .texOffs(110, 44).addBox(-2.0F, -6.3F, -0.3978F, 4.0F, 9.4F, 0.7956F, new CubeDeformation(0.0F))
                .texOffs(42, 109).addBox(-0.3978F, -6.3F, -2.0F, 0.7956F, 9.4F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(8.625F, -2.1F, 8.55F, 0.0F, -0.3927F, 0.0F));

        tank.addOrReplaceChild("hexadecagon_r3", CubeListBuilder.create()
                .texOffs(92, 109).addBox(-2.0F, -6.3F, -0.3978F, 4.0F, 9.4F, 0.7956F, new CubeDeformation(0.0F))
                .texOffs(108, 31).addBox(-0.3978F, -6.3F, -2.0F, 0.7956F, 9.4F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(8.625F, -2.1F, 8.55F, 0.0F, 0.3927F, 0.0F));

        tank.addOrReplaceChild("hexadecagon_r4", CubeListBuilder.create()
                .texOffs(52, 109).addBox(-0.3978F, -6.3F, -2.0F, 0.7956F, 9.4F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(8.625F, -2.1F, 8.55F, 0.0F, -0.7854F, 0.0F));

        tank.addOrReplaceChild("hexadecagon_r5", CubeListBuilder.create()
                .texOffs(80, 124).addBox(-0.3978F, -6.3F, 0.15F, 0.7956F, 9.4F, 0.775F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.325F, -3.1F, 8.05F, 0.0F, -0.3927F, 0.0F));

        tank.addOrReplaceChild("hexadecagon_r6", CubeListBuilder.create()
                .texOffs(10, 108).addBox(-0.3978F, -6.3F, -2.0F, 0.7956F, 9.4F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(8.625F, -2.1F, 8.55F, 0.0F, 0.7854F, 0.0F));

        tank.addOrReplaceChild("hexadecagon_r7", CubeListBuilder.create()
                .texOffs(82, 109).addBox(-2.0F, -6.3F, -0.3978F, 4.0F, 9.4F, 0.7956F, new CubeDeformation(0.0F))
                .texOffs(104, 73).addBox(-0.3978F, -6.3F, -2.0F, 0.7956F, 9.4F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.125F, -2.1F, 8.55F, 0.0F, -0.3927F, 0.0F));

        tank.addOrReplaceChild("hexadecagon_r8", CubeListBuilder.create()
                .texOffs(104, 10).addBox(-2.0F, -6.3F, -0.3978F, 4.0F, 9.4F, 0.7956F, new CubeDeformation(0.0F))
                .texOffs(32, 100).addBox(-0.3978F, -6.3F, -2.0F, 0.7956F, 9.4F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.125F, -2.1F, 8.55F, 0.0F, 0.3927F, 0.0F));

        tank.addOrReplaceChild("hexadecagon_r9", CubeListBuilder.create()
                .texOffs(0, 108).addBox(-0.3978F, -6.3F, -2.0F, 0.7956F, 9.4F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.125F, -2.1F, 8.55F, 0.0F, -0.7854F, 0.0F));

        tank.addOrReplaceChild("hexadecagon_r10", CubeListBuilder.create()
                .texOffs(22, 100).addBox(-0.3978F, -6.3F, -2.0F, 0.7956F, 9.4F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.125F, -2.1F, 8.55F, 0.0F, 0.7854F, 0.0F));

        PartDefinition hexadecagon = tank.addOrReplaceChild("hexadecagon", CubeListBuilder.create()
                .texOffs(22, 78).addBox(10.98F, 0.32F, 8.2103F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F))
                .texOffs(114, 69).addBox(12.7103F, 0.32F, 6.48F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        hexadecagon.addOrReplaceChild("hexadecagon_r11", CubeListBuilder.create()
                .texOffs(78, 57).addBox(-2.16F, -0.54F, -0.4297F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F))
                .texOffs(94, 31).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 0.86F, 8.64F, 0.0F, -0.3927F, 0.0F));

        hexadecagon.addOrReplaceChild("hexadecagon_r12", CubeListBuilder.create()
                .texOffs(68, 57).addBox(-2.16F, -0.54F, -0.4297F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F))
                .texOffs(88, 60).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 0.86F, 8.64F, 0.0F, 0.3927F, 0.0F));

        hexadecagon.addOrReplaceChild("hexadecagon_r13", CubeListBuilder.create()
                .texOffs(82, 21).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 0.86F, 8.64F, 0.0F, 0.7854F, 0.0F));

        PartDefinition hexadecagon11 = hexadecagon.addOrReplaceChild("hexadecagon11", CubeListBuilder.create()
                .texOffs(120, 50).addBox(12.7103F, 9.82F, 6.48F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F))
                .texOffs(126, 28).addBox(10.98F, 9.82F, 8.2103F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.5F, -9.5F, 0.0F));

        hexadecagon11.addOrReplaceChild("hexadecagon_r14", CubeListBuilder.create()
                .texOffs(84, 126).addBox(-2.16F, -0.54F, -0.4297F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F))
                .texOffs(0, 121).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 10.36F, 8.64F, 0.0F, -0.3927F, 0.0F));

        hexadecagon11.addOrReplaceChild("hexadecagon_r15", CubeListBuilder.create()
                .texOffs(126, 26).addBox(-2.16F, -0.54F, -0.4297F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F))
                .texOffs(120, 45).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 10.36F, 8.64F, 0.0F, 0.3927F, 0.0F));

        hexadecagon11.addOrReplaceChild("hexadecagon_r16", CubeListBuilder.create()
                .texOffs(10, 121).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(17.64F, 10.36F, 8.64F, 0.0F, -0.7854F, 0.0F));

        hexadecagon11.addOrReplaceChild("hexadecagon_r17", CubeListBuilder.create()
                .texOffs(10, 121).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 10.36F, 8.64F, 0.0F, -0.7854F, 0.0F));

        hexadecagon11.addOrReplaceChild("hexadecagon_r18", CubeListBuilder.create()
                .texOffs(120, 40).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 10.36F, 8.64F, 0.0F, 0.7854F, 0.0F));

        PartDefinition hexadecagon12 = tank.addOrReplaceChild("hexadecagon12", CubeListBuilder.create()
                .texOffs(40, 122).addBox(12.7103F, 0.32F, 6.48F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F))
                .texOffs(126, 86).addBox(10.98F, 0.32F, 8.2103F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -9.5F, 0.0F));

        hexadecagon12.addOrReplaceChild("hexadecagon_r19", CubeListBuilder.create()
                .texOffs(126, 88).addBox(-2.16F, -0.54F, -0.4297F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F))
                .texOffs(50, 122).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 0.86F, 8.64F, 0.0F, -0.3927F, 0.0F));

        hexadecagon12.addOrReplaceChild("hexadecagon_r20", CubeListBuilder.create()
                .texOffs(126, 84).addBox(-2.16F, -0.54F, -0.4297F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F))
                .texOffs(122, 5).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 0.86F, 8.64F, 0.0F, 0.3927F, 0.0F));

        hexadecagon12.addOrReplaceChild("hexadecagon_r21", CubeListBuilder.create()
                .texOffs(60, 122).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 0.86F, 8.64F, 0.0F, -0.7854F, 0.0F));

        hexadecagon12.addOrReplaceChild("hexadecagon_r22", CubeListBuilder.create()
                .texOffs(122, 0).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 0.86F, 8.64F, 0.0F, 0.7854F, 0.0F));

        PartDefinition hexadecagon13 = hexadecagon12.addOrReplaceChild("hexadecagon13", CubeListBuilder.create()
                .texOffs(126, 92).addBox(10.98F, 0.32F, 8.2103F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F))
                .texOffs(70, 124).addBox(12.7103F, 0.32F, 6.48F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.5F, 0.0F, 0.0F));

        hexadecagon13.addOrReplaceChild("hexadecagon_r23", CubeListBuilder.create()
                .texOffs(94, 126).addBox(-2.16F, -0.54F, -0.4297F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F))
                .texOffs(30, 123).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 0.86F, 8.64F, 0.0F, -0.3927F, 0.0F));

        hexadecagon13.addOrReplaceChild("hexadecagon_r24", CubeListBuilder.create()
                .texOffs(126, 90).addBox(-2.16F, -0.54F, -0.4297F, 4.32F, 1.08F, 0.8593F, new CubeDeformation(0.0F))
                .texOffs(20, 123).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 0.86F, 8.64F, 0.0F, 0.3927F, 0.0F));

        hexadecagon13.addOrReplaceChild("hexadecagon_r25", CubeListBuilder.create()
                .texOffs(124, 10).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 0.86F, 8.64F, 0.0F, -0.7854F, 0.0F));

        hexadecagon13.addOrReplaceChild("hexadecagon_r26", CubeListBuilder.create()
                .texOffs(122, 119).addBox(-0.4297F, -0.54F, -2.16F, 0.8593F, 1.08F, 4.32F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(13.14F, 0.86F, 8.64F, 0.0F, 0.7854F, 0.0F));

        // === LEFT ARM (pivot adjusted from 4,-0.2,-2 to 5,2,0) ===
        PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(54, 19).addBox(-1.0F, -3.4F, -3.8F, 5.3F, 1.2F, 8.6F, new CubeDeformation(0.0F))
                .texOffs(0, 57).addBox(-0.4F, -2.2F, -3.8F, 0.7F, 12.15F, 8.5F, new CubeDeformation(0.0F))
                .texOffs(30, 19).addBox(-1.0F, -2.2F, -3.0F, 5.0F, 12.3F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(20, 57).addBox(1.8F, -2.2F, -3.8F, 0.7F, 12.15F, 8.5F, new CubeDeformation(0.0F))
                .texOffs(68, 29).addBox(0.0F, 10.1F, -3.5F, 4.6F, 3.0F, 7.9F, new CubeDeformation(0.0F)),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        leftArm.addOrReplaceChild("cube_r8", CubeListBuilder.create()
                .texOffs(44, 89).addBox(5.3F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, -4.1F, -2.0F, 0.0F, 0.0F, 0.7854F));

        leftArm.addOrReplaceChild("cube_r9", CubeListBuilder.create()
                .texOffs(82, 11).addBox(5.3F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, -2.2F, -2.0F, 0.0F, 0.0F, 0.7854F));

        leftArm.addOrReplaceChild("cube_r10", CubeListBuilder.create()
                .texOffs(82, 79).addBox(5.3F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, -0.2F, -2.0F, 0.0F, 0.0F, 0.7854F));

        leftArm.addOrReplaceChild("cube_r11", CubeListBuilder.create()
                .texOffs(86, 40).addBox(5.3F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, 1.9F, -2.0F, 0.0F, 0.0F, 0.7854F));

        leftArm.addOrReplaceChild("cube_r12", CubeListBuilder.create()
                .texOffs(88, 50).addBox(5.3F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, 4.1F, -2.0F, 0.0F, 0.0F, 0.7854F));

        leftArm.addOrReplaceChild("cube_r13", CubeListBuilder.create()
                .texOffs(60, 69).addBox(5.3F, -1.2F, -1.8F, 2.5F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-0.2962F, -6.2991F, -2.0F, 0.0F, 0.0F, 0.7854F));

        // === RIGHT ARM (pivot adjusted from -4,15,-2 to -5,2,0) ===
        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-4.6F, 10.1F, -3.5F, 4.6F, 3.0F, 7.9F, new CubeDeformation(0.0F))
                .texOffs(40, 59).addBox(-0.3F, -2.2F, -3.8F, 0.7F, 12.15F, 8.5F, new CubeDeformation(0.0F))
                .texOffs(48, 38).addBox(-2.5F, -2.2F, -3.8F, 0.7F, 12.15F, 8.5F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-4.0F, -2.2F, -3.0F, 5.0F, 12.3F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(60, 59).addBox(-4.3F, -3.4F, -3.8F, 5.3F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        rightArm.addOrReplaceChild("cube_r14", CubeListBuilder.create()
                .texOffs(0, 78).addBox(-7.8F, -1.2F, -1.8F, 2.5F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.2962F, -6.2991F, -2.0F, 0.0F, 0.0F, -0.7854F));

        rightArm.addOrReplaceChild("cube_r15", CubeListBuilder.create()
                .texOffs(0, 88).addBox(-7.2F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, -4.1F, -2.0F, 0.0F, 0.0F, -0.7854F));

        rightArm.addOrReplaceChild("cube_r16", CubeListBuilder.create()
                .texOffs(60, 79).addBox(-7.2F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, -2.2F, -2.0F, 0.0F, 0.0F, -0.7854F));

        rightArm.addOrReplaceChild("cube_r17", CubeListBuilder.create()
                .texOffs(22, 80).addBox(-7.2F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, -0.2F, -2.0F, 0.0F, 0.0F, -0.7854F));

        rightArm.addOrReplaceChild("cube_r18", CubeListBuilder.create()
                .texOffs(82, 69).addBox(-7.2F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, 1.9F, -2.0F, 0.0F, 0.0F, -0.7854F));

        rightArm.addOrReplaceChild("cube_r19", CubeListBuilder.create()
                .texOffs(90, 0).addBox(-7.2F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, 4.1F, -2.0F, 0.0F, 0.0F, -0.7854F));

        // === RIGHT LEG ===
        PartDefinition rightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(24, 38).addBox(-3.1F, 0.0F, -3.0F, 5.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        rightLeg.addOrReplaceChild("cube_r4", CubeListBuilder.create()
                .texOffs(88, 99).addBox(-7.2F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, 2.9F, -2.0F, 0.0F, 0.0F, -0.7854F));

        rightLeg.addOrReplaceChild("cube_r5", CubeListBuilder.create()
                .texOffs(44, 99).addBox(-7.2F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, 4.9F, -2.0F, 0.0F, 0.0F, -0.7854F));

        rightLeg.addOrReplaceChild("cube_r6", CubeListBuilder.create()
                .texOffs(94, 21).addBox(-7.2F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, 6.9F, -2.0F, 0.0F, 0.0F, -0.7854F));

        rightLeg.addOrReplaceChild("cube_r7", CubeListBuilder.create()
                .texOffs(66, 89).addBox(-7.2F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, 0.9F, -2.0F, 0.0F, 0.0F, -0.7854F));

        // === LEFT LEG ===
        PartDefinition leftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(40, 0).addBox(-1.9F, 0.0F, -3.0F, 5.0F, 12.0F, 7.0F, new CubeDeformation(0.0F)),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        leftLeg.addOrReplaceChild("cube_r20", CubeListBuilder.create()
                .texOffs(66, 99).addBox(5.3F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, 0.9F, -2.0F, 0.0F, 0.0F, 0.7854F));

        leftLeg.addOrReplaceChild("cube_r21", CubeListBuilder.create()
                .texOffs(0, 98).addBox(5.3F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, 2.9F, -2.0F, 0.0F, 0.0F, 0.7854F));

        leftLeg.addOrReplaceChild("cube_r22", CubeListBuilder.create()
                .texOffs(22, 90).addBox(5.3F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, 4.9F, -2.0F, 0.0F, 0.0F, 0.7854F));

        leftLeg.addOrReplaceChild("cube_r23", CubeListBuilder.create()
                .texOffs(88, 89).addBox(5.3F, -1.2F, -1.8F, 1.9F, 1.2F, 8.6F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, 6.9F, -2.0F, 0.0F, 0.0F, 0.7854F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }
}
