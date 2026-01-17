package com.yori3o.yo_hooks.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import com.yori3o.yo_hooks.common.YoHooksEvents;
import com.yori3o.yo_hooks.impl.ClientPlatformRegistry;


public final class YoHooksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlatformRegistry.OnlyClientInit();
        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            YoHooksEvents.ClientTick_Pre();
        });
    }
}
