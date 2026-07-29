package com.yori3o.yo_hooks.impl;


import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;



public class PlatformEntityRendererRegistry {

    public static <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> renderer) {
        EntityRendererRegistry.register(
            entityType, 
            renderer
        );
    }
}
