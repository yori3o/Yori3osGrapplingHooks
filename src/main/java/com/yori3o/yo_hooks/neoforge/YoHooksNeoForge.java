package com.yori3o.yo_hooks.neoforge;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.compat.Compats;
import com.yori3o.yo_hooks.common.compat.sable.SableCompat;
import com.yori3o.yo_hooks.common.event.EventHandler;
import com.yori3o.yo_hooks.impl.CreativeTabRegistry;
import com.yori3o.yo_hooks.impl.PlatformEntityRegistry;
import com.yori3o.yo_hooks.impl.PlatformItemRegistry;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;



@Mod(YoHooks.MOD_ID)
public final class YoHooksNeoForge {

    public YoHooksNeoForge(IEventBus modEventBus) {
        (new YoHooks()).init();

        PlatformItemRegistry.register(modEventBus);

        CreativeTabRegistry.initRegister(modEventBus);

        PlatformEntityRegistry.ENTITIES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::serverTick);
        NeoForge.EVENT_BUS.addListener(this::tick);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);
        NeoForge.EVENT_BUS.addListener(this::onLivingDeath);

    }


    private void tick(ClientTickEvent.Pre event) {
        EventHandler.whenClientTickStart();
        if (Compats.isSableLoaded) SableCompat.tick();
    }

    private void serverTick(ServerTickEvent.Pre event) {
        if (Compats.isSableLoaded) SableCompat.tick();
    }

    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        EventHandler.whenPlayerJoinToServer((ServerPlayer)event.getEntity());
    }

    private void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        
        if (entity instanceof Player player) {
                EventHandler.whenPlayerDie(player, event.getSource());
            }
    }
    
}
