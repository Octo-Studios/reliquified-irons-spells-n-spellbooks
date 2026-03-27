package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
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
import it.hurts.sskirillss.relics.api.relics.data.AbilityData;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

public class RingOfElusivenessItem extends ISASWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("elusiveness")
                                .rankModifier(1, "restorative_evasion")
                                .rankModifier(3, "vanishing_step")
                                .rankModifier(5, "ambush_window")
                                .stat(AbilityStatTemplate.builder("max_charges")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("charge_regen_time")
                                        .initialValue(30D, 45D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.0222D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("heal_on_evasion")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("vanish_duration")
                                        .initialValue(1D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("ambush_window")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("ambush_bonus")
                                        .initialValue(0.25D, 0.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("ability_trigger").build())
                                        .source(ExperienceSourceTemplate.builder("ambush_attack")
                                                .rankModifierVisibilityState("ambush_window", VisibilityState.OBFUSCATED)
                                                .build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("ability_triggers")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("dodged_damage")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("restorative_evasion_healing")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("restorative_evasion", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("vanishing_step_duration")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("vanishing_step", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("ambush_bonus_damage")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("ambush_window", VisibilityState.OBFUSCATED)
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
        if (!(slotContext.entity() instanceof Player player) || player.level().isClientSide())
            return;

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("elusiveness");

        if (!ability.canPlayerUse(player))
            return;

        var gameTime = player.level().getGameTime();
        var maxCharges = Math.max(1D, ability.getStatData("max_charges").getValue());
        var rechargeTicks = Math.max(1L, Math.round(Math.max(0D, ability.getStatData("charge_regen_time").getValue()) * 20D));
        var storedCharges = stack.get(RISASDataComponents.RING_OF_ELUSIVENESS_CHARGES.get());
        var lastUpdate = stack.get(RISASDataComponents.RING_OF_ELUSIVENESS_LAST_UPDATE.get());

        if (storedCharges == null || lastUpdate == null) {
            stack.set(RISASDataComponents.RING_OF_ELUSIVENESS_CHARGES.get(), maxCharges);
            stack.set(RISASDataComponents.RING_OF_ELUSIVENESS_LAST_UPDATE.get(), gameTime);
            return;
        }

        var deltaTicks = Math.max(0L, gameTime - lastUpdate);
        var newCharges = Math.min(maxCharges, Math.max(0D, storedCharges) + (double) deltaTicks / (double) rechargeTicks);

        stack.set(RISASDataComponents.RING_OF_ELUSIVENESS_CHARGES.get(), newCharges);
        stack.set(RISASDataComponents.RING_OF_ELUSIVENESS_LAST_UPDATE.get(), gameTime);
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof Player player) || player.level().isClientSide() || event.getAmount() <= 0F)
                return;

            if (event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY))
                return;

            var gameTime = player.level().getGameTime();
            ItemStack stack = ItemStack.EMPTY;
            AbilityData ability = null;
            var selectedCharges = 0D;

            for (var candidateStack : EntityUtils.findEquippedCurios(player, RISASItems.RING_OF_ELUSIVENESS.value())) {
                if (!(candidateStack.getItem() instanceof RingOfElusivenessItem candidateItem))
                    continue;

                var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("elusiveness");

                if (!candidateAbility.canPlayerUse(player))
                    continue;

                var maxCharges = Math.max(1D, candidateAbility.getStatData("max_charges").getValue());
                var rechargeTicks = Math.max(1L, Math.round(Math.max(0D, candidateAbility.getStatData("charge_regen_time").getValue()) * 20D));
                var charges = candidateStack.get(RISASDataComponents.RING_OF_ELUSIVENESS_CHARGES.get());
                var lastUpdate = candidateStack.get(RISASDataComponents.RING_OF_ELUSIVENESS_LAST_UPDATE.get());

                if (charges == null || lastUpdate == null) {
                    charges = maxCharges;
                    lastUpdate = gameTime;
                }

                var deltaTicks = Math.max(0L, gameTime - lastUpdate);
                var updatedCharges = Math.min(maxCharges, Math.max(0D, charges) + (double) deltaTicks / (double) rechargeTicks);

                candidateStack.set(RISASDataComponents.RING_OF_ELUSIVENESS_CHARGES.get(), updatedCharges);
                candidateStack.set(RISASDataComponents.RING_OF_ELUSIVENESS_LAST_UPDATE.get(), gameTime);

                if (updatedCharges < 1D)
                    continue;

                if (updatedCharges <= selectedCharges)
                    continue;

                stack = candidateStack;
                ability = candidateAbility;
                selectedCharges = updatedCharges;
            }

            if (stack.isEmpty() || ability == null || selectedCharges < 1D)
                return;

            var oldX = player.getX();
            var oldY = player.getY();
            var oldZ = player.getZ();
            var random = player.getRandom();
            var teleported = false;

            for (var attempt = 0; attempt < 24; attempt++) {
                var angle = random.nextDouble() * Math.PI * 2D;
                var distance = Mth.lerp(random.nextDouble(), 2D, 16D);
                var targetX = oldX + Math.cos(angle) * distance;
                var targetY = Mth.clamp(oldY + random.nextDouble() * 6D - 3D, player.level().getMinBuildHeight(), player.level().getMaxBuildHeight() - 1);
                var targetZ = oldZ + Math.sin(angle) * distance;

                if (player.isPassenger())
                    player.stopRiding();

                if (!player.randomTeleport(targetX, targetY, targetZ, true))
                    continue;

                if (Math.abs(player.getY() - oldY) > 4D) {
                    player.teleportTo(oldX, oldY, oldZ);
                    player.fallDistance = 0F;
                    continue;
                }

                var unsafe = false;
                var area = player.getBoundingBox().inflate(0.05D);
                var minX = Mth.floor(area.minX);
                var maxX = Mth.floor(area.maxX);
                var minY = Mth.floor(area.minY);
                var maxY = Mth.floor(area.maxY);
                var minZ = Mth.floor(area.minZ);
                var maxZ = Mth.floor(area.maxZ);

                for (var x = minX; x <= maxX && !unsafe; x++) {
                    for (var y = minY; y <= maxY && !unsafe; y++) {
                        for (var z = minZ; z <= maxZ; z++) {
                            var state = player.level().getBlockState(new net.minecraft.core.BlockPos(x, y, z));

                            if (state.is(Blocks.CACTUS)
                                    || state.is(BlockTags.FIRE)
                                    || state.is(Blocks.MAGMA_BLOCK)
                                    || state.is(Blocks.SWEET_BERRY_BUSH)
                                    || state.getFluidState().is(FluidTags.LAVA)) {
                                unsafe = true;
                                break;
                            }
                        }
                    }
                }

                if (unsafe) {
                    player.teleportTo(oldX, oldY, oldZ);
                    player.fallDistance = 0F;
                    continue;
                }

                player.level().playSound(null, oldX, oldY, oldZ, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1F, 1F);
                player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
                teleported = true;
                break;
            }

            if (!teleported)
                return;

            var preventedDamage = Math.max(0F, event.getAmount());
            var relicData = ((RingOfElusivenessItem) stack.getItem()).getRelicData(player, stack);

            event.setCanceled(true);
            stack.set(RISASDataComponents.RING_OF_ELUSIVENESS_CHARGES.get(), Math.max(0D, selectedCharges - 1D));
            stack.set(RISASDataComponents.RING_OF_ELUSIVENESS_LAST_UPDATE.get(), gameTime);
            relicData.getLevelingData().addExperience("elusiveness", "ability_trigger", 1D);
            ability.getStatisticData().getMetricData("ability_triggers").addValue(1D);

            if (preventedDamage > 0F)
                ability.getStatisticData().getMetricData("dodged_damage").addValue(preventedDamage);

            if (ability.isRankModifierUnlocked("restorative_evasion")) {
                var heal = Math.max(0D, ability.getStatData("heal_on_evasion").getValue());

                if (heal > 0D) {
                    var healthBefore = player.getHealth();
                    player.heal((float) heal);
                    ability.getStatisticData().getMetricData("restorative_evasion_healing").addValue(Math.max(0F, player.getHealth() - healthBefore));
                }
            }

            if (ability.isRankModifierUnlocked("vanishing_step")) {
                var vanishTicks = Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("vanish_duration").getValue()) * 20D));

                if (vanishTicks > 0) {
                    player.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY, vanishTicks, 0, false, true, true), player);
                    ability.getStatisticData().getMetricData("vanishing_step_duration").addValue(vanishTicks / 20D);
                }
            }

            if (ability.isRankModifierUnlocked("ambush_window")) {
                var windowTicks = Math.max(0L, Math.round(Math.max(0D, ability.getStatData("ambush_window").getValue()) * 20D));

                if (windowTicks > 0L)
                    stack.set(RISASDataComponents.RING_OF_ELUSIVENESS_BONUS_UNTIL.get(), gameTime + windowTicks);
                else
                    stack.remove(RISASDataComponents.RING_OF_ELUSIVENESS_BONUS_UNTIL.get());
            } else {
                stack.remove(RISASDataComponents.RING_OF_ELUSIVENESS_BONUS_UNTIL.get());
            }
        }

        @SubscribeEvent
        public static void onLivingDamageDealtPre(LivingDamageEvent.Pre event) {
            if (!(event.getSource().getEntity() instanceof Player player) || player.level().isClientSide() || event.getEntity() == player || event.getNewDamage() <= 0F)
                return;

            var gameTime = player.level().getGameTime();
            ItemStack stack = ItemStack.EMPTY;
            AbilityData bestAbility = null;
            var bestBonus = -1D;

            for (var candidateStack : EntityUtils.findEquippedCurios(player, RISASItems.RING_OF_ELUSIVENESS.value())) {
                if (!(candidateStack.getItem() instanceof RingOfElusivenessItem item))
                    continue;

                var candidateAbility = item.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("elusiveness");

                if (!candidateAbility.canPlayerUse(player) || !candidateAbility.isRankModifierUnlocked("ambush_window"))
                    continue;

                var bonusUntil = candidateStack.getOrDefault(RISASDataComponents.RING_OF_ELUSIVENESS_BONUS_UNTIL.get(), 0L);

                if (bonusUntil <= gameTime) {
                    candidateStack.remove(RISASDataComponents.RING_OF_ELUSIVENESS_BONUS_UNTIL.get());
                    continue;
                }

                var bonus = Math.max(0D, candidateAbility.getStatData("ambush_bonus").getValue());

                if (bonus <= bestBonus)
                    continue;

                bestBonus = bonus;
                bestAbility = candidateAbility;
                stack = candidateStack;
            }

            if (stack.isEmpty() || bestAbility == null)
                return;

            ((RingOfElusivenessItem) stack.getItem()).getRelicData(player, stack).getLevelingData().addExperience("elusiveness", "ambush_attack", 1D);
            stack.remove(RISASDataComponents.RING_OF_ELUSIVENESS_BONUS_UNTIL.get());

            if (bestBonus <= 0D)
                return;

            var originalDamage = event.getNewDamage();
            var boostedDamage = (float) Math.max(0D, originalDamage * (1D + bestBonus));

            event.setNewDamage(boostedDamage);

            if (boostedDamage > originalDamage)
                bestAbility.getStatisticData().getMetricData("ambush_bonus_damage").addValue(boostedDamage - originalDamage);
        }
    }
}
