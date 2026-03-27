package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.spells.magic_arrow.MagicArrowProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASDataComponents;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.base.ISASRelicItem;
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
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.ArrayList;
import java.util.List;

public class EnderBowItem extends ISASRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("ender_bow")
                                .rankModifier(1, "stunning_bolt")
                                .rankModifier(3, "escalating_wounds")
                                .rankModifier(5, "multishot_arc")
                                .stat(AbilityStatTemplate.builder("arrow_damage")
                                        .initialValue(2.5D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("reveal_radius")
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("reveal_path_radius")
                                        .initialValue(1.5D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("stun_duration")
                                        .initialValue(1D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("bonus_per_mark")
                                        .initialValue(0.05D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("bonus_duration")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("multicast_chance")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("max_extra_arrows")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.2095D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("fired_arrow").build())
                                        .source(ExperienceSourceTemplate.builder("arrow_hit").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("arrows_fired")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("arrow_hits")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("stun_duration_total")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("stunning_bolt", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("escalating_bonus_damage")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("escalating_wounds", VisibilityState.OBFUSCATED)
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
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);

        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.pass(stack);

        var relicData = this.getRelicData(player, stack);
        var ability = relicData.getAbilitiesData().getAbilityData("ender_bow");

        if (!ability.canPlayerUse(player))
            return InteractionResultHolder.fail(stack);

        if (!level.isClientSide())
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.MAGIC_ARROW_CHARGE.get(), player.getSoundSource(), 1F, 1F);

        player.startUsingItem(hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        if (!(livingEntity instanceof ServerPlayer player))
            return;

        if (player.getUsedItemHand() != InteractionHand.MAIN_HAND)
            return;

        var chargeTicks = this.getUseDuration(stack, livingEntity) - timeLeft;

        if (chargeTicks < 20 || BowItem.getPowerForTime(chargeTicks) < 1F)
            return;

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("ender_bow");

        if (!ability.canPlayerUse(player))
            return;

        var baseDamage = Math.max(0D, ability.getStatData("arrow_damage").getValue());

        if (baseDamage <= 0D)
            return;

        var serverLevel = (ServerLevel) level;
        var look = player.getLookAngle().normalize();
        var offsets = new ArrayList<Double>();

        offsets.add(0D);

        if (ability.isRankModifierUnlocked("multishot_arc")) {
            var chance = Mth.clamp(ability.getStatData("multicast_chance").getValue(), 0D, 1D);
            var maxExtraArrows = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("max_extra_arrows").getValue())));
            var extraArrows = MathUtils.multicast(serverLevel.getRandom(), chance, maxExtraArrows);

            if (extraArrows == 1) {
                offsets.add(serverLevel.getRandom().nextBoolean() ? 12D : -12D);
            } else if (extraArrows > 1) {
                var spread = Math.min(100D, 12D * extraArrows);

                for (var i = 0; i < extraArrows; i++) {
                    var offset = -spread / 2D + spread * (double) i / (double) (extraArrows - 1);
                    offsets.add(offset);
                }
            }
        }

        for (var offset : offsets) {
            var direction = look.yRot((float) Math.toRadians(offset)).normalize();
            var arrow = new MagicArrowProjectile(serverLevel, player);
            var arrowPos = player.position()
                    .add(0D, player.getEyeHeight() - arrow.getBoundingBox().getYsize() * 0.5D, 0D)
                    .add(direction);

            arrow.setPos(arrowPos);
            arrow.shoot(direction);
            arrow.setDamage((float) baseDamage);
            arrow.getPersistentData().putBoolean("risas_ender_bow_arrow", true);
            arrow.getPersistentData().putInt("risas_ender_bow_slot", player.getInventory().selected);
            serverLevel.addFreshEntity(arrow);
            this.getRelicData(player, stack).getLevelingData().addExperience("ender_bow", "fired_arrow", 1D);
            ability.getStatisticData().getMetricData("arrows_fired").addValue(1D);
        }

        serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.MAGIC_ARROW_RELEASE.get(), player.getSoundSource(), 1F, 1F);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof ServerPlayer player) || level.isClientSide())
            return;

        if (!player.isUsingItem() || !ItemStack.isSameItemSameComponents(player.getUseItem(), stack))
            return;

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("ender_bow");

        if (!ability.canPlayerUse(player))
            return;

        var distance = Math.max(0D, ability.getStatData("reveal_radius").getValue());
        var radius = Math.max(0D, ability.getStatData("reveal_path_radius").getValue());

        if (distance <= 0D || radius <= 0D)
            return;

        var start = player.getEyePosition();
        var end = start.add(player.getLookAngle().normalize().scale(distance));
        var segment = end.subtract(start);
        var segmentLengthSqr = segment.lengthSqr();

        if (segmentLengthSqr <= 0D)
            return;

        for (var target : level.getEntitiesOfClass(
                LivingEntity.class,
                new AABB(start, end).inflate(radius),
                target -> target != player && target.isAlive() && !DamageSources.isFriendlyFireBetween(player, target)
        )) {
            var center = target.getBoundingBox().getCenter();
            var projection = Mth.clamp(center.subtract(start).dot(segment) / segmentLengthSqr, 0D, 1D);
            var closestPoint = start.add(segment.scale(projection));

            if (center.distanceToSqr(closestPoint) > radius * radius)
                continue;

            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false, false), player);
        }
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onMagicArrowIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity().level() instanceof ServerLevel level) || event.getAmount() <= 0F)
                return;

            if (!(event.getSource().getDirectEntity() instanceof MagicArrowProjectile arrow))
                return;

            if (!arrow.getPersistentData().getBoolean("risas_ender_bow_arrow"))
                return;

            if (!(event.getSource().getEntity() instanceof ServerPlayer player))
                return;

            var preferredSlot = arrow.getPersistentData().contains("risas_ender_bow_slot")
                    ? arrow.getPersistentData().getInt("risas_ender_bow_slot")
                    : -1;
            EnderBowItem item = null;
            ItemStack stack = ItemStack.EMPTY;
            AbilityData ability = null;
            var bestDamage = -1D;

            if (preferredSlot >= 0 && preferredSlot < player.getInventory().getContainerSize()) {
                var candidateStack = player.getInventory().getItem(preferredSlot);

                if (candidateStack.getItem() instanceof EnderBowItem candidateItem) {
                    var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("ender_bow");

                    if (candidateAbility.canPlayerUse(player)) {
                        item = candidateItem;
                        stack = candidateStack;
                        ability = candidateAbility;
                        bestDamage = Math.max(0D, candidateAbility.getStatData("arrow_damage").getValue());
                    }
                }
            }

            for (var candidateStack : List.of(player.getMainHandItem(), player.getOffhandItem())) {
                if (!(candidateStack.getItem() instanceof EnderBowItem candidateItem))
                    continue;

                var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("ender_bow");

                if (!candidateAbility.canPlayerUse(player))
                    continue;

                var damage = Math.max(0D, candidateAbility.getStatData("arrow_damage").getValue());

                if (damage <= bestDamage)
                    continue;

                item = candidateItem;
                stack = candidateStack;
                ability = candidateAbility;
                bestDamage = damage;
            }

            for (var i = 0; i < player.getInventory().getContainerSize(); i++) {
                var candidateStack = player.getInventory().getItem(i);

                if (!(candidateStack.getItem() instanceof EnderBowItem candidateItem))
                    continue;

                var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("ender_bow");

                if (!candidateAbility.canPlayerUse(player))
                    continue;

                var damage = Math.max(0D, candidateAbility.getStatData("arrow_damage").getValue());

                if (damage <= bestDamage)
                    continue;

                item = candidateItem;
                stack = candidateStack;
                ability = candidateAbility;
                bestDamage = damage;
            }

            if (stack.isEmpty() || ability == null || item == null)
                return;

            if (!ability.isRankModifierUnlocked("escalating_wounds"))
                return;

            var marks = new ArrayList<>(stack.getOrDefault(RISASDataComponents.ENDER_BOW_MARKS.get(), List.of()));
            var gameTime = level.getGameTime();
            var targetId = event.getEntity().getUUID();
            var changed = false;
            var activeMarks = 0;

            for (var mark : marks) {
                if (mark.level().equals(level.dimension()) && mark.expiresAtTick() <= gameTime) {
                    changed = true;
                    continue;
                }

                if (mark.level().equals(level.dimension()) && mark.target().equals(targetId))
                    activeMarks++;
            }

            if (changed) {
                var updatedMarks = new ArrayList<RISASDataComponents.EnderBowMarkData>(marks.size());

                for (var mark : marks) {
                    if (mark.level().equals(level.dimension()) && mark.expiresAtTick() <= gameTime)
                        continue;

                    updatedMarks.add(mark);
                }

                if (updatedMarks.isEmpty())
                    stack.remove(RISASDataComponents.ENDER_BOW_MARKS.get());
                else
                    stack.set(RISASDataComponents.ENDER_BOW_MARKS.get(), List.copyOf(updatedMarks));
            }

            var bonus = Math.max(0D, ability.getStatData("bonus_per_mark").getValue());

            if (activeMarks > 0 && bonus > 0D) {
                var originalAmount = event.getAmount();
                var boostedAmount = (float) Math.max(0D, originalAmount * (1D + activeMarks * bonus));

                event.setAmount(boostedAmount);

                var bonusDamage = Math.max(0F, boostedAmount - originalAmount);

                if (bonusDamage > 0F)
                    ability.getStatisticData().getMetricData("escalating_bonus_damage").addValue(bonusDamage);
            }
        }

        @SubscribeEvent
        public static void onMagicArrowDamagePost(LivingDamageEvent.Post event) {
            if (!(event.getEntity().level() instanceof ServerLevel level) || event.getNewDamage() <= 0F)
                return;

            if (!(event.getSource().getDirectEntity() instanceof MagicArrowProjectile arrow))
                return;

            if (!arrow.getPersistentData().getBoolean("risas_ender_bow_arrow"))
                return;

            if (!(event.getSource().getEntity() instanceof ServerPlayer player))
                return;

            var preferredSlot = arrow.getPersistentData().contains("risas_ender_bow_slot")
                    ? arrow.getPersistentData().getInt("risas_ender_bow_slot")
                    : -1;
            EnderBowItem item = null;
            ItemStack stack = ItemStack.EMPTY;
            AbilityData ability = null;
            var bestDamage = -1D;

            if (preferredSlot >= 0 && preferredSlot < player.getInventory().getContainerSize()) {
                var candidateStack = player.getInventory().getItem(preferredSlot);

                if (candidateStack.getItem() instanceof EnderBowItem candidateItem) {
                    var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("ender_bow");

                    if (candidateAbility.canPlayerUse(player)) {
                        item = candidateItem;
                        stack = candidateStack;
                        ability = candidateAbility;
                        bestDamage = Math.max(0D, candidateAbility.getStatData("arrow_damage").getValue());
                    }
                }
            }

            for (var candidateStack : List.of(player.getMainHandItem(), player.getOffhandItem())) {
                if (!(candidateStack.getItem() instanceof EnderBowItem candidateItem))
                    continue;

                var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("ender_bow");

                if (!candidateAbility.canPlayerUse(player))
                    continue;

                var damage = Math.max(0D, candidateAbility.getStatData("arrow_damage").getValue());

                if (damage <= bestDamage)
                    continue;

                item = candidateItem;
                stack = candidateStack;
                ability = candidateAbility;
                bestDamage = damage;
            }

            for (var i = 0; i < player.getInventory().getContainerSize(); i++) {
                var candidateStack = player.getInventory().getItem(i);

                if (!(candidateStack.getItem() instanceof EnderBowItem candidateItem))
                    continue;

                var candidateAbility = candidateItem.getRelicData(player, candidateStack).getAbilitiesData().getAbilityData("ender_bow");

                if (!candidateAbility.canPlayerUse(player))
                    continue;

                var damage = Math.max(0D, candidateAbility.getStatData("arrow_damage").getValue());

                if (damage <= bestDamage)
                    continue;

                item = candidateItem;
                stack = candidateStack;
                ability = candidateAbility;
                bestDamage = damage;
            }

            if (stack.isEmpty() || ability == null || item == null)
                return;

            item.getRelicData(player, stack).getLevelingData().addExperience("ender_bow", "arrow_hit", 1D);
            ability.getStatisticData().getMetricData("arrow_hits").addValue(1D);

            if (ability.isRankModifierUnlocked("stunning_bolt") && event.getEntity() instanceof LivingEntity target && target.isAlive()) {
                var stunTicks = Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("stun_duration").getValue()) * 20D));

                if (stunTicks > 0) {
                    target.addEffect(new MobEffectInstance(RelicsMobEffects.STUN, stunTicks, 0, false, true, true), player);
                    ability.getStatisticData().getMetricData("stun_duration_total").addValue(stunTicks / 20D);
                }
            }

            if (!ability.isRankModifierUnlocked("escalating_wounds"))
                return;

            var marks = new ArrayList<>(stack.getOrDefault(RISASDataComponents.ENDER_BOW_MARKS.get(), List.of()));
            var gameTime = level.getGameTime();
            var durationTicks = Math.max(1L, Math.round(Math.max(0D, ability.getStatData("bonus_duration").getValue()) * 20D));
            var updatedMarks = new ArrayList<RISASDataComponents.EnderBowMarkData>(marks.size() + 1);

            for (var mark : marks) {
                if (mark.level().equals(level.dimension()) && mark.expiresAtTick() <= gameTime)
                    continue;

                updatedMarks.add(mark);
            }

            updatedMarks.add(new RISASDataComponents.EnderBowMarkData(
                    event.getEntity().getUUID(),
                    level.dimension(),
                    gameTime + durationTicks
            ));

            stack.set(RISASDataComponents.ENDER_BOW_MARKS.get(), List.copyOf(updatedMarks));
        }
    }
}
