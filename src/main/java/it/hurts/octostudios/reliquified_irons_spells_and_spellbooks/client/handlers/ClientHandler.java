package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.client.handlers;

import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.init.RISASItems;
import it.hurts.sskirillss.relics.api.relics.IRelicItem;
import it.hurts.sskirillss.relics.client.style.base.RelicStyle;
import it.hurts.sskirillss.relics.init.RelicsRelicStyles;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID, value = Dist.CLIENT)
public class ClientHandler {
    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var entry : RISASItems.ITEMS.getEntries()) {
                var item = entry.get();

                if (!(item instanceof IRelicItem))
                    continue;

                RelicsRelicStyles.register(item, RelicStyle::new);
            }

            RelicsRelicStyles.init();
        });
    }

    @SubscribeEvent
    public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {

    }
}
