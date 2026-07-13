package com.yori3o.yo_hooks.common.config.categories;


import com.yori3o.yo_hooks.common.YoHooks;

import net.minecraft.resources.Identifier;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.CustomDescription;
import dev.isxander.yacl3.config.v2.api.autogen.CustomName;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;


public class VrConfig {

    public static ConfigClassHandler<VrConfig> HANDLER = ConfigClassHandler.createBuilder(VrConfig.class)
            .id(Identifier.fromNamespaceAndPath("yo_hooks", "vr_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YoHooks.CONFIG_FOLDER.resolve("yo_hooks-vr.json5"))
                    .setJson5(true)
                    .build())
            .build();
    
    @AutoGen(group = "vivecraft_compat", category = "client")
    @Boolean
    @SerialEntry
    @CustomName("vr_config.yo_hooks.remember_hook_hand_position")
    @CustomDescription("vr_config.yo_hooks.remember_hook_hand_position.desc")
    public boolean rememberHookHandPosition = true;

    @AutoGen(group = "vivecraft_compat", category = "client")
    @Boolean
    @SerialEntry
    @CustomName("vr_config.yo_hooks.move_along_chain")
    @CustomDescription("vr_config.yo_hooks.move_along_chain.desc")
    public boolean moveAlongChain = true;
}