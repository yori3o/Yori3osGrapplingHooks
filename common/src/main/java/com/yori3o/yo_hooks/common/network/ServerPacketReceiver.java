package com.yori3o.yo_hooks.common.network;

import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.sounds.SoundRegistry;
import com.yori3o.yo_hooks.common.config.ConfigCompare;
import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.utils.PhysicVariables;
import com.yori3o.yo_hooks.common.utils.PlayerWithHookData;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import dev.architectury.networking.NetworkManager;

// for 1.21.11 fabric
//import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ServerPacketReceiver {

    /*public static void registerTypes() { // 1.21.11 fabric
        PayloadTypeRegistry.playC2S().register(
            PlayerJumpPayload.TYPE,
            PlayerJumpPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
            PlayerCheckConfigPayload.TYPE,
            PlayerCheckConfigPayload.CODEC
        );
        PayloadTypeRegistry.playC2S().register(
            PlayerClimbPayload.TYPE,
            PlayerClimbPayload.CODEC
        );
    }*/


    public static void registerPackets() {
        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            PlayerJumpPayload.TYPE,
            PlayerJumpPayload.CODEC,
            (payload, context) -> {

                boolean usingCancel = payload.usingCancel();

                Player player = context.getPlayer();
                PlayerWithHookData hookDataPlayer = (PlayerWithHookData) player;
                
                
                if (hookDataPlayer == null) return;

                HookEntity hook = hookDataPlayer.getHook();
                
                //LoggerUtil.LOGGER.info("Player jump packet received");

                if (hook != null && hook.isInBlock()/* && hookDataPlayer.isJumpAllowed()*/) {

                    // this variable should change as quickly as possible
                    hookDataPlayer.setUsingCancelAfterJump(usingCancel);
                
                    // but this logic should been executed in main thread
                    context.queue(() -> {

                        if (!player.isCreative() && !DynamicConfigHandler.funnyMode) player.getFoodData().addExhaustion(DynamicConfigHandler.decreaseSatiety / 3f);
                
                        //LoggerUtil.LOGGER.info("Player jumped on server (main thread)");
                        // Отцепляем крюк
                        hook.discard(); 
                        //hookDataPlayer.setUsingCancelAfterJump(usingCancel);

                        hookDataPlayer.setHook(null);

                        player.level().playSound(null,
                                player.getX(), player.getY(), player.getZ(),
                                SoundRegistry.BACK,
                                SoundSource.PLAYERS,
                                1.0f,
                                1.0f
                        );

                        player.resetFallDistance();

                        player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                        
                    });
                }
            }
        );



        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            PlayerCheckConfigPayload.TYPE,
            PlayerCheckConfigPayload.CODEC,
            (payload, context) -> {
                Player player = context.getPlayer();

                //if (!player.level().getServer().isDedicatedServer()) {LoggerUtil.LOGGER.info("its integrated server"); return;}

                boolean softHook = payload.softHook();
                float stiffness = payload.stiffness();
                float climbSpeed = payload.climbSpeed();
                boolean funnyMode = payload.funnyMode();

                

                String result = ConfigCompare.checkValues(softHook, stiffness, climbSpeed, funnyMode);

                if (result != null) {
                    //context.queue(() -> {
                        ((ServerPlayer) player).connection.disconnect(Component.literal("\"yo_hooks_common.json\" doesn't match the server. Change it: " + result));
                    //});
                }

                /*String a = null;

                boolean bool1 = softHook != YoHooks.softHook;
                if (bool1) {
                    a = "softHook (" + YoHooks.softHook + ")";
                }
                boolean bool2 = (stiffness != YoHooks.stiffness) && YoHooks.softHook;
                if (bool2) {
                    if (a != null) {
                        a = a + ", ";
                    } else {
                        a = "";
                    }
                    a = a + "stiffness (" + YoHooks.stiffness + ")";
                }
                boolean bool3 = climbSpeed != YoHooks.climbSpeed;
                if (bool3) {
                    if (a != null) {
                        a = a + ", ";
                    } else {
                        a = "";
                    }
                    a = a + "climbSpeed (" + YoHooks.climbSpeed + ")";
                }
                
                if (bool1 || bool2 || bool3) {
                    ((ServerPlayer) player).connection.disconnect(Component.literal("\"yo_hooks_common.json\" doesn't match the server. Change it: " + a));
                }*/
            }
        );


        NetworkManager.registerReceiver(
            NetworkManager.Side.C2S,
            PlayerClimbPayload.TYPE,
            PlayerClimbPayload.CODEC,
            (payload, context) -> {

                boolean up = payload.up();
                int agility_level = payload.agility_level();

                Player player = context.getPlayer();
                PlayerWithHookData hookDataPlayer = (PlayerWithHookData) player;

                
                if (hookDataPlayer == null) return;

                HookEntity hook = hookDataPlayer.getHook();

                if (hook != null) {
                    context.queue(() -> {

                        if (up) {
                            if (hook.getLength() > 0.4) {
                                hook.setLength((float) (hook.getLength() - ((DynamicConfigHandler.climbSpeed + (agility_level * 0.045))) * PhysicVariables.climbSpeed_Multiplier) );
                                if (!player.isCreative() && !DynamicConfigHandler.funnyMode) player.getFoodData().addExhaustion((DynamicConfigHandler.decreaseSatiety / 75f) + (agility_level * 0.0065f));
                            }
                        } else {
                            if (hook.getLength() < hook.getMaxRange() - 2)
                                hook.setLength(hook.getLength() + (float)(DynamicConfigHandler.climbSpeed * 1.5));
                        }

                    });
                }
            }
        );
    }


}
