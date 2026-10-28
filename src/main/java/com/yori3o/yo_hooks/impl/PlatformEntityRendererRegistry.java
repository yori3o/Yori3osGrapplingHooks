//? if fabric {
package com.yori3o.yo_hooks.impl;


import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;



public class PlatformEntityRendererRegistry {

    public static <T extends Entity> void registerEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> renderer) {
        //? if >=26.1 {
        EntityRenderers.register(
        //? } else {
        /*net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry.register(
        *///? }
            entityType, 
            renderer
        );
    }
}
//? }
