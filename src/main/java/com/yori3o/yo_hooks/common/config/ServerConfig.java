package com.yori3o.yo_hooks.common.config;

import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class ServerConfig extends JsonConfigManager<ServerConfig.Values> {

    public static class Values {
        public float decreaseSatiety = 1.1f;
        public boolean breakingFragileBlocks = true;
    }

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("yo_hooks_server.json");

    public ServerConfig() {
        super(Values.class, CONFIG_PATH);
    }

    @Override
    protected Values getDefaultConfig() {
        return new Values();
    }

}
