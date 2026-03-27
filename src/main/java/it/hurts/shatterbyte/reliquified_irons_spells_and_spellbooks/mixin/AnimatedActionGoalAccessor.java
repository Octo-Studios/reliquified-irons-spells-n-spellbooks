package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.mixin;

import io.redspace.ironsspellbooks.entity.mobs.wizards.fire_boss.AnimatedActionGoal;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnimatedActionGoal.class)
public interface AnimatedActionGoalAccessor {
    @Accessor("mob")
    Mob risas$getMob();
}
