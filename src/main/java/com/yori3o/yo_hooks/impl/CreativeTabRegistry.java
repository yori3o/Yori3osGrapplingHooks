package com.yori3o.yo_hooks.impl;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.init.ItemRegistry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;



public class CreativeTabRegistry {

    public static final ResourceKey<CreativeModeTab> CUSTOM_ITEM_GROUP_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(YoHooks.MOD_ID, "grappling_hooks"));
    public static final CreativeModeTab CUSTOM_ITEM_GROUP = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ItemRegistry.getItemForIcon()))
            .title(Component.translatable("itemGroup.yo_hooks"))
            .build();

    public static void register() {
        
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);
        
        CreativeModeTabEvents.modifyOutputEvent(CUSTOM_ITEM_GROUP_KEY).register(itemGroup -> {

            /*for (Item item : ItemRegistry.ALL_ITEMS) {
                itemGroup.accept(item);
            }*/
            for (Supplier<? extends Item> supplier : ItemRegistry.ALL_ITEMS) {
                itemGroup.accept(supplier.get());
            }
            
        });
    }
}
