package com.yori3o.yo_hooks.common.config;


import java.util.List;

import com.yori3o.yo_hooks.common.hookregistry.HookDefinition;
import com.yori3o.yo_hooks.common.network.ServerSender;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.impl.PlatformUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;


/**
 * This class stores instances of configs that are used everywhere.
 */
public class ConfigManager {


    public static ClientConfig clc = new ClientConfig();
    public static CommonConfig cc = new CommonConfig();
    public static ServerConfig sc = new ServerConfig();
    public static OverlapConfig oc = new OverlapConfig();

    public static ModConfig CONFIG = new ModConfig();


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
        if (PlatformUtil.isModLoaded("vivecraft")) VrConfig.HANDLER.load();
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

        if (!ocv.durabilityOverlap.containsKey(material)) {
            ocv.durabilityOverlap.put(material, durability);
            //ocv.durabilityOverlap.remove(material);
        }

        if (!ocv.rangeOverlap.containsKey(material)) {
            ocv.rangeOverlap.put(material, range);
            //ocv.rangeOverlap.remove(material);
        }
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

    public static void save(ModConfig config) {
        CONFIG = config;
        
        ClientConfig.Values clcv = ConfigManager.client();
        CommonConfig.Values ccv = ConfigManager.common();
        ServerConfig.Values scv = ConfigManager.server();
        OverlapConfig.Values ocv = ConfigManager.overlap();

        ccv.softHook = config.softHook;
        ccv.stiffness = config.stiffness;
        ccv.climbSpeed = config.climbSpeed;
        ccv.funnyMode = config.funnyMode;
        scv.decreaseSatiety = config.decreaseSatiety;
        scv.breakingFragileBlocks = config.breakingFragileBlocks;
        clcv.holdHookTightly = config.holdHookTightly;
        
        ocv.rangeOverlap = config.rangeOverlap;
        ocv.durabilityOverlap = config.durabilityOverlap;

        scv.blocksBlacklist = config.blocksBlacklist;
        scv.whitelistMode = config.whitelistMode;
        

        ConfigManager.cc.save();
        ConfigManager.sc.save();
        ConfigManager.clc.save();
        ConfigManager.oc.save();
        if (PlatformUtil.isModLoaded("vivecraft")) VrConfig.HANDLER.save();

        if (Minecraft.getInstance().isLocalServer()) {   

            PhysicVariables.updateCommonVariables(config.softHook, config.stiffness, config.climbSpeed);
            PhysicVariables.updateFunnyModeConfig(config.funnyMode);

            sendCommonConfigToAllPlayersInMultiplayer();
        }
    }

    private static void sendCommonConfigToAllPlayersInMultiplayer() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer().isPublished()) {
            MinecraftServer server = mc.getSingleplayerServer();
            if (server != null) {
                List<ServerPlayer> players = server.getPlayerList().getPlayers();
                ServerPlayer host = server.getPlayerList().getPlayer(mc.player.getUUID());
                for (ServerPlayer p : players) {
                    if (host != null && p.getUUID().equals(host.getUUID())) continue;
                    ServerSender.sendCommonConfig(p, ConfigManager.cc.get(), null);
                }
            }
        }
    }

}
