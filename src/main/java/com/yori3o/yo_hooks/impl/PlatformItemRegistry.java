package com.yori3o.yo_hooks.impl;


import com.yori3o.yo_hooks.common.YoHooks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;



public class PlatformItemRegistry {


    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(Registries.ITEM, YoHooks.MOD_ID);


    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    public static <T extends Item> Supplier<T> registerItem(
            ResourceLocation id,
            Supplier<T> supplier
    ) {
        DeferredHolder<Item, T> obj = ITEMS.register(id.getPath(), supplier);
        return obj;
    }
     
}

