package com.yori3o.yo_hooks.common.util;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
public final class InventoryAccess {
    public static Iterable<ItemStack> items(Player player) {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) items.add(player.getInventory().getItem(i));
        return items;
    }
}
