package com.yori3o.yo_hooks.common;


import com.yori3o.yo_hooks.common.client.render.HookRenderer;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.impl.PlatformEntityRendererRegistry;
import com.yori3o.yo_hooks.impl.PlatformKeyMappingRegistry;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.resources.Identifier;

import com.mojang.blaze3d.platform.InputConstants;



public class YoHooksClient {
    

public static final Category YO_HOOKS_CATEGORY = new Category(Identifier.fromNamespaceAndPath("yo_hooks", "hook"));

    public static final KeyMapping JUMP = new KeyMapping(
        "key.yo_hooks.jump",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_SPACE,
        YO_HOOKS_CATEGORY
    );
    public static final KeyMapping CLIMB = new KeyMapping(
        "key.yo_hooks.climb",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        YO_HOOKS_CATEGORY
    );
    public static final KeyMapping CLIMB_DOWN = new KeyMapping(
        "key.yo_hooks.climb_down",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        YO_HOOKS_CATEGORY
    );

    public static final KeyMapping PREVENT_USE = new KeyMapping(
        "key.yo_hooks.prevent_use",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        YO_HOOKS_CATEGORY
    );


    public static void initClient() {
        PlatformKeyMappingRegistry.registerKeyMapping(JUMP);
        PlatformKeyMappingRegistry.registerKeyMapping(CLIMB);
        PlatformKeyMappingRegistry.registerKeyMapping(CLIMB_DOWN);
        PlatformKeyMappingRegistry.registerKeyMapping(PREVENT_USE);

        PlatformEntityRendererRegistry.registerEntityRenderer(EntityRegistry.HOOK_ENTITY.get(), HookRenderer::new);
    }
    
}
