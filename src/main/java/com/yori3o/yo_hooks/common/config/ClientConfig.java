package com.yori3o.yo_hooks.common.config;


import com.yori3o.yo_hooks.common.YoHooks;

import java.nio.file.Path;



public class ClientConfig extends JsonConfigManager<ClientConfig.Values> {

    public static class Values {
        public boolean holdHookTightly = false;
        public boolean climbWithMouseWheelScroll = false;
    }

    public static final Path CONFIG_PATH = YoHooks.CONFIG_FOLDER.resolve("yo_hooks-client.json");

    public ClientConfig() {
        super(Values.class, CONFIG_PATH);
    }

    @Override
    protected Values getDefaultConfig() {
        return new Values();
    }
}
