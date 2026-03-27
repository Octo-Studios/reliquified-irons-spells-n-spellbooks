package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASDataComponents;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.base.ISASRelicItem;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.misc.RISASLootEntries;
import it.hurts.sskirillss.relics.api.relics.AbilityMetricTemplate;
import it.hurts.sskirillss.relics.api.relics.AbilityStatisticTemplate;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourceTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.MathUtils;
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class MirrorOfTransgressionItem extends ISASRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("mirror_of_transgression")
                                .rankModifier(1, "homeward_focus")
                                .rankModifier(3, "steady_surface")
                                .rankModifier(5, "worldline_breach")
                                .stat(AbilityStatTemplate.builder("cast_time")
                                        .initialValue(10D, 7.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.019D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("focus_cast_speed")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("channel_damage_reduction")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("home_teleport").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("home_teleports")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("home_teleport_distance")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .build())
                                .build())
                        .build())
                .loot(LootTemplate.builder()
                        .entry(RISASLootEntries.ANY_STRUCTURE)
                        .build())
                .build();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);

        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.pass(stack);

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("mirror_of_transgression");

        if (!ability.canPlayerUse(player))
            return InteractionResultHolder.fail(stack);

        if (!level.isClientSide())
            stack.set(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get(), level.getGameTime());

        player.startUsingItem(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (!level.isClientSide())
            stack.remove(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get());
    }

    @Override
    public void onUseTick(Level level, LivingEntity entityLiving, ItemStack stack, int remainingUseDuration) {
        if (!(entityLiving instanceof ServerPlayer player) || level.isClientSide())
            return;

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("mirror_of_transgression");

        if (!ability.canPlayerUse(player)) {
            stack.remove(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get());
            player.stopUsingItem();
            return;
        }

        var startedAt = stack.getOrDefault(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get(), level.getGameTime());
        var targetPosition = player.getRespawnPosition();
        var targetDimension = player.getRespawnDimension();
        var adjustedCastTime = Math.max(1L, Math.round(Math.max(0D, ability.getStatData("cast_time").getValue()) * 20D));
        var server = player.getServer();

        if (server == null) {
            stack.remove(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get());
            player.stopUsingItem();
            return;
        }

        if (ability.isRankModifierUnlocked("homeward_focus") && targetDimension.equals(player.level().dimension())) {
            var targetLevel = server.getLevel(targetDimension);

            if (targetLevel == null) {
                stack.remove(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get());
                player.stopUsingItem();
                return;
            }

            var home = targetPosition != null ? targetPosition : targetLevel.getSharedSpawnPos();
            var toHome = net.minecraft.world.phys.Vec3.atCenterOf(home).subtract(player.getEyePosition());

            if (toHome.lengthSqr() > 1.0E-4D) {
                var alignment = player.getLookAngle().normalize().dot(toHome.normalize());

                if (alignment >= 0.8D) {
                    var speed = Mth.clamp(ability.getStatData("focus_cast_speed").getValue(), 0D, 0.95D);

                    adjustedCastTime = Math.max(1L, Math.round(adjustedCastTime * (1D - speed)));
                }
            }
        }

        if (level.getGameTime() - startedAt < adjustedCastTime)
            return;

        if (!ability.isRankModifierUnlocked("worldline_breach") && !targetDimension.equals(player.level().dimension())) {
            stack.remove(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get());
            player.stopUsingItem();
            return;
        }

        if (targetDimension.equals(player.level().dimension()) || ability.isRankModifierUnlocked("worldline_breach")) {
            var targetLevel = server.getLevel(targetDimension);

            if (targetLevel == null) {
                stack.remove(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get());
                player.stopUsingItem();
                return;
            }

            var home = targetPosition != null ? targetPosition : targetLevel.getSharedSpawnPos();
            var positionBefore = player.position();
            var dimensionBefore = player.level().dimension();

            SpellRegistry.RECALL_SPELL.get().onCast(level, 1, player, CastSource.NONE, MagicData.getPlayerMagicData(player));

            if (!positionBefore.equals(player.position()) || !dimensionBefore.equals(player.level().dimension())) {
                var sourceLevel = server.getLevel(dimensionBefore);
                var destinationLevel = player.serverLevel();
                var positionAfter = player.position();

                if (sourceLevel != null) {
                    sourceLevel.playSound(null, positionBefore.x, positionBefore.y, positionBefore.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                    sourceLevel.sendParticles(ParticleHelper.UNSTABLE_ENDER, positionBefore.x, positionBefore.y + 1D, positionBefore.z, 40, 0.45D, 0.65D, 0.45D, 0.01D);
                }

                destinationLevel.playSound(null, positionAfter.x, positionAfter.y, positionAfter.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                destinationLevel.sendParticles(ParticleHelper.UNSTABLE_ENDER, positionAfter.x, positionAfter.y + 1D, positionAfter.z, 40, 0.45D, 0.65D, 0.45D, 0.01D);

                this.getRelicData(player, stack).getLevelingData().addExperience("mirror_of_transgression", "home_teleport", 1D);
                ability.getStatisticData().getMetricData("home_teleports").addValue(1D);
                ability.getStatisticData().getMetricData("home_teleport_distance").addValue(Math.max(0D, dimensionBefore.equals(player.level().dimension())
                        ? positionBefore.distanceTo(player.position())
                        : positionBefore.distanceTo(Vec3.atCenterOf(home))));
            }

            stack.remove(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get());
            player.stopUsingItem();
            return;
        }
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getNewDamage() <= 0F)
                return;

            if (!player.isUsingItem())
                return;

            var stack = player.getUseItem();

            if (!(stack.getItem() instanceof MirrorOfTransgressionItem item))
                return;

            var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("mirror_of_transgression");

            if (!ability.canPlayerUse(player))
                return;

            if (ability.isRankModifierUnlocked("steady_surface")) {
                var reduction = Mth.clamp(ability.getStatData("channel_damage_reduction").getValue(), 0D, 1D);

                if (reduction > 0D)
                    event.setNewDamage((float) Math.max(0D, event.getNewDamage() * (1D - reduction)));
            }

            if (event.getNewDamage() > 0F)
                stack.set(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get(), player.level().getGameTime());
        }
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onComputeFovModifier(ComputeFovModifierEvent event) {
            var player = event.getPlayer();

            if (!player.isUsingItem())
                return;

            var stack = player.getUseItem();

            if (!(stack.getItem() instanceof MirrorOfTransgressionItem item))
                return;

            var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("mirror_of_transgression");

            if (!ability.canPlayerUse(player))
                return;

            var adjustedCastTime = Math.max(1L, Math.round(Math.max(0D, ability.getStatData("cast_time").getValue()) * 20D));

            if (ability.isRankModifierUnlocked("homeward_focus")) {
                var home = player.level().getSharedSpawnPos();
                var toHome = Vec3.atCenterOf(home).subtract(player.getEyePosition());

                if (toHome.lengthSqr() > 1.0E-4D) {
                    var alignment = player.getLookAngle().normalize().dot(toHome.normalize());

                    if (alignment >= 0.8D) {
                        var speed = Mth.clamp(ability.getStatData("focus_cast_speed").getValue(), 0D, 0.95D);
                        adjustedCastTime = Math.max(1L, Math.round(adjustedCastTime * (1D - speed)));
                    }
                }
            }

            var elapsedTicks = stack.has(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get())
                    ? Math.max(0L, player.level().getGameTime() - stack.getOrDefault(RISASDataComponents.MIRROR_OF_TRANSGRESSION_USE_STARTED_AT.get(), player.level().getGameTime()))
                    : Math.max(0L, item.getUseDuration(stack, player) - player.getUseItemRemainingTicks());
            var progress = Mth.clamp((float) elapsedTicks / (float) adjustedCastTime, 0F, 1F);
            var zoomFactor = Mth.lerp(progress, 1F, 0.6F);

            event.setNewFovModifier(event.getNewFovModifier() * zoomFactor);
        }
    }
}
