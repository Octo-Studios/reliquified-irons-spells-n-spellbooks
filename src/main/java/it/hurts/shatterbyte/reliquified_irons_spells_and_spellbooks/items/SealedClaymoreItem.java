package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.capabilities.magic.SummonManager;
import io.redspace.ironsspellbooks.capabilities.magic.SummonedEntitiesCastData;
import io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedClaymoreEntity;
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
import it.hurts.sskirillss.relics.init.RelicsMobEffects;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SealedClaymoreItem extends ISASWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("sealed_claymore")
                                .rankModifier(1, "absorption_strike")
                                .rankModifier(3, "stunning_strike")
                                .rankModifier(5, "claymore_guard_aura")
                                .modes("enabled", "disabled")
                                .stat(AbilityStatTemplate.builder("summon_count")
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("summon_health")
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("summon_damage")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("respawn_time")
                                        .initialValue(60D, 50D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.02285D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("summon_knockback")
                                        .initialValue(0D, 0.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("summon_regen_interval")
                                        .initialValue(10D, 7.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.019D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("summon_regen_amount")
                                        .initialValue(0D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("claymore_taunt_duration")
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("claymore_taunt_cooldown")
                                        .initialValue(25D, 10D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.02285D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("claymore_taunt_radius")
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("claymore_taunt_min_targets")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("absorption_gain")
                                        .initialValue(0.5D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("absorption_cap")
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("stun_chance")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("stun_duration")
                                        .initialValue(0.5D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("aura_radius")
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("damage_reduction_per_claymore")
                                        .initialValue(0.01D, 0.05D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 1))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("summoned_claymore_attack").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("claymore_damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("absorption_strike_absorbed")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("absorption_strike", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("stunning_strike_duration")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("stunning_strike", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("claymore_guard_aura_damage_reduced")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("claymore_guard_aura", VisibilityState.OBFUSCATED)
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
            updateClaymoreAbsorptionCap(player);
            return;
        }

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("sealed_claymore");

        if (!ability.canPlayerUse(player) || "disabled".equals(ability.getMode())) {
            CommonEvents.discardSummons(player.getServer(), stack);
            stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
            updateClaymoreAbsorptionCap(player);
            return;
        }

        var summonCount = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("summon_count").getValue())));
        var summonHealth = Math.max(1D, ability.getStatData("summon_health").getValue());
        var summonDamage = Math.max(0D, ability.getStatData("summon_damage").getValue());
        var respawnDelayTicks = Math.max(1L, Math.round(Math.max(0D, ability.getStatData("respawn_time").getValue()) * 20D));
        var summonKnockback = Math.max(0D, ability.getStatData("summon_knockback").getValue());
        var summonRegenIntervalTicks = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("summon_regen_interval").getValue()) * 20D));
        var summonRegenAmount = Math.max(0D, ability.getStatData("summon_regen_amount").getValue());
        var claymoreTauntDurationTicks = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("claymore_taunt_duration").getValue()) * 20D));
        var claymoreTauntCooldownTicks = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("claymore_taunt_cooldown").getValue()) * 20D));
        var claymoreTauntRadius = Math.max(0D, ability.getStatData("claymore_taunt_radius").getValue());
        var claymoreTauntVerticalRadius = claymoreTauntRadius * 0.5D;
        var claymoreTauntMinTargets = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("claymore_taunt_min_targets").getValue())));
        var gameTime = player.level().getGameTime();

        var summons = new ArrayList<RISASDataComponents.SealedWeaponSummonData>(
                stack.getOrDefault(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.of())
        );
        var reset = summons.size() != summonCount;

        if (!reset) {
            for (var summonData : summons) {
                if (summonData.weaponType() != 2) {
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
                        2,
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

            if (entity instanceof SummonedClaymoreEntity summon && summon.isAlive() && SummonManager.getOwner(summon) == player) {
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
                tuning.risas$setClaymoreTauntDurationTicks(claymoreTauntDurationTicks);
                tuning.risas$setClaymoreTauntCooldownTicks(claymoreTauntCooldownTicks);
                tuning.risas$setClaymoreTauntRadiusHorizontal(claymoreTauntRadius);
                tuning.risas$setClaymoreTauntRadiusVertical(claymoreTauntVerticalRadius);
                tuning.risas$setClaymoreTauntMinTargets(claymoreTauntMinTargets);

                updated.add(new RISASDataComponents.SealedWeaponSummonData(
                        2,
                        summon.getUUID(),
                        summon.level().dimension(),
                        0L
                ));
                continue;
            }

            if (summonData.summon().getMostSignificantBits() != 0L || summonData.summon().getLeastSignificantBits() != 0L) {
                if (summonData.respawnAtTick() <= 0L) {
                    updated.add(new RISASDataComponents.SealedWeaponSummonData(
                            2,
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
                        2,
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

            var summon = new SummonedClaymoreEntity(player.level(), player);
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
            tuning.risas$setClaymoreTauntDurationTicks(claymoreTauntDurationTicks);
            tuning.risas$setClaymoreTauntCooldownTicks(claymoreTauntCooldownTicks);
            tuning.risas$setClaymoreTauntRadiusHorizontal(claymoreTauntRadius);
            tuning.risas$setClaymoreTauntRadiusVertical(claymoreTauntVerticalRadius);
            tuning.risas$setClaymoreTauntMinTargets(claymoreTauntMinTargets);

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
                    2,
                    summon.getUUID(),
                    player.level().dimension(),
                    0L
            ));
        }

        if (updated.isEmpty())
            stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
        else
            stack.set(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.copyOf(updated));

        updateClaymoreAbsorptionCap(player);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer player) || player.level().isClientSide())
            return;

        if (newStack.getItem() instanceof SealedClaymoreItem) {
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

            updateClaymoreAbsorptionCap(player);
            return;
        }

        CommonEvents.discardSummons(player.getServer(), stack);
        stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
        stack.remove(RISASDataComponents.SEALED_WEAPON_INSTANCE.get());
        updateClaymoreAbsorptionCap(player);

        var hasActiveClaymoreAbsorption = false;

        for (var equippedStack : EntityUtils.findEquippedCurios(player, RISASItems.SEALED_CLAYMORE.value())) {
            if (!(equippedStack.getItem() instanceof SealedClaymoreItem equippedItem))
                continue;

            var equippedAbility = equippedItem.getRelicData(player, equippedStack).getAbilitiesData().getAbilityData("sealed_claymore");

            if (!equippedAbility.canPlayerUse(player) || "disabled".equals(equippedAbility.getMode()) || !equippedAbility.isRankModifierUnlocked("absorption_strike"))
                continue;

            if (Math.max(0D, equippedAbility.getStatData("absorption_cap").getValue()) > 0D) {
                hasActiveClaymoreAbsorption = true;
                break;
            }
        }

        if (!hasActiveClaymoreAbsorption)
            player.setAbsorptionAmount(0F);
    }

    private static void updateClaymoreAbsorptionCap(ServerPlayer player) {
        var modifierId = ResourceLocation.fromNamespaceAndPath(
                ReliquifiedIronsSpellsAndSpellbooks.MODID,
                "sealed_claymore_absorption_cap"
        );
        EntityUtils.removeAttribute(player, Attributes.MAX_ABSORPTION, AttributeModifier.Operation.ADD_VALUE, modifierId);

        var cap = 0D;

        for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SEALED_CLAYMORE.value())) {
            if (!(stack.getItem() instanceof SealedClaymoreItem item))
                continue;

            var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("sealed_claymore");

            if (!ability.canPlayerUse(player) || "disabled".equals(ability.getMode()) || !ability.isRankModifierUnlocked("absorption_strike"))
                continue;

            cap = Math.max(cap, Math.max(0D, ability.getStatData("absorption_cap").getValue()));
        }

        if (cap > 0D)
            EntityUtils.resetAttribute(player, Attributes.MAX_ABSORPTION, (float) cap, AttributeModifier.Operation.ADD_VALUE, modifierId);

        if (player.getAbsorptionAmount() > player.getMaxAbsorption())
            player.setAbsorptionAmount(player.getMaxAbsorption());
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (event.getNewDamage() <= 0F)
                return;

            if (!(event.getSource().getDirectEntity() instanceof SummonedClaymoreEntity summon))
                return;

            if (!(SummonManager.getOwner(summon) instanceof ServerPlayer player))
                return;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SEALED_CLAYMORE.value())) {
                if (!(stack.getItem() instanceof SealedClaymoreItem item))
                    continue;

                var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("sealed_claymore");

                if (!ability.canPlayerUse(player) || "disabled".equals(ability.getMode()))
                    continue;

                var summonTracked = false;

                for (var summonData : stack.getOrDefault(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.<RISASDataComponents.SealedWeaponSummonData>of())) {
                    if (summonData.weaponType() == 2 && summonData.summon().equals(summon.getUUID())) {
                        summonTracked = true;
                        break;
                    }
                }

                if (!summonTracked)
                    continue;

                var relicData = item.getRelicData(player, stack);

                relicData.getLevelingData().addExperience("sealed_claymore", "summoned_claymore_attack", 1D);
                ability.getStatisticData().getMetricData("claymore_damage_dealt").addValue(event.getNewDamage());

                if (ability.isRankModifierUnlocked("absorption_strike")) {
                    var gain = Math.max(0D, ability.getStatData("absorption_gain").getValue());
                    var cap = Math.max(0D, ability.getStatData("absorption_cap").getValue());

                    if (gain > 0D && cap > 0D) {
                        updateClaymoreAbsorptionCap(player);
                        var absorptionBefore = player.getAbsorptionAmount();
                        var absorptionAfter = (float) Math.min(Math.min(cap, player.getMaxAbsorption()), Math.max(0D, player.getAbsorptionAmount() + gain));

                        player.setAbsorptionAmount(absorptionAfter);
                        ability.getStatisticData().getMetricData("absorption_strike_absorbed").addValue(Math.max(0F, absorptionAfter - absorptionBefore));
                    }
                }

                if (ability.isRankModifierUnlocked("stunning_strike") && event.getEntity() instanceof LivingEntity target) {
                    var chance = Math.max(0D, Math.min(1D, ability.getStatData("stun_chance").getValue()));
                    var stunTicks = Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("stun_duration").getValue()) * 20D));

                    if (chance > 0D && stunTicks > 0 && target.getRandom().nextDouble() <= chance) {
                        var before = target.getEffect(RelicsMobEffects.STUN);
                        var beforeDuration = before == null ? 0 : Math.max(0, before.getDuration());
                        target.addEffect(new MobEffectInstance(
                                RelicsMobEffects.STUN,
                                stunTicks,
                                0,
                                false,
                                true,
                                true
                        ), player);
                        var after = target.getEffect(RelicsMobEffects.STUN);
                        var afterDuration = after == null ? 0 : Math.max(0, after.getDuration());
                        var addedDuration = Math.max(0, afterDuration - beforeDuration);

                        if (addedDuration > 0)
                            ability.getStatisticData().getMetricData("stunning_strike_duration").addValue(addedDuration / 20D);
                    }
                }

                break;
            }
        }

        @SubscribeEvent
        public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide() || event.getAmount() <= 0F)
                return;

            var bestReduction = 0D;
            SealedClaymoreItem bestItem = null;
            ItemStack bestStack = ItemStack.EMPTY;
            var server = player.getServer();

            if (server == null)
                return;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SEALED_CLAYMORE.value())) {
                if (!(stack.getItem() instanceof SealedClaymoreItem item))
                    continue;

                var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("sealed_claymore");

                if (!ability.canPlayerUse(player) || "disabled".equals(ability.getMode()) || !ability.isRankModifierUnlocked("claymore_guard_aura"))
                    continue;

                var radius = Math.max(0D, ability.getStatData("aura_radius").getValue());
                var perClaymore = Math.max(0D, ability.getStatData("damage_reduction_per_claymore").getValue());

                if (radius <= 0D || perClaymore <= 0D)
                    continue;

                var count = 0;
                var radiusSqr = radius * radius;

                for (var summonData : stack.getOrDefault(RISASDataComponents.SEALED_WEAPON_SUMMONS.get(), List.<RISASDataComponents.SealedWeaponSummonData>of())) {
                    if (summonData.weaponType() != 2)
                        continue;

                    if (summonData.summon().getMostSignificantBits() == 0L && summonData.summon().getLeastSignificantBits() == 0L)
                        continue;

                    var level = server.getLevel(summonData.level());

                    if (level == null)
                        continue;

                    var entity = level.getEntity(summonData.summon());

                    if (!(entity instanceof SummonedClaymoreEntity summonedClaymore) || !summonedClaymore.isAlive())
                        continue;

                    if (summonedClaymore.level() != player.level())
                        continue;

                    if (summonedClaymore.distanceToSqr(player) <= radiusSqr)
                        count++;
                }

                var reduction = count * perClaymore;

                if (reduction > bestReduction) {
                    bestReduction = reduction;
                    bestItem = item;
                    bestStack = stack;
                }
            }

            if (bestReduction > 0D) {
                var reduction = Math.max(0D, Math.min(1D, bestReduction));
                var originalDamage = event.getAmount();
                var reducedDamage = (float) Math.max(0D, originalDamage * (1D - reduction));

                event.setAmount(reducedDamage);

                if (bestItem != null && reducedDamage < originalDamage) {
                    var ability = bestItem.getRelicData(player, bestStack).getAbilitiesData().getAbilityData("sealed_claymore");
                    ability.getStatisticData().getMetricData("claymore_guard_aura_damage_reduced").addValue(originalDamage - reducedDamage);
                }
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide())
                return;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SEALED_CLAYMORE.value())) {
                discardSummons(player.getServer(), stack);
                stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
            }

            EntityUtils.removeAttribute(
                    player,
                    Attributes.MAX_ABSORPTION,
                    AttributeModifier.Operation.ADD_VALUE,
                    ResourceLocation.fromNamespaceAndPath(ReliquifiedIronsSpellsAndSpellbooks.MODID, "sealed_claymore_absorption_cap")
            );
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

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SEALED_CLAYMORE.value())) {
                discardSummons(player.getServer(), stack);
                stack.remove(RISASDataComponents.SEALED_WEAPON_SUMMONS.get());
            }

            EntityUtils.removeAttribute(
                    player,
                    Attributes.MAX_ABSORPTION,
                    AttributeModifier.Operation.ADD_VALUE,
                    ResourceLocation.fromNamespaceAndPath(ReliquifiedIronsSpellsAndSpellbooks.MODID, "sealed_claymore_absorption_cap")
            );
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

                if (entity instanceof SummonedClaymoreEntity summon && summon.isAlive())
                    summon.discard();
            }
        }
    }
}
