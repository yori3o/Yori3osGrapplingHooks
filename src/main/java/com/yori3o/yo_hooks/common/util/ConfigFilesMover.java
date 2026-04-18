package com.yori3o.yo_hooks.common.util;


import java.io.IOException;
import java.nio.file.*;

import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.impl.PlatformUtil;


public class ConfigFilesMover {


    private static final Path OLD_COMMON = PlatformUtil.getConfigDir().resolve("yo_hooks_common.json");
    private static final Path NEW_COMMON = YoHooks.CONFIG_FOLDER.resolve("yo_hooks-common.json");
    
    private static final Path OLD_SERVER = PlatformUtil.getConfigDir().resolve("yo_hooks_server.json");
    private static final Path NEW_SERVER = YoHooks.CONFIG_FOLDER.resolve("yo_hooks-server.json");

    
    public static void moveConfigFiles() {
        if (Files.exists(NEW_COMMON)) return;
        try {
            Files.createDirectories(YoHooks.CONFIG_FOLDER);
            moveAndRenameJson(OLD_COMMON, NEW_COMMON);
            moveAndRenameJson(OLD_SERVER, NEW_SERVER);
        } catch (Exception e) {
            LoggerUtil.errorWithException("Error when moving config files: ", e);
        }
    }

    public static void moveAndRenameJson(Path sourcePath, Path targetPath) {
        try {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LoggerUtil.errorWithException("Error when moving files: ", e);
        }
    }
}