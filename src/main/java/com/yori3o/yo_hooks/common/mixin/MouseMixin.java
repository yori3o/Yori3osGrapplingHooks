package com.yori3o.yo_hooks.common.mixin;


import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;



@Mixin(MouseHandler.class)
public class MouseMixin
{
	@Inject(
        method = "onScroll",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"
        ),
        cancellable = true
    )
    private void cancelHotbarScroll(long handle, double xoffset, double yoffset, CallbackInfo ci) {
        if (DynamicConfigHandler.client().holdHookTightly) {
            if (((PlayerWithHookData)(Minecraft.getInstance().player)).getHook() != null) {
                ci.cancel();
            }
        }
    }
}