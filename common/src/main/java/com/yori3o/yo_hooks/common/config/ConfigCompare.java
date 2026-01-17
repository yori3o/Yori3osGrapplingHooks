package com.yori3o.yo_hooks.common.config;


public final class ConfigCompare {


    public static final String checkValues(
            boolean softHook,
            float stiffness,
            float climbSpeed,
            boolean funnyMode
    ) {
        StringBuilder wrongValues = new StringBuilder();
        boolean hasErrors = false;

        hasErrors |= checkBoolean("softHook", softHook, DynamicConfigHandler.softHook, wrongValues);
        hasErrors |= checkFloat("stiffness", stiffness, DynamicConfigHandler.stiffness, wrongValues);
        hasErrors |= checkFloat("climbSpeed", climbSpeed, DynamicConfigHandler.climbSpeed, wrongValues);
        hasErrors |= checkBoolean("funnyMode", funnyMode, DynamicConfigHandler.funnyMode, wrongValues);

        return hasErrors ? wrongValues.toString() : null;
    }

    
    // ===== helpers =====

    private static final boolean checkBoolean(
            String name,
            boolean actual,
            boolean expected,
            StringBuilder out
    ) {
        if (actual != expected) {
            append(out, name + " (" + expected + ")");
            return true;
        }
        return false;
    }
    

    private static final boolean checkFloat(
            String name,
            float actual,
            float expected,
            StringBuilder out
    ) {
        if (Math.abs(actual - expected) > 0.001f) {
            append(out, name + " (" + expected + ")");
            return true;
        }
        return false;
    }


    private static final void append(StringBuilder out, String text) {
        if (out.length() > 0) {
            out.append(", ");
        }
        out.append(text);
    }
}
