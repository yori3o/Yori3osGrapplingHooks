package com.yori3o.yo_hooks.common.client.vr;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.vivecraft.api.client.HeldInteractModule;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPart;

import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.event.ClientEvents;
import com.yori3o.yo_hooks.common.network.ClientSender;
import com.yori3o.yo_hooks.common.util.PlayerWithHookData;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class HandInteractModule implements HeldInteractModule {

    public static final HandInteractModule INSTANCE = new HandInteractModule();
    
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("yo_hooks", "vr_hand");
    private static final double DDIFF_THRESHOLD = 0.01;
    private static final int HAPTIC_COOLDOWN = 5;

    private List<Optional<Double>> diffPrev = Arrays.asList(Optional.empty(), Optional.empty());
    private List<Optional<Vec3>> rememberedHookHandPosition = Arrays.asList(Optional.empty(), Optional.empty());
    private int hapticCooldown[] = {0, 0};

    private HandInteractModule() {}

    private static boolean isFreeHandInCone(Vec3 freeHandPos, Vec3 hookHandPos, Vec3 coneDirection) {
        double coneAngleRadians = Math.PI / 3;
        double halfAngleCos = Math.cos(coneAngleRadians / 2.0);

        Vec3 v = freeHandPos.subtract(hookHandPos);

        double dSquaredLength = coneDirection.lengthSqr();
        if (dSquaredLength == 0) {
            return false;
        }

        double dotProduct = v.dot(coneDirection);
        if (dotProduct < 0) {
            return false;
        }

        double vSquaredLength = v.lengthSqr();
        double minDotProductSquared = vSquaredLength * dSquaredLength * (halfAngleCos * halfAngleCos);
        
        return (dotProduct * dotProduct) >= minDotProductSquared;
    }

    private static Vec3 getDirection(Optional<Vec3> hookHeadPosition, Vec3 hookHandPosition) {
        return hookHeadPosition
            .orElseGet(() -> hookHandPosition.add(new Vec3(0.0D, 1.0D, 0.0D)))
            .subtract(hookHandPosition);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public int getPriority() {
        return 2000;
    }

    @Override
    public boolean isActive(LocalPlayer player, InteractionHand hand, Vec3 handPosition) {
        if (player.getItemInHand(hand) != ItemStack.EMPTY) {
            return false;
        }

        HandTracker ht = HandTracker.INSTANCE;

        Vec3 freeHandPosition;
        Vec3 hookHandPosition;
        try { // this is also a check that HandTracker is active
            freeHandPosition = ht.getFreeHandPosition().orElseThrow();
            hookHandPosition = ht.getHookHandPosition().orElseThrow();
        } catch (NoSuchElementException e) {
            return false;
        }
        
        Vec3 direction = getDirection(ht.getHookHeadPosition(), hookHandPosition);
        return isFreeHandInCone(freeHandPosition, hookHandPosition, direction);
    }

    @Override
    public boolean onPress(LocalPlayer player, InteractionHand hand) {
        return true;
    }

    @Override
    public boolean swingsArm() {
        return false;
    }

    @Override
    public boolean onHoldTick(LocalPlayer player, InteractionHand hand) {
        HandTracker ht = HandTracker.INSTANCE;

        Vec3 freeHandPosition;
        Vec3 hookHandPosition;
        try {
            freeHandPosition = ht.getFreeHandPosition().orElseThrow();
            hookHandPosition = ht.getHookHandPosition().orElseThrow();
        } catch (NoSuchElementException e) {
            return false;
        }

        if (ConfigManager.vr().rememberHookHandPosition && this.rememberedHookHandPosition.get(hand.ordinal()).isEmpty()) {
            this.rememberedHookHandPosition.set(hand.ordinal(), Optional.of(hookHandPosition));
        }
        
        Vec3 usedHookHandPosition = this.rememberedHookHandPosition.get(hand.ordinal()).orElse(hookHandPosition);
        Vec3 direction = getDirection(ht.getHookHeadPosition(), usedHookHandPosition);
        double diff = freeHandPosition
            .subtract(usedHookHandPosition)
            .dot(direction.normalize());

        double diffPrev;
        try {
            diffPrev = this.diffPrev.get(hand.ordinal()).orElseThrow();
        } catch (NoSuchElementException e) {
            this.diffPrev.set(hand.ordinal(), Optional.of(diff));
            return true;
        }

        PlayerWithHookData hookData = (PlayerWithHookData)player;
        HookEntity hook = hookData.getHook();
        if (hook == null) {
            return false;
        }
        double ddiff = diffPrev - diff;
        if (ddiff > DDIFF_THRESHOLD) { // up
            ClientSender.climb(true, ClientEvents.shouldPlayClimbSound());
            hookData.setClimbing(true, hook.getAgilityLevel());
        } else if (ddiff < -DDIFF_THRESHOLD) { // down
            ClientSender.climb(false, ClientEvents.shouldPlayClimbSound());
            hookData.setClimbing(false, 0);
        } else {
            this.hapticCooldown[hand.ordinal()] = HAPTIC_COOLDOWN;
            hookData.setClimbing(false, 0);
        }

        if (this.hapticCooldown[hand.ordinal()] == 0) {
            VRBodyPart vrHand = VRBodyPart.fromInteractionHand(hand);
            VRClientAPI.instance().triggerHapticPulse(vrHand, .05f, 80, .5f, .0f);
            this.hapticCooldown[hand.ordinal()] = HAPTIC_COOLDOWN;
        }
        this.hapticCooldown[hand.ordinal()]--;

        this.diffPrev.set(hand.ordinal(), Optional.of(diff));

        return true;
    }

    @Override
    public void onRelease(LocalPlayer player, InteractionHand hand) {
        this.diffPrev.set(hand.ordinal(), Optional.empty());
        this.rememberedHookHandPosition.set(hand.ordinal(), Optional.empty());
        this.hapticCooldown[hand.ordinal()] = 0;
    }
}