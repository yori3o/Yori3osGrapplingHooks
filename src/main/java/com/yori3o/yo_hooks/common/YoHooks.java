package com.yori3o.yo_hooks.common;


import java.nio.file.Path;

import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.hookregistry.HookRegistry;
import com.yori3o.yo_hooks.common.hookregistry.LootTableRegistry;
import com.yori3o.yo_hooks.common.init.ComponentRegistry;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.init.StatsRegistry;
import com.yori3o.yo_hooks.common.util.ConfigFilesMover;
import com.yori3o.yo_hooks.impl.PlatformUtil;



public class YoHooks {


	public static final String MOD_ID = "yo_hooks";

    public static final Path CONFIG_FOLDER = PlatformUtil.getConfigDir().resolve("yo_hooks");

    
	public void init() {

        ConfigFilesMover.moveConfigFiles();
        ConfigManager.loadCommon();
        ConfigManager.loadServer();
        ConfigManager.loadOverlap();

        ComponentRegistry.register();
        HookRegistry.loadAndRegisterHooks();
        LootTableRegistry.load();

		EntityRegistry.register();
		StatsRegistry.register();

		
		if (PlatformUtil.isClient()) {
            YoHooksClient.initClient();
        }

	}

}