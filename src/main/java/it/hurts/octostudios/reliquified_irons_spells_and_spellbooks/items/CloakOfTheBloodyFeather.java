package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items;

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
import it.hurts.sskirillss.relics.init.RelicsMobEffects;
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

public class CloakOfTheBloodyFeather extends ISASRelic {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("blood_feather")
                                .initialMaxLevel(10)
                                .rankModifier(1, "execution_heal")
                                .rankModifier(3, "needle_bleeding")
                                .rankModifier(5, "needle_recycle")
                                .stat(AbilityStatTemplate.builder("buffer_chance")
                                        .initialValue(0.05D, 0.15D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0381D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("buffered_needles")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("needle_damage")
                                        .initialValue(1.5D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("kill_heal")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("bleeding_level")
                                        .initialValue(1D, 2D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("bleeding_duration")
                                        .initialValue(3D, 5D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("needle_hit").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("needles_released")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("needle_hits")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("execution_healing")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("execution_heal", VisibilityState.OBFUSCATED)
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("bleeding_duration_total")
                                                .formatValue(value -> MathUtils.formatTime(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .rankModifierVisibilityState("needle_bleeding", VisibilityState.OBFUSCATED)
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

        var level = (ServerLevel) player.level();
        var gameTime = level.getGameTime();
        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("blood_feather");

        if (!ability.canPlayerUse(player))
            return;

        var buffer = CommonEvents.getBuffer(stack, level.dimension());

        if (buffer.isIdle()) {
            CommonEvents.clearBuffer(stack);
            return;
        }

        if (!buffer.isReleasing()) {
            if (gameTime - buffer.lastDamageTick() < 60L) {
                CommonEvents.setBuffer(stack, buffer);
                return;
            }

            buffer = buffer.beginRelease();
        }

        if (!buffer.isReleasing()) {
            CommonEvents.clearBuffer(stack);
            return;
        }

        var releaseTotal = buffer.releaseTotalNeedles();

        if (releaseTotal <= 0) {
            CommonEvents.clearBuffer(stack);
            return;
        }

        var releaseIndex = buffer.currentReleaseIndex();
        buffer = buffer.releaseOne();

        var damagePerNeedle = (float) Math.max(0D, ability.getStatData("needle_damage").getValue());

        if (damagePerNeedle > 0F) {
            var releaseStartTick = gameTime - releaseIndex;
            var offsetSeed = Math.floorMod(
                    releaseStartTick * 341873128712L + player.getUUID().getMostSignificantBits() + player.getUUID().getLeastSignificantBits(),
                    3600L
            );
            var angleOffset = (2D * Math.PI * (double) offsetSeed) / 3600D;
            var angle = angleOffset + (2D * Math.PI * releaseIndex) / releaseTotal;
            var direction = new Vec3(Math.cos(angle), 0.05D, Math.sin(angle)).normalize();
            var spawnPos = player.position()
                    .add(0D, player.getEyeHeight() * 0.6D, 0D)
                    .add(direction.scale(0.8D));
            var needle = new BloodNeedle(level, player);

            needle.moveTo(spawnPos);
            needle.shoot(direction.scale(0.45D));
            needle.setDamage(damagePerNeedle);
            needle.setScale(0.4F);
            needle.getPersistentData().putBoolean("risas_cloak_of_the_bloody_feather", true);

            level.addFreshEntity(needle);
            ability.getStatisticData().getMetricData("needles_released").addValue(1D);
        }


        CommonEvents.setBuffer(stack, buffer);
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onNeedleIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getSource().getDirectEntity() instanceof BloodNeedle needle))
                return;

            if (!needle.getPersistentData().getBoolean("risas_cloak_of_the_bloody_feather"))
                return;

            if (event.getSource() instanceof io.redspace.ironsspellbooks.damage.SpellDamageSource source && source.getLifestealPercent() > 0F)
                source.setLifestealPercent(0F);
        }

        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (!(event.getEntity().level() instanceof ServerLevel level) || event.getNewDamage() <= 0F)
                return;

            var gameTime = level.getGameTime();

            if (event.getSource().getDirectEntity() instanceof BloodNeedle needle
                    && needle.getPersistentData().getBoolean("risas_cloak_of_the_bloody_feather")
                    && event.getSource().getEntity() instanceof ServerPlayer owner) {
                ItemStack bestStack = ItemStack.EMPTY;
                CloakOfTheBloodyFeather bestRelic = null;
                AbilityData bestAbility = null;
                var bestDamage = -1D;

                for (var stack : EntityUtils.findEquippedCurios(owner, RISASItems.CLOAK_OF_THE_BLOODY_FEATHER.value())) {
                    if (!(stack.getItem() instanceof CloakOfTheBloodyFeather relic))
                        continue;

                    var ability = relic.getRelicData(owner, stack).getAbilitiesData().getAbilityData("blood_feather");

                    if (!ability.canPlayerUse(owner))
                        continue;

                    var needleDamage = Math.max(0D, ability.getStatData("needle_damage").getValue());

                    if (needleDamage <= bestDamage)
                        continue;

                    bestDamage = needleDamage;
                    bestStack = stack;
                    bestRelic = relic;
                    bestAbility = ability;
                }

                if (bestAbility != null && bestRelic != null && !bestStack.isEmpty()) {
                    var relicData = bestRelic.getRelicData(owner, bestStack);

                    bestAbility.getStatisticData().getMetricData("needle_hits").addValue(1D);
                    relicData.getLevelingData().addExperience("blood_feather", "needle_hit", 1D);
                }

                if (!bestStack.isEmpty() && bestAbility != null && bestAbility.isRankModifierUnlocked("needle_recycle")) {
                    var buffer = getBuffer(bestStack, level.dimension()).withAddedPendingNeedles(1);

                    setBuffer(bestStack, buffer);
                }

                if (event.getEntity().isAlive() && bestAbility != null && bestAbility.isRankModifierUnlocked("needle_bleeding")) {
                    var bleedingLevel = Math.max(1, (int) Math.round(Math.max(0D, bestAbility.getStatData("bleeding_level").getValue())));
                    var bleedingDurationTicks = Math.max(1, (int) Math.round(Math.max(0D, bestAbility.getStatData("bleeding_duration").getValue()) * 20D));

                    event.getEntity().addEffect(new MobEffectInstance(
                            RelicsMobEffects.BLEEDING,
                            bleedingDurationTicks,
                            Math.max(0, bleedingLevel - 1),
                            false,
                            true,
                            true
                    ), owner);
                    bestAbility.getStatisticData().getMetricData("bleeding_duration_total").addValue(bleedingDurationTicks / 20D);
                }
            }

            if (!(event.getEntity() instanceof ServerPlayer player))
                return;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.CLOAK_OF_THE_BLOODY_FEATHER.value())) {
                if (!(stack.getItem() instanceof CloakOfTheBloodyFeather relic))
                    continue;

                var ability = relic.getRelicData(player, stack).getAbilitiesData().getAbilityData("blood_feather");

                if (!ability.canPlayerUse(player))
                    continue;

                var buffer = getBuffer(stack, level.dimension()).onDamageTaken(gameTime);
                var chance = Mth.clamp(ability.getStatData("buffer_chance").getValue(), 0D, 1D);

                if (chance > 0D && player.getRandom().nextDouble() < chance) {
                    var needles = Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("buffered_needles").getValue())));

                    buffer = buffer.withAddedPendingNeedles(needles);
                }

                setBuffer(stack, buffer);
            }
        }

        @SubscribeEvent
        public static void onLivingDeath(LivingDeathEvent event) {
            if (event.getSource().getDirectEntity() instanceof BloodNeedle needle
                    && needle.getPersistentData().getBoolean("risas_cloak_of_the_bloody_feather")
                    && event.getSource().getEntity() instanceof ServerPlayer owner) {
                AbilityData bestAbility = null;
                var bestHeal = -1D;

                for (var stack : EntityUtils.findEquippedCurios(owner, RISASItems.CLOAK_OF_THE_BLOODY_FEATHER.value())) {
                    if (!(stack.getItem() instanceof CloakOfTheBloodyFeather relic))
                        continue;

                    var ability = relic.getRelicData(owner, stack).getAbilitiesData().getAbilityData("blood_feather");

                    if (!ability.canPlayerUse(owner) || !ability.isRankModifierUnlocked("execution_heal"))
                        continue;

                    var heal = Math.max(0D, ability.getStatData("kill_heal").getValue());

                    if (heal <= bestHeal)
                        continue;

                    bestHeal = heal;
                    bestAbility = ability;
                }

                if (bestAbility != null && bestHeal > 0D) {
                    var healthBefore = owner.getHealth();
                    owner.heal((float) bestHeal);
                    var healed = Math.max(0D, owner.getHealth() - healthBefore);

                    if (healed > 0D)
                        bestAbility.getStatisticData().getMetricData("execution_healing").addValue(healed);

                }
            }

            if (!(event.getEntity() instanceof Player player))
                return;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.CLOAK_OF_THE_BLOODY_FEATHER.value())) {
                if (stack.getItem() instanceof CloakOfTheBloodyFeather)
                    clearBuffer(stack);
            }
        }

        private static RISASDataComponents.CloakBufferData getBuffer(ItemStack stack, ResourceKey<Level> level) {
            var buffer = stack.get(RISASDataComponents.CLOAK_OF_THE_BLOODY_FEATHER_BUFFER.get());

            if (buffer == null || !buffer.level().equals(level))
                return RISASDataComponents.CloakBufferData.empty(level);

            return buffer;
        }

        private static void setBuffer(ItemStack stack, RISASDataComponents.CloakBufferData buffer) {
            if (buffer.isIdle()) {
                clearBuffer(stack);
                return;
            }

            stack.set(RISASDataComponents.CLOAK_OF_THE_BLOODY_FEATHER_BUFFER.get(), buffer);
        }

        private static void clearBuffer(ItemStack stack) {
            stack.remove(RISASDataComponents.CLOAK_OF_THE_BLOODY_FEATHER_BUFFER.get());
        }
    }
}
