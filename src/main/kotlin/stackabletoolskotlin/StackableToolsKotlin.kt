package stackabletoolskotlin

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import stackabletoolskotlin.CustomLogger
import stackabletoolskotlin.config.ConfigManager
import stackabletoolskotlin.config.StackableToolsKotlinConfig

object StackableToolsKotlin : ModInitializer {
    private val logger = LoggerFactory.getLogger("stackabletoolskotlin")
    private lateinit var config: StackableToolsKotlinConfig

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Initialisation du mod StackableToolsKotlin démarrée")
		
		// Charger la configuration
		config = ConfigManager.loadConfig()
		
		// Ajout de logs pour vérifier le bon chargement
		CustomLogger.info("Mod StackableToolsKotlin chargé avec succès")
		CustomLogger.info("Version du mod: 1.0.0")
		CustomLogger.info("Configuration chargée: Logging=${config.enableLogging}, Stacking=${config.enableStacking}")
		
		logger.info("Hello Fabric world!")
		
		CustomLogger.info("Initialisation du mod StackableToolsKotlin terminée")
	}
}