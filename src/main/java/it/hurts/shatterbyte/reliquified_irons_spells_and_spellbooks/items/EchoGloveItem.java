package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.EchoingStrikeEntity;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASItems;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.base.ISASWearableRelicItem;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.misc.RISASLootEntries;
import it.hurts.sskirillss.relics.api.relics.AbilityMetricTemplate;
import it.hurts.sskirillss.relics.api.relics.AbilityStatisticTemplate;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.VisibilityState;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourceTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.api.relics.data.AbilityData;
import it.hurts.sskirillss.relics.init.RelicsMobEffects;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public class EchoGloveItem extends ISASWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("echo_glove")
                                .rankModifier(1, "launch_echo")
                                .rankModifier(3, "stunning_echo")
                                .rankModifier(5, "chain_echo")
                                .stat(AbilityStatTemplate.builder("echo_chance")
                                        .initialValue(0.05D, 0.15D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("echo_damage")
                                        .initialValue(0.15D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("echo_radius")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("launch_height")
                                        .initialValue(0.25D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("fall_damage_bonus")
                                        .initialValue(0.25D, 0.75D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("stun_duration")
                                        .initialValue(0.5D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("echo_trigger").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("echo_triggers")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("launch_echo_fall_damage")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("launch_echo", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("stunning_echo_duration")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("stunning_echo", VisibilityState.OBFUSCATED)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .loot(LootTemplate.builder()
                        .entry(RISASLootEntries.ANY_STRUCTURE)
                        .build())
                .build();
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent event) {
            if (!(event.getEntity().level() instanceof ServerLevel level))
                return;

            var gameTime = level.getGameTime();
            var target = event.getEntity();
            var data = target.getPersistentData();

            if (!data.contains("risas_echo_glove_fall_expires_at"))
                return;

            if (data.getLong("risas_echo_glove_fall_expires_at") <= gameTime) {
                data.remove("risas_echo_glove_fall_expires_at");
                data.remove("risas_echo_glove_fall_bonus");
                data.remove("risas_echo_glove_owner");
                return;
            }

            event.setDistance(Math.max(event.getDistance(), 5F));
            event.setDamageMultiplier(Math.max(event.getDamageMultiplier(), 1F));
        }

        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (event.getNewDamage() <= 0F)
                return;

            if (!(event.getSource().getEntity() instanceof ServerPlayer player))
                return;

            if (!(player.level() instanceof ServerLevel level))
                return;

            if (!(event.getEntity() instanceof LivingEntity target) || target == player)
                return;

            var direct = event.getSource().getDirectEntity();
            var isEchoStrike = direct instanceof EchoingStrikeEntity strike && strike.getPersistentData().getBoolean("risas_echo_glove");

            if (!isEchoStrike && direct != player)
                return;

            ItemStack stack = ItemStack.EMPTY;
            AbilityData ability = null;
            var bestEchoDamage = -1D;

            for (var candidateStack : EntityUtils.findEquippedCurios(player, RISASItems.ECHO_GLOVE.value())) {
                if (!(candidateStack.getItem() instanceof EchoGloveItem candidateItem))
                    continue;

                var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("echo_glove");

                if (!candidateAbility.canPlayerUse(player))
                    continue;

                var candidateEchoDamage = Math.max(0D, candidateAbility.getStatData("echo_damage").getValue());

                if (candidateEchoDamage <= bestEchoDamage)
                    continue;

                bestEchoDamage = candidateEchoDamage;
                stack = candidateStack;
                ability = candidateAbility;
            }

            if (stack.isEmpty() || ability == null)
                return;

            if (isEchoStrike && target.isAlive() && target.onGround()) {
                if (ability.isRankModifierUnlocked("launch_echo")) {
                    var launchHeight = Math.max(0D, ability.getStatData("launch_height").getValue());

                    if (launchHeight > 0D) {
                        var current = target.getDeltaMovement();
                        var launchVelocity = Math.max(current.y, (0.45D + launchHeight * 0.25D) * 0.5D);

                        target.setDeltaMovement(current.x, launchVelocity, current.z);
                        target.hurtMarked = true;

                        var fallBonus = Math.max(0D, ability.getStatData("fall_damage_bonus").getValue());

                        if (fallBonus > 0D) {
                            var data = target.getPersistentData();

                            data.putLong("risas_echo_glove_fall_expires_at", level.getGameTime() + 200L);
                            data.putDouble("risas_echo_glove_fall_bonus", fallBonus);
                            data.putUUID("risas_echo_glove_owner", player.getUUID());
                        }
                    }
                }

                if (ability.isRankModifierUnlocked("stunning_echo")) {
                    var stunTicks = Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("stun_duration").getValue()) * 20D));

                    if (stunTicks > 0) {
                        target.addEffect(new MobEffectInstance(RelicsMobEffects.STUN, stunTicks, 0, false, true, true), player);
                        ability.getStatisticData().getMetricData("stunning_echo_duration").addValue(stunTicks / 20D);
                    }
                }

            }

            if (isEchoStrike && !ability.isRankModifierUnlocked("chain_echo"))
                return;

            var echoChance = Mth.clamp(ability.getStatData("echo_chance").getValue(), 0D, 1D);
            var echoDamageScale = Math.max(0D, ability.getStatData("echo_damage").getValue());

            if (echoChance <= 0D || echoDamageScale <= 0D || level.getRandom().nextDouble() >= echoChance)
                return;

            var echoDamage = (float) Math.max(0D, event.getNewDamage() * echoDamageScale);

            if (echoDamage <= 0F)
                return;

            var echoRadius = Math.max(0D, ability.getStatData("echo_radius").getValue());

            if (echoRadius <= 0D)
                return;

            var candidates = level.getEntitiesOfClass(
                    LivingEntity.class,
                    target.getBoundingBox().inflate(echoRadius),
                    entity -> entity != target && entity != player && entity.isAlive() && !DamageSources.isFriendlyFireBetween(player, entity)
            );

            if (candidates.isEmpty())
                return;

            var chainedTarget = candidates.get(level.getRandom().nextInt(candidates.size()));
            var echoStrike = new EchoingStrikeEntity(level, player, echoDamage, 2F);

            echoStrike.setPos(chainedTarget.getBoundingBox().getCenter().subtract(0D, echoStrike.getBbHeight() * 0.5D, 0D));
            echoStrike.setTracking(chainedTarget);
            echoStrike.getPersistentData().putBoolean("risas_echo_glove", true);

            level.addFreshEntity(echoStrike);
            ((EchoGloveItem) stack.getItem()).getRelicData(player, stack).getLevelingData().addExperience("echo_glove", "echo_trigger", 1D);
            ability.getStatisticData().getMetricData("echo_triggers").addValue(1D);
        }

        @SubscribeEvent
        public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity().level() instanceof ServerLevel level) || event.getAmount() <= 0F)
                return;

            if (!event.getSource().is(DamageTypes.FALL))
                return;

            var gameTime = level.getGameTime();
            var target = event.getEntity();
            var data = target.getPersistentData();

            if (!data.contains("risas_echo_glove_fall_expires_at") || data.getLong("risas_echo_glove_fall_expires_at") <= gameTime) {
                data.remove("risas_echo_glove_fall_expires_at");
                data.remove("risas_echo_glove_fall_bonus");
                data.remove("risas_echo_glove_owner");
                return;
            }

            var bonus = Math.max(0D, data.getDouble("risas_echo_glove_fall_bonus"));

            if (bonus <= 0D) {
                data.remove("risas_echo_glove_fall_expires_at");
                data.remove("risas_echo_glove_fall_bonus");
                data.remove("risas_echo_glove_owner");
                return;
            }

            event.setAmount((float) Math.max(0D, event.getAmount() * (1D + bonus)));

            if (data.contains("risas_echo_glove_owner")) {
                var owner = level.getServer() != null ? level.getServer().getPlayerList().getPlayer(data.getUUID("risas_echo_glove_owner")) : null;

                if (owner != null) {
                    for (var stack : EntityUtils.findEquippedCurios(owner, RISASItems.ECHO_GLOVE.value())) {
                        if (!(stack.getItem() instanceof EchoGloveItem item))
                            continue;

                        var ability = item.getRelicData(owner, stack).getAbilitiesData().getAbilityData("echo_glove");

                        if (!ability.canPlayerUse(owner) || !ability.isRankModifierUnlocked("launch_echo"))
                            continue;

                        ability.getStatisticData().getMetricData("launch_echo_fall_damage").addValue(event.getAmount());
                        break;
                    }
                }
            }

            data.remove("risas_echo_glove_fall_expires_at");
            data.remove("risas_echo_glove_fall_bonus");
            data.remove("risas_echo_glove_owner");
        }
    }
}
