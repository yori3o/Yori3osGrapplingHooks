package com.yori3o.yo_hooks.neoforge;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.client.render.HookRenderer;
import com.yori3o.yo_hooks.common.config.client.ConfigScreenFactory;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.impl.PlatformKeyMappingRegistry;

import net.minecraft.client.KeyMapping;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;



@EventBusSubscriber(modid = YoHooks.MOD_ID, value = Dist.CLIENT)
public final class YoHooksNeoForgeClientEvents {

    @SubscribeEvent
    public static void onRegisterKeymappings(RegisterKeyMappingsEvent event) {
        for (KeyMapping keyMapping : PlatformKeyMappingRegistry.keyMappings) {
            event.register(keyMapping);
        }
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.HOOK_ENTITY.get(), HookRenderer::new);
    }

    

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ModLoadingContext.get().registerExtensionPoint(
            IConfigScreenFactory.class,
            () -> (mc, parent) -> ConfigScreenFactory.create(parent)
        );
    }
}
