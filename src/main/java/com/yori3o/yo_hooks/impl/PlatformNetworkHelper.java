//? if fabric {
package com.yori3o.yo_hooks.impl;


import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.function.BiConsumer;



public class PlatformNetworkHelper {

    private static <T extends CustomPacketPayload> void registerS2CType(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec
    ) {
        PayloadTypeRegistry.clientboundPlay().register(type, codec);
    }

    private static <T extends CustomPacketPayload> void registerC2SType(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec
    ) {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
    }

    public static <T extends CustomPacketPayload> void registerS2C(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, PayloadContext> handler
    ) {
        registerS2CType(type, codec);

        if (PlatformUtil.isClient()) {
            ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
                handler.accept(payload, new PayloadContext() {
                    @Override
                    public void enqueue(Runnable runnable) {
                        context.client().execute(runnable);
                    }

                    @Override
                    public Player getPlayer() {
                        return context.player();
                    }
                });
            });
        }
    }

    public static <T extends CustomPacketPayload> void registerC2S(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            BiConsumer<T, PayloadContext> handler
    ) {
        registerC2SType(type, codec);

        //if (!PlatformUtil.isClient()) {
            ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
                handler.accept(payload, new PayloadContext() {
                    @Override
                    public void enqueue(Runnable runnable) {
                        context.server().execute(runnable);
                    }

                    @Override
                    public Player getPlayer() {
                        return context.player();
                    }
                });
            });
        //}
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static <T extends CustomPacketPayload> void sendToServer(T payload) {
        ClientPlayNetworking.send(payload);
    }

    public interface PayloadContext {
        void enqueue(Runnable runnable);
        Player getPlayer();
    }
}
//? } else {
/*package com.yori3o.yo_hooks.impl;


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

        registrationEvent.registrar("2")
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

        registrationEvent.registrar("2")
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
*///? }
