package com.yori3o.yo_hooks;

import com.yori3o.yo_hooks.config.CommonConfig;
import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.item.HookItem;
import com.yori3o.yo_hooks.network.ClientSender;
import com.yori3o.yo_hooks.utils.PlayerWithHookData;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import com.mojang.blaze3d.platform.InputConstants;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;


public class YoHooksClient {

    // --- common config values ---
    public static boolean softHook;
    public static float stiffness;
    //public static float climbSpeed;

    public static final KeyMapping JUMP = new KeyMapping(
        "key.yo_hooks.jump",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_SPACE,
        "category.yo_hooks"
    );
    public static final KeyMapping CLIMB = new KeyMapping(
        "key.yo_hooks.climb",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_X,
        "category.yo_hooks"
    );
    public static final KeyMapping CLIMB_DOWN = new KeyMapping(
        "key.yo_hooks.climb_down",
        InputConstants.Type.KEYSYM,
        InputConstants.KEY_Z,
        "category.yo_hooks"
    );

    public static void OnlyClientInit() {
        KeyMappingRegistry.register(JUMP);
        KeyMappingRegistry.register(CLIMB);
        KeyMappingRegistry.register(CLIMB_DOWN);

        ClientTickEvent.CLIENT_POST.register(mc -> {
            Player player = mc.player;
            if (player == null) return;

            PlayerWithHookData hookData = (PlayerWithHookData)player;

            HookEntity hook = hookData.getHook();

            while (JUMP.consumeClick() && hook != null && hook.isInBlock()) {
                if (hookData.isJumpAllowed()) {
                    ClientSender.JumpFromHook();
                    hookData.setHook(null);
                }
            }
            if (hook != null) {
                if (hook.isInBlock()) {
                    
                    if (CLIMB.isDown()) {
                        ClientSender.Climb(true);
                        hookData.setClimbing(true);
                    } else {
                        if (CLIMB_DOWN.isDown()) {
                            ClientSender.Climb(false);
                            hookData.setClimbing(true);
                        } else {
                            hookData.setClimbing(false);
                        }
                    }
                }
                if (!player.isAlive() ||  !((player.getMainHandItem().getItem() instanceof HookItem) || (player.getOffhandItem().getItem() instanceof HookItem))  || hook.distanceTo(player) > hook.getMaxRange()) {    
                    hookData.setHook(null);      
                }
            }
        });

        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register((level) -> {
            Minecraft.getInstance().execute(() -> {
                CommonConfig cc = new CommonConfig();
                cc.load();
                // These variables are used only on the client
                softHook = cc.get().softHook;
                stiffness = cc.get().stiffness;
                //climbSpeed = cc.get().climbSpeed;
                
                ClientSender.CheckConfig(softHook);
            });
        });
    }
    
}
