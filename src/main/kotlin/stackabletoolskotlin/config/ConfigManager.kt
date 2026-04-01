package stackabletoolskotlin.config

import com.moandjiezana.toml.Toml
import com.moandjiezana.toml.TomlWriter
import java.io.File
import java.nio.charset.StandardCharsets
import stackabletoolskotlin.CustomLogger

/**
 * Gestionnaire de configuration TOML
 */
object ConfigManager {
    private const val CONFIG_FILE_PATH = "config/stackabletoolskotlin.toml"
    private var config: StackableToolsKotlinConfig? = null

    /**
     * Charge la configuration depuis le fichier (ou crée par défaut)
     */
    fun loadConfig(): StackableToolsKotlinConfig {
        val configFile = File(CONFIG_FILE_PATH)
        
        if (!configFile.exists()) {
            // Créer le fichier de configuration par défaut
            createDefaultConfig(configFile)
            return loadConfigFromFile(configFile)
        }
        
        return loadConfigFromFile(configFile)
    }

    /**
     * Sauvegarde la configuration dans le fichier TOML
     */
    fun saveConfig(config: StackableToolsKotlinConfig) {
        val configFile = File(CONFIG_FILE_PATH)
        val writer = TomlWriter()

        try {
            configFile.parentFile?.mkdirs()
            writer.write(config, configFile)
        } catch (e: Exception) {
            CustomLogger.error("Erreur lors de la sauvegarde de la configuration: ${e.message}")
        }
    }

    /**
     * Met à jour une valeur spécifique dans la configuration
     */
    fun updateConfig(key: String, value: Any) {
        val currentConfig = config ?: loadConfig()
        val updatedConfig = updateConfigValue(currentConfig, key, value)
        saveConfig(updatedConfig)
        config = updatedConfig
    }

    /**
     * Récupère la configuration actuelle
     */
    fun getConfig(): StackableToolsKotlinConfig {
        if (config == null) {
            config = loadConfig()
        }
        return config!!
    }

    private fun loadConfigFromFile(configFile: File): StackableToolsKotlinConfig {
        try {
            val toml = Toml().read(configFile)

            val manualItemIds = toml.getList<String>("stacking.manual_item_ids")
                ?.mapNotNull { it?.trim()?.takeIf { it.isNotEmpty() } }
                ?: listOf()

            val activeCategories = toml.getList<String>("stacking.active_categories")
                ?.mapNotNull { it?.trim()?.lowercase()?.takeIf { it.isNotEmpty() } }
                ?: listOf("tools", "potions")

            val excludedItemIds = toml.getList<String>("stacking.excluded_item_ids")
                ?.mapNotNull { it?.trim()?.takeIf { it.isNotEmpty() } }
                ?: listOf()

            val loadedConfig = StackableToolsKotlinConfig(
                enableLogging = toml.getBoolean("logging.enable", true),
                logLevel = toml.getString("logging.level", "INFO"),
                logInFile = toml.getBoolean("logging.in_file", true),
                logInConsole = toml.getBoolean("logging.in_console", true),
                logInChat = toml.getBoolean("logging.in_chat", true),
                enableStacking = toml.getBoolean("stacking.enable", true),
                maxStackSize = toml.getLong("stacking.max_stack_size", 64L),
                maxToolStackSize = toml.getLong("stacking.max_tool_stack_size", 8L),
                maxPotionStackSize = toml.getLong("stacking.max_potion_stack_size", 16L),
                activeCategories = activeCategories,
                excludedItemIds = excludedItemIds,
                manualStackableItemIds = manualItemIds
            )
            loadedConfig.isLoaded = true
            config = loadedConfig
            return loadedConfig
        } catch (e: Exception) {
            CustomLogger.error("Erreur lors du chargement de la configuration: ${e.message}")
            val defaultConfig = StackableToolsKotlinConfig()
            config = defaultConfig
            return defaultConfig
        }
    }

    private const val DEFAULT_CONFIG_RESOURCE = "/stackabletoolskotlin.default.toml"

    private fun createDefaultConfig(configFile: File) {
        try {
            configFile.parentFile?.mkdirs()
            val defaultToml = loadDefaultConfigFromResources() ?: buildDefaultConfigString()
            configFile.writeText(defaultToml, StandardCharsets.UTF_8)
            CustomLogger.info("Fichier de configuration par défaut créé: $CONFIG_FILE_PATH")
        } catch (e: Exception) {
            CustomLogger.error("Erreur lors de la création du fichier de configuration par défaut: ${e.message}")
        }
    }

    private fun loadDefaultConfigFromResources(): String? {
        return try {
            ConfigManager::class.java.getResourceAsStream(DEFAULT_CONFIG_RESOURCE)?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }
        } catch (e: Exception) {
            CustomLogger.error("Impossible de charger la config par défaut depuis les ressources: ${e.message}")
            null
        }
    }

    private fun buildDefaultConfigString(): String {
        return buildString {
            appendLine("# Configuration pour StackableToolsKotlin")
            appendLine("#  - enable : activer/désactiver le mod")
            appendLine("#  - max_stack_size : limite globale (64 par défaut, utile pour override full stack)")
            appendLine("#  - max_tool_stack_size : taille maximale pour les outils")
            appendLine("#  - max_potion_stack_size : taille maximale pour les potions")
            appendLine("#  - active_categories : outils, potions, armors, all")
            appendLine("#  - manual_item_ids : ids précis (minecraft:diamond_hoe, etc.)")
            appendLine("#  - excluded_item_ids : pour forcer l'exclusion")
            appendLine()
            appendLine("[logging]")
            appendLine("enable = true")
            appendLine("level = \"INFO\"")
            appendLine("in_file = true")
            appendLine("in_console = true")
            appendLine("in_chat = true")
            appendLine()
            appendLine("[stacking]")
            appendLine("enable = true")
            appendLine("max_stack_size = 64")
            appendLine("max_tool_stack_size = 8")
            appendLine("max_potion_stack_size = 16")
            appendLine("active_categories = [\"tools\", \"potions\"]")
            appendLine("manual_item_ids = []")
            appendLine("excluded_item_ids = []")
            appendLine()
            appendLine("# Exemples :")
            appendLine("# manual_item_ids = [\"minecraft:shield\", \"minecraft:elytra\"]")
            appendLine("# excluded_item_ids = [\"minecraft:stone_axe\"]")
        }
    }

    private fun updateConfigValue(config: StackableToolsKotlinConfig, key: String, value: Any): StackableToolsKotlinConfig {
        return when (key) {
            "logging.enable" -> config.copy(enableLogging = value as Boolean)
            "logging.level" -> config.copy(logLevel = value as String)
            "logging.in_file" -> config.copy(logInFile = value as Boolean)
            "logging.in_console" -> config.copy(logInConsole = value as Boolean)
            "logging.in_chat" -> config.copy(logInChat = value as Boolean)
            "stacking.enable" -> config.copy(enableStacking = value as Boolean)
            "stacking.max_stack_size" -> config.copy(maxStackSize = value as Long)
            "stacking.max_tool_stack_size" -> config.copy(maxToolStackSize = value as Long)
            "stacking.max_potion_stack_size" -> config.copy(maxPotionStackSize = value as Long)
            "stacking.active_categories" -> config.copy(activeCategories = (value as? List<*>)?.mapNotNull { it?.toString()?.trim()?.lowercase() } ?: listOf())
            "stacking.excluded_item_ids" -> config.copy(excludedItemIds = (value as? List<*>)?.mapNotNull { it?.toString()?.trim()?.takeIf { it.isNotEmpty() } } ?: listOf())
            "stacking.manual_item_ids" -> config.copy(manualStackableItemIds = (value as? List<*>)?.mapNotNull { it?.toString()?.trim()?.takeIf { it.isNotEmpty() } } ?: listOf())
            else -> config
        }
    }
}