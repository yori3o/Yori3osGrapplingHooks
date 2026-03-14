package com.yori3o.yo_hooks.common.mixin;


import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {


    @Shadow
    private boolean clientIsFloating;


    @Inject(method = "handleMovePlayer", at = @At("TAIL"))
    private void onTravel(ServerboundMovePlayerPacket serverboundMovePlayerPacket, CallbackInfo ci) {

        Player player = (Player)(((ServerGamePacketListenerImpl)(Object)this).player);

        PlayerWithHookData hookData = (PlayerWithHookData)player;

        if (hookData.getHook() != null) {
            if (hookData.getHook().isInBlock()) {
                clientIsFloating = false;
            }
        }
    }
    
}