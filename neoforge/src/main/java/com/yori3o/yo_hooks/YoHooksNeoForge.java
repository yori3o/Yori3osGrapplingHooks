package com.yori3o.yo_hooks;

import net.neoforged.fml.common.Mod;

@Mod(YoHooks.MOD_ID)
public final class YoHooksNeoForge {
    public YoHooksNeoForge() {
        YoHooks YoHooksClass = new YoHooks();
        
        YoHooksClass.init();
    }
}
