package com.yori3o.yo_hooks.common.client.vr;

import java.util.Optional;

import org.vivecraft.api.client.Tracker;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRPose;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;

import com.yori3o.yo_hooks.common.config.categories.VrConfig;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class HandTracker implements Tracker {

    public static final HandTracker INSTANCE = new HandTracker();
    
    private Optional<Vec3> mainHandPosition = Optional.empty();
    private Optional<Vec3> offHandPosition = Optional.empty();
    private Optional<Vec3> hookHeadPosition = Optional.empty();

    private Optional<SpaceTransformer> spaceTransformer = Optional.empty();

    private HandTracker() { }
    
    public Optional<Vec3> getMainHandPosition() {
        return this.mainHandPosition;
    }

    public Optional<Vec3> getOffHandPosition() {
        return this.offHandPosition;
    }

    public Optional<Vec3> getHookHeadPosition() {
        return this.hookHeadPosition;
    }

    public static boolean isHook(ItemStack itemStack) {
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
        this.mainHandPosition = Optional.ofNullable(vrPoseRoom.getMainHand().getPos());
        this.offHandPosition = Optional.ofNullable(vrPoseRoom.getOffHand().getPos());
        
        VRPose vrPoseWorld = VRClientAPI.instance().getPreTickWorldPose();
        if (this.spaceTransformer.isEmpty()) {
            try {
                this.spaceTransformer = Optional.of(new SpaceTransformer(
                    vrPoseWorld.getOffHand().getPos(), 
                    vrPoseWorld.getMainHand().getPos(),
                    vrPoseWorld.getHead().getPos(), 
                    vrPoseRoom.getOffHand().getPos(),
                    vrPoseRoom.getMainHand().getPos(), 
                    vrPoseRoom.getHead().getPos()
                ));
            } catch (IllegalArgumentException e) {
                // pass
            }
        }
        
        PlayerWithHookData hookData = (PlayerWithHookData)player;
        HookEntity hook = hookData.getHook();
        if (VrConfig.HANDLER.instance().moveAlongChain && hook.isInBlock()) {
            Vec3 hookHeadPos = new Vec3(hook.getX(), hook.getY(), hook.getZ());
            this.hookHeadPosition = this.spaceTransformer
                .map(spaceTransformer -> spaceTransformer.gameToRoom(hookHeadPos));
        }
    }

    @Override
    public void inactiveProcess(LocalPlayer player) {
        this.mainHandPosition = Optional.empty();
        this.offHandPosition = Optional.empty();
        this.hookHeadPosition = Optional.empty();
        this.spaceTransformer = Optional.empty();
    }
    
    @Override
    public ProcessType processType() {
        return ProcessType.PER_TICK;
    }
}