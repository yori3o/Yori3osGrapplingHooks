package com.yori3o.yo_hooks.common.utils;

import java.awt.Desktop;
import java.io.File;


public final class FileOpenUtil {

    public static final void revealConfigFile(File file) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                new ProcessBuilder(
                    "explorer.exe",
                    "/select," + file.getAbsolutePath()
                ).start();
            }
            else if (os.contains("mac")) {
                new ProcessBuilder(
                    "open",
                    "-R",
                    file.getAbsolutePath()
                ).start();
            }
            else {
                // Linux / unknown
                Desktop.getDesktop().open(file.getParentFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}