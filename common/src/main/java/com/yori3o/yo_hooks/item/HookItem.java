package com.yori3o.yo_hooks.item;

import com.yori3o.yo_hooks.YoHooks;
import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.utils.PlayerWithHookData;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
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

    
    public HookItem(Properties properties, int hookRangeInBlocks, Item itemForRepair, String correspondingHead) {
        super(properties);
        this.hookRangeInBlocks = hookRangeInBlocks;
        this.itemForRepair = itemForRepair;
        this.correspondingHead = correspondingHead;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        HookEntity hook = ((PlayerWithHookData) player).getHook();

        if (hook != null) {
            discard(world, player, hook);
        } else {
            if (!world.isClientSide) {
                stack.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                /*stack.hurtAndBreak(1, player, p -> { // FOR 1.20.1
                    p.broadcastBreakEvent(hand == InteractionHand.MAIN_HAND 
                            ? EquipmentSlot.MAINHAND 
                            : EquipmentSlot.OFFHAND);
                });*/
                player.getFoodData().addExhaustion(YoHooks.decreaseSatiety / 1.5f);
            }
            fire(world, player, stack);
        }

        return InteractionResultHolder.success(stack);
    }

    private void fire(Level world, Player player, ItemStack stack) {
        if (!world.isClientSide) {
            world.addFreshEntity(new HookEntity(world, player, /*(int)(Math.pow(*/hookRangeInBlocks,/*  2)),*/ stack));
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        world.playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.FISHING_BOBBER_THROW,
                SoundSource.NEUTRAL,
                0.5f,
                0.4f / (world.getRandom().nextFloat() * 0.4f + 0.9f)
        );
        player.gameEvent(GameEvent.ITEM_INTERACT_START);
    }

    private static void discard(Level world, Player player, HookEntity hook) {
        if (!world.isClientSide) {
            hook.discard();
        }
        ((PlayerWithHookData) player).setHook(null);

        world.playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.FISHING_BOBBER_RETRIEVE,
                SoundSource.NEUTRAL,
                1.0f,
                0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
        );
        player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
    }

    /*public int getHookRange() {
        return hookRangeInBlocks;
    }*/

    //@Override // FOR 1.20.1
    //public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
        tooltip.add(Component.literal(String.format(Component.translatable("gui.yo_hooks.hooks.desc_1").getString(), hookRangeInBlocks)));
    }

    @Override
    public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack2.is(itemForRepair);
    }
}
