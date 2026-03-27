package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.entity.spells.devour_jaw.DevourJaw;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASItems;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASDataComponents;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.base.ISASRelicItem;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class LivingFleshItem extends ISASWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("living_flesh")
                                .rankModifier(1, "gluttonous_devour")
                                .rankModifier(3, "vital_feast")
                                .rankModifier(5, "insatiable_echo")
                                .stat(AbilityStatTemplate.builder("mark_duration")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("devour_damage")
                                        .initialValue(5D, 7.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0476D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("devour_food")
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("devour_saturation")
                                        .initialValue(0.15D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("devour_heal")
                                        .initialValue(1D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("repeat_chance")
                                        .initialValue(0.05D, 0.15D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0381D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("repeat_limit")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> Math.max(0, (int) MathUtils.round(value, 0)))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("ability_trigger").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("ability_triggers")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("devour_damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("devour_food_restored")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("gluttonous_devour", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("devour_healing_restored")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("vital_feast", VisibilityState.OBFUSCATED)
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
        var scheduledDevours = CommonEvents.getScheduledDevours(stack);

        if (marks.isEmpty() && scheduledDevours.isEmpty())
            return;

        var server = player.getServer();

        if (server == null)
            return;

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("living_flesh");
        var updated = new ArrayList<RISASDataComponents.LivingFleshMarkData>(marks.size());
        var changed = false;
        var devouredTargets = new HashSet<UUID>();

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

            if (mark.expiresAtTick() <= level.getGameTime()) {
                changed = true;
                continue;
            }

            if (!mark.caster().equals(player.getUUID())) {
                changed = true;
                continue;
            }

            if (!mark.level().equals(player.level().dimension()) || target.distanceToSqr(player) > 256D) {
                if (!devouredTargets.add(mark.target())) {
                    changed = true;
                    continue;
                }

                var caster = level.getServer().getPlayerList().getPlayer(mark.caster());
                LivingEntity owner = caster != null && caster.isAlive() && caster.level() == level ? caster : target;
                var devourDamage = (float) Math.max(0D, mark.devourDamage());

                if (devourDamage > 0F) {
                    var jaw = new DevourJaw(level, owner, target);
                    var foodPerHit = ability.isRankModifierUnlocked("gluttonous_devour") ? Math.max(0D, ability.getStatData("devour_food").getValue()) : 0D;
                    var saturationPerHit = ability.isRankModifierUnlocked("gluttonous_devour") ? Math.max(0D, ability.getStatData("devour_saturation").getValue()) : 0D;
                    var healPerHit = ability.isRankModifierUnlocked("vital_feast") ? Math.max(0D, ability.getStatData("devour_heal").getValue()) : 0D;
                    var repeatChance = ability.isRankModifierUnlocked("insatiable_echo") ? Mth.clamp(ability.getStatData("repeat_chance").getValue(), 0D, 1D) : 0D;
                    var repeatLimit = ability.isRankModifierUnlocked("insatiable_echo") ? Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("repeat_limit").getValue()))) : 0;

                    jaw.setPos(target.position());
                    jaw.setYRot(owner.getYRot());
                    jaw.setDamage(devourDamage);
                    jaw.getPersistentData().putBoolean("risas_living_flesh_devour", true);
                    jaw.getPersistentData().putBoolean("risas_living_flesh_repeatable", true);
                    jaw.getPersistentData().putDouble("risas_living_flesh_food", foodPerHit);
                    jaw.getPersistentData().putDouble("risas_living_flesh_saturation", saturationPerHit);
                    jaw.getPersistentData().putDouble("risas_living_flesh_heal", healPerHit);
                    jaw.getPersistentData().putDouble("risas_living_flesh_repeat_chance", repeatChance);
                    jaw.getPersistentData().putInt("risas_living_flesh_repeat_limit", repeatLimit);

                    level.addFreshEntity(jaw);
                }

                if (ability.canPlayerUse(player)) {

                }

                changed = true;
                continue;
            }

            updated.add(mark);
        }

        if (changed)
            CommonEvents.setMarks(stack, updated);

        if (scheduledDevours.isEmpty())
            return;

        var updatedScheduledDevours = new ArrayList<RISASDataComponents.LivingFleshScheduledDevourData>(scheduledDevours.size());
        var scheduledChanged = false;

        for (var scheduledDevour : scheduledDevours) {
            var level = server.getLevel(scheduledDevour.level());

            if (level == null) {
                updatedScheduledDevours.add(scheduledDevour);
                continue;
            }

            if (scheduledDevour.triggerAtTick() > level.getGameTime()) {
                updatedScheduledDevours.add(scheduledDevour);
                continue;
            }

            var entity = level.getEntity(scheduledDevour.target());

            if (!(entity instanceof LivingEntity target) || !target.isAlive()) {
                scheduledChanged = true;
                continue;
            }

            var caster = level.getServer().getPlayerList().getPlayer(scheduledDevour.caster());
            LivingEntity owner = caster != null && caster.isAlive() && caster.level() == level ? caster : target;
            var payload = scheduledDevour.payload();
            var devourDamage = (float) Math.max(0D, payload.devourDamage());

            if (devourDamage <= 0F) {
                scheduledChanged = true;
                continue;
            }

            var repeatJaw = new DevourJaw(level, owner, target);

            repeatJaw.setPos(target.position());
            repeatJaw.setYRot(owner.getYRot());
            repeatJaw.setDamage(devourDamage);
            repeatJaw.getPersistentData().putBoolean("risas_living_flesh_devour", true);
            repeatJaw.getPersistentData().putBoolean("risas_living_flesh_repeatable", false);
            repeatJaw.getPersistentData().putDouble("risas_living_flesh_food", Math.max(0D, payload.food()));
            repeatJaw.getPersistentData().putDouble("risas_living_flesh_saturation", Math.max(0D, payload.saturation()));
            repeatJaw.getPersistentData().putDouble("risas_living_flesh_heal", Math.max(0D, payload.heal()));
            level.addFreshEntity(repeatJaw);
            scheduledChanged = true;
        }

        if (scheduledChanged)
            CommonEvents.setScheduledDevours(stack, updatedScheduledDevours);
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (!(event.getEntity().level() instanceof ServerLevel level) || event.getNewDamage() <= 0F)
                return;

            if (event.getSource().getDirectEntity() instanceof DevourJaw jaw) {
                if (!jaw.getPersistentData().getBoolean("risas_living_flesh_devour"))
                    return;

                if (!(event.getSource().getEntity() instanceof ServerPlayer caster))
                    return;

                var foodToAdd = Math.max(0, (int) Math.round(Math.max(0D, jaw.getPersistentData().getDouble("risas_living_flesh_food"))));
                var saturationToAdd = Math.max(0F, (float) Math.max(0D, jaw.getPersistentData().getDouble("risas_living_flesh_saturation")));
                var healToAdd = (float) Math.max(0D, jaw.getPersistentData().getDouble("risas_living_flesh_heal"));
                var foodData = caster.getFoodData();
                var foodBefore = foodData.getFoodLevel();
                var healthBefore = caster.getHealth();

                if (foodToAdd > 0)
                    foodData.setFoodLevel(Math.min(20, foodData.getFoodLevel() + foodToAdd));

                if (saturationToAdd > 0F)
                    foodData.setSaturation((float) Math.min(foodData.getFoodLevel(), foodData.getSaturationLevel() + saturationToAdd));

                if (healToAdd > 0F)
                    caster.heal(healToAdd);

                var restoredHealth = Math.max(0F, caster.getHealth() - healthBefore);

                LivingFleshItem relic = null;
                ItemStack stack = ItemStack.EMPTY;
                AbilityData ability = null;
                var bestDamage = -1D;

                for (var candidateStack : EntityUtils.findEquippedCurios(caster, RISASItems.LIVING_FLESH.value())) {
                    if (!(candidateStack.getItem() instanceof LivingFleshItem candidateRelic))
                        continue;

                    var candidateAbility = candidateRelic.getRelicData(caster, candidateStack).getAbilitiesData().getAbilityData("living_flesh");

                    if (!candidateAbility.canPlayerUse(caster))
                        continue;

                    var damage = Math.max(0D, candidateAbility.getStatData("devour_damage").getValue());

                    if (damage <= bestDamage)
                        continue;

                    relic = candidateRelic;
                    stack = candidateStack;
                    ability = candidateAbility;
                    bestDamage = damage;
                }

                if (!stack.isEmpty() && ability != null && relic != null) {
                    var relicData = relic.getRelicData(caster, stack);
                    var restoredFood = Math.max(0, foodData.getFoodLevel() - foodBefore);

                    ability.getStatisticData().getMetricData("ability_triggers").addValue(1D);
                    ability.getStatisticData().getMetricData("devour_damage_dealt").addValue(event.getNewDamage());
                    relicData.getLevelingData().addExperience("living_flesh", "ability_trigger", 1D);

                    if (ability.isRankModifierUnlocked("gluttonous_devour") && restoredFood > 0)
                        ability.getStatisticData().getMetricData("devour_food_restored").addValue(restoredFood);

                    if (ability.isRankModifierUnlocked("vital_feast") && restoredHealth > 0F)
                        ability.getStatisticData().getMetricData("devour_healing_restored").addValue(restoredHealth);
                }

                if (jaw.getPersistentData().getBoolean("risas_living_flesh_repeatable")
                        && event.getEntity() instanceof LivingEntity target
                        && target.isAlive()
                        && target.getHealth() > 0F) {
                    var repeatLimit = Math.max(0, jaw.getPersistentData().getInt("risas_living_flesh_repeat_limit"));
                    var repeatChance = Mth.clamp(jaw.getPersistentData().getDouble("risas_living_flesh_repeat_chance"), 0D, 1D);

                    if (!stack.isEmpty() && repeatLimit > 0 && repeatChance > 0D) {
                        var repeats = MathUtils.multicast(level.getRandom(), repeatChance, repeatLimit);

                        if (repeats > 0) {
                            var scheduledDevours = new ArrayList<>(getScheduledDevours(stack));

                            for (var i = 0; i < repeats; i++) {
                                scheduledDevours.add(new RISASDataComponents.LivingFleshScheduledDevourData(
                                        target.getUUID(),
                                        level.dimension(),
                                        caster.getUUID(),
                                        level.getGameTime() + (long) (i + 1) * 30L,
                                        new RISASDataComponents.LivingFleshScheduledDevourPayload(
                                                jaw.getDamage(),
                                                Math.max(0D, jaw.getPersistentData().getDouble("risas_living_flesh_food")),
                                                Math.max(0D, jaw.getPersistentData().getDouble("risas_living_flesh_saturation")),
                                                Math.max(0D, jaw.getPersistentData().getDouble("risas_living_flesh_heal"))
                                        )
                                ));
                            }

                            setScheduledDevours(stack, scheduledDevours);
                        }
                    }
                }


                return;
            }

            if (!(event.getSource().getEntity() instanceof Player attacker) || attacker == event.getEntity())
                return;

            var targetId = event.getEntity().getUUID();
            var gameTime = level.getGameTime();
            LivingFleshItem relic = null;
            ItemStack stack = ItemStack.EMPTY;
            AbilityData ability = null;
            var bestDamage = -1D;
            var bestDuration = -1L;

            for (var candidateStack : EntityUtils.findEquippedCurios(attacker, RISASItems.LIVING_FLESH.value())) {
                if (!(candidateStack.getItem() instanceof LivingFleshItem candidateRelic))
                    continue;

                var candidateAbility = candidateRelic.getRelicData(attacker, candidateStack).getAbilitiesData().getAbilityData("living_flesh");

                if (!candidateAbility.canPlayerUse(attacker))
                    continue;

                var durationTicks = Math.max(0L, Math.round(Math.max(0D, candidateAbility.getStatData("mark_duration").getValue()) * 20D));

                if (durationTicks <= 0L)
                    continue;

                var damage = Math.max(0D, candidateAbility.getStatData("devour_damage").getValue());

                if (damage < bestDamage || damage == bestDamage && durationTicks <= bestDuration)
                    continue;

                relic = candidateRelic;
                stack = candidateStack;
                ability = candidateAbility;
                bestDamage = damage;
                bestDuration = durationTicks;
            }

            if (relic == null || stack.isEmpty() || ability == null || bestDuration <= 0L)
                return;

            var refreshed = false;
            var newMark = new RISASDataComponents.LivingFleshMarkData(
                    targetId,
                    level.dimension(),
                    attacker.getUUID(),
                    gameTime + bestDuration,
                    bestDamage
            );
            var marks = new ArrayList<RISASDataComponents.LivingFleshMarkData>();

            for (var mark : getMarks(stack)) {
                if (mark.level().equals(level.dimension()) && mark.expiresAtTick() <= gameTime)
                    continue;

                if (mark.level().equals(level.dimension()) && mark.target().equals(targetId) && mark.caster().equals(attacker.getUUID())) {
                    if (!refreshed) {
                        marks.add(newMark);
                        refreshed = true;
                    }

                    continue;
                }

                marks.add(mark);
            }

            if (!refreshed)
                marks.add(newMark);

            setMarks(stack, marks);

            for (var otherStack : EntityUtils.findEquippedCurios(attacker, RISASItems.LIVING_FLESH.value())) {
                if (otherStack == stack || !(otherStack.getItem() instanceof LivingFleshItem))
                    continue;

                var otherMarks = getMarks(otherStack);

                if (otherMarks.isEmpty())
                    continue;

                var updated = new ArrayList<RISASDataComponents.LivingFleshMarkData>(otherMarks.size());
                var changed = false;

                for (var mark : otherMarks) {
                    if (mark.level().equals(level.dimension()) && mark.expiresAtTick() <= gameTime) {
                        changed = true;
                        continue;
                    }

                    if (mark.level().equals(level.dimension()) && mark.target().equals(targetId) && mark.caster().equals(attacker.getUUID())) {
                        changed = true;
                        continue;
                    }

                    updated.add(mark);
                }

                if (changed)
                    setMarks(otherStack, updated);
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            if (!(event.getEntity().level() instanceof ServerLevel level))
                return;

            var target = event.getEntity().getUUID();
            var players = level.getServer().getPlayerList().getPlayers();

            for (var player : players) {
                for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.LIVING_FLESH.value())) {
                    if (!(stack.getItem() instanceof LivingFleshItem))
                        continue;

                    var marks = getMarks(stack);

                    if (marks.isEmpty())
                        continue;

                    var updated = new ArrayList<RISASDataComponents.LivingFleshMarkData>(marks.size());
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
                }
            }
        }

        private static List<RISASDataComponents.LivingFleshMarkData> getMarks(ItemStack stack) {
            return stack.getOrDefault(RISASDataComponents.LIVING_FLESH_MARKS.get(), List.of());
        }

        private static List<RISASDataComponents.LivingFleshScheduledDevourData> getScheduledDevours(ItemStack stack) {
            return stack.getOrDefault(RISASDataComponents.LIVING_FLESH_SCHEDULED_DEVOURS.get(), List.of());
        }

        private static void setMarks(ItemStack stack, List<RISASDataComponents.LivingFleshMarkData> marks) {
            if (marks.isEmpty()) {
                stack.remove(RISASDataComponents.LIVING_FLESH_MARKS.get());
                return;
            }

            stack.set(RISASDataComponents.LIVING_FLESH_MARKS.get(), List.copyOf(marks));
        }

        private static void setScheduledDevours(ItemStack stack, List<RISASDataComponents.LivingFleshScheduledDevourData> scheduledDevours) {
            if (scheduledDevours.isEmpty()) {
                stack.remove(RISASDataComponents.LIVING_FLESH_SCHEDULED_DEVOURS.get());
                return;
            }

            stack.set(RISASDataComponents.LIVING_FLESH_SCHEDULED_DEVOURS.get(), List.copyOf(scheduledDevours));
        }
    }
}
