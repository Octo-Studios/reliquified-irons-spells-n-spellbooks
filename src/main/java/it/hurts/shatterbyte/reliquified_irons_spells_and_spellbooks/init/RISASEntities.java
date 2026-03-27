package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.init;

import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities.DragonBloodDropletEntity;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.entities.DragonBloodPoolEntity;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RISASEntities {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, ReliquifiedIronsSpellsAndSpellbooks.MODID);
    public static final DeferredHolder<EntityType<?>, EntityType<DragonBloodDropletEntity>> DRAGON_BLOOD_DROPLET = ENTITIES.register(
            "dragon_blood_droplet",
            () -> EntityType.Builder.<DragonBloodDropletEntity>of(DragonBloodDropletEntity::new, MobCategory.MISC)
                    .sized(0.3F, 0.3F)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build("dragon_blood_droplet")
    );
    public static final DeferredHolder<EntityType<?>, EntityType<DragonBloodPoolEntity>> DRAGON_BLOOD_POOL = ENTITIES.register(
            "dragon_blood_pool",
            () -> EntityType.Builder.<DragonBloodPoolEntity>of(DragonBloodPoolEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(8)
                    .updateInterval(1)
                    .build("dragon_blood_pool")
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
