package com.yori3o.yo_hooks.impl;


import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.api.distmarker.Dist;

import java.nio.file.Path;



public final class PlatformUtil {


    public static boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    public static boolean isClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    public static String getVerison(String modId) {
        return ModList.get()
                .getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse(null);
    }

    public static boolean isFabric() {
        return false;
    }
    
}
