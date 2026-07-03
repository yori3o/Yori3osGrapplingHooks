package com.yori3o.yo_hooks.common.network;


import com.yori3o.yo_hooks.common.config.categories.CommonConfig;
import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.util.LoggerUtil;
import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;



public class ClientPacketReceiver {


    public static void registerPackets() {
        PlatformNetworkHelper.registerS2C(
            SendCommonConfigPayload.TYPE,
            SendCommonConfigPayload.CODEC,
            (payload, context) -> {
                try {
                    byte[] bytesConfig = payload.values();
                    byte[] bytesHookLengths = payload.hookLengths();

                    String jsonConfig = new String(bytesConfig, StandardCharsets.UTF_8);
                    String jsonHookLengths = new String(bytesHookLengths, StandardCharsets.UTF_8);

                    Gson gson = new Gson();
                    CommonConfig.Values values = gson.fromJson(jsonConfig, CommonConfig.Values.class);

                    ConfigManager.commonConfigUpdate(values);

                    @SuppressWarnings("unchecked")
                    Map<String, Object> hookLengths = gson.fromJson(jsonHookLengths, HashMap.class);

                    for (String hookId : hookLengths.keySet()) {
                        int length = ((Double)hookLengths.get(hookId)).intValue();
                        ItemRegistry.ALL_HOOKS.get(hookId).get().setLengthServerOverlap(length);
                    }
                } catch (Exception e) {
                    LoggerUtil.errorWithException("Error receiving config from server: ", e);
                }
            }
        );
    }

}
