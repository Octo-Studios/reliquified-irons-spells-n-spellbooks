package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items.misc;

import it.hurts.sskirillss.relics.items.relics.base.data.loot.LootEntry;

public class RISASLootEntries {
    public static final LootEntry ANY_STRUCTURE = LootEntry.builder()
            .dimension(".*")
            .biome(".*")
            .table("irons_spellbooks:chests\\/[\\w_\\/]*[\\w]+[\\w_\\/]*")
            .weight(500)
            .build();
}