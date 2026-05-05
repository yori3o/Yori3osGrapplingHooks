package com.yori3o.yo_hooks.common.compat;


import com.yori3o.yo_hooks.impl.PlatformUtil;



public class Compats {


    private static final boolean ENABLED = true;

	public static boolean isSableLoaded = false;


    public static void checkForLoadedMods() {
        if (ENABLED) {
            isSableLoaded = PlatformUtil.isModLoaded("sable");
        }
    }

}