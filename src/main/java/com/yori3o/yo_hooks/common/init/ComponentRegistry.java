package com.yori3o.yo_hooks.common.init;


import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.yori3o.yo_hooks.neoforge.YoHooksNeoForge;

import net.minecraft.core.Registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;



public class ComponentRegistry {


    public static final Supplier<DataComponentType<Boolean>> HOOK_ACTIVE =
         YoHooksNeoForge.registerC(
            Identifier.fromNamespaceAndPath("yo_hooks", "hook_active"),
            () -> DataComponentType.<Boolean>builder()
                .persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL)
                .build()
        );

        public static void register() {}

}
