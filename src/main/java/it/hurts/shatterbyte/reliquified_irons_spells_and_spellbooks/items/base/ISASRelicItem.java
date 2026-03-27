package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.base;

import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.sskirillss.relics.items.relics.base.RelicItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public abstract class ISASRelicItem extends RelicItem {
    public ISASRelicItem(Item.Properties properties) {
        super(properties);
    }

    public ISASRelicItem() {
        super(new Item.Properties()
                .rarity(Rarity.EPIC)
                .stacksTo(1));
    }

    @Override
    public String getConfigRoute() {
        return ReliquifiedIronsSpellsAndSpellbooks.MODID;
    }
}
