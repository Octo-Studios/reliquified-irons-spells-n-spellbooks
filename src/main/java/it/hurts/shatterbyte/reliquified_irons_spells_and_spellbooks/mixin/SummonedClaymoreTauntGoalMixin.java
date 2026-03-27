package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.mixin;

import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities.SummonedWeaponEntityTuning;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(targets = "io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedClaymoreEntity$ClaymoreTauntGoal")
public abstract class SummonedClaymoreTauntGoalMixin {
    @ModifyConstant(method = "getActionTimestamp", constant = @Constant(intValue = 20))
    private int risas$customActionTimestamp(int value) {
        var mob = ((AnimatedActionGoalAccessor) this).risas$getMob();

        if (mob instanceof SummonedWeaponEntityTuning tuning)
            return tuning.risas$getClaymoreTauntActionTimestampTicks();

        return value;
    }

    @ModifyConstant(method = "getActionDuration", constant = @Constant(intValue = 120))
    private int risas$customActionDuration(int value) {
        var mob = ((AnimatedActionGoalAccessor) this).risas$getMob();

        if (mob instanceof SummonedWeaponEntityTuning tuning)
            return tuning.risas$getClaymoreTauntDurationTicks();

        return value;
    }

    @ModifyConstant(method = "getCooldown", constant = @Constant(intValue = 100))
    private int risas$customCooldown(int value) {
        var mob = ((AnimatedActionGoalAccessor) this).risas$getMob();

        if (mob instanceof SummonedWeaponEntityTuning tuning)
            return tuning.risas$getClaymoreTauntCooldownTicks();

        return value;
    }

    @ModifyConstant(method = "canStartAction", constant = @Constant(doubleValue = 12D))
    private double risas$customHorizontalRadius(double value) {
        var mob = ((AnimatedActionGoalAccessor) this).risas$getMob();

        if (mob instanceof SummonedWeaponEntityTuning tuning)
            return tuning.risas$getClaymoreTauntRadiusHorizontal();

        return value;
    }

    @ModifyConstant(method = "canStartAction", constant = @Constant(doubleValue = 6D))
    private double risas$customVerticalRadius(double value) {
        var mob = ((AnimatedActionGoalAccessor) this).risas$getMob();

        if (mob instanceof SummonedWeaponEntityTuning tuning)
            return tuning.risas$getClaymoreTauntRadiusVertical();

        return value;
    }

    @ModifyConstant(method = "canStartAction", constant = @Constant(intValue = 2))
    private int risas$customMinTargetsThreshold(int value) {
        var mob = ((AnimatedActionGoalAccessor) this).risas$getMob();

        if (mob instanceof SummonedWeaponEntityTuning tuning)
            return Math.max(0, tuning.risas$getClaymoreTauntMinTargets() - 1);

        return value;
    }
}
