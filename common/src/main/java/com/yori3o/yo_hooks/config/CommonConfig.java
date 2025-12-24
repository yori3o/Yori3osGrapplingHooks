package com.yori3o.yo_hooks.config;

import java.nio.file.Path;
import dev.architectury.platform.Platform;

public class CommonConfig extends JsonConfigManager<CommonConfig.Values> {

    public static class Values {
        public int ironHookDurabitility = 48;
        public int goldHookDurabitility = 24;
        public int diamondHookDurabitility = 96;
        public int netheriteHookDurabitility = 144;

        public int ironHookLength = 18;
        public int goldHookLength = 22;
        public int diamondHookLength = 26;
        public int netheriteHookLength = 32;

        //public float climbSpeed = 0.1f;

        public boolean softHook = false;
        public float stiffness = 0.10f;
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
