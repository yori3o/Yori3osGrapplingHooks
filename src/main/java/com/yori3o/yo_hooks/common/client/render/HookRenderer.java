package com.yori3o.yo_hooks.common.client.render;


import com.yori3o.yo_hooks.common.client.vr.HandTracker;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.hookregistry.HookRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.util.interfaces.AvatarRendererAccess;
import com.yori3o.yo_hooks.impl.PlatformUtil;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPartData;
import org.vivecraft.api.data.VRPose;

import org.joml.Quaternionf;
import org.joml.Vector3f;



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

        boolean isVR = player == Minecraft.getInstance().player
            && PlatformUtil.isModLoaded("vivecraft")
            && VRClientAPI.instance().isVRActive()
            && !VRClientAPI.instance().isSeated();

        Vec3 handPos = HookRenderer.getHandPosition(player, partialTicks, this.entityRenderDispatcher, isVR);
        if (handPos == null) {
            state.shouldRender = false;
            return;
        }
        state.shouldRender = true;
        
        Vec3 hookPos = new Vec3(
            Mth.lerp(partialTicks, hookEntity.xo, hookEntity.getX()),
            Mth.lerp(partialTicks, hookEntity.yo, hookEntity.getY()) + (isVR ? 0 : hookEntity.getEyeHeight()),
            Mth.lerp(partialTicks, hookEntity.zo, hookEntity.getZ())
        );

        Vec3 vectorCable = handPos.subtract(hookPos);
        state.length = Math.max(0f, (float) (vectorCable.length() - 0.05f + (isVR ? 0 : 0.1f)));
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

        // --- first person view ---
        if (dispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
            double fovScale = 960.0D / (double)dispatcher.options.fov().get();
            float swing = Mth.sin((double)(Mth.sqrt(player.getAttackAnim(partialTicks)) * 3.1415927F));
            
            Vec3 baseVec = dispatcher.camera.getNearPlane(dispatcher.options.fov().get())
                    .getPointOnPlane((float) armSign * 0.825F, -0.5F)
                    .scale(fovScale); // without swing offset

            Quaternionf camRot = new Quaternionf(dispatcher.camera.rotation());
            Quaternionf camRotInv = new Quaternionf(camRot).conjugate();

            Vector3f v = new Vector3f((float) baseVec.x, (float) baseVec.y, (float) baseVec.z);

            v.rotate(camRotInv); // we switch to the camera's local space

            // the same swaying motion, but now in local axes
            Quaternionf swingRot = new Quaternionf()
                    .rotateY(swing * 0.5F)
                    .rotateX(-swing * 0.7F);
            v.rotate(swingRot);

            v.rotate(camRot); // back

            Vec3 vec3 = new Vec3(v.x, v.y, v.z);
            return player.getEyePosition(partialTicks).add(vec3);
            
        } else { // --- third person view ---

            AvatarRenderer renderer =
                (AvatarRenderer) Minecraft.getInstance()
                    .getEntityRenderDispatcher()
                    .getRenderer(player);

            PlayerModel model = (PlayerModel)renderer.getModel();

            AvatarRenderState state =
                    ((AvatarRendererAccess) renderer).yo_hooks$getState(player.getId());

            if (state == null) {
                state = (AvatarRenderState)renderer.createRenderState(player, partialTicks);
                model.setupAnim(state);
            }

            PoseStack poseStack = new PoseStack();

            // player position and rotation
            Vec3 pos = player.getPosition(partialTicks);
            poseStack.translate(pos.x, pos.y, pos.z);

            float bodyYaw = Mth.rotLerp(partialTicks, player.yBodyRotO, player.yBodyRot);
            if (!state.hasPose(Pose.SLEEPING)) {
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyYaw));
            }

            // lying rotations
            float swimAmount = state.swimAmount;
            if (state.isFallFlying) {
                float scale = state.fallFlyingScale();
                if (!state.isAutoSpinAttack) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(scale * (-90.0F - state.xRot)));
                }
                if (state.shouldApplyFlyingYRot) {
                    poseStack.mulPose(Axis.YP.rotation(state.flyingYRot));
                }
            } else if (swimAmount > 0.0F) {
                float xRot = state.xRot;
                float scale = state.isInWater ? -90.0F - xRot : -90.0F;
                float xAngle = Mth.lerp(swimAmount, 0.0F, scale);
                poseStack.mulPose(Axis.XP.rotationDegrees(xAngle));

                if (state.isVisuallySwimming) {
                    poseStack.translate(0.0F, -1.0F, 0.3F);
                }
            } else if (state.hasPose(Pose.SLEEPING)) {
                Direction bedOrientation = state.bedOrientation;
                float angle = bedOrientation != null ? sleepDirectionToRotation(bedOrientation) : bodyYaw;
                poseStack.mulPose(Axis.YP.rotationDegrees(angle));
                poseStack.mulPose(Axis.ZP.rotationDegrees(90f));
                poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            }
            
            poseStack.scale(-1.0F, -1.0F, 1.0F);
            poseStack.translate(0.0F, -1.501F, 0.0F);
            
            // translate and rotate to the hand and desired point
            model.translateToHand(state, armSign == 1 ? HumanoidArm.RIGHT : HumanoidArm.LEFT, poseStack);

            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

            float offsetX = 0.25F;
            float offsetY = 5.0F;
            float offsetZ = -5.8F;
            poseStack.translate((float)armSign * offsetX / 16.0F, offsetY / 16.0F, offsetZ / 16.0F);

            Vector3f translation = poseStack.last().pose().getTranslation(new Vector3f());
            Vec3 position = new Vec3(translation.x, translation.y, translation.z);

            return position;
        }
    }

    private static final float sleepDirectionToRotation(final Direction direction) {
      float var10000;
      switch(direction.ordinal()) {
      case 1:
         var10000 = 90.0F;
         break;
      case 2:
         var10000 = 0.0F;
         break;
      case 3:
         var10000 = 270.0F;
         break;
      case 4:
         var10000 = 180.0F;
         break;
      default:
         var10000 = 0.0F;
      }

      return var10000;
   }
}