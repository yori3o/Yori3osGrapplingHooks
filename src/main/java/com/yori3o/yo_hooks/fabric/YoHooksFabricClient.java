package com.yori3o.yo_hooks.fabric;


import com.yori3o.yo_hooks.common.event.EventHandler;
import com.yori3o.yo_hooks.impl.ClientPlatformRegistry;
import com.yori3o.yo_hooks.impl.CreativeTabRegistry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;



public final class YoHooksFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        ClientPlatformRegistry.initClient();
        

        CreativeTabRegistry.register();
        
        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            EventHandler.whenClientTickStart();;
        });
    }

}
