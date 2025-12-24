package com.yori3o.yo_hooks;

import com.yori3o.yo_hooks.world.LootInjector;

import net.fabricmc.api.ModInitializer;


public final class YoHooksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        YoHooks YoHooksClass = new YoHooks();
        
        YoHooksClass.init();
        LootInjector.register();
    }
}
