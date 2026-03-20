package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init;

import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RISASEntities {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ReliquifiedIronsSpellsAndSpellbooks.MODID);

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
