package com.yori3o.yo_hooks.impl;


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
