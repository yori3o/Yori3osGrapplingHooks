package com.yori3o.yo_hooks.common.network;

import net.minecraft.client.Minecraft;
import dev.architectury.networking.NetworkManager;

// for 1.20.1
/*import io.netty.buffer.Unpooled;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.FriendlyByteBuf;*/

// for 1.21.11 fabric
//import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientSender {

    public static void JumpFromHook(boolean cancelUsing) {
        
        //LoggerUtil.LOGGER.info("Player jump packet sended, cancelUsing = " + cancelUsing);
            
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return;

        /*
        FriendlyByteBuf originalBuf = new FriendlyByteBuf(Unpooled.buffer()); 
        originalBuf.writeBoolean(cancelUsing);
        NetworkManager.sendToServer(ResourceLocation.fromNamespaceAndPath("yo_hooks", "player_jump"), originalBuf);*/

        PlayerJumpPayload payload = new PlayerJumpPayload(
                cancelUsing
        );

        NetworkManager.sendToServer(payload);

        //ClientPlayNetworking.send(payload); // FOR 1.21.11 fabric
    }

    public static void CheckConfig(boolean softHook, float stiffness, float climbSpeed, boolean funnyMode) {
    
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return;
        /*FriendlyByteBuf originalBuf = new FriendlyByteBuf(Unpooled.buffer()); 
        originalBuf.writeBoolean(softHook);
        originalBuf.writeFloat(stiffness);
        originalBuf.writeFloat(climbSpeed);
        NetworkManager.sendToServer(ResourceLocation.fromNamespaceAndPath("yo_hooks", "check_config"), originalBuf); */

        PlayerCheckConfigPayload payload = new PlayerCheckConfigPayload(
                softHook, stiffness, climbSpeed, funnyMode
        );

        NetworkManager.sendToServer(payload);

        //ClientPlayNetworking.send(payload); // FOR 1.21.11 fabric
    }

    public static void Climb(boolean up, int agility_level) {
    
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return;
        /*FriendlyByteBuf originalBuf = new FriendlyByteBuf(Unpooled.buffer()); 
        originalBuf.writeBoolean(up);
        NetworkManager.sendToServer(ResourceLocation.fromNamespaceAndPath("yo_hooks", "climb"), originalBuf); */

        PlayerClimbPayload payload = new PlayerClimbPayload(
                up, agility_level
        );

        NetworkManager.sendToServer(payload);

        //ClientPlayNetworking.send(payload); // FOR 1.21.11 fabric
    }
}
