package com.yori3o.yo_hooks.common.compat.sable;


import java.util.HashMap;
import java.util.Map;

import org.joml.Vector3d;

import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.util.LoggerUtil;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.sublevel.SubLevel;



public class SableCompat {

    public static Map<HookEntity, HookWithSableData> hooks = new HashMap<>();



    /*public static boolean handleHookFlight(HookEntity hookEntity) {
        BlockHitResult hit = getHitResult(hookEntity.level(), hookEntity, hookEntity.position(), hookEntity.position().add(hookEntity.getDeltaMovement()));
        if (hit != null) {
            if (onHookHitBlock(hookEntity, hit)) {
                hookEntity.allowOnHit = false;
                return true;
            }
        }
        return false;
    }*/


	/**
     * Пытаемся найти попадание в подмир. 
     * Если Sable не дает готовый рейкаст, мы делаем "проекцию".
     */
    public static BlockHitResult getHitResult(Level level, HookEntity hook, Vec3 currentPos, Vec3 nextPos) {
        // 1. Сначала делаем обычный клип. 
        // В новых версиях Sable/Aeronautics ванильный clip часто патчится через Mixin,
        // чтобы он видел блоки внутри SubLevel.
        BlockHitResult hit = level.clip(new ClipContext(
                currentPos,
                nextPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                hook
        ));

        if (hit.getType() == HitResult.Type.BLOCK) {
            LoggerUtil.info("RETURN HIT");
            return hit;
        }
            LoggerUtil.info("RETURN NULL HIT");

        return null;
    }

    /**
     * Возвращает подмир по позиции. Нужен для инициализации привязки.
     */
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

    public static boolean onHookHitBlock(HookEntity hookEntity, BlockHitResult hit) {
        SubLevel potentialShip = getSubLevel(hookEntity.level(), hit.getLocation());
        if (potentialShip != null) {
            HookWithSableData sableData = new HookWithSableData();
            sableData.attachedShip = potentialShip;
            sableData.localAttachPos = convertToLocal(potentialShip, hit.getLocation(), hookEntity.level());
            hooks.put(hookEntity, sableData);
            return true;
        }
        return false;
    }

    public static void tick() {
        for (HookEntity hookEntity : hooks.keySet()) {
            if (hookEntity.isRemoved()) {
                hooks.remove(hookEntity);
                continue;
            }

        }
    }

    public static double calculateDist(HookEntity hookEntity, Player player, BlockHitResult hit) {
        Vec3 hitPos = Sable.HELPER.projectOutOfSubLevel(hookEntity.level(), hit.getLocation());
        return player.getEyePosition().subtract(hitPos).length();
    }

}