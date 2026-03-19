package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.entity.mobs.IMagicSummon;
import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import io.redspace.ironsspellbooks.spells.ender.TeleportSpell;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.init.RISASDataComponents;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items.misc.RISASLootEntries;
import it.hurts.sskirillss.relics.api.relics.AbilityMetricTemplate;
import it.hurts.sskirillss.relics.api.relics.AbilityStatisticTemplate;
import it.hurts.sskirillss.relics.api.relics.IRelicItem;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourceTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsCreativeTabs;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.misc.CreativeContentConstructor;
import it.hurts.sskirillss.relics.items.misc.ICreativeTabContent;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.misc.LootEntries;
import it.hurts.sskirillss.relics.utils.MathUtils;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculatePlayerTurnEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SlicerItem extends ExtendedSwordItem implements IRelicItem, ICreativeTabContent {
    public SlicerItem() {
        super(Tiers.NETHERITE, new Item.Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, new Unbreakable(false)));
    }

    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("slicer_weapon")
                                .initialMaxLevel(10)
                                .rankModifier(3, "slow_charge_decay")
                                .stat(AbilityStatTemplate.builder("weapon_damage")
                                        .initialValue(5D, 7.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0476D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("weapon_attack_speed")
                                        .initialValue(0.75D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("combo_window")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.019D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("charge_damage_bonus")
                                        .initialValue(0.05D, 0.1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("max_charges")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("fully_charged_attack").build())
                                        .source(ExperienceSourceTemplate.builder("gained_stack").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("charges_accumulated")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .build())
                                .build())
                        .ability(AbilityTemplate.builder("slicer")
                                .initialMaxLevel(10)
                                .rankModifier(1, "unstable_mark")
                                .rankModifier(5, "blood_cycle")
                                .stat(AbilityStatTemplate.builder("radius")
                                        .initialValue(5D, 7.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("cooldown")
                                        .initialValue(30D, 20D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.0143D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("attack_interval")
                                        .initialValue(1.5D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.0071D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("interval_reduction_on_kill")
                                        .initialValue(0.01D, 0.05D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("mark_ignore_chance")
                                        .initialValue(0.15D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("ability_activation").build())
                                        .source(ExperienceSourceTemplate.builder("ability_attack").build())
                                        .source(ExperienceSourceTemplate.builder("ability_kill").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("ability_uses")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("targets_attacked")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .build())
                                .build())
                        .build())
                .loot(LootTemplate.builder()
                        .entry(RISASLootEntries.ANY_STRUCTURE)
                        .build())
                .build();
    }

    private static void finishSlicerState(ServerPlayer player, ItemStack stack, RISASDataComponents.SlicerStateData state, long cooldownTicks) {
        if (state.level().equals(player.level().dimension()) && player.level() instanceof ServerLevel level) {
            var returnPoint = new Vec3(state.centerX(), state.centerY(), state.centerZ());
            var destination = TeleportSpell.solveTeleportDestination(level, player, net.minecraft.core.BlockPos.containing(returnPoint), returnPoint);
            var safeDestination = destination;
            var safeDestinationFound = level.noCollision(player, player.getBoundingBox().move(destination.x - player.getX(), destination.y - player.getY(), destination.z - player.getZ()));

            if (!safeDestinationFound) {
                for (var fallback : new Vec3[]{
                        returnPoint.add(0D, 1D, 0D),
                        returnPoint.add(0D, 2D, 0D),
                        returnPoint.add(0.6D, 1D, 0.6D),
                        returnPoint.add(-0.6D, 1D, -0.6D)
                }) {
                    var solved = TeleportSpell.solveTeleportDestination(level, player, net.minecraft.core.BlockPos.containing(fallback), fallback);

                    if (!level.noCollision(player, player.getBoundingBox().move(solved.x - player.getX(), solved.y - player.getY(), solved.z - player.getZ())))
                        continue;

                    safeDestination = solved;
                    safeDestinationFound = true;
                    break;
                }
            }

            if (safeDestinationFound && player.position().distanceToSqr(safeDestination) > 0.25D)
                player.teleportTo(safeDestination.x, safeDestination.y, safeDestination.z);
        }

        stack.remove(RISASDataComponents.SLICER_STATE.get());
        stack.remove(RISASDataComponents.SLICER_REVEAL_UNTIL.get());
        stack.remove(RISASDataComponents.SLICER_ACTIVE.get());
        stack.set(RISASDataComponents.SLICER_COOLDOWN_UNTIL.get(), player.level().getGameTime() + cooldownTicks);
        player.removeEffect(MobEffectRegistry.TRUE_INVISIBILITY);
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedIronsSpellsAndSpellbooks.MODID;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);

        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.pass(stack);

        if (stack.getOrDefault(RISASDataComponents.SLICER_COOLDOWN_UNTIL.get(), 0L) > level.getGameTime() || stack.get(RISASDataComponents.SLICER_STATE.get()) != null)
            return InteractionResultHolder.fail(stack);

        var relicData = this.getRelicData(player, stack);
        var ability = relicData.getAbilitiesData().getAbilityData("slicer");

        if (!ability.canPlayerUse(player))
            return InteractionResultHolder.fail(stack);

        var center = player.position();
        var radius = Math.max(1D, ability.getStatData("radius").getValue());
        var radiusSqr = radius * radius;
        var hasTargets = !level.getEntitiesOfClass(
                LivingEntity.class,
                AABB.ofSize(center, radius * 2D, radius * 2D, radius * 2D),
                target -> target != player
                        && target.isAlive()
                        && target.position().distanceToSqr(center) <= radiusSqr
                        && !DamageSources.isFriendlyFireBetween(player, target)
                        && (!(target instanceof IMagicSummon summon) || summon.getSummoner() == null || !summon.getSummoner().getUUID().equals(player.getUUID()))
                        && (!(target instanceof OwnableEntity ownable) || ownable.getOwnerUUID() == null || !ownable.getOwnerUUID().equals(player.getUUID()))
        ).isEmpty();

        if (!hasTargets)
            return InteractionResultHolder.fail(stack);

        if (level instanceof ServerLevel serverLevel) {
            var intervalTicks = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("attack_interval").getValue()) * 20D));

            stack.set(
                    RISASDataComponents.SLICER_STATE.get(),
                    new RISASDataComponents.SlicerStateData(
                            serverLevel.dimension(),
                            center.x,
                            center.y,
                            center.z,
                            radius,
                            serverLevel.getGameTime(),
                            intervalTicks,
                            java.util.List.of()
                    )
            );
            stack.set(RISASDataComponents.SLICER_ACTIVE.get(), true);
            stack.remove(RISASDataComponents.SLICER_REVEAL_UNTIL.get());
            ability.getStatisticData().getMetricData("ability_uses").addValue(1D);
            relicData.getLevelingData().addExperience("slicer", "ability_activation", 1D);

        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof ServerPlayer player))
            return;

        if (!stack.has(DataComponents.UNBREAKABLE))
            stack.set(DataComponents.UNBREAKABLE, new Unbreakable(false));

        if (stack.getOrDefault(DataComponents.DAMAGE, 0) > 0)
            stack.set(DataComponents.DAMAGE, 0);

        var gameTime = level.getGameTime();
        var bladeAbility = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("slicer_weapon");
        var charges = Math.max(0, stack.getOrDefault(RISASDataComponents.SLICER_BLADE_CHARGES.get(), 0));
        var windowUntil = stack.getOrDefault(RISASDataComponents.SLICER_BLADE_WINDOW_UNTIL.get(), 0L);

        if (charges <= 0) {
            stack.remove(RISASDataComponents.SLICER_BLADE_CHARGES.get());
            stack.remove(RISASDataComponents.SLICER_BLADE_WINDOW_UNTIL.get());
            stack.remove(RISASDataComponents.SLICER_BLADE_DECAY_AT.get());
        } else if (windowUntil <= gameTime) {
            if (bladeAbility.canPlayerUse(player) && bladeAbility.isRankModifierUnlocked("slow_charge_decay")) {
                var nextDecayAt = stack.getOrDefault(RISASDataComponents.SLICER_BLADE_DECAY_AT.get(), 0L);

                if (nextDecayAt <= 0L)
                    nextDecayAt = gameTime + 20L;

                if (nextDecayAt <= gameTime) {
                    var decaySteps = 1L + (gameTime - nextDecayAt) / 20L;
                    charges = Math.max(0, charges - (int) decaySteps);


                    if (charges <= 0) {
                        stack.remove(RISASDataComponents.SLICER_BLADE_CHARGES.get());
                        stack.remove(RISASDataComponents.SLICER_BLADE_WINDOW_UNTIL.get());
                        stack.remove(RISASDataComponents.SLICER_BLADE_DECAY_AT.get());
                    } else {
                        stack.set(RISASDataComponents.SLICER_BLADE_CHARGES.get(), charges);
                        stack.set(RISASDataComponents.SLICER_BLADE_DECAY_AT.get(), gameTime + 20L);
                    }
                } else {
                    stack.set(RISASDataComponents.SLICER_BLADE_DECAY_AT.get(), nextDecayAt);
                }
            } else {
                stack.remove(RISASDataComponents.SLICER_BLADE_CHARGES.get());
                stack.remove(RISASDataComponents.SLICER_BLADE_WINDOW_UNTIL.get());
                stack.remove(RISASDataComponents.SLICER_BLADE_DECAY_AT.get());
            }
        } else {
            stack.remove(RISASDataComponents.SLICER_BLADE_DECAY_AT.get());
        }

        if (player.getMainHandItem() == stack && bladeAbility.canPlayerUse(player)) {
            var damage = Math.max(0D, bladeAbility.getStatData("weapon_damage").getValue());
            var attackSpeed = Math.max(0D, bladeAbility.getStatData("weapon_attack_speed").getValue());
            var desired = SwordItem.createAttributes(Tiers.NETHERITE, (float) Math.max(-Tiers.NETHERITE.getAttackDamageBonus(), damage - 1D - Tiers.NETHERITE.getAttackDamageBonus()), (float) (attackSpeed - 4D));

            if (!desired.equals(stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)))
                stack.set(DataComponents.ATTRIBUTE_MODIFIERS, desired);
        }

        if (!(level instanceof ServerLevel serverLevel))
            return;

        if (stack.getOrDefault(RISASDataComponents.SLICER_ACTIVE.get(), false)) {
            player.setDeltaMovement(Vec3.ZERO);
            player.fallDistance = 0F;
            player.hurtMarked = true;
        }

        var state = stack.get(RISASDataComponents.SLICER_STATE.get());

        if (state == null) {
            stack.remove(RISASDataComponents.SLICER_ACTIVE.get());
            return;
        }

        var slicerRelicData = this.getRelicData(player, stack);
        var slicerAbility = slicerRelicData.getAbilitiesData().getAbilityData("slicer");
        var cooldownTicks = Math.max(1L, Math.round(Math.max(0D, slicerAbility.getStatData("cooldown").getValue()) * 20D));
        gameTime = serverLevel.getGameTime();

        if (player.getMainHandItem() != stack) {
            finishSlicerState(player, stack, state, cooldownTicks);
            return;
        }

        if (!slicerAbility.canPlayerUse(player)) {
            finishSlicerState(player, stack, state, cooldownTicks);
            return;
        }

        if (!state.level().equals(serverLevel.dimension())) {
            finishSlicerState(player, stack, state, cooldownTicks);
            return;
        }

        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0F;
        player.hurtMarked = true;

        if (stack.getOrDefault(RISASDataComponents.SLICER_REVEAL_UNTIL.get(), 0L) > gameTime) {
            player.removeEffect(MobEffectRegistry.TRUE_INVISIBILITY);
        } else {
            player.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY, 15, 0, false, false, true));
        }

        if (serverLevel.getGameTime() % 2L == 0L) {
            var outline = ParticleUtils.constructSimpleSpark(new Color(176, 32, 52), 0.5F, 1, 1F);
            var points = Math.max(48, (int) Math.round(state.radius() * 32D));

            for (var point = 0; point < points; point++) {
                var angle = (Math.PI * 2D * point) / points;
                var x = state.centerX() + Math.cos(angle) * state.radius();
                var z = state.centerZ() + Math.sin(angle) * state.radius();

                serverLevel.sendParticles(outline, x, state.centerY() + 0.15D, z, 2, 0.03D, 0.02D, 0.03D, 0D);
            }
        }

        if (serverLevel.getGameTime() < state.nextActionTick())
            return;

        var center = new Vec3(state.centerX(), state.centerY(), state.centerZ());
        var radiusSqr = state.radius() * state.radius();
        var targets = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                AABB.ofSize(center, state.radius() * 2D, state.radius() * 2D, state.radius() * 2D),
                target -> target != player
                        && target.isAlive()
                        && target.position().distanceToSqr(center) <= radiusSqr
                        && !DamageSources.isFriendlyFireBetween(player, target)
                        && (!(target instanceof IMagicSummon summon) || summon.getSummoner() == null || !summon.getSummoner().getUUID().equals(player.getUUID()))
                        && (!(target instanceof OwnableEntity ownable) || ownable.getOwnerUUID() == null || !ownable.getOwnerUUID().equals(player.getUUID()))
        );

        if (targets.isEmpty()) {
            finishSlicerState(player, stack, state, cooldownTicks);
            return;
        }

        var attacked = new ArrayList<>(state.attackedTargets());
        var intervalTicks = state.intervalTicks();

        var attackedSet = new HashSet<>(attacked);
        var candidates = new ArrayList<LivingEntity>();

        for (var target : targets) {
            if (!attackedSet.contains(target.getUUID()))
                candidates.add(target);
        }

        if (candidates.isEmpty()) {
            finishSlicerState(player, stack, state, cooldownTicks);
            return;
        }

        var target = candidates.get(serverLevel.getRandom().nextInt(candidates.size()));
        var look = target.getLookAngle();
        var horizontalLook = new Vec3(look.x, 0D, look.z);

        if (horizontalLook.lengthSqr() < 1.0E-4D)
            horizontalLook = new Vec3(0D, 0D, 1D).yRot((float) Math.toRadians(-target.getYRot()));

        var direction = horizontalLook.normalize();
        var side = new Vec3(-direction.z, 0D, direction.x);
        var behind = target.position().subtract(direction.scale(target.getBbWidth() + 1.35D));
        var destination = TeleportSpell.solveTeleportDestination(serverLevel, player, net.minecraft.core.BlockPos.containing(behind), behind);
        var safeDestination = destination;
        var safeDestinationFound = serverLevel.noCollision(player, player.getBoundingBox().move(destination.x - player.getX(), destination.y - player.getY(), destination.z - player.getZ()));

        if (!safeDestinationFound) {
            for (var fallback : new Vec3[]{
                    behind.add(0D, 1D, 0D),
                    behind.add(side.scale(0.65D)),
                    behind.add(side.scale(-0.65D)),
                    target.position().subtract(direction.scale(target.getBbWidth() + 1.05D)).add(0D, 0.5D, 0D),
                    target.position().subtract(direction.scale(target.getBbWidth() + 1.75D))
            }) {
                var solved = TeleportSpell.solveTeleportDestination(serverLevel, player, net.minecraft.core.BlockPos.containing(fallback), fallback);

                if (!serverLevel.noCollision(player, player.getBoundingBox().move(solved.x - player.getX(), solved.y - player.getY(), solved.z - player.getZ())))
                    continue;

                safeDestination = solved;
                safeDestinationFound = true;
                break;
            }
        }

        if (!safeDestinationFound) {
            stack.set(
                    RISASDataComponents.SLICER_STATE.get(),
                    new RISASDataComponents.SlicerStateData(
                            state.level(),
                            state.centerX(),
                            state.centerY(),
                            state.centerZ(),
                            state.radius(),
                            serverLevel.getGameTime() + intervalTicks,
                            intervalTicks,
                            java.util.List.copyOf(attacked)
                    )
            );
            return;
        }

        var previousPosition = player.position().add(0D, player.getBbHeight() * 0.5D, 0D);
        var magicData = MagicData.getPlayerMagicData(player);

        magicData.setAdditionalCastData(new TeleportSpell.TeleportData(safeDestination));
        SpellRegistry.BLOOD_STEP_SPELL.get().onCast(serverLevel, 1, player, CastSource.SWORD, magicData);
        magicData.resetAdditionalCastData();

        if (player.position().distanceToSqr(safeDestination) > 0.25D)
            player.teleportTo(safeDestination.x, safeDestination.y, safeDestination.z);

        var currentPosition = player.position().add(0D, player.getBbHeight() * 0.5D, 0D);

        if (currentPosition.distanceToSqr(previousPosition) > 0.01D) {
            var trailParticle = ParticleUtils.constructSimpleSpark(new Color(204, 28, 42), 0.38F, 6, 1F);
            var distance = previousPosition.distanceTo(currentPosition);
            var steps = Math.max(10, (int) Math.ceil(distance * 8D));

            for (var step = 0; step <= steps; step++) {
                var progress = (double) step / (double) steps;
                var point = previousPosition.lerp(currentPosition, progress);

                serverLevel.sendParticles(trailParticle, point.x, point.y, point.z, 1, 0.01D, 0.01D, 0.01D, 0D);
            }
        }

        player.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
        player.swing(InteractionHand.MAIN_HAND, true);
        player.attack(target);
        slicerAbility.getStatisticData().getMetricData("targets_attacked").addValue(1D);
        slicerRelicData.getLevelingData().addExperience("slicer", "ability_attack", 1D);
        stack.set(RISASDataComponents.SLICER_REVEAL_UNTIL.get(), gameTime + 5L + serverLevel.getRandom().nextInt(6));
        player.removeEffect(MobEffectRegistry.TRUE_INVISIBILITY);

        var updatedState = stack.get(RISASDataComponents.SLICER_STATE.get());

        if (updatedState != null && updatedState.level().equals(state.level())) {
            attacked = new ArrayList<>(updatedState.attackedTargets());
            intervalTicks = updatedState.intervalTicks();
        }

        attacked.add(target.getUUID());
        if (slicerAbility.isRankModifierUnlocked("unstable_mark")
                && serverLevel.getRandom().nextDouble() < Mth.clamp(slicerAbility.getStatData("mark_ignore_chance").getValue(), 0D, 1D)) {
            attacked.remove(attacked.size() - 1);
        }

        var nextActionTick = serverLevel.getGameTime() + intervalTicks;

        if (updatedState != null && updatedState.level().equals(state.level()))
            nextActionTick = Math.max(updatedState.nextActionTick(), nextActionTick);

        stack.set(
                RISASDataComponents.SLICER_STATE.get(),
                new RISASDataComponents.SlicerStateData(
                        state.level(),
                        state.centerX(),
                        state.centerY(),
                        state.centerZ(),
                        state.radius(),
                        nextActionTick,
                        intervalTicks,
                        List.copyOf(attacked)
                )
        );
    }

    @Override
    public void gatherCreativeTabContent(CreativeContentConstructor creativeContentConstructor) {
        creativeContentConstructor.entry(RelicsCreativeTabs.RELICS_TAB.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS, this);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getEntity() == player || event.getNewDamage() <= 0F)
                return;

            if (event.getSource().getDirectEntity() != player)
                return;

            var stack = player.getMainHandItem();

            if (!(stack.getItem() instanceof SlicerItem item))
                return;

            var relicData = item.getRelicData(player, stack);
            var state = stack.get(RISASDataComponents.SLICER_STATE.get());

            if (state != null && state.level().equals(player.level().dimension())) {
                var slicerAbility = relicData.getAbilitiesData().getAbilityData("slicer");

                if (slicerAbility.canPlayerUse(player))
                    slicerAbility.getStatisticData().getMetricData("damage_dealt").addValue(event.getNewDamage());

                return;
            }

            var weaponAbility = relicData.getAbilitiesData().getAbilityData("slicer_weapon");

            if (weaponAbility.canPlayerUse(player))
                weaponAbility.getStatisticData().getMetricData("damage_dealt").addValue(event.getNewDamage());
        }

        @SubscribeEvent
        public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getEntity() == player || event.getNewDamage() <= 0F)
                return;

            if (event.getSource().getDirectEntity() != player)
                return;

            var stack = player.getMainHandItem();

            if (!(stack.getItem() instanceof SlicerItem item))
                return;

            var relicData = item.getRelicData(player, stack);
            var ability = relicData.getAbilitiesData().getAbilityData("slicer_weapon");

            if (!ability.canPlayerUse(player))
                return;

            var gameTime = player.level().getGameTime();
            var charges = Math.max(0, stack.getOrDefault(RISASDataComponents.SLICER_BLADE_CHARGES.get(), 0));
            var windowUntil = stack.getOrDefault(RISASDataComponents.SLICER_BLADE_WINDOW_UNTIL.get(), 0L);
            var maxCharges = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("max_charges").getValue())));
            var bonusPerCharge = Math.max(0D, ability.getStatData("charge_damage_bonus").getValue());
            var windowTicks = Math.max(1L, Math.round(Math.max(0D, ability.getStatData("combo_window").getValue()) * 20D));

            if (windowUntil <= gameTime) {
                if (ability.isRankModifierUnlocked("slow_charge_decay")) {
                    var nextDecayAt = stack.getOrDefault(RISASDataComponents.SLICER_BLADE_DECAY_AT.get(), 0L);

                    if (nextDecayAt <= 0L)
                        nextDecayAt = windowUntil > 0L ? windowUntil + 20L : gameTime + 20L;

                    if (nextDecayAt <= gameTime) {
                        var decaySteps = 1L + (gameTime - nextDecayAt) / 20L;
                        charges = Math.max(0, charges - (int) decaySteps);

                    }
                } else {
                    charges = 0;
                }
            }

            if (Math.max(player.getAttackStrengthScale(0F), player.getAttackStrengthScale(0.5F)) >= 0.99F)
                relicData.getLevelingData().addExperience("slicer_weapon", "fully_charged_attack", 1D);

            var previousCharges = charges;
            charges = Math.min(maxCharges, charges + 1);
            var gainedCharges = Math.max(0, charges - previousCharges);

            if (gainedCharges > 0) {
                ability.getStatisticData().getMetricData("charges_accumulated").addValue(gainedCharges);
                relicData.getLevelingData().addExperience("slicer_weapon", "gained_stack", gainedCharges);
            }

            var damage = Math.max(0D, event.getNewDamage());
            var state = stack.get(RISASDataComponents.SLICER_STATE.get());

            if (state != null && state.level().equals(player.level().dimension()))
                damage = Math.max(damage, Math.max(0D, ability.getStatData("weapon_damage").getValue()));

            if (bonusPerCharge > 0D)
                damage *= 1D + charges * bonusPerCharge;

            event.setNewDamage((float) Math.max(0D, damage));

            stack.set(RISASDataComponents.SLICER_BLADE_CHARGES.get(), charges);
            stack.set(RISASDataComponents.SLICER_BLADE_WINDOW_UNTIL.get(), gameTime + windowTicks);
            stack.remove(RISASDataComponents.SLICER_BLADE_DECAY_AT.get());
        }

        @SubscribeEvent
        public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getAmount() <= 0F)
                return;

            for (var i = 0; i < player.getInventory().getContainerSize(); i++) {
                var stack = player.getInventory().getItem(i);

                if (!(stack.getItem() instanceof SlicerItem))
                    continue;

                var state = stack.get(RISASDataComponents.SLICER_STATE.get());

                if (state == null || !state.level().equals(player.level().dimension()))
                    continue;

                if (player.getMainHandItem() != stack) {
                    var ability = ((SlicerItem) stack.getItem()).getRelicData(player, stack).getAbilitiesData().getAbilityData("slicer");
                    var cooldownTicks = Math.max(1L, Math.round(Math.max(0D, ability.getStatData("cooldown").getValue()) * 20D));

                    finishSlicerState(player, stack, state, cooldownTicks);
                    continue;
                }

                event.setCanceled(true);
                return;
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            if (!(event.getEntity().level() instanceof ServerLevel serverLevel))
                return;

            if (event.getEntity() instanceof ServerPlayer player) {
                for (var i = 0; i < player.getInventory().getContainerSize(); i++) {
                    var stack = player.getInventory().getItem(i);

                    if (stack.getItem() instanceof SlicerItem) {
                        stack.remove(RISASDataComponents.SLICER_STATE.get());
                        stack.remove(RISASDataComponents.SLICER_ACTIVE.get());
                        stack.remove(RISASDataComponents.SLICER_BLADE_CHARGES.get());
                        stack.remove(RISASDataComponents.SLICER_BLADE_WINDOW_UNTIL.get());
                        stack.remove(RISASDataComponents.SLICER_BLADE_DECAY_AT.get());
                        stack.remove(RISASDataComponents.SLICER_REVEAL_UNTIL.get());
                    }
                }
            }

            var targetPos = event.getEntity().position();

            for (var player : serverLevel.players()) {
                for (var i = 0; i < player.getInventory().getContainerSize(); i++) {
                    var stack = player.getInventory().getItem(i);

                    if (!(stack.getItem() instanceof SlicerItem relic))
                        continue;

                    var state = stack.get(RISASDataComponents.SLICER_STATE.get());

                    if (state == null || !state.level().equals(serverLevel.dimension()))
                        continue;

                    if (event.getSource().getEntity() != player)
                        continue;

                    if (targetPos.distanceToSqr(state.centerX(), state.centerY(), state.centerZ()) > state.radius() * state.radius())
                        continue;

                    var relicData = relic.getRelicData(player, stack);
                    var ability = relicData.getAbilitiesData().getAbilityData("slicer");

                    if (!ability.canPlayerUse(player))
                        continue;

                    relicData.getLevelingData().addExperience("slicer", "ability_kill", 1D);

                    if (!ability.isRankModifierUnlocked("blood_cycle"))
                        continue;

                    var intervalTicks = Math.max(1,
                            state.intervalTicks() - Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("interval_reduction_on_kill").getValue()) * 20D))
                    );

                    stack.set(
                            RISASDataComponents.SLICER_STATE.get(),
                            new RISASDataComponents.SlicerStateData(
                                    state.level(),
                                    state.centerX(),
                                    state.centerY(),
                                    state.centerZ(),
                                    state.radius(),
                                    serverLevel.getGameTime() + intervalTicks,
                                    intervalTicks,
                                    List.of()
                            )
                    );

                }
            }
        }
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            var minecraft = Minecraft.getInstance();
            var player = minecraft.player;

            if (player == null || minecraft.level == null)
                return;

            var stack = player.getMainHandItem();

            if (!(stack.getItem() instanceof SlicerItem))
                return;

            if (!stack.getOrDefault(RISASDataComponents.SLICER_ACTIVE.get(), false))
                return;

            var state = stack.get(RISASDataComponents.SLICER_STATE.get());

            if (state == null || !state.level().equals(minecraft.level.dimension()))
                return;

            player.setDeltaMovement(Vec3.ZERO);
            player.fallDistance = 0F;
            player.hurtMarked = true;
        }

        @SubscribeEvent
        public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
            var minecraft = Minecraft.getInstance();

            if (minecraft.player == null || minecraft.level == null || event.getEntity() != minecraft.player)
                return;

            var stack = minecraft.player.getMainHandItem();

            if (!(stack.getItem() instanceof SlicerItem))
                return;

            if (!stack.getOrDefault(RISASDataComponents.SLICER_ACTIVE.get(), false))
                return;

            var state = stack.get(RISASDataComponents.SLICER_STATE.get());

            if (state == null || !state.level().equals(minecraft.level.dimension()))
                return;

            var input = event.getInput();
            input.leftImpulse = 0F;
            input.forwardImpulse = 0F;
            input.up = false;
            input.down = false;
            input.left = false;
            input.right = false;
            input.jumping = false;
            input.shiftKeyDown = false;
        }

        @SubscribeEvent
        public static void onCalculatePlayerTurn(CalculatePlayerTurnEvent event) {
            var minecraft = Minecraft.getInstance();
            var player = minecraft.player;

            if (player == null || minecraft.level == null)
                return;

            var stack = player.getMainHandItem();

            if (!(stack.getItem() instanceof SlicerItem))
                return;

            if (!stack.getOrDefault(RISASDataComponents.SLICER_ACTIVE.get(), false))
                return;

            var state = stack.get(RISASDataComponents.SLICER_STATE.get());

            if (state == null || !state.level().equals(minecraft.level.dimension()))
                return;

            event.setMouseSensitivity(0D);
        }
    }
}
