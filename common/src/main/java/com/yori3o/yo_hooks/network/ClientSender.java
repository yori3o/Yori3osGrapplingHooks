package com.yori3o.yo_hooks.network;

import net.minecraft.network.FriendlyByteBuf;
// only for 1.21.1+
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;

import io.netty.buffer.Unpooled;
import dev.architectury.networking.NetworkManager;


public class ClientSender {

    public static void JumpFromHook() {
    
        // Получаем необходимые объекты на клиентской стороне
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return; // Проверка соединения
        // Получаем RegistryAccess
        RegistryAccess registryAccess = minecraft.getConnection().registryAccess();
        // Создаем буферы
        FriendlyByteBuf originalBuf = new FriendlyByteBuf(Unpooled.buffer()); 
        // ❗️ Оборачиваем в RegistryFriendlyByteBuf
        RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(originalBuf, registryAccess);

        
        NetworkManager.sendToServer(ResourceLocation.fromNamespaceAndPath("yo_hooks", "player_jump"), registryBuf);
        
    }

    public static void CheckConfig(Boolean softHook) {
    
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return;
        RegistryAccess registryAccess = minecraft.getConnection().registryAccess();
        FriendlyByteBuf originalBuf = new FriendlyByteBuf(Unpooled.buffer()); 
        RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(originalBuf, registryAccess);

        registryBuf.writeBoolean(softHook);
        
        NetworkManager.sendToServer(ResourceLocation.fromNamespaceAndPath("yo_hooks", "check_config"), registryBuf); 
    }

    public static void Climb(boolean up) {
    
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getConnection() == null) return;
        RegistryAccess registryAccess = minecraft.getConnection().registryAccess();
        FriendlyByteBuf originalBuf = new FriendlyByteBuf(Unpooled.buffer()); 
        RegistryFriendlyByteBuf registryBuf = new RegistryFriendlyByteBuf(originalBuf, registryAccess);

        registryBuf.writeBoolean(up);
        
        NetworkManager.sendToServer(ResourceLocation.fromNamespaceAndPath("yo_hooks", "climb"), registryBuf); 
    }
}
