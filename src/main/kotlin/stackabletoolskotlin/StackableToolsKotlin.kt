package stackabletoolskotlin

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import stackabletoolskotlin.CustomLogger

object StackableToolsKotlin : ModInitializer {
    private val logger = LoggerFactory.getLogger("stackabletoolskotlin")

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Initialisation du mod StackableToolsKotlin démarrée")
		
		// Ajout de logs pour vérifier le bon chargement
		CustomLogger.info("Mod StackableToolsKotlin chargé avec succès")
		CustomLogger.info("Version du mod: 1.0.0")
		
		logger.info("Hello Fabric world!")
		
		CustomLogger.info("Initialisation du mod StackableToolsKotlin terminée")
	}
}