package com.yori3o.yo_hooks.common.mixin;


import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.event.ClientEvents;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(MouseHandler.class)
public class MouseMixin
{
	@Inject(
        method = "onScroll",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;swapPaint(I)V"
        ),
        cancellable = true
    )
    private void cancelHotbarScroll(long handle, double xoffset, double yoffset, CallbackInfo ci) {
        if (ConfigManager.client().holdHookTightly) {
            if (((PlayerWithHookData)(Minecraft.getInstance().player)).getHook() != null) {
                ci.cancel();
            }
        }
    }


    @Inject(at = @At("HEAD"), method = "onScroll(JDD)V")
	private void onOnMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
		if (ConfigManager.client().holdHookTightly) {
            if (ConfigManager.client().climbWithMouseWheelScroll) {
                if (vertical == 0) return;
                if (vertical > 0) {
                    ClientEvents.climbingUpWithMouseWheel = true;
                    ClientEvents.climbingDownWithMouseWheel = false;
                    ClientEvents.mouseWheelClimbingResetTimer += vertical * 2;
                } else {
                    ClientEvents.climbingUpWithMouseWheel = false;
                    ClientEvents.climbingDownWithMouseWheel = true;
                    ClientEvents.mouseWheelClimbingResetTimer += -vertical * 2;
                }
            }
        }
	}
}