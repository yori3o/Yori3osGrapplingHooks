package com.yori3o.yo_hooks.common;


import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.impl.PlatformKeyMappingRegistry;

import net.minecraft.client.KeyMapping;

import com.mojang.blaze3d.platform.InputConstants;



public class YoHooksClient {
    

public static final KeyMapping JUMP = new KeyMapping(
        "key.yo_hooks.jump",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_SPACE,
        "category.yo_hooks"
    );
    public static final KeyMapping CLIMB = new KeyMapping(
        "key.yo_hooks.climb",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        "category.yo_hooks"
    );
    public static final KeyMapping CLIMB_DOWN = new KeyMapping(
        "key.yo_hooks.climb_down",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        "category.yo_hooks"
    );
    public static final KeyMapping PREVENT_USE = new KeyMapping(
        "key.yo_hooks.prevent_use",
        InputConstants.Type.KEYSYM,
        InputConstants.UNKNOWN.getValue(),
        "category.yo_hooks"
    );


    public static void initClient() {
        PlatformKeyMappingRegistry.registerKeyMapping(JUMP);
        PlatformKeyMappingRegistry.registerKeyMapping(CLIMB);
        PlatformKeyMappingRegistry.registerKeyMapping(CLIMB_DOWN);
        PlatformKeyMappingRegistry.registerKeyMapping(PREVENT_USE);
        
        DynamicConfigHandler.loadClient();

    }
    
}
