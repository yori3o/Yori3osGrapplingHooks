//? if fabric {
package com.yori3o.yo_hooks.fabric;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.event.EventHandler;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.network.ClientReceiver;
import com.yori3o.yo_hooks.common.network.ServerReceiver;
import com.yori3o.yo_hooks.impl.LootInjector;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;



public class YoHooksFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        (new YoHooks()).init();

        com.yori3o.yo_hooks.impl.CreativeTabRegistry.register();
        com.yori3o.yo_hooks.common.init.StatsRegistry.initializeFormatters();
        ServerReceiver.register();
        ClientReceiver.register();
        
        LootInjector.register();
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.START_SERVER_TICK.register(server -> EventHandler.whenServerTick());

        ServerPlayConnectionEvents.JOIN.register((serverGamePacketListenerImpl, packetSender, minecraftServer) -> {
            EventHandler.whenPlayerJoinToServer(serverGamePacketListenerImpl.player);
        });

        ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer) -> {
            EventHandler.onServerStarted(minecraftServer);
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
            if (livingEntity instanceof Player player) {
                EventHandler.whenPlayerDie(player, damageSource);
            }
        });
    }

}
//? }
