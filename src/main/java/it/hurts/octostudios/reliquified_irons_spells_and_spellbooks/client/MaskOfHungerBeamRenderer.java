package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.client;

import com.mojang.math.Axis;
import io.redspace.ironsspellbooks.render.SpellRenderingHelper;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;

@EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID, value = Dist.CLIENT)
public class MaskOfHungerBeamRenderer {
    private static final ArrayList<BeamRenderData> BEAMS = new ArrayList<>();
    private static final RenderType BEAM_RENDER_TYPE = RenderType.entityTranslucent(SpellRenderingHelper.BEACON, true);
    private static final RenderType GLOW_RENDER_TYPE = RenderType.entityTranslucent(SpellRenderingHelper.TWISTING_GLOW);

    public static void add(int fromEntityId, int toEntityId) {
        var minecraft = Minecraft.getInstance();
        var level = minecraft.level;

        if (level == null)
            return;

        BEAMS.add(new BeamRenderData(fromEntityId, toEntityId, level.getGameTime() + 20));
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES || BEAMS.isEmpty())
            return;

        var minecraft = Minecraft.getInstance();
        var level = minecraft.level;

        if (level == null)
            return;

        var gameTime = level.getGameTime();

        BEAMS.removeIf(beam -> beam.expiresAtTick <= gameTime || level.getEntity(beam.fromEntityId) == null || level.getEntity(beam.toEntityId) == null);

        if (BEAMS.isEmpty())
            return;

        var camera = event.getCamera().getPosition();
        var poseStack = event.getPoseStack();
        var buffer = minecraft.renderBuffers().bufferSource();
        var partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        var textureProgressEnd = -(gameTime + partialTick) % 10F * 0.2F;
        var textureProgressStart = -1F + Mth.frac(textureProgressEnd);

        for (var beam : BEAMS) {
            if (!(level.getEntity(beam.fromEntityId) instanceof LivingEntity fromEntity) || !(level.getEntity(beam.toEntityId) instanceof LivingEntity toEntity)
                    || fromEntity.isDeadOrDying() || toEntity.isDeadOrDying())
                continue;

            var from = fromEntity.getPosition(partialTick).add(0D, fromEntity.getBbHeight() / 2F, 0D);
            var to = toEntity.getPosition(partialTick).add(0D, toEntity.getBbHeight() / 2F, 0D);
            var direction = to.subtract(from);
            var distance = (float) direction.length();

            if (distance <= 0.001F)
                continue;

            var normalized = direction.normalize();
            var directionX = (float) normalized.x;
            var directionY = (float) normalized.y;
            var directionZ = (float) normalized.z;
            var yaw = (float) Mth.atan2(directionZ, directionX) - 1.5707F;
            var pitch = (float) Mth.atan2(directionY, Mth.sqrt(directionX * directionX + directionZ * directionZ));
            var previous = Vec3.ZERO;

            poseStack.pushPose();
            poseStack.translate(from.x - camera.x, from.y - camera.y, from.z - camera.z);
            poseStack.mulPose(Axis.YP.rotation(-yaw));
            poseStack.mulPose(Axis.XP.rotation(-pitch));

            var pose = poseStack.last();

            for (var progress = 1F; progress <= distance; progress += 0.5F) {
                var point = new Vec3(0D, 0D, Math.min(progress, distance));
                var beacon = buffer.getBuffer(BEAM_RENDER_TYPE);

                SpellRenderingHelper.drawHull(previous, point, 0.12F, 0.12F, pose, beacon, 178, 0, 0, 255, textureProgressStart, textureProgressEnd);

                var glow = buffer.getBuffer(GLOW_RENDER_TYPE);
                SpellRenderingHelper.drawQuad(previous, point, 0.48F, 0F, pose, glow, 178, 0, 0, 255, textureProgressStart, textureProgressEnd);
                SpellRenderingHelper.drawQuad(previous, point, 0F, 0.48F, pose, glow, 178, 0, 0, 255, textureProgressStart, textureProgressEnd);

                previous = point;
            }

            poseStack.popPose();
        }

        buffer.endBatch(BEAM_RENDER_TYPE);
        buffer.endBatch(GLOW_RENDER_TYPE);
    }

    private record BeamRenderData(int fromEntityId, int toEntityId, long expiresAtTick) {
    }
}
