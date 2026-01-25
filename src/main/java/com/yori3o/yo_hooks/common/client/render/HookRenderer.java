package com.yori3o.yo_hooks.common.client.render;

import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.item.HookItem;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;



public class HookRenderer extends EntityRenderer<HookEntity, HookRendererState> {
    
    ItemModelResolver resolver = Minecraft.getInstance().getItemModelResolver();


    public HookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected boolean affectedByCulling(HookEntity hookEntity) {
        return false;
    }

    @Override
    public HookRendererState createRenderState() {
        return new HookRendererState(); 
    }

    /*private void vertex(VertexConsumer vertexConsumer, PoseStack.Pose matrix, float x, float y, float z, float u, float v, int packedLight) {
        vertexConsumer.addVertex(matrix.pose(), x, y, z)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(matrix, 0.0f, 1.0f, 0.0f); // 0 1 0
    }*/


    public Vec3 getHandPosition(Player player, float partialTicks, EntityRenderDispatcher dispatcher) {
    
        int armSign = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        ItemStack itemStack = player.getMainHandItem();
        if (!(itemStack.getItem() instanceof HookItem)) {
            if (player.getOffhandItem().getItem() instanceof HookItem) {
                armSign = -armSign;
            } else {
                return null;
            }
        }

        if (dispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            double fovScale = 960.0D / (double)dispatcher.options.fov().get();
            float f = Mth.sin(Mth.sqrt(player.getAttackAnim(partialTicks)) * 3.1415927F);
            Vec3 vec3 = dispatcher.camera.getNearPlane().getPointOnPlane((float)armSign * 0.825F, -0.5F).scale(fovScale).yRot(f * 0.5F).xRot(-f * 0.7F);

            return player.getEyePosition(partialTicks).add(vec3);
            
        } else {
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



    @Override
    public void extractRenderState(HookEntity hookEntity, HookRendererState state, float partialTicks) {
        super.extractRenderState(hookEntity, state, partialTicks);

        if (hookEntity == null) {
            state.shouldRender = false;
            return;
        }
        state.shouldRender = true;

        Player player = hookEntity.getPlayerOwner();
        if (player == null) {
            state.shouldRender = false;
            return;
        }
        state.shouldRender = true;

        // 0. Базовые данные
        state.level = hookEntity.level();
        state.id = hookEntity.getId();
        state.packedLight = LevelRenderer.getLightColor(
            hookEntity.level(),
            player.blockPosition()
        );
        

        // 1. Считаем позиции (используем lerp для плавности)
        Vec3 handPos = this.getHandPosition(player, partialTicks, this.entityRenderDispatcher);
        if (handPos == null) {
            state.shouldRender = false;
            return;
        }
        state.shouldRender = true;
        
        Vec3 hookPos = new Vec3(
            Mth.lerp(partialTicks, hookEntity.xo, hookEntity.getX()),
            Mth.lerp(partialTicks, hookEntity.yo, hookEntity.getY()) + hookEntity.getEyeHeight(),
            Mth.lerp(partialTicks, hookEntity.zo, hookEntity.getZ())
        );

        // 2. Расчеты векторов
        Vec3 vectorCable = handPos.subtract(hookPos);
        state.length = (float)(vectorCable.length() + 0.1);
        Vec3 normalized = vectorCable.normalize();
        state.pitch = (float)Math.acos(normalized.y);
        state.yawAngle = (float)Math.atan2(normalized.z, normalized.x);
        
        // 3. Предмет
        state.itemStack = hookEntity.getHeadItem();
        resolver.updateForTopItem(
            state.itemRenderState,
            state.itemStack,
            ItemDisplayContext.GROUND,
            state.level,
            null,
            state.id
        );

    }



    @Override
    public void submit(HookRendererState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!state.shouldRender) return;

        if (!cameraState.initialized) return;

        poseStack.pushPose();

        // Повороты (используем данные из state)
        poseStack.mulPose(Axis.YP.rotationDegrees((1.5707964f - state.yawAngle) * Mth.RAD_TO_DEG));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.pitch * Mth.RAD_TO_DEG));

        // --- Рендер предмета ---
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(135.0f));
        poseStack.translate(-0.07f, -0.055f, 0f);


        //MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource(); 
        int packedLight = state.packedLight;
        state.itemRenderState.submit(
            poseStack,
            collector,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            0
        );

        poseStack.popPose();

        // --- Рендер цепи (вершины) ---
        float ropeAB = state.length * 2.5f - 1.0f;
        float ropeAA = -1.0f;


        /*VertexConsumer vertexConsumer = bufferSource.getBuffer(
            RenderTypes.entityCutoutNoCull(Identifier.fromNamespaceAndPath("yo_hooks", "textures/entity/hook_rope.png"))
        );
        PoseStack.Pose entry = poseStack.last();*/

        collector.submitCustomGeometry(
            poseStack,
            RenderTypes.entityCutoutNoCull(Identifier.fromNamespaceAndPath("yo_hooks", "textures/entity/hook_rope.png")),
            new HookCustomGeometryRenderer(state.length, ropeAA, ropeAB, packedLight)
        );

        // 6. Расчет координат вершин (Константы - p, q, r, s, t, u, v, w)
        /*float x1 = 0.1f * Mth.cos((float)((float)Math.PI));
        float z1 = 0.1f * Mth.sin((float)((float)Math.PI));

        float x2 = 0.1f * Mth.cos((float)(0.0f));
        float z2 = 0.1f * Mth.sin((float)(0.0f));

        float x3 = 0.1f * Mth.cos((float)(1.5707964f));
        float z3 = 0.1f * Mth.sin((float)(1.5707964f));

        float x4 = 0.1f * Mth.cos((float)(4.712389f));
        float z4 = 0.1f * Mth.sin((float)(4.712389f));

        // 8. Код отрисовки 8 вершин (HookRenderer.vertex)
        vertex(vertexConsumer, entry, x1, state.length, z1, 0.4999f, ropeAB, packedLight );
        vertex(vertexConsumer, entry, x1, 0.0f, z1, 0.4999f, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x2, 0.0f, z2, 0.0f, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x2, state.length, z2, 0.0f, ropeAB, packedLight );

        vertex(vertexConsumer, entry, x3, state.length, z3, 1, ropeAB, packedLight );
        vertex(vertexConsumer, entry, x3, 0.0f, z3, 1, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x4, 0.0f, z4, 0.4999f, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x4, state.length, z4, 0.4999f, ropeAB, packedLight );*/

        poseStack.popPose();

        super.submit(state, poseStack, collector, cameraState);
    }
}