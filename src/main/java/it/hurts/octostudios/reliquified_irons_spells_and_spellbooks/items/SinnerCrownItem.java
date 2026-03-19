package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.SummonedZombie;
import io.redspace.ironsspellbooks.particle.BlastwaveParticleOptions;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import io.redspace.ironsspellbooks.util.ParticleHelper;
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
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;

public class SinnerCrownItem extends ISASRelic {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("sinner_crown")
                                .initialMaxLevel(10)
                                .rankModifier(1, "summon_lineage")
                                .rankModifier(3, "shared_pain")
                                .rankModifier(5, "sacrificial_end")
                                .stat(AbilityStatTemplate.builder("summon_duration")
                                        .initialValue(10D, 15D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("summon_damage")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("max_summons")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.2095D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("explosion_damage")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("shared_pain_radius")
                                        .initialValue(5D, 7.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("summon_spawn").build())
                                        .source(ExperienceSourceTemplate.builder("summon_kill").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("summons_created")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("summon_damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("shared_pain_damage")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("shared_pain", VisibilityState.OBFUSCATED)
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

        var summons = CommonEvents.getSummons(stack);

        if (summons.isEmpty())
            return;

        var server = player.getServer();

        if (server == null)
            return;

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("sinner_crown");
        var updated = new ArrayList<RISASDataComponents.SinnerCrownSummonData>(summons.size());
        var changed = false;

        for (var summonData : summons) {
            var level = server.getLevel(summonData.level());

            if (level == null) {
                updated.add(summonData);
                continue;
            }

            var entity = level.getEntity(summonData.summon());

            if (entity == null) {
                if (summonData.expiresAtTick() > level.getGameTime())
                    updated.add(summonData);
                else
                    changed = true;

                continue;
            }

            if (!(entity instanceof LivingEntity summon) || !summon.isAlive()) {
                changed = true;
                continue;
            }

            summon.getPersistentData().putBoolean("risas_sinner_crown_summon", true);
            summon.getPersistentData().putUUID("risas_sinner_crown_owner", player.getUUID());
            summon.getPersistentData().putLong("risas_sinner_crown_expires_at", summonData.expiresAtTick());

            if (summonData.expiresAtTick() > level.getGameTime()) {
                updated.add(summonData);
                continue;
            }

            var exploded = ability.canPlayerUse(player) && ability.isRankModifierUnlocked("sacrificial_end");

            if (exploded)
                CommonEvents.explodeSummon(level, summon, player, summonData.explosionDamage());

            summon.discard();
            changed = true;

        }

        if (changed)
            CommonEvents.setSummons(stack, updated);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        if (!(slotContext.entity() instanceof ServerPlayer player) || player.level().isClientSide())
            return;

        CommonEvents.discardOwnedSummons(player);
        CommonEvents.setSummons(stack, List.of());
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            if (!(event.getEntity().level() instanceof ServerLevel level))
                return;

            if (event.getSource().getEntity() instanceof Player killer && event.getEntity() instanceof LivingEntity victim && !(victim instanceof SummonedZombie)) {
                var directEntity = event.getSource().getDirectEntity();
                var width = victim.getBbWidth();
                var height = victim.getBbHeight();

                if (Math.abs(width - 0.6F) <= 0.25F && Math.abs(height - 1.8F) <= 0.5F) {
                    var gameTime = level.getGameTime();

                    for (var stack : EntityUtils.findEquippedCurios(killer, RISASItems.SINNER_CROWN.value())) {
                        if (!(stack.getItem() instanceof SinnerCrownItem crown))
                            continue;

                        var relicData = crown.getRelicData(killer, stack);
                        var ability = relicData.getAbilitiesData().getAbilityData("sinner_crown");

                        if (!ability.canPlayerUse(killer))
                            continue;

                        var server = killer.level().getServer();

                        if (server == null)
                            continue;

                        var summons = new ArrayList<>(getSummons(stack));
                        var cleaned = new ArrayList<RISASDataComponents.SinnerCrownSummonData>(summons.size());

                        for (var summonData : summons) {
                            var summonLevel = server.getLevel(summonData.level());
                            var summonEntity = summonLevel == null ? null : summonLevel.getEntity(summonData.summon());

                            if (summonLevel == null) {
                                cleaned.add(summonData);
                                continue;
                            }

                            if (summonEntity == null) {
                                if (summonData.expiresAtTick() > summonLevel.getGameTime())
                                    cleaned.add(summonData);

                                continue;
                            }

                            if (summonEntity instanceof LivingEntity living && living.isAlive()) {
                                cleaned.add(summonData);
                            }
                        }

                        if (directEntity instanceof SummonedZombie summonKiller) {
                            var ownedByThisCrown = false;

                            for (var summonData : cleaned) {
                                if (summonData.level().equals(level.dimension()) && summonData.summon().equals(summonKiller.getUUID())) {
                                    ownedByThisCrown = true;
                                    break;
                                }
                            }

                            if (ownedByThisCrown)
                                relicData.getLevelingData().addExperience("sinner_crown", "summon_kill", 1D);

                            if (!ownedByThisCrown || !ability.isRankModifierUnlocked("summon_lineage")) {
                                if (cleaned.size() != summons.size())
                                    setSummons(stack, cleaned);

                                continue;
                            }
                        }

                        var maxSummons = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("max_summons").getValue())));

                        if (cleaned.size() >= maxSummons) {
                            setSummons(stack, cleaned);
                            continue;
                        }

                        var summonDurationTicks = Math.max(1L, Math.round(Math.max(0D, ability.getStatData("summon_duration").getValue()) * 20D));
                        var summonDamage = Math.max(0D, ability.getStatData("summon_damage").getValue());
                        var explosionDamage = Math.max(0D, ability.getStatData("explosion_damage").getValue());
                        var summonExpiresAt = gameTime + summonDurationTicks;
                        var summon = new SummonedZombie(level, killer, true);

                        summon.moveTo(victim.getX(), victim.getY(), victim.getZ(), victim.getYRot(), victim.getXRot());
                        summon.setPersistenceRequired();
                        summon.getPersistentData().putBoolean("risas_sinner_crown_summon", true);
                        summon.getPersistentData().putUUID("risas_sinner_crown_owner", killer.getUUID());
                        summon.getPersistentData().putLong("risas_sinner_crown_expires_at", summonExpiresAt);

                        var attackDamage = summon.getAttribute(Attributes.ATTACK_DAMAGE);

                        if (attackDamage != null)
                            attackDamage.setBaseValue(summonDamage);

                        level.addFreshEntity(summon);

                        cleaned.add(new RISASDataComponents.SinnerCrownSummonData(
                                summon.getUUID(),
                                level.dimension(),
                                summonExpiresAt,
                                explosionDamage
                        ));

                        setSummons(stack, cleaned);
                        ability.getStatisticData().getMetricData("summons_created").addValue(1D);
                        relicData.getLevelingData().addExperience("sinner_crown", "summon_spawn", 1D);


                    }
                }
            }

            var deadEntity = event.getEntity();
            var players = level.getServer().getPlayerList().getPlayers();

            for (var player : players) {
                for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SINNER_CROWN.value())) {
                    if (!(stack.getItem() instanceof SinnerCrownItem crown))
                        continue;

                    var summons = getSummons(stack);

                    if (summons.isEmpty())
                        continue;

                    var ability = crown.getRelicData(player, stack).getAbilitiesData().getAbilityData("sinner_crown");
                    var updated = new ArrayList<RISASDataComponents.SinnerCrownSummonData>(summons.size());
                    var changed = false;
                    for (var summonData : summons) {
                        if (!summonData.level().equals(level.dimension()) || !summonData.summon().equals(deadEntity.getUUID())) {
                            updated.add(summonData);
                            continue;
                        }

                        if (ability.canPlayerUse(player) && ability.isRankModifierUnlocked("sacrificial_end")) {
                            explodeSummon(level, deadEntity, player, summonData.explosionDamage());
                        }

                        changed = true;
                    }

                    if (changed) {
                        setSummons(stack, updated);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onSummonDamagePost(net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post event) {
            if (event.getNewDamage() <= 0F)
                return;

            if (!(event.getEntity().level() instanceof ServerLevel level))
                return;

            if (!(event.getSource().getDirectEntity() instanceof SummonedZombie summon))
                return;

            var summonData = summon.getPersistentData();

            if (!summonData.getBoolean("risas_sinner_crown_summon") || !summonData.hasUUID("risas_sinner_crown_owner"))
                return;

            var owner = level.getServer().getPlayerList().getPlayer(summonData.getUUID("risas_sinner_crown_owner"));

            if (owner == null)
                return;

            for (var stack : EntityUtils.findEquippedCurios(owner, RISASItems.SINNER_CROWN.value())) {
                if (!(stack.getItem() instanceof SinnerCrownItem crown))
                    continue;

                var ability = crown.getRelicData(owner, stack).getAbilitiesData().getAbilityData("sinner_crown");

                if (!ability.canPlayerUse(owner))
                    continue;

                var ownsSummon = false;

                for (var data : getSummons(stack)) {
                    if (data.level().equals(level.dimension()) && data.summon().equals(summon.getUUID())) {
                        ownsSummon = true;
                        break;
                    }
                }

                if (!ownsSummon)
                    continue;

                ability.getStatisticData().getMetricData("summon_damage_dealt").addValue(event.getNewDamage());
                break;
            }
        }

        @SubscribeEvent
        public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level) || event.getAmount() <= 0F)
                return;

            var damage = event.getAmount();
            var bestRadius = -1D;
            List<LivingEntity> bestSummons = List.of();
            ItemStack bestStack = ItemStack.EMPTY;
            SinnerCrownItem bestCrown = null;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SINNER_CROWN.value())) {
                if (!(stack.getItem() instanceof SinnerCrownItem crown))
                    continue;

                var ability = crown.getRelicData(player, stack).getAbilitiesData().getAbilityData("sinner_crown");

                if (!ability.canPlayerUse(player) || !ability.isRankModifierUnlocked("shared_pain"))
                    continue;

                var radius = Math.max(0D, ability.getStatData("shared_pain_radius").getValue());

                if (radius <= 0D)
                    continue;

                var summons = getSummons(stack);

                if (summons.isEmpty())
                    continue;

                var nearbySummons = new ArrayList<LivingEntity>();
                var radiusSqr = radius * radius;

                for (var summonData : summons) {
                    if (!summonData.level().equals(level.dimension()))
                        continue;

                    var entity = level.getEntity(summonData.summon());

                    if (entity instanceof LivingEntity living && living.isAlive() && living.distanceToSqr(player) <= radiusSqr)
                        nearbySummons.add(living);
                }

                if (nearbySummons.isEmpty())
                    continue;

                if (radius <= bestRadius)
                    continue;

                bestRadius = radius;
                bestSummons = nearbySummons;
                bestStack = stack;
                bestCrown = crown;
            }

            if (bestSummons.isEmpty())
                return;

            var remainingPlayerDamage = Math.max(0F, damage);
            var remainingSplit = Math.max(0F, damage);

            for (var index = 0; index < bestSummons.size(); index++) {
                var part = index == bestSummons.size() - 1 ? remainingSplit : damage / bestSummons.size();

                remainingSplit -= part;

                if (part <= 0F)
                    continue;

                var summon = bestSummons.get(index);
                var before = Math.max(0F, summon.getHealth() + summon.getAbsorptionAmount());

                if (before <= 0F)
                    continue;

                summon.hurt(event.getSource(), part);

                var after = summon.isAlive() ? Math.max(0F, summon.getHealth() + summon.getAbsorptionAmount()) : 0F;
                var absorbed = Math.min(part, Math.max(0F, before - after));

                remainingPlayerDamage = Math.max(0F, remainingPlayerDamage - absorbed);
            }

            if (remainingPlayerDamage <= 0.0001F) {
                event.setAmount(0F);
                event.setCanceled(true);
            } else {
                event.setAmount(remainingPlayerDamage);
            }

            var distributed = Math.max(0F, damage - remainingPlayerDamage);

            if (distributed > 0F && bestCrown != null && !bestStack.isEmpty()) {
                var ability = bestCrown.getRelicData(player, bestStack).getAbilitiesData().getAbilityData("sinner_crown");

                if (ability.canPlayerUse(player))
                    ability.getStatisticData().getMetricData("shared_pain_damage").addValue(distributed);
            }

        }

        @SubscribeEvent
        public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide())
                return;

            discardOwnedSummons(player);
        }

        @SubscribeEvent
        public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide())
                return;

            discardOwnedSummons(player);
        }

        @SubscribeEvent
        public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
            if (!(event.getLevel() instanceof ServerLevel level) || !(event.getEntity() instanceof SummonedZombie summon))
                return;

            var data = summon.getPersistentData();

            if (!data.getBoolean("risas_sinner_crown_summon"))
                return;

            var expiresAt = data.getLong("risas_sinner_crown_expires_at");

            if (expiresAt > 0L && level.getGameTime() >= expiresAt) {
                summon.discard();
                return;
            }

            if (!data.hasUUID("risas_sinner_crown_owner"))
                return;

            var owner = level.getServer().getPlayerList().getPlayer(data.getUUID("risas_sinner_crown_owner"));

            if (owner == null) {
                summon.discard();
                return;
            }

            if (EntityUtils.findEquippedCurios(owner, RISASItems.SINNER_CROWN.value()).isEmpty())
                summon.discard();
        }

        static void discardOwnedSummons(ServerPlayer player) {
            var server = player.getServer();

            if (server == null)
                return;

            for (var level : server.getAllLevels()) {
                for (var entity : level.getAllEntities()) {
                    if (!(entity instanceof SummonedZombie summon))
                        continue;

                    var data = summon.getPersistentData();

                    if (data.getBoolean("risas_sinner_crown_summon")
                            && data.hasUUID("risas_sinner_crown_owner")
                            && data.getUUID("risas_sinner_crown_owner").equals(player.getUUID()))
                        summon.discard();
                }
            }

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.SINNER_CROWN.value())) {
                if (stack.getItem() instanceof SinnerCrownItem)
                    setSummons(stack, List.of());
            }
        }

        private static List<RISASDataComponents.SinnerCrownSummonData> getSummons(ItemStack stack) {
            return stack.getOrDefault(RISASDataComponents.SINNER_CROWN_SUMMONS.get(), List.of());
        }

        private static void setSummons(ItemStack stack, List<RISASDataComponents.SinnerCrownSummonData> summons) {
            if (summons.isEmpty()) {
                stack.remove(RISASDataComponents.SINNER_CROWN_SUMMONS.get());
                return;
            }

            stack.set(RISASDataComponents.SINNER_CROWN_SUMMONS.get(), List.copyOf(summons));
        }

        private static double explodeSummon(ServerLevel level, LivingEntity summon, LivingEntity player, double explosionDamage) {
            var dealt = 0D;
            var damage = (float) Math.max(0D, explosionDamage);

            MagicManager.spawnParticles(level, ParticleHelper.BLOOD, summon.getX(), summon.getY() + 0.25D, summon.getZ(), 100, 0.03D, 0.4D, 0.03D, 0.4D, true);
            MagicManager.spawnParticles(level, ParticleHelper.BLOOD, summon.getX(), summon.getY() + 0.25D, summon.getZ(), 100, 0.03D, 0.4D, 0.03D, 0.4D, false);
            MagicManager.spawnParticles(level, new BlastwaveParticleOptions(SchoolRegistry.BLOOD.get().getTargetingColor(), 3F), summon.getX(), summon.getBoundingBox().getCenter().y, summon.getZ(), 1, 0D, 0D, 0D, 0D, true);
            level.playSound(null, summon.blockPosition(), SoundRegistry.BLOOD_EXPLOSION.get(), SoundSource.PLAYERS, 3F, Utils.random.nextIntBetweenInclusive(8, 12) * 0.1F);

            if (damage <= 0F)
                return dealt;

            for (var nearby : level.getEntitiesOfClass(LivingEntity.class, summon.getBoundingBox().inflate(3D), nearby -> nearby != summon && nearby.isAlive())) {
                if (DamageSources.applyDamage(nearby, damage, SpellRegistry.SACRIFICE_SPELL.get().getDamageSource(player, player)))
                    dealt += damage;
            }

            return dealt;
        }
    }
}

