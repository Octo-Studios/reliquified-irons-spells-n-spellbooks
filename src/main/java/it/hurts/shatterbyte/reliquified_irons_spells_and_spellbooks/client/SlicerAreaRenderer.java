package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.client;

import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASDataComponents;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.SlicerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID, value = Dist.CLIENT)
public class SlicerAreaRenderer {
    private static final RenderType AREA_RENDER_TYPE = RenderType.energySwirl(SpellRenderingHelper.SOLID, 0F, 0F);

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES)
            return;

        var minecraft = Minecraft.getInstance();
        var player = minecraft.player;
        var level = minecraft.level;

        if (player == null || level == null)
            return;

        var stack = player.getMainHandItem();

        if (!(stack.getItem() instanceof SlicerItem))
            return;

        if (!stack.getOrDefault(RISASDataComponents.SLICER_ACTIVE.get(), false))
            return;

        var state = stack.get(RISASDataComponents.SLICER_STATE.get());

        if (state == null || !state.level().equals(level.dimension()))
            return;

        var radius = (float) Math.max(0.5D, state.radius());
        var segments = Math.max(24, (int) (5F * radius + 9F));
        var angleStep = (2F * Mth.PI) / (float) segments;
        var partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        var deltaTicks = level.getGameTime() + partialTick;
        var deltaUV = -deltaTicks % 10F;
        var uvMax = Mth.frac(deltaUV * 0.2F - (float) Mth.floor(deltaUV * 0.1F));
        var uvMin = -1F + uvMax;
        var center = new Vec3(state.centerX(), state.centerY(), state.centerZ());
        var heights = new float[6];

        for (var i = 0; i < 6; i++) {
            var degrees = i * 60F;
            var x = radius * Mth.cos(degrees * Mth.DEG_TO_RAD);
            var z = radius * Mth.sin(degrees * Mth.DEG_TO_RAD);
            var y = Utils.findRelativeGroundLevel(level, center.add(x, 2D, z), 8);
            heights[i] = (float) (y - state.centerY());

            if (level.collidesWithSuffocatingBlock(null, AABB.ofSize(new Vec3(center.x + x, y, center.z + z), 0.1D, 0.1D, 0.1D)))
                heights[i] = 0F;
        }

        var camera = event.getCamera().getPosition();
        var poseStack = event.getPoseStack();
        var bufferSource = minecraft.renderBuffers().bufferSource();
        var consumer = bufferSource.getBuffer(AREA_RENDER_TYPE);

        poseStack.pushPose();
        poseStack.translate(state.centerX() - camera.x, state.centerY() - camera.y, state.centerZ() - camera.z);

        var pose = poseStack.last();
        var poseMatrix = pose.pose();

        for (var i = 0; i < segments; i++) {
            var theta = angleStep * i;
            var thetaNext = angleStep * (i + 1);
            var x1 = radius * Mth.cos(theta);
            var z1 = radius * Mth.sin(theta);
            var x2 = radius * Mth.cos(thetaNext);
            var z2 = radius * Mth.sin(thetaNext);
            var j = ((int) (theta * Mth.RAD_TO_DEG) / 60) % 6;
            var f = ((theta * Mth.RAD_TO_DEG) % 60F) / 60F;
            var fNext = ((thetaNext * Mth.RAD_TO_DEG) % 60F) / 60F;
            var heightMin = heights[j];
            var heightMax = heights[(j + 1) % 6];
            var y1 = Mth.lerp(f, heightMin, heightMax);

            if (fNext < f) {
                heightMin = heightMax;
                heightMax = heights[(j + 2) % 6];
            }

            var y2 = Mth.lerp(fNext, heightMin, heightMax);

            consumer.addVertex(poseMatrix, x2, y2 - 0.6F, z2).setColor(0.6901961F, 0.1254902F, 0.20392157F, 1F).setUv(0F, uvMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0F, 1F, 0F);
            consumer.addVertex(poseMatrix, x2, y2 + 0.6F, z2).setColor(0F, 0F, 0F, 1F).setUv(0F, uvMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0F, 1F, 0F);
            consumer.addVertex(poseMatrix, x1, y1 + 0.6F, z1).setColor(0F, 0F, 0F, 1F).setUv(1F, uvMin).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0F, 1F, 0F);
            consumer.addVertex(poseMatrix, x1, y1 - 0.6F, z1).setColor(0.6901961F, 0.1254902F, 0.20392157F, 1F).setUv(1F, uvMax).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(0F, 1F, 0F);
        }

        poseStack.popPose();
        bufferSource.endBatch(AREA_RENDER_TYPE);
    }
}
