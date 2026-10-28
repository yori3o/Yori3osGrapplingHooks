//? if >=26.1 {
package com.yori3o.yo_hooks.common.client.render;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.entity.player.Player;
public final class RenderAccess {
    public static void rotateDegrees(PoseStack poses, Axis axis, float angle) {
        //? if >=26.3 {
        poses.rotateDegrees(axis, angle);
        //? } else {
        /*poses.mulPose(axis.rotationDegrees(angle));
        *///? }
    }
    public static void rotate(PoseStack poses, Axis axis, float angle) {
        //? if >=26.3 {
        poses.rotate(axis, angle);
        //? } else {
        /*poses.mulPose(axis.rotation(angle));
        *///? }
    }
    public static float swing(Player player, float partialTicks) {
        //? if >=26.3 {
        return player.getSwingAnimation(partialTicks);
        //? } else {
        /*return player.getAttackAnim(partialTicks);
        *///? }
    }
    public static int light(Player player) {
        //? if >=26.2 {
        return net.minecraft.util.LightCoordsUtil.getLightCoords(player.level(), player.blockPosition());
        //? } else {
        /*return net.minecraft.client.renderer.LevelRenderer.getLightCoords(player.level(), player.blockPosition());
        *///? }
    }
}
//? }
