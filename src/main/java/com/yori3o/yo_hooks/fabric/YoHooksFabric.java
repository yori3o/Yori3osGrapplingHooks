package com.yori3o.yo_hooks.fabric;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.event.EventHandler;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.network.ClientReceiver;
import com.yori3o.yo_hooks.common.network.ServerReceiver;
import com.yori3o.yo_hooks.impl.LootInjector;
import com.yori3o.yo_hooks.common.compat.Compats;
import com.yori3o.yo_hooks.common.compat.sable.SableCompat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;



public class YoHooksFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        (new YoHooks()).init();

        EntityRegistry.register();
        ServerReceiver.register();
        ClientReceiver.register();
        
        LootInjector.register();

        ServerPlayConnectionEvents.JOIN.register((serverGamePacketListenerImpl, packetSender, minecraftServer) -> {
            EventHandler.whenPlayerJoinToServer(serverGamePacketListenerImpl.player);
        });

        ServerLifecycleEvents.SERVER_STARTED.register((minecraftServer) -> {
            EventHandler.onServerStarted(minecraftServer);
        });

        ServerTickEvents.START_WORLD_TICK.register((minecraftServer) -> {
            if (Compats.isSableLoaded) SableCompat.tick();
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((livingEntity, damageSource) -> {
            if (livingEntity instanceof Player player) {
                EventHandler.whenPlayerDie(player, damageSource);
            }
        });
    }

}
