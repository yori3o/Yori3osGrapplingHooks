package com.yori3o.yo_hooks.common.init;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.impl.PlatformEntityRegistry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;



public class EntityRegistry {


    public static final Supplier<EntityType<HookEntity>> HOOK_ENTITY =
        PlatformEntityRegistry.registerEntity(
            Identifier.fromNamespaceAndPath(YoHooks.MOD_ID, "hook_entity"),
            () -> EntityType.Builder.<HookEntity>of( 
                (type, level) -> new HookEntity(type, level),
                MobCategory.MISC
            )
            .sized(0.25f, 0.25f)
            .clientTrackingRange(4)
            .updateInterval(4)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(YoHooks.MOD_ID, "hook_entity")))
                    
        );

    public static void register() {}

}
