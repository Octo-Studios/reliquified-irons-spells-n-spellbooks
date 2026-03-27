package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities;

public interface SummonedWeaponEntityTuning {
    void risas$setAttackIntervalTicks(int min, int max);

    void risas$setRapierSidestepChance(double value);

    double risas$getRapierSidestepChance();

    void risas$setRegenIntervalTicks(int value);

    int risas$getRegenIntervalTicks();

    void risas$setRegenAmount(float value);

    float risas$getRegenAmount();

    void risas$setClaymoreTauntActionTimestampTicks(int value);

    int risas$getClaymoreTauntActionTimestampTicks();

    void risas$setClaymoreTauntDurationTicks(int value);

    int risas$getClaymoreTauntDurationTicks();

    void risas$setClaymoreTauntCooldownTicks(int value);

    int risas$getClaymoreTauntCooldownTicks();

    void risas$setClaymoreTauntRadiusHorizontal(double value);

    double risas$getClaymoreTauntRadiusHorizontal();

    void risas$setClaymoreTauntRadiusVertical(double value);

    double risas$getClaymoreTauntRadiusVertical();

    void risas$setClaymoreTauntMinTargets(int value);

    int risas$getClaymoreTauntMinTargets();
}
