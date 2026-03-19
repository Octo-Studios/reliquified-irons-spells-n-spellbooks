package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.init;

import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items.*;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.items.CardiacTrapItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ReliquifiedIronsSpellsAndSpellbooks.MODID);

    public static final DeferredHolder<Item, BloodyVoodooDollItem> BLOODY_VOODOO_DOLL = ITEMS.register("bloody_voodoo_doll", BloodyVoodooDollItem::new);
    public static final DeferredHolder<Item, CardiacTrapItem> CARDIAC_TRAP = ITEMS.register("cardiac_trap", CardiacTrapItem::new);
    public static final DeferredHolder<Item, CloakOfTheBloodyFeather> CLOAK_OF_THE_BLOODY_FEATHER = ITEMS.register("cloak_of_the_bloody_feather", CloakOfTheBloodyFeather::new);
    public static final DeferredHolder<Item, FlaskOfTheRedMistItem> FLASK_OF_THE_RED_MIST = ITEMS.register("flask_of_the_red_mist", FlaskOfTheRedMistItem::new);
    public static final DeferredHolder<Item, HatOfOmniscienceItem> HAT_OF_OMNISCIENCE = ITEMS.register("hat_of_omniscience", HatOfOmniscienceItem::new);
    public static final DeferredHolder<Item, LivingFleshItem> LIVING_FLESH = ITEMS.register("living_flesh", LivingFleshItem::new);
    public static final DeferredHolder<Item, MaskOfHungerItem> MASK_OF_HUNGER = ITEMS.register("mask_of_hunger", MaskOfHungerItem::new);
    public static final DeferredHolder<Item, RingOfBlades> RING_OF_BLADES = ITEMS.register("ring_of_blades", RingOfBlades::new);
    public static final DeferredHolder<Item, SinnerCrownItem> SINNER_CROWN = ITEMS.register("sinner_crown", SinnerCrownItem::new);
    public static final DeferredHolder<Item, SlicerItem> SLICER = ITEMS.register("slicer", SlicerItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
