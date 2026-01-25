package com.yori3o.yo_hooks.common;

import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.network.ServerPacketReceiver;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.init.ItemRegistry;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;


public class YoHooks {
	public static final String MOD_ID = "yo_hooks";

	
	public void init() {

        DynamicConfigHandler.CommonConfigLoad();
        DynamicConfigHandler.CommonConfigUpdate();
        DynamicConfigHandler.ServerConfigUpdate();

		EntityRegistry.register();
		ItemRegistry.register();
         ServerPacketReceiver.registerTypes();
        ServerPacketReceiver.registerPackets();
       


		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        //ServerLifecycleEvents.SETUP.register(this::onSetup);
		
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            YoHooksClient.OnlyClientInit();
        }
	}




	private void onServerStarted(MinecraftServer server) {
        DynamicConfigHandler.ServerConfigUpdate();
        DynamicConfigHandler.CommonConfigUpdate();
    }
}