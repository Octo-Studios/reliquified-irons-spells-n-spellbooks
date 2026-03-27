package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities.DragonBloodDropletEntity;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities.DragonBloodPoolEntity;
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
import it.hurts.sskirillss.relics.init.RelicsScalingModels;
import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootTemplate;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import top.theillusivec4.curios.api.SlotContext;

public class DragonBloodVialItem extends ISASWearableRelicItem {
    @Override
    public RelicTemplate constructDefaultRelicTemplate() {
        return RelicTemplate.builder()
                .abilities(AbilitiesTemplate.builder()
                                .ability(AbilityTemplate.builder("dragon_blood")
                                .rankModifier(1, "dragon_regeneration")
                                .rankModifier(3, "magnetic_drop")
                                .rankModifier(5, "split_burst")
                                .stat(AbilityStatTemplate.builder("trigger_chance")
                                        .initialValue(0.05D, 0.15D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("pool_radius")
                                        .initialValue(0.5D, 1D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0429D)
                                        .formatValue(value -> MathUtils.round(value, 1))
                                        .build())
                                .stat(AbilityStatTemplate.builder("pool_damage")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("regeneration_per_second")
                                        .initialValue(0.25D, 0.75D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> MathUtils.round(value, 2))
                                        .build())
                                .stat(AbilityStatTemplate.builder("split_chance")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0286D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .stat(AbilityStatTemplate.builder("split_limit")
                                        .initialValue(1D, 3D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0667D)
                                        .formatValue(value -> Math.max(1, (int) MathUtils.round(value, 0)))
                                        .build())
                                .stat(AbilityStatTemplate.builder("split_power")
                                        .initialValue(0.1D, 0.25D)
                                        .upgradeModifier(RelicsScalingModels.MULTIPLICATIVE_BASE.get(), 0.0571D)
                                        .formatValue(value -> (int) MathUtils.round(value * 100D, 0))
                                        .build())
                                .experienceSources(ExperienceSourcesTemplate.builder()
                                        .source(ExperienceSourceTemplate.builder("dragon_blood_droplet_created").build())
                                        .build())
                                .statistic(AbilityStatisticTemplate.builder()
                                        .metric(AbilityMetricTemplate.builder("dragon_blood_droplets_thrown")
                                                .formatValue(value -> String.valueOf(Math.max(0, (int) MathUtils.round(value, 0))))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("dragon_blood_pool_damage_dealt")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .build())
                                        .metric(AbilityMetricTemplate.builder("dragon_regeneration_healing")
                                                .formatValue(value -> String.valueOf(MathUtils.round(value, 2)))
                                                .rankModifierVisibilityState("dragon_regeneration", VisibilityState.OBFUSCATED)
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
        if (!(slotContext.entity() instanceof ServerPlayer player) || player.level().isClientSide() || player.tickCount % 20 != 0)
            return;

        var ability = this.getRelicData(player, stack).getAbilitiesData().getAbilityData("dragon_blood");

        if (!ability.canPlayerUse(player) || !ability.isRankModifierUnlocked("dragon_regeneration"))
            return;

        var regenerationPerSecond = Math.max(0D, ability.getStatData("regeneration_per_second").getValue());

        if (regenerationPerSecond <= 0D)
            return;

        var healScale = 0D;

        for (var pool : player.level().getEntitiesOfClass(DragonBloodPoolEntity.class, player.getBoundingBox().inflate(8D), pool -> pool.isAlive()
                && pool.getOwner() != null
                && pool.getOwner().getUUID().equals(player.getUUID())
                && pool.getBoundingBox().inflate(0.05D).intersects(player.getBoundingBox()))) {
            healScale = Math.max(healScale, Math.max(0D, pool.getPowerScale()));
        }

        if (healScale <= 0D)
            return;

        var healthBefore = player.getHealth();
        player.heal((float) (regenerationPerSecond * healScale));

        if (player.getHealth() > healthBefore)
            ability.getStatisticData().getMetricData("dragon_regeneration_healing").addValue(player.getHealth() - healthBefore);
    }

    @EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
            if (!(event.getEntity() instanceof ServerPlayer player))
                return;

            if (!(event.getSource().getDirectEntity() instanceof DragonBloodPoolEntity pool))
                return;

            if (pool.getOwner() == null || !pool.getOwner().getUUID().equals(player.getUUID()))
                return;

            event.setCanceled(true);
        }

        @SubscribeEvent
        public static void onLivingDamagePost(LivingDamageEvent.Post event) {
            if (!(event.getEntity() instanceof ServerPlayer player) || event.getNewDamage() <= 0F)
                return;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.DRAGON_BLOOD_VIAL.value())) {
                if (!(stack.getItem() instanceof DragonBloodVialItem relic))
                    continue;

                var ability = relic.getRelicData(player, stack).getAbilitiesData().getAbilityData("dragon_blood");

                if (!ability.canPlayerUse(player))
                    continue;

                var chance = Mth.clamp(ability.getStatData("trigger_chance").getValue(), 0D, 1D);

                if (chance <= 0D || player.getRandom().nextDouble() >= chance)
                    continue;

                LivingEntity target = null;

                if (ability.isRankModifierUnlocked("magnetic_drop")) {
                    if (event.getSource().getEntity() instanceof LivingEntity sourceLiving && sourceLiving.isAlive()) {
                        target = sourceLiving;
                    } else if (event.getSource().getDirectEntity() instanceof LivingEntity directLiving && directLiving.isAlive()) {
                        target = directLiving;
                    }
                }
                Vec3 direction;
                float speed;
                float inaccuracy;

                if (target != null) {
                    var from = player.position().add(0D, player.getBbHeight() * 0.72D + 0.18D, 0D);
                    var to = target.getBoundingBox().getCenter();
                    var flat = new Vec3(to.x - from.x, 0D, to.z - from.z);
                    var horizontalDistance = flat.length();

                    if (horizontalDistance <= 0.0001D) {
                        var look = player.getLookAngle();
                        var lookFlat = new Vec3(look.x, 0D, look.z);

                        if (lookFlat.lengthSqr() <= 0.0001D)
                            lookFlat = new Vec3(1D, 0D, 0D);

                        flat = lookFlat.normalize();
                        horizontalDistance = 1D;
                    } else {
                        flat = flat.scale(1D / horizontalDistance);
                    }

                    var angle = Math.toRadians(50D);
                    var cos = Math.cos(angle);
                    var dy = to.y - from.y;
                    var denominator = 2D * cos * cos * Math.max(0.05D, horizontalDistance * Math.tan(angle) - dy);
                    var ballisticSpeed = Math.sqrt(0.09D * horizontalDistance * horizontalDistance / denominator) * 1.12D;

                    speed = (float) Mth.clamp(ballisticSpeed, 0.45D, 1.6D);
                    direction = flat.scale(speed * cos).add(0D, speed * Math.sin(angle), 0D).normalize();
                    inaccuracy = 0F;
                } else {
                    var angle = player.getRandom().nextDouble() * Math.PI * 2D;

                    direction = new Vec3(Math.cos(angle), 0.95D + player.getRandom().nextDouble(), Math.sin(angle)).normalize();
                    speed = 0.5F;
                    inaccuracy = 0.01F;
                }

                var droplet = new DragonBloodDropletEntity(player.level(), player);
                var start = player.position()
                        .add(0D, player.getBbHeight() * 0.72D, 0D)
                        .add(direction.x * 0.55D, 0.18D, direction.z * 0.55D);

                droplet.moveTo(start);
                droplet.shoot(direction.x, direction.y, direction.z, speed, inaccuracy);
                droplet.configure(
                        Math.max(0D, ability.getStatData("pool_radius").getValue()),
                        Math.max(0D, ability.getStatData("pool_damage").getValue()),
                        ability.isRankModifierUnlocked("split_burst") ? Mth.clamp(ability.getStatData("split_chance").getValue(), 0D, 1D) : 0D,
                        ability.isRankModifierUnlocked("split_burst") ? Math.max(1, (int) Math.round(Math.max(0D, ability.getStatData("split_limit").getValue()))) : 0,
                        ability.isRankModifierUnlocked("split_burst") ? Mth.clamp(ability.getStatData("split_power").getValue(), 0D, 1D) : 0D,
                        1D,
                        true
                );
                player.level().addFreshEntity(droplet);
                relic.getRelicData(player, stack).getLevelingData().addExperience("dragon_blood", "dragon_blood_droplet_created", 1D);
                ability.getStatisticData().getMetricData("dragon_blood_droplets_thrown").addValue(1D);
            }
        }

        @SubscribeEvent
        public static void onDragonBloodPoolDamagePost(LivingDamageEvent.Post event) {
            if (event.getNewDamage() <= 0F)
                return;

            if (!(event.getSource().getDirectEntity() instanceof DragonBloodPoolEntity pool))
                return;

            if (!(pool.getOwner() instanceof ServerPlayer player))
                return;

            for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.DRAGON_BLOOD_VIAL.value())) {
                if (!(stack.getItem() instanceof DragonBloodVialItem relic))
                    continue;

                var ability = relic.getRelicData(player, stack).getAbilitiesData().getAbilityData("dragon_blood");

                if (!ability.canPlayerUse(player))
                    continue;

                ability.getStatisticData().getMetricData("dragon_blood_pool_damage_dealt").addValue(event.getNewDamage());
                break;
            }
        }
    }
}
