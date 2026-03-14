package com.yori3o.yo_hooks.common;


import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.hookregistry.HookRegistry;
import com.yori3o.yo_hooks.common.hookregistry.LootTableRegistry;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.impl.PlatformUtil;



public class YoHooks {


	public static final String MOD_ID = "yo_hooks";

	
	public void init() {

        DynamicConfigHandler.loadCommon();
        DynamicConfigHandler.loadServer();

        HookRegistry.loadAndRegisterHooks();
        LootTableRegistry.load();

		EntityRegistry.register();

		
		if (PlatformUtil.isClient()) {
            YoHooksClient.initClient();
        }

	}

}