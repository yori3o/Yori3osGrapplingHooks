package com.yori3o.yo_hooks.impl;


import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;



public class PlatformItemRegistry {

    public static <T extends Item> Supplier<T> registerItem(Identifier id, Supplier<T> supplier) {
        T item = supplier.get();
        Registry.register(BuiltInRegistries.ITEM, id, item);
        return () -> item;
    } 
     
}

