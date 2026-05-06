package com.navaronee.meuprimeiromod.client.model;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import com.navaronee.meuprimeiromod.entity.MutantEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

/**
 * Modelo novo do Mutant (mutant_radioativo) — head1/head4/boca/boca2 agora ficam
 * sob headCenter, e a textura é 128×128. As animações dirigem partes via nome,
 * então a arvore precisa casar com os addAnimation(...) do MutantAnimations.
 */
public class MutantModel extends HierarchicalModel<MutantEntity> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(MeuPrimeiroMod.MODID, "mutant"), "main");

    private final ModelPart root;

    public MutantModel(ModelPart root) {
        this.root = root;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 76).addBox(-1.6F, 14.0F, -4.5F, 6.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
        .texOffs(40, 58).addBox(-2.0F, -1.6F, -3.2F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.6F, 7.0F, 0.0F));

        left_leg.addOrReplaceChild("left_leg_r1", CubeListBuilder.create().texOffs(18, 56).addBox(-1.2F, -6.5F, -1.6F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 7.8F, 0.0F, -0.192F, 0.0F, 0.0F));
        left_leg.addOrReplaceChild("left_leg_r2", CubeListBuilder.create().texOffs(0, 56).addBox(-1.2F, -6.2F, -2.2F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.1396F, 0.0F, 0.0F));

        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(30, 76).addBox(-4.4F, 14.0F, -4.5F, 6.0F, 3.0F, 9.0F, new CubeDeformation(0.0F))
        .texOffs(64, 56).addBox(-4.0F, -1.6F, -3.2F, 6.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(5.6F, 7.0F, 0.0F));

        right_leg.addOrReplaceChild("right_leg_r1", CubeListBuilder.create().texOffs(58, 70).addBox(-2.8F, -6.5F, -1.6F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 7.8F, 0.0F, -0.192F, 0.0F, 0.0F));
        right_leg.addOrReplaceChild("right_leg_r2", CubeListBuilder.create().texOffs(40, 70).addBox(-3.8F, -6.2F, -2.2F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.1396F, 0.0F, 0.0F));

        PartDefinition upper_body = partdefinition.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offset(0.0F, 5.6F, -0.2F));

        upper_body.addOrReplaceChild("upper_body_r1", CubeListBuilder.create().texOffs(72, 0).addBox(-1.2F, -3.5F, -1.3F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.1F, -1.3F, 0.3491F, 0.0F, 0.0F));
        upper_body.addOrReplaceChild("upper_body_r2", CubeListBuilder.create().texOffs(40, 0).addBox(-4.0F, -4.0F, -1.0F, 8.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.6F, 4.7F, 0.3142F, 0.0F, 0.0F));
        upper_body.addOrReplaceChild("upper_body_r3", CubeListBuilder.create().texOffs(0, 32).addBox(-4.5F, -4.0F, -3.0F, 9.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -10.6F, -0.2F, 0.2443F, 0.0F, 0.0F));
        upper_body.addOrReplaceChild("upper_body_r4", CubeListBuilder.create().texOffs(0, 18).addBox(-5.5F, -4.6F, -4.2F, 11.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.6F, -0.2F, 0.1745F, 0.0F, 0.0F));
        upper_body.addOrReplaceChild("upper_body_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-6.5F, -3.8F, -4.4F, 13.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.6F, -0.6F, 0.1047F, 0.0F, 0.0F));

        PartDefinition rightside = upper_body.addOrReplaceChild("rightside", CubeListBuilder.create(), PartPose.offset(6.3F, -6.4F, -0.7F));
        rightside.addOrReplaceChild("rightside_r1", CubeListBuilder.create().texOffs(54, 14).addBox(-0.6F, -3.0F, -1.7F, 3.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.4F, 0.1047F, 0.0F, -0.1745F));
        rightside.addOrReplaceChild("rightside_r2", CubeListBuilder.create().texOffs(40, 14).addBox(-1.0F, -4.2F, -3.8F, 4.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.2F, -0.1F, 0.1745F, 0.0F, -0.1396F));

        PartDefinition upperright = rightside.addOrReplaceChild("upperright", CubeListBuilder.create(), PartPose.offset(-0.7F, 1.2F, 0.1F));
        upperright.addOrReplaceChild("upperright_r1", CubeListBuilder.create().texOffs(56, 32).addBox(-0.8F, -2.7F, -2.9F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.9F, 0.0F, 0.2443F, 0.0F, -0.1047F));
        upperright.addOrReplaceChild("upperright_r2", CubeListBuilder.create().texOffs(56, 22).addBox(-1.2F, -2.0F, -3.3F, 5.0F, 3.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2094F, 0.0F, -0.1396F));

        PartDefinition neckright = rightside.addOrReplaceChild("neckright", CubeListBuilder.create(), PartPose.offset(-3.9F, -6.6F, -1.1F));
        neckright.addOrReplaceChild("neckright_r1", CubeListBuilder.create().texOffs(76, 18).addBox(-0.2F, -2.4F, -0.8F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.2F, -1.5F, 0.733F, 0.0F, -0.2094F));
        neckright.addOrReplaceChild("neckright_r2", CubeListBuilder.create().texOffs(74, 10).addBox(-0.9F, -2.4F, -0.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4189F, 0.0F, -0.1396F));

        PartDefinition right_arm = rightside.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(5.1F, -0.6F, 0.1F));
        right_arm.addOrReplaceChild("right_arm_r1", CubeListBuilder.create().texOffs(44, 44).addBox(-1.4F, -2.5F, -2.3F, 4.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3F, 12.9F, 0.3F, -0.1396F, 0.0F, 0.0698F));
        right_arm.addOrReplaceChild("right_arm_r2", CubeListBuilder.create().texOffs(26, 44).addBox(-1.5F, -2.8F, -1.6F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 8.0F, 0.0F, -0.0349F, 0.0F, -0.1396F));
        right_arm.addOrReplaceChild("right_arm_r3", CubeListBuilder.create().texOffs(0, 44).addBox(-2.7F, -2.4F, -3.0F, 6.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0698F, 0.0F, -0.2094F));

        PartDefinition finger2 = right_arm.addOrReplaceChild("finger2", CubeListBuilder.create(), PartPose.offset(2.4F, 14.6F, 0.0F));
        finger2.addOrReplaceChild("finger2_r1", CubeListBuilder.create().texOffs(74, 24).addBox(-0.4F, -0.6F, -0.6F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2F, 0.3F, -0.5F, -0.1396F, 0.0F, 0.1745F));

        PartDefinition finger4 = right_arm.addOrReplaceChild("finger4", CubeListBuilder.create(), PartPose.offset(2.8F, 14.8F, 1.9F));
        finger4.addOrReplaceChild("finger4_r1", CubeListBuilder.create().texOffs(74, 32).addBox(-0.3F, -0.7F, 0.1F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6F, 0.4F, -0.3F, 0.1047F, 0.0F, 0.1396F));

        PartDefinition finger3 = right_arm.addOrReplaceChild("finger3", CubeListBuilder.create(), PartPose.offset(2.6F, 14.7F, 0.9F));
        finger3.addOrReplaceChild("finger3_r1", CubeListBuilder.create().texOffs(74, 38).addBox(-0.3F, -0.5F, -0.1F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3F, 0.4F, -0.2F, -0.0175F, 0.0F, 0.1396F));

        PartDefinition finger1 = right_arm.addOrReplaceChild("finger1", CubeListBuilder.create(), PartPose.offset(1.1F, 14.5F, -1.0F));
        finger1.addOrReplaceChild("finger1_r1", CubeListBuilder.create().texOffs(74, 44).addBox(-0.3F, -0.5F, -0.6F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4F, 0.4F, 0.0F, -0.2094F, 0.0F, 0.1047F));

        PartDefinition leftside = upper_body.addOrReplaceChild("leftside", CubeListBuilder.create(), PartPose.offset(-6.3F, -6.4F, -0.7F));
        leftside.addOrReplaceChild("leftside_r1", CubeListBuilder.create().texOffs(34, 34).addBox(-1.9F, -2.3F, -1.5F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.0F, 0.6F, 0.0873F, 0.0F, 0.1745F));
        leftside.addOrReplaceChild("leftside_r2", CubeListBuilder.create().texOffs(18, 34).addBox(-2.8F, -3.2F, -3.3F, 4.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.8F, 0.0F, 0.1396F, 0.0F, 0.1047F));

        PartDefinition uperleft = leftside.addOrReplaceChild("uperleft", CubeListBuilder.create(), PartPose.offset(0.7F, 1.1F, 0.0F));
        uperleft.addOrReplaceChild("uperleft_r1", CubeListBuilder.create().texOffs(32, 30).addBox(-3.7F, -2.8F, -3.1F, 5.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.8F, 0.0F, 0.2269F, 0.0F, 0.1047F));
        uperleft.addOrReplaceChild("uperleft_r2", CubeListBuilder.create().texOffs(32, 22).addBox(-4.1F, -2.0F, -3.7F, 5.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.1396F));

        PartDefinition left = leftside.addOrReplaceChild("left", CubeListBuilder.create(), PartPose.offset(3.9F, -6.6F, -1.1F));
        left.addOrReplaceChild("left_r1", CubeListBuilder.create().texOffs(76, 58).addBox(-0.7F, -2.4F, -0.7F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.1F, -1.4F, 0.6981F, 0.0F, 0.2094F));
        left.addOrReplaceChild("left_r2", CubeListBuilder.create().texOffs(74, 52).addBox(-1.1F, -2.3F, -0.5F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4189F, 0.0F, 0.1396F));

        PartDefinition left_arm = leftside.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, -0.6F, 0.2F));
        left_arm.addOrReplaceChild("left_arm_r1", CubeListBuilder.create().texOffs(34, 64).addBox(-2.0F, -2.6F, -1.8F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.2F, 12.4F, 0.2F, -0.0698F, 0.0F, -0.0524F));
        left_arm.addOrReplaceChild("left_arm_r2", CubeListBuilder.create().texOffs(18, 64).addBox(-2.3F, -3.2F, -1.4F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 7.8F, 0.0F, 0.0F, 0.0F, 0.1222F));
        left_arm.addOrReplaceChild("left_arm_r3", CubeListBuilder.create().texOffs(0, 64).addBox(-2.8F, -2.6F, -2.7F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.1745F));

        PartDefinition finger5 = left_arm.addOrReplaceChild("finger5", CubeListBuilder.create(), PartPose.offset(-1.2F, 14.6F, -1.0F));
        finger5.addOrReplaceChild("finger5_r1", CubeListBuilder.create().texOffs(74, 64).addBox(-0.5F, -0.6F, -0.6F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 0.4F, -0.1F, -0.2094F, 0.0F, -0.1047F));

        PartDefinition finger8 = left_arm.addOrReplaceChild("finger8", CubeListBuilder.create(), PartPose.offset(-2.8F, 14.9F, 1.8F));
        finger8.addOrReplaceChild("finger8_r1", CubeListBuilder.create().texOffs(74, 70).addBox(-0.5F, -0.8F, 0.1F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6F, 0.4F, -0.3F, 0.1047F, 0.0F, -0.1396F));

        PartDefinition finger7 = left_arm.addOrReplaceChild("finger7", CubeListBuilder.create(), PartPose.offset(-2.6F, 14.8F, 0.8F));
        finger7.addOrReplaceChild("finger7_r1", CubeListBuilder.create().texOffs(78, 0).addBox(-0.5F, -0.6F, -0.1F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3F, 0.4F, -0.2F, -0.0175F, 0.0F, -0.1396F));

        PartDefinition finger6 = left_arm.addOrReplaceChild("finger6", CubeListBuilder.create(), PartPose.offset(-2.3F, 14.7F, -0.2F));
        finger6.addOrReplaceChild("finger6_r1", CubeListBuilder.create().texOffs(78, 6).addBox(-0.5F, -0.7F, -0.6F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.1F, 0.3F, -0.3F, -0.1396F, 0.0F, -0.1745F));

        PartDefinition headCenter = upper_body.addOrReplaceChild("headCenter", CubeListBuilder.create(), PartPose.offset(0.0F, -17.2F, -4.6F));
        headCenter.addOrReplaceChild("headCenter_r1", CubeListBuilder.create().texOffs(60, 52).addBox(-2.2F, -2.4F, -1.8F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
        .texOffs(60, 52).addBox(-2.2F, -2.4F, -1.8F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.8F, -5.6F, 0.2094F, 0.0F, 0.0F));
        headCenter.addOrReplaceChild("headCenter_r2", CubeListBuilder.create().texOffs(40, 44).addBox(-4.2F, -4.8F, -3.6F, 8.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.3F, -2.4F, 0.1396F, 0.0F, 0.0F));

        PartDefinition boca = headCenter.addOrReplaceChild("boca", CubeListBuilder.create(), PartPose.offset(0.0F, 2.4F, -3.6F));
        boca.addOrReplaceChild("headCenter_r3", CubeListBuilder.create().texOffs(35, 127).addBox(-2.5F, -0.5F, -0.8F, 5.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.2965F, -0.1653F, -1.65F, 0.1745F, 0.0F, 0.0F));
        boca.addOrReplaceChild("headCenter_r4", CubeListBuilder.create().texOffs(35, 127).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4715F, 0.6597F, -1.7F, 0.1745F, 0.0F, 0.0F));
        boca.addOrReplaceChild("headCenter_r5", CubeListBuilder.create().texOffs(35, 122).addBox(-2.8F, -2.4F, -2.1F, 0.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
        .texOffs(35, 122).addBox(-7.8F, -2.4F, -2.1F, 0.0F, 1.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 1.3F, 0.0F, 0.2443F, 0.0F, 0.0F));
        boca.addOrReplaceChild("headCenter_r6", CubeListBuilder.create().texOffs(117, 115).addBox(-2.5F, -0.65F, -1.0F, 5.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3F, -10.1323F, 0.5632F, 0.2443F, 0.0F, 0.0F));
        boca.addOrReplaceChild("headCenter_r7", CubeListBuilder.create().texOffs(60, 44).addBox(-2.8F, -1.4F, -2.1F, 5.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.3F, 0.0F, 0.2443F, 0.0F, 0.0F));

        PartDefinition head1 = headCenter.addOrReplaceChild("head1", CubeListBuilder.create(), PartPose.offset(-4.9F, 0.2F, -0.2F));
        head1.addOrReplaceChild("head1_r1", CubeListBuilder.create().texOffs(60, 68).addBox(-1.6F, -2.7F, -1.2F, 1.4F, 4.6F, 1.7F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -7.6F, -1.9F, 0.1396F, 0.0F, 0.0175F));
        head1.addOrReplaceChild("head1_r2", CubeListBuilder.create().texOffs(57, 66).addBox(-2.1F, -2.0F, -2.1F, 2.5F, 3.9F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -6.8F, -1.9F, 0.1396F, 0.0F, 0.0175F));
        head1.addOrReplaceChild("head1_r3", CubeListBuilder.create().texOffs(60, 68).addBox(-1.6F, -2.7F, -0.7F, 1.3F, 4.6F, 1.9F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -7.4F, -2.8F, 0.1396F, 0.0F, -0.0262F));
        head1.addOrReplaceChild("head1_r4", CubeListBuilder.create().texOffs(57, 66).addBox(-2.1F, -2.0F, -2.1F, 2.6F, 3.9F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -6.8F, -1.9F, 0.1396F, 0.0F, -0.0262F));
        head1.addOrReplaceChild("head1_r5", CubeListBuilder.create().texOffs(56, 65).addBox(-2.1F, -3.1F, -1.9F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.2F, -1.9F, 0.1396F, 0.0F, 0.1047F));

        PartDefinition head4 = headCenter.addOrReplaceChild("head4", CubeListBuilder.create(), PartPose.offset(4.8F, 0.0F, -0.4F));
        head4.addOrReplaceChild("head4_r1", CubeListBuilder.create().texOffs(63, 60).addBox(-0.3F, -3.2F, -0.8F, 1.4F, 5.0F, 1.8F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.7F, -6.9F, -2.6F, 0.1396F, 0.0F, 0.0262F));
        head4.addOrReplaceChild("head4_r2", CubeListBuilder.create().texOffs(58, 57).addBox(-1.2F, -2.6F, -2.1F, 2.7F, 4.4F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.7F, -6.0F, -1.7F, 0.1396F, 0.0F, 0.0262F));
        head4.addOrReplaceChild("head4_r3", CubeListBuilder.create().texOffs(56, 56).addBox(-1.6F, -3.2F, -1.9F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.3F, -1.7F, 0.1396F, 0.0F, -0.1047F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(MutantEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        // Idle sempre ativo (breathing). Walk dirigida pelo limbSwingAmount.
        // Demais são triggadas server→client via AnimationStates.
        this.animate(entity.idleAnimationState, MutantAnimations.idle, ageInTicks);
        this.animateWalk(MutantAnimations.walk, limbSwing, limbSwingAmount, 2.0F, 2.5F);

        this.animate(entity.atackMeleeState, MutantAnimations.atackMelee, ageInTicks);
        this.animate(entity.spinState, MutantAnimations.spin, ageInTicks);
        this.animate(entity.hardHitState, MutantAnimations.hardHit, ageInTicks);
        this.animate(entity.highAtackState, MutantAnimations.highAtack, ageInTicks);
    }
}
