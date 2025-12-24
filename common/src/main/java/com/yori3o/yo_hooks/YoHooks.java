package com.yori3o.yo_hooks;

import com.yori3o.yo_hooks.config.CommonConfig;
import com.yori3o.yo_hooks.config.ServerConfig;
import com.yori3o.yo_hooks.network.ServerPacketReceiver;
import com.yori3o.yo_hooks.init.EntitiyRegistry;
import com.yori3o.yo_hooks.init.ItemRegistry;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;


public class YoHooks {
	public static final String MOD_ID = "yo_hooks";

    public static Level level;

    // --- config values ---
    public static float decreaseSatiety;
    public static boolean breakingFragileBlocks;
    //public static float climbSpeed;
    public static final float climbSpeed = 0.125f;

    // --- common ---
    public static boolean softHook;
    public static int ironHookDurabitility;
    public static int goldHookDurabitility;
    public static int diamondHookDurabitility;
    public static int netheriteHookDurabitility;
    public static int ironHookLength;
    public static int goldHookLength;
    public static int diamondHookLength;
    public static int netheriteHookLength;

	
	public void init() {

        CommonConfig cc = new CommonConfig();
        cc.load();
        ironHookDurabitility = cc.get().ironHookDurabitility;
        goldHookDurabitility = cc.get().goldHookDurabitility;
        diamondHookDurabitility = cc.get().diamondHookDurabitility;
        netheriteHookDurabitility = cc.get().netheriteHookDurabitility;
        ironHookLength = cc.get().ironHookLength;
        goldHookLength = cc.get().goldHookLength;
        diamondHookLength = cc.get().diamondHookLength;
        netheriteHookLength = cc.get().netheriteHookLength;

		EntitiyRegistry.register();
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
        level = server.overworld();

        ServerConfig sc = new ServerConfig();
        sc.load();
        decreaseSatiety = sc.get().decreaseSatiety;
        breakingFragileBlocks = sc.get().breakingFragileBlocks;

        CommonConfig cc = new CommonConfig();
        cc.load();
        softHook = cc.get().softHook;
        //climbSpeed = cc.get().climbSpeed;
    }

}