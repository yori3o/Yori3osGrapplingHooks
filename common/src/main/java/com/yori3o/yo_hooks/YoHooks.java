package com.yori3o.yo_hooks;

import com.yori3o.yo_hooks.config.CommonConfig;
import com.yori3o.yo_hooks.config.ServerConfig;
import com.yori3o.yo_hooks.network.JumpServerPacket;
import com.yori3o.yo_hooks.register.ModEntities;
import com.yori3o.yo_hooks.register.ModItems;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import net.minecraft.server.MinecraftServer;


public class YoHooks {
	public static final String MOD_ID = "yo_hooks";
	
	public void init() {
		ModEntities.register();
		ModItems.register();
		JumpServerPacket.registerPackets();

		CommonConfig cc = new CommonConfig();
        cc.load();

		LifecycleEvent.SERVER_STARTED.register(this::onServerStarted);
		
		EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            YoHooksClient.OnlyClientInit();
        });
	}

	private void onServerStarted(MinecraftServer server) {
        ServerConfig sc = new ServerConfig();
        sc.load();
    }
}