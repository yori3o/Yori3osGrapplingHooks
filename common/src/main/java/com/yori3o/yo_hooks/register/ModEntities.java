package com.yori3o.yo_hooks.register;

import com.yori3o.yo_hooks.YoHooks;
import com.yori3o.yo_hooks.entity.HookEntity;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntities {

    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(YoHooks.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<HookEntity>> HOOK_ENTITY =
        ENTITIES.register("hook_entity",
                () -> EntityType.Builder.<HookEntity>of( 
                            (type, level) -> new HookEntity(type, level),
                            MobCategory.MISC
                        )
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .updateInterval(5)
                    .build(ResourceLocation.fromNamespaceAndPath(YoHooks.MOD_ID, "hook_entity").toString())
        );


    public static void register() {
        ENTITIES.register();
    }
}
