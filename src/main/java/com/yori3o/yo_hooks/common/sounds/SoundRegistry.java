package com.yori3o.yo_hooks.common.sounds;

import com.yori3o.yo_hooks.impl.PlatformSoundRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {

    public static final SoundEvent CAST = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "cast"));
    public static final SoundEvent BACK = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "back"));
    public static final SoundEvent AMBIENT = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "ambient"));
    public static final SoundEvent CLIMB = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "climb"));

    public static void register() {

        PlatformSoundRegistry.registerSound(CAST.location(), CAST);
        PlatformSoundRegistry.registerSound(BACK.location(), BACK);
        PlatformSoundRegistry.registerSound(AMBIENT.location(), AMBIENT);
        PlatformSoundRegistry.registerSound(CLIMB.location(), CLIMB);
    }
    
}