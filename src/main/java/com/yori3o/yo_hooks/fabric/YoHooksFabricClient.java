//? if fabric {
package com.yori3o.yo_hooks.fabric;


import com.yori3o.yo_hooks.common.event.EventHandler;
import com.yori3o.yo_hooks.impl.CreativeTabRegistry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;



public final class YoHooksFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        com.yori3o.yo_hooks.common.YoHooksClient.initClient();
        com.yori3o.yo_hooks.impl.PlatformEntityRendererRegistry.registerEntityRenderer(
            com.yori3o.yo_hooks.common.init.EntityRegistry.HOOK_ENTITY.get(),
            com.yori3o.yo_hooks.common.client.render.HookRenderer::new);
        
        ClientTickEvents.START_CLIENT_TICK.register(mc -> {
            EventHandler.whenClientTickStart();;
        });

        ClientPlayConnectionEvents.DISCONNECT.register((clientPacketListener, minecraft) -> {
            EventHandler.clientDisconnect();
        });
    }

}
//? }
