package com.yori3o.yo_hooks.common.compat.sable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.mixinhelpers.camera.camera_rotation.EntitySubLevelRotationHelper;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class SableCompat {
    public static final Map<HookEntity, HookWithSableData> hooks = new HashMap<>();
    public static final Map<HookEntity, HookWithSableData> hooksClient = new HashMap<>();

    public static boolean onHookHitBlock(HookEntity hook, BlockHitResult hit) {
        // Sable's BlockGetter.clip returns the hit in plot coordinates, not world coordinates.
        // Use the block center to identify the plot even when the hit is on its boundary.
        SubLevel ship = Sable.HELPER.getContaining(hook.level(), hit.getBlockPos().getCenter());
        if (ship == null) return false;
        hook.setSableAttachPos(hit.getLocation());
        cache(hook, ship, hit.getLocation());
        return true;
    }

    private static HookWithSableData cache(HookEntity hook, SubLevel ship, Vec3 local) {
        var map = hook.level().isClientSide ? hooksClient : hooks;
        HookWithSableData data = map.get(hook);
        if (data == null || data.attachedShip != ship || !local.equals(data.localAttachPos)) {
            data = new HookWithSableData(ship, local);
            map.put(hook, data);
        }
        return data;
    }

    @Nullable
    public static HookWithSableData resolveAttachment(HookEntity hook) {
        if (!hook.isInSableBlock()) return null;
        Vec3 local = hook.getSableAttachPos();
        if (local == null) return null; // Wait for the precise server metadata, never freeze an interpolated position.
        SubLevel ship = Sable.HELPER.getContaining(hook.level(), hook.getBlockPos().getCenter());
        if (ship == null || ship.isRemoved()) return null;
        return cache(hook, ship, local);
    }

    public static void tick() {
        hooks.keySet().removeIf(hook -> hook.isRemoved() || hook.level().isClientSide);
    }

    public static void tickClient() {
        hooksClient.keySet().removeIf(HookEntity::isRemoved);
    }

    public static double calculateDist(HookEntity hook, Player player, BlockHitResult hit) {
        Vec3 hitPos = Sable.HELPER.projectOutOfSubLevel(hook.level(), hit.getLocation());
        Vec3 eye = Sable.HELPER.projectOutOfSubLevel(player.level(), player.getEyePosition());
        return eye.distanceTo(hitPos);
    }

    @Nullable
    public static Vec3 getHookPos(Vec3 position, HookEntity hook) {
        if (!hook.isInSableBlock()) return position;
        HookWithSableData data = resolveAttachment(hook);
        return data == null ? null : data.attachedShip.logicalPose().transformPosition(data.localAttachPos);
    }

    /** Same origin calculation as Sable 2.0.3/2.0.6's LevelRendererMixin. */
    private static Vec3 dispatcherOrigin(HookEntity hook, float partialTicks) {
        Vec3 local = new Vec3(Mth.lerp(partialTicks, hook.xOld, hook.getX()),
            Mth.lerp(partialTicks, hook.yOld, hook.getY()), Mth.lerp(partialTicks, hook.zOld, hook.getZ()));
        var containing = Sable.HELPER.getContainingClient(hook);
        if (containing != null) return containing.renderPose(partialTicks).transformPosition(local);
        var tracking = Sable.HELPER.getTrackingSubLevel(hook);
        if (tracking instanceof ClientSubLevel ship && !hook.isPassenger()) {
            Vec3 previous = ship.lastPose().transformPositionInverse(new Vec3(hook.xOld, hook.yOld, hook.zOld));
            Vec3 current = ship.logicalPose().transformPositionInverse(hook.position());
            return ship.renderPose(partialTicks).transformPosition(previous.lerp(current, partialTicks));
        }
        return local;
    }

    @Nullable
    public static Vec3 getRenderPosition(HookEntity hook, float partialTicks) {
        if (!hook.isInSableBlock()) return dispatcherOrigin(hook, partialTicks);
        HookWithSableData data = resolveAttachment(hook);
        return data != null && data.attachedShip instanceof ClientSubLevel ship
            ? ship.renderPose(partialTicks).transformPosition(data.localAttachPos) : null;
    }

    public static void prepareWorldRender(PoseStack stack, HookEntity hook, float partialTicks, Vec3 anchor) {
        var containing = Sable.HELPER.getContainingClient(hook);
        var rotation = EntitySubLevelUtil.shouldKick(hook)
            ? EntitySubLevelRotationHelper.getEntityOrientation(hook,
                ship -> ((ClientSubLevel) ship).renderPose(partialTicks), partialTicks,
                EntitySubLevelRotationHelper.Type.ENTITY) : null;
        Vec3 eyeOffset = hook.getEyePosition().subtract(hook.position());
        Vec3 interpolationOffset = Sable.HELPER.getEyePositionInterpolated(hook, partialTicks)
            .subtract(hook.getEyePosition(partialTicks));
        SableRenderMath.undoEntityTransforms(stack,
            containing == null ? null : containing.renderPose(partialTicks).orientation(),
            rotation, eyeOffset, interpolationOffset);
        Vec3 offset = anchor.subtract(dispatcherOrigin(hook, partialTicks));
        stack.translate(offset.x, offset.y, offset.z);
    }

    public static Vec3 getWorldHandPosition(Player player, Vec3 vanillaHand, float partialTicks) {
        Vec3 eye = Sable.HELPER.getEyePositionInterpolated(player, partialTicks);
        Vec3 offset = vanillaHand.subtract(player.getEyePosition(partialTicks));
        var containing = Sable.HELPER.getContainingClient(player);
        if (containing != null) eye = containing.renderPose(partialTicks).transformPosition(eye);
        var rotation = EntitySubLevelUtil.shouldKick(player)
            ? EntitySubLevelRotationHelper.getEntityOrientation(player,
                ship -> ((ClientSubLevel) ship).renderPose(partialTicks), partialTicks,
                EntitySubLevelRotationHelper.Type.ENTITY) : null;
        if (rotation != null) {
            var vector = rotation.transform(new org.joml.Vector3d(offset.x, offset.y, offset.z));
            offset = new Vec3(vector.x, vector.y, vector.z);
        }
        return eye.add(offset);
    }
}
