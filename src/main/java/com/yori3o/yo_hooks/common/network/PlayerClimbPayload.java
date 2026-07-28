package com.yori3o.yo_hooks.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record PlayerClimbPayload(boolean up, boolean playSound) implements CustomPacketPayload {

    public static final Type<PlayerClimbPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("yo_hooks", "climb"));

    public static final StreamCodec<FriendlyByteBuf, PlayerClimbPayload> CODEC =
            StreamCodec.of(
                (buf, payload) -> {
                    buf.writeBoolean(payload.up());
                    buf.writeBoolean(payload.playSound());
                },
                buf -> new PlayerClimbPayload(
                    buf.readBoolean(),
                    buf.readBoolean()
                )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
