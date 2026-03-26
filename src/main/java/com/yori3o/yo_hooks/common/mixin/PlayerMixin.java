package com.yori3o.yo_hooks.common.mixin;


import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.event.ClientEvents;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * This mixin is responsible for the physics and logic of the player hanging on the hook.
 */
@Mixin(Player.class)
public class PlayerMixin implements PlayerWithHookData {
    

    private HookEntity hookEntity;
    private boolean isJumpAllowed = false;
    private boolean isClimbingUp;
    private boolean usingCancelAfterJump;
    private int agility_level;


    @Override
    public void setUsingCancelAfterJump(boolean bl) {
        usingCancelAfterJump = bl;
    }
    @Override
    public boolean isUsingCancelAfterJump() {
        return usingCancelAfterJump;
    }


    @Override
    public boolean isJumpAllowed() {
        return this.isJumpAllowed;
    }


    @Override
    public void setClimbing(boolean up, int level) {
        agility_level = level;
        isClimbingUp = up;
    }


    @Override
    public void setHook(HookEntity value) {
        this.hookEntity = value;
    }
    @Override
    public HookEntity getHook() {
        return this.hookEntity;
    }


    @Inject(method = "travel", at = @At("HEAD"))
    private void onTravel(Vec3 travelVector, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        // --- HOOK HANDLING ---
        if (this.hookEntity != null && this.hookEntity.isInBlock()) {

            // --- variables ---
            Vec3 hookPos = this.hookEntity.position();
            Vec3 playerEyePos = player.getEyePosition();
            Vec3 vecToHook = hookPos.subtract(playerEyePos);
            Vec3 unitVector = vecToHook.normalize();

            double MAX_R = this.hookEntity.getLength();
            double dist = vecToHook.length();
        
            Vec3 V = player.getDeltaMovement();
            double vRadial = V.dot(unitVector);
            Vec3 vTangential = V.subtract(unitVector.scale(vRadial));

            double vTangentialMultiplier = 1.0204;


            if (dist > MAX_R) {
                double stretch = dist - MAX_R;

                if (stretch >= 0) {
                    vTangentialMultiplier = 1.047;
                }

                if (isClimbingUp) {
                    if (MAX_R > 0.4) {
                        vTangentialMultiplier = 1.013;
                        vRadial = (PhysicVariables.climbSpeed + (agility_level * 0.041)) * PhysicVariables.climbSpeedMultiplier;
                    } else {
                        ClientEvents.soundCooldown++;
                    }
                }

                if (!PhysicVariables.softHook) {
                    if (stretch >= 0) {
                        double new_vRadial = stretch * 0.055;
                        if (!(vRadial > new_vRadial)) vRadial = new_vRadial;
                    }
                } else { // soft mode
                    vRadial = Math.max(vRadial, vRadial + stretch * PhysicVariables.stiffness);
                    vRadial = Math.min(vRadial, 1);
                }
            }


            if (!player.onGround() && !player.isFallFlying()) {
                vTangential = vTangential.scale(vTangentialMultiplier);
                vRadial = vRadial * 0.99;
            }

            Vec3 V_new = vTangential.add(unitVector.scale(vRadial)); 

            player.setDeltaMovement(V_new);




            if (player.level().isClientSide()) {
                // --- client logic for allowing jump ---
                if (unitVector.y > -0.15 || PhysicVariables.jumpAlwaysAllowed) {
                    isJumpAllowed = true;
                } else {
                    isJumpAllowed = false;
                }
            } else {
                // --- server logic for fall damage reset ---
                if (PhysicVariables.jumpAlwaysAllowed) {
                    player.resetFallDistance();
                } else {
                    if (unitVector.y > -0.15) { 
                        if ((dist + 0.5) > MAX_R)
                            player.resetFallDistance();
                    }
                }
                if (!player.onGround()) player.hurtMarked = false;
            }
        }
    }
}