package com.yori3o.yo_hooks.common.compat.sable;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.joml.Vector3d;

import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.util.LoggerUtil;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;



public class SableCompat {

    public static Map<HookEntity, HookWithSableData> hooks = new HashMap<>();



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
        if (potentialShip != null) {
            HookWithSableData sableData = new HookWithSableData();
            sableData.attachedShip = potentialShip;
            sableData.localAttachPos = convertToLocal(potentialShip, vec, hookEntity.level());
            hooks.put(hookEntity, sableData);
            return true;
        }
        return false;
    }

    public static void tick() {
        Iterator<HookEntity> iterator = hooks.keySet().iterator();
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

}