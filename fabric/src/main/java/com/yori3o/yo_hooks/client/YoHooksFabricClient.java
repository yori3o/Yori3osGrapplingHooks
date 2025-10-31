package com.yori3o.yo_hooks.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

import com.yori3o.yo_hooks.YoHooksClientPlatformInit;


public final class YoHooksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        YoHooksClientPlatformInit.OnlyClientInit();
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            YoHooksClientPlatformInit.makeHooksItem();
        });
    }
}
