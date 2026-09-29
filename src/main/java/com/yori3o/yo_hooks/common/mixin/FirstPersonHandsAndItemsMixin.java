package com.yori3o.yo_hooks.common.mixin;


import com.yori3o.yo_hooks.common.init.ComponentRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;


/**
 * This mixin removes the twitching of an object when jumping from a hook.
 * When you jump, the HOOK_ACTIVE component becomes false, causing the ItemStack to be recreated. The mixin tricks the game renderer into thinking it hasn't changed.
 */
@Mixin(FirstPersonHandsAndItems.class)
public class FirstPersonHandsAndItemsMixin {

    
    @WrapOperation(
        method = "tick",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/player/FirstPersonHandsAndItems;mainHandItem:Lnet/minecraft/world/item/ItemStack;",
            opcode = Opcodes.GETFIELD
        )
    )
    private ItemStack yo_hooks$preventHookJitter(
            FirstPersonHandsAndItems instance,
            Operation<ItemStack> original) {

        ItemStack oldStack = original.call(instance);
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return oldStack;
        ItemStack newStack = player.getMainHandItem();

        if (oldStack.getItem() instanceof HookItem
                && newStack.getItem() instanceof HookItem
                && oldStack.getOrDefault(ComponentRegistry.HOOK_ACTIVE, false)
                && !newStack.getOrDefault(ComponentRegistry.HOOK_ACTIVE, false)) {

            return newStack;
        }

        return oldStack;
    }
    
}