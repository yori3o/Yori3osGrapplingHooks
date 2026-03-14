package com.yori3o.yo_hooks.common.item;


import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.hookregistry.HookDefinition;
import com.yori3o.yo_hooks.common.sound.SoundRegistry;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;



public class HookItem extends Item {

    
    public final HookDefinition hookDefinition;

    
    public HookItem(Properties properties, HookDefinition hookDefinition) {
        super(properties);
        this.hookDefinition = hookDefinition;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PlayerWithHookData hookData = (PlayerWithHookData) player;
        HookEntity hook = hookData.getHook();
        

        if (hookData.isUsingCancelAfterJump()) {
            hookData.setUsingCancelAfterJump(false);
            return InteractionResult.PASS;
        }


        if (hook != null) {
            
            discard(world, player, hook);
        } else {
            
            if (!world.isClientSide() && !DynamicConfigHandler.common().funnyMode && !hookDefinition.doesNotConsumeHunger) {
                /*stack.hurtAndBreak(1, player, p -> { // FOR 1.20.1
                    p.broadcastBreakEvent(hand == InteractionHand.MAIN_HAND 
                            ? EquipmentSlot.MAINHAND 
                            : EquipmentSlot.OFFHAND);
                });*/
                player.causeFoodExhaustion(DynamicConfigHandler.server().decreaseSatiety / 1.5f);
            }
            fire(world, player, stack);
        }

        return InteractionResult.SUCCESS;
    }

    private void fire(Level world, Player player, ItemStack stack) {
        
        if (!world.isClientSide()) {

            int range = hookDefinition.length;
            int agilityLevel = 0;
            boolean gentleTouch = false;

            if (DynamicConfigHandler.common().funnyMode) range = 79;
            for (Entry<Holder<Enchantment>> a : stack.getEnchantments().entrySet()) {
                if (a.getKey().getRegisteredName().equals("yo_hooks:long_reach")) {
                    range += (int)(a.getIntValue() * 3.5);
                } else if (a.getKey().getRegisteredName().equals("yo_hooks:agility")) {
                    agilityLevel = a.getIntValue();
                } else if (a.getKey().getRegisteredName().equals("yo_hooks:gentle_touch")) {
                    gentleTouch = true;
                }
            }

            if (hookDefinition.doesNotBreakFragileBlocks) {
                gentleTouch = true;
            }
            agilityLevel += hookDefinition.defaultAgilityLevel;

            world.addFreshEntity(new HookEntity(world, player, range, stack, agilityLevel, gentleTouch, hookDefinition.damageOnHit));
        

            player.awardStat(Stats.ITEM_USED.get(this));
            world.playSound(null,
                    player.getX(), player.getY() + 1, player.getZ(),
                    SoundRegistry.getCastSound(hookDefinition.id),
                    SoundSource.PLAYERS,
                    0.7f, 1.0f
            );
            player.gameEvent(GameEvent.ITEM_INTERACT_START);}
    }

    private void discard(Level world, Player player, HookEntity hook) {
        ((PlayerWithHookData) player).setHook(null);
        if (!world.isClientSide()) {
            
            hook.discard();
        
            world.playSound(null,
                    player.getX(), player.getY() + 1, player.getZ(),
                    SoundRegistry.getBackSound(hookDefinition.id),
                    SoundSource.PLAYERS,
                    1.0f, 1.0f
            );
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);}
    }

    
    //public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) { // FOR 1.20.1
    /*public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
        int range = hookDefinition.length;
        if (PhysicVariables.funnyMode) range = 80;
        for (Entry<Holder<Enchantment>> a : stack.getEnchantments().entrySet()) {
            if (a.getKey().getRegisteredName().equals("yo_hooks:long_reach")) {
                range += (int)(a.getIntValue() * 3.5);
            }
        }
        tooltip.add(Component.translatable("gui.yo_hooks.hooks.desc_1", range).withColor(0xFF5555FF));
    }*/
    // FOR 1.21.5+
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, 
                TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        int range = hookDefinition.length;
        if (PhysicVariables.funnyMode) range = 80;
        for (Entry<Holder<Enchantment>> a : stack.getEnchantments().entrySet()) {
            if (a.getKey().getRegisteredName().equals("yo_hooks:long_reach")) {
                range += (a.getIntValue() * 3.5);
            }
        }
        consumer.accept(Component.translatable("gui.yo_hooks.hooks.desc_1", range).withColor(0xFF5555FF));
        if (this.hookDefinition.doesNotConsumeHunger && !PhysicVariables.funnyMode) {
            consumer.accept(Component.translatable("gui.yo_hooks.hooks.desc_2").withColor(0xFF5555FF));
        }
        if (this.hookDefinition.doesNotBreakFragileBlocks) {
            consumer.accept(Component.translatable("gui.yo_hooks.hooks.desc_3").withColor(0xFF5555FF));
        }
        if (this.hookDefinition.defaultAgilityLevel > 0) {
            consumer.accept(Component.translatable("gui.yo_hooks.hooks.desc_4", this.hookDefinition.defaultAgilityLevel).withColor(0xFF5555FF));
        }
    }

   /*  @Override
    public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
        if (repairTag != null) {
            return itemStack2.is(repairTag);
        } else {
            return false;
        }
    }

    @Override
    public int getEnchantmentValue() {
      return hookDefinition.enchantability;
    }*/

}
