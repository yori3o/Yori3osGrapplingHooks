package com.yori3o.yo_hooks.common.client.vr;

import java.util.Optional;

import org.vivecraft.api.client.Tracker;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRPose;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;

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
    
    private HandTracker() { }
    
    public Optional<Vec3> getMainHandPosition() {
        return mainHandPosition;
    }

    public Optional<Vec3> getOffHandPosition() {
        return offHandPosition;
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
        VRPose vrPose = VRClientAPI.instance().getPreTickWorldPose();
        this.mainHandPosition = Optional.of(vrPose.getMainHand().getPos());
        this.offHandPosition = Optional.of(vrPose.getOffHand().getPos());
    }

    @Override
    public void inactiveProcess(LocalPlayer player) {
        this.mainHandPosition = Optional.empty();
        this.offHandPosition = Optional.empty();
    }
    
    @Override
    public ProcessType processType() {
        return ProcessType.PER_TICK;
    }
}