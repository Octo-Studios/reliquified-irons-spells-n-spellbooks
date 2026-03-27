package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.mixin;

import io.redspace.ironsspellbooks.entity.spells.summoned_weapons.SummonedRapierEntity;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities.SummonedWeaponEntityTuning;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(SummonedRapierEntity.class)
public abstract class SummonedRapierEntityMixin {
    @ModifyConstant(method = "hurt", constant = @Constant(floatValue = 0.4F))
    private float risas$customSidestepChance(float value) {
        return (float) ((SummonedWeaponEntityTuning) this).risas$getRapierSidestepChance();
    }
}
