
package stackabletoolskotlin

import java.nio.charset.StandardCharsets

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory
import stackabletoolskotlin.CustomLogger

object StackableToolsKotlin : ModInitializer {
	private val logger = LoggerFactory.getLogger("stackabletoolskotlin")

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
		CustomLogger.info("Initialisation de StackableToolsKotlin version $version")
	}
}