package com.yori3o.yo_hooks.common.network;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;



public record SendCommonConfigPayload(byte[] values, byte[] hookLengths, byte[] hookDamages) implements CustomPacketPayload {

    public static final Type<SendCommonConfigPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("yo_hooks", "send_common_config"));

    public static final StreamCodec<FriendlyByteBuf, SendCommonConfigPayload> CODEC =
            StreamCodec.of(
                (buf, payload) -> {
                    buf.writeByteArray(payload.values());
                    buf.writeByteArray(payload.hookLengths());
                    buf.writeByteArray(payload.hookDamages());
                },
                buf -> new SendCommonConfigPayload(
                    buf.readByteArray(),
                    buf.readByteArray(),
                    buf.readByteArray()
                )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
