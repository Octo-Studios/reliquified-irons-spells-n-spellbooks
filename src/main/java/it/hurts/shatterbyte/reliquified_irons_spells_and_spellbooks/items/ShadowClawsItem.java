package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.api.item.weapons.ExtendedSwordItem;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.damage.SpellDamageSource;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASDataComponents;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.misc.RISASLootEntries;
import it.hurts.sskirillss.relics.api.relics.AbilityMetricTemplate;
import it.hurts.sskirillss.relics.api.relics.AbilityStatisticTemplate;
import it.hurts.sskirillss.relics.api.relics.IRelicItem;
import it.hurts.sskirillss.relics.api.relics.RelicTemplate;
import it.hurts.sskirillss.relics.api.relics.VisibilityState;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilitiesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.AbilityTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourceTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.ExperienceSourcesTemplate;
import it.hurts.sskirillss.relics.api.relics.abilities.stats.AbilityStatTemplate;
import it.hurts.sskirillss.relics.init.RelicsCreativeTabs;
import it.hurts.sskirillss.relics.init.RelicsMobEffects;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.misc.CreativeContentConstructor;
import it.hurts.sskirillss.relics.items.misc.ICreativeTabContent;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class ShadowClawsItem extends ExtendedSwordItem implements IRelicItem, ICreativeTabContent {
    public ShadowClawsItem() {
        super(Tiers.NETHERITE, new Item.Properties().stacksTo(1).component(DataComponents.UNBREAKABLE, new Unbreakable(false)));
    }

    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("shadow_claws")
                                .rankModifier(1, "mana_exploit")
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
                                .stat(AbilityStatTemplate.builder("mana_steal")
                                        .initialValue(5D, 10D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("mana_less_damage_bonus")
                                        .initialValue(0.25D, 0.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("full_charge_attack").build())
                                        .source(ExperienceSourceTemplate.builder("mana_steal_success").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("mana_stolen")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .build())
                                .build())
                        .ability(AbilityTemplate.builder("shadow_slash")
                                .rankModifier(3, "bleeding_slash")
                                .rankModifier(5, "charge_recovery")
                                .stat(AbilityStatTemplate.builder("max_charges")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("charge_regen_time")
                                        .initialValue(30D, 45D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), -0.0222D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("shadow_slash_damage")
                                        .initialValue(5D, 7.5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0476D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("bleeding_level")
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("bleeding_duration")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("charge_refund_chance")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("ability_use").build())
                                        .source(ExperienceSourceTemplate.builder("ability_hit").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("ability_triggers")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("ability_hits")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("ability_damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("bleeding_duration_total")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("bleeding_slash", VisibilityState.OBFUSCATED)
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
    public String getConfigRoute() {
        return ReliquifiedIronsSpellsAndSpellbooks.MODID;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        var relicData = this.getRelicData(player, stack);

        if (hand != InteractionHand.MAIN_HAND)
            return InteractionResultHolder.pass(stack);

        var ability = relicData.getAbilitiesData().getAbilityData("shadow_slash");

        if (!ability.canPlayerUse(player))
            return InteractionResultHolder.fail(stack);

        if (level instanceof ServerLevel serverLevel) {
            var gameTime = serverLevel.getGameTime();
            var maxCharges = Math.max(1D, ability.getStatData("max_charges").getValue());
            var rechargeTicks = Math.max(1L, Math.round(Math.max(0D, ability.getStatData("charge_regen_time").getValue()) * 20D));
            var charges = stack.get(RISASDataComponents.SHADOW_CLAWS_CHARGES.get());
            var lastUpdate = stack.get(RISASDataComponents.SHADOW_CLAWS_LAST_UPDATE.get());

            if (charges == null || lastUpdate == null) {
                charges = maxCharges;
                lastUpdate = gameTime;
            } else {
                charges = Math.min(maxCharges, Math.max(0D, charges) + (double) Math.max(0L, gameTime - lastUpdate) / (double) rechargeTicks);
            }

            if (charges < 1D) {
                stack.set(RISASDataComponents.SHADOW_CLAWS_CHARGES.get(), charges);
                stack.set(RISASDataComponents.SHADOW_CLAWS_LAST_UPDATE.get(), gameTime);
                return InteractionResultHolder.fail(stack);
            }

            charges = Math.max(0D, charges - 1D);

            stack.set(RISASDataComponents.SHADOW_CLAWS_CHARGES.get(), charges);
            stack.set(RISASDataComponents.SHADOW_CLAWS_LAST_UPDATE.get(), gameTime);
            stack.set(RISASDataComponents.SHADOW_CLAWS_SHADOW_SLASH_ACTIVE.get(), true);
            stack.set(RISASDataComponents.SHADOW_CLAWS_SHADOW_SLASH_HIT.get(), false);
            relicData.getLevelingData().addExperience("shadow_slash", "ability_use", 1D);
            ability.getStatisticData().getMetricData("ability_triggers").addValue(1D);

            var lookDirection = player.getLookAngle().normalize();
            var sideDirection = lookDirection.cross(new Vec3(0D, 1D, 0D));

            if (sideDirection.lengthSqr() < 1.0E-6D)
                sideDirection = new Vec3(1D, 0D, 0D);
            else
                sideDirection = sideDirection.normalize();

            var origin = player.position();
            var originDelta = player.getDeltaMovement();
            var originFallDistance = player.fallDistance;
            var centerDelta = originDelta;
            var centerFallDistance = originFallDistance;
            var spell = SpellRegistry.SHADOW_SLASH.get();
            var magicData = MagicData.getPlayerMagicData(player);
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.SHADOW_SLASH.get(), player.getSoundSource(), 1F, 1F);

            for (var side = -1; side <= 1; side++) {
                var sideOffset = sideDirection.scale(side * 0.7D);

                if (side != 0)
                    player.setPos(origin.x + sideOffset.x, origin.y + sideOffset.y, origin.z + sideOffset.z);

                var previousDelta = player.getDeltaMovement();
                var previousFallDistance = player.fallDistance;

                spell.onCast(serverLevel, 1, player, CastSource.SWORD, magicData);

                if (side == 0) {
                    centerDelta = player.getDeltaMovement();
                    centerFallDistance = player.fallDistance;
                } else {
                    player.setPos(origin.x, origin.y, origin.z);
                    player.setDeltaMovement(previousDelta);
                    player.fallDistance = previousFallDistance;
                }
            }

            player.setPos(origin.x, origin.y, origin.z);
            player.setDeltaMovement(centerDelta);
            player.fallDistance = centerFallDistance;

            if (stack.getOrDefault(RISASDataComponents.SHADOW_CLAWS_SHADOW_SLASH_HIT.get(), false)
                    && ability.isRankModifierUnlocked("charge_recovery")
                    && serverLevel.getRandom().nextDouble() < Mth.clamp(ability.getStatData("charge_refund_chance").getValue(), 0D, 1D)) {
                stack.set(RISASDataComponents.SHADOW_CLAWS_CHARGES.get(), Math.min(maxCharges, charges + 1D));
            }

            stack.remove(RISASDataComponents.SHADOW_CLAWS_SHADOW_SLASH_ACTIVE.get());
            stack.remove(RISASDataComponents.SHADOW_CLAWS_SHADOW_SLASH_HIT.get());
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof ServerPlayer player) || level.isClientSide())
            return;

        if (!stack.has(DataComponents.UNBREAKABLE))
            stack.set(DataComponents.UNBREAKABLE, new Unbreakable(false));

        if (stack.getOrDefault(DataComponents.DAMAGE, 0) > 0)
            stack.set(DataComponents.DAMAGE, 0);

        var abilities = this.getRelicData(player, stack).getAbilitiesData();
        var slashAbility = abilities.getAbilityData("shadow_slash");

        if (slashAbility.canPlayerUse(player)) {
            var gameTime = level.getGameTime();
            var maxCharges = Math.max(1D, slashAbility.getStatData("max_charges").getValue());
            var rechargeTicks = Math.max(1L, Math.round(Math.max(0D, slashAbility.getStatData("charge_regen_time").getValue()) * 20D));
            var charges = stack.get(RISASDataComponents.SHADOW_CLAWS_CHARGES.get());
            var lastUpdate = stack.get(RISASDataComponents.SHADOW_CLAWS_LAST_UPDATE.get());

            if (charges == null || lastUpdate == null) {
                stack.set(RISASDataComponents.SHADOW_CLAWS_CHARGES.get(), maxCharges);
                stack.set(RISASDataComponents.SHADOW_CLAWS_LAST_UPDATE.get(), gameTime);
            } else {
                stack.set(
                        RISASDataComponents.SHADOW_CLAWS_CHARGES.get(),
                        Math.min(maxCharges, Math.max(0D, charges) + (double) Math.max(0L, gameTime - lastUpdate) / (double) rechargeTicks)
                );
                stack.set(RISASDataComponents.SHADOW_CLAWS_LAST_UPDATE.get(), gameTime);
            }
        }

        if (player.getMainHandItem() == stack) {
            var weaponAbility = abilities.getAbilityData("shadow_claws");

            if (weaponAbility.canPlayerUse(player)) {
                var damage = Math.max(0D, weaponAbility.getStatData("weapon_damage").getValue());
                var attackSpeed = Math.max(0D, weaponAbility.getStatData("weapon_attack_speed").getValue());
                var desired = SwordItem.createAttributes(Tiers.NETHERITE, (float) Math.max(-Tiers.NETHERITE.getAttackDamageBonus(), damage - 1D - Tiers.NETHERITE.getAttackDamageBonus()), (float) (attackSpeed - 4D));

                if (!desired.equals(stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY)))
                    stack.set(DataComponents.ATTRIBUTE_MODIFIERS, desired);
            }
        }
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
        public static void onAttackEntity(AttackEntityEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide())
                return;

            var stack = player.getMainHandItem();

            if (!(stack.getItem() instanceof ShadowClawsItem item))
                return;

            var weaponAbility = item.getRelicData(player, stack).getAbilitiesData().getAbilityData("shadow_claws");

            if (!weaponAbility.canPlayerUse(player))
                return;

            if (!(event.getTarget() instanceof net.minecraft.world.entity.LivingEntity target) || !target.isAlive())
                return;

            if (Math.max(player.getAttackStrengthScale(0F), player.getAttackStrengthScale(0.5F)) < 0.99F)
                return;

            var relicData = item.getRelicData(player, stack);

            relicData.getLevelingData().addExperience("shadow_claws", "full_charge_attack", 1D);

            var manaSteal = Math.max(0D, weaponAbility.getStatData("mana_steal").getValue());

            if (manaSteal <= 0D)
                return;

            var targetMagic = MagicData.getPlayerMagicData(target);
            var targetMana = Math.max(0F, targetMagic.getMana());
            var stolenMana = (float) Math.min(manaSteal, targetMana);

            if (stolenMana <= 0F)
                return;

            targetMagic.setMana(targetMana - stolenMana);
            if (target instanceof ServerPlayer targetPlayer)
                PacketDistributor.sendToPlayer(targetPlayer, new SyncManaPacket(targetMagic));
            var playerMagic = MagicData.getPlayerMagicData(player);
            playerMagic.addMana(stolenMana);
            PacketDistributor.sendToPlayer(player, new SyncManaPacket(playerMagic));
            weaponAbility.getStatisticData().getMetricData("mana_stolen").addValue(stolenMana);
            relicData.getLevelingData().addExperience("shadow_claws", "mana_steal_success", 1D);
        }

        @SubscribeEvent
        public static void onLivingDamagePre(LivingDamageEvent.Pre event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getEntity() == player || event.getNewDamage() <= 0F)
                return;

            var stack = player.getMainHandItem();

            if (!(stack.getItem() instanceof ShadowClawsItem item))
                return;

            var abilities = item.getRelicData(player, stack).getAbilitiesData();
            var isShadowSlash = event.getSource() instanceof SpellDamageSource spellDamageSource
                    && spellDamageSource.spell() == SpellRegistry.SHADOW_SLASH.get()
                    && stack.getOrDefault(RISASDataComponents.SHADOW_CLAWS_SHADOW_SLASH_ACTIVE.get(), false);
            var isWeaponHit = event.getSource().getDirectEntity() == player && !(event.getSource() instanceof SpellDamageSource);

            if (!isShadowSlash && !isWeaponHit)
                return;

            if (isShadowSlash) {
                var slashAbility = abilities.getAbilityData("shadow_slash");

                if (!slashAbility.canPlayerUse(player))
                    return;

                event.setNewDamage((float) Math.max(0D, slashAbility.getStatData("shadow_slash_damage").getValue()));
                return;
            }

            var weaponAbility = abilities.getAbilityData("shadow_claws");

            if (!weaponAbility.canPlayerUse(player) || !weaponAbility.isRankModifierUnlocked("mana_exploit"))
                return;

            if (!(event.getEntity() instanceof Player) && !(event.getEntity() instanceof IMagicEntity))
                return;

            var manaAttribute = event.getEntity().getAttribute(AttributeRegistry.MAX_MANA);

            if (manaAttribute == null)
                return;

            var maxMana = (float) Math.max(0D, manaAttribute.getValue());

            if (maxMana <= 0F)
                return;

            var targetMana = Mth.clamp(MagicData.getPlayerMagicData(event.getEntity()).getMana(), 0F, maxMana);
            var missingManaFraction = Mth.clamp((maxMana - targetMana) / maxMana, 0F, 1F);

            if (missingManaFraction <= 0F)
                return;

            var bonus = Math.max(0D, weaponAbility.getStatData("mana_less_damage_bonus").getValue()) * missingManaFraction;

            if (bonus > 0D)
                event.setNewDamage((float) Math.max(0D, event.getNewDamage() * (1D + bonus)));
        }

        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (!(event.getSource().getEntity() instanceof ServerPlayer player) || event.getEntity() == player || event.getNewDamage() <= 0F)
                return;

            var stack = player.getMainHandItem();

            if (!(stack.getItem() instanceof ShadowClawsItem item))
                return;

            var relicData = item.getRelicData(player, stack);
            var abilities = relicData.getAbilitiesData();
            var isShadowSlash = event.getSource() instanceof SpellDamageSource spellDamageSource
                    && spellDamageSource.spell() == SpellRegistry.SHADOW_SLASH.get();
            var isActiveShadowSlash = isShadowSlash && stack.getOrDefault(RISASDataComponents.SHADOW_CLAWS_SHADOW_SLASH_ACTIVE.get(), false);
            var isWeaponHit = event.getSource().getDirectEntity() == player && !isShadowSlash && !(event.getSource() instanceof SpellDamageSource);

            if (isWeaponHit) {
                var weaponAbility = abilities.getAbilityData("shadow_claws");

                if (!weaponAbility.canPlayerUse(player))
                    return;

                weaponAbility.getStatisticData().getMetricData("damage_dealt").addValue(event.getNewDamage());
            }

            if (!isActiveShadowSlash)
                return;

            stack.set(RISASDataComponents.SHADOW_CLAWS_SHADOW_SLASH_HIT.get(), true);

            var slashAbility = abilities.getAbilityData("shadow_slash");

            if (!slashAbility.canPlayerUse(player))
                return;

            relicData.getLevelingData().addExperience("shadow_slash", "ability_hit", 1D);
            slashAbility.getStatisticData().getMetricData("ability_hits").addValue(1D);
            slashAbility.getStatisticData().getMetricData("ability_damage_dealt").addValue(event.getNewDamage());

            if (!slashAbility.isRankModifierUnlocked("bleeding_slash") || !event.getEntity().isAlive())
                return;

            var bleedingLevel = Math.max(1, (int) Math.round(Math.max(0D, slashAbility.getStatData("bleeding_level").getValue())));
            var bleedingDurationTicks = Math.max(1, (int) Math.round(Math.max(0D, slashAbility.getStatData("bleeding_duration").getValue()) * 20D));

            slashAbility.getStatisticData().getMetricData("bleeding_duration_total").addValue(bleedingDurationTicks / 20D);

            event.getEntity().addEffect(new MobEffectInstance(
                    RelicsMobEffects.BLEEDING,
                    bleedingDurationTicks,
                    Math.max(0, bleedingLevel - 1),
                    false,
                    true,
                    true
            ), player);
        }
    }
}
