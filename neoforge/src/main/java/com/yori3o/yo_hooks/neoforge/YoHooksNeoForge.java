package com.yori3o.yo_hooks.neoforge;

import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.YoHooksEvents;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(YoHooks.MOD_ID)
public final class YoHooksNeoForge {
    public YoHooksNeoForge() {
        YoHooks YoHooksClass = new YoHooks();
        
        YoHooksClass.init();

        NeoForge.EVENT_BUS.addListener(this::tick);

    }


    private void tick(ClientTickEvent.Pre event) {
        YoHooksEvents.ClientTick_Pre();
    }
    
}
