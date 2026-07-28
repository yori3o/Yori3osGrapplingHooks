package com.yori3o.yo_hooks.common.network;


import com.yori3o.yo_hooks.common.config.categories.CommonConfig;
import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.util.LoggerUtil;
import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.gson.Gson;



public class ClientReceiver {


    public static void register() {
        PlatformNetworkHelper.registerS2C(
            SendCommonConfigPayload.TYPE,
            SendCommonConfigPayload.CODEC,
            (payload, context) -> {
                context.enqueue(() -> {
                    try {
                        byte[] bytesConfig = payload.values();
                        byte[] bytesHookLengths = payload.hookLengths();

                        String jsonConfig = new String(bytesConfig, StandardCharsets.UTF_8);
                        String jsonHookLengths = new String(bytesHookLengths, StandardCharsets.UTF_8);

                        Gson gson = new Gson();
                        CommonConfig values = gson.fromJson(jsonConfig, CommonConfig.class);

                        ConfigManager.commonConfigUpdate(values);

                        @SuppressWarnings("unchecked")
                        Map<String, Object> hookLengths = gson.fromJson(jsonHookLengths, HashMap.class);

                        if (hookLengths != null) {
                            for (String hookId : hookLengths.keySet()) {
                                Object val = hookLengths.get(hookId);
                                if (val instanceof Number num) {
                                    int length = num.intValue();
                                    Supplier<HookItem> hookSupplier = ItemRegistry.ALL_HOOKS.get(hookId);
                                    if (hookSupplier != null && hookSupplier.get() != null) {
                                        hookSupplier.get().setLengthServerOverlap(length);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        LoggerUtil.errorWithException("Error when receiving a common config from the server: ", e);
                    }
                });
            }
        );
    }

}
