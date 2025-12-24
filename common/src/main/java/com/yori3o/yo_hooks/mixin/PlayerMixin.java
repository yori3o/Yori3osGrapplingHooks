package com.yori3o.yo_hooks.mixin;

import com.yori3o.yo_hooks.YoHooks;
import com.yori3o.yo_hooks.YoHooksClient;
import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.utils.PlayerWithHookData;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Player.class)
public class PlayerMixin implements PlayerWithHookData {
    
    private HookEntity hookEntity;
    private boolean isJumpAllowed = false;
    private boolean isClimbing;
    /*private Vec3 lastServerPos;
    private Vec3 deltaMovement2;*/


    @Override
    public HookEntity getHook() {
        return this.hookEntity;
    }

    @Override
    public boolean isJumpAllowed() {
        return this.isJumpAllowed;
    }

    /*@Override
    public boolean isClimbing() {
        return this.isClimbing;
    }*/

    @Override
    public void setClimbing(boolean bl) {
        isClimbing = bl;
    }

    @Override
    public void setHook(HookEntity value) {
        this.hookEntity = value;
    }


    @Inject(method = "travel", at = @At("HEAD"))
    private void onTravel(Vec3 travelVector, CallbackInfo ci) {

        Player player = (Player)(Object)this;

        // --- HOOK HANDLING ---
        if (this.hookEntity != null && this.hookEntity.isInBlock()) {
            
            // --- переменные ---
            Vec3 hookPos = this.hookEntity.position();
            Vec3 playerEyePos = player.getEyePosition();
            Vec3 vecToHook = hookPos.subtract(playerEyePos);
            Vec3 unitVector = vecToHook.normalize();
            
            
            
            // --- физика применяется на клиенте ---
            if (player.level().isClientSide()) {

                // --- ещё переменные (не нужные на сервере) ---
                double MAX_R = this.hookEntity.getLength(); 
                double dist = vecToHook.length();
                Vec3 V = player.getDeltaMovement();

                // Разложение скорости
                double vRadial = V.dot(unitVector);
                Vec3 vTangential = V.subtract(unitVector.scale(vRadial));

                // режим твердой верёвки
                if (!YoHooksClient.softHook) {
                    if (dist > MAX_R/*  && vRadial <= 0*/) {

                        double stretch = dist - MAX_R;

                        if (isClimbing) {
                            vRadial = YoHooks.climbSpeed;
                        }
                        
                        if (stretch > 0.03) {
                            
                            double new_vRadial = stretch * 0.0333;
                            if (!(vRadial > new_vRadial)) {
                                vRadial = new_vRadial;
                            }
                            
                        } else {
                            vRadial = 0;
                        }

                        // увеличиваем тангенциальную скорость, так как ванильное сопротивление слишком сильное
                        if (!player.isFallFlying())
                            vTangential = vTangential.scale(1.05f);
                    } else {
                        if (isClimbing) {
                            if (MAX_R < MAX_R - 2)
                                vRadial = -(YoHooks.climbSpeed * 1.5);
                        }
                    }
                } else { // мягкой, сила упругости
                    if (dist > MAX_R) {
                        double stretch = dist - MAX_R;

                        if (isClimbing) {
                            vRadial = YoHooks.climbSpeed;
                        }

                        vRadial = Math.max(vRadial, vRadial + stretch * YoHooksClient.stiffness);
                        vRadial = Math.min(vRadial, 1);

                        // увеличиваем тангенциальную скорость, так как ванильное сопротивление слишком сильное
                        if (!player.isFallFlying())
                            vTangential = vTangential.scale(1.05f);
                    } else {
                        if (isClimbing) {
                            if (MAX_R < MAX_R - 2)
                                vRadial = -(YoHooks.climbSpeed * 1.5);
                        }
                    }
                }

                Vec3 V_new;
                
                if (player.onGround()) {
                    V_new = vTangential.add(unitVector.scale(vRadial));
                } else { // лёгкое демпфирование
                    V_new = vTangential.add(unitVector.scale(vRadial * 0.99));
                }
                
                player.setDeltaMovement(V_new);
            
                if (unitVector.y > -0.15) { // эта переменная нужны для работы прыжка с крюка
                    isJumpAllowed = true;
                } else {
                    isJumpAllowed = false;
                }

            } else { // --- Обнуление урона от падения и урон - это серверная логика ---
                if (unitVector.y > -0.15) {
                    isJumpAllowed = true;
                    ((LivingEntity) player).resetFallDistance();
                } else {
                    isJumpAllowed = false;
                }
                /*lastServerPos = player.position();
                //deltaMovement2 = player.position();
                LoggerUtil.LOGGER.info("old vec3 = " + lastServerPos);
                //LoggerUtil.LOGGER.info("old vec3 = " + deltaMovement2);*/
            }
        }
    }

    /*@Inject(method = "travel", at = @At("TAIL"))
    private void onTravel2(Vec3 travelVector, CallbackInfo ci) {

        Player player = (Player)(Object)this;

        if (this.hookEntity != null && this.hookEntity.isInBlock()) {
            
        
            if (!player.level().isClientSide()) {

                

                LoggerUtil.LOGGER.info("player.horizontalCollision = " + player.horizontalCollision);

                LoggerUtil.LOGGER.info("player.minorHorizontalCollision = " + player.minorHorizontalCollision);

                if (player.horizontalCollision) { 
                    Vec3 delta = player.position().subtract(lastServerPos);
                    LoggerUtil.LOGGER.info("new vec3 = " + player.position());
                    double speed = delta.horizontalDistance();


                    //double speed = player.getDeltaMovement().horizontalDistance(); // XZ-скорость

                    

                    double n = speed;
                    LoggerUtil.LOGGER.info("n = " + n);
                    float o = (float)(n * 10.0D - 3.0D);
                    if (o > 0.0F) {
                        player.playSound(getFallDamageSound((int)o, player), 1.0F, 1.0F);
                        player.hurt(player.damageSources().flyIntoWall(), o);
                    }
                    //Vec3 delta = player.position().subtract(lastServerPos);
                    //double speed = delta.horizontalDistance();
                    //Vec3 deltaPos = player.position().subtract(player.xo, player.yo, player.zo);
                    //double speed = deltaPos.horizontalDistance();
                    /*LoggerUtil.LOGGER.info("new vec3 = " + player.position());
                    LoggerUtil.LOGGER.info("subtract = " + player.position().subtract(lastServerPos));
                    LoggerUtil.LOGGER.info("speed = " + delta.horizontalDistance());*/

                    /*LoggerUtil.LOGGER.info("using newiest vec3 on server = " + player.getDeltaMovement());

                    double speedBefore = deltaMovement2.horizontalDistance();
                    double speedAfter  = player.getDeltaMovement().horizontalDistance();*/

                    /*double n = speed;
                    //LoggerUtil.LOGGER.info("n = " + n);

                    float damage = (float)(n * 100D - 3.0D);

                    //LoggerUtil.LOGGER.info(speedBefore > 1.5 && speedAfter < speedBefore * 0.4);
                    LoggerUtil.LOGGER.info("damage = " + damage);

                    if (damage > 0.0F) {
                        player.playSound(getFallDamageSound((int)damage, player), 1.0F, 1.0F);
                        player.hurt(player.damageSources().flyIntoWall(), damage);
                    }

                }
            }
        }
    }

    

    private SoundEvent getFallDamageSound(int i, Player player) {
        return i > 4 ? player.getFallSounds().big() : player.getFallSounds().small();
    }*/
/* 
    @Inject(method = "tick", at = @At("HEAD"))
    private void savePos(CallbackInfo ci) {
        Player player = (Player)(Object)this;
        if (this.hookEntity != null &&  this.hookEntity.isInBlock() && !player.level().isClientSide()) {
            lastServerPos = player.position();
            LoggerUtil.LOGGER.info("old vec3 = " + lastServerPos);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void afterTick(CallbackInfo ci) {
        Player player = (Player)(Object)this;
        if (this.hookEntity != null &&  this.hookEntity.isInBlock() && !player.level().isClientSide()) {
            Vec3 delta = player.position().subtract(lastServerPos);
            LoggerUtil.LOGGER.info("new vec3 = " + player.position());
            speed = delta.horizontalDistance();
            LoggerUtil.LOGGER.info("speed = " + speed);
        }
    }*/
}




