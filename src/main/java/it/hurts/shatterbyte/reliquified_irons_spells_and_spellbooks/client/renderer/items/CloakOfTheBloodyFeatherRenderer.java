package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.client.renderer.items;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.client.models.items.CloakOfTheBloodyFeatherModel;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.CloakOfTheBloodyFeatherItem;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.HatOfOmniscienceItem;
import it.hurts.sskirillss.relics.client.renderer.items.base.IRelicRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CloakOfTheBloodyFeatherRenderer implements IRelicRenderer {
    private final CloakOfTheBloodyFeatherModel model;

    public CloakOfTheBloodyFeatherRenderer() {
        this.model = new CloakOfTheBloodyFeatherModel(Minecraft.getInstance().getEntityModels().bakeLayer(CloakOfTheBloodyFeatherModel.LAYER));
    }

    @Override
    public <E extends LivingEntity, EM extends EntityModel<E>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<E, EM> parent, MultiBufferSource bufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        var entity = slotContext.entity();
        var relic = (CloakOfTheBloodyFeatherItem) stack.getItem();

        poseStack.pushPose();

        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        ICurioRenderer.followBodyRotations(entity, this.model);

        this.model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.fromNamespaceAndPath(ReliquifiedIronsSpellsAndSpellbooks.MODID, "textures/item/model/cloak_of_bloody_feather" + (relic.getRelicData(entity, stack).isFlawless() ? "_flawless" : "") + ".png"))), relic.getRelicData(entity, stack).isFlawless() ? LightTexture.FULL_BRIGHT : light, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}