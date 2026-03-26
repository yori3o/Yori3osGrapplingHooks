package com.yori3o.yo_hooks.impl;


import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;



public class PlatformEntityRegistry {

    /*public static <T extends Entity> EntityType<T> registerEntity(
            Identifier id,
            EntityType<T> entityType
    ) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                id,
                entityType
        );
    }*/

    public static <T extends Entity> Supplier<EntityType<T>> registerEntity(Identifier id, Supplier<EntityType<T>> supplier) {
        EntityType<T> entityType = supplier.get();
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id, entityType);
        return () -> entityType;
    }
     
}

