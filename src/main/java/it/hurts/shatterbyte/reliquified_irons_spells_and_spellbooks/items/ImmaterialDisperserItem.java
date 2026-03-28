package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.events.CounterSpellEvent;
import io.redspace.ironsspellbooks.api.events.SpellDamageEvent;
import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MultiTargetEntityCastData;
import io.redspace.ironsspellbooks.capabilities.magic.RecastResult;
import io.redspace.ironsspellbooks.capabilities.magic.TargetEntityCastData;
import io.redspace.ironsspellbooks.effect.MagicMobEffect;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.entity.mobs.AntiMagicSusceptible;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.network.casting.SyncTargetingDataPacket;
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
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;

public class ImmaterialDisperserItem extends ISASWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("immaterial_disperser")
                                .rankModifier(1, "mana_refund")
                                .rankModifier(3, "summon_dispel")
                                .rankModifier(5, "retaliation_mark")
                                .stat(AbilityStatTemplate.builder("radius")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("dispel_chance")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("mana_restore")
                                        .initialValue(5D, 25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("retaliation_bonus")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("retaliation_duration")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("spell_dispelled").build())
                                        .source(ExperienceSourceTemplate.builder("summon_dispelled")
                                                .rankModifierVisibilityState("summon_dispel", VisibilityState.OBFUSCATED)
                                                .build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("spells_dispelled")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("mana_refunded")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("mana_refund", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("summons_dispelled")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("summon_dispel", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("retaliation_bonus_damage")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("retaliation_mark", VisibilityState.OBFUSCATED)
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

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("immaterial_disperser");

        if (ability.canPlayerUse(player) && ability.isRankModifierUnlocked("summon_dispel")) {
            var magicData = MagicData.getPlayerMagicData(player);
            var summonsDispelled = 0;

            for (var entity : player.level().getEntitiesOfClass(
                    LivingEntity.class,
                    player.getBoundingBox(),
                    entity -> entity.isAlive()
                            && entity != player
                            && (entity instanceof IMagicSummon || entity instanceof AntiMagicSusceptible)
                            && entity.getBoundingBox().intersects(player.getBoundingBox())
            )) {
                if (entity instanceof AntiMagicSusceptible antiMagicSusceptible)
                    antiMagicSusceptible.onAntiMagic(magicData);

                if (entity.isAlive())
                    entity.discard();

                if (!entity.isAlive())
                    summonsDispelled++;
            }

            if (summonsDispelled > 0) {
                var relicData = this.getRelicData(player, stack);

                relicData.getLevelingData().addExperience("immaterial_disperser", "summon_dispelled", summonsDispelled);
                ability.getStatisticData().getMetricData("summons_dispelled").addValue(summonsDispelled);
            }
        }

        var buffs = stack.getOrDefault(RISASDataComponents.IMMATERIAL_DISPERSER_RETALIATION.get(), List.<RISASDataComponents.ImmaterialDisperserRetaliationData>of());

        if (buffs.isEmpty())
            return;

        var gameTime = player.level().getGameTime();
        var cleaned = new ArrayList<RISASDataComponents.ImmaterialDisperserRetaliationData>(buffs.size());

        for (var buff : buffs) {
            if (buff.expiresAtTick() > gameTime)
                cleaned.add(buff);
        }

        if (cleaned.isEmpty()) {
            stack.remove(RISASDataComponents.IMMATERIAL_DISPERSER_RETALIATION.get());
            return;
        }

        if (cleaned.size() != buffs.size())
            stack.set(RISASDataComponents.IMMATERIAL_DISPERSER_RETALIATION.get(), List.copyOf(cleaned));
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onSpellPreCast(SpellPreCastEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer caster) || caster.level().isClientSide())
                return;

            var spell = SpellRegistry.getSpell(event.getSpellId());

            if (spell == null)
                return;

            if (!(caster.level() instanceof ServerLevel level))
                return;

            if (!spell.canBeInterrupted(caster))
                return;

            var casterMagicData = MagicData.getPlayerMagicData(caster);
            var castData = casterMagicData.getAdditionalCastData();

            if (!(castData instanceof TargetEntityCastData || castData instanceof MultiTargetEntityCastData))
                return;

            for (var player : level.players()) {
                if (!player.isAlive() || player == caster)
                    continue;

                var targetsPlayer = false;

                if (castData instanceof TargetEntityCastData targetData) {
                    var target = targetData.getTarget(level);

                    if (target != null && target.getUUID().equals(player.getUUID()))
                        targetsPlayer = true;
                } else if (castData instanceof MultiTargetEntityCastData multiTargetData) {
                    targetsPlayer = multiTargetData.getTargets().contains(player.getUUID());
                }

                if (!targetsPlayer)
                    continue;

                ImmaterialDisperserItem item = null;
                ItemStack stack = ItemStack.EMPTY;
                var bestRadius = -1D;
                var bestChance = 0D;

                for (var candidateStack : it.hurts.sskirillss.relics.utils.EntityUtils.findEquippedCurios(player, RISASItems.IMMATERIAL_DISPERSER.value())) {
                    if (!(candidateStack.getItem() instanceof ImmaterialDisperserItem candidateItem))
                        continue;

                    var ability = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("immaterial_disperser");

                    if (!ability.canPlayerUse(player))
                        continue;

                    var radius = Math.max(0D, ability.getStatData("radius").getValue());
                    var chance = Math.max(0D, Math.min(1D, ability.getStatData("dispel_chance").getValue()));

                    if (radius <= 0D || caster.distanceToSqr(player) > radius * radius)
                        continue;

                    if (chance <= 0D)
                        continue;

                    if (radius < bestRadius || radius == bestRadius && chance <= bestChance)
                        continue;

                    bestRadius = radius;
                    bestChance = chance;
                    item = candidateItem;
                    stack = candidateStack;
                }

                if (item == null || stack.isEmpty())
                    continue;

                if (level.random.nextDouble() > bestChance)
                    continue;

                if (NeoForge.EVENT_BUS.post(new CounterSpellEvent(player, caster)).isCanceled())
                    continue;

                event.setCanceled(true);

                var playerMagicData = MagicData.getPlayerMagicData(player);

                if (caster instanceof AntiMagicSusceptible antiMagicSusceptible)
                    antiMagicSusceptible.onAntiMagic(playerMagicData);

                Utils.serverSideCancelCast(caster, true);
                casterMagicData.getPlayerRecasts().removeAll(RecastResult.COUNTERSPELL);
                casterMagicData.resetAdditionalCastData();
                PacketDistributor.sendToPlayer(caster, new SyncTargetingDataPacket(spell, List.of()));

                for (var effect : caster.getActiveEffectsMap().keySet().stream().toList()) {
                    if (effect.value() instanceof MagicMobEffect)
                        caster.removeEffect(effect);
                }

                var relicData = item.getRelicData(player, stack);
                var ability = relicData.getAbilitiesData().getAbilityData("immaterial_disperser");

                relicData.getLevelingData().addExperience("immaterial_disperser", "spell_dispelled", 1D);
                ability.getStatisticData().getMetricData("spells_dispelled").addValue(1D);

                if (ability.isRankModifierUnlocked("mana_refund")) {
                    var manaRestore = Math.max(0D, ability.getStatData("mana_restore").getValue());

                    if (manaRestore > 0D) {
                        var manaBefore = Math.max(0D, playerMagicData.getMana());
                        playerMagicData.addMana((float) manaRestore);
                        PacketDistributor.sendToPlayer(player, new SyncManaPacket(playerMagicData));
                        ability.getStatisticData().getMetricData("mana_refunded").addValue(Math.max(0D, playerMagicData.getMana() - manaBefore));
                    }
                }

                if (!ability.isRankModifierUnlocked("retaliation_mark"))
                    return;

                var durationTicks = Math.max(0L, Math.round(Math.max(0D, ability.getStatData("retaliation_duration").getValue()) * 20D));
                var bonus = Math.max(0D, ability.getStatData("retaliation_bonus").getValue());

                if (durationTicks <= 0L || bonus <= 0D)
                    return;

                var gameTime = level.getGameTime();
                var entries = new ArrayList<>(stack.getOrDefault(RISASDataComponents.IMMATERIAL_DISPERSER_RETALIATION.get(), List.of()));

                entries.removeIf(data -> data.expiresAtTick() <= gameTime || data.target().equals(caster.getUUID()) && data.level().equals(level.dimension()));
                entries.add(new RISASDataComponents.ImmaterialDisperserRetaliationData(caster.getUUID(), level.dimension(), gameTime + durationTicks, bonus));
                stack.set(RISASDataComponents.IMMATERIAL_DISPERSER_RETALIATION.get(), List.copyOf(entries));
                return;
            }
        }

        @SubscribeEvent
        public static void onSpellDamage(SpellDamageEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getAmount() <= 0F)
                return;

            var spellDamageSource = event.getSpellDamageSource();

            if (spellDamageSource == null || spellDamageSource.spell() == null)
                return;

            var sourceEntity = spellDamageSource.getEntity();
            var directEntity = spellDamageSource.getDirectEntity();
            LivingEntity source = null;

            if (sourceEntity instanceof LivingEntity sourceLiving && sourceLiving.isAlive() && sourceLiving != player)
                source = sourceLiving;
            else if (directEntity instanceof LivingEntity sourceLiving && sourceLiving.isAlive() && sourceLiving != player)
                source = sourceLiving;

            var threatEntity = directEntity != null ? directEntity : sourceEntity;

            if (threatEntity == null || !threatEntity.isAlive() || threatEntity == player)
                return;

            ImmaterialDisperserItem item = null;
            ItemStack stack = ItemStack.EMPTY;
            var bestRadius = -1D;
            var bestChance = 0D;

            for (var candidateStack : it.hurts.sskirillss.relics.utils.EntityUtils.findEquippedCurios(player, RISASItems.IMMATERIAL_DISPERSER.value())) {
                if (!(candidateStack.getItem() instanceof ImmaterialDisperserItem candidateItem))
                    continue;

                var ability = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("immaterial_disperser");

                if (!ability.canPlayerUse(player))
                    continue;

                var radius = Math.max(0D, ability.getStatData("radius").getValue());
                var chance = Math.max(0D, Math.min(1D, ability.getStatData("dispel_chance").getValue()));

                if (radius <= 0D || threatEntity.distanceToSqr(player) > radius * radius)
                    continue;

                if (chance <= 0D)
                    continue;

                if (radius < bestRadius || radius == bestRadius && chance <= bestChance)
                    continue;

                bestRadius = radius;
                bestChance = chance;
                item = candidateItem;
                stack = candidateStack;
            }

            if (item == null || stack.isEmpty() || player.level().getRandom().nextDouble() > bestChance)
                return;

            var counterTarget = source != null ? source : threatEntity;

            if (NeoForge.EVENT_BUS.post(new CounterSpellEvent(player, counterTarget)).isCanceled())
                return;

            event.setCanceled(true);
            event.setAmount(0F);

            var playerMagicData = MagicData.getPlayerMagicData(player);

            if (counterTarget instanceof AntiMagicSusceptible antiMagicSusceptible)
                antiMagicSusceptible.onAntiMagic(playerMagicData);

            if (counterTarget instanceof ServerPlayer sourcePlayer) {
                Utils.serverSideCancelCast(sourcePlayer, true);
                var sourceMagicData = MagicData.getPlayerMagicData(sourcePlayer);
                sourceMagicData.getPlayerRecasts().removeAll(RecastResult.COUNTERSPELL);
                sourceMagicData.resetAdditionalCastData();
                PacketDistributor.sendToPlayer(sourcePlayer, new SyncTargetingDataPacket(spellDamageSource.spell(), List.of()));
            } else if (counterTarget instanceof IMagicEntity magicEntity) {
                magicEntity.cancelCast();
            }

            if (counterTarget instanceof LivingEntity livingSource) {
                for (var effect : livingSource.getActiveEffectsMap().keySet().stream().toList()) {
                    if (effect.value() instanceof MagicMobEffect)
                        livingSource.removeEffect(effect);
                }
            }

            var relicData = item.getRelicData(player, stack);
            var ability = relicData.getAbilitiesData().getAbilityData("immaterial_disperser");

            relicData.getLevelingData().addExperience("immaterial_disperser", "spell_dispelled", 1D);
            ability.getStatisticData().getMetricData("spells_dispelled").addValue(1D);

            if (ability.isRankModifierUnlocked("mana_refund")) {
                var manaRestore = Math.max(0D, ability.getStatData("mana_restore").getValue());

                if (manaRestore > 0D) {
                    var manaBefore = Math.max(0D, playerMagicData.getMana());
                    playerMagicData.addMana((float) manaRestore);
                    PacketDistributor.sendToPlayer(player, new SyncManaPacket(playerMagicData));
                    ability.getStatisticData().getMetricData("mana_refunded").addValue(Math.max(0D, playerMagicData.getMana() - manaBefore));
                }
            }

            if (!ability.isRankModifierUnlocked("retaliation_mark") || source == null)
                return;

            var durationTicks = Math.max(0L, Math.round(Math.max(0D, ability.getStatData("retaliation_duration").getValue()) * 20D));
            var bonus = Math.max(0D, ability.getStatData("retaliation_bonus").getValue());

            if (durationTicks <= 0L || bonus <= 0D)
                return;

            var gameTime = player.level().getGameTime();
            var levelKey = player.level().dimension();
            var sourceId = source.getUUID();
            var entries = new ArrayList<>(stack.getOrDefault(RISASDataComponents.IMMATERIAL_DISPERSER_RETALIATION.get(), List.of()));

            entries.removeIf(data -> data.expiresAtTick() <= gameTime || data.target().equals(sourceId) && data.level().equals(levelKey));
            entries.add(new RISASDataComponents.ImmaterialDisperserRetaliationData(sourceId, levelKey, gameTime + durationTicks, bonus));
            stack.set(RISASDataComponents.IMMATERIAL_DISPERSER_RETALIATION.get(), List.copyOf(entries));
        }

        @SubscribeEvent
        public static void onPlayerDamagedBySummonMelee(LivingDamageEvent.Pre event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getNewDamage() <= 0F)
                return;

            LivingEntity attacker = null;
            var directEntity = event.getSource().getDirectEntity();
            var sourceEntity = event.getSource().getEntity();

            if (directEntity instanceof LivingEntity directLiving && directLiving.isAlive())
                attacker = directLiving;
            else if (sourceEntity instanceof LivingEntity sourceLiving && sourceLiving.isAlive())
                attacker = sourceLiving;

            if (attacker == null || attacker == player)
                return;

            if (!(attacker instanceof IMagicSummon || attacker instanceof AntiMagicSusceptible))
                return;

            if (directEntity != attacker)
                return;

            for (var stack : it.hurts.sskirillss.relics.utils.EntityUtils.findEquippedCurios(player, RISASItems.IMMATERIAL_DISPERSER.value())) {
                if (!(stack.getItem() instanceof ImmaterialDisperserItem item))
                    continue;

                var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("immaterial_disperser");

                if (!ability.canPlayerUse(player) || !ability.isRankModifierUnlocked("summon_dispel"))
                    continue;

                var magicData = MagicData.getPlayerMagicData(player);

                if (attacker instanceof AntiMagicSusceptible antiMagicSusceptible)
                    antiMagicSusceptible.onAntiMagic(magicData);

                if (attacker.isAlive())
                    attacker.discard();

                if (!attacker.isAlive()) {
                    var relicData = item.getRelicData(player, stack);

                    relicData.getLevelingData().addExperience("immaterial_disperser", "summon_dispelled", 1D);
                    ability.getStatisticData().getMetricData("summons_dispelled").addValue(1D);
                }

                return;
            }
        }

        @SubscribeEvent
        public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getEntity() == player || event.getNewDamage() <= 0F)
                return;

            var gameTime = player.level().getGameTime();
            var targetId = event.getEntity().getUUID();
            var bestBonus = 0D;
            AbilityData bestAbility = null;

            for (var stack : it.hurts.sskirillss.relics.utils.EntityUtils.findEquippedCurios(player, RISASItems.IMMATERIAL_DISPERSER.value())) {
                if (!(stack.getItem() instanceof ImmaterialDisperserItem item))
                    continue;

                var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("immaterial_disperser");

                if (!ability.canPlayerUse(player) || !ability.isRankModifierUnlocked("retaliation_mark"))
                    continue;

                var entries = new ArrayList<>(stack.getOrDefault(RISASDataComponents.IMMATERIAL_DISPERSER_RETALIATION.get(), List.of()));

                if (entries.isEmpty())
                    continue;

                entries.removeIf(data -> data.expiresAtTick() <= gameTime);

                if (entries.isEmpty()) {
                    stack.remove(RISASDataComponents.IMMATERIAL_DISPERSER_RETALIATION.get());
                    continue;
                }

                stack.set(RISASDataComponents.IMMATERIAL_DISPERSER_RETALIATION.get(), List.copyOf(entries));

                for (var entry : entries) {
                    if (!entry.level().equals(player.level().dimension()) || !entry.target().equals(targetId))
                        continue;

                    if (entry.bonus() > bestBonus) {
                        bestBonus = entry.bonus();
                        bestAbility = ability;
                    }
                }
            }

            if (bestBonus <= 0D)
                return;

            var baseDamage = Math.max(0D, event.getNewDamage());
            var newDamage = Math.max(0D, baseDamage * (1D + bestBonus));

            event.setNewDamage((float) newDamage);

            if (bestAbility != null && newDamage > baseDamage)
                bestAbility.getStatisticData().getMetricData("retaliation_bonus_damage").addValue(newDamage - baseDamage);
        }
    }
}
