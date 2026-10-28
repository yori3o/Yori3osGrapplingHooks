package com.yori3o.yo_hooks.common.compat.sable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;

/** Undo Sable's entity transforms without changing the caller's camera transform. */
public final class SableRenderMath {
    private SableRenderMath() {}

    public static void undoEntityTransforms(PoseStack stack, Quaterniondc containingRotation,
            Quaterniondc entityRotation, Vec3 eyeOffset, Vec3 interpolationOffset) {
        // Sable applies T(offset) T(eye) R(entity) T(-eye) after the containing rotation.
        // Undo in reverse order; cancelling just R leaves an offset on tilted sub-levels.
        if (entityRotation != null) {
            stack.translate(eyeOffset.x, eyeOffset.y, eyeOffset.z);
            stack.mulPose(new Quaternionf(entityRotation).invert());
            Vec3 offset = eyeOffset.add(interpolationOffset);
            stack.translate(-offset.x, -offset.y, -offset.z);
        }
        if (containingRotation != null) stack.mulPose(new Quaternionf(containingRotation).invert());
    }
}
