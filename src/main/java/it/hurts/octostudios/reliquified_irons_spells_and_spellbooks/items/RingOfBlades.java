package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items;

import io.redspace.ironsspellbooks.entity.spells.blood_slash.BloodSlashProjectile;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.init.RISASItems;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items.misc.RISASLootEntries;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.network.payload.RingOfBladesAirSwingPayload;
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
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class RingOfBlades extends ISASRelic {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                        .ability(AbilityTemplate.builder("ring_of_blades")
                                .initialMaxLevel(10)
                                .rankModifier(1, "blade_leech")
                                .rankModifier(3, "blade_rampage")
                                .rankModifier(5, "multicast_arc")
                                .stat(AbilityStatTemplate.builder("slash_damage")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("blade_heal")
                                        .initialValue(0.5D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.1143D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("ramping_damage_bonus")
                                        .initialValue(0.05D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0857D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("max_multicast_slashes")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("multicast_chance")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("spawned_blade").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("slashes_spawned")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("slash_damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("blade_leech_healing")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("blade_leech", VisibilityState.OBFUSCATED)
                                                .build())
                                        .build())
                                .build())
                        .build())
                .loot(LootTemplate.builder()
                        .entry(RISASLootEntries.ANY_STRUCTURE)
                        .build())
                .build();
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onBloodSlashIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getSource().getDirectEntity() instanceof BloodSlashProjectile projectile))
                return;

            if (!projectile.getPersistentData().getBoolean("risas_ring_of_blades"))
                return;

            if (event.getSource() instanceof io.redspace.ironsspellbooks.damage.SpellDamageSource source && source.getLifestealPercent() > 0F)
                source.setLifestealPercent(0F);
        }

        @SubscribeEvent
        public static void onBloodSlashDamagePost(LivingDamageEvent.Post event) {
            if (event.getNewDamage() <= 0F)
                return;

            if (!(event.getSource().getDirectEntity() instanceof BloodSlashProjectile projectile))
                return;

            if (!(projectile.getOwner() instanceof ServerPlayer player))
                return;

            if (!projectile.getPersistentData().getBoolean("risas_ring_of_blades"))
                return;

            var relic = findBestActiveRelic(player);

            if (relic == null)
                return;

            relic.ability().getStatisticData().getMetricData("slash_damage_dealt").addValue(event.getNewDamage());

            if (relic.ability().isRankModifierUnlocked("blade_leech")) {
                var heal = Math.max(0D, relic.ability().getStatData("blade_heal").getValue());

                if (heal > 0D) {
                    var healthBefore = player.getHealth();
                    player.heal((float) heal);
                    relic.ability().getStatisticData().getMetricData("blade_leech_healing").addValue(Math.max(0D, player.getHealth() - healthBefore));
                }
            }

            if (relic.ability().isRankModifierUnlocked("blade_rampage")) {
                var bonus = Math.max(0D, relic.ability().getStatData("ramping_damage_bonus").getValue());

                if (bonus > 0D) {
                    var currentDamage = projectile.getPersistentData().getFloat("risas_ring_of_blades_damage");

                    if (currentDamage <= 0F)
                        currentDamage = (float) Math.max(0D, event.getNewDamage());

                    currentDamage = (float) Math.max(0D, currentDamage * (1D + bonus));

                    projectile.setDamage(currentDamage);
                    projectile.getPersistentData().putFloat("risas_ring_of_blades_damage", currentDamage);
                }
            }

        }

        public static void tryTriggerOnAirSwing(ServerPlayer player) {
            var relic = findBestActiveRelic(player);

            if (relic == null)
                return;

            var mainHand = player.getMainHandItem();

            if (mainHand.isEmpty())
                return;

            var attackDamage = new double[]{0D};

            mainHand.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> {
                if (attribute.equals(Attributes.ATTACK_DAMAGE))
                    attackDamage[0] += modifier.amount();
            });

            if (attackDamage[0] <= 0D)
                return;

            if (player.getAttackStrengthScale(0.5F) < 0.99F)
                return;

            var eye = player.getEyePosition();
            var look = player.getLookAngle();
            var blockReach = Math.max(0D, player.blockInteractionRange());
            var entityReach = Math.max(0D, player.entityInteractionRange());
            var maxReach = Math.max(blockReach, entityReach);

            if (maxReach > 0D) {
                var end = eye.add(look.scale(maxReach));
                var blockHit = player.level().clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

                if (blockHit.getType() != HitResult.Type.MISS) {
                    var distanceSq = blockHit.getLocation().distanceToSqr(eye);

                    if (distanceSq <= blockReach * blockReach)
                        return;
                }

                var entityHit = ProjectileUtil.getEntityHitResult(
                        player,
                        eye,
                        end,
                        player.getBoundingBox().expandTowards(look.scale(maxReach)).inflate(1D),
                        entity -> !entity.isSpectator() && entity.isPickable() && entity != player,
                        maxReach * maxReach
                );

                if (entityHit != null) {
                    var distanceSq = entityHit.getLocation().distanceToSqr(eye);

                    if (distanceSq <= entityReach * entityReach)
                        return;
                }
            }

            var slashDamage = (float) Math.max(0D, relic.ability().getStatData("slash_damage").getValue());

            if (slashDamage <= 0F)
                return;

            var level = (ServerLevel) player.level();
            var slashes = 1;
            var relicData = relic.item().getRelicData(player, relic.stack());

            if (relic.ability().isRankModifierUnlocked("multicast_arc")) {
                var maxSlashes = Math.max(1, (int) Math.round(Math.max(0D, relic.ability().getStatData("max_multicast_slashes").getValue())));
                var chance = Mth.clamp(relic.ability().getStatData("multicast_chance").getValue(), 0D, 1D);

                if (maxSlashes > 1 && chance > 0D)
                    slashes = Math.max(1, MathUtils.multicast(level.getRandom(), chance, maxSlashes));
            }

            var directionBase = player.getLookAngle().normalize();
            var spread = slashes <= 1 ? 0D : Math.min(80D, 12D * (slashes - 1));
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundRegistry.BLOOD_CAST.get(), SoundSource.PLAYERS, 2F, 0.9F + level.getRandom().nextFloat() * 0.2F);

            for (var i = 0; i < slashes; i++) {
                var offset = slashes <= 1 ? 0D : -spread / 2D + spread * (double) i / (double) (slashes - 1);
                var direction = directionBase.yRot((float) Math.toRadians(offset)).normalize();
                var slash = new BloodSlashProjectile(level, player);

                slash.setPos(player.getEyePosition().add(direction.scale(0.2D)));
                slash.shoot(direction);
                slash.setDamage(slashDamage);
                slash.getPersistentData().putBoolean("risas_ring_of_blades", true);
                slash.getPersistentData().putFloat("risas_ring_of_blades_damage", slashDamage);

                level.addFreshEntity(slash);
                relic.ability().getStatisticData().getMetricData("slashes_spawned").addValue(1D);
                relicData.getLevelingData().addExperience("ring_of_blades", "spawned_blade", 1D);
            }


        }

        @Nullable
        private static ActiveRelic findBestActiveRelic(Player player) {
            var highestDamage = -1D;
            ActiveRelic bestRelic = null;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.RING_OF_BLADES.value())) {
                if (!(stack.getItem() instanceof RingOfBlades relic))
                    continue;

                var ability = relic.getRelicData(player, stack).getAbilitiesData().getAbilityData("ring_of_blades");

                if (!ability.canPlayerUse(player))
                    continue;

                var slashDamage = Math.max(0D, ability.getStatData("slash_damage").getValue());

                if (slashDamage <= highestDamage)
                    continue;

                highestDamage = slashDamage;
                bestRelic = new ActiveRelic(relic, stack, ability);
            }

            return bestRelic;
        }

        private record ActiveRelic(RingOfBlades item, ItemStack stack, AbilityData ability) {
        }
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
            if (!event.getEntity().level().isClientSide())
                return;

            PacketDistributor.sendToServer(RingOfBladesAirSwingPayload.INSTANCE);
        }
    }
}
