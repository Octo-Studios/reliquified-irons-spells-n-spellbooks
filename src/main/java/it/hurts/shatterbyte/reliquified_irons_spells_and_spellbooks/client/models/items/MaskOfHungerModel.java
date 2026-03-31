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

public class MaskOfHungerModel extends HumanoidModel<LivingEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ReliquifiedIronsSpellsAndSpellbooks.MODID, "mask_of_hunger"), "mask_of_hunger");

    public ModelPart headPart;

    public MaskOfHungerModel(ModelPart root) {
        super(root);

        this.headPart = root.getChild("head");
    }

    public static LayerDefinition constructLayerDefinition() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(new CubeDeformation(0.4F), 0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 26).addBox(-4.0F, -7.75F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F))
                .texOffs(36, 42).addBox(-4.0F, -11.75F, -1.0F, 8.0F, 3.0F, 2.0F, new CubeDeformation(0.6F))
                .texOffs(32, 26).addBox(-4.0F, -7.75F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.7F)), PartPose.offset(0.0F, 20.0F, 0.0F));

        PartDefinition head_r1 = head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(0, 13).addBox(-5.0F, -1.0F, -6.0F, 11.0F, 2.0F, 11.0F, new CubeDeformation(0.2F))
                .texOffs(0, 0).addBox(-5.0F, -1.0F, -6.0F, 11.0F, 2.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 0.25F, 0.6F, 0.1745F, 0.0F, 0.0F));

        PartDefinition head_r2 = head.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(18, 42).addBox(-3.5F, -1.0F, -1.5F, 6.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.7957F, -7.4178F, 0.0F, 0.0F, 0.0F, -1.9199F));

        PartDefinition head_r3 = head.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(0, 42).addBox(-2.5F, -1.0F, -1.5F, 6.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.7957F, -7.4178F, 0.0F, 0.0F, 0.0F, 1.9199F));

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