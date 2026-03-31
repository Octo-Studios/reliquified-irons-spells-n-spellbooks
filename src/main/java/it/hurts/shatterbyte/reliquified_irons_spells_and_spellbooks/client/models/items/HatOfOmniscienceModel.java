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

public class HatOfOmniscienceModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ReliquifiedIronsSpellsAndSpellbooks.MODID, "hat_of_omniscience"), "hat_of_omniscience");

    public ModelPart headPart;

    public HatOfOmniscienceModel(ModelPart root) {
        super(root);

        this.headPart = root.getChild("head");
    }

    public static LayerDefinition constructLayerDefinition() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 29.0F, 0.0F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(40, 36).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -6.0F, 0.0F, -0.174F, -0.0133F, 0.0883F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(32, 51).addBox(-1.4946F, -1.7919F, -0.232F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2571F, -13.8087F, 8.9087F, -1.4016F, 0.2935F, -0.0013F));

        PartDefinition head_r3 = head.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(44, 51).addBox(-1.0627F, -1.3787F, -3.2433F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2571F, -13.8087F, 8.9087F, -0.863F, 0.3026F, 0.0528F));

        PartDefinition head_r4 = head.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(56, 51).addBox(-2.1639F, -3.0579F, -3.4781F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.25F))
                .texOffs(20, 51).addBox(-2.1639F, -3.0579F, -3.4781F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6824F, -14.1298F, 5.6606F, -1.3183F, 0.3225F, 0.5645F));

        PartDefinition head_r5 = head.addOrReplaceChild("head_r5", CubeListBuilder.create().texOffs(0, 18).addBox(-8.0F, -1.0F, -8.0F, 8.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.15F, -7.0F, 0.0F, -0.1744F, -0.0201F, -0.1267F));

        PartDefinition head_r6 = head.addOrReplaceChild("head_r6", CubeListBuilder.create().texOffs(0, 51).addBox(-3.1391F, -2.0685F, -4.761F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.25F))
                .texOffs(48, 24).addBox(-3.1391F, -2.0685F, -4.761F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6824F, -14.0298F, 5.4606F, -0.7377F, 0.0843F, 0.2668F));

        PartDefinition head_r7 = head.addOrReplaceChild("head_r7", CubeListBuilder.create().texOffs(48, 12).addBox(-4.5095F, -0.0946F, -6.5795F, 7.0F, 5.0F, 7.0F, new CubeDeformation(0.25F))
                .texOffs(48, 0).addBox(-4.5095F, -0.0946F, -6.5795F, 7.0F, 5.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6824F, -14.0298F, 5.4606F, -0.3489F, -0.0266F, 0.1311F));

        PartDefinition head_r8 = head.addOrReplaceChild("head_r8", CubeListBuilder.create().texOffs(0, 36).addBox(-5.0F, -6.0F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.9F, 0.0F, -0.174F, -0.0133F, 0.0883F));

        PartDefinition head_r9 = head.addOrReplaceChild("head_r9", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -1.0F, -8.0F, 8.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.15F, -7.0F, 0.0F, -0.1744F, 0.0201F, 0.1267F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.headPart);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of();
    }
}