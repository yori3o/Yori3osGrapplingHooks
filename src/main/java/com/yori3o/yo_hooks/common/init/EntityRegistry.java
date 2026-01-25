package com.yori3o.yo_hooks.common.init;

import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class EntityRegistry {

    public static final EntityType<HookEntity> HOOK_ENTITY = Registry.register(
        BuiltInRegistries.ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(YoHooks.MOD_ID, "hook_entity"),
        EntityType.Builder.<HookEntity>of(HookEntity::new, MobCategory.MISC)
            .sized(0.25f, 0.25f)
            .clientTrackingRange(4)
            .updateInterval(5)
            .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(YoHooks.MOD_ID, "hook_entity")))
    );

    public static void register() {}
}
