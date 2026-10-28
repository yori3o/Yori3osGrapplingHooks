//? if neoforge {
/*package com.yori3o.yo_hooks.neoforge;

import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.event.EventHandler;
import com.yori3o.yo_hooks.common.init.StatsRegistry;
import com.yori3o.yo_hooks.common.network.ClientReceiver;
import com.yori3o.yo_hooks.common.network.ServerReceiver;
import com.yori3o.yo_hooks.impl.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(YoHooks.MOD_ID)
public final class YoHooksNeoForge {
    public YoHooksNeoForge(IEventBus bus) {
        new YoHooks().init();
        PlatformItemRegistry.register(bus);
        PlatformEntityRegistry.ENTITIES.register(bus);
        PlatformRegistries.attach(bus);
        CreativeTabRegistry.initRegister(bus);
        bus.addListener(PlatformSoundRegistry::onRegister);
        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(StatsRegistry::initializeFormatters));
        bus.addListener((RegisterPayloadHandlersEvent event) -> {
            PlatformNetworkHelper.init(event);
            ServerReceiver.register();
            ClientReceiver.register();
        });
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.tick.ServerTickEvent.Pre event) -> EventHandler.whenServerTick());
        NeoForge.EVENT_BUS.addListener((ServerStartedEvent event) -> EventHandler.onServerStarted(event.getServer()));
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> EventHandler.whenPlayerJoinToServer((ServerPlayer) event.getEntity()));
        NeoForge.EVENT_BUS.addListener((LivingDeathEvent event) -> {
            if (event.getEntity() instanceof Player player) EventHandler.whenPlayerDie(player, event.getSource());
        });
    }
}
*///? }
