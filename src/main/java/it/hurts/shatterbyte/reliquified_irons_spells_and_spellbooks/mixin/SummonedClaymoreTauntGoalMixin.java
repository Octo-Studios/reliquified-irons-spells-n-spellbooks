package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.mixin;

import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.goals.AnimatedActionGoal;
import io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedClaymoreEntity;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities.SummonedWeaponEntityTuning;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SummonedClaymoreEntity.ClaymoreTauntGoal.class)
public abstract class SummonedClaymoreTauntGoalMixin extends AnimatedActionGoal<SummonedClaymoreEntity> {
    public SummonedClaymoreTauntGoalMixin(SummonedClaymoreEntity mob) {
        super(mob);
    }

    @ModifyConstant(method = "getActionTimestamp", constant = @Constant(intValue = 20))
    private int risas$customActionTimestamp(int value) {
        if (this.mob instanceof SummonedWeaponEntityTuning tuning)
            return tuning.risas$getClaymoreTauntActionTimestampTicks();

        return value;
    }

    @ModifyConstant(method = "getActionDuration", constant = @Constant(intValue = 120))
    private int risas$customActionDuration(int value) {
        if (this.mob instanceof SummonedWeaponEntityTuning tuning)
            return tuning.risas$getClaymoreTauntDurationTicks();

        return value;
    }

    @ModifyConstant(method = "getCooldown", constant = @Constant(intValue = 100))
    private int risas$customCooldown(int value) {
        if (this.mob instanceof SummonedWeaponEntityTuning tuning)
            return tuning.risas$getClaymoreTauntCooldownTicks();

        return value;
    }

    @ModifyConstant(method = "canStartAction", constant = @Constant(doubleValue = 12D))
    private double risas$customHorizontalRadius(double value) {
        if (this.mob instanceof SummonedWeaponEntityTuning tuning)
            return tuning.risas$getClaymoreTauntRadiusHorizontal();

        return value;
    }

    @ModifyConstant(method = "canStartAction", constant = @Constant(doubleValue = 6D))
    private double risas$customVerticalRadius(double value) {
        if (this.mob instanceof SummonedWeaponEntityTuning tuning)
            return tuning.risas$getClaymoreTauntRadiusVertical();

        return value;
    }

    @ModifyConstant(method = "canStartAction", constant = @Constant(intValue = 2))
    private int risas$customMinTargetsThreshold(int value) {
        if (this.mob instanceof SummonedWeaponEntityTuning tuning)
            return Math.max(0, tuning.risas$getClaymoreTauntMinTargets() - 1);

        return value;
    }
}
