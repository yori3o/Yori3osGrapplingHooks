package com.yori3o.yo_hooks.common.config.categories;


import com.yori3o.yo_hooks.common.YoHooks;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.FloatSlider;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedList;
import java.util.List;



public class ServerConfig {

    private static final String TYPE = "server";

    public static ConfigClassHandler<ServerConfig> HANDLER = ConfigClassHandler.createBuilder(ServerConfig.class)
            .id(ResourceLocation.fromNamespaceAndPath("yo_hooks", TYPE + "_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YoHooks.CONFIG_FOLDER.resolve("yo_hooks-" + TYPE + ".json"))
                    //.setJson5(true)
                    .build())
            .build();

    
    @AutoGen(group = TYPE, category = TYPE)
    @FloatSlider(min = 0, max = 3, step = 0.1f)
    @SerialEntry     
    public float decreaseSatiety = 1.1f;

    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    public boolean breakingFragileBlocks = true;

    @SerialEntry
    public List<String> blocksBlacklist = new LinkedList<>();

    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    public boolean whitelistMode = false;
    



}
