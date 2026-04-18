package com.yori3o.yo_hooks.common.config;


import com.yori3o.yo_hooks.common.util.LoggerUtil;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * This manager provides basic config file functionality.
 */
public abstract class JsonConfigManager<T> {


    private final Class<T> configClass;
    private final Path configPath;
    private final Gson gson;

    private T configInstance;


    protected JsonConfigManager(Class<T> configClass, Path configPath) {
        this.configClass = configClass;
        this.configPath = configPath;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    
    public void load() {
        try {
            if (Files.notExists(configPath)) {
                saveDefault();
                return;
            }

            getDefaultConfig();

            try (Reader reader = Files.newBufferedReader(configPath)) {
                configInstance = gson.fromJson(new JsonReader(reader), configClass);
            }

            if (configInstance == null) {
                throw new IOException("Config parsed to null (corrupted JSON?)");
            }

            save();

        } catch (Exception e) {
            LoggerUtil.errorWithException("[CONFIG] Failed to load " + configPath.getFileName() + ": ", e);
            backupCorruptedFile();
            saveDefault();
        }
    }

    
    public T get() {
        if (configInstance == null) {
            load();
        }
        return configInstance;
    }

    
    public void save() {
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                gson.toJson(configInstance, writer);
            }
        } catch (Exception e) {
            LoggerUtil.errorWithException("[CONFIG] Failed to save config: ", e);
        }
    }

    
    private void saveDefault() {
        try {
            configInstance = getDefaultConfig();
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                gson.toJson(configInstance, writer);
            }
        } catch (IOException e) {
            LoggerUtil.errorWithException("[CONFIG] Failed to save config: ", e);
        }
    }

    
    private void backupCorruptedFile() {
        try {
            if (Files.exists(configPath)) {
                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
                Path backup = configPath.resolveSibling(configPath.getFileName() + ".broken_" + timestamp + ".bak");
                Files.move(configPath, backup, StandardCopyOption.REPLACE_EXISTING);
                LoggerUtil.warn("[CONFIG] Corrupted config renamed to: " + backup.getFileName());
            }
        } catch (IOException e) {
            LoggerUtil.errorWithException("[CONFIG] Failed to backup corrupted file: ", e);
        }
    }

    
    protected abstract T getDefaultConfig();
}