package com.yori3o.yo_hooks.common.utils;

public class PhysicVariables {

    public static float jump_Multiplier = 1f;

    public static float hookSpeed = 2f;

    public static float vTangential_Multiplier = 1.048f;
    
    public static float climbSpeed_Multiplier = 1f;

    public static boolean jumpAlwaysAllowed = false;

    public static boolean funnyMode;
    public static boolean softHook;
    public static float climbSpeed;
    public static float stiffness;



    public static void UpdateFunnyModeConfig(boolean funnyMode) {
        PhysicVariables.funnyMode = funnyMode;
        if (funnyMode) {
            jump_Multiplier = 1.4f;
            hookSpeed = 4f;
            vTangential_Multiplier = 1.05f;
            jumpAlwaysAllowed = true;
            climbSpeed_Multiplier = 1.5f;
        } else {
            jump_Multiplier = 1f;
            hookSpeed = 2f;
            vTangential_Multiplier = 1.0475f;
            jumpAlwaysAllowed = false;
            climbSpeed_Multiplier = 1f;
        }
    }

    public static void UpdateCommonVariables(boolean bool, float a, float a2) {
        softHook = bool;
        stiffness = a;
        climbSpeed = a2;
        
    }
    
}
