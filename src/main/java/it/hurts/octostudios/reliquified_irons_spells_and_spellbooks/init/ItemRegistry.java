package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.init;

import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items.HyperfocusGloveItem;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items.SpellbooksRelic;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ReliquifiedIronsSpellsAndSpellbooks.MODID);

   // public static final DeferredHolder<Item, SpellbooksRelic> HYPERFOCUS_GLOVE = ITEMS.register("hyperfocus_glove", HyperfocusGloveItem::new);
    public static final DeferredHolder<Item, SpellbooksRelic> BLOODIED_VOODOO_DOLL = ITEMS.register("bloodied_voodoo_doll", HyperfocusGloveItem::new);
    public static final DeferredHolder<Item, SpellbooksRelic> HAT_OF_OMNISCIENCE = ITEMS.register("hat_of_omniscience", HyperfocusGloveItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
