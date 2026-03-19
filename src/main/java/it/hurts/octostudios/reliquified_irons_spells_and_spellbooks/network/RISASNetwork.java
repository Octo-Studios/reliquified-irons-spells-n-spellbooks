package it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.network;

import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.network.payload.MaskOfHungerBeamPayload;
import it.hurts.octostudios.reliquified_irons_spells_and_spellbooks.network.payload.RingOfBladesAirSwingPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = ReliquifiedIronsSpellsAndSpellbooks.MODID)
public class RISASNetwork {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(MaskOfHungerBeamPayload.TYPE, MaskOfHungerBeamPayload.STREAM_CODEC, MaskOfHungerBeamPayload::handle)
                .playToServer(RingOfBladesAirSwingPayload.TYPE, RingOfBladesAirSwingPayload.STREAM_CODEC, RingOfBladesAirSwingPayload::handle);
    }
}
