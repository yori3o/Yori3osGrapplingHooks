package com.yori3o.yo_hooks.impl;


import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.api.EnvType;

import java.nio.file.Path;
import java.util.Optional;



public final class PlatformUtil {

    
    public static boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static String getVerison(String modId) {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);

        if (modContainer.isPresent()) {
            return modContainer.get().getMetadata().getVersion().getFriendlyString();
        }
        return null;
    }
    
    public static final boolean isFabric() {
        return true;
    }

}