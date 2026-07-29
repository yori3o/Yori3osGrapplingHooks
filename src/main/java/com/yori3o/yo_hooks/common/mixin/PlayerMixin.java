package com.yori3o.yo_hooks.common.mixin;


import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.event.ClientEvents;
import com.yori3o.yo_hooks.common.init.StatsRegistry;
import com.yori3o.yo_hooks.common.util.LoggerUtil;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;
import com.yori3o.yo_hooks.common.compat.Compats;
import com.yori3o.yo_hooks.common.compat.sable.SableCompat;

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
    
    private boolean allowStatsIncrease;
    private Vec3 oldPosition;
    
    private boolean suddenFall = false;


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

    @Override
    public void setSuddenFall(boolean bool) {
        suddenFall = bool;
    }
    @Override
    public boolean isSuddenFall() {
        return suddenFall;
    }

    @Inject(method = "travel", at = @At("HEAD"))
    private void onTravel(Vec3 travelVector, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        allowStatsIncrease = false;

        // --- HOOK HANDLING ---
        if (this.hookEntity != null && this.hookEntity.isInBlock()) {
            
            oldPosition = player.position();

            // --- variables ---
            Vec3 hookPos = this.hookEntity.position();
            if (Compats.isSableLoaded) {
                hookPos = SableCompat.getHookPos(hookPos, this.hookEntity);
                LoggerUtil.info("hookPos " + hookPos);
                if (hookPos == null) return;
            }
            Vec3 playerEyePos = player.getEyePosition();
            Vec3 vecToHook = hookPos.subtract(playerEyePos);
            Vec3 unitVector = vecToHook.normalize();

            double MAX_R = this.hookEntity.getLength();
            double dist = vecToHook.length();
        
            Vec3 V = player.getDeltaMovement();
            double vRadial = V.dot(unitVector);
            Vec3 vTangential = V.subtract(unitVector.scale(vRadial));

            double vTangentialMultiplier = 1.01;


            if (dist > MAX_R) {
                double stretch = dist - MAX_R;

                vTangentialMultiplier = 1.047;

                if (isClimbingUp) {
                    if (!PhysicVariables.softHook) {
                        if (MAX_R > 0.4) {
                            vTangentialMultiplier = 1.017;
                            vRadial = (PhysicVariables.climbSpeed + (agility_level * 0.041)) * PhysicVariables.climbSpeedMultiplier;
                        } else {
                            if (player.level().isClientSide()) ClientEvents.soundCooldown++;
                        }
                    }
                }

                if (!PhysicVariables.softHook) {
                    double new_vRadial = stretch * 0.055;
                    if (!(vRadial > new_vRadial)) vRadial = new_vRadial;
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
                }
                if (!player.onGround()) {
                    player.hurtMarked = false;
                    if ((dist + 0.6) > MAX_R) {
                        allowStatsIncrease = true;
                        if (unitVector.y > -0.15) { 
                            player.resetFallDistance();
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "travel", at = @At("TAIL"))
    private void afterTravel(Vec3 travelVector, CallbackInfo ci) {
        Player player = (Player) (Object) this;

        if (allowStatsIncrease) {
            Vec3 current = player.position();
            double traveledDistance = current.distanceTo(oldPosition);
            player.awardStat(StatsRegistry.DISTANCE_TRAVELED_ON_HOOK_ONE_CM, (int)(traveledDistance * 200));
        }
    }
}