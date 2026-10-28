package com.yori3o.yo_hooks.common.util;


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
        if (!Files.exists(OLD_COMMON)) return;
        try {
            Files.createDirectories(YoHooks.CONFIG_FOLDER);
            Files.move(OLD_COMMON, NEW_COMMON, StandardCopyOption.REPLACE_EXISTING);
            Files.move(OLD_SERVER, NEW_SERVER, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            LoggerUtil.errorWithException("Error when moving config files: ", e);
        }
    }
}