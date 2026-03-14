package com.yori3o.yo_hooks.common.network;


import com.yori3o.yo_hooks.common.config.CommonConfig;
import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;



public class ClientPacketReceiver {


    public static void registerPackets() {
        PlatformNetworkHelper.registerS2C(
            SendCommonConfigPayload.TYPE,
            SendCommonConfigPayload.CODEC,
            (payload, context) -> {

                byte[] bytes = payload.values();

                String json = new String(bytes, StandardCharsets.UTF_8);

                Gson gson = new Gson();
                CommonConfig.Values values = gson.fromJson(json, CommonConfig.Values.class);

                DynamicConfigHandler.commonConfigUpdate(values);
            }
        );
    }

}
