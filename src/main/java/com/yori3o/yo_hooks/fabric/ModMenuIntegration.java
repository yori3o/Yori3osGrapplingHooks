package com.yori3o.yo_hooks.fabric;


import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;



public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        //if (!PlatformUtil.isModLoaded("yet_another_config_lib_v3")) return null;
        return parent -> com.yori3o.yo_hooks.common.config.client.ConfigScreenFactory.create(parent);
    }
}
