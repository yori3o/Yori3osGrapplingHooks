package com.yori3o.yo_hooks.common.client.vr;

import java.util.Arrays;
import java.util.List;
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

    private List<Optional<Double>> diffPrev = Arrays.asList(Optional.empty(), Optional.empty());
    private List<Optional<Vec3>> rememberedHookHandPositionWorld = Arrays.asList(Optional.empty(), Optional.empty());
    private List<Optional<Vec3>> rememberedHookHandPositionRoom = Arrays.asList(Optional.empty(), Optional.empty());
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

    private static Vec3 getDirection(Optional<Vec3> hookHeadPositionWorld, Vec3 hookHandPositionWorld) {
        return hookHeadPositionWorld
            .orElseGet(() -> hookHandPositionWorld.add(Vec3.Y_AXIS))
            .subtract(hookHandPositionWorld);
    }

    private record HandPositions(
        Vec3 freeHandPositionWorld, 
        Vec3 hookHandPositionWorld, 
        Vec3 freeHandPositionRoom, 
        Vec3 hookHandPositionRoom
    ) {}

    private HandPositions getHandPositions(InteractionHand hand) throws NoSuchElementException {
        HandTracker ht = HandTracker.INSTANCE;
        Vec3 mainHandPositionWorld = ht.getMainHandPositionWorld().orElseThrow();
        Vec3 offHandPositionWorld = ht.getOffHandPositionWorld().orElseThrow();
        Vec3 mainHandPositionRoom = ht.getMainHandPositionRoom().orElseThrow();
        Vec3 offHandPositionRoom = ht.getOffHandPositionRoom().orElseThrow();
        if (hand == InteractionHand.MAIN_HAND) {
            return new HandPositions(mainHandPositionWorld, offHandPositionWorld, mainHandPositionRoom, offHandPositionRoom);
        } else {
            return new HandPositions(offHandPositionWorld, mainHandPositionWorld, offHandPositionRoom, mainHandPositionRoom);
        }
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
        if (player.getItemInHand(hand) != ItemStack.EMPTY) {
            return false;
        }

        HandPositions hp;
        try { // this is also a check that HandTracker is active
            hp = getHandPositions(hand);
        } catch (NoSuchElementException e) {
            return false;
        }
        
        HandTracker ht = HandTracker.INSTANCE;
        Vec3 direction = getDirection(ht.getHookHeadPositionWorld(), hp.hookHandPositionWorld);
        return isFreeHandInCone(hp.freeHandPositionRoom, hp.hookHandPositionRoom, direction);
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

        HandPositions hp;
        try {
            hp = getHandPositions(hand);
        } catch (NoSuchElementException e) {
            return false;
        }

        if (VrConfig.HANDLER.instance().rememberHookHandPosition && this.rememberedHookHandPositionWorld.get(hand.ordinal()).isEmpty()) {
            this.rememberedHookHandPositionWorld.set(hand.ordinal(), Optional.of(hp.hookHandPositionWorld));
            this.rememberedHookHandPositionRoom.set(hand.ordinal(), Optional.of(hp.hookHandPositionRoom));
        }
        
        Vec3 usedHookHandPositionWorld = this.rememberedHookHandPositionWorld.get(hand.ordinal()).orElse(hp.hookHandPositionWorld);
        Vec3 usedHookHandPositionRoom = this.rememberedHookHandPositionRoom.get(hand.ordinal()).orElse(hp.hookHandPositionRoom);
        Vec3 direction = getDirection(ht.getHookHeadPositionWorld(), usedHookHandPositionWorld);
        double diff = hp.freeHandPositionRoom
            .subtract(usedHookHandPositionRoom)
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
        int agilityLevel = hook.getAgilityLevel();
        double ddiff = diffPrev - diff;
        if (ddiff > DDIFF_THRESHOLD) { // up
            ClientSender.climb(true, agilityLevel, false); // Vibrate controller instead of playing sound
            hookData.setClimbing(true, agilityLevel);
        } else if (ddiff < -DDIFF_THRESHOLD) { // down
            ClientSender.climb(false, agilityLevel, false);
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
        this.rememberedHookHandPositionWorld.set(hand.ordinal(), Optional.empty());
        this.rememberedHookHandPositionRoom.set(hand.ordinal(), Optional.empty());
        this.hapticCooldown[hand.ordinal()] = 0;
    }
}