package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks;

import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.init.EntityRegistry;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.init.ItemRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(ReliquifiedIronsSpellsAndSpellbooks.MODID)
public class ReliquifiedIronsSpellsAndSpellbooks {
    public static final String MODID = "reliquified_irons_spells_and_spellbooks";

    public ReliquifiedIronsSpellsAndSpellbooks(IEventBus bus) {
        bus.addListener(this::setupCommon);

        ItemRegistry.register(bus);
        EntityRegistry.register(bus);
    }

    private void setupCommon(final FMLCommonSetupEvent event) {

    }
}