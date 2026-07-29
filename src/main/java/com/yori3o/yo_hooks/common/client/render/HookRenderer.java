package com.yori3o.yo_hooks.common.client.render;


import com.yori3o.yo_hooks.common.client.vr.HandTracker;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.hookregistry.HookRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.impl.PlatformUtil;
import com.yori3o.yo_hooks.common.compat.Compats;
import com.yori3o.yo_hooks.common.compat.sable.SableCompat;

import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPartData;
import org.vivecraft.api.data.VRPose;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;



public class HookRenderer extends EntityRenderer<HookEntity> {
    

    private final ItemRenderer itemRenderer;


    public HookRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }


    @Override
    public void render(HookEntity hookEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Player player = hookEntity.getPlayerOwner(); 
        
        if (player == null) return;

        boolean isVR = player == Minecraft.getInstance().player
            && PlatformUtil.isModLoaded("vivecraft")
            && VRClientAPI.instance().isVRActive()
            && !VRClientAPI.instance().isSeated();

        Vec3 handPos = HookRenderer.getHandPosition(player, partialTicks, this.entityRenderDispatcher, isVR);
        if (handPos == null) {
            return;
        }
        
        Vec3 hookPos = new Vec3(
            Mth.lerp(partialTicks, hookEntity.xo, hookEntity.getX()),
            Mth.lerp(partialTicks, hookEntity.yo, hookEntity.getY()) + (isVR ? 0 : hookEntity.getEyeHeight()),
            Mth.lerp(partialTicks, hookEntity.zo, hookEntity.getZ())
        );
        if (Compats.isSableLoaded) {
            hookPos = SableCompat.getGlobalPositionOfHookEntity(hookPos, hookEntity);
        }

        Quaterniond sableOffset = null;
        if (Compats.isSableLoaded) { sableOffset = SableCompat.getGlobalRotationOfSubLevel(hookEntity); }

        Vec3 vectorCable = handPos.subtract(hookPos);
        if (sableOffset != null) vectorCable = rotateVec(vectorCable, sableOffset);
        float length = (float)(vectorCable.length()) + (isVR ? 0 : .1f);
        Vec3 normalized = vectorCable.normalize();
        float pitch = (float)Math.acos(normalized.y);
        float yawAngle = (float)Math.atan2(normalized.z, normalized.x);

        poseStack.pushPose();
        
        if (sableOffset != null) poseStack.mulPose(new Quaternionf(sableOffset));

        poseStack.mulPose(Axis.YP.rotationDegrees((1.5707964f - yawAngle) * Mth.RAD_TO_DEG));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch * Mth.RAD_TO_DEG));

        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(135.0f));
        poseStack.translate(-0.07f, -0.055f, 0f);


        ItemStack hookStack = hookEntity.getHeadItem();

        this.itemRenderer.renderStatic(
            hookStack,
            ItemDisplayContext.GROUND,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            bufferSource,
            hookEntity.level(),
            hookEntity.getId()
        );

        poseStack.popPose();

        float ropeAB = length * 2.5f + (length * 2.5f - 1.0f);

        String hookMaterial = hookEntity.getHookItemMaterial();
        ResourceLocation f;
        if (HookRegistry.hookMaterialsWithCustomVisuals.contains(hookMaterial)) {
            f = ResourceLocation.fromNamespaceAndPath("yo_hooks", "textures/entity/hook_rope_" + hookMaterial + ".png");
        } else {
            f = ResourceLocation.fromNamespaceAndPath("yo_hooks", "textures/entity/hook_rope.png");
        }
        VertexConsumer vertexConsumer = bufferSource.getBuffer(
            RenderType.entityCutoutNoCull(f)
        );
        PoseStack.Pose entry = poseStack.last();

        vertex(vertexConsumer, entry, -0.1f, length, 0, 0.5f, ropeAB, packedLight);
        vertex(vertexConsumer, entry, -0.1f, 0,0, 0.5f, -1.0f, packedLight);
        vertex(vertexConsumer, entry, 0.1f, 0, 0, 0, -1.0f, packedLight);
        vertex(vertexConsumer, entry, 0.1f, length, 0, 0, ropeAB, packedLight);

        vertex(vertexConsumer, entry, 0, length, 0.1f, 1, ropeAB, packedLight);
        vertex(vertexConsumer, entry, 0, 0, 0.1f, 1, -1.0f, packedLight);
        vertex(vertexConsumer, entry, 0, 0, -0.1f, 0.5f, -1.0f, packedLight);
        vertex(vertexConsumer, entry, 0, length, -0.1f, 0.5f, ropeAB, packedLight);
    
        poseStack.popPose();
        super.render(hookEntity, yaw, partialTicks, poseStack, bufferSource, packedLight);
    }


    private void vertex(VertexConsumer vertexConsumer, PoseStack.Pose matrix, float x, float y, float z, float u, float v, int packedLight) {
        vertexConsumer.addVertex(matrix.pose(), x, y, z)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(matrix, 0.0f, 1.0f, 0.0f);
    }

    public static Vec3 rotateVec(Vec3 v, Quaterniond q) {
        Vector3d vec = new Vector3d(v.x, v.y, v.z);
        vec.rotate(q);
        return new Vec3(vec.x, vec.y, vec.z);
    }

    public static final Vec3 getHandPosition(Player player, float partialTicks, EntityRenderDispatcher dispatcher, boolean isVR) {
        int armSign = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack itemStack = player.getMainHandItem();
        boolean mainHandHoldsHook = itemStack.getItem() instanceof HookItem;
        if (!mainHandHoldsHook) {
            if (player.getOffhandItem().getItem() instanceof HookItem) {
                armSign = -armSign;
            } else {
                return null;
            }
        }

        if (isVR) {
            VRPose vrPose = VRClientAPI.instance().getWorldRenderPose();
            VRBodyPartData vrHand = mainHandHoldsHook ? vrPose.getMainHand() : vrPose.getOffHand();
            if (vrHand != null) {
                return HandTracker.getChainStartWorld(vrHand);
            }
        }

        // copied from vanilla fishing rod
        // --- first person view ---
        if (dispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            double fovScale = 960.0D / (double)dispatcher.options.fov().get();
            float f = Mth.sin(Mth.sqrt(player.getAttackAnim(partialTicks)) * 3.1415927F);
            Vec3 vec3 = dispatcher.camera.getNearPlane().getPointOnPlane((float)armSign * 0.825F, -0.5F).scale(fovScale).yRot(f * 0.5F).xRot(-f * 0.7F);

            return player.getEyePosition(partialTicks).add(vec3);
            
        } else { // --- third person view ---
            float h = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * 0.017453292F;
            double d = (double)Mth.sin(h);
            double e = (double)Mth.cos(h);
            float j = player.getScale();
            double k = (double)armSign * 0.35D * (double)j;
            double l = 0.4D * (double)j;
            float m = player.isCrouching() ? -0.1875F : 0.0F;
            return player.getEyePosition(partialTicks).add(-e * k - d * l, (double)m - 0.55 * (double)j, -d * k + e * l);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(HookEntity hookEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}