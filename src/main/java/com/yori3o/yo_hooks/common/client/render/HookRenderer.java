package com.yori3o.yo_hooks.common.client.render;


import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.hookregistry.HookRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.impl.PlatformUtil;

import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPartData;
import org.vivecraft.api.data.VRPose;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
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


    @Override
    public void extractRenderState(HookEntity hookEntity, HookRendererState state, float partialTicks) {
        super.extractRenderState(hookEntity, state, partialTicks);

        if (hookEntity == null) {
            state.shouldRender = false;
            return;
        }

        Player player = hookEntity.getPlayerOwner();
        if (player == null) {
            state.shouldRender = false;
            return;
        }

        state.lightCoords = LightCoordsUtil.getLightCoords(
            player.level(),
            player.blockPosition()

        );

        Vec3 handPos = HookRenderer.getHandPosition(player, partialTicks, this.entityRenderDispatcher);
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

        Vec3 vectorCable = handPos.subtract(hookPos);
        state.length = (float)(vectorCable.length() + 0.1);
        Vec3 normalized = vectorCable.normalize();
        state.pitch = (float)Math.acos(normalized.y);
        state.yawAngle = (float)Math.atan2(normalized.z, normalized.x);
        
        resolver.updateForTopItem(
            state.itemRenderState,
            hookEntity.getHeadItem(),
            ItemDisplayContext.GROUND,
            hookEntity.level(),
            null,
            hookEntity.getId()
        );

        String hookMaterial = hookEntity.getHookItemMaterial();
        if (HookRegistry.hookMaterialsWithCustomVisuals.contains(hookMaterial)) {
            state.ropeTexture = Identifier.fromNamespaceAndPath("yo_hooks", "textures/entity/hook_rope_" + hookMaterial + ".png");
        } else {
            state.ropeTexture = Identifier.fromNamespaceAndPath("yo_hooks", "textures/entity/hook_rope.png");
        }

    }


    @Override
    public void submit(HookRendererState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!state.shouldRender) return;

        if (!cameraState.initialized) return;

        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees((1.5707964f - state.yawAngle) * Mth.RAD_TO_DEG));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.pitch * Mth.RAD_TO_DEG));

        // item render (hook head)
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(135.0f));
        poseStack.translate(-0.07f, -0.055f, 0f);

        int lightCoords = state.lightCoords;
        state.itemRenderState.submit(
            poseStack,
            collector,
            lightCoords,
            OverlayTexture.NO_OVERLAY,
            0
        );

        poseStack.popPose();

        // rope
        collector.submitCustomGeometry(
            poseStack,
            RenderTypes.entityCutout(state.ropeTexture),
            new HookCustomGeometryRenderer(state.length, state.length * 2.5f - 1.0f, lightCoords)
        );

        poseStack.popPose();

        super.submit(state, poseStack, collector, cameraState);
    }


    public static final Vec3 getHandPosition(Player player, float partialTicks, EntityRenderDispatcher dispatcher) {
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

        if (player == Minecraft.getInstance().player
                && PlatformUtil.isModLoaded("vivecraft")
                && VRClientAPI.instance().isVRActive()
                && !VRClientAPI.instance().isSeated()) {
            VRPose vrPose = VRClientAPI.instance().getWorldRenderPose();
            VRBodyPartData vrHand = mainHandHoldsHook ? vrPose.getMainHand() : vrPose.getOffHand();
            Vec3 vrHandPos = vrHand.getPos(); // TODO: Нужно поэкспериментировать с подбором параметров, влияющих на начало цепи
            Vec3 vrHandDir = vrHand.getDir();
            if (vrHandPos != null && vrHandDir != null) {
                return vrHandPos.add(vrHandDir.scale(0.3f));
            }
        }

        // copied from vanilla fishing rod
        // --- first person view ---
        if (dispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            double fovScale = 960.0D / (double)dispatcher.options.fov().get();
            float f = Mth.sin(Mth.sqrt(player.getAttackAnim(partialTicks)) * 3.1415927F);
            Vec3 vec3 = dispatcher.camera.getNearPlane(dispatcher.options.fov().get()).getPointOnPlane((float)armSign * 0.825F, -0.5F).yRot(f * 0.5F).xRot(-f * 0.7F).scale(fovScale);

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
}