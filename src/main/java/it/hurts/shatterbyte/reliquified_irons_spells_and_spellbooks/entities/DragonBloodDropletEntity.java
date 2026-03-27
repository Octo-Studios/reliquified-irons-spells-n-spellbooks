package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities;

import it.hurts.octostudios.octolib.module.particle.trail.EntityTrailProvider;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASEntities;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASItems;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.DragonBloodVialItem;
import it.hurts.sskirillss.relics.utils.EntityUtils;
import it.hurts.sskirillss.relics.utils.MathUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class DragonBloodDropletEntity extends ThrowableItemProjectile {
    private double poolRadius = 1.8D;
    private double poolDamage = 2D;
    private double splitChance = 0D;
    private int splitLimit = 0;
    private double splitPower = 0D;
    private double puddlePowerScale = 1D;
    private boolean canSplit = true;

    public DragonBloodDropletEntity(EntityType<? extends DragonBloodDropletEntity> entityType, net.minecraft.world.level.Level level) {
        super(entityType, level);
    }

    public DragonBloodDropletEntity(net.minecraft.world.level.Level level, LivingEntity owner) {
        super(RISASEntities.DRAGON_BLOOD_DROPLET.value(), owner, level);
        this.setOwner(owner);
    }

    public void configure(
            double poolRadius,
            double poolDamage,
            double splitChance,
            int splitLimit,
            double splitPower,
            double puddlePowerScale,
            boolean canSplit
    ) {
        this.poolRadius = Math.max(0D, poolRadius);
        this.poolDamage = Math.max(0D, poolDamage);
        this.splitChance = Mth.clamp(splitChance, 0D, 1D);
        this.splitLimit = Math.max(0, splitLimit);
        this.splitPower = Mth.clamp(splitPower, 0D, 1D);
        this.puddlePowerScale = Math.max(0D, puddlePowerScale);
        this.canSplit = canSplit;
    }

    @Override
    protected Item getDefaultItem() {
        return RISASItems.DRAGON_BLOOD_VIAL.value();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
    }

    @Override
    public void tick() {
        super.tick();

        if (!(this.level() instanceof ServerLevel))
            return;

        if (this.tickCount > 200)
            this.discard();
    }

    @Override
    protected double getDefaultGravity() {
        return 0.09D;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if (!(this.level() instanceof ServerLevel level)) {
            this.discard();
            return;
        }

        var pool = new DragonBloodPoolEntity(level);
        var owner = this.getOwner();
        var impactPoint = result.getLocation();

        if (owner != null)
            pool.setOwner(owner);

        pool.setDamage((float) Math.max(0D, this.poolDamage * this.puddlePowerScale));
        pool.setRadius((float) Math.max(0.2D, this.poolRadius * this.puddlePowerScale));
        pool.setPowerScale(this.puddlePowerScale);
        pool.moveTo(impactPoint);
        level.addFreshEntity(pool);

        if (this.canSplit && this.splitLimit > 0 && this.splitChance > 0D) {
            var droplets = MathUtils.multicast(level.getRandom(), this.splitChance, this.splitLimit);

            for (var i = 0; i < droplets; i++) {
                var angle = level.random.nextDouble() * (Math.PI * 2D) + (Math.PI * 2D * i) / Math.max(1, droplets);
                var horizontal = new Vec3(Math.cos(angle), 0D, Math.sin(angle));
                var direction = horizontal.scale(0.88D).add(0D, 0.82D + level.random.nextDouble() * 0.22D, 0D).normalize();
                DragonBloodDropletEntity droplet;

                if (owner instanceof LivingEntity livingOwner) {
                    droplet = new DragonBloodDropletEntity(level, livingOwner);
                } else {
                    droplet = new DragonBloodDropletEntity(RISASEntities.DRAGON_BLOOD_DROPLET.value(), level);

                    if (owner != null)
                        droplet.setOwner(owner);
                }

                droplet.moveTo(impactPoint.add(horizontal.scale(0.2D)).add(0D, 0.2D, 0D));
                droplet.shoot(direction.x, direction.y, direction.z, 0.72F, 0.03F);
                droplet.configure(
                        this.poolRadius,
                        this.poolDamage,
                        0D,
                        0,
                        this.splitPower,
                        this.puddlePowerScale * this.splitPower,
                        false
                );
                level.addFreshEntity(droplet);

                if (owner instanceof ServerPlayer player) {
                    for (var stack : EntityUtils.findEquippedCurios(player, RISASItems.DRAGON_BLOOD_VIAL.value())) {
                        if (!(stack.getItem() instanceof DragonBloodVialItem relic))
                            continue;

                        var ability = relic.getRelicData(player, stack).getAbilitiesData().getAbilityData("dragon_blood");

                        if (!ability.canPlayerUse(player))
                            continue;

                        relic.getRelicData(player, stack).getLevelingData().addExperience("dragon_blood", "dragon_blood_droplet_created", 1D);
                        ability.getStatisticData().getMetricData("dragon_blood_droplets_thrown").addValue(1D);
                        break;
                    }
                }
            }
        }

        this.discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putDouble("risas_pool_radius", this.poolRadius);
        compound.putDouble("risas_pool_damage", this.poolDamage);
        compound.putDouble("risas_split_chance", this.splitChance);
        compound.putInt("risas_split_limit", this.splitLimit);
        compound.putDouble("risas_split_power", this.splitPower);
        compound.putDouble("risas_puddle_power_scale", this.puddlePowerScale);
        compound.putBoolean("risas_can_split", this.canSplit);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.poolRadius = Math.max(0D, compound.getDouble("risas_pool_radius"));
        this.poolDamage = Math.max(0D, compound.getDouble("risas_pool_damage"));
        this.splitChance = Mth.clamp(compound.getDouble("risas_split_chance"), 0D, 1D);
        this.splitLimit = Math.max(0, compound.getInt("risas_split_limit"));
        this.splitPower = Mth.clamp(compound.getDouble("risas_split_power"), 0D, 1D);
        this.puddlePowerScale = Math.max(0D, compound.getDouble("risas_puddle_power_scale"));
        this.canSplit = compound.getBoolean("risas_can_split");
    }

    @OnlyIn(Dist.CLIENT)
    public static class TrailProvider extends EntityTrailProvider<DragonBloodDropletEntity> {
        public TrailProvider(DragonBloodDropletEntity entity) {
            super(entity);
        }

        @Override
        public Vec3 getTrailPosition(float partialTicks) {
            return this.entity.getPosition(partialTicks).add(this.entity.getDeltaMovement().scale(-1));
        }

        @Override
        public int getTrailUpdateFrequency() {
            return 1;
        }

        @Override
        public boolean isTrailAlive() {
            return this.entity.isAlive();
        }

        @Override
        public boolean isTrailGrowing() {
            return this.entity.tickCount > 2;
        }

        @Override
        public int getTrailMaxLength() {
            return 3;
        }

        @Override
        public int getTrailFadeInColor() {
            return 0xFFFF00FF;
        }

        @Override
        public int getTrailFadeOutColor() {
            return 0x80800080;
        }

        @Override
        public double getTrailScale() {
            return 0.075F;
        }
    }
}
