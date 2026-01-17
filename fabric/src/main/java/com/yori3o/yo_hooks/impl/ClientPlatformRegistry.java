package com.yori3o.yo_hooks.impl;

import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.init.EntityRegistry;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.client.render.HookRenderer;
import com.yori3o.yo_hooks.common.utils.PlayerWithHookData;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.resources.ResourceLocation;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;


public class ClientPlatformRegistry {

    public static void OnlyClientInit() {
        EntityRendererRegistry.register(
            EntityRegistry.HOOK_ENTITY.get(), 
            HookRenderer::new
        );

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((content) -> {
            content.addAfter(new ItemStack(Items.IRON_HOE), new ItemStack(ItemRegistry.IRON_GRAPPLING_HOOK.get()));//.prepend(ItemRegistry.IRON_GRAPPLING_HOOK.get());
            content.addAfter(new ItemStack(Items.GOLDEN_HOE), new ItemStack(ItemRegistry.GOLD_GRAPPLING_HOOK.get()));//.prepend(ItemRegistry.GOLD_GRAPPLING_HOOK.get());
            content.addAfter(new ItemStack(Items.DIAMOND_HOE), new ItemStack(ItemRegistry.DIAMOND_GRAPPLING_HOOK.get()));//.prepend(ItemRegistry.DIAMOND_GRAPPLING_HOOK.get());
            content.addAfter(new ItemStack(Items.NETHERITE_HOE), new ItemStack(ItemRegistry.NETHERITE_GRAPPLING_HOOK.get()));//.prepend(ItemRegistry.NETHERITE_GRAPPLING_HOOK.get());
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register((content) -> {
            content.addAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.NETHERITE_HOOK_HEAD.get()));//.prepend(ItemRegistry.IRON_HOOK_HEAD.get());
            content.addAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.DIAMOND_HOOK_HEAD.get()));//.prepend(ItemRegistry.GOLD_HOOK_HEAD.get());
            content.addAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.GOLD_HOOK_HEAD.get()));//.prepend(ItemRegistry.DIAMOND_HOOK_HEAD.get());
            content.addAfter(new ItemStack(Items.DISC_FRAGMENT_5), new ItemStack(ItemRegistry.IRON_HOOK_HEAD.get()));//.prepend(ItemRegistry.NETHERITE_HOOK_HEAD.get());
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            ClientPlatformRegistry.registerItemProperty();
        });
    }

    private static void registerItemProperty() {
        ItemProperties.register(
                ItemRegistry.IRON_GRAPPLING_HOOK.get(),
                ResourceLocation.fromNamespaceAndPath("yo_hooks", "grappling_hook_handle"),
                (stack, level, entity, seed) -> {
                    if (entity == null || !(entity instanceof Player) /*|| level == null || !level.isClientSide()*/) {
                        return 0.0F;
                    }
                    Player player = (Player) entity;
                    HookEntity hook = ((PlayerWithHookData) player).getHook();
                    ItemStack mainHandItem = player.getMainHandItem();

                    boolean flag = mainHandItem == stack;
                    boolean flag1 = player.getOffhandItem() == stack;
                    if (mainHandItem.getItem() instanceof HookItem) {
                        flag1 = false;
                    }

                    boolean hookIsActive = hook != null && !hook.isRemoved();
                    if ((flag || flag1) && hookIsActive) {
                        return 1.0F;
                    } else {
                        return 0.0F;
                    }
                }
            );

            ItemProperties.register(
                ItemRegistry.GOLD_GRAPPLING_HOOK.get(),
                ResourceLocation.fromNamespaceAndPath("yo_hooks", "grappling_hook_handle"),
                (stack, level, entity, seed) -> {
                    if (entity == null || !(entity instanceof Player) /*|| level == null || !level.isClientSide()*/) {
                        return 0.0F;
                    }
                    Player player = (Player) entity;
                    HookEntity hook = ((PlayerWithHookData) player).getHook();
                    ItemStack mainHandItem = player.getMainHandItem();

                    boolean flag = mainHandItem == stack;
                    boolean flag1 = player.getOffhandItem() == stack;
                    if (mainHandItem.getItem() instanceof HookItem) {
                        flag1 = false;
                    }

                    boolean hookIsActive = hook != null && !hook.isRemoved();
                    if ((flag || flag1) && hookIsActive) {
                        return 1.0F;
                    } else {
                        return 0.0F;
                    }
                }
            );

            ItemProperties.register(
                ItemRegistry.DIAMOND_GRAPPLING_HOOK.get(),
                ResourceLocation.fromNamespaceAndPath("yo_hooks", "grappling_hook_handle"),
                (stack, level, entity, seed) -> {
                    if (entity == null || !(entity instanceof Player) /*|| level == null || !level.isClientSide()*/) {
                        return 0.0F;
                    }
                    Player player = (Player) entity;
                    HookEntity hook = ((PlayerWithHookData) player).getHook();
                    ItemStack mainHandItem = player.getMainHandItem();

                    boolean flag = mainHandItem == stack;
                    boolean flag1 = player.getOffhandItem() == stack;
                    if (mainHandItem.getItem() instanceof HookItem) {
                        flag1 = false;
                    }

                    boolean hookIsActive = hook != null && !hook.isRemoved();
                    if ((flag || flag1) && hookIsActive) {
                        return 1.0F;
                    } else {
                        return 0.0F;
                    }
                }
            );

            ItemProperties.register(
                ItemRegistry.NETHERITE_GRAPPLING_HOOK.get(),
                ResourceLocation.fromNamespaceAndPath("yo_hooks", "grappling_hook_handle"),
                (stack, level, entity, seed) -> {
                    if (entity == null || !(entity instanceof Player) /*|| level == null || !level.isClientSide()*/) {
                        return 0.0F;
                    }
                    Player player = (Player) entity;
                    HookEntity hook = ((PlayerWithHookData) player).getHook();
                    ItemStack mainHandItem = player.getMainHandItem();

                    boolean flag = mainHandItem == stack;
                    boolean flag1 = player.getOffhandItem() == stack;
                    if (mainHandItem.getItem() instanceof HookItem) {
                        flag1 = false;
                    }

                    boolean hookIsActive = hook != null && !hook.isRemoved();
                    if ((flag || flag1) && hookIsActive) {
                        return 1.0F;
                    } else {
                        return 0.0F;
                    }
                }
            );
    }
}
