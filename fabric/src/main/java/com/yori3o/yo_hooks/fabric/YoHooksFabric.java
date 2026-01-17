package com.yori3o.yo_hooks.fabric;

import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.impl.LootInjector;

import net.fabricmc.api.ModInitializer;


public final class YoHooksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YoHooks YoHooksClass = new YoHooks();
        YoHooksClass.init();
        
        LootInjector.register();
    }
}
