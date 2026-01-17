package com.yori3o.yo_hooks.common.sounds;

import com.yori3o.yo_hooks.impl.PlatformSoundRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class SoundRegistry {

    public static final SoundEvent CAST = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("yo_hooks", "cast"));
    public static final SoundEvent BACK = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("yo_hooks", "back"));
    public static final SoundEvent AMBIENT = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("yo_hooks", "ambient"));
    public static final SoundEvent CLIMB = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("yo_hooks", "climb"));

    public static void register() {
        PlatformSoundRegistry.registerSound(CAST.getLocation(), CAST);
        PlatformSoundRegistry.registerSound(BACK.getLocation(), BACK);
        PlatformSoundRegistry.registerSound(AMBIENT.getLocation(), AMBIENT);
        PlatformSoundRegistry.registerSound(CLIMB.getLocation(), CLIMB);
    }
    
}
        /*PlatformSoundRegistry.registerSound(CAST.location(), CAST);
        PlatformSoundRegistry.registerSound(BACK.location(), BACK);
        PlatformSoundRegistry.registerSound(AMBIENT.location(), AMBIENT);
        PlatformSoundRegistry.registerSound(CLIMB.location(), CLIMB);
    }
    
}*/