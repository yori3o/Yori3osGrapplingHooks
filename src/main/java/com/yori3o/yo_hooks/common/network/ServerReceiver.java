package com.yori3o.yo_hooks.common.network;


import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.hookregistry.HookDefinition;
import com.yori3o.yo_hooks.common.init.ComponentRegistry;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.sound.SoundRegistry;

import java.util.function.Supplier;

import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;
import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;



public class ServerReceiver {


    public static void register() {
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

                    Supplier<HookItem> supplier = ItemRegistry.ALL_HOOKS.get(hookEntity.getHookItemMaterial());
                    if (supplier == null) return;
                    HookDefinition hookDefinition = supplier.get().hookDefinition;
                
                    // but this logic should been executed in main thread
                    context.enqueue(() -> {

                        if (!ConfigManager.common().funnyMode && !hookDefinition.doesNotConsumeHunger) {
                            player.causeFoodExhaustion(ConfigManager.server().decreaseSatiety / 3f);
                        }
                
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

                        ItemStack stack = player.getMainHandItem();
                        if (!(stack.getItem() instanceof HookItem)) {
                            stack = player.getOffhandItem();
                        }
                        stack.set(ComponentRegistry.HOOK_ACTIVE, false);

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
                boolean shouldPlaySound = payload.playSound();

                Player player = context.getPlayer();
                PlayerWithHookData hookDataPlayer = (PlayerWithHookData) player;

                HookEntity hook = hookDataPlayer.getHook();

                if (hook != null) {
                    context.enqueue(() -> {
                        int agility_level = hook.getAgilityLevel();

                        if (up) {
                            if (hook.getLength() > 0.4) {
                                hook.setLength(
                                    (float)(hook.getLength() - ((ConfigManager.common().climbSpeed + (agility_level * 0.041))) * PhysicVariables.climbSpeedMultiplier)
                                );
                                if (!ConfigManager.common().funnyMode) player.causeFoodExhaustion((ConfigManager.server().decreaseSatiety / 75f) + (agility_level * 0.0065f));
                            }
                        } else {
                            if (hook.getLength() < hook.getMaxRange() - 2) {
                                hook.setLength(hook.getLength() + (float)(ConfigManager.common().climbSpeed * 1.5));
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
