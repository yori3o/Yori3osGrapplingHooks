package com.yori3o.yo_hooks.common.network;


import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import net.minecraft.client.Minecraft;



public class ClientSender {

    public static void jumpFromHook(boolean cancelUsing) {
        
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null || minecraft.player == null) return;
        var hook = ((com.yori3o.yo_hooks.common.util.interfaces.PlayerWithHookData) minecraft.player).yo_hooks$getHook();
        if (hook == null || hook.isRemoved() || !hook.isInBlock()) return;

        PlayerJumpPayload payload = new PlayerJumpPayload(
                hook.getUUID(), cancelUsing
        );

        PlatformNetworkHelper.sendToServer(payload);
    }

    public static void climb(boolean up, boolean shouldPlaySound) {
    
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null || minecraft.player == null) return;
        var hook = ((com.yori3o.yo_hooks.common.util.interfaces.PlayerWithHookData) minecraft.player).yo_hooks$getHook();
        if (hook == null || hook.isRemoved() || !hook.isInBlock()) return;

        PlayerClimbPayload payload = new PlayerClimbPayload(
                hook.getUUID(), up, shouldPlaySound
        );

        PlatformNetworkHelper.sendToServer(payload);
    }
}
