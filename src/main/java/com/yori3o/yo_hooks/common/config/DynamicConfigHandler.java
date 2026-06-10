package com.yori3o.yo_hooks.common.config;


import com.yori3o.yo_hooks.common.hookregistry.HookDefinition;
import com.yori3o.yo_hooks.common.util.PhysicVariables;


/**
 * This class stores instances of configs that are used everywhere.
 */
public class DynamicConfigHandler {


    public static ClientConfig clc = new ClientConfig();
    public static CommonConfig cc = new CommonConfig();
    public static ServerConfig sc = new ServerConfig();
    public static OverlapConfig oc = new OverlapConfig();


    public static CommonConfig.Values common() {
        return cc.get();
    }

    public static ClientConfig.Values client() {
        return clc.get();
    }

    public static ServerConfig.Values server() {
        return sc.get();
    }

    public static OverlapConfig.Values overlap() {
        return oc.get();
    }

    public static void loadCommon() {
        cc.load();
        PhysicVariables.updateCommonVariables(common().softHook, common().stiffness, common().climbSpeed);
        PhysicVariables.updateFunnyModeConfig(common().funnyMode);
        ConfigManager.CONFIG.softHook = common().softHook;
        ConfigManager.CONFIG.stiffness = common().stiffness;
        ConfigManager.CONFIG.climbSpeed = common().climbSpeed;
        ConfigManager.CONFIG.funnyMode = common().funnyMode;
    }

    public static void loadClient() {
        clc.load();
        ConfigManager.CONFIG.holdHookTightly = client().holdHookTightly;
    }

    public static void loadServer() {
        sc.load();
        ConfigManager.CONFIG.breakingFragileBlocks = server().breakingFragileBlocks;
        ConfigManager.CONFIG.decreaseSatiety = server().decreaseSatiety;
        ConfigManager.CONFIG.blocksBlacklist = server().blocksBlacklist;
        ConfigManager.CONFIG.whitelistMode = server().whitelistMode;
    }

    public static void loadOverlap() {
        oc.load();
        ConfigManager.CONFIG.rangeOverlap = overlap().rangeOverlap;
        ConfigManager.CONFIG.durabilityOverlap = overlap().durabilityOverlap;
    }


    public static void commonConfigUpdate(CommonConfig.Values cc) {
        common().softHook = cc.softHook;
        common().stiffness = cc.stiffness;
        common().climbSpeed = cc.climbSpeed;
        common().funnyMode = cc.funnyMode;

        PhysicVariables.updateCommonVariables(common().softHook, common().stiffness, common().climbSpeed);
        PhysicVariables.updateFunnyModeConfig(common().funnyMode);
    }

    public static void addNewHookToOverlaps(String material, int durability, int range) {
        OverlapConfig.Values ocv = oc.get();

        if (ocv.durabilityOverlap.containsKey(material)) {
            ocv.durabilityOverlap.remove(material);
        }
        ocv.durabilityOverlap.put(material, durability);

        if (ocv.rangeOverlap.containsKey(material)) {
            ocv.rangeOverlap.remove(material);
        }
        ocv.rangeOverlap.put(material, range);
    }

    public static void setOverlapValues(HookDefinition def) {
        String material = def.id;
        
        OverlapConfig.Values ocv = oc.get();

        if (ocv.durabilityOverlap.containsKey(material)) {
            def.durabilityOverlap = ocv.durabilityOverlap.get(material);
        }

        if (ocv.rangeOverlap.containsKey(material)) {
            def.lengthOverlap = ocv.rangeOverlap.get(material);
        }
    }

    public static void saveOverlap() {
        oc.save();
    }

}
