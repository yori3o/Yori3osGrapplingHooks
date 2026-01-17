package com.yori3o.yo_hooks.common;

import net.minecraft.client.KeyMapping;

import com.mojang.blaze3d.platform.InputConstants;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;


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
        InputConstants.KEY_X,
        "category.yo_hooks"
    );
    public static final KeyMapping CLIMB_DOWN = new KeyMapping(
        "key.yo_hooks.climb_down",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_Z,
        "category.yo_hooks"
    );

    public static void OnlyClientInit() {
        KeyMappingRegistry.register(JUMP);
        KeyMappingRegistry.register(CLIMB);
        KeyMappingRegistry.register(CLIMB_DOWN);


        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register((level) -> {
            YoHooksEvents.ClientPlayerJoin();
        });
    }
    
}
