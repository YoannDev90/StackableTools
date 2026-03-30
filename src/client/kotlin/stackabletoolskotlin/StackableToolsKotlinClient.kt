package stackabletoolskotlin

import net.fabricmc.api.ClientModInitializer
import stackabletoolskotlin.CustomLogger
import stackabletoolskotlin.config.ConfigManager

object StackableToolsKotlinClient : ClientModInitializer {
	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		CustomLogger.info("Initialisation du client StackableToolsKotlin démarrée")
		
		// Charger la configuration
		val config = ConfigManager.getConfig()
		
		// Ajout de logs pour vérifier le bon chargement du client
		CustomLogger.info("Client StackableToolsKotlin chargé avec succès")
		CustomLogger.info("Configuration client: Logging=${config.enableLogging}, Stacking=${config.enableStacking}")
		
		CustomLogger.info("Initialisation du client StackableToolsKotlin terminée")
	}
}