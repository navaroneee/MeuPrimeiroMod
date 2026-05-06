package com.navaronee.meuprimeiromod.client.model;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * Modelo da armadura de chumbo (versão 2 — exportada do BlockBench, textura 128x128).
 *
 * Ajustes feitos no port pra HumanoidModel:
 *  - Removido o wrapper "root" do export (offset 0,24,0)
 *  - Swap dos braços (BlockBench exportou com naming invertido vs convenção vanilla)
 *  - Pivots dos braços normalizados pra HumanoidModel padrão (-5,2,0) / (5,2,0)
 *    porque o export tinha pivot do braço esquerdo na coxa (Y=15), o que fazia
 *    o braço aparecer de cabeça pra baixo em jogo
 *  - Detalhes que estavam dentro de "helmet" mas em altura de chestplate
 *    (hexadecagons das laterais + cubo das costas) movidos pro `body`
 *  - Pernas com Z centralizado
 */
public class LeadArmorModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(MeuPrimeiroMod.MODID, "lead_armor"), "main");
    public static final ModelLayerLocation LAYER_LOCATION_LEGGINGS =
            new ModelLayerLocation(new ResourceLocation(MeuPrimeiroMod.MODID, "lead_armor"), "leggings");
    public static final ModelLayerLocation LAYER_LOCATION_BOOTS =
            new ModelLayerLocation(new ResourceLocation(MeuPrimeiroMod.MODID, "lead_armor"), "boots");

    public LeadArmorModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // === BODY === (chestplate + detalhes que estavam mal-grupados no helmet do export)
        // Padrão dual-layer (igual knight): inner 0.10 cobre o player base perto da skin,
        // outer 0.30+ fica fora da outer skin layer (0.25). Sem brechas pra skin aparecer.
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 5.0F, new CubeDeformation(0.10F))
                .texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 5.0F, new CubeDeformation(0.30F))
                .texOffs(26, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 5.0F, new CubeDeformation(0.40F))
                // cubo das costas (era box 3 do helmet) — agora em local do body
                .texOffs(58, 26).addBox(-3.0F, 9.0F, 3.0F, 6.0F, 1.0F, 3.0F, new CubeDeformation(0.01F)),
                PartPose.offset(0.0F, 0.0F, -1.0F));

        // hexadecagons laterais (eram do helmet) — pivôs ajustados pra coords do body
        body.addOrReplaceChild("hexadecagon_r1", CubeListBuilder.create()
                .texOffs(48, 43).addBox(-2.5F, -5.0F, -3.9F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.32F))
                .texOffs(16, 43).addBox(-2.5F, -5.0F, -3.9F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(-1.5F, 5.0F, 6.9F, 0.0F, 0.7854F, 0.0F));

        body.addOrReplaceChild("hexadecagon_r2", CubeListBuilder.create()
                .texOffs(0, 49).addBox(-2.0F, -5.0F, -2.9F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.32F))
                .texOffs(32, 43).addBox(-2.0F, -5.0F, -2.9F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(2.0F, 5.0F, 5.9F, 0.0F, -0.7854F, 0.0F));

        // === HEAD (rotacao base) + HELMET (visual) ===
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // Helmet box: estava em Z=-5..3 (deslocado 1 bloco pra frente do head),
        // o que deixava o fundo do crânio exposto. Realinhamos pra Z=-4..4 (mesmo
        // intervalo do player head) e dilation 0.3 pra cobrir uniformemente.
        // Player HAT layer (hair/etc) é deformation 0.5. Helmet outer precisa
        // ser 0.55+ pra cobrir totalmente; senão pixels da hat layer (cabelo,
        // sobrancelha) aparecem atravessando o capacete.
        PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -32.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.10F))
                .texOffs(0, 0).addBox(-4.0F, -32.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.55F))
                .texOffs(52, 22).addBox(-4.0F, -26.0F, 3.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.01F))
                .texOffs(64, 46).addBox(-3.0F, -26.1F, -6.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.21F))
                .texOffs(58, 30).addBox(-5.0F, -33.0F, -0.5F, 5.0F, 8.0F, 1.0F, new CubeDeformation(0.21F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        helmet.addOrReplaceChild("head_r1", CubeListBuilder.create()
                .texOffs(48, 7).addBox(0.0F, -1.0F, 0.5F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.31F)),
                PartPose.offsetAndRotation(0.0F, -25.0F, -6.0F, 0.0F, 0.0F, 0.2618F));

        helmet.addOrReplaceChild("head_r2", CubeListBuilder.create()
                .texOffs(48, 0).addBox(-5.0F, -1.0F, 0.5F, 5.0F, 1.0F, 6.0F, new CubeDeformation(0.31F)),
                PartPose.offsetAndRotation(0.0F, -25.0F, -6.0F, 0.0F, 0.0F, -0.2618F));

        // hat — exigido pelo HumanoidModel, vazio
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        // === LEFT ARM (era "rightArm" do export, swap) — pivot normalizado (5,2,0) ===
        // Box Z: -3..1 → -2..2 (centralizado no corpo, sem espirrar pra fora)
        PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(0, 33).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.10F))
                .texOffs(0, 33).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.32F)),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        leftArm.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                .texOffs(12, 64).addBox(0.0F, -2.8F, -1.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.31F)),
                PartPose.offsetAndRotation(1.0F, 6.8F, -2.0F, 0.0F, 0.0F, 0.2618F));

        leftArm.addOrReplaceChild("cube_r2", CubeListBuilder.create()
                .texOffs(0, 62).addBox(0.0F, -2.8F, -1.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.36F)),
                PartPose.offsetAndRotation(1.0F, 7.8F, -2.0F, 0.0F, 0.0F, 0.5236F));

        leftArm.addOrReplaceChild("cube_r3", CubeListBuilder.create()
                .texOffs(52, 14).addBox(0.0F, -4.0F, -1.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.36F)),
                PartPose.offsetAndRotation(-1.0F, 0.8F, -2.0F, 0.0F, 0.0F, 0.2182F));

        // === RIGHT ARM (era "leftArm" do export, swap) — pivot normalizado (-5,2,0) ===
        // Box Z: -3..1 → -2..2 (centralizado no corpo)
        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(42, 27).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.10F))
                .texOffs(42, 27).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.32F)),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        rightArm.addOrReplaceChild("cube_r4", CubeListBuilder.create()
                .texOffs(16, 56).addBox(-6.0F, -4.0F, -1.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.36F)),
                PartPose.offsetAndRotation(1.0F, 0.8F, -2.0F, 0.0F, 0.0F, -0.2182F));

        rightArm.addOrReplaceChild("cube_r5", CubeListBuilder.create()
                .texOffs(64, 39).addBox(-3.0F, -3.0F, -1.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.36F)),
                PartPose.offsetAndRotation(0.0F, 8.0F, -2.0F, 0.0F, 0.0F, -0.5236F));

        rightArm.addOrReplaceChild("cube_r6", CubeListBuilder.create()
                .texOffs(24, 64).addBox(-3.0F, -3.0F, -1.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.31F)),
                PartPose.offsetAndRotation(0.0F, 7.0F, -2.0F, 0.0F, 0.0F, -0.2618F));

        // === RIGHT LEG === (só leg shaft — boot detail vai no createBootsLayer)
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(26, 27).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.10F))
                .texOffs(26, 27).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.32F)),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        // === LEFT LEG === (só leg shaft)
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(32, 0).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.10F))
                .texOffs(32, 0).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.32F)),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    /**
     * Layer só de LEGGINGS (slot LEGS): body vazio, só os leg shafts.
     * Vanilla por padrão mostra body+legs no slot LEGS, mas a gente quer que body
     * só apareça com CHEST equipado.
     */
    public static LayerDefinition createLeggingsLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(),
                PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(26, 27).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.10F))
                .texOffs(26, 27).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.32F)),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(32, 0).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.10F))
                .texOffs(32, 0).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.32F)),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    /**
     * Layer só do BOOTS (slot FEET): cabeça/corpo/braços vazios; pernas com APENAS
     * o detalhe da bota. Assim:
     *  - LEGS slot renderiza com layer principal (leg shafts visíveis)
     *  - FEET slot renderiza com este (boot detail visível)
     *  - Set completo = ambos sobrepostos sem Z-fight (deformation diferente, boot fica fora)
     */
    public static LayerDefinition createBootsLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Parts vazios — exigidos pelo HumanoidModel pra bake funcionar
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(),
                PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        // Boot detail — deformation 0.4 (vs 0.25 da legging) faz a bota ficar POR
        // FORA da perna, visível mesmo se as duas peças estiverem equipadas
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(36, 56).addBox(-2.1F, 4.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.41F)),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(52, 56).addBox(-1.9F, 4.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.41F)),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }
}
