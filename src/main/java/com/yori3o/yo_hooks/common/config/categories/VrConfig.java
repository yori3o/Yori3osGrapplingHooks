package com.yori3o.yo_hooks.common.config.categories;


import com.yori3o.yo_hooks.common.YoHooks;

import net.minecraft.resources.Identifier;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;


public class VrConfig {

    private static final String TYPE = "vr";

    public static ConfigClassHandler<VrConfig> HANDLER = ConfigClassHandler.createBuilder(VrConfig.class)
            .id(Identifier.fromNamespaceAndPath("yo_hooks", TYPE + "_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YoHooks.CONFIG_FOLDER.resolve("yo_hooks-" + TYPE + ".json"))
                    //.setJson5(true)
                    .build())
            .build();
    
    @AutoGen(group = "vivecraft_compat", category = "client")
    @Boolean
    @SerialEntry
    public boolean rememberHookHandPosition = true;

    @AutoGen(group = "vivecraft_compat", category = "client")
    @Boolean
    @SerialEntry
    public boolean moveAlongChain = true;
}