package com.yori3o.yo_hooks.common.compat;

import com.yori3o.yo_hooks.common.init.ItemRegistry;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

public final class LegacyTrades {
    private static boolean registered;
    public static void register() {
        if (registered) return;
        registered = true;
        add(3, "iron", 3, 10);
        add(5, "diamond", 13, 30);
    }
    private static void add(int level, String material, int price, int xp) {
        var hook = ItemRegistry.ALL_HOOKS.get(material);
        if (hook == null) return;
        VillagerTrades.ItemListing listing = (entity, random) -> {
            ItemStack stack = EnchantmentHelper.enchantItem(random, new ItemStack(hook.get()),
                5 + random.nextInt(15), entity.registryAccess(),
                entity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(EnchantmentTags.ON_TRADED_EQUIPMENT));
            return new MerchantOffer(new ItemCost(Items.EMERALD, price), stack, 3, xp, 0.2f);
        };
        var trades = VillagerTrades.TRADES.get(VillagerProfession.TOOLSMITH);
        var previous = trades.get(level);
        var combined = Arrays.copyOf(previous, previous.length + 1);
        combined[previous.length] = listing;
        trades.put(level, combined);
    }
}
