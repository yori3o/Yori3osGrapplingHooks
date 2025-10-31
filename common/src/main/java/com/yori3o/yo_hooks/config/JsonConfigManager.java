package com.yori3o.yo_hooks.config;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



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

            try (Reader reader = Files.newBufferedReader(configPath)) {
                configInstance = gson.fromJson(new JsonReader(reader), configClass);
            }

            if (configInstance == null) {
                throw new IOException("Config parsed to null (corrupted JSON?)");
            }

        } catch (Exception e) {
            System.err.println("[CONFIG] Failed to load " + configPath.getFileName() + ": " + e.getMessage());
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
            System.err.println("[CONFIG] Failed to save config: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private void saveDefault() {
        try {
            configInstance = getDefaultConfig();
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                gson.toJson(configInstance, writer);
            }
            System.out.println("[CONFIG] Created new config file: " + configPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    private void backupCorruptedFile() {
        try {
            if (Files.exists(configPath)) {
                String timestamp = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
                Path backup = configPath.resolveSibling(configPath.getFileName() + ".broken_" + timestamp + ".bak");
                Files.move(configPath, backup, StandardCopyOption.REPLACE_EXISTING);
                System.err.println("[CONFIG] Corrupted config renamed to: " + backup.getFileName());
            }
        } catch (IOException ex) {
            System.err.println("[CONFIG] Failed to backup corrupted file: " + ex.getMessage());
        }
    }

    
    protected abstract T getDefaultConfig();
}
