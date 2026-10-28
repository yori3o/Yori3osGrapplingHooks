//? if neoforge {
/*package com.yori3o.yo_hooks.neoforge;

import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.YoHooksClient;
import com.yori3o.yo_hooks.common.client.render.HookRenderer;
import com.yori3o.yo_hooks.common.config.client.ConfigScreenFactory;
import com.yori3o.yo_hooks.common.event.EventHandler;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.impl.PlatformKeyMappingRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@EventBusSubscriber(modid = YoHooks.MOD_ID, value = Dist.CLIENT)
public final class YoHooksNeoForgeClient {
    @SubscribeEvent
    public static void keys(RegisterKeyMappingsEvent event) {
        YoHooksClient.initClient();
        PlatformKeyMappingRegistry.keyMappings.forEach(event::register);
        ModList.get().getModContainerById(YoHooks.MOD_ID).orElseThrow().registerExtensionPoint(
            IConfigScreenFactory.class, (container, parent) -> ConfigScreenFactory.create(parent));
    }
    @SubscribeEvent
    public static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.HOOK_ENTITY.get(), HookRenderer::new);
    }
    @SubscribeEvent
    public static void tick(ClientTickEvent.Pre event) { EventHandler.whenClientTickStart(); }
    @SubscribeEvent
    public static void disconnect(ClientPlayerNetworkEvent.LoggingOut event) { EventHandler.clientDisconnect(); }
}
*///? }
