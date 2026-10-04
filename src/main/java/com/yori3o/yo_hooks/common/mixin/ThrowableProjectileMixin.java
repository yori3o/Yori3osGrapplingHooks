package com.yori3o.yo_hooks.common.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.yori3o.yo_hooks.common.entity.HookEntity;

import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


/**
 * This mixin fixes a bug where the hook would twitch when hitting an entity.
 */
@Mixin(ThrowableProjectile.class)
public class ThrowableProjectileMixin {

    @WrapOperation (
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/ThrowableProjectile;setPos(DDD)V"
        )
    )
    private void yo_hooks$modifySetPos(ThrowableProjectile self, double x, double y, double z,
                                       Operation<Void> original) {
        if (self instanceof HookEntity) {
            Vec3 next = self.position().add(self.getDeltaMovement());
            original.call(self, next.x, next.y, next.z);
        } else {
            original.call(self, x, y, z);
        }
    }
}