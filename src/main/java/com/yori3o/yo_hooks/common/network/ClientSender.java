package com.yori3o.yo_hooks.common.network;


import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import net.minecraft.client.Minecraft;

// for 1.20.1
/*import io.netty.buffer.Unpooled;
import net.minecraft.resources.Identifier;
import net.minecraft.network.FriendlyByteBuf;*/



public class ClientSender {

    public static void jumpFromHook(boolean cancelUsing) {
        
            
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return;

        /*
        FriendlyByteBuf originalBuf = new FriendlyByteBuf(Unpooled.buffer()); 
        originalBuf.writeBoolean(cancelUsing);
        PlatformNetworkHelper.sendToServer(Identifier.fromNamespaceAndPath("yo_hooks", "player_jump"), originalBuf);*/

        PlayerJumpPayload payload = new PlayerJumpPayload(
                cancelUsing
        );

        PlatformNetworkHelper.sendToServer(payload);
    }

    public static void climb(boolean up, int agility_level, boolean shouldPlaySound) {
    
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return;
        /*FriendlyByteBuf originalBuf = new FriendlyByteBuf(Unpooled.buffer()); 
        originalBuf.writeBoolean(up);
        PlatformNetworkHelper.sendToServer(Identifier.fromNamespaceAndPath("yo_hooks", "climb"), originalBuf); */

        PlayerClimbPayload payload = new PlayerClimbPayload(
                up, agility_level, shouldPlaySound
        );

        PlatformNetworkHelper.sendToServer(payload);
    }
}
