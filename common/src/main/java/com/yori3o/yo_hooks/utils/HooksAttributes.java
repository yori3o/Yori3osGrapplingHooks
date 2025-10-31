package com.yori3o.yo_hooks.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import com.yori3o.yo_hooks.config.CommonConfig;
import com.yori3o.yo_hooks.register.ModItems;

public class HooksAttributes {

    static CommonConfig cc = new CommonConfig();

    // the number squared
    public static final int ironHookLength = (int) Math.pow(cc.get().ironHookLength, 2); // 18 blocks default
    public static final int goldHookLength = (int) Math.pow(cc.get().goldHookLength, 2); // 22 blocks default
    public static final int diamondHookLength = (int) Math.pow(cc.get().diamondHookLength, 2); // 26 blocks default
    public static final int netheriteHookLength = (int) Math.pow(cc.get().netheriteHookLength, 2); // 32 blocks default

    public static final int ironHookDurabitility = cc.get().ironHookDurabitility; // 32 default
    public static final int goldHookDurabitility = cc.get().goldHookDurabitility; // 16 default
    public static final int diamondHookDurabitility = cc.get().diamondHookDurabitility; // 64 default
    public static final int netheriteHookDurabitility = cc.get().netheriteHookDurabitility; // 96 blocks default

    public static ItemStack hookStack(int hookEntityRange) {
        //LoggerUtil.LOGGER.info(hookEntityRange);
        if (hookEntityRange == ironHookLength)
            return(new ItemStack(ModItems.IRON_HOOK_HEAD));
        else if (hookEntityRange == goldHookLength)
            return(new ItemStack(ModItems.GOLD_HOOK_HEAD));
        else if (hookEntityRange == diamondHookLength)
            return(new ItemStack(ModItems.DIAMOND_HOOK_HEAD));
        else if (hookEntityRange == netheriteHookLength)
            return(new ItemStack(ModItems.NETHERITE_HOOK_HEAD));
        else
            return(new ItemStack(Items.DIRT));
    }

    public static int getHookRangeInBlocks(int hookRange) {
        if (hookRange == HooksAttributes.ironHookLength)
            return cc.get().ironHookLength;
        if (hookRange == HooksAttributes.goldHookLength)
            return cc.get().goldHookLength;
        if (hookRange == HooksAttributes.diamondHookLength)
            return cc.get().diamondHookLength;
        if (hookRange == HooksAttributes.netheriteHookLength)
            return cc.get().netheriteHookLength;
        return 0; // kinda default
    }

}