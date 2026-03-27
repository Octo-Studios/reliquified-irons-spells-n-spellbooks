package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.mixin;

import io.redspace.ironsspellbooks.entity.mobs.wizards.GenericAnimatedWarlockAttackGoal;
import io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedWeaponEntity;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities.SummonedWeaponEntityTuning;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SummonedWeaponEntity.class)
public abstract class SummonedWeaponEntityMixin implements SummonedWeaponEntityTuning {
    @Shadow
    GenericAnimatedWarlockAttackGoal<? extends SummonedWeaponEntity> attackGoal;

    @Unique
    private double risas$rapierSidestepChance = 0.4D;

    @Unique
    private int risas$regenIntervalTicks = 80;

    @Unique
    private float risas$regenAmount = 1F;

    @Unique
    private int risas$claymoreTauntActionTimestampTicks = 20;

    @Unique
    private int risas$claymoreTauntDurationTicks = 120;

    @Unique
    private int risas$claymoreTauntCooldownTicks = 100;

    @Unique
    private double risas$claymoreTauntRadiusHorizontal = 12D;

    @Unique
    private double risas$claymoreTauntRadiusVertical = 6D;

    @Unique
    private int risas$claymoreTauntMinTargets = 3;

    @Override
    public void risas$setAttackIntervalTicks(int min, int max) {
        if (this.attackGoal != null)
            this.attackGoal.setMeleeAttackInverval(Math.max(0, min), Math.max(Math.max(0, min), max));
    }

    @Override
    public void risas$setRapierSidestepChance(double value) {
        this.risas$rapierSidestepChance = Math.max(0D, Math.min(1D, value));
    }

    @Override
    public double risas$getRapierSidestepChance() {
        return this.risas$rapierSidestepChance;
    }

    @Override
    public void risas$setRegenIntervalTicks(int value) {
        this.risas$regenIntervalTicks = Math.max(1, value);
    }

    @Override
    public int risas$getRegenIntervalTicks() {
        return this.risas$regenIntervalTicks;
    }

    @Override
    public void risas$setRegenAmount(float value) {
        this.risas$regenAmount = Math.max(0F, value);
    }

    @Override
    public float risas$getRegenAmount() {
        return this.risas$regenAmount;
    }

    @Override
    public void risas$setClaymoreTauntActionTimestampTicks(int value) {
        this.risas$claymoreTauntActionTimestampTicks = Math.max(0, value);
    }

    @Override
    public int risas$getClaymoreTauntActionTimestampTicks() {
        return this.risas$claymoreTauntActionTimestampTicks;
    }

    @Override
    public void risas$setClaymoreTauntDurationTicks(int value) {
        this.risas$claymoreTauntDurationTicks = Math.max(1, value);
    }

    @Override
    public int risas$getClaymoreTauntDurationTicks() {
        return this.risas$claymoreTauntDurationTicks;
    }

    @Override
    public void risas$setClaymoreTauntCooldownTicks(int value) {
        this.risas$claymoreTauntCooldownTicks = Math.max(1, value);
    }

    @Override
    public int risas$getClaymoreTauntCooldownTicks() {
        return this.risas$claymoreTauntCooldownTicks;
    }

    @Override
    public void risas$setClaymoreTauntRadiusHorizontal(double value) {
        this.risas$claymoreTauntRadiusHorizontal = Math.max(0D, value);
    }

    @Override
    public double risas$getClaymoreTauntRadiusHorizontal() {
        return this.risas$claymoreTauntRadiusHorizontal;
    }

    @Override
    public void risas$setClaymoreTauntRadiusVertical(double value) {
        this.risas$claymoreTauntRadiusVertical = Math.max(0D, value);
    }

    @Override
    public double risas$getClaymoreTauntRadiusVertical() {
        return this.risas$claymoreTauntRadiusVertical;
    }

    @Override
    public void risas$setClaymoreTauntMinTargets(int value) {
        this.risas$claymoreTauntMinTargets = Math.max(1, value);
    }

    @Override
    public int risas$getClaymoreTauntMinTargets() {
        return this.risas$claymoreTauntMinTargets;
    }

    @ModifyConstant(method = "customServerAiStep", constant = @Constant(intValue = 80))
    private int risas$customRegenInterval(int value) {
        return this.risas$getRegenIntervalTicks();
    }

    @ModifyConstant(method = "customServerAiStep", constant = @Constant(floatValue = 1F))
    private float risas$customRegenAmount(float value) {
        return this.risas$getRegenAmount();
    }
}
