package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.init;

import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ReliquifiedIronsSpellsAndSpellbooks.MODID);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
