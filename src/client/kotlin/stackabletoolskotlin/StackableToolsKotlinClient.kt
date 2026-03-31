package stackabletoolskotlin

import net.fabricmc.api.ClientModInitializer
import stackabletoolskotlin.CustomLogger

object StackableToolsKotlinClient : ClientModInitializer {
	override fun onInitializeClient() {
		CustomLogger.info("Client StackableToolsKotlin initialisé")
	}
}