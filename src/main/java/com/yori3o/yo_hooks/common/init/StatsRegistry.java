package com.yori3o.yo_hooks.common.init;

import com.yori3o.yo_hooks.impl.PlatformRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public final class StatsRegistry {
    public static final Identifier DISTANCE_TRAVELED_ON_HOOK_ONE_CM = PlatformRegistries.stat("distance_traveled_on_hook_one_cm");
    public static void register() {}
    public static void initializeFormatters() {
        //? if <26.1 {
        /*com.yori3o.yo_hooks.common.compat.LegacyTrades.register();
        *///? }
        Stats.CUSTOM.get(DISTANCE_TRAVELED_ON_HOOK_ONE_CM, StatFormatter.DISTANCE);
    }
}
