package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASItems;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASDataComponents;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.base.ISASRelicItem;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.base.ISASWearableRelicItem;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.misc.RISASLootEntries;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.network.payload.MaskOfHungerBeamPayload;
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
import it.hurts.sskirillss.relics.items.relics.base.data.research.ResearchTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class MaskOfHungerItem extends ISASWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("hunger_mask")
                                .rankModifier(1, "gluttonous_surge")
                                .rankModifier(3, "paralyzing_beam")
                                .rankModifier(5, "predator_reset")
                                .stat(AbilityStatTemplate.builder("health_threshold")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("siphon_damage")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("siphon_targets")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("cooldown")
                                        .initialValue(60D, 45D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.0159D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("food_per_target")
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> Math.max(0, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("saturation_per_target")
                                        .initialValue(0.25D, 0.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("paralysis_duration")
                                        .initialValue(1.5D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("target_drained").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("ability_triggers")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("targets_drained")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("healing_restored")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("food_restored")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("gluttonous_surge", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("paralysis_duration_total")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("paralyzing_beam", VisibilityState.OBFUSCATED)
                                                .build())
                                        .build())
                                .research(ResearchTemplate.builder()
                                        .star(0, 5, 25).star(1, 18, 19).star(2, 18, 13).star(3, 12, 13).star(4, 7, 10).star(5, 3, 14).star(6, 11, 18)
                                        .link(5, 0).link(1, 2).link(3, 4).link(4, 5).link(3, 2).link(0, 6).link(6, 1)
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
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getNewDamage() <= 0F || !player.isAlive())
                return;

            var maxHealth = Math.max(1F, player.getMaxHealth());
            var healthAfter = Math.max(0F, player.getHealth());
            var healthBefore = Math.min(maxHealth, healthAfter + event.getNewDamage());
            var gameTime = player.level().getGameTime();
            MaskOfHungerItem item = null;
            ItemStack stack = ItemStack.EMPTY;
            AbilityData ability = null;
            var bestDamage = -1D;
            var bestTargets = -1;
            var bestCooldown = Double.MAX_VALUE;

            for (var candidateStack : EntityUtils.findEquippedCurios(player, RISASItems.MASK_OF_HUNGER.value())) {
                if (!(candidateStack.getItem() instanceof MaskOfHungerItem candidateItem))
                    continue;

                if (candidateStack.getOrDefault(RISASDataComponents.MASK_OF_HUNGER_COOLDOWN_UNTIL.get(), 0L) > gameTime)
                    continue;

                var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("hunger_mask");

                if (!candidateAbility.canPlayerUse(player))
                    continue;

                var threshold = Mth.clamp(candidateAbility.getStatData("health_threshold").getValue(), 0D, 1D);
                var crossedThreshold = (healthBefore / maxHealth) > threshold && (healthAfter / maxHealth) <= threshold;

                if (!crossedThreshold)
                    continue;

                var damage = Math.max(0D, candidateAbility.getStatData("siphon_damage").getValue());
                var targets = Math.max(1, (int) Math.round(Math.max(0D, candidateAbility.getStatData("siphon_targets").getValue())));
                var cooldown = Math.max(0D, candidateAbility.getStatData("cooldown").getValue());

                if (damage <= 0D || targets <= 0)
                    continue;

                if (damage < bestDamage || damage == bestDamage && targets < bestTargets || damage == bestDamage && targets == bestTargets && cooldown >= bestCooldown)
                    continue;

                item = candidateItem;
                stack = candidateStack;
                ability = candidateAbility;
                bestDamage = damage;
                bestTargets = targets;
                bestCooldown = cooldown;
            }

            if (item == null || stack.isEmpty() || ability == null)
                return;

            var relicData = item.getRelicData(player, stack);
            var healthBeforeSiphon = player.getHealth();
            var targetsDrained = 0;
            var totalDamage = 0D;
            var killedAny = false;
            var paralysisTicks = ability.isRankModifierUnlocked("paralyzing_beam") ? Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("paralysis_duration").getValue()) * 20D)) : 0;
            var paralyzedTargets = 0;

            var targets = player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox().inflate(128D),
                    target -> target != player && target.isAlive() && !DamageSources.isFriendlyFireBetween(player, target)
            );

            targets.sort((first, second) -> Double.compare(first.distanceToSqr(player), second.distanceToSqr(player)));

            if (targets.size() > bestTargets)
                targets = targets.subList(0, bestTargets);

            for (var target : targets) {
                var targetHealthBefore = target.getHealth();
                var targetEffectiveBefore = Math.max(0F, target.getHealth() + target.getAbsorptionAmount());
                var damageSource = SpellRegistry.RAY_OF_SIPHONING_SPELL.get().getDamageSource(player, player);

                if (damageSource instanceof SpellDamageSource spellDamageSource && spellDamageSource.getLifestealPercent() > 0F)
                    spellDamageSource.setLifestealPercent(0F);

                if (!DamageSources.applyDamage(target, (float) bestDamage, damageSource))
                    continue;

                var targetEffectiveAfter = target.isAlive() ? Math.max(0F, target.getHealth() + target.getAbsorptionAmount()) : 0F;
                var dealtDamage = Math.max(0D, targetEffectiveBefore - targetEffectiveAfter);

                if (dealtDamage <= 0D)
                    dealtDamage = bestDamage;

                totalDamage += dealtDamage;
                targetsDrained++;
                killedAny = killedAny || targetHealthBefore > 0F && !target.isAlive();

                if (paralysisTicks > 0 && target.isAlive()) {
                    target.addEffect(new MobEffectInstance(RelicsMobEffects.PARALYSIS, paralysisTicks, 0, false, true, true));
                    paralyzedTargets++;
                }

                PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                        player,
                        new MaskOfHungerBeamPayload(
                                player.getId(),
                                target.getId()
                        )
                );
            }

            if (targetsDrained <= 0)
                return;

            ability.getStatisticData().getMetricData("ability_triggers").addValue(1D);
            ability.getStatisticData().getMetricData("targets_drained").addValue(targetsDrained);
            ability.getStatisticData().getMetricData("damage_dealt").addValue(totalDamage);
            relicData.getLevelingData().addExperience("hunger_mask", "target_drained", targetsDrained);

            if (paralyzedTargets > 0 && paralysisTicks > 0)
                ability.getStatisticData().getMetricData("paralysis_duration_total").addValue((paralysisTicks / 20D) * paralyzedTargets);

            if (ability.isRankModifierUnlocked("gluttonous_surge")) {
                var foodToAdd = Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("food_per_target").getValue()) * targetsDrained));
                var saturationToAdd = Math.max(0F, (float) (Math.max(0D, ability.getStatData("saturation_per_target").getValue()) * targetsDrained));
                var foodData = player.getFoodData();
                var foodBefore = foodData.getFoodLevel();

                if (foodToAdd > 0)
                    foodData.setFoodLevel(Math.min(20, foodData.getFoodLevel() + foodToAdd));

                if (saturationToAdd > 0F)
                    foodData.setSaturation((float) Math.min(foodData.getFoodLevel(), foodData.getSaturationLevel() + saturationToAdd));

                var restoredFood = Math.max(0, foodData.getFoodLevel() - foodBefore);

                if (restoredFood > 0)
                    ability.getStatisticData().getMetricData("food_restored").addValue(restoredFood);
            }

            player.heal((float) Math.max(0D, totalDamage));
            var restoredHealth = Math.max(0D, player.getHealth() - healthBeforeSiphon);

            if (restoredHealth > 0D)
                ability.getStatisticData().getMetricData("healing_restored").addValue(restoredHealth);

            if (ability.isRankModifierUnlocked("predator_reset") && killedAny) {
                stack.remove(RISASDataComponents.MASK_OF_HUNGER_COOLDOWN_UNTIL.get());
            } else {
                stack.set(RISASDataComponents.MASK_OF_HUNGER_COOLDOWN_UNTIL.get(), gameTime + Math.max(1L, Math.round(bestCooldown * 20D)));
            }


        }
    }
}
