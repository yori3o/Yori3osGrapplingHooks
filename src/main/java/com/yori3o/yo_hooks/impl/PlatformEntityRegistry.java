package com.yori3o.yo_hooks.impl;


import com.yori3o.yo_hooks.common.YoHooks;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;



public class PlatformEntityRegistry {


    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, YoHooks.MOD_ID);
            

    public static <T extends Entity> Supplier<EntityType<T>> registerEntity(ResourceLocation id, Supplier<EntityType<T>> supplier) {
        DeferredHolder<EntityType<?>, EntityType<T>> obj = ENTITIES.register(id.getPath(), supplier);
        return obj;
    } 
     
}

