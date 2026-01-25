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


public class HookItem extends Item {

    private final int hookRangeInBlocks;
    public final String correspondingHead;
    public final int damageOnHit;

    
    public HookItem(Properties properties, int hookRangeInBlocks, String correspondingHead, int damageOnHit) {
        super(properties);
        this.hookRangeInBlocks = hookRangeInBlocks;
        this.correspondingHead = correspondingHead;
        this.damageOnHit = damageOnHit;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        PlayerWithHookData hookData = (PlayerWithHookData) player;
        HookEntity hook = hookData.getHook();

        //LoggerUtil.LOGGER.info("hook use started.");

        
        /*if (YoHooksClient.isFabricAndJUMPisUseKey) {
            if (YoHooksClient.a) {
                YoHooksClient.a = false;
            } else {
                YoHooksClient.a = true;
                return InteractionResultHolder.pass(stack);
            }
        }*/
       //LoggerUtil.LOGGER.info(hookData.isUsingCancelAfterJump());

        if (hookData.isUsingCancelAfterJump()) {
            //LoggerUtil.LOGGER.info("hook use canceled.");
            hookData.setUsingCancelAfterJump(false);
            return InteractionResult.PASS;
        }

        //hookData.setUsingCancelAfterJump(true);

        if (hook != null) {
            //LoggerUtil.LOGGER.info("hook use ended with discard.");
            discard(world, player, hook);
        } else {
            //LoggerUtil.LOGGER.info("hook use ended with fire (new hook).");
            /*if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResultHolder.pass(stack);
            }*/
            if (!world.isClientSide() && !PhysicVariables.funnyMode) {
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

        return InteractionResult.SUCCESS;
    }

    private void fire(Level world, Player player, ItemStack stack) {
        if (!world.isClientSide()) {
            //player.getCooldowns().addCooldown(this, 20); // 1 сек

            int range = hookRangeInBlocks;
            int agility_level = 0;
            boolean gentle_touch = false;

            if (PhysicVariables.funnyMode) range = 79;
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
            /*world.playSound(null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW,
                    SoundSource.NEUTRAL,
                    0.5f,
                    0.4f / (world.getRandom().nextFloat() * 0.4f + 0.9f)
            );*/
            world.playSound(null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundRegistry.CAST,
                    SoundSource.PLAYERS,
                    0.75f, 1.0f
                    //0.4f / (world.getRandom().nextFloat() * 0.4f + 0.9f)
            );
            player.gameEvent(GameEvent.ITEM_INTERACT_START);}
    }

    private static void discard(Level world, Player player, HookEntity hook) {
        ((PlayerWithHookData) player).setHook(null);
        if (!world.isClientSide()) {
            
            hook.discard();
        
            world.playSound(null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundRegistry.BACK,
                    SoundSource.PLAYERS,
                    1.0f, 1.0f
                    //0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
            );
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);}
    }

    //private static void forceDiscard(Level world, Player player) {
        //if (!world.isClientSide) {
            
    
            //player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);}
    //}

    
    // FOR 1.21.5+
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        int range = hookRangeInBlocks;
        if (PhysicVariables.funnyMode) range = 80;
        for (Entry<Holder<Enchantment>> a : stack.getEnchantments().entrySet()) {
            if (a.getKey().getRegisteredName().equals("yo_hooks:long_reach")) {
                range += (a.getIntValue() * 3.5);
            }
        }
        consumer.accept(Component.translatable("gui.yo_hooks.hooks.desc_1", "§9" + String.valueOf(range)));
    }

}
