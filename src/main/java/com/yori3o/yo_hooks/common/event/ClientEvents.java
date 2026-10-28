package com.yori3o.yo_hooks.common.event;


import com.yori3o.yo_hooks.common.YoHooksClient;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.network.ClientSender;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.common.util.interfaces.PlayerWithHookData;

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
//? if <26.1 {
/*if (com.yori3o.yo_hooks.common.compat.Compats.isSableLoaded) {
            com.yori3o.yo_hooks.common.compat.sable.SableCompat.tickClient();
        }
*///? }

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        PlayerWithHookData hookData = (PlayerWithHookData)player;

        HookEntity hook = hookData.yo_hooks$getHook();
        if (hook != null) {
            if (hook.isRemoved()) { hookData.yo_hooks$setHook(null); return; }
            clientTickKeybindsHandler(player, hookData, hook);
        } else {
            jumpKeybindWasDown = YoHooksClient.JUMP.isDown();
            hookData.yo_hooks$setClimbing(false, 0);
        }
        tickMouseWheelClimbing();
    }
    
    private static void clientTickKeybindsHandler(Player player, PlayerWithHookData hookData, HookEntity hook) {
        boolean down = YoHooksClient.JUMP.isDown();

        if (soundCooldown > 0) {
            soundCooldown--;
        }

        if (hook.isInBlock()) {
            if (down && !jumpKeybindWasDown && !YoHooksClient.PREVENT_USE.isDown()) {

                boolean usingCancel = YoHooksClient.JUMP.same(Minecraft.getInstance().options.keyUse);
                hookData.yo_hooks$setUsingCancelAfterJump(usingCancel);
                
                boolean jumpAllowed = hookData.yo_hooks$isJumpAllowed();
                ClientSender.jumpFromHook(usingCancel);
                hookData.yo_hooks$setHook(null);
                mouseWheelClimbingResetTimer = 0;

                if (jumpAllowed) {
                    applyJumpImpulse(player, hook.getAgilityLevel());
                }
                jumpKeybindWasDown = down;
                return;
            }
            
            if (YoHooksClient.CLIMB.isDown() || climbingUpWithMouseWheel) {
                ClientSender.climb(true, shouldPlayClimbSound());
                hookData.yo_hooks$setClimbing(true, hook.getAgilityLevel());
            } else {
                if (YoHooksClient.CLIMB_DOWN.isDown() || climbingDownWithMouseWheel) {
                    ClientSender.climb(false, shouldPlayClimbSound());
                    hookData.yo_hooks$setClimbing(false, 0);
                } else {
                    hookData.yo_hooks$setClimbing(false, 0);
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

    protected static void clearServerOverlays() {
        ItemRegistry.ALL_HOOKS.forEach((a, hookSupplier) -> {
            hookSupplier.get().setDamageServerOverlap(null);
            hookSupplier.get().setLengthServerOverlap(null);
        });
    }

    public static void resetInputState() {
        jumpKeybindWasDown = false;
        soundCooldown = 0;
        mouseWheelClimbingResetTimer = 0;
        climbingUpWithMouseWheel = false;
        climbingDownWithMouseWheel = false;
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