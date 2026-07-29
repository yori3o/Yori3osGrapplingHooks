package com.yori3o.yo_hooks.common.compat.sable;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

import com.yori3o.yo_hooks.common.compat.Compats;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.util.LoggerUtil;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;



public class SableCompat {

    public static Map<HookEntity, HookWithSableData> hooks = new HashMap<>();
    public static Map<HookEntity, HookWithSableData> hooksClient = new HashMap<>();



    public static SubLevel getSubLevel(Level level, Vec3 hitPos) {
        return Sable.HELPER.getContaining(level, hitPos);
    }

    public static Vec3 convertToLocal(SubLevel ship, Vec3 worldHit, Level level) {
        Vec3 worldHitNormal = Sable.HELPER.projectOutOfSubLevel(level, worldHit);
        
        Vec3 physicsCenter = ship.logicalPose().transformPosition(new Vec3(0,0,0));
        Vec3 worldCenterNormal = Sable.HELPER.projectOutOfSubLevel(level, physicsCenter);
        
        Vec3 deltaWorld = worldHitNormal.subtract(worldCenterNormal);
        
        Vector3d localResult = ship.logicalPose().orientation()
                                .transformInverse(JOMLConversion.toJOML(deltaWorld), new Vector3d());
        
        return JOMLConversion.toMojang(localResult);
    }

    public static boolean onHookHitBlock(HookEntity hookEntity, Vec3 vec) {
        SubLevel potentialShip = getSubLevel(hookEntity.level(), vec);
        LoggerUtil.info("onHookHitBlockl");
        if (potentialShip != null) {
            LoggerUtil.info("onHookHitBlockl 2");
            HookWithSableData sableData = new HookWithSableData();
            sableData.attachedShip = potentialShip;
            sableData.localAttachPos = convertToLocal(potentialShip, vec, hookEntity.level());
            if (hookEntity.level().isClientSide) {
                hooksClient.put(hookEntity, sableData);
            } else {
                hooks.put(hookEntity, sableData);
            }
            return true;
        }
        return false;
    }

    public static void tick() { // server-side
        Iterator<HookEntity> iterator = hooks.keySet().iterator();
        while (iterator.hasNext()) {
            HookEntity hookEntity = iterator.next();
            if (hookEntity.isRemoved()) {
                iterator.remove();
            }
        }
    }

    public static void tickClient() {
        Iterator<HookEntity> iterator = hooksClient.keySet().iterator();
        while (iterator.hasNext()) {
            HookEntity hookEntity = iterator.next();
            if (hookEntity.isRemoved()) {
                iterator.remove();
            }
        }
    }

    public static double calculateDist(HookEntity hookEntity, Player player, BlockHitResult hit) {
        Vec3 hitPos = Sable.HELPER.projectOutOfSubLevel(hookEntity.level(), hit.getLocation());
        return player.getEyePosition().subtract(hitPos).length();
    }

    public static Vec3 projectOutOfSubLevel(Level level, Vec3 pos) {
        return Sable.HELPER.projectOutOfSubLevel(level, pos);
    }

    public static Vec3 getGlobalPositionOfHookEntity(Vec3 position, HookEntity hookEntity) {
        if (!hookEntity.level().isClientSide) return position;
        HookWithSableData data = SableCompat.hooksClient.get(hookEntity);
        if (data != null) {
            if (data.attachedShip != null) {
                Vec3 physicsPos = ((ClientSubLevel)data.attachedShip).renderPose().transformPosition(data.localAttachPos);
                return SableCompat.projectOutOfSubLevel(hookEntity.level(), physicsPos);
            }
        }
        return position;
    }

    public static Quaterniond getGlobalRotationOfSubLevel(HookEntity hookEntity) {
        if (!hookEntity.level().isClientSide) return null;
        HookWithSableData data = SableCompat.hooksClient.get(hookEntity);
        if (data != null && data.attachedShip != null) {
            Quaterniondc shipRot = ((ClientSubLevel)(data.attachedShip)).renderPose().orientation();
            return new Quaterniond(shipRot).invert();
        }
        return null;
    }

    public static Vec3 getHookPos(Vec3 vec3, HookEntity hookEntity) {
        //if (!Compats.isSableLoaded) return vec3;
        HookWithSableData data;
        if (hookEntity.level().isClientSide) {
            data = SableCompat.hooksClient.get(hookEntity);
        } else {
            data = SableCompat.hooks.get(hookEntity);
        }
        if (data != null) {
            Vec3 physicsPos = data.attachedShip.logicalPose().transformPosition(data.localAttachPos);
            return SableCompat.projectOutOfSubLevel(hookEntity.level(), physicsPos);
        } else {
            if (hookEntity.isInSableBlock()) {
                return null;
            }
        }
        return vec3;
    }

}