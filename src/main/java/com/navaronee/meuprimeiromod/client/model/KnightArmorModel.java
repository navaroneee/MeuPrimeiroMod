package com.navaronee.meuprimeiromod.client.model;

import com.navaronee.meuprimeiromod.MeuPrimeiroMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * Knight Armor — armadura medieval com shoulder pads, leggings de placa e
 * uma espada decorativa cravada no peito.
 *
 * Port do BlockBench: parts renomeados pra convenção HumanoidModel
 * (head/body/leftArm/rightArm/leftLeg/rightLeg). Boots e leggings ficam num
 * layer próprio pra evitar Z-fight com o chestplate quando o player equipa
 * parcial.
 */
public class KnightArmorModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(MeuPrimeiroMod.MODID, "knight_armor"), "main");
    public static final ModelLayerLocation LAYER_LOCATION_LEGGINGS =
            new ModelLayerLocation(new ResourceLocation(MeuPrimeiroMod.MODID, "knight_armor"), "leggings");
    public static final ModelLayerLocation LAYER_LOCATION_BOOTS =
            new ModelLayerLocation(new ResourceLocation(MeuPrimeiroMod.MODID, "knight_armor"), "boots");

    /** Lâmina (filha de body) — só aparece se o player tem knight_sword no inventário. */
    public final ModelPart swordBone;
    /** Pomo+cabo+guarda (filha de sword) — some quando knight_sword tá na mão. */
    public final ModelPart bainhaBone;

    public KnightArmorModel(ModelPart root) {
        super(root);
        // sword + bainha só existem na main layer (body); nas outras dão null silently
        ModelPart body = root.hasChild("body") ? root.getChild("body") : null;
        ModelPart sword = (body != null && body.hasChild("sword")) ? body.getChild("sword") : null;
        this.swordBone = sword;
        this.bainhaBone = (sword != null && sword.hasChild("bainha")) ? sword.getChild("bainha") : null;
    }

    /**
     * Define visibilidade baseada no inventário do entity:
     *  - sword visible: tem knight_sword em qualquer slot do inventário
     *  - bainha visible: visible=true E knight_sword NÃO está na mão (main/off)
     */
    public void updateSwordVisibility(net.minecraft.world.entity.LivingEntity entity) {
        if (swordBone == null) return;
        boolean hasInInventory = false;
        boolean hasInHand = false;
        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            net.minecraft.world.item.ItemStack mainHand = player.getMainHandItem();
            net.minecraft.world.item.ItemStack offHand = player.getOffhandItem();
            hasInHand = mainHand.is(com.navaronee.meuprimeiromod.item.ModItems.KNIGHT_SWORD.get())
                    || offHand.is(com.navaronee.meuprimeiromod.item.ModItems.KNIGHT_SWORD.get());
            hasInInventory = hasInHand
                    || player.getInventory().contains(
                        new net.minecraft.world.item.ItemStack(
                            com.navaronee.meuprimeiromod.item.ModItems.KNIGHT_SWORD.get()));
        }
        swordBone.visible = hasInInventory;
        if (bainhaBone != null) {
            bainhaBone.visible = hasInInventory && !hasInHand;
        }
    }

    /**
     * Layer principal — usado pros slots HEAD e CHEST. Tem helmet, chestplate
     * (+ espada), arms (+ shoulder pads). Pernas ficam vazias aqui pra não
     * conflitar com leggings/boots layers.
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // === HEAD === (helmet + side flaps)
        // Player HAT layer (hair/etc) é deformation 0.5. Helmet precisa estar
        // acima disso (0.55+) pra cobrir; senão hat skin layer aparece atravessando.
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(70, 47).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.55F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("helmet_r1", CubeListBuilder.create()
                .texOffs(54, 52).mirror().addBox(-6.3487F, -1.01F, -4.8841F, 5.0F, 9.0F, 3.0F, new CubeDeformation(0.1F)).mirror(false),
                PartPose.offsetAndRotation(4.0F, -8.0F, -4.0F, 0.0F, 0.6545F, 0.0F));

        head.addOrReplaceChild("helmet_r2", CubeListBuilder.create()
                .texOffs(54, 52).addBox(1.3487F, -1.01F, -4.8841F, 5.0F, 9.0F, 3.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(-4.0F, -8.0F, -4.0F, 0.0F, -0.6545F, 0.0F));

        // hat — exigido pelo HumanoidModel, vazio
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        // === BODY === (chestplate + espada decorativa)
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 47).addBox(-4.0F, 0.0F, -2.5F, 8.0F, 12.0F, 5.0F, new CubeDeformation(0.1F))
                .texOffs(26, 46).addBox(-4.0F, 0.0F, -2.5F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.3F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        // Lâmina (sword) — só a long thin cube, sem hilt
        PartDefinition sword = body.addOrReplaceChild("sword", CubeListBuilder.create()
                .texOffs(50, 20).addBox(-2.7934F, -6.3912F, 0.0F, 3.0F, 18.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, 6.0F, 3.5F, 0.0F, 0.0F, 0.6545F));

        // Cabo+pomo+guarda (bainha) — sub-bone que some quando a sword tá na mão
        sword.addOrReplaceChild("bainha", CubeListBuilder.create()
                .texOffs(60, 20).addBox(-0.2934F, -33.0912F, 3.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(60, 24).addBox(-0.2934F, -31.2912F, 3.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(50, 17).addBox(-2.7934F, -25.3912F, 3.5F, 7.0F, 1.0F, 2.0F, new CubeDeformation(0.1F)),
                PartPose.offset(-2.0F, 18.0F, -3.5F));

        // === LEFT ARM === (sleeve + shoulder pad)
        PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
                .texOffs(18, 28).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(34, 28).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition shoulder = leftArm.addOrReplaceChild("shoulder", CubeListBuilder.create(),
                PartPose.offsetAndRotation(1.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

        shoulder.addOrReplaceChild("shoulder_r1", CubeListBuilder.create()
                .texOffs(0, 27).addBox(-3.9848F, -4.6737F, -2.5F, 4.0F, 6.0F, 5.0F, new CubeDeformation(0.1F))
                .texOffs(0, 38).addBox(-3.9848F, 1.3263F, -2.5F, 3.0F, 4.0F, 5.0F, new CubeDeformation(0.1F))
                .texOffs(0, 20).addBox(0.0152F, -4.6737F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(3.0F, 2.5F, 0.0F, 0.0F, 0.0F, -0.3491F));

        // === RIGHT ARM === (sleeve + shoulder pad)
        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                .texOffs(18, 28).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(34, 28).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.3F)),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition shoulder2 = rightArm.addOrReplaceChild("shoulder2", CubeListBuilder.create(),
                PartPose.offsetAndRotation(-1.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

        shoulder2.addOrReplaceChild("shoulder2_r1", CubeListBuilder.create()
                .texOffs(0, 27).addBox(-0.0152F, -4.6737F, -2.5F, 4.0F, 6.0F, 5.0F, new CubeDeformation(0.1F))
                .texOffs(0, 38).addBox(0.9848F, 1.3263F, -2.5F, 3.0F, 4.0F, 5.0F, new CubeDeformation(0.1F))
                .texOffs(0, 20).addBox(-1.0152F, -4.6737F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(-3.0F, 2.5F, 0.0F, 0.0F, 0.0F, 0.3491F));

        // Pernas vazias na main layer (vão no leggings/boots layers)
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    /**
     * Layer LEGGINGS (slot LEGS) — só as pernas com placa de leggings + side panels.
     * Body/braços/cabeça ficam vazios pra não duplicar o chestplate quando o player
     * equipa só leggings.
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

        // Right leg — leva os side panels do export "leggingsright" (que apesar do nome,
        // tem cube renderizado no lado RIGHT do corpo). Anima junto com a perna direita.
        PartDefinition rightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        rightLeg.addOrReplaceChild("right_side_r1", CubeListBuilder.create()
                .texOffs(0, 0).mirror().addBox(-5.0F, -0.5F, -2.5F, 4.0F, 10.0F, 5.0F, new CubeDeformation(0.2F)).mirror(false),
                PartPose.offsetAndRotation(2.9F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0873F));

        rightLeg.addOrReplaceChild("right_side_r2", CubeListBuilder.create()
                .texOffs(54, 0).mirror().addBox(-4.6F, 0.5F, -2.5F, 4.0F, 10.0F, 5.0F, new CubeDeformation(0.21F)).mirror(false),
                PartPose.offsetAndRotation(2.9F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

        // Left leg — leva os panels do export "legginsleft" (cube no lado LEFT do corpo).
        PartDefinition leftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        leftLeg.addOrReplaceChild("left_side_r1", CubeListBuilder.create()
                .texOffs(54, 0).addBox(0.9F, 0.6F, -2.5F, 4.0F, 10.0F, 5.0F, new CubeDeformation(0.21F)),
                PartPose.offsetAndRotation(-2.9F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

        leftLeg.addOrReplaceChild("left_side_r2", CubeListBuilder.create()
                .texOffs(0, 0).addBox(1.0F, -0.5F, -2.5F, 4.0F, 10.0F, 5.0F, new CubeDeformation(0.19F)),
                PartPose.offsetAndRotation(-2.9F, 0.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }

    /**
     * Layer BOOTS (slot FEET) — só os shafts das pernas pra cobrir os pés/tornozelos.
     * Deformation 0.2 fica POR FORA da perna do player.
     */
    public static LayerDefinition createBootsLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(),
                PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        // Boots: parte inferior da perna (Y=4..12 = altura da bota).
        // Os pivôs ficam nos lados padrões do HumanoidModel; o cube X é centrado
        // (-2..2) — animação vem direto do copyPropertiesTo do player.
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
                .texOffs(18, 12).addBox(-2.0F, 4.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(34, 12).addBox(-2.0F, 4.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.31F)),
                PartPose.offset(-2.0F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                .texOffs(18, 12).addBox(-2.0F, 4.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(34, 12).addBox(-2.0F, 4.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.31F)),
                PartPose.offset(2.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 64);
    }
}
