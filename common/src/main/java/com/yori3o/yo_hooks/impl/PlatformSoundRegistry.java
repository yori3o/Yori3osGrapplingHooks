package com.yori3o.yo_hooks.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class PlatformSoundRegistry {

    public static void registerSound(ResourceLocation id, SoundEvent event) {
        throw new RuntimeException("Platform-specific implementation missing");
    }
    
}
