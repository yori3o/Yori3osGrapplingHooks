package com.yori3o.yo_hooks.common.config.categories;


import com.yori3o.yo_hooks.common.YoHooks;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.resources.ResourceLocation;



public class ClientConfig {

    private static final String TYPE = "client";

    public static ConfigClassHandler<ClientConfig> HANDLER = ConfigClassHandler.createBuilder(ClientConfig.class)
            .id(ResourceLocation.fromNamespaceAndPath("yo_hooks", TYPE + "_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YoHooks.CONFIG_FOLDER.resolve("yo_hooks-" + TYPE + ".json"))
                    .build())
            .build();
    
    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    public boolean holdHookTightly = false;

    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    public boolean climbWithMouseWheelScroll = false;

    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    public boolean usingWhileHoldingFood = false;

}
