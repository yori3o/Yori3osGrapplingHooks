package com.yori3o.yo_hooks.common.util;



public class PhysicVariables {


    public static float jumpMultiplier = 1f;

    public static float hookSpeed = 2f;
    
    public static float climbSpeedMultiplier = 1f;

    public static boolean jumpAlwaysAllowed = false;

    public static boolean funnyMode;
    public static boolean softHook;
    public static float climbSpeed;
    public static float stiffness;


    public static void updateFunnyModeConfig(boolean funnyMode) {
        PhysicVariables.funnyMode = funnyMode;
        if (funnyMode) {
            jumpMultiplier = 1.4f;
            hookSpeed = 4f;
            jumpAlwaysAllowed = true;
            climbSpeedMultiplier = 1.5f;
        } else {
            jumpMultiplier = 1f;
            hookSpeed = 2f;
            jumpAlwaysAllowed = false;
            climbSpeedMultiplier = 1f;
        }
    }

    public static void updateCommonVariables(boolean softHook, float stiffness, float climbSpeed) {
        PhysicVariables.softHook = softHook;
        PhysicVariables.stiffness = stiffness;
        PhysicVariables.climbSpeed = climbSpeed;
    }
    
}
