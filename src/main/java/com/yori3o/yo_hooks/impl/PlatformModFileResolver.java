package com.yori3o.yo_hooks.impl;


import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.yori3o.yo_hooks.common.util.LoggerUtil;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.IModFile;



public class PlatformModFileResolver {

    public static List<InputStream> findFiles(String path) {

        List<InputStream> streams = new ArrayList<>();

        //for (IModInfo mod : ModList.get().getMods()) {

            //IModFile file = mod.getOwningFile().getFile();

            try {
                //Path found = file.getFilePath().resolve(path);

                //if (Files.exists(found)) {
                InputStream stream = Thread.currentThread()
                    .getContextClassLoader()
                    .getResourceAsStream(path);
                if (stream != null) streams.add(stream);
                //}

            } catch (Exception e) {
                LoggerUtil.errorWithException("dssdgsgdd: ", e);
            }
        //}

        return streams;
    }
}