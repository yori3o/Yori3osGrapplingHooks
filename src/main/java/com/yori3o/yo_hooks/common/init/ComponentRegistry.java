package com.yori3o.yo_hooks.common.init;

import com.mojang.serialization.Codec;
import com.yori3o.yo_hooks.impl.PlatformRegistries;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;

public final class ComponentRegistry {
    public static final Supplier<DataComponentType<Boolean>> HOOK_ACTIVE = PlatformRegistries.component(
        "hook_active", DataComponentType.<Boolean>builder().persistent(Codec.BOOL)
            .networkSynchronized(ByteBufCodecs.BOOL).build());
    // Identifies a cast, not an item type or hotbar slot. Survives stack copies and inventory sync.
    public static final Supplier<DataComponentType<String>> HOOK_ID = PlatformRegistries.component(
        "hook_id", DataComponentType.<String>builder().persistent(Codec.STRING)
            .networkSynchronized(ByteBufCodecs.STRING_UTF8).build());
    public static void register() {}
}
