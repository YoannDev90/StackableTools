package stackabletools.config

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import java.io.File
import java.nio.charset.StandardCharsets
import stackabletools.CustomLogger

/**
 * TOML configuration manager.
 */
object ConfigManager {
    private const val CONFIG_FILE_PATH = "config/stackabletools.toml"
    private var cachedConfig: StackableToolsConfig? = null

    /**
     * Retrieves the current configuration (loads if necessary).
     * @return The current configuration.
     */
    fun getConfig(): StackableToolsConfig {
        if (cachedConfig == null) {
            cachedConfig = loadConfig()
        }
        return cachedConfig!!
    }

    /**
     * Loads the configuration from the file (or creates it by default).
     * @return The loaded configuration.
     */
    fun loadConfig(): StackableToolsConfig {
        val configFile = File(CONFIG_FILE_PATH)
        
        if (!configFile.exists()) {
            createDefaultConfig(configFile)
        }
        
        return try {
            val toml = Toml().read(configFile)
            
            val logging = StackableToolsConfig.LoggingConfig(
                enable = toml.getBoolean("logging.enable", true),
                level = toml.getString("logging.level", "INFO"),
                inFile = toml.getBoolean("logging.in_file", true),
                inConsole = toml.getBoolean("logging.in_console", true)
            )

            val categoriesRaw = toml.getList<String>("stacking.active_categories") ?: listOf()
            val categories = categoriesRaw.mapNotNull { StackingCategory.fromString(it) }
                .ifEmpty { listOf(StackingCategory.TOOLS, StackingCategory.POTIONS, StackingCategory.ENCHANTED_BOOKS, StackingCategory.WEAPONS) }

            val stacking = StackableToolsConfig.StackingConfig(
                enable = toml.getBoolean("stacking.enable", true),
                maxStackSize = toml.getLong("stacking.max_stack_size", 64L),
                maxToolStackSize = toml.getLong("stacking.max_tool_stack_size", 8L),
                maxPotionStackSize = toml.getLong("stacking.max_potion_stack_size", 16L),
                maxEnchantedBooksStackSize = toml.getLong("stacking.max_enchanted_book_stack_size", 4L),
                maxWeaponsStackSize = toml.getLong("stacking.max_weapon_stack_size", 1L),
                maxElytraStackSize = toml.getLong("stacking.max_elytra_stack_size", 1L),
                maxArmorPieceStackSize = toml.getLong("stacking.max_armor_piece_stack_size", 1L),
                activeCategories = categories,
                excludedItemIds = toml.getList<String>("stacking.excluded_item_ids")?.filterNotNull() ?: listOf(),
                manualStackableItemIds = toml.getList<String>("stacking.manual_item_ids")?.filterNotNull() ?: listOf()
            )

            val config = StackableToolsConfig(logging, stacking, true)
            cachedConfig = config
            CustomLogger.info("Configuration successfully loaded from $CONFIG_FILE_PATH")
            config
        } catch (e: Exception) {
            CustomLogger.error("CRITICAL: Fatal error while loading configuration: ${e.message}")
            CustomLogger.error("Default configuration will be used.")
            val defaultConfig = StackableToolsConfig(isLoaded = true)
            cachedConfig = defaultConfig
            defaultConfig
        }
    }

    /**
     * Saves the configuration to the TOML file.
     * @param config The configuration to save.
     */
    fun saveConfig(config: StackableToolsConfig) {
        val configFile = File(CONFIG_FILE_PATH)
        val writer = TomlWriter()

        try {
            configFile.parentFile?.mkdirs()
            
            val data = mapOf(
                "logging" to mapOf(
                    "enable" to config.logging.enable,
                    "level" to config.logging.level,
                    "in_file" to config.logging.inFile,
                    "in_console" to config.logging.inConsole
                ),
                "stacking" to mapOf(
                    "enable" to config.stacking.enable,
                    "max_stack_size" to config.stacking.maxStackSize,
                    "max_tool_stack_size" to config.stacking.maxToolStackSize,
                    "max_potion_stack_size" to config.stacking.maxPotionStackSize,
                    "max_enchanted_book_stack_size" to config.stacking.maxEnchantedBooksStackSize,
                    "max_weapon_stack_size" to config.stacking.maxWeaponsStackSize,
                    "max_elytra_stack_size" to config.stacking.maxElytraStackSize,
                    "max_armor_piece_stack_size" to config.stacking.maxArmorPieceStackSize,
                    "active_categories" to config.stacking.activeCategories.map { it.key },
                    "excluded_item_ids" to config.stacking.excludedItemIds,
                    "manual_item_ids" to config.stacking.manualStackableItemIds
                )
            )
            
            writer.write(data, configFile)
            cachedConfig = config
        } catch (e: Exception) {
            CustomLogger.error("Error while saving config: ${e.message}")
        }
    }

    private val configSetters: Map<String, (StackableToolsConfig, Any) -> StackableToolsConfig> = mapOf(
        "logging.enable" to { c, v -> c.copy(logging = c.logging.copy(enable = v as Boolean)) },
        "logging.level" to { c, v -> c.copy(logging = c.logging.copy(level = v as String)) },
        "logging.in_file" to { c, v -> c.copy(logging = c.logging.copy(inFile = v as Boolean)) },
        "logging.in_console" to { c, v -> c.copy(logging = c.logging.copy(inConsole = v as Boolean)) },
        "stacking.enable" to { c, v -> c.copy(stacking = c.stacking.copy(enable = v as Boolean)) },
        "stacking.max_stack_size" to { c, v -> c.copy(stacking = c.stacking.copy(maxStackSize = v as Long)) },
        "stacking.max_tool_stack_size" to { c, v -> c.copy(stacking = c.stacking.copy(maxToolStackSize = v as Long)) },
        "stacking.max_potion_stack_size" to { c, v -> c.copy(stacking = c.stacking.copy(maxPotionStackSize = v as Long)) },
        "stacking.max_enchanted_books_stack_size" to { c, v -> c.copy(stacking = c.stacking.copy(maxEnchantedBooksStackSize = v as Long)) },
        "stacking.max_weapons_stack_size" to { c, v -> c.copy(stacking = c.stacking.copy(maxWeaponsStackSize = v as Long)) },
        "stacking.max_elytra_stack_size" to { c, v -> c.copy(stacking = c.stacking.copy(maxElytraStackSize = v as Long)) },
        "stacking.max_armor_piece_stack_size" to { c, v -> c.copy(stacking = c.stacking.copy(maxArmorPieceStackSize = v as Long)) },
        "stacking.active_categories" to { c, v ->
            val list = (v as? List<*>)?.mapNotNull { it?.toString()?.let(StackingCategory::fromString) } ?: c.stacking.activeCategories
            c.copy(stacking = c.stacking.copy(activeCategories = list))
        },
        "stacking.excluded_item_ids" to { c, v ->
            c.copy(stacking = c.stacking.copy(excludedItemIds = (v as? List<*>)?.mapNotNull { it?.toString() } ?: c.stacking.excludedItemIds))
        },
        "stacking.manual_item_ids" to { c, v ->
            c.copy(stacking = c.stacking.copy(manualStackableItemIds = (v as? List<*>)?.mapNotNull { it?.toString() } ?: c.stacking.manualStackableItemIds))
        }
    )

    fun updateValue(path: String, value: Any) {
        val current = getConfig()
        val updater = configSetters[path] ?: return
        val updated = updater(current, value)
        if (updated != current) {
            saveConfig(updated)
        }
    }

    /**
     * Creates a default configuration file based on internal default values.
     * @param configFile The target file where the default configuration will be saved.
     */
    private fun createDefaultConfig(configFile: File) {
        try {
            configFile.parentFile?.mkdirs()
            
            // Use default object structure to guarantee consistency
            val defaultConfig = StackableToolsConfig.getDefault()
            saveConfig(defaultConfig)
            
            CustomLogger.info("Default config generated from internal values.")
        } catch (e: Exception) {
            CustomLogger.error("Error creating default config: ${e.message}")
        }
    }
}
