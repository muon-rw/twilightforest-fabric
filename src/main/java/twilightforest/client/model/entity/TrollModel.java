package twilightforest.client.model.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import twilightforest.entity.TrollEntity;

public class TrollModel extends HumanoidModel<TrollEntity> {

	public ModelPart nose;

	public TrollModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition create() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition partRoot = mesh.getRoot();

		var head = partRoot.addOrReplaceChild("head", CubeListBuilder.create()
						.texOffs(0, 0)
						.addBox(-5.0F, -8.0F, -3.0F, 10.0F, 10.0F, 10.0F),
				PartPose.offset(0.0F, -9.0F, -6.0F));

		partRoot.addOrReplaceChild("hat", CubeListBuilder.create(),
				PartPose.ZERO);

		head.addOrReplaceChild("nose", CubeListBuilder.create()
						.texOffs(0, 21)
						.addBox(-2.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F),
				PartPose.offset(0.0F, -2.0F, -4.0F));

		partRoot.addOrReplaceChild("body", CubeListBuilder.create()
						.texOffs(40, 0)
						.addBox(-8.0F, 0.0F, -5.0F, 16.0F, 26.0F, 10.0F),
				PartPose.offset(0.0F, -14.0F, 0.0F));

		partRoot.addOrReplaceChild("right_arm", CubeListBuilder.create()
						.texOffs(32, 36)
						.addBox(-5.0F, -2.0F, -3.0F, 6.0F, 22.0F, 6.0F),
				PartPose.offset(-9.0F, -9.0F, 0.0F));

		partRoot.addOrReplaceChild("left_arm", CubeListBuilder.create().mirror()
						.texOffs(32, 36)
						.addBox(-1.0F, -2.0F, -3.0F, 6.0F, 22.0F, 6.0F),
				PartPose.offset(9.0F, -9.0F, 0.0F));

		partRoot.addOrReplaceChild("right_leg", CubeListBuilder.create()
						.texOffs(0, 44)
						.addBox(-3.0F, 0.0F, -4.0F, 6.0F, 12.0F, 8.0F),
				PartPose.offset(-5.0F, 12.0F, 0.0F));

		partRoot.addOrReplaceChild("left_leg", CubeListBuilder.create().mirror()
						.texOffs(0, 44)
						.addBox(-3.0F, 0.0F, -4.0F, 6.0F, 12.0F, 8.0F),
				PartPose.offset(5.0F, 12.0F, 0.0F));

		return LayerDefinition.create(mesh, 128, 64);
	}

	@Override
	public void setupAnim(TrollEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw / (180F / (float) Math.PI);
		this.head.xRot = headPitch / (180F / (float) Math.PI);
		this.hat.yRot = this.head.yRot;
		this.hat.xRot = this.head.xRot;
		this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
		this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
		this.rightArm.zRot = 0.0F;
		this.leftArm.zRot = 0.0F;
		this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
		this.rightLeg.yRot = 0.0F;
		this.leftLeg.yRot = 0.0F;

		if (entity.isVehicle()) {
			// arms up!
			this.rightArm.xRot += Math.PI;
			this.leftArm.xRot += Math.PI;

		}

		if (this.leftArmPose != ArmPose.EMPTY) {
			this.rightArm.xRot += Math.PI;
		}
		if (this.rightArmPose != ArmPose.EMPTY) {
			this.leftArm.xRot += Math.PI;
		}

		if (this.attackTime > 0F) {
			float swing = 1.0F - this.attackTime;

			this.rightArm.xRot -= (Math.PI * swing);
			this.leftArm.xRot -= (Math.PI * swing);
		}

		this.rightArm.yRot = 0.0F;
		this.leftArm.yRot = 0.0F;

		this.rightArm.zRot += Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.leftArm.zRot -= Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		this.rightArm.xRot += Mth.sin(ageInTicks * 0.067F) * 0.05F;
		this.leftArm.xRot -= Mth.sin(ageInTicks * 0.067F) * 0.05F;
	}

	@Override
	public void prepareMobModel(TrollEntity entity, float limbSwing, float limbSwingAmount, float partialTicks) {
		if (entity.getTarget() != null) {
			this.rightArm.xRot += Math.PI;
			this.leftArm.xRot += Math.PI;
		}
	}
}