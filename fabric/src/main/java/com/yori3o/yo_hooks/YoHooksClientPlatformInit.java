package com.yori3o.yo_hooks;

import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.item.HookItem;
import com.yori3o.yo_hooks.init.EntitiyRegistry;
import com.yori3o.yo_hooks.init.ItemRegistry;
import com.yori3o.yo_hooks.render.entity.HookRenderer;
import com.yori3o.yo_hooks.utils.PlayerWithHookData;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;


public class YoHooksClientPlatformInit {

    public static void OnlyClientInit() {
        EntityRendererRegistry.register(
            EntitiyRegistry.HOOK_ENTITY.get(), 
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
    }

    public static void makeHooksItem() {
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
