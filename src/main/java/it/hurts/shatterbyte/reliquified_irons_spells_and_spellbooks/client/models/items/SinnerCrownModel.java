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

public class SinnerCrownModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ReliquifiedIronsSpellsAndSpellbooks.MODID, "sinner_crown"), "sinner_crown");

    public ModelPart headPart;

    public SinnerCrownModel(ModelPart root) {
        super(root);

        this.headPart = root.getChild("head");
    }

    public static LayerDefinition constructLayerDefinition() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -8.0F, 3.5F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 4).addBox(-2.5F, -8.0F, -4.5F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 20).addBox(-3.5F, -9.0F, -5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.1F))
                .texOffs(28, 30).addBox(2.5F, -9.0F, -5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.1F))
                .texOffs(14, 28).addBox(-1.0F, -13.0F, -4.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 20).addBox(-1.0F, -13.0F, 3.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 4).addBox(3.5F, -13.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 28).addBox(-4.5F, -13.0F, -1.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 0).addBox(-4.5F, -11.0F, 2.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 8).addBox(2.5F, -11.0F, 2.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(8, 16).addBox(2.5F, -11.0F, -4.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(-4.5F, -11.0F, -4.5F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 26).addBox(-1.0F, -9.0F, -5.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.1F))
                .texOffs(35, 20).addBox(0.1F, -10.0F, -5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 29.0F, 0.0F));

        PartDefinition cube_r1 = head.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 12).addBox(-2.75F, -1.5F, -4.5F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 8).addBox(-2.75F, -1.5F, 3.5F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.5F, -0.25F, 0.0F, 1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
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