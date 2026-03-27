package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.entity.spells.magic_missile.MagicMissileProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASDataComponents;
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
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

public class PulsarMantleItem extends ISASWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("pulsar_mantle")
                                .rankModifier(1, "gravitational_pulse")
                                .rankModifier(3, "restorative_missile")
                                .rankModifier(5, "missile_buffer")
                                .stat(AbilityStatTemplate.builder("health_threshold")
                                        .initialValue(0.15D, 0.35D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0327D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("missile_count")
                                        .initialValue(10D, 25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("missile_damage")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("cooldown")
                                        .initialValue(45D, 60D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.0238D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("pulse_radius")
                                        .initialValue(1.5D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("pulse_push")
                                        .initialValue(0.5D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("heal_on_hit")
                                        .initialValue(0.25D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("ability_trigger").build())
                                        .source(ExperienceSourceTemplate.builder("released_projectile").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("ability_triggers")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("projectiles_released")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("restorative_missile_healing")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("restorative_missile", VisibilityState.OBFUSCATED)
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

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("pulsar_mantle");

        if (!ability.canPlayerUse(player))
            return;

        if (!player.isAlive() || player.isDeadOrDying()) {
            stack.remove(RISASDataComponents.PULSAR_MANTLE_PENDING_MISSILES.get());
            stack.remove(RISASDataComponents.PULSAR_MANTLE_NEXT_SHOT_AT.get());
            stack.remove(RISASDataComponents.PULSAR_MANTLE_RELEASE_STEP.get());
            return;
        }

        var gameTime = player.level().getGameTime();
        var cooldownUntil = stack.getOrDefault(RISASDataComponents.PULSAR_MANTLE_COOLDOWN_UNTIL.get(), 0L);
        var pendingMissiles = Math.max(0, stack.getOrDefault(RISASDataComponents.PULSAR_MANTLE_PENDING_MISSILES.get(), 0));
        var nextShotAt = stack.getOrDefault(RISASDataComponents.PULSAR_MANTLE_NEXT_SHOT_AT.get(), gameTime);
        var releaseStep = Math.max(0, stack.getOrDefault(RISASDataComponents.PULSAR_MANTLE_RELEASE_STEP.get(), 0));
        var wasBelowThreshold = stack.getOrDefault(RISASDataComponents.PULSAR_MANTLE_BELOW_THRESHOLD.get(), false);
        var threshold = Mth.clamp(ability.getStatData("health_threshold").getValue(), 0D, 1D);
        var isBelowThreshold = player.getMaxHealth() > 0F && player.getHealth() / player.getMaxHealth() <= threshold;

        if (isBelowThreshold && !wasBelowThreshold && pendingMissiles <= 0 && cooldownUntil <= gameTime) {
            pendingMissiles = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("missile_count").getValue())));
            nextShotAt = gameTime;
            releaseStep = 0;
            stack.set(RISASDataComponents.PULSAR_MANTLE_PENDING_MISSILES.get(), pendingMissiles);
            stack.set(RISASDataComponents.PULSAR_MANTLE_NEXT_SHOT_AT.get(), nextShotAt);
            stack.set(RISASDataComponents.PULSAR_MANTLE_RELEASE_STEP.get(), releaseStep);

            var relicData = this.getRelicData(player, stack);

            relicData.getLevelingData().addExperience("pulsar_mantle", "ability_trigger", 1D);
            ability.getStatisticData().getMetricData("ability_triggers").addValue(1D);
        }

        if (pendingMissiles > 0 && ability.isRankModifierUnlocked("gravitational_pulse")) {
            var level = player.serverLevel();
            var pulseRadius = Math.max(0D, ability.getStatData("pulse_radius").getValue());
            var pulsePush = Math.max(0D, ability.getStatData("pulse_push").getValue());

            if (pulseRadius > 0D && pulsePush > 0D) {
                for (var target : level.getEntities(
                        player,
                        player.getBoundingBox().inflate(pulseRadius),
                        entity -> entity != player && entity.isAlive()
                )) {
                    if (target instanceof Projectile proj && proj.getOwner() != null && proj.getOwner().equals(player))
                        continue;

                    var pushVector = target.position().subtract(player.position());

                    if (pushVector.lengthSqr() <= 1.0E-4D) {
                        var randomAngle = level.random.nextDouble() * Math.PI * 2D;
                        pushVector = new Vec3(Math.cos(randomAngle), 0D, Math.sin(randomAngle));
                    }

                    pushVector = pushVector.normalize();
                    target.push(pushVector.x * pulsePush * 0.35D, Math.max(0.08D, pulsePush * 0.08D), pushVector.z * pulsePush * 0.35D);
                    target.hurtMarked = true;
                }
            }
        }

        if (pendingMissiles > 0 && gameTime >= nextShotAt) {
            var level = player.serverLevel();
            var projectile = new MagicMissileProjectile(level, player);
            var angle = level.getRandom().nextDouble() * Mth.TWO_PI;
            var direction = new Vec3(Math.cos(angle), 0D, Math.sin(angle));

            projectile.setPos(player.position()
                    .add(0D, player.getBbHeight() * 0.6D - projectile.getBoundingBox().getYsize() * 0.5D, 0D)
                    .add(direction.scale(0.35D)));
            projectile.setDeltaMovement(direction.x * projectile.getSpeed(), 0D, direction.z * projectile.getSpeed());
            projectile.setDamage((float) Math.max(0D, ability.getStatData("missile_damage").getValue()));
            projectile.getPersistentData().putBoolean("risas_pulsar_mantle_projectile", true);

            level.addFreshEntity(projectile);
            level.playSound(
                    null,
                    projectile.getX(),
                    projectile.getY(),
                    projectile.getZ(),
                    SoundRegistry.ENDER_CAST.get(),
                    SoundSource.PLAYERS,
                    1F,
                    1.75F + level.getRandom().nextFloat() * 0.25F
            );

            var relicData = this.getRelicData(player, stack);

            relicData.getLevelingData().addExperience("pulsar_mantle", "released_projectile", 1D);
            ability.getStatisticData().getMetricData("projectiles_released").addValue(1D);

            pendingMissiles--;
            releaseStep++;

            if (pendingMissiles > 0) {
                var shotDelay = Math.max(1L, 10L - Math.min(8L, releaseStep));
                stack.set(RISASDataComponents.PULSAR_MANTLE_PENDING_MISSILES.get(), pendingMissiles);
                stack.set(RISASDataComponents.PULSAR_MANTLE_NEXT_SHOT_AT.get(), gameTime + shotDelay);
                stack.set(RISASDataComponents.PULSAR_MANTLE_RELEASE_STEP.get(), releaseStep);
            } else {
                stack.remove(RISASDataComponents.PULSAR_MANTLE_PENDING_MISSILES.get());
                stack.remove(RISASDataComponents.PULSAR_MANTLE_NEXT_SHOT_AT.get());
                stack.remove(RISASDataComponents.PULSAR_MANTLE_RELEASE_STEP.get());
                stack.set(
                        RISASDataComponents.PULSAR_MANTLE_COOLDOWN_UNTIL.get(),
                        gameTime + Math.max(1L, Math.round(Math.max(0D, ability.getStatData("cooldown").getValue()) * 20D))
                );
            }
        }

        if (isBelowThreshold != wasBelowThreshold)
            stack.set(RISASDataComponents.PULSAR_MANTLE_BELOW_THRESHOLD.get(), isBelowThreshold);
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getEntity() == player || event.getNewDamage() <= 0F)
                return;

            if (!(event.getSource().getDirectEntity() instanceof MagicMissileProjectile projectile))
                return;

            if (!projectile.getPersistentData().getBoolean("risas_pulsar_mantle_projectile"))
                return;

            ItemStack selectedStack = ItemStack.EMPTY;
            var bestDamage = -1D;
            var bestItem = (PulsarMantleItem) null;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.PULSAR_MANTLE.value())) {
                if (!(stack.getItem() instanceof PulsarMantleItem item))
                    continue;

                var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("pulsar_mantle");

                if (!ability.canPlayerUse(player))
                    continue;

                var missileDamage = Math.max(0D, ability.getStatData("missile_damage").getValue());

                if (missileDamage <= bestDamage)
                    continue;

                bestDamage = missileDamage;
                selectedStack = stack;
                bestItem = item;
            }

            if (selectedStack.isEmpty() || bestItem == null)
                return;

            var ability = bestItem.getRelicData(player, selectedStack).getAbilitiesData().getAbilityData("pulsar_mantle");

            if (ability.isRankModifierUnlocked("restorative_missile")) {
                var heal = Math.max(0D, ability.getStatData("heal_on_hit").getValue());

                if (heal > 0D) {
                    var healthBefore = player.getHealth();
                    player.heal((float) heal);
                    ability.getStatisticData().getMetricData("restorative_missile_healing").addValue(Math.max(0F, player.getHealth() - healthBefore));
                }
            }

            if (!ability.isRankModifierUnlocked("missile_buffer"))
                return;

            var gameTime = player.level().getGameTime();
            var pendingMissiles = Math.max(0, selectedStack.getOrDefault(RISASDataComponents.PULSAR_MANTLE_PENDING_MISSILES.get(), 0)) + 1;

            selectedStack.set(RISASDataComponents.PULSAR_MANTLE_PENDING_MISSILES.get(), pendingMissiles);

            if (selectedStack.getOrDefault(RISASDataComponents.PULSAR_MANTLE_NEXT_SHOT_AT.get(), 0L) <= gameTime)
                selectedStack.set(RISASDataComponents.PULSAR_MANTLE_NEXT_SHOT_AT.get(), gameTime + 1L);
        }
    }
}
