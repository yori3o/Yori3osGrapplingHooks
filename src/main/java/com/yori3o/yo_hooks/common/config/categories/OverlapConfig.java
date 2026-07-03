package com.yori3o.yo_hooks.common.config.categories;


import com.yori3o.yo_hooks.common.YoHooks;
import com.yori3o.yo_hooks.common.config.JsonConfigManager;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;



public class OverlapConfig extends JsonConfigManager<OverlapConfig.Values> {

    public static class Values {
        public Map<String, Integer> durabilityOverlap = new HashMap<>();
        public Map<String, Integer> rangeOverlap = new HashMap<>();
    }

    public static final Path CONFIG_PATH = YoHooks.CONFIG_FOLDER.resolve("overlaps.json");

    public OverlapConfig() {
        super(Values.class, CONFIG_PATH);
    }

    @Override
    protected Values getDefaultConfig() {
        return new Values();
    }

}
