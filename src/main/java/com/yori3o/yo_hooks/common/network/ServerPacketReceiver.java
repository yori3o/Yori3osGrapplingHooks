package com.yori3o.yo_hooks.common.network;


import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.hookregistry.HookDefinition;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.sound.SoundRegistry;
import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;
import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;



public class ServerPacketReceiver {


    public static void registerPackets() {
        PlatformNetworkHelper.registerC2S(
            PlayerJumpPayload.TYPE,
            PlayerJumpPayload.CODEC,
            (payload, context) -> {

                boolean usingCancel = payload.usingCancel();

                Player player = context.getPlayer();
                PlayerWithHookData hookDataPlayer = (PlayerWithHookData) player;
                
                
                if (hookDataPlayer == null) return;

                HookEntity hookEntity = hookDataPlayer.getHook();

                if (hookEntity != null && hookEntity.isInBlock()) {

                    // this variable should change as quickly as possible
                    hookDataPlayer.setUsingCancelAfterJump(usingCancel);

                    HookDefinition hookDefinition = ItemRegistry.ALL_HOOKS.get(hookEntity.getHookItemMaterial()).get().hookDefinition;
                
                    // but this logic should been executed in main thread
                    context.enqueue(() -> {

                        if (!DynamicConfigHandler.common().funnyMode && !hookDefinition.doesNotConsumeHunger) {
                            player.causeFoodExhaustion(DynamicConfigHandler.server().decreaseSatiety / 3f);
                        }
                
                        // Отцепляем крюк
                        hookEntity.discard(); 

                        hookDataPlayer.setHook(null);

                        player.level().playSound(null,
                                player.getX(), player.getY() + 1, player.getZ(),
                                SoundRegistry.getBackSound(hookDefinition.id),
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




        PlatformNetworkHelper.registerC2S(
            PlayerClimbPayload.TYPE,
            PlayerClimbPayload.CODEC,
            (payload, context) -> {

                boolean up = payload.up();
                int agility_level = payload.agilityLevel();
                boolean shouldPlaySound = payload.playSound();

                Player player = context.getPlayer();
                PlayerWithHookData hookDataPlayer = (PlayerWithHookData) player;

                
                if (hookDataPlayer == null) return;

                HookEntity hook = hookDataPlayer.getHook();

                if (hook != null) {
                    context.enqueue(() -> {

                        if (up) {
                            if (hook.getLength() > 0.4) {
                                hook.setLength((float) (hook.getLength() - ((DynamicConfigHandler.common().climbSpeed + (agility_level * 0.041))) * PhysicVariables.climbSpeedMultiplier) );
                                if (!player.isCreative() && !DynamicConfigHandler.common().funnyMode) player.getFoodData().addExhaustion((DynamicConfigHandler.server().decreaseSatiety / 75f) + (agility_level * 0.0065f));
                            }
                        } else {
                            if (hook.getLength() < hook.getMaxRange() - 2) {
                                hook.setLength(hook.getLength() + (float)(DynamicConfigHandler.common().climbSpeed * 1.5));
                            } else {
                                return;
                            }
                        }

                        if (shouldPlaySound) {
                            player.level().playSound(null,
                                player,
                                SoundRegistry.getClimbSound(hook.getHookItemMaterial()),
                                SoundSource.PLAYERS,
                                0.21f, 1.0f
                            );
                        }

                    });
                }
            }
        );
    }

}
