package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.network.payload;

import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.items.RingOfBlades;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class RingOfBladesAirSwingPayload implements CustomPacketPayload {
    public static final Type<RingOfBladesAirSwingPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            ReliquifiedIronsSpellsAndSpellbooks.MODID,
            "ring_of_blades_air_swing"
    ));
    public static final RingOfBladesAirSwingPayload INSTANCE = new RingOfBladesAirSwingPayload();
    public static final StreamCodec<RegistryFriendlyByteBuf, RingOfBladesAirSwingPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private RingOfBladesAirSwingPayload() {
    }

    @Override
    public Type<RingOfBladesAirSwingPayload> type() {
        return TYPE;
    }

    public static void handle(RingOfBladesAirSwingPayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player))
            return;

        RingOfBlades.CommonEvents.tryTriggerOnAirSwing(player);
    }
}
