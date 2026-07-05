package com.yori3o.yo_hooks.common.client.vr;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.vivecraft.api.client.HeldInteractModule;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPart;

import com.yori3o.yo_hooks.common.config.categories.VrConfig;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.network.ClientSender;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class HandInteractModule implements HeldInteractModule {

    public static final HandInteractModule INSTANCE = new HandInteractModule();
    
    private static final Identifier ID = Identifier.fromNamespaceAndPath("yo_hooks", "vr_hand");
    private static final double DDIFF_THRESHOLD = 0.01;
    private static final int HAPTIC_COOLDOWN = 5;

    private Optional<Double> diffPrev = Optional.empty();
    private Optional<Vec3> rememberedHookHandPosition = Optional.empty();
    private int hapticCooldown = 0;

    private HandInteractModule() {}

    private static boolean isFreeHandInCone(Vec3 freeHandPos, Vec3 hookHandPos, Vec3 coneDirection) {
        double coneAngleRadians = Math.PI / 3;
        double halfAngleCos = Math.cos(coneAngleRadians / 2.0);

        Vec3 v = freeHandPos.subtract(hookHandPos);
        Vec3 coneDirectional = coneDirection.subtract(hookHandPos);

        double dSquaredLength = coneDirectional.lengthSqr();
        if (dSquaredLength == 0) {
            return false;
        }

        double dotProduct = v.dot(coneDirectional);
        if (dotProduct < 0) {
            return false;
        }

        double vSquaredLength = v.lengthSqr();
        double minDotProductSquared = vSquaredLength * dSquaredLength * (halfAngleCos * halfAngleCos);
        
        return (dotProduct * dotProduct) >= minDotProductSquared;
    }

    private static double diff(Vec3 movablePoint, Vec3 relativePoint, Vec3 relativeDirection) {
        Vec3 directional = relativeDirection.subtract(relativePoint);
        Vec3 v = movablePoint.subtract(relativePoint);
        return v.dot(directional.normalize());
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public int getPriority() {
        return 2000;
    }

    @Override
    public boolean isActive(LocalPlayer player, InteractionHand hand, Vec3 handPosition) {
        HandTracker ht = HandTracker.INSTANCE;

        Vec3 mainHandPosition;
        Vec3 offHandPosition;
        try {
            mainHandPosition = ht.getMainHandPosition().orElseThrow();
            offHandPosition = ht.getOffHandPosition().orElseThrow();
        } catch (NoSuchElementException e) {
            return false;
        }

        if (player.getItemInHand(hand) != ItemStack.EMPTY) {
            return false;
        }
        
        if (hand == InteractionHand.MAIN_HAND) {
            Vec3 direction = ht.getHookHeadPosition().orElseGet(() -> offHandPosition.add(Vec3.Y_AXIS));
            return isFreeHandInCone(mainHandPosition, offHandPosition, direction);
        } else {
            Vec3 direction = ht.getHookHeadPosition().orElseGet(() -> mainHandPosition.add(Vec3.Y_AXIS));
            return isFreeHandInCone(offHandPosition, mainHandPosition, direction);
        }
    }

    @Override
    public boolean onPress(LocalPlayer player, InteractionHand hand) {
        if (VrConfig.HANDLER.instance().rememberHookHandPosition) {
            HandTracker ht = HandTracker.INSTANCE;
            try {
                Vec3 mainHandPosition = ht.getMainHandPosition().orElseThrow();
                Vec3 offHandPosition = ht.getOffHandPosition().orElseThrow();
                if (hand == InteractionHand.MAIN_HAND) {
                    this.rememberedHookHandPosition = Optional.of(offHandPosition);
                } else {
                    this.rememberedHookHandPosition = Optional.of(mainHandPosition);
                }
            } catch (NoSuchElementException e) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public boolean swingsArm() {
        return false;
    }

    @Override
    public boolean onHoldTick(LocalPlayer player, InteractionHand hand) {
        if (!HandTracker.INSTANCE.isActive(player)) {
            return false;
        }

        HandTracker ht = HandTracker.INSTANCE;
        Vec3 freeHandPosition;
        Vec3 hookHandPosition;
        VRBodyPart vrHand;
        try {
            Vec3 mainHandPosition = ht.getMainHandPosition().orElseThrow();
            Vec3 offHandPosition = ht.getOffHandPosition().orElseThrow();
            if (hand == InteractionHand.MAIN_HAND) {
                vrHand = VRBodyPart.MAIN_HAND;
                freeHandPosition = mainHandPosition;
                hookHandPosition = offHandPosition;
            } else {
                vrHand = VRBodyPart.OFF_HAND;
                freeHandPosition = offHandPosition;
                hookHandPosition = mainHandPosition;
            }
        } catch (NoSuchElementException e) {
            return false;
        }
        
        Vec3 usedHookHandPosition = this.rememberedHookHandPosition.orElse(hookHandPosition);
        Vec3 hookHeadDirection = ht.getHookHeadPosition().orElseGet(() -> usedHookHandPosition.add(Vec3.Y_AXIS));
        double diff = diff(freeHandPosition, usedHookHandPosition, hookHeadDirection);

        double diffPrev;
        try {
            diffPrev = this.diffPrev.orElseThrow();
        } catch (NoSuchElementException e) {
            this.diffPrev = Optional.of(diff);
            return true;
        }

        PlayerWithHookData hookData = (PlayerWithHookData)player;
        HookEntity hook = hookData.getHook();
        int agilityLevel = hook.getAgilityLevel();
        double ddiff = diffPrev - diff;
        if (ddiff > DDIFF_THRESHOLD) { // up
            ClientSender.climb(true, agilityLevel, false); // Vibrate controller instead of playing sound
            hookData.setClimbing(true, agilityLevel);
        } else if (ddiff < -DDIFF_THRESHOLD) { // down
            ClientSender.climb(false, agilityLevel, false);
            hookData.setClimbing(false, 0);
        } else {
            this.hapticCooldown = HAPTIC_COOLDOWN;
            hookData.setClimbing(false, 0);
        }

        if (this.hapticCooldown == 0) {
            VRClientAPI.instance().triggerHapticPulse(vrHand, .05f, 80, .5f, .0f);
            this.hapticCooldown = HAPTIC_COOLDOWN;
        }
        this.hapticCooldown--;

        this.diffPrev = Optional.of(diff);

        return true;
    }

    @Override
    public void onRelease(LocalPlayer player, InteractionHand hand) {
        this.diffPrev = Optional.empty();
        this.rememberedHookHandPosition = Optional.empty();
    }
}