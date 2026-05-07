package com.yori3o.yo_hooks.common.client.render;


import com.yori3o.yo_hooks.common.compat.Compats;
import com.yori3o.yo_hooks.common.compat.sable.HookWithSableData;
import com.yori3o.yo_hooks.common.compat.sable.SableCompat;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.hookregistry.HookRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3d;

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

        Vec3 handPos = getHandPosition(player, partialTicks, this.entityRenderDispatcher);

        if (handPos == null) {
            return;
        }

        Vec3 hookPos = hookEntity.getPosition(partialTicks).add(0.0D, 0.25D, 0.0D);
        if (Compats.isSableLoaded) {
            HookWithSableData data = SableCompat.hooks.get(hookEntity);
            if (data != null) {
                if (data.attachedShip == null) return;
                Vec3 physicsPos = data.attachedShip.logicalPose().transformPosition(data.localAttachPos);
                hookPos = SableCompat.projectOutOfSubLevel(hookEntity.level(), physicsPos);
            }
        }

        Quaterniond inv = null;

        if (Compats.isSableLoaded) {
            HookWithSableData data = SableCompat.hooks.get(hookEntity);
            if (data != null && data.attachedShip != null) {
                Quaterniond shipRot = data.attachedShip.logicalPose().orientation();
                inv = new Quaterniond(shipRot).invert();
            }
        }

        Vec3 vectorCable = handPos.subtract(hookPos);
        if (inv != null) vectorCable = rotateVec(vectorCable, inv);
        float length = (float)(vectorCable.length());
        vectorCable = vectorCable.normalize();
        float pitch = (float)Math.acos(vectorCable.y);
        float yawAngle = (float)Math.atan2(vectorCable.z, vectorCable.x);

        poseStack.pushPose();

        if (inv != null) poseStack.mulPose(new Quaternionf((float)inv.x, (float)inv.y, (float)inv.z, (float)inv.w));

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

    public static Vec3 rotateVec(Vec3 v, Quaterniond q) {
        Vector3d vec = new Vector3d(v.x, v.y, v.z);
        vec.rotate(q);
        return new Vec3(vec.x, vec.y, vec.z);
    }


    
    private void vertex(VertexConsumer vertexConsumer, PoseStack.Pose matrix, float x, float y, float z, float u, float v, int packedLight) {
        vertexConsumer.addVertex(matrix.pose(), x, y, z)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(matrix, 0.0f, 1.0f, 0.0f);
    }




    public static final Vec3 getHandPosition(Player player, float partialTicks, EntityRenderDispatcher dispatcher) {
        int armSign = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack itemStack = player.getMainHandItem();
        if (!(itemStack.getItem() instanceof HookItem)) {
            if (player.getOffhandItem().getItem() instanceof HookItem) {
                armSign = -armSign;
            } else {
                return null;
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