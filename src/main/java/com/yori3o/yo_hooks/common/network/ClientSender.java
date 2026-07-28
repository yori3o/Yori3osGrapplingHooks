package com.yori3o.yo_hooks.common.network;


import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import net.minecraft.client.Minecraft;



public class ClientSender {

    public static void jumpFromHook(boolean cancelUsing) {
        
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return;

        PlayerJumpPayload payload = new PlayerJumpPayload(
                cancelUsing
        );

        PlatformNetworkHelper.sendToServer(payload);
    }

    public static void climb(boolean up, boolean shouldPlaySound) {
    
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return;

        PlayerClimbPayload payload = new PlayerClimbPayload(
                up, shouldPlaySound
        );

        PlatformNetworkHelper.sendToServer(payload);
    }
}
