package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.init.RISASItems;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.init.RISASDataComponents;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items.misc.RISASLootEntries;
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
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class FlaskOfTheRedMistItem extends ISASRelic {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("red_mist")
                                .initialMaxLevel(10)
                                .rankModifier(1, "smokescreen")
                                .rankModifier(3, "hunt_mark")
                                .rankModifier(5, "execution_window")
                                .stat(AbilityStatTemplate.builder("cooldown")
                                        .initialValue(60D, 45D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.0222D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("blindness_duration")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("taunt_radius")
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("next_attack_window")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("next_attack_bonus")
                                        .initialValue(0.5D, 0.75D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("prevented_damage").build())
                                        .source(ExperienceSourceTemplate.builder("blinded_target")
                                                .rankModifierVisibilityState("smokescreen", VisibilityState.OBFUSCATED)
                                                .build())
                                        .source(ExperienceSourceTemplate.builder("execution_bonus_damage")
                                                .rankModifierVisibilityState("execution_window", VisibilityState.OBFUSCATED)
                                                .build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("ability_triggers")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("smokescreen_blindness_duration")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("smokescreen", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("execution_bonus_damage")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("execution_window", VisibilityState.OBFUSCATED)
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
        public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getNewDamage() <= 0F)
                return;

            if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY))
                return;

            if (Math.max(0F, event.getNewDamage() - player.getAbsorptionAmount()) < player.getHealth())
                return;

            var gameTime = player.level().getGameTime();
            FlaskOfTheRedMistItem item = null;
            ItemStack stack = ItemStack.EMPTY;
            AbilityData ability = null;
            var bestCooldown = Double.MAX_VALUE;

            for (var candidateStack : EntityUtils.findEquippedCurios(player, RISASItems.FLASK_OF_THE_RED_MIST.value())) {
                if (!(candidateStack.getItem() instanceof FlaskOfTheRedMistItem candidateItem))
                    continue;

                if (candidateStack.getOrDefault(RISASDataComponents.FLASK_OF_THE_RED_MIST_COOLDOWN_UNTIL.get(), 0L) > gameTime)
                    continue;

                var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("red_mist");

                if (!candidateAbility.canPlayerUse(player))
                    continue;

                var cooldown = Math.max(0D, candidateAbility.getStatData("cooldown").getValue());

                if (cooldown >= bestCooldown)
                    continue;

                bestCooldown = cooldown;
                item = candidateItem;
                stack = candidateStack;
                ability = candidateAbility;
            }

            if (item == null || stack.isEmpty() || ability == null)
                return;

            var relicData = item.getRelicData(player, stack);
            var preventedDamage = Math.max(0D, event.getOriginalDamage() - Math.max(0F, player.getHealth() + player.getAbsorptionAmount() - 1F));

            event.setNewDamage(Math.max(0F, player.getHealth() + player.getAbsorptionAmount() - 1F));
            ability.getStatisticData().getMetricData("ability_triggers").addValue(1D);

            if (preventedDamage > 0D)
                relicData.getLevelingData().addExperience("red_mist", "prevented_damage", preventedDamage);

            if (player.level() instanceof ServerLevel level && event.getSource().getEntity() instanceof LivingEntity attacker && attacker.isAlive()) {
                var look = attacker.getLookAngle();
                var horizontalLook = new Vec3(look.x, 0D, look.z);

                if (horizontalLook.lengthSqr() < 1.0E-4D)
                    horizontalLook = new Vec3(0D, 0D, 1D).yRot((float) Math.toRadians(-attacker.getYRot()));

                var behind = attacker.position().subtract(horizontalLook.normalize().scale(attacker.getBbWidth() + 1.35D));
                var destination = TeleportSpell.solveTeleportDestination(level, player, BlockPos.containing(behind), behind);
                var magicData = MagicData.getPlayerMagicData(player);

                magicData.setAdditionalCastData(new TeleportSpell.TeleportData(destination));
                SpellRegistry.BLOOD_STEP_SPELL.get().onCast(level, 1, player, CastSource.SWORD, magicData);
                magicData.resetAdditionalCastData();

                if (ability.isRankModifierUnlocked("smokescreen")) {
                    var blindnessTicks = Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("blindness_duration").getValue()) * 20D));

                    if (blindnessTicks > 0 && attacker.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, blindnessTicks, 0, false, true, true))) {
                        ability.getStatisticData().getMetricData("smokescreen_blindness_duration").addValue(blindnessTicks / 20D);
                        relicData.getLevelingData().addExperience("red_mist", "blinded_target", 1D);
                    }
                }

                if (ability.isRankModifierUnlocked("hunt_mark")) {
                    var radius = Math.max(0D, ability.getStatData("taunt_radius").getValue());
                    var taunted = 0;

                    if (radius > 0D) {
                        for (var mob : level.getEntitiesOfClass(Mob.class, attacker.getBoundingBox().inflate(radius), mob -> mob != attacker && mob.isAlive() && !mob.getStringUUID().equals(player.getStringUUID()))) {
                            mob.setTarget(attacker);
                            taunted++;
                        }
                    }

                }
            }

            if (ability.isRankModifierUnlocked("execution_window")) {
                var windowTicks = Math.max(0L, Math.round(Math.max(0D, ability.getStatData("next_attack_window").getValue()) * 20D));

                if (windowTicks > 0L) {
                    stack.set(RISASDataComponents.FLASK_OF_THE_RED_MIST_BONUS_UNTIL.get(), gameTime + windowTicks);
                } else {
                    stack.remove(RISASDataComponents.FLASK_OF_THE_RED_MIST_BONUS_UNTIL.get());
                }
            } else {
                stack.remove(RISASDataComponents.FLASK_OF_THE_RED_MIST_BONUS_UNTIL.get());
            }

            stack.set(RISASDataComponents.FLASK_OF_THE_RED_MIST_COOLDOWN_UNTIL.get(), gameTime + Math.max(1L, Math.round(bestCooldown * 20D)));
        }

        @SubscribeEvent
        public static void onLivingDamageDealtPre(LivingDamageEvent.Pre event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getEntity() == player || event.getNewDamage() <= 0F)
                return;

            var gameTime = player.level().getGameTime();
            FlaskOfTheRedMistItem item = null;
            ItemStack stack = ItemStack.EMPTY;
            AbilityData ability = null;
            var bestBonus = -1D;

            for (var candidateStack : EntityUtils.findEquippedCurios(player, RISASItems.FLASK_OF_THE_RED_MIST.value())) {
                if (!(candidateStack.getItem() instanceof FlaskOfTheRedMistItem candidateItem))
                    continue;

                var bonusUntil = candidateStack.getOrDefault(RISASDataComponents.FLASK_OF_THE_RED_MIST_BONUS_UNTIL.get(), 0L);

                if (bonusUntil <= gameTime) {
                    candidateStack.remove(RISASDataComponents.FLASK_OF_THE_RED_MIST_BONUS_UNTIL.get());
                    continue;
                }

                var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("red_mist");

                if (!candidateAbility.canPlayerUse(player) || !candidateAbility.isRankModifierUnlocked("execution_window"))
                    continue;

                var bonus = Math.max(0D, candidateAbility.getStatData("next_attack_bonus").getValue());

                if (bonus <= bestBonus)
                    continue;

                bestBonus = bonus;
                stack = candidateStack;
                ability = candidateAbility;
                item = candidateItem;
            }

            if (stack.isEmpty() || ability == null || item == null || bestBonus <= 0D)
                return;

            var baseDamage = Math.max(0D, event.getNewDamage());
            var bonusDamage = Math.max(0D, baseDamage * bestBonus);
            var relicData = item.getRelicData(player, stack);

            event.setNewDamage((float) Math.max(0D, baseDamage + bonusDamage));
            stack.remove(RISASDataComponents.FLASK_OF_THE_RED_MIST_BONUS_UNTIL.get());

            if (bonusDamage > 0D) {
                ability.getStatisticData().getMetricData("execution_bonus_damage").addValue(bonusDamage);
                relicData.getLevelingData().addExperience("red_mist", "execution_bonus_damage", bonusDamage);
            }

        }
    }
}

