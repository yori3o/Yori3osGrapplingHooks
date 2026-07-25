package com.yori3o.yo_hooks.common.client.vr;

import java.util.Optional;

import org.vivecraft.api.client.Tracker;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRPose;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;

import com.yori3o.yo_hooks.common.config.ConfigManager;
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
    
    private Optional<Vec3> mainHandPositionRoom = Optional.empty();
    private Optional<Vec3> offHandPositionRoom = Optional.empty();
    private Optional<Vec3> mainHandPositionWorld = Optional.empty();
    private Optional<Vec3> offHandPositionWorld = Optional.empty();
    private Optional<Vec3> hookHeadPositionWorld = Optional.empty();

    private HandTracker() { }
    
    public Optional<Vec3> getMainHandPositionRoom() {
        return this.mainHandPositionRoom;
    }

    public Optional<Vec3> getOffHandPositionRoom() {
        return this.offHandPositionRoom;
    }

    public Optional<Vec3> getMainHandPositionWorld() {
        return this.mainHandPositionWorld;
    }

    public Optional<Vec3> getOffHandPositionWorld() {
        return this.offHandPositionWorld;
    }

    public Optional<Vec3> getHookHeadPositionWorld() {
        return this.hookHeadPositionWorld;
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
        this.mainHandPositionRoom = Optional.ofNullable(vrPoseRoom.getMainHand().getPos());
        this.offHandPositionRoom = Optional.ofNullable(vrPoseRoom.getOffHand().getPos());

        VRPose vrPoseWorld = VRClientAPI.instance().getPreTickWorldPose();
        this.mainHandPositionWorld = Optional.ofNullable(vrPoseWorld.getMainHand().getPos());
        this.offHandPositionWorld = Optional.ofNullable(vrPoseWorld.getOffHand().getPos());
        
        PlayerWithHookData hookData = (PlayerWithHookData)player;
        HookEntity hook = hookData.getHook();
        if (ConfigManager.vr().moveAlongChain && hook.isInBlock()) {
            Vec3 hookHeadPos = new Vec3(hook.getX(), hook.getY(), hook.getZ());
            this.hookHeadPositionWorld = Optional.of(hookHeadPos);
        }
    }

    @Override
    public void inactiveProcess(LocalPlayer player) {
        this.mainHandPositionRoom = Optional.empty();
        this.offHandPositionRoom = Optional.empty();
        this.mainHandPositionWorld = Optional.empty();
        this.offHandPositionWorld = Optional.empty();
        this.hookHeadPositionWorld = Optional.empty();
    }
    
    @Override
    public ProcessType processType() {
        return ProcessType.PER_TICK;
    }
}