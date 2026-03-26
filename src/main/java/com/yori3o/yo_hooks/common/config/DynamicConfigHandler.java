package com.yori3o.yo_hooks.common.config;


import com.yori3o.yo_hooks.common.util.PhysicVariables;


/**
 * This class stores instances of configs that are used everywhere.
 */
public class DynamicConfigHandler {


    public static CommonConfig cc = new CommonConfig();
    public static ServerConfig sc = new ServerConfig();


    public static CommonConfig.Values common() {
        return cc.get();
    }

    public static ServerConfig.Values server() {
        return sc.get();
    }

    public static void loadCommon() {
        cc.load();
        PhysicVariables.updateCommonVariables(common().softHook, common().stiffness, common().climbSpeed);
        PhysicVariables.updateFunnyModeConfig(common().funnyMode);
    }

    public static void loadServer() {
        sc.load();
    }


    public static void commonConfigUpdate(CommonConfig.Values cc) {

        common().softHook = cc.softHook;
        common().stiffness = cc.stiffness;
        common().climbSpeed = cc.climbSpeed;
        common().funnyMode = cc.funnyMode;

        PhysicVariables.updateCommonVariables(common().softHook, common().stiffness, common().climbSpeed);
        PhysicVariables.updateFunnyModeConfig(common().funnyMode);
    }

}
