package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.entity.spells.blood_needle.BloodNeedle;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.List;

public class BloodyVoodooDollItem extends ISASRelic {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("voodoo_mark")
                                .initialMaxLevel(10)
                                .rankModifier(1, "needle_leech")
                                .rankModifier(3, "needle_ricochet")
                                .rankModifier(5, "death_burst")
                                .stat(AbilityStatTemplate.builder("mark_chance")
                                        .initialValue(0.15D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("needle_damage")
                                        .initialValue(0.5D, 1.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("max_needles")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("needle_heal")
                                        .initialValue(0.5D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("ricochet_chance")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("ricochet_range")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("death_burst_needles")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("death_burst_multicast_chance")
                                        .initialValue(0.25D, 0.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0229D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("marked_target").build())
                                        .source(ExperienceSourceTemplate.builder("summoned_needle").build())
                                        .source(ExperienceSourceTemplate.builder("needle_ricochet")
                                                .rankModifierVisibilityState("needle_ricochet", VisibilityState.OBFUSCATED)
                                                .build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("marked_targets")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("needles_summoned")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("needle_damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("needle_leech_healing")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("needle_leech", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("needle_ricochet")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("needle_ricochet", VisibilityState.OBFUSCATED)
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

        var marks = CommonEvents.getMarks(stack);
        var releases = CommonEvents.getNeedleReleases(stack);

        if (marks.isEmpty() && releases.isEmpty())
            return;

        var relicData = this.getRelicData(player, stack);
        var ability = relicData.getAbilitiesData().getAbilityData("voodoo_mark");

        if (!ability.canPlayerUse(player))
            return;

        var server = player.getServer();

        if (server == null)
            return;

        var updated = new ArrayList<RISASDataComponents.VoodooMarkData>(marks.size());
        var changed = false;
        var updatedReleases = new ArrayList<RISASDataComponents.VoodooNeedleReleaseData>(releases.size());
        var releasesChanged = false;

        for (var release : releases) {
            var level = server.getLevel(release.level());

            if (level == null) {
                releasesChanged = true;
                continue;
            }

            var entity = level.getEntity(release.target());

            if (!(entity instanceof LivingEntity target) || !target.isAlive()) {
                releasesChanged = true;
                continue;
            }

            if (level.getGameTime() < release.nextShotTick()) {
                updatedReleases.add(release);
                continue;
            }

            if (release.needleDamage() > 0D) {
                var caster = level.getServer().getPlayerList().getPlayer(release.caster());
                LivingEntity owner = caster != null && caster.isAlive() ? caster : target;
                var center = target.position().add(0D, target.getEyeHeight() / 2F, 0D);
                var offset = new Vec3(0D, level.getRandom().nextDouble(), 0.55D)
                        .normalize()
                        .scale(target.getBbWidth() + 2.75F)
                        .yRot((float) (level.getRandom().nextDouble() * Math.PI * 2D));
                var needlePos = center.add(offset);
                var direction = center.subtract(needlePos).normalize();
                var needle = new BloodNeedle(level, owner);

                needle.moveTo(needlePos);
                needle.shoot(direction.scale(0.35D));
                needle.setDamage((float) release.needleDamage());
                needle.setScale(0.4F);
                needle.getPersistentData().putBoolean("risas_voodoo_needle", true);

                level.addFreshEntity(needle);
                ability.getStatisticData().getMetricData("needles_summoned").addValue(1D);
                relicData.getLevelingData().addExperience("voodoo_mark", "summoned_needle", 1D);
            }

            var remainingNeedles = release.remainingNeedles() - 1;

            if (remainingNeedles > 0) {
                updatedReleases.add(new RISASDataComponents.VoodooNeedleReleaseData(
                        release.target(),
                        release.level(),
                        release.caster(),
                        release.needleDamage(),
                        remainingNeedles,
                        level.getGameTime() + Mth.clamp(10 - remainingNeedles, 2, 8)
                ));
            }

            releasesChanged = true;
        }

        for (var mark : marks) {
            var level = server.getLevel(mark.level());

            if (level == null) {
                updated.add(mark);
                continue;
            }

            var entity = level.getEntity(mark.target());

            if (!(entity instanceof LivingEntity target) || !target.isAlive()) {
                changed = true;
                continue;
            }

            if (mark.expiresAtTick() > level.getGameTime()) {
                updated.add(mark);
                continue;
            }

            var storedDamage = Math.max(0D, mark.storedDamage());
            var needleDamagePerPoint = Math.max(0D, mark.needleDamagePerPoint());

            if (storedDamage > 0D && needleDamagePerPoint > 0D) {
                var totalNeedleDamage = storedDamage * needleDamagePerPoint;
                var maxNeedles = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("max_needles").getValue())));
                var needles = Math.max(1, MathUtils.multicast(level.getRandom(), storedDamage / (storedDamage + 1D), maxNeedles));
                var needleDamage = (float) (totalNeedleDamage / needles);

                updatedReleases.add(new RISASDataComponents.VoodooNeedleReleaseData(
                        mark.target(),
                        mark.level(),
                        mark.caster(),
                        needleDamage,
                        needles,
                        level.getGameTime() + Mth.clamp(10 - needles, 2, 8)
                ));
                releasesChanged = true;

                if (ability.canPlayerUse(player) && totalNeedleDamage > 0D) {

                }
            }

            changed = true;
        }

        if (changed)
            CommonEvents.setMarks(stack, updated);

        if (releasesChanged)
            CommonEvents.setNeedleReleases(stack, updatedReleases);
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onVoodooNeedleIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getSource().getDirectEntity() instanceof BloodNeedle needle))
                return;

            if (!needle.getPersistentData().getBoolean("risas_voodoo_needle"))
                return;

            if (event.getSource() instanceof SpellDamageSource source && source.getLifestealPercent() > 0F)
                source.setLifestealPercent(0F);
        }

        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (!(event.getEntity().level() instanceof ServerLevel level) || event.getNewDamage() <= 0F)
                return;

            var target = event.getEntity();
            var gameTime = level.getGameTime();
            var targetId = target.getUUID();
            var players = level.getServer().getPlayerList().getPlayers();

            for (var player : players) {
                for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.BLOODY_VOODOO_DOLL.value())) {
                    if (!(stack.getItem() instanceof BloodyVoodooDollItem))
                        continue;

                    var marks = getMarks(stack);
                    var releases = getNeedleReleases(stack);

                    if (marks.isEmpty() && releases.isEmpty())
                        continue;

                    var updated = new ArrayList<RISASDataComponents.VoodooMarkData>(marks.size());
                    var changed = false;

                    for (var mark : marks) {
                        if (mark.level().equals(level.dimension()) && mark.expiresAtTick() > gameTime && mark.target().equals(targetId)) {
                            updated.add(mark.withAddedStoredDamage(event.getNewDamage()));
                            changed = true;
                            continue;
                        }

                        updated.add(mark);
                    }

                    if (changed)
                        setMarks(stack, updated);
                }
            }

            var source = event.getSource();

            if (source.getDirectEntity() instanceof BloodNeedle needle
                    && source.getEntity() instanceof ServerPlayer owner
                    && needle.getPersistentData().getBoolean("risas_voodoo_needle")) {
                AbilityData ability = null;
                BloodyVoodooDollItem relic = null;
                ItemStack stack = ItemStack.EMPTY;
                var bestNeedleDamage = -1D;

                for (var candidateStack : EntityUtils.findEquippedCurios(owner, RISASItems.BLOODY_VOODOO_DOLL.value())) {
                    if (!(candidateStack.getItem() instanceof BloodyVoodooDollItem candidateRelic))
                        continue;

                    var candidateAbility = candidateRelic.getRelicData(owner, candidateStack).getAbilitiesData().getAbilityData("voodoo_mark");

                    if (!candidateAbility.canPlayerUse(owner))
                        continue;

                    var needleDamage = Math.max(0D, candidateAbility.getStatData("needle_damage").getValue());

                    if (needleDamage <= bestNeedleDamage)
                        continue;

                    bestNeedleDamage = needleDamage;
                    ability = candidateAbility;
                    relic = candidateRelic;
                    stack = candidateStack;
                }

                if (ability != null) {
                    var relicData = relic.getRelicData(owner, stack);
                    ability.getStatisticData().getMetricData("needle_damage_dealt").addValue(event.getNewDamage());

                    if (ability.isRankModifierUnlocked("needle_leech")) {
                        var heal = Math.max(0D, ability.getStatData("needle_heal").getValue());

                        heal = 1;

                        if (heal > 0D) {
                            var healthBefore = owner.getHealth();
                            owner.heal((float) heal);
                            var healed = Math.max(0D, owner.getHealth() - healthBefore);

                            if (healed > 0D)
                                ability.getStatisticData().getMetricData("needle_leech_healing").addValue(healed);
                        }
                    }

                    if (ability.isRankModifierUnlocked("needle_ricochet")) {
                        var chance = Mth.clamp(ability.getStatData("ricochet_chance").getValue(), 0D, 1D);
                        var range = Math.max(0D, ability.getStatData("ricochet_range").getValue());

                        if (chance > 0D && range > 0D && level.getRandom().nextDouble() < chance) {
                            LivingEntity nearest = null;
                            var nearestDistance = Double.MAX_VALUE;

                            for (var candidate : level.getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(range), candidate -> candidate != target && candidate != owner && candidate.isAlive() && !DamageSources.isFriendlyFireBetween(owner, candidate))) {
                                var distance = candidate.distanceToSqr(target);

                                if (distance >= nearestDistance)
                                    continue;

                                nearestDistance = distance;
                                nearest = candidate;
                            }

                            if (nearest != null) {
                                var from = target.position().add(0D, target.getEyeHeight() / 2F, 0D);
                                var to = nearest.position().add(0D, nearest.getEyeHeight() / 2F, 0D);
                                var direction = to.subtract(from);

                                if (direction.lengthSqr() > 1.0E-6D) {
                                    var bounced = new BloodNeedle(level, owner);

                                    bounced.moveTo(from);
                                    bounced.shoot(direction.normalize().scale(0.45D));
                                    bounced.setDamage(Math.max(0F, needle.getDamage()));
                                    bounced.setScale(needle.getScale());
                                    bounced.getPersistentData().putBoolean("risas_voodoo_needle", true);

                                    level.addFreshEntity(bounced);
                                    ability.getStatisticData().getMetricData("needle_ricochet").addValue(1D);
                                    ability.getStatisticData().getMetricData("needles_summoned").addValue(1D);
                                    relicData.getLevelingData().addExperience("voodoo_mark", "needle_ricochet", 1D);
                                    relicData.getLevelingData().addExperience("voodoo_mark", "summoned_needle", 1D);
                                }
                            }
                        }
                    }
                }
            }

            if (!(source.getEntity() instanceof Player attacker) || attacker == target || source.getDirectEntity() instanceof BloodNeedle)
                return;

            for (var stack : EntityUtils.findEquippedCurios(attacker, RISASItems.BLOODY_VOODOO_DOLL.value())) {
                if (!(stack.getItem() instanceof BloodyVoodooDollItem relic))
                    continue;

                var relicData = relic.getRelicData(attacker, stack);
                var ability = relicData.getAbilitiesData().getAbilityData("voodoo_mark");

                if (!ability.canPlayerUse(attacker))
                    continue;

                var needleDamage = Math.max(0D, ability.getStatData("needle_damage").getValue());
                var marks = new ArrayList<>(getMarks(stack));
                var changed = marks.removeIf(mark -> mark.level().equals(level.dimension()) && mark.expiresAtTick() <= gameTime);
                var refreshed = false;

                for (var index = 0; index < marks.size(); index++) {
                    var mark = marks.get(index);

                    if (!mark.level().equals(level.dimension()) || !mark.target().equals(targetId) || !mark.caster().equals(attacker.getUUID()))
                        continue;

                    marks.set(index, new RISASDataComponents.VoodooMarkData(
                            mark.target(),
                            mark.level(),
                            mark.caster(),
                            gameTime + 60L,
                            needleDamage,
                            Math.max(0D, mark.storedDamage())
                    ));
                    changed = true;
                    refreshed = true;
                    break;
                }

                if (refreshed) {
                    setMarks(stack, marks);
                    continue;
                }

                var chance = Mth.clamp(ability.getStatData("mark_chance").getValue(), 0D, 1D);

                if (chance > 0D && attacker.getRandom().nextDouble() < chance) {
                    marks.add(new RISASDataComponents.VoodooMarkData(
                            targetId,
                            level.dimension(),
                            attacker.getUUID(),
                            gameTime + 60L,
                            needleDamage,
                            Math.max(0D, event.getNewDamage())
                    ));
                    ability.getStatisticData().getMetricData("marked_targets").addValue(1D);
                    relicData.getLevelingData().addExperience("voodoo_mark", "marked_target", 1D);
                    changed = true;
                }

                if (changed)
                    setMarks(stack, marks);
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            if (!(event.getEntity().level() instanceof ServerLevel level))
                return;

            if (event.getSource().getDirectEntity() instanceof BloodNeedle needle
                    && event.getSource().getEntity() instanceof ServerPlayer owner
                    && needle.getPersistentData().getBoolean("risas_voodoo_needle")) {
                AbilityData ability = null;
                BloodyVoodooDollItem relic = null;
                ItemStack stack = ItemStack.EMPTY;
                var bestNeedles = -1D;

                for (var candidateStack : EntityUtils.findEquippedCurios(owner, RISASItems.BLOODY_VOODOO_DOLL.value())) {
                    if (!(candidateStack.getItem() instanceof BloodyVoodooDollItem candidateRelic))
                        continue;

                    var candidateAbility = candidateRelic.getRelicData(owner, candidateStack).getAbilitiesData().getAbilityData("voodoo_mark");

                    if (!candidateAbility.canPlayerUse(owner) || !candidateAbility.isRankModifierUnlocked("death_burst"))
                        continue;

                    var deathBurstNeedles = Math.max(0D, candidateAbility.getStatData("death_burst_needles").getValue());

                    if (deathBurstNeedles <= bestNeedles)
                        continue;

                    bestNeedles = deathBurstNeedles;
                    ability = candidateAbility;
                    relic = candidateRelic;
                    stack = candidateStack;
                }

                if (ability != null && bestNeedles > 0D) {
                    var relicData = relic.getRelicData(owner, stack);
                    var maxNeedles = Math.max(1, (int) Math.round(bestNeedles));
                    var burstNeedles = Math.max(1, MathUtils.multicast(level.getRandom(), Mth.clamp(ability.getStatData("death_burst_multicast_chance").getValue(), 0D, 1D), maxNeedles));
                    var center = event.getEntity().position().add(0D, event.getEntity().getEyeHeight() / 2F, 0D);
                    var damage = Math.max(0F, needle.getDamage());

                    if (damage > 0F) {
                        for (var i = 0; i < burstNeedles; i++) {
                            var angle = level.getRandom().nextDouble() * Math.PI * 2D;
                            var side = 0.2D + level.getRandom().nextDouble() * 0.2D;
                            var direction = new Vec3(
                                    Math.cos(angle) * side,
                                    0.2D + level.getRandom().nextDouble() * 0.2D,
                                    Math.sin(angle) * side
                            );

                            var burst = new BloodNeedle(level, owner);

                            burst.moveTo(center);
                            burst.shoot(direction.normalize().scale(0.18D));
                            burst.setDamage(damage);
                            burst.setScale(needle.getScale());
                            burst.getPersistentData().putBoolean("risas_voodoo_needle", true);

                            level.addFreshEntity(burst);
                            ability.getStatisticData().getMetricData("needles_summoned").addValue(1D);
                            relicData.getLevelingData().addExperience("voodoo_mark", "summoned_needle", 1D);
                        }
                    }

                }
            }

            var target = event.getEntity().getUUID();
            var players = level.getServer().getPlayerList().getPlayers();

            for (var player : players) {
                for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.BLOODY_VOODOO_DOLL.value())) {
                    if (!(stack.getItem() instanceof BloodyVoodooDollItem))
                        continue;

                    var marks = getMarks(stack);
                    var releases = getNeedleReleases(stack);

                    if (marks.isEmpty() && releases.isEmpty())
                        continue;

                    var updated = new ArrayList<RISASDataComponents.VoodooMarkData>(marks.size());
                    var changed = false;

                    for (var mark : marks) {
                        if (mark.target().equals(target)) {
                            changed = true;
                            continue;
                        }

                        updated.add(mark);
                    }

                    if (changed)
                        setMarks(stack, updated);

                    if (releases.isEmpty())
                        continue;

                    var updatedReleases = new ArrayList<RISASDataComponents.VoodooNeedleReleaseData>(releases.size());
                    var releasesChanged = false;

                    for (var release : releases) {
                        if (release.target().equals(target)) {
                            releasesChanged = true;
                            continue;
                        }

                        updatedReleases.add(release);
                    }

                    if (releasesChanged)
                        setNeedleReleases(stack, updatedReleases);
                }
            }
        }

        private static List<RISASDataComponents.VoodooMarkData> getMarks(ItemStack stack) {
            return stack.getOrDefault(RISASDataComponents.BLOODY_VOODOO_DOLL_MARKS.get(), List.of());
        }

        private static void setMarks(ItemStack stack, List<RISASDataComponents.VoodooMarkData> marks) {
            if (marks.isEmpty()) {
                stack.remove(RISASDataComponents.BLOODY_VOODOO_DOLL_MARKS.get());
                return;
            }

            stack.set(RISASDataComponents.BLOODY_VOODOO_DOLL_MARKS.get(), List.copyOf(marks));
        }

        private static List<RISASDataComponents.VoodooNeedleReleaseData> getNeedleReleases(ItemStack stack) {
            return stack.getOrDefault(RISASDataComponents.BLOODY_VOODOO_DOLL_RELEASES.get(), List.of());
        }

        private static void setNeedleReleases(ItemStack stack, List<RISASDataComponents.VoodooNeedleReleaseData> releases) {
            if (releases.isEmpty()) {
                stack.remove(RISASDataComponents.BLOODY_VOODOO_DOLL_RELEASES.get());
                return;
            }

            stack.set(RISASDataComponents.BLOODY_VOODOO_DOLL_RELEASES.get(), List.copyOf(releases));
        }

    }
}
