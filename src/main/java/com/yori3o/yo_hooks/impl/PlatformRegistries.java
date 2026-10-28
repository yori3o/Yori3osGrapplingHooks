package com.yori3o.yo_hooks.impl;

import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
//? if neoforge {
/*import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.bus.api.IEventBus;
*///? }

public final class PlatformRegistries {
    //? if neoforge {
    /*private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, "yo_hooks");
    private static final DeferredRegister<Identifier> CUSTOM_STATS = DeferredRegister.create(Registries.CUSTOM_STAT, "yo_hooks");
    public static void attach(IEventBus bus) {
        COMPONENTS.register(bus);
        CUSTOM_STATS.register(bus);
    }
    *///? }

    public static <T> Supplier<DataComponentType<T>> component(String name, DataComponentType<T> component) {
        //? if fabric {
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath("yo_hooks", name), component);
        return () -> component;
        //? } else {
        /*return COMPONENTS.register(name, () -> component);
        *///? }
    }

    public static Identifier stat(String name) {
        Identifier id = Identifier.fromNamespaceAndPath("yo_hooks", name);
        //? if fabric {
        Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
        //? } else {
        /*CUSTOM_STATS.register(name, () -> id);
        *///? }
        return id;
    }
}
