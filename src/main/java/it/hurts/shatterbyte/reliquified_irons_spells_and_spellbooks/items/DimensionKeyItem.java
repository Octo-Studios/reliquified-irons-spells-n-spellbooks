package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.capabilities.magic.PortalManager;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalData;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalEntity;
import io.redspace.ironsspellbooks.entity.spells.portal.PortalPos;
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
import io.redspace.ironsspellbooks.util.ParticleHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import top.theillusivec4.curios.api.SlotContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class DimensionKeyItem extends ISASRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("dimension_key")
                                .rankModifier(1, "vital_gate")
                                .rankModifier(3, "tremor_passage")
                                .rankModifier(5, "immortal_crossing")
                                .stat(AbilityStatTemplate.builder("portal_duration")
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1429D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("regen_boost")
                                        .initialValue(0.05D, 0.15D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1619D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("tremor_duration")
                                        .initialValue(1D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("immortality_duration")
                                        .initialValue(1D, 2.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("vital_gate_radius")
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("portal_pair_created").build())
                                        .source(ExperienceSourceTemplate.builder("portal_traversal").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("portal_pairs_created")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("portal_traversals")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("vital_gate_healing")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("vital_gate", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("tremor_duration_total")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("tremor_passage", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("immortality_duration_total")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("immortal_crossing", VisibilityState.OBFUSCATED)
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

        if (!player.isShiftKeyDown())
            return InteractionResultHolder.pass(stack);

        if (level instanceof ServerLevel serverLevel) {
            var hadPoint = stack.get(RISASDataComponents.DIMENSION_KEY_POINT.get()) != null;
            var hadPortals = stack.get(RISASDataComponents.DIMENSION_KEY_ACTIVE_PORTALS.get()) != null;
            stack.remove(RISASDataComponents.DIMENSION_KEY_POINT.get());
            clearActivePortals(serverLevel, stack);

            if (hadPoint || hadPortals) {
                serverLevel.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundRegistry.ENDER_CAST.get(),
                        SoundSource.PLAYERS,
                        2F,
                        0.9F + serverLevel.getRandom().nextFloat() * 0.2F
                );
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();

        if (player == null)
            return InteractionResult.PASS;

        var stack = context.getItemInHand();
        var level = context.getLevel();

        if (context.getHand() != InteractionHand.MAIN_HAND)
            return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            if (level instanceof ServerLevel serverLevel) {
                var hadPoint = stack.get(RISASDataComponents.DIMENSION_KEY_POINT.get()) != null;
                var hadPortals = stack.get(RISASDataComponents.DIMENSION_KEY_ACTIVE_PORTALS.get()) != null;
                stack.remove(RISASDataComponents.DIMENSION_KEY_POINT.get());
                clearActivePortals(serverLevel, stack);

                if (hadPoint || hadPortals) {
                    serverLevel.playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundRegistry.ENDER_CAST.get(),
                            SoundSource.PLAYERS,
                            2F,
                            0.9F + serverLevel.getRandom().nextFloat() * 0.2F
                    );
                }
            }

            return InteractionResult.SUCCESS;
        }

        var relicData = this.getRelicData(player, stack);
        var ability = relicData.getAbilitiesData().getAbilityData("dimension_key");

        if (!ability.canPlayerUse(player))
            return InteractionResult.FAIL;

        if (!(level instanceof ServerLevel serverLevel))
            return InteractionResult.SUCCESS;

        var clicked = context.getClickedPos();
        var clickPos = new Vec3(clicked.getX() + 0.5D, clicked.getY() + 1D, clicked.getZ() + 0.5D);

        var point = stack.get(RISASDataComponents.DIMENSION_KEY_POINT.get());

        if (point == null) {
            stack.set(
                    RISASDataComponents.DIMENSION_KEY_POINT.get(),
                    new RISASDataComponents.DimensionKeyPointData(
                            serverLevel.dimension(),
                            clickPos.x,
                            clickPos.y,
                            clickPos.z,
                            player.getYRot()
                    )
            );
            serverLevel.playSound(
                    null,
                    clickPos.x,
                    clickPos.y,
                    clickPos.z,
                    SoundRegistry.ENDER_CAST.get(),
                    SoundSource.PLAYERS,
                    2F,
                    0.9F + serverLevel.getRandom().nextFloat() * 0.2F
            );

            return InteractionResult.SUCCESS;
        }

        var firstLevel = serverLevel.getServer().getLevel(point.level());

        if (firstLevel == null) {
            stack.remove(RISASDataComponents.DIMENSION_KEY_POINT.get());
            return InteractionResult.FAIL;
        }

        clearActivePortals(serverLevel, stack);

        var durationTicks = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("portal_duration").getValue()) * 20D));
        var portalData = new PortalData();
        var firstPos = new Vec3(point.x(), point.y(), point.z());
        var secondPos = clickPos;

        portalData.setPortalDuration(durationTicks);
        portalData.globalPos1 = PortalPos.of(firstLevel.dimension(), firstPos, point.yRot());
        portalData.globalPos2 = PortalPos.of(serverLevel.dimension(), secondPos, player.getYRot());

        var firstPortal = new PortalEntity(firstLevel, portalData);

        firstPortal.setOwnerUUID(player.getUUID());
        firstPortal.moveTo(firstPos);
        firstPortal.setYRot(point.yRot());
        firstLevel.addFreshEntity(firstPortal);

        var secondPortal = new PortalEntity(serverLevel, portalData);

        secondPortal.setOwnerUUID(player.getUUID());
        secondPortal.moveTo(secondPos);
        secondPortal.setYRot(player.getYRot());
        serverLevel.addFreshEntity(secondPortal);

        portalData.portalEntityId1 = firstPortal.getUUID();
        portalData.portalEntityId2 = secondPortal.getUUID();
        PortalManager.INSTANCE.addPortalData(firstPortal.getUUID(), portalData);
        PortalManager.INSTANCE.addPortalData(secondPortal.getUUID(), portalData);
        firstPortal.setPortalConnected();
        secondPortal.setPortalConnected();
        firstPortal.setTicksToLive(durationTicks);
        secondPortal.setTicksToLive(durationTicks);
        firstLevel.playSound(
                null,
                firstPos.x,
                firstPos.y,
                firstPos.z,
                SoundRegistry.ENDER_CAST.get(),
                SoundSource.PLAYERS,
                2F,
                0.9F + firstLevel.getRandom().nextFloat() * 0.2F
        );
        serverLevel.playSound(
                null,
                secondPos.x,
                secondPos.y,
                secondPos.z,
                SoundRegistry.ENDER_CAST.get(),
                SoundSource.PLAYERS,
                2F,
                0.9F + serverLevel.getRandom().nextFloat() * 0.2F
        );

        stack.set(
                RISASDataComponents.DIMENSION_KEY_ACTIVE_PORTALS.get(),
                new RISASDataComponents.DimensionKeyActivePortalData(
                        firstLevel.dimension(),
                        firstPortal.getUUID(),
                        serverLevel.dimension(),
                        secondPortal.getUUID(),
                        serverLevel.getGameTime() + durationTicks,
                        List.of()
                )
        );
        this.getRelicData(player, stack).getLevelingData().addExperience("dimension_key", "portal_pair_created", 1D);
        ability.getStatisticData().getMetricData("portal_pairs_created").addValue(1D);
        stack.remove(RISASDataComponents.DIMENSION_KEY_POINT.get());

        return InteractionResult.SUCCESS;
    }

    private static void clearActivePortals(ServerLevel serverLevel, ItemStack stack) {
        var active = stack.get(RISASDataComponents.DIMENSION_KEY_ACTIVE_PORTALS.get());

        if (active == null)
            return;

        var firstLevel = serverLevel.getServer().getLevel(active.firstLevel());

        if (firstLevel != null) {
            var entity = firstLevel.getEntity(active.firstPortal());

            if (entity != null)
                entity.discard();
        }

        PortalManager.INSTANCE.removePortalData(active.firstPortal());

        if (!active.firstPortal().equals(active.secondPortal())) {
            var secondLevel = serverLevel.getServer().getLevel(active.secondLevel());

            if (secondLevel != null) {
                var entity = secondLevel.getEntity(active.secondPortal());

                if (entity != null)
                    entity.discard();
            }

            PortalManager.INSTANCE.removePortalData(active.secondPortal());
        }

        stack.remove(RISASDataComponents.DIMENSION_KEY_ACTIVE_PORTALS.get());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity holder, int slotId, boolean isSelected) {
        if (!(holder instanceof Player player))
            return;

        var isInHands = isSelected || player.getOffhandItem() == stack;

        var point = stack.get(RISASDataComponents.DIMENSION_KEY_POINT.get());

        if (isInHands && point != null && point.level().equals(level.dimension()) && level.getGameTime() % 2L == 0L) {
            for (var i = 0; i < 2; i++) {
                level.addParticle(
                        ParticleHelper.PORTAL_FRAME,
                        point.x(),
                        point.y() + 1,
                        point.z(),
                        1D,
                        2D,
                        player.getYRot()
                );
            }
        }

        if (!(player instanceof ServerPlayer serverPlayer) || level.isClientSide())
            return;

        var state = stack.get(RISASDataComponents.DIMENSION_KEY_ACTIVE_PORTALS.get());

        if (state == null)
            return;

        var gameTime = level.getGameTime();

        if (state.expiresAtTick() <= gameTime) {
            stack.remove(RISASDataComponents.DIMENSION_KEY_ACTIVE_PORTALS.get());
            return;
        }

        var server = serverPlayer.getServer();

        if (server == null)
            return;

        var firstLevel = server.getLevel(state.firstLevel());
        var secondLevel = server.getLevel(state.secondLevel());
        PortalEntity firstPortal = null;
        PortalEntity secondPortal = null;

        if (firstLevel != null) {
            var entity = firstLevel.getEntity(state.firstPortal());

            if (entity instanceof PortalEntity portal)
                firstPortal = portal;
        }

        if (secondLevel != null) {
            var entity = secondLevel.getEntity(state.secondPortal());

            if (entity instanceof PortalEntity portal)
                secondPortal = portal;
        }

        if (firstPortal == null && secondPortal == null) {
            stack.remove(RISASDataComponents.DIMENSION_KEY_ACTIVE_PORTALS.get());
            return;
        }

        var relicData = this.getRelicData(player, stack);
        var ability = relicData.getAbilitiesData().getAbilityData("dimension_key");

        if (!ability.canPlayerUse(player))
            return;

        var processed = new HashSet<>(state.processedEntities());
        var nearby = new LinkedHashMap<UUID, Entity>();

        if (firstPortal != null) {
            for (var entity : firstPortal.level().getEntitiesOfClass(Entity.class, firstPortal.getBoundingBox().inflate(1.75D), Entity::isAlive))
                nearby.putIfAbsent(entity.getUUID(), entity);
        }

        if (secondPortal != null) {
            for (var entity : secondPortal.level().getEntitiesOfClass(Entity.class, secondPortal.getBoundingBox().inflate(1.75D), Entity::isAlive))
                nearby.putIfAbsent(entity.getUUID(), entity);
        }

        var changed = false;
        var portalTraversals = 0;

        for (var entity : nearby.values()) {
            var onCooldown = PortalManager.INSTANCE.isEntityOnCooldown(entity, state.firstPortal()) || PortalManager.INSTANCE.isEntityOnCooldown(entity, state.secondPortal());

            if (!onCooldown || !processed.add(entity.getUUID()))
                continue;

            changed = true;
            portalTraversals++;

            if (!(entity instanceof LivingEntity living))
                continue;

            if (living == serverPlayer && ability.isRankModifierUnlocked("immortal_crossing")) {
                var ticks = Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("immortality_duration").getValue()) * 20D));

                if (ticks > 0) {
                    serverPlayer.addEffect(new MobEffectInstance(RelicsMobEffects.IMMORTALITY, ticks, 0, false, true, true), serverPlayer);
                    ability.getStatisticData().getMetricData("immortality_duration_total").addValue(ticks / 20D);
                }
            } else if (!(living instanceof Player) && ability.isRankModifierUnlocked("tremor_passage")) {
                var ticks = Math.max(0, (int) Math.round(Math.max(0D, ability.getStatData("tremor_duration").getValue()) * 20D));

                if (ticks > 0) {
                    living.addEffect(new MobEffectInstance(RelicsMobEffects.TREMOR, ticks, 0, false, true, true), serverPlayer);
                    ability.getStatisticData().getMetricData("tremor_duration_total").addValue(ticks / 20D);
                }
            }
        }

        if (portalTraversals > 0) {
            relicData.getLevelingData().addExperience("dimension_key", "portal_traversal", portalTraversals);
            ability.getStatisticData().getMetricData("portal_traversals").addValue(portalTraversals);
        }

        for (var uuid : new ArrayList<>(processed)) {
            var onCooldown = false;

            if (firstLevel != null) {
                var entity = firstLevel.getEntity(uuid);

                if (entity != null && (PortalManager.INSTANCE.isEntityOnCooldown(entity, state.firstPortal()) || PortalManager.INSTANCE.isEntityOnCooldown(entity, state.secondPortal())))
                    onCooldown = true;
            }

            if (!onCooldown && secondLevel != null) {
                var entity = secondLevel.getEntity(uuid);

                if (entity != null && (PortalManager.INSTANCE.isEntityOnCooldown(entity, state.firstPortal()) || PortalManager.INSTANCE.isEntityOnCooldown(entity, state.secondPortal())))
                    onCooldown = true;
            }

            if (!onCooldown) {
                processed.remove(uuid);
                changed = true;
            }
        }

        if (!changed)
            return;

        stack.set(
                RISASDataComponents.DIMENSION_KEY_ACTIVE_PORTALS.get(),
                new RISASDataComponents.DimensionKeyActivePortalData(
                        state.firstLevel(),
                        state.firstPortal(),
                        state.secondLevel(),
                        state.secondPortal(),
                        state.expiresAtTick(),
                        List.copyOf(processed)
                )
        );
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingHeal(LivingHealEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getAmount() <= 0F)
                return;

            var bestBoost = 0D;
            var bestItem = (DimensionKeyItem) null;
            var bestStack = ItemStack.EMPTY;
            AbilityData bestAbility = null;
            var originalAmount = event.getAmount();

            for (var stack : player.getInventory().items) {
                if (!(stack.getItem() instanceof DimensionKeyItem item))
                    continue;

                var state = stack.get(RISASDataComponents.DIMENSION_KEY_ACTIVE_PORTALS.get());

                if (state == null)
                    continue;

                var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("dimension_key");

                if (!ability.canPlayerUse(player) || !ability.isRankModifierUnlocked("vital_gate"))
                    continue;

                var boost = Math.max(0D, ability.getStatData("regen_boost").getValue());
                var radius = Math.max(0D, ability.getStatData("vital_gate_radius").getValue());

                if (boost <= 0D || radius <= 0D)
                    continue;

                var nearPortal = false;
                var server = player.getServer();

                if (server == null)
                    continue;

                var firstLevel = server.getLevel(state.firstLevel());

                if (firstLevel != null && firstLevel == player.level()) {
                    var entity = firstLevel.getEntity(state.firstPortal());

                    if (entity instanceof PortalEntity && player.distanceToSqr(entity) <= radius * radius)
                        nearPortal = true;
                }

                if (!nearPortal) {
                    var secondLevel = server.getLevel(state.secondLevel());

                    if (secondLevel != null && secondLevel == player.level()) {
                        var entity = secondLevel.getEntity(state.secondPortal());

                        if (entity instanceof PortalEntity && player.distanceToSqr(entity) <= radius * radius)
                            nearPortal = true;
                    }
                }

                if (nearPortal && boost > bestBoost) {
                    bestBoost = boost;
                    bestItem = item;
                    bestStack = stack;
                    bestAbility = ability;
                }
            }

            for (var stack : player.getInventory().offhand) {
                if (!(stack.getItem() instanceof DimensionKeyItem item))
                    continue;

                var state = stack.get(RISASDataComponents.DIMENSION_KEY_ACTIVE_PORTALS.get());

                if (state == null)
                    continue;

                var ability = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("dimension_key");

                if (!ability.canPlayerUse(player) || !ability.isRankModifierUnlocked("vital_gate"))
                    continue;

                var boost = Math.max(0D, ability.getStatData("regen_boost").getValue());
                var radius = Math.max(0D, ability.getStatData("vital_gate_radius").getValue());

                if (boost <= 0D || radius <= 0D)
                    continue;

                var nearPortal = false;
                var server = player.getServer();

                if (server == null)
                    continue;

                var firstLevel = server.getLevel(state.firstLevel());

                if (firstLevel != null && firstLevel == player.level()) {
                    var entity = firstLevel.getEntity(state.firstPortal());

                    if (entity instanceof PortalEntity && player.distanceToSqr(entity) <= radius * radius)
                        nearPortal = true;
                }

                if (!nearPortal) {
                    var secondLevel = server.getLevel(state.secondLevel());

                    if (secondLevel != null && secondLevel == player.level()) {
                        var entity = secondLevel.getEntity(state.secondPortal());

                        if (entity instanceof PortalEntity && player.distanceToSqr(entity) <= radius * radius)
                            nearPortal = true;
                    }
                }

                if (nearPortal && boost > bestBoost) {
                    bestBoost = boost;
                    bestItem = item;
                    bestStack = stack;
                    bestAbility = ability;
                }
            }

            if (bestBoost <= 0D)
                return;

            event.setAmount((float) Math.max(0D, event.getAmount() * (1D + bestBoost)));

            if (bestItem != null && !bestStack.isEmpty() && bestAbility != null) {
                var bonusHealing = Math.max(0D, event.getAmount() - originalAmount);

                if (bonusHealing > 0D && bestAbility.isRankModifierUnlocked("vital_gate"))
                    bestAbility.getStatisticData().getMetricData("vital_gate_healing").addValue(bonusHealing);
            }
        }
    }
}
