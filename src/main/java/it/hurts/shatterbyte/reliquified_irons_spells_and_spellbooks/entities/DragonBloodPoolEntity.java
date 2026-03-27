package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities;

import io.redspace.ironsspellbooks.damage.DamageSources;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.entity.spells.AoeEntity;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init.RISASEntities;
import it.hurts.sskirillss.relics.utils.ParticleUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.util.Optional;

public class DragonBloodPoolEntity extends AoeEntity {
    private DamageSource damageSource;
    private double powerScale = 1D;

    public DragonBloodPoolEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
        this.setCircular();
        this.setRadius(1.8F);
        this.radiusOnUse = -0.15F;
        this.radiusPerTick = -0.02F;
    }

    public DragonBloodPoolEntity(Level level) {
        this(RISASEntities.DRAGON_BLOOD_POOL.value(), level);
    }

    public void setPowerScale(double powerScale) {
        this.powerScale = Math.max(0D, powerScale);
    }

    public double getPowerScale() {
        return this.powerScale;
    }

    @Override
    public void applyEffect(LivingEntity target) {
        if (this.damageSource == null)
            this.damageSource = new DamageSource(DamageSources.getHolderFromResource(target, ISSDamageTypes.DRAGON_BREATH_POOL), this, this.getOwner());

        DamageSources.ignoreNextKnockback(target);
        target.hurt(this.damageSource, this.getDamage());
    }

    @Override
    public float getParticleCount() {
        return 3F;
    }

    @Override
    public Optional<ParticleOptions> getParticle() {
        return Optional.of(ParticleUtils.constructSimpleSpark(new Color(170, 38, 188), 0.55F, 20, 0.96F));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putDouble("risas_power_scale", this.powerScale);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.powerScale = Math.max(0D, compound.getDouble("risas_power_scale"));
        this.damageSource = null;
    }
}
