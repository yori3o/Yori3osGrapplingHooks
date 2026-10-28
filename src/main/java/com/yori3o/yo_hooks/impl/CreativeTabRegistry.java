//? if fabric {
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

            for (Supplier<? extends Item> supplier : ItemRegistry.ALL_ITEMS) {
                itemGroup.accept(supplier.get());
            }
            
        });
    }
}
//? } else {
/*package com.yori3o.yo_hooks.impl;


import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.init.ItemRegistry;




public class CreativeTabRegistry {


    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, YoHooks.MOD_ID);

    public static void initRegister(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }

    public static final Supplier<CreativeModeTab> TAB = TABS.register(
        "grappling_hooks",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.yo_hooks"))
            .icon(() -> new ItemStack(ItemRegistry.getItemForIcon()))
            .displayItems((featureFlags, output) -> {
                for (Supplier<? extends Item> supplier : ItemRegistry.ALL_ITEMS) {
                    output.accept(supplier.get());
                }
            })
            .build()
    );

}
*///? }
