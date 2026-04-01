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

            val loadedConfig = StackableToolsKotlinConfig(
                enableLogging = toml.getBoolean("logging.enable", true),
                logLevel = toml.getString("logging.level", "INFO"),
                maxStackSize = toml.getLong("stacking.max_stack_size", 64L),
                enableStacking = toml.getBoolean("stacking.enable", true),
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
            appendLine("[logging]")
            appendLine("enable = true")
            appendLine("level = \"INFO\"")
            appendLine()
            appendLine("[stacking]")
            appendLine("max_stack_size = 64")
            appendLine("enable = true")
            appendLine("manual_item_ids = []")
        }
    }

    private fun updateConfigValue(config: StackableToolsKotlinConfig, key: String, value: Any): StackableToolsKotlinConfig {
        return when (key) {
            "logging.enable" -> config.copy(enableLogging = value as Boolean)
            "logging.level" -> config.copy(logLevel = value as String)
            "stacking.max_stack_size" -> config.copy(maxStackSize = value as Long)
            "stacking.enable" -> config.copy(enableStacking = value as Boolean)
            "stacking.manual_item_ids" -> config.copy(manualStackableItemIds = (value as? List<*>)?.mapNotNull { it?.toString() } ?: listOf())
            else -> config
        }
    }
}