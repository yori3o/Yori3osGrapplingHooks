package com.yori3o.yo_hooks.impl;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;



public class PlatformModFileResolver {

    public static List<InputStream> findFiles(String path) {

        List<InputStream> streams = new ArrayList<>();

        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {

            Optional<Path> file = mod.findPath(path);

            if (file.isPresent()) {
                try {
                    streams.add(Files.newInputStream(file.get()));
                } catch (IOException ignored) {}
            }
        }

        return streams;
    }
}