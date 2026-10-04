package com.yori3o.yo_hooks.neoforge;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.network.ClientReceiver;
import com.yori3o.yo_hooks.common.network.ServerReceiver;
import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;



@EventBusSubscriber(modid = YoHooks.MOD_ID)
public final class YoHooksNeoForgeEvents {

    @SubscribeEvent
    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PlatformNetworkHelper.init(event);
        ServerReceiver.register();
        ClientReceiver.register();
    }
}
