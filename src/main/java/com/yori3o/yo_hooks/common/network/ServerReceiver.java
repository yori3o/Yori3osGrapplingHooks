package com.yori3o.yo_hooks.common.network;

import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.sound.SoundRegistry;
import com.yori3o.yo_hooks.common.util.PhysicVariables;
import com.yori3o.yo_hooks.common.util.interfaces.PlayerWithHookData;
import com.yori3o.yo_hooks.impl.PlatformNetworkHelper;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.gameevent.GameEvent;
import java.util.UUID;

public class ServerReceiver {
    public static void register() {
        PlatformNetworkHelper.registerC2S(PlayerJumpPayload.TYPE, PlayerJumpPayload.CODEC,
            (payload, context) -> context.enqueue(() -> jump(context.getPlayer(), payload)));
        PlatformNetworkHelper.registerC2S(PlayerClimbPayload.TYPE, PlayerClimbPayload.CODEC,
            (payload, context) -> context.enqueue(() -> climb(context.getPlayer(), payload)));
    }

    private static HookEntity controlledHook(Player player, UUID id) {
        if (player == null) return null;
        HookEntity hook = ((PlayerWithHookData) player).yo_hooks$getHook();
        return hook != null && hook.getUUID().equals(id) && hook.isInBlock()
            && hook.isControlledBy(player) ? hook : null;
    }

    // All reads and writes happen on the server thread, including cast validation.
    static void jump(Player player, PlayerJumpPayload payload) {
        HookEntity hook = controlledHook(player, payload.hookId());
        if (hook == null) return;
        var supplier = ItemRegistry.ALL_HOOKS.get(hook.getHookItemMaterial());
        if (supplier == null) return;
        var definition = supplier.get().hookDefinition;
        ((PlayerWithHookData) player).yo_hooks$setUsingCancelAfterJump(payload.usingCancel());
        if (!ConfigManager.common().funnyMode && !definition.doesNotConsumeHunger) {
            player.causeFoodExhaustion(ConfigManager.server().decreaseSatiety / 3f);
        }
        hook.discard();
        player.level().playSound(null, player.getX(), player.getY() + 1, player.getZ(),
            SoundRegistry.getBackSound(definition.id), SoundSource.PLAYERS, 1f, 1f);
        player.resetFallDistance();
        player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
    }

    static void climb(Player player, PlayerClimbPayload payload) {
        HookEntity hook = controlledHook(player, payload.hookId());
        if (hook == null) return;
        long tick = player.level().getGameTime();
        if (!hook.acceptClimb(tick)) return;
        int agility = hook.getAgilityLevel();
        float oldLength = hook.getLength();
        float maxLength = Math.max(0.4f, hook.getMaxRange() - 2f);
        double speed = Math.max(0, ConfigManager.common().climbSpeed);
        float length = payload.up()
            ? (float) Math.max(0.4, oldLength - (speed + agility * 0.041) * PhysicVariables.climbSpeedMultiplier)
            : Math.min(maxLength, oldLength + (float) (speed * 1.5));
        if (payload.up() ? length >= oldLength : length <= oldLength) return;
        if (!Float.isFinite(length)) return;
        hook.setLength(length);
        var supplier = ItemRegistry.ALL_HOOKS.get(hook.getHookItemMaterial());
        if (payload.up() && !ConfigManager.common().funnyMode && supplier != null
            && !supplier.get().hookDefinition.doesNotConsumeHunger) {
            player.causeFoodExhaustion(ConfigManager.server().decreaseSatiety / 75f + agility * 0.0065f);
        }
        if (payload.playSound() && hook.acceptClimbSound(tick)) {
            player.level().playSound(null, player, SoundRegistry.getClimbSound(hook.getHookItemMaterial()),
                SoundSource.PLAYERS, 0.21f, 1f);
        }
    }
}
