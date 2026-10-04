package com.yori3o.yo_hooks.common.hookregistry;


import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.yori3o.yo_hooks.common.config.ConfigManager;
import com.yori3o.yo_hooks.common.init.ItemRegistry;
import com.yori3o.yo_hooks.common.sound.SoundRegistry;
import com.yori3o.yo_hooks.common.util.LoggerUtil;
import com.yori3o.yo_hooks.common.util.ResourceResolver;


/**
 * This class reads and contains hooks from hooks.json, as well as a list of those with a custom visual.
 */
public final class HookRegistry {


    private static final Gson GSON = new Gson();

    public static LinkedHashMap<String, HookDefinition> hooks = new LinkedHashMap<>();

    public static final Set<String> hookMaterialsWithCustomVisuals = new HashSet<>();


    public static void loadAndRegisterHooks() {
        load();
        registerAllHooks();
    }

    private static void registerAllHooks() {
        for (HookDefinition def : hooks.values()) {
            def = ConfigManager.setOverlapValuesToHook(def);
            ConfigManager.addNewHookToOverlaps(def.id, def.durability, def.length, def.damageOnHit);
            ItemRegistry.registerHook(def);
        }
        ConfigManager.saveOverlap();
    }

    private static void load() {
        List<URL> urls = ResourceResolver.findFiles("yo_hooks/hooks.json");

        for (URL url : urls) {
            try (Reader reader = new InputStreamReader(url.openStream())) {
                HookDefinition[] array = GSON.fromJson(reader, HookDefinition[].class);

                if (array != null) {
                    for (HookDefinition def : array) {
                        if (def.id == null) {
                            LoggerUtil.warn("HookDefinition without id!");
                            continue;
                        }
                        if (hooks.containsKey(def.id)) continue;
                        def.applyDefaults();
                        hooks.put(def.id, def);
                        if (def.customVisual) {
                            hookMaterialsWithCustomVisuals.add(def.id);
                            SoundRegistry.registerNewCustomSounds(def.id);
                        }
                    }
                }
            } catch (Exception e) {
                LoggerUtil.errorWithException("Failed to parse hooks.json: ", e);
            }
        }
    }
    
}