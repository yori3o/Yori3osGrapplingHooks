package com.yori3o.yo_hooks.common.init;


import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;



public class ComponentRegistry {


    public static final DataComponentType<Boolean> HOOK_ACTIVE =
        Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath("yo_hooks", "hook_active"),
            DataComponentType.<Boolean>builder()
                .persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL)
                .build()
        );

        public static void register() {}

}
