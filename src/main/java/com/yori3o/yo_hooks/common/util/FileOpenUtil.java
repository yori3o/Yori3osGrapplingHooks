package com.yori3o.yo_hooks.common.util;


import java.awt.Desktop;
import java.io.File;
import java.nio.file.Path;



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

    public static final void revealConfigFolder(Path path) {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                new ProcessBuilder(
                    "explorer.exe",
                    "/select," + path.toAbsolutePath()
                ).start();
            }
            else if (os.contains("mac")) {
                new ProcessBuilder(
                    "open",
                    "-R",
                    path.toAbsolutePath().toString()
                ).start();
            }
            else {
                // Linux / unknown
                Desktop.getDesktop().open(path.toFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}