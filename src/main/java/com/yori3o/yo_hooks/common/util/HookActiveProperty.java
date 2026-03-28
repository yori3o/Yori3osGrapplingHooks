package com.yori3o.yo_hooks.common.util;


import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.item.HookItem;

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
        ItemStack mainHandItem = player.getMainHandItem();

        boolean flag = mainHandItem == stack;
        boolean flag1 = player.getOffhandItem() == stack;
        if (mainHandItem.getItem() instanceof HookItem) {
            flag1 = false;
        }

        boolean hookIsActive = hook != null && !hook.isRemoved();
        if ((flag || flag1) && hookIsActive) {
            return true;
        } else {
            return false;
        }
    }

   public MapCodec<HookActiveProperty> type() {
      return MAP_CODEC;
   }
}
