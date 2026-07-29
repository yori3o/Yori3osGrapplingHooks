package com.yori3o.yo_hooks.fabric;


import com.yori3o.yo_hooks.common.event.EventHandler;
import com.yori3o.yo_hooks.impl.CreativeTabRegistry;
import com.yori3o.yo_hooks.common.compat.Compats;
import com.yori3o.yo_hooks.common.compat.sable.SableCompat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;



public final class YoHooksFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        CreativeTabRegistry.register();
        
        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            EventHandler.whenClientTickStart();
            if (Compats.isSableLoaded) SableCompat.tickClient();
        });
    }

}
