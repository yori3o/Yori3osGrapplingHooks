package com.yori3o.yo_hooks.common.client.vr;

import java.util.Optional;

import org.vivecraft.api.client.Tracker;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPartData;
import org.vivecraft.api.data.VRPose;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.gameplay.VRPlayer;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;

import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;


public class HandTracker implements Tracker {

    public static final HandTracker INSTANCE = new HandTracker();
    
    private Optional<Vec3> freeHandPosition = Optional.empty();
    private Optional<Vec3> hookHandPosition = Optional.empty();
    private Optional<Vec3> hookHeadPosition = Optional.empty();

    private HandTracker() { }
    
    public Optional<Vec3> getFreeHandPosition() {
        return this.freeHandPosition;
    }

    public Optional<Vec3> getHookHandPosition() {
        return this.hookHandPosition;
    }

    public Optional<Vec3> getHookHeadPosition() {
        return this.hookHeadPosition;
    }

    public static Vec3 getChainStartWorld(VRBodyPartData hookHandWorld) {
        final double PHI = 3 * Mth.PI / 2;
        final double THETA = Mth.PI / 2 - 0.07;
        final double R = 0.15;
        Vec3 sourceNorm = new Vec3(
            Math.sin(THETA) * Math.cos(PHI), 
            Math.cos(THETA), 
            Math.sin(THETA) * Math.sin(PHI)
        ).scale(R * VRClientAPI.instance().getWorldScale());
        Vec3 handSource = hookHandWorld.getPos().add(new Vec3(hookHandWorld.getRotation().transform(sourceNorm.toVector3f())));
        return handSource;
    }

    private static boolean isHook(ItemStack itemStack) {
        if (itemStack == ItemStack.EMPTY) {
            return false;
        } else {
            return itemStack.getItem() instanceof HookItem;
        }
    }

    private static boolean isHoldingActiveHookWithFreeHand(LocalPlayer player) {
        if (ClientDataHolderVR.getInstance().vrSettings.seated) {
            return false;
        }

        PlayerWithHookData hookData = (PlayerWithHookData)player;
        HookEntity hook = hookData.getHook();
        if (hook == null || !hook.isInBlock()) {
            return false;
        }

        if (isHook(player.getItemInHand(InteractionHand.MAIN_HAND)) && player.getItemInHand(InteractionHand.OFF_HAND) == ItemStack.EMPTY) {
            return true;
        } else if (isHook(player.getItemInHand(InteractionHand.OFF_HAND)) && player.getItemInHand(InteractionHand.MAIN_HAND) == ItemStack.EMPTY) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isActive(LocalPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (player == null) {
            return false;
        } else if (mc.gameMode == null) {
            return false;
        } else if (mc.gui.screen() != null) {
            return false;
        } else if (KeyboardHandler.SHOWING) {
            return false;
        } else if (!player.isAlive()) {
            return false;
        } else if (player.isSleeping()) {
            return false;
        } else {
            return isHoldingActiveHookWithFreeHand(player);
        }
    }

    @Override
    public void activeProcess(LocalPlayer player) {
        VRPose vrPoseRoom = VRClientAPI.instance().getLatestRoomPose();
        VRBodyPartData mainHandRoom = vrPoseRoom.getMainHand();
        VRBodyPartData offHandRoom = vrPoseRoom.getOffHand();

        VRPose vrPoseWorld = VRClientAPI.instance().getPreTickWorldPose();
        VRBodyPartData mainHandWorld = vrPoseWorld.getMainHand();
        VRBodyPartData offHandWorld = vrPoseWorld.getOffHand();

        if (player.getItemInHand(InteractionHand.OFF_HAND) == ItemStack.EMPTY) {
            this.freeHandPosition = Optional.ofNullable(offHandRoom.getPos());
            Vec3 chainStart = new Vec3(VRPlayer.worldToRoomPos(getChainStartWorld(mainHandWorld), ClientDataHolderVR.getInstance().vrPlayer.vrdata_world_pre));
            this.hookHandPosition = Optional.ofNullable(chainStart);
        } else {
            this.freeHandPosition = Optional.ofNullable(mainHandRoom.getPos());
            Vec3 chainStart = new Vec3(VRPlayer.worldToRoomPos(getChainStartWorld(offHandWorld), ClientDataHolderVR.getInstance().vrPlayer.vrdata_world_pre));
            this.hookHandPosition = Optional.ofNullable(chainStart);
        }
        
        PlayerWithHookData hookData = (PlayerWithHookData)player;
        HookEntity hook = hookData.getHook();
        if (ConfigManager.vr().moveAlongChain && hook.isInBlock()) {
            Vec3 hookHeadPosWorld = new Vec3(hook.getX(), hook.getY(), hook.getZ());
            Vec3 hookHeadPosRoom = new Vec3(VRPlayer.worldToRoomPos(hookHeadPosWorld, ClientDataHolderVR.getInstance().vrPlayer.vrdata_world_pre));
            this.hookHeadPosition = Optional.of(hookHeadPosRoom);
        }
    }

    @Override
    public void inactiveProcess(LocalPlayer player) {
        this.freeHandPosition = Optional.empty();
        this.hookHandPosition = Optional.empty();
        this.hookHeadPosition = Optional.empty();
    }
    
    @Override
    public ProcessType processType() {
        return ProcessType.PER_TICK;
    }
}