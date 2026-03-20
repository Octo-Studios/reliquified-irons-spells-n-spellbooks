package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items;

import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;

public abstract class ISASRelic extends RelicItem {
    @Override
    public String getConfigRoute() {
        return ReliquifiedIronsSpellsAndSpellbooks.MODID;
    }
}
