package com.yori3o.yo_hooks.common.util;


import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.init.ComponentRegistry;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;



public record HookActiveProperty() implements ConditionalItemModelProperty {


    public static final MapCodec<HookActiveProperty> MAP_CODEC = MapCodec.unit(new HookActiveProperty());


    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext itemDisplayContext) {
        if (entity == null || !(entity instanceof Player)) {
            return false;
        }
        Player player = (Player) entity;
        HookEntity hook = ((PlayerWithHookData) player).getHook();

        if (hook == null || hook.isRemoved()) return false;

        return stack.getOrDefault(ComponentRegistry.HOOK_ACTIVE, false);

        //if (Minecraft.getInstance().player == entity) { // logic for local player who playing
            /*LoggerUtil.info("local");
            if (HookItemStackInUsing.hookItemStack == stack) {
                LoggerUtil.info("hook in use, true");
                return true;
            } else {
                ItemStack mainHandItem = player.getMainHandItem();
                ItemStack offHandItem = player.getOffhandItem();
                boolean flag = mainHandItem == stack;
                boolean flag1 = offHandItem == stack;
                if (mainHandItem.getItem() instanceof HookItem) {
                    flag1 = false;
                }
                if (flag || flag1) {
                    HookItemStackInUsing.hookItemStack = stack;
                    LoggerUtil.info("NEW hook for in use, true");
                    return true;
                }
            }
            LoggerUtil.info("FALSE");
            if (player.getMainHandItem().getItem() instanceof HookItem) {
                LoggerUtil.info("FALSE WITH HOOK IN MAIN HAND");
            }
            return false;*/
            
            
            //boolean isMain = ItemStack.isSameItem(mainHandItem, stack);
            //boolean isOff = ItemStack.isSameItem(offHandItem, stack);

            // если у нас уже есть активный крюк
            /*if (HookItemStackInUsing.hookItemStack != null) {

                // идеальный случай — ссылка совпала
                if (HookItemStackInUsing.hookItemStack == stack) {
                    return true;
                }

                ItemStack mainHandItem = player.getMainHandItem();
                ItemStack offHandItem = player.getOffhandItem();

                boolean isMain = mainHandItem == stack;
                boolean isOff = offHandItem == stack;

                if (mainHandItem.getItem() instanceof HookItem) {
                    isOff = false;
                }

                // ВАЖНО: ресинк только если это реально предмет в руке
                if (isMain || isOff) {
                    HookItemStackInUsing.hookItemStack = stack;
                    return true;
                }
            } 
            return false;
        }
        
        LoggerUtil.info("NOT local");

        ItemStack mainHandItem = player.getMainHandItem(); // logic for other players
        ItemStack offHandItem = player.getOffhandItem();

        boolean flag = mainHandItem == stack;
        boolean flag1 = offHandItem == stack;

        if (mainHandItem.getItem() instanceof HookItem) {
            flag1 = false;
        }

        if (flag || flag1) {
            return true;
        } else {
            return false;
        }*/
    }

    public MapCodec<HookActiveProperty> type() {
        return MAP_CODEC;
    }
}
