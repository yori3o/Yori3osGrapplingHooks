package com.yori3o.yo_hooks.render.entity;

import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.item.HookItem;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import org.jetbrains.annotations.Nullable;



public class HookRenderer extends EntityRenderer<HookEntity> {
    
    private final ItemRenderer itemRenderer;


    public HookRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(HookEntity hookEntity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        @Nullable Player player = hookEntity.getPlayerOwner(); 
        
        if (player != null) {
            
            /*Vec3 testPos = this.getHandPosition(player, partialTicks, this.entityRenderDispatcher);
            // Переводим мировые координаты точки в локальные координаты рендера сущности
            double tx = testPos.x - Mth.lerp(partialTicks, hookEntity.xo, hookEntity.getX());
            double ty = testPos.y - Mth.lerp(partialTicks, hookEntity.yo, hookEntity.getY());
            double tz = testPos.z - Mth.lerp(partialTicks, hookEntity.zo, hookEntity.getZ());

            poseStack.pushPose();
            poseStack.translate(tx, ty, tz);
            // Рисуем маленький красный квадрат
            VertexConsumer debugBuf = bufferSource.getBuffer(RenderType.debugQuads());
            Matrix4f mat = poseStack.last().pose();
            debugBuf.addVertex(mat, -0.03f, -0.03f, 0).setColor(100, 0, 0, 100);
            debugBuf.addVertex(mat, 0.03f, -0.03f, 0).setColor(255, 0, 0, 255);
            debugBuf.addVertex(mat, 0.03f, 0.03f, 0).setColor(255, 0, 0, 255);
            debugBuf.addVertex(mat, -0.03f, 0.03f, 0).setColor(255, 0, 0, 255);
            poseStack.popPose();*/

            

            // 1. Позиция руки (Начало цепи)
            Vec3 handPos = this.getHandPosition(player, partialTicks, this.entityRenderDispatcher);

            if (handPos == null) {
                return;
            }

            // 2. Позиция крюка (Конец цепи)
            Vec3 hookPos = hookEntity.getPosition(partialTicks).add(0.0D, 0.25D, 0.0D);

            // 3. Расчет вектора, длины и углов
            Vec3 vectorCable = handPos.subtract(hookPos);
            float length = (float)(vectorCable.length());
            vectorCable = vectorCable.normalize();
            float pitch = (float)Math.acos(vectorCable.y);
            float yawAngle = (float)Math.atan2(vectorCable.z, vectorCable.x);


            poseStack.pushPose();

            // 4. Повороты PoseStack (Критично!)
            poseStack.mulPose(Axis.YP.rotationDegrees((1.5707964f - yawAngle) * Mth.RAD_TO_DEG));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch * Mth.RAD_TO_DEG));


            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(135.0f));
            poseStack.translate(-0.07f, -0.055f, 0f);


            // 5. Рендер навершия крюка (Item)
            ItemStack hookStack = hookEntity.getHeadItem();
            BakedModel model = this.itemRenderer.getModel(hookStack, hookEntity.level(), null, 0);

            this.itemRenderer.render(
                hookStack,
                ItemDisplayContext.GROUND,
                false,
                poseStack,
                bufferSource,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                model
            );

            poseStack.popPose();


            // 6. Расчет координат вершин (Константы - p, q, r, s, t, u, v, w)
            float x1 = 0.1f * Mth.cos((float)((float)Math.PI));
            float z1 = 0.1f * Mth.sin((float)((float)Math.PI));

            float x2 = 0.1f * Mth.cos((float)(0.0f));
            float z2 = 0.1f * Mth.sin((float)(0.0f));

            float x3 = 0.1f * Mth.cos((float)(1.5707964f));
            float z3 = 0.1f * Mth.sin((float)(1.5707964f));

            float x4 = 0.1f * Mth.cos((float)(4.712389f));
            float z4 = 0.1f * Mth.sin((float)(4.712389f));

            float ropeAA = 1.0f;
            float ropeAB = length * 2.5f + ropeAA;


            // 7. Получение буфера и отрисовка вершин
            VertexConsumer vertexConsumer = bufferSource.getBuffer(
                RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath("yo_hooks", "textures/entity/hook_rope.png"))
            );
            PoseStack.Pose entry = poseStack.last();


            vertex(vertexConsumer, entry, x1, length, z1, 0.4999f, ropeAB, packedLight );
            vertex(vertexConsumer, entry, x1, 0.0f, z1, 0.4999f, ropeAA, packedLight );
            vertex(vertexConsumer, entry, x2, 0.0f, z2, 0.0f, ropeAA, packedLight );
            vertex(vertexConsumer, entry, x2, length, z2, 0.0f, ropeAB, packedLight );

            vertex(vertexConsumer, entry, x3, length, z3, 1, ropeAB, packedLight );
            vertex(vertexConsumer, entry, x3, 0.0f, z3, 1, ropeAA, packedLight );
            vertex(vertexConsumer, entry, x4, 0.0f, z4, 0.4999f, ropeAA, packedLight );
            vertex(vertexConsumer, entry, x4, length, z4, 0.4999f, ropeAB, packedLight );
        
            poseStack.popPose();
            super.render(hookEntity, yaw, partialTicks, poseStack, bufferSource, packedLight);
        }
    }


    
    private void vertex(VertexConsumer vertexConsumer, PoseStack.Pose matrix, float x, float y, float z, float u, float v, int packedLight) {
        vertexConsumer.addVertex(matrix.pose(), x, y, z)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(matrix, 0.0f, 1.0f, 0.0f);

        /*vertexConsumer.vertex(matrix.pose(), x, y, z) // FOR 1.20.1
            .color(255, 255, 255, 255)
            .uv(u, v)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(packedLight)
            .normal(matrix.normal(), 0.0f, 1.0f, 0.0f).endVertex();*/
    }




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

        // --- 3. Логика от Третьего Лица (TPP) ---
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




        // --- 4. Логика от Первого Лица (FPP) ---
        // Масштабирование FOV (m)
        
    }


    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(HookEntity hookEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}