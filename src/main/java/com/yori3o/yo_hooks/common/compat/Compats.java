package com.yori3o.yo_hooks.common.compat;


import com.yori3o.yo_hooks.impl.PlatformUtil;



public class Compats {

    
	public static boolean isSableLoaded = false;


    public static void checkForLoadedMods() {
        isSableLoaded = PlatformUtil.isModLoaded("sable");
    }

}