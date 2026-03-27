package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedClaymoreEntity;
import io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedRapierEntity;
import io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedSwordEntity;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities.SummonedWeaponEntityTuning;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SealedSwordItem extends ISASWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("sealed_sword")
                                .rankModifier(1, "heal_strike")
                                .rankModifier(3, "disorient_strike")
                                .rankModifier(5, "sword_power_aura")
                                .modes("enabled", "disabled")
                                .stat(AbilityStatTemplate.builder("summon_count")
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("summon_health")
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("summon_damage")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("respawn_time")
                                        .initialValue(60D, 50D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.0229D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("summon_knockback")
                                        .initialValue(0D, 0.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("summon_regen_interval")
                                        .initialValue(10D, 7.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.019D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("summon_regen_amount")
                                        .initialValue(0D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("heal_on_hit")
                                        .initialValue(0.25D, 0.75D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("lose_target_chance")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("aura_radius")
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("damage_per_sword")
                                        .initialValue(0.01D, 0.05D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 1))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("summoned_sword_attack").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("sword_damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("heal_strike_healing")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("heal_strike", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("sword_power_bonus_damage")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("sword_power_aura", VisibilityState.OBFUSCATED)
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

        if (stack.get(RISASDataComponents.SEALED_WEAPON_INSTANCE.get()) == null) {
            stack.set(RISASDataComponents.SEALED_WEAPON_INSTANCE.get(), UUID.randomUUID());
            stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
        }

        if (!player.isAlive()) {
            CommonEvents.discardSummons(player.getServer(), stack);
            stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
            return;
        }

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("sealed_sword");

        if (!ability.canPlayerUse(player) || "disabled".equals(ability.getMode())) {
            CommonEvents.discardSummons(player.getServer(), stack);
            stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
            return;
        }

        var summonCount = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("summon_count").getValue())));
        var summonHealth = Math.max(1D, ability.getStatData("summon_health").getValue());
        var summonDamage = Math.max(0D, ability.getStatData("summon_damage").getValue());
        var respawnDelayTicks = Math.max(1L, Math.round(Math.max(0D, ability.getStatData("respawn_time").getValue()) * 20D));
        var summonKnockback = Math.max(0D, ability.getStatData("summon_knockback").getValue());
        var summonRegenIntervalTicks = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("summon_regen_interval").getValue()) * 20D));
        var summonRegenAmount = Math.max(0D, ability.getStatData("summon_regen_amount").getValue());
        var gameTime = player.level().getGameTime();

        var summons = new ArrayList<RISASDataComponents.SealedWeaponSummonData>(
                stack.getOrDefault(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.of())
        );
        var reset = summons.size() != summonCount;

        if (!reset) {
            for (var summonData : summons) {
                if (summonData.weaponType() != 0) {
                    reset = true;
                    break;
                }
            }
        }

        if (reset) {
            CommonEvents.discardSummons(player.getServer(), stack);
            summons.clear();

            for (var index = 0; index < summonCount; index++) {
                summons.add(new RISASDataComponents.SealedWeaponSummonData(
                        0,
                        new UUID(0L, 0L),
                        player.level().dimension(),
                        gameTime
                ));
            }
        }

        var server = player.getServer();

        if (server == null) {
            if (summons.isEmpty())
                stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
            else
                stack.set(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.copyOf(summons));

            return;
        }

        var updated = new ArrayList<RISASDataComponents.SealedWeaponSummonData>(summons.size());

        for (var summonData : summons) {
            var summonLevel = server.getLevel(summonData.level());
            Entity entity = null;

            if (summonLevel != null && (summonData.summon().getMostSignificantBits() != 0L || summonData.summon().getLeastSignificantBits() != 0L))
                entity = summonLevel.getEntity(summonData.summon());

            if (entity instanceof SummonedSwordEntity summon && summon.isAlive() && SummonManager.getOwner(summon) == player) {
                var maxHealth = summon.getAttribute(Attributes.MAX_HEALTH);

                if (maxHealth != null)
                    maxHealth.setBaseValue(summonHealth);

                if (summon.getHealth() > summonHealth)
                    summon.setHealth((float) summonHealth);

                var attackDamage = summon.getAttribute(Attributes.ATTACK_DAMAGE);

                if (attackDamage != null)
                    attackDamage.setBaseValue(summonDamage);

                var attackKnockback = summon.getAttribute(Attributes.ATTACK_KNOCKBACK);

                if (attackKnockback != null)
                    attackKnockback.setBaseValue(summonKnockback);

                var tuning = (SummonedWeaponEntityTuning) summon;
                tuning.risas$setRegenIntervalTicks(summonRegenIntervalTicks);
                tuning.risas$setRegenAmount((float) summonRegenAmount);

                updated.add(new RISASDataComponents.SealedWeaponSummonData(
                        0,
                        summon.getUUID(),
                        summon.level().dimension(),
                        0L
                ));
                continue;
            }

            if (summonData.summon().getMostSignificantBits() != 0L || summonData.summon().getLeastSignificantBits() != 0L) {
                if (summonData.respawnAtTick() <= 0L) {
                    updated.add(new RISASDataComponents.SealedWeaponSummonData(
                            0,
                            summonData.summon(),
                            summonData.level(),
                            gameTime + 40L
                    ));
                    continue;
                }

                if (summonData.respawnAtTick() > gameTime) {
                    updated.add(summonData);
                    continue;
                }

                updated.add(new RISASDataComponents.SealedWeaponSummonData(
                        0,
                        new UUID(0L, 0L),
                        player.level().dimension(),
                        gameTime + respawnDelayTicks
                ));
                continue;
            }

            if (summonData.respawnAtTick() > gameTime) {
                updated.add(summonData);
                continue;
            }

            var summon = new SummonedSwordEntity(player.level(), player);
            var maxHealth = summon.getAttribute(Attributes.MAX_HEALTH);

            if (maxHealth != null)
                maxHealth.setBaseValue(summonHealth);

            var attackDamage = summon.getAttribute(Attributes.ATTACK_DAMAGE);

            if (attackDamage != null)
                attackDamage.setBaseValue(summonDamage);

            var attackKnockback = summon.getAttribute(Attributes.ATTACK_KNOCKBACK);

            if (attackKnockback != null)
                attackKnockback.setBaseValue(summonKnockback);

            var tuning = (SummonedWeaponEntityTuning) summon;
            tuning.risas$setRegenIntervalTicks(summonRegenIntervalTicks);
            tuning.risas$setRegenAmount((float) summonRegenAmount);

            summon.setHealth((float) summonHealth);
            summon.moveTo(
                    player.getX() + (player.level().getRandom().nextDouble() - 0.5D) * 2.5D,
                    player.getY() + 0.2D,
                    player.getZ() + (player.level().getRandom().nextDouble() - 0.5D) * 2.5D,
                    player.getYRot(),
                    player.getXRot()
            );
            player.level().addFreshEntity(summon);
            SummonManager.initSummon(summon, player, 630720000, new SummonedEntitiesCastData());

            updated.add(new RISASDataComponents.SealedWeaponSummonData(
                    0,
                    summon.getUUID(),
                    player.level().dimension(),
                    0L
            ));
        }

        if (updated.isEmpty())
            stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
        else
            stack.set(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.copyOf(updated));
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer player) || player.level().isClientSide())
            return;

        if (newStack.getItem() instanceof SealedSwordItem) {
            if (newStack != stack) {
                var oldInstance = stack.get(RISASDataComponents.SEALED_WEAPON_INSTANCE.get());

                if (oldInstance == null) {
                    oldInstance = UUID.randomUUID();
                    stack.set(RISASDataComponents.SEALED_WEAPON_INSTANCE.get(), oldInstance);
                }

                var newInstance = newStack.get(RISASDataComponents.SEALED_WEAPON_INSTANCE.get());

                if (newInstance == null) {
                    newStack.set(RISASDataComponents.SEALED_WEAPON_INSTANCE.get(), oldInstance);
                    newInstance = oldInstance;
                }

                var summons = stack.getOrDefault(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.<RISASDataComponents.SealedWeaponSummonData>of());

                if (oldInstance.equals(newInstance)) {
                    if (!summons.isEmpty() && newStack.getOrDefault(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.<RISASDataComponents.SealedWeaponSummonData>of()).isEmpty())
                        newStack.set(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.copyOf(summons));
                } else {
                    CommonEvents.discardSummons(player.getServer(), stack);
                    CommonEvents.discardSummons(player.getServer(), newStack);
                    newStack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
                }

                stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
                stack.remove(RISASDataComponents.SEALED_WEAPON_INSTANCE.get());
            }

            return;
        }

        CommonEvents.discardSummons(player.getServer(), stack);
        stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
        stack.remove(RISASDataComponents.SEALED_WEAPON_INSTANCE.get());
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (event.getNewDamage() <= 0F)
                return;

            if (!(event.getSource().getDirectEntity() instanceof SummonedSwordEntity summon))
                return;

            if (!(SummonManager.getOwner(summon) instanceof ServerPlayer player))
                return;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SEALED_SWORD.value())) {
                if (!(stack.getItem() instanceof SealedSwordItem item))
                    continue;

                var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("sealed_sword");

                if (!ability.canPlayerUse(player) || "disabled".equals(ability.getMode()))
                    continue;

                var summonTracked = false;

                for (var summonData : stack.getOrDefault(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.<RISASDataComponents.SealedWeaponSummonData>of())) {
                    if (summonData.weaponType() == 0 && summonData.summon().equals(summon.getUUID())) {
                        summonTracked = true;
                        break;
                    }
                }

                if (!summonTracked)
                    continue;

                var relicData = item.getRelicData(player, stack);

                relicData.getLevelingData().addExperience("sealed_sword", "summoned_sword_attack", 1D);
                ability.getStatisticData().getMetricData("sword_damage_dealt").addValue(event.getNewDamage());

                if (ability.isRankModifierUnlocked("heal_strike")) {
                    var heal = Math.max(0D, ability.getStatData("heal_on_hit").getValue());

                    if (heal > 0D) {
                        var healthBefore = player.getHealth();
                        player.heal((float) heal);
                        ability.getStatisticData().getMetricData("heal_strike_healing").addValue(Math.max(0D, player.getHealth() - healthBefore));
                    }
                }

                if (ability.isRankModifierUnlocked("disorient_strike") && event.getEntity() instanceof Mob mob) {
                    var chance = Math.max(0D, Math.min(1D, ability.getStatData("lose_target_chance").getValue()));

                    if (chance > 0D && mob.getRandom().nextDouble() <= chance) {
                        mob.setTarget(null);
                        mob.setLastHurtByMob(null);
                        mob.setLastHurtByPlayer(null);
                        mob.setAggressive(false);

                        SummonedSwordEntity redirectedTarget = null;
                        var bestDistance = Double.MAX_VALUE;
                        var server = player.getServer();

                        if (server != null) {
                            for (var summonData : stack.getOrDefault(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.<RISASDataComponents.SealedWeaponSummonData>of())) {
                                if (summonData.weaponType() != 0)
                                    continue;

                                if (summonData.summon().getMostSignificantBits() == 0L && summonData.summon().getLeastSignificantBits() == 0L)
                                    continue;

                                var level = server.getLevel(summonData.level());

                                if (level == null || level != mob.level())
                                    continue;

                                var entity = level.getEntity(summonData.summon());

                                if (!(entity instanceof SummonedSwordEntity sword) || !sword.isAlive())
                                    continue;

                                var distance = mob.distanceToSqr(sword);

                                if (distance >= bestDistance)
                                    continue;

                                bestDistance = distance;
                                redirectedTarget = sword;
                            }
                        }

                        if (redirectedTarget != null)
                            mob.setTarget(redirectedTarget);
                    }
                }

                break;
            }
        }

        @SubscribeEvent
        public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getEntity() == player || event.getNewDamage() <= 0F)
                return;

            if (event.getSource().getDirectEntity() instanceof SummonedSwordEntity
                    || event.getSource().getDirectEntity() instanceof SummonedRapierEntity
                    || event.getSource().getDirectEntity() instanceof SummonedClaymoreEntity)
                return;

            var bestBonus = 0D;
            AbilityData bestAbility = null;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SEALED_SWORD.value())) {
                if (!(stack.getItem() instanceof SealedSwordItem item))
                    continue;

                var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("sealed_sword");

                if (!ability.canPlayerUse(player) || "disabled".equals(ability.getMode()) || !ability.isRankModifierUnlocked("sword_power_aura"))
                    continue;

                var radius = Math.max(0D, ability.getStatData("aura_radius").getValue());
                var perSword = Math.max(0D, ability.getStatData("damage_per_sword").getValue());

                if (radius <= 0D || perSword <= 0D)
                    continue;

                var count = 0;
                var radiusSqr = radius * radius;
                var server = player.getServer();

                if (server == null)
                    continue;

                for (var summonData : stack.getOrDefault(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.<RISASDataComponents.SealedWeaponSummonData>of())) {
                    if (summonData.weaponType() != 0)
                        continue;

                    if (summonData.summon().getMostSignificantBits() == 0L && summonData.summon().getLeastSignificantBits() == 0L)
                        continue;

                    var level = server.getLevel(summonData.level());

                    if (level == null)
                        continue;

                    var entity = level.getEntity(summonData.summon());

                    if (!(entity instanceof SummonedSwordEntity summonedSword) || !summonedSword.isAlive())
                        continue;

                    if (summonedSword.level() != player.level())
                        continue;

                    if (summonedSword.distanceToSqr(player) <= radiusSqr)
                        count++;
                }

                var bonus = count * perSword;

                if (bonus > bestBonus) {
                    bestBonus = bonus;
                    bestAbility = ability;
                }
            }

            if (bestBonus > 0D) {
                var originalDamage = event.getNewDamage();
                var boostedDamage = (float) Math.max(0D, originalDamage * (1D + bestBonus));

                event.setNewDamage(boostedDamage);

                if (bestAbility != null) {
                    var bonusDamage = Math.max(0F, boostedDamage - originalDamage);

                    if (bonusDamage > 0F)
                        bestAbility.getStatisticData().getMetricData("sword_power_bonus_damage").addValue(bonusDamage);
                }
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide())
                return;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SEALED_SWORD.value())) {
                discardSummons(player.getServer(), stack);
                stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
            }
        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide())
                return;
        }

        @SubscribeEvent
        public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide())
                return;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SEALED_SWORD.value())) {
                discardSummons(player.getServer(), stack);
                stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
            }
        }

        static void discardSummons(MinecraftServer server, ItemStack stack) {
            if (server == null)
                return;

            var summons = stack.getOrDefault(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.<RISASDataComponents.SealedWeaponSummonData>of());

            if (summons.isEmpty())
                return;

            for (var summonData : summons) {
                var level = server.getLevel(summonData.level());

                if (level == null)
                    continue;

                if (summonData.summon().getMostSignificantBits() == 0L && summonData.summon().getLeastSignificantBits() == 0L)
                    continue;

                var entity = level.getEntity(summonData.summon());

                if (entity instanceof SummonedSwordEntity summon && summon.isAlive())
                    summon.discard();
            }
        }
    }
}
