package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.client.models.items;

import com.google.common.collect.ImmutableList;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class CloakOfTheBloodyFeatherModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ReliquifiedIronsSpellsAndSpellbooks.MODID, "cloak_of_the_bloody_feather"), "cloak_of_the_bloody_feather");

    public ModelPart bodyPart;
    public ModelPart leftArmPart;
    public ModelPart rightArmPart;

    public CloakOfTheBloodyFeatherModel(ModelPart root) {
        super(root);

        this.bodyPart = root.getChild("body");
        this.leftArmPart = root.getChild("left_arm");
        this.rightArmPart = root.getChild("right_arm");
    }

    public static LayerDefinition constructLayerDefinition() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(-0.0003F, 0.3556F, 0.0307F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(44, 24).addBox(-5.501F, 0.3112F, -2.8444F, 11.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0003F, 0.2131F, 0.3331F, -0.1745F, 0.0F, 0.0F));

        PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(70, 18).addBox(-7.5F, -3.0F, 0.0F, 15.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0003F, 20.1503F, 9.4187F, 1.0036F, 0.0F, 0.0F));

        PartDefinition cube_r3 = body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(70, 12).addBox(-7.5F, -3.0F, 0.0F, 15.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0003F, 16.9003F, 8.5187F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r4 = body.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(70, 0).addBox(-7.5F, -3.0F, 0.0F, 15.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0003F, 3.4003F, 5.3687F, 0.6109F, 0.0F, 0.0F));

        PartDefinition cube_r5 = body.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(70, 6).addBox(-7.5F, -3.0F, 0.0F, 15.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0003F, 5.9003F, 5.9687F, 0.6109F, 0.0F, 0.0F));

        PartDefinition cube_r6 = body.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(70, 6).addBox(-7.5F, -3.0F, 0.0F, 15.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0003F, 12.9003F, 7.5687F, 0.6109F, 0.0F, 0.0F));

        PartDefinition cube_r7 = body.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(70, 0).addBox(-7.5F, -3.0F, 0.0F, 15.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0003F, 9.4003F, 6.8687F, 0.6109F, 0.0F, 0.0F));

        PartDefinition cube_r8 = body.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 15).addBox(-5.5F, -0.5585F, 3.1936F, 11.0F, 24.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(12, 39).addBox(-5.5F, -0.5585F, -2.8064F, 0.0F, 24.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 39).addBox(5.5F, -0.5585F, -2.8064F, 0.0F, 24.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(99, -2).mirror().addBox(-5.5F, -4.5585F, -2.8064F, 0.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(116, -2).addBox(5.5F, -4.5585F, -2.8064F, 0.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(99, 0).addBox(-5.5F, -4.5585F, 3.1936F, 11.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(32, 6).addBox(-5.5F, -0.5585F, -2.8064F, 11.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0003F, -0.0365F, 0.3386F, 0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r9 = body.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(32, 0).addBox(-5.501F, 20.9365F, 11.0185F, 11.0F, 0.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0003F, -0.0365F, 0.3386F, -0.1309F, 0.0F, 0.0F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(24, 39).addBox(-1.5F, -2.5F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(5.751F, 2.2082F, 0.0538F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 12).addBox(-3.5F, -2.5F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.751F, 2.2082F, 0.0538F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.bodyPart, this.leftArmPart, this.rightArmPart);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of();
    }
}