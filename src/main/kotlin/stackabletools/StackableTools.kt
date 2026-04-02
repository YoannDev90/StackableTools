
package stackabletools

import java.nio.charset.StandardCharsets

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import stackabletools.CustomLogger
import stackabletools.config.ConfigManager

object StackableTools : ModInitializer {
	private val logger = LoggerFactory.getLogger("stackabletools")

	private fun getModVersion(): String {
		// Essaye de lire la version depuis fabric.mod.json dans le jar
		return try {
			val resource = javaClass.classLoader.getResourceAsStream("fabric.mod.json")
			if (resource != null) {
				val text = resource.reader(StandardCharsets.UTF_8).readText()
				val match = Regex("\"version\"\\s*:\\s*\"([^\"]+)\"").find(text)
				val version = match?.groupValues?.get(1) ?: "unknown"
				if (version.startsWith("$")) "unknown" else version
			} else {
				"unknown"
			}
		} catch (_: Exception) {
			"unknown"
		}
	}

	override fun onInitialize() {
		val version = getModVersion()
		CustomLogger.info("Initialisation de StackableTools version $version")

		// Force le chargement et la création de la config au démarrage si elle n'existe pas
		val config = ConfigManager.getConfig()
		if (!config.isLoaded) {
			CustomLogger.info("Configuration stackabletools chargée par défaut via ConfigManager")
		}
	}
}