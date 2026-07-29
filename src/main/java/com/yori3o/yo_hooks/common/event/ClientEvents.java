package com.yori3o.yo_hooks.common.event;


import com.yori3o.yo_hooks.common.YoHooksClient;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.network.ClientSender;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;



public class ClientEvents {


    public static int soundCooldown = 0;
    private static final int SOUND_LENGTH_TICKS = 39; // ~2 sec
    private static boolean jumpKeybindWasDown = false;

    public static boolean climbingUpWithMouseWheel = false;
    public static boolean climbingDownWithMouseWheel = false;
    public static int mouseWheelClimbingResetTimer = 0;

    
    public static void clientTickStart() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        PlayerWithHookData hookData = (PlayerWithHookData)player;

        HookEntity hook = hookData.getHook();
        if (hook != null) {
            hookDiscardIfInvalid(player, hookData, hook);
            clientTickKeybindsHandler(player, hookData, hook);
            tickMouseWheelClimbing();
        }
    }
    
    private static void clientTickKeybindsHandler(Player player, PlayerWithHookData hookData, HookEntity hook) {
        boolean down = YoHooksClient.JUMP.isDown();

        if (soundCooldown > 0) {
            soundCooldown--;
        }

        if (hook.isInBlock()) {
            if (down && !jumpKeybindWasDown && !YoHooksClient.PREVENT_USE.isDown()) {

                boolean usingCancel = YoHooksClient.JUMP.same(Minecraft.getInstance().options.keyUse);
                hookData.setUsingCancelAfterJump(usingCancel);
                
                ClientSender.jumpFromHook(usingCancel);
                hookData.setHook(null);
                mouseWheelClimbingResetTimer = 0;

                if (hookData.isJumpAllowed()) {
                    applyJumpImpulse(player, hook.getAgilityLevel());
                }
            }
            
            if (YoHooksClient.CLIMB.isDown() || climbingUpWithMouseWheel) {
                ClientSender.climb(true, shouldPlayClimbSound());
                hookData.setClimbing(true, hook.getAgilityLevel());
            } else {
                if (YoHooksClient.CLIMB_DOWN.isDown() || climbingDownWithMouseWheel) {
                    ClientSender.climb(false, shouldPlayClimbSound());
                    hookData.setClimbing(false, 0);
                } else {
                    hookData.setClimbing(false, 0);
                }
            }
        }
        jumpKeybindWasDown = down;

        //climbingUpWithMouseWheel = false;
        //climbingDownWithMouseWheel = false;
    }

    public static final boolean shouldPlayClimbSound() {
        if (soundCooldown <= 0) {
            soundCooldown = SOUND_LENGTH_TICKS;
            return true;
        }
        return false;
    }

    private static final void hookDiscardIfInvalid(Player player, PlayerWithHookData hookData, HookEntity hook) {
        if (player.getMainHandItem().getItem() instanceof HookItem || player.getOffhandItem().getItem() instanceof HookItem) {
            if (hook.distanceTo(player) <= hook.getMaxRange()) {    
                if (player.isAlive()) {
                    if (!(hook.getBlockY() != -99999 && (hook.level().getBlockState(hook.getBlockPos()).isAir() && hook.isNoGravity()))) {
                        if (!hook.isRemoved()) {
                            return;
                        }
                    }
                }
            }
        }
        hookData.setHook(null);
        mouseWheelClimbingResetTimer = 0;
    }

    private static final void tickMouseWheelClimbing() {
        if (mouseWheelClimbingResetTimer <= 0) {
            climbingUpWithMouseWheel = false;
            climbingDownWithMouseWheel = false;
        } else {
            mouseWheelClimbingResetTimer--;
        }
    }

    private static final void applyJumpImpulse(Player player, int agility_level) {
        
        final double FORWARD_VELOCITY = (0.47 + (agility_level * 0.09)) * PhysicVariables.jumpMultiplier;
        final double UPWARD_VELOCITY = (0.41 + (agility_level * 0.04)) * PhysicVariables.jumpMultiplier;
        
        Vec3 lookVector = player.getLookAngle();
        Vec3 oldV = player.getDeltaMovement();

        double targetX = lookVector.x * FORWARD_VELOCITY;
        double targetZ = lookVector.z * FORWARD_VELOCITY;
        
        
        // We take the maximum of: current speed OR desired UPWARD_VELOCITY.
        // Otherwise, the jump will slow down the player if his previous speed was greater
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