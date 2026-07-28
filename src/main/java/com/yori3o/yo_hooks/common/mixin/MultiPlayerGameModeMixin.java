package com.yori3o.yo_hooks.common.mixin;


import com.yori3o.yo_hooks.common.YoHooksClient;
import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.item.HookItem;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/**
 * This mixin prevents the hook from being released when the prevent use keybind is clamped
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {

    
    @Inject(method = "useItem", at = @At("HEAD"), cancellable = true)
    private void cancelHookUse(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {

        ItemStack stack = player.getItemInHand(hand);

        if (stack.getItem() instanceof HookItem) {
            if (YoHooksClient.PREVENT_USE.isDown()) {
                cir.setReturnValue(InteractionResult.PASS);
            } else if (hand == InteractionHand.OFF_HAND) {
                if (!ConfigManager.client().usingWhileHoldingFood) {
                    ItemStack stackMainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
                    if (stackMainHand.has(DataComponents.FOOD)) {
                        //if (!player.getFoodData().needsFood()) {
                            cir.setReturnValue(InteractionResult.PASS);
                        //}
                    }
                }
            }
        }
    }

}