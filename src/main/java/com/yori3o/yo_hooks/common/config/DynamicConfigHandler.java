package com.yori3o.yo_hooks.common.config;

import com.yori3o.yo_hooks.common.utils.PhysicVariables;

public class DynamicConfigHandler {

    // --- server config variables ---
    public static float decreaseSatiety;
    public static boolean breakingFragileBlocks;

    // --- common config variables ---
    public static int copperHookDurabitility;
    public static int ironHookDurabitility;
    public static int goldHookDurabitility;
    public static int diamondHookDurabitility;
    public static int netheriteHookDurabitility;

    public static int copperHookLength;
    public static int ironHookLength;
    public static int goldHookLength;
    public static int diamondHookLength;
    public static int netheriteHookLength;

    public static boolean softHook;
    public static float stiffness;
    public static float climbSpeed;
    public static boolean funnyMode;


    private static CommonConfig cc = new CommonConfig();
    private static ServerConfig sc = new ServerConfig();



    public static void CommonConfigLoad() {
        CommonConfig cc = new CommonConfig();
        cc.load();

        copperHookDurabitility = cc.get().copperHookDurabitility;
        ironHookDurabitility = cc.get().ironHookDurabitility;
        goldHookDurabitility = cc.get().goldHookDurabitility;
        diamondHookDurabitility = cc.get().diamondHookDurabitility;
        netheriteHookDurabitility = cc.get().netheriteHookDurabitility;

        copperHookLength = cc.get().copperHookLength;
        ironHookLength = cc.get().ironHookLength;
        goldHookLength = cc.get().goldHookLength;
        diamondHookLength = cc.get().diamondHookLength;
        netheriteHookLength = cc.get().netheriteHookLength;
    }

    public static void CommonConfigUpdate() {
        CommonConfig cc = new CommonConfig();
        cc.load();

        softHook = cc.get().softHook;
        stiffness = cc.get().stiffness;
        climbSpeed = cc.get().climbSpeed;
        funnyMode = cc.get().funnyMode;

        PhysicVariables.UpdateCommonVariables(softHook, stiffness, climbSpeed);
        PhysicVariables.UpdateFunnyModeConfig(funnyMode);
    }

    public static void ServerConfigUpdate() {
        ServerConfig sc = new ServerConfig();
        sc.load();
        decreaseSatiety = sc.get().decreaseSatiety;
        breakingFragileBlocks = sc.get().breakingFragileBlocks;
    }




    // - common setters -
    public static void setSoftHook(boolean value) {
        softHook = value;
        cc.get().softHook = value;
        cc.save();
    }
    public static void setStiffness(float value) {
        stiffness = value;
        cc.get().stiffness = value;
        cc.save();
    }
    public static void setClimbSpeed(float value) {
        climbSpeed = value;
        cc.get().climbSpeed = value;
        cc.save();
    }
    public static void setFunnyMode(boolean value) {
        funnyMode = value;
        cc.get().funnyMode = value;
        cc.save();
    }


    // - server setters -
    public static void setDecreaseSatiety(float value) {
        decreaseSatiety = value;
        sc.get().decreaseSatiety = value; 
        sc.save();
    }
    public static void setBreakingFragileBlocks(boolean value) {
        breakingFragileBlocks = value;
        sc.get().breakingFragileBlocks = value; 
        sc.save();
    }

}
