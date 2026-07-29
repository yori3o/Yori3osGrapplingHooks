package com.yori3o.yo_hooks.common.init;



import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;



public class StatsRegistry {


    public static ResourceLocation DISTANCE_TRAVELED_ON_HOOK_ONE_CM = makeCustomStat("distance_traveled_on_hook_one_cm", StatFormatter.DISTANCE);

    
    private static ResourceLocation makeCustomStat(final String id, final StatFormatter formatter) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath("yo_hooks", id);
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, location);
        Stats.CUSTOM.get(location, formatter);
        return location;
    }

    public static void register() {}

}