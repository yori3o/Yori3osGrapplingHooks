package com.yori3o.yo_hooks.common;

import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.network.ServerPacketReceiver;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.init.ItemRegistry;

import net.minecraft.server.MinecraftServer;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;


public class YoHooks {
	public static final String MOD_ID = "yo_hooks";

	
	public void init() {

        DynamicConfigHandler.CommonConfigLoad();
        DynamicConfigHandler.CommonConfigUpdate();
        DynamicConfigHandler.ServerConfigUpdate();

		EntityRegistry.register();
		ItemRegistry.register();
        ServerPacketReceiver.registerPackets();


		LifecycleEvent.SERVER_STARTED.register(this::onServerStarted);
        LifecycleEvent.SETUP.register(this::onSetup);
		
		EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            YoHooksClient.OnlyClientInit();
        });
	}


    private void onSetup() {
        ItemRegistry.registerHookHeads();
    }


	private void onServerStarted(MinecraftServer server) {
        DynamicConfigHandler.ServerConfigUpdate();
        DynamicConfigHandler.CommonConfigUpdate();
    }
}