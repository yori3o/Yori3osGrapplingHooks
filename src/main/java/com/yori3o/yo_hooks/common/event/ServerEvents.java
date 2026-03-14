package com.yori3o.yo_hooks.common.event;


import com.yori3o.yo_hooks.common.config.CommonConfig;
import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.network.ServerSender;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;



public class ServerEvents {


    public static void loadConfigOnServer(MinecraftServer server) {
        DynamicConfigHandler.loadServer();
        DynamicConfigHandler.loadCommon();
    }

    public static void sendConfigToNewPlayer(ServerPlayer serverPlayer) {
        CommonConfig cc = new CommonConfig();
        cc.load();
        ServerSender.sendCommonConfig(serverPlayer, cc.get());
    }
    
}