package org.yoann.stackabletools.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.yoann.stackabletools.StackableToolsMod;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Configuration class for Stackable Tools mod.
 * Handles loading and saving configuration from/to JSON file.
 */
public class StackableToolsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_FILE = "config/stackabletools.json";
    
    private static StackableToolsConfig INSTANCE;
    
    // Configuration values
    public int maxStackSize = 8;
    public boolean enableLogging = false;
    
    /**
     * Loads the configuration from file or creates a default one.
     */
    public static StackableToolsConfig load() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        File configFile = new File(CONFIG_FILE);
        
        // Create config directory if it doesn't exist
        if (!configFile.getParentFile().exists()) {
            if (!configFile.getParentFile().mkdirs()) {
                System.err.println("[StackableTools] Failed to create config directory");
            }
        }
        
        // Load existing config or create default
        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                INSTANCE = GSON.fromJson(reader, StackableToolsConfig.class);
                StackableToolsMod.LOGGER.info("Configuration loaded from {}", CONFIG_FILE);
                
                // Validate config values
                if (INSTANCE.maxStackSize < 1 || INSTANCE.maxStackSize > 64) {
                    StackableToolsMod.LOGGER.warn("Invalid maxStackSize value ({}), using default (8)", INSTANCE.maxStackSize);
                    INSTANCE.maxStackSize = 8;
                }
                
                return INSTANCE;
            } catch (IOException e) {
                StackableToolsMod.LOGGER.error("Failed to load config file, using defaults", e);
                INSTANCE = new StackableToolsConfig();
            }
        } else {
            StackableToolsMod.LOGGER.info("Config file not found, creating default configuration");
            INSTANCE = new StackableToolsConfig();
            INSTANCE.save();
        }
        
        return INSTANCE;
    }
    
    /**
     * Saves the current configuration to file.
     */
    public void save() {
        File configFile = new File(CONFIG_FILE);
        
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
            StackableToolsMod.LOGGER.info("Configuration saved to {}", CONFIG_FILE);
        } catch (IOException e) {
            StackableToolsMod.LOGGER.error("Failed to save config file", e);
        }
    }
    
    /**
     * Gets the singleton instance of the config.
     * If not loaded yet, returns a default instance.
     */
    public static StackableToolsConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StackableToolsConfig();
        }
        return INSTANCE;
    }
    
    /**
     * Gets the configured maximum stack size for tools.
     */
    public int getMaxStackSize() {
        return maxStackSize;
    }
    
    /**
     * Checks if debug logging is enabled.
     */
    public boolean isLoggingEnabled() {
        return enableLogging;
    }
}
