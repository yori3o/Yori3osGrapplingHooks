package com.yori3o.yo_hooks.config;

import java.nio.file.Path;
import dev.architectury.platform.Platform;

public class ServerConfig extends JsonConfigManager<ServerConfig.Values> {

    public static class Values {
        public double hooksStiffness = 0.5;
        public double dampingForce = 1;
        public double forceModifier = 0.08;
        public double radialControl = 0.33;
        public Boolean isJumpAllowed = true;
        public float decreaseSatiety = 0.6f;
    }

    private static final Path CONFIG_PATH = Platform.getConfigFolder().resolve("yo_hooks_server.json");

    public ServerConfig() {
        super(Values.class, CONFIG_PATH);
    }

    @Override
    protected Values getDefaultConfig() {
        return new Values();
    }

    /*public double hooksStiffness() { return get().hooksStiffness; }
    public Boolean isJumpAllowed() { return get().isJumpAllowed; }*/
    //public void setSaveBossKiller(boolean value) { get().SaveBossKiller = value; save(); }
}
