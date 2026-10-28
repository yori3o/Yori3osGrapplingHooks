package com.yori3o.yo_hooks.common.mixin;


import com.yori3o.yo_hooks.common.util.interfaces.PlayerWithHookData;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * A small fix for the achievement to work more correctly.
 */
@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "resetFallDistance", at = @At("TAIL"))
    private void yo_hooks$whenResetFallDistance(CallbackInfo ci) {
        if ((Entity)(Object)this instanceof Player player) {
            ((PlayerWithHookData) player).yo_hooks$setSuddenFall(false);
        }
    }
}