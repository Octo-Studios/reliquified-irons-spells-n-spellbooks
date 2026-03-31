package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.client.renderer.items;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.client.models.items.MaskOfHungerModel;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.client.models.items.SinnerCrownModel;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.MaskOfHungerItem;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.SinnerCrownItem;
import it.hurts.sskirillss.relics.client.renderer.items.base.IRelicRenderer;
import it.hurts.sskirillss.relics.utils.FlawlessUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class MaskOfHungerRenderer implements IRelicRenderer {
    private final MaskOfHungerModel model;

    public MaskOfHungerRenderer() {
        this.model = new MaskOfHungerModel(Minecraft.getInstance().getEntityModels().bakeLayer(MaskOfHungerModel.LAYER));
    }

    @Override
    public <E extends LivingEntity, EM extends EntityModel<E>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack, RenderLayerParent<E, EM> parent, MultiBufferSource bufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        var entity = slotContext.entity();
        var relic = (MaskOfHungerItem) stack.getItem();

        poseStack.pushPose();

        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        ICurioRenderer.followBodyRotations(entity, this.model);

        var time = entity.tickCount + (Minecraft.getInstance().isPaused() ? 0 : partialTicks);
        float flicker = 0.75F + 0.25F * Mth.sin(time * 0.15F);

        this.model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutout(ResourceLocation.fromNamespaceAndPath(ReliquifiedIronsSpellsAndSpellbooks.MODID, "textures/item/model/mask_of_hunger" + (relic.getRelicData(entity, stack).isFlawless() ? "_flawless" : "") + ".png"))), relic.getRelicData(entity, stack).isFlawless() ? LightTexture.FULL_BRIGHT : light, OverlayTexture.NO_OVERLAY);
        this.model.renderToBuffer(poseStack, bufferSource.getBuffer(RenderType.entityCutout(FlawlessUtils.getTexture(entity, stack, ResourceLocation.fromNamespaceAndPath(ReliquifiedIronsSpellsAndSpellbooks.MODID, "textures/item/model/mask_of_hunger_blood.png")))), relic.getRelicData(entity, stack).isFlawless() ? LightTexture.FULL_BRIGHT : LightTexture.pack((int) (15 * flicker), (int) (15 * flicker)), OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}