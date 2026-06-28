package com.yori3o.yo_hooks.common.client.vr;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.vivecraft.api.client.HeldInteractModule;
import org.vivecraft.api.client.VRClientAPI;
import org.vivecraft.api.data.VRBodyPart;

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
    private static final double DDY_THRESHOLD = 0.01;
    private static final int HAPTIC_COOLDOWN = 5;

    private Optional<Double> dyPrev = Optional.empty();
    private int hapticCooldown = 0;

    private HandInteractModule() {}

    private static boolean isPointInCone(Vec3 freeHandPos, Vec3 hookHandPos) {
        double coneAngleRadians = Math.PI / 3;
        Vec3 d = freeHandPos.subtract(hookHandPos);
        
        if (d.y < 0) {
            return false;
        }
            
        double squaredRadius = d.x * d.x + d.z * d.z;
        double halfAngleTan = Math.tan(coneAngleRadians / 2.);
        double maxSquaredRadius = (d.y * d.y) * (halfAngleTan * halfAngleTan);
        
        return squaredRadius <= maxSquaredRadius;
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
            return isPointInCone(mainHandPosition, offHandPosition);
        } else {
            return isPointInCone(offHandPosition, mainHandPosition);
        }
    }

    @Override
    public boolean onPress(LocalPlayer player, InteractionHand hand) {
        double dy;
        try {
            HandTracker ht = HandTracker.INSTANCE;
            Vec3 mainHandPosition = ht.getMainHandPosition().orElseThrow();
            Vec3 offHandPosition = ht.getOffHandPosition().orElseThrow();
            if (hand == InteractionHand.MAIN_HAND) {
                dy = mainHandPosition.y - offHandPosition.y; // free hand is main hand
            } else {
                dy = offHandPosition.y - mainHandPosition.y; // free hand is off hand
            }
        } catch (NoSuchElementException e) {
            return false;
        }

        this.dyPrev = Optional.of(dy);

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

        VRBodyPart vrHand;
        double dy;
        try {
            HandTracker ht = HandTracker.INSTANCE;
            Vec3 mainHandPosition = ht.getMainHandPosition().orElseThrow();
            Vec3 offHandPosition = ht.getOffHandPosition().orElseThrow();
            if (hand == InteractionHand.MAIN_HAND) {
                vrHand = VRBodyPart.MAIN_HAND;
                dy = mainHandPosition.y - offHandPosition.y; // free hand is main hand
            } else {
                vrHand = VRBodyPart.OFF_HAND;
                dy = offHandPosition.y - mainHandPosition.y; // free hand is off hand
            }
        } catch (NoSuchElementException e) {
            return false;
        }

        double dyPrev;
        try {
            dyPrev = this.dyPrev.orElseThrow();
        } catch (NoSuchElementException e) {
            this.dyPrev = Optional.of(dy);
            return true;
        }

        PlayerWithHookData hookData = (PlayerWithHookData)player;
        HookEntity hook = hookData.getHook();
        int agilityLevel = hook.getAgilityLevel();
        double ddy = dyPrev - dy;
        if (ddy > DDY_THRESHOLD) { // up
            ClientSender.climb(true, agilityLevel, false); // Vibrate controller instead of playing sound
            hookData.setClimbing(true, agilityLevel);
        } else if (ddy < -DDY_THRESHOLD) { // down
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

        this.dyPrev = Optional.of(dy);

        return true;
    }

    @Override
    public void onRelease(LocalPlayer player, InteractionHand hand) {
        this.dyPrev = Optional.empty();
    }
}