package it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.network.payload;

import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.ReliquifiedIronsSpellsAndSpellbooks;
import it.hurts.shatterbyte.reliquified_irons_spells_and_spellbooks.client.MaskOfHungerBeamRenderer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record MaskOfHungerBeamPayload(int fromEntityId, int toEntityId) implements CustomPacketPayload {
    public static final Type<MaskOfHungerBeamPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            ReliquifiedIronsSpellsAndSpellbooks.MODID,
            "mask_of_hunger_beam"
    ));
    public static final StreamCodec<RegistryFriendlyByteBuf, MaskOfHungerBeamPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            MaskOfHungerBeamPayload::fromEntityId,
            ByteBufCodecs.VAR_INT,
            MaskOfHungerBeamPayload::toEntityId,
            MaskOfHungerBeamPayload::new
    );

    @Override
    public Type<MaskOfHungerBeamPayload> type() {
        return TYPE;
    }

    public static void handle(MaskOfHungerBeamPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> MaskOfHungerBeamRenderer.add(payload.fromEntityId(), payload.toEntityId()));
    }
}
