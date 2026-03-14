package com.yori3o.yo_hooks.common.network;


import com.yori3o.yo_hooks.common.config.CommonConfig;
import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import net.minecraft.server.level.ServerPlayer;

import java.nio.charset.StandardCharsets;
import com.google.gson.Gson;

// for 1.20.1
/*import io.netty.buffer.Unpooled;
import net.minecraft.resources.Identifier;
import net.minecraft.network.FriendlyByteBuf;*/

// for 1.21.11 fabric
//import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;



public class ServerSender {

    
    public static void sendCommonConfig(ServerPlayer serverPlayer, CommonConfig.Values values) {

        Gson gson = new Gson();
        byte[] bytes = gson.toJson(values).getBytes(StandardCharsets.UTF_8);

        /*FriendlyByteBuf originalBuf = new FriendlyByteBuf(Unpooled.buffer()); 
        originalBuf.writeBytes(bytes);
        PlatformNetworkHelper.sendToServer(Identifier.fromNamespaceAndPath("yo_hooks", "send_common_config"), originalBuf); */

        SendCommonConfigPayload payload = new SendCommonConfigPayload(
                bytes
        );//LoggerUtil.LOGGER.info("dfdfdfdf");

        PlatformNetworkHelper.sendToPlayer(serverPlayer, payload);

        //ClientPlayNetworking.send(payload); // FOR 1.21.11 fabric
    }

}
