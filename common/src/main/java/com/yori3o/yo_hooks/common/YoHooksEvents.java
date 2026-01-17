package com.yori3o.yo_hooks.common;

import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.network.ClientSender;
import com.yori3o.yo_hooks.common.sounds.SoundRegistry;
import com.yori3o.yo_hooks.common.utils.PhysicVariables;
import com.yori3o.yo_hooks.common.utils.PlayerWithHookData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;


public class YoHooksEvents {

    public static int soundCooldown = 0;
    private static final int SOUND_LENGTH_TICKS = 39; // ~2 sec
    private static boolean wasDown = false;



    
    
    public static void ClientTick_Pre() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        PlayerWithHookData hookData = (PlayerWithHookData)player;

        HookEntity hook = hookData.getHook();
        if (hook != null) {
            hookDiscardIfInvalid(player, hookData, hook);
            ClientTickKeybindsHandler(player, hookData, hook);
        }
    }
    



    public static void ClientPlayerJoin() {
        DynamicConfigHandler.CommonConfigUpdate();
        
        if (!Minecraft.getInstance().isLocalServer()) ClientSender.CheckConfig(DynamicConfigHandler.softHook, DynamicConfigHandler.stiffness, DynamicConfigHandler.climbSpeed, DynamicConfigHandler.funnyMode);
    }












    private static void ClientTickKeybindsHandler(Player player, PlayerWithHookData hookData, HookEntity hook) {
        
        boolean down = YoHooksClient.JUMP.isDown();

        if (hook.isInBlock()) {

            // we jump only if the button is pressed, but was not pressed before, i.e. once at the moment of pressing.
            if (down && !wasDown) {
                if (hookData.isJumpAllowed()) {

                    boolean usingCancel = (YoHooksClient.JUMP.same(Minecraft.getInstance().options.keyUse));
                    hookData.setUsingCancelAfterJump(usingCancel);
                    
                    ClientSender.JumpFromHook(usingCancel);
                    hookData.setHook(null);
                    applyJumpImpulse(player, hook.getAgilityLevel());
                    
                }
            }


            if (soundCooldown > 0) {
                soundCooldown--;
            }

            if (YoHooksClient.CLIMB.isDown()) {
                int agility_level = hook.getAgilityLevel();
                PlayClimbSound();
                ClientSender.Climb(true, agility_level);
                hookData.setClimbing(true, agility_level);
            } else {
                if (YoHooksClient.CLIMB_DOWN.isDown()) {
                    int agility_level = hook.getAgilityLevel();
                    PlayClimbSound();
                    ClientSender.Climb(false, agility_level);
                    hookData.setClimbing(false, 0);
                } else {
                    hookData.setClimbing(false, 0);
                }
            }
        }
        wasDown = down;
    }




    private static final void PlayClimbSound() {
        if (soundCooldown <= 0) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundRegistry.CLIMB, 1.0F, 1.0F));
            soundCooldown = SOUND_LENGTH_TICKS;
        }
    }

    private static final void hookDiscardIfInvalid(Player player, PlayerWithHookData hookData, HookEntity hook) {
        if ( !((player.getMainHandItem().getItem() instanceof HookItem) || (player.getOffhandItem().getItem() instanceof HookItem)) ) {
            hookData.setHook(null);
            return;
        }
        if (hook.distanceTo(player) > hook.getMaxRange()) {    
            hookData.setHook(null);
            return;
        }
        if (!player.isAlive()) {
            hookData.setHook(null);
            return;
        }
        if (hook.getBlockY() != -99999) {
            if (hook.level().getBlockState(hook.getBlockPos()).isAir() && hook.isNoGravity()) {
                hookData.setHook(null);
                return; 
            }
        }
        if (hook.isRemoved()) {
            hookData.setHook(null);
            return;
        }
    }


    private static final void applyJumpImpulse(Player player, int agility_level) {
        
        final double FORWARD_VELOCITY = (0.47 + (agility_level * 0.097)) * PhysicVariables.jump_Multiplier;
        final double UPWARD_VELOCITY = (0.41 + (agility_level * 0.043)) * PhysicVariables.jump_Multiplier;
        
        Vec3 lookVector = player.getLookAngle();
        Vec3 oldV = player.getDeltaMovement();

        double targetX = lookVector.x * FORWARD_VELOCITY;
        double targetZ = lookVector.z * FORWARD_VELOCITY;
        
        
        // Мы берем максимальное из: текущая скорость ИЛИ желаемый UPWARD_VELOCITY.
        // В противном случае прыжок замедлит игрока, если его бывшая скорость была больше
        double newY = Math.max(oldV.y, UPWARD_VELOCITY);

        double newX = target((oldV.x + targetX) / 2, targetX);
        double newZ = target((oldV.z + targetZ) / 2, targetZ);
        
        player.setDeltaMovement(newX, newY, newZ);
        
    }

    

    private static final double target(double currentMotion, double target) {
        if (currentMotion >= 0) {
            if (target >= 0) {
                return Math.max(currentMotion, target);
            } else {
                return target * 0.3;
            }
        } else {
            if (target >= 0) {
                return target * 0.3;
            } else {
                return Math.min(currentMotion, target);
            }
        }
    }
    
}
