package com.yori3o.yo_hooks.common.network;


import com.yori3o.yo_hooks.common.config.categories.CommonConfig;
import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import net.minecraft.server.level.ServerPlayer;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import com.google.gson.Gson;



public class ServerSender {

    
    public static void sendCommonConfig(ServerPlayer serverPlayer, CommonConfig.Values values, Map<String, Integer> HookLengths) {

        Gson gson = new Gson();
        byte[] bytesConfig = gson.toJson(values).getBytes(StandardCharsets.UTF_8);
        
        byte[] bytesHookLengths = gson.toJson(HookLengths).getBytes(StandardCharsets.UTF_8);

        SendCommonConfigPayload payload = new SendCommonConfigPayload(
                bytesConfig, bytesHookLengths
        );

        PlatformNetworkHelper.sendToPlayer(serverPlayer, payload);
    }

}
