package com.yori3o.yo_hooks;

import net.fabricmc.api.ModInitializer;


public final class YoHooksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YoHooks YoHooksClass = new YoHooks();
        
        YoHooksClass.init();
    }
}
