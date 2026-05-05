package com.yori3o.yo_hooks.neoforge;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.impl.PlatformKeyMappingRegistry;

import net.minecraft.client.KeyMapping;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;



@EventBusSubscriber(modid = YoHooks.MOD_ID, value = Dist.CLIENT)
public final class YoHooksNeoForgeClientEvents {

    @SubscribeEvent
    public static void onRegisterKeymappings(RegisterKeyMappingsEvent event) {
        for (KeyMapping keyMapping : PlatformKeyMappingRegistry.keyMappings) {
            event.register(keyMapping);
        }
    }
}
