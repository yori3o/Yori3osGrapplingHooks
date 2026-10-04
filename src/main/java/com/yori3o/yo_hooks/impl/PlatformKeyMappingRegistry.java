package com.yori3o.yo_hooks.impl;


import net.minecraft.client.KeyMapping;

import java.util.ArrayList;
import java.util.List;



public class PlatformKeyMappingRegistry {


    public static final List<KeyMapping> keyMappings = new ArrayList<>();


    public static void registerKeyMapping(KeyMapping keyMapping) {
        keyMappings.add(keyMapping);
    }

}

