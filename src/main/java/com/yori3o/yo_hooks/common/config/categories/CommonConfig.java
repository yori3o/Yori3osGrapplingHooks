package com.yori3o.yo_hooks.common.config.categories;


import com.yori3o.yo_hooks.common.YoHooks;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.FloatSlider;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.resources.Identifier;




public class CommonConfig {

    private static final String TYPE = "common";

    public static ConfigClassHandler<CommonConfig> HANDLER = ConfigClassHandler.createBuilder(CommonConfig.class)
            .id(Identifier.fromNamespaceAndPath("yo_hooks", TYPE + "_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YoHooks.CONFIG_FOLDER.resolve("yo_hooks-" + TYPE + ".json"))
                    //.setJson5(true)
                    .build())
            .build();

    @AutoGen(group = TYPE, category = TYPE)
    @FloatSlider(min = 0, max = 1, step = 0.01f)
    @SerialEntry
    public float climbSpeed = 0.115f;

    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    public boolean softHook = false;

    @AutoGen(group = TYPE, category = TYPE)
    @FloatSlider(min = 0.1f, max = 1, step = 0.01f)
    @SerialEntry
    public float stiffness = 0.10f;

    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    public boolean funnyMode = false;
    


}
