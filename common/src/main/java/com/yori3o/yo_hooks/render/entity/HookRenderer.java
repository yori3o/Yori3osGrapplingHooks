package com.yori3o.yo_hooks.render.entity;

import com.yori3o.yo_hooks.entity.HookEntity;
import com.yori3o.yo_hooks.item.HookItem;
import com.yori3o.yo_hooks.utils.HooksAttributes;

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
        
        if (player == null) {
            return;
        }

        //HookUtil.setArmRenderContext(hookEntity, player.getMainArm());

        poseStack.pushPose(); // Начало рендера

        // 1. Позиция руки (Начало цепи)
        Vec3 handPos = this.getHandPosition(player, partialTicks, this.entityRenderDispatcher);
        //Vec3 handPos = this.getArmWorldPos(hookEntity);

        // 2. Позиция крюка (Конец цепи)
        Vec3 hookPos = new Vec3(
            Mth.lerp((double)partialTicks, hookEntity.xo, hookEntity.getX()), 
            Mth.lerp((double)partialTicks, hookEntity.yo, hookEntity.getY()) + (double)hookEntity.getEyeHeight(), 
            Mth.lerp((double)partialTicks, hookEntity.zo, hookEntity.getZ())
        );

        // 3. Расчет вектора, длины и углов
        Vec3 vectorCable = handPos.subtract(hookPos);
        float length = (float)(vectorCable.length() + 0.1);
        vectorCable = vectorCable.normalize();
        float pitch = (float)Math.acos(vectorCable.y);
        float yawAngle = (float)Math.atan2(vectorCable.z, vectorCable.x);


        // 4. Повороты PoseStack (Критично!)
        poseStack.mulPose(Axis.YP.rotationDegrees((1.5707964f - yawAngle) * Mth.RAD_TO_DEG));
        poseStack.mulPose(Axis.XP.rotationDegrees(pitch * Mth.RAD_TO_DEG));


        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(135.0f));
        poseStack.translate(-0.125f, 0f, 0f);


        // 5. Рендер навершия крюка (Item)
        ItemStack hookStack = HooksAttributes.hookStack(hookEntity.getMaxRange());
        BakedModel model = this.itemRenderer.getModel(hookStack, hookEntity.level(), null, 0);

        this.itemRenderer.render(
            hookStack,
            ItemDisplayContext.GROUND,
            false, // leftHand
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

        //x, y, z, aa, ab
        float ropeLength = length; 
        float ropeAA = -1.0f;
        float ropeAB = length * 2.5f + ropeAA;


        // 7. Получение буфера и отрисовка вершин
        VertexConsumer vertexConsumer = bufferSource.getBuffer(
            RenderType.entityCutoutNoCull(ResourceLocation.fromNamespaceAndPath("yo_hooks", "textures/entity/hook_rope.png"))
        );
        PoseStack.Pose entry = poseStack.last();


        // 8. Ваш код отрисовки 8 вершин (HookRenderer.vertex)
        vertex(vertexConsumer, entry, x1, ropeLength, z1, 0.4999f, ropeAB, packedLight );
        vertex(vertexConsumer, entry, x1, 0.0f, z1, 0.4999f, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x2, 0.0f, z2, 0.0f, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x2, ropeLength, z2, 0.0f, ropeAB, packedLight );

        vertex(vertexConsumer, entry, x3, ropeLength, z3, 1, ropeAB, packedLight );
        vertex(vertexConsumer, entry, x3, 0.0f, z3, 1, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x4, 0.0f, z4, 0.4999f, ropeAA, packedLight );
        vertex(vertexConsumer, entry, x4, ropeLength, z4, 0.4999f, ropeAB, packedLight );
    
        poseStack.popPose();
        super.render(hookEntity, yaw, partialTicks, poseStack, bufferSource, packedLight);

        //HookUtil.clearArmRenderContext();
    }

    
    private void vertex(VertexConsumer vertexConsumer, PoseStack.Pose matrix, float x, float y, float z, float u, float v, int packedLight) {
        vertexConsumer.addVertex(matrix.pose(), x, y, z)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(packedLight)
            .setNormal(matrix, 0.0f, 1.0f, 0.0f);
    }




    public Vec3 getHandPosition(Player player, float partialTicks, EntityRenderDispatcher dispatcher) {
    
        // 1. Определение используемой руки и знака (Ваш код)
        boolean isMainHand = player.getMainHandItem().getItem() instanceof HookItem;
        boolean isOffHand = player.getOffhandItem().getItem() instanceof HookItem;
        HumanoidArm hookArm = isMainHand ? player.getMainArm() :
                isOffHand ? player.getMainArm().getOpposite() :
                player.getMainArm();

        int armSign = hookArm == HumanoidArm.RIGHT ? 1 : -1;
        
        // --- 2. Общий расчет и интерполяция ---
        
        float bodyYawRad = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * Mth.DEG_TO_RAD; // h
        
        double sinYaw = Mth.sin(bodyYawRad); // d
        double cosYaw = Mth.cos(bodyYawRad); // e
        
        double sideOffset = (double)armSign * 0.35; // j (смещение вбок)
        double forwardOffset = 0.4; // смещение вперед

        // --- 3. Логика от Третьего Лица (TPP) ---
        if (!dispatcher.options.getCameraType().isFirstPerson() || player != Minecraft.getInstance().player) {
            float sneakOffset = player.isCrouching() ? -0.1875f : 0.0f;
            
            // X = X_интерп - cos(Yaw) * sideOffset - sin(Yaw) * forwardOffset
            double x = Mth.lerp(partialTicks, player.xo, player.getX()) - cosYaw * sideOffset - sinYaw * forwardOffset;
            
            // Z = Z_интерп - sin(Yaw) * sideOffset + cos(Yaw) * forwardOffset
            double z = Mth.lerp(partialTicks, player.zo, player.getZ()) - sinYaw * sideOffset + cosYaw * forwardOffset;

            // Y = Y_интерп + EyeHeight - 0.55 + SneakOffset
            double y = Mth.lerp(partialTicks, player.yo, player.getY()) + (double)player.getEyeHeight() - 0.55 + (double)sneakOffset;
            
            return new Vec3(x, y, z);
        }

        // --- 4. Логика от Первого Лица (FPP) ---
        // Масштабирование FOV (m)
        double fovScale = 960.0 / (double)dispatcher.options.fov().get();

        Vec3 lookVector = new Vec3(dispatcher.camera.getLookVector());

        // Вектор, направленный ВПРАВО (перпендикулярно LookVector)
        // Используем Cross Product (векторное произведение) LookVector x WorldUp (0, 1, 0)
        Vec3 rightVector = lookVector.cross(new Vec3(0, 1, 0)).normalize();

        // Вектор, направленный ВВЕРХ (перпендикулярно LookVector и RightVector)
        Vec3 upVector = rightVector.cross(lookVector).normalize();

        // 4. Комбинируем векторы со стандартными смещениями (0.525f и -0.1f)
        // 0.525f — смещение вправо/влево, -0.1f — смещение вниз.
        Vec3 projectedVec = rightVector.scale((double)armSign * 0.055).add(
            upVector.scale(-0.025)
        ).scale(fovScale); // Применяем масштабирование FOV ко всей проекции.

        // 5. Вращения (как было):
        // Note: sinSwing - это g из оригинального кода, должно быть float
        float sinSwing = Mth.sin(Mth.sqrt(player.getAttackAnim(partialTicks)) * (float)Math.PI);
        
        // Применение анимации взмаха
        projectedVec = projectedVec.yRot(sinSwing * 0.5f);
        projectedVec = projectedVec.xRot(-sinSwing * 0.7f);

        // Финальная позиция = Интерполированная позиция ног + смещение FPP + EyeHeight
        return new Vec3(
            Mth.lerp(partialTicks, player.xo, player.getX()) + projectedVec.x, 
            Mth.lerp(partialTicks, player.yo, player.getY()) + projectedVec.y + (double)player.getEyeHeight(), 
            Mth.lerp(partialTicks, player.zo, player.getZ()) + projectedVec.z
        );
    }


    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(HookEntity hookEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}