package com.yori3o.yo_hooks.common.item;

import com.yori3o.yo_hooks.common.config.DynamicConfigHandler;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.sounds.SoundRegistry;
import com.yori3o.yo_hooks.common.utils.PhysicVariables;
import com.yori3o.yo_hooks.common.utils.PlayerWithHookData;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.List;


public class HookItem extends Item {

    private final int hookRangeInBlocks;
    private final Item itemForRepair;
    public final String correspondingHead;
    private final int enchValue;
    public final int damageOnHit;

    
    public HookItem(Properties properties, int hookRangeInBlocks, Item itemForRepair, String correspondingHead, int enchValue, int damageOnHit) {
        super(properties);
        this.hookRangeInBlocks = hookRangeInBlocks;
        this.itemForRepair = itemForRepair;
        this.correspondingHead = correspondingHead;
        this.enchValue = enchValue;
        this.damageOnHit = damageOnHit;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PlayerWithHookData hookData = (PlayerWithHookData) player;
        HookEntity hook = hookData.getHook();
        

        if (hookData.isUsingCancelAfterJump()) {
            hookData.setUsingCancelAfterJump(false);
            return InteractionResultHolder.pass(stack);
        }


        if (hook != null) {
            
            discard(world, player, hook);
        } else {
            
            if (!world.isClientSide && !DynamicConfigHandler.funnyMode) {
                stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                /*stack.hurtAndBreak(1, player, p -> { // FOR 1.20.1
                    p.broadcastBreakEvent(hand == InteractionHand.MAIN_HAND 
                            ? EquipmentSlot.MAINHAND 
                            : EquipmentSlot.OFFHAND);
                });*/
                if (!player.isCreative()) player.getFoodData().addExhaustion(DynamicConfigHandler.decreaseSatiety / 1.5f);
            }
            fire(world, player, stack);
        }

        return InteractionResultHolder.success(stack);
    }

    private void fire(Level world, Player player, ItemStack stack) {
        if (!world.isClientSide) {
            //player.getCooldowns().addCooldown(this, 20); // 1 sec

            int range = hookRangeInBlocks;
            int agility_level = 0;
            boolean gentle_touch = false;

            if (DynamicConfigHandler.funnyMode) range = 79;
            for (Entry<Holder<Enchantment>> a : stack.getEnchantments().entrySet()) {
                if (a.getKey().getRegisteredName().equals("yo_hooks:long_reach")) {
                    range += (int)(a.getIntValue() * 3.5);
                } else if (a.getKey().getRegisteredName().equals("yo_hooks:agility")) {
                    agility_level = a.getIntValue();
                } else if (a.getKey().getRegisteredName().equals("yo_hooks:gentle_touch")) {
                    gentle_touch = true;
                }
            }
            world.addFreshEntity(new HookEntity(world, player, range, stack, agility_level, gentle_touch, damageOnHit));
        

            player.awardStat(Stats.ITEM_USED.get(this));
            world.playSound(null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundRegistry.CAST,
                    SoundSource.PLAYERS,
                    0.75f, 1.0f
            );
            player.gameEvent(GameEvent.ITEM_INTERACT_START);}
    }

    private static void discard(Level world, Player player, HookEntity hook) {
        ((PlayerWithHookData) player).setHook(null);
        if (!world.isClientSide) {
            
            hook.discard();
        
            world.playSound(null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundRegistry.BACK,
                    SoundSource.PLAYERS,
                    1.0f, 1.0f
            );
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);}
    }

    // FOR 1.20.1
    //public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
        int range = hookRangeInBlocks;
        if (PhysicVariables.funnyMode) range = 80;
        for (Entry<Holder<Enchantment>> a : stack.getEnchantments().entrySet()) {
            if (a.getKey().getRegisteredName().equals("yo_hooks:long_reach")) {
                range += (int)(a.getIntValue() * 3.5);
            }
        }
        tooltip.add(Component.literal(String.format(Component.translatable("gui.yo_hooks.hooks.desc_1").getString(), range)));
    }
    // FOR 1.21.5+
    /*public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        int range = hookRangeInBlocks;
        for (Entry<Holder<Enchantment>> a : stack.getEnchantments().entrySet()) {
            if (a.getKey().getRegisteredName().equals("yo_hooks:long_reach")) {
                range += (a.getIntValue() * 3.5);
            }
        }
        if (DynamicConfigHandler.funnyMode) range = 100;
        consumer.accept(Component.literal(String.format(Component.translatable("gui.yo_hooks.hooks.desc_1").getString(), range)));
    }*/

    @Override
    public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack2.is(itemForRepair);
    }

    @Override
    public int getEnchantmentValue() {
      return enchValue;
    }
}
