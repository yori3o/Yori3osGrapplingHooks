package com.yori3o.yo_hooks.impl;


import com.yori3o.yo_hooks.common.YoHooks;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.function.BiConsumer;



public class PlatformNetworkHelper {

    // we store the event temporarily during mod initialization
    private static RegisterPayloadHandlersEvent registrationEvent;

    public static void init(RegisterPayloadHandlersEvent event) {
        registrationEvent = event;
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T payload) {
        PacketDistributor.sendToServer(payload);
    }

    public static <T extends CustomPacketPayload> void registerS2C(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, PayloadContext> handler
    ) {
        if (registrationEvent == null) {
            throw new IllegalStateException("PlatformNetworkHelper.init() must be called before registration!");
        }

        registrationEvent.registrar(YoHooks.MOD_ID)
            .playToClient(type, codec, (payload, context) -> {
                handler.accept(payload, new PayloadContext() {
                    @Override
                    public void enqueue(Runnable runnable) {
                        context.enqueueWork(runnable);
                    }

                    @Override
                    public Player getPlayer() {
                        return context.player();
                    }
                });
            });
    }

    public static <T extends CustomPacketPayload> void registerC2S(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, PayloadContext> handler
    ) {
        if (registrationEvent == null) {
            throw new IllegalStateException("PlatformNetworkHelper.init() must be called before registration!");
        }

        registrationEvent.registrar(YoHooks.MOD_ID)
            .playToServer(type, codec, (payload, context) -> {
                handler.accept(payload, new PayloadContext() {
                    @Override
                    public void enqueue(Runnable runnable) {
                        context.enqueueWork(runnable);
                    }

                    @Override
                    public Player getPlayer() {
                        return context.player();
                    }
                });
            });
    }

    public interface PayloadContext {
        void enqueue(Runnable runnable);
        Player getPlayer();
    }
}

