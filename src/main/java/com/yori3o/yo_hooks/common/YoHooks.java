package com.yori3o.yo_hooks.common;


import java.nio.file.Path;

import com.yori3o.yo_hooks.common.compat.Compats;
import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.hookregistry.HookRegistry;
import com.yori3o.yo_hooks.common.hookregistry.LootTableRegistry;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.util.ConfigFilesMover;
import com.yori3o.yo_hooks.impl.PlatformUtil;



public class YoHooks {


	public static final String MOD_ID = "yo_hooks";

    public static final Path CONFIG_FOLDER = PlatformUtil.getConfigDir().resolve("yo_hooks");

    
	public void init() {

        ConfigFilesMover.moveConfigFiles();
        DynamicConfigHandler.loadCommon();
        DynamicConfigHandler.loadServer();
        DynamicConfigHandler.loadOverlap();

        HookRegistry.loadAndRegisterHooks();
        LootTableRegistry.load();

		EntityRegistry.register();

        Compats.checkForLoadedMods();

		
		if (PlatformUtil.isClient()) {
            YoHooksClient.initClient();
        }

	}

}