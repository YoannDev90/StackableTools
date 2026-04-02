package stackabletools

import net.fabricmc.api.ClientModInitializer
import stackabletools.CustomLogger

object StackableToolsClient : ClientModInitializer {
	override fun onInitializeClient() {
		CustomLogger.info("Client StackableTools initialisé")
	}
}