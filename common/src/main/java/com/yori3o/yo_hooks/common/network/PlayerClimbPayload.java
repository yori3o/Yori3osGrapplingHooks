package com.yori3o.yo_hooks.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record PlayerClimbPayload(boolean up, int agility_level) implements CustomPacketPayload {

    public static final Type<PlayerClimbPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("yo_hooks", "climb"));

    public static final StreamCodec<FriendlyByteBuf, PlayerClimbPayload> CODEC =
            StreamCodec.of(
                (buf, payload) -> {
                    buf.writeBoolean(payload.up());
                    buf.writeInt(payload.agility_level());
                },
                buf -> new PlayerClimbPayload(
                    buf.readBoolean(),
                    buf.readInt()
                )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
