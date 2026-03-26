package com.yori3o.yo_hooks.common.event;


import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.yori3o.yo_hooks.common.config.CommonConfig;
import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;
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

        Map<String, Integer> hookLengths = new HashMap<>();
        for (Supplier<HookItem> hook : ItemRegistry.ALL_HOOKS.values()) {
            hookLengths.put(hook.get().hookDefinition.id, hook.get().hookDefinition.length);
        }
        ServerSender.sendCommonConfig(serverPlayer, cc.get(), hookLengths);
    }
    
}