package com.yori3o.yo_hooks.common.mixin;


import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.yori3o.yo_hooks.common.entity.HookEntity;


/**
 * This mixin fixes a bug where the hook would twitch when hitting an entity.
 */
@Mixin(ThrowableProjectile.class)
public class ThrowableProjectileMixin {
    

    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/projectile/ThrowableProjectile;setPos(Lnet/minecraft/world/phys/Vec3;)V"
        )
    )
    private void modifySetPos(ThrowableProjectile self, Vec3 pos) {
        if (self instanceof HookEntity) {
            self.setPos(self.position().add(self.getDeltaMovement()));
        } else {
            self.setPos(pos);
        }
    }
}