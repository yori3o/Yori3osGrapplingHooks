package com.yori3o.yo_hooks.item;

import java.util.List;

import com.yori3o.yo_hooks.config.ServerConfig;
import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.utils.HooksAttributes;
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

public class HookItem extends Item {

    private final int hookRange;
    private final Item itemForRepair;

    private static ServerConfig sc = new ServerConfig();

    private static final float decreaseSatiety = sc.get().decreaseSatiety;

    
    public HookItem(Properties properties, int hookRange, Item itemForRepair) {
        super(properties);
        this.hookRange = hookRange;
        this.itemForRepair = itemForRepair;
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
                player.getFoodData().addExhaustion(decreaseSatiety / 1.5f);
            }
            fire(world, player);
        }

        return InteractionResultHolder.success(stack);
    }

    private void fire(Level world, Player player) {
        if (!world.isClientSide) {
            world.addFreshEntity(new HookEntity(world, player, hookRange));
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        world.playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.FISHING_BOBBER_THROW,
                SoundSource.NEUTRAL,
                0.5f,
                0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
        );
        player.gameEvent(GameEvent.ITEM_INTERACT_START);
    }

    private static void discard(Level world, Player player, HookEntity hook) {
        if (!world.isClientSide) {
            hook.discard();
            ((PlayerWithHookData) player).setHook(null);
        }

        world.playSound(null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.FISHING_BOBBER_RETRIEVE,
                SoundSource.NEUTRAL,
                1.0f,
                0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f)
        );
        player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
    }

    public int getHookRange() {
        return hookRange;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
        tooltip.add(Component.literal(Component.translatable("gui.yo_hooks.hooks.desc_1").getString().split("%")[0] + HooksAttributes.getHookRangeInBlocks(hookRange) + Component.translatable("gui.yo_hooks.hooks.desc_1").getString().split("%")[1]));
    }

    /*@Override
    public int getEnchantmentValue() {
        return 0;
    }*/

    @Override
    public boolean isValidRepairItem(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack2.is(itemForRepair);
    }
}
