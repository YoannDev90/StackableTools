package stackabletools

import net.fabricmc.api.ClientModInitializer
import stackabletools.CustomLogger

/**
 * Main entry point for the client side of the StackableTools mod.
 */
object StackableToolsClient : ClientModInitializer {
    /**
     * Initializes the mod in a client-side context.
     * Registers any client-only hooks or event listeners.
     */
    override fun onInitializeClient() {
        CustomLogger.info("StackableTools client initialized.")
    }
}