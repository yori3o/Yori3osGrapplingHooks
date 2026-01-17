package com.yori3o.yo_hooks.common.config;

import java.nio.file.Path;
import dev.architectury.platform.Platform;

public class CommonConfig extends JsonConfigManager<CommonConfig.Values> {

    public static class Values {
        public int ironHookDurabitility = 72;
        public int goldHookDurabitility = 24;
        public int diamondHookDurabitility = 156;
        public int netheriteHookDurabitility = 384;

        public int ironHookLength = 12; // 26 - max with enchantment 4 level
        public int goldHookLength = 17; // 31
        public int diamondHookLength = 21; // 35
        public int netheriteHookLength = 26; // 40

        public float climbSpeed = 0.115f;

        public boolean softHook = false;
        public float stiffness = 0.10f;

        public boolean funnyMode = false;
    }

    private static final Path CONFIG_PATH = Platform.getConfigFolder().resolve("yo_hooks_common.json");

    public CommonConfig() {
        super(Values.class, CONFIG_PATH);
    }

    @Override
    protected Values getDefaultConfig() {
        return new Values();
    }

    //public void set...(boolean value) { get(). ... = value; save(); }
}
