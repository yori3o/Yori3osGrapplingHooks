package com.yori3o.yo_hooks.neoforge;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.network.ClientPacketReceiver;
import com.yori3o.yo_hooks.common.network.ServerPacketReceiver;
import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;



@EventBusSubscriber(modid = YoHooks.MOD_ID)
public final class YoHooksNeoForgeEvents {

    @SubscribeEvent
    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PlatformNetworkHelper.init(event);
        ServerPacketReceiver.registerPackets();
        ClientPacketReceiver.registerPackets();
    }
}
