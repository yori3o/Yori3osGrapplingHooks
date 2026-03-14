package com.yori3o.yo_hooks.common.event;


import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;


/**
 * These methods are called from platform events.
 * They also check configuration settings and separate client and server logic.
 */
public class EventHandler {


    public static void onServerStarted(MinecraftServer server) {
        ServerEvents.loadConfigOnServer(server);
    }

    public static void whenPlayerJoinToServer(ServerPlayer serverPlayer) {
        ServerEvents.sendConfigToNewPlayer(serverPlayer);
    }

    public static void whenClientTickStart() {
        ClientEvents.clientTickStart();
    }

}