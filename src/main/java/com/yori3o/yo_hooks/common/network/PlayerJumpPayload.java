package com.yori3o.yo_hooks.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record PlayerJumpPayload(boolean usingCancel) implements CustomPacketPayload {

    public static final Type<PlayerJumpPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("yo_hooks", "player_jump"));

    public static final StreamCodec<FriendlyByteBuf, PlayerJumpPayload> CODEC =
            StreamCodec.of(
                (buf, payload) -> {
                    buf.writeBoolean(payload.usingCancel());
                },
                buf -> new PlayerJumpPayload(
                    buf.readBoolean()
                )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
