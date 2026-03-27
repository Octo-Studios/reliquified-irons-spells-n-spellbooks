package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init;

import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RISASItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ReliquifiedIronsSpellsAndSpellbooks.MODID);

    public static final DeferredHolder<Item, HatOfOmniscienceItem> HAT_OF_OMNISCIENCE = ITEMS.register("hat_of_omniscience", HatOfOmniscienceItem::new);
    public static final DeferredHolder<Item, BloodyVoodooDollItem> BLOODY_VOODOO_DOLL = ITEMS.register("bloody_voodoo_doll", BloodyVoodooDollItem::new);
    public static final DeferredHolder<Item, CardiacTrapItem> CARDIAC_TRAP = ITEMS.register("cardiac_trap", CardiacTrapItem::new);
    public static final DeferredHolder<Item, FlaskOfTheRedMistItem> FLASK_OF_THE_RED_MIST = ITEMS.register("flask_of_the_red_mist", FlaskOfTheRedMistItem::new);
    public static final DeferredHolder<Item, LivingFleshItem> LIVING_FLESH = ITEMS.register("living_flesh", LivingFleshItem::new);
    public static final DeferredHolder<Item, SinnerCrownItem> SINNER_CROWN = ITEMS.register("sinner_crown", SinnerCrownItem::new);
    public static final DeferredHolder<Item, SlicerItem> SLICER = ITEMS.register("slicer", SlicerItem::new);
    public static final DeferredHolder<Item, CloakOfTheBloodyFeatherItem> CLOAK_OF_THE_BLOODY_FEATHER = ITEMS.register("cloak_of_the_bloody_feather", CloakOfTheBloodyFeatherItem::new);
    public static final DeferredHolder<Item, MaskOfHungerItem> MASK_OF_HUNGER = ITEMS.register("mask_of_hunger", MaskOfHungerItem::new);
    public static final DeferredHolder<Item, RingOfBladesItem> RING_OF_BLADES = ITEMS.register("ring_of_blades", RingOfBladesItem::new);
    public static final DeferredHolder<Item, DimensionKeyItem> DIMENSION_KEY = ITEMS.register("dimension_key", DimensionKeyItem::new);
    public static final DeferredHolder<Item, DragonBloodVialItem> DRAGON_BLOOD_VIAL = ITEMS.register("dragon_blood_vial", DragonBloodVialItem::new);
    public static final DeferredHolder<Item, EchoGloveItem> ECHO_GLOVE = ITEMS.register("echo_glove", EchoGloveItem::new);
    public static final DeferredHolder<Item, EnderBowItem> ENDER_BOW = ITEMS.register("ender_bow", EnderBowItem::new);
    public static final DeferredHolder<Item, GalaxyDevourerDiademItem> GALAXY_DEVOURER_DIADEM = ITEMS.register("galaxy_devourer_diadem", GalaxyDevourerDiademItem::new);
    public static final DeferredHolder<Item, ImmaterialDisperserItem> IMMATERIAL_DISPERSER = ITEMS.register("immaterial_disperser", ImmaterialDisperserItem::new);
    public static final DeferredHolder<Item, LunarSextantItem> LUNAR_SEXTANT = ITEMS.register("lunar_sextant", LunarSextantItem::new);
    public static final DeferredHolder<Item, SealedSwordItem> SEALED_SWORD = ITEMS.register("sealed_sword", SealedSwordItem::new);
    public static final DeferredHolder<Item, SealedRapierItem> SEALED_RAPIER = ITEMS.register("sealed_rapier", SealedRapierItem::new);
    public static final DeferredHolder<Item, SealedClaymoreItem> SEALED_CLAYMORE = ITEMS.register("sealed_claymore", SealedClaymoreItem::new);
    public static final DeferredHolder<Item, MirrorOfTransgressionItem> MIRROR_OF_TRANSGRESSION = ITEMS.register("mirror_of_transgression", MirrorOfTransgressionItem::new);
    public static final DeferredHolder<Item, PulsarMantleItem> PULSAR_MANTLE = ITEMS.register("pulsar_mantle", PulsarMantleItem::new);
    public static final DeferredHolder<Item, RingOfElusivenessItem> RING_OF_ELUSIVENESS = ITEMS.register("ring_of_elusiveness", RingOfElusivenessItem::new);
    public static final DeferredHolder<Item, ShadowClawsItem> SHADOW_CLAWS = ITEMS.register("shadow_claws", ShadowClawsItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
