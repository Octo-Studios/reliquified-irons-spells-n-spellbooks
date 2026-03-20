package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASItems;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASDataComponents;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import top.theillusivec4.curios.api.SlotContext;

public class CardiacTrapItem extends ISASRelic {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("cardiac_trap")
                                .initialMaxLevel(10)
                                .rankModifier(1, "heart_overdrive")
                                .rankModifier(3, "paralyzing_pulse")
                                .rankModifier(5, "cooldown_gamble")
                                .stat(AbilityStatTemplate.builder("heartstop_duration")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("cooldown")
                                        .initialValue(60D, 35D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.0204D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("regen_boost")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.2D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("paralysis_radius")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("paralysis_duration")
                                        .initialValue(1D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("cooldown_skip_chance")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("prevented_damage").build())
                                        .source(ExperienceSourceTemplate.builder("paralyzed_target")
                                                .rankModifierVisibilityState("paralyzing_pulse", VisibilityState.OBFUSCATED)
                                                .build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("ability_triggers")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("heartstop_duration_total")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("heart_overdrive_healing")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("heart_overdrive", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("paralyzing_pulse_duration")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("paralyzing_pulse", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("paralyzing_pulse_targets")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("paralyzing_pulse", VisibilityState.OBFUSCATED)
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
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer player) || player.level().isClientSide())
            return;

        var pendingAt = stack.getOrDefault(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_AT.get(), 0L);

        if (pendingAt <= 0L)
            return;

        var gameTime = player.level().getGameTime();

        if (pendingAt > gameTime)
            return;

        stack.remove(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_AT.get());

        var durationTicks = Math.max(0, stack.getOrDefault(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_DURATION.get(), 0));
        var pendingDamage = Math.max(0D, stack.getOrDefault(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_DAMAGE.get(), 0D));

        stack.remove(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_DURATION.get());
        stack.remove(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_DAMAGE.get());

        if (durationTicks <= 0 || !player.isAlive()) {
            stack.remove(RISASDataComponents.cardiac_trap_HEARTSTOP_UNTIL.get());
            return;
        }

        if (pendingDamage > 0D)
            MagicData.getPlayerMagicData(player).getSyncedData().addHeartstopDamage((float) pendingDamage);

        player.addEffect(new MobEffectInstance(MobEffectRegistry.HEARTSTOP, durationTicks, 0, false, true, true), player);
        stack.set(RISASDataComponents.cardiac_trap_HEARTSTOP_UNTIL.get(), gameTime + durationTicks);
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getNewDamage() <= 0F)
                return;

            if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY))
                return;

            var gameTime = player.level().getGameTime();
            var isHeartstopDamage = event.getSource().is(ISSDamageTypes.HEARTSTOP);

            for (var candidateStack : EntityUtils.findEquippedCurios(player, RISASItems.CARDIAC_TRAP.value())) {
                if (!isHeartstopDamage && candidateStack.getOrDefault(RISASDataComponents.cardiac_trap_HEARTSTOP_UNTIL.get(), 0L) > gameTime)
                    return;

                if (candidateStack.getOrDefault(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_AT.get(), 0L) > 0L)
                    return;
            }

            if (Math.max(0F, event.getNewDamage() - player.getAbsorptionAmount()) < player.getHealth())
                return;

            CardiacTrapItem item = null;
            ItemStack stack = ItemStack.EMPTY;
            AbilityData ability = null;
            var bestDuration = -1D;
            var bestCooldown = Double.MAX_VALUE;

            for (var candidateStack : EntityUtils.findEquippedCurios(player, RISASItems.CARDIAC_TRAP.value())) {
                if (!(candidateStack.getItem() instanceof CardiacTrapItem candidateItem))
                    continue;

                if (candidateStack.getOrDefault(RISASDataComponents.cardiac_trap_COOLDOWN_UNTIL.get(), 0L) > gameTime)
                    continue;

                var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("cardiac_trap");

                if (!candidateAbility.canPlayerUse(player))
                    continue;

                var duration = Math.max(0D, candidateAbility.getStatData("heartstop_duration").getValue());
                var cooldown = Math.max(0D, candidateAbility.getStatData("cooldown").getValue());

                if (duration < bestDuration || duration == bestDuration && cooldown >= bestCooldown)
                    continue;

                bestDuration = duration;
                bestCooldown = cooldown;
                item = candidateItem;
                stack = candidateStack;
                ability = candidateAbility;
            }

            if (item == null || stack.isEmpty() || ability == null)
                return;

            var relicData = item.getRelicData(player, stack);
            var incomingDamage = Math.max(0F, event.getNewDamage());
            var appliedDamage = Math.max(0F, player.getHealth() + player.getAbsorptionAmount() - 1F);
            var preventedDamage = Math.max(0D, incomingDamage - appliedDamage);

            event.setNewDamage(appliedDamage);

            var durationTicks = Math.max(0, (int) Math.round(bestDuration * 20D));
            ability.getStatisticData().getMetricData("ability_triggers").addValue(1D);
            ability.getStatisticData().getMetricData("heartstop_duration_total").addValue(durationTicks / 20D);

            if (preventedDamage > 0D)
                relicData.getLevelingData().addExperience("cardiac_trap", "prevented_damage", preventedDamage);

            if (durationTicks > 0) {
                stack.set(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_AT.get(), gameTime + 1L);
                stack.set(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_DURATION.get(), durationTicks);
                stack.set(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_DAMAGE.get(), Math.max(0D, incomingDamage * 0.5D));
            } else {
                stack.remove(RISASDataComponents.cardiac_trap_HEARTSTOP_UNTIL.get());
                stack.remove(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_AT.get());
                stack.remove(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_DURATION.get());
                stack.remove(RISASDataComponents.cardiac_trap_PENDING_HEARTSTOP_DAMAGE.get());
            }

            if (ability.isRankModifierUnlocked("paralyzing_pulse")) {
                var radius = Math.max(0D, ability.getStatData("paralysis_radius").getValue());
                var paralysisTicks = Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("paralysis_duration").getValue()) * 20D));
                var paralyzedTargets = 0;

                if (radius > 0D && paralysisTicks > 0) {
                    for (var target : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius), target -> target != player && target.isAlive() && !DamageSources.isFriendlyFireBetween(player, target))) {
                        target.addEffect(new MobEffectInstance(RelicsMobEffects.PARALYSIS, paralysisTicks, 0, false, true, true), player);
                        paralyzedTargets++;
                    }
                }

                if (paralyzedTargets > 0) {
                    ability.getStatisticData().getMetricData("paralyzing_pulse_targets").addValue(paralyzedTargets);
                    ability.getStatisticData().getMetricData("paralyzing_pulse_duration").addValue((paralysisTicks / 20D) * paralyzedTargets);
                    relicData.getLevelingData().addExperience("cardiac_trap", "paralyzed_target", paralyzedTargets);
                }

            }

            var skippedCooldown = false;

            if (ability.isRankModifierUnlocked("cooldown_gamble")) {
                var chance = Mth.clamp(ability.getStatData("cooldown_skip_chance").getValue(), 0D, 1D);

                if (chance > 0D && player.getRandom().nextDouble() < chance)
                    skippedCooldown = true;
            }

            if (skippedCooldown) {
                stack.remove(RISASDataComponents.cardiac_trap_COOLDOWN_UNTIL.get());
            } else {
                stack.set(RISASDataComponents.cardiac_trap_COOLDOWN_UNTIL.get(), gameTime + Math.max(1L, Math.round(bestCooldown * 20D)));
            }

        }

        @SubscribeEvent
        public static void onLivingHeal(LivingHealEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getAmount() <= 0F || !player.hasEffect(MobEffectRegistry.HEARTSTOP))
                return;

            var gameTime = player.level().getGameTime();
            var bestBoost = 0D;
            CardiacTrapItem bestItem = null;
            ItemStack bestStack = ItemStack.EMPTY;
            AbilityData bestAbility = null;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.CARDIAC_TRAP.value())) {
                if (!(stack.getItem() instanceof CardiacTrapItem item))
                    continue;

                if (stack.getOrDefault(RISASDataComponents.cardiac_trap_HEARTSTOP_UNTIL.get(), 0L) <= gameTime)
                    continue;

                var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("cardiac_trap");

                if (!ability.canPlayerUse(player) || !ability.isRankModifierUnlocked("heart_overdrive"))
                    continue;

                var boost = Math.max(0D, ability.getStatData("regen_boost").getValue());

                if (boost > bestBoost) {
                    bestBoost = boost;
                    bestItem = item;
                    bestStack = stack;
                    bestAbility = ability;
                }
            }

            if (bestBoost > 0D) {
                var boostedAmount = Math.max(0D, event.getAmount() * (1D + bestBoost));
                var bonusHealing = Math.max(0D, boostedAmount - event.getAmount());

                event.setAmount((float) boostedAmount);

                if (bestAbility != null && bonusHealing > 0D)
                    bestAbility.getStatisticData().getMetricData("heart_overdrive_healing").addValue(bonusHealing);
            }
        }
    }
}
