package com.yori3o.yo_hooks.common.config.categories;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.config.JsonConfigManager;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;



public class ServerConfig extends JsonConfigManager<ServerConfig.Values> {

    public static class Values {
        public float decreaseSatiety = 1.1f;
        public boolean breakingFragileBlocks = true;

        public List<String> blocksBlacklist = new LinkedList<>();
        public boolean whitelistMode = false;
    }

    public static final Path CONFIG_PATH = YoHooks.CONFIG_FOLDER.resolve("yo_hooks-server.json");

    public ServerConfig() {
        super(Values.class, CONFIG_PATH);
    }

    @Override
    protected Values getDefaultConfig() {
        return new Values();
    }

}
