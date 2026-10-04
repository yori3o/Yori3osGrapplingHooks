package com.yori3o.yo_hooks.common.mixin;


import com.yori3o.yo_hooks.common.init.ComponentRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.ItemStack;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;


/**
 * This mixin removes the twitching of an object when jumping from a hook.
 * When you jump, the HOOK_ACTIVE component becomes false, causing the ItemStack to be recreated. The mixin tricks the game renderer into thinking it hasn't changed.
 */
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {


    @Shadow
    private Minecraft minecraft;

    
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;mainHandItem:Lnet/minecraft/world/item/ItemStack;",
            opcode = Opcodes.GETFIELD
        )
    )
    private ItemStack preventHookJitter(
            ItemInHandRenderer instance,
            Operation<ItemStack> original) {

        ItemStack oldStack = original.call(instance);
        ItemStack newStack = minecraft.player.getMainHandItem();

        if (oldStack.getItem() instanceof HookItem
                && newStack.getItem() instanceof HookItem
                && oldStack.getOrDefault(ComponentRegistry.HOOK_ACTIVE, false)
                && !newStack.getOrDefault(ComponentRegistry.HOOK_ACTIVE, false)) {

            return newStack;
        }

        return oldStack;
    }
    
}