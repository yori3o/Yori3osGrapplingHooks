package com.yori3o.yo_hooks.common.util;

import com.yori3o.yo_hooks.common.entity.HookEntity;
import com.yori3o.yo_hooks.common.init.ComponentRegistry;
import com.yori3o.yo_hooks.common.item.HookItem;
import com.yori3o.yo_hooks.common.util.interfaces.PlayerWithHookData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** The server owns component updates; rendering also verifies the cast identity. */
public final class HookStackState {
    private HookStackState() {}

    public static boolean belongsTo(ItemStack stack, HookEntity hook) {
        return !stack.isEmpty() && stack.getItem() instanceof HookItem item
            && item.hookDefinition.id.equals(hook.getHookItemMaterial())
            && hook.getUUID().toString().equals(stack.get(ComponentRegistry.HOOK_ID.get()));
    }

    public static boolean isActive(ItemStack stack, Player player) {
        HookEntity hook = ((PlayerWithHookData) player).yo_hooks$getHook();
        return hook != null && !hook.isRemoved() && belongsTo(stack, hook)
            && stack.getOrDefault(ComponentRegistry.HOOK_ACTIVE.get(), false);
    }

    public static void activate(Player player, ItemStack stack, HookEntity hook) {
        clearAll(player);
        stack.set(ComponentRegistry.HOOK_ID.get(), hook.getUUID().toString());
        stack.set(ComponentRegistry.HOOK_ACTIVE.get(), true);
    }

    public static void clear(ItemStack stack) {
        if (stack.getOrDefault(ComponentRegistry.HOOK_ACTIVE.get(), false)) {
            stack.set(ComponentRegistry.HOOK_ACTIVE.get(), false);
        }
        stack.remove(ComponentRegistry.HOOK_ID.get());
    }

    public static void clearAll(Player player) {
        for (ItemStack stack : InventoryAccess.items(player)) clear(stack);
    }

    public static void clearCast(Player player, HookEntity hook) {
        for (ItemStack stack : InventoryAccess.items(player)) {
            if (belongsTo(stack, hook)) clear(stack);
        }
    }

    public static void reconcile(Player player) {
        HookEntity hook = ((PlayerWithHookData) player).yo_hooks$getHook();
        ItemStack held = hook == null ? ItemStack.EMPTY : hook.getHeldStack(player);
        for (ItemStack stack : InventoryAccess.items(player)) {
            boolean active = !held.isEmpty() && stack == held && !hook.isRemoved();
            if (stack.getOrDefault(ComponentRegistry.HOOK_ACTIVE.get(), false) != active) {
                stack.set(ComponentRegistry.HOOK_ACTIVE.get(), active);
            }
            // Retain the cast ID while its saved entity's chunk is loading.
            if (hook != null && stack != held) stack.remove(ComponentRegistry.HOOK_ID.get());
        }
    }
}
