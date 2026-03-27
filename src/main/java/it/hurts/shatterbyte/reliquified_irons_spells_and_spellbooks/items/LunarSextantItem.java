package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.entity.spells.comet.Comet;
import io.redspace.ironsspellbooks.damage.DamageSources;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import java.util.ArrayList;

public class LunarSextantItem extends ISASWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("lunar_sextant")
                                .rankModifier(1, "zenith_blessing")
                                .rankModifier(3, "stunning_impact")
                                .rankModifier(5, "chain_starfall")
                                .stat(AbilityStatTemplate.builder("trigger_chance")
                                        .initialValue(0.05D, 0.15D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("star_damage")
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("zenith_bonus")
                                        .initialValue(0.25D, 0.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("stun_duration")
                                        .initialValue(1D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("chain_chance")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("chain_radius")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("star_created").build())
                                        .source(ExperienceSourceTemplate.builder("star_hit").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("stars_spawned")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("star_hits")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("star_damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("stun_duration_total")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("stunning_impact", VisibilityState.OBFUSCATED)
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
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (!(event.getEntity().level() instanceof ServerLevel level) || event.getNewDamage() <= 0F)
                return;

            var source = event.getSource();
            var target = event.getEntity();

            if (source.getDirectEntity() instanceof Comet comet
                    && comet.getPersistentData().getBoolean("risas_lunar_sextant_star")
                    && source.getEntity() instanceof ServerPlayer owner) {
                if (!level.isNight())
                    return;

                ItemStack stack = ItemStack.EMPTY;
                AbilityData ability = null;
                var bestDamage = -1D;

                for (var candidateStack : EntityUtils.findEquippedCurios(owner, RISASItems.LUNAR_SEXTANT.value())) {
                    if (!(candidateStack.getItem() instanceof LunarSextantItem item))
                        continue;

                    var candidateAbility = item.getRelicData(owner, candidateStack).getAbilitiesData().getAbilityData("lunar_sextant");

                    if (!candidateAbility.canPlayerUse(owner))
                        continue;

                    var damage = Math.max(0D, candidateAbility.getStatData("star_damage").getValue());

                    if (damage <= bestDamage)
                        continue;

                    bestDamage = damage;
                    ability = candidateAbility;
                    stack = candidateStack;
                }

                if (stack.isEmpty() || ability == null)
                    return;

                var relicData = ((LunarSextantItem) stack.getItem()).getRelicData(owner, stack);

                relicData.getLevelingData().addExperience("lunar_sextant", "star_hit", 1D);
                ability.getStatisticData().getMetricData("star_hits").addValue(1D);
                ability.getStatisticData().getMetricData("star_damage_dealt").addValue(event.getNewDamage());

                if (ability.isRankModifierUnlocked("stunning_impact") && target instanceof LivingEntity living && living.isAlive()) {
                    var stunTicks = Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("stun_duration").getValue()) * 20D));

                    if (stunTicks > 0) {
                        living.addEffect(new MobEffectInstance(RelicsMobEffects.STUN, stunTicks, 0, false, true, true), owner);
                        ability.getStatisticData().getMetricData("stun_duration_total").addValue(stunTicks / 20D);
                    }
                }

                if (!ability.isRankModifierUnlocked("chain_starfall"))
                    return;

                var chance = Mth.clamp(ability.getStatData("chain_chance").getValue(), 0D, 1D);
                var radius = Math.max(0D, ability.getStatData("chain_radius").getValue());

                if (chance <= 0D || radius <= 0D || level.getRandom().nextDouble() >= chance)
                    return;

                var candidates = new ArrayList<>(level.getEntitiesOfClass(
                        LivingEntity.class,
                        target.getBoundingBox().inflate(radius),
                        entity -> entity != target && entity != owner && entity.isAlive() && !DamageSources.isFriendlyFireBetween(owner, entity)
                ));

                if (candidates.isEmpty())
                    return;

                var nextTarget = candidates.get(level.getRandom().nextInt(candidates.size()));
                var damage = Math.max(0D, ability.getStatData("star_damage").getValue());

                if (ability.isRankModifierUnlocked("zenith_blessing")) {
                    var peakMultiplier = Mth.clamp(ability.getStatData("zenith_bonus").getValue(), 0D, 10D);
                    var timeOfDay = level.getDayTime() % 24000L;

                    if (timeOfDay < 0L)
                        timeOfDay += 24000L;

                    var zenith = 1D - Math.min(1D, Math.abs(timeOfDay - 18000D) / 5000D);

                    damage *= 1D + peakMultiplier * zenith;
                }

                if (damage <= 0D)
                    return;

                var center = nextTarget.position().add(0D, nextTarget.getBbHeight() * 0.5D, 0D);
                var spawnX = center.x + (level.getRandom().nextDouble() - 0.5D) * 2D;
                var spawnY = center.y + 12D + level.getRandom().nextDouble() * 3D;
                var spawnZ = center.z + (level.getRandom().nextDouble() - 0.5D) * 2D;
                var direction = center.subtract(spawnX, spawnY, spawnZ);

                if (direction.lengthSqr() <= 1.0E-6D)
                    return;

                var nextComet = new Comet(level, owner);

                nextComet.setPos(spawnX - 1D, spawnY, spawnZ);
                nextComet.shoot(direction.normalize(), 0.075F);
                nextComet.setDamage((float) damage);
                nextComet.setExplosionRadius(2F);
                nextComet.getPersistentData().putBoolean("risas_lunar_sextant_star", true);

                level.addFreshEntity(nextComet);
                relicData.getLevelingData().addExperience("lunar_sextant", "star_created", 1D);
                ability.getStatisticData().getMetricData("stars_spawned").addValue(1D);
                level.playSound(null, spawnX, spawnY, spawnZ, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 3F, 0.7F + level.getRandom().nextFloat() * 0.3F);
                return;
            }

            if (!(source.getEntity() instanceof ServerPlayer attacker) || attacker == target || !level.isNight() || source.getDirectEntity() instanceof Comet)
                return;

            if (Math.max(attacker.getAttackStrengthScale(0F), attacker.getAttackStrengthScale(0.5F)) < 0.99F)
                return;

            ItemStack stack = ItemStack.EMPTY;
            AbilityData ability = null;
            var bestDamage = -1D;

            for (var candidateStack : EntityUtils.findEquippedCurios(attacker, RISASItems.LUNAR_SEXTANT.value())) {
                if (!(candidateStack.getItem() instanceof LunarSextantItem item))
                    continue;

                var candidateAbility = item.getRelicData(attacker, candidateStack).getAbilitiesData().getAbilityData("lunar_sextant");

                if (!candidateAbility.canPlayerUse(attacker))
                    continue;

                var chance = Mth.clamp(candidateAbility.getStatData("trigger_chance").getValue(), 0D, 1D);

                if (chance <= 0D || level.getRandom().nextDouble() >= chance)
                    continue;

                var damage = Math.max(0D, candidateAbility.getStatData("star_damage").getValue());

                if (candidateAbility.isRankModifierUnlocked("zenith_blessing")) {
                    var peakMultiplier = Mth.clamp(candidateAbility.getStatData("zenith_bonus").getValue(), 0D, 10D);
                    var timeOfDay = level.getDayTime() % 24000L;

                    if (timeOfDay < 0L)
                        timeOfDay += 24000L;

                    var zenith = 1D - Math.min(1D, Math.abs(timeOfDay - 18000D) / 5000D);

                    damage *= 1D + peakMultiplier * zenith;
                }

                if (damage <= bestDamage)
                    continue;

                bestDamage = damage;
                ability = candidateAbility;
                stack = candidateStack;
            }

            if (stack.isEmpty() || ability == null || bestDamage <= 0D)
                return;

            var relicData = ((LunarSextantItem) stack.getItem()).getRelicData(attacker, stack);
            var center = target.position().add(0D, target.getBbHeight() * 0.5D, 0D);
            var spawnX = center.x + (level.getRandom().nextDouble() - 0.5D) * 2D;
            var spawnY = center.y + 12D + level.getRandom().nextDouble() * 3D;
            var spawnZ = center.z + (level.getRandom().nextDouble() - 0.5D) * 2D;
            var direction = center.subtract(spawnX, spawnY, spawnZ);

            if (direction.lengthSqr() <= 1.0E-6D)
                return;

            var comet = new Comet(level, attacker);

            comet.setPos(spawnX - 1D, spawnY, spawnZ);
            comet.shoot(direction.normalize(), 0.075F);
            comet.setDamage((float) bestDamage);
            comet.setExplosionRadius(2F);
            comet.getPersistentData().putBoolean("risas_lunar_sextant_star", true);

            level.addFreshEntity(comet);
            relicData.getLevelingData().addExperience("lunar_sextant", "star_created", 1D);
            ability.getStatisticData().getMetricData("stars_spawned").addValue(1D);
            level.playSound(null, spawnX, spawnY, spawnZ, SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 3F, 0.7F + level.getRandom().nextFloat() * 0.3F);
        }
    }
}
