package com.yori3o.yo_hooks.config;

import java.nio.file.Path;
import dev.architectury.platform.Platform;

public class CommonConfig extends JsonConfigManager<CommonConfig.Values> {

    public static class Values {
        public int ironHookDurabitility = 32;
        public int goldHookDurabitility = 16;
        public int diamondHookDurabitility = 64;
        public int netheriteHookDurabitility = 96;

        public int ironHookLength = 18;
        public int goldHookLength = 22;
        public int diamondHookLength = 26;
        public int netheriteHookLength = 32;
    }

    private static final Path CONFIG_PATH = Platform.getConfigFolder().resolve("yo_hooks_common.json");

    public CommonConfig() {
        super(Values.class, CONFIG_PATH);
    }

    @Override
    protected Values getDefaultConfig() {
        return new Values();
    }

    //public boolean isSaveBossKiller() { return get().SaveBossKiller; }
    //public void setSaveBossKiller(boolean value) { get().SaveBossKiller = value; save(); }
}
