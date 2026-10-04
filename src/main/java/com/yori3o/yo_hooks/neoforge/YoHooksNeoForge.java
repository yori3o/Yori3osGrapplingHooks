package com.yori3o.yo_hooks.neoforge;


import java.util.function.Supplier;

import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.event.EventHandler;
import com.yori3o.yo_hooks.impl.CreativeTabRegistry;
import com.yori3o.yo_hooks.impl.PlatformEntityRegistry;
import com.yori3o.yo_hooks.impl.PlatformItemRegistry;

import net.minecraft.core.component.DataComponentExactPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;



@Mod(YoHooks.MOD_ID)
public final class YoHooksNeoForge {

    
    public static final DeferredRegister<DataComponentType<?>> cm =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, YoHooks.MOD_ID);


    public static void register(IEventBus bus) {
        cm.register(bus);
    }

    public static <T extends DataComponentType<?>> Supplier<T> registerC(
            Identifier id,
            Supplier<T> supplier
    ) {
        DeferredHolder<DataComponentType<?>, T> obj = cm.register(id.getPath(), supplier);
        return obj;
    }

    
    
    
    
    
    
    
    
    public YoHooksNeoForge(IEventBus modEventBus) {
        (new YoHooks()).init();

        PlatformItemRegistry.register(modEventBus);

        CreativeTabRegistry.initRegister(modEventBus);

        PlatformEntityRegistry.ENTITIES.register(modEventBus);

        cm.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::tick);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);
        NeoForge.EVENT_BUS.addListener(this::onLivingDeath);

    }


    private void tick(ClientTickEvent.Pre event) {
        EventHandler.whenClientTickStart();
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
