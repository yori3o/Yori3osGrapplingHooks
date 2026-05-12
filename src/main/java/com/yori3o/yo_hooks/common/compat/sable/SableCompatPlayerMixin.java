package com.yori3o.yo_hooks.common.compat.sable;



import com.yori3o.yo_hooks.common.compat.Compats;
import com.yori3o.yo_hooks.common.entity.HookEntity;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;




public class SableCompatPlayerMixin {

    public static Vec3 projectOutOfSubLevel(Level level, Vec3 pos) {
        if (!Compats.isSableLoaded) return null;
        return SableCompat.projectOutOfSubLevel(level, pos);
    }

    public static Vec3 handleHookPos(HookEntity hookEntity, Vec3 vec3) {
        if (!Compats.isSableLoaded) return vec3;
        HookWithSableData data = SableCompat.hooks.get(hookEntity);
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