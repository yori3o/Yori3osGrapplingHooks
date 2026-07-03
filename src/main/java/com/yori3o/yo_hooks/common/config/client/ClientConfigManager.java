package com.yori3o.yo_hooks.common.config.client;


import java.util.List;

import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.config.ModConfig;
import com.yori3o.yo_hooks.common.config.categories.*;
import com.yori3o.yo_hooks.common.network.ServerSender;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.impl.PlatformUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;



public class ClientConfigManager {

    public static void save(ModConfig config) {
        ConfigManager.CONFIG = config;
        
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
        clcv.climbWithMouseWheelScroll = config.climbWithMouseWheelScroll;
        
        ocv.rangeOverlap = config.rangeOverlap;
        ocv.durabilityOverlap = config.durabilityOverlap;

        scv.blocksBlacklist = config.blocksBlacklist;
        scv.whitelistMode = config.whitelistMode;
        

        ConfigManager.cc.save();
        ConfigManager.sc.save();
        ConfigManager.clc.save();
        ConfigManager.oc.save();
        if (PlatformUtil.isModLoaded("vivecraft")) VrConfig.HANDLER.save();

        if ( Minecraft.getInstance().isLocalServer()) {   

            PhysicVariables.updateCommonVariables(config.softHook, config.stiffness, config.climbSpeed);
            PhysicVariables.updateFunnyModeConfig(config.funnyMode);

            sendCommonConfigToAllPlayersInMultiplayer();
        }
    }


    public static void sendCommonConfigToAllPlayersInMultiplayer() {
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
