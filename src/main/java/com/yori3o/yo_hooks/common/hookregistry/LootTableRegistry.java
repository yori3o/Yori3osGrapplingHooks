package com.yori3o.yo_hooks.common.hookregistry;


import com.yori3o.yo_hooks.common.util.LoggerUtil;
import com.yori3o.yo_hooks.impl.PlatformModFileResolver;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;


/**
 * This class reads and contains loot tables from loot_tables.json
 */
public final class LootTableRegistry {


    private static final Gson GSON = new Gson();

    public static List<LootTableDefinition> lootTables = new ArrayList<>();


    public static void load() {
        List<InputStream> files = PlatformModFileResolver.findFiles("yo_hooks/loot_tables.json");
        for (InputStream stream : files) {
            try (Reader reader = new InputStreamReader(stream)) {
                LootTableDefinition[] array = GSON.fromJson(reader, LootTableDefinition[].class);

                if (array != null) {
                    for (LootTableDefinition def : array) {
                        if (def.lootTable == null || def.lootTableForInject == null) continue;
                        lootTables.add(def);
                    }
                }
            } catch (Exception e) {
                LoggerUtil.errorWithException("Failed to parse loot_tables.json: ", e);
            }
        }
    }
    
}