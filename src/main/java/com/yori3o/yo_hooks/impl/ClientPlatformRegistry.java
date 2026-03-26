package com.yori3o.yo_hooks.impl;


import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.client.render.HookRenderer;

import net.minecraft.client.renderer.entity.EntityRenderers;



public class ClientPlatformRegistry {

    public static void initClient() {
        EntityRenderers.register(
            EntityRegistry.HOOK_ENTITY.get(), 
            HookRenderer::new
        );
    }
}
