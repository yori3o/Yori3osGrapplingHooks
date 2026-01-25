package com.yori3o.yo_hooks.impl;


import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.client.render.HookRenderer;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTabs;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.client.renderer.entity.EntityRenderers;


public class ClientPlatformRegistry {

    public static void OnlyClientInit() {
        /*EntityRendererRegistry.register(
            EntityRegistry.HOOK_ENTITY, 
            HookRenderer::new
        );*/

        EntityRenderers.register(
            EntityRegistry.HOOK_ENTITY, 
            HookRenderer::new
        );

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((content) -> {
            content.addAfter(new ItemStack(Items.COPPER_HOE), new ItemStack(ItemRegistry.COPPER_GRAPPLING_HOOK ));
            content.addAfter(new ItemStack(Items.IRON_HOE), new ItemStack(ItemRegistry.IRON_GRAPPLING_HOOK ));//.prepend(ItemRegistry.IRON_GRAPPLING_HOOK );
            content.addAfter(new ItemStack(Items.GOLDEN_HOE), new ItemStack(ItemRegistry.GOLD_GRAPPLING_HOOK ));//.prepend(ItemRegistry.GOLD_GRAPPLING_HOOK );
            content.addAfter(new ItemStack(Items.DIAMOND_HOE), new ItemStack(ItemRegistry.DIAMOND_GRAPPLING_HOOK ));//.prepend(ItemRegistry.DIAMOND_GRAPPLING_HOOK );
            content.addAfter(new ItemStack(Items.NETHERITE_HOE), new ItemStack(ItemRegistry.NETHERITE_GRAPPLING_HOOK ));//.prepend(ItemRegistry.NETHERITE_GRAPPLING_HOOK );
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register((content) -> {
            content.addAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.NETHERITE_HOOK_HEAD ));//.prepend(ItemRegistry.IRON_HOOK_HEAD );
            content.addAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.DIAMOND_HOOK_HEAD ));//.prepend(ItemRegistry.GOLD_HOOK_HEAD );
            content.addAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.GOLD_HOOK_HEAD ));//.prepend(ItemRegistry.DIAMOND_HOOK_HEAD );
            content.addAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.IRON_HOOK_HEAD ));//.prepend(ItemRegistry.NETHERITE_HOOK_HEAD );
            content.addAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.COPPER_HOOK_HEAD ));
        });

    }

}
