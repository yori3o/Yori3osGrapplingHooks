package com.yori3o.yo_hooks.common.config.categories;


import com.yori3o.yo_hooks.common.YoHooks;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.CustomDescription;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.resources.Identifier;



public class ClientConfig {

    private static final String TYPE = "client";

    public static ConfigClassHandler<ClientConfig> HANDLER = ConfigClassHandler.createBuilder(ClientConfig.class)
            .id(Identifier.fromNamespaceAndPath("yo_hooks", TYPE + "_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YoHooks.CONFIG_FOLDER.resolve("yo_hooks-" + TYPE + ".json"))
                    .build())
            .build();
    
    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    @CustomDescription("yacl3.config.yo_hooks:" + TYPE + "-config." + /**/"holdHookTightly"/**/ + ".desc")
    public boolean holdHookTightly = false;

    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    @CustomDescription("yacl3.config.yo_hooks:" + TYPE + "-config." + /**/"climbWithMouseWheelScroll"/**/ + ".desc")
    public boolean climbWithMouseWheelScroll = false;

}
