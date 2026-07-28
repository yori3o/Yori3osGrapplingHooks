package com.yori3o.yo_hooks.common.config;


import com.yori3o.yo_hooks.common.config.categories.*;
import com.yori3o.yo_hooks.common.hookregistry.HookDefinition;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.impl.PlatformUtil;



public class ConfigManager {
    

    public static CommonConfig common() {
        return CommonConfig.HANDLER.instance();
    }

    public static ClientConfig client() {
        return ClientConfig.HANDLER.instance();
    }

    public static ServerConfig server() {
        return ServerConfig.HANDLER.instance();
    }

    public static OverlapConfig overlap() {
        return OverlapConfig.HANDLER.instance();
    }

    public static VrConfig vr() {
        return VrConfig.HANDLER.instance();
    }

    public static void loadCommon() {
        CommonConfig.HANDLER.load();
        PhysicVariables.updateCommonVariables(common().softHook, common().stiffness, common().climbSpeed);
        PhysicVariables.updateFunnyModeConfig(common().funnyMode);
    }

    public static void loadClient() {
        ClientConfig.HANDLER.load();
        if (PlatformUtil.isModLoaded("vivecraft")) VrConfig.HANDLER.load();
    }

    public static void loadServer() {
        ServerConfig.HANDLER.load();
    }

    public static void loadOverlap() {
        OverlapConfig.HANDLER.load();
    }


    public static void commonConfigUpdate(CommonConfig a) {
        common().softHook = a.softHook;
        common().stiffness = a.stiffness;
        common().climbSpeed = a.climbSpeed;
        common().funnyMode = a.funnyMode;

        PhysicVariables.updateCommonVariables(a.softHook, a.stiffness, a.climbSpeed);
        PhysicVariables.updateFunnyModeConfig(a.funnyMode);
    }

    public static void addNewHookToOverlaps(String material, int durability, int range) {
        OverlapConfig ocv = OverlapConfig.HANDLER.instance();

        if (!ocv.durabilityOverlap.containsKey(material)) {
            ocv.durabilityOverlap.put(material, durability);
        }

        if (!ocv.rangeOverlap.containsKey(material)) {
            ocv.rangeOverlap.put(material, range);
        }
    }

    public static HookDefinition setOverlapValuesToHook(HookDefinition def) {
        String material = def.id;
        
        OverlapConfig ocv = OverlapConfig.HANDLER.instance();

        if (ocv.durabilityOverlap.containsKey(material)) {
            def.durabilityOverlap = ocv.durabilityOverlap.get(material);
        }

        if (ocv.rangeOverlap.containsKey(material)) {
            def.lengthOverlap = ocv.rangeOverlap.get(material);
        }

        return def;
    }

    public static void saveOverlap() {
        OverlapConfig.HANDLER.save();
    }

}
