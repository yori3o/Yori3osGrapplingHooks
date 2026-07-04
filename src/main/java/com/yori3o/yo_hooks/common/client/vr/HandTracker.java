package com.yori3o.yo_hooks.common.client.vr;

import java.util.Optional;

import org.vivecraft.api.client.Tracker;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRPose;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.gameplay.screenhandlers.KeyboardHandler;

import com.yori3o.yo_hooks.common.config.VrConfig;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class HandTracker implements Tracker {

    public static final HandTracker INSTANCE = new HandTracker();
    
    private Optional<Vec3> mainHandPosition = Optional.empty();
    private Optional<Vec3> offHandPosition = Optional.empty();
    private Optional<Vec3> hookHeadPosition = Optional.empty();

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

    public static Optional<Vec3> getHookHeadRoom(
        Vec3 offHandWorld,
        Vec3 mainHandWorld,
        Vec3 headWorld,
        Vec3 hookHeadWorld,
        Vec3 offHandRoom, 
        Vec3 mainHandRoom, 
        Vec3 headRoom
    ) {
        final double EPSILON = 1e-5;

        // 1. Находим масштабный коэффициент
        // Расстояние между руками в комнате делим на расстояние в игре
        Vec3 gameDir = mainHandWorld.subtract(offHandWorld);
        Vec3 roomDir = mainHandRoom.subtract(offHandRoom);
        double lengthGame = gameDir.length();
        double lengthRoom = roomDir.length();
        
        // Защита от деления на ноль, если руки оказались в одной точке
        if (lengthGame < EPSILON || lengthRoom < EPSILON) return Optional.empty();
        double scale = lengthRoom / lengthGame;

        // 2. Строим локальный базис мира игры (World)
        Vec3 forwardGame = gameDir.normalize();
        Vec3 toHeadGame = headWorld.subtract(offHandWorld); // Вектор от левой руки к голове
        Vec3 crossGame = toHeadGame.cross(forwardGame);
        if (crossGame.length() < EPSILON) {
            return Optional.empty();
        }
        Vec3 rightGame = crossGame.normalize(); // Вектор вправо (перпендикуляр к плоскости тела)
        Vec3 upGame = forwardGame.cross(rightGame).normalize(); // Вектор вверх (перпендикуляр к вперед и вправо)

        // 3. Находим локальные координаты точки hookHeadWorld относительно правой руки
        Vec3 vecMainHandToHookHeadWorld = hookHeadWorld.subtract(offHandWorld);
        double locX = vecMainHandToHookHeadWorld.dot(rightGame);
        double locY = vecMainHandToHookHeadWorld.dot(upGame);
        double locZ = vecMainHandToHookHeadWorld.dot(forwardGame);

        // 4. Строим локальный базис Комнаты (Room)
        Vec3 forwardRoom = roomDir.normalize();
        Vec3 toHeadRoom = headRoom.subtract(offHandRoom);
        Vec3 crossRoom = toHeadRoom.cross(forwardRoom);
        if (crossRoom.length() < EPSILON) {
            return Optional.empty();
        }
        Vec3 rightRoom = crossRoom.normalize();
        Vec3 upRoom = forwardRoom.cross(rightRoom).normalize();

        // 5. Восстанавливаем точку O в пространстве комнаты
        // Шагаем от точки M вдоль осей комнаты на локальные дистанции, умноженные на масштаб
        Vec3 stepRight = rightRoom.scale(locX * scale);
        Vec3 stepUp = upRoom.scale(locY * scale);
        Vec3 stepForward = forwardRoom.scale(locZ * scale);

        Vec3 hookHeadRoom = offHandRoom.add(stepRight).add(stepUp).add(stepForward);

        return Optional.of(hookHeadRoom);
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
        
        PlayerWithHookData hookData = (PlayerWithHookData)player;
        HookEntity hook = hookData.getHook();
        if (VrConfig.HANDLER.instance().moveAlongChain && hook.isInBlock()) {
            BlockPos hookHeadPos = hook.getBlockPos();
            if (hookHeadPos != null) {
                VRPose vrPoseWorld = VRClientAPI.instance().getPreTickWorldPose();
                this.hookHeadPosition = getHookHeadRoom(
                    vrPoseWorld.getOffHand().getPos(), 
                    vrPoseWorld.getMainHand().getPos(),
                    vrPoseWorld.getHead().getPos(), 
                    Vec3.atCenterOf(hookHeadPos),
                    vrPoseRoom.getOffHand().getPos(),
                    vrPoseRoom.getMainHand().getPos(), 
                    vrPoseRoom.getHead().getPos());
            }
        }
    }

    @Override
    public void inactiveProcess(LocalPlayer player) {
        this.mainHandPosition = Optional.empty();
        this.offHandPosition = Optional.empty();
        this.hookHeadPosition = Optional.empty();
    }
    
    @Override
    public ProcessType processType() {
        return ProcessType.PER_TICK;
    }
}