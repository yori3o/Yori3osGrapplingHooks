package com.yori3o.yo_hooks.common.sound;


import com.yori3o.yo_hooks.common.hookregistry.HookRegistry;
import com.yori3o.yo_hooks.impl.PlatformSoundRegistry;

import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import java.util.HashMap;
import java.util.Map;


/**
 * This class registers and contains sounds, as well as custom ones.
 */
public class SoundRegistry {


    public static final SoundEvent CAST = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "cast"));
    public static final SoundEvent BACK = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "back"));
    public static final SoundEvent AMBIENT = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "ambient"));
    public static final SoundEvent CLIMB = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "climb"));
    public static final SoundEvent HIT = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "hit"));

    public static final Map<String, CustomSoundsHolder> customSounds = new HashMap<>();

    private static boolean checkForCustomVisual = false; // This thing is needed for easy optimization when there is no one hook with customVisual


    public static void register() {
        PlatformSoundRegistry.registerSound(CAST.location(), CAST);
        PlatformSoundRegistry.registerSound(BACK.location(), BACK);
        PlatformSoundRegistry.registerSound(AMBIENT.location(), AMBIENT);
        PlatformSoundRegistry.registerSound(CLIMB.location(), CLIMB);
        PlatformSoundRegistry.registerSound(HIT.location(), HIT);
    }

    public static void registerNewCustomSounds(String material) {
        checkForCustomVisual = true;

        final SoundEvent CAST_CUSTOM = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "cast_" + material));
        final SoundEvent BACK_CUSTOM = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "back_" + material));
        final SoundEvent AMBIENT_CUSTOM = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "ambient_" + material));
        final SoundEvent CLIMB_CUSTOM = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "climb_" + material));
        final SoundEvent HIT_CUSTOM = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("yo_hooks", "hit_" + material));

        PlatformSoundRegistry.registerSound(CAST_CUSTOM.location(), CAST_CUSTOM);
        PlatformSoundRegistry.registerSound(BACK_CUSTOM.location(), BACK_CUSTOM);
        PlatformSoundRegistry.registerSound(AMBIENT_CUSTOM.location(), AMBIENT_CUSTOM);
        PlatformSoundRegistry.registerSound(CLIMB_CUSTOM.location(), CLIMB_CUSTOM);
        PlatformSoundRegistry.registerSound(HIT_CUSTOM.location(), HIT_CUSTOM);

        customSounds.put(material, new CustomSoundsHolder(CAST_CUSTOM, BACK_CUSTOM, AMBIENT_CUSTOM, CLIMB_CUSTOM, HIT_CUSTOM));
    }
    
    public static SoundEvent getCastSound(String material) {
        if (!checkForCustomVisual) return CAST;
        if (HookRegistry.hookMaterialsWithCustomVisuals.contains(material)) {
            return customSounds.get(material).CAST;
        } else {
            return CAST;
        }
    }

    public static SoundEvent getBackSound(String material) {
        if (!checkForCustomVisual) return BACK;
        if (HookRegistry.hookMaterialsWithCustomVisuals.contains(material)) {
            return customSounds.get(material).BACK;
        } else {
            return BACK;
        }
    }

    public static SoundEvent getAmbientSound(String material) {
        if (!checkForCustomVisual) return AMBIENT;
        if (HookRegistry.hookMaterialsWithCustomVisuals.contains(material)) {
            return customSounds.get(material).AMBIENT;
        } else {
            return AMBIENT;
        }
    }

    public static SoundEvent getClimbSound(String material) {
        if (!checkForCustomVisual) return CLIMB;
        if (HookRegistry.hookMaterialsWithCustomVisuals.contains(material)) {
            return customSounds.get(material).CLIMB;
        } else {
            return CLIMB;
        }
    }

    public static SoundEvent getHitSound(String material) {
        if (!checkForCustomVisual) return HIT;
        if (HookRegistry.hookMaterialsWithCustomVisuals.contains(material)) {
            return customSounds.get(material).HIT;
        } else {
            return HIT;
        }
    }

}