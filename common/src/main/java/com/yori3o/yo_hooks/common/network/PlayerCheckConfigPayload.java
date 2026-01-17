package com.yori3o.yo_hooks.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record PlayerCheckConfigPayload(boolean softHook, float stiffness, float climbSpeed, boolean funnyMode) implements CustomPacketPayload {

    public static final Type<PlayerCheckConfigPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("yo_hooks", "check_config"));

    public static final StreamCodec<FriendlyByteBuf, PlayerCheckConfigPayload> CODEC =
            StreamCodec.of(
                (buf, payload) -> {
                    buf.writeBoolean(payload.softHook());
                    buf.writeFloat(payload.stiffness());
                    buf.writeFloat(payload.climbSpeed());
                    buf.writeBoolean(payload.funnyMode());
                },
                buf -> new PlayerCheckConfigPayload(
                    buf.readBoolean(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readBoolean()
                )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
