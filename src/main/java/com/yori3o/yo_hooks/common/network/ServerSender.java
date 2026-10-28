package com.yori3o.yo_hooks.common.network;


import com.yori3o.yo_hooks.common.config.categories.CommonConfig;
import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import net.minecraft.server.level.ServerPlayer;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import com.google.gson.Gson;



public class ServerSender {

    
    public static void sendCommonConfig(ServerPlayer serverPlayer, CommonConfig values, Map<String, Integer> hookLengths, Map<String, Integer> hookDamages) {

        Gson gson = new Gson();
        byte[] bytesConfig = gson.toJson(values).getBytes(StandardCharsets.UTF_8);
        
        byte[] bytesHookLengths = gson.toJson(hookLengths).getBytes(StandardCharsets.UTF_8);
        
        byte[] bytesHookDamages = gson.toJson(hookDamages).getBytes(StandardCharsets.UTF_8);

        SendCommonConfigPayload payload = new SendCommonConfigPayload(
                bytesConfig, bytesHookLengths, bytesHookDamages
        );

        PlatformNetworkHelper.sendToPlayer(serverPlayer, payload);
    }

}
