package com.yori3o.yo_hooks.common;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.KeyMapping;
//import net.minecraft.client.Minecraft;
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
        InputConstants.KEY_X,
        YO_HOOKS_CATEGORY
    );
    public static final KeyMapping CLIMB_DOWN = new KeyMapping(
        "key.yo_hooks.climb_down",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_Z,
        YO_HOOKS_CATEGORY
    );

    public static void OnlyClientInit() {
        KeyBindingHelper.registerKeyBinding(JUMP);
        KeyBindingHelper.registerKeyBinding(CLIMB);
        KeyBindingHelper.registerKeyBinding(CLIMB_DOWN);


        ClientPlayConnectionEvents.JOIN.register((ClientPacketListener, PacketSender, mc) -> {
            YoHooksEvents.ClientPlayerJoin();
        });
    }
    
}
