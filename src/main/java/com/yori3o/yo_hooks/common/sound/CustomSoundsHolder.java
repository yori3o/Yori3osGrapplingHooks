package com.yori3o.yo_hooks.common.sound;


import net.minecraft.sounds.SoundEvent;



public class CustomSoundsHolder {


    public final SoundEvent CAST;
    public final SoundEvent BACK;
    public final SoundEvent AMBIENT;
    public final SoundEvent CLIMB;
    public final SoundEvent HIT;

    
    public CustomSoundsHolder(SoundEvent cast, SoundEvent back, SoundEvent ambient, SoundEvent climb, SoundEvent hit) {
        CAST = cast;
        BACK = back;
        AMBIENT = ambient;
        CLIMB = climb;
        HIT = hit;
    }
    
}