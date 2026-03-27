package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.black_hole.BlackHole;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.*;

public class GalaxyDevourerDiademItem extends ISASWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("galaxy_devourer_diadem")
                                .rankModifier(1, "eternal_hunger")
                                .rankModifier(3, "collapse_wave")
                                .rankModifier(5, "singularity_fusion")
                                .stat(AbilityStatTemplate.builder("spawn_chance")
                                        .initialValue(0.05D, 0.15D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.019D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("black_hole_radius")
                                        .initialValue(1D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("black_hole_damage")
                                        .initialValue(1D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("black_hole_lifetime")
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("collapse_radius")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("collapse_damage")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("lifetime_extension")
                                        .initialValue(1D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("fusion_transfer")
                                        .initialValue(0.05D, 0.15D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0381D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("black_hole_created").build())
                                        .source(ExperienceSourceTemplate.builder("black_hole_kill").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("black_holes_created")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("black_hole_damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("black_hole_kills")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("black_holes_absorbed")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("singularity_fusion", VisibilityState.OBFUSCATED)
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

        List<RISASDataComponents.GalaxyDevourerDiademBlackHoleData> entries = stack.getOrDefault(
                RISASDataComponents.GALAXY_DEVOURER_DIADEM_BLACK_HOLES.get(),
                List.of()
        );

        if (entries.isEmpty()) {
            stack.remove(RISASDataComponents.GALAXY_DEVOURER_DIADEM_DAMAGE_DEALT.get());
            return;
        }

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("galaxy_devourer_diadem");
        var gameTime = player.level().getGameTime();
        var server = player.getServer();
        var damageTotals = new HashMap<UUID, Double>();
        var baseLifetimeTicks = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("black_hole_lifetime").getValue()) * 20D));

        for (var entry : stack.getOrDefault(RISASDataComponents.GALAXY_DEVOURER_DIADEM_DAMAGE_DEALT.get(), List.<RISASDataComponents.GalaxyDevourerDiademDamageData>of()))
            damageTotals.merge(entry.blackHole(), Math.max(0D, entry.dealtDamage()), Double::sum);

        if (server == null)
            return;

        var mergedConsumed = new HashSet<UUID>();
        var forcedExpires = new HashMap<UUID, Long>();

        if (ability.canPlayerUse(player) && ability.isRankModifierUnlocked("singularity_fusion")) {
            var fusionTransfer = Mth.clamp(ability.getStatData("fusion_transfer").getValue(), 0D, 1D);

            for (var entry : entries) {
                if (mergedConsumed.contains(entry.blackHole()))
                    continue;

                var level = server.getLevel(entry.level());

                if (level == null)
                    continue;

                var entity = level.getEntity(entry.blackHole());

                if (!(entity instanceof BlackHole hole) || !hole.isAlive())
                    continue;

                for (var candidateEntry : entries) {
                    if (!candidateEntry.level().equals(entry.level()) || candidateEntry.blackHole().equals(entry.blackHole()) || mergedConsumed.contains(candidateEntry.blackHole()))
                        continue;

                    var candidateEntity = level.getEntity(candidateEntry.blackHole());

                    if (!(candidateEntity instanceof BlackHole candidate) || !candidate.isAlive())
                        continue;

                    var holeBox = hole.getBoundingBox();
                    var candidateBox = candidate.getBoundingBox();

                    if (holeBox.maxX < candidateBox.minX || holeBox.minX > candidateBox.maxX
                            || holeBox.maxY < candidateBox.minY || holeBox.minY > candidateBox.maxY
                            || holeBox.maxZ < candidateBox.minZ || holeBox.minZ > candidateBox.maxZ)
                        continue;

                    BlackHole primary = hole;
                    BlackHole secondary = candidate;

                    if (candidate.getRadius() > hole.getRadius()) {
                        primary = candidate;
                        secondary = hole;
                    }

                    var centerBeforeMerge = primary.getBoundingBox().getCenter();
                    primary.setDamage((float) Math.max(0D, primary.getDamage() + secondary.getDamage() * fusionTransfer));
                    primary.setRadius((float) Math.max(0D, primary.getRadius() + secondary.getRadius() * fusionTransfer));
                    var centerAfterMerge = primary.getBoundingBox().getCenter();
                    primary.setPos(
                            primary.getX() + centerBeforeMerge.x - centerAfterMerge.x,
                            primary.getY() + centerBeforeMerge.y - centerAfterMerge.y,
                            primary.getZ() + centerBeforeMerge.z - centerAfterMerge.z
                    );
                    primary.setDuration(primary.tickCount + baseLifetimeTicks);
                    forcedExpires.put(primary.getUUID(), gameTime + baseLifetimeTicks);
                    damageTotals.merge(primary.getUUID(), Math.max(0D, damageTotals.getOrDefault(secondary.getUUID(), 0D)) * fusionTransfer, Double::sum);
                    damageTotals.remove(secondary.getUUID());
                    mergedConsumed.add(secondary.getUUID());
                    ability.getStatisticData().getMetricData("black_holes_absorbed").addValue(1D);
                    secondary.discard();
                    break;
                }
            }
        }

        var updated = new ArrayList<RISASDataComponents.GalaxyDevourerDiademBlackHoleData>(entries.size());

        for (var entry : entries) {
            if (mergedConsumed.contains(entry.blackHole()))
                continue;

            var level = server.getLevel(entry.level());

            if (level == null)
                continue;

            var entity = level.getEntity(entry.blackHole());

            if (entity instanceof BlackHole hole && hole.isAlive()) {
                var center = hole.getBoundingBox().getCenter();
                updated.add(new RISASDataComponents.GalaxyDevourerDiademBlackHoleData(
                        entry.blackHole(),
                        entry.level(),
                        center.x,
                        center.y,
                        center.z,
                        forcedExpires.getOrDefault(entry.blackHole(), entry.expiresAtTick())
                ));

                continue;
            }

            var accumulatedDamage = Math.max(0D, damageTotals.getOrDefault(entry.blackHole(), 0D));
            damageTotals.remove(entry.blackHole());

            if (!ability.canPlayerUse(player) || !ability.isRankModifierUnlocked("collapse_wave") || gameTime + 2L < entry.expiresAtTick())
                continue;

            var collapseRadius = Math.max(0D, ability.getStatData("collapse_radius").getValue());
            var collapseDamageScale = Mth.clamp(ability.getStatData("collapse_damage").getValue(), 0D, 1D);
            var collapseDamage = (float) Math.max(0D, accumulatedDamage * collapseDamageScale);

            if (collapseRadius <= 0D || collapseDamage <= 0F)
                continue;

            var center = new Vec3(entry.x(), entry.y(), entry.z());
            var targets = level.getEntitiesOfClass(
                    LivingEntity.class,
                    new AABB(center.x - collapseRadius, center.y - collapseRadius, center.z - collapseRadius, center.x + collapseRadius, center.y + collapseRadius, center.z + collapseRadius),
                    target -> target != player && target.isAlive() && !DamageSources.isFriendlyFireBetween(player, target)
            );

            for (var target : targets) {
                DamageSources.applyDamage(target, collapseDamage, SpellRegistry.BLACK_HOLE_SPELL.get().getDamageSource(player, player));
                ability.getStatisticData().getMetricData("black_hole_damage_dealt").addValue(collapseDamage);
            }
        }

        if (updated.isEmpty()) {
            stack.remove(RISASDataComponents.GALAXY_DEVOURER_DIADEM_BLACK_HOLES.get());
            stack.remove(RISASDataComponents.GALAXY_DEVOURER_DIADEM_DAMAGE_DEALT.get());
            return;
        }

        stack.set(RISASDataComponents.GALAXY_DEVOURER_DIADEM_BLACK_HOLES.get(), List.copyOf(updated));

        var updatedDamage = new ArrayList<RISASDataComponents.GalaxyDevourerDiademDamageData>(updated.size());

        for (var entry : updated)
            updatedDamage.add(new RISASDataComponents.GalaxyDevourerDiademDamageData(
                    entry.blackHole(),
                    entry.level(),
                    Math.max(0D, damageTotals.getOrDefault(entry.blackHole(), 0D))
            ));

        stack.set(RISASDataComponents.GALAXY_DEVOURER_DIADEM_DAMAGE_DEALT.get(), List.copyOf(updatedDamage));
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (event.getNewDamage() <= 0F || !(event.getEntity().level() instanceof ServerLevel level))
                return;

            if (!(event.getSource().getDirectEntity() instanceof BlackHole hole) || !(hole.getOwner() instanceof ServerPlayer owner))
                return;

            for (var stack : EntityUtils.findEquippedCurios(owner, RISASItems.GALAXY_DEVOURER_DIADEM.value())) {
                if (!(stack.getItem() instanceof GalaxyDevourerDiademItem item))
                    continue;

                var ability = item.getRelicData(owner, stack).getAbilitiesData().getAbilityData("galaxy_devourer_diadem");

                if (!ability.canPlayerUse(owner))
                    continue;

                var tracked = false;

                for (var entry : stack.getOrDefault(RISASDataComponents.GALAXY_DEVOURER_DIADEM_BLACK_HOLES.get(), List.<RISASDataComponents.GalaxyDevourerDiademBlackHoleData>of())) {
                    if (entry.blackHole().equals(hole.getUUID()) && entry.level().equals(level.dimension())) {
                        tracked = true;
                        break;
                    }
                }

                if (!tracked)
                    continue;

                var damageEntries = new ArrayList<RISASDataComponents.GalaxyDevourerDiademDamageData>(
                        stack.getOrDefault(RISASDataComponents.GALAXY_DEVOURER_DIADEM_DAMAGE_DEALT.get(), List.of())
                );
                var updated = false;

                for (var i = 0; i < damageEntries.size(); i++) {
                    var entry = damageEntries.get(i);

                    if (!entry.blackHole().equals(hole.getUUID()) || !entry.level().equals(level.dimension()))
                        continue;

                    damageEntries.set(i, new RISASDataComponents.GalaxyDevourerDiademDamageData(
                            entry.blackHole(),
                            entry.level(),
                            Math.max(0D, entry.dealtDamage()) + event.getNewDamage()
                    ));
                    updated = true;
                    break;
                }

                if (!updated) {
                    damageEntries.add(new RISASDataComponents.GalaxyDevourerDiademDamageData(
                            hole.getUUID(),
                            level.dimension(),
                            event.getNewDamage()
                    ));
                }

                stack.set(RISASDataComponents.GALAXY_DEVOURER_DIADEM_DAMAGE_DEALT.get(), List.copyOf(damageEntries));
                ability.getStatisticData().getMetricData("black_hole_damage_dealt").addValue(event.getNewDamage());
                break;
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            if (event.getEntity().level().isClientSide())
                return;

            if (!(event.getEntity().level() instanceof ServerLevel level))
                return;

            if (event.getSource().getDirectEntity() instanceof BlackHole hole && hole.getOwner() instanceof ServerPlayer owner) {
                for (var stack : EntityUtils.findEquippedCurios(owner, RISASItems.GALAXY_DEVOURER_DIADEM.value())) {
                    if (!(stack.getItem() instanceof GalaxyDevourerDiademItem item))
                        continue;

                    var ability = item.getRelicData(owner, stack).getAbilitiesData().getAbilityData("galaxy_devourer_diadem");

                    if (!ability.canPlayerUse(owner))
                        continue;

                    var tracked = false;

                    for (var entry : stack.getOrDefault(RISASDataComponents.GALAXY_DEVOURER_DIADEM_BLACK_HOLES.get(), List.<RISASDataComponents.GalaxyDevourerDiademBlackHoleData>of())) {
                        if (!entry.level().equals(level.dimension()) || !entry.blackHole().equals(hole.getUUID()))
                            continue;

                        tracked = true;
                        break;
                    }

                    if (!tracked)
                        continue;

                    item.getRelicData(owner, stack).getLevelingData().addExperience("galaxy_devourer_diadem", "black_hole_kill", 1D);
                    ability.getStatisticData().getMetricData("black_hole_kills").addValue(1D);
                    break;
                }

                ItemStack selectedStack = ItemStack.EMPTY;
                AbilityData selectedAbility = null;
                var bestExtension = -1D;

                for (var stack : EntityUtils.findEquippedCurios(owner, RISASItems.GALAXY_DEVOURER_DIADEM.value())) {
                    if (!(stack.getItem() instanceof GalaxyDevourerDiademItem item))
                        continue;

                    var ability = item.getRelicData(owner, stack).getAbilitiesData().getAbilityData("galaxy_devourer_diadem");

                    if (!ability.canPlayerUse(owner) || !ability.isRankModifierUnlocked("eternal_hunger"))
                        continue;

                    var extension = Math.max(0D, ability.getStatData("lifetime_extension").getValue());

                    if (extension <= bestExtension)
                        continue;

                    selectedStack = stack;
                    selectedAbility = ability;
                    bestExtension = extension;
                }

                if (!selectedStack.isEmpty() && selectedAbility != null && bestExtension > 0D) {
                    var extensionTicks = Math.max(1, (int) Math.round(bestExtension * 20D));
                    hole.setDuration(hole.getDuration() + extensionTicks);

                    var entries = new ArrayList<RISASDataComponents.GalaxyDevourerDiademBlackHoleData>(
                            selectedStack.getOrDefault(RISASDataComponents.GALAXY_DEVOURER_DIADEM_BLACK_HOLES.get(), List.of())
                    );

                    for (var i = 0; i < entries.size(); i++) {
                        var entry = entries.get(i);

                        if (!entry.level().equals(level.dimension()) || !entry.blackHole().equals(hole.getUUID()))
                            continue;

                        entries.set(i, new RISASDataComponents.GalaxyDevourerDiademBlackHoleData(
                                entry.blackHole(),
                                entry.level(),
                                hole.getBoundingBox().getCenter().x,
                                hole.getBoundingBox().getCenter().y,
                                hole.getBoundingBox().getCenter().z,
                                entry.expiresAtTick() + extensionTicks
                        ));
                        selectedStack.set(RISASDataComponents.GALAXY_DEVOURER_DIADEM_BLACK_HOLES.get(), List.copyOf(entries));
                        break;
                    }
                }
            }

            if (!(event.getSource().getEntity() instanceof ServerPlayer player))
                return;

            if (!(event.getEntity() instanceof LivingEntity target) || target == player || DamageSources.isFriendlyFireBetween(player, target))
                return;

            ItemStack selectedStack = ItemStack.EMPTY;
            AbilityData selectedAbility = null;
            var bestChance = -1D;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.GALAXY_DEVOURER_DIADEM.value())) {
                if (!(stack.getItem() instanceof GalaxyDevourerDiademItem item))
                    continue;

                var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("galaxy_devourer_diadem");

                if (!ability.canPlayerUse(player))
                    continue;

                var chance = Mth.clamp(ability.getStatData("spawn_chance").getValue(), 0D, 1D);

                if (chance <= bestChance)
                    continue;

                selectedStack = stack;
                selectedAbility = ability;
                bestChance = chance;
            }

            if (selectedStack.isEmpty() || selectedAbility == null || bestChance <= 0D)
                return;

            if (level.getRandom().nextDouble() >= bestChance)
                return;

            var blackHole = new BlackHole(level, player);
            var radius = (float) Math.max(0D, selectedAbility.getStatData("black_hole_radius").getValue());
            var damage = (float) Math.max(0D, selectedAbility.getStatData("black_hole_damage").getValue());
            var lifetimeTicks = Math.max(1, (int) Math.round(Math.max(0D, selectedAbility.getStatData("black_hole_lifetime").getValue()) * 20D));
            var deathCenter = target.getBoundingBox().getCenter();

            if (radius <= 0F || damage <= 0F)
                return;

            blackHole.setRadius(radius);
            blackHole.setDamage(damage);
            blackHole.setDuration(lifetimeTicks);
            blackHole.moveTo(deathCenter.x, deathCenter.y, deathCenter.z);
            var blackHoleCenter = blackHole.getBoundingBox().getCenter();
            blackHole.setPos(
                    blackHole.getX() + deathCenter.x - blackHoleCenter.x,
                    blackHole.getY() + deathCenter.y - blackHoleCenter.y,
                    blackHole.getZ() + deathCenter.z - blackHoleCenter.z
            );
            level.addFreshEntity(blackHole);

            var entries = new ArrayList<RISASDataComponents.GalaxyDevourerDiademBlackHoleData>(
                    selectedStack.getOrDefault(RISASDataComponents.GALAXY_DEVOURER_DIADEM_BLACK_HOLES.get(), List.of())
            );

            entries.removeIf(entry -> entry.blackHole().equals(blackHole.getUUID()) || entry.level().equals(level.dimension()) && level.getEntity(entry.blackHole()) == null);
            entries.add(new RISASDataComponents.GalaxyDevourerDiademBlackHoleData(
                    blackHole.getUUID(),
                    level.dimension(),
                    blackHole.getBoundingBox().getCenter().x,
                    blackHole.getBoundingBox().getCenter().y,
                    blackHole.getBoundingBox().getCenter().z,
                    level.getGameTime() + blackHole.getDuration()
            ));
            selectedStack.set(RISASDataComponents.GALAXY_DEVOURER_DIADEM_BLACK_HOLES.get(), List.copyOf(entries));

            var damageEntries = new ArrayList<RISASDataComponents.GalaxyDevourerDiademDamageData>(
                    selectedStack.getOrDefault(RISASDataComponents.GALAXY_DEVOURER_DIADEM_DAMAGE_DEALT.get(), List.of())
            );

            damageEntries.removeIf(entry -> entry.blackHole().equals(blackHole.getUUID()) || entry.level().equals(level.dimension()) && level.getEntity(entry.blackHole()) == null);
            damageEntries.add(new RISASDataComponents.GalaxyDevourerDiademDamageData(
                    blackHole.getUUID(),
                    level.dimension(),
                    0D
            ));
            selectedStack.set(RISASDataComponents.GALAXY_DEVOURER_DIADEM_DAMAGE_DEALT.get(), List.copyOf(damageEntries));
            ((GalaxyDevourerDiademItem) selectedStack.getItem()).getRelicData(player, selectedStack).getLevelingData().addExperience("galaxy_devourer_diadem", "black_hole_created", 1D);
            selectedAbility.getStatisticData().getMetricData("black_holes_created").addValue(1D);
        }
    }
}
