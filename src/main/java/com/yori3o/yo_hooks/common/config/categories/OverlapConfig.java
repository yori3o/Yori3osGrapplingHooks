package com.yori3o.yo_hooks.common.config.categories;


import com.yori3o.yo_hooks.common.YoHooks;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;



public class OverlapConfig {

    public static ConfigClassHandler<OverlapConfig> HANDLER = ConfigClassHandler.createBuilder(OverlapConfig.class)
            .id(Identifier.fromNamespaceAndPath("yo_hooks", "overlap_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YoHooks.CONFIG_FOLDER.resolve("overlaps.json"))
                    //.setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public Map<String, Integer> durabilityOverlap = new HashMap<>();

    @SerialEntry
    public Map<String, Integer> rangeOverlap = new HashMap<>();

}
