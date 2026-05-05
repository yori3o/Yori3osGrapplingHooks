package com.yori3o.yo_hooks.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record PlayerClimbPayload(boolean up, int agilityLevel, boolean playSound) implements CustomPacketPayload {

    public static final Type<PlayerClimbPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("yo_hooks", "climb"));

    public static final StreamCodec<FriendlyByteBuf, PlayerClimbPayload> CODEC =
            StreamCodec.of(
                (buf, payload) -> {
                    buf.writeBoolean(payload.up());
                    buf.writeInt(payload.agilityLevel());
                    buf.writeBoolean(payload.playSound());
                },
                buf -> new PlayerClimbPayload(
                    buf.readBoolean(),
                    buf.readInt(),
                    buf.readBoolean()
                )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
