//? if <26.1 {
/*package com.yori3o.yo_hooks.common.client.render;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.Identifier;
public final class LegacyItemProperties {
    public static void register() {
        ItemRegistry.ALL_HOOKS.values().forEach(supplier -> ItemProperties.register(supplier.get(),
            Identifier.fromNamespaceAndPath("yo_hooks", "hook_active"),
            (stack, level, entity, seed) -> entity instanceof net.minecraft.world.entity.player.Player player
                && com.yori3o.yo_hooks.common.util.HookStackState.isActive(stack, player) ? 1 : 0));
    }
}
*///? }
