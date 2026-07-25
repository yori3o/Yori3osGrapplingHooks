package com.yori3o.yo_hooks.common.config.categories;


import com.yori3o.yo_hooks.common.YoHooks;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.autogen.CustomDescription;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.resources.Identifier;

import java.util.LinkedList;
import java.util.List;



public class ServerConfig {

    private static final String TYPE = "server";

    public static ConfigClassHandler<ServerConfig> HANDLER = ConfigClassHandler.createBuilder(ServerConfig.class)
            .id(Identifier.fromNamespaceAndPath("yo_hooks", TYPE + "_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YoHooks.CONFIG_FOLDER.resolve("yo_hooks-" + TYPE + ".json"))
                    //.setJson5(true)
                    .build())
            .build();

    
    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    @CustomDescription("yacl3.config.yo_hooks:" + TYPE + "-config." + /**/"decreaseSatiety"/**/ + ".desc")        
    public float decreaseSatiety = 1.1f;

    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    @CustomDescription("yacl3.config.yo_hooks:" + TYPE + "-config." + /**/"breakingFragileBlocks"/**/ + ".desc")
    public boolean breakingFragileBlocks = true;

    //@AutoGen(group = TYPE, category = TYPE)
    //@Boolean
    @SerialEntry
    //@CustomDescription("yacl3.config.yo_hooks:" + TYPE + "-config." + /**/"funnyMode"/**/ + ".desc")
    public List<String> blocksBlacklist = new LinkedList<>();

    @AutoGen(group = TYPE, category = TYPE)
    @Boolean
    @SerialEntry
    @CustomDescription("yacl3.config.yo_hooks:" + TYPE + "-config." + /**/"whitelistMode"/**/ + ".desc")
    public boolean whitelistMode = false;
    



}
