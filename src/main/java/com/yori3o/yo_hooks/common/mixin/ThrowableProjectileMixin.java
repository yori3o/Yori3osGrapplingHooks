package com.yori3o.yo_hooks.common.mixin;


import com.yori3o.yo_hooks.common.entity.HookEntity;

import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;


/**
 * This mixin fixes a bug where the hook would twitch when hitting an entity.
 */
@Mixin(ThrowableProjectile.class)
public class ThrowableProjectileMixin {
    

    @WrapOperation(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/ThrowableProjectile;setPos(Lnet/minecraft/world/phys/Vec3;)V"
        )
    )
    private void yo_hooks$modifySetPos(ThrowableProjectile self, Vec3 vec3, Operation<Void> original) {
        if (self instanceof HookEntity) {
            original.call(self, self.position().add(self.getDeltaMovement()));
            return;
        }

        original.call(self, vec3);
    }
}