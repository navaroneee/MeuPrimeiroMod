// Made with Blockbench 5.1.3
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class mutant<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "mutant"), "main");
	private final ModelPart left_leg;
	private final ModelPart right_leg;
	private final ModelPart upper_body;
	private final ModelPart rightside;
	private final ModelPart upperright;
	private final ModelPart neckright;
	private final ModelPart right_arm;
	private final ModelPart finger2;
	private final ModelPart finger4;
	private final ModelPart finger3;
	private final ModelPart finger1;
	private final ModelPart head4;
	private final ModelPart leftside;
	private final ModelPart head1;
	private final ModelPart uperleft;
	private final ModelPart left;
	private final ModelPart left_arm;
	private final ModelPart finger5;
	private final ModelPart finger8;
	private final ModelPart finger7;
	private final ModelPart finger6;
	private final ModelPart headCenter;

	public mutant(ModelPart root) {
		this.left_leg = root.getChild("left_leg");
		this.right_leg = root.getChild("right_leg");
		this.upper_body = root.getChild("upper_body");
		this.rightside = this.upper_body.getChild("rightside");
		this.upperright = this.rightside.getChild("upperright");
		this.neckright = this.rightside.getChild("neckright");
		this.right_arm = this.rightside.getChild("right_arm");
		this.finger2 = this.right_arm.getChild("finger2");
		this.finger4 = this.right_arm.getChild("finger4");
		this.finger3 = this.right_arm.getChild("finger3");
		this.finger1 = this.right_arm.getChild("finger1");
		this.head4 = this.rightside.getChild("head4");
		this.leftside = this.upper_body.getChild("leftside");
		this.head1 = this.leftside.getChild("head1");
		this.uperleft = this.leftside.getChild("uperleft");
		this.left = this.leftside.getChild("left");
		this.left_arm = this.leftside.getChild("left_arm");
		this.finger5 = this.left_arm.getChild("finger5");
		this.finger8 = this.left_arm.getChild("finger8");
		this.finger7 = this.left_arm.getChild("finger7");
		this.finger6 = this.left_arm.getChild("finger6");
		this.headCenter = this.upper_body.getChild("headCenter");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(44, 50).addBox(-1.0F, 15.0F, -4.0F, 5.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(22, 64).addBox(-1.0F, 0.1422F, -2.2986F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.8F, 7.0F, 0.0F));

		PartDefinition cube_r1 = left_leg.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(62, 70).addBox(0.0F, -6.0F, 0.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 10.1422F, -1.2986F, -0.2182F, 0.0F, 0.0F));

		PartDefinition cube_r2 = left_leg.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(70, 59).addBox(0.0F, -6.0F, 0.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(44, 60).addBox(-4.0F, 14.0F, -4.0F, 5.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(0, 64).addBox(-4.0F, -0.8578F, -2.2986F, 5.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.8F, 8.0F, 0.0F));

		PartDefinition cube_r3 = right_leg.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(74, 9).addBox(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.1422F, -1.2986F, -0.2182F, 0.0F, 0.0F));

		PartDefinition cube_r4 = right_leg.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(72, 0).addBox(-3.0F, -6.0F, 0.0F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 15.0F, 0.0F, 0.2182F, 0.0F, 0.0F));

		PartDefinition upper_body = partdefinition.addOrReplaceChild("upper_body", CubeListBuilder.create(), PartPose.offset(-0.1F, 8.1422F, 0.4014F));

		PartDefinition cube_r5 = upper_body.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(52, 79).addBox(2.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(48, 79).addBox(-4.6F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.3F, -18.4405F, -4.2878F, 1.1345F, 0.0F, 0.0F));

		PartDefinition cube_r6 = upper_body.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(40, 77).addBox(2.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3F, -16.7123F, -2.6309F, 0.6109F, 0.0F, 0.0F));

		PartDefinition cube_r7 = upper_body.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(28, 77).addBox(2.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.3F, -14.425F, -2.15F, 0.0436F, 0.0F, 0.0F));

		PartDefinition rightside = upper_body.addOrReplaceChild("rightside", CubeListBuilder.create(), PartPose.offset(8.7F, -10.325F, -1.85F));

		PartDefinition upperright = rightside.addOrReplaceChild("upperright", CubeListBuilder.create(), PartPose.offset(-4.3F, 4.4658F, 1.733F));

		PartDefinition cube_r8 = upperright.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -5.0F, -3.0F, 10.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -2.9658F, -3.033F, 0.0436F, 0.0F, 0.0F));

		PartDefinition cube_r9 = upperright.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(38, 34).addBox(-6.0F, -5.0F, -2.0F, 9.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 2.8592F, -2.583F, 0.0873F, 0.0F, 0.0F));

		PartDefinition cube_r10 = upperright.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(42, 14).addBox(-6.0F, -1.0F, -1.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 4.8592F, -2.583F, 0.1309F, 0.0F, 0.0F));

		PartDefinition neckright = rightside.addOrReplaceChild("neckright", CubeListBuilder.create(), PartPose.offset(-4.4F, -4.1F, -0.3F));

		PartDefinition cube_r11 = neckright.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(78, 76).addBox(2.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.2873F, -0.4809F, 0.6109F, 0.0F, 0.0F));

		PartDefinition cube_r12 = neckright.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(32, 77).addBox(2.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition right_arm = rightside.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(4.0F, 0.0F, 0.0F));

		PartDefinition cube_r13 = right_arm.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(74, 26).addBox(2.0F, -2.0F, 0.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.834F, 11.097F, 0.2181F, 0.0436F, 0.0F, 0.0436F));

		PartDefinition cube_r14 = right_arm.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(74, 18).addBox(2.0F, -2.0F, 0.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1F, 7.0F, 0.0F, 0.0436F, 0.0F, -0.1309F));

		PartDefinition cube_r15 = right_arm.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(0, 50).addBox(1.0F, -2.0F, -2.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0436F, 0.0F, -0.1309F));

		PartDefinition finger2 = right_arm.addOrReplaceChild("finger2", CubeListBuilder.create(), PartPose.offset(1.3712F, 14.0747F, 0.9617F));

		PartDefinition cube_r16 = finger2.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(76, 42).addBox(2.0F, -2.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.425F, 1.825F, -0.7F, -0.0436F, 0.0F, 0.0436F));

		PartDefinition finger4 = right_arm.addOrReplaceChild("finger4", CubeListBuilder.create(), PartPose.offset(1.7462F, 14.3997F, 2.8862F));

		PartDefinition cube_r17 = finger4.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(74, 76).addBox(2.0F, -2.0F, 2.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, 1.775F, -2.25F, 0.1745F, 0.0F, 0.0436F));

		PartDefinition finger3 = right_arm.addOrReplaceChild("finger3", CubeListBuilder.create(), PartPose.offset(1.6212F, 14.1747F, 1.9612F));

		PartDefinition cube_r18 = finger3.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(4, 77).addBox(2.0F, -2.0F, 1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.675F, 2.0F, -1.525F, 0.0436F, 0.0F, 0.0436F));

		PartDefinition finger1 = right_arm.addOrReplaceChild("finger1", CubeListBuilder.create(), PartPose.offset(-0.5269F, 13.9625F, 0.4612F));

		PartDefinition cube_r19 = finger1.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(20, 77).addBox(2.0F, -2.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.475F, 1.8984F, -0.1853F, -0.1309F, 0.0F, 0.0436F));

		PartDefinition head4 = rightside.addOrReplaceChild("head4", CubeListBuilder.create(), PartPose.offset(-2.2F, -8.6F, -3.3F));

		PartDefinition cube_r20 = head4.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(44, 70).addBox(0.0F, -3.0F, -1.0F, 5.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.2F, -0.6F, -2.8F, 0.0436F, 0.0F, 0.0F));

		PartDefinition leftside = upper_body.addOrReplaceChild("leftside", CubeListBuilder.create(), PartPose.offset(-9.4981F, -11.4875F, 0.2612F));

		PartDefinition head1 = leftside.addOrReplaceChild("head1", CubeListBuilder.create(), PartPose.offset(2.7981F, -8.0375F, -5.2112F));

		PartDefinition cube_r21 = head1.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(70, 50).addBox(-5.0F, -3.0F, -1.0F, 5.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6F, 0.0F, -3.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition uperleft = leftside.addOrReplaceChild("uperleft", CubeListBuilder.create(), PartPose.offset(3.7981F, 10.4875F, -2.9612F));

		PartDefinition cube_r22 = uperleft.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(42, 24).addBox(-2.0F, -1.0F, -1.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

		PartDefinition cube_r23 = uperleft.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(0, 34).addBox(-3.0F, -5.0F, -2.0F, 9.0F, 6.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0873F, 0.0F, 0.0F));

		PartDefinition cube_r24 = uperleft.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(0, 17).addBox(-4.0F, -5.0F, -3.0F, 10.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.825F, -0.45F, 0.0436F, 0.0F, 0.0F));

		PartDefinition left = leftside.addOrReplaceChild("left", CubeListBuilder.create(), PartPose.offset(2.8981F, -2.6658F, -1.9646F));

		PartDefinition cube_r25 = left.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(44, 79).addBox(-3.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -4.2872F, -2.5845F, 1.1345F, 0.0F, 0.0F));

		PartDefinition cube_r26 = left.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(36, 77).addBox(-3.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -2.559F, -0.9275F, 0.6109F, 0.0F, 0.0F));

		PartDefinition cube_r27 = left.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(24, 77).addBox(-3.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, -0.2717F, -0.4466F, 0.0436F, 0.0F, 0.0F));

		PartDefinition left_arm = leftside.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(-3.0F, 1.0F, 0.0F));

		PartDefinition cube_r28 = left_arm.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(76, 34).addBox(-5.0F, -2.0F, 0.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8321F, 11.2595F, -1.8931F, 0.0436F, 0.0F, -0.0436F));

		PartDefinition cube_r29 = left_arm.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(74, 68).addBox(-5.0F, -2.0F, 0.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0981F, 7.1625F, -2.1112F, 0.0436F, 0.0F, 0.1309F));

		PartDefinition cube_r30 = left_arm.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(22, 50).addBox(-6.0F, -2.0F, -2.0F, 5.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.9981F, 0.1625F, -2.1112F, 0.0436F, 0.0F, 0.1309F));

		PartDefinition finger5 = left_arm.addOrReplaceChild("finger5", CubeListBuilder.create(), PartPose.offset(0.5F, 14.4F, -1.7F));

		PartDefinition cube_r31 = finger5.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(16, 77).addBox(-3.0F, -2.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 1.6F, -0.3F, -0.1309F, 0.0F, -0.0436F));

		PartDefinition finger8 = left_arm.addOrReplaceChild("finger8", CubeListBuilder.create(), PartPose.offset(-1.4481F, 14.3372F, 0.725F));

		PartDefinition cube_r32 = finger8.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(0, 77).addBox(-3.0F, -2.0F, 2.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5F, 2.0F, -2.2F, 0.1745F, 0.0F, -0.0436F));

		PartDefinition finger7 = left_arm.addOrReplaceChild("finger7", CubeListBuilder.create(), PartPose.offset(-1.6481F, 14.4372F, -0.275F));

		PartDefinition cube_r33 = finger7.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(8, 77).addBox(-3.0F, -2.0F, 1.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.7F, 1.9F, -1.4F, 0.0436F, 0.0F, -0.0436F));

		PartDefinition finger6 = left_arm.addOrReplaceChild("finger6", CubeListBuilder.create(), PartPose.offset(-1.5481F, 14.4622F, -1.2495F));

		PartDefinition cube_r34 = finger6.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(12, 77).addBox(-3.0F, -2.0F, 0.0F, 1.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6F, 1.6F, -0.6F, -0.0436F, 0.0F, -0.0436F));

		PartDefinition headCenter = upper_body.addOrReplaceChild("headCenter", CubeListBuilder.create(), PartPose.offset(0.2F, -19.725F, -3.05F));

		PartDefinition cube_r35 = headCenter.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(42, 0).addBox(-1.0F, -5.0F, -3.0F, 7.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, 0.2F, -4.9F, 0.0436F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		left_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		right_leg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		upper_body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}