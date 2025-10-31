package com.yori3o.yo_hooks.network;

import net.minecraft.network.FriendlyByteBuf;
// only for 1.21.1+
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;

import io.netty.buffer.Unpooled;
import dev.architectury.networking.NetworkManager;


public class JumpClientSend {

    @SuppressWarnings("removal")
    public static void PlayerJumpedFromHook() {
    
        // Получаем необходимые объекты на клиентской стороне
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return; // Проверка соединения
        
        // Получаем RegistryAccess
        RegistryAccess registryAccess = minecraft.getConnection().registryAccess();
        
        // Создаем буферы
        FriendlyByteBuf originalBuf = new FriendlyByteBuf(Unpooled.buffer()); 
        // ❗️ Оборачиваем в RegistryFriendlyByteBuf
        RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(originalBuf, registryAccess);

        //registryBuf.clear();

        //registryBuf.writeInt(1);
        
        NetworkManager.sendToServer(ResourceLocation.fromNamespaceAndPath("yo_hooks", "player_jumped_from_hook"), registryBuf);
        
    }
}
