package com.yori3o.yo_hooks.common.utils;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.item.HookItem;



public record  HookActiveProperty() implements ConditionalItemModelProperty {
   public static final MapCodec< HookActiveProperty> MAP_CODEC = MapCodec.unit(new  HookActiveProperty());

   // Логика проверки состояния
    @Override
    //public boolean get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity, int seed, ItemDisplayContext itemDisplayContext) {
    public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext itemDisplayContext) {
        // 1. Проверка сущности и мира
        /*if (livingEntity == null || !(livingEntity instanceof Player player)) {
            return false;
        }

        // 2. Проверка, что игрок держит именно этот предмет
        boolean inMainHand = player.getMainHandItem() == itemStack;
        boolean inOffHand = player.getOffhandItem() == itemStack;
        
        // ВАЖНО: Проверка, что игрок держит HookItem
        // Ваша логика с `HookItem` в 1.21.1 выглядела странно, мы ее упростим. 
        // Если itemStack - это алмазный крюк, и он в руке, проверяем его.
        if (!inMainHand && !inOffHand) {
            return false;
        }

        // 3. Проверка активного крюка (HookEntity)
        // Используем ваш специфичный метод для получения HookEntity
        // Убедитесь, что PlayerWithHookData доступен!
        if (player instanceof PlayerWithHookData playerWithHook) {
            HookEntity hook = playerWithHook.getHook();
            // Крюк активен, если он существует и не помечен как удаленный
            boolean hookIsActive = hook != null && !hook.isRemoved();

            // 4. Возвращаем true, если предмет находится в руке И крюк активен
            return hookIsActive;
        }*/
        if (entity == null || !(entity instanceof Player) /*|| level == null || !level.isClientSide()*/) {
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

        //return false;
    }

   public MapCodec< HookActiveProperty> type() {
      return MAP_CODEC;
   }
}
